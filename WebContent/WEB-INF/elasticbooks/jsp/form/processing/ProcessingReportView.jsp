<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Processing report view form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
	formObjectId = parseInt("${processingReport.ebObjectId}");

	if ("${processingReport.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">
			Processing - ${processingReport.processingType.name}
		</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table>
					<tr>
						<td class="label">
							Sequence No.
						</td>
						<td class="label-value">${processingReport.formattedPRNumber}</td>
					</tr>
					<tr>
						<td class="label">Status </td>
						<td class="label-value">${processingReport.formWorkflow.currentFormStatus.description}</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Processing Header</legend>
				<table class="formTable">
					<tr>
						<td class="label">Company</td>
						<td class="label-value">${processingReport.company.numberAndName}</td>
					</tr>
					<tr>
						<td class="label">Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${processingReport.date}"/>
						</td>
					</tr>
					<tr>
						<td class="label">Ref No.</td>
						<td class="label-value">${processingReport.refNumber}</td>
					</tr>
					<tr>
						<td class="label">Remarks.</td>
						<td class="label-value">${processingReport.remarks}</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
			<legend>Raw Materials Item Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="10%" class="th-td-norm">Warehouse</th>
						<th width="10%" class="th-td-norm">Selected<br>Stocks</th>
						<th width="8%" class="th-td-norm">Bags</th>
						<th width="8%" class="th-td-norm">Qty</th>
						<th width="8%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${processingReport.prRawMaterialsItems}" var="rmItem" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock code -->
							<td class="th-td-norm v-align-top">${rmItem.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${rmItem.item.description}</td>
							<!-- Warehouse list -->
							<td class="th-td-norm v-align-top">${rmItem.warehouse.name}</td>
							<!-- Existing stocks -->
							<td class="th-td-norm v-align-top">${rmItem.source}</td>
							<!-- Bag -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${rmItem.itemBagQuantity}" />
							</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${rmItem.quantity}" />
							</td>

							<!-- UOM -->
							<td class="th-td-norm v-align-top">${rmItem.item.unitMeasurement.name}</td>
						</tr>
						<c:set var="totalRMIQuantity" value="${totalRMIQuantity + rmItem.quantity}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="5" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalRMIQuantity}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>

			<br>
			<fieldset class="frmField_set">
			<legend>Other Materials Item Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="10%" class="th-td-norm">Warehouse</th>
						<th width="10%" class="th-td-norm">Selected<br>Stocks</th>
						<th width="8%" class="th-td-norm">Bags</th>
						<th width="8%" class="th-td-norm">Qty</th>
						<th width="8%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${processingReport.prOtherMaterialsItems}" var="omItem" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock code -->
							<td class="th-td-norm v-align-top">${omItem.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${omItem.item.description}</td>
							<!-- Warehouse list -->
							<td class="th-td-norm v-align-top">${omItem.warehouse.name}</td>
							<!-- Existing stocks -->
							<td class="th-td-norm v-align-top">${omItem.source}</td>
							<!-- Bag -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${omItem.itemBagQuantity}" />
							</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${omItem.quantity}" />
							</td>

							<!-- UOM -->
							<td class="th-td-norm v-align-top">${omItem.item.unitMeasurement.name}</td>
						</tr>
						<c:set var="totalOMIQuantity" value="${totalOMIQuantity + omItem.quantity}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="5" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalOMIQuantity}" /></td>
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
					<c:forEach items="${processingReport.prOtherCharges}" var="arLine" varStatus="status">
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
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${arLine.amount}" /></td>
						</tr>
						<c:set var="otherCharges" value="${otherCharges + arLine.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${otherCharges}" /></td>
					</tr>
					<c:set var="totalSales" value="${otherCharges + totalAmount}" />
				</tfoot>
			</table>
			</fieldset>

			<br>
			<fieldset class="frmField_set">
			<legend>Main Product Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="10%" class="th-td-norm">Warehouse</th>
						<th width="8%" class="th-td-norm">Bags</th>
						<th width="8%" class="th-td-norm">Qty</th>
						<th width="8%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${processingReport.prMainProducts}" var="mp" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock code -->
							<td class="th-td-norm v-align-top">${mp.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${mp.item.description}</td>
							<!-- Warehouse list -->
							<td class="th-td-norm v-align-top">${mp.warehouse.name}</td>
							<!-- Bags -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${mp.itemBagQuantity}" />
							</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${mp.quantity}" />
							</td>

							<!-- UOM -->
							<td class="th-td-norm v-align-top">${mp.item.unitMeasurement.name}</td>
						</tr>
						<c:set var="totalMPQuantity" value="${totalMPQuantity + mp.quantity}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalMPQuantity}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>

			<br>
			<fieldset class="frmField_set">
			<legend>By Product Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="10%" class="th-td-norm">Warehouse</th>
						<th width="8%" class="th-td-norm">Bags</th>
						<th width="8%" class="th-td-norm">Qty</th>
						<th width="8%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${processingReport.prByProducts}" var="bp" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock code -->
							<td class="th-td-norm v-align-top">${bp.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${bp.item.description}</td>
							<!-- Warehouse list -->
							<td class="th-td-norm v-align-top">${bp.warehouse.name}</td>
							<!-- Bags -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${bp.itemBagQuantity}" />
							</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${bp.quantity}" />
							</td>

							<!-- UOM -->
							<td class="th-td-norm v-align-top">${bp.item.unitMeasurement.name}</td>
						</tr>
						<c:set var="totalBPQuantity" value="${totalBPQuantity + bp.quantity}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalBPQuantity}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>