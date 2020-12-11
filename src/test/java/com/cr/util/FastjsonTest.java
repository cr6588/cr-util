package com.cr.util;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * create in 2020年04月16日
 * @category TODO
 * @author chenyi
 */
public class FastjsonTest {

    @Test
    public void nullTest() throws Exception {
        //https://github.com/alibaba/fastjson/wiki/WriteNull_cn
        //https://github.com/alibaba/fastjson/issues/196
        //https://segmentfault.com/q/1010000004216492
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        JSONObject v = new JSONObject(); //jsonobject 无用，因为无法判断remark类型，WriteNullStringAsEmpty针对的是Field
        v.put("remark", null);
        String text = JSON.toJSONString(v, mapping, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullStringAsEmpty); //
        Assert.assertEquals("{\"remark\":\"\"}", text);
    }

    @Test
    public void test() throws Exception {
        V0 v = new V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);

        String text = JSON.toJSONString(v, mapping, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullStringAsEmpty);
        Assert.assertEquals("{\"value\":\"\"}", text);
    }

    public static class V0 {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
