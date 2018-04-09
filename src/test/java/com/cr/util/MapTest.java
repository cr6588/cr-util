package com.cr.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

import com.alibaba.fastjson.JSON;


/**
 * create in 2017年09月11日
 * @category map测试
 * @author chenyi
 */
public class MapTest {

    @Test
    public void test() {
        Map<String, String> treeMap = new TreeMap<>();
        treeMap.put("a", "a");
        treeMap.put("d", "d");
        treeMap.put("c", "c");
        treeMap.put("f", "f");
        treeMap.put("g", "g");
        treeMap.put("e", "e");
        for (Entry<String, String> entry : treeMap.entrySet()) {
            System.out.println(entry.getValue());
            
        }
        //根据key排序
        Map<String, String> sortMap = new TreeMap<>((String o1, String o2) -> o2.compareTo(o1));
        sortMap.putAll(treeMap);
        for (Entry<String, String> entry : sortMap.entrySet()) {
            System.out.println(entry.getValue());
            
        }
        //根据value排序
        //这里将map.entrySet()转换成list
        List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(sortMap.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String,String>>() {
            //升序排序
            public int compare(Entry<String, String> o1,
                    Entry<String, String> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
            
        });
        
        //list排了序但sortMap未排序
        for(Map.Entry<String,String> mapping:list){ 
               System.out.println(mapping.getKey()+":"+mapping.getValue()); 
        }
        //
//                                      new TreeMap<>((String o1, String o2) -> o2.compareTo(o1));
//        sortMap = new TreeMap<>(
//            (Map.Entry<String, String> o1, Map.Entry<String, String> o2) -> o1.getValue().compareTo(o2.getValue())
//        );
    }

    @Test
    public void mapsetTest() {
        Map<String, Object> m = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("list");
        m.put("s", "s");
        m.put("list", list);
        Map<String, Object> h = new HashMap<>();
        h.putAll(m);
        h.put("s", "xxx");
        System.out.println(h.get("s")); //xxx
        System.out.println(m.get("s")); //s
        list = (List<String>) h.get("list");
        System.out.println(JSON.toJSONString(m.get("list")));
        System.out.println(JSON.toJSONString(h.get("list")));
        list.clear();
        System.out.println(JSON.toJSONString(m.get("list")));
    }
}
