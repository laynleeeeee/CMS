<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Receiving Report - Raw Materials form for viewing only.
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
	formObjectId = parseInt("${rrRawMaterial.ebObjectId}");

	//To enable the edit button even if the form is completed.
	var status = "${rrRawMaterial.formWorkflow.complete}";
	if(status == "true") {
		$("#imgEdit").show();
	} else if ("${rrRawMaterial.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}

	if ("${rrRawMaterial.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
<div class="formDivBigForms">
	<div class="modFormLabel">Receiving Report - Raw Materials Palay</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">RR-RM P No.</td>
				<td class="label-value">${rrRawMaterial.receivingReport.formattedRRNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${rrRawMaterial.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<br>
	<fieldset class="frmField_set">
		<legend>RR - Raw Material Palay Header</legend>
		<table>
			<tr>
				<td class="label">Company</td>
				<td class="label-value">${rrRawMaterial.receivingReport.company.name}</td>
			</tr>
			<tr>
				<td class="label">Warehouse</td>
				<td class="label-value">${rrRawMaterial.receivingReport.warehouse.name}</td>
			</tr>
			<tr>
				<td class="label"> PO Number</td>
				<td class="label-value">${rrRawMaterial.receivingReport.poNumber}</td>
			</tr>
			<tr>
				<td class="label">Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${rrRawMaterial.glDate}"/></td>
			</tr>
			<tr>
				<td class="label">Invoice Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${rrRawMaterial.invoiceDate}"/></td>
			</tr>
			<tr>
				<td class="label">Supplier</td>
				<td class="label-value">${rrRawMaterial.supplier.name}</td>
			</tr>
			<tr>
				<td class="label">Supplier Account</td>
				<td class="label-value">${rrRawMaterial.supplierAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Term</td>
				<td class="label-value">${rrRawMaterial.term.name}</td>
			</tr>
			<tr>
				<td class="label">Due Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${rrRawMaterial.dueDate}"/></td>
			</tr>
			<tr>
				<td class="label">Scale Sheet No</td>
				<td class="label-value">${rrRawMaterial.invoiceNumber}</td>
			</tr>
			<tr>
				<td class="label">Stock Code</td>
				<td class="label-value">${rrRawMaterial.rrRawMatItemDto.stockCode}</td>
			</tr>
			<tr>
				<td class="label">Description</td>
				<td class="label-value">${rrRawMaterial.rrRawMatItemDto.description}</td>
			</tr>
			<tr>
				<td class="label">Unit Of Measurement</td>
				<td class="label-value">${rrRawMaterial.rrRawMatItemDto.uom}</td>
			</tr>
			<tr>
				<td class="label">Buying Price</td>
				<td class="label-value">
					<span style="display: block; width: 100px; text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${rrRawMaterial.rrRawMatItemDto.buyingPrice}"/>
					</span>
				</td>
			</tr>
			<tr>
				<td class="label">Amount</td>
				<td class="label-value">
					<span style="display: block; width: 100px; text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${rrRawMaterial.rrRawMatItemDto.amount}"/>
					</span>
				</td>
			</tr>
		</table>
	</fieldset>
	<br>
	<fieldset class="frmField_set">
		<legend>Weight Table</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="2%" class="th-td-norm">#</th>
					<th width="25%" class="th-td-norm">Bags</th>
					<th width="25%" class="th-td-norm">Kilos</th>
					<th width="25%" class="th-td-edge">MTS</th>
				</tr>
			</thead>
			<c:set var="totalBagQty" value="0"/>
			<c:set var="totalQty" value="0"/>
			<c:set var="totalWeight" value="0"/>
			<tbody>
				<c:forEach items="${rrRawMaterial.rriBagQuantities}" var="rriBQty" varStatus="status">
					<tr>
						<!-- Row number -->
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<!-- Bag Quantity -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${rriBQty.bagQuantity}"/>
							<c:set var="totalBagQty" value="${totalBagQty + rriBQty.bagQuantity}"/>
						</td>
						<!-- Quantity -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${rriBQty.quantity}"/>
							<c:set var="totalQty" value="${totalQty + rriBQty.quantity}"/>
						</td>
						<!-- Net Weight -->
						<td class="td-numeric v-align-top" style="border-right: none;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${rriBQty.netWeight}"/>
							<c:set var="totalWeight" value="${totalWeight + rriBQty.netWeight}"/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td style="font-weight:bold;">Total</td>
					<td align="right">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalBagQty}" />
					</td>
					<td align="right">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalQty}" />
					</td>
					<td align="right">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalWeight}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</fieldset>
	<br>
	<fieldset class="frmField_set">
		<legend>Discount Table</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="2%" class="th-td-norm">#</th>
					<th width="25%" class="th-td-norm">Bags</th>
					<th width="25%" class="th-td-norm">Discounts</th>
					<th width="25%" class="th-td-edge">Total</th>
				</tr>
			</thead>
			<c:set var="totalDiscount" value="0"></c:set>
			<tbody>
				<c:forEach items="${rrRawMaterial.rriBagDiscounts}" var="rriBDisc" varStatus="status">
					<tr>
						<!-- Row number -->
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<!-- Bag -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${rriBDisc.bagQuantity}"/>
						</td>
						<!-- Discount -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${rriBDisc.discountQuantity}"/>
						</td>
						<!-- Total -->
						<td class="td-numeric v-align-top" style="border-right: none;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${rriBDisc.bagQuantity * rriBDisc.discountQuantity}"/>
						</td>
						<c:set var="totalDiscount" value="${totalDiscount + (rriBDisc.bagQuantity * rriBDisc.discountQuantity)}"/>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<td style="font-weight:bold;">Total</td>
				<td colspan="3" align="right">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalDiscount}" />
				</td>
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
					<th width="25%" class="th-td-norm">AP Line</th>
					<th width="15%" class="th-td-norm">Qty</th>
					<th width="15%" class="th-td-norm">UOM</th>
					<th width="15%" class="th-td-norm">UP</th>
					<th width="20%" class="th-td-edge">Amount</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${rrRawMaterial.apInvoiceLines}" var="apl" varStatus="status">
					<tr>
						<!-- Row number -->
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<!-- AR Line Setup -->
						<td class="th-td-norm v-align-top">${apl.apLineSetup.name}</td>
						<!-- Quantity -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2"
									maxFractionDigits="2" value="${apl.quantity}" /></td>
						<!-- UOM -->
						<td class="th-td-norm v-align-top">${apl.unitMeasurement.name}</td>
						<!-- UP Amount -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2"
									maxFractionDigits="2" value="${apl.upAmount}" /></td>
						<!-- Amount -->
						<td class="td-numeric v-align-top" style="border-right: none;">
							<fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${apl.amount}" /></td>
					</tr>
					<c:set var="otherCharges" value="${otherCharges + apl.amount}" />
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4" style="font-weight:bold;">Total</td>
					<td colspan="2" align="right"><fmt:formatNumber type="number" minFractionDigits="2"
									maxFractionDigits="2" value="${otherCharges}" /></td>
				</tr>
			</tfoot>
		</table>
		</fieldset>
		<br>
		<c:set var="netWeight" value="${totalWeight - totalDiscount}"></c:set>
		<table class="frmField_set">
			<tr>
				<td align="right" width="90%">Weight</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${totalWeight}" /></td>
			</tr>
			<tr>
				<td align="right" width="90%">Discount</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${totalDiscount}" /></td>
			</tr>
			<tr>
				<td align="right" width="90%">Net Weight</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${netWeight}" /></td>
			</tr>
			<tr>
				<td align="right" width="90%">Buying Price</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${rrRawMaterial.rrRawMatItemDto.buyingPrice}" /></td>
			</tr>
			<tr>
				<td align="right" width="90%">Amount</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${rrRawMaterial.rrRawMatItemDto.amount}" /></td>
			</tr>
			<tr>
				<td align="right" width="90%">Other Charges</td>
				<td align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${otherCharges}" />
				</td>
			</tr>
			<tr>
				<td align="right" width="90%">Grand Total</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${rrRawMaterial.rrRawMatItemDto.amount + otherCharges}" /></td>
			</tr>
		</table>
</div>
</body>
</html>