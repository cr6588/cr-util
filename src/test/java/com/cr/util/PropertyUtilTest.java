package com.cr.util;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;

import org.junit.Test;

import com.cr.util.db.JDBC;

public class PropertyUtilTest {

    @Test
    public void getValueByKeyTest() {
        System.out.println(PropertyUtil.getValueByKey("D:/workspace/cr-util/target/classes/tempDir.properties", "tempImgDir"));
    }

    @Test
    public void getProperties() throws UnsupportedEncodingException {
        Properties properties = PropertyUtil.getProperties("D:/git/cr-util/src/main/resources/zh_CN.properties");
        Iterator<Object> it = properties.keySet().iterator();
        JDBC jdbc = new JDBC("jdbc:mysql://localhost:3306/erp?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "dev", "dev");
        while (it.hasNext()) {
            String key =  (String)it.next();
            String value  = new String(properties.getProperty(key).getBytes("ISO-8859-1"), "UTF-8");
            String sql = "INSERT INTO `sys_i18n`(code, text) VALUES ( '" + key + "', '" + value + "');";
            try {
                jdbc.getCon().prepareStatement(sql).execute();;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
