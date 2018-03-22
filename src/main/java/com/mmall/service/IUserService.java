package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * 用户模块 接口
 * Created by yangshaojun on 2017/11/24.
 */
public interface IUserService {
    //登录 接口
    ServerResponse<User> login(String username, String password);

    //注册 接口
    ServerResponse<String> register(User user);

    //校验用户名和邮箱是否符合 规则
    ServerResponse<String> checkValid(String str, String type);

    //    获取用户 密码提示问题
    ServerResponse<String> selectQuestion(String username);

    /**
     * 校验用户密码提示 问题的答案
     *
     * @param username
     * @param password
     * @param answer
     * @return
     */
    ServerResponse<String> checkAnswer(String username, String question, String answer);

    /**
     * 重置密码
     *
     * @param username
     * @param password
     * @param forgetToken
     * @return
     */
    ServerResponse<String> forgetRestPasswod(String username, String passwordNew, String forgetToken);


    /**
     * * 登录状态下重置密码功能开发
     *
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    /**
     * * 更新用户个人信息
     *
     * @param user
     * @return
     */
    ServerResponse<User> update_information(User user);

    /**
     *     * 获取用户详细信息功能开发

     * @param id
     * @return
     */
    ServerResponse<User> get_information(Integer id);

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    ServerResponse chackAdminRole(User user);
}
