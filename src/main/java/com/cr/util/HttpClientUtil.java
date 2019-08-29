package com.cr.util;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create in 2017年6月15日
 * @category HttpClient工具类
 * @author chenyi
 */
public class HttpClientUtil {

    private static final String UTF8 = "UTF-8";
    public static final int TIMEOUT = 10000;
    private static HttpClient client;
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);


    public static SSLConnectionSocketFactory createSSLClientDefault() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();
        return new SSLConnectionSocketFactory(sslContext);
    }

    static {
        // 定义 配置获取网络环境的类
        RequestConfig requestConfig =
        // 获取嵌套类RequestConfig.Builder，用来配置网络环境的（姑且叫它“配置器”吧）
        RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).setConnectionRequestTimeout(TIMEOUT)
            .setConnectTimeout(TIMEOUT).setSocketTimeout(TIMEOUT).build(); //http://zhoujinhuang.iteye.com/blog/2109067
        // 创建一个cookie库
        CookieStore cookieStore = new BasicCookieStore();
        // 创建client 模拟浏览器
        // 创建HttpClientBuilder类，就是CloseableHttpClient的配置器
        try {
            client = HttpClients.custom().setSSLSocketFactory(createSSLClientDefault()).setDefaultCookieStore(cookieStore).setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(1000).setMaxConnPerRoute(1000).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 将相应转化成指定编码的字符串
     * @param response 响应
     * @param encode 编码
     * @return
     * @throws IOException
     */
    public static String responseEntity2Str(HttpResponse response, String encode) throws IOException {
        if (response == null || response.getEntity() == null) {
            return null;
        }
        String str = EntityUtils.toString(response.getEntity(), encode);
        EntityUtils.consume(response.getEntity()); // 会自动释放连接
        return str;
    }

    /**
     * 将响应转化成字符串 utf8
     * @param response 响应
     * @return
     * @throws IOException
     */
    public static String responseEntity2Str(HttpResponse response) throws IOException {
        return responseEntity2Str(response, UTF8);
    }

    /**
     * 获取发送json post请求后的响应信息
     * @param url
     * @param param
     * @param header 请求头
     * @return
     * @throws IOException
     */
    public static String getStrBySendJSONPost(String url, HttpEntity param, Header... header) throws IOException {
        return responseEntity2Str(sendJSONPost(url, param, header));
    }

    /**
     * 发送post请求
     * @param url
     * @param param 参数
     * @param header 请求头
     * @return
     * @throws IOException
     */
    public static HttpResponse sendJSONPost(String url, HttpEntity param, Header... header) throws IOException {
        HttpPost post = new HttpPost(url);
        setJSONHeaders(post, header);
        if (param != null) {
            post.setEntity(param);
        }
        return client.execute(post);
    }

    /**
     * 发送post请求
     * @param url
     * @param param 参数
     * @param token 密钥
     * @return
     * @throws IOException
     */
    public static HttpResponse sendPost(String url, HttpEntity param, Header... header) throws IOException {
        HttpPost post = new HttpPost(url);
        if(header != null && header.length != 0) {
            post.setHeaders(header);
        }
        if (param != null) {
            post.setEntity(param);
        }
        return client.execute(post);
    }

    /**
     * 发送Get请求
     * @param url
     * @param header 请求头
     * @return
     * @throws IOException
     */
    public static HttpResponse sendGet(String url, Header[] headers) throws IOException {
        HttpGet get = new HttpGet(url);
        if(headers != null && headers.length != 0) {
            get.setHeaders(headers);
        }
        return client.execute(get);
    }

    /**
     * 获取json get请求后的字符串
     * @param url
     * @param header
     * @return
     * @throws IOException
     */
    public static String getStrSendJSONGet(String url, Header... header)
        throws IOException {
        HttpResponse response = sendJSONGet(url, header);
        return responseEntity2Str(response);
    }

    /**
     * 获取get请求后的字符串
     * @param url
     * @param header
     * @return
     * @throws IOException
     */
    public static String getStrSendGet(String url, Header[] headers)
        throws IOException {
        HttpResponse response = sendGet(url, headers);
        return responseEntity2Str(response);
    }

    /**
     * 获取post请求后的字符串
     * @param url
     * @param param
     * @param headers
     * @return
     * @throws IOException
     */
    public static String getStrSendPost(String url, HttpEntity param, Header[] headers)
        throws IOException {
        HttpResponse response = sendPost(url, param, headers);
        return responseEntity2Str(response);
    }

    /**
     * 发送json get请求
     * @param url
     * @param header
     * @return
     * @throws IOException
     */
    public static HttpResponse sendJSONGet(String url, Header... header)
        throws IOException {
        HttpGet get = new HttpGet(url);
        setJSONHeaders(get, header);
        return client.execute(get);
    }

    /**
     * 设置请求头
     * @param request 请求
     * @param headers 请求头
     */
    private static void setJSONHeaders(HttpUriRequest request, Header... headers) {
        request.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        request.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        request.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,en-US;q=0.4");
        request.setHeader("connection", "Keep-Alive");
        request.setHeader("Content-Type", "application/json; charset=utf-8");
        request.setHeader("X-Requested-With", "XMLHttpRequest");
        request.setHeader("user-agent",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        if(headers != null && headers.length != 0) {
            for (Header header : headers) {
                request.setHeader(header);
            }
        }
    }


    public static String getStrSendXMLGet(String url, Header[] headers) throws IOException {
        HttpGet get = new HttpGet(url);
        setXMLHeaders(get, headers);
        HttpResponse response = null;
        response = client.execute(get);
        return responseEntity2Str(response);
    }

    private static void setXMLHeaders(HttpUriRequest request, Header[] headers) {
        request.setHeader("Accept", "application/xml, text/javascript, */*; q=0.01");
        request.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        request.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,en-US;q=0.4");
        request.setHeader("connection", "Keep-Alive");
        request.setHeader("Content-Type", "application/xml; charset=UTF-8");
        request.setHeader("X-Requested-With", "XMLHttpRequest");
        request.setHeader("user-agent",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        if(headers != null && headers.length != 0) {
            for (Header header : headers) {
                request.setHeader(header);
            }
        }
    }

    public static String getStrBySendXMLPost(String url, HttpEntity param, Header... headers) throws IOException {
        return responseEntity2Str(sendXMLPost(url, param, headers));
    }

    public static HttpResponse sendXMLPost(String url, HttpEntity param, Header... headers) throws IOException {
        HttpPost post = new HttpPost(url);
        setXMLHeaders(post, headers);
        if (param != null) {
            post.setEntity(param);
        }
        return client.execute(post);
    }

    public static HttpResponse sendXMLGet(String url, Header... headers) throws IOException {
        HttpPost get = new HttpPost(url);
        setXMLHeaders(get, headers);
        return client.execute(get);
    }

    public static String getStrSendJSONDelete(String url, Header[] headers) throws IOException {
        HttpResponse response = sendJSONDelete(url, headers);
        return responseEntity2Str(response);
    }

    public static HttpResponse sendJSONDelete(String url, Header[] headers) throws IOException {
        HttpDelete delete = new HttpDelete(url);
        setJSONHeaders(delete, headers);
        return client.execute(delete);
    }

    /**
     * 从url获取文件bytes
     * @param url
     * @param times 重试次数，默认3
     * @param millis 间隔毫秒，默认700
     * @return
     * @throws Exception
     */
    public static byte[] getBytes(String url, Integer times, Long millis) throws Exception {
        Integer time = times == null ? 3 : times;
        Long milli = millis == null ? 700L : millis;
        for(int k = 0; k < time; k++) {
            HttpResponse response = sendGet(url, null);
            int sc = response.getStatusLine().getStatusCode();
            if(sc == 404) {
                EntityUtils.consume(response.getEntity());
                Thread.sleep(milli);
                continue;
            }
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            return bytes;
        }
        return null;
    } 
}
