<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description	: AR Invoice View for NSB.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<style>
.footerViewCls {
	font-size: 12;
	font-weight: bold;
	text-align: right;
	width: 22%;
	padding: 4px;
}
a.btnPrintCheck{
	color: blue;
}

a.btnPrintCheck:hover {
	color: blue;
	cursor: pointer;
}
</style>
<script type="text/javascript">
var RECEIVED_ID = 7;
var workflowAction = null;
var workflowHtml = null;
var divStatusHtml = null;
$(document).ready(function () {
	formObjectId = parseInt("${arInvoice.ebObjectId}");
	if("${arInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});

function printForm (isSalesInvoice, infoType, hasDate) {
	var uri = contextPath + "/arInvoice/${arInvoice.arInvoiceTypeId}/pdf?pId=${arInvoice.id}"
			+"&isSalesInvoice="+isSalesInvoice+"&infoType="+infoType+"&hasDate="+hasDate;
	window.open(uri);
}

function doFormPreUpdateStatus(statusId, pId) {
	changeStatusId = statusId;
	divStatusHtml = $("#divStatus").html();
	workflowHtml = $("#workflowlog").html();
	workflowAction = $("#workflowlog").attr("action");
	if(statusId == RECEIVED_ID) {
		var url = contextPath + "/arInvoice/receiveAri/" + pId;
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
		var url = contextPath + "/arInvoice/receiveAri/" + formId;
		doPostWithCallBackObjParam(url, "#arInvoiceId", divForm, false, function(data) {
			if(data.startsWith("saved")) {
				var formStatus = new Object();
				formStatus.title = "AR Invoice";
				formStatus.message ="Receive log saved successfully.";
				updateTable (formStatus, true);
				dojo.byId("form").innerHTML = "";
				if (typeof $("#divStatus").html() == "undefined") {
					var $divStatus = $("<div id='divStatusAri'></div>");
					$($divStatus).html(divStatusHtml);
					var $workflowLog = $($divStatus).find("form");
					$($workflowLog).find("#formStatusId").val(changeStatusId);
					$($workflowLog).attr("id", "workflowLogAri");
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
<title>AR Invoice View</title>
</head>
<body>
<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">AR Invoice - ${arInvoice.division.name}</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">ARI No.</td>
					<td class="label-value">${arInvoice.sequenceNo}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${arInvoice.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>AR Invoice - Goods Header</legend>
			<table>
				<tr>
					<td class="label">DR-I Reference</td>
					<td class="label-value">${arInvoice.drNumber}</td>
				</tr>
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${companyName}</td>
				</tr>
				<tr>
					<td class="label">Customer </td>
					<td class="label-value">${arInvoice.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${arInvoice.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${arInvoice.term.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${arInvoice.date}"/></td>
				</tr>
				<tr>
					<td class="label">Due Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${arInvoice.dueDate}"/></td>
				</tr>
				<tr>
					<td class="label">Ship To</td>
					<td class="label-value">${arInvoice.remarks}</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(true, 2, true);">Print Sales Invoice with Item Description, UOM, Quantity, Amount</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(false, 1, true);">Print Billing Statement with Item Description and Amount</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(false, 2, true);">Print Billing Statement with Item Description, UOM, Quantity, Amount</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(false, 3, true);">Print Billing Statement with Item Description, UOM, Quantity, Amount, Percentage</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(false, 4, true);">Print Billing Statement with Item Description, Percentage and Amount</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(true, 1, false);">Print Sales Invoice with Item Description and Amount (No date and term)</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(true, 2, false);">Print Sales Invoice with Item Description, UOM, Quantity, Amount (No date and term)</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(false, 1, false);">Print Billing Statement with Item Description and Amount (No date and term)</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(false, 2, false);">Print Billing Statement with Item Description, UOM, Quantity, Amount (No date and term)</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(false, 3, false);">Print Billing Statement with Item Description, UOM, Quantity, Amount, Percentage (No date and term)</a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td style="font-weight: bold;" class="label-value">
						<a class="btnPrintCheck" onclick="printForm(false, 4, false);">Print Billing Statement with Item Description, Percentage and Amount (No Date and term)</a>
					</td>
				</tr>
			</table>
		</fieldset>
		<c:if test="${fn:length(arInvoice.serialArItems) gt 0}">
			<fieldset class="frmField_set">
				<legend>Serialized Good/s</legend>
					<table class="dataTable" id="serialItems" style="width: 100%;">
						<thead>
							<tr>
								<th width="3%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
								<th width="16%" class="th-td-norm">Stock Code</th>
								<th width="16%" class="th-td-norm">Description</th>
								<th width="9%" class="th-td-norm">Warehouse</th>
								<th width="6%" class="th-td-norm">Serial Number</th>
								<th width="6%" class="th-td-norm">Available <br> Stocks</th>
								<th width="6%" class="th-td-norm">Qty</th>
								<th width="6%" class="th-td-norm">UOM</th>
								<th width="6%" class="th-td-norm">Gross Price</th>
								<th width="6%" class="th-td-norm">Discount</th>
								<th width="8%" class="th-td-norm">Tax Type</th>
								<th width="6%" class="th-td-norm">VAT Amount</th>
								<th width="6%" class="th-td-norm">Amount</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${arInvoice.serialArItems}" var="ariSerialItem" varStatus="status">
								<tr style=" border-bottom: 1px solid black;"> 
									<td class="td-numeric v-align-top" style="border-bottom: 1px solid black; border-left: 1px solid black;">${status.index + 1}</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.item.stockCode}</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.item.description}</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.warehouse.name}</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.serialNumber}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${ariSerialItem.existingStocks}" />
									</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${ariSerialItem.quantity}" />
									</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.item.unitMeasurement.name}</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="6" value="${ariSerialItem.srp}"/>
									</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariSerialItem.discount}"/>
									</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.taxType.name}</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariSerialItem.vatAmount}"/>
									</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariSerialItem.amount}"/>
									</td>
								</tr>
								<c:set var="totalSerialQty" value="${totalSerialQty + ariSerialItem.quantity}"/>
								<c:set var="totalSerialAmount" value="${totalSerialAmount + ariSerialItem.amount}"/>
								<c:set var="totalSerialVat" value="${totalSerialVat + ariSerialItem.vatAmount}"/>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="6" align="left" style="font-weight:bold;">Total</td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${totalSerialQty}" />
								</td>
								<td colspan="5"></td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${totalSerialAmount}"/>
								</td>
							</tr>
						</tfoot>
					</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(arInvoice.nonSerialArItems) gt 0}">
			<fieldset class="frmField_set">
				<legend>Non-Serialized Good/s</legend>
					<table class="dataTable" id="nonSerialItems" style="width: 100%;">
						<thead>
							<tr>
								<th width="3%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
								<th width="17%" class="th-td-norm">Stock Code</th>
								<th width="20%" class="th-td-norm">Description</th>
								<th width="10%" class="th-td-norm">Warehouse</th>
								<th width="7%" class="th-td-norm">Existing <br> Stocks</th>
								<th width="7%" class="th-td-norm">Qty</th>
								<th width="7%" class="th-td-norm">UOM</th>
								<th width="7%" class="th-td-norm">Gross Price</th>
								<th width="7%" class="th-td-norm">Discount</th>
								<th width="7%" class="th-td-norm">Tax Type</th>
								<th width="8%" class="th-td-norm">VAT Amount</th>
								<th width="8%" class="th-td-norm">Amount</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${arInvoice.nonSerialArItems}" var="ariNonSerialItem" varStatus="status">
								<tr style=" border-bottom: 1px solid black;"> 
									<td class="td-numeric v-align-top" style="border-bottom: 1px solid black; border-left: 1px solid black;">${status.index + 1}</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.item.stockCode}</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.item.description}</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.warehouse.name}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${ariNonSerialItem.existingStocks}" />
									</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${ariNonSerialItem.quantity}" />
									</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.item.unitMeasurement.name}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="6" value="${ariNonSerialItem.srp}"/>
									</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariNonSerialItem.discount}"/>
									</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.taxType.name}</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariNonSerialItem.vatAmount}"/>
									</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariNonSerialItem.amount}"/>
									</td>
								</tr>
								<c:set var="totalNonSerialQty" value="${totalNonSerialQty + ariNonSerialItem.quantity}"/>
								<c:set var="totalNonSerialGross" value="${totalNonSerialGross + ariNonSerialItem.srp}"/>
								<c:set var="totalNonSerialAmount" value="${totalNonSerialAmount + ariNonSerialItem.amount}"/>
								<c:set var="totalNonSerialVat" value="${totalNonSerialVat + ariNonSerialItem.vatAmount}"/>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="5" align="left" style="font-weight:bold;">Total</td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${totalNonSerialQty}" />
								</td>
								<td></td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="6" value="${totalNonSerialGross}" />
								</td>
								<td colspan=3"></td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${totalNonSerialAmount}"/>
								</td>
							</tr>
						</tfoot>
					</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(arInvoice.ariLines) gt 0}">
			<fieldset class="frmField_set">
			<legend>Service/s</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
						<th width="33%" class="th-td-norm">Service</th>
						<th width="5%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<th width="10%" class="th-td-norm">Gross Price</th>
						<th width="10%" class="th-td-norm">Discount</th>
						<th width="10%" class="th-td-norm">Tax Type</th>
						<th width="10%" class="th-td-norm">VAT Amount</th>
						<th width="10%" class="th-td-edge" style="border-right: 1px solid black;">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${arInvoice.ariLines}" var="line" varStatus="status">
						<tr style="border: 1px solid black;">
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${line.serviceSettingName}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="9" value="${line.quantity}" /></td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${line.unitMeasurement.name}</td>
							<!-- UP Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="6" value="${line.upAmount}" /></td>
							<!-- Discount Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.discount}" /></td>
							<!-- Tax Type -->
							<td class="th-td-norm v-align-top">${line.taxType.name}</td>
							<!-- VAT Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${line.vatAmount}" /></td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${line.amount}" /></td>
						</tr>
						<c:set var="totalOtherCharges" value="${totalOtherCharges + line.amount}" />
						<c:set var="totalLineVat" value="${totalLineVat + line.vatAmount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2" style="font-weight:bold;">Total</td>
						<td colspan="7" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalOtherCharges}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty arInvoice.referenceDocuments}">
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
						<c:forEach items="${arInvoice.referenceDocuments}" var="doc" varStatus="status">
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
		<br>
		<c:set var="totalVat" value="${totalSerialVat + totalNonSerialVat + totalLineVat}"/>
		<table class="frmField_set footerTotalAmountTbl">
			<tr>
				<td class="footerViewCls"></td>
				<td class="footerViewCls"></td>
				<td class="footerViewCls"></td>
				<td class="footerViewCls"></td>
				<td class="footerViewCls"></td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total VAT</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" 
						maxFractionDigits="2" value="${totalVat}" />
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Sub Total</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${totalSerialAmount + totalNonSerialAmount + totalOtherCharges}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Withholding VAT</td>
				<td class="footerViewCls" colspan="2">
					<span>(
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${arInvoice.wtVatAmount}"/>
					)</span>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Withholding Tax</td>
				<td class="footerViewCls" >${arInvoice.wtAcctSetting.name}</td>
				<td class="footerViewCls"  align="right">
					<span>(
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${arInvoice.wtAmount}" />
					)</span>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Recoupment (
				<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${capBalance}"/>)</td>
				<td class="footerViewCls" colspan="2">
					<span>(
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${arInvoice.recoupment}"/>
					)</span>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Retention</td>
				<td class="footerViewCls" colspan="2">
					<span>(
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${arInvoice.retention}"/>
					)</span>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total Amount Due</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" 
						maxFractionDigits="2" value="${arInvoice.amount}" />
				</td>
			</tr>
		</table>
		<br>
	</div>
</body>
</html>