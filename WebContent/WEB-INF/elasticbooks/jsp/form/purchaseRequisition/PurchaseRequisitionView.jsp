<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp"%>

    <!-- Description: Requisition Form view form -->
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
	<div class="modFormLabel">${requisitionFormDto.requisitionTypeName}</div>
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
		<legend>Purchase Requisition Header</legend>
		<table class="formTable">
			<tr>
				<td class="labels">Company</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.company.name}</td>
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
				<c:set value="${requisitionFormDto.requisitionForm.requisitionTypeId}" var="typeId"></c:set>
					<c:choose>
						<c:when test="${typeId eq 1}">
							<c:set var="rfTypeLabel" value="Tire" />
						</c:when>
						<c:when test="${typeId eq 2}">
							<c:set var="rfTypeLabel" value="Fuel" />
						</c:when>
						<c:when test="${typeId eq 3}">
							<c:set var="rfTypeLabel" value="PMS" />
						</c:when>
						<c:when test="${typeId eq 4}">
							<c:set var="rfTypeLabel" value="Electrical" />
						</c:when>
						<c:when test="${typeId eq 5}">
							<c:set var="rfTypeLabel" value="CM" />
						</c:when>
						<c:when test="${typeId eq 6}">
							<c:set var="rfTypeLabel" value="Admin" />
						</c:when>
						<c:when test="${typeId eq 7}">
							<c:set var="rfTypeLabel" value="Motorpool" />
						</c:when>
						<c:when test="${typeId eq 8}">
							<c:set var="rfTypeLabel" value="Oil" />
						</c:when>
						<c:when test="${typeId eq 9}">
							<c:set var="rfTypeLabel" value="SUBCON" />
						</c:when>
						<c:when test="${typeId eq 10}">
							<c:set var="rfTypeLabel" value="Pakyawan" />
						</c:when>
					</c:choose>
				<td class="labels">Requisition Form Reference</td>
				<td class="label-value">${rfTypeLabel}
				${requisitionFormDto.requisitionForm.reqFormRef.sequenceNumber}</td>
			</tr>
			<tr>
				<td class="labels">Fleet</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.reqFormRef.fleetProfile.codeVesselName}</td>
			</tr>
			<tr style="display: none;">
				<td class="labels">Project</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.reqFormRef.arCustomer.name}</td>
			</tr>
			<tr>
				<td class="labels">Remarks</td>
				<td class="label-value">${requisitionFormDto.requisitionForm.remarks}</td>
			</tr>
		</table>
	</fieldset>
	<c:if test="${fn:length(requisitionFormDto.requisitionForm.purchaseRequisitionItems) gt 0}">
		<fieldset class="frmField_set">
			<legend>Purchase Requisition Items</legend>
			<table class="dataTable" id="refItemTbl" style="table-layout: fixed">
				<thead>
					<tr>
						<th width="3%" class="th-td-norm">#</th>
						<th width="17%" class="th-td-norm">Stock Code</th>
						<th width="30%" class="th-td-norm">Description</th>
						<th width="15%" class="th-td-norm">Existing Stocks</th>
						<th width="15%" class="th-td-norm">QTY</th>
						<th width="20%" class="th-td-edge">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${requisitionFormDto.requisitionForm.purchaseRequisitionItems}" var="pri" varStatus="status">
						<tr> 
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${pri.item.stockCode}</td>
							<td class="th-td-norm v-align-top">${pri.item.description}</td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${pri.existingStocks}"/></td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${pri.quantity}"/></td>
							<td class="th-td-norm v-align-top">${pri.item.unitMeasurement.name}</td>
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