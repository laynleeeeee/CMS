<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: Monthly cash flow report report filter page
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
function getCommonParam() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var currentMonth = $("#currentMonth").val();
	var prevMonths = $("#prevMonth").val();
	var formatType = $("#formatType").val();
	var typeId = $("#typeId").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&currentMonth="+currentMonth
			+"&prevMonths="+prevMonths+"&formatType="+formatType;
}

function generateMonthlyCashflowReport() {
	var url = contextPath+"/monthlyCashFlowReport/generate"+getCommonParam();
	$('#ifrMonthlyCashflow').attr('src',url);
	$('#ifrMonthlyCashflow').load();
}

function companyDivOnChange() {
	$("#ifrMonthlyCashflow").attr('src', "");
}
</script>
</head>
<body>
<table>

	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" onchange="companyDivOnChange();" class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" onchange="companyDivOnChange();" class="frmSelectClass">
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Current Month</td>
		<td>
			<select id="currentMonth" onchange="companyDivOnChange();" class="frmSelectClass">
				<c:forEach var="m" items="${months}" varStatus="status">
					<c:choose>
						<c:when test="${status.index eq defaultMonth}">
							<option value="${status.index}" selected="selected" >${m}</option>
						</c:when>
						<c:otherwise>
							<option value="${status.index}">${m}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Previous Month(s)</td>
		<td>
			<select id="prevMonth" onchange="companyDivOnChange();" class="frmSelectClass">
				<c:forEach begin="1" end="11" varStatus="status">
					<option value="${status.index}">${status.index}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Format</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select>
		</td>
	</tr>
	<tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateMonthlyCashflowReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="ifrMonthlyCashflow"></iframe>
</div>
</body>
</html>