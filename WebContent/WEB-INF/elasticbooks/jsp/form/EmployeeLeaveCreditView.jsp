<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Employee Leave Credit view.
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
	formObjectId = parseInt("${employeeLeaveCredit.ebObjectId}");
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
<div class="formDivBigForms">
	<div class="modFormLabel">Employee Leave Credit</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Sequence Number</td>
				<td class="label-value">${employeeLeaveCredit.sequenceNumber}</td>
			<tr>
				<td class="labels">Status</td>
				<td class="label-value">${employeeLeaveCredit.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Employee Leave Credit Header</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Date</td>
				<td class="label-value">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${employeeLeaveCredit.date}"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Leave Type</td>
				<td class="label-value">${employeeLeaveCredit.typeOfLeave.name }</td>
			</tr>
			<tr>
				<td class="labels">Remarks</td>
				<td class="label-value">${employeeLeaveCredit.remarks }</td>
			</tr>
		</table>
	</fieldset>
	<c:if test="${fn:length(employeeLeaveCredit.elcLines) gt 0}">
		<fieldset class="frmField_set">
			<legend>Leaves Table</legend>
			<table class="formTable">
				<tr>
					<td class="labels">Company/Branch</td>
					<td class="label-value">${employeeLeaveCredit.company.name}</td>
				</tr>
				<tr>
					<td class="labels">Division/Department</td>
					<td class="label-value">${employeeLeaveCredit.division.name}</td>
				</tr>
			</table>
			<table class="dataTable" id="leaves" style="table-layout: fixed">
				<thead>
					<tr>
						<th width="3%" class="th-td-norm">#</th>
						<th width="40%" class="th-td-norm">Employee Name</th>
						<th width="19%" class="th-td-norm">Available Leaves</th>
						<th width="19%" class="th-td-norm">Deduct(Debit)</th>
						<th width="19%" class="th-td-edge">Add(Credit)</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${employeeLeaveCredit.elcLines}" var="elcLine" varStatus="status">
						<tr> 
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${elcLine.employeeName}</td>
							<td class="th-td-norm v-align-top" align="right">${elcLine.availableLeaves}</td>
							<td class="th-td-norm v-align-top" align="right">${elcLine.deductDebit}</td>
							<td class="th-td-edge v-align-top" align="right">${elcLine.addCredit}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</c:if>

	<c:if test="${fn:length(employeeLeaveCredit.referenceDocuments) gt 0}">
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
					<c:forEach items="${employeeLeaveCredit.referenceDocuments}" var="refDoc" varStatus="status">
						<tr> 
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${refDoc.fileName}</td>
							<td class="th-td-norm v-align-top">${refDoc.description}</td>
							<td class="th-td-edge v-align-top" id="file"><a onclick="convBase64ToFile('${refDoc.file}','${refDoc.fileName}')"
								href="#" class="fileLink" id="file">${refDoc.fileName}</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</c:if>
</div>
</body>
</html>