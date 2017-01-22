package com.cdzy.cr.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Iterator;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FileUtilTest {

    // @Test
    public void txtTest() {
        String testDataPath = this.getClass().getResource("").getPath() + "data" + File.separator + "txt.js";
        String txt = FileUtil.readFile2String(testDataPath); // 此方法读取字符串有问题
        System.out.println(txt);
        txt = FileUtil.readTxtFile2String(testDataPath);
        System.out.println(txt);
        Long time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            txt = FileUtil.readTxtFile2String(testDataPath);
        }
        System.out.println("String用时" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            txt = FileUtil.readTxtFile2StrByStringBuffer(testDataPath);
        }
        System.out.println("StringBuffer用时" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            txt = FileUtil.readTxtFile2StrByStringBuilder((testDataPath));
        }
        System.out.println("StringBuilder用时" + (System.currentTimeMillis() - time));

    }

    @Test
    public void readBigFileTest() {
        String testDataPath = this.getClass().getResource("").getPath() + "data" + File.separator + "AllLog[2016-11-02].log";
        Long time = System.currentTimeMillis();
        FileUtil.readTxtFile2String(testDataPath);
        System.out.println("String用时" + (System.currentTimeMillis() - time));
    }

    @Test
    public void parseHarParam() {
        String txtPath = this.getClass().getResource("").getPath() + "data" + File.separator + "har.txt";
        writeXml(txtPath, "D:\\workspace\\cr-util\\src\\test\\java\\com\\cdzy\\cr\\util\\data\\har.xml", "getDestination");
    }

    public void writeXml(String sourceHarPath, String targetPathName, String intferfaceName) {
        FileWriter fw = null;
        try {
            File file = new File(targetPathName);
            fw = new FileWriter(file);
            String txt = FileUtil.readTxtFile2StrByStringBuilder(sourceHarPath);
            JSONObject request = JSON.parseObject(txt).getJSONObject("log").getJSONArray("entries").getJSONObject(0).getJSONObject("request");
            fw.append("<http id=\"" + intferfaceName + "\">");
            fw.append("\r\n");
            fw.append("    <url><![CDATA[" + request.getString("url") + "]]></url>");
            fw.append("\r\n");
            JSONArray headers = request.getJSONArray("headers");
            if(headers != null && headers.size() != 0) {
                fw.append("    <headers>");
                fw.append("\r\n");
                for (Iterator iterator = headers.iterator(); iterator.hasNext();) {
                    JSONObject header = (JSONObject) iterator.next();
                    if(header.getString("name").equals("Cookie")) continue;
                    fw.append("        <header key=\""+ header.getString("name") +"\" value=\"" + header.getString("value") + "\"/>");
                    fw.append("\r\n");
                }
                fw.append("    </headers>");
                fw.append("\r\n");
            }
            fw.append("    <method>" + request.getString("method") + "</method>");
            fw.append("\r\n");
            JSONArray params = request.getJSONObject("postData").getJSONArray("params");
            if(params != null && params.size() != 0) {
                params = JSON.parseArray(URLDecoder.decode(params.toJSONString(), "UTF-8"));
                fw.append("    <params>");
                fw.append("\r\n");
                for (Iterator iterator = params.iterator(); iterator.hasNext();) {
                    JSONObject JSON = (JSONObject) iterator.next();
                    if(JSON.getString("name").equals("")) continue;
                    fw.append("        <param key=\""+ JSON.getString("name") +"\" value=\"" + JSON.getString("value") + "\"/>");
                    fw.append("\r\n");
                }
                fw.append("    </params>");
                fw.append("\r\n");
            }
            fw.append("</http>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
