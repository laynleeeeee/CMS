<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: Retention costs report filter/generator jsp page
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
	var customerId = $("#customerId").val();
	var customerAcctId = $("#customerAcctId").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var asOfDate = $.trim($("#asOfDate").val());
	var formatType = $("#formatType").val();
	var isValidFilter = true;
	if (companyId < 0) {
		$("#companyError").text("Company is required.");
		isValidFilter = false;
	}
	//Delivery date
	if(asOfDate == "" && (dateFrom == "" || dateTo == "")) {
		$("#dateError").text("Either estimated delivery date or as of date is required.");
		isValidFilter = false;
	}
	if(asOfDate != "" && (dateFrom != "" || dateTo != "")) {
		$("#dateError").text("Either estimated delivery date or as of date should be used as date filter.");
		isValidFilter = false;
	}

	if(dateFrom != "" && dateTo != "" && new Date(dateFrom) > new Date(dateTo)) {
		$("#deliveryDateErr").text("Invalid Date range.");
		isValidFilter = false;
	}
	//Customer
	if($("#spanCustomerError").text().trim() != "") {
		isValidFilter = false;
	}
	var url = "";
	if (isValidFilter) {
		clearSpanMsg();
		url = contextPath+"/retentionCostRprt/generatePDF?companyId="+companyId
				+"&divisionId="+divisionId+"&customerId="+customerId+"&customerAcctId="+customerAcctId
				+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&asOfDate="+asOfDate+"&formatType="+formatType;
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}

function companyDivOnChange() {
	$("#iFrame").attr('src', "");
	clearCustomer();
	clearSpanMsg();
}

function clearCustomer() {
	$("#customerId").val();
	$("#customerName").val("");
	filterCustomerAccts();
}

function clearSpanMsg() {
	$("#companyError").text("");
	$("#dateError").text("");
	$("#deliveryDateErr").text("");
	$("#spanCustomerError").text("");
	$("#dateError").text("");
}

function showCustomers() {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $("#customerName").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)
			+"&companyId="+companyId;
	var divisionId = $("#divisionId").val();
	if (divisionId != "" && divisionId != "undefined" && divisionId != -1) {
		uri += "&divisionId="+divisionId;
	}
	$("#customerName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#customerId").val(ui.item.id);
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

function getCustomer() {
	var companyId = $("#companyId").val();
	var customerName = $("#customerName").val();
	var divisionId = $("#divisionId").val();
	if (customerName != "") {
		$("#spanCustomerError").text("");
		var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&isExact=true"+"&companyId="+companyId;
		if (divisionId != "" && divisionId != "undefined" && divisionId != -1) {
			uri += "&divisionId="+divisionId;
		}
		$.ajax({
			url: uri,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					var customerId = customer[0].id;
					$("#customerId").val(customerId);
					$("#customerName").val(customer[0].name);
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#customerId").val("");
					$("#arCustomerAcctId").empty();
				}
				filterCustomerAccts();
			},
			dataType: "json"
		});
	} else {
		$("#hdnArCustomerId").val("");
		$("#arCustomerIdError").text("");
		$("#customerTypeId").empty();
		$("#arCustomerAcctId").empty();
	}
}

function filterCustomerAccts() {
	$("#customerAcctId").empty();
	selectedCompanyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var selectedCustomerId = $("#customerId").val();
	var uri = contextPath+"/getArCustomerAccounts?arCustomerId="+selectedCustomerId+"&companyId="+selectedCompanyId
				+(divisionId == -1 ? "" : "&divisionId="+divisionId);;
	var optionParser = {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	loadPopulate (uri, true, null, "customerAcctId", optionParser, null);
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
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer</td>
		<td>
			<input id="customerName" class="input" onkeypress="showCustomers();" onblur="getCustomer();">
			<input type="hidden" id="customerId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCustomerError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer Account</td>
		<td>
			<select id="customerAcctId" class="frmSelectClass">
				<option selected='selected' value="-1">ALL</option>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCustomerAcctError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Estimated Delivery Date</td>
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
		<td><span id="deliveryDateErr" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">As of</td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('asOfDate')" style="cursor:pointer"
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>