package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 购物车 controller
 *
 * @author
 * @create 2017-12-07 上午10:42
 **/
@Controller
@RequestMapping("/cart")
public class CartController {


    // 注入service
    @Autowired
    private ICartService iCartService;

    /**
     * 向购物车添加商品
     *
     * @param session
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("/add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        // 逻辑
        return iCartService.add(user.getId(), productId, count);
    }


    /**
     * 更新购物车中商品个数
     *
     * @param session
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("/update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        // 逻辑
        return iCartService.update(user.getId(), productId, count);
    }


    /**
     * 删除 购物车中商品
     *
     * @param session
     * @param productIds 要 删除的 商品 id 字符串 （逗号分隔）
     * @return
     */
    @RequestMapping("/delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productIds) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //  执行 删除 指定 商品 逻辑
        return iCartService.deleteProduct(user.getId(), productIds);


    }


    /**
     * 查询 购物车列表方法
     *
     * @param session
     * @return
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        // 逻辑
        return iCartService.list(user.getId());
    }


    /**
     * //     全选
     *
     * @param session
     * @return
     */
    @RequestMapping("/select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        // 逻辑
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }


    /**
     * //     全反选
     *
     * @param session
     * @return
     */
    @RequestMapping("/un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        // 逻辑
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }


    /**
     * //    单独反选
     *
     * @param session
     * @return
     */
    @RequestMapping("/un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session, Integer productId) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        // 逻辑
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }


    /**
     * //    单独选
     *
     * @param session
     * @return
     */
    @RequestMapping("/select.do")
    @ResponseBody
    public ServerResponse<CartVo> elect(HttpSession session, Integer productId) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        // 逻辑
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    /**
     * //     查询 用户 购物车中商品数量

     * @param session
     * @return
     */
    @RequestMapping("/get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session) {
        // 判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createBySuccess(0);
        }

        //  查询购物车中商品数量逻辑
        return iCartService.getCartProductCount(user.getId());
    }
}
