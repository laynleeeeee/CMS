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
	formObjectId = parseInt("${rrRawMaterial.ebObjectId}");
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
	<div class="modFormLabel">Receiving Report - Raw Material IS</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">RR - RM IS No.</td>
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
		<legend>RR - Raw Material IS Header</legend>
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
				<td class="label">Supplier Invoice No</td>
				<td class="label-value">${rrRawMaterial.invoiceNumber}</td>
			</tr>
			<tr>
				<td class="label">Delivery Receipt No</td>
				<td class="label-value">${rrRawMaterial.receivingReport.deliveryReceiptNo}</td>
			</tr>
		</table>
	</fieldset>
	<br>
	<fieldset class="frmField_set">
		<legend>RR - Raw Material Items Table</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="1%" class="th-td-norm">#</th>
					<th width="10%" class="th-td-norm">Stock Code</th>
					<th width="15%" class="th-td-norm">Description</th>
					<th width="8%" class="th-td-norm">Existing<br>Stocks</th>
					<th width="8%" class="th-td-norm">QTY</th>
					<th width="8%" class="th-td-norm">UOM</th>
					<th width="10%" class="th-td-norm">Add On<br>(Computed)</th>
					<th width="10%" class="th-td-norm">Buying Price</th>
					<th width="10%" class="th-td-norm">Discount<br>(Computed)</th>
					<th width="10%" class="th-td-edge">Amount</th>
				</tr>
				<tr>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="rr" items="${rrRawMaterial.rrItems}" varStatus="status">
					<tr>
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${rr.item.stockCode}</td>
						<td class="th-td-norm v-align-top">${rr.item.description}</td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type="number"
										minFractionDigits="2" maxFractionDigits="2" value="${rr.existingStocks}" /></td>
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${rr.quantity}" /></td>
						<td class="th-td-norm v-align-top">${rr.item.unitMeasurement.name}</td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${rr.rmItem.addOn}'/></td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${rr.unitCost}'/></td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${rr.rmItem.discount}'/></td>
						<td class="th-td-edge txt-align-right v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${rr.rmItem.amount}'/></td>
					</tr>
					<c:set var="rrItemsTotal" value="${rrItemsTotal + rr.rmItem.amount}" />
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td style="font-weight:bold;">Total</td>
					<td colspan="9" class="txt-align-right">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${rrItemsTotal}' />
					</td>
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
		<table class="frmField_set">
			<tr>
				<td>Grand Total</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${rrRawMaterial.amount}" /></td>
			</tr>
		</table>
</div>
</body>
</html>