<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: Summary List of Sales and Purchases report filter page
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
	var year = Number($("#year").val());
	var month = $("#month").val();
	var taxType = $("#taxType").val();
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&year="
			+year+"&month="+month+"&taxType="+taxType+"&formatType="+formatType;
}

function generate() {
	var isValidFilters = true;
	var month = $("#month").val();
	var taxType = $("#taxType").val();
	var formatType = $("#formatType").val();
	var path = "generateDAT";
	if(formatType == "dat") {
		path = "generateDAT";
	} else {
		path = "generate";
	}
	if (month == -1) {
		$("#spanMonthError").text("Month is required.");
		isValidFilters = false;
	} else if (taxType == -1) {
		$("#spanTaxTypeError").text("Tax type is required.");
		isValidFilters = false;
	}
	if (isValidFilters) {
		$("#spanMonthError").text("");
		$("#spanTaxTypeError").text("");
		var url = contextPath + "/summaryLSPReport/"+path+getCommonParam();
	}
	$('#iFrame').attr('src',url);
	$('#iFrame').load();

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
		<td></td>
		<td>
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
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
		<td class="title2">Year </td>
		<td>
			<select id="year" class="frmSelectClass"  style="width:150px">
				<c:forEach var="years" items="${years}">
					<c:choose>
						<c:when test="${years eq currentYear}">
							<option value="${years}" selected="selected" >${years}</option>
						</c:when>
						<c:otherwise>
							<option value="${years}">${years}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>	
	<tr>
		<td class="title2">Month</td>
		<td>
			<select id="month" class="frmSelectClass" style="width:150px">
				<c:forEach var="mm" items="${months}">
					<option value="${mm.month}">${mm.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="1"><span id="spanMonthError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Tax Type</td>
		<td>
			<select id="taxType" class="frmSelectClass">
			<option selected='selected' value=-1></option>
				<option value="0">Sales</option>
				<option value="1">Purchases</option>
				<option value="2">Importation</option>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="1"><span id="spanTaxTypeError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Format</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
					<option value="dat">DAT</option>
			</select>
		</td>
	</tr>
	<tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generate();"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>