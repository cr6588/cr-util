package com.cr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.cr.bean.SysI18n;
import com.cr.util.translate.TransApi;

/**
 * create in 2017年07月10日
 * @category jsp中文翻译
 * @author chenyi
 */
public class AutoTranslate {

    private static Logger logger = LoggerFactory.getLogger(AutoTranslate.class);
    ConInfo local = new ConInfo("localhost", "dev", "dev"); // 设置本地的mysql用户名密码
    // 在平台申请的APP_ID 详见
    // http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20170711000064053";
    private static final String SECURITY_KEY = "tRBK5YBGGuSto0azRHZN";

    Map<String, SysI18n> needInsert = new HashMap<>();

    @Test
    public void length() {
        String s = "message.rose.red.summer.2014.new.children's.dress,.high.quality.new.flower.yarn.dress,.children's.dress";
        System.out.println(s.length());
    }
    @Test
    public void jsoupclearTest() {
        String str = " asdf <li>                                                <span><em title=\"上海仓\">上海仓</em></span><option value=\"1\" selected=\"\">1倍asd111f</option><span class=\"key fb\">11号哦Deleteasdfss111</span>或： <span class=\"key fb\">Del</span><strong class=\"c999\">删除</strong></li> 中 ";
        String key = "";
        List<String> zhList = new ArrayList<>();
        boolean start = false;
        boolean existCh = false;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) == '<') {
                start = true;
                key = key.trim();
                if(!key.equals("") && existCh){
                    if(key.endsWith("：") || key.endsWith(":")) {
                        //如果key以：或:结尾，key去除
                        key = key.substring(0, key.lastIndexOf("："));
                    }
                    key = key.replaceAll("^([0-9]+)?(.*[^0-9])([0-9]+)?$", "$2");
                    zhList.add(key);
                }
                key = "";
                existCh = false;
            }
            if (isChineseByREG(str.charAt(i) + "")) {
                existCh = true;
            }
            if(!start) {
                key += str.charAt(i);
            }
            if(str.charAt(i) == '>') {
                start = false;
            }
        }
        //以汉字结尾在上面的循环中不会加入到zhList中
        if (!key.equals("") && existCh) {
            zhList.add(key);
        }
        System.out.println(zhList.toString());
    }

    @Test
    public void translateTest() {
        String jspPath = "D:/git/erp/erp-web/src/main/webapp/WEB-INF/templates/default/share/left/finance";
        File file = new File(jspPath + ".jsp");
        String copuJspPath = jspPath + "Copy.jsp";
        File copyFile = new File(copuJspPath);
        try (
            FileReader fis = new FileReader(file);
            BufferedReader br = new BufferedReader(fis);
            FileWriter fw = new FileWriter(copyFile);) {
            String str = null;
            boolean isInJs = false;
            while ((str = br.readLine()) != null) {
                if (str.contains("<script type=\"text/javascript\">") || str.contains("<script>")) {
                    isInJs = true;
                }
                if (str.contains("</script>")) {
                    isInJs = false;
                }
                String translateStr = "";
                if (isInJs) {
                    translateStr = translateStrInJs(str);
                } else {
//                    translateStr = translateStrInHtml(str);
                    translateStr = translateStrInHtmlV2(str);
                }
                fw.write(translateStr + "\n");
            }
            addI18ns(needInsert, local);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatUrl(String prefixUrl) {
        if (!prefixUrl.startsWith("/")) {
            prefixUrl = "/" + prefixUrl;
        }
        if (prefixUrl.endsWith("/")) {
            prefixUrl = prefixUrl.substring(0, prefixUrl.length() - 1);
        }
        return prefixUrl;
    }

    @Test
    public void addCommonOperateTest() {
        String menuCode = "REPORT";
        String jspPath = "D:/git/erp/erp-web/src/main/webapp/WEB-INF/templates/default/share/left/report.jsp";
        String menuJSP = FileUtil.readTxtFile2StrByStringBuilder(jspPath);
        Document doc = Jsoup.parse(menuJSP);
        Elements as = doc.select("a");
        Map<String, String> m = new HashMap<>();
        for (Element a : as) {
            String href = a.attr("href");
            if(!StringUtil.isBlank(href) && href.contains("/")) {
                String html = a.html();
                if(html.contains("<spring:message")) {
                    html = html.substring(html.indexOf("<spring:message"));
                    html = html.substring(html.indexOf("code=\"") + "code=\"".length());
                    String code = html.substring(0, html.indexOf("\""));
                    m.put(href, code);
                }
            }
        }
        String packag = "D:/git/erp/erp-web/src/main/java/com/sjdf/erp/web/controller/report";
        List<File> filles = FileUtil.getFilesBySuffix(packag, "java");
        for (File file : filles) {
            String copuJspPath = file.getAbsolutePath() + "Copy";
            File copyFile = new File(copuJspPath);
            try (
                FileReader fis = new FileReader(file);
                BufferedReader br = new BufferedReader(fis);
                FileWriter fw = new FileWriter(copyFile);) {
                String str = null;
                String prefixUrl = null;
                String lastCode = null;
                int i = 0;
                while ((str = br.readLine()) != null) {
                    if(str.contains("@RequestMapping")) {
                        fw.write(str + "\n");
                        if(prefixUrl == null) {
                            //
                            prefixUrl = getReMa(str);
                        } else {
                            String suffixUrl = getReMa(str);
                            String url = prefixUrl + suffixUrl;
                            String code = m.get(url);
                            if(code == null) {
                                continue;
                            }
                            i = 0;
                            lastCode = code;
                            fw.write("    @CommonOperate(value=\"" + code + "\", menu=MenuCode." + menuCode + ")" + "\n");
                        }
                    } else if(str.contains("@PermissionCode(") && i < 2) {
                        String name = str.substring(str.indexOf("name") + 4).trim();
                        name = name.substring(name.indexOf("\"") + 1).trim();
                        name = name.substring(0, name.indexOf("\""));
                        if(null != lastCode) {
                            str = str.replace(name, lastCode);
                        }
                        fw.write(str + "\n");
                    } else {
                        fw.write(str + "\n");
                    }
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private String getReMa(String str) {
        String url= str.substring(str.indexOf("\"") + 1).trim();
        url = url.substring(0, url.indexOf("\""));
        url = formatUrl(url);
        return url;
    }

    @Test
    public void getReMaTest() {
        System.out.println(getReMa("    @RequestMapping(value = \"/updAddressStatusOrDel\", method = RequestMethod.POST)"));
    }

    private String translateStrInHtmlV2(String str) {
        if (str.trim().startsWith("<%--") || str.trim().startsWith("<!--")) {
            return str;
        }
        String key = "";
        List<String> zhList = new ArrayList<>();
        boolean start = false;
        boolean existCh = false;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) == '<') {
                start = true;
                key = key.trim();
                if(!key.equals("") && existCh){
                    if(key.endsWith("：") || key.endsWith(":")) {
                        //如果key以：或:结尾，key去除
                        int index = key.lastIndexOf("：");
                        key = key.substring(0, index != -1 ? index : key.lastIndexOf(":"));
                    }
                    //去除首尾数字
                    key = key.replaceAll("^([0-9]+)?(.*[^0-9])([0-9]+)?$", "$2");
                    zhList.add(key);
                }
                key = "";
                existCh = false;
            }
            if (isChineseByREG(str.charAt(i) + "")) {
                existCh = true;
            }
            if(!start) {
                key += str.charAt(i);
            }
            if(str.charAt(i) == '>') {
                start = false;
            }
        }
        //以汉字结尾在上面的循环中不会加入到zhList中
        if (!key.equals("") && existCh) {
            zhList.add(key);
        }
        for (int i = 0; i < zhList.size(); i++) {
            String zh = zhList.get(i);
            List<SysI18n> i18ns = getI18nsByZhText(local, zh);
            String code = "";
            if (i18ns == null || i18ns.isEmpty()) {
                String enText = getEnFromZh(zh);
                if(enText.endsWith("?") && !zh.endsWith("?")) {
                    enText = enText.substring(0, enText.lastIndexOf("?"));
                }
                code = "message." + enText.toLowerCase().replace(" ", ".");
                SysI18n enI18n = new SysI18n(code, enText, "en_US");
                SysI18n zhI18n = new SysI18n(code, zh, "zh_CN");
                needInsert.put(enI18n.getCode() + enI18n.getLanguage(), enI18n);
                needInsert.put(zhI18n.getCode() + zhI18n.getLanguage(), zhI18n);
            } else {
                boolean hasEn = false;
                for (SysI18n i18n : i18ns) {
                    code = i18n.getCode();
                    if (i18n.getLanguage().equals("en_US")) {
                        hasEn = true;
                    }
                    if (!hasEn) {
                        SysI18n enI18n = new SysI18n(code, getEnFromZh(zh), "en_US");
                        needInsert.put(enI18n.getCode() + enI18n.getLanguage(), enI18n);
                    }
                }
            }
            String s = "<spring:message code=\"" + code + "\"/>";
            str = str.replace(zh, s);
        }
        return str;
    }

    private String translateStrInHtml(String str) {
        if (str.trim().startsWith("<%--") || str.trim().startsWith("<!--")) {
            return str;
        }
        List<String> zhList = new ArrayList<>(); // 需要翻译的中文 eg:修改回邮地址
        String key = "";
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (isChineseByREG(ch + "")) {
                key += ch;
            } else if (!key.equals("")) {
                if (ch == '(' || ch == ')') {
                    key += ch;
                } else {
                    zhList.add(key);
                    key = "";
                }
            }
        }
        //以汉字结尾在上面的循环中不会加入到zhList中
        if (!key.equals("")) {
            zhList.add(key);
        }
        for (int i = 0; i < zhList.size(); i++) {
            String zh = zhList.get(i);
            List<SysI18n> i18ns = getI18nsByZhText(local, zh);
            String code = "";
            if (i18ns == null || i18ns.isEmpty()) {
                String enText = getEnFromZh(zh);
                code = "message." + enText.toLowerCase().replace(" ", ".");
                SysI18n enI18n = new SysI18n(code, enText, "en_US");
                SysI18n zhI18n = new SysI18n(code, zh, "zh_CN");
                needInsert.put(enI18n.getCode() + enI18n.getLanguage(), enI18n);
                needInsert.put(zhI18n.getCode() + zhI18n.getLanguage(), zhI18n);
            } else {
                boolean hasEn = false;
                for (SysI18n i18n : i18ns) {
                    code = i18n.getCode();
                    if (i18n.getLanguage().equals("en_US")) {
                        hasEn = true;
                    }
                    if (!hasEn) {
                        SysI18n enI18n = new SysI18n(code, getEnFromZh(zh), "en_US");
                        needInsert.put(enI18n.getCode() + enI18n.getLanguage(), enI18n);
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
    public void translateStrTest() {
        String str = " $(\".freightHeavyLi:last\").after('<div class=\"t-dash-bor clearfix lh30 pt10 pb10 freightHeavyLi\"><div class=\"fl ml10\"><b class=\"fl f13\">' + '重量范围' + '：</b><input type=\"text\" class=\"h28-text blueFocus w60\" value=\"0\" name=\"minWeight\"> - <input type=\"text\" class=\"h28-text blueFocus w60\" value=\"0\" name=\"maxWeight\" ' + ($(\"#limitWeight\").is(\":checked\") ? \"disabled='disabled'\" : '') +'> g</div><div class=\"fl ml30\"><b class=\"fl f13\">续重单位重量：</b><input type=\"text\" class=\"h28-text blueFocus w60\" value=\"0\" name=\"unitWeight\"> g </div><div class=\"fl ml30\"><b class=\"fl f13\">续重单价：</b><input type=\"text\" class=\"h28-text blueFocus w60\" value=\"0\" name=\"unitPrice\"> 元 </div><a class=\"c-link1 fr f12\"  onclick=\"delGroupFee(this)\"><i class=\"icon iconfont erp-del mr3 f14\"></i>删除</a></div>');";
        System.out.println(translateStrInJs(str));
//        String str = "                        <input type=\"text\" class=\"h28-text blueFocus w150\" value=\"0\" name=\"firstWeightPrice\"> 元";
//        System.out.println(translateStrInHtml(str));
//        System.out.println(isChineseByREG("元"));
    }

    /**
     * 替换str中的中文信息
     * 不支持替换js中没有用''或""分割开的字符，在js拼接html会出现这种情况  eg:'</div><div class=\"fl ml30\"><b class=\"fl f13\">续重单位重量：</b><input'
     * 为避免这种情况则需要用''或""分隔开即可 eg:'</div><div class=\"fl ml30\"><b class=\"fl f13\">' + '续重单位重量' + '：</b><input'
     * @param str
     * @return
     */
    public String translateStrInJs(String str) {
        List<String> zhList = new ArrayList<>(); // 需要翻译的中文 eg:修改回邮地址
        List<String> keyList = new ArrayList<>(); // 需要翻译的中文带有''或者"" eg:'修改回邮地址'
        String key = "";
        char keyStartMark = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\'' || ch == '"') {
                keyStartMark = ch;
            }
            if (isChineseByREG(ch + "")) {
                key += ch;
            } else if (!key.equals("")) {
                if (ch == keyStartMark) { // 以'或"开头且结尾中间的字符有中文的为text,
                                          // bug:如果有转义会出错
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
            if (i18ns == null || i18ns.isEmpty()) {
                String enText = getEnFromZh(zh);
                code = "message." + enText.toLowerCase().replace(" ", ".");
                SysI18n enI18n = new SysI18n(code, enText, "en_US");
                SysI18n zhI18n = new SysI18n(code, zh, "zh_CN");
                needInsert.put(enI18n.getCode() + enI18n.getLanguage(), enI18n);
                needInsert.put(zhI18n.getCode() + zhI18n.getLanguage(), zhI18n);
            } else {
                boolean hasEn = false;
                for (SysI18n i18n : i18ns) {
                    code = i18n.getCode();
                    if (i18n.getLanguage().equals("en_US")) {
                        hasEn = true;
                    }
                    if (!hasEn) {
                        SysI18n enI18n = new SysI18n(code, getEnFromZh(zh), "en_US");
                        needInsert.put(enI18n.getCode() + enI18n.getLanguage(), enI18n);
                    }
                }
            }
            String s = "getI18nMsg('" + code + "')";
            if (str.contains("jSuccess") || str.contains("jFail") || str.contains("jWarn") || str.contains("jConfirm")
                || str.contains("tips") || str.contains("failTips") || str.contains("jSuccMsg")
                || str.contains("jFailMsg") || str.contains("jLoadMsg")) {
                s = "'" + code + "'";
            }
            str = str.replace(keyList.get(i), s);
        }
        return str;
    }

    /**
     * 根据中文text获取国际化信息
     * @param c
     * @param zhText
     * @return
     */
    public List<SysI18n> getI18nsByZhText(ConInfo c, String zhText) {
        Connection con = null;
        List<SysI18n> i18ns = new ArrayList<>();
        String sql = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://" + c.getHost()
                    + ":3306/erp?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8",
                c.getUsername(), c.getPassword());
            sql = "select * from `sys_i18n` where `text` = ? ";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, zhText);
            ResultSet rs = pstmt.executeQuery();
            String code = "";
            while (rs.next()) {
                code = rs.getString(2);
                break;
            }
            sql = "select * from `sys_i18n` where `code` = ? ";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, code);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                SysI18n SysI18n = new SysI18n(rs.getString(2), rs.getString(3), rs.getString(4));
                i18ns.add(SysI18n);
            }
        } catch (Exception e) {
            System.out.println(sql);
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

    @Test
    public void getEnFromZhTest() {
        System.out.println(getEnFromZh("修改回邮地址"));
    }

    private String getEnFromZh(String query) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        // {"from":"zh","to":"en","trans_result":[{"src":"\u9ad8\u5ea6600\u7c73","dst":"Height
        // 600 meters"}]}
        String res = api.getTransResult(query, "zh", "en");
        return JSON.parseObject(res).getJSONArray("trans_result").getJSONObject(0).getString("dst");
    }

    class ConInfo {

        String host;
        String username;
        String password;

        public ConInfo(String host, String username, String password) {
            super();
            this.host = host;
            this.username = username;
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 插入到test库i18n表
     * @param i18ns
     * @param conInfo
     */
    private void addI18ns(Map<String, SysI18n> i18ns, ConInfo conInfo) {
        Connection con = null;
        SysI18n i18 = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://" + conInfo.getHost()
                    + ":3306/test?allowMultiQueries=true&amp;useUnicode=true&amp;characterEncoding=UTF-8",
                conInfo.getUsername(), conInfo.getPassword());
            String sql = "truncate sys_i18n";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt = con.prepareStatement(sql);
            stmt.execute();
            sql = "insert into sys_i18n(code,text,language) values (?,?,?)";
            stmt = con.prepareStatement(sql);
            for (Map.Entry<String, SysI18n> i18nEntry : i18ns.entrySet()) {
                i18 = i18nEntry.getValue();
                stmt.setString(1, i18.getCode());
                stmt.setString(2, i18.getText());
                stmt.setString(3, i18.getLanguage());
                stmt.execute();
            }
        } catch (Exception e) {
            logger.error("i18n=" + i18.toString() + "_" + e.getMessage(), e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
