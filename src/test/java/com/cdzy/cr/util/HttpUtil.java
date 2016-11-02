package com.cdzy.cr.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpUtil {

    private static final String UTF8 = "UTF-8";

    /**
     * 公共方法：执行GET请求
     * @param httpClient
     * @param URL 请求地址
     * @return
     * @throws IOException
     */
    public static HttpResponse excuteHttpGet(HttpClient httpClient, String URL) throws IOException {
        HttpGet get = new HttpGet(URL);
        setBrowerAttrHeaders(get);
        return httpClient.execute(get);
    }

    /**
     * 公共方法：执行GET请求
     * @param httpClient
     * @param URL 请求地址
     * @return
     * @throws IOException
     */
    public static HttpResponse excuteHttpGet(HttpClient httpClient, String URL, boolean isAjax) throws IOException {
        HttpGet get = new HttpGet(URL);
        setBrowerAttrHeaders(get, isAjax);
        return httpClient.execute(get);
    }

    public static String getGetHttpResStr(HttpClient httpClient, String URL, boolean isAjax) throws IOException, Exception {
        return responseEntity2Str(excuteHttpGet(httpClient, URL, isAjax));
    }

    public static String getGetHttpResStr(HttpClient httpClient, String URL, boolean isAjax, String encode) throws IOException, Exception {
        return responseEntity2Str(excuteHttpGet(httpClient, URL, isAjax), encode);
    }

    public static String getPostHttpResStr(HttpClient httpClient, String URL, boolean isAjax, List<BasicNameValuePair> params) throws IOException, Exception {
        return responseEntity2Str(excuteHttpPost(httpClient, URL, params, isAjax));
    }

    public static String getPostHttpResStr(HttpClient httpClient, String URL, boolean isAjax, List<BasicNameValuePair> params, String encode) throws IOException, Exception {
        return responseEntity2Str(excuteHttpPost(httpClient, URL, params, isAjax), encode);
    }
    /**
     * 公共方法，执行POST请求
     * @param httpClient
     * @param URL 请求地址
     * @param params 请求参数
     * @param entityCode 编码格式
     * @return
     * @throws IOException
     */
    public static HttpResponse excuteHttpPost(HttpClient httpClient, String URL, List<BasicNameValuePair> params, String entityCode) throws IOException {
        HttpPost post = new HttpPost(URL);
        setBrowerAttrHeaders(post);
        if(params != null) {
            post.setEntity(new UrlEncodedFormEntity(params, entityCode));
        }
        return httpClient.execute(post);
    }

    /**
     * 公共方法，执行POST请求
     * @param httpClient
     * @param URL
     * @param params
     * @return
     * @throws IOException
     */
    public static HttpResponse excuteHttpPost(HttpClient httpClient, String URL, List<BasicNameValuePair> params) throws IOException {
        HttpPost post = new HttpPost(URL);
        setBrowerAttrHeaders(post);
        if(params != null) {
            post.setEntity(new UrlEncodedFormEntity(params, UTF8));
        }
        return httpClient.execute(post);
    }

    /**
     * 公共方法，执行POST请求
     * @param httpClient
     * @param URL
     * @param params
     * @param isAjax
     * @return
     * @throws IOException
     */
    public static HttpResponse excuteHttpPost(HttpClient httpClient, String URL, List<BasicNameValuePair> params, boolean isAjax) throws IOException {
        HttpPost post = new HttpPost(URL);
        setBrowerAttrHeaders(post, isAjax);
        if(params != null) {
            post.setEntity(new UrlEncodedFormEntity(params, UTF8));
        }
        return httpClient.execute(post);
    }

    /**
     * 公共方法：设置请求头
     * @param request 具体请求对象
     */
    public static void setBrowerAttrHeaders(HttpUriRequest request) {
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1941.0 Safari/537.36");
        request.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        request.setHeader("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
        request.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
        request.setHeader("Cache-Control", "no-cache");
    }

    /**
     * 公共方法：设置请求头
     * @param request 具体请求对象
     */
    public static void setBrowerAttrHeaders(HttpUriRequest request, boolean isAjax) {
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1941.0 Safari/537.36");
        request.setHeader("Accept-Encoding", "gzip,deflate");
        request.setHeader("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
        request.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
        request.setHeader("Cache-Control", "no-cache");

        if(isAjax) {
            request.setHeader("X-Requested-With", "XMLHttpRequest"); //ajax请求头中含有
        }
    }

    /**
     * 将response.getEntity()转为字符串返回
     * @param response
     * @param encode TODO
     * @return
     * @throws Exception
     */
    public static String responseEntity2Str(HttpResponse response, String encode) throws Exception {
        if(response == null) {
            return null;
        }
        String str = EntityUtils.toString(response.getEntity(), encode);
        EntityUtils.consume(response.getEntity()); //会自动释放连接 
        return str;
    }

    /**
     * 将response.getEntity()转为字符串返回
     * @param response
     * @return
     * @throws Exception
     */
    public static String responseEntity2Str(HttpResponse response) throws Exception {
        if(response == null) {
            return null;
        }
        String str = EntityUtils.toString(response.getEntity(), UTF8);
        EntityUtils.consume(response.getEntity()); //会自动释放连接
        return str;
    }
}
