<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Transfer Receipt - Individual Selection view form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {

	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${rTransferReceipt.ebObjectId}");

	if ("${rTransferReceipt.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	$("#tblTRItem tbody tr").each (function (i, o) {
		var $sourceObjectId = $(this).find(".referenceObject");
		setRefShortDesc ($sourceObjectId, 8, $sourceObjectId.attr("id"), contextPath);
	});
});
</script>
</head>
<body>
	<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">Transfer Receipt - IS</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">TR - IS No. </td>
					<td class="label-value">${rTransferReceipt.formattedTRNumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${rTransferReceipt.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Transfer Receipt Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${rTransferReceipt.company.numberAndName}</td>
				</tr>
				<tr>
					<td class="label">Warehouse From </td>
					<td class="label-value">${rTransferReceipt.warehouseFrom.name}</td>
				</tr>
				<tr>
					<td class="label">To </td>
					<td class="label-value">${rTransferReceipt.warehouseTo.name}</td>
				</tr>
				<tr>
					<td class="label">Date </td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${rTransferReceipt.trDate}"/></td>
				</tr>
				<tr>
					<td class="label">Delivery Receipt No. </td>
					<td class="label-value">${rTransferReceipt.drNumber}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Transfer Receipt Items</legend>
			<table class="dataTable" id="tblTRItem">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="25%" class="th-td-norm">Stock Code</th>
						<th width="35%" class="th-td-norm">Description</th>
						<th width="10%" class="th-td-norm">Selected <br>Bags/Stocks</th>
						<th width="12%" class="th-td-norm">Bags</th>
						<th width="15%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-edge">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${rTransferReceipt.rTrItems}" var="trItem" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock Code -->
							<td class="th-td-norm v-align-top">${trItem.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${trItem.item.description}</td>
							<!-- Selected Stocks -->
							<td class="th-td-norm v-align-top">
								<span class="referenceObject" id="${trItem.ebObjectId}"></span>
							</td>
							<!-- Bags -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${trItem.itemBagQuantity}" /></td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${trItem.quantity}" /></td>
							<!-- Unit of Measure -->
							<td class="th-td-edge txt-align-left">${trItem.item.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</div>
</body>
</html>