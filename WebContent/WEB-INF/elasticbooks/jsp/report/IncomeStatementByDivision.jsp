<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Income Statement By Division Report main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
	loadDivisions();
});

function loadDivisions() {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions/byCompany?companyId="+companyId+"&isMainLevelOnly=true";
	$("#slctDivisionId").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},
		getLabel: function (rowObject) {
			return rowObject["number"] + " " + rowObject["name"];
		}
	};
	postHandler = {
			doPost: function(data) {
				loadDivisionLevels();
			}
	};
	loadPopulate (uri, true, null, "slctDivisionId", optionParser, postHandler);
}

function generateIncomeStatement() {
	var companyId = $("#companyId").val();
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var isValidCompany = false;
	var isValidDate = false;
	if(companyId == "") {
		isValidCompany = true;
		$("#errorCompany").text("Please select a company.");
	}
	if(dateFrom ==  "" || dateTo == "") {
		isValidDate = true;
		$("#errorDate").text("Please enter valid start or end date.");
	} else if(dateFrom > dateTo) {
		isValidDate = true;
		$("#errorDate").text("Invalid date range.");
	}
	var url = "";
	if(!isValidCompany && !isValidDate) {
		$("#errorCompany").text("");
		$("#errorDate").text("");
		url = contextPath + "/incomeStatementByDiv/generate"+getCommonParam();
	}
	$('#incomeStatementByDiv').attr('src',url);
	$("#spinner").show();
	$('#incomeStatementByDiv').load(function name() {
		$("#spinner").hide();
	});
}

function getCommonParam () {
	var companyId = $("#companyId option:selected").val();
	var divisionId = $("#slctDivisionId option:selected").val();
	var subDivision = $("#divisionLevel option:selected").val();
	var accountLevelId = $("#slctAccountLevel option:selected").val();
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+(subDivision != undefined && subDivision != null ? "&subDivision="+subDivision : "")+"&accountLevelId="+accountLevelId
		+"&divisionId="+divisionId+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType="+formatType;
}

function loadDivisionLevels() {
	var parentDivisionId = $("#slctDivisionId").val();
	if (parentDivisionId == -1) {
		$("#divisionLevel").hide();
	} else {
		$("#divisionLevel").show();
		$.ajax({
			url: contextPath + "/getDivisions/getChildrenDivision?parentDivisionId="+parentDivisionId,
			success: function (divisions) {
				var options = "<option value='-1'>All</option>";
				for (var index = 0; index < divisions.length; index++){
					div = divisions[index];
					options += "<option  value='"+div.id+"'>"+div.name+"</option>";
				}
				$("#slctDivisionLevel").empty().append(options);
			},
			dataType: "json"
		});
	}
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td class="value">
			<select id="companyId" class="frmSelectClass" onchange="loadDivisions();">
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
		<td class="title2">Date </td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2"
				onblur="evalDate('dateFrom');">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2"
				onblur="evalDate('dateTo');">
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
		<td class="title2">Division </td>
		<td class="value">
			<select id="slctDivisionId" class="frmSelectClass" onchange="loadDivisionLevels();"></select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value">
			<span id="spanDivisionError" class="error"></span>
		</td>
	</tr>
	<tr id="divisionLevel" style="display: none;">
		<td class="title2">Sub Division </td>
		<td class="value">
			<select id="slctDivisionLevel" class="frmSelectClass">
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value">
			<span id="spanDivisionError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Account Level </td>
		<td class="value">
			<select id="slctAccountLevel" class="frmSelectClass">
				<option selected='selected' value=1>MAIN</option>
				<option value=2>LEVEL 2</option>
				<option value=3>LEVEL 3</option>
				<option value=4>LEVEL 4</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value">
			<span id="spanDivisionError" class="error"></span>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateIncomeStatement()"/></td>
	</tr>
</table>
<div>
	<iframe id="incomeStatementByDiv"></iframe>
</div>
</body>
</html>