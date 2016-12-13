package com.cdzy.cr.util;

import java.util.Date;

import org.junit.Test;

public class DateUtilTest {

//    @Test
    public void test() {
        Date date = new Date();
        System.out.println(DateUtil.getCurrentMonth());
        System.out.println(DateUtil.dateFormat(date));
        System.out.println(DateUtil.dateFormat(DateUtil.addMonth(date, 5)));
    }

//    @Test
    public void getDateListFromStart2End() {
        Date date = new Date();
        for(int i = 0; i < 30; i++) {
            System.out.println(DateUtil.getWeekOfDate(date));
            date = DateUtil.addDay(date, 1);
            
        }
    }
    
    @Test
    public void getFirstDayInMonthTest() {
        System.out.println(DateUtil.getFirstDayInMonth(new Date()));
    }
}
