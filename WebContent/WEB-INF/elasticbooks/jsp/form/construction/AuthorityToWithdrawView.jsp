<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Authority to withdraw form view jsp page
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${authorityToWithdraw.ebObjectId}");
	if ("${authorityToWithdraw.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
<style type="text/css">
.monetary {
	text-align: right;
}

.footerViewCls {
	font-size: 12;
	font-weight: bold;
	text-align: right;
	width: 22%;
}

.td-txt-align-top {
	vertical-align: top;
	padding-top: 8px;
}
</style>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">Authority To Withdraw</div>
	<br>
	<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="label">Sequence Number</td>
					<td class="label-value">${authorityToWithdraw.sequenceNumber}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${authorityToWithdraw.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Authority To Withdraw Header</legend>
			<table class="formTable" border="0">
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${authorityToWithdraw.company.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${authorityToWithdraw.date}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Sales Order Reference</td>
					<td class="label-value">${authorityToWithdraw.soNumber}</td>
				</tr>
				<tr>
					<td class="label">Customer</td>
					<td class="label-value">${authorityToWithdraw.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${authorityToWithdraw.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Ship To</td>
					<td class="label-value">${authorityToWithdraw.shipTo}</td>
				</tr>
				<tr>
					<td class="label">Plate Number</td>
					<td class="label-value">${authorityToWithdraw.fleetProfile.plateNo}</td>
				</tr>
				<tr>
					<td class="label">Driver</td>
					<td class="label-value">${authorityToWithdraw.driverName}</td>
				</tr>
				<tr>
					<td class="label">Remarks</td>
					<td class="label-value">${authorityToWithdraw.remarks}</td>
				</tr>
			</table>
		</fieldset>
		<c:if test="${not empty authorityToWithdraw.serialItems}">
			<fieldset class="frmField_set">
				<legend>Serialized Items Table</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="5%" class="th-td-norm">#</th>
							<th width="20%" class="th-td-norm">Stock Code</th>
							<th width="20%" class="th-td-norm">Description</th>
							<th width="12%" class="th-td-norm">Warehouse</th>
							<th width="10%" class="th-td-norm">Existing Stocks</th>
							<th width="15%" class="th-td-norm">Serial Number</th>
							<th width="10%" class="th-td-norm">Qty</th>
							<th width="8%" class="th-td-edge">UOM</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${authorityToWithdraw.serialItems}" var="si" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${si.item.stockCode}</td>
								<td class="th-td-norm v-align-top">${si.item.description}</td>
								<td class="th-td-norm v-align-top">${si.warehouse.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${si.existingStocks}"/>
								</td>
								<td class="th-td-norm v-align-top">${si.serialNumber}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${si.quantity}"/>
								</td>
								<td class="th-td-edge v-align-top ">${si.item.unitMeasurement.name}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty authorityToWithdraw.atwItems}">
			<fieldset class="frmField_set">
				<legend>Non-Serialized Items Table</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="5%" class="th-td-norm">#</th>
							<th width="20%" class="th-td-norm">Stock Code</th>
							<th width="20%" class="th-td-norm">Description</th>
							<th width="16%" class="th-td-norm">Warehouse</th>
							<th width="13%" class="th-td-norm">Existing Stocks</th>
							<th width="13%" class="th-td-norm">Qty</th>
							<th width="13%" class="th-td-edge">UOM</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${authorityToWithdraw.atwItems}" var="atwi" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${atwi.item.stockCode}</td>
								<td class="th-td-norm v-align-top">${atwi.item.description}</td>
								<td class="th-td-norm v-align-top">${atwi.warehouse.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${atwi.existingStocks}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${atwi.quantity}"/>
								</td>
								<td class="th-td-edge v-align-top">${atwi.item.unitMeasurement.name}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(authorityToWithdraw.atwLines) gt 0}">
			<fieldset class="frmField_set">
			<legend>Service Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
						<th width="80%" class="th-td-norm">Service</th>
						<th width="5%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${authorityToWithdraw.atwLines}" var="line" varStatus="status">
						<tr style="border: 1px solid black;">
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${line.arLineSetup.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.quantity}" /></td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${line.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty authorityToWithdraw.atwItems}">
			<c:if test="${not empty authorityToWithdraw.referenceDocuments}">
				<fieldset class="frmField_set">
					<legend>Documents</legend>
					<table class="dataTable" id="referenceDocuments">
						<thead>
							<tr>
								<th width="3%" class="th-td-norm">#</th>
								<th width="18%" class="th-td-norm">Name</th>
								<th width="18%" class="th-td-norm">Description</th>
								<th width="18%" class="th-td-edge">file</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${authorityToWithdraw.referenceDocuments}" var="refDoc" varStatus="status">
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
		</c:if>
	</div>
</div>
</body>
</html>