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
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable2.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview2.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
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
	disableRefTblLink("${hasEditAccess}", "referenceDocuments", "file");
});
</script>
</head>
<body>
	<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">Reclass - ${repacking.division.name}</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">RC No. </td>
					<td class="label-value">${repacking.rNumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${repacking.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Reclass Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${repacking.company.numberAndName}</td>
				</tr>
				<tr>
					<td class="label">Division</td>
					<td class="label-value">${repacking.division.name}</td>
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
			<legend>Reclass Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">From<br>Stock Code</th>
						<th width="13%" class="th-td-norm">Description</th>
						<th width="5%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="5%" class="th-td-norm">Qty</th>
						<th width="5" class="th-td-norm">UOM</th>
						<th width="5" class="th-td-norm">Unit Cost</th>
						<th width="5" class="th-td-norm">Total</th>
						<th width="10%" class="th-td-norm">To Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="5%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="5%" class="th-td-norm">Reclassed<br>Qty</th>
						<th width="5%" class="th-td-norm">UOM</th>
						<th width="5" class="th-td-norm">Unit Cost</th>
						<th width="5" class="th-td-norm">Total</th>
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
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value="${rpi.quantity}" /></td>
							<!-- Unit of Measure -->
							<td class="th-td-norm v-align-top">${rpi.fromItem.unitMeasurement.name}</td>
							<!-- Unit Cost -->
							<td class="th-td-norm v-align-top" align="right">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value="${rpi.unitCost}"/>
							</td>
							<!-- Total -->
							<td class="th-td-norm v-align-top" align="right">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value="${rpi.fromTotal}"/>
							</td>
							<!-- To Stock Code -->
							<td class="th-td-norm v-align-top toItemTd">${rpi.toItem.stockCode}</td>
							<!-- To Description -->
							<td class="th-td-norm v-align-top toItemTd">${rpi.toItem.description}</td>
							<!-- Existing Stocks -->
							<td class="td-numeric v-align-top toItemTd"><fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rpi.toItem.existingStocks}" /></td>
							<!-- Repacked Qty -->
							<td class="th-td-norm v-align-top toItemTd" align="right">
								<fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rpi.repackedQuantity}"/>
							</td>
							<!-- Unit of Measure -->
							<td class="th-td-norm v-align-top toItemTd">${rpi.toItem.unitMeasurement.name}</td>
							<!-- Unit Cost -->
							<td class="td-numeric v-align-top toItemTd" align="right">
								<fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rpi.repackedUnitCost}"/>
							</td>
							<!-- Total -->
							<td class="th-td-norm v-align-top toItemTd" align="right">
								<fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value="${rpi.toTotal}"/>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
		<c:if test="${not empty repacking.referenceDocuments}">
			<fieldset class="frmField_set">
				<legend>Documents</legend>
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
						<c:forEach items="${repacking.referenceDocuments}" var="refDoc" varStatus="status">
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
		</c:if>
	</div>
</body>
</html>