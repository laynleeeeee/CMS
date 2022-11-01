<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: View form of CAP Delivery.
 -->
<!DOCTYPE
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>

<script type="text/javascript">
$(document).ready(function () {

	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${capDelivery.ebObjectId}");

	if ("${capDelivery.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}

	var capDTypeId = "${capDelivery.customerAdvancePaymentTypeId}";
	$(".referenceObject").each (function (i, o) {
		var objectId = $(this).attr("id");
		if(capDTypeId == 3) {
			setRefShortDesc (this, 12, objectId, contextPath);
		} else {
			setSpanShortDesc (this, objectId, 12, contextPath);
		}
	});
});
</script>
</head>
<body>
<div class="formDivBigForms">
	<div class="modFormLabel">Paid in Advance Delivery
		<c:if test="${capDelivery.customerAdvancePaymentTypeId == 3}"> - IS</c:if>
	</div>
	<br>
	<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">PIAD
						<c:if test="${capDelivery.customerAdvancePaymentTypeId == 3}"> - IS</c:if> No.</td>
					<td class="label-value">${capDelivery.formattedCAPDNumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${capDelivery.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Paid in Advance Delivery Header</legend>
			<table class="formTable">
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${capDelivery.company.numberAndName}</td>
				</tr>

				<tr>
					<td class="label">Customer</td>
					<td class="label-value">${capDelivery.arCustomer.name }</td>
				</tr>

				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${capDelivery.arCustomerAccount.name}</td>
				</tr>

				<tr>
					<td class="label">Sales Invoice No.</td>
					<td class="label-value">${capDelivery.salesInvoiceNo}</td>
				</tr>

				<tr>
					<td class="label">Delivery Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${capDelivery.deliveryDate}"/></td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Delivery Item Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="10%" class="th-td-norm">Warehouse</th>
						<th width="10%" class="th-td-norm">
							<c:choose>
								<c:when test="${capDelivery.customerAdvancePaymentTypeId == 3}">
									Selected<br>Bags/Stocks
								</c:when>
								<c:otherwise>
									Existing<br>Stocks
								</c:otherwise>
							</c:choose>
						</th>
						<c:if test="${capDelivery.customerAdvancePaymentTypeId == 3}">
							<th width="8%" class="th-td-norm">Bags</th>
						</c:if>
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
					<c:forEach items="${capDelivery.deliveryItems}" var="deliveryItem" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock code -->
							<td class="th-td-norm v-align-top">${deliveryItem.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${deliveryItem.item.description}</td>
							<!-- Warehouse list -->
							<td class="th-td-norm v-align-top">${deliveryItem.warehouse.name}</td>
							<c:choose>
								<c:when test="${capDelivery.customerAdvancePaymentTypeId == 3}">
									<!-- Selected Stocks -->
									<td class="th-td-norm v-align-top">
										<span class="referenceObject" id="${deliveryItem.ebObjectId}"></span></td>
								</c:when>
								<c:otherwise>
									<!-- Existing stocks -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
												maxFractionDigits="2" value="${deliveryItem.existingStocks}" /></td>
								</c:otherwise>
							</c:choose>
							<c:if test="${capDelivery.customerAdvancePaymentTypeId == 3}">
							<!-- Bags -->
								<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${deliveryItem.itemBagQuantity}" />
								</td>
							</c:if>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${deliveryItem.quantity}" />
							</td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${deliveryItem.item.unitMeasurement.name}</td>
							<!-- Add On -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
 									maxFractionDigits="2" value="${deliveryItem.addOn}" />
							</td>
							<!-- SRP -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${deliveryItem.srp}" />
							</td>
							<!-- Discount list -->
							<td class="th-td-norm v-align-top">${deliveryItem.itemDiscount.name}</td>
							<!-- Discount (computed) -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${deliveryItem.discount}" />
							</td>
							<!-- Amount -->
							<td class="txt-align-right th-td-edge v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${deliveryItem.amount}" />
							</td>
						</tr>
						<c:set var="totalAmount" value="${totalAmount + deliveryItem.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<c:choose>
						<c:when test="${capDelivery.customerAdvancePaymentTypeId == 3}">
							<c:set var="colSpan" value="11"/>
						</c:when>
						<c:otherwise>
							<c:set var="colSpan" value="10"/>
						</c:otherwise>
					</c:choose>
					<tr>
						<td colspan="${colSpan}" style="font-weight:bold;">Total</td>
						<td colspan="2" class="txt-align-right"><fmt:formatNumber type="number" minFractionDigits="2" 
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
				<c:forEach items="${capDelivery.deliveryArLines}" var="arLine" varStatus="status">
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
						<td class="txt-align-right th-td-edge v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${arLine.amount}" /></td>
					</tr>
					<c:set var="otherCharges" value="${otherCharges + arLine.amount}" />
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4" style="font-weight:bold;">Total</td>
					<td colspan="2" class="txt-align-right"><fmt:formatNumber type="number" minFractionDigits="2" 
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
		<hr class="thin" />
	</div>
</div>
</body>
</html>