<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp"%>
	<!--

		Description: Account Sales -IS view form.
	 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<style type="text/css">
.monetary {
	text-align: right;
}
</style>
<script type="text/javascript">
$(document).ready(function () {

	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${accountSale.ebObjectId}");

	if ("${accountSale.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}

	$("#tblAccountSale tbody tr").each (function (i, o) {
		var $sourceObjectId = $(this).find(".referenceObject");
		setRefShortDesc ($sourceObjectId, 5, $sourceObjectId.attr("id"), contextPath);
	});
});
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Account Sale IS</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table>
					<tr>
						<td class="label">AS - IS No.</td>
						<td class="label-value">${accountSale.formattedASNumber}</td>
					</tr>
					<tr>
						<td class="label">Status</td>
						<td class="label-value">${accountSale.formWorkflow.currentFormStatus.description}</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Account Sales Header</legend>
				<table class="formTable">
					<tr>
						<td class="label">Company</td>
						<td class="label-value">${accountSale.company.numberAndName}</td>
					</tr>
					<tr>
						<td class="label">Customer</td>
						<td class="label-value">${accountSale.arCustomer.name}</td>
					</tr>
					<tr>
						<td class="label">Customer Account</td>
						<td class="label-value">${accountSale.arCustomerAccount.name}</td>
					</tr>
					<tr>
						<td class="label">Credit Limit</td>
						<td class="label-value"><fmt:formatNumber type="number"
								minFractionDigits="2" maxFractionDigits="2"
								value="${accountSale.arCustomer.maxAmount}" /></td>
					</tr>
					<tr>
						<td class="label">Available Balance</td>
						<td class="label-value"><fmt:formatNumber type="number"
								minFractionDigits="2" maxFractionDigits="2"
								value="${accountSale.availableBalance}" /></td>
					</tr>
					<tr>
						<td class="label">Term</td>
						<td class="label-value">${accountSale.term.name}</td>
					</tr>
					<tr>
						<td class="label">Date</td>
						<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy"
								value="${accountSale.transactionDate}" /></td>
					</tr>
					<tr>
						<td class="label">Due Date</td>
						<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy"
								value="${accountSale.dueDate}" /></td>
					</tr>
					<tr>
						<td class="label">Remarks</td>
						<td class="label-value">${accountSale.description}</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
				<legend>Account Sales Item Table</legend>
				<table class="dataTable" id="tblAccountSale">
					<thead>
						<tr>
							<th width="5%" class="th-td-norm">#</th>
							<th width="10%" class="th-td-norm">Stock Code</th>
							<th width="15%" class="th-td-norm">Description</th>
							<th width="10%" class="th-td-norm">Warehouse</th>
							<th width="10%" class="th-td-norm">Selected<br>Bags/Stocks
							</th>
							<th width="8%" class="th-td-norm">Bags</th>
							<th width="8%" class="th-td-norm">Qty</th>
							<th width="8%" class="th-td-norm">UOM</th>
							<th width="8%" class="th-td-norm">Add On<br>(Computed)
							</th>
							<th width="8%" class="th-td-norm">SRP</th>
							<th width="10%" class="th-td-norm">Discount</th>
							<th width="8%" class="th-td-norm">Discount<br>(Computed)
							</th>
							<th width="8%" class="th-td-edge">Amount</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${accountSale.accountSaleItems}" var="asItem"
							varStatus="status">
							<tr>
								<!-- Row number -->
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<!-- Stock code -->
								<td class="th-td-norm v-align-top">${asItem.item.stockCode}</td>
								<!-- Description -->
								<td class="th-td-norm v-align-top">${asItem.item.description}</td>
								<!-- Warehouse list -->
								<td class="th-td-norm v-align-top">${asItem.warehouse.name}</td>
								<!-- Selected stocks -->
								<td class="th-td-norm v-align-top">
									<input type="hidden" id="itemId" value="${asItem.item.id}">
									<input type="hidden" id="warehouseId" value="${asItem.warehouse.id}">
									<span class="referenceObject" id="${asItem.ebObjectId}"></span>
								</td>
								<!-- Bags -->
								<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${asItem.itemBagQuantity}" />
								</td>
								<!-- Quantity -->
								<td class="td-numeric v-align-top"><fmt:formatNumber
										type="number" minFractionDigits="2" maxFractionDigits="2"
										value="${asItem.quantity}" /></td>
								<!-- UOM -->
								<td class="th-td-norm v-align-top">${asItem.item.unitMeasurement.name}</td>
								<!-- Add On -->
								<td class="td-numeric v-align-top"><fmt:formatNumber
										type="number" minFractionDigits="2" maxFractionDigits="2"
										value="${asItem.itemAddOn.value * asItem.quantity}" /></td>
								<!-- SRP -->
								<td class="td-numeric v-align-top"><fmt:formatNumber
										type="number" minFractionDigits="2" maxFractionDigits="2"
										value="${asItem.srp}" /></td>
								<!-- Discount list -->
								<td class="th-td-norm v-align-top">${asItem.itemDiscount.name}</td>
								<!-- Discount (computed) -->
								<td class="td-numeric v-align-top"><fmt:formatNumber
										type="number" minFractionDigits="2" maxFractionDigits="2"
										value="${asItem.discount}" /></td>
								<!-- Amount -->
								<td class="td-numeric v-align-top" style="border-right: none;">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${asItem.amount}" />
								</td>
							</tr>
							<c:set var="totalQuantity"
								value="${totalQuantity + asItem.quantity}" />
							<c:set var="totalAmount" value="${totalAmount + asItem.amount}" />
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="5" style="font-weight: bold;">Total</td>
							<td colspan="2" class="monetary"><fmt:formatNumber
									type="number" minFractionDigits="2" maxFractionDigits="2"
									value="${totalQuantity}" /></td>
							<td colspan="6" class="monetary"><fmt:formatNumber
									type="number" minFractionDigits="2" maxFractionDigits="2"
									value="${totalAmount}" /></td>
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
						<c:forEach items="${accountSale.arLines}" var="arLine"
							varStatus="status">
							<tr>
								<!-- Row number -->
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<!-- AR Line Setup -->
								<td class="th-td-norm v-align-top">${arLine.arLineSetup.name}</td>
								<!-- Quantity -->
								<td class="td-numeric v-align-top"><fmt:formatNumber
										type="number" minFractionDigits="2" maxFractionDigits="2"
										value="${arLine.quantity}" /></td>
								<!-- UOM -->
								<td class="th-td-norm v-align-top">${arLine.unitMeasurement.name}</td>
								<!-- UP Amount -->
								<td class="td-numeric v-align-top"><fmt:formatNumber
										type="number" minFractionDigits="2" maxFractionDigits="2"
										value="${arLine.upAmount}" /></td>
								<!-- Amount -->
								<td class="td-numeric v-align-top" style="border-right: none;">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${arLine.amount}" />
								</td>
							</tr>
							<c:set var="otherCharges" value="${otherCharges + arLine.amount}" />
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="4" style="font-weight: bold;">Total</td>
							<td colspan="2" class="monetary"><fmt:formatNumber
									type="number" minFractionDigits="2" maxFractionDigits="2"
									value="${otherCharges}" /></td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
			<br>
			<table class="frmField_set">
				<tr>
					<td>Grand Total</td>
					<td align="right"><fmt:formatNumber type="number"
							minFractionDigits="2" maxFractionDigits="2"
							value="${accountSale.amount}" /></td>
				</tr>
			</table>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>