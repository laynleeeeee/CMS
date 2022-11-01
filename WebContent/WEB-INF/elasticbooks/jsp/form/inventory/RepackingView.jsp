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
		<div class="modFormLabel">Repacking</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">RP No. </td>
					<td class="label-value">${repacking.formattedRNumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${repacking.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Repacking Header</legend>
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
			<legend>Repacking Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">From<br>Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="7%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="10%" class="th-td-norm">Qty</th>
						<th width="7" class="th-td-norm">UOM</th>
						<th width="10%" class="th-td-norm">To Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="7%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="10%" class="th-td-norm">Repacked<br>Qty</th>
						<th width="7%" class="th-td-edge">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${repacking.rItems}" var="rpi" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- From Stock Code -->
							<td class="th-td-norm v-align-top">${rpi.fromItem.stockCode}</td>
							<!-- From Description -->
							<td class="th-td-norm v-align-top">${rpi.fromItem.description}</td>
							<!-- Existing Stocks -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rpi.fromItem.existingStocks}" /></td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rpi.quantity}" /></td>
							<!-- Unit of Measure -->
							<td class="th-td-norm v-align-top">${rpi.fromItem.unitMeasurement.name}</td>
							<!-- To Stock Code -->
							<td class="th-td-norm v-align-top toItemTd">${rpi.toItem.stockCode}</td>
							<!-- To Description -->
							<td class="th-td-norm v-align-top toItemTd">${rpi.toItem.description}</td>
							<!-- Existing Stocks -->
							<td class="td-numeric v-align-top toItemTd"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rpi.toItem.existingStocks}" /></td>
							<!-- Repacked Qty -->
							<td class="th-td-norm v-align-top toItemTd" align="right"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rpi.repackedQuantity}" /></td>
							<!-- Unit of Measure -->
							<td class="th-td-edge v-align-top toItemTd">${rpi.toItem.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</div>
</body>
</html>