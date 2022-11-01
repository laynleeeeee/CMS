<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : Loan Proceeds view form jsp page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable2.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview2.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	// Disable this function; this is only applicable to AP payment
	doFormPreUpdateStatus = null;

	formObjectId = parseInt("${loanProceeds.ebObjectId}");
	if ("${loanProceeds.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	//disable reference table link
	disableRefTblLink("${hasEditAccess}", "referenceDocuments", "file");
	if ("${hasReference}" == "false"){
		$("#referenceDocumentsFieldset").hide();
	}
});

</script>
<style type="text/css">
.disabled-link {
	pointer-events: none;
	cursor: default;
}

.footerViewCls {
	font-size: 12;
	font-weight: bold;
	text-align: right;
	width: 300px;
}
</style>
</head>
<body>
<div class="formDivBigForms">
	<div class="modFormLabel">Loan Proceeds - ${loanProceeds.division.name}</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
					<td class="labels">LP No.</td>
				<td class="label-value">${loanProceeds.sequenceNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${loanProceeds.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Loan Proceeds Header</legend>
		<table>
			<tr>
				<td class="label">Company</td>
				<td class="label-value">${loanProceeds.company.name}</td>
			</tr>
			<tr>
				<td class="label">Division</td>
				<td class="label-value">${loanProceeds.division.name}</td>
			</tr>
			<tr>
				<td class="label">Lender</td>
				<td class="label-value">${loanProceeds.supplier.numberAndName}</td>
			</tr>
			<tr>
				<td class="label">Lender Account</td>
				<td class="label-value">${loanProceeds.supplierAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Receipt Method</td>
				<td class="label-value">${loanProceeds.receiptMethod.name}</td>
			</tr>
			<tr>
				<td class="label">Loan Account</td>
				<td class="label-value">${loanProceeds.loanAccount.accountName}</td>
			</tr>
			<tr>
				<td class="label">Term</td>
				<td class="label-value">${loanProceeds.term.name}</td>
			</tr>
			<tr>
				<td class="label">Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${loanProceeds.date}"/></td>
			</tr>
			<tr>
				<td class="label">GL Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${loanProceeds.glDate}"/></td>
			</tr>
			<tr>
				<td class="label">Description</td>
				<td class="label-value">${loanProceeds.description}</td>
			</tr>
			<tr>
				<td class="label">Currency</td>
				<td class="label-value">${loanProceeds.currency.name}</td>
			</tr>
			<tr>
				<td class="label">Loan Amount</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${loanProceeds.amount}'/></td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Loan Charge/s</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="2%" class="th-td-norm">#</th>
					<th width="15%" class="th-td-norm">Division</th>
					<th width="30%" class="th-td-norm">Account</th>
					<th width="10%" class="th-td-norm">Gross Amount</th>
					<th width="10%" class="th-td-norm">Tax Type</th>
					<th width="8%" class="th-td-norm">VAT Amount</th>
					<th width="10%" class="th-td-norm">Amount</th>
					<th width="15%" class="th-td-norm">Description</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${loanProceeds.lPlines}" var="lPline" varStatus="status">
					<tr> 
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${lPline.divisionNumber} - ${lPline.accountCombination.division.name}</td>
						<td class="th-td-norm v-align-top">${lPline.accountNumber} - ${lPline.accountCombination.account.accountName}</td>
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type='number' minFractionDigits='4' maxFractionDigits='6' value='${lPline.grossAmount}'/>
						</td>
						<td class="th-td-norm v-align-top">${lPline.taxType.name}</td>
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${lPline.vatAmount}'/>
						</td>
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${lPline.amount}'/>
						</td>
						<td class="th-td-norm">${lPline.description}</td>
						<c:set var="totalVatAmount" value="${totalVatAmount + lPline.vatAmount}" />
						<c:set var="totalCharges" value="${totalCharges + lPline.amount}" />
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">Total</td>
					<td colspan="4"></td>
					<td class="txt-align-right">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalAmount}' />
					</td>
				</tr>
			</tfoot>
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
					<th width="18%" class="th-td-norm">File</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${loanProceeds.referenceDocuments}" var="refDoc" varStatus="status">
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
	<br>
	<c:set var="subtotal" value="${loanProceeds.amount - totalCharges}" />
	<fieldset class="frmField_set" style="border: 0;">
		<table>
			<tr>
				<td class="footerViewCls"></td>
				<td class="footerViewCls"></td>
				<td class="footerViewCls"></td>
				<td class="footerViewCls"></td>
				<td class="footerViewCls"></td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Sub Total</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${subtotal}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total VAT</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalVatAmount}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Withholding Tax</td>
				<td class="footerViewCls">${loanProceeds.wtAcctSetting.name}</td>
				<td class="footerViewCls">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${loanProceeds.wtAmount}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Loan Proceeds</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${subtotal + totalVatAmount - loanProceeds.wtAmount}"/>
				</td>
			</tr>
		</table>
	</fieldset>
</div>
</body>
</html>