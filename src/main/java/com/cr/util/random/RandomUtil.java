package com.cr.util.random;

import java.util.Random;

public class RandomUtil {
    public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String numberChar = "0123456789";
    public static Random random = new Random();
    /**
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String getStr(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(letterChar.length())));
        }
        return sb.toString();
    }

    /**
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String getNumStr(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        return sb.toString();
    }

    /**
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String getChineseStr(int length) {
        String zhUnicodeStart = "4e00";
        String zhUnicodeEnd = "9fa5";
        int count = Integer.parseInt(zhUnicodeEnd, 16) - Integer.parseInt(zhUnicodeStart, 16) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(Integer.toHexString(random.nextInt(count) + Integer.parseInt(zhUnicodeStart, 16)));
        }
        return sb.toString();
    }

    /**
     * 获取长度随机在1（包含）到maxLenth（包含）的中文字符
     * @param maxLenth
     * @return
     */
    public static String getMaxLenthChineseStr(int maxLenth) {
        int length = random.nextInt(maxLenth) + 1;
        String zhUnicodeStart = "4e00";
        String zhUnicodeEnd = "9fa5";
        int count = Integer.parseInt(zhUnicodeEnd, 16) - Integer.parseInt(zhUnicodeStart, 16) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char)(random.nextInt(count) + Integer.parseInt(zhUnicodeStart, 16)));
        }
        return sb.toString();
    }

    public static String getRandomStrByStr(String str) {
        return String.valueOf(str.charAt(random.nextInt(str.length())));
    }
}
