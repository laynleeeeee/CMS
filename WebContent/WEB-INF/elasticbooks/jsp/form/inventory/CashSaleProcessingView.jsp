<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Cash Sales - Processing view form.
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
	width: 75%;
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
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Cash Sales - Processing
		</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table>
					<tr>
						<td class="label">Sequence No.</td>
						<td class="label-value">${cashSale.formattedCSNumber}</td>
					</tr>
					<tr>
						<td class="label">Status </td>
						<td class="label-value">${cashSale.formWorkflow.currentFormStatus.description}</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Header</legend>
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
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
			<legend>Items</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="10%" class="th-td-norm">Description</th>
						<th width="8%" class="th-td-norm">Warehouse</th>
						<th width="7%" class="th-td-norm">Qty</th>
						<th width="6%" class="th-td-norm">UOM</th>
						<th width="7%" class="th-td-norm">Add On<br>(Computed)</th>
						<th width="7%" class="th-td-norm">SRP</th>
						<th width="7%" class="th-td-norm">Discount</th>
						<th width="7%" class="th-td-norm">Discount<br>(Computed)</th>
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
						<td colspan="4" style="font-weight:bold;">Total</td>
						<td class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalQuantity}" /></td>
						<td colspan="6" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${totalAmount}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
			<br>
			<table class="frmField_set">
				<tr>
					<td class="cashAndChange">Grand Total</td>
					<td class="monetary">
						<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${totalAmount}" />
					</td>
				</tr>
				<tr>
					<td class="cashAndChange">Cash/Check</td>
					<td class="monetary">
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${cashSale.cash}" />
					</td>
				</tr>
				<tr>
					<td class="cashAndChange">Change</td>
					<td class="monetary">
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${cashSale.cash - totalAmount}" />
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>