<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

           Description: Monthly Shift Schedule Form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
var selectTPSched = "";
$(document).ready(function () {
	var id = "${monthlyShiftSchedule.id}";
	var payrollTimePeriodScheduleId = "${monthlyShiftSchedule.payrollTimePeriodScheduleId}";
	if(id != 0){
		$("#slctMonth").val("${monthlyShiftSchedule.payrollTimePeriod.month}");
		$("#slctYear").val("${monthlyShiftSchedule.payrollTimePeriod.year}");
	}
	loadTimePeriodSchedules(payrollTimePeriodScheduleId);
	setErrMargin();
});

function loadTimePeriodSchedules(payrollTimePeriodScheduleId) {
	$("#slctTimePeriodSchedule").empty();
	var month = $("#slctMonth").val();
	var year = $("#slctYear").val();
	var uri = contextPath + "/admin/monthlyShiftSchedule/getTPSchedules?month="+month+"&year="+year;
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
				if (payrollTimePeriodScheduleId != "" && payrollTimePeriodScheduleId != null) {
					$("#slctTimePeriodSchedule").val(payrollTimePeriodScheduleId);
				}
			}
	};
	loadPopulate (uri, false, selectTPSched, "slctTimePeriodSchedule", optionParser, postHandler);
}

function generateEmployeeSheets (){
	if($("#companyId").val() == null){
		$("#spanCompanyErrMessage").text("Company is a required field.");
	} else {
		$("#spanCompanyErrMessage").text("");
		validateTimePeriod($("#slctTimePeriodSchedule").val());
		var uri =  contextPath +"/admin/monthlyShiftSchedule/generateEmployeeSheet?payrollTimePeriodScheduleId="+
		$("#slctTimePeriodSchedule").val().split("-")[0]+"&companyId="+$("#companyId").val()+"&isReload="+false;
		$("#divTimeSheetTable").load(uri);
	}
}

function clearTimeSheetTable (){
	var uri =  contextPath +"/admin/monthlyShiftSchedule/generateEmployeeSheet?isReload="+true;
	$("#divTimeSheetTable").load(uri);
}

var isSaving = false;
function saveEmployeeDetails() {
	var timeSchedule = buidlJsonEmployeeSchedule ();
	$("#employeeShiftScheduleJson").val(timeSchedule);
	var payrollTimePeriodScheduleId = $("#slctTimePeriodSchedule").val();
	var month = $("#slctMonth").val();
	var year = $("#slctYear").val();
	if(payrollTimePeriodScheduleId != null){
		$("#payrollTimePeriodScheduleId").val($("#slctTimePeriodSchedule").val().split("-")[0]);
	}
	if(isSaving == false) {
		isSaving = true;
		$("#btnSavePtp").attr("disabled", "disabled");
		doPostWithCallBack ("monthlyShiftScheduleForm", "divPMonthlyShiftScheduleForm", function(data) {
			if (data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "Monthly Shift Schedule ");
				$("#divPMonthlyShiftScheduleForm").html("");
				searchDailyShift();
			} else {
				$("#divPMonthlyShiftScheduleForm").html(data);
				$("#slctYear").val(year);
				$("#slctMonth").val(month);
				$("#slctTimePeriodSchedule").val(payrollTimePeriodScheduleId);
				loadTimePeriodSchedules(payrollTimePeriodScheduleId);
			}
			isSaving = false;
		});
	}
}

function validateTimePeriod(payrollTimePeriodScheduleId){
	if(payrollTimePeriodScheduleId == null){
		$("#payrollTimePeriodScheduleIdErr").css("display", "none");
		$("#spanErrorMessage").text("Time period is a required field.");
	} else {
		$("#spanErrorMessage").text("");
		$("#payrollTimePeriodScheduleIdErr").css("display", "none");
	}
}

$(window).resize(function() {
	setErrMargin();
});

function setErrMargin(){
	var empShiftIdWidth = $("#empShiftId").css("margin-left");
	$("#monthlyShiftScheduleDtos").css("margin-left",empShiftIdWidth);
}

</script>
</head>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="monthlyShiftSchedule" id="monthlyShiftScheduleForm">
			<div class="modFormLabel">Monthly Shift Schedule</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<form:hidden path="payrollTimePeriodScheduleId" id="payrollTimePeriodScheduleId"/>
						<form:hidden path="employeeShiftScheduleJson" id="employeeShiftScheduleJson"/>
						<tr>
							<td class="labels" >* Company </td>
							<td class="value">
								<form:select path="companyId" cssClass="frmSelectClass" id="companyId"
									items="${companies}" itemLabel="name" itemValue="id" onchange="clearTimeSheetTable ();"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error"/>
								<span id="spanCompanyErrMessage" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels">* Month/Year </td>
							<td class="value">
								<form:select path="month" cssClass="frmSmallSelectClass" id="slctMonth" onchange="loadTimePeriodSchedules(); clearTimeSheetTable();"
									items="${months}" itemValue="month" itemLabel="name"/>
								<form:select path="year" cssClass="frmSmallSelectClass" id="slctYear" onchange="loadTimePeriodSchedules(); clearTimeSheetTable();"
									items="${years}"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="payrollTimePeriodId" cssClass="error" style="margin-left: 12px;" /></td>
						</tr>

						<tr>
							<td class="labels">* Time Period</td>
							<td class="value">
								<select class="frmSelectClass" id="slctTimePeriodSchedule" onchange="clearTimeSheetTable();">
								</select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="payrollTimePeriodScheduleId" id="payrollTimePeriodScheduleIdErr" cssClass="error" style="margin-left: 12px;" />
								<span id="spanErrorMessage" class="error" style="margin-left: 12px;"></span>
							</td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value"><form:checkbox path="active"/> </td>
						</tr>
						<tr>
							<td colspan="2" align="right">
								<input type="button" id="btnGenerate" value="Generate" onclick="generateEmployeeSheets();"/>
							</td>
						</tr>
					</table>
					<br>
					<div id="divTimeSheetTable" >
						<%@ include file="EmployeeShiftSchedule.jsp" %> 
					</div>
					<table class="formTable">
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="monthlyShiftScheduleDtos" id="monthlyShiftScheduleDtos" cssClass="error" />
							</td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="formDiv">
					<tr>
						<td colspan="2" align="right">
							<input type="button" id="btnSavePtp" value="${monthlyShiftSchedule.id eq 0 ? 'Save' : 'Update'}" onclick="saveEmployeeDetails()"/>
							<input type="button" id="btnCancelPtp" value="Cancel" onclick="cancelPTimePeriod();"/>
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>