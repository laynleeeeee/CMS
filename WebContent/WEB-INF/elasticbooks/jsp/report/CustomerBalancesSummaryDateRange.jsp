<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Customer Balances Summary Report with date range
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript">
function getCommonParam () {
	var companyId = $("#selectCompanyId").val() == null ? -1 : $("#selectCompanyId").val();
	var balOption = $("#balanceOption").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&balanceOption="+balOption
			+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType="+formatType;
}

function generateReport() {
	var companyId = $("#companyId").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var isGenerateRprt = true;
	if(companyId == -1){
		$("#errorCompany").text("Company is required.");
		isGenerateRprt = false;
	} else {
		$("#errorCompany").text("");
	}
	if($.trim(dateFrom) == "" && $.trim(dateTo) == "") {
		$("#errorDate").text("Date from and to are required.");
		isGenerateRprt = false;
	} else if($.trim(dateFrom) == "") {
		$("#errorDate").text("Date from are required.");
		isGenerateRprt = false;
	} else if($.trim(dateTo) == "") {
		$("#errorDate").text("Date to are required.");
		isGenerateRprt = false;
	} else if($.trim(dateFrom) > $.trim(dateTo)) {
		$("#errorDate").text("Date From should not be greater than the value of Date To.");
		isGenerateRprt = false;
	} else {
		$("#errorDate").text("");
	}

	if(isGenerateRprt) {
		$("#errorCompany").text("");
		$("#errorDate").text("");
		var url = contextPath + "/customerBalancesSummary/generateReport"+getCommonParam();
		$("#iFrame").attr('src', url);
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
		<td class="title2"></td>
		<td class="value">
			<span id="errorCompany" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Balance Option </td>
		<td>
			<select id="balanceOption" class="frmSelectClass">
				<option value=0>No Zero</option>
				<option value=-1>ALL</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">* Date </td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;"> To </span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value">
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
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport()"/></td>
	</tr>
</table>
<div>
 <iframe id="iFrame"></iframe>
</div>
<hr class="thin2">
</body>
</html>