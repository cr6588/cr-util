package com.cr.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1; // 由于月份是从0开始的所以加1
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 增加月
     * @param date
     * @param month
     * @return
     */
    public static Date addMonth(Date date, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);
        return cal.getTime();
    }
    /**
     * 增加日
     * @param date
     * @param month
     * @return
     */
    public static Date addDay(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }


    public static String dateFormat(Date date) {
        return dateFormat.format(date);
    }

    public static String dateTimeFormat(Date date) {
        return dateTimeFormat.format(date);
    }

    public static int getYearByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getMonthByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    public static int getDayByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH) + 1;
    }

    /**
     * 获取结束日期减去开始日期的天数
     * @param beginDateStr
     * @param endDateStr
     * @return
     * @throws ParseException
     */
    public static long getDaySub(String beginDateStr, String endDateStr) throws ParseException {
        long day = 0;
        Date beginDate;
        Date endDate;
        beginDate = dateFormat.parse(beginDateStr);
        endDate = dateFormat.parse(endDateStr);
        day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * 将年月日转化为yyyy-MM-dd格式
     * @param year
     * @param month
     * @param day
     * @return
     * @throws Exception 
     */
    public static String getFormatDateStrByYearMonthDay(int year, int month, int day) throws Exception {
        if(year < 1000 || month < 1 || month > 12 || day < 1 || day >31) {
            throw new Exception("日期数据不合法");
        }
        String str = year + "-";
        if(month < 10) {
            str += "0";
        }
        str += month;
        if(day < 10) {
            str += "0";
        }
        str += day;
        return str;
    }

    /**
     * 是否是yyyy-MM-dd格式日期字符串
     * @param dateStr
     * @return
     */
    public static boolean isDateStr(String dateStr) {
        if(dateStr == null) {
            return false;
        }
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    /**
     * 获取当前日期是星期几
     * @param date
     * @return 当前日期是星期几
     */
    public static int getWeekOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK - 1);
        if (w < 0)
            w = 0;
        return w;
    }
    /**
     * 获取开始日期到结束日期中星期数在weekList中的日期
     * @param startDate
     * @param endDate
     * @param weekList  1--7分别对应表示星期1到星期日 选择多个时中间用逗号分隔，例如：选择了周一、周二、周五，则该字段的值："1,2,5"
     * @return
     */
    public static List<String> getDateListFromStart2End(String startDate, String endDate, String weekList) {
       return null;
    }

    /**
     * 获取月的第一天
     */
    public static String getFirstDayInMonth(Date date) {
        return new SimpleDateFormat("yyyy-MM").format(date) + "-01";
    }
}
