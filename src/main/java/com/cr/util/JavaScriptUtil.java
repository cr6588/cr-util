package com.cr.util;

import java.util.HashMap;
import java.util.Map;

public class JavaScriptUtil {

    /**
     * 解析javascript中的变量
     * @param script
     * @return
     * @throws Exception
     */
    public static Map<String, Object> parseScriptVar(String script) throws Exception {
        String[] rowStr = script.split("\n");
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < rowStr.length; i++) {
            if (rowStr[i].contains("var ")) {
                String keyValueStr = rowStr[i].substring(rowStr[i].indexOf("var ") + "var ".length()).replace(" ", "").replace("\r", "");
                if (!keyValueStr.contains("=")) {
                    throw new Exception("字符串中没有定义变量");
                }
                if (keyValueStr.split("=").length > 2) {
                    throw new Exception("暂不支持每行多变量解析");
                }
                String key = keyValueStr.split("=")[0];
                String value = keyValueStr.split("=")[1];
                if(value.substring(value.length() - 1).equals(";")) {
                    value = value.substring(0, value.length() - 1);
                }
                
                if (result.get(key) != null) {
                    throw new Exception("给定的js中含有重复变量名" + key);
                }
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 解析javascript中的变量
     * @param script
     * @return
     * @throws Exception
     */
    public static String parseScriptVarByKey(String script, String key) throws Exception {
        String result = script.substring(script.indexOf(key) + key.length()).replace(" ", "");
        result = result.replace("=", "");
        result = result.substring(0, result.indexOf(";"));
        return result;
    }
}
