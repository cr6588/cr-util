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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Test;

/**
 * create in 2017年04月20日
 * @category mybatis接口xml生成
 * @author chenyi
 */
public class MapperTest {

    private static final String[] beanNames = { "SysCategory", "SysCategoryAttr", "SysCategoryValue" };
    private static final String[] beanCnNames = { "类目", "类目属性", "类目属性值" };
    private static final boolean isNeedComId = false;
    private static final boolean isNeedDeleted = false;
    private static final boolean isNeedUpdateNoNull = false;//xml中是否需要update 字段非空时的修改
    private static final boolean isNeedBatchSave = true;//xml中是否需要批量插入
    private static final Set<String> updateIgnoreFields = Stream.of("id", "comId", "createTime", "createUser").collect(Collectors.toSet());
    @Test
    public void test() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://user.mysql.jtongi.cn:31111/test?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8", "root", "tTdAdf2129");
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

    private String createMapperDao(String beanDesc, String beanName) {
        String daoExample = FileUtil.readTxtFile2StrByStringBuilder(this.getClass().getResource("").getPath() + "/data/LogisticsDao.txt");
        String daoStr = daoExample.replace("交接单", beanDesc).replace("PubHandover", beanName).replaceAll("pubHandover", beanName.toLowerCase().charAt(0) + beanName.substring(1));
        return daoStr;
    }

    @Test
    public void createMapperDaoTest() {
        for (int i = 0; i < beanCnNames.length; i++) {
            String beanCnName = beanCnNames[i];
            String beanName = beanNames[i];
            System.out.println(createMapperDao(beanCnName, beanName));
        }
    }

    private String createService(String beanDesc, String beanName, String serviceName) {
        String daoExample = FileUtil.readTxtFile2StrByStringBuilder(this.getClass().getResource("").getPath() + "/data/LogisticsService.txt");
        String daoStr = daoExample.replace("交接单", beanDesc).replace("PubHandover", beanName).replaceAll("pubHandover", beanName.toLowerCase().charAt(0) + beanName.substring(1));
        return daoStr.replace("HandoverDao", serviceName).replace("handoverDao", serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1));
    }

    @Test
    public void createServiceTest() {
        for (int i = 0; i < beanCnNames.length; i++) {
            String beanCnName = beanCnNames[i];
            String beanName = beanNames[i];
            System.out.println(createService(beanCnName, beanName, "SysCategoryDao"));
        }
    }

    private String createFacade(String beanDesc, String beanName, String serviceName) {
        String daoExample = FileUtil.readTxtFile2StrByStringBuilder(this.getClass().getResource("").getPath() + "/data/LogisticsFacade.txt");
        String daoStr = daoExample.replace("交接单", beanDesc).replace("PubHandover", beanName).replaceAll("pubHandover", beanName.toLowerCase().charAt(0) + beanName.substring(1));
        return daoStr.replace("HandoverService", serviceName).replace("handoverService", serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1));
    }

    @Test
    public void createFacadeTest() {
        for (int i = 0; i < beanCnNames.length; i++) {
            String beanCnName = beanCnNames[i];
            String beanName = beanNames[i];
            System.out.println(createFacade(beanCnName, beanName, "SysCategoryService"));
        }
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
            if (isNeedBatchSave) {
                mapperXML += createBatchSaveXml(className, classNameLowerCaseFirstWord, tableName, entry.getValue());    
            }
            mapperXML += createAddXml(className, classNameLowerCaseFirstWord, tableName, entry.getValue());
            mapperXML += createUpdXml(className, classNameLowerCaseFirstWord, tableName, entry.getValue(), false);
            if(isNeedUpdateNoNull) {
                mapperXML += createUpdXml(className, classNameLowerCaseFirstWord, tableName, entry.getValue(), true);
            }
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
        if(isNeedDeleted) {
            str += "            `deleted` = ${@com.sjdf.erp.common.dictionary.bean.WhetherState@NO}" + enter;
        }
        for (String column : columns) {
            str += "            <if test=\"" + column + " != null\"> and `" + column + "` = #{" + column + "}</if>" + enter;
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
        if(isNeedDeleted) {
            String str = "";
            str += "    <update id=\"delete" + className + "\" parameterType=\"map\">" + enter;
            str += "        update `" + tableName + "`" + enter;
            str += "            set `deleted` = ${@com.sjdf.erp.common.dictionary.bean.WhetherState@YES}" + enter;
            str += "        where id = #{id}" + getComIdStr() + enter;
            str += "    </update>" + enter;
            return str;
        }
        String str = "";
        str += "    <delete id=\"delete" + className + "\" parameterType=\"map\">" + enter;
        str += "        delete from `" + tableName + "`" + enter;
        str += "        where id = #{id}" + getComIdStr() + enter;
        str += "    </delete>" + enter;
        return str;
    }

    private String getComIdStr() {
        return isNeedComId ? " and comId = #{comId}" : "";
    }

    /**
     * 生成修改xml
     * @param className
     * @param classNameLowerCaseFirstWord
     * @param tableName
     * @param columns
     * @param judegeFieldNull TODO
     * @return
     */
    private String createUpdXml(String className, String classNameLowerCaseFirstWord, String tableName, List<String> columns, boolean judegeFieldNull) {
        String str = "";
        str += "    <update id=\"update" + className + (judegeFieldNull ? "NoNull" : "") + "\" parameterType=\""
            + classNameLowerCaseFirstWord + "\" >" + enter;
        str += "        update `" + tableName + "`" + enter;
        str += "        <set>" + enter;
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            if (!updateIgnoreFields.contains(column)) {
                if(judegeFieldNull) {
                    str += String.format("            <if test=\"%s != null\">`%s` = #{%s},</if>", column, column, column);
                } else {
                    str += String.format("            `%s` = #{%s},", column, column);
                }
                str += enter;
            }
        }
        str += "        </set>" + enter;
        str += "        where id = #{id}" + getComIdStr() + enter;
        str += "    </update>" + enter;
        return str;
    }

    /**
     * 生成批量添加xml
     * @param className
     * @param classNameLowerCaseFirstWord
     * @param tableName
     * @param value
     * @return
     */
    private String createBatchSaveXml(String className, String classNameLowerCaseFirstWord, String tableName, List<String> columes) {
        String str = "";
        str += "    <insert id=\"batchSave" + className + "\" parameterType=\"list\">" + enter;
        str += "        insert into `" + tableName + "` (" + enter;
        str += "            <include refid=\"" + classNameLowerCaseFirstWord + "ColumnList\"/>" + enter;
        str += "        ) values" + enter;
        str += "        <foreach collection=\"list\" separator=\",\" item=\"l\" >" + enter;
        str += "            (" + enter;
        for (int i = 0; i < columes.size(); i++) {
            if (i % 3 == 0 && i != 0) {
                str += enter;
            }
            if (i % 3 == 0) {
                str += "                ";
            }
            str += " #{l." + columes.get(i) + "}";
            if (i != columes.size() - 1) {
                str += ",";
            }
        }
        if(isNeedDeleted) {
            str += ", ${@com.sjdf.erp.common.dictionary.bean.WhetherState@NO}";
        }
        str += enter;
        str += "            )" + enter;
        str += "        </foreach>" + enter;
        str += "    </insert>" + enter;
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
        str += "    <insert id=\"save" + className + "\" parameterType=\"" + classNameLowerCaseFirstWord + "\" keyProperty=\"id\">" + enter;
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
        if(isNeedDeleted) {
            str += ", ${@com.sjdf.erp.common.dictionary.bean.WhetherState@NO}";
        }
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
            if (i % 10 == 0 || i == 0) {
                str += "       ";
            }
            str += " `" + columes.get(i) + "`";
            if (i != columes.size() - 1) {
                str += ",";
            }
            if ((i + 1) % 10 == 0 && i != 0) {
                str += enter;
            }
        }
        if(isNeedDeleted) {
            str += ", `deleted`";
        }
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
