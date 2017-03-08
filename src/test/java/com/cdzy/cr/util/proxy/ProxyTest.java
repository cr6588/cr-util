package com.cdzy.cr.util.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.cr.http.DownloadOtherHttp;
import com.cr.http.IHello;
import com.cr.http.proxy.FacadeProxy;
import com.cr.http.proxy.MapperClient;
import com.cr.util.SyncSession;

public class ProxyTest {

    @Test
    public void test() {
        IHello hello = FacadeProxy.newMapperProxy(IHello.class);  
        System.out.println(hello.say("hello world"));
    }

    @Test
    public void parseXMLTest() throws Throwable {
        SAXReader saxReader = new SAXReader();
        String path = DownloadOtherHttp.class.getResource("").getPath() + "xml" + File.separator + "mapper.xml";
        Document document = saxReader.read(new FileInputStream(path));
        MapperClient client = new MapperClient(document);
        Map<String, Object> params = new HashMap<String, Object>();
        String res = null;
        params.put("tag_id", "192");
        String roles = "[{\"update_time\":\"2016-07-19 16:31:10\",\"name\":\"东京\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"182\"},{\"update_time\":\"2016-07-19 16:31:14\",\"name\":\"大阪\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"183\"},{\"update_time\":\"2016-07-19 16:31:17\",\"name\":\"京都\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"184\"},{\"update_time\":\"2016-07-19 16:31:19\",\"name\":\"北海道\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"185\"},{\"update_time\":\"2016-07-19 16:31:22\",\"name\":\"冲绳\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"186\"},{\"update_time\":\"2016-07-19 16:31:24\",\"name\":\"福冈\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"187\"},{\"update_time\":\"2016-07-19 16:31:26\",\"name\":\"熊本\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"188\"},{\"update_time\":\"2016-07-19 16:31:28\",\"name\":\"长崎\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"189\"},{\"update_time\":\"2016-07-19 16:31:30\",\"name\":\"神户\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"190\"},{\"update_time\":\"2016-07-19 16:31:40\",\"name\":\"首尔\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"191\"},{\"update_time\":\"2016-07-19 16:31:42\",\"name\":\"济州岛\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"192\"},{\"update_time\":\"2016-07-19 16:31:45\",\"name\":\"江原道\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"193\"},{\"update_time\":\"2016-07-19 16:31:46\",\"name\":\"仁川\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"194\"},{\"update_time\":\"2016-07-27 13:05:37\",\"name\":\"岗山\",\"seq\":\"0\",\"catg_id\":\"28\",\"tag_id\":\"276\"}]";
        params.put("roles", JSON.parseArray(roles));
        client.parseXML("updRoute", new SyncSession(), params);
    }
}
