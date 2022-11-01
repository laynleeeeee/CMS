<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--


	Description: Reordering Point Report search page
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxValidation.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$("#asOfDate").val("${currentDate}");
	filterWarehouses();
});

 function filterWarehouses() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath+"/getWarehouse/new?";
	if(companyId != -1){
		uri += "companyId="+companyId;
	}
	if(divisionId != -1){
		uri += "&divisionId="+divisionId;
	}
	$("#warehouseId").empty();
	var optionParser = {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	loadPopulate (uri, true, null, "warehouseId", optionParser, null);
}

function searchReport(){
	validateCategory();
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var warehouseId = $("#warehouseId").val();
	var statusId = $("#selectStatusId").val();
	var icId = $("#hdnCategoryId").val();
	var asOfDate = $("#asOfDate").val();
	var formatType = $("#formatType").val();
	console.log("icId", icId);
	if(companyId == -1){
		$("#spanCompanyError").text("Company is required");
		$("#iFrame").attr('src', "");
	}
	if(asOfDate == "") {
		$("#spanErrorDate").text("As of date is a required field.");
		$("#iFrame").attr('src', "");
	}
	if(companyId != -1 && divisionId != null && asOfDate != "" && warehouseId != null) {
		var url = contextPath+"/reorderingPointPDF?companyId="+companyId+"&divisionId="+divisionId+"&warehouseId="+warehouseId
			+"&categoryId="+icId+"&status="+statusId+"&asOfDate="+asOfDate+"&formatType="+formatType;
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
		clearValues();
	} else {
		$("#iFrame").attr('src', "");
	}
}

function clearValues() {
	$("#spanCompanyError").text("");
	$("#spanErrorDate").text("");
	$("#spanCategoryError").text("");
}

function showCategories () {
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

function validateCategory() {
	var itemCategory = $.trim($("#categoryId").val());
	if(itemCategory != "") {
		itemCategory = processSearchName(itemCategory);
		var companyId = $("#companyId").val();
		var sourceUrl = "/getItemCategories/perCategory?term="+itemCategory
				+ "&companyId="+companyId;
	
		validateInput(sourceUrl,
		function(itemCategory) {
			//success
			$("#spanCategoryError").text("");
			$("#hdnCategoryId").val(itemCategory.id);
		}, function(itemCategory) {
			//error
			$("#spanCategoryError").text("Invalid item category.");
		});
	} else {
		$("#hdnCategoryId").val("");
	}
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
			<select id="companyId" class="frmSelectClass">
				<option selected='selected' value=-1>Please select a company</option>
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
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
		<td class="title2">Division </td>
		<td class="value">
			<select id="divisionId" class="frmSelectClass" onchange="filterWarehouses();">
				<option selected='selected' value=-1>ALL</option>
			<c:forEach var="division" items="${divisions}">
				<option value="${division.id}">${division.name}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Warehouse </td>
		<td>
			<select id="warehouseId" class="frmSelectClass" >
				<option value="-1">ALL</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Item Category </td>
		<td><input type="hidden" id="hdnCategoryId"/>
			<input class="input" id="categoryId" onkeydown="showCategories();"
				onkeyup="showCategories();" onblur="validateCategory();">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCategoryError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Status</td>
		<td><select id="selectStatusId" class="frmSelectClass">
			<c:forEach var="status" items="${status}">
					<option>${status}</option>
			</c:forEach>
		</select>
	</tr>
	<tr>
		<td class="title2">As of Date </td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
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
		<td class="title2">Format:</td>
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