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
	if("${isEmptyIID}" == "true"){
		$("#IIDFieldset").hide();
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
				<td class="label">API-I No.</td>
				<td class="label-value">${apInvoice.sequenceNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${apInvoice.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>AP Invoice - Importation Header</legend>
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
	<fieldset class="frmField_set" id="IIDFieldset">
		<legend>Invoice Importation Details</legend>
		<table>
			<tr>
				<td>
					<table>
						<tr>
							<td class="label">Import Entry No.</td>
							<td class="label-value">${apInvoice.invoiceImportationDetails.importEntryNo}</td>
						</tr>
						<tr>
							<td class="label">Assessment Release Date</td>
							<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.invoiceImportationDetails.assessmentReleaseDate}"/></td>
						</tr>
						<tr>
							<td class="label">Registered Name</td>
							<td class="label-value">${apInvoice.invoiceImportationDetails.registeredName}</td>
						</tr>
						<tr>
							<td class="label">Importation Date</td>
							<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.invoiceImportationDetails.importationDate}"/></td>
						</tr>
						<tr>
							<td class="label">Country of Origin</td>
							<td class="label-value">${apInvoice.invoiceImportationDetails.countryOfOrigin}</td>
						</tr>
						<tr>
							<td class="label">Total Landed Cost</td>
							<td class="label-value">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value='${apInvoice.invoiceImportationDetails.totalLandedCost}'/>
							</td>
						</tr>
					</table>
				</td>
				<td>
					<table>
						<tr>
							<td class="label">Dutiable Value</td>
							<td class="label-value">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value='${apInvoice.invoiceImportationDetails.dutiableValue}'/>
							</td>
						</tr>
						<tr>
							<td class="label">Charges Before Release from Custom</td>
							<td class="label-value">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value='${apInvoice.invoiceImportationDetails.chargesFromCustom}'/>
							</td>
						</tr>
						<tr>
							<td class="label">Taxable Import</td>
							<td class="label-value">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value='${apInvoice.invoiceImportationDetails.taxableImport}'/>
							</td>
						</tr>
						<tr>
							<td class="label">Exempt Import</td>
							<td class="label-value">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value='${apInvoice.invoiceImportationDetails.exemptImport}'/>
							</td>
						</tr>
						<tr>
							<td class="label">OR No.</td>
							<td class="label-value">${apInvoice.invoiceImportationDetails.orNumber}</td>
						</tr>
						<tr>
							<td class="label">Date of Payment</td>
							<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.invoiceImportationDetails.paymentDate}"/></td>
							
						</tr>
					</table>
				</td>
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
					<th width="35%" class="th-td-norm">Account</th>
					<th width="15%" class="th-td-norm">Tax Type</th>
					<th width="15%" class="th-td-norm">VAT Amount</th>
					<th width="18%" class="th-td-norm">Description</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${apInvoice.aPlines}" var="aPLine" varStatus="status">
					<tr> 
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${aPLine.divisionNumber} - ${aPLine.accountCombination.division.name}</td>
						<td class="th-td-norm v-align-top">${aPLine.accountNumber} - ${aPLine.accountCombination.account.accountName}</td>
						<td class="th-td-norm v-align-top">${aPLine.taxType.name}</td>
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${aPLine.vatAmount}'/>
						</td>
						<td class="th-td-norm">${aPLine.description}</td>
						<c:set var="totalVatAmount" value="${totalVatAmount + aPLine.vatAmount}" />
					</tr>
				</c:forEach>
			</tbody>
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
				<td class="footerViewCls" colspan="3">Total Amount Due</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${totalVatAmount}"/>
				</td>
			</tr>
		</table>
	</fieldset>
</div>
</body>
</html>