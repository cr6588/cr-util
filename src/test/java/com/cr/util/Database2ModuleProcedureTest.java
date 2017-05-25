package com.cr.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

/**
 * create in 2017年04月20日
 * @category 生成模块建表存储过程
 * @auther chenyi
 */
public class Database2ModuleProcedureTest {

    String enter = "\n";            //换行
    String dBName = "mappertest";   //数据库名称
    String user = "dev";            //用户名
    String password = "dev";        //密码
    String moduleName = "logistics";//模块名称

    @Test
    public void printProcedureSqltest() {
        try {
            //表名若已经存在多余后缀例如_1请在生成之后删除
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dBName + "?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", user, password);
            String sql = "show tables;";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            String str = "";
            str += "DROP PROCEDURE IF EXISTS `create_" + moduleName + "_table`;" + enter;
            str += "create procedure `create_" + moduleName + "_table` ( in dbName varchar(20),in mark  varchar(20))" + enter;
            str += "BEGIN" + enter;
            str += "    SET @dbName = dbName;" + enter;  
            str += "    SET @mark = mark;" + enter;
            while (rs.next()) {
                String tableName = rs.getString(1);
                str += "    SET @insertSql = CONCAT('CREATE TABLE `', @dbName ,'`." + tableName + "_', @mark,\"(" + enter;
                sql = "SHOW full fields FROM " + tableName;
                ResultSet res = con.createStatement().executeQuery(sql);
                String primaryKey = "";
                while (res.next()) {
                    String column = res.getString(1);
                    if(res.getString(5).equals("PRI")) {
                        primaryKey += column;
                    }
                    str += "        `" + column + "` " + res.getString(2) + " " + (res.getString(4).equals("NO") ? "NOT NULL " : " ") +  (res.getObject(6) != null ? " DEFAULT '" + res.getObject(6) +"'" : " ") + " COMMENT '" + res.getString(9) + "'," + enter;
                }
                str += "        PRIMARY KEY (`" + primaryKey + "`)" + enter;
                str += "        ) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci" + enter;
                str += "        ROW_FORMAT=COMPACT;\");" + enter;
                str += "    select @insertSql;" + enter;
                str += "    PREPARE stmtinsert FROM @insertSql;" + enter;
                str += "    EXECUTE stmtinsert;" + enter;
                str += "    DEALLOCATE PREPARE stmtinsert;" + enter;
            }
            str += "END;" + enter;
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
