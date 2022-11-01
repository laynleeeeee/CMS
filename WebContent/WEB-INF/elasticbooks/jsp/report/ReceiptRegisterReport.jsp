<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- 

	Description: Receipt Register Report.
 -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/commonUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/retrieveArCustomer.js"></script>
<script type="text/javascript">
var selectedCompanyId = -1;
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#receiptDateFrom").val(newDate);
	$("#receiptDateTo").val(newDate);
	$("#maturityDateFrom").val(newDate);
	$("#maturityDateTo").val(newDate);
});

function getCommonParam () {
	var companyId = $("#companyId").val();
	var sourceId = $("#sourceId").val();
	var divisionId = $("#divisionId").val();
	var receiptTypeId = $("#receiptTypeId option:selected").val();
	var receiptMethodId = $("#receiptMethodId option:selected").val();
	var customerId = $("#customerId").val() == "" ? -1 : $("#arCustomerId").val();
	var customerAcctId = $("#customerAcctId option:selected").val();
	var receiptNo = processSearchName($("#receiptNo").val());
	var receiptDateFrom = $.trim($("#receiptDateFrom").val());
	var receiptDateTo = $.trim($("#receiptDateTo").val());
	var maturityDateFrom = $.trim($("#maturityDateFrom").val());
	var maturityDateTo = $.trim($("#maturityDateTo").val());
	var amountFrom = $.trim($("#amountFrom").val());
	var amountTo = $.trim($("#amountTo").val());
	var wfStatusId = $("#wfStatusId option:selected").val();
	var appliedStatusId = $("#appliedStatusId option:selected").val();
	var formatType = $("#formatType").val();

	return "?companyId="+companyId+"&divisionId="+divisionId+"&source="+sourceId+"&receiptTypeId="+receiptTypeId+"&receiptMethodId="+receiptMethodId
			+"&customerId="+customerId+"&customerAcctId="+customerAcctId+"&receiptNo="+receiptNo+"&receiptDateFrom="+receiptDateFrom
			+"&receiptDateTo="+receiptDateTo+"&maturityDateFrom="+maturityDateFrom+"&maturityDateTo="+maturityDateTo
			+"&amountFrom="+amountFrom+"&amountTo="+amountTo+"&wfStatusId="+wfStatusId+"&appliedStatusId="+appliedStatusId
			+"&formatType=" + formatType;
}

function clearCompanyError() {
	$("#spanCompanyError").text("");
}

function clearAndAddEmpty() {
	$("#customerAcctId").empty();
	var option = "<option selected='selected' value='-1'>ALL</option>";
	$("#customerAcctId").append(option);
}

function generateReceiptRegister () {
	clearCompanyError();
	$("#spanCustomerError").text("");
	if ($("#companyId option:selected").val() == -1) {
		$("#spanCompanyError").text("Company is required.");
	}

	if($("#arCustomerId").val() == "") {
		if($("#customerId").val() != "") {
			$("#spanCustomerError").text("Invalid customer");
		} else {
			$("#arCustomerId").val(-1);
		}
	}

	if($("#companyId option:selected").val() != -1 && $("#arCustomerId").val() != "") {
		var url = contextPath + "/receiptRegister/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}

$(function () {
	$("#amountFrom").live("keydown keyup", function () {
		checkAndSetDecimal("amountFrom");
	});

	$("#amountTo").live("keydown keyup", function () {
		checkAndSetDecimal("amountTo");
	});
});

function getArCustomers () {
	getCustomer ("customerId", "arCustomerId", "companyId",
			"spanCustomerError", "spanCompanyError", "true");
	filterCustomerAccts();
}

function showArCustomers () {
	showCustomers ("customerId", "arCustomerId", "companyId",
			"spanCustomerError", "spanCompanyError", "false");
}

function filterCustomerAccts() {
	$("#customerAcctId").empty();
	if($.trim($("#customerId").val()) == "") {
		$("#arCustomerId").val("");
		clearAndAddEmpty();
	}else{
		selectedCompanyId = $("#companyId").val();
		var selectedCustomerId = $("#arCustomerId").val();
		var uri = contextPath+"/getArCustomerAccounts?arCustomerId="+selectedCustomerId+"&companyId="+selectedCompanyId;
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
					found.push(this.value);
				});
			}
		};
		loadPopulate (uri, true, null, "customerAcctId", optionParser, postHandler);
	}
}

function loadReceiptMethods () {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();

	if(companyId != -1) {
		$("#receiptMethodId").empty();
		var uri = contextPath + "/getReceiptMethods/byDivision?companyId="+companyId+"&divisionId="+divisionId;
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, true, null, "receiptMethodId", optionParser, null);
	}
}

function companyOnChange() {
	$("#arCustomerId").val("");
	$("#customerId").val("");
	clearAndAddEmpty();
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" class="frmSelectClass" onchange="loadReceiptMethods();companyOnChange();">
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
			<select id="divisionId" class="frmSelectClass" onchange="loadReceiptMethods();companyOnChange();">
				<option selected='selected' value=-1>All</option>
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
				<option selected='selected' value="ALL">ALL</option>
				<c:forEach var="source" items="${sources}">
					<option value="${source}">${source}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Receipt Type</td>
		<td>
			<select id="receiptTypeId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="rt" items="${arReceiptTypes}">
					<option value="${rt.id}">${rt.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Receipt Method</td>
		<td>
			<select id="receiptMethodId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Receipt No.</td>
		<td>
			<input type="text" id="receiptNo" size="10%" class="dateClass2"/>
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
		<td></td>
		<td colspan="2">
			<span id="spanCustomerError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer Account</td>
		<td>
			<select id="customerAcctId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="customerAcct" items="${customerAccts}">
					<option value="${customerAcct.id}">${customerAcctt.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCustomerAcctError" class="error" style="margin-left: 12px;"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Receipt Date </td>
		<td class="tdDateFilter">
			<input type="text" id="receiptDateFrom" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('receiptDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="receiptDateTo" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('receiptDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanReceiptDateError" class="error" style="margin-left: 12px;"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Maturity Date </td>
		<td class="tdDateFilter">
			<input type="text" id="maturityDateFrom" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('maturityDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="maturityDateTo" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('maturityDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Amount </td>
		<td class="tdDateFilter">
			<input type="text" id="amountFrom" class="dateClass2">
			<span style="font-size: small;">To</span>
			<input type="text" id="amountTo" class="dateClass2">
		</td>
	</tr>
	<tr>
		<td class="title2">Status</td>
		<td><select id="wfStatusId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="status" items="${formStatuses}">
					<option value="${status.id}">${status.description}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Applied Status</td>
		<td><select id="appliedStatusId" class="frmSelectClass">
			<option selected='selected' value=-1>ALL</option>
			<option value=1>UNAPPLIED</option>
			<option value=2>PARTIALLY APPLIED</option>
			<option value=3>FULLY APPLIED</option>
		</select></td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanMaturityDateError" class="error" style="margin-left: 12px;"></span>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateReceiptRegister()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>