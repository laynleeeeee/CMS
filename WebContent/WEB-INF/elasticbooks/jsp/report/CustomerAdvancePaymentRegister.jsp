<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Customer Advance Payment Register main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	appendAll();
});

function getArCustomer () {
	var companyId = $("#companyId").val();
	var customerName = $.trim($("#customerName").val());
	var divisionId = $("#divisionId").val();
	if (customerName != "") {
		$("#spanCustomerError").text("");
		var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&isExact=true"+"&companyId="+companyId;
			+(divisionId != -1 ? "&divisionId="+divisionId : "");
		$.ajax({
			url: uri,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					$("#arCustomerId").val(customer[0].id);
					$("#customerName").val(customer[0].name);
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#arCustomerId").val("");
					$("#arCustomerAccountId").empty();
				}
				filterArcustomerAccount();
			},
			dataType: "json"
		});
	} else {
		$("#hdnArCustomerId").val("");
		$("#arCustomerIdError").text("");
		clearCustomerAndAcct();
	}
}

function clearCustomerAndAcct() {
	$("#arCustomerId").val("");
	$("#customerName").val("");
	$("#arCustomerAccountId").empty();
	$("#arCustomerAccountId").append("<option value=-1>ALL</option>");
}

function showArCustomers () {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $("#customerName").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&companyId="+companyId
		+(divisionId != -1 ? "&divisionId="+divisionId : "");
	$("#customerName").autocomplete({
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

function filterArcustomerAccount() {
	$("#arCustomerAccountId").empty();
	if($.trim($("#customerName").val()) == "") {
		clearCustomerAndAcct();
	} else {
		var companyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var arCustomerId = $("#arCustomerId").val();
		var uri = contextPath + "/getArCustomerAccounts?arCustomerId="+arCustomerId+"&companyId="+companyId
					+(divisionId == -1 ? "" : "&divisionId="+divisionId);
		var optionParser = {
			getValue : function(rowObject) {
				return rowObject["id"];
			},
			getLabel : function(rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate(uri, true, null, "arCustomerAccountId", optionParser, null);
	}
}

function generateCAPRegReport() {
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var isValidFilters = true;

	if ($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Company is required.");
		isValidFilters = false;
	} else if ($("#spanCustomerError").text() != "") {
		isValidFilters = false;
	} else if (dateFrom == "" && dateTo == "") {
		$("#spanDateError").text("Date range is required.");
		isValidFilters = false;
	} else if (dateFrom == "" || dateTo == "") {
		$("#spanDateError").text("Invalid date range.");
		isValidFilters = false;
	} else if (new Date(dateFrom) > new Date(dateTo)) {
		$("#spanDateError").text("Invalid date range.");
		isValidFilters = false;}
	var url = "";

	if (isValidFilters) {
		$("#spanCompanyError").text("");
		$("#spanCustomerError").text("");
		$("#spanDateError").text("");
		url = contextPath + "/customerAdvancePaymentRegister/generatePDF"+getCommonParam();
	}
	$('#reportCustomerAdvancePayment').attr('src',url);
	$('#reportCustomerAdvancePayment').load();
}

function appendAll() {
	$("#arCustomerAccountId").empty();
	$("#arCustomerAccountId").append("<option selected='selected'  value=-1>ALL</option>");
}

function getCommonParam() {
	var companyId = $("#companyId").val();
	var dateFrom = document.getElementById("dateFrom").value;
	var dateTo = document.getElementById("dateTo").value;
	var arCustomerId = $("#arCustomerId").val();
	var arCustomerAccountId = $("#arCustomerAccountId").val();
	var statusId = $("#statusId").val();
	var formatType = $("#formatType").val();
	var divisionId = $("#divisionId").val();

	if(arCustomerId == "") {
		// Set the value to -1
		arCustomerId = -1;
	}

	return "?companyId=" + companyId  + "&arCustomerId=" + arCustomerId + "&arCustomerAccountId=" + arCustomerAccountId
		+ "&dateFrom=" + dateFrom + "&dateTo=" + dateTo+ "&statusId=" + statusId + "&formatType=" + formatType
		+ "&divisionId="+ divisionId;
}


function selectOnChange() {
	clearCustomerAndAcct();
	$("#customerName").val("");
	filterArcustomerAccount();
}
</script>
</head>
<body>
	<table>

		<tr>
			<td class="title2">Company</td>
			<td><select id="companyId" class="frmSelectClass" onchange="selectOnChange();">
					<option selected='selected' value=-1>Please select a company</option>
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanCompanyError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Division</td>
			<td><select id="divisionId" class="frmSelectClass" onchange="selectOnChange();">
					<option selected='selected' value=-1>All</option>
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Customer</td>
			<td>
				<input id="customerName" class="input" onkeydown="showArCustomers();" onkeyup="showArCustomers();" onblur="getArCustomer();">
				<input type="hidden" id="arCustomerId">
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanCustomerError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Customer Account</td>
			<td><select id="arCustomerAccountId" class="frmSelectClass"></select></td>
		</tr>
		<tr>
			<td class="title2">Date: </td>
			<td class="tdDateFilter">
				<input type="text" id="dateFrom" maxlength="10" class="dateClass2" value="${currentDate}" 
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
							style="float: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="dateTo" maxlength="10" class="dateClass2" value="${currentDate}" 
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
							style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanDateError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Status</td>
			<td><select id="statusId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="status" items="${statuses}">
						<option value="${status.id}">${status.description}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Format:</td>
			<td class="value"><select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select></td>
		</tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate"
				onclick="generateCAPRegReport()" /></td>
		</tr>
	</table>
	<div>
		<iframe id="reportCustomerAdvancePayment"></iframe>
	</div>
	<hr class="thin2">
</body>
</html>