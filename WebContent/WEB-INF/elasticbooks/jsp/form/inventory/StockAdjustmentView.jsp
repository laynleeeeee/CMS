<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Stock Adjustment view form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${stockAdjustment.ebObjectId}");

	if ("${stockAdjustment.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">Stock Adjustment</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">SA
						<c:choose>
							<c:when test="${typeId == 1}">I</c:when>
							<c:otherwise>O</c:otherwise>
						</c:choose>No.</td>
					<td class="label-value">${stockAdjustment.formattedSANumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${stockAdjustment.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Stock Adjustment Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${stockAdjustment.company.numberAndName}</td>
				</tr>
				<tr>
					<td class="label">Warehouse </td>
					<td class="label-value">${stockAdjustment.warehouse.name}</td>
				</tr>
				<tr>
					<td class="label">Adjustment Type </td>
					<td class="label-value">${stockAdjustment.adjustmentType.name}</td>
				</tr>
				<tr>
					<td class="label">Date </td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${stockAdjustment.saDate}"/></td>
				</tr>
				<tr>
					<td class="label">Remarks </td>
					<td class="label-value">${stockAdjustment.remarks}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Stock Adjustment Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="12%" class="th-td-norm">Stock Code</th>
						<th width="17%" class="th-td-norm">Description</th>
						<th width="5%" class="th-td-norm">Existing <br>Stocks</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<c:choose>
							<c:when test="${typeId == 1}">
								<th width="12%" class="th-td-norm">Adjustment<br>Qty</th>
								<th width="12%" class="th-td-norm">Gross Price</th>
								<th width="12%" class="th-td-edge">Total</th>
							</c:when>
							<c:otherwise>
								<th width="12%" class="th-td-edge">Adjustment<br>Qty</th>
							</c:otherwise>
						</c:choose>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${stockAdjustment.saItems}" var="saItem" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock Code -->
							<td class="th-td-norm v-align-top">${saItem.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${saItem.item.description}</td>
							<!-- Existing Stocks -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${saItem.item.existingStocks}" /></td>
							<!-- Unit of Measure -->
							<td class="th-td-norm v-align-top">${saItem.item.unitMeasurement.name}</td>
							<c:choose>
							<c:when test="${typeId == 1}">
								<!-- Quantity -->
								<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${saItem.quantity}" /></td>
								<!-- Unit Cost -->
								<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='4'
										maxFractionDigits='4' value="${saItem.unitCost}" /></td>
								<!-- Total -->
								<td class="th-td-edge v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${saItem.quantity * saItem.unitCost}" /></td>
							</c:when>
							<c:otherwise>
								<td class="th-td-edge v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${saItem.quantity}" /></td>
							</c:otherwise>
						</c:choose>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</div>
</body>
</html>