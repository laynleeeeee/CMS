<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : Accounts payable invoice view form jsp page -->
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
	if("${hasReference}" == "false"){
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
	<div class="modFormLabel">${title}</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<c:choose>
					<c:when test="${isNonPO}">
						<td class="labels">API-NPO No.</td>
					</c:when>
					<c:otherwise>
						<td class="labels">API-C No.</td>
					</c:otherwise>
				</c:choose>
				<td class="label-value">${apInvoice.sequenceNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${apInvoice.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<c:choose>
			<c:when test="${isNonPO}">
				<legend>AP Invoice - Non PO Header</legend>
			</c:when>
			<c:otherwise>
				<legend>AP Invoice - Confidential Header</legend>
			</c:otherwise>
		</c:choose>
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
				<td class="label">Invoice Type</td>
				<td class="label-value">${apInvoice.invoiceClassification.name}</td>
			</tr>
			<tr>
				<td class="label">SI No. / SOA Ref No.</td>
				<td class="label-value">${apInvoice.invoiceNumber}</td>
			</tr>
			<tr>
				<td class="label">BMS No.</td>
				<td class="label-value">${apInvoice.bmsNumber}</td>
			</tr>
			<tr>
				<td class="label">Supplier Name</td>
				<td class="label-value">${apInvoice.supplier.numberAndName}</td>
			</tr>
			<tr>
				<td class="label">Supplier's Account</td>
				<td class="label-value">${apInvoice.supplierAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Term</td>
				<td class="label-value">${apInvoice.term.name}</td>
			</tr>
			<tr>
				<td class="label">Invoice Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.invoiceDate}"/></td>
			</tr>
			<tr>
				<td class="label">GL Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.glDate}"/></td>
			</tr>
			<tr>
				<td class="label">Due Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.dueDate}"/></td>
			</tr>
			<tr>
				<td class="label">Description</td>
				<td class="label-value">${apInvoice.description}</td>
			</tr>
			<tr>
				<td class="label">Currency</td>
				<td class="label-value">${apInvoice.currency.name}</td>
			</tr>
			<tr>
				<td class="label">Amount</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${apInvoice.amount}'/></td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>AP Lines</legend>
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
				<c:forEach items="${apInvoice.aPlines}" var="aPLine" varStatus="status">
					<tr> 
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${aPLine.divisionNumber} - ${aPLine.accountCombination.division.name}</td>
						<td class="th-td-norm v-align-top">${aPLine.accountNumber} - ${aPLine.accountCombination.account.accountName}</td>
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
						<td class="th-td-norm">${aPLine.description}</td>
						<c:set var="totalVatAmount" value="${totalVatAmount + aPLine.vatAmount}" />
						<c:set var="totalAmount" value="${totalAmount + aPLine.amount}" />
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
	<br>
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
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalAmount}"/>
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
						value="${(totalAmount + totalVatAmount) - apInvoice.wtAmount}"/>
				</td>
			</tr>
		</table>
	</fieldset>
</div>
</body>
</html>