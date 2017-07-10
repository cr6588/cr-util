package com.cr.util;

import org.junit.Test;

/**
 * create in 2017年06月22日
 * @category TODO
 * @author chenyi
 */
public class RegexTest {

    @Test
    public void replaceTest() {
        String str = "'平认可物流 使用json串保存{\"平台ID_1\":平台认可物流ID}'".replace("\"", "\\\"");
        System.out.println(str);
    }
}
