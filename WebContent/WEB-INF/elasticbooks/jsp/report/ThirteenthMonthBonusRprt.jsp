<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: 13th Month Bonus search filter jsp page for CHL
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	loadDivisions();
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function loadDivisions() {
	var companyId = $("#selectCompanyId").val();
	var uri = contextPath + "/thirteenthMonBonus/loadDivisions?companyId="+companyId;
	$("#selectDivisionId").empty();
	var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
	var postHandler = {
			doPost: function(data) {
				// Do nothing
			}
	};
	loadPopulate (uri, true, null, "selectDivisionId", optionParser, postHandler);
}

function getCommonParam() {
	var companyId = $("#selectCompanyId").val();
	var divisionId = $("#selectDivisionId").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var formatType = $("#formatType").val();
	var hasError = false;

	if($.trim(dateFrom) == "" && $.trim(dateTo) == "") {
		$("#errorDate").text("Date from and/or to are required.");
		hasError = true;
	} else if(dateFrom > dateTo) {
		$("#errorDate").text("Invalid date range.");
		hasError = true;
	}

	if(companyId == "" || companyId == null) {
		$("#errorCompany").text("Company is required.");
		hasError = true;
	}

	if(divisionId == "" || divisionId == null ) {
		$("#errorDivision").text("Department is required.");
		hasError = true;
	}

	if(!hasError) {
		hasError = false;
		var uri = "?companyId=" + companyId + "&divisionId=" + divisionId + "&dateFrom=" + dateFrom
				+ "&dateTo=" + dateTo + "&formatType=" + formatType;
		return uri;
	} else {
		return ""
	}
}

function generateReport() {
	var rprtParam = getCommonParam();
	if(rprtParam != "") {
		$("#errorDate").text("");
		$("#errorDivision").text("");
		$("#errorCompany").text("");
		var url = contextPath + "/thirteenthMonBonus/generateReport" + rprtParam;
		console.log("url :: "+url);
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	} else {
		$("#iFrame").attr('src', "");
	}
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company</td>
		<td class="value">
			<select id="selectCompanyId"  class="frmSelectClass" onchange="loadDivisions();">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value">
			<span id="errorCompany" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Department</td>
		<td class="value">
			<select id="selectDivisionId" class="frmSelectClass"></select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value">
			<span id="errorDivision" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Date</td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2" onblur="evalDate('dateFrom');">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">&nbsp;&nbsp;To&nbsp;&nbsp;</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2" onblur="evalDate('dateTo');">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value">
			<span id="errorDate" class="error"></span>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>