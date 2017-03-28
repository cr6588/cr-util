package com.cr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtil {
    /**
     * 读取文件内容到内存中用此方法有严重问题存在断码问题 用原始的字节流读取内容 而不是字符流 在个别字节没有读取完毕的情况下就进行输出 当然会出错
     * @param path
     * @return
     */
    @Deprecated
    public static String readFile2String(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        String str = "";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] bytes = new byte[1];
            int len;
            while ((len = fis.read(bytes)) != -1) {
                String temp = new String(bytes, 0, len, "utf8");
                str += temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    /**
     * 读取文本内容用字符流不要使用字节流
     * @param path
     * @return
     */
    public static String readTxtFile2String(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        String str = "";
        FileReader fis = null;
        try {
            fis = new FileReader(file);
            char[] chars = new char[1];
            int len;
            while ((len = fis.read(chars)) != -1) {
                str += new String(chars, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    /**
     * 读取文本内容用字符流不要使用字节流
     * @param path
     * @return
     */
    public static String readTxtFile2StrByStringBuilder(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        FileReader fis = null;
        try {
            fis = new FileReader(file);
            char[] chars = new char[1];
            int len;
            while ((len = fis.read(chars)) != -1) {
                sb.append(chars, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 读取clazz所在jar包文件path路径的文件
     * @param clazz
     * @param path
     * @return
     */
    public static String readTxtFile2StrByStringBuilder(Class<?> clazz, String path) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = clazz.getResourceAsStream(path); BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
            char[] chars = new char[1];
            int len;
            while ((len = br.read(chars)) != -1) {
                sb.append(chars, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 读取文本内容用字符流不要使用字节流
     * @param path
     * @return
     */
    public static String readTxtFile2StrByStringBuffer(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        FileReader fis = null;
        try {
            fis = new FileReader(file);
            char[] chars = new char[1];
            int len;
            while ((len = fis.read(chars)) != -1) {
                sb.append(chars, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 文件后缀过滤
     *
     */
    static class FileSuffixFilter implements FilenameFilter {
        private String suffix;
        FileSuffixFilter(String suffix) {
            this.suffix = suffix;
        }
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(suffix);
        }
    }

    /**
     * 获取指定路径下的指定后缀的文件
     * @param path
     * @param suffix
     * @return
     */
    public static List<File> getFilesBySuffix(String path, String suffix) {
        File[] files = new File(path).listFiles(new FileSuffixFilter(suffix));
        List<File> listFile = new ArrayList<File>();
        for (File file : files) {
            if(file.isFile()) {
                listFile.add(file);
            } else if (file.isDirectory()) {
                listFile.addAll(getFilesBySuffix(file.getPath(), suffix));
            }
        }
        return listFile;
    }

    /**
     * 获取jar包中指定后缀的文件
     * @param jarPath
     * @param suffix
     * @return
     */
    @SuppressWarnings("resource")
    public static List<JarEntry> getJarEntiesBySuffix(String jarPath, String suffix) {
        JarFile xmlFile = null;
        try {
            xmlFile = new JarFile(jarPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration<JarEntry> Enumeration = xmlFile.entries();
        List<JarEntry> jarEntries = new ArrayList<JarEntry>();
        for (;Enumeration.hasMoreElements();) {
            JarEntry entry = Enumeration.nextElement();
            if(entry.getName().endsWith(suffix)) {
                jarEntries.add(entry);
            }
        }
        return jarEntries.size() == 0 ? null : jarEntries;
    }

    /**
     * 获取指定类的根路径，jar包中返回的jar包文件所在路径
     * @param clazz
     * @return
     */
    public static <T> String getRootPath(Class<T> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static boolean deleteFileDir(String path) throws Exception {
        File file = new File(path);
        return deleteFileDir(file);
    }

    public static boolean deleteFileDir(File file) throws Exception {
        if(!file.exists()) {
            throw new Exception( file.getName() + " not found!");
        }
        
        //Deletes the file or directory denoted by this abstract pathname.
        //If this pathname denotes a directory, then the directory must be empty in order to be deleted.
        File[] files = file.listFiles();
        //Arrays.asList()返回的是Arrays中的内部类ArrayList，ArrayList继承AbstractList,AbstractList中的add方法会直接抛出异常
        for (File f : files) {
            if(f.isDirectory()) {
                deleteFileDir(f);
            } else {
                if(!f.delete()) {
                    return false; //有一个文件没有删除成功就返回
                }
            }
        }
        return file.delete();
    }
}
