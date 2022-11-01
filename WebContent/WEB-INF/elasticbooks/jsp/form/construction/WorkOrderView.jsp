<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Work order form view jsp page
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${workOrder.ebObjectId}");
	if ("${workOrder.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});

function editSubWorkOrder(editUri) {
	showEditForm(contextPath + editUri);
}
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

a.editWorkOrder {
	font-size: small;
	font-weight: bold;
	text-decoration: none;
}

.td-txt-align-top {
	vertical-align: top;
	padding-top: 6px;
}
</style>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">Work Order</div>
	<br>
	<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="label">Sequence Number</td>
					<td class="label-value">${workOrder.sequenceNumber}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${workOrder.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Work Order Header</legend>
			<table class="formTable" border="0">
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${workOrder.company.name}</td>
				</tr>
				<c:if test="${workOrder.refWorkOrderId gt 0}">
					<tr>
						<td class="label">Work Order Reference</td>
						<td class="label-value">${workOrder.refWoNumber}</td>
					</tr>
				</c:if>
				<tr>
					<td class="label">Sales Order Reference</td>
					<td class="label-value">${workOrder.soNumber}</td>
				</tr>
				<tr>
					<td class="label">Customer</td>
					<td class="label-value">${workOrder.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${workOrder.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${workOrder.date}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Targer End Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${workOrder.targetEndDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Work Description</td>
					<td class="label-value">${workOrder.workDescription}</td>
				</tr>
				<c:if test="${workOrder.refWorkOrderId eq null}">
					<tr>
						<td class="label td-txt-align-top">Sub Work Orders</td>
						<td class="label-value">
							<table>
								<tbody>
									<c:forEach items="${workOrder.subWorkOrderDtos}" var="wo" varStatus="status">
										<tr><td><a href='#' class='editWorkOrder' onClick='editSubWorkOrder("${wo.editUri}");'>${wo.woNumber}</a></td></tr>
									</c:forEach>
								</tbody>
							</table>
						</td>
					</tr>
				</c:if>
			</table>
		</fieldset>
		<c:if test="${not empty workOrder.woPurchasedItems}">
			<fieldset class="frmField_set">
				<legend>Purchased Items Table</legend>
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
						<c:forEach items="${workOrder.woPurchasedItems}" var="woPurchasedItems" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${woPurchasedItems.item.stockCode}</td>
								<td class="th-td-norm v-align-top">${woPurchasedItems.item.description}</td>
								<td class="th-td-norm v-align-top">${woPurchasedItems.warehouse.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${woPurchasedItems.existingStocks}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${woPurchasedItems.quantity}"/>
								</td>
								<td class="th-td-edge v-align-top">${woPurchasedItems.item.unitMeasurement.name}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(workOrder.woLines) gt 0}">
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
					<c:forEach items="${workOrder.woLines}" var="line" varStatus="status">
						<tr style="border: 1px solid black;">
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${line.arLineSetup.name}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.quantity}" /></td>
							<td class="th-td-norm v-align-top">${line.unitMeasurement.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty workOrder.woInstructions}">
			<fieldset class="frmField_set">
				<legend>Work Instruction</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="5%" class="th-td-norm">#</th>
							<th width="95%" class="th-td-edge">Instruction</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${workOrder.woInstructions}" var="woi" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-edge v-align-top">${woi.workDescription}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty workOrder.woItems}">
			<fieldset class="frmField_set">
				<legend>Materials Needed</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="5%" class="th-td-norm">#</th>
							<th width="25%" class="th-td-norm">Stock Code</th>
							<th width="25%" class="th-td-norm">Description</th>
							<th width="15%" class="th-td-norm">Existing Stocks</th>
							<th width="15%" class="th-td-norm">Qty</th>
							<th width="15%" class="th-td-edge">UOM</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${workOrder.woItems}" var="woi" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${woi.item.stockCode}</td>
								<td class="th-td-norm v-align-top">${woi.item.description}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${woi.existingStocks}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${woi.quantity}"/>
								</td>
								<td class="th-td-edge v-align-top">${woi.item.unitMeasurement.name}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty workOrder.referenceDocuments}">
			<fieldset class="frmField_set">
				<legend>Documents</legend>
				<table class="dataTable" id="referenceDocuments">
					<thead>
						<tr>
							<th width="5%" class="th-td-norm">#</th>
							<th width="30%" class="th-td-norm">Name</th>
							<th width="30%" class="th-td-norm">Description</th>
							<th width="30%" class="th-td-edge">file</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${workOrder.referenceDocuments}" var="refDoc" varStatus="status">
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
</div>
</body>
</html>