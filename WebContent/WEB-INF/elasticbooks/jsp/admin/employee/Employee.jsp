<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		 Description: Employee admin setting main page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
</head>
<script type="text/javascript">
$(function () {
	$("#txtName, #txtPosition, #slctDivision").bind("keypress", function (e) {
		if (e.which == 13) {
			searchEmployee();
			e.preventDefault();
		}
	});
});

function addEmployee() {
	$("#divEmployeeForm").load(contextPath + "/admin/employee/form");
	$("html, body").animate({scrollTop: $("#divEmployeeForm").offset().top}, 0050);
}

function editEmployee() {
	var id = getCheckedId("cbEmployee");
	$("#divEmployeeForm").load(contextPath + "/admin/employee/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divEmployeeForm").offset().top}, 0050);
}

function cancelEmployee() {
	$("#divEmployeeForm").html("");
	$("html, body").animate({scrollTop: $("#divEmployeeTbl").offset().top}, 0050);
}

var isSaving = false;
function saveEmployee() {
	if(isSaving == false && $("#errorBiometricId").text() == "") {
		unFormatSalaryDetails();
		isSaving = true;
		var gender = $("#genderId").val();
		var civilStatus = $("#civilStatusId").val();
		$("#hdnGenderId").val(gender);
		$("#hdnCivilStatusId").val(civilStatus);
		$("#btnSaveEmployee").attr("disabled", "disabled");
		var empShiftId = $("#employeeShiftId").val();
		doPostWithCallBack ("employeeFormId", "divEmployeeForm", function(data) {
			if (data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "employee: " + $("#firstName").val() 
					+ " " + $("#lastName").val() + ".");
				$("#divEmployeeForm").html("");
				searchEmployee();
			} else {
				var position = $("#positionNameId").val();
				var gender = $("#genderId").val();
				var civilStatus = $("#civilStatusId").val();
				$("#divEmployeeForm").html(data);
				$("#positionNameId").val(position);
				$("#genderId").val(gender);
				$("#employeeShiftId").val(empShiftId);
				$("#civilStatusId").val(civilStatus);
			}
			isSaving = false;
		});
	}
}

function getCommonParam() {
	var txtName = processSearchName($("#txtName").val());
	var txtPosition = processSearchName($("#txtPosition").val());
	var divisionId = $("#slctDivision").val();
	return "?name="+txtName+"&position="+txtPosition+"&divisionId="+divisionId;
}

function searchEmployee() {
	doSearch ("divEmployeeTbl", "/admin/employee/search"+getCommonParam()+"&pageNumber=1");
}
</script>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Name </td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Position </td>
				<td><input type="text" id="txtPosition" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Division </td>
				<td>
					<select id="slctDivision" class="frmSmallSelectClass">
						<option value=-1>ALL</option>
						<c:forEach var="d" items="${divisions}">
							<option value="${d.id}">${d.name}</option>
						</c:forEach>
					</select>
					<input type="button" value="Search" onclick="searchEmployee();"/>
				</td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divEmployeeTbl">
		<%@ include file="EmployeeTable.jsp" %>
	</div>
	<div class="controls" >
		<!--	Disabling add button, adding of employees must be implemented in Employee profile -->
		<!--	<input type="button" id="btnAddEmployee" value="Add" onclick="addEmployee();"></input> -->
		<input type="button" id="btnEditEmployee" value="Edit" onclick="editEmployee();"></input>
	</div>
	<br>
	<br>
	<div id="divEmployeeForm" style="margin-top: 20px;"></div>
</body>
</html>