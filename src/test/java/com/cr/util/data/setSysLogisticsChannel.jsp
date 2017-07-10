<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0">
<title>Insert title here</title>
<%@ include file="../../share/css.jsp" %>
<%@ include file="../../share/js.jsp" %>
</head>
<script type="text/javascript">
//编辑地址
function editAddressBtn(obj){
    var id = $(obj).find("span").text();
    var oldName;
    $.post("/logistics/basicSetting/getAddress", {id:id}, function (res) {
        if(res.code == 0) {
            var address = res.body;
            for(var key in address) {
                $(".editAddressBox [name='" + key + "']").val(address[key]);
            }
            oldName = address.name;
        }
    })
    layer.open({
        type: 1,
        area:['700px','auto'],
        title: '修改回邮地址',
        closeBtn: 1, 
        shadeClose: true, //开启遮罩关闭
        anim:0,
        btn:['确定','取消'],
        content: $(".editAddressBox"),
        yes: function(index, layero){
            var array = $("#editAddrsssForm").serializeArray();
            var data = {};  
            for (var i = 0; i < array.length; i++) {
                var $obj = $("#editAddrsssForm [name='" + array[i].name + "']");
                if(array[i].value.trim() == "") {
                    failTips($obj, $obj.is('input') ? "message.must.fill" : "message.must.select");
                    return;
                } else {
                    delErrCss($obj);
                    data[array[i].name] = array[i].value;
                    if( i == array.length - 1) {
                        $.postJSON(sjdf.sysParams.getHost() + "/logistics/basicSetting/saveOrUpdateAddress", data, function (res) {
                           if(res.code == 0) {
                               jSuccMsg("message.operate.succ", 500);
                               layer.close(index);
                               loadAddress($(obj).parents("ul"), data.id);
                               //更新地址下拉名称
                               if(oldName != data.name) {
                                   $(obj).parents("ul").prev().find(".selectAddress option[value='" + data.id + "']").text(data.name);
                               }
                           } else {
                               jFail(res.msg);
                           }
                        });
                    }
                }
            }
        }
    });
}
$(function(){
    var tabTitle = ".print-tag-tit li";
    var tabContent = ".print-tag-con";
    $(tabTitle + ":first").addClass("print-tag-active");
    $(tabContent).not(":first").hide();
    $(tabTitle).unbind("mousedown").bind("mousedown", function(){
        $(this).siblings("li").removeClass("print-tag-active").end().addClass("print-tag-active");
        var index = $(tabTitle).index( $(this) );
        $(tabContent).eq(index).siblings(tabContent).hide().end().fadeIn(0);
    });
    $("#thumbnail li a").mouseover(function(){
        $(".tag-large img").hide().attr({ "src": $(this).attr("href"), "title": $("> img", this).attr("title") });
        $("#thumbnail li.tag-current").removeClass("tag-current");
        $(this).parents("li").addClass("tag-current");
        return false;
    });
    $(".tag-large>img").load(function(){
        $(".tag-large>img:hidden").show();
    }); 
    $('.use-unselected-li li').click(function(){
        $(this).toggleClass("selected-function");
    })
    //添加地址
    $(".addAddress").click(function () {
        $(this).parent().next().html($(".addAddressUl").html());
    });
    //选择地址
    $(".selectAddress").change(function () {
        var $this = $(this);
        var id = $this.val();
        $this.val("");
        loadAddress($this.parent().parent().next(), id);
    });
    //标签类型切换
    $(":radio[name='labelType']").click(function () {
        var labelType = $(":radio[name='labelType']:checked").val();
        if(labelType == 1) {
            if(!$(".customSelectBox").hasClass("none")) {
                $(".customSelectBox").addClass("none")
            }
        } else {
            if($(".customSelectBox").hasClass("none")) {
                $(".customSelectBox").removeClass("none")
            }
        }
    });
    //保存
    $(".save").click(function () {
        var array = $("#channelForm").serializeArray();
        var data = {};  
        for (var i = 0; i < array.length; i++) {
            var $obj = $("#channelForm [name='" + array[i].name + "']");
                data[array[i].name] = array[i].value;
                if( i == array.length - 1) {
                    if(!data.declareNameCn) {
                        jFailMsg("申报品名(中)为空");
                        return;
                    }
                    if(!data.declareNameEn) {
                        jFailMsg("申报品名(英)为空");
                        return;
                    }
                    for(var j = 0; j < $(".addressUl").length; j++) {
                        var $addressUl = $(".addressUl").eq(j);
                        var $inputs = $addressUl.find("input, select");
                        var address = {};
                        address.type=$addressUl.attr("type");
                        if($inputs.length > 0) {
                            for(var k = 0; k < $inputs.length; k++) {
                                var $input = $inputs.eq(k);
                                if(!$input.val()) {
                                    jFailMsg($input.parent().prev().text() + "为空");
                                    return;
                                }
                                address[$input.attr("name")] = $input.val();
                            }
                            if(address.type == '1') {
                                data.returnPubAddress = address;
                            } else {
                                data.pickupPubAddress = address;
                            }
                        } else if($addressUl.find("span[name=\"id\"]").length > 0) {
                            address.id = $addressUl.find("span[name=\"id\"]").text();
                            if(address.type == '1') {
                                data.returnAddress = address.id;
                            } else {
                                data.pickupAddress = address.id;
                            }
                        } else {
                            jFailMsg("请选择地址");
                            return;
                        }
                    }
                    saveOrUpdateChannel(data);
                }
        }
    });
    /**
     * 获取平台认可物流数据
     */
    function getPlatAccept() {
        var platAccept = {};
        $(".platformLog").each(function () {
            if($(this).val()) {
                platAccept[$(this).attr("platformId")] = $(this).val();
            }
        });
        return platAccept;
    }
    function saveOrUpdateChannel(data) {
        data.id = "${channel.id}";
        data.autoDeliver = 1;
        data.logId = parent.pubLogId;
        data.sysChannelId = "${sysChannel.id}";
        data.name = "${channel.name == null ? sysChannel.name : channel.name }";
        data.sysLogId = "${sysChannel.logId}";
        data.type = 1;
        data.platAccept = JSON.stringify(getPlatAccept());
        //TODO 标签类型组装数据等待
        $.postJSON(sjdf.sysParams.getHost() + "/logistics/list/saveOrUpdateChannel", data, function (res) {
            if(res.code == 0) {
                jSuccMsg("message.operate.succ", 500);
                setTimeout(function () {
                    parent.document.getElementsByTagName("form")[0].submit();
                }, 500);
            } else {
                jFail(res.msg);
            }
         });
    }
    if('${channel.returnAddress}') {
        $(".selectAddress:eq(0)").val('${channel.returnAddress}').trigger("change");
    }
    if('${channel.pickupAddress}') {
        $(".selectAddress:eq(1)").val('${channel.pickupAddress}').trigger("change");
    }
    $(".cancel").click(function () {
        parent.layer.closeAll();
        if("${param.byEnable}") {
            parent.cancelChannel();
        }
    });
    //初始化平台认可物流
    var platAcceptJSON = '${platAcceptJSON}';
    if(platAcceptJSON) {
        platAcceptJSON = JSON.parse(platAcceptJSON);
        for(var platformId in platAcceptJSON) {
            $(".platformLog[platformId='" + platformId + "']").val(platAcceptJSON[platformId]);
        }
    }
});
/**
 * 加载地址
 */
function loadAddress($obj, id) {
    $.post("/logistics/basicSetting/getAddress", {id:id}, function (res) {
        if(res.code == 0) {
            var address = res.body;
            for(var key in address) {
                var text = address[key];
                if(key == 'country') {
                    text = $(".addAddressUl select option[value='" + text + "']").text();
                }
                $(".detailAddress span[name='" + key + "']").text(text);
            }
            $obj.html($(".detailAddress").html());
        }
    })
}
</script>
</head>

<body>
<p class="pt10 b-ql-bor f15 pl20 pb10"><spring:message code="message.set.logistics.channel"/></p>
<form action="/logistics/list/saveOrUpdateChannel" method="post" id="channelForm">
    <div class="p10-20 clearfix">
        <div style="height:540px; overflow-y:auto;">
            <div class="b-dash-bor pb5 clearfix mt5">
                <h5 class="f16 ml10"><spring:message code="message.basic.info"/></h5>
                <ul class="add-order-li mt10">
                    <li class="w50p">
                        <b class="fl tr w120 f13"><span class="c-red mr3">*</span><spring:message code="message.logistics.channel"/>：</b>
                        <div class="fl ml10 f13">${channel.name == null ? sysChannel.name : channel.name }</div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13"><spring:message code="message.auto.deliver"/>：</b>
                        <div class="fl ml10 f13"><spring:message code="message.support"/></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">运费规则：</b>
                        <div class="fl ml10 f13">
                            <select class="h30-select blueFocus w180 fl f12" name="feeRuleId">
                                <option value="">- 请选择 -</option>
                                <c:forEach var="feeRule" items="${feeRules }">
                                    <option value="${feeRule.id }" ${channel.feeRuleId == feeRule.id ? 'selected' : ''}>${feeRule.name }</option>
                                </c:forEach>
                            </select>
                            <a href="/logistics/basicSetting/feeRuleList" class="ml10 c-link1 fl f12" target="_blank"><i class="icon iconfont erp-ordinaryset mr3 f15 fl"></i>自定义运费规则</a>
                        </div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13"><span class="c-red mr3">*</span>折扣：</b>
                        <div class="fl ml10 f13">
                            <input type="text" class="h28-text blueFocus w200" value="${channel == null ? 1 : channel.discount }" name="discount">
                        </div>
                    </li>
                    <li class="w">
                        <b class="fl tr w120 f13">查询网址：</b>
                        <div class="fl ml10 f13">
                            <input type="text" class="h28-text blueFocus w350" value="${channel.selectUrl}" name="selectUrl">
                            <label class="ml20"><input type="checkbox" class="mr3" value="1" name="hideTrackNumber" ${channel.hideTrackNumber == 1 ? 'checked' : ''}>屏蔽跟踪号</label>
                        </div>
                    </li>
                </ul>
            </div>
            <div class="b-dash-bor pb5 clearfix">
                <h5 class="f16 ml10 mt10">申报信息</h5>
                <ul class="add-order-li mt10">
                    <li class="w50p">
                        <b class="fl tr w120 f13"><span class="c-red mr3">*</span>申报品名(中)：</b>
                        <div class="fl ml10 f13">
                            <input type="text" class="h28-text blueFocus w250" value="${channel.declareNameCn }" name="declareNameCn" >
                        </div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13"><span class="c-red mr3">*</span>申报品名(英)：</b>
                        <div class="fl ml10 f13">
                            <input type="text" class="h28-text blueFocus w250" value="${channel.declareNameEn }" name="declareNameEn">
                        </div>
                    </li>
                    <li>
                        <b class="fl tr w120 f13">申报百分比：</b>
                        <div class="fl ml10 f13">
                            <input type="text" class="h28-text blueFocus w100" value="${channel.declareRatio }" name="declareRatio"> %
                        </div>
                    </li>
                    <li>
                        <b class="fl tr w120 f13">最小申报价值：</b>
                        <div class="fl ml10 f13">
                            <input type="text" class="h28-text blueFocus w100" value="${channel.declareMin }" name="declareMin"> $
                        </div>
                    </li>
                    <li>
                        <b class="fl tr w120 f13">最大申报价值：</b>
                        <div class="fl ml10 f13">
                            <input type="text" class="h28-text blueFocus w100" value="${channel.declareMax }" name="declareMax"> $
                        </div>
                    </li>
                </ul>
            </div>
            <div class="b-dash-bor pb5 clearfix">
                <h5 class="f16 ml10 mt10">平台认可物流方式</h5>
                <ul class="add-order-li mt10">
                    <c:forEach items="${platforms }" var="platform">
                        <li class="w50p">
                            <b class="fl tr w120 f13">${platform.enName }:</b>
                            <div class="fl ml10 f13">
                                <select class="h30-select blueFocus w200 fl f12 platformLog" platformId="${platform.id }">
                                    <option value="">- 请选择 -</option>
                                    <c:forEach items="${platformLogisticsList }" var="platformLogistics">
                                        <c:if test="${platformLogistics.platformId == platform.id }">
                                            <option value="${platformLogistics.id}">${platformLogistics.name}</option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </div>
            <div class="b-dash-bor pb10 clearfix">
                <h5 class="f16 ml10 mt10">打印标签设置</h5>
                <ul class="add-order-li mt10 clearfix">
                    <li class="w50p">
                        <b class="fl tr w120 f13"><span class="c-red mr3">*</span>标签打印类型：</b>
                        <div class="fl ml10 f13">
                            <label><input type="radio" class="mr3"  name="labelType" value="1" ${channel.labelType != 2 ? 'checked': '' }>官方标签(物流公司提供的标签)</label>
                            <label class="ml10"><input type="radio" class="mr3" name="labelType" value="2" ${channel.labelType == 2 ? 'checked': '' }>自定义标签</label>
                        </div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13"><span class="c-red mr3">*</span>标签尺寸：</b>
                        <div class="fl ml10 f13">
                            <select class="h30-select blueFocus w180 fl f12" name="labelSize">
                                <option value="1" ${channel.labelSize == 1 ? 'selected' : '' }>10cm×10cm</option>
                                <option value="2" ${channel.labelSize == 2 ? 'selected' : '' }>10cm×5cm</option>
                                <option value="3" ${channel.labelSize == 3 ? 'selected' : '' }>A4(21cm×29.7cm)</option>
                                <option value="4" ${channel.labelSize == 4 ? 'selected' : '' }>8cm×3cm</option>
                                <option value="5" ${channel.labelSize == 5 ? 'selected' : '' }>自定义</option>
                            </select>
                            <a href="print_template_list.html" class="ml10 c-link1 fl f12" target="_blank"><i class="icon iconfont erp-ordinaryset mr3 f15 fl"></i>标签模板管理</a>
                        </div>
                    </li>
                </ul>
                <div class="customSelectBox ${channel.labelType != 2 ? 'none' :''}">
                    <ul class="add-order-li clearfix">
                        <li>
                            <b class="fl tr w120 f13">地址单：</b>
                            <div class="fl ml10 f13">
                                <label><input type="checkbox" class="mr3" name="useAdd" value="1" ${channel.useAdd == 1 ? 'checked':'' }>启用</label>
                            </div>
                        </li>
                        <li>
                            <b class="fl tr w120 f13">报关单：</b>
                            <div class="fl ml10 f13">
                                <label><input type="checkbox" class="mr3" name="useDec"  value="1" ${channel.useDec == 1 ? 'checked':'' }>启用</label>
                            </div>
                        </li>
                        <li>
                            <b class="fl tr w120 f13">配货单：</b>
                            <div class="fl ml10 f13">
                                <label><input type="checkbox" class="mr3" name="useDis"  value="1" ${channel.useDis == 1 ? 'checked':'' }>启用</label>
                            </div>
                        </li>
                    </ul>
                    <ul class="print-tag-tit w">
                        <li><a href="#">地址单</a></li>
                        <li><a href="#">报关单</a></li>
                        <li><a href="#">配货单</a></li>
                    </ul>
                    <div class="print-tag-con">
                        <div class="tag-choose-box clearfix mt15">
                            <div class="tag-large fl"><img src="${staticPath}/images/system/tag/1.jpg" width="280"></div>
                            <div class="tag-small-box fr" id="thumbnail">
                                <ul>
                                    <li class="tag-current">
                                        <a href="${staticPath}/images/system/tag/1.jpg">
                                            <img src="${staticPath}/images/system/tag/1.jpg" width="70">
                                            <p class="ellipsis">快越达广州平邮小包</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/2.jpg">
                                            <img src="${staticPath}/images/system/tag/2.jpg" width="70">
                                            <p class="ellipsis">中国邮政江苏小包</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/3.jpg">
                                            <img src="${staticPath}/images/system/tag/3.jpg" width="70">
                                            <p class="ellipsis">E邮宝-Qinli</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/4.jpg">
                                            <img src="${staticPath}/images/system/tag/4.jpg" width="70">
                                            <p class="ellipsis">美国邮政</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/5.jpg">
                                            <img src="${staticPath}/images/system/tag/5.jpg" width="70">
                                            <p class="ellipsis">出口易专线</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/6.jpg">
                                            <img src="${staticPath}/images/system/tag/6.jpg" width="70">
                                            <p class="ellipsis">顺丰平邮</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/7.jpg">
                                            <img src="${staticPath}/images/system/tag/7.jpg" width="70">
                                            <p class="ellipsis">线下E邮宝--欧洲美洲经济包</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/8.jpg">
                                            <img src="${staticPath}/images/system/tag/8.jpg" width="70">
                                            <p class="ellipsis">中邮专线标签</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/1.jpg">
                                            <img src="${staticPath}/images/system/tag/1.jpg" width="70">
                                            <p class="ellipsis">快越达广州平邮小包</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/2.jpg">
                                            <img src="${staticPath}/images/system/tag/2.jpg" width="70">
                                            <p class="ellipsis">中国邮政江苏小包</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/3.jpg">
                                            <img src="${staticPath}/images/system/tag/3.jpg" width="70">
                                            <p class="ellipsis">E邮宝-Qinli</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/4.jpg">
                                            <img src="${staticPath}/images/system/tag/4.jpg" width="70">
                                            <p class="ellipsis">美国邮政</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/5.jpg">
                                            <img src="${staticPath}/images/system/tag/5.jpg" width="70">
                                            <p class="ellipsis">出口易专线</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/6.jpg">
                                            <img src="${staticPath}/images/system/tag/6.jpg" width="70">
                                            <p class="ellipsis">顺丰平邮</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/7.jpg">
                                            <img src="${staticPath}/images/system/tag/7.jpg" width="70">
                                            <p class="ellipsis">线下E邮宝--欧洲美洲经济包</p>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="${staticPath}/images/system/tag/8.jpg">
                                            <img src="${staticPath}/images/system/tag/8.jpg" width="70">
                                            <p class="ellipsis">中邮专线标签</p>
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="b-dash-bor pb5 clearfix">
                <h5 class="f16 ml10 mt10 lh30">
                    回邮地址<span class="c-red f13 ml10">(线上发货必须用英文填写)</span><a href="#" class="site-bg radius3 bg-green cfff ml10 addAddress"><i class="icon iconfont erp-add mr3 f12"></i>新增</a>
                    <p class="fr mr10">
                        <select class="h30-select-ql blueFocus w150 fr f12 selectAddress">
                            <option value="">选择回邮地址</option>
                            <c:forEach items="${addresses}" var="address">
                                <c:if test="${address.type == 1 }">
                                    <option value="${address.id }">${address.name }</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </p>
                </h5>
                <ul class="add-order-li mt10 addressUl" type="1">
                    <li class="w50p">
                        <b class="fl tr w120 f13">地址名称：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">联系人：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li>
                        <b class="fl tr w120 f13">所在地区：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li style="width: 22%">
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li style="width: 22%">
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li style="width: 22%">
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li style="width: 100%">
                        <b class="fl tr w120 f13">详细地址：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">邮箱：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">邮编：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">固定电话：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">移动电话：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">公司名称：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">公司传真：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                </ul>
            </div>
            
            <div class="clearfix">
                <h5 class="f16 ml10 mt10 lh30">
                    揽货地址<span class="c-red f13 ml10">(必须用英文填写)</span><a href="#" class="site-bg radius3 bg-green cfff ml10 addAddress"><i class="icon iconfont erp-add mr3 f12"></i>新增</a>
                    <p class="fr mr10">
                        <select class="h30-select-ql blueFocus w150 fr selectAddress">
                            <option value="">选择揽货地址</option>
                            <c:forEach items="${addresses }" var="address">
                                <c:if test="${address.type == 2 }">
                                    <option value="${address.id }">${address.name }</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </p>
                </h5>
                <ul class="add-order-li mt10 addressUl" type="2">
                    <li class="w50p">
                        <b class="fl tr w120 f13">地址名称：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">联系人：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li>
                        <b class="fl tr w120 f13">所在地区：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li style="width: 22%">
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li style="width: 22%">
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li style="width: 22%">
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li style="width: 100%">
                        <b class="fl tr w120 f13">详细地址：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">邮箱：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">邮编：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">固定电话：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">移动电话：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">公司名称：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                    <li class="w50p">
                        <b class="fl tr w120 f13">公司传真：</b>
                        <div class="fl ml10 f13"></div>
                    </li>
                </ul>
            </div>
        </div>
        <div class="mt10 pt15 tc t-ql-bor">
            <button type="button" class="f-yh h32-btn radius3 search-btn f13 bg-blue cfff mr5 save">保 存</button>
            <button type="button" class="f-yh h32-btn-bor-ql search-btn radius3 f13 cancel">取 消</button>
        </div>
    </div>
</form>
<ul class="add-order-li mt10 none detailAddress">
    <li class="w50p">
        <b class="fl tr w120 f13">地址名称：</b>
        <div class="fl ml10 f13"><span name="name">广东省 惠州市</span><a class="site-bg radius3 bg-blue cfff ml10" onclick="editAddressBtn(this)"><i class="icon iconfont erp-edit mr3 f14"></i>修改<span hidden name="id"></span></a></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">联系人：</b>
        <div class="fl ml10 f13"><span name="contact">广东省 惠州市</span></div>
    </li>
    <li class="w">
        <b class="fl tr w120 f13">所在地区：</b>
        <div class="fl ml10 f13"><span name="country">广东省 惠州市</span> <span name="province"></span> <span name="city"></span> <span name="county"></span></div>
    </li>
    <li class="w">
        <b class="fl tr w120 f13">详细地址：</b>
        <div class="fl ml10 f13"><span name="address">广东省 惠州市</span></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">邮箱：</b>
        <div class="fl ml10 f13"><span name="mail">广东省 惠州市</span></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">邮编：</b>
        <div class="fl ml10 f13"><span name="zipcode">广东省 惠州市</span></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">固定电话：</b>
        <div class="fl ml10 f13"><span name="telephone">广东省 惠州市</span></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">移动电话：</b>
        <div class="fl ml10 f13"><span name="mobile">广东省 惠州市</span></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">公司名称：</b>
        <div class="fl ml10 f13"><span name="companyName">广东省 惠州市</span></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">公司传真：</b>
        <div class="fl ml10 f13"><span name="fax">广东省 惠州市</span></div>
    </li>
</ul>
<ul class="add-order-li mt10 none addAddressUl">
    <li class="w50p">
        <b class="fl tr w120 f13">地址名称：</b>
        <div class="fl ml10 f13"><input type="text" class="h28-text blueFocus w250" placeholder="" name="name"></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">联系人：</b>
        <div class="fl ml10 f13"><input type="text" class="h28-text blueFocus w250" placeholder="" name="contact"></div>
    </li>
    <li class="w">
        <b class="fl tr w120 f13">所在地区：</b>
        <div class="fl ml10 f13">
            <select class="h30-select blueFocus w140 f12 fl" name="country">
                <c:forEach items="${countries }" var="country">
                    <option value="${country.id }">${country.enName } <c:if test="${lang == 'zh'}">(${country.cnName })</c:if></option>
                </c:forEach>
            </select>
            <input type="text" class="h28-text blueFocus w150 fl ml10" placeholder="省份" name="province">
            <input type="text" class="h28-text blueFocus w150 fl ml10" placeholder="城市" name="city">
            <input type="text" class="h28-text blueFocus w150 fl ml10" placeholder="区域" name="county">
        </div>
    </li>
    <li class="w">
        <b class="fl tr w120 f13">详细地址：</b>
        <div class="fl ml10 f13"><input type="text" class="h28-text blueFocus w600 fl" placeholder="" name="address"></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">邮箱：</b>
        <div class="fl ml10 f13"><input type="text" class="h28-text blueFocus w250" placeholder="" name="mail"></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">邮编：</b>
        <div class="fl ml10 f13"><input type="text" class="h28-text blueFocus w250" placeholder="" name="zipcode"></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">固定电话：</b>
        <div class="fl ml10 f13"><input type="text" class="h28-text blueFocus w250" placeholder="" name="telephone"></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">移动电话：</b>
        <div class="fl ml10 f13"><input type="text" class="h28-text blueFocus w250" placeholder="" name="mobile"></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">公司名称：</b>
        <div class="fl ml10 f13"><input type="text" class="h28-text blueFocus w250" placeholder="" name="companyName"></div>
    </li>
    <li class="w50p">
        <b class="fl tr w120 f13">公司传真：</b>
        <div class="fl ml10 f13"><input type="text" class="h28-text blueFocus w250" placeholder="" name="fax"></div>
    </li>
</ul>
<div class=" p10-20 editAddressBox none">
    <form id="editAddrsssForm">
        <ul class="add-order-li mt5">
            <li class="w50p">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>地址名称：</b>
                <div class="fl ml10 f13">
                    <input type="hidden" class="h28-text blueFocus w200" name="id" value="">
                    <input type="hidden" class="h28-text blueFocus w200" name="type" value="">
                    <input type="text" class="h28-text blueFocus w200" name="name" value="广东省 惠州市">
                </div>
            </li>
            <li class="w50p">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>联系人：</b>
                <div class="fl ml10 f13">
                    <input type="text" class="h28-text blueFocus w200" name="contact" value="白海亮">
                </div>
            </li>
            <li class="w">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>所在地区：</b>
                <div class="fl ml10 f13">
                    <select class="h30-select blueFocus w100 fl f12" name="country">
                        <c:forEach items="${countries }" var="country">
                            <option value="${country.id }">${country.enName }<c:if test="${lang == 'zh'}">(${country.cnName })</c:if></option>
                        </c:forEach>
                    </select>
                    <input type="text" class="h28-text blueFocus w130 ml5 fl" placeholder="省份" name="province">
                    <input type="text" class="h28-text blueFocus w130 ml5 fl" placeholder="城市" name="city">
                    <input type="text" class="h28-text blueFocus w130 ml5 fl" placeholder="区域" name="county">
                </div>
            </li>
            <li class="w">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>详细地址：</b>
                <div class="fl ml10 f13">
                    <input type="text" class="h28-text blueFocus w450" value="浦东新区金融街" name="address">
                </div>
            </li>
            <li class="w50p">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>邮箱：</b>
                <div class="fl ml10 f13">
                    <input type="text" class="h28-text blueFocus w200" name="mail" value="714414169@qq.com">
                </div>
            </li>
            <li class="w50p">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>邮编：</b>
                <div class="fl ml10 f13">
                    <input type="text" class="h28-text blueFocus w200" value="600210" name="zipcode">
                </div>
            </li>
            <li class="w50p">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>固定电话：</b>
                <div class="fl ml10 f13">
                    <input type="text" class="h28-text blueFocus w200" value="111" name="telephone">
                </div>
            </li>
            <li class="w50p">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>移动电话：</b>
                <div class="fl ml10 f13">
                    <input type="text" class="h28-text blueFocus w200" value="13874125489" name="mobile">
                </div>
            </li>
            <li class="w50p">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>公司名称：</b>
                <div class="fl ml10 f13">
                    <input type="text" class="h28-text blueFocus w200" value="阿里巴巴" name="companyName">
                </div>
            </li>
            <li class="w50p">
                <b class="fl tr w90 f13"><span class="c-red mr3">*</span>公司传真：</b>
                <div class="fl ml10 f13">
                    <input type="text" class="h28-text blueFocus w200" value="5521556" name="fax">
                </div>
            </li>
        </ul>
    </form>
</div>
</body>
</html>