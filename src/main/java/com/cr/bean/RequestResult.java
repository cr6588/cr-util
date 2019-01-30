package com.cr.bean;

import java.io.Serializable;

/**
 * 处理结果返回类
 * @author Mx
 */
public class RequestResult<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4165589096808957834L;
    public static final int SUCC_CODE = 0;
    public static final int ERR_CODE = 100;

    /**
     * 处理结果编号0代表成功，其他代表失败
     */
    int code = SUCC_CODE;
    /**
     * 如果出错的信息
     */
    String msg;
    /**
     * msg含有{0}，{1}...需要texts替换，用于国际化替代
     */
    String[] texts;
    /**
     * 其他附加信息
     */
    T body;

    public RequestResult() {

    }

    public RequestResult(T t) {
        this.body = t;
    }

    public RequestResult(int code, String msg, T body) {
        this.code = code;
        this.msg = msg;
        this.body = body;
    }

    public RequestResult(int code, String msg, T body,  String... texts) {
        this.code = code;
        this.msg = msg;
        this.body = body;
        this.texts = texts;
    }

    public static <T> RequestResult<T> createErr(String msg) {
        return new RequestResult<>(ERR_CODE, msg, null);
    }

    public static <T> RequestResult<T> createErr(String msg, T body) {
        return new RequestResult<>(ERR_CODE, msg, body);
    }

    public static <T> RequestResult<T> createByRequestResult(RequestResult<?> rr) {
        return new RequestResult<T>(rr.getCode(), rr.getMsg(), null, rr.getTexts());
    }

    public static <T> RequestResult<T> createSucc() {
        return new RequestResult<>(SUCC_CODE, null, null);
    }

    public static <T> RequestResult<T> createSucc(String msg) {
        return new RequestResult<>(SUCC_CODE, msg, null);
    }

    public static <T> RequestResult<T> createSucc(String msg, T body) {
        return new RequestResult<>(SUCC_CODE, msg, body);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public String[] getTexts() {
        return texts;
    }

    public void setTexts(String[] texts) {
        this.texts = texts;
    }

    public boolean isErr() {
        return code != SUCC_CODE;
    }

    public boolean isSucc() {
        return code == SUCC_CODE;
    }

    public void setErrMsg(String msg) {
        this.code = ERR_CODE;
        this.msg = msg;
    }

    /**
     * 设置系统错误
     */
    public void setSysErrMsg() {
        setErrMsg("message.system.error");
    }

    public static <T> RequestResult<T> createSysErr() {
        return new RequestResult<>(ERR_CODE, "message.system.error", null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("}");
        sb.append("code:").append(code).append(",");
        if (msg != null) {
            sb.append("msg:").append(msg).append(",");
        }
        if (texts != null) {
            sb.append("texts:").append(texts.toString()).append(",");
        }
        if (body != null) {
            sb.append("body:").append(body.toString());
        }
        sb.append("}");
        return sb.toString();
    }
}
