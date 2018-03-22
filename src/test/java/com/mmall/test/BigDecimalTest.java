package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * 测试
 *
 * @author yangshaojun
 * @create 2017-12-07 上午11:52
 **/

public class BigDecimalTest {

    @Test
    public void test1() {
        System.out.println(0.05 + 0.01);
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.05");
        System.out.println(b1.add(b2));
        System.out.println("你好伟大");
    }
}
