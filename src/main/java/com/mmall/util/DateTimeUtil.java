package com.mmall.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 时间转换工具
 *
 * @author
 * @create 2017-12-06 下午4:11
 **/

public class DateTimeUtil {
//     使用 joda-time 开源包做转换

    public static final String STANDARD_FORMAT="YYYY-MM-dd HH:mm:ss";

    /**
     * 字符串转换时间方法
     *
     * @param dateTimeStr
     * @param formatStr
     * @return
     */
    public static Date strToDate(String dateTimeStr, String formatStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dataTime = dateTimeFormatter.parseDateTime(dateTimeStr);

        return dataTime.toDate();
    }

    /**
     * 字符串转换时间方法
     *
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {
        if (date == null) {
            return "";
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }


    /**
     * 字符串转换时间方法
     *
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dataTime = dateTimeFormatter.parseDateTime(dateTimeStr);

        return dataTime.toDate();
    }

    /**
     * 字符串转换时间方法
     *
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date, String formatStr) {
        if (date == null) {
            return "";
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtil.dateToStr(new Date(),"YYYY-MM-dd HH:mm:ss"));
        System.out.println(DateTimeUtil.strToDate("2010-11-01 11:11:11","YYYY-MM-dd HH:mm:ss"));
    }

}
