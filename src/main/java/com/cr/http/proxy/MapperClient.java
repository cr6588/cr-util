package com.cr.http.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlContext;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cr.util.SyncSession;

public class MapperClient implements InvocationHandler {
    private static Logger logger = LoggerFactory.getLogger(MapperClient.class);
    private Document document;
    private static final String UTF8 = "UTF-8";

    public MapperClient(Document document) {
        this.document = document;
    }

    /**
     * 对http的接口进行代理执行
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SyncSession session = null;
        if (args != null) {
            //从方法参数中找出session, map
            Map<String, Object> map = null;
            for (Object arg : args) {
                if (arg instanceof SyncSession) {
                    session = (SyncSession) arg;
                }
                if (arg instanceof Map) {
                    map = (HashMap<String, Object>) arg;
                }
            }
            if(session == null) {
                throw new Throwable(method.getName() + "的参数中没有session");
            }
            return parseXML(method.getName(), session, map);
        }
        return null;
    }

    public Object parseXML(String  methodName, SyncSession session, Map<String, Object> map) throws Throwable {
        Element rootElement = document.getRootElement();
        //获取适用于xml中的参数map
        if (rootElement.elements("http") != null) {
            List<Element> https = rootElement.elements("http");
            for (Element http : https) {
                if(http.attribute("id") == null) {
                    throw new Throwable("http属性id的不存在");
                }
                if (methodName.equals(http.attribute("id").getText())) {
                    String resEncode = UTF8;
                    if(http.attribute("resEncode") != null) {
                        resEncode = http.attributeValue("resEncode");
                    }
                    //解析url
                    String url = http.element("url").getText();
                    if(http.element("urlformat") != null) {
                        String urlformatXMLValue = http.element("urlformat").getText();
                        if(urlformatXMLValue.startsWith("#{") && urlformatXMLValue.endsWith("}")) {
                            String mapKey = getKeyByXMLValue(urlformatXMLValue);
                            if(mapKey == null || mapKey.equals("")) {
                                throw new Throwable("urlformat的值去除#{}后为空");
                            }
                            if(map.get(mapKey) instanceof String) {
                                String urlformat = (String) map.get(getKeyByXMLValue(urlformatXMLValue));
                                url = String.format(url, urlformat);
                            } else if (map.get(getKeyByXMLValue(urlformatXMLValue)) instanceof String[]) {
                                String[] urlformat = (String[]) map.get(getKeyByXMLValue(urlformatXMLValue));
                                url = String.format(url, urlformat);
                            } else {
                                throw new Throwable("参数map中" + mapKey + "的值既不是字符串也不是字符串数组");
                            }
                        } else {
                            throw new Throwable("urlformat的值" + urlformatXMLValue + "非法,请更改");
                        }
                    }
                    //解析params
                    List<BasicNameValuePair> paramsBasicNameValuePairs = new ArrayList<BasicNameValuePair>();
                    String paramsEncode = UTF8;
                    if (http.element("params") != null) {
                        if(http.element("params").attribute("encode") != null) {
                            paramsEncode = http.element("params").attributeValue("encode");
                        }
                        //params下直系param节点
                        List<Element> paramsHttp = http.element("params").elements("param");
                        if (paramsHttp != null) {
                            for (Element paramHttp : paramsHttp) {
                                String xmlValue = paramHttp.attributeValue("value");
                                paramsBasicNameValuePairs.add(new BasicNameValuePair(paramHttp.attributeValue("key"), getValue(map, xmlValue)));
                            }
                        }
                        //params下直系if节点
                        List<Element> ifElements = http.element("params").elements("if");
                        if(ifElements != null) {
                            for (Element ifElement : ifElements) {
                                String express = ifElement.attributeValue("test");
                                boolean result = (boolean) parseOgnlExpress(map, express);
                                if(!result) {
                                    continue;
                                }
                                List<Element> paramElements = ifElement.elements("param");
                                if(paramElements != null) {
                                    for (Element paramElement : paramElements) {
                                        String xmlValue = paramElement.attributeValue("value");
                                        paramsBasicNameValuePairs.add(new BasicNameValuePair(paramElement.attributeValue("key"), getValue(map, xmlValue)));
                                    }
                                }
                            }
                        }
                        //params下直系foreach节点
                        List<Element> forElements = http.element("params").elements("foreach");
                        if(forElements != null) {
                            for (Element forElement : forElements) { //foreach标签循环
                                String collection = forElement.attributeValue("collection");
                                if(map.get(collection) == null || !(map.get(collection) instanceof List)) {
                                    throw new Throwable("map中" + collection + "为空!");
                                }
                                if(!(map.get(collection) instanceof List)) {
                                    throw new Throwable("map中" + collection + "不是list!");
                                }
                                List list = (List) map.get(collection);
                                if(list == null) {
                                    continue;
                                }
                                String item = forElement.attributeValue("item");
                                String index = forElement.attributeValue("index");
                                for (int i = 0; i < list.size(); i++) { //list循环
                                    //foreach下直系param节点
                                    List<Element> paramElements = forElement.elements("param");
                                    if(paramElements != null) {
                                        for (Element paramElement : paramElements) {
                                            String key = paramElement.attributeValue("key");
                                            if(index != null && !index.trim().equals("")) {
                                                key = key.replace("index", "" + i); //key中有序列的替换
                                            }
                                            String value = paramElement.attributeValue("value");
                                            if (value!= null && value.trim().startsWith("#{") && value.trim().endsWith("}")) {
                                                String httpArgKey = getKeyByXMLValue(value);
                                                String httpArgValue = "";
                                                if(httpArgKey != null && httpArgKey.contains(".")) { //如果#{}里面的内容是含有item.xx时
                                                    httpArgValue = (String) parseOgnlExpress(map, collection + "[" + i + "]" + httpArgKey.substring(httpArgKey.indexOf(".")));
                                                } else { //如果直接取map中的参数没有.
                                                    httpArgValue = (String) map.get(httpArgKey);
                                                }
                                                paramsBasicNameValuePairs.add(new BasicNameValuePair(key, httpArgValue));
                                            } else {
                                                paramsBasicNameValuePairs.add(new BasicNameValuePair(key, value));
                                            }
                                        }
                                    }
                                    //foreach下直系if节点
                                    List<Element> ifElementsInFor = forElement.elements("if");
                                    if(ifElementsInFor != null) {
                                        for (Element ifElement : ifElementsInFor) {
                                            String express = ifElement.attributeValue("test");
                                            if(express.contains(item + ".")) {
                                                express = express.replace(item + ".", collection + "[" + i + "].");
                                            }
                                            try {
                                                boolean result = (boolean)parseOgnlExpress(map, express);
                                                if(!result) {
                                                    continue;
                                                }
                                            } catch (Exception e) {
                                                logger.error("if 标签表达式：" + express + "解析出错");
                                            }
                                            List<Element> params = ifElement.elements("param");
                                            if(params != null) {
                                                for (Element paramElement : params) {
                                                    String key = paramElement.attributeValue("key");
                                                    if(index != null && !index.trim().equals("")) {
                                                        key = key.replace("index", "" + i); //key中有序列的替换
                                                    }
                                                    String value = paramElement.attributeValue("value");
                                                    if (value!= null && value.trim().startsWith("#{") && value.trim().endsWith("}")) {
                                                        String httpArgKey = getKeyByXMLValue(value);
                                                        String httpArgValue = "";
                                                        if(httpArgKey != null && httpArgKey.contains(".")) { //如果#{}里面的内容是含有item.xx时
                                                            httpArgValue = (String) parseOgnlExpress(map, collection + "[" + i + "]" + httpArgKey.substring(httpArgKey.indexOf(".")));
                                                        } else { //如果直接取map中的参数没有.
                                                            httpArgValue = (String) map.get(httpArgKey);
                                                        }
                                                        paramsBasicNameValuePairs.add(new BasicNameValuePair(key, httpArgValue));
                                                    } else {
                                                        paramsBasicNameValuePairs.add(new BasicNameValuePair(key, value));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //TODO 测试通过后需要删除
                    if (paramsBasicNameValuePairs != null && paramsBasicNameValuePairs.size() != 0) {
                        for (BasicNameValuePair paramsBasicNameValuePair : paramsBasicNameValuePairs) {
                            System.out.println("key:" + paramsBasicNameValuePair.getName() + "-----------value:" + paramsBasicNameValuePair.getValue());
                        }
                        if (true) {
                            return null;
                        }
                    }

                    //解析method
                    String methodHttp = http.element("method").getText();
                    HttpUriRequest request = null;
                    if (methodHttp.equalsIgnoreCase("get")) {
                        request = new HttpGet(url);
                    } else if (methodHttp.equalsIgnoreCase("post")) {
                        HttpPost post = new HttpPost(url);
                        post.setEntity(new UrlEncodedFormEntity(paramsBasicNameValuePairs, paramsEncode));
                        request = post;
                    } else {
                        // method 非法
                        throw new Throwable("method的值" + methodHttp + "非法,请更改");
                    }
                    //解析headers
                    setDefaultHeaders(request);
                    if(http.element("headers") != null) {
                        List<Element> headers = http.element("headers").elements("header");
                        if(headers != null) {
                            for (Element header : headers) {
                                String headerKey = header.attributeValue("key");
                                String headerValue = header.attributeValue("value");
                                if(StringUtil.isBlank(headerKey) || StringUtil.isBlank(headerValue)) {
                                    throw new Throwable("header中key:" + headerKey + "或value:"  + headerValue + "为空");
                                }
                                request.setHeader(headerKey, headerValue);
                            }
                        }
                    }
                    //执行请求
                    HttpResponse response = session.getHttpClient().execute(request);
                    if(response == null) {
                        return null;
                    }
                    String str = EntityUtils.toString(response.getEntity(), resEncode);
                    EntityUtils.consume(response.getEntity()); //会自动释放连接
                    return str;
                }
            }
        }
        return null;
    }

    /**
     * 获取xml中value代表的值
     * @param map
     * @param xmlValue
     * @return 如果xml value中有#{}则返回map中的值，无则返回xmlValue
     */
    public String getValue(Map<String, Object> map, String xmlValue) {
        if (xmlValue.trim().startsWith("#{") && xmlValue.trim().endsWith("}")) {
            String key = getKeyByXMLValue(xmlValue);
            String value = map.get(key).toString();
            return value;
        } else {
            return xmlValue;
        }
    }

    /**
     * 解析ognl表达式
     */
    public static Object parseOgnlExpress(Map<String, Object> params, String express) throws Exception {
        OgnlContext context = new OgnlContext();
        context.setRoot(params);
        Object o = Ognl.getValue(Ognl.parseExpression(express), context, context.getRoot());
        return o;
    }

    public static void setDefaultHeaders(HttpRequest request) {
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1941.0 Safari/537.36");
//        request.setHeader("Accept-Encoding", "gzip,deflate");
        request.setHeader("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
        request.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
        request.setHeader("Cache-Control", "no-cache");
//        request.setHeader("X-Requested-With", "XMLHttpRequest"); //ajax请求头中含有
    }

    /**
     * 从xml的value中取出key
     * @param XMLValue 格式为 #{xxxx}
     * @return
     */
    public static String getKeyByXMLValue(String XMLValue) {
        return XMLValue.trim().substring(XMLValue.trim().indexOf("#{") + 2, XMLValue.trim().lastIndexOf("}"));
    }

}
