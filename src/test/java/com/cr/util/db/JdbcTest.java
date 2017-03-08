package com.cr.util.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

import com.cr.util.FileUtil;
import com.cr.util.random.RandomUtil;
import com.cr.util.random.RandomUtilTest;

public class JdbcTest {
    static String[] nameArray;
    static {
        String path = RandomUtilTest.class.getResource("").getPath() + "常用姓.txt";
        String names = FileUtil.readTxtFile2StrByStringBuffer(path).replace("\r\n", " ");
        nameArray = names.split(" ");
    }
    public static final String sex = "男女 ";
    public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String numberChar = "0123456789";

    @Test
    public void createRandomUserTest() {
        JDBC jdbc = new JDBC("jdbc:mysql://localhost:3307/cy?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "dev", "dev");
        String sql = "INSERT into `user`(username,password,realname,tel,address,company,sex,age,qq,email) values (?,?,?,?,?,?,?,?,?,?);";
        jdbc.setSql(sql);
        try {
            Date start = new Date();
            int executeBatchNum = 10 * 2 * 5 * 2, batchNum = 5000;
            System.out.println(start.toString());
            PreparedStatement pst = jdbc.getPstmt();
            for(int i = 0; i < executeBatchNum ; i++) {
                for(int j = 0; j < batchNum; j++) {
                    pst.setString(1, generateMixString(8));
                    pst.setString(2, generateMixString(8));
                    pst.setString(3, nameArray[RandomUtil.random.nextInt(nameArray.length)] + RandomUtil.getMaxLenthChineseStr(3));
                    pst.setString(4, "1" + RandomUtil.getNumStr(10));
                    pst.setString(5, RandomUtil.getMaxLenthChineseStr(20));
                    pst.setString(6, RandomUtil.getMaxLenthChineseStr(20));
                    pst.setString(7, RandomUtil.getRandomStrByStr(sex));
                    pst.setInt(8, RandomUtil.random.nextInt(60));
                    pst.setString(9, RandomUtil.getNumStr(9));
                    pst.setString(10, generateMixString(15));
                    pst.addBatch();
                }
                pst.executeBatch();
                jdbc.getCon().commit();
                pst.clearBatch();
            }
            Date end = new Date();
            System.out.println(end.toString());
            System.out.println("insert " +  executeBatchNum * batchNum + " time " + (end.getTime() - start.getTime()));
            jdbc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateMixString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(letterChar.length())));
        }
        return sb.toString();
    }

    @Test
    public void initUserTest() {
        try {
            JDBC.initUser(null, "3306",  null,  "654321");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void finallyTest() throws Exception {
        try {
            throw new Exception();
        } catch(Exception e) {
            throw new Exception();
        } finally {
            // TODO: handle finally clause
            System.out.println("finally");
        }
    }

}
