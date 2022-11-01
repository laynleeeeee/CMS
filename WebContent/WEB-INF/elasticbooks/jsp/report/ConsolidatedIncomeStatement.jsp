<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description: Consolidated income statement report page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript">
var companyIds = [];
function generateReport() {
	var consolidatedIds = "";
	var keys = Object.keys(companyIds);
	for (var i = 0; i < keys.length; i++) {
		if (i == 0) {
			consolidatedIds += keys[i];
		} else {
			consolidatedIds += ";" + keys[i];
		}
	}

	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var formatType = $("#formatType").val();

	var isValidDate = dateFrom != "" && dateTo != "";
	if (!isValidDate) {
		$("#spanDateError").text("Complete and valid date range is required.");
	}

	var isValidCompany = $.trim(consolidatedIds) != "";
	if (!isValidCompany) {
		$("#spanCompanyError").text("Please select at least one(1) company.");
	}

	var url = "";
	if (isValidDate && isValidCompany) {
		$("#spanDateError").text("");
		$("#spanCompanyError").text("");
		url = contextPath + "/incomeStatement/generateReport?strCompanyIds="+consolidatedIds
				+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType="+formatType;
	}
	$('#iFrame').attr('src', url);
	$('#iFrame').load();
}

function populateCompanyIds($elem, value) {
	var isChecked = $($elem).is(":checked");
	if (isChecked) {
		if (typeof companyIds[value] == "undefined") {
			companyIds[value] = value;
		}
	} else {
		delete companyIds[value];
	}
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2" style="vertical-align: top;">Company</td>
		<td>
			<table id="companyTbl">
				<c:forEach var="c" items="${companies}">
					<tr>
						<td>
							<input type="checkbox" id="companyId-${c.id}" name="cbCompany" class="cbCompany"
								onclick="populateCompanyIds(this, '${c.id}');"/>
						</td>
						<td>&nbsp;&nbsp;${c.name}</td>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Date</td>
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
		<td class="title2"></td>
		<td><span id="spanDateError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Format</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
			</select>&nbsp;&nbsp;
			<input type="button" value="Generate" onclick="generateReport();"/>
		</td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>