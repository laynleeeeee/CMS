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
	if(status == "true") {
		$("#imgEdit").hide();
	}
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
	<div class="modFormLabel">Receiving Report - ${apInvoice.receivingReport.division.name}</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">RR No.</td>
				<td class="label-value">${apInvoice.sequenceNumber}</td>
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
				<td class="label">Division</td>
				<td class="label-value">${apInvoice.receivingReport.division.name}</td>
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
				<td class="label">BMS No.</td>
				<td class="label-value">${apInvoice.receivingReport.bmsNumber}</td>
			</tr>
			<tr>
				<td class="label">Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoice.glDate}"/></td>
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
				<td class="label">Supplier Invoice No</td>
				<td class="label-value">${apInvoice.invoiceNumber}</td>
			</tr>
			<tr>
				<td class="label">Delivery Receipt No</td>
				<td class="label-value">${apInvoice.receivingReport.deliveryReceiptNo}</td>
			</tr>
			<tr>
				<td class="label">Remarks</td>
				<td class="label-value">${apInvoice.receivingReport.remarks}</td>
			</tr>
		</table>
	</fieldset>
	<c:if test="${not empty apInvoice.receivingReport.serialItems}">
		<fieldset class="frmField_set">
			<legend>Serialized Good/s</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="1%" class="th-td-norm">#</th>
						<th width="17%" class="th-td-norm">Stock Code</th>
						<th width="22%" class="th-td-norm">Description</th>
						<th width="7%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="7%" class="th-td-norm">QTY</th>
						<th width="7%" class="th-td-norm">Serial Number</th>
						<th width="7%" class="th-td-norm">UOM</th>
					</tr>
					<tr>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="si" items="${apInvoice.receivingReport.serialItems}" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${si.item.stockCode}
								<input type="hidden" class="hdnItemId" value="${si.itemId}"/>
							</td>
							<td class="th-td-norm v-align-top">${si.item.description}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${si.existingStocks}" /></td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="6" value="${si.quantity}" /></td>
							<td class="th-td-norm v-align-top">${si.serialNumber}</td>
							<td class="th-td-norm v-align-top">${si.item.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${not empty apInvoice.rrItems}">
		<fieldset class="frmField_set">
			<legend>Non-Serialized Good/s</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="1%" class="th-td-norm">#</th>
						<th width="17%" class="th-td-norm">Stock Code</th>
						<th width="22%" class="th-td-norm">Description</th>
						<th width="7%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="7%" class="th-td-norm">QTY</th>
						<th width="7%" class="th-td-norm">UOM</th>
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
											maxFractionDigits="6" value="${rr.quantity}" /></td>
							<td class="th-td-norm v-align-top">${rr.item.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${not empty apInvoice.apInvoiceLines}">
		<fieldset class="frmField_set">
			<legend>Service/s</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="25%" class="th-td-norm">AP Line</th>
						<th width="10%" class="th-td-norm">Percentile</th>
						<th width="10%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${apInvoice.apInvoiceLines}" var="apl" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- AR Line Setup -->
							<td class="th-td-norm v-align-top">${apl.apLineSetup.name}</td>
							<!-- Percentile -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${apl.percentile}" />
							</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="6" value="${apl.quantity}" />
							</td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${apl.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${not empty apInvoice.referenceDocuments}">
			<fieldset class="frmField_set">
				<legend>Documents</legend>
				<table class="dataTable" id="referenceDocuments">
					<thead>
						<tr>
							<th width="3%" class="th-td-norm">#</th>
							<th width="18%" class="th-td-norm">Name</th>
							<th width="18%" class="th-td-norm">Description</th>
							<th width="18%" class="th-td-edge">File</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${apInvoice.referenceDocuments}" var="doc" varStatus="status">
							<tr> 
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${doc.fileName}</td>
								<td class="th-td-norm v-align-top">${doc.description}</td>
								<td class="th-td-edge v-align-top" id="file">
									<a href="${doc.file}" download="${doc.fileName}" class="fileLink" id="file">${doc.fileName}</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
</div>
</body>
</html>