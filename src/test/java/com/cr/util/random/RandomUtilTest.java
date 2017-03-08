package com.cr.util.random;

import org.junit.Test;

public class RandomUtilTest {

    @Test
    public void getChineseStrTest() {
        // String cc="\u4fdd";
        // System.out.print(cc);
        // System.out.println((char)Integer.parseInt("4e00", 16));
//        String path = this.getClass().getResource("").getPath() + "常用姓.txt";
//        String names = FileUtil.readTxtFile2StrByStringBuffer(path).replace("\r\n", " ");
//        String[] nameArray = names.split(" ");
//        System.out.println(names);
        for (int i = 0; i < 20; i++) {
            System.out.println(RandomUtil.getMaxLenthChineseStr(3));
        }
    }
}
