package com.cdzy.cr.util;

import java.io.File;

import org.junit.Test;

public class FileUtilTest {

  @Test
  public void txtTest() {
      String testDataPath = this.getClass().getResource("").getPath() + "data" + File.separator + "txt.js";
      String txt = FileUtil.readFile2String(testDataPath); //此方法读取字符串有问题
      System.out.println(txt);
      txt = FileUtil.readTxtFile2String(testDataPath);
      System.out.println(txt);
      Long time = System.currentTimeMillis();
      for(int i = 0; i < 10000; i++) {
          txt = FileUtil.readTxtFile2String(testDataPath);
      }
      System.out.println("String用时" + (System.currentTimeMillis() - time));
      time = System.currentTimeMillis();
      for(int i = 0; i < 10000; i++) {
          txt = FileUtil.readTxtFile2StrByStringBuffer(testDataPath);
      }
      System.out.println("StringBuffer用时" + (System.currentTimeMillis() - time));
      time = System.currentTimeMillis();
      for(int i = 0; i < 10000; i++) {
          txt = FileUtil.readTxtFile2StrByStringBuilder((testDataPath));
      }
      System.out.println("StringBuilder用时" + (System.currentTimeMillis() - time));

  }

}
