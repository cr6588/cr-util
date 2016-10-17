package com.cdzy.cr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.junit.Test;

public class FileCopyTest {

//    @Test
    public void fileCopyTest() {
        String path = "D:\\workspace\\alatin-sync-jinxiongmao\\src\\test\\java\\test";
        fileGbkToUtf8(path);
    }

    /**
     * 将文件目录中的所有文件从gbk转到utf8
     * @param path
     */
    private void fileGbkToUtf8(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isFile()) {
                File fileCopy = new File(files[i].getParent() + File.separator + files[i].getName().replace(".", "copy."));
                FileInputStream fis = null;
                InputStreamReader isr = null;
                FileOutputStream fos = null;
                OutputStreamWriter osw = null;
                try {
                    fis = new FileInputStream(files[i]);
                    isr = new InputStreamReader(fis, "utf8");
                    fos = new FileOutputStream(fileCopy);
                    osw = new OutputStreamWriter(fos, "gbk");
//                    byte[] bytes = new byte[1024 * 4];
                    char[] c = new char[1024];//缓冲
                    int len = 0;
                    while((len = isr.read(c)) != -1) {
//                        String str = new String(c);
//                        if(str.contains("class")) {
//                            str = str.substring(0, str.indexOf("class") + "class".length() + 1) + files[i].getName().replace(".java", "copy") + str.substring(str.indexOf("{"));
//                        }
                        osw.write(c, 0, len);
                        osw.flush();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(fis != null) {
                            fis.close();
                        }
                        if(isr != null) {
                            isr.close();
                        }
                        if(fos != null) {
                            fos.close();
                        }
                        if(osw != null) {
                            osw.close();
                        }

                        //删除更名文件时确保文件不被占用，在输入输出流未关闭时前面操作会失�?
                        String fileName = files[i].getPath();
                        files[i].delete();
                        File targetFile = new File(fileName);
                        System.out.println("sourece file "  + fileCopy.getPath() + " ,is exists?" + fileCopy.exists() + " target file " + targetFile.exists() + "  renameTo result " + fileCopy.renameTo(targetFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (files[i].isDirectory()) {
                fileGbkToUtf8(files[i].getPath());
            }
        }
    }

//    @Test
    public void renameToTest() {
        String fileName = "d:\\a.txt";
        File fileCopy = new File(fileName);
        File targetFile = new File(fileName + "s");
        System.out.println("sourece file " + fileCopy.exists() + " target file " + targetFile.exists() + "  renameTo result " + fileCopy.renameTo(targetFile));
    }

    @Test
    public void batchRenameFileTest() {
        batchRenameFile("D:\\script_test\\", "weilv_");
    }

    /**
     * 批量修改path路径下含有containStr的文件名，去掉containStr
     * @param path
     * @param containStr
     */
    public static void batchRenameFile(String path, String containStr) {
        File[] files = new File(path).listFiles();
        for (File file : files) {
            System.out.println(file.getName());
            if(file.isFile()) {
                if(file.getName().contains(containStr)) {
                    File targetFile = new File(file.getParent() + File.separator + file.getName().replace(containStr, ""));
                    System.out.println("sourece file " + file.exists() + " target file " + targetFile.exists() + "  renameTo result " + file.renameTo(targetFile));
                }
            } else if (file.isDirectory()) {
                batchRenameFile(file.getPath(), containStr);
            }
        }
    }
}
