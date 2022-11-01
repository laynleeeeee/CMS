<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Bank Reconciliation Report.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<title>Insert title here</title>
<style type="text/css">
input.numeric {
	border: 1px solid gray;
	text-align: right;
	padding: 3px;
	width: 170px;
}
</style>
<script type="text/javascript">
$(document).ready (function () {
	filterBankAccts();
});

$(function () {
	$("#bankBalance").bind("keyup keydown", function() {
		checkAndSetDecimal("bankBalance");
	});
});

function filterBankAccts() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath+"/getBankAccounts/bySupplierAcc?companyId="+companyId+(divisionId > 0 ? "&divisionId="+divisionId : "");
	$("#bankAcctId").empty();
	var optionParser = {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	loadPopulate (uri, false, null, "bankAcctId", optionParser, null);
}

function generateReport() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var bankAcctId = $("#bankAcctId").val();
	var asOfDate = $.trim($("#asOfDate").val());
	var bankDate = $.trim($("#bankDate").val());
	var amount = $("#bankBalance").val();
	var uri = "";
	if (companyId < 0) {
		$("#companyError").text("Company is required.");
	} else if (bankAcctId == null) {
		$("#bankAcctError").text("Bank account is required.");
	} else if (amount == "") {
		$("#bankBalanceError").text("Bank balance is required.");
	} else if (asOfDate == "" || bankDate == "") {
		$("#dateError").text("As of date and/or bank date are required.");
	} else {
		$("#companyError").text("");
		$("#bankAcctError").text("");
		$("#bankBalanceError").text("");
		$("#dateError").text("");
		uri = contextPath+"/bankReconPDF?companyId="+companyId+"&bankAcctId="+bankAcctId+"&amount="+amount
			+"&asOfDate="+asOfDate+"&bankDate="+bankDate+"&divisionId="+divisionId;
	}
	$("#iFrame").attr('src', uri);
	$("#iFrame").load();
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company</td>
		<td><select id="companyId" class="frmSelectClass">
			<c:forEach var="company" items="${companies}">
				<option value="${company.id}">${company.name}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td><span id="companyError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" class="frmSelectClass" onchange="filterBankAccts();">
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Bank Account</td>
		<td><select id="bankAcctId" class="frmSelectClass"></select></td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td><span id="bankAcctError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">As of Date</td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);" value="${currentDate}">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('asOfDate')" style="cursor:pointer"
				style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Bank Balance</td>
		<td><input type="text" id="bankBalance" class="numeric"></td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td><span id="bankBalanceError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Bank Date</td>
		<td class="tdDateFilter">
			<input type="text" id="bankDate" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);" value="${currentDate}">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('bankDate')" style="cursor:pointer"
				style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td><span id="dateError" class="error"></span></td>
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