<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Personnel Action Notice view form
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
	formObjectId = parseInt("${personnelActionNotice.ebObjectId}");
	//disable reference table link
	disableRefTblLink("${hasEditAccess}", "referenceDocuments", "file");
});
</script>
<style>
.disabled-link {
	pointer-events: none;
	cursor: default;
}
</style>
</head>
<body>
<div class="formDivBigForms">
	<div class="modFormLabel">Personnel Action Notice</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Sequence Number</td>
				<td class="label-value">${personnelActionNotice.sequenceNo}</td>
			<tr>
				<td class="labels">Status</td>
				<td class="label-value">${personnelActionNotice.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Personnel Action Notice Header</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Company/Branch</td>
				<td class="label-value">${personnelActionNotice.company.name}</td>
			</tr>
			<tr>
				<td class="labels">Employee</td>
				<td class="label-value">${personnelActionNotice.employeeFullName}</td>
			</tr>
			<tr>
				<td class="labels">Position</td>
				<td class="label-value">${personnelActionNotice.employeePosition}</td>
			</tr>
			<tr>
				<td class="labels">Division/Department</td>
				<td class="label-value">${personnelActionNotice.employee.division.name}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Action Notice</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Date</td>
				<td class="label-value">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${personnelActionNotice.date}"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Date Hired</td>
				<td class="label-value">${personnelActionNotice.hiredDate}</td>
			</tr>
			<tr>
				<td colspan="3" class="labels" style="padding-left: 146px; width: 600px; text-align: left;">
				This is to notify all concerned of the respective action taken 
				for the above-mentioned employee.</td>
			</tr>
			<tr>
				<td class="labels">Type of Action Notice</td>
				<td class="label-value">${personnelActionNotice.actionNotice.name}</td>
			</tr>
			<tr>
				<td class="labels">Justification</td>
				<td class="label-value">${personnelActionNotice.justification}</td>
			</tr>
		</table>
	</fieldset>
	<c:if test="${fn:length(personnelActionNotice.referenceDocuments) gt 0}">
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
					<c:forEach items="${personnelActionNotice.referenceDocuments}" var="refDoc" varStatus="status">
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