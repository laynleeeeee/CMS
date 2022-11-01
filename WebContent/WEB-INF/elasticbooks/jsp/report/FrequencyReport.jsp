<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function generateFrequencyReport() {
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	if ($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Please select a Company/Branch.");
	} else {
		$("#spanCompanyError").text("");
	}

	if ($("#divisionId").val() == null) {
		$("#spanDivisionError").text("Division/Department is required.");
	} else {
		$("#spanDivisionError").text("");
	}

	if (dateFrom == null || dateTo == null || dateFrom == "" || dateTo == "") {
		$("#spanDateError").text("Please enter complete date range.");
	} else if(new Date(dateFrom) > new Date(dateTo)) {
		$("#spanDateError").text("Invalid date range.");
	} else {
		$("#spanDateError").text("");
	}

	if ($("#spanCompanyError").text() == "" && $("#spanDateError").text() == "" && 
			$("#spanDivisionError").text() == "") {
		var url = contextPath + "/frequencyReport/generate" + getCommonParam();
		$('#divFrequencyReport').attr('src',url);
		$('#divFrequencyReport').load(function () {
			$('#divFrequencyReport').contents().find("pre:lt(1)").remove();
			$('#divFrequencyReport').contents().find("p").remove();
		});
	} else {
		$('#divFrequencyReport').attr('src',"");
		$('#divFrequencyReport').load();
	}
}

function getCommonParam() {
	var companyId = $("#companyId option:selected").val();
	var typeId = $("#typeId option:selected").val();
	var divisionId = $("#divisionId option:selected").val();
	var status = $("#selectStatus option:selected").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var format = $("#formatType").val();
	return "?companyId="+companyId+"&typeId="+typeId+"&divisionId="+divisionId
		+"&status="+status+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&format="+format
		+"&isFirstNameFirst=false";
}
</script>
</head>
<body>
	<table>
		<tr>
			<td class="title2">Company/Branch </td>
			<td>
				<select id="companyId" class="frmSelectClass">
					<option selected='selected' value=-1>Please select a Company/Branch</option>
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
			<td class="title2">Type </td>
			<td>
				<select id="typeId" class="frmSelectClass">
					<option selected='selected' value=1>LATE</option>
					<option value="2">OVERSTAY</option>
					<option value="3">UNDERTIME</option>
					<option value="4">ABSENCE</option>
				</select>
			</td>
		</tr>
		<tr>
			<td class="title2">Division/Department</td>
			<td><select id="divisionId" class="frmSelectClass">
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}" id="${division.id}">${division.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td></td>
			<td><span id="spanDivisionError" class="error"></span></td>
		</tr>
		<tr>
			<td  class="title2">Status</td>
			<td>
				<select id="selectStatus" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<option value=1>ACTIVE</option>
					<option value=0>INACTIVE</option>
				</select>
			</td>
		</tr>
		<tr>
			<td class="title2">Date</td>
			<td class="tdDateFilter">
				<input type="text" id="dateFrom" maxlength="10" class="dateClass2"
						onblur="evalDate('dateFrom')" value="${currentDate}">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
							style="float: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="dateTo" maxlength="10" class="dateClass2"
						onblur="evalDate('dateTo')" value="${currentDate}">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
							style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<span id="spanDateError" class="error"></span>
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
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate" onclick="generateFrequencyReport();"/></td>
		</tr>
	</table>
	<div>
		<iframe id="divFrequencyReport"></iframe>
	</div>
</body>
</html>