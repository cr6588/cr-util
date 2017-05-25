package com.cr.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Test;

/**
 * create in 2017年04月20日
 * @category mybatis接口xml生成
 * @auther chenyi
 */
public class MapperTest {

    @Test
    public void test() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mappertest?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "dev", "dev");
            String sql = "show tables;";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Map<String, List<String>> tables = new HashMap<>();
            while (rs.next()) {
                String tableName = rs.getString(1);
                sql = "SHOW columns FROM " + tableName;
                List<String> columns = new ArrayList<>();
                ResultSet res = con.createStatement().executeQuery(sql);
                while (res.next()) {
                    String column = res.getString(1);
                    columns.add(column);
                }
                tables.put(tableName, columns);
            }
            createMapperXml(tables);
            createMapperDao(tables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existStrInArray(String str, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (str.equals(array[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取表中文map<数据库表名,中文表名>
     * @return
     */
    public Map<String, String> getTableZhCn() {
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(this.getClass().getResource("").getPath() + "data/表设计.xls"));
            // 得到Excel工作簿对象
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            // 得到Excel工作表对象
            int sheetCount = wb.getNumberOfSheets();
            String[] logName = { "系统物流公司", "系统物流渠道" };
            List<String> existTable = new ArrayList<>();
            Map<String, String> existTableName = new HashMap<>();
            for (int i = 0; i < sheetCount; i++) {
                boolean canStart = false;
                HSSFSheet sheet = wb.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                if (existStrInArray(sheetName, logName)) {
                    existTable.add(sheetName);
                    int rowNum = sheet.getPhysicalNumberOfRows();
                    String tableName = "";
                    for (int j = 1; j < rowNum; j++) {
                        HSSFRow row = sheet.getRow(j);
                        if (row != null) {
                            // int cellNum = row.getPhysicalNumberOfCells();
                            for (int k = 0; k < 2 && !canStart; k++) { // k<cellNum
                                                                       // TODO
                                                                       // 物流渠道有问题待检查
                                HSSFCell cell = row.getCell(k);
                                if (cell != null) {
                                    String cellValue = cell.getStringCellValue();
                                    if (tableName.equals("")) {
                                        if (existTableName.get(cellValue) != null) {
                                            throw new Exception("当前表" + sheetName + ":" + cellValue + "与" + existTableName.get(cellValue) + "的数据库表名" + cellValue + "名称一样，请检查！");
                                        }
                                        tableName = cellValue;
                                        existTableName.put(tableName, sheetName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return existTableName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createMapperDao(Map<String, List<String>> tables) {
        String dao = "";
        dao += "package com.sjdf.erp.logistics.dao;" + enter + enter;
        dao += "import java.util.List;" + enter;
        dao += "import java.util.Map;" + enter;
        dao += "import com.sjdf.erp.facade.vo.PagerInfo;" + enter;
        dao += "public interface LogisticsDao {" + enter;
        String daoExample = FileUtil.readTxtFile2StrByStringBuilder(this.getClass().getResource("").getPath() + "/data/LogisticsDao.txt");
        Iterator<Entry<String, List<String>>> it = tables.entrySet().iterator();
        Map<String, String> tableZhMap = getTableZhCn();
        for (; it.hasNext();) {
            Entry<String, List<String>> entry = it.next();
            String tableName = entry.getKey();
            String className = getClassNameByTableName(tableName);
            String lowerClassName = getLowerClassName(className);
            dao += daoExample.replace("PubLogisticsCompany", className).replace("pubLogisticsCompany", lowerClassName).replace("物流公司", tableZhMap.get(tableName));
            // break;
        }
        dao += "}";
//         System.out.println(dao);
    }

    public String getClassNameByTableName(String tableName) {
        String[] tableNameArray = tableName.split("_");
        String className = "";
        for (int j = 0; j < tableNameArray.length; j++) {
            className += tableNameArray[j].substring(0, 1).toUpperCase() + tableNameArray[j].substring(1);
        }
        return className;
    }

    public String getLowerClassName(String className) {
        String classNameLowerCaseFirstWord = className.substring(0, 1).toLowerCase() + className.substring(1);
        return classNameLowerCaseFirstWord;
    }

    String enter = "\n";

    /**
     * 生成mapper xml
     * @param tables
     */
    private void createMapperXml(Map<String, List<String>> tables) {
        String mapperXML = "";
        mapperXML += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + enter;
        mapperXML += "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >" + enter;
        mapperXML += "<mapper namespace=\"com.sjdf.erp.logistics.dao.LogisticsDao\">" + enter;
        Iterator<Entry<String, List<String>>> it = tables.entrySet().iterator();
        for (; it.hasNext();) {
            Entry<String, List<String>> entry = it.next();
            String tableName = entry.getKey();
            String className = getClassNameByTableName(tableName);
            String classNameLowerCaseFirstWord = getLowerClassName(className);
            mapperXML += enter;
            mapperXML += "    <!--     " + className + " start -->" + enter;
            mapperXML += createResultMapXml(classNameLowerCaseFirstWord, entry.getValue());
            mapperXML += createColumnListXml(classNameLowerCaseFirstWord, entry.getValue());
            mapperXML += createAddXml(className, classNameLowerCaseFirstWord, tableName, entry.getValue());
            mapperXML += createUpdXml(className, classNameLowerCaseFirstWord, tableName, entry.getValue());
            mapperXML += createDelXml(className, tableName);
            mapperXML += createSelectConditionXml(classNameLowerCaseFirstWord, entry.getValue());
            mapperXML += createSelectXml(className, classNameLowerCaseFirstWord, tableName);
            mapperXML += createSelectListXml(className, classNameLowerCaseFirstWord, tableName);
//            mapperXML += createSelectCountXml(className, classNameLowerCaseFirstWord, tableName);
            mapperXML += "    <!--     " + className + " end -->" + enter;
            mapperXML += enter;
            // break;
        }
        mapperXML += "</mapper>";
        System.out.println(mapperXML);
    }

    /**
     * 生成查询数量xml
     * @param className
     * @param classNameLowerCaseFirstWord
     * @param tableName
     * @return
     */
    private String createSelectCountXml(String className, String classNameLowerCaseFirstWord, String tableName) {
        String str = "";
        str += "    <select id=\"get" + className + "ListCnt\" parameterType=\"map\" resultType=\"long\">" + enter;
        str += "        select count(*) from `" + tableName + "`" + enter;
        str += "        <include refid=\"" + classNameLowerCaseFirstWord + "SelectCondition\" />" + enter;
        str += "    </select>" + enter;
        return str;
    }

    /**
     * 生成查询列表xml
     * @param className
     * @param classNameLowerCaseFirstWord
     * @param tableName
     * @return
     */
    private String createSelectListXml(String className, String classNameLowerCaseFirstWord, String tableName) {
        String str = "";
        str += "    <select id=\"get" + className + "List\" parameterType=\"map\" resultMap=\"" + classNameLowerCaseFirstWord + "Map\">" + enter;
        str += "        select" + enter;
        str += "            <include refid=\"" + classNameLowerCaseFirstWord + "ColumnList\" />" + enter;
        str += "        from `" + tableName + "`" + enter;
        str += "        <include refid=\"" + classNameLowerCaseFirstWord + "SelectCondition\" />" + enter;
        str += "    </select>" + enter;
        return str;
    }

    /**
     * 生成查询xml
     * @param className
     * @param classNameLowerCaseFirstWord
     * @param tableName
     * @return
     */
    private String createSelectXml(String className, String classNameLowerCaseFirstWord, String tableName) {
        String str = "";
        str += "    <select id=\"get" + className + "\" parameterType=\"map\" resultMap=\"" + classNameLowerCaseFirstWord + "Map\">" + enter;
        str += "        select" + enter;
        str += "            <include refid=\"" + classNameLowerCaseFirstWord + "ColumnList\" />" + enter;
        str += "        from `" + tableName + "`" + enter;
        str += "        <include refid=\"" + classNameLowerCaseFirstWord + "SelectCondition\" />" + enter;
        str += "    </select>" + enter;
        return str;
    }

    /**
     * 生成查询条件xml
     * @param classNameLowerCaseFirstWord
     * @param columns
     * @return
     */
    private String createSelectConditionXml(String classNameLowerCaseFirstWord, List<String> columns) {
        String str = "";
        str += "    <sql id=\"" + classNameLowerCaseFirstWord + "SelectCondition\">" + enter;
        str += "        <where>" + enter;
        str += "            `deleted` = ${@com.sjdf.erp.common.dictionary.bean.WhetherState@NO}" + enter;
        for (String column : columns) {
            str += "            <if test=\"" + column + " != null\"> and " + column + " = #{" + column + "}</if>" + enter;
        }
        str += "        </where>" + enter;
        str += "    </sql>" + enter;
        return str;
    }

    /**
     * 生成删除xml
     * @param className
     * @param tableName
     * @return
     */
    private String createDelXml(String className, String tableName) {
        String str = "";
        str += "    <update id=\"delete" + className + "\" parameterType=\"map\">" + enter;
        str += "        update `" + tableName + "`" + enter;
        str += "            set `deleted` = ${@com.sjdf.erp.common.dictionary.bean.WhetherState@YES}" + enter;
        str += "        where id = #{id} and comId = #{comId}" + enter;
        str += "    </update>" + enter;
        return str;
    }

    /**
     * 生成修改xml
     * @param className
     * @param classNameLowerCaseFirstWord
     * @param tableName
     * @param columns
     * @return
     */
    private String createUpdXml(String className, String classNameLowerCaseFirstWord, String tableName, List<String> columns) {
        String str = "";
        str += "    <update id=\"update" + className + "\" parameterType=\"" + classNameLowerCaseFirstWord + "\" >" + enter;
        str += "        update `" + tableName + "`" + enter;
        str += "        set ";
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            if (!column.equals("id")) {
                if (i == 1) {
                    str += column + " = #{" + column + "}";
                } else {
                    str += "            " + column + " = #{" + column + "}";
                }
                if (i != columns.size() - 1) {
                    str += ",";
                }
                str += enter;
            }
        }
        str += "        where id = #{id} and comId = #{comId}" + enter;
        str += "    </update>" + enter;
        return str;
    }

    /**
     * 生成添加xml
     * @param className
     * @param classNameLowerCaseFirstWord
     * @param tableName
     * @param value
     * @return
     */
    private String createAddXml(String className, String classNameLowerCaseFirstWord, String tableName, List<String> columes) {
        String str = "";
        str += "    <insert id=\"add" + className + "\" parameterType=\"" + classNameLowerCaseFirstWord + "\" keyProperty=\"id\">" + enter;
        str += "        insert into `" + tableName + "` (" + enter;
        str += "            <include refid=\"" + classNameLowerCaseFirstWord + "ColumnList\"/>" + enter;
        str += "        ) values (" + enter;
        for (int i = 0; i < columes.size(); i++) {
            if (i % 3 == 0 && i != 0) {
                str += enter;
            }
            if (i % 3 == 0) {
                str += "           ";
            }
            str += " #{" + columes.get(i) + "}";
            if (i != columes.size() - 1) {
                str += ",";
            }
        }
        str += ", ${@com.sjdf.erp.common.dictionary.bean.WhetherState@NO}";
        str += enter;
        str += "        )" + enter;
        str += "    </insert>" + enter;
        return str;
    }

    /**
     * 生成ColumnList
     * @param className
     * @param columes
     * @return
     */
    private String createColumnListXml(String className, List<String> columes) {
        String str = "    <sql id=\"" + className + "ColumnList\">" + enter;
        for (int i = 0; i < columes.size(); i++) {
            if (i % 10 == 0) {
                str += "       ";
            }
            str += " `" + columes.get(i) + "`";
            if (i != columes.size() - 1) {
                str += ",";
            }
            if (i % 10 == 0 && i != 0) {
                str += enter;
            }
        }
        str += ", `deleted`";
        str += enter;
        str += "    </sql>" + enter;
        return str;
    }

    /**
     * 生成resultMap
     * @param className
     * @param columes
     * @return
     */
    private String createResultMapXml(String className, List<String> columes) {
        String str = "    <resultMap id=\"" + className + "Map\" type=\"" + className + "\" >" + enter;
        for (String colume : columes) {
            if (colume.equals("id")) {
                str += "        <id column=\"id\" property=\"id\" />" + enter;
            } else {
                str += "        <result column=\"" + colume + "\" property=\"" + colume + "\" />" + enter;
            }
        }
        str += "    </resultMap>" + enter;
        return str;
    }

}
