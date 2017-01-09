package com.cdzy.cr.proxy;

import java.util.Map;

import com.cdzy.cr.util.SyncSession;

public interface DownloadOtherHttp {
    /**
     * 获取目的地
     * @param params
     * @param session
     * @return
     * @throws Exception
     */
    public String getDestination(Map<String, Object> params, SyncSession session) throws Exception;

    /**
     * 修改行程
     * @param params
     * @param session
     * @return
     * @throws Exception
     */
    public String updRoute(Map<String, Object> params, SyncSession session) throws Exception;
}
