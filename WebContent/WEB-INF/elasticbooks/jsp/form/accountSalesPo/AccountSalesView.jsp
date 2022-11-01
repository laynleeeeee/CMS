<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Account Sales view.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<style type="text/css">
.monetary {
	text-align: right;
}
</style>
<script type="text/javascript">
$(document).ready(function () {

	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${accountSales.ebObjectId}");

	if ("${accountSales.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Sales Order</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">Sales No. </td>
					<td class="label-value">${accountSales.formattedPONumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${accountSales.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Sales Order Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${accountSales.company.numberAndName}</td>
				</tr>
				<tr>
					<td class="label">Date </td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${accountSales.poDate}"/></td>
				</tr>
				<tr>
					<td class="label">Customer </td>
					<td class="label-value">${accountSales.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer  Account </td>
					<td class="label-value">${accountSales.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Remarks </td>
					<td class="label-value">${accountSales.remarks}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Sales Order Items</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="1%" class="th-td-norm">#</th>
						<th width="14%" class="th-td-norm">Stock Code</th>
						<th width="20%" class="th-td-norm">Description</th>
						<th width="15%" class="th-td-norm">Existing Stocks</th>
						<th width="15%" class="th-td-norm">Warehouse</th>
						<th width="15%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="poItem" items="${accountSales.asPoItems}" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock Code -->
							<td class="th-td-norm v-align-top">${poItem.item.stockCode}
							<input type="hidden" class="hdnItemId" value="${poItem.itemId}"></td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${poItem.item.description}</td>
							<!-- Existing Stocks -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${poItem.existingStocks}" /></td>
							<!-- Warehouse -->
							<td class="th-td-norm v-align-top">${poItem.warehouse.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${poItem.quantity}" /></td>
							<!-- Unit of Measure -->
							<td class="th-td-norm v-align-top">${poItem.item.unitMeasurement.name}</td>
							<c:set var="totalQuantity" value="${totalQuantity + poItem.quantity}" />
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="5" style="font-weight:bold;">Total</td>
						<td colspan="1" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalQuantity}" /></td>
					</tr>
				</tfoot>
			</table>
		</fieldset>
	</div>
</body>
</html>