<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

	Description: Monthly Value Added Tax Declaration Main Page -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/commonUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/ZeroClipboard.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/TableTools.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<title>Monthly Value Added Tax Declaration</title>
<script type="text/javascript">
function getCommonParam() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var monthId = $("#monthId").val();
	var year = $("#year").val();
	var formatType = $("#formatType").val();

	return "?companyId="+companyId+"&monthId="+monthId+"&divisionId="+divisionId
	+"&year="+year+"&formatType=" + formatType;;
}
function generateMonthlyReport() {
	$("#spanCompanyError").text("");
	if ($("#companyId option:selected").val() == -1) {
		$("#spanCompanyError").text("Company is required.");
	}
	if ($("#monthId option:selected").val() == -1) {
		$("#spanErrorMonth").text("Invalid Month");
	}
if($("#companyId option:selected").val() != -1 && $("#monthId option:selected").val() != -1 ) {
		var url = contextPath + "/monthlyValueAddedTaxDec/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
		}
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
		<td colspan="2">
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" class="frmSelectClass">
				<option selected='selected' value=-1>All</option>
				<c:forEach var="divisions" items="${divisions}">
					<option value="${divisions.id}">${divisions.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
		<tr>
		<td class="title2">Year</td>
		<td>
			<select id=year class="frmSelectClass" style="width:150px">
				<c:forEach var="year" items="${year}">
					<c:choose>
						<c:when test="${year eq defaultYear}">
							<option value="${year}" selected="selected" >${year}</option>
						</c:when>
						<c:otherwise>
							<option value="${year}">${year}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Month</td>
		<td>
			<select id="monthId" class="frmSelectClass" style="width:150px">
				<c:forEach var="months" items="${months}">
					<option value="${months.month}">${months.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
			<td></td>
			<td colspan="2">
				<span id="spanErrorMonth" class="error"></span>
			</td>
		</tr>
	<tr>
		<td class="title2">Format:</td>
		<td class="value"><select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
		</select></td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateMonthlyReport()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>