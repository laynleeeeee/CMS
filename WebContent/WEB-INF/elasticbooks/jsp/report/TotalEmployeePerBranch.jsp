<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		 Description: Total Employee Per Branch Report.-->
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
	$("#asOfDate").val(newDate);
});

function getCommonParam () {
	var companyId = $("#selectCompanyId").val();
	var divisionId = $("#slctDivisionId").val();
	var selectStatus = $("#selectStatus").val();
	var asOfDate = $("#asOfDate").val();
	var formatType = $("#formatType").val();
	if($.trim(asOfDate) == "") {
		$("#errorDate").text("As of Date is required.");
		return "";
	} else if(asOfDate != "") {
		$("#errorDate").text("");
		evalDate("asOfDate", true);
		return "?companyId="+companyId+"&divisionId="+divisionId+"&strStatus="+selectStatus+"&asOfDate="+
		asOfDate+"&formatType=" + formatType;
	}
	return "";
}

function generateTotalEmployee () {
	if ($("#selectCompanyId").val() ==  "null" || $("#selectCompanyId").val() ==  null) {
		$("#spanCompanyError").text("Please select a Company/Branch.");
	} else {
		$("#spanCompanyError").text("");
	}
	if(getCommonParam() != "" && $("#spanCompanyError").text() == "") {
		var url = contextPath + "/employeeTotalPerBranch/generateReport" + getCommonParam();
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
		<td class="title2">Status</td>
		<td><select id="selectStatus" class="frmSelectClass">
			<c:forEach var="status" items="${status}">
					<option>${status}</option>
			</c:forEach>
		</select>
	</tr>
	<tr>
		<td class="title2">As of Date </td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2" onblur="evalDate('asOfDate')" >
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('asOfDate')" style="cursor:pointer"
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
		<td class="title2">Format:</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select>
	</td>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateTotalEmployee()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>