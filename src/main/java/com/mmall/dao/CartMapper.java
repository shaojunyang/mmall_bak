package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    /**
     * // 根据用户id和商品id 查找现有的购物车
     *
     * @param userId
     * @param productId
     * @return
     */
    Cart selectCartByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    /**
     * 根据用户 id 查询 购物车集合
     *
     * @param userId
     * @return
     */
    List<Cart> selectCartByUserId(Integer userId);

    /**
     * 根据 用户id 查看是否全选 购物车商品
     *
     * @param userId
     * @return
     */
    int selectCartProductCheckedStatusByUserId(Integer userId);

    /**
     * 删除 购物车中一个或者 多个 商品
     *
     * @param userId
     * @param productIdList
     * @return
     */
    int deleteByUserIdProductIds(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    /**
     * 全选 或者 全反选
     *
     * @param userId
     * @param checked
     * @return
     */
    int checkOrUnCheckProduct(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

    /**
     *  查询购物车中商品数量
     * @param userId
     * @return
     */
    int selectCartProductCount(Integer userId);

    /**
     * 从购物车中获取已经被勾选的商品
     * @param userId
     * @return
     */
    List<Cart> selectCheckedCartByUserId(Integer userId);
}