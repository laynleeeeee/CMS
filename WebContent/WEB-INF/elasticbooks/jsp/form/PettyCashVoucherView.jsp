<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : Petty Cash Voucher view form page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable2.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview2.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${pcv.ebObjectId}");
	if ("${pcv.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	disableRefTblLink("${hasEditAccess}", "referenceDocuments", "file");
	if("${hasReference}" == "false"){
		$("#referenceDocumentsFieldset").hide();
	}
});
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">Petty Cash Voucher - ${pcv.division.name}</div>
	<br>
	<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="label">PCV No.</td>
					<td class="label-value">${pcv.sequenceNo}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${pcv.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Petty Cash Voucher Header</legend>
			<table class="formTable" border=0>
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${pcv.company.name}</td>
				</tr>
				<tr>
					<td class="label">Division </td>
					<td class="label-value">${pcv.division.name}</td>
				</tr>
				<tr>
					<td class="label">Custodian</td>
					<td class="label-value">${pcv.userCustodian.custodianAccount.custodianName}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${pcv.pcvDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Requestor</td>
					<td class="label-value">${pcv.requestor}</td>
				</tr>
				<tr>
					<td class="label">Refrerence</td>
					<td class="label-value">${pcv.referenceNo}</td>
				</tr>
				<tr>
					<td class="label">Description</td>
					<td class="label-value">${pcv.description}</td>
				</tr>
				<tr>
					<td class="label">Amount</td>
					<td class="label-value">
						<fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${pcv.amount}'/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set" id="referenceDocumentsFieldset">
		<legend>Documents</legend>
		<table class="dataTable" id="referenceDocuments">
			<thead>
				<tr>
					<th width="3%" class="th-td-norm">#</th>
					<th width="18%" class="th-td-norm">Name</th>
					<th width="18%" class="th-td-norm">Description</th>
					<th width="18%" class="th-td-norm">file</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${pcv.referenceDocuments}" var="refDoc" varStatus="status">
					<tr> 
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${refDoc.fileName}</td>
						<td class="th-td-norm v-align-top">${refDoc.description}</td>
						<td class="th-td-norm v-align-top" id="file">
							<a href="${refDoc.file}" download="${refDoc.fileName}" class="fileLink" id="file">${refDoc.fileName}</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</fieldset>
	</div>
</div>
</body>
</html>