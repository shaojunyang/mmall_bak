package com.mmall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 配置 工具  读取  Properties文件中的key
 *
 * @author
 * @create 2017-12-06 下午3:48
 **/

public class PropertiesUtil {
    // 两个常量
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties props;


    // 静态块
    static {
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), "UTF-8"));
        } catch (IOException e) {
            // 打印日志并打印堆栈
            logger.error("配置文件读取异常", e);
        }
    }

    /**
     * 获取 配置文件中的value
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        String value = props.getProperty(key.trim());
        if (value.isEmpty()) {
            return null;
        }
        return value.trim();
    }

    /** 重载方法
     * 获取 配置文件中的value
     *
     * @param key
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        String value = props.getProperty(key.trim());
        if (value.isEmpty()) {
            value = defaultValue;
        }
        return value.trim();
    }


}
