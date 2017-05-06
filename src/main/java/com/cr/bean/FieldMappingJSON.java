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
    FieldMappingJSON parent;
    // 类型 object,array,field
    String type;
    List<FieldMappingJSON> childs;
}
