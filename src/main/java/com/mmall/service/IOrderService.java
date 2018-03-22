package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * 订单 和 支付
 *
 * @author yangshaojun
 * @create 2017-12-08 下午2:07
 **/

public interface IOrderService {

    /**
     * 支付
     * @param orderNo 订单号
     * @param userId 用户id
     * @param path 上传二维码的路径
     * @return
     */
    ServerResponse pay(Long orderNo ,Integer userId,String path);

    /**
     * 回调 成功后的业务逻辑
     * @param params
     * @return
     */
    ServerResponse aliCallback(Map<String, String> params);

    /**
     *  付款好之后、前台会调用这个接口看是不是订单付款成功了
     * @param    userId a
     * @param orderNo
     * @return
     */
    ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo);


    /**
     * 创建 订单 、返回 OrderVo
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId);

    /**
     * 取消订单
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<String> cancelOrder(Integer userId, Long orderNo);

    /**
     * 获取 购物车中已经选中的商品
     * @param userId
     * @return
     */
    ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId);

    /**
     * 前台 用户 查看 订单详情
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    /**
     * 前台 用户 查看 订单列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> getOrderlist(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 后台管理 订单 列表 list
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> manageList(Integer pageNum, Integer pageSize);

    /**
     * 后台管理员查看订单详情
     * @param orderNo
     * @return
     */
    ServerResponse<OrderVo> manageDetail(Long orderNo);

    /**
     * 后台管理员 根据订单号搜索订单
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo> manageSearch(Long orderNo, Integer pageNum, Integer pageSize);


    /**
     * 发货
     * @param orderNo
     * @return
     */
    ServerResponse<String> manageSendGoods(Long orderNo);
}
