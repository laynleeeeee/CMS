<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: Loan balances summary report filter/generator jsp page
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
	var asOfDate = parseServerDate(new Date);
	$("#asOfDate").val(asOfDate);
});

function generateReport() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var balanceOption = $("#balanceOption").val();
	// Add balance option
	var asOfDate = $.trim($("#asOfDate").val());
	var formatType = $("#formatType").val();
	var isValidFilter = true;
	if (companyId < 0) {
		$("#companyError").text("Company is required.");
		isValidFilter = false;
	}
	if (asOfDate == "") {
		$("#dateError").text("As of date is required.");
		isValidFilter = false;
	}
	var url = "";
	if (isValidFilter) {
		clearSpanMsg();
		url = contextPath+"/loanBalancesSummary/generatePDF?companyId="+companyId
				+"&divisionId="+divisionId+"&balanceOption="+balanceOption
				+"&asOfDate="+asOfDate+"&formatType="+formatType;
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}

function companyDivOnChange() {
	$("#iFrame").attr('src', "");
	clearSpanMsg();
}

function clearSpanMsg() {
	$("#companyError").text("");
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
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" onchange="companyDivOnChange();" class="frmSelectClass">
				<option value='-1'>ALL</option>
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Balance Option</td>
		<td>
			<select id="balanceOption" class="frmSelectClass">
				<option value=0>ALL</option>
				<option value=1 selected="selected">NO ZERO</option>
				<option value=2>NO NEGATIVE</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">As of Date</td>
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