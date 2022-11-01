<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : AR Transaction view form page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable2.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview2.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	// Disable this method;
	doFormPreUpdateStatus = null;

	formObjectId = parseInt("${arTransaction.ebObjectId}");
	if ("${arTransaction.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	disableRefTblLink("${hasEditAccess}", "referenceDocuments", "file");
	if("${hasReference}" == "false"){
		$("#referenceDocumentsFieldset").hide();
	}
});
</script>
<style type="text/css">
.footerViewCls {
	font-size: 12;
	font-weight: bold;
	text-align: right;
	width: 300px;
}
</style>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">AR Transaction - ${arTransaction.division.name}</div>
	<br>
	<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="label">Sequence Number</td>
					<td class="label-value">${arTransaction.sequenceNumber}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${arTransaction.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Transaction Header</legend>
			<table class="formTable" border=0>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${arTransaction.company.name}</td>
				</tr>
				<tr>
					<td class="label">Division </td>
					<td class="label-value">${arTransaction.division.name}</td>
				</tr>
				<tr>
					<td class="label">Transaction Type</td>
					<td class="label-value">${arTransaction.transactionClassification.name}</td>
				</tr>
				<tr>
					<td class="label">Transaction No</td>
					<td class="label-value">${arTransaction.transactionNumber}</td>
				</tr>
				<tr>
					<td class="label">Customer Name</td>
					<td class="label-value">${arTransaction.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${arTransaction.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${arTransaction.term.name}</td>
				</tr>
				<tr>
					<td class="label">Transaction Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${arTransaction.transactionDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">GL Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${arTransaction.glDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Due Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${arTransaction.dueDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Description</td>
					<td class="label-value">${arTransaction.description}</td>
				</tr>
				<tr>
					<td class="label">Currency </td>
					<td class="label-value">${arTransaction.currency.name}</td>
				</tr>
				<tr>
					<td class="label">Amount</td>
					<td class="label-value">
						<fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${arTransaction.amount}'/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>AR Lines</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="28%" class="th-td-norm">AR Line</th>
						<th width="10%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<th width="10%" class="th-td-norm">Gross Price</th>
						<th width="15%" class="th-td-norm">Tax Type</th>
						<th width="10%" class="th-td-norm">VAT Amount</th>
						<th width="15%" class="th-td-norm">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${arTransaction.arServiceLines}" var="arServiceLine" varStatus="status">
						<tr>
							<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- AR Line Setup -->
							<td class="th-td-norm v-align-top">${arServiceLine.serviceSetting.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${arServiceLine.quantity}" /></td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${arServiceLine.unitMeasurement.name}</td>
							<!-- UP Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="4" 
										maxFractionDigits="6" value="${arServiceLine.upAmount}" /></td>
							<td class="th-td-norm v-align-top">${arServiceLine.taxType.name}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${arServiceLine.vatAmount}" />
							</td>
							<!-- Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${arServiceLine.amount}" /></td>
						</tr>
						<c:set var="totalVat" value="${totalVat + arServiceLine.vatAmount}" />
						<c:set var="total" value="${total + arServiceLine.amount}" />
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
		<br>
		<fieldset class="frmField_set" id="referenceDocumentsFieldset">
		<legend>Documents</legend>
		<table class="dataTable" id="referenceDocuments">
			<thead>
				<tr>
					<th width="3%" class="th-td-norm">#</th>
					<th width="18%" class="th-td-norm">Name</th>
					<th width="18%" class="th-td-norm">Description</th>
					<th width="18%" class="th-td-norm">file</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${arTransaction.referenceDocuments}" var="refDoc" varStatus="status">
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
					<td class="footerViewCls" colspan="4">Sub Total</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${total}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="4">Total VAT</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalVat}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="4">Withholding VAT</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${arTransaction.wtVatAmount}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="4">Withholding Tax</td>
					<td class="footerViewCls">${arTransaction.wtAcctSetting.name}</td>
					<td class="footerViewCls">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${arTransaction.wtAmount}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="4">Total Amount Due</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${(total + totalVat) - arTransaction.wtAmount - arTransaction.wtVatAmount}"/>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</div>
</body>
</html>