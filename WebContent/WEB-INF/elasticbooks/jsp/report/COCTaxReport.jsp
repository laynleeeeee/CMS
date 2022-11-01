<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Certificate of Creditable Tax Withheld at Source main page.
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
});

function searchReport() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var strDateFrom =  new Date($("#dateFrom").val());
	var strDateTo = new Date($("#dateTo").val());
	var formatType = $("#formatType").val();
	var hasFilterError = false;

	if($.trim($("#dateFrom").val()) == "" || $.trim($("#dateTo").val()) == "") {
		$('#cocTaxWithheldAtSrc').attr('src',"");
		$("#spanDateError").text("Date from and to are required fields.");
		return "";
	} else if (strDateFrom > strDateTo) {
		$('#cocTaxWithheldAtSrc').attr('src',"");
		$("#spanDateError").text("Invalid date range.");
		hasFilterError = true;
	}else if (strDateFrom == 'Invalid Date' || strDateTo == 'Invalid Date') {
		$('#strDateTo').attr('src',"");
		$("#spanDateError").text("Invalid date.");
		hasFilterError = true;
	}

	if (!hasFilterError) {
		var url = contextPath + "/cocTaxReport/generate?companyId="+companyId+"&divisionId="+divisionId
				+"&dateFrom="+$("#dateFrom").val()+"&dateTo="+$("#dateTo").val()+"&formatType="+formatType;
		$('#cocTaxWithheldAtSrc').attr('src',url);
		$('#cocTaxWithheldAtSrc').load();
		$("#spanDateError").text("");
	}
}

</script>
</head>
<body>
<table border=0>
	<tr>
		<td class="title2">Company</td>
		<td>
			<select id="companyId" class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
	<tr>
		<td class="title2">Division</td>
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
		<td></td>
		<td colspan="2">
			<span id="spanGPAMsg" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Date</td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>

			<span class="title2">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDateError" class="error" ></span>
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
		<td colspan="3"><input type="button" value="Generate" onclick="searchReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="cocTaxWithheldAtSrc"></iframe>
</div>
<hr class="thin2">
</body>
</html>