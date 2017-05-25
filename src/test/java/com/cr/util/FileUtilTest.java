package com.cr.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;

import org.apache.http.protocol.HttpCoreContext;
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

    @Test
    public void deleteFileDirTest() {
        try {
            System.out.println(FileUtil.deleteFileDir("D:\\mysql-5.6.35-winx64"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void loadJarTest() {
        HttpCoreContext b = new HttpCoreContext();
        System.out.println(b.getClass().getResource("").getPath());
    }

    @Test
    public void test() throws Exception{
        //new一个URL对象  
        URL url = new URL("https://www.baidu.com/img/bd_logo1.png");  
        //打开链接  
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
        //设置请求方式为"GET"  
        conn.setRequestMethod("GET");  
        //超时响应时间为5秒  
        conn.setConnectTimeout(5 * 1000);  
        //通过输入流获取图片数据  
        InputStream inStream = conn.getInputStream();  
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性  
        byte[] data = readInputStream(inStream);  
        //new一个文件对象用来保存图片，默认保存当前工程根目录  
        File imageFile = new File("D:/git/cr-util/src/test/java/com/cr/util/BeautyGirl.jpg");  
        //创建输出流  
        FileOutputStream outStream = new FileOutputStream(imageFile);  
        //写入数据  
        outStream.write(data);  
        //关闭输出流  
        outStream.close();  
    }

    @Test
    public void urlFile() throws Exception {
        URI uri = new URI("https://www.baidu.com/img/bd_logo1.png");
//        URL url = new URL("https://www.baidu.com/img/bd_logo1.png"); 
        File f = new File(uri);
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        //创建一个Buffer字符串  
        byte[] buffer = new byte[1024];  
        //每次读取的字符串长度，如果为-1，代表全部读取完毕  
        int len = 0;  
        //使用一个输入流从buffer里把数据读取出来  
        while( (len=inStream.read(buffer)) != -1 ){  
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
            outStream.write(buffer, 0, len);  
        }  
        //关闭输入流  
        inStream.close();  
        //把outStream里的数据写入内存  
        return outStream.toByteArray();  
    }  
}
