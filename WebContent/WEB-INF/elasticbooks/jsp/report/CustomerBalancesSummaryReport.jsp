<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Customer Balances Summary Report main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#asOfDate").val(newDate);
	getArAccounts();
});

function getCommonParam () {
	var companyId = $("#selectCompanyId").val();
	var classificationId = encodeURIComponent($("#slctClassificationId option:selected").val());
	var divisionId = $("#selectDivisionId").val();
	var balOption = $("#balanceOption").val();
	var strDate = document.getElementById("asOfDate").value;
	var currencyId = $("#currencyId").val();
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&balanceOption="+balOption+"&currencyId="+currencyId
			+"&asOfDate="+strDate+"&formatType="+formatType+"&classificationId="+classificationId;
}

function getArAccounts() {
	var companyId = $("#selectCompanyId").val();
	var divisionId = $("#selectDivisionId").val();
	if(companyId != "") {
		var uri = contextPath+"/customerBalancesSummary/getArAccounts?companyId="+companyId+"&divisionId="+divisionId;
		$("#slctClassificationId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject;
			},

			getLabel: function (rowObject) {
				return rowObject;
			}
		};
		loadPopulate (uri, true, null, "slctClassificationId", optionParser, null);
	}
}

function searchCustomerBalances() {
	if($.trim($("#asOfDate").val()) == "" || $.trim($("#asOfDate").val()) == "") {
		$("#spanDateError").text("As of date is required.");
	}else {
		var url = contextPath + "/customerBalancesSummary/generatePDF"+getCommonParam();
		$('#reportCustomerBalances').attr('src',url);
		$('#reportCustomerBalances').load();
	}
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="selectCompanyId" class="frmSelectClass" onchange="getArAccounts();">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
	<tr>
		<td class="title2">Division </td>
		<td>
			<select id="selectDivisionId" class="frmSelectClass" onchange="getArAccounts();">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Classification </td>
		<td>
			<select id="slctClassificationId" class="frmSelectClass">
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Balance Option </td>
		<td>
			<select id="balanceOption" class="frmSelectClass">
				<option value=-1 selected="selected">ALL</option>
				<option value=1>No Zero</option>
				<option value=2>No Negative</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">As of Date </td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('asOfDate')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDateError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Currency </td>
		<td>
			<select id="currencyId" class="frmSelectClass">
				<c:forEach var="currency" items="${currencies}">
					<option value="${currency.id}">${currency.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Format </td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select>
		</td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchCustomerBalances()"/></td>
	</tr>
</table>
<div>
 <iframe id="reportCustomerBalances"></iframe>
</div>
<hr class="thin2">
</body>
</html>