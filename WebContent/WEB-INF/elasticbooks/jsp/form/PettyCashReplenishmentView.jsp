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
	<div class="modFormLabel">Petty Cash Replenishment - ${apInvoice.division.name}</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="labels">PCR No.</td>
				<td class="label-value">${apInvoice.sequenceNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${apInvoice.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Petty Cash Replenishment Header</legend>
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
				<td class="label">Custodian</td>
				<td class="label-value">${apInvoice.userCustodian.custodianAccount.custodianName}</td>
			</tr>
			<tr>
				<td class="label">GL Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.glDate}"/></td>
			</tr>
			<tr>
				<td class="label">PCF Amount</td>
				<td class="label-value">
				<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${pcfAmount}' /></td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Petty Cash Details</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="2%" class="th-td-norm">#</th>
					<th width="6%" class="th-td-norm">Date</th>
					<th width="6%" class="th-td-norm">PCVL No.</th>
					<th width="6%" class="th-td-norm">BMS No.</th>
					<th width="6%" class="th-td-norm">OR/SI</th>
					<th width="6%" class="th-td-norm">Supplier</th>
					<th width="6%" class="th-td-norm">TIN</th>
					<th width="6%" class="th-td-norm">Barangay, Street</th>
					<th width="7%" class="th-td-norm">City, Province, And Zip Code</th>
					<th width="7%" class="th-td-norm">Description</th>
					<th width="7%" class="th-td-norm">Division</th>
					<th width="7%" class="th-td-norm">Account</th>
					<th width="7%" class="th-td-norm">Gross Amount</th>
					<th width="7%" class="th-td-norm">Tax Type</th>
					<th width="7%" class="th-td-norm">VAT Amount</th>
					<th width="7%" class="th-td-norm">Amount</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<c:forEach items="${apInvoice.pcrls}" var="pcrls" varStatus="status">
					<tr>
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${pcrls.pcvlDateString}</td>
						<td class="th-td-norm v-align-top">${pcrls.sequenceNo}</td>
						<td class="th-td-norm v-align-top">${pcrls.bmsNumber}</td>
						<td class="th-td-norm v-align-top">${pcrls.orNumber}</td>
						<td class="th-td-norm v-align-top">${pcrls.supplierName}</td>
						<td class="th-td-norm v-align-top">${pcrls.supplierTin}</td>
						<td class="th-td-norm v-align-top">${pcrls.brgyStreet}</td>
						<td class="th-td-norm v-align-top">${pcrls.city}</td>
						<td class="th-td-norm v-align-top">${pcrls.description}</td>
						<td class="th-td-norm v-align-top">${pcrls.divisionName}</td>
						<td class="th-td-norm v-align-top">${pcrls.accountName}</td>
						<td class="th-td-norm v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${pcrls.grossAmount}'/></td>
						<td class="th-td-norm v-align-top">${pcrls.taxName}</td>
						<td class="th-td-norm v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${pcrls.vatAmount}'/></td>
						<td class="th-td-norm v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${pcrls.amount}'/></td>
					</tr>
						<c:set var="totalGrossAmount" value="${totalGrossAmount + pcrls.grossAmount}" />
						<c:set var="totalVatAmount" value="${totalVatAmount + pcrls.vatAmount}" />
						<c:set var="subTotal" value="${subTotal + pcrls.amount}" />
					</c:forEach>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" style="font-weight: bold;">Total</td>
					<td colspan="10"></td>
					<td class="txt-align-right" style="font-weight: bold;">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalGrossAmount}' />
					</td>
					<td></td>
					<td class="txt-align-right" style="font-weight: bold;">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalVatAmount}' />
					</td>
					<td class="txt-align-right" style="font-weight: bold;">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${subTotal}' />
					</td>
				</tr>
			</tfoot>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>AP Lines</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="3%" class="th-td-norm">#</th>
					<th width="18%" class="th-td-norm">Account Number</th>
					<th width="18%" class="th-td-norm">Account</th>
					<th width="20%" class="th-td-norm">Gross Amount</th>
					<th width="15%" class="th-td-norm">VAT Amount</th>
					<th width="25%" class="th-td-edge">Amount</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${apInvoice.summarizedApLines}" var="aPLine" varStatus="status">
					<tr> 
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${aPLine.accountNumber}</td>
						<td class="th-td-norm v-align-top">${aPLine.accountName}</td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${aPLine.grossAmount}'/></td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${aPLine.vatAmount}'/></td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${aPLine.amount}'/></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" style="font-weight: bold;">Total</td>
					<td colspan="1"></td>
					<td class="txt-align-right" style="font-weight: bold;">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalGrossAmount}' />
					</td>
					<td class="txt-align-right" style="font-weight: bold;">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalVatAmount}' />
					</td>
					<td class="txt-align-right" style="font-weight: bold;">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${subTotal}' />
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
				<td class="footerViewCls" colspan="3">Net Total</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${subTotal}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">VAT</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalVatAmount}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total Liquidation</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalGrossAmount}"/>
				</td>
			</tr>
		</table>
	</fieldset>
</div>
</body>
</html>