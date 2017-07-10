package com.cr.util;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * create in 2017年04月26日
 * @category TODO
 * @author chenyi
 */
public class LogisticsTest {

    @Test
    public void test() throws ClientProtocolException, IOException, Exception {
//        SyncSession session = new SyncSession();
//        List<BasicNameValuePair> params =new ArrayList<>();
//        params.add(new BasicNameValuePair("secretkey", "03245846-d5d3-4f22-a10f-8338fd7e74e692073"));
//        try {
//            String str = HttpUtil.getPostHttpResStr(session, "http://www.pfcexpress.com/webservice/APIWebService.asmx/getCountry", true, params);
//            System.out.println(str);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        HttpClient httpClient = HttpClients.createDefault();
        String url = "http://www.pfcexpress.com/webservice/APIWebService.asmx/getCountry";
        HttpPost post = new HttpPost(url);
        HttpUtil.setBrowerAttrHeaders(post, true);
        StringEntity stringEntity = new StringEntity("{secretkey:'03245846-d5d3-4f22-a10f-8338fd7e74e692073'}");
        post.setEntity(stringEntity);
        String str = HttpUtil.responseEntity2Str(httpClient.execute(post));
        str = str.replace("\\\\", "-").replace("\\", "").replace("-", "\\");
        str = str.substring(str.indexOf("["), str.indexOf("]") + 1);
        System.out.println(str);
        JSONArray array = JSONArray.parseArray(str);
        for (int i = 0; i <  array.size(); i++) {
            JSONObject data =  array.getJSONObject(i);
            System.out.println(data.getString("Cnname"));
        }
    }

    @Test
    public void str() {
        String str = "[{\"ShortName\":\"AF\",\"EnName\":\"Afghanistan\",\"Cnname\":\"\\u963F\\u5BCC\\u6C57\",\"Base_placeId\":\"20\"}]";
        str = str.replace("\\", "-").replace("\\", "").replace("-", "\\");
        System.out.println(str);
    }

    /**
     * @throws Exception 
     * @throws IOException 
     * @throws ClientProtocolException 
     * 
     */
    @Test
    public void winitTest() throws ClientProtocolException, IOException, Exception {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "http://openapi.sandbox.winit.com.cn/openapi/service";
        HttpPost post = new HttpPost(url);
        HttpUtil.setBrowerAttrHeaders(post, true);
        StringEntity stringEntity = new StringEntity(addPickupService());
        post.setEntity(stringEntity);
        String str = HttpUtil.responseEntity2Str(httpClient.execute(post));
        System.out.println(str);

        stringEntity = new StringEntity(getByPickupService());
        post.setEntity(stringEntity);
        str = HttpUtil.responseEntity2Str(httpClient.execute(post));
        System.out.println(str);
    }

    public String addPickupService() {
        JSONObject param = new JSONObject();
        String action = "ums.address.add";
        param.put("action", action);
        param.put("app_key", "support@jtongi.cn");
        JSONObject dataValue = new JSONObject();
        dataValue.put("code", "test110");
        dataValue.put("cityCode", "SHENZHEN");
        dataValue.put("contactNumber", "22222222");
        dataValue.put("contactPerson", "2");
        dataValue.put("contactPersonEn", "2");
        dataValue.put("countryCode", "CN");
        dataValue.put("detail1", "test");
        dataValue.put("detail2", "autotest");
        dataValue.put("detailCn", "2222");
        dataValue.put("detailEn", "22222");
        dataValue.put("districtCode", "NA");
        dataValue.put("districtName", "NanShan");
        dataValue.put("doorplate", "winit");
        dataValue.put("email", "winit@winit.com");
        dataValue.put("postCode", "518000");
        dataValue.put("state", "GUANGDONG");
        param.put("data", dataValue);
        param.put("format", "json");
        param.put("platform", "SELLERERP");
        param.put("sign_method", "md5");
        param.put("timestamp", DateUtil.now());
        param.put("version", "1.0");
        param.put("sign", getSign(action, dataValue.toJSONString()));
        param.put("language", "zh_CN");
        System.out.println(param.toJSONString());
        return param.toJSONString();
    }

    public String getByPickupService() {
        JSONObject param = new JSONObject();
        String action = "ums.address.getByPickupService";
        param.put("action", action);
        param.put("app_key", "support@jtongi.cn");
        JSONObject dataValue = new JSONObject();
        dataValue.put("dispatchType", "C");
        dataValue.put("winitProductCode", "USCN00001");
        param.put("data", dataValue);
        param.put("format", "json");
        param.put("platform", "SELLERERP");
        param.put("sign_method", "md5");
        param.put("timestamp", DateUtil.now());
        param.put("version", "1.0");
        param.put("sign", getSign(action, dataValue.toJSONString()));
        param.put("language", "zh_CN");
        System.out.println(param.toJSONString());
        return param.toJSONString();
    }
    public String getSign(String action, String dataValue) {
        //签名串 = token + action + actionValue + app_key + app_keyValue + data + dataValue + format + formatValue + platform + platformValue + sign_method + sign_methodValue + timestamp + timestampValue + version + versionValue + token
        String str = "";
        String token = "89435277FA3BA272DE795559998E";
//        String token = "8078D70FBA1C72DA1F9BB4F96F0226C4";
//        str += token + "action" + action + "app_key" + "support@jtongi.cn"
        str += token + "action" + action + "app_key" + "rebecca"
            + "data" + dataValue
            + "format" + "json" + "platform" + "SELLERERP" + "sign_method" + "md5" + "timestamp" + DateUtil.now() + "version" + "1.0" + token;
        str = MD5.md5(str).toUpperCase();
        System.out.println(str);
        return str;
    } 
}
