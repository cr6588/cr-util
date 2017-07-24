package com.cr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.cr.bean.ConInfo;
import com.cr.bean.SysI18n;
import com.cr.util.translate.TransApi;


/**
 * create in 2017年07月10日
 * @category jsp中文翻译
 * @author chenyi
 */
public class AutoTranslate {

    List<SysI18n> needInsert = new ArrayList<>();

    @Test
    public void test() {
        String jspPath = this.getClass().getResource("").getPath() + "data/eg1.jsp";
        File file = new File(jspPath);
        String copuJspPath = this.getClass().getResource("").getPath() + "data/eg1Copy.jsp";
        File copyFile = new File(copuJspPath);
        try (
            
            FileReader fis = new FileReader(file);
            BufferedReader br = new BufferedReader(fis);
            FileWriter fw = new FileWriter(copyFile);
        ){
            String str = null;
            boolean isInJs = false;
            while ((str = br.readLine()) != null) {
                if(str.contains("<script type=\"text/javascript\">")) {
                    isInJs = true;
                }
                if(str.contains("</script>")) {
                    isInJs = false;
                }
                String translateStr = "";
                if(isInJs) {
                    translateStr = translateStrInJs(str);
                } else {
                    translateStr = translateStrInHtml(str);
                }
                fw.write(translateStr + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String translateStrInHtml(String str) {
        if(str.trim().startsWith("<%--") || str.trim().startsWith("<!--")) {
            return str;
        }
        List<String> zhList = new ArrayList<>();  //需要翻译的中文  eg:修改回邮地址
        String key = "";
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if(isChineseByREG(ch + "")) {
                key += ch;
            } else if(!key.equals("")) {
                if(ch == '(' || ch == ')') {
                    key += ch;
                } else {
                    zhList.add(key);
                    key = "";
                }
            }
        }
        for (int i = 0; i < zhList.size(); i++) {
            String zh = zhList.get(i);
            List<SysI18n> i18ns = getI18nsByZhText(local, zh);
            String code = "";
            if(i18ns == null || i18ns.isEmpty()) {
                String enText = getEnFromZh(zh);
                code = "message." + enText.toLowerCase().replace(" ", ".");
                SysI18n enI18n = new SysI18n(code, enText, "en_US");
                SysI18n zhI18n = new SysI18n(code, zh, "zh_CN");
                needInsert.add(enI18n);
                needInsert.add(zhI18n);
            } else {
                boolean hasEn = false;
                for (SysI18n i18n : i18ns) {
                    code = i18n.getCode();
                    if(i18n.getLanguage().equals("en_US")) {
                        hasEn = true;
                    }
                    if(!hasEn) {
                        SysI18n enI18n = new SysI18n(code, getEnFromZh(zh), "en_US");
                        needInsert.add(enI18n);
                    }
                }
            }
            String s = "<spring:message code=\"" + code + "\"/>";
            str = str.replace(zh, s);
        }
        return str;
    }


    // 只能判断部分CJK字符（CJK统一汉字）
    public static boolean isChineseByREG(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str.trim()).find();
    }

    @Test
    public void translateStrInJsTest() {
        String str = "        title: '修改回邮地址',";
        System.out.println(translateStrInJs(str));
    }

    /**
     * 替换str中的中文信息
     * @param str
     * @return
     */
    public String translateStrInJs(String str) {
        List<String> zhList = new ArrayList<>();  //需要翻译的中文  eg:修改回邮地址
        List<String> keyList = new ArrayList<>(); //需要翻译的中文带有''或者"" eg:'修改回邮地址'
        String key = "";
        char keyStartMark = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if((ch == '\'' || ch == '"') && keyStartMark == 0) {
                keyStartMark = ch;
            }
            if(isChineseByREG(ch + "")) {
                key += ch;
            } else if(!key.equals("")) {
                if(ch == keyStartMark) { //以'或"开头且结尾中间的字符有中文的为text, bug:如果有转义会出错
                    zhList.add(key);
                    keyList.add(keyStartMark + key + keyStartMark);
                    key = "";
                    keyStartMark = 0;
                } else {
                    key += ch;
                }
            }
        }
        for (int i = 0; i < keyList.size(); i++) {
            String zh = zhList.get(i);
            List<SysI18n> i18ns = getI18nsByZhText(local, zh);
            String code = "";
            if(i18ns == null || i18ns.isEmpty()) {
                String enText = getEnFromZh(zh);
                code = "message." + enText.toLowerCase().replace(" ", ".");
                SysI18n enI18n = new SysI18n(code, enText, "en_US");
                SysI18n zhI18n = new SysI18n(code, zh, "zh_CN");
                needInsert.add(enI18n);
                needInsert.add(zhI18n);
            } else {
                boolean hasEn = false;
                for (SysI18n i18n : i18ns) {
                    code = i18n.getCode();
                    if(i18n.getLanguage().equals("en_US")) {
                        hasEn = true;
                    }
                    if(!hasEn) {
                        SysI18n enI18n = new SysI18n(code, getEnFromZh(zh), "en_US");
                        needInsert.add(enI18n);
                    }
                }
            }
            String s = "sjdf.tools.i18n.getMessage('" + code + "')";
            str = str.replace(keyList.get(i), s);
        }
        return str;
    }

    ConInfo local = new ConInfo("localhost", "dev", "dev");              // 设置本地的mysql用户名密码

    /**
     * 根据中文text获取国际化信息
     * @param c
     * @param zhText
     * @return
     */
    public List<SysI18n> getI18nsByZhText(ConInfo c, String zhText) {
        Connection con = null;
        List<SysI18n> i18ns = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + c.getHost()+ ":3306/erp?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8",c.getUsername(), c.getPassword());
            Statement stmt = con.createStatement();
            String sql = "";
            sql = "select * from `sys_i18n` where `text` = '" + zhText + "'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                SysI18n SysI18n = new SysI18n(rs.getString(2), rs.getString(3), rs.getString(4));
                i18ns.add(SysI18n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i18ns;
    }

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20170711000064053";
    private static final String SECURITY_KEY = "tRBK5YBGGuSto0azRHZN";

    @Test
    public void getEnFromZhTest() {
        System.out.println(getEnFromZh("修改回邮地址"));
    }

    private String getEnFromZh(String query) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        //{"from":"zh","to":"en","trans_result":[{"src":"\u9ad8\u5ea6600\u7c73","dst":"Height 600 meters"}]}
        String res = api.getTransResult(query, "zh", "en");
        return JSON.parseObject(res).getJSONArray("trans_result").getJSONObject(0).getString("dst");
    }
}
