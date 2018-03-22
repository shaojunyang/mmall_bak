package com.mmall.controller.portal;

import ch.qos.logback.classic.pattern.ClassNameOnlyAbbreviator;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 收货地址
 *
 * @author yangshaojun
 * @create 2017-12-07 下午3:59
 **/
@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    /**
     * 增加地址
     *
     * @param session
     * @param shipping
     * @return
     */
    @RequestMapping("/add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 如果 用户没有 登录
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        // 增加  逻辑
        return iShippingService.add(user.getId(), shipping);
    }

    /**
     * 删除地址
     *
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping("/del.do")
    @ResponseBody
    public ServerResponse del(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 如果 用户没有 登录
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        // 增加   删除地址 逻辑
        return iShippingService.del(user.getId(), shippingId);
    }

    /**
     * 更新收货地址
     *
     * @param session
     * @param shipping
     * @return
     */
    @RequestMapping("/update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 如果 用户没有 登录
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        // 增加   删除地址 逻辑
        return iShippingService.update(user.getId(), shipping);
    }


    /**
     * 查看 某个地址详情
     *
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping("/select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 如果 用户没有 登录
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        // 增加   查询地址 逻辑
        return iShippingService.select(user.getId(), shippingId);
    }


    /**
     * 查看 收货地址列表
     *
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 如果 用户没有 登录
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        // 查询列表 并返回
        return iShippingService.list(user.getId(), pageNum, pageSize);
    }

}
