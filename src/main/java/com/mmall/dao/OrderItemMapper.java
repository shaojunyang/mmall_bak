package com.mmall.dao;

import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    /**
     * 根据 用户名id 和 订单号 查询 订单明细
     *
     * @param orderNo
     * @param userId
     * @return
     */
    List<OrderItem> getByOrderNoUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);


    /**
     * 批量 插入 订单明细
     *
     * @param orderItemList
     */
    void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);

    List<OrderItem> getByOrderNo(Long orderNo);
}