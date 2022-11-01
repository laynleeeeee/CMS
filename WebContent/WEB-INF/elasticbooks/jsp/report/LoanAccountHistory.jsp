<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: Loan account history report filter/generator jsp page
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function showSuppliers() {
	var companyId = $("#companyId").val();
	if ($("#companyId").val() < 0) {
		$("#spanCompanyError").text("Company is required.");
	} else {
		var supplierName = $.trim($("#txtSupplierName").val());
		var divisionId = $("#divisionId").val();
		var uri = contextPath+"/getSuppliers/new?name="+encodeURIComponent(supplierName)
				+"&companyId="+companyId+"&divisionId="+divisionId;
		$("#txtSupplierName").autocomplete({
			source: uri,
			select: function( event, ui ) {
				$(this).val(ui.item.name);
				return false;
			}, minLength: 2
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>" +item.name + "</a>" )
				.appendTo( ul );
		};
	}
}

function getSupplier() {
	$("#supplierError").text("");
	$("#supplierAcctError").text("");
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var supplierName = $.trim($("#txtSupplierName").val());
	var uri = contextPath + "/getSuppliers/new?name="+encodeURIComponent(supplierName)
			+"&companyId="+companyId+"&divisionId="+divisionId+"&isExact=true";
	if (companyId > 0 && supplierName != "") {
		$.ajax({
			url: uri,
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#hdnSupplierId").val(supplier[0].id);
					$("#txtSupplierName").val(supplier[0].name);
				} else {
					$("#supplierError").text("Invalid supplier.");
					$("#hdnSupplierId").val("");
					$("#supplierAcctId").empty();
				}
				filterSupplierAccts();
			},
			dataType: "json"
		});
	}
}

function filterSupplierAccts() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var supplierId = $("#hdnSupplierId").val();
	if (supplierId != "") {
		var uri = contextPath+"/getApSupplierAccts?supplierId="+supplierId
				+"&companyId="+companyId+"&divisionId="+divisionId;
		$("#supplierAcctId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, false, null, "supplierAcctId", optionParser, null);
	}
}

function generateReport() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var supplierName = $.trim($("#txtSupplierName").val());
	var supplierId = $("#hdnSupplierId").val();
	var supplierAcctId = $("#supplierAcctId").val();
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var formatType = $("#formatType").val();
	var isValidFilter = true;
	if (companyId < 0) {
		$("#companyError").text("Company is a required.");
		isValidFilter = false;
	}
	if (supplierName == "") {
		$("#supplierError").text("Supplier is a required.");
		isValidFilter = false;
	}
	if (supplierAcctId == null || supplierAcctId == "") {
		$("#supplierAcctError").text("Supplier account is a field.");
		isValidFilter = false;
	}
	if (dateFrom == "" || dateTo == "") {
		$("#dateError").text("Date from and/or to are required fields.");
		isValidFilter = false;
	} else if (dateFrom == "" && dateTo != "") {
		$("#dateError").text("Invalid date range.");
		isValidFilter = false;
	} else if (dateFrom != "" && dateTo == "") {
		$("#dateError").text("Invalid date range.");
		isValidFilter = false;
	} else if (new Date(dateFrom) > new Date(dateTo)) {
		$("#dateError").text("Invalid date range.");
		isValidFilter = false;
	}
	var url = "";
	if (isValidFilter) {
		clearSpanMsg();
		url = contextPath+"/loanAcctHistory/generatePDF?companyId="+companyId
				+"&divisionId="+divisionId+"&supplierId="+supplierId
				+"&supplierAcctId="+supplierAcctId+"&dateFrom="+dateFrom+"&dateTo="+dateTo
				+"&formatType="+formatType;
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}

function companyDivOnChange() {
	$("#txtSupplierName").val("");
	$("#hdnSupplierId").val("");
	$("#supplierAcctId").empty();
	$("#iFrame").attr('src', "");
	clearSpanMsg();
}

function clearSpanMsg() {
	$("#companyError").text("");
	$("#supplierError").text("");
	$("#supplierAcctError").text("");
	$("#dateError").text("");
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company</td>
		<td>
			<select id="companyId" onchange="companyDivOnChange();" class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="companyError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Division </td>
		<td>
			<select id="divisionId" onchange="companyDivOnChange();" class="frmSelectClass">
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Lender</td>
		<td>
			<input type="hidden" id="hdnSupplierId"/>
			<input id="txtSupplierName" class="input" onkeydown="showSuppliers();"
				onblur="getSupplier();">
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="supplierError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Lender Account</td>
		<td><select id="supplierAcctId" class="frmSelectClass"></select></td>
	</tr>
	<tr>
		<td></td>
		<td><span id="supplierAcctError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Date</td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2"
				value="${currentDate}" onblur="evalDate(this.id, false);">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
				style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2"
				value="${currentDate}" onblur="evalDate(this.id, false);">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
				style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="dateError" class="error"></span></td>
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
		<td colspan="3"><input type="button" value=Generate onclick="generateReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>