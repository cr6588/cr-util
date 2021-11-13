package com.cr.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Test;

/**
 * create in 2017年04月20日
 * @category 根据excel内容生成sql语句
 * @author chenyi
 */
public class ExcelUtilTest {

    public boolean exist(String str, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (str.equals(array[i])) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void createWinitByExcel() {
        InputStream is = null;
        try {
            boolean isPrintZhClassName = true;  //是否打印中文表名，类名
            String excelPath = this.getClass().getResource("").getPath() + "data/万邑通.xls";
            is = new FileInputStream(excelPath);
            POIFSFileSystem fs = new POIFSFileSystem(is);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            int sheetCount = wb.getNumberOfSheets();
            String[] logName = { "中邮小包+国家码" };
            List<String> existTable = new ArrayList<>();
            Map<String, String> existTableName = new HashMap<>();
            for (int i = 0; i < sheetCount; i++) {
                HSSFSheet sheet = wb.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                if (exist(sheetName, logName)) {
                    existTable.add(sheetName);
                    int rowNum = sheet.getPhysicalNumberOfRows();
                    String sql = "";
                    String tableName = "";
                    String primary = "";
                    String rowSQL = "";
                    for (int j = 1; j < rowNum; j++) {
                        HSSFRow row = sheet.getRow(j);
                        if (row != null) {
                            String code = row.getCell(3).getStringCellValue().trim();
                            Double numCode = row.getCell(4).getNumericCellValue();
                            Double sortCode = row.getCell(5).getNumericCellValue();
                            System.out.println((code + "(\"" + code  + "\", \"" + numCode + "\", " + sortCode + "), ").replace(".0", ""));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从excel文件生成sql，目前只支持xls文件即excel97-2003
     */
    @Test
    public void createSQLByExcel() {
        InputStream is = null;
        try {
            boolean isPrintZhClassName = false;  //是否打印中文表名，类名
            String excelPath = this.getClass().getResource("").getPath() + "data/表设计.xls";
            is = new FileInputStream(excelPath);
            POIFSFileSystem fs = new POIFSFileSystem(is);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            int sheetCount = wb.getNumberOfSheets();
            String[] logName = { "刊登产品表", "刊登产品属性表", "刊登产品区域价格表" };
            List<String> existTable = new ArrayList<>();
            Map<String, String> existTableName = new HashMap<>();
            for (int i = 0; i < sheetCount; i++) {
                boolean canStart = false;
                HSSFSheet sheet = wb.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                if (exist(sheetName, logName)) {
                    existTable.add(sheetName);
                    int rowNum = sheet.getPhysicalNumberOfRows();
                    String sql = "";
                    String tableName = "";
                    String primary = "";
                    String rowSQL = "";
                    for (int j = 1; j < rowNum; j++) {
                        HSSFRow row = sheet.getRow(j);
                        if (row != null) {
                            for (int k = 0; k < 2 && !canStart; k++) {
                                HSSFCell cell = row.getCell(k);
                                if (cell != null) {
                                    String cellValue = cell.getStringCellValue();
                                    if(tableName.equals("") && !StringUtil.isBlank(cellValue)) {
                                        if(existTableName.get(cellValue) != null) {
                                            throw new Exception("当前表" + sheetName + ":" + cellValue + "与" + existTableName.get(cellValue) + "的数据库表名" + cellValue + "名称一样，请检查！");
                                        }
                                        tableName = cellValue;
                                        existTableName.put(tableName, sheetName);
                                    }
                                    if(cellValue.equalsIgnoreCase("id")) {
                                        canStart = true;
                                    }
                                }
                            }
                            if(canStart) {
                                String columnName = row.getCell(1).getStringCellValue().trim();
//                                if(columnName.equalsIgnoreCase("createUser") || columnName.equalsIgnoreCase("createTime") ||columnName.equalsIgnoreCase("updateUser") ||columnName.equalsIgnoreCase("updateTime")){
//                                    continue;
//                                }
                                String type = row.getCell(3).getStringCellValue().trim();
                                String length = getStringCellValue(row.getCell(4));
                                if(!length.trim().equals("")) {
                                    if(length.contains(",")) {
                                        String array[] = length.split(",");
                                        length = "(" + array[0].trim() + "," + array[1].trim()+ ")";
                                    } else if(length.contains("，")) {
                                        String array[] = length.split("，");
                                        length = "(" + array[0].trim() + "," + array[1].trim()+ ")";
                                    } else {
                                        length = "(" + length.trim() + ")";
                                    }
                                }
                                String notNull = "";
                                if(row.getCell(9) == null || row.getCell(9).getStringCellValue().trim().equals("")) {
                                    notNull = "NOT NULL";
                                }
                                String defaultValue = getStringCellValue(row.getCell(8));
                                if(defaultValue != null && !defaultValue.trim().equals("")) {
                                    defaultValue = "DEFAULT " + "'"+ defaultValue +"'";
                                }
                                String explain = getStringCellValue(row.getCell(5));
                                if(!explain.trim().equals("")) {
                                    
                                    explain = " " + explain.replace("\n", " ");
                                }
                                String comment = "COMMENT '" + row.getCell(2).getStringCellValue() + explain +"'";
                                String isPrimary = getStringCellValue(row.getCell(6));
                                if(!isPrimary.trim().equals("")) {
                                    primary = columnName;
                                }
                                String unsigned = " ";
                                if ("bigint".equals(type) || "tinyint".equals(type) || "int".equals(type)) {
                                    unsigned =" unsigned ";
                                }
                                rowSQL += "    `" + columnName + "`" + " " + type + " " + length + unsigned + notNull + " "+ defaultValue + " " + comment + ", \n";
                            }
                        }
                    }
                    sql += "DROP TABLE IF EXISTS `" + tableName + "`;\nCREATE TABLE `" + tableName + "` (\n" +rowSQL + "    PRIMARY KEY (`" + primary + "`)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                    String[] tableNameArray = tableName.split("_");
                    String className = "";
                    for (int j = 0; j < tableNameArray.length; j++) {
                        className +=tableNameArray[j].substring(0, 1).toUpperCase() + tableNameArray[j].substring(1);
                    }
                    System.out.println((isPrintZhClassName ? sheetName + ":" + className + "\n" : "") +sql);
                }
            }
            for(int i = 0; i < logName.length; i++) {
                boolean exist = false;
                for (String string : existTable) {
                    if(string.equals(logName[i])){
                        exist = true;
                        break;
                    }
                }
                if(!exist) {
                    System.out.println("未能正常解析表名：" + logName[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String getStringCellValue(HSSFCell cell) {
        if(cell == null) {
            return "";
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                String value = cell.getNumericCellValue() + "";
                if(value.contains(".")) {
                    value = value.split("\\.")[0];
                }
                return value;
            default:
                break;
        }
        return "";
    }
}
