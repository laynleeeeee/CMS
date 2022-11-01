<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Inventory Listing search page.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<title>Inventory Listing Report</title>
<script type="text/javascript">
$(document).ready(function() {
	$("#asOfDate").val("${currentDate}");
	companyOnChange();
});

function filterWarehouses() {
	var companyId = $("#companyId").val();
	if(companyId != "") {
		var uri = contextPath+"/getWarehouse?companyId="+companyId;
		$("#warehouseId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, false, null, "warehouseId", optionParser, null);
	}
}

function searchReport(){
	var itemCategory = processSearchName($.trim($("#categoryId").val()));
	if(itemCategory == "") {
		$("#hdnCategoryId").val(null);
	}

	var asOfDate = $("#asOfDate").val();
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	var categoryId = $("#hdnCategoryId").val();
	var stockCode = $.trim($("#txtStockCode").val());
	var orderBy = $("#orderBy").val();
	var formatType = $("#formatType").val();
	var reportType = $("#reportType").val();
	if(companyId == -1){
		$("#spanCompanyError").text("Company is required");
		$("#iFrame").attr('src', "");
	} if(asOfDate == "") {
		$("#spanErrorDate").text("As of date is a required field.");
		$("#iFrame").attr('src', "");
	} if(warehouseId == null){
		$("#spanWarehouseError").text("Warehouse is required.");
		$("#iFrame").attr('src', "");
	} if(companyId != -1 && asOfDate != "" && warehouseId != null) {
		var url = "availableStocksPDF?companyId="+companyId+"&warehouseId="+warehouseId
				+"&itemCategoryId="+categoryId+"&stockCode="+stockCode+"&orderBy="+orderBy
				+"&asOfDate="+asOfDate+"&formatType="+formatType+"&reportType="+reportType;
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
		clearValues();
	}
}

function showCategories() {
	clearValues();
	var companyId = $("#companyId").val();
	if(companyId == -1) {
		$("#spanCompanyError").text("Company is required.");
		return;
	} else {
		var hdnCatId = "hdnCategoryId";
		var uri = contextPath+"/getItemCategories?companyId="+companyId;
		loadACList("categoryId", hdnCatId, uri, uri, "name", "name",
				function(category) {
					//Select
					$("#spanCategoryError").text("");
					if(category != "") {
						$("#"+hdnCatId).val(category.id);
					}
				}, function(category) {
					//Change
					$("#spanCategoryError").text("");
					if(category != null) {
						$("#"+hdnCatId).val(category.id);
					}
				}, function() {
					//Success
					$("#spanCategoryError").text("");
				}, function() {
					//Error
					$("#spanCategoryError").text("Invalid Item Category.");
					$("#"+hdnCatId).val("");
				}
		);
	}
}

function loadItems() {
	var companyId = $("#companyId").val();
	var categoryId = $("#hdnCategoryId").val();
	var stockCode = processSearchName($.trim($("#txtStockCode").val()));
	var uri = contextPath+"/getRItems/filter?companyId="+companyId
			+"&itemCategoryId="+categoryId+"&stockCode="+stockCode;
	loadACList("txtStockCode", "hdnItemId", uri,
			contextPath + "/getItem?stockCode=", "stockCodeAndDesc", "stockCode",
			function(item) {
				//Select
				$("#spanSCError").text("");
				if(item != null) {
					$("#hdnItemId").val(item.id);
				}
			}, function(item) {
				//Change
				$("#spanSCError").text("");
				if(item != null) {
					$("#hdnItemId").val(item.id);
				}
			}, function(item) {
				//Success
				$("#spanSCError").text("");
				if(item != null) {
					$("#hdnItemId").val(item.id);
				}
			}, function() {
				//Error
				$("#spanSCError").text("Invalid stock code.");
			}
	);
}

function validateStockCode() {
	var stockCode = $.trim($("#txtStockCode").val());
	if (stockCode != "") {
		$.ajax({
			url: contextPath+"/"+"/getItem?stockCode="+processSearchName(stockCode),
			success: function (item) {
				$("#spanSCError").text("");
				$("#txtStockCode").val(item.stockCode);
				$("#hdnItemId").val(item.id);
			},
			error : function(error) {
				$("#hdnItemId").val("");
				$("#spanSCError").text("Invalid stock code.");
			},
			dataType: "json"
		});
	}
}

function clearValues() {
	$("#spanCompanyError").text("");
	$("#spanWarehouseError").text("");
}

function companyOnChange() {
	filterWarehouses();
}
</script>
</head>
<body>
<table>
	<tr>
		<td>
			<br/>
		</td>
	</tr>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" class="frmSelectClass" onchange="companyOnChange();">
				<option selected='selected' value=-1>Please select a company</option>
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Report Type</td>
		<td class="value">
			<select id="reportType" class="frmSelectClass">
					<option value="1">Inventory Worksheet</option>
					<option value="2">Available Stocks</option>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Warehouse </td>
		<td>
			<select id="warehouseId" class="frmSelectClass" ></select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanWarehouseError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Item Category </td>
		<td><input type="hidden" id="hdnCategoryId"/>
			<input class="input" id="categoryId" onkeydown="showCategories();" onkeyup="showCategories();">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCategoryError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Stock Code/Description</td>
		<td class="value"><input type="text" id="txtStockCode" class="input" onkeyup="loadItems();" 
				onkeydown="loadItems();" onblur="validateStockCode();"/><input type="hidden" id="hdnItemId"/></td>
	</tr>
	<tr>
		<td></td>
		<td><span id="spanSCError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Order By</td>
		<td class="value">
			<select id="orderBy" class="frmSelectClass">
					<option value="sc">Stock Code</option>
					<option value="desc">Description</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">As of Date </td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('asOfDate')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="spanErrorDate" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Format</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select>
		</td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>