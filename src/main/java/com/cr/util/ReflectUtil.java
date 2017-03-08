package com.cr.util;

import java.lang.reflect.Field;

public class ReflectUtil {
    /**
     * 设置对象中字符串属性值为空串，无法设置继承对象与子对象中的字符串属性
     * @param o
     */
    public static void setStringFieldBlankValue(Object o) {
        if( o == null) {
            return;
        }
        Field[] fields = o.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getGenericType().toString().equals("class java.lang.String")) {
                try {
                    fields[i].setAccessible(true);
                    if(fields[i].get(o) == null) {
                        fields[i].set(o, "");
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
