package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    /**
     * 根据用户id 和 订单号判断 该订单是否存在
     * @param userId
     * @param orderNo
     * @return
     */
    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    /**
     * 根据  订单号  查询 订单是否 存在
     * @param orderNo
     * @return
     */
    Order selectByOrderNo(Long orderNo);


    /**
     * 根据 用户id 查询 订单集合
     * @param userId
     * @return
     */
    List<Order> selectByUserId(Integer userId);

    /**
     * 查询 所有 订单
     * @return
     */
    List<Order> selectAllOrder();
}