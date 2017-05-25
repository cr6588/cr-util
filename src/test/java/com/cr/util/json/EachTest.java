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
import com.cr.util.StringUtil;

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
//        List<ExistFieldValue> fields = new ArrayList<>();
//        fields.add(new ExistFieldValue(1, 1, 1, new ExistField(1, 1, 1, "merchantId", "所属商家Id", 1, null, false), "MN006334"));
//        fields.add(new ExistFieldValue(1, 1, 1, new ExistField(2, 1, 1, "location", "处理点 如不填则使用商家默认", 2, "api", false), "GZ"));
//        fields.add(new ExistFieldValue(1, 1, 1, new ExistField(3, 1, 1, "ServiceCode", "发货服务代码", 2, "api", false), "CUE"));

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

        FieldVo vo = new FieldVo("SMT001", shipToAddress, shipFromAddress, 11, 11.1, 22.2, 33.3, skus, "{\"merchantId\":\"MN006334\", \"Location\":\"GZ\",\"ServiceCode\":\"ServiceCode\",\"LabelData\":[{\"key\":\"AreaName\", \"value\":\"A001\"}, {\"key\":\"AreaName\", \"value\":\"A001\"}]}", "remark");
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
        List<FieldMappingJSON> mappings = getMapping();
        JSONObject curJson = parseObject(mappings, vo, StringUtil.isBlank(vo.getOther()) ? null : JSONObject.parseObject(vo.getOther()));
        return curJson.toJSONString();
    }

    private <T> JSONArray parseArray(List<FieldMappingJSON> childs, List<T> list) {
        JSONArray array = new JSONArray();
        for (Object o : list) {
            JSONObject curJson = parseObject(childs, o, o instanceof JSONObject ? (JSONObject) o : null);
            array.add(curJson);
        }
        return array;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private JSONObject parseObject(List<FieldMappingJSON> childs, Object o, JSONObject other) {
        JSONObject curJson = new JSONObject();
        for (FieldMappingJSON mapping : childs) {
            switch (mapping.getType()) {
                case "field":
                    //ReflectUtil.getFieldValue(o, mapping.getAttribue())为null时，不会put进去
                    Object fieldValue = null;
                    if(mapping.isOther()) {
                        fieldValue = other != null ? other.get(mapping.getAttribue()) : null;
                    } else {
                        fieldValue = ReflectUtil.getFieldValue(o, mapping.getAttribue());
                    }
                    curJson.put(mapping.getKey(), fieldValue);
                    break;
                case "object":
                    Object objectValue = null;
                    //fieldVo已有数据需要映射在一个额外的对象中,eg：出口易的包裹信息都映射在package中，即package:{fieldVo中大部分信息}，此时直接将fieldVo传入
                    if(mapping.getAttribue() != null) {
                        objectValue = ReflectUtil.getFieldValue(o, mapping.getAttribue());
                    } else {
                        objectValue = o;
                    }
                    curJson.put(mapping.getKey(), parseObject(mapping.getChilds(), objectValue, other));
                    break;
                case "array":
                    Object arrayValue = null;
                    if(mapping.isOther()) {
                        arrayValue = other != null ?  other.getJSONArray(mapping.getAttribue()) : null;
                    } else {
                        arrayValue = ReflectUtil.getFieldValue(o, mapping.getAttribue());
                    }
                    curJson.put(mapping.getKey(), parseArray(mapping.getChilds(), (List)arrayValue));
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
        List<FieldMappingJSON> mapps = new ArrayList<>();

        List<FieldMappingJSON> packageChilds = new ArrayList<>();
        //attribue为null时取值本身，eg:出口易的fieldVo里大部分信息都装在package中，而fieldVo没有package字段，所以形成json的value时以filedVo传过去
        FieldMappingJSON Package = new FieldMappingJSON(1, null, "Package", 1, null, "object", null);
        FieldMappingJSON m1 = new FieldMappingJSON(1, "weight", "weight", 2, null, "field", null);
        FieldMappingJSON m2 = new FieldMappingJSON(2, "length", "length", 2, null, "field", null);
        FieldMappingJSON m3 = new FieldMappingJSON(3, "width", "width", 2, null, "field", null);
        List<FieldMappingJSON> skus = new ArrayList<>();
        FieldMappingJSON m4 = new FieldMappingJSON(6, "skus", "skus", 2, null, "array", skus);
        skus.add(new FieldMappingJSON(4, "sku", "Sku", 3, m4, "field", null));
        skus.add(new FieldMappingJSON(4, "quantity", "quantity", 3, m4, "field", null));
        skus.add(new FieldMappingJSON(5, "weight", "weight", 3, m4, "field", null));
        skus.add(new FieldMappingJSON(4, "declareValue", "declareValue", 3, m4, "field", null));
        skus.add(new FieldMappingJSON(5, "declareNameEn", "declareNameEn", 3, m4, "field", null));
        packageChilds.add(m1);
        packageChilds.add(m2);
        packageChilds.add(m3);
        packageChilds.add(m4);
        packageChilds.add(new FieldMappingJSON(1, "packageId", "packageId", 2, null, "field", null));
        packageChilds.add(new FieldMappingJSON(1, "height", "height", 2, null, "field", null));

        List<FieldMappingJSON> shipAddress = new ArrayList<>();
        FieldMappingJSON shipToAddress = new FieldMappingJSON(6, "shipToAddress", "ShipToAddress", 2, null, "object", shipAddress);
        addField(shipToAddress, shipAddress);
        packageChilds.add(shipToAddress);

//        List<FieldMappingJSON> shipFromAddressList = new ArrayList<>();
//        FieldMappingJSON shipFromAddress = new FieldMappingJSON(6, "shipFromAddress", "ShipFromAddress", 1, null, "object", shipFromAddressList);
//        addField(shipFromAddress, shipFromAddressList);
//        list.add(shipFromAddress);

        

        packageChilds.add(new FieldMappingJSON(1, "packageId", "packageId", 2, null, "field", null));
        packageChilds.add(new FieldMappingJSON(1, "ServiceCode", "ServiceCode", 2, null, "field", true, null));
        Package.setChilds(packageChilds);
        mapps.add(Package);
        mapps.add(new FieldMappingJSON(1, "remark", "Remark", 1, null, "field", null));
        //特殊项
        mapps.add(new FieldMappingJSON(1, "merchantId", "MerchantId", 1, null, "field", true, null));
        mapps.add(new FieldMappingJSON(1, "Location", "Location", 1, null, "field", true, null));
        List<FieldMappingJSON> items = new ArrayList<>();
        items.add(new FieldMappingJSON(1, "key", "Key", 4, null, "field", true, null));
        items.add(new FieldMappingJSON(1, "value", "value", 4, null, "field", true, null));

        List<FieldMappingJSON> LabelDataChilds = new ArrayList<>();
        LabelDataChilds.add(new FieldMappingJSON(1, "LabelData", "Items", 3, null, "array", true, items));
        mapps.add(new FieldMappingJSON(1, null, "LabelData", 2, null, "object", true, LabelDataChilds));
        return mapps;
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
