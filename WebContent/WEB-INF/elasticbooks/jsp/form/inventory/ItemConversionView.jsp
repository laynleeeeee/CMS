<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Repacking view form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<style type="text/css">
.toItemTd {
	background-color: #F2F1F0;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${repacking.ebObjectId}");
	if ("${repacking.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">Item Conversion</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">IC No. </td>
					<td class="label-value">${repacking.formattedRNumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${repacking.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Item Conversion Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${repacking.company.numberAndName}</td>
				</tr>
				<tr>
					<td class="label">Warehouse </td>
					<td class="label-value">${repacking.warehouse.name}</td>
				</tr>
				<tr>
					<td class="label">Date </td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${repacking.rDate}"/></td>
				</tr>
				<tr>
					<td class="label">Remarks </td>
					<td class="label-value">${repacking.remarks}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Raw Material Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="23%" class="th-td-norm">Stock Code</th>
						<th width="30%" class="th-td-norm">Description</th>
						<th width="15%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="15%" class="th-td-norm">Qty</th>
						<th width="15" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${repacking.repackingRawMaterials}" var="rrm" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${rrm.stockCode}</td>
							<td class="th-td-norm v-align-top">${rrm.item.description}</td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rrm.existingStocks}" /></td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rrm.quantity}"/></td>
							<td class="th-td-norm v-align-top">${rrm.item.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Finished Good Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="23%" class="th-td-norm">Stock Code</th>
						<th width="30%" class="th-td-norm">Description</th>
						<th width="15%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="15%" class="th-td-norm">Qty</th>
						<th width="15" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${repacking.repackingFinishedGoods}" var="rfg" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${rfg.stockCode}</td>
							<td class="th-td-norm v-align-top">${rfg.item.description}</td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rfg.existingStocks}" /></td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rfg.quantity}"/></td>
							<td class="th-td-norm v-align-top">${rfg.item.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</div>
</body>
</html>