package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * 购物车 service
 *
 * @author
 * @create 2017-12-07 上午10:50
 **/

public interface ICartService {
    /**
     * 添加商品到购物车
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    ServerResponse add(Integer userId, Integer productId, Integer count);

    /**
     * 更新购物车中商品个数
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    /**
     * 删除 购物车中商品
     *
     * @param userId
     * @param productIds 要 删除的 商品 id 字符串 （逗号分隔）
     * @return
     */
    ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

    /**
     * 购物车列表方法
     *
     * @param userId
     * @return
     */
    ServerResponse<CartVo> list(Integer userId);

    /**
     * 全选 或者 全反选
     *
     * @param userId
     * @param checked
     * @param productId
     * @return
     */
    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId,Integer checked);

    /**
     *  查询购物车中商品数量
     * @param userId
     * @return
     */
    ServerResponse<Integer> getCartProductCount(Integer userId);
}
