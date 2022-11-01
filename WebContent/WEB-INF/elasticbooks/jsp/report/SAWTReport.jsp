<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Summary alphalist of withholding taxes jsp report filter
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
var currentMonth = $(currentMonth).val();
var currentYear = $(currentYear).val();
$(document).ready(function() {
	getDivisions();
});

function getDivisions() {
	var companyId = $("#companyId").val();
	if (companyId != "") {
		var uri = contextPath+"/getDivisions?companyId="+companyId;
		$("#divisionId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, true, null, "divisionId", optionParser, null);
	}
}

function getCommonParam(includeFormatType){
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId option:selected").val() != "" ?
			$("#divisionId option:selected").val() : -1;
	var year = Number($("#slctYear").val());
	var fromMonth = $("#fromMonth").val();
	var toMonth = $("#toMonth").val();
	var birFormType = $("#birFormType").val();
	var formatType = $("#formatType").val();
	var param = "?companyId="+companyId+"&divisionId="+divisionId+"&fromMonth="+fromMonth+"&toMonth="+toMonth+
			"&birFormType="+birFormType+"&year="+year;
	if (includeFormatType == true) {
		var formatType = $("#formatType").val();
		param += "&formatType=" + formatType;
	}
	return param;
}

function generateReport(){
	var hasFilterError = false;
	var fromMonth = $.trim($("#fromMonth").val());
	var toMonth = $.trim($("#toMonth").val());
	if (fromMonth == "" || toMonth == "") {
		$("#spanErrorDate").text("Month from and to are required fields.");
		hasFilterError = true;
	} else if (fromMonth > toMonth) {
		$("#spanErrorDate").text("Invalid month range.");
		hasFilterError = true;
	}
	var url = "";
	if (!hasFilterError){
		$("#spanErrorDate").text("");
		var formatType = $("#formatType").val();
		if(formatType=="dat"){
			url = contextPath + "/SAWTReport/generateDAT"+getCommonParam(false);
		} else {
			url = contextPath + "/SAWTReport/generatePDF"+getCommonParam(true);
		}
	}
	$('#reportSAWT').attr('src', url);
	$('#reportSAWT').load();
}
</script>
</head>
<body>
	<table>
		<tr>
		<td class="title2">Company</td>
		<td class="value">
			<select id="companyId" class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Division</td>
		<td class="value">
			<select id="divisionId" class="frmSelectClass">
			<option selected ='selected' value=-1>ALL </option>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDivisionError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Year</td>
		<td class="value">
			<select id="slctYear"  class="frmSelectClass" style="width:150px">
				<c:forEach var="yy" items="${years}">
					<option value="${yy}"
					<c:if test="${yy eq currentYear}">selected="selected"</c:if>
					>${yy}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Month</td>
		<td class="tdDateFilter">
			<select id="fromMonth" class="frmSelectClass" style="width:150px">
				<c:forEach var="mm" items="${months}">
					<option value="${mm.month}"
					<c:if test="${mm.month eq currentMonth}">selected="selected"</c:if>
					>${mm.name}</option>
				</c:forEach>
			</select>
			<span style="font-size: small;">To</span>
			<select  id="toMonth"  class="frmSelectClass" style="float:right; width:180px;">
				<c:forEach var="mm" items="${months}">
					<option value="${mm.month}"
					<c:if test="${mm.month eq currentMonth}">selected="selected"</c:if>
					>${mm.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanErrorDate" class="error" ></span>
		</td>
	</tr>
	<tr>
		<td class="title2">BIR Form Type</td>
		<td class="value">
			<select id="birFormType" class="frmSelectClass">
				<option value="1700">1700</option>
				<option value="1701Q">1701Q</option>
				<option value="1701">1701</option>
				<option value="2250M">2550M</option>
				<option value="2250Q">2550Q</option>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanBirAtcError" class="error"></span>
		</td>
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
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport();"/></td>
	</tr>
	</table>
	<div>
		<iframe id="reportSAWT"></iframe>
	</div>
</body>
</html>