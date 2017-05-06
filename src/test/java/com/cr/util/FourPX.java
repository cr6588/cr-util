package com.cr.util;
import javax.xml.rpc.ParameterMode; 
import java.io.IOException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

/**
 * create in 2017年05月04日
 * @category TODO
 * @auther chenyi
 */
public class FourPX {

    @Test
    public void test() throws ClientProtocolException, IOException, Exception {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "http://apisandbox.4pxtech.com:8090/OrderOnline/ws/OrderOnlineService.dll?wsdl";
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1941.0 Safari/537.36");
        post.setHeader("Accept-Encoding", "gzip,deflate");
        post.setHeader("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
        post.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
        post.setHeader("Cache-Control", "no-cache");
        post.setHeader("X-Requested-With", "XMLHttpRequest");
        post.setHeader("Accept", "application/xml, text/javascript, */*; q=0.01");
        post.setHeader("Content-Type", "application/xml");
        String str = FileUtil.readTxtFile2StrByStringBuilder(this.getClass().getResource("").getPath() + "data/4px.xml");
        System.out.println(str);
        StringEntity stringEntity = new StringEntity(str);
        post.setEntity(stringEntity);
        str = HttpUtil.responseEntity2Str(httpClient.execute(post));
        System.out.println(str);
    }

    @Test
    public void axisTest() {
        try {
            String endpoint = "http://api.4px.com:8058/OrderOnline/ws/OrderOnlineService.dll?wsdl";
            // 直接引用远程的wsdl文件
            // 以下都是套路
            Service service = new Service();

            Call call =  (Call) service.createCall();
            call.setTargetEndpointAddress(endpoint);
            call.setOperationName("FindOrderService");// WSDL里面描述的接口名称

            call.setSOAPActionURI("http://api.4px.com:8058/OrderOnline/ws/OrderOnlineService.dll?wsdl");
            call.addParameter("AuthToken", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数
            call.addParameter("ReferenceNumber", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数
//            call.addParameter("HJDM", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数
//            call.addParameter("BLSM", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数
//            call.addParameter("SetState", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数
//            call.addParameter("CZRID", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数
//            call.addParameter("LogIP", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数

            call.setReturnType(XMLType.XSD_STRING);// 设置返回类型

            String AuthToken = "AA97B38BED18FD0819D247BE3306A2D1";
            String ReferenceNumber = "22";
//            String hjdm = "33";
//            String blsm = "33";
//            String ss = "33";
//            String cid = "33";
//            String ip = "33";
            String result = (String) call.invoke(new Object[] { AuthToken, ReferenceNumber});

            // 给方法传递参数，并且调用方法
            System.out.println("result is " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
