<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: NSB Stock Adjustment view form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable2.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview2.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${stockAdjustmentDto.stockAdjustment.ebObjectId}");
	if ("${stockAdjustmentDto.stockAdjustment.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	if("${hasReference}" == "false"){
		$("#referenceDocumentsFieldset").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">Stock Adjustment
		<c:choose>
				<c:when test="${typeId == 1}"> - IN</c:when>
				<c:otherwise> - OUT</c:otherwise>
		</c:choose>
		 - ${stockAdjustmentDto.stockAdjustment.division.name}</div>
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
					<td class="label-value">${stockAdjustmentDto.stockAdjustment.saNumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${stockAdjustmentDto.stockAdjustment.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Stock Adjustment Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${stockAdjustmentDto.stockAdjustment.company.numberAndName}</td>
				</tr>
				<tr>
					<td class="label">Division </td>
					<td class="label-value">${stockAdjustmentDto.stockAdjustment.division.name}</td>
				</tr>
				<tr>
					<td class="label">Warehouse </td>
					<td class="label-value">${stockAdjustmentDto.stockAdjustment.warehouse.name}</td>
				</tr>
				<tr>
					<td class="label">Adjustment Type </td>
					<td class="label-value">${stockAdjustmentDto.stockAdjustment.adjustmentType.name}</td>
				</tr>
				<tr>
					<td class="label">BMS No.</td>
					<td class="label-value">${stockAdjustmentDto.stockAdjustment.bmsNumber}</td>
				</tr>
				<tr>
					<td class="label">Date </td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${stockAdjustmentDto.stockAdjustment.saDate}"/></td>
				</tr>
				<tr>
					<td class="label">Remarks </td>
					<td class="label-value">${stockAdjustmentDto.stockAdjustment.remarks}</td>
				</tr>
				<c:if test="${typeId == 1}">
					<tr>
						<td class="label">Currency</td>
						<td class="label-value">${stockAdjustmentDto.stockAdjustment.currency.name}</td>
					</tr>
				</c:if>
			</table>
		</fieldset>
		<c:if test="${not empty stockAdjustmentDto.serialItems}">
			<fieldset class="frmField_set">
				<legend>Serialized Good/s</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="5%" class="th-td-norm">#</th>
							<th width="12%" class="th-td-norm">Stock Code</th>
							<th width="12%" class="th-td-norm">Description</th>
							<c:if test="${typeId == 1}">
								<th width="10%" class="th-td-norm">Customer PO No.</th>
							</c:if>
							<th width="5%" class="th-td-norm">Available <br>Stocks</th>
							<th width="10%" class="th-td-norm">Serial Number</th>
							<th width="5%" class="th-td-norm">UOM</th>
							<th width="12%" class="th-td-edge">Adjustment<br>Qty</th>
							<th width="12%" class="th-td-norm">Unit Cost</th>
							<th width="12%" class="th-td-edge">Total</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${stockAdjustmentDto.serialItems}" var="saItem" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<!-- Stock Code -->
								<td class="th-td-norm v-align-top">${saItem.item.stockCode}</td>
								<!-- Description -->
								<td class="th-td-norm v-align-top">${saItem.item.description}</td>
								<!-- Description -->
								<c:if test="${typeId == 1}">
									<td class="th-td-norm v-align-top">${saItem.poNumber}</td>
								</c:if>
								<!-- Existing Stocks -->
								<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value="${saItem.existingStocks}" /></td>
								<td class="th-td-norm v-align-top">${saItem.serialNumber}</td>
								<!-- Unit of Measure -->
								<td class="th-td-norm v-align-top">${saItem.item.unitMeasurement.name}</td>
								<c:choose>
								<c:when test="${typeId == 1}">
									<!-- Quantity -->
									<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
											maxFractionDigits='2' value="${saItem.quantity}" /></td>
									<!-- Unit Cost -->
									<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='4'
											maxFractionDigits='6' value="${saItem.unitCost}" /></td>
									<!-- Total -->
									<td class="th-td-edge v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
											maxFractionDigits='2' value="${saItem.amount}" /></td>
									<c:set var="totalSiInRowAmount" value="${totalSiInRowAmount + saItem.amount}"/>
								</c:when>
								<c:otherwise>
									<!-- Quantity -->
									<td class="th-td-edge v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
											maxFractionDigits='2' value="${saItem.quantity}" /></td>
									<!-- Unit Cost -->
									<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='4'
											maxFractionDigits='4' value="${Math.abs(saItem.unitCost)}" /></td>
									<!-- Total -->
									<td class="th-td-edge v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
											maxFractionDigits='2' value="${saItem.amount}" /></td>
									<c:set var="totalSiOutRowAmount" value="${totalSiOutRowAmount + saItem.amount}"/>
								</c:otherwise>
							</c:choose>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<c:choose>
							<c:when test="${typeId == 1}">
								<td colspan="9" style="font-weight:bold;">Total</td>
								<td style="text-align: right; font-weight: bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalSiInRowAmount}"/>
								</td>
							</c:when>
							<c:otherwise>
								<td colspan="8" style="font-weight:bold;">Total</td>
								<td style="text-align: right; font-weight: bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalSiOutRowAmount}"/>
								</td>
							</c:otherwise>
						</c:choose>
					</tfoot>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty stockAdjustmentDto.stockAdjustment.saItems}">
			<fieldset class="frmField_set">
				<legend>Non-Serialized Good/s</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="5%" class="th-td-norm">#</th>
							<th width="12%" class="th-td-norm">Stock Code</th>
							<th width="12%" class="th-td-norm">Description</th>
							<c:if test="${typeId == 1}">
								<th width="10%" class="th-td-norm">Customer PO No.</th>
							</c:if>
							<th width="5%" class="th-td-norm">Existing <br>Stocks</th>
							<th width="5%" class="th-td-norm">UOM</th>
							<th width="12%" class="th-td-edge">Adjustment<br>Qty</th>
							<th width="12%" class="th-td-norm">Unit Cost</th>
							<th width="12%" class="th-td-edge">Total</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${stockAdjustmentDto.stockAdjustment.saItems}" var="saItem" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<!-- Stock Code -->
								<td class="th-td-norm v-align-top">${saItem.item.stockCode}</td>
								<!-- Description -->
								<td class="th-td-norm v-align-top">${saItem.item.description}</td>
								<!-- Customer PO No. -->
								<c:if test="${typeId == 1}">
									<td class="th-td-norm v-align-top">${saItem.poNumber}</td>
								</c:if>
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
											maxFractionDigits='2' value="${saItem.amount}" /></td>
									<c:set var="totalInRowAmount" value="${totalInRowAmount + saItem.amount}"/>
								</c:when>
								<c:otherwise>
									<td class="th-td-edge v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
											maxFractionDigits='2' value="${saItem.quantity}" /></td>
									<!-- Unit Cost -->
									<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='4'
											maxFractionDigits='4' value="${Math.abs(saItem.unitCost)}" /></td>
									<!-- Total -->
									<td class="th-td-edge v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
											maxFractionDigits='2' value="${saItem.amount}" /></td>
									<c:set var="totalOutRowAmount" value="${totalOutRowAmount + saItem.amount}"/>
								</c:otherwise>
							</c:choose>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<c:choose>
							<c:when test="${typeId == 1}">
								<td colspan="8" style="font-weight:bold;">Total</td>
								<td style="text-align: right; font-weight: bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalInRowAmount}"/>
								</td>
							</c:when>
							<c:otherwise>
								<td colspan="7" style="font-weight:bold;">Total</td>
								<td style="text-align: right; font-weight: bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalOutRowAmount}"/>
								</td>
							</c:otherwise>
						</c:choose>
					</tfoot>
				</table>
			</fieldset>
		</c:if>
		<fieldset class="frmField_set" id="referenceDocumentsFieldset">
		<legend>Document/s</legend>
		<table class="dataTable" id="referenceDocuments">
			<thead>
				<tr>
					<th width="3%" class="th-td-norm">#</th>
					<th width="18%" class="th-td-norm">Name</th>
					<th width="18%" class="th-td-norm">Description</th>
					<th width="18%" class="th-td-norm">file</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${stockAdjustmentDto.stockAdjustment.referenceDocuments}" var="refDoc" varStatus="status">
					<tr> 
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<td class="th-td-norm v-align-top">${refDoc.fileName}</td>
						<td class="th-td-norm v-align-top">${refDoc.description}</td>
						<td class="th-td-norm v-align-top" id="file">
							<a href="${refDoc.file}" download="${refDoc.fileName}" class="fileLink" id="file">${refDoc.fileName}</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</fieldset>
	</div>
</body>
</html>