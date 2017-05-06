package com.cr.util.json;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cr.bean.ExistField;
import com.cr.bean.ExistFieldValue;
import com.cr.bean.FieldMappingJSON;
import com.cr.bean.FieldVo;
import com.cr.bean.ShipAddress;
import com.cr.bean.Sku;
import com.cr.util.ReflectUtil;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 * create in 2017年05月06日
 * @category TODO
 * @auther chenyi
 */
public class EachTest {

    @Test
    public void test() {
        List<Sku> skus = new ArrayList<>();
        skus.add(new Sku(1, 11, 11.2, "phone"));
        skus.add(new Sku(2, 11, 11.2, "pen"));
        ShipAddress shipToAddress = new ShipAddress("US", "NEW WORK", "NEW YOURK", "SSS", "5245-19", "RUTER", "54654852");
        ShipAddress shipFromAddress = new ShipAddress("US", "NEW WORK", "NEW YOURK", "SSS", "5245-19", "RUTER", "54654852");
        List<ExistFieldValue> fields = new ArrayList<>();
        fields.add(new ExistFieldValue(1, 1, 1, new ExistField(1, 1, 1, "merchantId", "所属商家Id", 1, null, false), "MN006334"));
        fields.add(new ExistFieldValue(1, 1, 1, new ExistField(2, 1, 1, "location", "处理点 如不填则使用商家默认", 2, "api", false), "GZ"));
        fields.add(new ExistFieldValue(1, 1, 1, new ExistField(3, 1, 1, "ServiceCode", "发货服务代码", 2, "api", false), "CUE"));

//        ExistField label1 = new ExistField(3, 1, 1, "key", "key", 1, null, false);
//        ExistField label2 = new ExistField(3, 1, 1, "value", "value", 1, null, false);
//        ExistField labels = new ExistField(3, 1, 1, "labels", "标签数据列表", 2, null, false);
//        label1.setParent(labels);
//        label2.setParent(labels);
//        List<ExistField> labelList = new ArrayList<>();
//        labelList.add(label1);
//        labelList.add(label2);
//        labels.setChilds(labelList);
//        labels.setObjectType("list");

        
//        fields.add(new ExistFieldValue(1, 1, 1, labels, ));

        FieldVo vo = new FieldVo("SMT001", shipToAddress, shipFromAddress, 11, 11.1, 22.2, 33.3, skus, fields, "remark");
        String param  = getParam(vo);
        System.out.println(param);
    }

//    @Test
//    public void fileldVoOgnlTest() throws OgnlException {
//        List<Sku> skus = new ArrayList<>();
//        skus.add(new Sku(1, 11, 11, "phone"));
//        skus.add(new Sku(1, 11, 11, "pen"));
//        FieldVo vo = new FieldVo(20, 11, 11, 11, skus, null);
//        OgnlContext context = new OgnlContext();
//        context.put("vo", vo);
//        context.setRoot(vo);
//        Object o = Ognl.getValue(Ognl.parseExpression("weight"), context, context.getRoot());
//        System.out.println(o);
//        o = Ognl.getValue(Ognl.parseExpression("skus"), context, context.getRoot());
//        for (Sku sku : (List<Sku>)o) {
//            System.out.println(sku.getName());
//            System.out.println(sku.getWeight());
//        }
//    }

    public static Object getOgnlValue(String key, OgnlContext context) {
        try {
            return Ognl.getValue(Ognl.parseExpression(key), context, context.getRoot());
        } catch (OgnlException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getParam(FieldVo vo) {
        OgnlContext context = new OgnlContext();
        context.put("vo", vo);
        context.setRoot(vo);
        List<FieldMappingJSON> mappings = getMapping();
        JSONObject curJson = parseObject(mappings, vo);
        return curJson.toJSONString();
    }

    private <T> JSONArray parseArray(List<FieldMappingJSON> childs, List<T> list) {
        JSONArray array = new JSONArray();
        for (Object o : list) {
            JSONObject curJson = parseObject(childs, o);
            array.add(curJson);
        }
        return array;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private JSONObject parseObject(List<FieldMappingJSON> childs, Object o) {
        JSONObject curJson = new JSONObject();
        for (FieldMappingJSON mapping : childs) {
            switch (mapping.getType()) {
                case "field":
                    curJson.put(mapping.getKey(), ReflectUtil.getFieldValue(o, mapping.getAttribue()));
                    break;
                case "object":
                    curJson.put(mapping.getKey(), parseObject(mapping.getChilds(), ReflectUtil.getFieldValue(o, mapping.getAttribue())));
                    break;
                case "array":
                    curJson.put(mapping.getKey(), parseArray(mapping.getChilds(), (List)ReflectUtil.getFieldValue(o, mapping.getAttribue())));
                    break;
                default:
                    break;
            }
        }
        return curJson;
    }

    /**
     * 获取带有层级的数据映射
     * @return
     */
    public List<FieldMappingJSON> getMapping() {
        List<FieldMappingJSON> list = new ArrayList<>();
        FieldMappingJSON m1 = new FieldMappingJSON(1, "weight", "weight", 1, null, "field", null);
        FieldMappingJSON m2 = new FieldMappingJSON(2, "length", "length", 1, null, "field", null);
        FieldMappingJSON m3 = new FieldMappingJSON(3, "width", "width", 1, null, "field", null);
        List<FieldMappingJSON> skus = new ArrayList<>();
        FieldMappingJSON m4 = new FieldMappingJSON(6, "skus", "skus", 1, null, "array", skus);
        skus.add(new FieldMappingJSON(4, "quantity", "quantity", 2, m4, "field", null));
        skus.add(new FieldMappingJSON(5, "weight", "weight", 2, m4, "field", null));
        skus.add(new FieldMappingJSON(4, "declareValue", "declareValue", 2, m4, "field", null));
        skus.add(new FieldMappingJSON(5, "declareNameEn", "declareNameEn", 2, m4, "field", null));
        list.add(m1);
        list.add(m2);
        list.add(m3);
        list.add(m4);
        list.add(new FieldMappingJSON(1, "packageId", "packageId", 1, null, "field", null));

        List<FieldMappingJSON> shipAddress = new ArrayList<>();
        FieldMappingJSON shipToAddress = new FieldMappingJSON(6, "shipToAddress", "ShipToAddress", 1, null, "array", shipAddress);
        addField(shipToAddress, shipAddress);
        list.add(shipToAddress);

        List<FieldMappingJSON> shipFromAddressList = new ArrayList<>();
        FieldMappingJSON shipFromAddress = new FieldMappingJSON(6, "shipFromAddress", "ShipFromAddress", 1, null, "array", shipFromAddressList);
        addField(shipFromAddress, shipFromAddressList);
        list.add(shipFromAddress);

        list.add(new FieldMappingJSON(1, "packageId", "packageId", 1, null, "field", null));
        return list;
    }

    public void addField(FieldMappingJSON m4, List<FieldMappingJSON> shipAddress) {
        shipAddress.add(new FieldMappingJSON(4, "country", "Country", 2, m4, "field", null));
        shipAddress.add(new FieldMappingJSON(4, "province", "Province", 2, m4, "field", null));
        shipAddress.add(new FieldMappingJSON(4, "city", "City", 2, m4, "field", null));
        shipAddress.add(new FieldMappingJSON(4, "street1", "Street1", 2, m4, "field", null));
        shipAddress.add(new FieldMappingJSON(4, "street2", "Street2", 2, m4, "field", null));
        shipAddress.add(new FieldMappingJSON(4, "postcode", "Postcode", 2, m4, "field", null));
        shipAddress.add(new FieldMappingJSON(4, "contact", "Contact", 2, m4, "field", null));
        shipAddress.add(new FieldMappingJSON(4, "phone", "Phone", 2, m4, "field", null));
        shipAddress.add(new FieldMappingJSON(4, "email", "Email", 2, m4, "field", null));
    }

}
