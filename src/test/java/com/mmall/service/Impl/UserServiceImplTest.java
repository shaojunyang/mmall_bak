package com.mmall.service.Impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Created by yangshaojun on 2017/12/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class UserServiceImplTest {

    //     注入dao
    @Autowired
    private IUserService userService;

    //    注入dao
    @Autowired
    private UserMapper userMapper;

    //    日志
    private Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);

    @Test
    public void testLogin() throws Exception {
        ServerResponse<User> login = userService.login("admin", "111");
        System.out.println(login);

    }

    @Test
    public void testRegister() throws Exception {
        User user = new User("imooc", "123456");
        ServerResponse<String> register = userService.register(user);
        System.out.println(register);
    }

    @Test
    public void testCheckValid() throws Exception {
    }

    @Test
    public void testSelectQuestion() throws Exception {
        // TODO: 2017/12/5  待测试
        ServerResponse<String> question = userService.selectQuestion("soonerbetter");

        System.out.println(question);
    }

    @Test
    public void testCheckAnswer() throws Exception {
        ServerResponse<String> soonerbetter = userService.checkAnswer("soonerbetter", "105204", "10204");
        System.out.println(soonerbetter);
    }


    public void  test1(String str) {
        if (Objects.equals(str,null)) {
            System.out.println(111);
        }
    }

    @Test
    public void test2() {
        test1(null);
    }
}