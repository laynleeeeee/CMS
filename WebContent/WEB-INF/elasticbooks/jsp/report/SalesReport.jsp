<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: Sales report filter/generator jsp page
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
	var date = parseServerDate(new Date);
	$("#dateFrom").val(date);
	$("#dateTo").val(date);
});

function generateReport() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var salesPersonnelId = $("#salesPersonnelId").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var formatType = $("#formatType").val();
	var currencyId = $("#currencyId").val();
	var isValidFilter = true;
	if (companyId < 0) {
		$("#companyError").text("Company is required.");
		isValidFilter = false;
	}

	if(dateFrom != "" && dateTo != "" && new Date(dateFrom) > new Date(dateTo)) {
		$("#invoiceDateErr").text("Invalid date range.");
		isValidFilter = false;
	}

	if(dateFrom == "" && dateTo != "") {
		$("#invoiceDateErr").text("Invalid date range.");
		isValidFilter = false;
	}

	if(dateFrom != "" && dateTo == "") {
		$("#invoiceDateErr").text("Invalid date range.");
		isValidFilter = false;
	}

	if(dateFrom == "" && dateTo == "") {
		$("#invoiceDateErr").text("Invoice date is required.");
		isValidFilter = false;
	}

	//Customer
	if($("#spnSalesOfficerErr").text().trim() != "") {
		isValidFilter = false;
	}
	var url = "";
	if (isValidFilter) {
		clearSpanMsg();
		url = contextPath+"/salesReport/generatePDF?companyId="+companyId
				+"&divisionId="+divisionId+"&salesPersonnelId="+salesPersonnelId+"&currencyId="+currencyId
				+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType="+formatType;
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}

function companyDivOnChange() {
	$("#iFrame").attr('src', "");
	clearSalesPersonnel();
	clearSpanMsg();
}

function clearSpanMsg() {
	$("#companyError").text("");
	$("#dateError").text("");
	$("#invoiceDateErr").text("");
	$("#spanCustomerError").text("");
	$("#dateError").text("");
}

function showSalesPersonnels() {
	var companyId = $("#companyId").val();
	var name = $("#txtSalesOfficer").val();
	$("#txtSalesOfficer").autocomplete({
		source: contextPath + "/getSalesPersonnels?name="+processSearchName(name)+"&companyId="+companyId,
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

function getSalesPersonnel() {
	$("#spnSalesOfficerErr").text("");
	var companyId = $("#companyId").val();
	var name = $("#txtSalesOfficer").val();
	if (name != "") {
		$.ajax({
			url: contextPath + "/getSalesPersonnels?name="+processSearchName(name)
					+"&isExact=true"+"&companyId="+companyId,
			success : function(salesPersonnel) {
				if (salesPersonnel != null && salesPersonnel[0] != undefined) {
					$("#salesPersonnelId").val(salesPersonnel[0].id);
					$("#txtSalesOfficer").val(salesPersonnel[0].name);
				} else {
					$("#spnSalesOfficerErr").text("Invalid sales representative.");
					$("#salesPersonnelId").val("");
				}
			},
			dataType: "json"
		});
	} else {
		$("#salesPersonnelId").val("");
	}
}

function clearSalesPersonnel() {
	$("#salesPersonnelId").val("");
	$("#txtSalesOfficer").val("");
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
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" onchange="companyDivOnChange();" class="frmSelectClass">
				<option value="-1">ALL</option>
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Sales/Account Officer</td>
		<td>
			<input id="txtSalesOfficer" class="input" onkeypress="showSalesPersonnels();" onblur="getSalesPersonnel();">
			<input type="hidden" id="salesPersonnelId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spnSalesOfficerErr" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Invoice Date</td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
				style="float: right;"/>
			<span>To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
				style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="invoiceDateErr" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Currency</td>
		<td class="value">
			<select id="currencyId" class="frmSelectClass">
				<option value="-1">ALL</option>
				<c:forEach var="currency" items="${currencies}">
					<option value="${currency.id}">${currency.name}</option>
				</c:forEach>
			</select>
		</td>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>