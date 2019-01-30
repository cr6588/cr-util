package com.cr.util;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cr.bean.RequestResult;

/**
 * create in 2019年01月19日
 * @category 腾讯ocr识别
 * @author chenyi
 */
public class TencentOcrUtil {

    private static final String SECRETKEY = "";
    private static final String SECRETID = "";
    private static final long APPID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TencentOcrUtil.class);

    /**
     * https://cloud.tencent.com/document/product/866/17597
     * 统一身份证识别
     * @param url
     * @return 正确时身份证号返回在info
     */
    public static RequestResult<String> ocrIdCard(String url) {
        JSONObject param = new JSONObject();
        param.put("appid", APPID);
        param.put("card_type", 0);
        JSONArray url_list = new JSONArray();
        url_list.add(url);
        param.put("url_list", url_list);
        String bodyStr = param.toJSONString();
        StringEntity entity = new StringEntity(bodyStr, "UTF-8");
        try {
            Header[] header = getHeader();
            String str = HttpClientUtil.getStrBySendJSONPost("https://recognition.image.myqcloud.com/ocr/idcard", entity, header );
            JSONObject res = JSONObject.parseObject(str);
            if(res.getInteger("code") != null && res.getInteger("code") != 0) {
                return RequestResult.createErr(res.getString("message"));
            }
            JSONObject jsonObject = res.getJSONArray("result_list").getJSONObject(0);
            if(jsonObject.getInteger("code") != 0) {
                return RequestResult.createErr(jsonObject.getString("message"));
            }
            String id = jsonObject.getJSONObject("data").getString("id");
            return RequestResult.createSucc(id);
        } catch (Exception e) {
            return RequestResult.createErr(e.getMessage());
        }
    }

    private static Header[] getHeader() throws Exception {
        String authorization = TencentSign.appSign(APPID, SECRETID, SECRETKEY, null, 20);
        Header[] header =  {new BasicHeader("host", "recognition.image.myqcloud.com"),
                new BasicHeader("content-type", "application/json"),
                new BasicHeader("authorization", authorization)
        };
        return header;
    }

    /**
     * https://cloud.tencent.com/document/product/866/17598
     * @param url 识别营业执照统一社会信用代码
     * @return 正确时营业执照号返回在info中
     */
    public static RequestResult<String> ocrBizlicense(String url) {
        JSONObject param = new JSONObject();
        param.put("appid", APPID + "");
        param.put("url", url);
        String bodyStr = param.toJSONString();
        StringEntity entity = new StringEntity(bodyStr, "UTF-8");
        try {
            Header[] header = getHeader();
            String str = HttpClientUtil.getStrBySendJSONPost("https://recognition.image.myqcloud.com/ocr/bizlicense", entity, header );
            System.out.println(str);
            JSONObject res = JSONObject.parseObject(str);
            if(res.getInteger("code") != null && res.getInteger("code") != 0) {
                return  RequestResult.createErr(res.getString("message"));
            }
            //注册号
            JSONArray items = res.getJSONObject("data").getJSONArray("items");
            for (Object i : items) {
                JSONObject item = (JSONObject)i;
                if("注册号".equals(item.getString("item"))) {
                    String id = item.getString("itemstring");
                    RequestResult<String> succ = RequestResult.createSucc(id);
                    return succ;
                }
            }
            return RequestResult.createSucc();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return  RequestResult.createErr(e.getMessage());
        }
    }
}
