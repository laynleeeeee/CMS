<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Quarterly Value-Added Tax Declaration of Payees main page. -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
		var newDate = parseServerDate(new Date);
		$("#asOfDate").val(newDate);
});

	function generatePCVReport() {
		var isValidFilters = true;
		var asOfDate = $.trim($("#asOfDate").val());

		if ($("#companyId").val() == -1) {
			$("#spanCompanyError").text("Company is required.");
			isValidFilters = false;
		}
		if ($("#divisionId").val() == null) {
			$("#spanDivisionError").text("Division is required.");
			isValidFilters = false;
		}
		var url = "";
		if (isValidFilters) {
			clearValues();
			url = contextPath + "/quarterlyValueAddedTaxDeclaration/generatePDF" + getCommonParam();
		}
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}

	function clearValues() {
		$("#spanCompanyError").text("");
		$("#spanDateError").text("");
		$("#spanDivisionError").text("");
	}

	function getCommonParam() {
		var companyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var quarter = $("#quarter").val();
		var selectYear = $('#selectYear').val();
		var formatType = $("#formatType").val();
		var year = $("#year").val();

		return "?companyId=" + companyId + "&divisionId=" + divisionId +
			"&quarter=" + quarter + "&year="+ selectYear + "&formatType=" + formatType;
	}

	function clearAndAddEmpty() {
		$("#supplierAcctId").empty();
		var option = "<option selected='selected' value='-1'>ALL</option>";
		$("#supplierAcctId").append(option);
	}

	function companyOnChange() {
		clearValues();
	}

</script>
</head>
<body>
	<table>
			<tr>
				<td class="title2">Company</td>
				<td><select id="companyId" onchange="companyOnChange();"
					class="frmSelectClass">
						<c:forEach var="company" items="${companies}">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
				</select></td>
			</tr>
			<tr>
				<td></td>
				<td colspan="1"><span id="spanCompanyError" class="error"></span></td>
			</tr>
			<tr>
				<td class="title2">Division</td>
				<td><select id="divisionId" onchange="companyOnChange();"
					class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.name}</option>
					</c:forEach>
				</select></td>
			</tr>
			<tr>
				<td></td>
				<td colspan="1"><span id="spanDivisionError" class="error"></span></td>
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
				<td class="title2">Quarter</td>
				<td>
					<select id="quarter" class="frmSelectClass">
					<c:forEach var="m" items="${quarter}" varStatus="status">
						<c:choose>
							<c:when test="${status.index eq defaultQuarter}">
								<option value="${status.index}" selected="selected" >${m}</option>
							</c:when>
							<c:otherwise>
								<option value="${status.index}">${m}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="title2">Format:</td>
				<td><select id="formatType" class="frmSelectClass">
						<option value="pdf">PDF</option>
						<option value="xls">EXCEL</option>
				</select></td>
			</tr>
			<tr align="right">
				<td colspan="3"><input type="button" value="Generate" onclick="generatePCVReport()"
					 /></td>
			</tr>
	</table>
	<hr class="thin2">
	<div>
		<iframe id="iFrame"></iframe>
	</div>
</body>
</html>