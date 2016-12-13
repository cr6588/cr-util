package com.cdzy.cr.util;

public class StringUtil {

    /**
     * 字符串是null或这空字符串时返回true,其余返回false
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        if (str.trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 得到唯一的数字，如果有多个数字则返回null
     * @param str
     * @return
     */
    public static String getOnlyNumber(String str) {
        String number = null;
        int charNo = -1;
        if (null != str) {
            for (int i = 0; i < str.length(); i++) {
                int ascii = (int) str.charAt(i);
                if (ascii >= 48 && ascii <= 57) {
                    number = null == number ? str.charAt(i) + "" : number + str.charAt(i);
                    if (charNo != -1 && charNo != i - 1) {
                        return null;
                    }
                    charNo = i;
                }
            }
        }
        return number;
    }

    public static String getFirstNumFromStr(String str) {
        StringBuffer numStr = null;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                if (numStr == null) {
                    numStr = new StringBuffer();
                    numStr.append(c);
                } else {
                    if (numStr.toString().equals("0") && c != '0') {
                        numStr = new StringBuffer();
                        numStr.append(c);
                    } else if (numStr.toString().equals("0") && c == '0') {
                        continue;
                    } else {
                        numStr.append(c);
                    }
                }
            } else if (numStr != null) {
                break;
            }
        }
        return numStr == null ? null : new String(numStr);
    }
}
