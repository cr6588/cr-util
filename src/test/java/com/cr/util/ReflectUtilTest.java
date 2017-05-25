package com.cr.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.cr.bean.ExistField;

/**
 * create in 2017年05月25日
 * @category TODO
 * @auther chenyi
 */
public class ReflectUtilTest {

    private void setAddObjTestField(Object obj, String str) throws Exception {
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("id")) {
                    continue;
                }
                if (!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                
                if(field.get(obj) instanceof String) {
                    field.set(obj, str);
                } else if (field.get(obj) instanceof Integer) {
                    field.set(obj, 1);
                } else if (field.get(obj) instanceof Long) {
                    field.set(obj, 1L);
                } else if (field.getType() == Date.class) {
                    field.set(obj, new Date());
                } else if (field.getType() == Boolean.TYPE || field.getType() == Boolean.class) {
                    field.set(obj, true);
                }
                field.setAccessible(false);
            }
        }
    }

    @Test
    public void test() throws Exception {
        ExistField field  = new ExistField();
        setAddObjTestField(field, "ss");
        System.out.println(JSON.toJSONString(field));
    }

}
