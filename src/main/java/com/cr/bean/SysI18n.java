package com.cr.bean;

import java.io.Serializable;

/**
 * create in 2017年04月18日
 * @category 国际化类
 * @author chenyi
 */
public class SysI18n implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4619131320397192701L;
    /**
     * 国际化信息识别码
     */
    private String code;
    /**
     * 国际化信息
     */
    private String text;
    /**
     * 系统语言
     */
    private String language;

    public SysI18n(String code, String text, String language) {
        super();
        this.code = code;
        this.text = text;
        this.language = language;
    }

    public SysI18n() {
        super();
    }

    @Override
    public String toString() {
        return super.toString() + "SysI18n [code=" + code + ", text=" + text + ", language=" + language + "]";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
