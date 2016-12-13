package com.cdzy.cr.proxy;

import java.util.Map;

import com.cdzy.cr.util.SyncSession;

public interface DownloadOtherHttp {
    public String getDestination(Map<String, Object> params, SyncSession session);
}
