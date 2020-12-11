package com.cr.util;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import org.junit.Test;


/**
 * create in 2020年09月24日
 * @category TODO
 * @author chenyi
 */
public class UrlTest {

    @Test
    public void test() {
        //正确 11642622491705831_%E5%BF%AB%E8%B6%8A%E8%BE%BEBO-UK%20%E7%AC%AC26%E6%89%B9%2B26-1%E6%89%B9%20%E7%89%A9%E6%B5%81%E5%94%9B%E5%A4%B4%EF%BC%883%E7%AE%B1%EF%BC%89.pdf
        //    11642622491705831_%BF%EC%D4%BD%B4%EFBO-UK+%B5%DA26%C5%FA%2B26-1%C5%FA+%CE%EF%C1%F7%DF%E9%CD%B7%A3%A83%CF%E4%A3%A9.pdf
        //    11642622491705831_%E5%BF%AB%E8%B6%8A%E8%BE%BEBO-UK+%E7%AC%AC26%E6%89%B9%2B26-1%E6%89%B9+%E7%89%A9%E6%B5%81%E5%94%9B%E5%A4%B4%EF%BC%883%E7%AE%B1%EF%BC%89.pdf
        String url = "#11642622491705831_快越达BO-UK 第26批+26-1批 物流唛头（3箱）.pdf";
        try {
            System.out.println(URLEncoder.encode(url, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
