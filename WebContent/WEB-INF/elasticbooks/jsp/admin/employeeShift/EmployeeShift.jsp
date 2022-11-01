<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>
    <!--

		Description: Employee shift main page
     -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
var HAS_ERROR = false;
var isSaving = false;

$(function () {
	$("#txtName, #txtFirtStartShift, #txtFirstEndShift, #txtSecondStartShift, #txtSecondEndShift, #txtDailyWorkingHours, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchEmployeeShift();
			e.preventDefault();
		}
	});
});

function saveEmployeeShift(){
	if(isSaving == false && !HAS_ERROR) {
		isSaving = true;
		$("#btnSaveEmployeeShift").attr("disabled", "disabled");
		unformatBeforeSaving();
		doPostWithCallBack ("frmEmployeeShift", "divEmployeeShiftForm", function(data) {
			if (data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "employee shift: " + $("#name").val() + ".");
				$("#divEmployeeShiftForm").html("");
				searchEmployeeShift();
			} else {
				$("#divEmployeeShiftForm").html(data);
			}
			isSaving = false;
		});
	}
}

function cancelEmployeeShift(){
	$("#divEmployeeShiftForm").html("");
	searchEmployeeShift();
}

function addEmployeeShift() {
	$("#divEmployeeShiftForm").load(contextPath + "/admin/employeeShift/form", function() {
		$("html, body").animate({scrollTop: $("#divEmployeeShiftForm").offset().top}, 0050);
	});
}

function editEmployeeShift() {
	var id = getCheckedId("cbEmployeeShift");
	$("#divEmployeeShiftForm").load(contextPath + "/admin/employeeShift/form?pId="+id, function() {
		$("html, body").animate({scrollTop: $("#divEmployeeShiftForm").offset().top}, 0050);
	});
}

function getCommonParam() {
	var name = encodeURIComponent($.trim($("#txtName").val()));
	var firstHalfShiftStart = encodeURIComponent($.trim($("#txtFirtStartShift").val()));
	var secondHalfShiftEnd = encodeURIComponent($.trim($("#txtSecondEndShift").val()));
	var dailyWorkingHours = encodeURIComponent($.trim($("#txtDailyWorkingHours").val()));
	var status = $.trim($("#selectStatus").val());
	return "?name="+name
			+"&firstHalfShiftStart="+firstHalfShiftStart
			+"&secondHalfShiftEnd="+secondHalfShiftEnd
			+"&dailyWorkingHours="+dailyWorkingHours
			+"&status="+status;
}

function searchEmployeeShift() {
	doSearch ("divEmployeeShiftTbl", "/admin/employeeShift/search"+getCommonParam()+"&pageNumber=1");
}

function formatNumeric (textbox, isDouble) {
	$("#"+textbox).val(accounting.formatMoney($("#"+textbox).val()));
}

function unformatBeforeSaving() {
	$("#allowableBreak").val(accounting.unformat($("#allowableBreak").val()));
	$("#lateMultiplier").val(accounting.unformat($("#lateMultiplier").val()));
	$("#weekendMultiplier").val(accounting.unformat($("#weekendMultiplier").val()));
	$("#holidayMultiplier").val(accounting.unformat($("#holidayMultiplier").val()));
	$("#dailyWorkingHours").val(accounting.unformat($("#dailyWorkingHours").val()));
}
</script>
</head>
<body>
<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td class="title">Name</td>
				<td class="value"><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td class="title">Start Shift</td>
				<td class="value"><input type="text" id="txtFirtStartShift" class="inputSmall"></td>
			</tr>
			<tr>
				<td class="title">End Shift</td>
				<td class="value"><input type="text" id="txtSecondEndShift" class="inputSmall"></td>
			</tr>
			<tr>
				<td class="title">Daily Working Hours</td>
				<td class="value"><input type="text" id="txtDailyWorkingHours" class="inputSmall"></td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td class="value"><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchEmployeeShift();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divEmployeeShiftTbl">
		<%@ include file="EmployeeShiftTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddEmployeeShift" value="Add" onclick="addEmployeeShift();"></input>
		<input type="button" id="btnEditEmployeeShift" value="Edit" onclick="editEmployeeShift();"></input>
	</div>
	<br>
	<br>
	<div id="divEmployeeShiftForm" style="margin-top: 20px;">
	</div>
</body>
</html>