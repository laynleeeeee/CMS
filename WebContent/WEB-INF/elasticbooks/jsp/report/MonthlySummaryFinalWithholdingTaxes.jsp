<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Monthly Summary of Final Withholding Taxes.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
$(document).ready(function() {
});
function getCommonParam () {
	var companyId = $("#companyId option:selected").val();
	var divisionId = $("#divisionId").val();
	var year = $("#year").val();
	var month = $("#month").val();
	var formatType = $("#formatType").val();
	if(companyId == null) {
		return "?companyId=-1"+"&formatType="+formatType;
	}
	return "?companyId="+companyId+"&divisionId="+divisionId+"&year="+year+"&month="+month+"&formatType="+formatType;
}

function searchMonthlySummary() {
	var isValidFilters = true;
	var month = $("#month").val();
	if (month == -1) {
		$("#spanMonthError").text("Month is required.");
		isValidFilters = false;
	}

	if (isValidFilters) {
		$("#spanMonthError").text("");
		var url = contextPath + "/summaryWithholdingTaxes/generateMonthlyFinal" + getCommonParam();
	}
	$('#reportMonthlySummaryFinal').attr('src',url);
	$('#reportMonthlySummaryFinal').load();

}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
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
		<td class="title2">Year </td>
		<td>
			<select id="year" class="frmSelectClass" style="width: 150px">
				<c:forEach var="m" items="${years}">
					<c:choose>
						<c:when test="${m eq defaultYear}">
							<option value="${m}" selected="selected" >${m}</option>
						</c:when>
						<c:otherwise>
							<option value="${m}">${m}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Month </td>
		<td>
			<select id="month" class="frmSelectClass" style="width: 150px">
				<c:forEach var="m" items="${months}" varStatus="status">
					<option value="${status.index}">${m}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="1"><span id="spanMonthError" class="error"></span></td>
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
		<td colspan="3"><input type="button" value="Generate" onclick="searchMonthlySummary()"/></td>
	</tr>
</table>
<div>
 <iframe id="reportMonthlySummaryFinal"></iframe>
</div>
<hr class="thin2">
</body>
</html>