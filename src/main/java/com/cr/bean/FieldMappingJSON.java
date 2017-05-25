package com.cr.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create in 2017年05月06日
 * @category 属性映射json
 * @auther chenyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldMappingJSON {
    long id;
    // 属性 eg:weight, sku.weight根据
    String attribue;
    // json key
    String key;
    // 层级
    int level;
    //父级
    FieldMappingJSON parent;
    // 类型 object,array,field
    String type;
    //是否是fieldVo中other的映射关系
    boolean isOther = false;
    //子集
    List<FieldMappingJSON> childs;
    public FieldMappingJSON(long id, String attribue, String key, int level, FieldMappingJSON parent, String type, List<FieldMappingJSON> childs) {
        super();
        this.id = id;
        this.attribue = attribue;
        this.key = key;
        this.level = level;
        this.parent = parent;
        this.type = type;
        this.childs = childs;
    }
}
