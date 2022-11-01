<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../include.jsp" %>

<!--

	Description: Main form for workflow
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebApproval.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<style type="text/css">
.formFloat {
	display:none;
	background: white;
	top: 50%;
	left: 50%;
	margin-top: -300px;
	margin-left: -250px;
	border:1px solid black;
	width: 80%;
}

.imgPrint: hover {
	cursor: pointer;
}
</style>
<script>
var formStatusId = "${workflowlog.formStatusId}";
var formTitle = "${formProperty.title}";
var printUri = "${formProperty.print}";
var formObjectId = null;
$(document).ready(function() {
	processForm();
});

function processForm () {
	$("#divForm").load ("${formProperty.form}"+ "?pId="+${formId}, function (data){
		//Set background color of disabled inputs.
		$("#divForm :input").css("background-color", "white");
		$("#divForm :input").attr("disabled", "disabled").off('click');
		$("#divForm img").hide().off('click');
		if (typeof specialCaseEditing == 'function')
			specialCaseEditing ();
		if (typeof postWorkflowInit == "function") {
			// Post initialization for the workflow man form. The form can override this method to perform
			// extra initialization. 
			postWorkflowInit($("#btnPrint"), $("#btnSave"), $("#imgEdit"), $("#formStatusId"));
		}
	});
	// If there's no configured print, hide print button
	// Show the print button for the completed workflow only.
	//if (printUri == "" || ${workflow.complete} != true){
	//	$("#btnPrint").hide();
	//}
	//Disable only when form is cancelled.
	var isCancelled = ${isCancelled};
	if (isCancelled == true) {
		$("#formStatusId").attr ("disabled", "disabled");
		$("#btnSave").attr("disabled", "disabled");
	}
	var isEditable = ${isEditable};
	if (!isEditable) {
		$("#imgEdit").hide();
	}

	$("#formStatusId").prepend('<option value="-1" selected="selected"></option>');
}

function initFunctions () {
	$("#btnSave").attr("onclick", "updateStatus();");
	$("#imgEdit").click(function () {
		var path = "${formProperty.edit}"+ "?pId="+${formId};
		if (typeof disableFormFields == 'function')
				disableFormFields();
		if (typeof enableFormFields == 'function')
			enableFormFields();
		showEditForm (path);
	});
	$("#btnPrint").click (function () {
		window.open(printUri+"?pId=${formId}");
	});
}

var isSaving = false;
function updateStatus() {
	if(isSaving == false) {
		isSaving = true;
		if(typeof doFormPreUpdateStatus == 'function') {
			var statusId = $("#formStatusId").val();
			var pId = "${formId}";
			doFormPreUpdateStatus(statusId, pId, saveWorkflowLog);
		} else {
			saveWorkflowLog();
		}
	} 
}

function specialSaveWorkflowLog(actionURL, $workflowLog, $divForm) {
	var workflowLogId = $($workflowLog).attr("id");
	var divFormId = $($divForm).attr("id");
	doPostWithCallBackObjParam(actionURL, $workflowLog, $divForm, false, function(data) {
		if(data.startsWith("saved")) {
			var objectId = formObjectId;
			var formStatus = new Object();
			formStatus.objectId = objectId;
			updateTable (formStatus, true);
			$($divForm).html("");
		} else {
			dojo.byId("form").innerHTML = data;
			processForm();
			initFunctions();
		}
		isSaving = false;
	});
}

function saveWorkflowLog() {
	doPostWithCallBack("workflowlog", "divForm", function(data) {
		if(data.startsWith("saved")) {
			var objectId = formObjectId;
			var formStatus = new Object();
			formStatus.objectId = objectId;
			updateTable (formStatus, true);
			dojo.byId("divForm").innerHTML = "";
		} else {
			dojo.byId("form").innerHTML = data;
			processForm();
			initFunctions();
		}
		isSaving = false;
	});
}

$(function () {
	initFunctions();
});

function enableSaveButton (selectStatusId) {
	if ($(selectStatusId).val() != -1){
		$("#btnSave").removeAttr("disabled");
	} else {
		$("#btnSave").attr("disabled", "disabled");
	}
}

function showEditForm (path) {
	$("#editForm").load(path, function (data) {
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
}

function showCLosingDialog() {
	$("#successDialog").dialog({
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog("close");
				$(this).dialog("destroy");
			}
		}
	});
}

function updateTable(formStatus) {
	showCLosingDialog();
	$("#successDialog").dialog({
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog("close");
				$(this).dialog("destroy");
			}
		}
	});
	$('#editForm').trigger('close');
	var objectId = formStatus.objectId;
	if (objectId != null) {
		$.ajax({
			url: contextPath + "/oinfo?objectId="+objectId,
			success: function(responseText){
				var objectInfo = JSON.parse(responseText);
				var latestStatus = objectInfo.latestStatus;
				var popupLink = objectInfo.popupLink.substr(1);
				var printOutLink = contextPath + objectInfo.printOutLink;
				var message = "<img class='imgPrint' src='../CMS/images/ic_print.png' height='20' onclick='window.open(\""+printOutLink+"\")' width='20'style='vertical-align: middle; padding-bottom: 5px;'>"
					+ "<a href='#' onClick='showEditForm(\""+popupLink+"\")'>"+objectInfo.title +"</a> was successfully " + latestStatus +".";
				$("#tableStatusId tr:first").after("<tr><td class='recSMS'>"+message +"</td></tr><tr>");
				retrieveIndivForms(selectedFormType, "", 1);
			}
		})
	} else {
		$("#tableStatusId tr:first").after("<tr><td class='recTitle'>"+formStatus.title +"</td></tr><tr><td class='recSMS'>"+formStatus.message+"</td></tr><tr><td></td></tr>");
		retrieveIndivForms(selectedFormType, "", 1);
	}
}
</script>
<title>Form work flow</title>
</head>
<body>
<div id="successDialog" style="display: none;">
	<p align="center" style="font-size: small; font-style: italic;">
		Successfully updated
	</p>
</div>
<div id="divStatus">
	<form:form method="POST" commandName="workflowlog">
		<table >
			<form:hidden path="id"/>
			<form:hidden path="formWorkflowId"/>
		<tr class='searchTextBox'>
			<td width="5%">Remarks </td>
			<td width="10%"><form:textarea path="comment" /></td>
			<td width="5%">Status</td>
			<td>
				<div id="divFilterId">
				<form:select cssClass="frmSmallSelectClass" path="formStatusId" items="${statuses}" id="formStatusId" 
								itemLabel="description" itemValue="id"
								onchange="enableSaveButton (this);"/>
								<input type="button" id="btnSave" value="Update Status" disabled="disabled">
								<input type="button" id="btnPrint" value="Print" >
								<img src='../CMS/images/file_edit.png' height='20' width='20'
									style="vertical-align: middle; padding-bottom: 5px;" id="imgEdit">
				</div>
			</td>
			
		</tr>
		<tr>
			<td colspan="4" class="error">
				${workflowlog.workflowMessage}
			</td>
		</tr>
		<tr class='searchTextBox'> 
			<td align="left" colspan="4">
				START
				<c:forEach items="${process}" var="formStatus">
					<c:choose>
						<c:when test="${formStatus.selected == true}">
							--> <i><b><font color="red">${formStatus.description}</font></b></i>
						</c:when> 
						<c:otherwise>
							<c:if test="${formStatus.id ne 32}">
								--> ${formStatus.description}
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</td>
		</tr>
		
	</table>
	</form:form>
</div>

<div id="divForm">
</div>
<div>
	<hr>
	<table>
		<c:forEach items="${workflow.formWorkflowLogs}" var="logStatus">
			<tr>
				<td>
					<table class="wfLogStatTbl">
						<tr>
							<td width="35%">${logStatus.formStatus.description} BY:</td>
							<td width="15%"></td>
							<td>Remarks:</td>
						</tr>
						<tr class="processFlowName">
							<td>
								<table>
									<tr>
										<td align="center"><input class="workFlowInput" value="${logStatus.created.lastName}, ${logStatus.created.firstName}"/></td>
									</tr>
									<tr>
										<td align="center">${logStatus.created.position.name}</td>
									</tr>
									<tr>
										<td align="center" style="padding-left: 5%"><input class="workFlowInput" style="width: 40%;"
											value="<fmt:formatDate value="${logStatus.createdDate}" pattern="MM/dd/yyyy"/>"></td>
									</tr>
									<tr>
										<td align="center">Date</td>
									</tr>
								</table>
							</td>
							<td></td>
							<td>
								<pre style="text-indent: 0px; white-space: pre-wrap; font-size: x-small;
										font-family: sans-serif;">${logStatus.comment}</pre>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</c:forEach>
	</table>
</div>
</body>
</html>