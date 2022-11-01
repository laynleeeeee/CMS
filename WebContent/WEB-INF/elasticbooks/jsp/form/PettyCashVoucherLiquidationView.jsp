<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : Petty Cash Voucher Liquidation view form page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable2.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview2.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${pcvl.ebObjectId}");
	if ("${pcvl.formWorkflow.currentStatusId}" == 4) {
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
	<div class="modFormLabel">Petty Cash Voucher Liquidation - ${pcvl.division.name}</div>
	<br>
	<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="label">PCVL No.</td>
					<td class="label-value">${pcvl.sequenceNo}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${pcvl.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Petty Cash Voucher Header</legend>
			<table class="formTable" border=0>
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${pcvl.company.name}</td>
				</tr>
				<tr>
					<td class="label">Division </td>
					<td class="label-value">${pcvl.division.name}</td>
				</tr>
				<tr>
					<td class="label">PCV Reference</td>
					<td class="label-value">${pcvl.pcvNumber}</td>
				</tr>
				<tr>
					<td class="label">Custodian</td>
					<td class="label-value">${pcvl.userCustodian.custodianAccount.custodianName}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${pcvl.pcvlDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Requestor</td>
					<td class="label-value">${pcvl.requestor}</td>
				</tr>
				<tr>
					<td class="label">Reference</td>
					<td class="label-value">${pcvl.referenceNo}</td>
				</tr>
				<tr>
					<td class="label">Description</td>
					<td class="label-value">${pcvl.description}</td>
				</tr>
				<tr>
					<td class="label">Amount</td>
					<td class="label-value">
						<fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${pcvl.amount}'/>
					</td>
				</tr>
				<tr>
					<td class="label">Total Cash Returned</td>
					<td class="label-value">
						<fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${pcvl.cashReturned}'/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
		<legend>Petty Cash Voucher Liquidation Particulars</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="2%" class="th-td-norm">#</th>
					<th width="7%" class="th-td-norm">Issue Date</th>
					<th width="7%" class="th-td-norm">BMS No.</th>
					<th width="7%" class="th-td-norm">OR/SI</th>
					<th width="7%" class="th-td-norm">Supplier</th>
					<th width="7%" class="th-td-norm">TIN</th>
					<th width="7%" class="th-td-norm">Barangay, Street</th>
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
					<c:forEach items="${pcvl.pcvlLines}" var="pcvlLine" varStatus="status">
					<tr>
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.pcvDateString}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.bmsNumber}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.orNumber}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.supplierName}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.supplierTin}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.brgyStreet}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.city}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.description}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.divisionName}</td>
						<td class="th-td-norm v-align-top">${pcvlLine.accountName}</td>
						<td class="th-td-norm v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${pcvlLine.upAmount}'/></td>
						<td class="th-td-norm v-align-top">${pcvlLine.taxType.name}</td>
						<td class="th-td-norm v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${pcvlLine.vatAmount}'/></td>
						<td class="th-td-norm v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${pcvlLine.amount}'/></td>
					</tr>
						<c:set var="totalUpAmount" value="${totalUpAmount + pcvlLine.upAmount}" />
						<c:set var="totalVatAmount" value="${totalVatAmount + pcvlLine.vatAmount}" />
						<c:set var="subTotal" value="${subTotal + pcvlLine.amount}" />
					</c:forEach>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" style="font-weight: bold;">Total</td>
					<td colspan="9"></td>
					<td class="txt-align-right" style="font-weight: bold;">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalUpAmount}' />
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
		<c:if test="${not empty pcvl.referenceDocuments}">
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
						<c:forEach items="${pcvl.referenceDocuments}" var="refDoc" varStatus="status">
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
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${subTotal}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total VAT</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalVatAmount}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total Liquidation</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${subTotal + totalVatAmount}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total Cash Returned</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${pcvl.cashReturned}"/>
				</td>
			</tr>
		</table>
	</fieldset>
	</div>
</div>
</body>
</html>