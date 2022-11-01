<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: AR Invoice - Service View
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${arInvoice.ebObjectId}");
	if("${arInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
<title>AR Invoice - Service View</title>
</head>
<body>
<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">AR Invoice - Service</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">ARI No.</td>
					<td class="label-value">${arInvoice.sequenceNo}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${arInvoice.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>AR Invoice - Service Header</legend>
			<table>
				<tr>
					<td class="label">DR/WB/EU Reference</td>
					<td class="label-value">${arInvoice.drNumber}</td>
				</tr>
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${companyName}</td>
				</tr>
				<tr>
					<td class="label">Customer </td>
					<td class="label-value">${arInvoice.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${arInvoice.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${arInvoice.term.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${arInvoice.date}"/></td>
				</tr>
				<tr>
					<td class="label">Due Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${arInvoice.dueDate}"/></td>
				</tr>
				<tr>
					<td class="label">Ship To</td>
					<td class="label-value">${arInvoice.remarks}</td>
				</tr>
			</table>
		</fieldset>
		<c:if test="${fn:length(arInvoice.ariLines) gt 0}">
			<fieldset class="frmField_set">
			<legend>Service Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
						<th width="33%" class="th-td-norm">Service</th>
						<th width="5%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<th width="10%" class="th-td-norm">Gross Price</th>
						<th width="10%" class="th-td-norm">Discount</th>
						<th width="10%" class="th-td-norm">Tax Type</th>
						<th width="10%" class="th-td-norm">VAT Amount</th>
						<th width="10%" class="th-td-edge" style="border-right: 1px solid black;">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${arInvoice.ariLines}" var="line" varStatus="status">
						<tr style="border: 1px solid black;">
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${line.arLineSetup.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.quantity}" /></td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${line.unitMeasurement.name}</td>
							<!-- UP Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.upAmount}" /></td>
							<!-- Discount Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.discount}" /></td>
							<!-- Tax Type -->
							<td class="th-td-norm v-align-top">${line.taxType.name}</td>
							<!-- VAT Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.vatAmount}" /></td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${line.amount}" /></td>
						</tr>
						<c:set var="otherCharges" value="${otherCharges + line.amount}" />
						<c:set var="totalLineVat" value="${totalLineVat + line.vatAmount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2" style="font-weight:bold;">Total</td>
						<td colspan="7" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${otherCharges}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(arInvoice.arInvoiceTruckingLines) gt 0}">
			<fieldset class="frmField_set">
			<legend>Trucking Service Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
						<th width="33%" class="th-td-norm">Service</th>
						<th width="5%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<th width="10%" class="th-td-norm">Gross Price</th>
						<th width="10%" class="th-td-norm">Discount</th>
						<th width="10%" class="th-td-norm">Tax Type</th>
						<th width="10%" class="th-td-norm">VAT Amount</th>
						<th width="10%" class="th-td-edge" style="border-right: 1px solid black;">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${arInvoice.arInvoiceTruckingLines}" var="line" varStatus="status">
						<tr style="border: 1px solid black;">
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${line.arLineSetup.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.quantity}" /></td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${line.unitMeasurement.name}</td>
							<!-- UP Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.upAmount}" /></td>
							<!-- Discount Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.discount}" /></td>
							<!-- Tax Type -->
							<td class="th-td-norm v-align-top">${line.taxType.name}</td>
							<!-- VAT Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.vatAmount}" /></td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${line.amount}" /></td>
						</tr>
						<c:set var="otherCharges" value="${otherCharges + line.amount}" />
						<c:set var="totalLineVat" value="${totalLineVat + line.vatAmount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2" style="font-weight:bold;">Total</td>
						<td colspan="7" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${otherCharges}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(arInvoice.arInvoiceEquipmentLines) gt 0}">
			<fieldset class="frmField_set">
			<legend>Equipment Service Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
						<th width="33%" class="th-td-norm">Service</th>
						<th width="5%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<th width="10%" class="th-td-norm">Gross Price</th>
						<th width="10%" class="th-td-norm">Discount</th>
						<th width="10%" class="th-td-norm">Tax Type</th>
						<th width="10%" class="th-td-norm">VAT Amount</th>
						<th width="10%" class="th-td-edge" style="border-right: 1px solid black;">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${arInvoice.arInvoiceEquipmentLines}" var="line" varStatus="status">
						<tr style="border: 1px solid black;">
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${line.arLineSetup.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.quantity}" /></td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${line.unitMeasurement.name}</td>
							<!-- UP Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.upAmount}" /></td>
							<!-- Discount Type -->
							<td class="th-td-norm v-align-top">${line.itemDiscountType.name}</td>
							<!-- Discount Value -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.discountValue}" /></td>
							<!-- Discount Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.discount}" /></td>
							<!-- Tax Type -->
							<td class="th-td-norm v-align-top">${line.taxType.name}</td>
							<!-- VAT Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.vatAmount}" /></td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${line.amount}" /></td>
						</tr>
						<c:set var="equipmentTotal" value="${equipmentTotal + line.amount}" />
						<c:set var="totalLineVat" value="${totalLineVat + line.vatAmount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2" style="font-weight:bold;">Total</td>
						<td colspan="7" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${equipmentTotal}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
		</c:if>
		<br>
		<c:set var="totalVat" value="${totalLineVat}"/>
		<table class="frmField_set footerTotalAmountTbl">
			<tr>
				<td style="width: 22%;"></td>
				<td style="width: 22%;"></td>
				<td style="width: 22%; display: none;">Sub Total</td>
				<td style="width: 22%;"></td>
				<td style="width: 12%; display: none;"><span id="subTotal"></span></td>
			</tr>
			<tr>
				<td colspan="3">Total VAT</td>
				<td></td>
				<td>
					<fmt:formatNumber type="number" minFractionDigits="2" 
						maxFractionDigits="2" value="${totalVat}" />
				</td>
			</tr>
			<tr>
				<td colspan="3">Withholding Tax</td>
				<td>${arInvoice.wtAcctSetting.name}</td>
				<td colspan="7" align="right">
					<fmt:formatNumber type="number" minFractionDigits="2" 
						maxFractionDigits="2" value="${arInvoice.wtAmount}" />
				</td>
			</tr>
			<tr>
				<td colspan="3">Total Amount Due</td>
				<td></td>
				<td>
					<fmt:formatNumber type="number" minFractionDigits="2" 
						maxFractionDigits="2" value="${arInvoice.amount}" />
				</td>
			</tr>
		</table>
		<br>
	</div>
</body>
</html>