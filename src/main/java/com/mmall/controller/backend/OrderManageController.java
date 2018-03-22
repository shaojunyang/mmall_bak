package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 管理员 管理商品 controller
 *
 * @author yangshaojun
 * @create 2017-12-08 下午9:51
 **/
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {


    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    /**
     * 后台管理 订单 列表 list
     *
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        //如果用户没有登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录");
        }
        // 校验是否是管理员
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //    是管理员
            // 查询  后台管理 订单 列表 list
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员");
        }
    }


    /**
     * 后台管理员 查看订单详情
     *
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo) {
        //如果用户没有登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录");
        }
        // 校验是否是管理员
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //    是管理员
            // 查询  后台管理 订单 列表 list
            return iOrderService.manageDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员");
        }
    }


    /**
     * 后台管理员 根据订单号搜索订单
     *
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        //如果用户没有登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录");
        }
        // 校验是否是管理员
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //    是管理员
            // 查询  后台管理 订单 列表 list
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员");
        }
    }


    /**
     * 后台 管理员 对订单发货 操作
     *
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("/send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo) {
        //如果用户没有登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录");
        }
        // 校验是否是管理员
        if (iUserService.chackAdminRole(user).isSuccess()) {
            //    是管理员
            // 查询  后台管理 订单 列表 list
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("不是管理员");
        }
    }


}
