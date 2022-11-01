<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description	: Monthly alphalist of payees
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css"media="all">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"media="all">
	<script type="text/javascript"src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
	<script type="text/javascript"src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
	<script type="text/javascript"src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
	<script type="text/javascript"src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		getDivisions();
	});

	function getDivisions() {
		$("#divisionId").empty();
		var companyId = $("#companyId").val();
		if (companyId != "" && companyId != -1) {
			var uri = contextPath + "/getDivisions?companyId=" + companyId;
			$("#divisionId").empty();
			var optionParser = {
				getValue : function(rowObject) {
					return rowObject["id"];
				},
				getLabel : function(rowObject) {
					return rowObject["name"];
				}
			};
			loadPopulate(uri, true, null, "divisionId", optionParser, null);
		} else {
			$("#divisionId").append("<option selected='selected' value=-1>ALL</option>");
		}
	}

	function getCommonParam() {
		var companyId = $("#companyId").val();
		var currentMonthId = $("#currentMonthId").val();
		var formatType = $("#formatType").val();
		var divisionId = $("#divisionId").val();
		var year = $("#slctYearId").val();
		return "?companyId=" + companyId + "&divisionId="+ divisionId + "&year="+ year +"&currentMonthId=" + currentMonthId
				+ "&formatType=" + formatType;

	}

	function generateMAP() {
		var isInvalidMonth= $("#currentMonthId").val() == -1 ||  $("#companyId").val() == "";
		var url;
		$("#spanErrorMonth").text( isInvalidMonth ? "Invalid Month" : "");
		if(!isInvalidMonth){
			$("#spanCompanyError").text("");
			$("#spanErrorMonth").text("");
			url = contextPath + "/monthlyalphalistpayees/generate"+ getCommonParam();
		}
		$('#reportMonthylAlphalist').attr('src', url);
		$('#reportMonthylAlphalist').load();

	}
</script>
</head>
<body>
	<table>

		<tr>
			<td class="title2">Company</td>
			<td>
				<select id="companyId" class="frmSelectClass" onchange="getDivisions()">
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td></td>
			<td><span id="spanCompanyError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Division</td>
			<td><select id="divisionId" class="frmSelectClass">
			</select></td>
		</tr>
		<tr></tr>
		<tr>
			<td class="title2">Year</td>
			<td>
			<select id="slctYearId"  class="frmSelectClass" style="width: 150px">
				<c:forEach var="years" items="${years}">
					<c:choose>
						<c:when test="${years eq defaultYear}">
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
			<td><select id="currentMonthId" onchange="" class="frmSelectClass" style="width:150px">
				<c:forEach var="mm" items="${months}">
					<option value="${mm.month}"
					<c:if test="${mm.month eq defaultMonth}">selected="selected"</c:if>
					>${mm.name}</option>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="2">
				<span id="spanErrorMonth" class="error"></span>
			</td>
		</tr>
		<tr>
		</tr>
		<tr>
			<td class="title2">Format</td>
			<td class="value"><select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select></td>
		</tr>
		<tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate"
				onclick="generateMAP();" /></td>
		</tr>
	</table>
	<div>
		<iframe id="reportMonthylAlphalist"></iframe>
	</div>
	<hr class="thin2">
</body>
</html>