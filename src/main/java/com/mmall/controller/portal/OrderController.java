package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付和 订单的接口
 *
 * @author yangshaojun
 * @create 2017-12-08 下午2:02
 **/
@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;


    /**
     * 创建订单
     *
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping("/create.do")
    @ResponseBody
    public ServerResponse<OrderVo> create(HttpSession session, Integer shippingId) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        //     创建订单
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 未付款 状态  取消订单
     *
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("/cancel.do")
    @ResponseBody
    public ServerResponse<String> cancel(HttpSession session, Long orderNo) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        //     取消 订单
        return iOrderService.cancelOrder(user.getId(), orderNo);
    }


    /**
     * 获取 购物车中已经选中的商品
     *
     * @param session
     * @return
     */
    @RequestMapping("/get_order_cart_product.do")
    @ResponseBody
    public ServerResponse<OrderProductVo> getOrderCartProduct(HttpSession session) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        //     取消获取 购物车中已经选中的商品
        return iOrderService.getOrderCartProduct(user.getId());
    }


    /**
     * 前台 用户 查看 订单详情
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpSession session,Long orderNo) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        //     查看 订单详情
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }


    /**
     * 前台 用户 查看 订单列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> detail(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        //     查看 订单详情
        return iOrderService.getOrderlist(user.getId(),pageNum,pageSize);
    }
















    /**
     * 支付
     *
     * @param session
     * @param orderNo 订单号
     * @param request
     * @return
     */
    @RequestMapping("/pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createBySuccess(0);
        }
        // 获取 项目服务器下 upload的目录
        String path = request.getSession().getServletContext().getRealPath("upload");

        //     支付
        return iOrderService.pay(orderNo, user.getId(), path);
    }

    /**
     * 支付宝 字符回调函数
     *
     * @param request
     * @return
     */
    @RequestMapping("/alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();

        //     从request中获取 支付宝 回调参数的map
        Map requestParams = request.getParameterMap();
        Iterator<String> iter = requestParams.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        logger.info("支付宝回调,sign:{},trade_status:{},参数是:{}", params.get("sign"), params.get("trade_status"), params.toString());

        //     非常重要、验证回调的正确性是不是支付宝发的、而且 防止重复通知
        params.remove("sign_type");

        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求、验证不通过、再恶意请求我就报网警了");
            }
            //     业务逻辑
            return iOrderService.aliCallback(params);
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常", e);
        }


        //TODO  验证各种数据


        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 付款好之后、前台会调用这个接口看是不是订单付款成功了
     *
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("/query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createBySuccess();
        }

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);

    }
}













