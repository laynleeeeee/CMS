<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Timesheet view form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<style type="text/css">
.monetary {
	text-align: right;
}

.inner {
  overflow-x:scroll;
  width: 100%;
}
a#btnPrintPayslip{
	color: blue;
}

a#btnPrintPayslip:hover {
	color: blue;
	cursor: pointer;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${timesheetFormDto.timeSheet.ebObjectId}");
	if ("${timeSheet.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Timesheet</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table>
					<tr>
						<td class="label">
							Sequence No.
						</td>
						<td class="label-value">${timesheetFormDto.timeSheet.sequenceNumber}</td>
					</tr>
					<tr>
						<td class="label">Status </td>
						<td class="label-value">${timesheetFormDto.timeSheet.formWorkflow.currentFormStatus.description}</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Time Sheet Header</legend>
				<table class="formTable" >
					<tr>
						<td class="label">Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${timesheetFormDto.timeSheet.date}"/>
						</td>
					</tr>
					<tr>
						<td class="label">Company/Branch</td>
						<td class="label-value">${timesheetFormDto.timeSheet.company.numberAndName}</td>
					</tr>
					<tr>
						<td class="label">Division/Department</td>
						<td class="label-value">${timesheetFormDto.timeSheet.division.numberAndName}</td>
					</tr>
					<tr>
						<td class="label">Month/Year</td>
						<td class="label-value">
						<c:choose>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 1}">JANUARY</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 2}">FEBRUARY</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 3}">MARCH</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 4}">APRIL</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 5}">MAY</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 6}">JUNE</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 7}">JULY</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 8}">AUGUST</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 9}">SEPTEMBER</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 10}">OCTOBER</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 11}">NOVEMBER</c:when>
							<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriod.month eq 12}">DECEMBER</c:when>
						</c:choose>
						${timesheetFormDto.timeSheet.payrollTimePeriod.year}
						</td>
					</tr>
					<tr>
						<td class="label">Time Period</td>
						<td class="label-value">
							${timesheetFormDto.timeSheet.payrollTimePeriodSchedule.name}
						</td>
					</tr>
					<tr>
						<td class="label">Compute Contribution</td>
						<td class="label-value">
							<c:choose>
								<c:when test="${timesheetFormDto.timeSheet.payrollTimePeriodSchedule.computeContributions}">
									<input type="checkbox" checked="checked" onclick="return false;"/>
								</c:when>
								<c:otherwise>
									<input type="checkbox" onclick="return false;" />
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="label">DTR File Name</td>
						<td class="label-value">${timesheetFormDto.referenceDocument.fileName}</td>
					</tr>
					<tr>
						<td class="label">Biometric Model</td>
						<td class="label-value">${timesheetFormDto.biometricModel.modelName}</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<div id="divTimeSheetViewTable">
				<%@ include file = "TimeSheetTableView.jsp" %>
			</div>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>