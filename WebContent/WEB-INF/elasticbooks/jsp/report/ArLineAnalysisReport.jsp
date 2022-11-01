<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: AR Line Analysis Report.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/commonUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/ZeroClipboard.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
var INVALID_UOM = "Invalid Unit Measurement.";
var INVALID_CUSTOMER = "Invalid Customer name.";
var INVALID_AR_LINE = "Invalid AR Line.";

$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#transactionDateFrom").val(newDate);
	$("#transactionDateTo").val(newDate);
	$("#glDateFrom").val(newDate);
	$("#glDateTo").val(newDate);
});

function getCommonParam () {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var source = $("#sourceId").val();
	var serviceId = $("#serviceId").val();
	var unitOfMeasureId =  $("#uOfMeasureId").val();
	var transactionDateFrom = $("#transactionDateFrom").val();
	var transactionDateTo = $("#transactionDateTo").val();
	var glDateFrom = $("#glDateFrom").val();
	var glDateTo = $("#glDateTo").val();
	var customerId = $("#customerId").val();
	var customerAcctId = $("#customerAcctId option:selected").val();
	var formatType = $("#formatType").val();

	var param = "?companyId=" + companyId + "&sourceId=" + source +"&serviceId=" + serviceId+ "&divisionId=" + divisionId
				+ "&customerId=" + customerId + "&customerAcctId=" + customerAcctId;


	return param + "&transactionDateFrom=" + transactionDateFrom + "&transactionDateTo="
		+ transactionDateTo	+ "&glDateFrom=" + glDateFrom + "&glDateTo=" + glDateTo	+ "&formatType=" + formatType;
}

function searchArLineAnalysisReport () {
	clearErrorMessages();
	if ($("#companyId option:selected").val() == -1)
		$("#spanCompanyError").text("Company is required.");
	else if($.trim($("#serviceName").val()) == "" || $("#serviceId").val() == "")
		$("#spanServiceError").text("Ar Line is required.");
	else {
		var url = contextPath + "/arLineAnalysisReport/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}

function printArLineAnalysisReport () {
	var printContents = $("#printDiv").clone(true);
	var myWindow = window.open("", "popup","width=885,height=600,scrollbars=yes,resizable=yes," +
			"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
	var doc = myWindow.document;
	doc.open();
	generate(printContents);
	$(printContents).find("#tblArLineAnalysisReport_length").hide();
	$(printContents).find("#tblArLineAnalysisReport_paginate").hide();
	doc.write($(printContents).html());
	doc.close();
}

function clearErrorMessages() {
	$("#spanCompanyError").text("");
	$("#spanServiceError").text("");
}

function showServiceSettings() {
	if($("#companyId").val() == -1){
		$("#spanCompanyError").text("Company is required.");
	} else{
		clearErrorMessages();
		var divisionId = $("#divisionId").val();
		var customerAcctId =  $("#customerAcctId").val();
		var companyId = $("#companyId").val();
		var serviceName = processSearchName($.trim($("#serviceName").val()));
		var listUri = contextPath + "/getServices/byDivision?name="+serviceName+(divisionId == -1 ? "" : "&divisionId="+divisionId)
				+(customerAcctId == -1 ? "" : "&customerAcctId="+customerAcctId);
		var getServiceSetupUri = contextPath + "/getServices/byDivision?name="+serviceName+(divisionId == -1 ? "" : "&divisionId="+divisionId)
				+(customerAcctId == -1 ? "" : "&customerAcctId="+customerAcctId);
		loadACList("serviceName", "serviceId", listUri, getServiceSetupUri, "name", "name");
	}
}

function getServiceSettings () {
	var companyId = $("#companyId").val();
	var serviceName = $("#serviceName").val();
	var divisionId = $("#divisionId").val();
	var customerAcctId =  $("#customerAcctId").val();
	if (serviceName != "") {
		$("#spanServiceError").text("");
		var uri = contextPath + "/getServices/byDivision?name="+serviceName+(divisionId == -1 ? "" : "&divisionId="+divisionId)
				+(customerAcctId == -1 ? "" : "&customerAcctId="+customerAcctId);
		$.ajax({
			url: uri,
			success : function(service) {
				if (service != null && service[0] != undefined) {
					var serviceId = service[0].id;
					$("#serviceId").val(serviceId);
					$("#serviceName").val(service[0].name);
				} else {
					$("#spanServiceError").text("Invalid Ar Line.");
					$("#serviceId").val("");
				}
			},
			dataType: "json"
		});
}
}
function filterCustomerAccts() {
	$("#customerAcctId").empty();
	if($.trim($("#customerName").val()) == "") {
		$("#customerId").val("") ;
		clearAndAddEmpty();
	} else {
		selectedCompanyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var selectedCustomerId = $("#customerId").val();
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
		postHandler = {
				doPost: function(data) {
					// This is to remove any duplication.
					var found = [];
					$("#customerAcctId option").each(function() {
						if($.inArray(this.value, found) != -1)
							$(this).remove();
						found.push("ALL");
						found.push(this.value);
					});
				}
		};
		loadPopulate (uri, true, null, "customerAcctId", optionParser, postHandler);
	}
}

function clearAndAddEmpty () {
	$("#customerAcctId").empty();
	var option = "<option selected='selected' value='-1'>ALL</option>";
	$("#customerAcctId").append(option);
}

function companyOnChange() {
	$("#divisionId").val("");
	clearSearchFilters();
	clearAndAddEmpty();
}

function clearSearchFilters() {
	$("#customerAcctId").val("");
	$("#customerId").val("");
	$("#customerName").val("");
	$("#serviceId").val("");
	$("#serviceName").val("");
}

function divisionOnChange() {
	clearSearchFilters();
	filterCustomerAccts();
}

function customerOnChange() {
	$("#customerAcctId").val("");
	clearAndAddEmpty();
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
</script>
</head>
<body>
<table>
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
		<td></td>
		<td colspan="2">
			<span id="spanCompanyError" class="error" style="margin-left: 12px;"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" class="frmSelectClass" onchange="divisionOnChange();">
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Source</td>
		<td>
			<select id="sourceId" class="frmSelectClass">
				<option selected='selected' value="-1">ALL</option>
				<c:forEach var="source" items="${sources}">
					<option value="${source.value}">${source.description}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Ar Line</td>
		<td>
			<input id="serviceName" onkeypress="showServiceSettings();" onblur="getServiceSettings();"  class="input">
			<input type="hidden" id="serviceId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanServiceError" class="error" style="margin-left: 12px;"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Transaction/Receipt Date </td>
		<td class="tdDateFilter">
			<input type="text" id="transactionDateFrom" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('transactionDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="transactionDateTo" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('transactionDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">GL/Maturity Date</td>
		<td class="tdDateFilter">
			<input type="text" id="glDateFrom" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('glDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="glDateTo" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('glDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer</td>
		<td>
			<input id="customerName" class="input" onkeypress="showCustomers();" onblur="getCustomer();"onchange="customerOnChange();">
			<input type="hidden" id="customerId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCustomerError" class="error" style="margin-left: 12px;"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer Account</td>
		<td>
			<select id="customerAcctId" class="frmSelectClass"></select>
		</td>
	</tr>
	<tr>
		<td class="title2">Format:</td>
		<td class="value"><select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
		</select></td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchArLineAnalysisReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>