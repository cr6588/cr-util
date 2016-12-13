package com.cdzy.cr.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.Element;

import com.cdzy.cr.util.HttpUtil;
import com.cdzy.cr.util.SyncSession;

public class MapperProxy implements InvocationHandler {
    private Document document;
    MapperProxy (Document document) {
        this.document = document;
    }
    MapperProxy () {
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Element rootElement = document.getRootElement();
        SyncSession session = null;
        if(args != null) {
            for (Object arg : args) {
                if(arg instanceof SyncSession) {
                    session = (SyncSession) arg;
                }
            }
            Map<String, Object> httpArg = (HashMap<String, Object>)args[0];
            if(rootElement.elements("http") != null) {
                List<Element> https = rootElement.elements("http");
                for (Element http : https) {
                    if(method.getName().equals(http.attribute("id").getText())) {
                        String resultType = http.attribute("resultType").getText();
//                        if(httpArg.get("url") == null) {
//                            //
//                            return null;
//                        }
//                        checkArg();
                        String url = http.element("url").getText();
                        if(httpArg.get("urlformat") != null) {
                            url = String.format(url, httpArg.get("urlformat"));
                        }
                        String methodHttp = http.element("method").getText();
                        List<Element> headers = http.element("headers").elements("header");
                        if(headers != null) {
                            //set header
                        }
                        List<BasicNameValuePair> paramsBasicNameValuePairs = null;
                        if(http.element("params") != null) {
                            List<Element> paramsHttp = http.element("params").elements("param");
                            if(paramsHttp != null) {
                                paramsBasicNameValuePairs = new ArrayList<BasicNameValuePair>();
                                for (Element paramHttp : paramsHttp) {
                                    String paramValue = paramHttp.attributeValue("value");
                                    if(paramValue.trim().startsWith("#{") && paramValue.trim().endsWith("}")) {
                                        String httpArgKey = paramValue.trim().substring(paramValue.trim().indexOf("#{") + 2, paramValue.trim().lastIndexOf("}"));
                                        String httpArgValue = httpArg.get(httpArgKey).toString();
                                        paramsBasicNameValuePairs.add(new BasicNameValuePair(paramHttp.attributeValue("key"), httpArgValue));
                                    } else {
                                        paramsBasicNameValuePairs.add(new BasicNameValuePair(paramHttp.attributeValue("key"), paramValue));
                                    }
                                }
                            }
                        }
                        boolean isAjax = false;
                        if(http.element("ajax") != null) {
                            isAjax = Boolean.parseBoolean(http.element("ajax").getText());
                        }
                        if(methodHttp.equalsIgnoreCase("get")) {
                            if (resultType.equalsIgnoreCase("String")) {
                                return HttpUtil.getGetHttpResStr(session, url, isAjax);
                            }
                        } else if (methodHttp.equalsIgnoreCase("post")) {
                            
                        } else {
                            //method 非法
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static <T> T newMapperProxy(Class<T> mapperInterface) {  
        ClassLoader classLoader = mapperInterface.getClassLoader();  
        Class<?>[] interfaces = new Class[]{mapperInterface};  
        MapperProxy proxy = new MapperProxy();  
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);  
      }

    public static <T> T newMapperProxy(Class<T> mapperInterface ,Document document) {  
        ClassLoader classLoader = mapperInterface.getClassLoader();  
        Class<?>[] interfaces = new Class[]{mapperInterface};  
        MapperProxy proxy = new MapperProxy(document);  
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);  
      }
}
