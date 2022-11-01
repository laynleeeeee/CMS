<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

       Description: Payroll Time Period Form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
var $timeSheetTable = null;
$(document).ready(function() {
	initializeTable();
	if("${payrollTimePeriod.id}" > 0) {
		reformatDates();
	}
});

function initializeTable() {
	var timeSheetJson = JSON.parse($("#payrollTimeScheduleJsonId").val());
	var cPath = "${pageContext.request.contextPath}";
	$timeSheetTable = $("#timeSheetTable").editableItem({
		data: timeSheetJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "payrollTimePeriodId", "varType" : "string"},
				{"name" : "name", "varType" : "string"},
				{"name" : "dateFrom", "varType" : "date"},
				{"name" : "dateTo", "varType" : "date"},
				{"name" : "computeContributions", "varType" : "boolean"}
		],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "payrollTimePeriodId",
				"cls" : "payrollTimePeriodId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Name",
				"cls" : "name tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "35%"},
			{"title" : "Start Date",
				"cls" : "dateFrom tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "25%" },
			{"title" : "End Date",
				"cls" : "dateTo tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "25%" },
			{"title" : "",
				"cls" : "computeContributions",
				"editor" : "hidden",
				"visible" : false,
				"width" : "25%" },
			{"title" : "Contributions",
				"cls" : "radioButton tblInputText",
				"editor" : "radioButton",
				"visible" : true,
				"width" : "20%" }
	    ],
	    "disableDuplicateStockCode" : false,
        "itemTableMessage": ""
	});

	$("#timeSheetTable").on("blur", ".dateFrom", function(){
		evaluateDate(this, true);
	});

	$("#timeSheetTable").on("blur", ".dateTo", function(){
		evaluateDate(this, true);
	});

	$(".computeContributions").each(function () {
		if ($(this).val() == "true") {
			$(this).closest("tr").find(".radioButton").prop("checked", true);
		}
	});

	$("#timeSheetTable").on("click", ".radioButton", function(){
		setContributions();
	});
}

function evaluateDate($textbox, isSetDefault) {
	var input = $($textbox).val();
	var datesplit = input.split("/");
	if (input.length <= 10) {
		if(datesplit.length == 3) {
			if (!IsValidDate(datesplit[0], datesplit[1], datesplit[2])) {
				handleEvaluatedDate($textbox, isSetDefault);
			}
		} else {
			handleEvaluatedDate($textbox, isSetDefault);
		}
	} else {
		handleEvaluatedDate($textbox, isSetDefault);
	}
}

function handleEvaluatedDate($textbox, isSetDefault) {
	if ($($textbox).val() != ""){
		alert("Date should be in mm/dd/yyyy format.");
		$($textbox).val("");
		$($textbox).focus();
	}
}

</script>
</head>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="payrollTimePeriod" id="payrollTimePeriodForm">
			<div class="modFormLabel">Payroll Time Period</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<form:hidden path="payrollTimeScheduleJson" id="payrollTimeScheduleJsonId"/>
						<tr>
							<td class="labels" >* Name </td>
							<td class="value">
								<form:input path="name" cssClass="frmInput" id="txtNameId" />
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="name" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">* Month/Year </td>
							<td class="value">
								<form:select path="month" cssClass="frmSmallSelectClass" id="slctMonth">
									<form:options items="${months}" itemValue="month" itemLabel="name"/>
								</form:select>
								<form:select path="year" cssClass="frmSmallSelectClass" id="slctYear">
									<form:options items="${years}"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="month" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value"><form:checkbox path="active"/></td>
						</tr>
					</table>
					<br>
					<div id="timeSheetTable"></div>
					<form:errors path="payrollTimePeriodSchedules" cssClass="error" />
				</fieldset>
				<br>
				<table class="formDiv">
					<tr>
						<td colspan="2" align="right">
							<input type="button" id="btnSavePtp" value="${payrollTimePeriod.id eq 0 ? 'Save' : 'Update'}" onclick="savePTimePeriod();"/>
							<input type="button" id="btnCancelPtp" value="Cancel" onclick="cancelPTimePeriod();"/>
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>