package com.cdzy.cr.util;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.alibaba.fastjson.JSON;

public class JavaScriptEngineUtil {
    /**
     * @param jsText
     * @param key
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getValue(String jsText, String[] key) throws Exception {
        if (key == null || key.length == 0) {
            return null;
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        engine.eval(jsText);
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < key.length; i++) {
            result.put(key[i], engine.get(key[i]));
        }
        return result;
    }

    public static void main(String args[]) {
        String js = "var themeFlight=new Array();";
        js += "themeFlight[1]=new Array('1','高尔夫','','');";
        try {
            System.out.println(JSON.toJSON(getValue(js, new String[]{"themeFlight"})));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
