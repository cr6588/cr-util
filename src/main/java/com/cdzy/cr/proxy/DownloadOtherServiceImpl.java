package com.cdzy.cr.proxy;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cdzy.cr.util.SyncSession;

/**
 * 下载其它相关的类
 * @author test
 */
public class DownloadOtherServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(DownloadOtherServiceImpl.class);
    private DownloadOtherHttp downloadOtherHttp;
    public DownloadOtherServiceImpl () {
        downloadOtherHttp = MapperProxy.getHttpInstance(DownloadOtherHttp.class, downloadOtherHttp); // downloadOtherHttp为空才会初始化，但在方法中null = 实例之后，并不会将downloadOtherHttp=实例，所以又必须返回一个实例让其=实例
    }

    /************************************mapper 测试 start**************************************/
    public JSONArray getDestination(SyncSession session, String secondCategoryId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("urlformat", secondCategoryId);
        String res = null;
        try {
            res = downloadOtherHttp.getDestination(params, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(res).getJSONArray("list");
    }

    public String updRoute(SyncSession session) {
        Map<String, Object> params = new HashMap<String, Object>();
        String res = null;
        params.put("tag_id", "192");
        params.put("roles", getDestination(session, "28"));
        try {
            res = downloadOtherHttp.updRoute(params, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    /************************************mapper 测试 end**************************************/

}
