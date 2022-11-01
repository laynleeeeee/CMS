<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description: Payroll HTML Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebForm.css"media="all">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"media="all">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
var selectTPSched = "";
var selectedMonth = "";
var selectedYear = "";
var dateFrom = "";
var dateTo = "";
$(document).ready(function () {
	if ("${timesheetFormDto.timeSheet.id}" > 0) {
		$("#hdnCompanyId").val("${timesheetFormDto.timeSheet.companyId}");
		$("#hdnDivisionId").val("${timesheetFormDto.timeSheet.divisionId}");
		$("#slctMonth").val("${timesheetFormDto.timeSheet.payrollTimePeriod.month}");
		$("#slctYear").val("${timesheetFormDto.timeSheet.payrollTimePeriod.year}");
		if ("${fileName}" != "") {
			$("#fileName").val("${fileName}");
			$("#hdnFile").val("${file}");
			$("#hdnFileSize").val("${fileSize}");
			$("#hdnDescription").val("${description}");
			$("#lblFile").text("${fileName}");
		}
		loadTimePeriodSchedules("${timesheetFormDto.timeSheet.selectTPSched}");
	} else {
		$("#payrollTimePeriodId").val("${payrollTimePeriodId}");
		$("#payrollTimePeriodScheduleId").val("${payrollTimePeriodScheduleId}");
		$("#slctMonth").val("${month}");
		$("#slctYear").val("${year}");
		loadTimePeriodSchedules();
	}
	resize();
	disableFields();
});

function disableFields() {
	if ("${timesheetFormDto.timeSheet.formWorkflow.complete}" == "true"
			|| "${timesheetFormDto.timeSheet.formWorkflow.currentStatusId}" == 4) {
		$("#imgDate1").hide();
		$("#timeSheetForm :input").attr("disabled","disabled");
	}
}

function loadTimePeriodSchedules(slctTimePeriodSchedule) {
	resize();
	$("#slctTimePeriodSchedule").empty();
	var month = $("#slctMonth").val();
	var year = $("#slctYear").val();
	var uri = contextPath + "/payroll/getTPSchedules?month="+month+"&year="+year;
	var optionParser = {
			getValue: function (rowObject){
				if (rowObject != null) {
					return rowObject["id"] + "-" + rowObject["payrollTimePeriodId"] +
						(rowObject["computeContributions"] ?  "-" + rowObject["computeContributions"] : "");
				}
			},

			getLabel: function (rowObject){
				if (rowObject != null)
					return rowObject["name"];
			}
	};
	postHandler = {
			doPost: function(data) {
				// This is to remove any duplication.
				var found = [];
				$("#slctTimePeriodSchedule option").each(function() {
					if($.inArray(this.value, found) != -1) 
						$(this).remove();
					found.push(this.value);
				});
				if (slctTimePeriodSchedule != "" || slctTimePeriodSchedule != "undefined") {
					console.log("${timesheetFormDto.timeSheet.selectTPSched}");
					$("#slctTimePeriodSchedule").val(slctTimePeriodSchedule);
				}
				setCompContribAndTimePeriod();
			}
	};
	loadPopulate (uri, false, selectTPSched, "slctTimePeriodSchedule", optionParser, postHandler);
}

function getDates() {
	var uri =  contextPath + "/payroll/getDates?payrollTimePeriodScheduleId="+
	$("#slctTimePeriodSchedule").val().split("-")[0];
	$.ajax({
		url: uri,
		type: "GET",
		dataType: "text",
		success: function(data) {
			var splitted = data.split("-");
			dateFrom = splitted[0];
			dateTo = splitted[1];
		},
		error: function(jqXHR, textStatus, errorMessage) {
			console.log(errorMessage); // Optional
		}
	});
}

function setCompContribAndTimePeriod() {
	var value = $("#slctTimePeriodSchedule").val();
	if (value != null) {
		selectTPSched = value;
		// Set the checkbox
		var isContribution = value.indexOf("-true") !== -1;
		$("#cbComputeContribution").prop("checked", isContribution);
		// Split the value string.
		var splitted = value.split("-");
		// Set the payroll time period id.
		var payrollTimePeriodId = splitted[1];
		$("#payrollTimePeriodId").val(payrollTimePeriodId);
		getDates();
	} else {
		selectTPSched = "";
		dateFrom = "";
		dateTo = "";
		$("#payrollTimePeriodId").val("");
		$("#payrollTimePeriodScheduleId").val("");
	}
}

function setTPScheduleId() {
	var value = $("#slctTimePeriodSchedule").val();
	if (value != null) {
		var timePeriodScheduleId = value.split("-")[0];
		$("#payrollTimePeriodScheduleId").val(timePeriodScheduleId);
		$("#selectTPSched").val(value);
	} 
}


function formatMoney($field){
	$($field).val(accounting.formatMoney($($field).val()));
}

function clearError(){
	$("#companyError").text("");
	$("#fileNameError").text("");
	$("#fileNameErrorPath").text("");
	$("#employeeTypeError").text("");
	$("#datefromError").text("");
	$("#dateToError").text("");
}

var isSaving = false;
function saveTimesheet() {
	setTPScheduleId();
	$("#dateFrom").val(dateFrom);
	$("#dateTo").val(dateTo);
	var payrollJson = buidlJsonPayroll();
	$("#timeSheetDtoJson").val(payrollJson);
	if(isSaving == false){
		clearError();
		isSaving = true;
		$("#btnSaveTimeSheet").attr("disabled", "disabled");
		doPostWithCallBack ("timeSheetForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				$("#form").html("");
				isSaving = false;
			} else {
				setCompContribAndTimePeriod();
				var month = $("#slctMonth").val();
				var year =  $("#slctYear").val();
				var lblFile = $("#lblFile").html();
				var fileName = $("#fileName").val();
				var hdnFile = $("#hdnFile").val();
				var hdnFileSize = $("#hdnFileSize").val();
				var description = $("#hdnDescription").val();
				var slctTimePeriodSchedule = $("#slctTimePeriodSchedule").val();
				if ("${timesheetFormDto.timeSheet.id}" > 0) {
					var companyId = $("#hdnCompanyId").val();
					var companyName = $("#lblCompany").html();
					var divisionId = $("#hdnDivisionId").val();
					var divisionName = $("#lblDivision").html();
					$("#editForm").html(data);
					$("#hdnCompanyId").val(companyId);
					$("#lblCompany").html(companyName);
					$("#hdnDivisionId").val(divisionId);
					$("#lblDivision").html(divisionName);
				} else {
					var companyId = $("#companyId").val();
					var divisionId = $("#divisionId").val();
					$("#form").html(data);
					$("#companyId").val(companyId);
					$("#divisionId").val(divisionId);
				}
				$("#lblFile").html(lblFile);
				$("#fileName").val(fileName);
				$("#hdnFile").val(hdnFile);
				$("#hdnFileSize").val(hdnFileSize);
				$("#hdndescription").val(description);
				$("#slctMonth").val(month);
				$("#slctYear").val(year);
				loadTimePeriodSchedules(slctTimePeriodSchedule);
			}
			resize();
			$("#btnSaveTimeSheet").removeAttr("disabled");
			isSaving = false;
		});
	}
}

function setSelectedMonth (value) {
	selectedMonth = value;
}

function setSelectedYear (value) {
	selectedYear = value;
}

function reloadData() {
	$("#divPayrollETimesheet").load(contextPath + "/payroll/reloadTimesheet");
	$("#divPayrollESalary").load(contextPath + "/payroll/reloadSalary");
	resize();
}

function browseFile() {
	$("#file").val("");
	$("#file").trigger("click");
}

function parseTimeSheet() {
	if (!validateBeforeParse() && $.trim($("#file").val()) != "") {
		setTPScheduleId();
		$("#divPayrollETimesheet").html("");
		var formData = new FormData();
		var file = $("#file")[0].files[0];
		formData.append("file", file);
		var pId = "${timesheetFormDto.timeSheet.id}" > 0;
		var companyId = pId ? $("#hdnCompanyId").val() : $("#companyId").val();
		var divisionId = pId ? $("#hdnDivisionId").val() : $("#divisionId").val();
		var biometricModelId = $("#biometricModelId").val();
		var payrollTimePeriodId = $("#payrollTimePeriodId").val();
		var payrollTimePeriodScheduleId = $("#payrollTimePeriodScheduleId").val();
		var month = $("#slctMonth").val();
		var year = $("#slctYear").val();
		var date = $("#date").val();
		var uri = contextPath + "/timesheet/parseTimeSheet?tId=${timesheetFormDto.timeSheet.id}&date="+date
				+"&dateTo="+dateTo +"&dateFrom="+dateFrom+"&companyId="+companyId+"&biometricModelId="+biometricModelId
				+"&payrollTimePeriodId="+payrollTimePeriodId+"&payrollTimePeriodScheduleId="+payrollTimePeriodScheduleId
				+"&month="+month+"&year="+year+"&selectTPSched="+selectTPSched+"&divisionId="+divisionId;
		$.ajax({
			url: uri,
			type: "POST",
			data: formData,
			dataType: "text",
			processData: false,
			contentType: false,
			success: function(data) {
				$("#divPayrollETimesheet").html(data);
			},
			error: function(jqXHR, textStatus, errorMessage) {
				var txtErrorMsg = jqXHR.responseText;
				$("#divPayrollETimesheet").html(txtErrorMsg);
				$('#divPayrollETimesheet').find("pre:lt(1)").remove();
				$('#divPayrollETimesheet').find("p").remove();
			}
		});
		resize();
	} else {
		$("#errorFileData").html("Please select a log file.");
	}
}

function validateBeforeParse(){
	clearError();
	var hasError = false;
	var pId = "${timesheetFormDto.timeSheet.id}" > 0;
	var companyId = pId ? $("#hdnCompanyId").val() : $("#companyId").val();
	if(companyId == "" && $.trim($("#companyErrorPath").text()) == "") {
		$("#companyError").text("Company is a required field.");
		hasError = true;
	}
	if($("#lblFile").text() == "" && $.trim($("#fileNameErrorPath").text()) == ""){
		$("#fileNameError").text("File is a required field.");
		hasError = true;
	}
	return hasError;
} 

function clearError(){
	$("#companyError").text("");
	$("#fileNameError").text("");
	$("#fileNameErrorPath").text("");
	$("#employeeTypeError").text("");
	$("#datefromError").text("");
	$("#dateToError").text("");
}

$("#file").change(function () {
	var value = $.trim($(this).val());
	if (value != "") {
		var fileNames = value.split("\\");
		$("#lblFile").text($.trim(fileNames.slice(-1)[0]));
		convertDocToBase64($(this));
	}
});

function convertDocToBase64($fileObj) {
	var value = $.trim($($fileObj).val());
	if (value != "") {
		var file = $($fileObj)[0].files[0];
		var FR = new FileReader();
		FR.onload = function(e) {
			$("#hdnFile").val(e.target.result);
		};
		FR.readAsDataURL(file);
	}
}

function convBase64ToFile(strBase64, filename) {
	var tmp = strBase64.split(",");
	var prefix = tmp[0];
	var contentType = prefix.split(/[:;]+/)[1];
	var byteCharacters = atob(tmp[1]);

	var byteNumbers = new Array(byteCharacters.length);
	for (var i = 0; i < byteCharacters.length; i++) {
	    byteNumbers[i] = byteCharacters.charCodeAt(i);
	}
	var byteArray = new Uint8Array(byteNumbers);
	var blob = new Blob([byteArray], {type: contentType});
	var blobUrl = URL.createObjectURL(blob);

	var link = document.createElement("a");
	link.href = blobUrl;
	link.download = filename;
	link.style.display = "none";
	document.body.appendChild(link);
	link.click();
	delete link;
}

$(window).resize(function() {
	resize();
});

function resize(){
	var screenWidth = screen.width;
	var origWidth = 0;
	var pxDeductTS = 330;
	if (screenWidth >= 1920) { // 1920 Screen Resolution
		origWidth = 1229;
	} else if (screenWidth >= 1680){ // 1680 Screen Resolution
		origWidth = 1070;
	} else if (screenWidth >= 1440){ // 1440 Screen Resolution
		origWidth = 911;
	} else if (screenWidth >= 1280) { // 1280 Screen Resolution
		origWidth = 800;
	} else if (screenWidth >= 1024) { // 1024 Screen Resolution
		origWidth = 650;
	}
	$(".resizeTS").css("width", origWidth-Number(pxDeductTS));
}

$(document).ajaxStop(function(){
	resize();
});

function downloadTemplate() {
	var formId = parseInt($("#hdnId").val());
	var companyId = formId == 0 ? $("#companyId").val() : $("#hdnCompanyId").val();
	var payrollTimePeriodId = $("#payrollTimePeriodId").val();
	var payrollTimePeriodScheduleId = $("#payrollTimePeriodScheduleId").val();
	var url = contextPath + "/timesheet/timeSheetTemplate.csv?payrollTimePeriodId="+payrollTimePeriodId
			+"&payrollTimePeriodScheduleId="+payrollTimePeriodScheduleId+"&companyId="+companyId;
	window.open(url);
}
</script>
</head>
<body>
	<div class="formDivBigForms">
		<form:form method="POST" commandName="timesheetFormDto" id="timeSheetForm">
			<form:hidden path="timeSheet.id" id="hdnId" />
			<form:hidden path="timeSheet.formWorkflowId" />
			<form:hidden path="timeSheet.sequenceNumber"/>
			<form:hidden path="timeSheet.createdBy"/>
			<form:hidden path="timeSheet.timeSheetDtoJson" id="timeSheetDtoJson"/>
			<form:hidden path="timeSheet.payrollTimePeriodId" id="payrollTimePeriodId"/>
			<form:hidden path="timeSheet.payrollTimePeriodScheduleId" id="payrollTimePeriodScheduleId"/>
			<form:hidden path="timeSheet.dateFrom" id="dateFrom"/>
			<form:hidden path="timeSheet.dateTo" id="dateTo"/>
			<form:hidden path="timeSheet.selectTPSched" id="selectTPSched"/>
			<form:hidden path="employeeDtrsJson" id="hdnEmployeeDtrsJson"/>
			<form:hidden path="file" id="hdnFile"/>
			<form:hidden path="fileName" id="fileName"/>
			<form:hidden path="fileSize" id="hdnFileSize"/>
			<form:hidden path="description" id="hdnDescription"/>
			<form:hidden path="timeSheet.ebObjectId" />
			<div class="modFormLabel">
				Timesheet<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set" id="sysGenId">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number</td>
							<td class="value">
								<c:choose>
									<c:when test="${timesheetFormDto.timeSheet.id == 0 }">
										<input type="text" id="txtSequenceNo" class="textBoxLabel" readonly="readonly" />
									</c:when>
									<c:otherwise>
										${timeSheet.sequenceNumber}
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status"
								value="${timesheetFormDto.timeSheet.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text" id="txtPayrollStatus"
								class="textBoxLabel" readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Timesheet Header</legend>
					<table class="formTable">
						
						<tr>
							<td class="labels">* Date</td>
							<td class="value"><form:input path="timeSheet.date" id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate1"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="timeSheet.date" cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<c:choose>
									<c:when test="${timesheetFormDto.timeSheet.id eq 0}">
										<form:select path="timeSheet.companyId" id="companyId" cssClass="frmSelectClass">
											<form:options items="${companies}" itemValue="id" itemLabel="name"/>
										</form:select>
									</c:when>
									<c:otherwise>
										<form:hidden path="timeSheet.companyId" id="hdnCompanyId"/>
										<label id="lblCompany">${timesheetFormDto.timeSheet.company.name}</label>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><span id="companyError" class="error"></span></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="timeSheet.companyId" cssClass="error" id="companyErrorPath" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Division</td>
							<td class="value">
								<c:choose>
									<c:when test="${timesheetFormDto.timeSheet.id eq 0}">
										<form:select path="timeSheet.divisionId" id="divisionId" cssClass="frmSelectClass">
											<form:options items="${divisions}" itemValue="id" itemLabel="name"/>
										</form:select>
									</c:when>
									<c:otherwise>
										<form:hidden path="timeSheet.divisionId" id="hdnDivisionId"/>
										<label id="lblDivision">${timesheetFormDto.timeSheet.division.name}</label>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><span id="divisionError" class="error"></span></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="timeSheet.divisionId" cssClass="error" id="divisionErrorPath" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Month/Year</td>
							<td class="value">
								<select  id="slctMonth" class="frmSmallSelectClass" onchange="reloadData(); setSelectedYear(this.val); loadTimePeriodSchedules();">
									<c:forEach var="m" items="${months}">
										<option value="${m.month}">${m.name}</option>
									</c:forEach>
								</select>
								<select  id="slctYear" class="frmSmallSelectClass" onchange="reloadData(); setSelectedMonth(this.val);  loadTimePeriodSchedules();">
									<c:forEach var="y" items="${years}">
										<option value="${y}">${y}</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="timeSheet.payrollTimePeriodId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Time Period</td>
							<td class="value">
								<select class="frmSelectClass" id="slctTimePeriodSchedule" onchange="reloadData(); setCompContribAndTimePeriod();">
								</select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="timeSheet.payrollTimePeriodScheduleId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Compute Contribution</td>
							<td class="value">
								<input type="checkbox" id="cbComputeContribution" 
									readonly="readonly" onclick="return false;"/>
							</td>
						</tr>
						<tr>
							<td class="value" colspan="4"><span id="employeeTypeError" class="error"></span></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><input type="button" value="Download Template" id="btnDownloadTemplate" onclick="downloadTemplate();"/><td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Employee Daily Time Record</legend>
					<table class="formTable" >
						<tr>
							<td class="labels">* Upload Time Record</td>
							<td class="value">
								<form:input path="fileData" type="file" id="file" title="" style="display: none;"/>
								<input type="button" value="CHOOSE FILE" id="btnFile" onclick="browseFile();"/>
								<form:select path="timeSheet.biometricModelId" id="biometricModelId"
									cssClass="frmSmallSelectClass" items="${biometricModels}" itemLabel="modelName" itemValue="id"/>
								<input type="button" value="Upload" id="btnUpload" onclick="parseTimeSheet();"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<label for="file" id="lblFile"></label>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value" colspan="4"><form:errors path="fileName" id="fileNameErrorPath" cssClass="error"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value" colspan="4"><span id="fileNameError" class="error"></span></td>
						</tr>
						<tr>
							<td></td>
							<td class="value" colspan="4"><form:errors path="timeSheet.biometricModelId" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset id="fsTimeSheet" class="frmField_set">
					<legend>Timesheet and Adjustments</legend>
					<div id="divPayrollETimesheet">
						<%@ include file="PayrollEmployeeTimeSheet.jsp" %> 
					</div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="timeSheet.formWorkflowId" cssClass="error"/></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSaveTimeSheet"
							onclick="saveTimesheet();" value="Save" /></td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>