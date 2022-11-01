
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: AP payment form for viewing only.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
a#btnPrintCheck{
	color: blue;
}

a#btnPrintCheck:hover {
	color: blue;
	cursor: pointer;
}

.disabled-link {
	pointer-events: none;
	cursor: default;
}
</style>
<script type="text/javascript">
var checkbookTemplateId = null;
$(document).ready(function () {
	formObjectId = parseInt("${directPayment.directPayment.ebObjectId}");
	$("#divPayeeId").hide();
	if("${directPayment.directPayment.directPaymentTypeId}" == 2) {
		$("#divPayeeId").show();

		if("${directPayment.payment.specifyPayee}" == "true"){
			specifyPayee.setAttribute("checked", "checked");
		}
	}

	checkbookTemplateId = "${directPayment.payment.checkbook.checkbookTemplateId}";
	if (checkbookTemplateId == null || checkbookTemplateId == 0) {
		$("#btnPrintCheck").hide(); 
	}

	if ("${directPayment.payment.formWorkflow.currentStatusId}" == 4
			|| "${directPayment.payment.formWorkflow.currentStatusId}" == 9) {
		$("#btnPrintCheck").hide();
	}

	//disable reference table link
	disableRefTblLink("${hasEditAccess}", "referenceDocuments", "file");
});

function printCheck () {
	var supplierName = encodeURIComponent(specifyPayee.checked ? "${directPayment.payment.payee}" :
		"${directPayment.payment.supplier.name}");
	window.open(contextPath + "/checkPrinting/new?checkbookTemplateId="
			+checkbookTemplateId+"&name="+supplierName+"&amount="+"${directPayment.payment.amount}"
			+"&checkDate="+$("#tdCheckDate").text());
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">Direct Payment</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">Voucher Number</td>
					<td class="label-value">${directPayment.payment.voucherNumber}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${directPayment.payment.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Payment Header</legend>
		<table>
			<tr>
				<td class="label">Payment Type</td>
				<td class="label-value">${directPayment.paymentType}</td>
			</tr>
			<tr>
				<td class="label">Payment Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${directPayment.payment.paymentDate}"/></td>
			</tr>
			<tr>
				<td class="label">Invoice No.</td>
				<td class="label-value">${directPayment.directPayment.invoiceNo}</td>
			</tr>
			<c:if test="${directPayment.directPayment.directPaymentTypeId == 1}">
				<tr>
					<td class="label">Payment Account</td>
					<td class="label-value">${directPayment.payment.bankAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${directPayment.termName}</td>
				</tr>
			</c:if>
			<c:if test="${directPayment.directPayment.directPaymentTypeId == 2}">
				<tr>
					<td class="label">Bank Account</td>
					<td class="label-value">${directPayment.payment.bankAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Checkbook</td>
					<td class="label-value">${directPayment.payment.checkbook.name}</td>
				</tr>
				<tr>
					<td class="label">Check Number</td>
					<td class="label-value">${directPayment.payment.checkNumber}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${directPayment.termName}</td>
				</tr>
				<tr>
					<td class="label">Check Date</td>
					<td class="label-value" id="tdCheckDate"><fmt:formatDate pattern="MM/dd/yyyy" value="${directPayment.payment.checkDate}"/></td>
				</tr>
			</c:if>
			<tr>
				<td class="label">Supplier Name</td>
				<td class="label-value">${directPayment.payment.supplier.numberAndName}</td>
			</tr>
			<tr>
				<td class="label">Supplier's Account</td>
				<td class="label-value">${directPayment.payment.supplierAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Description</td>
				<td class="label-value">${directPayment.directPayment.description}</td>
			</tr>
			<tr>
				<td class="label">Amount</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${directPayment.payment.amount}'/></td>
			</tr>
			<tr>
				<td class="label"></td>
				<td style="font-weight: bold;" class="label-value">
					<a id="btnPrintCheck" onclick="printCheck();">Click here to print check</a>
				</td>
			</tr>
		</table>
	</fieldset>
	<div id="divPayeeId">
		<fieldset class="frmField_set">
			<legend>Check format option</legend>
			<table>
				<tr>
					<td class="label">Specify payee</td>
					<td class="label-value">
						<input type="checkbox" id="specifyPayee"
							name="specifyPayee"/>
					</td>
				</tr>
				<tr>
					<td class="label">Payee</td>
						<td class="label-value">${directPayment.payment.payee}</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<fieldset class="frmField_set">
		<legend>AP Lines</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="3%" class="th-td-norm">#</th>
					<th width="18%" class="th-td-norm">Division</th>
					<th width="20%" class="th-td-norm">Account</th>
					<th width="20%" class="th-td-norm">Combination</th>
					<th width="15%" class="th-td-norm">Amount</th>
					<th width="25%" class="th-td-edge">Description</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${directPayment.directPayment.paymentLines}" var="aPLine" varStatus="status">
					<tr> 
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${aPLine.divisionNumber}</td>
						<td class="th-td-norm v-align-top">${aPLine.accountNumber}</td>
						<td class="th-td-norm v-align-top">${aPLine.accountCombination.division.name} - ${aPLine.accountCombination.account.accountName}</td>
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
				<c:forEach items="${directPayment.referenceDocuments}" var="refDoc" varStatus="status">
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