package com.cdzy.cr.util;

import org.junit.Test;

import com.cr.util.PropertyUtil;

public class PropertyUtilTest {

    @Test
    public void getValueByKeyTest() {
        System.out.println(PropertyUtil.getValueByKey("D:/workspace/cr-util/target/classes/tempDir.properties", "tempImgDir"));
    }

}
