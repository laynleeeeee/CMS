<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>

<!-- Description: AP Invoice - Goods view form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<style type="text/css">
.footerTableView {
 	color: black;
	text-align: right;
	font-family: sans-serif;
	font-style: normal;
	font-size: small;
	font-weight: bold;
	margin-left: auto;
	margin-right: auto;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${apInvoiceItemDto.apInvoice.ebObjectId}");
	if ("${apInvoiceItemDto.apInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}

	var wtAmount = parseFloat("${apInvoiceItemDto.apInvoice.wtAmount}");
	if(!isNaN(wtAmount)) {
		$("#wtAmount").html(accounting.formatMoney(wtAmount, '', 4));
	} else {
		$("#wtAmount").html("0.0000");
	}
});
</script>
</head>
<body>
<div class="formDivBigForms">
	<div class="modFormLabel">AP Invoice - Goods</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">Sequence Number</td>
				<td class="label-value">${apInvoiceItemDto.apInvoice.sequenceNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${apInvoiceItemDto.apInvoice.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Invoice Header</legend>
		<table>
			<tr>
				<td class="label">SOA No.</td>
				<td class="label-value" style="word-break:break-all;">${apInvoiceItemDto.apInvoice.invoiceNumber}</td>
			</tr>
			<tr>
				<td class="label">Supplier Name</td>
				<td class="label-value">${apInvoiceItemDto.apInvoice.supplier.numberAndName}</td>
			</tr>
			<tr>
				<td class="label">Supplier's Account</td>
				<td class="label-value">${apInvoiceItemDto.apInvoice.supplierAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Term</td>
				<td class="label-value">${apInvoiceItemDto.apInvoice.term.name}</td>
			</tr>
			<tr>
				<td class="label">SOA Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy"
					value="${apInvoiceItemDto.apInvoice.invoiceDate}"/></td>
			</tr>
			<tr>
				<td class="label">GL Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy"
					value="${apInvoiceItemDto.apInvoice.glDate}"/></td>
			</tr>
			<tr>
				<td class="label">Due Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy"
					value="${apInvoiceItemDto.apInvoice.dueDate}"/></td>
			</tr>
			<tr>
				<td class="label">Description</td>
				<td class="label-value">${apInvoiceItemDto.apInvoice.description}</td>
			</tr>
			<tr>
				<td class="label">Amount</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='4'
					maxFractionDigits='4' value='${apInvoiceItemDto.apInvoice.amount}'/></td>
			</tr>
		</table>
	</fieldset>
	<c:if test="${fn:length(apInvoiceItemDto.apInvoice.apInvoiceItems) gt 0}">
		<fieldset class="frmField_set">
			<legend>AP Invoices</legend>
			<table class="dataTable" id="apInvoices">
				<thead>
					<tr>
						<th width="3" class="th-td-norm">#</th>
						<th width="57%" class="th-td-norm">Invoice Number</th>
						<th width="40%" class="th-td-edge">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${apInvoiceItemDto.apInvoice.apInvoiceItems}" var="apInvoiceItems" varStatus="status">
						<tr> 
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${apInvoiceItems.invoiceNumber}</td>
							<td class="td-numeric v-align-top" style="border-right: 0;"><fmt:formatNumber type='number' minFractionDigits='4'
								maxFractionDigits='4' value='${apInvoiceItems.amount}'/></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${fn:length(apInvoiceItemDto.apInvoice.apInvoiceLines) gt 0}">
		<fieldset class="frmField_set">
		<legend>Other Charges</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="25%" class="th-td-norm">AP Line</th>
						<th width="15%" class="th-td-norm">Qty</th>
						<th width="15%" class="th-td-norm">UOM</th>
						<th width="15%" class="th-td-norm">UP</th>
						<th width="20%" class="th-td-edge">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${apInvoiceItemDto.apInvoice.apInvoiceLines}" var="apl" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- AR Line Setup -->
							<td class="th-td-norm v-align-top">${apl.apLineSetup.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${apl.quantity}" /></td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${apl.unitMeasurement.name}</td>
							<!-- UP Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${apl.upAmount}" /></td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${apl.amount}" /></td>
						</tr>
						<c:set var="otherCharges" value="${otherCharges + apl.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4">Total</td>
						<td colspan="2" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${otherCharges}" /></td>
					</tr>
				</tfoot>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${fn:length(apInvoiceItemDto.apInvoice.referenceDocuments) gt 0}">
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
					<c:forEach items="${apInvoiceItemDto.apInvoice.referenceDocuments}" var="refDoc" varStatus="status">
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
	<br>
	<table class="footerTableView">
		<tr>
		<td style="width: 73%">Withholding tax&nbsp;&nbsp;</td>
		<td style="width: 12%">${apInvoiceItemDto.apInvoice.wtAcctSetting.name}</td>
		<td style="width: 10%"><span id="wtAmount"></span>
		<td style="width: 5%"></td>
		</tr>
		<tr>
			<td style="width: 73%">Total Amount Due&nbsp;&nbsp;</td>
			<td style="width: 12%"></td>
			<td style="width: 10%"><fmt:formatNumber type="number" minFractionDigits="4" 
							maxFractionDigits="4" value="${apInvoiceItemDto.apInvoice.amount}"/></td>
			<td style="width: 5%"></td>
		</tr>
	</table>
</div>
</body>
</html>