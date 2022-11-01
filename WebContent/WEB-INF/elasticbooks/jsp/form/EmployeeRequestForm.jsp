<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description: Request for leave Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebForm.css"media="all">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">

</style>
<script type="text/javascript">
var $documentsTable = null;
var typeId = $("#hdnRequestTypeId").val();
$(document).ready(function(){
	$("#overtimeHours").attr("disabled", "disabled");
	if ("${employeeRequest.id}" != 0) {
		disableSlctField();
		$("#txtEmployee").val("${employeeRequest.employeeFullName}");
		$("#spanPositionId").text("${employeeRequest.employeePosition}");
		$("#spanDivisionId").text("${employeeRequest.employeeDivision}");
		$("#allowableBreak").val("${employeeRequest.overtimeDetail.allowableBreak}");
		handleHalfDay();
	} else {
		$("#rbAm").attr("checked", "checked");
	}
	initializeDocumentsTbl();

	if ("${employeeRequest.formWorkflow.complete}" == "true" 
			|| "${employeeRequest.formWorkflow.currentStatusId}" == 4) {
		$("#employeeRequestFormId :input").attr("disabled","disabled");
		$("#btnSave").attr("disabled", "disabled");
	}
});

function disableSlctField() {
	$("#companyId").attr("disabled", "disabled");
}

function enableSlctField() {
	$("#companyId").removeAttr("disabled");
}

function getEmployee () {
	var companyId = $("#companyId").val();
	var txtEmployee = encodeURIComponent($.trim($("#txtEmployee").val()));
	var uri = "/getEmployees/bycCompanyAndName?companyId="+ companyId + "&name=" + txtEmployee;
	if(txtEmployee == "") {
		$("#hdnEmployeeId").val("");
	}
	$.ajax({
		url: contextPath + uri,
		success : function(employee) {
			if (txtEmployee != ""){
				$("#spanEmployeeError").text("");
				$("#spanEmployeeReqError").html("");
				if (employee != null && employee[0] != undefined) {
					$("#hdnEmployeeId").val(employee[0].id);
				} else {
					$("#spanEmployeeError").text("Invalid employee.");
					$("#hdnEmployeeId").val("");
					$("#spanPositionId").html("");
					$("#spanDivisionId").html("");
					if ($("#spanEmployeeError").text() != "") {
						$("#spanEmployeeReqError").html("");
					}
				}
				if (typeId == 1) {
					getAvailableLeaves();
				}
			}
			getEmployeePosition();
			getEmployeeDivision();
		},
		error : function(error) {
			$("#spanEmployeeError").text("Invalid employee.");
			$("#hdnEmployeeId").val("");
			$("#spanPositionId").html("");
			$("#spanDivisionId").html("");
		},
		dataType: "json"
	});
}

function showEmployees () {
	var companyId = $("#companyId").val();
	var employeeName = encodeURIComponent($.trim($("#txtEmployee").val()))
	var uri = contextPath + "/getEmployees/byName?companyId="+ companyId + "&name=" + employeeName;
	$("#txtEmployee").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnEmployeeId").val(ui.item.id);
			var employeeName = ui.item.lastName + ", "
					+ ui.item.firstName + " " + ui.item.middleName;
			$(this).val(employeeName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + item.fullName + "</a>" )
			.appendTo( ul );
	};
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$documentsTable = $("#documentsTable").editableItem({
		data: refDocsJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "referenceObjectId", "varType" : "int"},
				{"name" : "fileName", "varType" : "string"},
				{"name" : "description", "varType" : "string"},
				{"name" : "file", "varType" : "string"},
				{"name" : "fileInput", "varType" : "string"},
				{"name" : "fileSize", "varType" : "double"},
		],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "fileName",
				"cls" : "fileName",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Name",
				"cls" : "docName tblInputText",
				"editor" : "label",
				"visible" : true,
				"width" : "25%" },
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "45%" },
			{"title" : "file",
				"cls" : "file",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Upload Image",
				"cls" : "fileInput tblInputFile",
				"editor" : "file",
				"visible" : true,
				"width" : "25%" },
			{"title" : "fileSize",
				"cls" : "fileSize",
				"editor" : "hidden",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});

	$("#documentsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize,$("#referenceDocsMgs"), $("#documentsTable"));
	});

	$("#documentsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

var isSaving = false;
function saveForm(){
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("");
		var isChecked = $("#cbHalfday").is(":checked");
		var dateFrom = $.trim($("#dateFrom").val());
		if (dateFrom != "" && isChecked) {
			$("#dateTo").val(dateFrom);
		}
		isSaving = true;
		enableSlctField();
		accountingUnformat();
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#btnSave").attr("disabled", "disabled");
		$("#overtimeHours").removeAttr("disabled");
		doPostWithCallBack ("employeeRequestFormId", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var status = $("#txtRFLStatus").val();
				if("${employeeRequest.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableSlctField();
					$("#txtRFLStatus").val(status);
				}
				if($("#hdnEmployeeId").val() == null || $("#hdnEmployeeId").val() == "") {
					$("#txtEmployee").val("");
				}
				var leaveBalance = $("#leaveBalanceId");
				var noOfDays = $("#noOfLeaveDaysId");
				var overtimeHours = $("#overtimeHours");
				var allowableBreak = $("#allowableBreak");
				if (typeId == 1) {
					formatMoney(leaveBalance);
					formatMoney(noOfDays);
				} else {
					formatMoney(overtimeHours);
					formatMoney(allowableBreak);
				}
				initializeDocumentsTbl();
				getEmployeePosition();
				getEmployeeDivision();
				handleHalfDay();
				if (isChecked) {
					var dateFromMsg = $.trim($("#spLdDateFromMsg").text());
					if (dateFromMsg != "") {
						$("#spLdDateFromMsg").text(dateFromMsg.replace("From", ""));
					}
				}
				isSaving = false;
			}
			$("#btnSave").removeAttr("disabled");
			$("#overtimeHours").attr("disabled", "disabled");
		});
	} else if (checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function getAvailableLeaves() {
	var employeeId = $("#hdnEmployeeId").val();
	var leaveTypeId = $("#leaveTypeId").val();
	var uri = "/getEmployees/getAvailableLeave?employeeId="+employeeId+"&typeOfLeaveId="+Number(leaveTypeId)+"&isRequestForm=true";
	$.ajax({
		url: contextPath + uri,
		success : function(credit) {
			if (credit != null && typeof credit != undefined) {
				$("#leaveBalanceId").val(accounting.formatMoney(credit));
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "text"
	});
}

function getEmployeePosition() {
	var employeeId = $("#hdnEmployeeId").val();
	var uri = "/getEmployees/getPosition?employeeId="+employeeId;
	if (employeeId != "") {
		$.ajax({
			url: contextPath + uri,
			success : function(position) {
				if (position != null && typeof position != undefined) {
					console.log(position);
					$("#spanPositionId").text(position.name);
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$("#spanPositionId").text("");
	}
}

function getEmployeeDivision() {
	var employeeId = $("#hdnEmployeeId").val();
	var uri = "/getEmployees/getDivision?employeeId="+employeeId;
	if (employeeId != "") {
		$.ajax({
			url: contextPath + uri,
			success : function(division) {
				if (division != null && typeof division != undefined) {
					console.log(division);
					$("#spanDivisionId").text(division.name);
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$("#spanDivisionId").text("");
	}
}

function changeCompany() {
	$("#hdnEmployeeId").val("")
	$("#txtEmployee").val("");
	$("#spanPositionId").text("");
	$("#spanDivisionId").text("");
}

function formatMoney(elem) {
	$(elem).val(accounting.formatMoney($(elem).val()));
}

function accountingUnformat() {
	var unformattedValue = typeId == 1 ? $("#noOfLeaveDaysId").val() : $("#overtimeHours").val();
	typeId == 1 ? $("#noOfLeaveDaysId").val(accounting.unformat(unformattedValue))
			: $("#overtimeHours").val(accounting.unformat(unformattedValue));
}

function computeNoOfHours() {
	var startTime = $.trim($("#startTime").val());
	var endTime = $.trim($("#endTime").val());
	var allowableBreak = $.trim($("#allowableBreak").val());
	$("#overtimeHours").val("");
	if(startTime != "" && endTime != ""){
		$("#spanEndTimeError").text("");
		var uri = "/employeeRequest/computeNoOfHours?startTime="+startTime+"&endTime="+endTime
				+"&allowableBreak="+allowableBreak;
		$.ajax({
			url: contextPath + uri,
			success : function(hoursDiff) {
				if (hoursDiff != null && typeof hoursDiff != undefined && hoursDiff != "Invalid Time Format.") {
					$("#overtimeHours").val(accounting.formatMoney(hoursDiff));
				} else {
					$("#spanEndTimeError").text("Invalid time format.");
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "text"
		});
	}
}

function computeNoOfDays() {
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	$("#noOfLeaveDaysId").val("");
	var $checkBox = $("#cbHalfday");
	if ($($checkBox).is(":checked")) {
		$("#noOfLeaveDaysId").val("0.50");
	} else {
		if(dateFrom != "" && dateTo != ""){
			$("#spanDaysLeave").text("");
			var uri = "/employeeRequest/computeNoOfDays?dateFrom="+dateFrom+"&dateTo="+dateTo;
			$.ajax({
				url: contextPath + uri,
				success : function(daysDiff) {
					if (daysDiff != null && typeof daysDiff != undefined) {
						if(daysDiff <= 0) {
							$("#spanDaysLeave").text("Invalid date range.");
							$("#noOfLeaveDaysId").val(0);
						} else {
							$("#noOfLeaveDaysId").val(daysDiff);
						}
					}
				},
				error : function(error) {
					console.log(error);
				},
				dataType: "text"
			});
		}
	}
}

function handleHalfDay() {
	var $checkBox = $("#cbHalfday");
	var isChecked = $($checkBox).is(":checked");
	var dateFromMsg = $.trim($("#spLdDateFromMsg").text());
	if (isChecked) {
		if (dateFromMsg != "") {
			$("#spLdDateFromMsg").text("Date is required.");
		}
		$("#spanDaysLeave").text("");
		$("#tdLdDateFrom").text("* Date");
		$("#trLdDateTo").hide('fast');
		$("#noOfLeaveDaysId").val("0.50");
		$("#trLdDateToMsg").hide();
		$("#noOfLeaveDaysId").attr("readonly", "readonly");
		$("#trPeriod").show();
	} else {
		if (dateFromMsg != "") {
			$("#spLdDateFromMsg").text("Date From is required.");
		}
		$("#tdLdDateFrom").text("* Date From");
		$("#trPeriod").hide();
		$("#trLdDateTo").show('fast');
		$("#trLdDateToMsg").show();
		$("#noOfLeaveDaysId").removeAttr("readonly");
		$("#trPeriod").hide();
		computeNoOfDays();
	}
}
</script>
</head>
<body>
	<div class="formDivBigForms">
		<form:form method="POST" commandName="employeeRequest" id="employeeRequestFormId">
			<div class="modFormLabel">Request For
				<c:choose>
					<c:when test="${employeeRequest.requestTypeId == 1}"> Leave</c:when>
					<c:otherwise> Overtime</c:otherwise>
				</c:choose> 
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId" />
			<form:hidden path="leaveDetail.id" />
			<form:hidden path="overtimeDetail.id"/>
			<form:hidden path="createdBy" />
			<form:hidden path="createdDate" />
			<form:hidden path="updatedBy" />
			<form:hidden path="formWorkflowId" />
			<form:hidden path="sequenceNo" />
			<form:hidden path="requestTypeId" id="hdnRequestTypeId"/>
			<form:hidden path="employeeId" id="hdnEmployeeId" />
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson" />
			<form:hidden path="ebObjectId"/>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number</td>
							<td class="value">
								<c:choose>
									<c:when test="${employeeRequest.id == 0 }">
										<input type="text" id="txtSequenceNo"
											class="textBoxLabel" readonly="readonly" />
									</c:when>
									<c:otherwise>
										${employeeRequest.sequenceNo}
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status"
								value="${employeeRequest.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text" id="txtRFLStatus"
								class="textBoxLabel" readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>
					<c:choose>
						<c:when test="${employeeRequest.requestTypeId == 1}">Request For Leave</c:when>
						<c:otherwise>Request For Overtime</c:otherwise>
					</c:choose> Header </legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Date</td>
							<td class="value"><form:input path="date" id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')" style="cursor: pointer"
								style="float: right;" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="date" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">* Company/Branch </td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass" onChange="changeCompany();">
									<form:options items="${companies}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Employee</td>
							<td class="value">
								<form:input path="employeeFullName" id="txtEmployee" class="input" onkeydown="showEmployees();"
								onkeyup="showEmployees();" onfocusout="getEmployee();" onblur="getEmployee();"
								value="${employeeRequest.employeeFullName}" />
							</td>
						</tr>

						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanEmployeeError" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors id="spanEmployeeReqError" path="employeeId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">Position</td>
							<td class="value"><span id="spanPositionId"></span></td>
						 </tr>
						 <tr>
							<td class="labels">Division/Department</td>
							<td class="value"><span id="spanDivisionId"></span></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
				<c:choose>
					<c:when test="${employeeRequest.requestTypeId eq 1}">
				<legend>Leave Details</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Half Day</td>
							<td class="value">
								<form:checkbox path="leaveDetail.halfDay" id="cbHalfday" onclick="handleHalfDay();"/>
							</td>
						</tr>
						<tr id="trPeriod" style="display: none;">
							<td class="labels" >Period</td>
							<td>
								<form:radiobutton path="leaveDetail.period" value="1" id="rbAm" />AM
								<form:radiobutton path="leaveDetail.period" value="2" />PM
							</td>
						</tr>
						<tr>
							<td class="labels" id="tdLdDateFrom">* Date From</td>
							<td class="value">
								<form:input path="leaveDetail.dateFrom" id="dateFrom" onblur="evalDate('dateFrom'); computeNoOfDays();" 
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2"
									src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('dateFrom')" style="cursor: pointer"
									style="float: right;" />
							</td>
						</tr>
						<tr id="trLdDateFromMsg">
							<td class="labels"></td>
							<td class="value"><form:errors path="leaveDetail.dateFrom" cssClass="error" id="spLdDateFromMsg"/></td>
						</tr>
						<tr id="trLdDateTo">
							<td class="labels" >* Date To</td>
							<td class="value">
								<form:input path="leaveDetail.dateTo" id="dateTo" onblur="evalDate('dateTo'); computeNoOfDays();"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2"
									src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('dateTo')" style="cursor: pointer"
									style="float: right;" /></td>
						</tr>
						<tr id="trLdDateToMsg">
							<td class="labels"></td>
							<td class="value"><form:errors path="leaveDetail.dateTo" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">Days</td>
							<td class="value">
								<form:input path="leaveDetail.leaveDays" id="noOfLeaveDaysId" class="numeric" value="${employeeRequest.leaveDetail.leaveDays}"
									style="width: 75px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanDaysLeave" class="error"></span>
								<form:errors path="leaveDetail.leaveDays" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Leave Type</td>
							<td class=value>
								<form:select path="leaveDetail.typeOfLeaveId" id="leaveTypeId" cssClass="frmSelectClass"
									onchange="getAvailableLeaves();" >
									<form:options items="${typeOfLeaves}" itemLabel="name" itemValue="id" />
								</form:select>
								<label class="labels" style="padding-left: 25px">Balance</label>
								<form:input path="leaveDetail.leaveBalance" id="leaveBalanceId" class="textBoxLabel"
									value="${employeeRequest.leaveDetail.leaveBalance}" style="width: 40px;" readonly="true"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="leaveDetail.typeOfLeaveId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">Remarks</td>
							<td class="value"><form:textarea path="leaveDetail.remarks" id="description" class="input" /></td>
						</tr>
					</table>
					<br>
					</c:when>
					<c:otherwise>
						<legend>Overtime Details</legend>
						<table class="formTable">
							<tr>
								<td class="labels">* Overtime Date</td>
								<td class="value"><form:input path="overtimeDetail.overtimeDate" id="overtimeDate" onblur="evalDate('overtimeDate')"
										style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
									src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('overtimeDate')" style="cursor: pointer"
									style="float: right;" /></td>
							</tr>
							<tr>
								<td></td>
								<td colspan="2"><form:errors path="overtimeDetail.overtimeDate" cssClass="error"
										style="margin-left: 12px;" /></td>
							</tr>
							<tr>
								<td class="labels">* Start Time</td>
								<td class="value">
									<form:input path="overtimeDetail.startTime" id="startTime" class="input"
										value="${employeeRequest.overtimeDetail.startTime}" style="width: 75px;" onblur="computeNoOfHours();"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td colspan="2"><form:errors path="overtimeDetail.startTime" cssClass="error"
										style="margin-left: 12px;" /></td>
							</tr>
							<tr>
								<td class="labels">* End Time</td>
								<td class="value">
									<form:input path="overtimeDetail.endTime" id="endTime" class="input"
										value="${employeeRequest.overtimeDetail.endTime}" style="width: 75px;" onblur="computeNoOfHours();"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td colspan="2"><form:errors path="overtimeDetail.endTime" cssClass="error"
										style="margin-left: 12px;" /></td>
							</tr>
							<tr>
								<td class="labels">* No. of Hours</td>
								<td class="value">
									<form:input path="overtimeDetail.overtimeHours" id="overtimeHours" class="numeric"
										value="${employeeRequest.overtimeDetail.overtimeHours}" style="width: 75px;"
										onblur="formatMoney(this);" />
									<form:label path="overtimeDetail.allowableBreak" style="padding-left: 85px;
											padding-right: 15px">Allowable Break</form:label>
									<form:input path="overtimeDetail.allowableBreak" id="allowableBreak" class="numeric"
											style="width: 75px;" onblur="formatMoney(this); computeNoOfHours();" />
								</td>
							</tr>
							<tr>
								<td></td>
								<td colspan="2">
									<span id="spanEndTimeError" class="error" style="margin-left: 12px;"></span>
									<form:errors path="overtimeDetail.overtimeHours" cssClass="error" />
								</td>
							</tr>
							<tr>
								<td class="labels">* Purpose</td>
								<td class="value" colspan="2"><form:textarea path="overtimeDetail.purpose" id="description" class="input" /></td>
							</tr>
							<tr>
								<td></td>
								<td class="value">
									<form:errors path="overtimeDetail.purpose" cssClass="error" />
								</td>
							</tr>
						</table>
					</c:otherwise>
				</c:choose>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Documents Table</legend>
					<div id="documentsTable"></div>
					<table>
						<tr>
							<td colspan="12">
								<form:errors path="referenceDocsMessage" cssClass="error"
									style="margin-top: 12px;" />
							</td>
						</tr>
						<tr>
							<td colspan="12"><span class="error" id="spDocSizeMsg"></span></td>
						</tr>
						<tr>
							<td colspan="12"><span class="error" id="referenceDocsMgs" style="margin-top: 12px;"></span></td>
						</tr>
					</table>
					</fieldset>
					<table class="frmField_set">
						<tr>
							<td><form:errors path="formWorkflowId" cssClass="error"/></td>
						</tr>
					</table>
					<br>
					<table class="frmField_set">
						<tr>
							<td align="right" colspan="2"><input type="button" id="btnSave"
								value="Save" onclick="saveForm();" />
							</td>
						</tr>
					</table>
			</div>
		</form:form>
	</div>
</body>
</html>