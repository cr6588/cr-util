package com.cr.util;

import static org.junit.Assert.*;

import java.io.FileInputStream;
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
 * create in 2017年10月16日
 * @category TODO
 * @author chenyi
 */
public class RustoneTest {

    @Test
    public void test() {
        InputStream is = null;
        try {
            String excelPath = this.getClass().getResource("").getPath() + "data/俄速通状态表.xls";
            is = new FileInputStream(excelPath);
            POIFSFileSystem fs = new POIFSFileSystem(is);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            int sheetCount = wb.getNumberOfSheets();
            String sheetName = "俄速通";
            List<String> existTable = new ArrayList<>();
            Map<String, String> existTableName = new HashMap<>();
            for (int i = 0; i < sheetCount; i++) {
                HSSFSheet sheet = wb.getSheetAt(i);
                if (sheetName.equals(sheet.getSheetName())) {
                    int rowNum = sheet.getPhysicalNumberOfRows();
                    for (int j = 1; j < rowNum; j++) {
                        HSSFRow row = sheet.getRow(j);
                        if (row != null) {
                            System.out.println("            case \"" + row.getCell(0).getStringCellValue() + "\":");
                            System.out
                                .println("                return \"" + row.getCell(1).getStringCellValue().replace("\n", "") + "\";");
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
