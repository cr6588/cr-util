package com.cr.bean;

import com.cr.annotation.required;

/**
 * create in 2017年05月06日
 * @category 地址，联系人
 * @auther chenyi
 */
public class ShipAddress {
    @required
    String country;
    @required
    String province;
    @required
    String city;
    @required
    String street1;
    String street2;
    @required
    String postcode;
    @required
    String contact;
    @required
    String phone;
    String email;

    public ShipAddress(String country, String province, String city, String street1, String postcode, String contact, String phone) {
        super();
        this.country = country;
        this.province = province;
        this.city = city;
        this.street1 = street1;
        this.postcode = postcode;
        this.contact = contact;
        this.phone = phone;
    }

}
