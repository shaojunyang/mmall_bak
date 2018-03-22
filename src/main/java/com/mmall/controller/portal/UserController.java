package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

/**
 * 门户 用户 模块controller
 *
 * @author
 * @create 2017-11-24 下午6:47
 **/

@Controller
@RequestMapping("/user")
public class UserController {
    //controller中注入service 接口
    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录 handler
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
//        调用 service
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
//         如果 是 成功的话、把用户对象信息放入session
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 用户 登出功能
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
//        把用户的session删除
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册  controller功能
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/register.do")
    @ResponseBody
    public ServerResponse<String> register(User user, HttpServletRequest request) {

//        调用service
        return iUserService.register(user);
    }

    /**
     * 校验用户名 和邮箱
     *
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "/check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {

        return iUserService.checkValid(str, type);
    }

    /**
     * 获取用户信息
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {

//        从session中获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            //返回用户信息
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录、无法获取用户信息");
    }

    /**
     * 获取用户 密码 提示问题
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "/forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 校验密码 问题答案
     *
     * @return
     */
    @RequestMapping(value = "/forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);

    }

    /**
     * 重置密码
     *
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "/forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetRestPasswod(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态下重置密码功能开发
     *
     * @param session
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "/reset_pass.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        //从session中获取用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户没有登录");
        }
        //更新 密码
        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    /**
     * 更新用户个人信息
     *
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "/update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session, User user) {
        //从session中获取用户
        User current_user = (User) session.getAttribute(Const.CURRENT_USER);
        if (current_user == null) {
            return ServerResponse.createByErrorMessage("用户没有登录");
        }
        user.setId(current_user.getId());
        user.setUsername(current_user.getUsername());

        //    更新用户信息
        ServerResponse<User> response = iUserService.update_information(user);
        response.getData().setUsername(current_user.getUsername());
        if (response.isSuccess()) {
            //    更新 session中的个人用户信息
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 获取用户详细信息功能开发
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpSession session) {
        //从session中获取用户
        User current_user = (User) session.getAttribute(Const.CURRENT_USER);
        // 如果用户没有登录 需要 强制登录
        if (current_user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录需要强制登录status=10");
        }

        //    返回用户详细信息
        ServerResponse<User> userInfo = iUserService.get_information(current_user.getId());
        return userInfo;
    }
}


