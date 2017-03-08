package com.cr.util;

import org.junit.Assert;
import org.junit.Test;

import com.cr.util.StringUtil;

public class StringUtilTest {

    @Test
    public void test() {
        Assert.assertEquals("012", StringUtil.getOnlyNumber("asfasf012fasdf"));
        Assert.assertEquals(null, StringUtil.getOnlyNumber("asfasf012fa1sdf"));
    }

    @Test
    public void getFirstNumFromStrTest() {
        Assert.assertEquals("12", StringUtil.getFirstNumFromStr("012asdf"));
        Assert.assertEquals("12", StringUtil.getFirstNumFromStr("12asdf"));
        Assert.assertEquals("12", StringUtil.getFirstNumFromStr("12"));
        Assert.assertEquals("12", StringUtil.getFirstNumFromStr("12 元"));
        Assert.assertEquals("12", StringUtil.getFirstNumFromStr("000012 元"));
        Assert.assertEquals("0", StringUtil.getFirstNumFromStr("0000 元"));
        Assert.assertEquals("1", StringUtil.getFirstNumFromStr("01 元"));
    }

}
