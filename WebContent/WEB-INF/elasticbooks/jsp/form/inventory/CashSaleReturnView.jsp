<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Cash Sales Return view form.
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
	formObjectId = parseInt("${cashSaleReturn.ebObjectId}");

	if ("${cashSaleReturn.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}

	var csTypeId = "${cashSaleReturn.cashSaleTypeId}";
	$(".referenceObject").each (function (i, o) {
		var objectId = $(this).attr("id");
		var quantity = $.trim($(this).closest("tr").find(".quantity").text());
		if(objectId != null) {
			var orTypeId = 4;
			if(accounting.unformat(quantity) > 0) {
				orTypeId = 6;
			}
			if(csTypeId == 3) {
				setRefShortDesc(this, orTypeId, objectId, contextPath);
			} else {
				setSpanShortDesc (this, objectId, orTypeId, contextPath);
			}
		}
	});

	if(csTypeId == 3) {
		// Hide Other Charges portion if the type of CSR is Individual Selection
		$("#fsOtherChargesId").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Cash Sales Return</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table>
					<tr>
						<td class="label">CSR
							<c:choose>
								<c:when test="${cashSaleReturn.cashSaleTypeId == 1}"/>
								<c:when test="${cashSaleReturn.cashSaleTypeId == 2}"> - W</c:when>
								<c:otherwise> - IS</c:otherwise>
							</c:choose> No.</td>
						<td class="label-value">${cashSaleReturn.formattedCSRNumber}</td>
					</tr>
					<tr>
						<td class="label">Status </td>
						<td class="label-value">${cashSaleReturn.formWorkflow.currentFormStatus.description}</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Cash Sales Return Header</legend>
				<table class="formTable">
					<tr>
						<td class="label">Reference</td>
						<td class="label-value">${cashSaleReturn.referenceNo}</td>
					</tr>
					<tr>
						<td class="label">Company</td>
						<td class="label-value">${cashSaleReturn.company.numberAndName}</td>
					</tr>
					<tr>
						<td class="label">Customer</td>
						<td class="label-value">${cashSaleReturn.arCustomer.name }</td>
					</tr>
					<tr>
						<td class="label">Customer Account</td>
						<td class="label-value">${cashSaleReturn.arCustomerAccount.name}</td>
					</tr>
					<tr>
						<td class="label">Sales Invoice No.</td>
						<td class="label-value">${cashSaleReturn.salesInvoiceNo}</td>
					</tr>
					<tr>
						<td class="label">Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${cashSaleReturn.date}"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
			<legend>Cash Sales Return Item Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="10%" class="th-td-norm">Description</th>
						<th width="8%" class="th-td-norm">Warehouse</th>
						<th width="7%" class="th-td-norm">
							<c:choose>
								<c:when test="${cashSaleReturn.cashSaleTypeId == 3}">
									Selected<br>Bags/Stocks
								</c:when>
								<c:otherwise>
									Existing<br>Stocks
								</c:otherwise>
							</c:choose>
						</th>
						<c:choose>
							<c:when test="${cashSaleReturn.cashSaleTypeId == 3}">
								<th width="8%" class="th-td-norm">Bags</th>
							</c:when>
						</c:choose>
						<th width="7%" class="th-td-norm">Qty</th>
						<th width="6%" class="th-td-norm">UOM</th>
						<th width="7%" class="th-td-norm">Add On<br>(Computed)</th>
						<th width="7%" class="th-td-norm">
							<c:choose>
								<c:when test="${(cashSaleReturn.cashSaleTypeId == 1 ||
										cashSaleReturn.cashSaleTypeId == 3)}">SRP</c:when>
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
					<c:forEach items="${cashSaleReturn.cashSaleReturnItems}" var="csrItem" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock code -->
							<td class="th-td-norm v-align-top">${csrItem.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${csrItem.item.description}</td>
							<!-- Warehouse list -->
							<td class="th-td-norm v-align-top">${csrItem.warehouse.name}</td>
							<c:choose>
								<c:when test="${cashSaleReturn.cashSaleTypeId == 3}">
									<!-- Selected Stocks -->
									<td class="th-td-norm v-align-top">
										<c:choose>
											<c:when test="${csrItem.quantity < 0}"><span class="referenceObject" id="${csrItem.origRefObjectId}"></span></c:when>
											<c:otherwise><span class="referenceObject" id="${csrItem.ebObjectId}"></span></c:otherwise>
										</c:choose>
									</td>
									<!-- Bags -->
									<td class="quantity td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${csrItem.itemBagQuantity}" />
									</td>
								</c:when>
								<c:otherwise>
									<!-- Existing stocks -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
												maxFractionDigits="2" value="${csrItem.existingStocks}" /></td>
								</c:otherwise>
							</c:choose>
							<!-- Quantity -->
							<td class="quantity td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${csrItem.quantity}" />
							</td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${csrItem.item.unitMeasurement.name}</td>
							<!-- ADD ON -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${csrItem.itemAddOn.value * csrItem.quantity}" />
							</td>
							<!-- Selling Price -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${csrItem.srp}" />
							</td>
							<!-- Discount list -->
							<td class="th-td-norm v-align-top">${csrItem.itemDiscount.name}</td>
							<!-- Discount (computed) -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${csrItem.discount}" />
							</td>
							<td class="th-td-norm v-align-top">${csrItem.taxType.name}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${csrItem.vatAmount}" />
							</td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${csrItem.amount}" />
							</td>
						</tr>
						<c:set var="totalItemVat" value="${totalItemVat + (csrItem.quantity < 0 ? -csrItem.vatAmount : csrItem.vatAmount)}" />
						<c:set var="totalAmount" value="${totalAmount + csrItem.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<c:set var="colSpan" value="${cashSaleReturn.cashSaleTypeId == 3 ? 11 : 12}"/>
					<tr>
						<td colspan="${colSpan}" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary">
							<span id="spanTotalAmount">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${totalAmount}" />
							</span>
						</td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set"  id="fsOtherChargesId">
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
					<c:forEach items="${cashSaleReturn.cashSaleReturnArLines}" var="arLine" varStatus="status">
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
					<td class="footerViewCls">${cashSaleReturn.wtAcctSetting.name}</td>
					<td class="footerViewCls">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${cashSaleReturn.wtAmount}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Total Amount Due</td>
					<td class="footerViewCls" colspan="2"><fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${totalSales + totalItemVat + totalOcVat - cashSaleReturn.wtAmount}" /></td>
				</tr>
			</table>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>