package com.facewarrant.fw.util;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by licrynoob on 2016/7/18 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 时间工具类
 */
public class TimeUtil {

    /**
     * 时间日期格式化到年月日时分秒.
     */
    public static final String FORMAT_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    /**
     * 时间日期格式化到年月日
     */
    public static final String FORMAT_YMD = "yyyy-MM-dd";
    /**
     * 时间日期格式化到年月
     */
    public static final String FORMAT_YM = "yyyy-MM";
    /**
     * 时间日期格式化到年月日时分
     */
    public static final String FORMAT_YMD_HM = "yyyy-MM-dd HH:mm";

    /**
     * 时间日期格式化到月日
     */
    public static final String FORMAT_MD = "MM/dd";
    /**
     * 时分秒
     */
    public static final String FORMAT_HMS = "HH:mm:ss";
    /**
     * 时分
     */
    public static final String FORMAT_HM = "HH:mm";
    /**
     * 上午
     */
    public static final String AM = "AM";
    /**
     * 下午
     */
    public static final String PM = "PM";

    /**
     * 设置时间格式
     *
     * @param timeMillis 时间
     * @param format     所需日期时间格式
     * @return TimeStr
     */
    public static String formatTime(long timeMillis, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return simpleDateFormat.format(new Date(timeMillis));
    }

    /**
     * 设置时间格式
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatTime(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前日期时间
     *
     * @param format 所需日期时间格式
     * @return TimeStr
     */
    public static String getCurrentTime(String format) {
        return formatTime(System.currentTimeMillis(), format);
    }

    /**
     * 获取文件最后变更时间
     *
     * @param path 文件路径
     * @return 文件最后变更时间
     */
    public static String getModifiedFileTime(String path) {
        File file = new File(path);
        if (file.exists()) {
            long time = file.lastModified();
            return formatTime(time, FORMAT_YMD);
        }
        return "1970-01-01";
    }

    /**
     * 比较时间先后
     *
     * @param data1  data1
     * @param data2  data2
     * @param format format
     * @return 1:data1大于data2 -1:data1小于data2
     */
    public static int compareTime(String data1, String data2, String format) {
        DateFormat df = new SimpleDateFormat(format, Locale.CHINA);
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = df.parse(data1);
            dt2 = df.parse(data2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dt1 != null && dt2 != null) {
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * 获取当前年份
     */
    public static int getCurrentYear() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        return year;
//        month = c.grt(Calendar.MONTH)
//        day = c.get(Calendar.DAY_OF_MONTH)
//        取得系统时间：hour = c.get(Calendar.HOUR_OF_DAY);
//        minute = c.get(Calendar.MINUTE)
//        Calendar c = Calendar.getInstance();
//        取得系统日期:year = c.get(Calendar.YEAR)
//        month = c.grt(Calendar.MONTH)
//        day = c.get(Calendar.DAY_OF_MONTH)
//        取得系统时间：hour = c.get(Calendar.HOUR_OF_DAY);
//        minute = c.get(Calendar.MINUTE)
    }
    /**
     * 获取当前月份
     */
    public static int getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        return month;
//        month = c.grt(Calendar.MONTH)
//        day = c.get(Calendar.DAY_OF_MONTH)
//        取得系统时间：hour = c.get(Calendar.HOUR_OF_DAY);
//        minute = c.get(Calendar.MINUTE)
//        Calendar c = Calendar.getInstance();
//        取得系统日期:year = c.get(Calendar.YEAR)
//        month = c.grt(Calendar.MONTH)
//        day = c.get(Calendar.DAY_OF_MONTH)
//        取得系统时间：hour = c.get(Calendar.HOUR_OF_DAY);
//        minute = c.get(Calendar.MINUTE)
    }
}

