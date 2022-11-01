<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp"%>

    <!-- Description: Material Requisition view form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${requisitionFormDto.requisitionForm.ebObjectId}");

	if ("${requisitionFormDto.requisitionForm.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
<div class="formDivBigForms">
	<div class="modFormLabel">${requisitionTypeName}</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Sequence Number</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.sequenceNumber}</td>
			<tr>
				<td class="labels">Status</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Material Requisition Header</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Company</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.company.name}</td>
			</tr>
			<tr>
				<td class="labels">WO Reference</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.woNumber}</td>
			</tr>
			<tr>
				<td class="labels">Warehouse</td>
				<td class="label-value">
					<c:choose>
						<c:when test="${requisitionFormDto.requisitionForm.warehouseId ne null}">
							${requisitionFormDto.warehouseName}
						</c:when>
						<c:otherwise>ALL</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<td class="labels">Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${requisitionFormDto.requisitionForm.date}"/></td>
			</tr>
			<tr>
				<td class="labels">Fleet</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.fleetProfile.codeVesselName}</td>
			</tr>
			<tr>
				<td class="labels">Project</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.arCustomer.name}</td>
			</tr>
			<c:if test="${requisitionFormDto.requisitionForm.requisitionTypeId eq 2}">
				<tr>
					<td class="labels">Distance</td>
					<td class="label-value">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${requisitionFormDto.requisitionForm.distance}"/>
					</td>
				</tr>

				<tr>
					<td class="labels">Ratio</td>
					<td class="label-value">
						 ${requisitionFormDto.requisitionForm.ratio.name}
					</td>
				</tr>

				<tr>
					<td class="labels">Liters</td>
					<td class="value" id="tdLiters">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${requisitionFormDto.requisitionForm.liters}"/>
					</td>
				</tr>
			</c:if>
			<tr>
				<td class="labels">Requested By</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.requestedBy}</td>
			</tr>
			<tr>
				<td class="labels">Requested Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${requisitionFormDto.requisitionForm.requestedDate}"/></td>
			</tr>
			<tr>
				<td class="labels">Remarks</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.remarks}</td>
			</tr>
		</table>
	</fieldset>
	<c:if test="${fn:length(requisitionFormDto.requisitionForm.requisitionFormItems) gt 0}">
		<fieldset class="frmField_set">
			<legend>Material Requisition Items</legend>
			<table class="dataTable" id="refItemTbl" style="table-layout: fixed">
				<thead>
					<tr>
						<th width="3%" class="th-td-edge">#</th>
						<th width="17%" class="th-td-norm">Stock Code</th>
						<th width="30%" class="th-td-norm">Description</th>
						<th width="15%" class="th-td-norm">Existing Stocks</th>
						<th width="15%" class="th-td-norm">QTY</th>
						<th width="20%" class="th-td-edge">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${requisitionFormDto.requisitionForm.requisitionFormItems}" var="requisitionFormItem" varStatus="status">
						<tr> 
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${requisitionFormItem.item.stockCode}</td>
							<td class="th-td-norm v-align-top">${requisitionFormItem.item.description}</td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${requisitionFormItem.existingStocks}"/></td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${requisitionFormItem.quantity}"/></td>
							<td class="th-td-edge v-align-top">${requisitionFormItem.item.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${fn:length(requisitionFormDto.requisitionForm.referenceDocuments) gt 0}">
		<fieldset class="frmField_set">
			<legend>Documents Table</legend>
			<table class="dataTable" id="referenceDocuments" style="table-layout: fixed">
				<thead>
					<tr>
						<th width="3%" class="th-td-norm">#</th>
						<th width="32%" class="th-td-norm">Name</th>
						<th width="40%" class="th-td-norm">Description</th>
						<th width="25%" class="th-td-edge">File</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${requisitionFormDto.requisitionForm.referenceDocuments}" var="refDoc" varStatus="status">
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