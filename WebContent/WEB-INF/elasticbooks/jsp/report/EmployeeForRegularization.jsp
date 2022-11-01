<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		 Description: Employee for Regularization Report.-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#renewalDate").val(newDate);
});

function isNan () {
	var daysB4Renewal = $("#txtDaysB4Renewal").val();
	if (isNaN(daysB4Renewal)) {
		$("#txtDaysB4Renewal").val("");
	}
}

function getCommonParam () {
	var companyId = $("#selectCompanyId").val();
	var divisionId = $("#slctDivisionId").val();
	var renewalDate = $("#renewalDate").val();
	var daysB4Renewal = $("#txtDaysB4Renewal").val();
	var formatType = $("#formatType").val();
	if ($("#selectCompanyId").val() ==  "null" || $("#selectCompanyId").val() ==  null) {
		$("#spanCompanyError").text("Please select a Company/Branch.");
	} else {
		$("#spanCompanyError").text("");
	}
	if($.trim(renewalDate) == "") {
		$("#errorDate").text("Days before renewal is required.");
		return "";
	} else {
		$("#errorDate").text("");
	}
	if($.trim($("#txtDaysB4Renewal").val()) == "") {
		$("#errorDays").text("Renewal date is required.");
	} else {
		$("#errorDays").text("");
	}
	var hasErrors = ($("#errorDate").text() != "") || ($("#errorDays").text() != "") || $("#spanCompanyError").text() != "";
	if(!hasErrors) {
		evalDate("renewalDate", true);
		return "?companyId="+companyId+"&divisionId="+divisionId+"&renewalDate="+
		renewalDate+"&daysBeforeRenewal="+daysB4Renewal+"&formatType=" + formatType;
	}
	return "";
}

function generatEmployeeForRegularization () {
	if(getCommonParam() != "") {
		var url = contextPath + "/employeeForRegularization/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}
</script>
<body>
<table>
	<tr>
		<td class="title2">Company/Branch </td>
		<td>
			<select id="selectCompanyId" class="frmSelectClass" onchange="loadDivisions();">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Division/Department </td>
		<td>
			<select id="slctDivisionId" class="frmSelectClass">
				<option value=-1>ALL</option>
				<c:forEach var="d" items="${divisions}">
					<option value="${d.id}">${d.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Renewal Date </td>
		<td class="tdDateFilter">
			<input type="text" id="renewalDate" maxlength="10" class="dateClass2" onblur="evalDate('renewalDate')" >
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('renewalDate')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="errorDate" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Days before renewal</td>
		<td class="value">
			<input type="text" id="txtDaysB4Renewal" class="dateClass2" value="0" onblur="isNan();"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="errorDays" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Format:</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select>
	</td>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generatEmployeeForRegularization()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>