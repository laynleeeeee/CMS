<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Account Balances Report.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/ZeroClipboard.js"></script>
<style type="text/css" title="currentStyle" >
	@import "${pageContext.request.contextPath}/css/jquery/demo_page.css";
	@import "${pageContext.request.contextPath}/css/jquery/demo_table.css";
	@import "${pageContext.request.contextPath}/media/css/TableTools.css";
</style>
<script type="text/javascript">
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#asOfDate").val(newDate);
});

function getCommonParam () {
	var companyId = $("#selectCompanyId option:selected").val();
	var divisionId = $("#divisionId option:selected").val();
	var strDate = $("#asOfDate").val();
	var formatType = $("#formatType option:selected").val();
	if(companyId == null)
		return "?companyId="+companyId+"&strAsOfDate="+strDate+"&divisionId="+divisionId+"&formatType="+formatType;
	if ($.trim(strDate) == "") {
		$("#errorDate").text("As of date is required.");
		return "";
	} else {
		evalDate('asOfDate', true);
		return "?companyId="+companyId+"&strAsOfDate="+strDate+"&divisionId="+divisionId+"&formatType="+formatType;
	}
	return "";
}
function generateAccountBalances(){
	if(getCommonParam() != "") {
		$("#errorDate").text("");
		var url = contextPath + "/accountBalancesRpt/generateAcctBalancesReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
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
		<td class="title2">Division </td>
		<td>
			<select id="divisionId" class="frmSelectClass">
			<option selected='selected' value=-1>ALL</option>
			<c:forEach var="division" items="${divisions}">
				<option value="${division.id}">${division.name}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">As of Date </td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2">
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
		<td class="value"><select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
		</select></td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateAccountBalances()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>