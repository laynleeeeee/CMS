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
<style>
.outerTbl {
  position: relative;
  padding: 0px;
  width: inherit;
}

.innerTbl {
  overflow-x:scroll;
  width: 100%;
}
</style>
<script type="text/javascript">
var selectTPSched = "";
var selectedMonth = "";
var selectedYear = "";
var dateFrom = "";
var dateTo = "";
var $payrollReferenceWindow = null;
var currentTimeSheetId = null;
$(document).ready(function () {
	if ("${payrollDto.payroll.id}" != "0") {
		$("#spanCompany").text("${payrollDto.payroll.company.name}");
		$("#spanDivision").text("${payrollDto.payroll.division.name}");
		$("#spanMonthYear").text(getEquivMonth(Number("${payrollDto.payroll.payrollTimePeriod.month}")) + " " + "${payrollDto.payroll.payrollTimePeriod.year}");
		$("#spanTimePeriod").text("${payrollDto.payroll.payrollTimePeriodSchedule.name}");
		$("#cbComputeContribution").prop("checked", "${payrollDto.payroll.payrollTimePeriodSchedule.computeContributions}");
		$("#divPayrollETimesheet").load(contextPath + "/timesheet/tableView?timeSheetId="+"${payrollDto.payroll.timeSheetId}");
	}
	if ("${payrollDto.payroll.formWorkflow.currentStatusId}" == 3
			|| "${payrollDto.payroll.formWorkflow.currentStatusId}" == 4) {
		$("#btnSavePayroll").attr("disabled", "disabled");
	}
});


function getDates() {
	var uri =  contextPath + "/dssdPayroll/getDates?payrollTimePeriodScheduleId="+
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

function loadDtr() {
	var url = "/timesheet/reference";
	$payrollReferenceWindow = window.open(contextPath + url,"popup","width=1000,height=500,scrollbars=1,resizable=no," +
	"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
}

function formatMoney($field){
	$($field).val(accounting.formatMoney($($field).val()));
}

function validateBeforeParse(){
	clearError();
	var hasError = false;
	if($.trim($("#companyId").val()) == "" && $.trim($("#companyErrorPath").text()) == ""){
		$("#companyError").text("Company is a required field.");
		hasError = true;
	}
	if($.trim($("#employeeTypeId").val()) == "" && $.trim($("#employeeTypeErrorPath").text()) == ""){
		$("#employeeTypeError").text("Employee type is a required field.");
		hasError = true;
	}
	if($.trim($("#lblFile").text()) == "" && $.trim($("#fileNameErrorPath").text()) == ""){
		$("#fileNameError").text("File is a required field.");
		hasError = true;
	}
	return hasError;
}

function clearError(){
	$("#companyError").text("");
	$("#employeeTypeError").text("");
	$("#datefromError").text("");
	$("#dateToError").text("");
}

function buildJsonSalary(){
	var json = "[";
	var maxLength = $("#tblESalary tbody tr").length - 1;
	$("#tblESalary tbody tr").each(function (i) {
		json += "{";
		$(this).find("td").each(function () {
			var tdCls = $(this).attr("class");
			if (tdCls == "tdEmployeeNo") {
				json +=  '"employeeNo"' + ":" + '"' + $.trim($(this).text()) + '"' + ",";
			} else if (tdCls == "tdEmployeeName") {
				json +=  '"employeeName"' + ":" + '"' + $.trim($(this).text()) + '"' + ",";
			} else if (tdCls == "tdEmployeeStatus") {
				json +=  '"employeeStatus"' + ":" + '"' + $.trim($(this).text()) + '"' + ",";
			} else if (tdCls == "tdBasicPay") {
				var basicPay = $.trim($(this).text());
				json +=  '"basicPay"' + ":" + accounting.unformat(basicPay) + ",";
			} else if (tdCls == "tdPaidLeave") {
				json +=  '"paidLeave"' + ":" + accounting.unformat($.trim($(this).text())) + ",";
			} else if (tdCls == "tdCola") {
				json +=  '"cola"' + ":" +  accounting.unformat($.trim($(this).text())) + ",";
			} else if (tdCls == "tdBonus") {
				var bonus = $(this).find("#txtBonus").val();
				json +=  '"bonus"' + ":" + accounting.unformat($.trim(bonus)) + ",";
			} else if (tdCls == "tdSundayHoliday") {
				json +=  '"sundayHolidayPay"' + ":" + accounting.unformat($.trim($(this).text())) + ",";
			} else if (tdCls == "tdOvertime") {
				json +=  '"overtime"' + ":" + accounting.unformat($.trim($(this).text())) + ",";
			} else if (tdCls == "tdGrossPay") {
				json +=  '"grossPay"' + ":" + accounting.unformat($.trim($(this).text())) + ",";
			} else if (tdCls == "tdDeduction") {
				json +=  '"deduction"' + ":" + accounting.unformat($.trim($(this).text())) + ",";
			} else if (tdCls == "tdBreakage") {
				var breakageWastage = $(this).find("input").val();
				json +=  '"breakageWastage"' + ":" + accounting.unformat(breakageWastage) + ","; 
			} else if (tdCls == "tdCashAdvance") {
				var cashAdvance = $(this).find("input").val();
				json +=  '"cashAdvance"' + ":" + accounting.unformat(cashAdvance) + ",";  
			} else if (tdCls == "tdLateAbsent") {
				json +=  '"lateAbsent"' + ":" + accounting.unformat($.trim($(this).text())) + ",";
			} else if (tdCls == "tdWTax") {
					var wTax = $(this).find("#txtWTax").val();
					json +=  '"withholdingTax"' + ":" + accounting.unformat(wTax) + ",";
			} else if (tdCls == "tdSss") {
				json +=  '"sss"' + ":" + accounting.unformat($.trim($(this).text())) + ",";
				var sssEr = $(this).find("#hdnSssEr").val();
				json +=  '"sssEr"' + ":" + accounting.unformat(sssEr) + ",";
				var sssEc = $(this).find("#hdnSssEc").val();
				json +=  '"sssEc"' + ":" + accounting.unformat(sssEc) + ",";
			} else if (tdCls == "tdPhilHealth") {
				json +=  '"philHealth"' + ":" +  accounting.unformat($.trim($(this).text())) + ",";
				var philHealthEr = $(this).find("#hdnPhicEr").val();
				json +=  '"philHealthEr"' + ":" + accounting.unformat(philHealthEr) + ",";
			} else if (tdCls == "tdPagIbig") {
				json +=  '"pagibig"' + ":" + accounting.unformat($.trim($(this).text())) + ",";
				var pagibigEr = $(this).find("#hdnPagibigEr").val();
				json +=  '"pagibigEr"' + ":" + accounting.unformat(pagibigEr) + ",";
			} else if (tdCls == "tdSssLoan") {
				var sssLoan = $(this).find("#txtSssLoan").val();
				json +=  '"sssLoan"' + ":" +  accounting.unformat($.trim(sssLoan)) + ",";
			}  else if (tdCls == "tdPagibigLoan") {
				var pagibigLoan = $(this).find("#txtPagibigLoan").val();
				json +=  '"pagibigLoan"' + ":" +  accounting.unformat($.trim(pagibigLoan)) + ",";
			} else if (tdCls == "tdNetpay") {
				json +=  '"netPay"' + ":" + accounting.unformat($.trim($(this).text()));
			}
		});
		json += "}";
		if (i < maxLength) {
			json += ",";
		}
	});
	json += "]";
	return json;
}

function buildEDeductionJson(){
	var maxLength = $(".tblDeductions").length - 1;
	var json = '';
	$(".tblDeductions").each(function (i) {
		var employeeId = $(this).attr("id").split("-")[1];

		$(this).find(".hdnDeductionTypeIds" + employeeId).each(function(j) {
			var deductionTypeId = $(this).val();
			var amount = $(this).parent("td").parent("tr").closest("tr").find(".hdnAmounts").val();
			var fromDeductionForm = $(this).parent("td").find(".hdnFromDeductionForms").val();
			if (deductionTypeId != "" && (typeof amount != "undefined")) {
				json += "{";
				json += '"employeeId"' + ":" + employeeId + "," + '"deductionTypeId"' + ":" + deductionTypeId + "," 
					+ '"fromDeductionForm"' + ":" + fromDeductionForm + ","
					+ '"amount"' + ":" + accounting.unformat(amount);
				json += "},";
			}
		});

		$(this).find(".selDeductionTypes" + employeeId).each(function(j) {
			var deductionTypeId = $(this).val();
			var amount = $(this).parent("td").parent("tr").closest("tr").find(".txtAmounts").val();
			var fromDeductionForm = $(this).parent("td").find(".hdnFromDeductionForms").val();
			if (deductionTypeId != "" && (typeof amount != "undefined")) {
				json += "{";
				json += '"employeeId"' + ":" + employeeId + "," + '"deductionTypeId"' + ":" + deductionTypeId + "," 
					+ '"fromDeductionForm"' + ":" + fromDeductionForm + ","
					+ '"amount"' + ":" + accounting.unformat(amount);
				json += "},";
			}
		});
	});
	json = json.slice(",", -1);
	json = "[" + json + "]";
	return json;
}

var isSaving = false;
function savePayroll() {
	$("#dateFrom").val(dateFrom);
	$("#dateTo").val(dateTo);
	var salaryJson = buildJsonSalary();
	$("#employeeSalaryDTOJson").val(salaryJson);
	var employeeDeductionJson =  buildEDeductionJson();
	$("#employeeDeductionJson").val(employeeDeductionJson);

	if(isSaving == false){
		clearError();
		isSaving = true;
		$("#btnSavePayroll").attr("disabled", "disabled");
		doPostWithCallBack ("payrollForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				$("#form").html("");
				isSaving = false;
			} else {
				var month = $("#slctMonth").val();
				var year =  $("#slctYear").val();
				if("${payrollDto.payroll.id}" > 0) {
					dojo.byId("editForm").innerHTML = data;
				} else {
					dojo.byId("form").innerHTML = data;
				}
				$("#form").html(data);
				$("#slctMonth").val(month);
				$("#slctYear").val(year);
				$("#slctTimePeriodSchedule").val("${payroll.selectTPSched}");
				isSaving = false;
			}
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
}

function updateHeader (payroll) {
	$("#hdnCompanyId").val(payroll.companyId);
	$("#hdnDivisionId").val(payroll.divisionId);
	$("#hdnTimeSheetId").val(payroll.timeSheetId);
	$("#hdnPayrollTimePeriodId").val(payroll.payrollTimePeriodId);
	$("#hdnPayrollTimePeriodScheduleId").val(payroll.payrollTimePeriodScheduleId);
	$("#spanCompany").text(payroll.company.name);
	$("#spanDivision").text(payroll.division.name);
	$("#spanMonthYear").text(getEquivMonth(payroll.payrollTimePeriod.month) + " " + payroll.payrollTimePeriod.year);
	$("#spanTimePeriod").text(payroll.payrollTimePeriodSchedule.name);
	$("#cbComputeContribution").prop("checked", payroll.payrollTimePeriodSchedule.computeContributions);
}

function getEquivMonth (numMonth) {
	switch(numMonth) {
		case 1: return "JANUARY";
		case 2: return "FEBRUARY";
		case 3: return "MARCH";
		case 4: return "APRIL";
		case 5: return "MAY";
		case 6: return "JUNE";
		case 7: return "JULY";
		case 8: return "AUGUST";
		case 9: return "SEPTEMBER";
		case 10: return "OCTOBER";
		case 11: return "NOVEMBER";
		case 12: return "DECEMBER";
	}
}

function updateTimesheetData(timeSheetId) {
	$("#divPayrollETimesheet").load(contextPath + "/timesheet/tableView?timeSheetId="+timeSheetId);
}

function loadTSReference (timeSheetId) {
	currentTimeSheetId = timeSheetId;
	$.ajax({
		url: contextPath + "/payroll/loadTimesheet?timeSheetId="+timeSheetId,
		success : function(payroll) {
			payrollObj = payroll;
			updateHeader(payroll);
			updateTimesheetData(timeSheetId);
			$("#btnGenerateDeduction").removeAttr("disabled");
		},
		error : function(error) {
			$("#btnGenerateDeduction").attr("disabled", "disabled");
			console.log(error);
		},
		dataType: "json"
	});
	if ($payrollReferenceWindow != null)
		$payrollReferenceWindow.close();
	$payrollReferenceWindow = null;
}

function generateDeduction() {
	$("#divEmployeeDeduction").load(contextPath + "/payroll/generateDeduction?timeSheetId="+currentTimeSheetId);
}

</script>
</head>
<body>
	<div class="formDivBigForms">
		<form:form method="POST" commandName="payrollDto" id="payrollForm">
			<form:hidden path="payroll.id" id="hdnPayrollId"/>
			<form:hidden path="payroll.companyId" id="hdnCompanyId" />
			<form:hidden path="payroll.divisionId" id="hdnDivisionId" />
			<form:hidden path="payroll.formWorkflowId" />
			<form:hidden path="payroll.sequenceNumber"/>
			<form:hidden path="payroll.createdBy"/>
			<form:hidden path="payroll.timeSheetDtoJson" id="timeSheetDtoJson"/>
			<form:hidden path="payroll.employeeSalaryDTOJson" id="employeeSalaryDTOJson"/>
			<form:hidden path="payroll.employeeTypeId" id="employeeTypeId"/>
			<form:hidden path="payroll.timeSheetId" id="hdnTimeSheetId"/>
			<form:hidden path="payroll.payrollTimePeriodId" id="hdnPayrollTimePeriodId"/>
			<form:hidden path="payroll.payrollTimePeriodScheduleId" id="hdnPayrollTimePeriodScheduleId"/>
			<form:hidden path="employeeDeductionJson" id="employeeDeductionJson"/>
			<form:hidden path="payroll.ebObjectId"/>
			<div class="modFormLabel">
				Payroll <span class="btnClose" id="btnClose">[X]</span>
			</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number</td>
							<td class="value">
								<c:choose>
									<c:when test="${payrollDto.payroll.id == 0 }">
										<input type="text" id="txtSequenceNo"
											class="textBoxLabel" readonly="readonly" />
									</c:when>
									<c:otherwise>
										${payrollDto.payroll.sequenceNumber}
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status"
								value="${payrollDto.payroll.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text" id="txtPayrollStatus"
								class="textBoxLabel" readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Employee Daily Time Record</legend>
					<table>
						<tr>
							<td class="value"><input type="button" id="btnLoadDTR" onclick="loadDtr();" value="Load DTR"/></td>
						</tr>
					</table>
				</fieldset>

				<fieldset class="frmField_set">
					<legend>Payroll Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Date</td>
							<td class="value"><form:input path="payroll.date"
									id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate1"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payroll.date"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">Company</td>
							<td class="value">
								<span id="spanCompany"></span>
							</td>
						</tr>
						<tr>
							<td class="labels">Division</td>
							<td class="value">
								<span id="spanDivision"></span>
							</td>
						</tr>
						<tr>
							<td class="labels">Month/Year</td>
							<td class="value">
								<span id="spanMonthYear"></span>
							</td>
						</tr>
						<tr>
							<td class="labels">Time Period</td>
							<td class="value">
								<span id="spanTimePeriod"></span>
							</td>
						</tr>
						<tr>
							<td class="labels">Compute Contribution</td>
							<td class="value">
								<input type="checkbox" id="cbComputeContribution" 
									readonly="readonly" onclick="return false;"/>
							</td>
						</tr>
					</table>
				</fieldset> 
				<div id="divPayrollETimesheet"></div>
				<fieldset class="frmField_set">
				<legend>Other Deduction</legend>
					<table class="formTable" width="100%">
						<tr>
							<td class="value">
								<input type="button" id="btnGenerateDeduction" onclick="generateDeduction();" value="Generate" 
									disabled="disabled"/>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<div id="divEmployeeDeduction">
									<%@ include file="PayrollEmployeeDeduction.jsp" %>
								</div>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set" style="min-width: 89%;">
					<legend>Payroll Record</legend>
					<div id="divPayrollESalary">
						<%@ include file="PayrollEmployeeSalary.jsp" %>
					</div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="payroll.formWorkflowId" cssClass="error"/></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSavePayroll"
							onclick="savePayroll();" value="Save" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>