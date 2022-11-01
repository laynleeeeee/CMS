<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Income statement.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript">
function searchIncomeStatement () {
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var isValidDate = dateFrom != ""
		&& dateTo != "";
	if (!isValidDate) {
		$("#spanDateError").text("Complete and valid date range is required.");
	}
	if (isValidDate){
		$("#spanDateError").text("");
		var url = contextPath + "/incomeStatement/generatePDF" + getCommonParam();
		$('#reportIncomeStatement').attr('src',url);
		$('#reportIncomeStatement').load();
	}
}

function getCommonParam () {
	var companyId = $("#selectCompanyId").val();
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType="+formatType;
}
</script>
</head>
<body>
<!-- Table for search criteria -->
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
		<td class="title2">Date </td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2" onblur="evalDate('dateFrom')" >
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/><span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2" onblur="evalDate('dateTo')" >
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="1"><span id="spanDateError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Format:</td>
		<td class="value"><select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
		</select></td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchIncomeStatement();" /></td>
	</tr>
</table>
<div>
	<iframe id="reportIncomeStatement"></iframe>
</div>
<div id="divResultTable" style="margin-top: 20px;" >
</div>
</body>
</html>