<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Employee Document view.
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
	formObjectId = parseInt("${employeeDocument.ebObjectId}");
	$("#btnPrint").hide();
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
<div class="modFormLabel">Employee Document</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Sequence Number</td>
				<td class="label-value">${employeeDocument.sequenceNo}</td>
			<tr>
				<td class="labels">Status</td>
				<td class="label-value">${employeeDocument.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Employee Document Header</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Company/Branch</td>
				<td class="label-value">${employeeDocument.company.name}</td>
			</tr>
			<tr>
				<td class="labels">Date</td>
				<td class="label-value">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${employeeDocument.date}"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Employee</td>
				<td class="label-value">${employeeDocument.employee.fullName}</td>
			</tr>
			<tr>
				<td class="labels">Document Type</td>
				<td class="label-value">${employeeDocument.documentType.name}</td>
			</tr>
			<tr>
				<td class="labels" valign="top">Remarks</td>
				<td class="label-value" style="word-wrap: break-word;">${employeeDocument.remarks }</td>
			</tr>
		</table>
	</fieldset>
	
	<c:if test="${fn:length(employeeDocument.referenceDocuments) gt 0}">
		<fieldset class="frmField_set">
			<legend>Photo Attachment</legend>
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
					<c:forEach items="${employeeDocument.referenceDocuments}" var="refDoc" varStatus="status">
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