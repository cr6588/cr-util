package com.cdzy.cr.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
    /**
     * 从properties文件中根据key获取值
     * @param filePathName 文件绝对路径
     * @param key
     * @return
     */
    public static String getValueByKey(String filePathName, String key) {
        Properties properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePathName);
            properties.load(fis);
            return properties.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
