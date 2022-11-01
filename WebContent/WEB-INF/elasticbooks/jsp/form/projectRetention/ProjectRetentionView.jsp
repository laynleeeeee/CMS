<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Project Retention view.
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
	formObjectId = parseInt("${projectRetention.ebObjectId}");
	if("${projectRetention.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});

function doFormPreUpdateStatus(statusId, pId) {
	changeStatusId = statusId;
	divStatusHtml = $("#divStatus").html();
	workflowHtml = $("#workflowlog").html();
	workflowAction = $("#workflowlog").attr("action");
	if(statusId == RECEIVED_ID) {
		var url = contextPath + "/projectRetention/receivePr/" + pId;
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
		var url = contextPath + "/projectRetention/receivePr/" + formId;
		doPostWithCallBackObjParam(url, "#projectRetentionId", divForm, false, function(data) {
			if(data.startsWith("saved")) {
				var formStatus = new Object();
				formStatus.title = "Project Retention";
				formStatus.message ="Receive log saved successfully.";
				updateTable (formStatus, true);
				dojo.byId("form").innerHTML = "";
				if (typeof $("#divStatus").html() == "undefined") {
					var $divStatus = $("<div id='divStatusDr'></div>");
					$($divStatus).html(divStatusHtml);
					var $workflowLog = $($divStatus).find("form");
					$($workflowLog).find("#formStatusId").val(changeStatusId);
					$($workflowLog).attr("id", "workflowLogPr");
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
<style>
.footerViewCls {
	font-size: 12;
	font-weight: bold;
	text-align: right;
	width: 300px;
}
</style>
<title>Project Retention View</title>
</head>
<body>
<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">Project Retention - ${projectRetention.division.name}</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">PR No.</td>
					<td class="label-value">${projectRetention.sequenceNo}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${projectRetention.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Project Retention Header</legend>
			<table>
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${projectRetention.company.name}</td>
				</tr>
				<tr>
					<td class="label">Division</td>
					<td class="label-value">${projectRetention.division.name}</td>
				</tr>
				<tr>
					<td class="label">SO Reference</td>
					<td class="label-value">${projectRetention.soNumber}</td>
				</tr>
				<tr>
					<td class="label">PO/PCR No.</td>
					<td class="label-value">${projectRetention.poNumber}</td>
				</tr>
				<tr>
					<td class="label">Customer </td>
					<td class="label-value">${projectRetention.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${projectRetention.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${projectRetention.date}"/></td>
				</tr>
				<tr>
					<td class="label">Due Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${projectRetention.dueDate}"/></td>
				</tr>
					<tr>
					<td class="label">Amount</td>
					<td class="label-value">
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${projectRetention.amount}" />
					</td>
				</tr>
				<tr>
					<td class="label">Currency</td>
					<td class="label-value">${projectRetention.currency.name}</td>
				</tr>
				<tr>
					<td class="label">Remarks</td>
					<td class="label-value">${projectRetention.remarks}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Project Retention Detail</legend>
				<table class="dataTable" id="serialItems" style="width: 100%;">
					<thead>
						<tr>
							<th width="3%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
							<th width="42%" class="th-td-norm">Particulars</th>
							<th width="15%" class="th-td-norm">Gross Amount</th>
							<th width="15%" class="th-td-norm">VAT Type</th>
							<th width="15%" class="th-td-norm">VAT Amount</th>
							<th width="15%" class="th-td-norm">Amount</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${projectRetention.projectRetentionLines}" var="prLine" varStatus="status">
							<tr style=" border-bottom: 1px solid black;"> 
								<td class="td-numeric v-align-top" style="border-bottom: 1px solid black; border-left: 1px solid black;">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${prLine.referenceNo}</td>
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type='number' minFractionDigits='4'
										maxFractionDigits='6' value='${prLine.upAmount}'/>
								</td>
								<td class="th-td-norm v-align-top">${prLine.taxType.name}</td>
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value='${prLine.vatAmount}'/>
								</td>
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value='${prLine.amount}'/>
								</td>
								<c:set var="totalVat" value="${totalVat + prLine.vatAmount}"/>
								<c:set var="subTotal" value="${subTotal + prLine.amount}"/>
							</tr>
						</c:forEach>
					</tbody>
				</table>
		</fieldset>
		<c:if test="${not empty projectRetention.referenceDocuments}">
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
						<c:forEach items="${projectRetention.referenceDocuments}" var="doc" varStatus="status">
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
		<fieldset class="frmField_set" style="border: 0;">
			<table>
				<tr>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Retention Amount</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${subTotal}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">VAT</td>
					<td class="footerViewCls" colspan="2">
						<c:set var="totalVAT" value="${siTotalVat + apigTotalVat + totalApLineVat}"/>
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${totalVat}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Withholding Tax</td>
					<td class="footerViewCls">${projectRetention.wtAcctSetting.name}</td>
					<td class="footerViewCls">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${projectRetention.wtAmount}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Net Amount</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${(subTotal + totalVat) - projectRetention.wtAmount}"/>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>