<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Statement of Account Report.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript">
var selectedCompanyId = -1;
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function clearSpanError() {
	$("#spanCompanyError").text("");
	$("#spanCustomerError").text("");
	$("#spanCustomerAcctError").text("");
	$("#spanDateError").text("");
}

function searchStatementOfAccount () {
	clearSpanError();
	var isValidFilters = true;
	if ($("#companyId option:selected").val() == -1){
		$("#spanCompanyError").text("Company is required.");
		isValidFilters = false;
	}else if($("#customerId").val() == "" || $("#arCustomerId").val() == "") {
		$("#spanCustomerError").text("Customer is required.");
		isValidFilters = false;
	}else if($("#customerAcctId").val() == "" || $("#customerAcctId").val() == null) {
		$("#spanCustomerAcctError").text("Customer Account is required.");
		isValidFilters = false;
	}else if($.trim($("#dateFrom").val()) == "" || $.trim($("#dateTo").val()) == ""){
		$("#spanDateError").text("Dates from and to are required.");
		isValidFilters = false;
	}else if (new Date ($("#dateFrom").val()) > new Date ($("#dateTo").val())) {
			$("#spanDateError").text("Invalid date range.");
			isValidFilters = false;}
	var url = "";
	if (isValidFilters) {
		$("#spanCompanyError").text("");
		$("#spanCustomerError").text("");
		$("#spanCustomerAcctError").text("");
		$("#spanDateError").text("");
		url = contextPath + "/statementOfAccount/generatePDF"+getCommonParam();
	}
	$('#reportSOA').attr('src',url);
	$('#reportSOA').load();
}

function getCommonParam () {
	var companyId = $("#companyId option:selected").val();
	var divisionId = $("#divisionId").val();
	var customerId = $("#arCustomerId").val();
	var customerAcctId = $("#customerAcctId").val();
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var formatType = $("#formatType").val();
	var currencyId = $("#currencyId").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&customerId="+customerId+"&customerAcctId="+customerAcctId
			+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType="+formatType+"&currencyId="+currencyId;
}

function getArCustomers () {
	var companyId = $("#companyId").val();
	var customerId = $.trim($("#customerId").val());
	var divisionId = $("#divisionId").val();
	if (customerId != "") {
		$("#spanCustomerError").text("");
		var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerId)+"&isExact=true"+"&companyId="+companyId;
			+(divisionId != -1 ? "&divisionId="+divisionId : "");
		$.ajax({
			url: uri,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					$("#arCustomerId").val(customer[0].id);
					$("#customerId").val(customer[0].name);
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#arCustomerId").val("");
					$("#arCustomerAccountId").empty();
				}
				filterCustomerAccts();
			},
			dataType: "json"
		});
	} else {
		$("#hdnArCustomerId").val("");
		$("#arCustomerIdError").text("");
		clearAndAddEmpty();
	}
}

function showArCustomers () {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerId = $("#customerId").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerId)+"&companyId="+companyId
		+(divisionId != -1 ? "&divisionId="+divisionId : "");
	$("#customerId").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#arCustomerId").val(ui.item.id);
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

function filterCustomerAccts() {
	$("#customerAcctId").empty();
	if($.trim($("#customerId").val()) == "") {
		$("#arCustomerId").val("");
		clearAndAddEmpty ();
	}else{
		selectedCompanyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var selectedCustomerId = $("#arCustomerId").val();
		var uri = contextPath+"/getArCustomerAccounts?arCustomerId="+selectedCustomerId+"&companyId="+selectedCompanyId
				+(divisionId == -1 ? "" : "&divisionId="+divisionId);
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		var items = 0;
		postHandler = {
			doPost: function(data) {
				// This is to remove any duplication.
				var found = [];
				$("#customerAcctId option").each(function() {
					if($.inArray(this.value, found) != -1)
						$(this).remove();
					found.push(this.value);
				});
				items = parseInt($("#customerAcctId > option").length);
				if (items == 1 && $("#customerAcctId").val() == -1) {
					clearAndAddEmpty();
				}
			}
		};
		loadPopulate (uri, true, null, "customerAcctId", optionParser, postHandler);
	}
}

function clearAndAddEmpty () {
	$("#arCustomerId").val("");
	$("#customerId").val("");
	$("#arCustomerAccountId").empty();
	$("#arCustomerAccountId").append("<option value=-1>ALL</option>");
}

function addAllOption() {
	var option = "<option selected='selected' value='-1'>ALL</option>";
	$("#customerAcctId").append(option);
}

function selectOnChange() {
	$("#customerId").val("");
	clearAndAddEmpty();
	filterCustomerAccts();
	addAllOption();
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" class="frmSelectClass" onchange="selectOnChange();">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td>
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
		<tr>
		<td class="title2">Division </td>
		<td>
			<select id="divisionId" class="frmSelectClass" onchange="selectOnChange();" >
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer</td>
		<td>
			<input id="customerId" class="input" onkeydown="showArCustomers();" onkeyup="showArCustomers();" onblur="getArCustomers();">
			<input type="hidden" id="arCustomerId">
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td>
			<span id="spanCustomerError" class="error"></span>
		</td>
	</tr>
	<tr>
 		<td class="title2">Customer Account</td>
 		<td>
 			<select id="customerAcctId" class="frmSelectClass">
 				<c:forEach var="customerAcct" items="${customerAccts}">
 					<option value="${customerAcct.id}">${customerAcctt.name}</option>
 				</c:forEach>
 			</select>
 		</td>
 	</tr>
 	<tr>
 		<td class="title2"></td>
		<td>
 			<span id="spanCustomerAcctError" class="error"></span>
 		</td>
 	</tr>
	<tr>
		<td class="title2">Date Range</td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Currency</td>
		<td>
			<select id="currencyId" class="frmSelectClass">
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
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDateError" class="error"></span>
		</td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchStatementOfAccount();" /></td>
	</tr>
</table>
<div>
	<iframe id="reportSOA"></iframe>
</div>
<hr class="thin2">
</body>
</html>