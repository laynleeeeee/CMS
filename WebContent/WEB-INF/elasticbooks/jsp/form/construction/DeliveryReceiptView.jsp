<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Delivery Receipt - Goods view
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${deliveryReceipt.ebObjectId}");
	if("${deliveryReceipt.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
<title>Delivery Receipt - Goods View</title>
</head>
<body>
<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">Delivery Receipt - Goods</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">DR No.</td>
					<td class="label-value">${deliveryReceipt.sequenceNo}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${deliveryReceipt.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Delivery Receipt - Goods Header</legend>
			<table>
				<tr>
					<td class="label">SO Reference</td>
					<td class="label-value">${deliveryReceipt.atwNumber}</td>
				</tr>
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${deliveryReceipt.company.name}</td>
				</tr>
				<tr>
					<td class="label">Customer </td>
					<td class="label-value">${deliveryReceipt.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${deliveryReceipt.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${deliveryReceipt.term.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${deliveryReceipt.date}"/></td>
				</tr>
				<tr>
					<td class="label">Due Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${deliveryReceipt.dueDate}"/></td>
				</tr>
				<tr>
					<td class="label">Ship To</td>
					<td class="label-value">${deliveryReceipt.remarks}</td>
				</tr>
			</table>
		</fieldset>
		<c:if test="${fn:length(deliveryReceipt.serialDrItems) gt 0}">
			<fieldset class="frmField_set">
				<legend>Serialized Goods</legend>
					<table class="dataTable" id="serialItems" style="width: 100%;">
						<thead>
							<tr>
								<th width="3%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
								<th width="18%" class="th-td-norm">Stock Code</th>
								<th width="20%" class="th-td-norm">Description</th>
								<th width="15%" class="th-td-norm">Warehouse</th>
								<th width="15%" class="th-td-norm">Serial Number</th>
								<th width="10%" class="th-td-norm">Available <br> Stocks</th>
								<th width="10%" class="th-td-norm">Qty</th>
								<th width="10%" class="th-td-norm">UOM</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${deliveryReceipt.serialDrItems}" var="drSerialItem" varStatus="status">
								<tr style=" border-bottom: 1px solid black;"> 
									<td class="td-numeric v-align-top" style="border-bottom: 1px solid black; border-left: 1px solid black;">${status.index + 1}</td>
									<td class="th-td-norm v-align-top">${drSerialItem.item.stockCode}</td>
									<td class="th-td-norm v-align-top">${drSerialItem.item.description}</td>
									<td class="th-td-norm v-align-top">${drSerialItem.warehouse.name}</td>
									<td class="th-td-norm v-align-top">${drSerialItem.serialNumber}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${drSerialItem.existingStocks}" />
									</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${drSerialItem.quantity}" />
									</td>
									<td class="th-td-norm v-align-top">${drSerialItem.item.unitMeasurement.name}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(deliveryReceipt.nonSerialDrItems) gt 0}">
			<fieldset class="frmField_set">
				<legend>Non-Serialized Goods</legend>
					<table class="dataTable" id="nonSerialItems" style="width: 100%;">
						<thead>
							<tr>
								<th width="3%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
								<th width="20%" class="th-td-norm">Stock Code</th>
								<th width="20%" class="th-td-norm">Description</th>
								<th width="20%" class="th-td-norm">Warehouse</th>
								<th width="10%" class="th-td-norm">Existing <br> Stocks</th>
								<th width="12%" class="th-td-norm">Qty</th>
								<th width="15%" class="th-td-norm">UOM</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${deliveryReceipt.nonSerialDrItems}" var="drNonSerialItem" varStatus="status">
								<tr style=" border-bottom: 1px solid black;"> 
									<td class="td-numeric v-align-top" style="border-bottom: 1px solid black; border-left: 1px solid black;">${status.index + 1}</td>
									<td class="th-td-norm v-align-top">${drNonSerialItem.item.stockCode}</td>
									<td class="th-td-norm v-align-top">${drNonSerialItem.item.description}</td>
									<td class="th-td-norm v-align-top">${drNonSerialItem.warehouse.name}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${drNonSerialItem.existingStocks}" />
									</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${drNonSerialItem.quantity}" />
									</td>
									<td class="th-td-norm v-align-top">${drNonSerialItem.item.unitMeasurement.name}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(deliveryReceipt.drLines) gt 0}">
			<fieldset class="frmField_set">
			<legend>Service Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
						<th width="13%" class="th-td-norm">Service</th>
						<th width="5%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<th width="10%" class="th-td-norm">Gross Price</th>
						<th width="10%" class="th-td-norm">Discount Type</th>
						<th width="10%" class="th-td-norm">Discount Value</th>
						<th width="10%" class="th-td-norm">Computed Discount</th>
						<th width="10%" class="th-td-norm">Tax Type</th>
						<th width="10%" class="th-td-norm">VAT Amount</th>
						<th width="10%" class="th-td-edge" style="border-right: 1px solid black;">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${deliveryReceipt.drLines}" var="line" varStatus="status">
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
						<c:set var="otherCharges" value="${otherCharges + line.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4" style="font-weight:bold;">Total</td>
						<td colspan="7" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${otherCharges}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
		</c:if>
		<c:set var="grandTotal" value="${totalSerialAmount + totalNonSerialAmount + otherCharges}"/>
		<table class="frmField_set">
		</table>
	</div>
</body>
</html>