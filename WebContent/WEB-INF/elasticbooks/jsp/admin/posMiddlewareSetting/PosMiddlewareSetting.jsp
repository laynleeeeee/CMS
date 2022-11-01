<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description: POS Middleware Setting main page
	 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
var selectedWarehouseId = 0;
var selectedCustomerAcctId = 0;
var selectedCompanyId = 0;
$(document).ready(function () {
	loadWarehouses ();
});

$(function () {
	$("#slctCompany, #slctWarehouse, #txtCustomerName, #slctStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchPosMiddlewareSetting();
			e.preventDefault();
		}
	});
});

function loadWarehouses () {
	var selectedCompanyId = $("#slctCompany").val();
	if(selectedCompanyId != "" && selectedCompanyId != null) {
		$("#slctWarehouse").empty();
		$("#slctWarehouse").append("<option value=-1>ALL</>");
		var uri = contextPath+"/getWarehouse/byUserCompany";
		if (selectedCompanyId != -1) {
			uri += "?companyId="+selectedCompanyId+"&isActiveOnly=true";
		}
		var optionParser = {
				getValue: function (rowObject){
					if (rowObject != null)
						return rowObject["id"];
				},

				getLabel: function (rowObject){
					if (rowObject != null)
						return rowObject["name"];
				}
		};

		postHandler = {
				doPost: function(data) {
					// This is to remove any duplication.
					var found = [];
					$("#slctWarehouse option").each(function() {
						if($.inArray(this.value, found) != -1) 
							$(this).remove();
						found.push(this.value);
					});
				}
		};
		loadPopulate (uri, false, null, "slctWarehouse", optionParser, postHandler);
	}
}

function addPosMiddlewareSetting() {
	$("#divPosMiddlewareSettingForm").load(contextPath + "/admin/posMiddlewareSetting/form", function() {
		$("html, body").animate({scrollTop: $("#divPosMiddlewareSettingForm").offset().top}, 0050);
	});
}

function editPosMiddlewareSetting() {
	var id = getCheckedId("cbPosMiddlewareSetting");
	$("#divPosMiddlewareSettingForm").load(contextPath + "/admin/posMiddlewareSetting/form?pId="+id, function() {
		$("html, body").animate({scrollTop: $("#divPosMiddlewareSettingForm").offset().top}, 0050);
	});
}

var isSaving = false;
function savePosMiddlewareSetting() {
	if(isSaving == false) {
		isSaving = true;
		$("#hdnFrmCompanyId").val($("#slctFrmCompany").val());
		$("#hdnFrmWarehouseId").val($("#slctFrmWarehouse").val());
		$("#btnSavePosMiddlewareSetting").attr("disabled", "disabled");
		$("#hdnCustomerName").val($.trim($("#txtFrmCustomerName").val()));
		var companyName = $("#slctFrmCompany option:selected").text();
		doPostWithCallBack ("frmPosMiddlewareSetting", "divPosMiddlewareSettingForm", function(data) {
			if (data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "POS Middleware Setting for " 
					+ companyName + ".");
				$("#divPosMiddlewareSettingForm").html("");
				searchPosMiddlewareSetting();
			} else {
				var companyId = $("#hdnFrmCompanyId").val();
				var customerName = $("#txtFrmCustomerName").val();
				var customerId = $("#hdnArCustomerId").val();
				$("#divPosMiddlewareSettingForm").html(data);
				$("#slctFrmCompany").val(companyId);
				if (Number("${posMiddlewareSetting.id}") != 0) {
					$("#slctFrmCompany").attr("disabled", "disabled");
				}
				loadFrmWarehouses ();
				$("#txtFrmCustomerName").val(customerName);
				$("#hdnArCustomerId").val(customerId);
				filterCustomerAccts();
			}
			isSaving = false;
			$("#btnSavePosMiddlewareSetting").removeAttr("disabled");
		});
	}
}

function cancelPosMiddlewareSetting() {
	$("#divPosMiddlewareSettingForm").html("", function () {
		$("html, body").animate({scrollTop: $("#divPosMiddlewareSettingTbl").offset().top}, 0050);
	});
}

function getCommonParam() {
	var companyId = $("#slctCompany").val();
	var warehouseId = $("#slctWarehouse").val();
	var customerName = encodeURIComponent($("#txtCustomerName").val());
	var status = $("#slctStatus").val();
	return "?companyId="+companyId+"&warehouseId="+warehouseId+"&customerName="+customerName+"&status="+status;
}

function searchPosMiddlewareSetting() {
	doSearch ("divPosMiddlewareSettingTbl", "/admin/posMiddlewareSetting/search"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Company</td>
				<td>
					<select id="slctCompany" class="frmSmallSelectClass" 
						onchange="loadWarehouses();">
						<option value="-1">All</option>
						<c:forEach var="c" items="${companies}">
							<option value="${c.id}">${c.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Warehouse</td>
				<td>
					<select id="slctWarehouse" class="frmSmallSelectClass">
					</select>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Customer Name</td>
				<td>
					<input id="txtCustomerName" class="inputSmall">
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="slctStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchPosMiddlewareSetting();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divPosMiddlewareSettingTbl">
		<%@ include file="PosMiddlewareSettingTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddPosMiddlewareSetting" value="Add" onclick="addPosMiddlewareSetting();"></input>
		<input type="button" id="btnEditPosMiddlewareSetting" value="Edit" onclick="editPosMiddlewareSetting();"></input>
	</div>
	<br>
	<br>
	<div id="divPosMiddlewareSettingForm" style="margin-top: 20px;">
	</div>
</body>
</html>