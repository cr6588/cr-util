package com.cr.bean;

import com.cr.annotation.required;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create in 2017年05月06日
 * @category TODO
 * @author chenyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sku {
    String sku;// 商家SKU
    int quantity; // 数量
    int weight; // 单件重量(g)
    double declareValue; // 单件申报价值
    @required
    String declareNameEn; // 英文申报名称
    String declareNameCn; // 中文申报名称
    String productName; // 商品名称
    String price; // 商品单价

    public Sku(int quantity, int weight, double declareValue, String declareNameEn) {
        super();
        this.quantity = quantity;
        this.weight = weight;
        this.declareValue = declareValue;
        this.declareNameEn = declareNameEn;
    }

}
