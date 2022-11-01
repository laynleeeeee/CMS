<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- 

	Description: Certrificate of final tax withheld at source report filter.
 -->

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
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
function getCommonParam(){
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var selectYear = $('#selectYear').val();
	var fromMonth = $("#fromMonth").val();
	var toMonth = $("#toMonth").val();
	var birAtcId = $("#birAtcId").val();
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&divisionId="+(divisionId != "" ? divisionId : -1)
			+"&year="+selectYear+"&fromMonth="+fromMonth+"&toMonth="+toMonth+"&birAtcId="+birAtcId
			+"&formatType="+formatType;
}

function genReport(){
	var hasFilterError = false;
	var fromMonth = $("#fromMonth").val();
	var toMonth = $("#toMonth").val();
	$("#spanErrorDate").text("");
	if (Number(fromMonth) > Number(toMonth)) {
		$("#spanErrorDate").text("Invalid month range.");
		hasFilterError = true;
	} else if (fromMonth == -1 || toMonth == -1) {
		$("#spanErrorDate").text("Month is required.");
		hasFilterError = true;
	}
	var isValidFilters = true;
	var fromMonth = $("#fromMonth").val();
	var toMonth = $("#toMonth").val();
	var url = "";
	if (!hasFilterError){
		$("#spanErrorDate").text("");
		url = contextPath + "/certOfFinalTaxWithheldAtSrc/generatePDF"+getCommonParam();
	}
	console.log("URL = ", url);
	$('#reportCFTWS').attr('src', url);
	$('#reportCFTWS').load();
}

</script>
<title>Summary Alphalist of Withholding Taxes</title>
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
			<select id="selectYear"  class="frmSelectClass" style="width: 165px">
				<c:forEach var="yy" items="${years}">
					<option value="${yy}"
					<c:if test="${yy eq defaultYear}">selected="selected"</c:if>
					>${yy}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Month</td>
		<td class="tdDateFilter">
			<select id="fromMonth" onchange="" class="frmSelectClass" style="width:165px">
					<c:forEach var="listMonths" items="${listMonths}"> 
						<option value="${listMonths.month}">${listMonths.name}</option>
					</c:forEach>
			</select>
			<span style="font-size: small;">To</span>
			<select id="toMonth" onchange="" class="frmSelectClass" style="width:165px">
					<c:forEach var="listMonths" items="${listMonths}"> 
						<option value="${listMonths.month}">${listMonths.name}</option>
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
		<td class="title2">BIR ATC Code</td>
		<td class="value">
			<select id="birAtcId" class="frmSelectClass">
				<option selected ='selected' value=-1>ALL</option>
				<option value=1>WV 010</option>
				<option value=2>WV 020</option>
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
			</select>
		</td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="genReport();"/></td>
	</tr>
	</table>
	<div>
		<iframe id="reportCFTWS"></iframe>
	</div>
</body>
</html>
