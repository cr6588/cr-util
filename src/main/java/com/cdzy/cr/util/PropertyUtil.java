package com.cdzy.cr.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * 当需要读取jar包中的资源文件时利用XXX.class.getResourceAsStream("/xxxx.properties")获取properties文件输入流
     * 从properties文件输入流中根据key获取值
     * @param inputStream 输入流
     * @param key
     * @return
     */
    public static String getValueByKey(InputStream inputStream, String key) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
