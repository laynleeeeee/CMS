<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Paid In Advance Register main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	appendAll();
});

function showCustomers () {
	if($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Company is required.");
		return;
	} else {
		var customerId = "arCustomerId";
		var customer = processSearchName($.trim($("#customerName").val()));
		var listUri = contextPath + "/getArCustomers/new?name="+customer+"&companyId="+$("#companyId").val();
		var getCustomerUri = contextPath + "/getArCustomers/new?name="+customer+"&companyId="+$("#companyId").val()+"&isExact=true";
		loadACList("customerName", customerId, listUri, getCustomerUri, "name", "name",
				function(arCustomer) {
					//Select
					$("#spanCustomerError").text("");
					if(arCustomer != "") {
						$("#"+customerId).val(arCustomer.id);
						filterArcustomerAccount();
					}
				}, function(arCustomer) {
					//Change
					$("#spanCustomerError").text("");
					if(arCustomer != null) {
						$("#"+customerId).val(arCustomer.id);
						filterArcustomerAccount();
					} else {
						//Remove values from customer account drop down list.
						$("#customerAcctId").empty();
					}
				}, function() {
					//Success
					$("#spanCustomerError").text("");
				}, function() {
					//Error
					$("#spanCustomerError").text("Invalid Customer.");
					$("#"+customerId).val("");
				}
		);
	}
}

function validateCustomer() {
	var customer = $.trim($("#customerName").val());
	if(customer == "") {
		appendAll();
	}
}

function filterArcustomerAccount() {
	var companyId = $("#companyId").val();
	var arCustomerId = $("#arCustomerId").val();
	var uri = contextPath + "/getArCustomerAccounts?arCustomerId="+arCustomerId+"&companyId="+companyId;
	$("#arCustomerAccountId").empty();
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

function generateCAPRegReport() {
	var isValidDate = $.trim($("#dateFrom").val()) != "" && $.trim($("#dateTo").val()) != "";
	if (!isValidDate) {
		$("#spanDateError").text("Date is required.");
	} else {
		$("#spanDateError").text("");
	}

	if($("#companyId").val() == -1){
		$("#spanCompanyError").text("Company is required.");
	} else {
		$("#spanCompanyError").text("");
	}
	if($("#companyId").val() != -1 && isValidDate){
		var url = contextPath + "/paidInAdvanceDeliveryRegister/generatePDF"+getCommonParam();
		$('#reportPaidInAdvDel').attr('src',url);
		$('#reportPaidInAdvDel').load();
	}
}

function appendAll() {
	$("#arCustomerId").val("");
	$("#customerName").val("");
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

	if(arCustomerId == "") {
		// Set the value to -1
		arCustomerId = -1;
	}
	return "?companyId=" + companyId  + "&arCustomerId=" + arCustomerId + "&arCustomerAccountId=" + arCustomerAccountId
		+ "&dateFrom=" + dateFrom + "&dateTo=" + dateTo+ "&statusId=" + statusId + "&formatType=" + formatType;
}
</script>
</head>
<body>
	<table>

		<tr>
			<td class="title2">Company</td>
			<td>
				<select id="companyId" class="frmSelectClass" onchange="appendAll();">
					<option selected='selected' value=-1>Please select a company</option>
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanCompanyError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Customer</td>
			<td>
				<input id="customerName" class="input" onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="validateCustomer();">
				<input type="hidden" id="arCustomerId">
			</td>
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
			<td>
				<select id="statusId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="stat" items="${statuses}">
						<option value="${stat.id}">${stat.description}</option>
					</c:forEach>
				</select>
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
			<td colspan="3"><input type="button" value="Generate"
				onclick="generateCAPRegReport()" /></td>
		</tr>
	</table>
	<div>
	<iframe id="reportPaidInAdvDel"></iframe>
</div>
	<hr class="thin2">
</body>
</html>