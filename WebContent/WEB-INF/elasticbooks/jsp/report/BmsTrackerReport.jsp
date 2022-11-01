<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: BMS Tracker main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
function generateBmsTrackerReport() {
	var hasValidFilters = true;
	var companyId = $("#companyId").val();
	var invoiceDateFrom = $.trim($("#invoiceDateFrom").val());
	var invoiceDateTo = $.trim($("#invoiceDateTo").val());
	var poDateFrom = $.trim($("#poDateFrom").val());
	var poDateTo = $.trim($("#poDateTo").val());
	if (companyId < 0){
		$("#spanCompanyError").text("Company is required.");
		hasValidFilters = false;
	}

	if (invoiceDateFrom != "" && invoiceDateTo == "") {
		$("#spanInvoiceDateError").text("Invalid invoice date range.");
		hasValidFilters = false;
	} else if (invoiceDateFrom == "" && invoiceDateTo != "") {
		$("#spanInvoiceDateError").text("Invalid invoice date range.");
		hasValidFilters = false;
	} else if (new Date(invoiceDateFrom) > new Date(invoiceDateTo)) {
		$("#spanInvoiceDateError").text("Invalid invoice date range.");
		hasValidFilters = false;
	}

	if (poDateFrom != "" && poDateTo == "") {
		$("#spanPoDateError").text("Invalid PO date range.");
		hasValidFilters = false;
	} else if (poDateFrom == "" && poDateTo != "") {
		$("#spanPoDateError").text("Invalid PO date range.");
		hasValidFilters = false;
	} else if (new Date(poDateFrom) > new Date(poDateTo)) {
		$("#spanPoDateError").text("Invalid PO date range.");
		hasValidFilters = false;
	}

	var url = "";
	if (hasValidFilters) {
		$("#spanCompanyError").text("");
		$("#spanPoDateError").text("");
		$("#spanInvoiceDateError").text("");
		url = contextPath+"/bmsTracker/generatePDF"+getCommonParam();
	}
	$('#reportBmsTracker').attr('src',url);
	$('#reportBmsTracker').load();
}

function getCommonParam() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var bmsNo = $("#bmsNo").val();
	var poDateFrom = document.getElementById("poDateFrom").value;
	var poDateTo = document.getElementById("poDateTo").value;
	var invoiceDateFrom = document.getElementById("invoiceDateFrom").value;
	var invoiceDateTo = document.getElementById("invoiceDateTo").value;
	var statusId = $("#statusId").val();
	var formatType = $("#formatType").val();
	var typeId = $("#typeId").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&poDateFrom="+poDateFrom+"&poDateTo="+poDateTo
			+"&invoiceDateFrom="+invoiceDateFrom+"&invoiceDateTo="+invoiceDateTo+"&bmsNo="+bmsNo
			+"&statusId="+statusId+"&formatType="+formatType+"&typeId="+typeId;
}

</script>
</head>
<body>
	<table>

		<tr>
			<td class="title2">Company</td>
			<td><select id="companyId" class="frmSelectClass" >
					<option selected='selected' value=-1>Please select a company</option>
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
			<td><select id="divisionId" class="frmSelectClass" >
					<option selected='selected' value=-1>All</option>
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Type</td>
			<td>
			<select id="typeId" class="frmSelectClass">
				<option value=-1 selected="selected">ALL</option>
				<option value=1>PO</option>
				<option value=2>Non PO</option>
			</select>
		</td>
		<tr>
			<td class="title2">BMS Number</td>
			<td>
				<input type="text" id="bmsNo" class="input">
			</td>
		</tr>
		<tr>
			<td class="title2">PO Date</td>
			<td class="tdDateFilter">
				<input type="text" id="poDateFrom" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('poDateFrom')" style="cursor:pointer"
							style="float: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="poDateTo" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('poDateTo')" style="cursor:pointer"
							style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanPoDateError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Invoice Date</td>
			<td class="tdDateFilter">
				<input type="text" id="invoiceDateFrom" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('invoiceDateFrom')" style="cursor:pointer"
							style="float: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="invoiceDateTo" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('invoiceDateTo')" style="cursor:pointer"
							style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanInvoiceDateError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Check Status</td>
			<td><select id="statusId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="status" items="${formStatuses}">
						<option value="${status.id}">${status.description}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Format</td>
			<td class="value"><select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select></td>
		</tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate"
				onclick="generateBmsTrackerReport()" /></td>
		</tr>
	</table>
	<div>
		<iframe id="reportBmsTracker"></iframe>
	</div>
	<hr class="thin2">
</body>
</html>