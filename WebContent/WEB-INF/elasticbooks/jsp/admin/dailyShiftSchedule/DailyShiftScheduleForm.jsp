<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

       Description: Daily Shift Schedule Form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	if ("${dailyShiftSchedule.id}" != 0) {
		$("#slctMonth").val("${dailyShiftSchedule.payrollTimePeriod.month}");
		$("#slctYear").val("${dailyShiftSchedule.payrollTimePeriod.year}");
	}
	loadTimePeriodSchedules("${dailyShiftSchedule.payrollTimePeriodScheduleId}");
});

function loadTimePeriodSchedules(payrollTimePeriodScheduleId) {
	$("#slctTimePeriodSchedule").empty();
	var month = $("#slctMonth").val();
	var year = $("#slctYear").val();
	var uri = contextPath + "/admin/dailyShiftSchedule/getTPSchedules?month="+month+"&year="+year;
	if (payrollTimePeriodScheduleId != null && payrollTimePeriodScheduleId != undefined
			&& payrollTimePeriodScheduleId != "") {
		uri += "&payrollTimePeriodScheduleId="+payrollTimePeriodScheduleId;
	}
	var optionParser = {
			getValue: function (rowObject){
				if (rowObject != null) {
					return rowObject["id"];
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
					if($.inArray(this.value, found) != -1) {
						$(this).remove();
					}
					found.push(this.value);
				});
				if (payrollTimePeriodScheduleId != null && payrollTimePeriodScheduleId != undefined
						&& payrollTimePeriodScheduleId != "") {
					$("#slctTimePeriodSchedule").val(payrollTimePeriodScheduleId);
				}
			}
	};
	loadPopulate (uri, false, "", "slctTimePeriodSchedule", optionParser, postHandler);
}

function clearTimeSheetTable() {
	$("#divDailyShiftSchedTbl").load(contextPath +"/admin/dailyShiftSchedule/reloadTable");
}

var isSaving = false;
function saveEmployeeDetails() {
	var slctTimePeriodSchedId = $("#slctTimePeriodSchedule").val();
	if (slctTimePeriodSchedId != null && $.trim(slctTimePeriodSchedId) != "") {
		var timeSchedule = buidlJsonTimeSchedule();
		$("#employeeTimeScheduleJson").val(timeSchedule);
		var payrollTimePeriodScheduleId = $("#slctTimePeriodSchedule").val();
		var month = $("#slctMonth").val();
		var year = $("#slctYear").val();
		$("#payrollTimePeriodScheduleId").val(slctTimePeriodSchedId.split("-")[0]);
		if(isSaving == false) {
			isSaving = true;
			$("#btnSaveEmployee").attr("disabled", "disabled");
			doPostWithCallBack ("dailyShiftScheduleForm", "divPDailyShiftScheduleForm", function(data) {
				if (data.startsWith("saved")) {
					$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "daily shift schedule.");
					$("#dailyShiftScheduleForm").html("");
					searchDailyShift();
				} else {
					var tblETimeSheet = $("#tblETimeSheet").html();
					$("#divPDailyShiftScheduleForm").html(data);
					$("#slctYear").val(year);
					$("#slctMonth").val(month);
					loadTimePeriodSchedules(payrollTimePeriodScheduleId);
					$("#tblETimeSheet").html(tblETimeSheet);
				}
				isSaving = false;
			});
		}
	} else {
		$("#timePeriodErr").text("No payroll time period shedule for the selected Month/Year.");
	}
}

function downloadTemplate() {
	var companyId = $("#companyId").val();
	var timePeriodSchedId = $("#slctTimePeriodSchedule").val().split("-")[0];
	window.open(contextPath + "/admin/dailyShiftSchedule/dailyShiftScheduleTemplate.csv"
		+"?payrollTimePeriodScheduleId="+timePeriodSchedId+"&companyId="+companyId);
}

function browseFile() {
	$("#file").val("");
	$("#file").trigger("click");
}

$(".fileLink").on('click', function() {
	convBase64ToFile($("#hdnFile").val(), $.trim($("#lblFile").text()));
});

function dataURLtoFile(dataurl, filename) {
	var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
		bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
	while(n--){
		u8arr[n] = bstr.charCodeAt(n);
	}
	return new File([u8arr], filename, {type:mime});
}

function uploadSchedule() {
	$("#divDailyShiftSchedTbl").html("");
	var fileName = $.trim($("#lblFile").text());
	var companyId = $("#companyId").val();
	var timePeriodSchedId = $("#slctTimePeriodSchedule").val().split("-")[0];
	if (fileName != "") {
		var formData = new FormData();
		var file = dataURLtoFile($("#hdnFile").val(), fileName);
		formData.append("file", file);
		$.ajax({
			url: contextPath +"/admin/dailyShiftSchedule/generateShiftSched?"
				+"payrollTimePeriodScheduleId="+timePeriodSchedId+"&companyId="+companyId,
			type: "POST",
			data: formData,
			dataType: "text",
			processData: false,
			contentType: false,
			success: function(data) {
				$("#divDailyShiftSchedTbl").html(data);
			},
			error: function(jqXHR, textStatus, errorMessage) {
				console.log(errorMessage); // Optional
			}
		});
	} else {
		$("#divDailyShiftSchedTbl").load(contextPath +"/admin/dailyShiftSchedule/generateEmployeeSheet?"
				+"payrollTimePeriodScheduleId="+timePeriodSchedId+"&companyId="+companyId+"&isReload=false");
	}
}

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

$("#file").change(function () {
	var value = $.trim($(this).val());
	if (value != "") {
		var fileNames = value.split("\\");
		var fileName = $.trim(fileNames.slice(-1)[0]);
		$("#lblFile").text(fileName);
		$("#fileName").val(fileName);
		$("#description").val(fileName);
		$("#hdnFileSize").val(this.files[0].size);
		convertDocToBase64($(this));
	}
});

function browseFile() {
	$("#file").val("");
	$("#file").trigger("click");
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

</script>
</head>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="dailyShiftSchedule" id="dailyShiftScheduleForm">
			<div class="modFormLabel">Daily Shift Schedule</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<form:hidden path="ebObjectId"/>
						<form:hidden path="payrollTimePeriodScheduleId" id="payrollTimePeriodScheduleId"/>
						<form:hidden path="employeeTimeScheduleJson" id="employeeTimeScheduleJson"/>
						<form:hidden path="referenceDocument.file" id="hdnFile"/>
						<form:hidden path="referenceDocument.fileName" id="fileName"/>
						<form:hidden path="referenceDocument.description" id="description"/>
						<form:hidden path="referenceDocument.fileSize" id="hdnFileSize"/>
						<tr>
							<td class="labels" >* Company</td>
							<td class="value">
								<form:select path="companyId" cssClass="frmSelectClass" id="companyId"
									items="${companies}" itemLabel="name" itemValue="id" />
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Month/Year </td>
							<td class="value">
								<form:select path="month" cssClass="frmSmallSelectClass" id="slctMonth"
									onchange="loadTimePeriodSchedules(); clearTimeSheetTable();"
									items="${months}" itemValue="month" itemLabel="name"/>
								<form:select path="year" cssStyle="width: 174px;" cssClass="frmSmallSelectClass"
									id="slctYear" onchange="loadTimePeriodSchedules(); clearTimeSheetTable();"
									items="${years}"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="payrollTimePeriodId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Time Period</td>
							<td class="value">
								<select class="frmSelectClass" id="slctTimePeriodSchedule"
									onchange="clearTimeSheetTable();"></select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<span id="timePeriodErr" class="error"></span>
								<form:errors path="payrollTimePeriodScheduleId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><input type="button" value="Download Template"
								id="btnDownloadTemplate" onclick="downloadTemplate();"/><td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
							<input type="file" id="file" style="display: none;"/>
							<input type="button" value="CHOOSE FILE" id="btnFile" onclick="browseFile();"/></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value" >
								<a href="#" class='fileLink'>
									<span id="lblFile" style="color: blue;">
										${dailyShiftSchedule.referenceDocument.fileName}
									</span>
								</a>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="right">
								<input type="button" id="btnGenerate" value="Generate" onclick="uploadSchedule();"/>
							</td>
						</tr>
					</table>
					<br>
					<div id="divDailyShiftSchedTbl" >
						<%@ include file="EmployeeDailyShiftSchedule.jsp" %>
					</div>
				</fieldset>
				<br>
				<table class="formDiv">
					<tr>
						<td colspan="2" align="right">
							<input type="button" id="btnSavePtp" value="${dailyShiftSchedule.id eq 0 ? 'Save' : 'Update'}" onclick="saveEmployeeDetails()"/>
							<input type="button" id="btnCancelPtp" value="Cancel" onclick="cancelPTimePeriod();"/>
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>