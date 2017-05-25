package com.cr.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create in 2017年05月06日
 * @category TODO
 * @auther chenyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExistFieldValue {

    private long id;
    private long logId;
    private long chanelId;
    private ExistField field;
    private Object value;
//    private ExistField parent;
//    private List<ExistField> childs;
}
