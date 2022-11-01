<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		 Description: Employee admin setting form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
var positionName = null;
$(document).ready(function() {
	var employeeShiftId = null;
	if("${employee.id}" > 0) {
		$("#positionNameId").val("${employee.position.name}");
		$("#genderId").val("${employee.gender}");
		$("#civilStatusId").val("${employee.civilStatus}");
		positionName = $("#positionNameId").val();
		formatSalaryDetails();
		employeeShiftId = "${employee.employeeShiftId}";
	} else {
		formatSalaryDetails();
	}
	loadEmployeeShift(employeeShiftId);
});

function loadPositions(){
	loadACItems("positionNameId","hdnPositionId", null, contextPath + "/getPositions",
			contextPath + "/getPosition?name="+$("#positionNameId").val(),"name",
			function () {
				$("#spanPositionError").text("");
			}, function() {
				$("#spanPositionError").text("");
			}, function() {
				$("#spanPositionError").text("");
			}, function() {
				$("#spanPositionError").text("Invalid position.");
				$("#hdnPositionId").val("");
	});
	positionName = $("#positionNameId").val();
}

function isNumeric (textbox, isBiometric) {
	unformat(textbox);
	if(isNaN($("#"+textbox).val()) || $("#"+textbox).val().indexOf('-') !== -1){
		$("#"+textbox).val("0.00");
	} else {
		if(!isBiometric){
			formatMoney(textbox, $("#"+textbox).val());
		} else {
			$("#errorBiometricId").text("");
			if($("#"+textbox).val() > 2147483647){//Max value for integer is 2147483647.
				$("#errorBiometricId").text("Biometric ID must not exceed limit. Please contact your System Administrator.");
				$("#"+textbox).focus();
			} else if (!(($("#"+textbox).val() % 1 === 0))){
				$("#errorBiometricId").text("Biometric ID must be a whole number.");
				$("#"+textbox).focus();
			}
		}
	}
}

function formatMoney(textbox, val){
	$("#"+textbox).val(accounting.formatMoney(val));
}

function unformat(textbox){
	$("#"+textbox).val(accounting.unformat($("#"+textbox).val()));
}

function formatSalaryDetails(){
	formatMoney('ecola', "${employee.salaryDetail.ecola}");
	formatMoney('dailySalary', "${employee.salaryDetail.dailySalary}");
	formatMoney('basicSalary', "${employee.salaryDetail.basicSalary}");
	formatMoney('deMinimis', "${employee.salaryDetail.deMinimis}");
	formatMoney('sssContribution', "${employee.salaryDetail.sssContribution}");
	formatMoney('addSssContribution', "${employee.salaryDetail.addSssContribution}");
	formatMoney('philHealthContribution', "${employee.salaryDetail.philHealthContribution}");
	formatMoney('pagIbigContribution', "${employee.salaryDetail.pagIbigContribution}");
	formatMoney('addPagIbigContribution', "${employee.salaryDetail.addPagIbigContribution}");
	formatMoney('otherDeduction', "${employee.salaryDetail.otherDeduction}");
	formatMoney('bonus', "${employee.salaryDetail.bonus}");
}

function unFormatSalaryDetails(){
	unformat('ecola');
	unformat('dailySalary');
	unformat('basicSalary');
	unformat('deMinimis');
	unformat('sssContribution');
	unformat('addSssContribution');
	unformat('philHealthContribution');
	unformat('pagIbigContribution');
	unformat('addPagIbigContribution');
	unformat('otherDeduction');
	unformat('bonus');
}

function loadEmployeeShift(employeeShiftId){
	var companyId = $("#companyId option:selected").val();
	var uri = contextPath+"/getEmployeeShifts?companyId="+companyId;

	if(employeeShiftId != null){
		uri +="&employeeShiftId="+employeeShiftId;
	}
	$("#employeeShiftId").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	loadPopulate (uri, false, employeeShiftId, "employeeShiftId", optionParser, null);
}

</script>
</head>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="employee" id="employeeFormId">
			<div class="modFormLabel">Employee</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>Employee Information</legend>
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<form:hidden path="salaryDetail.id"/>
						<form:hidden path="positionId" id="hdnPositionId"/>
						<form:hidden path="gender" id="hdnGenderId"/>
						<form:hidden path="civilStatus" id="hdnCivilStatusId"/>
						<tr>
							<td class="labels">* Company</td>
							<td class="value"><form:select path="companyId" id="companyId" cssClass="frmSelectClass"
								items="${companies}" itemLabel="name" itemValue="id" onchange="loadEmployeeShift();"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Division</td>
							<td class="value"><form:select path="divisionId" id="divisionId" cssClass="frmSelectClass"
								items="${divisions}" itemLabel="name" itemValue="id" /></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Type</td>
							<td class="value"><form:select path="employeeTypeId" id="employeeTypeId"
								cssClass="frmSelectClass" items="${employeeTypes}" itemLabel="name" itemValue="id"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="employeeTypeId" cssClass="error"/></td>
						</tr>
						<tr>
						<tr>
							<td class="labels">Biometric ID</td>
							<td class="value"><form:input path="biometricId" maxlength="10"
								id="biometricId" class="input" onblur="isNumeric('biometricId', true);"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="biometricId" cssClass="error"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><span id="errorBiometricId" class="error"></span></td>
						</tr>
						<tr>
							<td class="labels">Employee No.</td>
							<td class="value"><form:input path="employeeNo" id="employeeNo" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="employeeNo" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* First Name</td>
							<td class="value"><form:input path="firstName" id="firstName" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="firstName" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Middle Name</td>
							<td class="value"><form:input path="middleName" id="middleName" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="middleName" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Last Name</td>
							<td class="value"><form:input path="lastName" id="lastName" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="lastName" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Position</td>
							<td class="value">
								<input id="positionNameId" class="input" onkeydown="loadPositions();"
									onkeyup="loadPositions();" onblur="loadPositions();"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="positionId" cssClass="error"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><span id="spanPositionError"></span></td>
						</tr>
						<tr>
							<td class="labels">* Gender</td>
							<td class="value"><select id="genderId" class="frmSelectClass">
								<option value="1">Male</option>
								<option value="2">Female</option>
							</select>
							</td>
						</tr>
						<tr>
							<td class="labels">* Birthdate</td>
							<td class="value"><form:input path="birthDate" onblur="evalDate('birthDate')"
								id="birthDate" cssClass="dateClass2"/>
								<img id="imgDate" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('birthDate')"
								style="cursor: pointer" style="float: right;" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="birthDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Civil Status</td>
							<td class="value"><select id="civilStatusId" class="frmSelectClass">
								<option value="1">Single</option>
								<option value="2">Married</option>
							</select>
							</td>
						</tr>
						<tr>
							<td class="labels">Contact Number</td>
							<td class="value"><form:input path="contactNo" id="contactNo" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="contactNo" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Email Address</td>
							<td class="value"><form:input path="emailAddress" id="emailAddress" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="emailAddress" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Address</td>
							<td class="value"><form:textarea path="address" cssClass="addressClass"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="address" cssClass="error"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="employeeShiftId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Status</td>
							<td class="value"><form:select path="employeeStatusId" id="employeeStatusId"
								cssClass="frmSelectClass" items="${employeeStatuses}" itemLabel="name" itemValue="id"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="employeeStatusId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Active</td>
							<td class="value"><form:checkbox path="active"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Salary Detail</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Basic Salary</td>
							<td class="value"><form:input path="salaryDetail.basicSalary"
								id="basicSalary" class="number" style="text-align: right" onblur="isNumeric('basicSalary', false);"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="salaryDetail.basicSalary" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Daily Salary</td>
							<td class="value"><form:input path="salaryDetail.dailySalary"
								id="dailySalary" class="number" style="text-align: right" onblur="isNumeric('dailySalary', false);"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="salaryDetail.dailySalary" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Emergency Cost of <br> Living Allowance</td>
							<td class="value"><form:input path="salaryDetail.ecola"
								id="ecola" class="number" style="text-align: right" onblur="isNumeric('ecola', false);"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="salaryDetail.ecola" cssClass="error"/></td>
						</tr>
						<tr style="display: none;">
							<td class="labels">De Minimis</td>
							<td class="value"><form:input path="salaryDetail.deMinimis"
								id="deMinimis" class="number" style="text-align: right" onblur="isNumeric('deMinimis', false);"/></td>
						</tr>
						<tr>
							<td><br></td>
						</tr>
						<tr>
							<td class="labels">* Exclude Contributions</td>
						</tr>
						<tr>
							<td class="labels">SSS</td>
							<td class="value"><form:checkbox path="salaryDetail.excludeSss"/></td>
						</tr>
						<tr>
							<td class="labels">PHIC</td>
							<td class="value"><form:checkbox path="salaryDetail.excludePhic"/></td>
						</tr>
						<tr>
							<td class="labels">HDMF</td>
							<td class="value"><form:checkbox path="salaryDetail.excludeHdmf"/></td>
						</tr>
						<tr style="display: none;">
							<td class="labels">SSS Loans</td>
							<td class="value"><form:input path="salaryDetail.addSssContribution"
								id="addSssContribution" class="number" style="text-align: right" onblur="isNumeric('addSssContribution', false);"/></td>
						</tr>
						<tr style="display: none;">
							<td></td>
							<td class="value"><form:errors path="salaryDetail.addSssContribution" cssClass="error"/></td>
						</tr>
						<tr style="display: none;">
							<td class="labels">Pag-ibig Loans</td>
							<td class="value"><form:input path="salaryDetail.addPagIbigContribution"
								id="addPagIbigContribution" class="number" style="text-align: right" onblur="isNumeric('addPagIbigContribution', false);"/></td>
						</tr>
						<tr style="display: none;">
							<td></td>
							<td class="value"><form:errors path="salaryDetail.addPagIbigContribution" cssClass="error"/></td>
						</tr>
						<tr style="display: none;">
							<td class="labels">Other deductions</td>
							<td class="value"><form:input path="salaryDetail.otherDeduction"
								id="otherDeduction" class="number" style="text-align: right" onblur="isNumeric('otherDeduction', false);"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="salaryDetail.otherDeduction" cssClass="error"/></td>
						</tr>
						<tr style="display: none;">
							<td class="labels">Bonus</td>
							<td class="value"><form:input path="salaryDetail.bonus"
								id="bonus" class="number" style="text-align: right" onblur="isNumeric('bonus', false);"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="salaryDetail.bonus" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="formDiv">
					<tr>
						<td colspan="2" align="right">
							<input type="button" id="btnSaveEmployee" value="${employee.id eq 0 ? 'Save' : 'Update'}" onclick="saveEmployee();"/>
							<input type="button" id="btnCancelEmployee" value="Cancel" onclick="cancelEmployee();"/>
						</td>
					</tr>
				</table>
			</div>
			<hr class="thin"/>
		</form:form>
	</div>
</body>
</html>