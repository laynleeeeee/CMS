<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Stock Adjustment form for Individual Selection
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {

	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${stockAdjustment.ebObjectId}");

	if ("${stockAdjustment.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}

	if("${typeId}" == 4) {
		$("#tblStockAdj tbody tr").each (function (i, o) {
			var $sourceObjectId = $(this).find(".referenceObject");
			var itemId = $(this).find("#itemId").val();
			setRefShortDesc ($sourceObjectId, 3, $sourceObjectId.attr("id"), contextPath);
		});
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">Stock Adjustment
			<c:choose>
				<c:when test="${typeId == 3}"> IN</c:when>
				<c:otherwise> OUT</c:otherwise>
			</c:choose> - IS
		</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">SA
						<c:choose>
							<c:when test="${typeId == 3}"> I</c:when>
							<c:otherwise> O</c:otherwise>
						</c:choose> - IS No.</td>
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
			<table class="dataTable" id="tblStockAdj">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="15%" class="th-td-norm">Stock Code</th>
						<th width="20%" class="th-td-norm">Description</th>
						<th width="10%" class="th-td-norm">
							<c:choose>
								<c:when test="${typeId == 3}">Existing</c:when>
								<c:otherwise>Selected</c:otherwise>
							</c:choose>
							<br>Bags/Stocks</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<c:choose>
							<c:when test="${typeId == 3}">
								<th width="10%" class="th-td-norm">Bags</th>
								<th width="10%" class="th-td-norm">Adjustment<br>Qty</th>
								<th width="10%" class="th-td-norm">Unit Cost</th>
								<th width="10%" class="th-td-edge">Total</th>
							</c:when>
							<c:otherwise>
								<th width="20%" class="th-td-norm">Bags</th>
								<th width="20%" class="th-td-edge">Adjustment<br>Qty</th>
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
							<c:choose>
								<c:when test="${typeId == 3}">
								<!-- Existing Stocks -->
									<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${saItem.existingStocks}"/>
									</td>
								</c:when>
								<c:otherwise>
								<!-- Selected Stocks -->
									<td class="th-td-norm v-align-top">
										<input type="hidden" id="itemId" value="${saItem.item.id}">
										<span class="referenceObject" id="${saItem.ebObjectId}"></span>
									</td>
								</c:otherwise>
							</c:choose>
							<!-- Unit of Measure -->
							<td class="th-td-norm v-align-top">${saItem.item.unitMeasurement.name}</td>
							<c:choose>
							<c:when test="${typeId == 3}">
								<!-- Bags -->
								<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${saItem.itemBagQuantity}" /></td>
								<!-- Quantity -->
								<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${saItem.quantity}" /></td>
								<!-- Unit Cost -->
								<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='6'
										maxFractionDigits='6' value="${saItem.unitCost}" /></td>
								<!-- Total -->
								<td class="th-td-edge v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${saItem.quantity * saItem.unitCost}" /></td>
							</c:when>
							<c:otherwise>
								<!-- Bags -->
								<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${saItem.itemBagQuantity}" /></td>
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