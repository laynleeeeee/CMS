<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Withdrawal Slip view form for JYEI.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${wsDto.withdrawalSlip.ebObjectId}");
	var warehouseId = "${wsDto.withdrawalSlip.warehouseId}";
	if ("${wsDto.withdrawalSlip.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	$(".prevUC").each (function (i, o) {
		var $prevUC = $(this);
		var itemId = $(this).closest("tr").find(".hdnItemId").val();
		var uri = contextPath + "/retailPurchaseOrder/getLatestUc?itemId="+itemId
				+"&warehouseId="+warehouseId;
		$.ajax({
			url: uri,
			success : function(unitCost) {
				$prevUC.text(unitCost);
			}
		});
	});
});
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Withdrawal Slip
		</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">Sequence No. </td>
					<td class="label-value">${wsDto.withdrawalSlip.wsNumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${wsDto.withdrawalSlip.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Purchase Order Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${wsDto.withdrawalSlip.companyName}</td>
				</tr>
				<tr>
					<td class="label">Requisition Form Reference</td>
					<td class="label-value">${wsDto.withdrawalSlip.poNumber}</td>
				</tr>
				<tr>
					<td class="label">Warehouse </td>
					<td class="label-value">${wsDto.withdrawalSlip.warehouseName}</td>
				</tr>
				<tr>
					<td class="label">Customer/Project</td>
					<td class="label-value">${wsDto.withdrawalSlip.customerName}</td>
				</tr>
				<tr>
					<td class="label">Customer Account </td>
					<td class="label-value">${wsDto.withdrawalSlip.customerAccountName}</td>
				</tr>
				<tr>
					<td class="label">Fleet </td>
					<td class="label-value">${wsDto.withdrawalSlip.fleetName}</td>
				</tr>
				<tr>
					<td class="label">Plate No </td>
					<td class="label-value">${wsDto.fleetPlateNo}</td>
				</tr>
				<tr>
					<td class="label">Account </td>
					<td class="label-value">${wsDto.withdrawalSlip.accountName}</td>
				</tr>
				<tr>
					<td class="label">Date </td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${wsDto.withdrawalSlip.date}"/></td>
				</tr>
				<tr>
					<td class="label">Requested By </td>
					<td class="label-value">${wsDto.withdrawalSlip.requesterName}</td>
				</tr>
				<tr>
					<td class="label">Remarks </td>
					<td class="label-value">${wsDto.withdrawalSlip.remarks}</td>
				</tr>
				<tr>
					<td class="label">Withdrawn By </td>
					<td class="label-value">${wsDto.jyeiWithdrawalSlip.withdrawnBy}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Withdrawal Slip Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="1%" class="th-td-norm">#</th>
						<th width="12%" class="th-td-norm">Stock Code</th>
						<th width="20%" class="th-td-norm">Description</th>
						<th width="5%" class="th-td-norm">Existing <br>Stocks</th>
						<th width="15%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="wsItem" items="${wsDto.withdrawalSlip.withdrawalSlipItems}" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock Code -->
							<td class="th-td-norm v-align-top">${wsItem.item.stockCode}
							<input type="hidden" class="hdnItemId" value="${wsItem.itemId}"></td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${wsItem.item.description}</td>
							<!-- Existing Stocks -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${wsItem.existingStocks}" /></td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${wsItem.quantity}" /></td>
							<!-- Unit of Measure -->
							<td class="th-td-norm v-align-top">${wsItem.item.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Serialized Item Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="12%" class="th-td-norm">Stock Code</th>
						<th width="17%" class="th-td-norm">Description</th>
						<th width="5%" class="th-td-norm">Available <br>Stocks</th>
							<th width="12%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">Serial Number</th>
						<th width="10%" class="th-td-edge">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${wsDto.serialItems}" var="sItem" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- Stock Code -->
							<td class="th-td-norm v-align-top">${sItem.item.stockCode}</td>
							<!-- Description -->
							<td class="th-td-norm v-align-top">${sItem.item.description}</td>
							<!-- Existing Stocks -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${sItem.existingStocks}" /></td>
							<td class="th-td-norm v-align-top txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${sItem.quantity}" /></td>
							<td class="th-td-norm v-align-top">${sItem.serialNumber}</td>
							<!-- Unit of Measure -->
							<td class="th-td-edge v-align-top">${sItem.item.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
		<c:if test="${fn:length(wsDto.referenceDocuments) gt 0}">
			<fieldset class="frmField_set">
				<legend>Documents Table</legend>
				<table class="dataTable" id="referenceDocuments" style="table-layout: fixed">
					<thead>
						<tr>
							<th width="3%" class="th-td-norm">#</th>
							<th width="18%" class="th-td-norm">Name</th>
							<th width="18%" class="th-td-norm">Description</th>
							<th width="18%" class="th-td-edge">File</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${wsDto.referenceDocuments}" var="refDoc" varStatus="status">
							<tr> 
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${refDoc.fileName}</td>
								<td class="th-td-norm v-align-top">${refDoc.description}</td>
								<td class="th-td-edge v-align-top" id="file"><a onclick="convBase64ToFile('${refDoc.file}','${refDoc.fileName}')"
									href="#" class="fileLink" id="file">${refDoc.fileName}</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
	</div>
</body>
</html>