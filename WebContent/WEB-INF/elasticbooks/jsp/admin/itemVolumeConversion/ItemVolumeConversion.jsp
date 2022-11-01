<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description: Item Volume Conversion main page. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Student</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
var SEARCH_URL = "/admin/volumeConversion/search";
var FORM_URL = contextPath+"/admin/volumeConversion/form";

function loadTheItems() {
	var companyId = $("#searchCompanyId").val();
	var stockCode = processSearchName($.trim($("#txtStockCodeId").val()));
	var uri = null;
	if(companyId != -1){
		uri = contextPath+"/getRItems/filter?companyId="+companyId+
			"&stockCode="+stockCode;
	} else {
		uri = contextPath+"/getRItems/filter?stockCode="+stockCode;
	}
	loadACList("txtStockCodeId", "itemId", uri,
			contextPath + "/getItem?stockCode=", "stockCodeAndDesc", "stockCode",
			function(item) {
				//Select
				$("#spanSCError").text("");
			}, function(item) {
				//Change
				$("#spanSCError").text("");
			}, function() {
				//Success
				$("#spanSCError").text("");
			}, function() {
				//Error
				$("#spanSCError").text("Invalid stock code.");
			}
	);
}

function getTheItem (elem) {
	var companyId = $("#searchCompanyId").val();
	var stockCode = $.trim($("#txtStockCodeId").val());
	var uri = null;
	if(companyId != -1){
		uri = contextPath+"/getItem?stockCode="+processSearchName(stockCode)+
		"&companyId="+companyId;
	} else {
		uri = contextPath+"/getItem?stockCode="+processSearchName(stockCode);
	}
	if (stockCode != "") {
		$.ajax({
			url: uri,
			success: function (item) {
				if (item != null) {
					$(elem).val(item.stockCode);
					$("#searchItemId").val(item.id);
				}
			},
			error : function(error) {
				$("#searchItemId").val("");
			},
			dataType: "json"
		});
	} else {
		$("#searchItemId").val("");
	}
}

function searchItemPointSettings() {
	var param = getCommonParam()+"&page=1";
	doSearch("divVolumeConversion", SEARCH_URL+param);
}

$(function () {
	$("#searchCompanyId").one("keypress", function (e) {
		if (e.which == 13) {
			searchItemPointSettings();
			e.preventDefault();
		}
	});
});

$(function () {
	$("#selectStatusId").one("keypress", function (e) {
		if (e.which == 13) {
			searchItemPointSettings();
			e.preventDefault();
		}
	});
});

$(function () {
	$("#txtStockCodeId").one("keypress", function (e) {
		if (e.which == 13) {
			searchItemPointSettings();
			e.preventDefault();
		}
	});
});

function getCommonParam() {
	var companyId = $("#searchCompanyId").val();
	var status = $("#selectStatusId").val();
	var itemId = ($("#searchItemId").val() != "" ? $("#searchItemId").val() : -1);
	return "?itemId="+itemId+"&status="+status+"&companyId="+companyId;
}

function addVolumeConversion() {
	$("#divItemConversionForm").load(FORM_URL, function (){
		$("html, body").animate({
			scrollTop: $("#divItemConversionForm").offset().top}, 0050);
	});
}

function editVolumeConversion() {
	var id = getCheckedId("cbVolumeConversion");
	$("#divItemConversionForm").load(FORM_URL+"?volumeConversionId="+id, function (){
		$("html, body").animate({
			scrollTop: $("#divItemConversionForm").offset().top}, 0050);
	});
}

function saveVolumeConversion() {
	$("#spanSCError").text("");
	$("#btnVolumeConversion").attr("disabled", "disabled");
	unformatNumbers();
	doPostWithCallBack ("volumeConversion", "divItemConversionForm", function(data) {
		if (data == "saved") {
			$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "item volume conversion " + $("#txtStockCode").val() + ".");
			searchItemPointSettings();
			dojo.byId("divItemConversionForm").innerHTML = "";
		} else {
			var qty = accounting.formatMoney($("#quantityId").val());
			var volume = accounting.formatMoney($("#volumeConversionId").val());
			$("#divItemConversionForm").html(data);
			$("#quantityId").val(qty);
			$("#volumeConversionId").val(volume);
		}
	});
	$("#btnSaveVolumeConversion").removeAttr("disabled");
}

function cancelForm() {
	$("#divItemConversionForm").html("");
}

function formatMoney(elem) {
	$(elem).val(accounting.formatMoney($(elem).val()));
}

function unformatNumbers() {
	var qty = accounting.unformat($("#quantityId").val());
	var volume = accounting.unformat($("#volumeConversionId").val());
		$("#quantityId").val(qty);
	$("#volumeConversionId").val(volume);
}

function clearItemId () {
	$("#searchItemId").val("");
	$("#txtStockCodeId").val("");
}
</script>
</head>
<body><body>
	<div id="divSearch">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Company</td>
				<td>
					<select id="searchCompanyId" class="frmSelectClass" onchange="clearItemId();">
						<option value="-1">All</option>
						<c:forEach var="company" items="${companies}">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="title">Item:</td>
				<td>
					<input type="text" id="txtStockCodeId" class="input" onkeyup="loadTheItems();" 
						onkeydown="loadTheItems();" onblur="getTheItem(this);"/>
					<input type = "hidden" id="searchItemId"/>
				</td>
			</tr>
			<tr>
				<td class="title">Status:</td>
				<td><select id="selectStatusId" class="frmSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
					<input type="button" id="btnSearchItemPointSettings" value="Search" onclick="searchItemPointSettings();"/>
				</td>
			</tr>	
		</table>
	</div>
		<span id="spanMessage" class="message"></span>
		<div id="divVolumeConversion">
			<%@ include file="ItemVolumeConversionTable.jsp" %>
		</div>
	<div class="controls">
		<input type="button" id="btnAddVolumeConversion" value="Add" onclick="addVolumeConversion();"/>
		<input type="button" id="btnEditVolumeConversion" value="Edit" onclick="editVolumeConversion();"/>
	</div>
	<br/>
	<br/>
	<div id="divItemConversionForm" style="margin-top: 50px;">
	</div>
</body>
</html>