<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Retail Receiving Report form for viewing only.
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

	var supplierAcctId = "${apInvoice.supplierAccountId}";
	var warehouseId = "${apInvoice.receivingReport.warehouseId}";
	var status = "${apInvoice.formWorkflow.complete}";
	if(status == "true") 
		$("#imgEdit").show();
	if ("${apInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}

	$(".prevUC").each (function (i, o) {
		var $prevUC = $(this);
		var itemId = $(this).closest("tr").find(".hdnItemId").val();
		var uri = contextPath + "/retailReceivingReport/getLatestUc?itemId="+itemId
				+"&warehouseId="+warehouseId+"&supplierAcctId="+supplierAcctId;
		$.ajax({
			url: uri,
			success : function(unitCost) {
				$prevUC.text(unitCost);
			}
		});
	});
});
</script>
</head>
<body>
<div class="formDivBigForms">
	<div class="modFormLabel">Receiving Report</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">RR No.</td>
				<td class="label-value">${apInvoice.receivingReport.formattedRRNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${apInvoice.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Receiving Report Header</legend>
		<table>
			<tr>
				<td class="label">Company</td>
				<td class="label-value">${apInvoice.receivingReport.company.name}</td>
			</tr>
			<tr>
				<td class="label">Warehouse</td>
				<td class="label-value">${apInvoice.receivingReport.warehouse.name}</td>
			</tr>
			<tr>
				<td class="label"> PO Number</td>
				<td class="label-value">${apInvoice.receivingReport.poNumber}</td>
			</tr>
			<tr>
				<td class="label">Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.glDate}"/></td>
			</tr>
			<tr>
				<td class="label">Invoice Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.invoiceDate}"/></td>
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
				<td class="label">Term</td>
				<td class="label-value">${apInvoice.term.name}</td>
			</tr>
			<tr>
				<td class="label">Due Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.dueDate}"/></td>
			</tr>
			<tr>
				<td class="label">Supplier Invoice No</td>
				<td class="label-value">${apInvoice.invoiceNumber}</td>
			</tr>
			<tr>
				<td class="label">Delivery Receipt No</td>
				<td class="label-value">${apInvoice.receivingReport.deliveryReceiptNo}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Receiving Report Table</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="1%" class="th-td-norm">#</th>
					<th width="17%" class="th-td-norm">Stock Code</th>
					<th width="22%" class="th-td-norm">Description</th>
					<th width="7%" class="th-td-norm">Existing<br>Stocks</th>
					<th width="7%" class="th-td-norm">QTY</th>
					<th width="7%" class="th-td-norm">UOM</th>
					<th width="7%" class="th-td-norm">Previous<br>Unit Cost</th>
					<th width="7%" class="th-td-norm">Gross Price</th>
					<th width="8%" class="th-td-norm">Tax Type</th>
					<th width="7%" class="th-td-norm">VAT Amount</th>
					<th width="7%" class="th-td-norm">SRP</th>
					<th width="10%" class="th-td-edge">Amount</th>
				</tr>
				<tr>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="rr" items="${apInvoice.rrItems}" varStatus="status">
					<tr>
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${rr.item.stockCode}
							<input type="hidden" class="hdnItemId" value="${rr.itemId}"/>
						</td>
						<td class="th-td-norm v-align-top">${rr.item.description}</td>
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${rr.existingStocks}" /></td>
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${rr.quantity}" /></td>
						<td class="th-td-norm v-align-top">${rr.item.unitMeasurement.name}</td>
						<td class="td-numeric v-align-top"><span class="prevUC"></span></td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${rr.unitCost}'/></td>
						<td class="th-td-norm v-align-top">${rr.taxType.name}</td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${rr.vatAmount}'/></td>
						<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${rr.srp}'/></td>
						<c:set var="amount" value="${(rr.unitCost - rr.vatAmount) * rr.quantity}"/>
						<td class="th-td-edge txt-align-right v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${amount}'/></td>
						<c:set var="total" value="${total + amount}" />
						<c:set var="rrItemVat" value="${rr.quantity * rr.vatAmount}"/>
						<c:set var="rriTotalVat" value="${rriTotalVat + rrItemVat}"/>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td>Total</td>
					<td colspan="11" align="right">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${total}' />
					</td>
				 </tr>
			</tfoot>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Other Charges Table</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="2%" class="th-td-norm">#</th>
					<th width="25%" class="th-td-norm">AP Line</th>
					<th width="10%" class="th-td-norm">Qty</th>
					<th width="10%" class="th-td-norm">UOM</th>
					<th width="10%" class="th-td-norm">UP</th>
					<th width="20%" class="th-td-norm">Tax Type</th>
					<th width="10%" class="th-td-norm">VAT Amount</th>
					<th width="13%" class="th-td-edge">Amount</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${apInvoice.apInvoiceLines}" var="apl" varStatus="status">
					<tr>
						<!-- Row number -->
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<!-- AR Line Setup -->
						<td class="th-td-norm v-align-top">${apl.apLineSetup.name}</td>
						<!-- Quantity -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${apl.quantity}" />
						</td>
						<!-- UOM -->
						<td class="th-td-norm v-align-top">${apl.unitMeasurement.name}</td>
						<!-- UP Amount -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${apl.upAmount}"/>
						</td>
						<td class="th-td-norm v-align-top">${apl.taxType.name}</td>
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${apl.vatAmount}"/>
						</td>
						<!-- Amount -->
						<td class="td-numeric v-align-top" style="border-right: none;">
							<fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${apl.amount}" /></td>
						<c:set var="apLineVat" value="${apl.quantity * apl.vatAmount}"/>
						<c:set var="totalApLineVat" value="${totalApLineVat + apLineVat}"/>
						<c:set var="otherCharges" value="${otherCharges + apl.amount}" />
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="6">Total</td>
					<td colspan="2" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${otherCharges}" /></td>
				</tr>
			</tfoot>
		</table>
		</fieldset>
		<br>
		<table class="frmField_set">
			<tr>
				<td align="right">Sub Total</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${total + otherCharges}" /></td>
			</tr>
			<tr>
				<td align="right">Total VAT</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${rriTotalVat + totalApLineVat}" /></td>
			</tr>
			<tr>
				<td align="right">Total Amount Due</td>
				<td align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${apInvoice.amount}" /></td>
			</tr>
		</table>
</div>
</body>
</html>