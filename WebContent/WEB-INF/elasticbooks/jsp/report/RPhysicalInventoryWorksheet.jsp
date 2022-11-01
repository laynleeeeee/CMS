<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Physical Inventory Listing search page.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<title>Inventory Listing Report</title>
<script type="text/javascript">
$(document).ready(function() {
	$("#asOfDate").val("${currentDate}");
	CompanyOnChange();
});

function filterWarehouses() {
	var divisionId = $("#divisionId option:selected").val();
	if(divisionId != "") {
		var uri = contextPath+"/getWarehouse/new?divisionId="+divisionId;
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
}
function filterDivisionbyCompany() {
	var companyId = $("#companyId").val();
	if(companyId != "") {
		var uri = contextPath+"/getDivisions?companyId="+companyId;
		$("#divisionId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, true, null, "divisionId", optionParser, null);
	}
}

function searchReport(){

	var categoryId = $("#hdnCategoryId").val();
	var divisionId = $("#divisionId option:selected").val();
	var stockOption = $("#stockOption").val();
	var companyId = $("#companyId").val();
	var asOfDate = $("#asOfDate").val();
	var warehouseId = $("#warehouseId").val();
	var statusId = $("#selectStatusId").val();
	var workflowStatusId = $("#workflowStatusId").val();
	var orderBy = $("#orderBy").val();
	var formatType = $("#formatType").val();
	var isIncludeQuantity = $("#isIncludeQuantity").val();
	if(companyId == -1){
		$("#spanCompanyError").text("Company is required");
		$("#iFrame").attr('src', "");
	} if(asOfDate == "") {
		$("#spanErrorDate").text("As of date is required.");
		$("#iFrame").attr('src', "");
	} if(warehouseId == null) {
		$("#spanWarehouseError").text("Warehouse is required.");
		$("#iFrame").attr('src', "");
	} if(companyId != -1 && asOfDate != "" && warehouseId != null) {
		var url = "rPhysicalInventoryWorksheetPDF?itemCategoryId="+categoryId+"&companyId="+companyId+"&divisionId="+divisionId+"&warehouseId="
				+warehouseId+"&status="+statusId+"&workflowStatusId="+workflowStatusId
				+"&stockOptionId="+stockOption+"&asOfDate="+asOfDate+"&isIncludeQuantity="+isIncludeQuantity+"&orderBy="+orderBy+"&formatType="+formatType;
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
		clearValues();
	}
}

function clearValues() {
	$("#spanCompanyError").text("");
	$("#spanCategoryError").text("");
	$("#spanErrorDate").text("");
	$("#spanWarehouseError").text("");
}

function showCategories() {
	clearValues();
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath+"/getItemCategories?companyId="+companyId+"&divisionId="+divisionId;
	$("#categoryId").autocomplete({
		source: uri,
		select: function(event, ui) {
			$("#hdnCategoryId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui) {
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#hdnCategoryId").val(ui.item.id);
						$(this).val(ui.item.name);
					}
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
		.data( "ui-autocomplete-item", item )
		.append( "<a style='font-size: small;'>"+item.name +"</a>" )
		.appendTo( ul );
	};
}

function getItemCategory() {
	var itemCategory = processSearchName($.trim($("#categoryId").val()));
	if(itemCategory != "") {
		var companyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		$.ajax ({
			url: contextPath + "/getItemCategories/perCategory?term="+itemCategory+"&companyId="+companyId+"&divisionId="+divisionId,
			success : function(item) {
				if (item != null) {
					$("#hdnCategoryId").val(item.id);
					$("#categoryId").val(item.name);
				} else {
					$("#spanCategoryError").text("Invalid item category.");
					$("#categoryId").val("");
					$("#hdnCategoryId").val("");
				}
			},
			dataType: "json"
		});
	} else {
		$("#hdnCategoryId").val(null);
	}
}

function CompanyOnChange() {
	filterDivisionbyCompany();
	showCategories();
	$("#hdnCategoryId").val("");
	$("#categoryId").val("");
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
			<select id="companyId" class="frmSelectClass" onchange="CompanyOnChange();">
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
		<td>
		<select id="divisionId" class="frmSelectClass" onchange="filterWarehouses();">
				<option value=-1>ALL</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Warehouse </td>
		<td>
			<select id="warehouseId" class="frmSelectClass" >
				<option value=-1>ALL</option>
			</select>
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
			<input class="input" id="categoryId" onkeydown="showCategories();" onkeyup="showCategories();"
			onblur="getItemCategory();">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCategoryError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Stock Option </td>
		<td>
			<select id="stockOption" class="frmSelectClass">
				<option value=-1>ALL</option>
				<option value=0>No Zero</option>
			</select>
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
		<td class="title2">Workflow Status </td>
		<td>
			<select id="workflowStatusId" class="frmSelectClass">
				<option value=-1>ALL</option>
				<option value=1 selected="selected">Completed</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">As of Date </td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);" >
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
		<td class="title2">Include Quantity</td>
			<td class="value">
			<select id="isIncludeQuantity" class="frmSelectClass">
					<option value="True">Yes</option>
					<option value="False">No</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Order By:</td>
		<td class="value">
			<select id="orderBy" class="frmSelectClass">
					<option value="sc">Stock Code</option>
					<option value="desc">Description</option>
			</select>
		</td>
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