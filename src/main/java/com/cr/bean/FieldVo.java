package com.cr.bean;

import java.util.List;

import com.cr.annotation.required;

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
public class FieldVo {
    @required
    String packageId; // 包裹Id(第三方系统自定义Id，客户+包裹Id 具有唯一性)
    @required
    ShipAddress shipToAddress; // 收货地址
    @required
    ShipAddress shipFromAddress; // 发货地址
    @required
    int weight;
    double length;
    double width;
    double height;
    @required
    List<Sku> skus;
    double sellPrice; // 售价
    String sellPriceCurrency; // 售价货币
    String salesPlatform; // 销售平台
    String trackingNumber; // 跟踪号
    List<ExistFieldValue> existFieldsValues; // 额外属性值
    String remark; // 备注

    public FieldVo(String packageId, ShipAddress shipToAddress, ShipAddress shipFromAddress, int weight, double length, double width, double height, List<Sku> skus,
        List<ExistFieldValue> existFieldsValues, String remark) {
        super();
        this.packageId = packageId;
        this.shipToAddress = shipToAddress;
        this.shipFromAddress = shipFromAddress;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.skus = skus;
        this.existFieldsValues = existFieldsValues;
        this.remark = remark;
    }

}
