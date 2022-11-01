<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Request for Leave view form
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
	formObjectId = parseInt("${employeeRequest.ebObjectId}");
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
	<c:choose>
		<c:when test="${employeeRequest.requestTypeId == 1}">
			<div class="modFormLabel">Request For Leave</div>
		</c:when>
		<c:otherwise>
			<div class="modFormLabel">Request For Overtime</div>
		</c:otherwise>
	</c:choose>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Sequence Number</td>
				<td class="label-value">${employeeRequest.sequenceNo}</td>
			<tr>
				<td class="labels">Status</td>
				<td class="label-value">${employeeRequest.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>
			<c:choose>
				<c:when test="${employeeRequest.requestTypeId == 1}">Request For Leave</c:when>
				<c:otherwise>Request For Overtime</c:otherwise>
			</c:choose> Header </legend>
		<table class="formTable">
			<tr>
				<td class="labels">Date</td>
				<td class="label-value">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${employeeRequest.date}"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Company/Branch</td>
				<td class="label-value">${employeeRequest.company.name}</td>
			</tr>
			<tr>
				<td class="labels">Employee</td>
				<td class="label-value">${employeeRequest.employeeFullName}</td>
			</tr>
			<tr>
				<td class="labels">Position</td>
				<td class="label-value">${employeeRequest.employeePosition}</td>
			</tr>
			<tr>
				<td class="labels">Division/Department</td>
				<td class="label-value">${employeeRequest.employee.division.name}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<c:choose>
			<c:when test="${employeeRequest.requestTypeId eq 1}">
				<legend>Leave Details</legend>
				<table class="formTable">
					<c:choose>
						<c:when test="${employeeRequest.leaveDetail.halfDay}">
							<tr>
								<td class="labels">Period</td>
								<td class="label-value">
									<c:choose>
										<c:when test="${employeeRequest.leaveDetail.period eq 1}">AM</c:when>
										<c:otherwise>PM</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td class="labels">Date</td>
								<td class="label-value">
									<fmt:formatDate pattern="MM/dd/yyyy" value="${employeeRequest.leaveDetail.dateFrom}"/>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td class="labels">Date From</td>
								<td class="label-value">
									<fmt:formatDate pattern="MM/dd/yyyy" value="${employeeRequest.leaveDetail.dateFrom}"/>
								</td>
							</tr>
							<tr>
								<td class="labels">Date To</td>
								<td class="label-value">
									<fmt:formatDate pattern="MM/dd/yyyy" value="${employeeRequest.leaveDetail.dateTo}"/>
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
					<tr>
						<td class="labels">Days</td>
						<td class="label-value">
							<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${employeeRequest.leaveDetail.leaveDays}" />
						</td>
					</tr>
					<tr>
						<td class="labels">Leave Type</td>
						<td class="label-value">${employeeRequest.leaveDetail.typeOfLeave.name}</td>
					</tr>
					<tr>
						<td class="labels">Balance</td>
						<td class="label-value">
							<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${employeeRequest.leaveDetail.leaveBalance}" />
						</td>
					</tr>
					<tr>
						<td class="labels">Remarks</td>
						<td class="label-value">${employeeRequest.leaveDetail.remarks}</td>
					</tr>
				</table>
			</c:when>
			<c:otherwise>
				<legend>Overtime Details</legend>
				<table class="formTable">
					<tr>
						<td class="labels">Overtime Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${employeeRequest.overtimeDetail.overtimeDate}"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Start Time</td>
						<td class="label-value">${employeeRequest.overtimeDetail.startTime}</td>
					</tr>
					<tr>
						<td class="labels">End Time</td>
						<td class="label-value">${employeeRequest.overtimeDetail.endTime}</td>
					</tr>
					<tr>
						<td class="labels">No. of Hours</td>
						<td class="labels" style="text-align: left; padding: 5px 15px;">${employeeRequest.overtimeDetail.overtimeHours}</td>
						<td class="labels">Allowable Break</td>
						<td class="label-value">${employeeRequest.overtimeDetail.allowableBreak}</td>
					</tr>
					<tr>
						<td class="labels">Purpose</td>
						<td class="label-value">${employeeRequest.overtimeDetail.purpose}</td>
					</tr>
				</table>
			</c:otherwise>
		</c:choose>
	</fieldset>
	<c:if test="${fn:length(employeeRequest.referenceDocuments) gt 0}">
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
					<c:forEach items="${employeeRequest.referenceDocuments}" var="refDoc" varStatus="status">
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