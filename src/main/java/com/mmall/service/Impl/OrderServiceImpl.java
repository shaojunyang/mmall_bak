package com.mmall.service.Impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单和 支付
 *
 * @author yangshaojun
 * @create 2017-12-08 下午2:07
 **/

@Service
public class OrderServiceImpl implements IOrderService {

    // 注入订单
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    /**
     * 创建订单
     *
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId) {
        //     从购物车中获取已经被勾选的商品
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);


        // 计算 这个  订单总价
        ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            // 如果没有获取 到
            return serverResponse;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        //总价
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        //     生成订单
        Order order = this.assembleOrder(userId, shippingId, payment);
        if (null == order) {
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("购物车 为空");
        }

        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        //     mybatis 批量插入
        // 批量 插入 订单明细到 订单明细表
        orderItemMapper.batchInsert(orderItemList);

        //     生成成功、减少产品库存
        this.reduceProductStock(orderItemList);

        //     清空购物车
        this.cleanCart(cartList);

        //    返回前端数据  vo对象
        OrderVo orderVo = assembleOrderVo(order, orderItemList);

        return ServerResponse.createBySuccess(orderVo);
    }


    /**
     * 取消订单
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<String> cancelOrder(Integer userId, Long orderNo) {
        // 根据 用户 和 订单号 查询 订单
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (null == order) {
            return ServerResponse.createByErrorMessage("该用户 此订单不存在");
        }

        //  判断订单状态
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("该订单已经付款、不能退款");
        }
        // 创建订单对象、赋值为 订单状态 为 已取消
        Order updateOrder = new Order();
        order.setId(order.getId());
        order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int updateRow = orderMapper.updateByPrimaryKeySelective(order);
        if (updateRow > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();

    }


    /**
     * 获取 购物车中已经选中的商品
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId) {

        OrderProductVo orderProductVo = new OrderProductVo();


        // 从购物车中获取 数据
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        // 根据 Cart结婚 获取 获取购物车的的订单item
        ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        // 计算 总价
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        // 初始化总价
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }

        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return ServerResponse.createBySuccess(orderProductVo);
    }


    /**
     * 前台 用户 查看 订单详情
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        // 查询订单
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        // 判断
        if (null != order) {
            //    获取 订单OrderItem集合
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
            //     组装 OrderVo
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            //     返回
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }


    /**
     * 后台管理员查看订单列表
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getOrderlist(Integer userId, Integer pageNum, Integer pageSize) {
        // 分页
        PageHelper.startPage(pageNum, pageSize);
        // sql
        // 获取用户订单集合
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);

        //  分页
        PageInfo pageResult = new PageInfo(orderList);

        pageResult.setList(orderVoList);

        return ServerResponse.createBySuccess(pageResult);
    }


    /**
     * 后台管理员查看订单详情
     *
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<OrderVo> manageDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (null != order) {
            // 通过订单号查询订单明细集合
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            //     组装结合
            OrderVo orderVo = assembleOrderVo(order, orderItemList);

            //     返回
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }


    /**
     * 后台管理员查看订单详情
     *
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<PageInfo> manageSearch(Long orderNo, Integer pageNum, Integer pageSize) {
        // 分页
        PageHelper.startPage(pageNum, pageSize);

        // 逻辑
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (null != order) {
            // 通过订单号查询订单明细集合
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            //     组装结合
            OrderVo orderVo = assembleOrderVo(order, orderItemList);

            // 分页
            PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
            pageResult.setList(Lists.newArrayList(orderVo));

            //     返回
            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }


    /**
     * 查看 订单详情 列表
     *
     * @param orderList
     * @param userId    管理员 查看 不需要 传 userId
     * @return
     */
    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        //   遍历订单
        for (Order order : orderList) {
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (null == userId) {
                // 管理员 查看订单列表、不需要 传 userId
                orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            } else {
                //    用户查看订单列表
                orderItemList = orderItemMapper.getByOrderNoUserId(order.getOrderNo(), userId);

            }
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }


    /**
     * 后台 管理员 对订单发货 操作
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<String> manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (null != order) {
            // 如果 已经付款的话
            if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
                //更新 状态 为 已发货
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                //   更新 发货时间
                order.setSendTime(new Date());
                //   对数据库的订单更新 发货状态
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccessMessage("发货成功");
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    /**
     * 生成  OrderVo对象
     *
     * @param order
     * @param orderItemList
     * @return
     */
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        // 初始化一个 OrderVo 对象
        OrderVo orderVo = new OrderVo();

        //    赋值
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());

        //  支付方式 赋值
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        // 收货地址
        orderVo.setShippingId(order.getShippingId());

        //    根据收货地址 查询 收货地址详细信息
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (null != shipping) {
            // 设置 收货地址 和 收货 vo对象
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShipping(shipping));
        }
        //  设置时间
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        //    设置 图片 地址前缀
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));


        // 声明 一个  OrderItemVo 集合
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        //     遍历 商品 明细
        for (OrderItem orderItem : orderItemList) {
            // 调用 自定义的方法
            OrderItemVo orderItemVo = this.assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }

        orderVo.setOrderItemVoList(orderItemVoList);

        return orderVo;

    }


    /**
     * 组装  OrderItemVo 对象
     *
     * @param orderItem
     * @return
     */
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {

        OrderItemVo orderItemVo = new OrderItemVo();

        //     赋值
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setProductId(orderItem.getProductId());

        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));


        return orderItemVo;
    }

    /**
     * 组装 收货地址vo 对象
     *
     * @param shipping
     * @return
     */
    private ShippingVo assembleShipping(Shipping shipping) {

        //     声明 一个 ShippingVo
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());


        return shippingVo;
    }


    /**
     * 创建订单之后 执行 商品 减库存
     *
     * @param orderItemList
     */

    private void reduceProductStock(List<OrderItem> orderItemList) {
        // 遍历 订单明细
        for (OrderItem orderItem : orderItemList) {
            // 查询出 商品
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            // 商品 减库存
            product.setStock(product.getStock() - orderItem.getQuantity());
            //     把减号的仓库 更新到数据表
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }


    /**
     * 组装订单
     *
     * @param userId
     * @param shippingId
     * @param payment
     * @return
     */
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        //      初始化 订单对象
        Order order = new Order();
        //     生成订单号
        long orderNo = this.generateOrderNo();
        //     组装订单
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        // 运费
        order.setPostage(0);
        // 支付方式
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        //     发货时间等等

        //     把 订单插入到订单表
        int insertCount = orderMapper.insert(order);
        if (insertCount > 0) {
            return order;
        }
        return null;
    }


    /**
     * 生成 订单号
     *
     * @return
     */
    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }


    /**
     * 计算 订单总价
     *
     * @param orderItemList
     * @return
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }


    /**
     * 获取 购物车中商品明细
     *
     * @param userId
     * @param cartList
     * @return
     */
    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        // 初始化
        List<OrderItem> orderItemList = Lists.newArrayList();

        if (cartList.isEmpty()) {
            return ServerResponse.createByErrorMessage("没有选择商品、购物车为空");

        }

        //     效验 购物车的数据、包括产品的状态和数量
        for (Cart cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            //     从购物车中获取 商品
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            //     判断 产品 状态
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                //         如果 产品 不是在售状态
                return ServerResponse.createByErrorMessage("商品 不是在线售卖状态");

            }
            //     效验库存
            if (cartItem.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("商品" + product.getName() + "库存不足");
            }
            //     组装 orderItem
            orderItem.setUserId(userId);
            orderItem.setProductName(product.getName());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
            //     add 到 集合中
            orderItemList.add(orderItem);
        }

        return ServerResponse.createBySuccess(orderItemList);
    }


    /**
     * 后台管理 订单 列表 list
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> manageList(Integer pageNum, Integer pageSize) {

        // 分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询 所有订单
        List<Order> orderList = orderMapper.selectAllOrder();

        // 组装
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);

        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);

        return ServerResponse.createBySuccess(pageResult);

    }


    /**
     * 支付
     *
     * @param orderNo 订单号
     * @param userId  用户id
     * @param path    上传二维码的路径
     * @return
     */
    @Override
    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        // 创建 map 集合
        Map<String, String> resultMap = Maps.newHashMap();

        // 先 根据 用户id 和订单号判断 校验 订单是否 存在
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (null == order) {
            //  如果订单不存在
            return ServerResponse.createByErrorMessage("用户没有改订单");
        }

        // 向map中注入 订单号
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));


        // 生成 支付宝订单参数


        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("happymmall扫描支付、订单号 :").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        // 遍历商品 、把商品添加到集合
        // 根据用户id 和订单号 查询订单明细表
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
        for (OrderItem orderItem : orderItemList) {
            // 构建商品明细
            GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            // 添加到 商品 详情 集合
            goodsDetailList.add(goods1);
        }


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();


        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 生成二维码、把二维码传送服务器、组装url、返回给前端
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                // 细节 path 后面 加  /
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                // 二维码 文件名
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                // 上传到 Ftp服务器
                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常", e);
                }
                logger.info("qrPath:" + qrPath);


                // 拼接返回给前端的url
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                //把 url放入map
                resultMap.put("qrUrl", qrUrl);

                // 返回
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");


            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");

        }


    }


    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }


    /**
     * 回调成功后的业务逻辑
     *
     * @param params 支付宝  回调参数 的map
     * @return
     */
    @Override
    public ServerResponse aliCallback(Map<String, String> params) {

        // 拿到订单号 和 支付宝交易号
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String traderNo = params.get("trade_no");
        // 交易状态
        String tradeStatus = params.get("trade_status");

        // 根据  订单号  查询 订单是否 存在
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (null == order) {
            return ServerResponse.createByErrorMessage("非快乐幕商城的订单、回调忽略");
        }
        //  判断订单状态
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        //  判断 回调状态
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            // 更新 付款时间
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            //     如果交易成功把订单状态置为已付款
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            //     把订单状态更新到  订单数据表
            orderMapper.updateByPrimaryKeySelective(order);
        }


        // 组装  payInfo  对象
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        // 支付宝交易号
        payInfo.setPlatformNumber(traderNo);
        // 支付状态
        payInfo.setPlatformStatus(tradeStatus);

        // 把 支付  信息 存储 到 支付 交易数据表中
        payInfoMapper.insert(payInfo);


        return ServerResponse.createBySuccess();
    }


    /**
     * 付款好之后、前台会调用这个接口看是不是订单付款成功了
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo) {
        // 查询订单是否存在
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (null == order) {
            return ServerResponse.createByErrorMessage("没有该订单");
        }

        // 判断支付状态
        //  判断订单状态
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


}
