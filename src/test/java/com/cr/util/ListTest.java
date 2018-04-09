package com.cr.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;


/**
 * create in 2018年03月28日
 * @category TODO
 * @author chenyi
 */
public class ListTest {

    @Test
    public void test() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        for (String string : list) {
            if (string.equals("1")) {
                list.remove(string);
                
            }
        }
        System.out.println(JSON.toJSONString(list));
    }

}
