package com.mmall.service.Impl;

import com.google.zxing.common.StringUtils;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import java.util.Objects;
import java.util.UUID;

/**
 * 用户service实现 接口
 *
 * @author
 * @create 2017-11-24 下午7:11
 **/
@Service("iUserService") //注入 service 接口
public class UserServiceImpl implements IUserService {
    //    注入 userMapper
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户 登录
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
//        先查询用户是否存在
        int count = userMapper.checkUsername(username);
        if (count == 0) {
            return ServerResponse.createBySuccessMessage("用户名不存在");
        }
//        密码登录 MD5 //
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
//        把 他的密码置为空
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);

    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    public ServerResponse<String> register(User user) {
        //        先查询用户是否存在
        int count = userMapper.checkUsername(user.getUsername());
        if (count > 0) {
            return ServerResponse.createBySuccessMessage("用户名已经存在");
        }//查询邮箱是否存在
        int emailCount = userMapper.checkEmail(user.getEmail());
        if (emailCount > 0) {
            return ServerResponse.createBySuccessMessage("邮箱已经存在");
        }
//        给该用户设置 权限
        user.setRole(Const.Role.ROLE_CUSTOMER);
//        md5 加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
//        保存用户到数据库
        int insertCount = userMapper.insert(user);
        if (insertCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 效验 用户名和 邮箱
     *
     * @param str
     * @param type
     * @return
     */
    public ServerResponse<String> checkValid(String str, String type) {
//        判断 传过来的参数
        if (!type.isEmpty()) {
//            开始校验
            if (Const.USERNAME.equals(type)) {
//                校验用户名
                int count = userMapper.checkUsername(str);
                if (count > 0) {
                    return ServerResponse.createBySuccessMessage("用户名已经存在");
                }
            }
//            校验email
            if (Const.EMAIL.equals(type)) {
                int emailCount = userMapper.checkEmail(str);
                if (emailCount > 0) {
                    return ServerResponse.createBySuccessMessage("邮箱已经存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 获取用户 密码提示问题
     *
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> selectQuestion(String username) {
//       判断用户名 是否存在
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
//         判断 session值
        if (!validResponse.isSuccess()) {
//            用户名不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
//        查询用户密码提示
        String question = userMapper.selectQuestionByUsername(username);
        // 如果 密码提示问题不等于空
        if (!question.equals("")) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");

    }

    /**
     * 效验用户密码提示答案
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
// 查询数据库 提示问题和 答案是否匹配
        int resultCount = userMapper.checkAnswer(username, question, answer);
//        如果查询结果大于0 、说明是对的
        if (resultCount > 0) {
//            说明问题及答案是这个用户的、并且是正确的
            String forgetToken = UUID.randomUUID().toString();
//            把token放入本地缓存中
            TokenCache.setKey("token_" + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }


    /**
     * 重置密码
     *
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetRestPasswod(String username, String passwordNew, String forgetToken) {
//        校验 token是否为空
        if (forgetToken.isEmpty()) {
            return ServerResponse.createByErrorMessage("参数错误、需要传递token");
        }
        //       判断用户名 是否存在
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
//         判断 session值
        if (!validResponse.isSuccess()) {
//            用户名不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //获取 缓存中的token
        String token = TokenCache.getKey("token_" + username);
//效验token

        if (token.isEmpty()) {
            return ServerResponse.createByErrorMessage("toekn无效或者过期");
        }
        if (Objects.equals(token, forgetToken)) {
            //    更新密码
            String md5passwordNew = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5passwordNew);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("toekn错误 ");
        }
        return ServerResponse.createByErrorMessage("修改密码错误 ");
    }

    /**
     * * 登录状态下重置密码功能开发
     *
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //效验 旧密码
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            //    旧密码错误
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        // 更新用户密码
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createBySuccessMessage("密码更新失败");

    }


    /**
     * * 更新用户个人信息
     *
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> update_information(User user) {
        //username 不能被更新
        //email 也要校验、校验新的email是否已经存在、并且存在的eami如果相同的话、不能是当前的这个用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("email已经存在、请更换其他email");
        }
        // 声明一个 新的 的user对象
        User updateUser = new User();
        updateUser.setPassword(user.getPassword());
        updateUser.setId(user.getId());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setPhone(user.getPhone());
        //更新 用户信息
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }


    /**
     * * 获取用户详细信息功能开发
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<User> get_information(Integer userId) {
        // 查询用户
        User user = userMapper.selectByPrimaryKey(userId);

        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        // 把密码置空
        user.setPassword("");
        // 返回用户信息
        return ServerResponse.createBySuccess(user);
    }


    //    backend

    /**
     * 校验是否是管理员
     *
     * @param user
     * @return
     */
    public ServerResponse chackAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();

        }
        return ServerResponse.createByError();
    }
}
