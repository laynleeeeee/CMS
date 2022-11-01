<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: AP Invoice form for viewing only.
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
</style>
</head>
<body>
<div class="formDivBigForms">
	<div class="modFormLabel">AP Invoice</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">Sequence Number</td>
				<td class="label-value">${apInvoice.sequenceNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${apInvoice.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Invoice Header</legend>
		<table>
			<tr>
				<td class="label">Invoice Type</td>
				<td class="label-value">${apInvoice.invoiceType.name}</td>
			</tr>
			<tr>
				<td class="label">Invoice No</td>
				<td class="label-value">${apInvoice.invoiceNumber}</td>
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
					<th width="3%" class="th-td-norm">#</th>
					<th width="18%" class="th-td-norm">Company</th>
					<th width="18%" class="th-td-norm">Division</th>
					<th width="20%" class="th-td-norm">Account</th>
					<th width="15%" class="th-td-norm">Amount</th>
					<th width="25%" class="th-td-edge">Description</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${apInvoice.aPlines}" var="aPLine" varStatus="status">
					<tr> 
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${aPLine.companyNumber} - ${aPLine.accountCombination.company.name}</td>
						<td class="th-td-norm v-align-top">${aPLine.divisionNumber} - ${aPLine.accountCombination.division.name}</td>
						<td class="th-td-norm v-align-top">${aPLine.accountNumber} - ${aPLine.accountCombination.account.accountName}</td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${aPLine.amount}'/></td>
						<td class="th-td-edge">${aPLine.description}</td>
						<c:set var="total" value="${total + aPLine.amount}" />
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			  <tr>
                   <td colspan="4"></td>
                   <td class="txt-align-right">
                   		<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${total}' />
                   </td>
               </tr>
			</tfoot>
		</table>
	</fieldset>
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
				<c:forEach items="${apInvoice.referenceDocuments}" var="refDoc" varStatus="status">
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
</div>
</body>
</html>