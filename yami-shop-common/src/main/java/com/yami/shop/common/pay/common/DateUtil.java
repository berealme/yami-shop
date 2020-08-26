package com.yami.shop.common.pay.common;

import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @author mowanka
 */
public class DateUtil {

    private static Logger log = Log.get();

    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * 获取当前Date型日期
     * 
     * @return Date() 当前日期
     */
    public static Date getNowDate(){
        return new Date();
    }

    /**
     * 获取当前年份, 格式为yyyy
     *
     * @return String
     */
    public static String getYear(){
        return dateToStr(YYYY, new Date());
    }

    /**
     * 获取当前月份, 格式为yyyy-MM
     * @return
     */
    public static final String getMonth(){
        return dateToStr(YYYY_MM, new Date());
    }

    /**
     * 获取当前日期, 格式为yyyy-MM-dd
     * 
     * @return String
     */
    public static String getDate(){
        return dateToStr(YYYY_MM_DD, new Date());
    }

    /**
     * 获取当前时间, 格式为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static final String getTime(){
        return dateToStr(YYYY_MM_DD_HH_MM_SS, new Date());
    }

    /**
     * 根据Date转换为指定格式String字符串
     * @param format
     * @param date
     * @return
     */
    public static final String dateToStr(final String format, final Date date){
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 根据指定格式String日期字符串转换为Date
     * @param format
     * @param time
     * @return
     */
    public static final Date strToDate(final String format, final String time) {
        try{
            return new SimpleDateFormat(format).parse(time);
        }catch (ParseException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算传入时间 是否在 当前时间戳【之后】
     * @param date 目标时间
     * @return true:传入时间在当前时间之后
     *         false:传入时间在当前时间之前
     */
    public static Boolean checkDateAndNow(Date date) {
        boolean result = false;
        long l = date.getTime() - System.currentTimeMillis();
        if (l > 0) {
            result = true;
        }
        return result;
    }

    /**
     * 计算传入时间与当前时间相差的秒数
     * @param date 目标时间
     * @return 单位[秒]
     */
    public static Long countDateAndNow(Date date) {
        return (date.getTime() - System.currentTimeMillis())/1000;
    }

    /**
     * 获取当前日期的前后几个月日期
     * @param month
     * @return
     */
    public static String getMonDate(int month){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        //获取当前日期前一个月的日期
        c.add(Calendar.MONTH, month);
        String last = format.format(c.getTime());
        return last;
    }

    /**
     * 获取当前日期的前后几个星期日期
     * @param week
     * @return
     */
    public static String getWeekDate(int week){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        //获取当前日期前一个星期的日期
        c.add(Calendar.DATE, week*7);
        String last = format.format(c.getTime());
        return last;
    }

    /**
     * 获取传入日期往前/后推N天的日期
     * @param date 传入日期
     * @param num 正负n天
     * @return Date类型时间
     */
    public static Date getCondDate(Date date, int num){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, num);
        return c.getTime();
    }

    /**
     * 获取当前日期是星期几
     *
     * @param date
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 获取当前时间的后N天  格式：yyyy-MM-dd
     * @param n
     * @return
     */
    public static String afterNDay(int n){
        Calendar c = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        c.setTime(new Date());
        c.add(Calendar.DATE,n);
        Date d2 = c.getTime();
        String s = df.format(d2);
        return s;
    }

    /**
     * 根据日期获取月份
     * @param dateStr 日期 eg:2018-08-08
     * @return
     */
    public static String getChMonth(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        String[] s1 = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二" };
        int i = 0;
        try {
            Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            i = cal.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s1[i]+"月份";
    }
}
