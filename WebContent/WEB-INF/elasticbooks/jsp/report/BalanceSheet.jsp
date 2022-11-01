<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Balance Sheet Report.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#asOfDate").val(newDate);
});

function searchBalanceSheet () {
	var url = contextPath + "/balanceSheet/generatePdf"+getCommonParam();
	if ($.trim($("#asOfDate").val()) == "") {
		$("#spanDateError").text("As of date is required.");
	} else {
		$("#spanDateError").text("");
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}

function getCommonParam () {
	var companyId = $("#selectCompanyId").val();
	var asOfDate = $("#asOfDate").val();
	var formatType = $("#formatType").val();
	console.log("Format type: "+formatType);
	return "?companyId="+companyId+"&asOfDate="+asOfDate+"&formatType="+formatType;
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="selectCompanyId"  class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.companyNumber} - ${company.name}</option>
				</c:forEach>
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
		<td class="title2">Format:</td>
		<td class="value"><select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
		</select></td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchBalanceSheet();" /></td>
	</tr>
</table>
<div id="balanceSheetTable" style="margin-top: 20px;" >
</div>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>