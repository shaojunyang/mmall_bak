package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    /**
     * 根据 用户id和 收货地址id 删除收货地址
     * @param userId
     * @param shippingId
     * @return
     */
    int deleteByShippingIdUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    /**
     * 根据 用户id和 收货地址id 更新收货地址
     * @param shipping
     * @return
     */
    int updateByShipping(Shipping shipping);

    /**
     * 根据 收货地址id 和 用户id  查询 用户 收货地址
     * @param userId
     * @param shippingId
     * @return
     */
     Shipping  selectByShippingUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    /**
     * 根据用户id 查询收货地址列表
     * @param userId
     * @return
     */
    List<Shipping> selectByUserId(Integer userId);
}