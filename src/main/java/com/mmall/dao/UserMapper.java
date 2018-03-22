package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 查询是否有该用户
     *
     * @param username
     * @return
     */
    int checkUsername(String username);

    /**
     * 登录 功能接口
     *
     * @param username
     * @param password
     * @return
     */
    User selectLogin(@Param("username") String username, @Param("password") String password);

    /**
     * 查询邮箱是否存在
     *
     * @param email
     * @return
     */
    int checkEmail(@Param("email") String email);


    //   根据用户名查询用户的密码提示问题
    String selectQuestionByUsername(String username);


    //查询用户 提示问题答案 和 用户名 和 问题 是否匹配
    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    /**
     * 更新 用户 密码
     *
     * @param username
     * @param md5passwordNew
     */
    int updatePasswordByUsername(@Param("username") String username, @Param("md5passwordNew") String md5passwordNew);

    /**
     * 效验用户密码是否 和用户名匹配
     *
     * @param passwordOld
     * @param id
     * @return
     */
    int checkPassword(@Param("passwordOld") String passwordOld, @Param("userId") Integer userId);

    /**
     * 根据 id 效验eamil是否存在
     *
     * @param email
     * @param id
     */
    int checkEmailByUserId(@Param("email") String email, @Param("userId") Integer userId);
}