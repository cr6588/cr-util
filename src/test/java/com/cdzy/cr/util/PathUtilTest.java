package com.cdzy.cr.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class PathUtilTest {

    @Test
    public void test() {
        System.out.println(ReflectUtil.class.getResource("").getPath());// /D:/workspace/cr-util/target/test-classes/com/cdzy/cr/util/
        System.out.println(ReflectUtil.class.getClassLoader().getResource("").getPath());
//        System.out.println(ReflectUtil.class.getClassLoader().getResource("/").getPath());
        System.out.println(ReflectUtil.class.getResource("/").getPath());// /D:/workspace/cr-util/target/test-classes/
        System.out.println(ReflectUtil.class.getResource("/").getPath());///D:/workspace/cr-util/target/test-classes/
        System.out.println(ReflectUtil.class.getResourceAsStream("/tempDir.properties"));// 读取classes中不是test-classes /D:/workspace/cr-util/target/classes/tempDir.properties
        ReflectUtil.class.getResourceAsStream("/tempDir.properties");
    }

}
