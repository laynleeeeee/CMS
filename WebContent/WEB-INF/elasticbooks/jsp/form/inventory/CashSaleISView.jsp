<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Cash Sales Individual Selection view.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
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
</style>
<script type="text/javascript">
$(document).ready(function () {

	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${cashSale.ebObjectId}");

	if ("${cashSale.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}

	$(".referenceObject").each (function (i, o) {
		var objectId = $(this).attr("id");
		setRefShortDesc (this, 4, objectId, contextPath);
	});
});

function printReceipt () {
	window.open(contextPath + "/cashSalePDF/receipt?pId="+"${cashSale.id}");
}
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Cash Sales IS
		</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table>
					<tr>
						<td class="label">CS - IS No.</td>
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
						<th width="5%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="10%" class="th-td-norm">Warehouse</th>
						<th width="10%" class="th-td-norm">Selected<br>Bags/Stocks</th>
						<th width="8%" class="th-td-norm">Bags</th>
						<th width="8%" class="th-td-norm">Qty</th>
						<th width="8%" class="th-td-norm">UOM</th>
						<th width="8%" class="th-td-norm">Add On<br>(Computed)</th>
						<th width="8%" class="th-td-norm">SRP</th>
						<th width="10%" class="th-td-norm">Discount</th>
						<th width="8%" class="th-td-norm">Discount<br>(Computed)</th>
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
							<!-- Selected Stocks -->
							<!-- Warehouse list -->
							<td class="th-td-norm v-align-top">${csItem.warehouse.name}</td>
							<td class="th-td-norm v-align-top">
								<span class="referenceObject" id="${csItem.ebObjectId}"></span>
							</td>
							<!-- Bags -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${csItem.itemBagQuantity}" />
							</td>
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

							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${csItem.amount}" />
							</td>
						</tr>
						<c:set var="totalQuantity" value="${totalQuantity + csItem.quantity}" />
						<c:set var="totalAmount" value="${totalAmount + csItem.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="5" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalQuantity}" /></td>
						<td colspan="6" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2"
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
						<th width="25%" class="th-td-norm">AR Line</th>
						<th width="15%" class="th-td-norm">Qty</th>
						<th width="15%" class="th-td-norm">UOM</th>
						<th width="15%" class="th-td-norm">UP</th>
						<th width="20%" class="th-td-edge">Amount</th>
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
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${arLine.upAmount}" /></td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${arLine.amount}" /></td>
						</tr>
						<c:set var="otherCharges" value="${otherCharges + arLine.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4" style="font-weight:bold;">Total</td>
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
					<td>Grand Total</td>
					<td align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${totalSales}" /></td>
				</tr>
			</table>
			<br />
			<table class="frmField_set">
				<tr>
					<td class="cashAndChange" style="width: 75%">Cash/Check</td>
					<td class="monetary">
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${cashSale.cash}" />
					</td>
				</tr>
				<tr>
					<td class="cashAndChange">Change</td>
					<td class="monetary">
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${cashSale.cash - totalSales}" />
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>