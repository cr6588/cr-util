package com.cr.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * create in 2017年06月22日
 * @category TODO
 * @author chenyi
 */
public class RegexTest {

    @Test
    public void replaceTest() {
        String str = "'平认可物流 使用json串保存{\"平台ID_1\":平台认可物流ID}'".replace("\"", "\\\"");
        System.out.println(str);
    }

    @Test
    public void matchMp4() {
        String str = FileUtil.readTxtFile2StrByStringBuilder("D:\\test\\prods\\B01MS6MO77.html");
//        String str = "\",\"url\":\"https://d2y5sgsy8bbmb8.cloudfront.net/v2/bd730d66-d356-5ad4-a49e-e1d3fb3a89fe/ShortForm-Generic-480p-16-9-1409173089793-rpcbe5.mp4\",\"vide";
        Pattern p = Pattern.compile("https://[0-9a-zA-Z\\./-]*\\.mp4");
        Matcher m = p.matcher(str);
        Set<String> urls = new HashSet<>();
        while(m.find()) {
            urls.add(m.group());
        }
        if(!urls.isEmpty()) {
            String urlStr = "";
            for(String url : urls) {
                urlStr += url + ",";
            }
            System.out.println(urlStr.substring(0, urlStr.length() - 1));
        }
    }
}
