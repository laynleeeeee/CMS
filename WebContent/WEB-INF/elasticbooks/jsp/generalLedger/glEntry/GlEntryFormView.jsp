<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: GL entry form for viewing only.
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
	formObjectId = parseInt("${generalLedgerDto.generalLedger.ebObjectId}");
	if ("${generalLedgerDto.generalLedger.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
<div class="formDivBigForms">
		<div class="modFormLabel">
			General Journal<span id="spanDivisionLbl"> -
				${generalLedgerDto.generalLedger.division.name}</span>
		</div>
		<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">Journal Voucher No.</td>
				<td class="label-value">${generalLedgerDto.generalLedger.sequenceNo}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${generalLedgerDto.generalLedger.formWorkflow.currentFormStatus.description}</td>
			</tr>
			<tr>
				<td class="label">Entry Source</td>
				<td class="label-value">${generalLedgerDto.generalLedger.glEntrySource.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>GL Header</legend>
		<table>
			<tr>
				<td class="label">* Company</td>
				<td class="label-value">${legder.company.companyNumber} - ${legder.company.name}</td>
			</tr>
			<tr>
				<td class="label">* Division</td>
				<td class="label-value">${legder.division.number} - ${legder.division.name}</td>
			</tr>
			<tr>
				<td class="label">* Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${legder.glDate}"/></td>
			</tr>
			<tr>
				<td class="label">* Description</td>
				<td class="label-value">${legder.comment}</td>
			</tr>
			<tr>
				<td class="label">Currency </td>
				<td class="label-value">${legder.currency.name}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>GL Lines</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="3%" class="th-td-norm">#</th>
					<th width="12%" class="th-td-norm">Division</th>
					<th width="15%" class="th-td-norm">Account</th>
					<th width="25%" class="th-td-norm">Combination</th>
					<th width="10%" class="th-td-norm">Debit</th>
					<th width="10%" class="th-td-norm">Credit</th>
					<th width="25%" class="th-td-edge">Description</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${generalLedgerDto.glEntries}" var="glEntry" varStatus="status">
					<tr>
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${glEntry.accountCombination.division.number} - ${glEntry.accountCombination.division.name}</td>
						<td class="th-td-norm v-align-top">${glEntry.accountCombination.account.number} - ${glEntry.accountCombination.account.accountName}</td>
						<td class="th-td-norm v-align-top">${glEntry.accountCombination.company.name} - ${glEntry.accountCombination.division.name} - ${glEntry.accountCombination.account.accountName}</td>
						<c:choose>
							<c:when test="${glEntry.debit}">
								<td class="td-numeric v-align-top">
									<c:set  var="debitAmount" value="${glEntry.amount}"/>
									<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${debitAmount}'/></td>
								<td class="td-numeric v-align-top">0.0</td>
								<c:set var="totalDebit" value="${totalDebit + debitAmount}" />
							</c:when>
							<c:otherwise>
								<td class="td-numeric v-align-top">0.0</td>
								<td class="td-numeric v-align-top">
									<c:set  var="creditAmount" value="${glEntry.amount}"/>
									<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${creditAmount}'/></td>
								<c:set var="totalCredit" value="${totalCredit + creditAmount}" />
							</c:otherwise>
						</c:choose>
						<td class="th-td-edge v-align-top">${glEntry.description}</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4"></td>
					<td class="txt-align-right">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalDebit}' />
					</td>
					<td class="txt-align-right">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalCredit}' />
					</td>
				</tr>
			</tfoot>
		</table>
	</fieldset>
	<c:if test="${fn:length(generalLedgerDto.referenceDocuments) gt 0}">
		<fieldset class="frmField_set">
			<legend>Documents</legend>
			<table class="dataTable" id="referenceDocuments">
				<thead>
					<tr>
						<th width="3%" class="th-td-norm">#</th>
						<th width="18%" class="th-td-norm">Name</th>
						<th width="18%" class="th-td-norm">Description</th>
						<th width="18%" class="th-td-edge">file</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${generalLedgerDto.referenceDocuments}" var="refDoc" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${refDoc.fileName}</td>
							<td class="th-td-norm v-align-top">${refDoc.description}</td>
							<td class="th-td-edge v-align-top" id="file">
								<a href="${refDoc.file}" download="${refDoc.fileName}" class="fileLink" id="file">${refDoc.fileName}</a>
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