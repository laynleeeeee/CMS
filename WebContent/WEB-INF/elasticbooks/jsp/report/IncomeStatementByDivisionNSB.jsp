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
			return rowObject["name"];
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
	var url = "";
	if(!isValidCompany) {
		$("#errorCompany").text("");
		$("#errorDate").text("");
		url = contextPath + "/incomeStatementByDivNSB/generate"+getCommonParam();
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
	var hasZeroBalance = $("#hasZeroBalance").val();
	var fromMonth = $("#fromMonth").val();
	var toMonth = $("#toMonth").val();
	var year = $("#year").val();
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&hasZeroBalance="+hasZeroBalance
			+"&fromMonth="+fromMonth+"&toMonth="+toMonth+"&year="+year+"&formatType="+formatType;
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
	</tr><tr>
		<td class="title2">Year</td>
		<td class="tdDateFilter">
			<select id="year" class="frmSelectClass" style="width:150px">
				<c:forEach var="year" items="${years}">
					<option value="${year}"
					<c:if test="${year eq currentYear}">selected="selected"</c:if>
					>${year}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">GL with Zero Balance </td>
		<td class="value">
			<select id="hasZeroBalance" class="frmSelectClass">
				<option selected='selected' value='-1'>NO</option>
				<option value='1'>YES</option>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateIncomeStatement()"/></td>
	</tr>
</table>
<div>
	<iframe id="incomeStatementByDiv"></iframe>
</div>
</body>
</html>