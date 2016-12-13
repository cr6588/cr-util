package com.cdzy.cr.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.alibaba.fastjson.JSONObject;

public class SyncSession {

    /**
     * 
     */
    private static final long serialVersionUID = -7746420027592233279L;

    private Map<String, Object> map = new HashMap<String, Object>();
    private CloseableHttpClient client;
    private RequestConfig requestConfig;
    private CookieStore cookieStore;
    private String id;
    private String loginName;
    private int organId;
    private int siteId;
    private boolean isLogin;
    private int timeout = 3000;

    public SyncSession() {
        // 定义 配置获取网络环境的类
        requestConfig =
        // 获取嵌套类RequestConfig.Builder，用来配置网络环境的（姑且叫它“配置器”吧）
        RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout)
        // 使用build方法返回requestConfig对象
                .build();
        // 创建一个cookie库
        cookieStore = new BasicCookieStore();
        // 创建client 模拟浏览器
        // 创建HttpClientBuilder类，就是CloseableHttpClient的配置器
        client = HttpClients.custom().setDefaultCookieStore(cookieStore).setDefaultRequestConfig(requestConfig).setMaxConnTotal(200).setMaxConnPerRoute(200)
        // 返回CloseableHttpClient对象
                .build();
    }

    
    public String getId() {
        return this.id;
    }

    
    public String getLoginName() {
        return this.loginName;
    }

    
    public int getSiteId() {
        // TODO Auto-generated method stub
        return this.siteId;
    }

    
    public int getOrganId() {
        // TODO Auto-generated method stub
        return this.organId;
    }

    
    public void setOrganId(int organId) {
        // TODO Auto-generated method stub
        this.organId = organId;
        
    }

    
    public HttpClient getHttpClient() {
        return client;
    }

    
    public void setIsLogin(boolean isLogin) {
        // TODO Auto-generated method stub
        this.isLogin = isLogin;
    }

    
    public void setError(String errorCode, String description) {
        // TODO Auto-generated method stub

    }

    
    public String getLastErrCode() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public String getLastErrDesc() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public boolean isLogin() {
        // TODO Auto-generated method stub
        return this.isLogin;
    }

    
    public boolean isFree() {
        // TODO Auto-generated method stub
        return true;
    }

    
    public long getLoginTm() {
        // TODO Auto-generated method stub
        return 0;
    }

    
    public long getLastActTm() {
        // TODO Auto-generated method stub
        return 0;
    }

    
    public long getLastActTv() {
        // TODO Auto-generated method stub
        return 0;
    }

    
    public void putObject(String name, Object value) {
        // TODO Auto-generated method stub
        this.map.put(name, value);
    }

    
    public Object getObject(String name) {
        // TODO Auto-generated method stub
        return map.get(name);
    }

    
    public void clearObjects() {
        // TODO Auto-generated method stub
        map.clear();
    }

    
    public void removeObject(String name) {
        // TODO Auto-generated method stub
        map.remove(name);
    }

    
    public int getErrors() {
        // TODO Auto-generated method stub
        return 0;
    }

    
    public int getDone() {
        // TODO Auto-generated method stub
        return 0;
    }

    
    public List<Cookie> getCookies() {
        // TODO Auto-generated method stub
        return cookieStore.getCookies();
    }

    
    public List<Cookie> getCookies(String name) {
        // TODO Auto-generated method stub
        System.out.println("getCookies:" + name);
        return null;
    }

    
    public List<Cookie> getCookies(String name, String domain) {
        // TODO Auto-generated method stub
        System.out.println("getCookies:" + name + "|" + domain);
        return null;
    }

    
    public Cookie getCookie(String name, String domain, String path) {
        // TODO Auto-generated method stub
        System.out.println("getCookie:" + name + "|" + domain + "|" + path);
        return null;
    }

    
    public void addCookie(Cookie cookie) {
        this.cookieStore.addCookie(cookie);
        System.out.println("add cookie:" + cookie);
    }

    
    public CookieStore getCookieStore() {
        // TODO Auto-generated method stub
        return cookieStore;
    }

    
    public void waitSecs(int secs) throws InterruptedException {
        // TODO Auto-generated method stub

    }

    
    public boolean isTemporary() {
        // TODO Auto-generated method stub
        return false;
    }

    
    public void close() {
        // TODO Auto-generated method stub

    }

    
    
    public void done(boolean succ, String errorCode, String description) {
        // TODO Auto-generated method stub
        System.out.println("done");
    }

    

    
    public int getUserId() {
        // TODO Auto-generated method stub
        return 0;
    }

    
    public void clearCookies() {
        // TODO Auto-generated method stub
        
    }

}
