<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Retail - Purchase Order view form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${rPurchaseOrder.ebObjectId}");

	if ("${rPurchaseOrder.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}

	var supplierAcctId = "${rPurchaseOrder.supplierAccountId}";
	$(".prevUC").each (function (i, e) {
		var $prevUC = $(this);
		var itemId = $(this).closest("tr").find(".hdnItemId").val();
		var uri = contextPath + "/retailPurchaseOrder/getLatestUC?itemId="+itemId
				+"&supplierAcctId="+supplierAcctId;
		$.ajax({
			url: uri,
			success : function(unitCost) {
				$prevUC.text(formatDecimalPlaces(unitCost, 4));
			}
		});
	});
});
</script>
<style type="text/css">
.footerTblCls {
	font-size: 14px;
	font-style: normal;
	font-weight: bold;
}
</style>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Purchase Order</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">Purchase Order No. </td>
					<td class="label-value">${rPurchaseOrder.formattedPONumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${rPurchaseOrder.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Purchase Order Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${rPurchaseOrder.company.numberAndName}</td>
				</tr>
				<tr>
					<td class="label">Date </td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${rPurchaseOrder.poDate}"/></td>
				</tr>
				<tr class="hidden">
					<td class="label">Purchase Requisition </td>
					<td class="label-value">${rPurchaseOrder.strPrReferences}</td>
				</tr>
				<tr>
					<td class="label">Supplier </td>
					<td class="label-value">${rPurchaseOrder.supplier.numberAndName}</td>
				</tr>
				<tr>
					<td class="label">Supplier Account </td>
					<td class="label-value">${rPurchaseOrder.supplierAccount.name}</td>
				</tr>
				<tr class="hidden">
					<td class="label">Project </td>
					<td class="label-value">${rPurchaseOrder.strCustomerName}</td>
				</tr>
				<tr class="hidden">
					<td class="label">Fleet </td>
					<td class="label-value">${rPurchaseOrder.strFleetCode}</td>
				</tr>
				<tr>
					<td class="label">Term </td>
					<td class="label-value">${rPurchaseOrder.term.name}</td>
				</tr>
				<tr class="hidden">
					<td class="label">Requested By </td>
					<td class="label-value">${rPurchaseOrder.employeeName}</td>
				</tr>
				<tr>
					<td class="label">Remarks </td>
					<td class="label-value">${rPurchaseOrder.remarks}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Purchase Order Items</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="1%" class="th-td-norm">#</th>
						<th width="12%" class="th-td-norm">Stock Code</th>
						<th width="20%" class="th-td-norm">Description</th>
						<th width="15%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<th width="8%" class="th-td-norm">Previous<br>Unit Cost</th>
						<th width="8%" class="th-td-norm">Gross Price</th>
						<th width="15%" class="th-td-edge">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="poItem" items="${rPurchaseOrder.rPoItems}" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock Code -->
							<td class="th-td-norm v-align-top">${poItem.item.stockCode}
							<input type="hidden" class="hdnItemId" value="${poItem.itemId}"></td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${poItem.item.description}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${poItem.quantity}"/></td>
							<!-- Unit of Measure -->
							<td class="th-td-norm v-align-top">${poItem.item.unitMeasurement.name}</td>
							<!-- Prev. Unit Cost -->
							<td class="td-numeric v-align-top"><span class="prevUC"></span></td>
							<!-- Unit Cost -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='4'
							maxFractionDigits='4' value="${poItem.unitCost}"/></td>
							<c:set var="amount" value="${poItem.quantity * poItem.unitCost}" />
							<td class="th-td-edge txt-align-right v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value="${amount}"/></td>
							<c:set var="total" value="${total + amount}" />
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
				<tr>
					<td>Total</td>
					<td colspan="9" class="txt-align-right">
						<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${total}' />
					</td>
				 </tr>
			</tfoot>
			</table>
		</fieldset>
		<c:if test="${fn:length(rPurchaseOrder.poLines) gt 0}">
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
						<c:forEach items="${rPurchaseOrder.poLines}" var="apl" varStatus="status">
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
							<td colspan="4">Total</td>
							<td colspan="2" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${otherCharges}" /></td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
		</c:if>
		<br>
		<fieldset class="frmField_set" style="border: none;">
			<table>
				<tr>
					<td class="footerTblCls">Total Amount Due</td>
					<td class="footerTblCls" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${total + otherCharges}" />
					</td>
				</tr>
			</table>
		</fieldset>
		<br>
	</div>
</body>
</html>