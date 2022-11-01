<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Form deduction view.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${formDeduction.ebObjectId}");
	if ("${formDeduction.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	$("#spanTotalAmount").html(accounting.formatMoney("${formDeduction.totalDeductionAmount }"));
	//disable reference table link
	disableRefTblLink("${hasEditAccess}", "referenceDocuments", "file");
});
</script>
<style type="text/css">
.disabled-link {
	pointer-events: none;
	cursor: default;
}
</style>
</head>
<body>
<div class="formDivBigForms" id="divForm">
	<div class="modFormLabel">
		<c:choose>
			<c:when test="${typeId == 1}">Authority to Deduct</c:when>
			<c:otherwise>Cash Bond Contract</c:otherwise>
		</c:choose>
	</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Sequence Number</td>
				<td class="label-value">${formDeduction.sequenceNumber}</td>
			<tr>
				<td class="labels">Status</td>
				<td class="label-value">${formDeduction.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>
			<c:choose>
				<c:when test="${typeId == 1}">Authority to Deduct</c:when>
				<c:otherwise>Cash Bond Contract</c:otherwise>
			</c:choose>&nbsp;Header
		</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Date</td>
				<td class="label-value">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${formDeduction.formDate}"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Company/Branch</td>
				<td class="label-value">${formDeduction.company.name}</td>
			</tr>
			<tr>
				<td class="labels">Employee</td>
				<td class="label-value">${formDeduction.employeeFullName}</td>
			</tr>
			<tr>
				<td class="labels">Position</td>
				<td class="label-value">${formDeduction.employeePosition}</td>
			</tr>
			<tr>
				<td class="labels">Division/Department</td>
				<td class="label-value">${formDeduction.division.name}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Deduction Table</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Start Date</td>
				<td class="label-value">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${formDeduction.startDate}"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Deduction or Loan Type</td>
				<td class="label-value">${formDeduction.deductionType.name }</td>
			</tr>
			<tr>
				<td class="labels">Amount to Deduct</td>
				<td class="label-value">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${formDeduction.totalDeductionAmount}" />
				</td>
			</tr>
			<tr>
				<td class="labels">No. of Payroll Deduction</td>
				<td class="label-value">${formDeduction.noOfPayrollDeduction}</td>
			</tr>
			<tr>
				<c:choose>
					<c:when test="${typeId == 1}"><td class="labels">Remarks</td>
					<td class="label-value">${formDeduction.remarks}</td></c:when>
				</c:choose>
			</tr>
		</table>
		<table class="dataTable" id="deductionTbl" style="table-layout: fixed">
			<tr>
				<td>
					<c:if test="${fn:length(formDeduction.formDeductionLines) gt 0}">
						<table class="dataTable" id="leaves" style="table-layout: fixed">
							<thead>
								<tr>
									<th width="3%">#</th>
									<th width="27%">Date</th>
									<th width="27%">Amount</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${formDeduction.formDeductionLines}" var="formDeductionLines" varStatus="status">
									<tr> 
										<td class="td-numeric v-align-top">${status.index + 1}</td>
										<td class="th-td-norm v-align-top"><fmt:formatDate pattern="MM/dd/yyyy" value="${formDeductionLines.date}"/></td>
										<td class="th-td-edge v-align-top" align="right">
											<fmt:formatNumber type="number" minFractionDigits="2"
												maxFractionDigits="2" value="${formDeductionLines.amount}" />
										</td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td><b>Total</b></td>
									<td colspan="2" style="text-align: right;"><b><span id="spanTotalAmount"></span></b></td>
								</tr>
							</tfoot>
						</table>
					</c:if>
				</td>
			</tr>
			</table>
	</fieldset>
	<c:if test="${fn:length(formDeduction.referenceDocuments) gt 0}">
		<fieldset class="frmField_set">
			<legend>Documents Table</legend>
			<table class="dataTable" id="referenceDocuments" style="table-layout: fixed">
				<thead>
					<tr>
						<th width="3%" class="th-td-norm">#</th>
						<th width="18%" class="th-td-norm">Name</th>
						<th width="18%" class="th-td-norm">Description</th>
						<th width="18%" class="th-td-edge">File</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${formDeduction.referenceDocuments}" var="refDoc" varStatus="status">
						<tr> 
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${refDoc.fileName}</td>
							<td class="th-td-norm v-align-top">${refDoc.description}</td>
							<td class="th-td-edge v-align-top tdFile">
								<a onclick="convBase64ToFile('${refDoc.file}','${refDoc.fileName}')"
									href="#" class="fileLink" id="file">${refDoc.fileName}</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</c:if>
</div>
</body>
</html>