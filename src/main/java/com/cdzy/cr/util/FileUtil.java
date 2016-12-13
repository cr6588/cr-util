package com.cdzy.cr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {
    /**
     * 读取文件内容到内存中用此方法有严重问题存在断码问题 用原始的字节流读取内容 而不是字符流 在个别字节没有读取完毕的情况下就进行输出 当然会出错
     * @param path
     * @return
     */
    @Deprecated
    public static String readFile2String(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        String str = "";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] bytes = new byte[1];
            int len;
            while ((len = fis.read(bytes)) != -1) {
                String temp = new String(bytes, 0, len, "utf8");
                str += temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    /**
     * 读取文本内容用字符流不要使用字节流
     * @param path
     * @return
     */
    public static String readTxtFile2String(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        String str = "";
        FileReader fis = null;
        try {
            fis = new FileReader(file);
            char[] chars = new char[1];
            int len;
            while ((len = fis.read(chars)) != -1) {
                String temp = new String(chars, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    /**
     * 读取文本内容用字符流不要使用字节流
     * @param path
     * @return
     */
    public static String readTxtFile2StrByStringBuilder(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        FileReader fis = null;
        try {
            fis = new FileReader(file);
            char[] chars = new char[1];
            int len;
            while ((len = fis.read(chars)) != -1) {
                sb.append(chars, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 读取文本内容用字符流不要使用字节流
     * @param path
     * @return
     */
    public static String readTxtFile2StrByStringBuffer(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        FileReader fis = null;
        try {
            fis = new FileReader(file);
            char[] chars = new char[1];
            int len;
            while ((len = fis.read(chars)) != -1) {
                sb.append(chars, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
