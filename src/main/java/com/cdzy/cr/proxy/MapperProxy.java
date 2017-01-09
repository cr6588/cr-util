package com.cdzy.cr.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdzy.cr.util.FileUtil;

public class MapperProxy {
    private static Logger logger = LoggerFactory.getLogger(MapperProxy.class);

    /**
     * 生成http接口与xml对应的实例
     * @param mapperInterface
     * @param document
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T newMapperProxy(Class<T> mapperInterface, Document document) {
        ClassLoader classLoader = mapperInterface.getClassLoader();
        Class<?>[] interfaces = new Class[] { mapperInterface };
        MapperClient proxy = new MapperClient(document);
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }

    /**
     * 获取http接口实例
     * @param clazz http接口所在类
     * @param o
     * @return
     */
    @SuppressWarnings("resource")
    public static <T> T getHttpInstance(Class<T> clazz, Object o) {
        if (o == null) {
            String jarPath = FileUtil.getRootPath(clazz);
            //测试时xml文件在target-class的http接口所在目录中，但实际运行时是在jar包中所以了获取xml文件分成2种方式
            if(new File(jarPath).isFile()) { //如果是在jar包中
                List<JarEntry> jarEnties = null;
                // 获取class所在包下的xml文件
                jarEnties = FileUtil.getJarEntiesBySuffix(jarPath, "xml");
                if (jarEnties == null || jarEnties.size() == 0) {
                    logger.error(jarPath + "路径下没有xml文件");
                    return null;
                }
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(jarPath);
                } catch (IOException e) {
                    return null;
                }
                for (JarEntry jarEntry : jarEnties) {
                    try {
                        T t = getInstanceByInputStream(clazz, jarFile.getInputStream(jarEntry));
                        if(t != null) {
                            return t;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String packagePath = clazz.getResource("").getPath();
                List<File> files = FileUtil.getFilesBySuffix(packagePath, "xml");
                if (files == null || files.size() == 0) {
                    logger.error(packagePath + "路径下没有xml文件");
                    return null;
                }
                for (File file : files) {
                    try {
                        T t = getInstanceByInputStream(clazz, new FileInputStream(file));
                        if(t != null) {
                            return t;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        logger.error(clazz.getName() + "的实例生成失败，请检查是否有与其对应的xml文件");
        return null;
    }

    public static <T> T getInstanceByInputStream(Class<T> clazz, InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            if (clazz.getName().equals(rootElement.attributeValue("namespace"))) {
                return MapperProxy.newMapperProxy(clazz, document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
