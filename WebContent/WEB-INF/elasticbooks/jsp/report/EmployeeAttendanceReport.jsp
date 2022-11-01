<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Employee Attendance Report search page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function generateEmpAttendanceReport() {
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	if ($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Please select a Company/Branch.");
	} else {
		$("#spanCompanyError").text("");
	}

	if ($("#selectDivisionId").val() == null) {
		$("#spanDivisionError").text("Division/Department is required.");
	} else {
		$("#spanDivisionError").text("");
	}

	if (dateFrom == null || dateTo == null || dateFrom == "" || dateTo == "") {
		$("#spanDateError").text("Please enter complete date range.");
	} else if(new Date(dateFrom) > new Date(dateTo)) {
		$("#spanDateError").text("Invalid date range.");
	} else {
		$("#spanDateError").text("");
	}

	if ($("#spanCompanyError").text() == "" && $("#spanDateError").text() == "" && 
			$("#spanDivisionError").text() == "") {
		var url = contextPath + "/employeeAttendanceReport/generate" + getCommonParam();
		$('#divEmpAttendanceReport').attr('src',url);
		$('#divEmpAttendanceReport').load();
	} else {
		$('#divEmpAttendanceReport').attr('src',"");
		$('#divEmpAttendanceReport').load();
	}
}

function getCommonParam() {
	var companyId = $("#companyId option:selected").val();
	var divisionId = $("#selectDivisionId option:selected").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	return "?companyId="+companyId+"&divisionId="+divisionId
			+"&dateFrom="+dateFrom+"&dateTo="+dateTo;
}
</script>
</head>
<body>
	<table>
		<tr>
			<td class="title2">Company/Branch </td>
			<td>
				<select id="companyId" class="frmSelectClass">
					<option selected='selected' value=-1>Please select a Company/Branch</option>
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<span id="spanCompanyError" class="error"></span>
			</td>
		</tr>
		<tr>
			<td  class="title2">Division/Department</td>
			<td>
				<select id="selectDivisionId" class="frmSelectClass">
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.number} - ${division.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td></td>
			<td><span id="spanDivisionError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Date</td>
			<td class="tdDateFilter">
				<input type="text" id="dateFrom" maxlength="10" class="dateClass2"
						onblur="evalDate('dateFrom')" value="${currentDate}">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
							style="float: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="dateTo" maxlength="10" class="dateClass2"
						onblur="evalDate('dateTo')" value="${currentDate}">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
							style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<span id="spanDateError" class="error"></span>
			</td>
		</tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate" onclick="generateEmpAttendanceReport()"/></td>
		</tr>
	</table>
	<div>
		<iframe id="divEmpAttendanceReport"></iframe>
	</div>
</body>
</html>