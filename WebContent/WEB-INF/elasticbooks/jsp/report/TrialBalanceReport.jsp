<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Trial Balance Report.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/commonUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/ZeroClipboard.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/TableTools.js"></script>
<style type="text/css" title="currentStyle" >
	@import "${pageContext.request.contextPath}/css/jquery/demo_page.css";
	@import "${pageContext.request.contextPath}/css/jquery/demo_table.css";
	@import "${pageContext.request.contextPath}/media/css/TableTools.css";
</style>
<script type="text/javascript">
$(document).ready (function(){
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function getCommonParam () {
	var companyId = $("#selectCompanyId option:selected").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var divisionId = $("#divisionId option:selected").val();
	var formatType = $("#formatType").val();
	if(companyId == null || typeof companyId == "undefined")
		companyId = -1;
	return "?companyId="+companyId+"&divisionId="+divisionId+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType="+formatType;
}

function generateTrialBalances() {
	$("#errorDate").text("");
	var companyId = $("#selectCompanyId option:selected").val();
	var divisionId = $("#divisionId option:selected").val();
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var isValidFilters = true;
	if (companyId == null || typeof companyId == "undefined") {
		$("#errorCompany").text("Company is required.");
		isValidFilters = false;
	}
	if (dateFrom == "" || dateTo == "") {
		$("#errorDate").text("Invalid date range.");
		isValidFilters = false;
	}
	if (new Date(dateFrom) > new Date(dateTo)) {
		$("#errorDate").text("Invalid date range.");
		isValidFilters = false;
	}
	var url = "";
	if (isValidFilters) {
		$("#errorCompany").text("");
		$("#errorDate").text("");
		url = contextPath + "/trialBalance/generate" + getCommonParam();
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="selectCompanyId" class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="errorCompany" class="error"></span>
		</td>
	</tr>
	
	<tr>
		<td class="title2">Division </td>
		<td><select id="divisionId" class="frmSelectClass">
			<option selected='selected' value=-1>ALL</option>
			<c:forEach var="division" items="${divisions}">
				<option value="${division.id}">${division.name}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Date </td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2" onblur="evalDate('dateFrom')" >
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2"
				onblur="evalDate('dateTo');">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateTrialBalances()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>