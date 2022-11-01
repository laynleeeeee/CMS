<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Return to supplier jsp view form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${apInvoice.ebObjectId}");
	if ("${apInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
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
<div class="formDivBigForms">
	<div class="modFormLabel">Return To Supplier</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">RTS No.</td>
				<td class="label-value">${apInvoice.returnToSupplier.formattedRTSNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${apInvoice.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Return To Supplier Header</legend>
		<table>
			<tr>
				<td class="label">Company</td>
				<td class="label-value">${apInvoice.returnToSupplier.company.name}</td>
			</tr>
			<tr>
				<td class="label">Warehouse</td>
				<td class="label-value">${apInvoice.returnToSupplier.warehouse.name}</td>
			</tr>
			<tr>
				<td class="label">RR No.</td>
				<td class="label-value">${apInvoice.returnToSupplier.rrNumber}</td>
			</tr>
			<tr>
				<td class="label">Supplier</td>
				<td class="label-value">${apInvoice.supplier.name}</td>
			</tr>
			<tr>
				<td class="label">Supplier Account</td>
				<td class="label-value">${apInvoice.supplierAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Supplier Invoice No.</td>
				<td class="label-value">${apInvoice.invoiceNumber}</td>
			</tr>
			<tr>
				<td class="label">Invoice Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.invoiceDate}"/></td>
			</tr>
			<tr>
				<td class="label">Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.glDate}"/></td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Return To Supplier Table</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="2%" class="th-td-norm">#</th>
					<th width="13%" class="th-td-norm">Stock Code</th>
					<th width="17%" class="th-td-norm">Description</th>
					<th width="10%" class="th-td-norm">Existing<br>Stocks</th>
					<th width="10%" class="th-td-norm">QTY</th>
					<th width="8%" class="th-td-norm">UOM</th>
					<th width="10%" class="th-td-norm">Gross Price</th>
					<th width="10%" class="th-td-norm">Tax Type</th>
					<th width="10%" class="th-td-norm">VAT Amount</th>
					<th width="10%" class="th-td-edge">Amount</th>
				</tr>
				<tr>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="rtsi" items="${apInvoice.rtsItems}" varStatus="status">
					<tr>
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${rtsi.item.stockCode}</td>
						<td class="th-td-norm v-align-top">${rtsi.item.description}</td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number'
								minFractionDigits='2' maxFractionDigits='2' value='${rtsi.item.existingStocks}'/></td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number'
								minFractionDigits='2' maxFractionDigits='2' value='${rtsi.quantity}'/></td>
						<td class="th-td-norm v-align-top">${rtsi.item.unitMeasurement.name}</td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number'
								minFractionDigits='4' maxFractionDigits='4' value="${rtsi.unitCost}"/></td>
						<td class="th-td-norm v-align-top">${rtsi.taxType.name}</td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number'
								minFractionDigits='2' maxFractionDigits='2' value="${rtsi.vatAmount}"/></td>
						<c:set var="amount" value="${(rtsi.quantity * rtsi.unitCost) - rtsi.vatAmount}" />
						<td class="th-td-edge v-align-top txt-align-right"><fmt:formatNumber type='number'
								minFractionDigits='2' maxFractionDigits='2' value="${amount}"/></td>
						<c:set var="totalVatAmount" value="${totalVatAmount + rtsi.vatAmount}" />
						<c:set var="totalAmount" value="${totalAmount + amount}" />
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="10" class="txt-align-right"><fmt:formatNumber type='number'
								minFractionDigits='2' maxFractionDigits='2' value='${totalAmount}'/></td>
				</tr>
			</tfoot>
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
				<td class="footerViewCls" colspan="2"><fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${totalAmount}" /></td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total VAT</td>
				<td class="footerViewCls" colspan="2"><fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${totalVatAmount}" /></td>
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