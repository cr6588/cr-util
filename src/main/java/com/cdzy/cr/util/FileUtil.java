package com.cdzy.cr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    public static String readFile2String(String path) {
        if(path == null) {
            return null;
        }
        File file = new File(path);
        if(!file.exists()) {
            return null;
        }
        String str = "";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] bytes = new byte[1024 * 4];
            int len;
            while((len = fis.read(bytes)) != -1) {
                str += new String(bytes, 0, len, "utf8");
            }
        } catch (Exception e) {
            logger.error("readFile2String error : " + e.getMessage());
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("readFile2String close FileInputStream error : " + e.getMessage());
                }
            }
        }
        return str;
    }
}
