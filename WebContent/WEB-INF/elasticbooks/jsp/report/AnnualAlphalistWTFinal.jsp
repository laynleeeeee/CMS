<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Annual Alphalist Withholding taxes Final main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript">
$(document).ready(function() {
});

function generateReport() {
	var formatType = $("#formatType").val();
	if(formatType=="dat"){
		url = contextPath + "/annualAlphalistWTFinal/generateDAT"+getCommonParam(false);
	} else {
		url = contextPath + "/annualAlphalistWTFinal/generatePDF"+getCommonParam(true);
	}
	$('#reportAnnual').attr('src',url);
	$('#reportAnnual').load();
}

function getCommonParam(excludeFormatType) {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var year = Number($("#year").val());
	console.log(year);
	var param = "?companyId=" + companyId + "&divisionId="+ divisionId + "&year=" + year;

	if (excludeFormatType == true) {
		var formatType = $("#formatType").val();
		param += "&formatType=" + formatType;
	}
	return param
}

</script>
</head>
<body>
	<table>

		<tr>
			<td class="title2">Company</td>
			<td><select id="companyId" class="frmSelectClass">
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Division</td>
			<td><select id="divisionId" class="frmSelectClass">
					<option selected='selected' value=-1>All</option>
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.name}</option>
					</c:forEach>
			</select></td>
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
			<td class="title2">Format:</td>
			<td class="value"><select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
					<option value="dat">DAT</option>
			</select></td>
		</tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate"
				onclick="generateReport()" /></td>
		</tr>
	</table>
	<div>
		<iframe id="reportAnnual"></iframe>
	</div>
	<hr class="thin2">
</body>
</html>