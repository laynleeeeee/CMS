<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description: Employer-Employee Contribution Report
	 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebReport.css"media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/commonUtil.js"></script>
<style type="text/css">
.frmSmallSelectClass {
	border: 1px solid gray;
	padding: 4px;
	font-weight: bold;
	text-transform: uppercase;
	background-color: #0000;
	width: 173px;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	loadTimePeriodSchedules();
});

function loadTimePeriodSchedules() {
	var month = $("#slctMonth").val();
	var year = $("#slctYear").val();
	$("#slctTimePeriod").empty();
	var uri = contextPath + "/payroll/getTPSchedules?month="+month+"&year="+year;
	var optionParser = {
			getValue: function (rowObject){
				if (rowObject != null) {
					return rowObject["id"];
				}
			},

			getLabel: function (rowObject){
				if (rowObject != null)
					return rowObject["name"];
			}
	};
	loadPopulate (uri, true, null, "slctTimePeriod", optionParser, null);
}

function getCommonParam() {
	var companyId = $("#slctCompany").val();
	var month = $("#slctMonth").val();
	var year = $("#slctYear").val();
	var timePeriodSched = $("#slctTimePeriod").val();
	var format = $("#formatType").val();
	var divisonId = $("#slctDivision").val();
	return "?companyId="+companyId+"&month="+month+"&year="+year+"&timePeriodSchedId="+timePeriodSched
			+"&format="+format+"&divisionId="+divisonId+"&isFirstNameFirst=false";
}

function generateReport() {
	var invalidCompany = $("#slctCompany").val() == -1;
	$("#spanCompanyErrorMsg").text(invalidCompany ? "Company is required." : "");

	if(!invalidCompany) {
		$("#iFrame").attr('src', contextPath + "/erEeContributionRprt/generateReport"+getCommonParam());
		$("#iFrame").load();
	} else {
		$("#iFrame").attr('src', "");
	}
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">* Company</td>
		<td class="value">
			<select class="frmSelectClass" id="slctCompany">
				<option selected='selected' value=-1>Please select a company</option>
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value"><span id="spanCompanyErrorMsg" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">* Division</td>
		<td class="value">
			<select class="frmSelectClass" id="slctDivision">
				<option selected='selected' value=-1>ALL</option>
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.name}</option>
					</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">* Month/Year</td>
		<td class="value">
			<select class="frmSmallSelectClass" id="slctMonth" onchange="loadTimePeriodSchedules();">
				<option selected='selected' value=-1>ALL</option>
					<c:forEach var="mm" items="${months}">
						<option value="${mm.month}">${mm.name}</option>
					</c:forEach>
			</select>
			<select class="frmSmallSelectClass" id="slctYear" onchange="loadTimePeriodSchedules();">
					<c:forEach var="yy" items="${years}">
						<option value="${yy}">${yy}</option>
					</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Time Period</td>
		<td class="value">
			<select class="frmSelectClass" id="slctTimePeriod"></select>
		</td>
	</tr>
	<tr>
		<td class="title2">* Format</td>
		<td class="value"><select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
		</select></td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport()"/></td>
	</tr>
</table>
<hr class="thin2">
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>