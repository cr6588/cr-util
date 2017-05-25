package com.cr.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create in 2017年05月06日
 * @category @auther chenyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExistField {
    private long id;
    private long logId;
    private long chanelId;
    private String key;
    private String name; // 显示名称
    private int type; // 类型 input,select,datetime...
    private String options; // 可选值 [{key:显示名称},{key:显示名称}] 如果数据来源必选是物流公司需要额外在考虑
    private boolean required = true; // 是否必填
    private String ObjectType; // 对象类型field, Object, list

    public ExistField(long id, long logId, long chanelId, String key, String name, int type, String options, boolean required) {
        super();
        this.id = id;
        this.logId = logId;
        this.chanelId = chanelId;
        this.key = key;
        this.name = name;
        this.type = type;
        this.options = options;
        this.required = required;
    }

}
