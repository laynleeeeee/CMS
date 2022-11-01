
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: AP payment form for viewing only.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<style type="text/css">
a#btnPrintCheck{
	color: blue;
}

a#btnPrintCheck:hover {
	color: blue;
	cursor: pointer;
}
</style>
<script type="text/javascript">
var checkbookTemplateId = null;
var CLEARED_ID = 16;
var workflowAction = null;
var workflowHtml = null;
var divStatusHtml = null;
var CLEARED_STATUS_ID = 16;
var STALED_STATUS_ID = 32;
$(document).ready(function () {
	formObjectId = parseInt("${apPayment.ebObjectId}");
	if("${apPayment.specifyPayee}" == "true"){
		specifyPayee.setAttribute("checked", "checked");
	}
	 checkbookTemplateId = "${apPayment.checkbook.checkbookTemplateId}";
	if (checkbookTemplateId == null || checkbookTemplateId == 0) {
		$("#btnPrintCheck").hide();
	}

	var currentStatusId = "${apPayment.formWorkflow.currentStatusId}";
	if (currentStatusId == 4 || currentStatusId == 32) {
		$("#btnPrintCheck").hide();
		if (currentStatusId == 32) {
			$("#btnPrint").hide();
		}
	}
	//Display edit icon when status is cleared.
	//Hide edit icon when status is staled
	if(currentStatusId == CLEARED_STATUS_ID) {
		$("#imgEdit").show();
	} else if(currentStatusId == STALED_STATUS_ID) {
		$("#imgEdit").hide();
	}
});

function printCheck () {
	var supplierName = encodeURIComponent(specifyPayee.checked ? "${apPayment.payee}" :
		"${apPayment.supplier.name}");
	window.open(contextPath + "/checkPrinting/new?checkbookTemplateId="
			+checkbookTemplateId+"&name="+supplierName+"&amount="+"${apPayment.amount}"
			+"&currencyId="+"${apPayment.currencyId}"+"&checkDate="+$("#tdCheckDate").text());
}

function doFormPreUpdateStatus(statusId, pId) {
	changeStatusId = statusId;
	divStatusHtml = $("#divStatus").html();
	workflowHtml = $("#workflowlog").html();
	workflowAction = $("#workflowlog").attr("action");
	if(statusId == CLEARED_ID) {
		var url = contextPath + "/apPayment/clearPayment/" + pId;
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
		var url = contextPath + "/apPayment/clearPayment/" + formId;
		doPostWithCallBackObjParam(url, "#apPaymentId", divForm, false, function(data) {
			if(data.startsWith("saved")) {
				var formStatus = new Object();
				formStatus.title = "AP Payment";
				formStatus.message ="Clear log saved successfully.";
				updateTable (formStatus, true);
				dojo.byId("form").innerHTML = "";
				if (typeof $("#divStatus").html() == "undefined") {
					var $divStatus = $("<div id='divStatusPayment'></div>");
					$($divStatus).html(divStatusHtml);
					var $workflowLog = $($divStatus).find("form");
					$($workflowLog).find("#formStatusId").val(changeStatusId);
					$($workflowLog).attr("id", "workflowLogPayment");
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
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">AP Payment - ${apPayment.division.name}</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">Voucher Number</td>
					<td class="label-value">${apPayment.voucherNumber}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${apPayment.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Payment Header</legend>
		<table>
			<tr>
				<td class="label">Payment Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apPayment.paymentDate}"/></td>
			</tr>
			<tr>
				<td class="label">Supplier Name</td>
				<td class="label-value">${apPayment.supplier.numberAndName}</td>
			</tr>
			<tr>
				<td class="label">Supplier Account</td>
				<td class="label-value">${apPayment.supplierAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Specify payee</td>
				<td class="label-value">
					<input type="checkbox" id="specifyPayee"
						name="specifyPayee"/>
				</td>
			</tr>
			<tr>
				<td class="label">Payee</td>
					<td class="label-value">${apPayment.payee}</td>
			</tr>
			<tr>
				<td class="label">Bank Account</td>
				<td class="label-value">${apPayment.bankAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Checkbook Name</td>
				<td class="label-value">${apPayment.checkbook.name}</td>
			</tr>
			<tr>
				<td class="label">Check No.</td>
				<td class="label-value">${apPayment.checkNumber}</td>
			</tr>
			<tr>
				<td class="label">Check Date</td>
				<td class="label-value" id="tdCheckDate"><fmt:formatDate pattern="MM/dd/yyyy" value="${apPayment.checkDate}"/></td>
			</tr>
			<tr>
				<td class="label">Currency</td>
				<td class="label-value">${apPayment.currency.name}</td>
			</tr>
			<tr>
				<td class="label">Amount</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${apPayment.amount}'/></td>
			</tr>
			<tr>
				<td class="label">Official Receipt</td>
				<td class="label-value">${apPayment.officialReceipt}</td>
			</tr>
			<tr>
				<td class="label">Remarks</td>
				<td class="label-value">${apPayment.remarks}</td>
			</tr>
			<tr>
				<td></td>
				<td style="font-weight: bold;" class="label-value">
					<a id="btnPrintCheck" onclick="printCheck();">Click here to print check</a>
				</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>AP Invoices</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="3%" class="th-td-norm">#</th>
					<th width="60%" class="th-td-norm">Invoice Number</th>
					<th width="30%" class="th-td-edge">Amount</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${apPayment.apPaymentLineDtos}" var="paymentDto" varStatus="status">
					<tr>
						<td class="td-numeric">${status.index + 1}</td>
						<td class="th-td-norm">${paymentDto.referenceNumber}</td>
						<td class="th-td-edge txt-align-right">
						<c:choose>
							<c:when test="${paymentDto.amount lt 0}">
								(<fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value='${paymentDto.amount * -1}'/>)
							</c:when>
							<c:otherwise>
								<fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value='${paymentDto.amount}'/>
							</c:otherwise>
						</c:choose>
						</td>
						<c:set var="total" value="${total + paymentDto.amount}" />
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
		 		<tr>
                   <td colspan="2"></td>
                   <td class="txt-align-right">
                   		<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${total}' />
                   </td>
           		</tr>
			</tfoot>
		</table>
	</fieldset>
	<c:if test="${not empty apPayment.referenceDocuments}">
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
					<c:forEach items="${apPayment.referenceDocuments}" var="doc" varStatus="status">
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
</div>
</body>
</html>