<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Delivery Receipt view for nsb.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
var RECEIVED_ID = 7;
var workflowAction = null;
var workflowHtml = null;
var divStatusHtml = null;
$(document).ready(function () {
	formObjectId = parseInt("${deliveryReceipt.ebObjectId}");
	if("${deliveryReceipt.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});

function doFormPreUpdateStatus(statusId, pId) {
	changeStatusId = statusId;
	divStatusHtml = $("#divStatus").html();
	workflowHtml = $("#workflowlog").html();
	workflowAction = $("#workflowlog").attr("action");
	if(statusId == RECEIVED_ID) {
		var url = contextPath + "/deliveryReceipt/receiveDr/" + pId;
		$("#editForm").load(url, function (data) {
			$("#editForm").lightbox_me({
				closeSelector: "#btnClose",
				centered: true,
				onClose: function() {
					$("#editForm").html("");
				}
			});
			//Set background color of the popup
			$("#editForm").css("background-color", "#FFF");
			updatePopupCss();
			//For initial loading of pop-up form.
			$("#btnClose").css("cursor","pointer");
			$("#btnClose").css("float","right");
		});
	} else {
		saveWorkflowLog();
	}
}

function saveStatusLogs() {
	if(isSaving == false) {
		isSaving = true;
		var divForm = $("#form");
		var url = contextPath + "/deliveryReceipt/receiveDr/" + formId;
		doPostWithCallBackObjParam(url, "#deliveryReceiptId", divForm, false, function(data) {
			if(data.startsWith("saved")) {
				var formStatus = new Object();
				formStatus.title = "Delivery Receipt";
				formStatus.message ="Receive log saved successfully.";
				updateTable (formStatus, true);
				dojo.byId("form").innerHTML = "";
				if (typeof $("#divStatus").html() == "undefined") {
					var $divStatus = $("<div id='divStatusDr'></div>");
					$($divStatus).html(divStatusHtml);
					var $workflowLog = $($divStatus).find("form");
					$($workflowLog).find("#formStatusId").val(changeStatusId);
					$($workflowLog).attr("id", "workflowLogDr");
					$($workflowLog).attr("action", workflowAction);
					specialSaveWorkflowLog(workflowAction, $($workflowLog) ,$($divStatus)); 
				}
				sucess = true;
			} else {
				dojo.byId("editForm").innerHTML = data;
				isSaving = false;
			}
			$("#btnSave").removeAttr("disabled");
		});
	}
}
</script>
<title>Delivery Receipt View</title>
</head>
<body>
<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">Delivery Receipt - ${deliveryReceipt.division.name}</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">DR No.</td>
					<td class="label-value">${deliveryReceipt.sequenceNo}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${deliveryReceipt.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Delivery Receipt - Goods Header</legend>
			<table>
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${deliveryReceipt.company.name}</td>
				</tr>
				<tr>
					<td class="label">Division</td>
					<td class="label-value">${deliveryReceipt.division.name}</td>
				</tr>
				<tr>
					<td class="label">SO Reference</td>
					<td class="label-value">${deliveryReceipt.atwNumber}</td>
				</tr>
				<tr>
					<td class="label">PO/PCR No.</td>
					<td class="label-value">${deliveryReceipt.poNumber}</td>
				</tr>
				<tr>
					<td class="label">Customer </td>
					<td class="label-value">${deliveryReceipt.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${deliveryReceipt.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${deliveryReceipt.term.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${deliveryReceipt.date}"/></td>
				</tr>
				<tr>
					<td class="label">Due Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${deliveryReceipt.dueDate}"/></td>
				</tr>
				<tr>
					<td class="label">Ship To</td>
					<td class="label-value">${deliveryReceipt.remarks}</td>
				</tr>
				<tr>
					<td class="label">DR Reference No.</td>
					<td class="label-value">${deliveryReceipt.drRefNumber}</td>
				</tr>
				<tr>
					<td class="label">Sales Representative</td>
					<td class="label-value">${deliveryReceipt.salesPersonnelName}</td>
				</tr>
			</table>
		</fieldset>
		<c:if test="${fn:length(deliveryReceipt.serialDrItems) gt 0}">
			<fieldset class="frmField_set">
				<legend>Serialized Good/s</legend>
					<table class="dataTable" id="serialItems" style="width: 100%;">
						<thead>
							<tr>
								<th width="3%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
								<th width="18%" class="th-td-norm">Stock Code</th>
								<th width="20%" class="th-td-norm">Description</th>
								<th width="15%" class="th-td-norm">Warehouse</th>
								<th width="10%" class="th-td-norm">Existing <br> Stocks</th>
								<th width="15%" class="th-td-norm">Serial Number</th>
								<th width="10%" class="th-td-norm">Qty</th>
								<th width="10%" class="th-td-norm">UOM</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${deliveryReceipt.serialDrItems}" var="drSerialItem" varStatus="status">
								<tr style=" border-bottom: 1px solid black;"> 
									<td class="td-numeric v-align-top" style="border-bottom: 1px solid black; border-left: 1px solid black;">${status.index + 1}</td>
									<td class="th-td-norm v-align-top">${drSerialItem.item.stockCode}</td>
									<td class="th-td-norm v-align-top">${drSerialItem.item.description}</td>
									<td class="th-td-norm v-align-top">${drSerialItem.warehouse.name}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${drSerialItem.existingStocks}" />
									</td>
									<td class="th-td-norm v-align-top">${drSerialItem.serialNumber}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${drSerialItem.quantity}" />
									</td>
									<td class="th-td-norm v-align-top">${drSerialItem.item.unitMeasurement.name}</td>
								</tr>
								<c:set var="totalSiQty" value="${totalSiQty + drSerialItem.quantity}" />
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="5" style="font-weight:bold;">Total</td>
								<td colspan="2" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalSiQty}" /></td>
							</tr>
						</tfoot>
					</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(deliveryReceipt.nonSerialDrItems) gt 0}">
			<fieldset class="frmField_set">
				<legend>Non-Serialized Good/s</legend>
					<table class="dataTable" id="nonSerialItems" style="width: 100%;">
						<thead>
							<tr>
								<th width="3%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
								<th width="20%" class="th-td-norm">Stock Code</th>
								<th width="20%" class="th-td-norm">Description</th>
								<th width="20%" class="th-td-norm">Warehouse</th>
								<th width="10%" class="th-td-norm">Existing <br> Stocks</th>
								<th width="12%" class="th-td-norm">Qty</th>
								<th width="15%" class="th-td-norm">UOM</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${deliveryReceipt.nonSerialDrItems}" var="drNonSerialItem" varStatus="status">
								<tr style=" border-bottom: 1px solid black;"> 
									<td class="td-numeric v-align-top" style="border-bottom: 1px solid black; border-left: 1px solid black;">${status.index + 1}</td>
									<td class="th-td-norm v-align-top">${drNonSerialItem.item.stockCode}</td>
									<td class="th-td-norm v-align-top">${drNonSerialItem.item.description}</td>
									<td class="th-td-norm v-align-top">${drNonSerialItem.warehouse.name}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${drNonSerialItem.existingStocks}" />
									</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${drNonSerialItem.quantity}" />
									</td>
									<td class="th-td-norm v-align-top">${drNonSerialItem.item.unitMeasurement.name}</td>
								</tr>
								<c:set var="totalDrItemQty" value="${totalDrItemQty + drNonSerialItem.quantity}" />
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="4" style="font-weight:bold;">Total</td>
								<td colspan="2" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalDrItemQty}" /></td>
							</tr>
						</tfoot>
					</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(deliveryReceipt.drLines) gt 0}">
			<fieldset class="frmField_set">
			<legend>Service Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
						<th width="35%" class="th-td-norm">Service</th>
						<th width="40%" class="th-td-norm">Description</th>
						<th width="5%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${deliveryReceipt.drLines}" var="line" varStatus="status">
						<tr style="border: 1px solid black;">
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${line.serviceSetting.name}</td>
							<td class="th-td-norm v-align-top">${line.description}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="9" value="${line.quantity}" /></td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${line.unitMeasurement.name}</td>
						</tr>
						<c:set var="totalLineQty" value="${totalLineQty + line.quantity}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2" style="font-weight:bold;">Total</td>
						<td colspan="2" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="9" value="${totalLineQty}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty deliveryReceipt.referenceDocuments}">
			<fieldset class="frmField_set">
				<legend>Documents</legend>
				<table class="dataTable" id="referenceDocuments">
					<thead>
						<tr>
							<th width="3%" class="th-td-norm">#</th>
							<th width="18%" class="th-td-norm">Name</th>
							<th width="18%" class="th-td-norm">Description</th>
							<th width="18%" class="th-td-edge">File</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${deliveryReceipt.referenceDocuments}" var="doc" varStatus="status">
							<tr> 
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${doc.fileName}</td>
								<td class="th-td-norm v-align-top">${doc.description}</td>
								<td class="th-td-edge v-align-top" id="file">
									<a href="${doc.file}" download="${doc.fileName}" class="fileLink" id="file">${doc.fileName}</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
		<c:set var="grandTotal" value="${totalSerialAmount + totalNonSerialAmount + otherCharges}"/>
		<table class="frmField_set">
		</table>
	</div>
</body>
</html>