package com.cr.util.db;

import java.sql.CallableStatement;
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
            JDBC jdbc = new JDBC(null, "3308" , "root", "123456");
            jdbc.initUser("654321");
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

    @Test
    public void excuteTest() {
        JDBC jdbc = new JDBC("jdbc:mysql://localhost:3306/jty_basic?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "dev", "dev");
        String sql ="{call jty_basic.create_order_table (?, ?)}"; //存储过程调用，模块库所在的mysql必须含有存储过程
        try {
            CallableStatement callableStatement = jdbc.getCstmt(sql);
            callableStatement.setString(1, "jty_order_x");
            callableStatement.setString(2, "1");  
            callableStatement.execute();  
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void delDbTest() {
        JDBC jdbc = new JDBC("jdbc:mysql://127.0.0.1:3306/test?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "root", "tTdAdf212");
        String sql ="show databases;";
        jdbc.setSql(sql);
        try {
            PreparedStatement pst = jdbc.getPstmt();
            ResultSet rs = pst.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                String dbName = rs.getString(1);
                if(dbName.startsWith("erp")) {
                    sb.append("drop database " + dbName + ";");
                }
            }
            pst.execute(sb.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void truncateTableTest() {
        JDBC jdbc = new JDBC("jdbc:mysql://118.123.12.120:3306/test?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "root", "tTdAdf212");
        String sql ="show databases;";
        jdbc.setSql(sql);
        try {
            PreparedStatement pst = jdbc.getPstmt();
            ResultSet rs = pst.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                String dbName = rs.getString(1);
                if(dbName.startsWith("db_") && dbName.compareTo("db_45892611181678") > 0) {
                    String showTable = "show tables from " + dbName + ";";
                    ResultSet tableRs = jdbc.getCon().prepareStatement(showTable).executeQuery();
                    while(tableRs.next()) {
                        String tableName = tableRs.getString(1);
                        jdbc.getCon().prepareStatement("TRUNCATE " + dbName + "." + tableName + ";").execute();
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void delAllTableTest() {
        String[] dbins = {"50845485686899","50845486247027","50845486303347","50845486775411","50845486995571","50845487445107"};
        String[] tableId = {"401596082512435","401596092113459","401596092449331","401596100379187","401596103959091","401596110791219"};
        JDBC jdbc = new JDBC("jdbc:mysql://114.115.139.202:8635/saturn?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "saturn", "x1!1Cbdc20X16240");
//        JDBC jdbc = new JDBC("jdbc:mysql://localhost:3306/base?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "root", "tTdAdf212");
        String sql ="show tables;";
        jdbc.setSql(sql);
        try {
            PreparedStatement pst = jdbc.getPstmt();
            ResultSet rs = pst.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                String tableName = rs.getString(1);
//                for(String t : tableId) {
//                    if(tableName.endsWith(t)) {
                        System.out.println(tableName);
                        sb.append("drop table " + tableName + ";");
//                        break;
//                    }
//                }
            }
            System.out.println(sb);
            pst.execute(sb.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void callProTest() {
        JDBC jdbc = new JDBC("jdbc:mysql://localhost:3306/base?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "dev", "dev");
        String sql ="CALL create_warehouse_table ('base', '33594154876331');";
        jdbc.setSql(sql);
        try {
            PreparedStatement pst = jdbc.getPstmt();
            pst.execute();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //CALL base.create_order_table ('db_33594154843563', '33594154876331');
    //CALL base.create_order_table ('db_33594154843563', '33594154876331');
    
}
