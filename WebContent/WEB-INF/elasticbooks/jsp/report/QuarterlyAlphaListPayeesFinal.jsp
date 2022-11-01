<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Quarterly Alpha List of Payees Final.
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
	var quarter = $("#quarter").val();
	var formatType = $("#formatType").val();
	if(companyId == null) {
		return "?companyId=-1"+"&formatType="+formatType;
	}
	return "?companyId="+companyId+"&divisionId="+divisionId+"&quarter="+quarter+"&formatType="+formatType;
}

function searchQuarterlySummary() {
		var url = contextPath + "/quarterlyAlphaListPayeesFinal/generatePDF"+getCommonParam();
		$('#reportQuarterly').attr('src',url);

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
		<td class="title2">Quarter </td>
		<td>
			<select id="quarter" class="frmSelectClass">
				<c:forEach var="q" items="${quarters}" varStatus="status">
					<option value="${status.index}">${q}</option>
				</c:forEach>
			</select>
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
		<td colspan="3"><input type="button" value="Generate" onclick="searchQuarterlySummary()"/></td>
	</tr>
</table>
<div>
 <iframe id="reportQuarterly"></iframe>
</div>
<hr class="thin2">
</body>
</html>