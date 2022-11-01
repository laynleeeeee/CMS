<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Cash Sales view form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<style type="text/css">
.monetary {
	text-align: right;
}

.cashAndChange {
	font-size: 14;
	font-weight: bold;
	text-align: right;
}

a#btnPrintReceipt{
	color: blue;
}

a#btnPrintReceipt:hover {
	color: blue;
	cursor: pointer;
}

.footerViewCls {
	font-size: 12;
	font-weight: bold;
	text-align: right;
	width: 22%;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${cashSale.ebObjectId}");

	if ("${cashSale.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});

function printReceipt () {
	window.open(contextPath + "/cashSalePDF/receipt?pId="+"${cashSale.id}");
}
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Cash Sales Order</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table>
					<tr>
						<td class="label">
							<c:choose>
								<c:when test="${cashSale.cashSaleTypeId == 1}">CS No.</c:when>
								<c:otherwise>CS - W No.</c:otherwise>
							</c:choose>
						</td>
						<td class="label-value">${cashSale.formattedCSNumber}</td>
					</tr>
					<tr>
						<td class="label">Status </td>
						<td class="label-value">${cashSale.formWorkflow.currentFormStatus.description}</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Cash Sales Header</legend>
				<table class="formTable">
					<tr>
						<td class="label">Company</td>
						<td class="label-value">${cashSale.company.numberAndName}</td>
					</tr>
					<tr>
						<td class="label">Type</td>
						<td class="label-value">${cashSale.arReceiptType.name}</td>
					</tr>
					<tr>
						<td class="label">Check Number</td>
						<td class="label-value">${cashSale.refNumber}</td>
					</tr>
					<tr>
						<td class="label">Customer</td>
						<td class="label-value">${cashSale.arCustomer.name }</td>
					</tr>
					<tr>
						<td class="label">Customer Account</td>
						<td class="label-value">${cashSale.arCustomerAccount.name}</td>
					</tr>
					<tr>
						<td class="label">Sales Invoice No.</td>
						<td class="label-value">${cashSale.salesInvoiceNo}</td>
					</tr>
					<tr>
						<td class="label">Receipt Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${cashSale.receiptDate}"/>
						</td>
					</tr>
					<tr>
						<td class="label">Maturity Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${cashSale.maturityDate}"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td style="font-weight: bold;" class="label-value">
							<a id="btnPrintReceipt" onclick="printReceipt();">Print Receipt</a>
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
			<legend>Cash Sales Item Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="10%" class="th-td-norm">Description</th>
						<th width="7%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="8%" class="th-td-norm">Warehouse</th>
						<th width="7%" class="th-td-norm">Qty</th>
						<th width="6%" class="th-td-norm">UOM</th>
						<th width="7%" class="th-td-norm">Add On<br>(Computed)</th>
						<th width="7%" class="th-td-norm">
						<c:choose>
							<c:when test="${cashSale.cashSaleTypeId == 1}">SRP</c:when>
							<c:otherwise>WP</c:otherwise>
						</c:choose>
						</th>
						<th width="7%" class="th-td-norm">Discount</th>
						<th width="7%" class="th-td-norm">Discount<br>(Computed)</th>
						<th width="8%" class="th-td-norm">Tax Type</th>
						<th width="7%" class="th-td-norm">VAT Amount</th>
						<th width="8%" class="th-td-edge">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${cashSale.cashSaleItems}" var="csItem" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock code -->
							<td class="th-td-norm v-align-top">${csItem.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${csItem.item.description}</td>
							<!-- Existing stocks -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${csItem.existingStocks}" /></td>
							<!-- Warehouse list -->
							<td class="th-td-norm v-align-top">${csItem.warehouse.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${csItem.quantity}" />
							</td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${csItem.item.unitMeasurement.name}</td>
							<!-- Add On -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
 									maxFractionDigits="2" value="${csItem.itemAddOn.value * csItem.quantity}" />
							</td>
							<!-- Selling Price -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${csItem.srp}" />
							</td>
							<!-- Discount list -->
							<td class="th-td-norm v-align-top">${csItem.itemDiscount.name}</td>
							<!-- Discount (computed) -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${csItem.discount}" />
							</td>
							<td class="th-td-norm v-align-top">${csItem.taxType.name}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${csItem.vatAmount}" />
							</td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${csItem.amount}" />
							</td>
						</tr>
						<c:set var="totalItemVat" value="${totalItemVat + csItem.vatAmount}" />
						<c:set var="totalQuantity" value="${totalQuantity + csItem.quantity}" />
						<c:set var="totalAmount" value="${totalAmount + csItem.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalQuantity}" /></td>
						<td colspan="8" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${totalAmount}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
			<legend>Other Charges Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="28%" class="th-td-norm">AR Line</th>
						<th width="10%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<th width="10%" class="th-td-norm">Gross Price</th>
						<th width="15%" class="th-td-norm">Tax Type</th>
						<th width="10%" class="th-td-norm">VAT Amount</th>
						<th width="15%" class="th-td-edge">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${cashSale.cashSaleArLines}" var="arLine" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- AR Line Setup -->
							<td class="th-td-norm v-align-top">${arLine.arLineSetup.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${arLine.quantity}" /></td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${arLine.unitMeasurement.name}</td>
							<!-- UP Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="4" 
										maxFractionDigits="4" value="${arLine.upAmount}" /></td>
							<td class="th-td-norm v-align-top">${arLine.taxType.name}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${arLine.vatAmount}" />
							</td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${arLine.amount}" /></td>
						</tr>
						<c:set var="totalOcVat" value="${totalOcVat + arLine.vatAmount}" />
						<c:set var="otherCharges" value="${otherCharges + arLine.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="6" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${otherCharges}" /></td>
					</tr>
					<c:set var="totalSales" value="${otherCharges + totalAmount}" />
				</tfoot>
			</table>
			</fieldset>
			<br>
			<table class="frmField_set">
				<tr>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls">Sub Total</td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls" style="width: 12%">
						<fmt:formatNumber type="number" minFractionDigits="2"
							maxFractionDigits="2" value="${totalSales}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Total VAT</td>
					<td class="footerViewCls" colspan="2"><fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${totalItemVat + totalOcVat}" /></td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Withholding Tax</td>
					<td class="footerViewCls">${cashSale.wtAcctSetting.name}</td>
					<td class="footerViewCls">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${cashSale.wtAmount}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Total Amount Due</td>
					<td class="footerViewCls" colspan="2"><fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${totalSales + totalItemVat + totalOcVat - cashSale.wtAmount}" /></td>
				</tr>
				<tr>
					<td colspan="3" class="footerViewCls">Cash/Check</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${cashSale.cash}" />
					</td>
				</tr>
				<tr>
					<td colspan="3" class="footerViewCls">Change</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${cashSale.cash - (totalSales + totalItemVat + totalOcVat - cashSale.wtAmount)}" />
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>