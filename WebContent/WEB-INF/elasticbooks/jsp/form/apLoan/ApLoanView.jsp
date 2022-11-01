<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : AP Loan view form jsp page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable2.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview2.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${apInvoice.ebObjectId}");
	if ("${apInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	//disable reference table link
	disableRefTblLink("${hasEditAccess}", "referenceDocuments", "file");
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
	<div class="modFormLabel">${title}</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="labels">APL No.</td>
				<td class="label-value">${apInvoice.sequenceNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${apInvoice.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>AP Loan Header</legend>
		<table>
			<tr>
				<td class="label">Company</td>
				<td class="label-value">${apInvoice.company.name}</td>
			</tr>
			<tr>
				<td class="label">Division</td>
				<td class="label-value">${apInvoice.division.name}</td>
			</tr>
			<tr>
				<td class="label">Loan Proceeds Reference</td>
				<td class="label-value">${apInvoice.lpNumber}</td>
			</tr>
			<tr>
			<tr>
				<td class="label">Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.glDate}"/></td>
			</tr>
			<tr>
				<td class="label">Lender</td>
				<td class="label-value">${apInvoice.supplier.name}</td>
			</tr>
			<tr>
				<td class="label">Lender Account</td>
				<td class="label-value">${apInvoice.supplierAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Currency</td>
				<td class="label-value">${apInvoice.currency.name}</td>
			</tr>
			<tr>
				<td class="label">Principal Loan</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${apInvoice.principalLoan}'/></td>
			</tr>
			<tr>
				<td class="label">Principal Payment</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${apInvoice.principalPayment}'/></td>
			</tr>
			<tr>
				<td class="label">Outstanding balance</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${apInvoice.principalLoan - apInvoice.principalPayment}'/></td>
			</tr>
			<tr>
				<td class="label">Description</td>
				<td class="label-value">${apInvoice.description}</td>
			</tr>
		</table>
	</fieldset>
	<c:if test="${not empty apInvoice.aPlines}">
		<fieldset class="frmField_set">
			<legend>Loan Details</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="20%" class="th-td-norm">Loan Charges</th>
						<th width="15%" class="th-td-norm">Charge Type</th>
						<th width="15%" class="th-td-norm">Gross Amount</th>
						<th width="15%" class="th-td-norm">Tax Type</th>
						<th width="15%" class="th-td-norm">VAT Amount</th>
						<th width="18%" class="th-td-norm">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${apInvoice.aPlines}" var="aPLine" varStatus="status">
						<tr> 
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${aPLine.accountCombination.account.accountName}</td>
							<td class="th-td-norm v-align-top">${aPLine.loanChargeType.name}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='6' value='${aPLine.grossAmount}'/>
							</td>
							<td class="th-td-norm v-align-top">${aPLine.taxType.name}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${aPLine.vatAmount}'/>
							</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${aPLine.amount}'/>
							</td>
							<c:set var="totalVatAmount" value="${totalVatAmount + aPLine.vatAmount}" />
							<c:set var="totalCharges" value="${totalCharges + aPLine.amount}" />
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2">Total</td>
						<td colspan="3"></td>
						<td class="txt-align-right">
							<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalAmount}' />
						</td>
					</tr>
				</tfoot>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${not empty apInvoice.referenceDocuments}">
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
					<c:forEach items="${apInvoice.referenceDocuments}" var="refDoc" varStatus="status">
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
	</c:if>
	<br>
	<c:set var="subtotal" value="${totalCharges + apInvoice.principalPayment}" />
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
				<td class="footerViewCls">${apInvoice.wtAcctSetting.name}</td>
				<td class="footerViewCls">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${apInvoice.wtAmount}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total Amount Due</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${(subtotal + totalVatAmount) - apInvoice.wtAmount}"/>
				</td>
			</tr>
		</table>
	</fieldset>
</div>
</body>
</html>