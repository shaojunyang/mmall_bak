package com.mmall.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车service
 *
 * @author
 * @create 2017-12-07 上午10:51
 **/
@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加商品到购物车
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {

        // 校验参数
        if (null == userId || null == productId || null == count) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }


        // 根据用户id和商品id 查找现有的购物车
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        // 如果查找是空的、就说明这个产品不在购物车、需要新增这个商品的记录
        if (null == cart) {
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);

            //     插入记录到购物车中
            int insert = cartMapper.insert(cartItem);
        } else {
            //     说明这个产品已经在购物车中了、执行 数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            //更新 购物车中的商品数量
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        // 调用下面的方法
        CartVo cartVo = this.getCartVoLimit(userId);

        return ServerResponse.createBySuccess(cartVo);
    }


    /**
     * 私有的方法、返回 CartVo 对象
     *
     * @param userId
     * @return
     */
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();

        //     查询当前用户下 Cart 购物车的一个集合
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);

        // 用查询到的数据封装一个 cartProductVo 对象结合
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        // 初始化一下购物车总价
        BigDecimal cartTotalPrice = new BigDecimal("0");

        //     如果查询到的结果不为空
        if (!cartList.isEmpty()) {
            for (Cart cartItem : cartList) {
                //     创建 cartProductVo  对象
                CartProductVo cartProductVo = new CartProductVo();
                //     给 cartProductVo  赋值
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                //     查询商品信息
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (null != product) {
                    // 继续赋值
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //     判断库存
                    int buyLimitCount = 0;
                    // 如果商品实际库存大于  购物车中商品数量
                    if (product.getStock() >= cartItem.getQuantity()) {
                        // 库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        //     成功的 产品库存大于购物车 库存
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        //     否则
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        // 购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        //     更新 库存一下
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }

                    //     继续 为 cartProductVo  中的属性 赋值
                    cartProductVo.setQuantity(buyLimitCount);
                    //     计算 购物车中单个 商品 * 数量的 总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity().doubleValue()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                //     如果产品 不为空
                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    //     如果已经勾选、增加到整个购物车的总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                // 添加 到 集合
                cartProductVoList.add(cartProductVo);
            }
        }

        // 赋值 cartVO
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }


    /**
     * 判断 购物车 用户 是否 全选商品
     *
     * @param userId
     * @return
     */
    private Boolean getAllCheckedStatus(Integer userId) {
        if (null == userId) {
            return false;
        }
        // 如果 登录0  是全选、返回true
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

    /**
     * 更新购物车中商品个数
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        // 校验参数
        if (null == userId || null == productId || null == count) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 查询一下
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (null != cart) {
            //     更新 购物车中这个 商品 的 数量
            cart.setQuantity(count);
        }

        // 调用mapper 更新 购物车中这个 商品 的 数量
        cartMapper.updateByPrimaryKeySelective(cart);
        // 调用核心方法
        CartVo cartVo = this.getCartVoLimit(userId);

        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 删除 购物车中商品
     *
     * @param userId
     * @param productIds 要 删除的 商品 id 字符串 （逗号分隔）
     * @return
     */
    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        // collect 中的方法把 字符串 用 ‘,’ 分隔 转换为结合
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (productList.isEmpty()) {
            // 参数错误
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 删除购物车中指定 的 几个商品
        int deleteCount = cartMapper.deleteByUserIdProductIds(userId, productList);
        // 调用核心方法
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);

    }

    /**
     * 查看购物车列表方法
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        // 调用核心方法
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 全选 或者 全反选
     *
     * @param userId
     * @param checked
     * @return
     */
    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {

        // 调用 dao逻辑
        int i = cartMapper.checkOrUnCheckProduct(userId, productId, checked);
        return this.list(userId);
    }

    /**
     * 查询购物车中商品数量
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        // 效验
        if (null == userId) {
            return ServerResponse.createBySuccess(0);
        }
        //查询购物车中商品数量
        int selectCount = cartMapper.selectCartProductCount(userId);
        // 返回
        return ServerResponse.createBySuccess(selectCount);
    }
}
