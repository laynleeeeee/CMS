<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

			Description: GVCH employee profile basic info form -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<style>
#divCamera {
	width: 320px;
	height: 300px;
	border: 1px solid green;
}
.btnPhoto {
	width: 90px;
}
#btnSaveEmployeeProfile, #btnCancelPatientProfile {
	font-weight: bold;
}
.tblLabels {
	text-align: left;
	font-size: small;
	white-space: nowrap;
	padding-left: 15px;
	padding-top: 3px;
}
.tblLabels2 {
	text-align: left;
	font-size: small;
	white-space: nowrap;
	padding-left: 15px;
}
</style>
<script type="text/javascript">
var $childrenTable = null;
var origEmpId = "${employeeProfile.strEmployeeNumber}";
var origCompId = "${employeeProfile.employee.companyId}";
$(document).ready(function() {
	if ($("#divEPBasicInfoForm").is(":visible")) {
		$("#hdnFormPage").val(1);
	}
	$("#txtPosition").val("${employeeProfile.employee.position.name}");
	$("#childrenTable").html("");
	initChildren();
});

function clearPos() {
	var value = $.trim($("#txtPosition").val());
	if (value == "") {
		$("#hdnPositionId").val("");
	}
}

function initChildren() {
	var employeeChildrenJson = JSON.parse($("#employeeChildrenJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$childrenTable = $("#childrenTable").editableItem({
		data: employeeChildrenJson,
		jsonProperties: [
                 {"name" : "id", "varType" : "int"},
                 {"name" : "employeeId", "varType" : "int"},
                 {"name" : "name", "varType" : "string"},
                 {"name" : "birthDate", "varType" : "date"}
        ],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "employeeId",
				"cls" : "employeeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Name",
				"cls" : "name tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "75%" },
			{"title" : "Birth Date",
				"cls" : "birthDate tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "25%" }
	    ],
	    "disableDuplicateStockCode" : false,
        "itemTableMessage": ""
	});

	$("#childrenTable").on('blur', '.birthDate', function (){
		var $row = $(this).closest("tr");
		var $col = $(this).closest("td");
		var $target = $($col).find("input");
		var id = "txtBirthDate" + $($row).find(".rowNumber").text() + $col[0].cellIndex;
		$($target).attr("id", id);
		evalDate(id);
	});

}

function generateEmployeeNumber() {
	var companyId = $("#companyId").val();
	if (companyId == origCompId) {
		$("#hdnEmployeeNoId").val(origEmpId);
		$("#tdEmpNumber").text(origEmpId);
	} else {
		var uri = contextPath + "/employeeProfile/generateEmployeeNumber?companyId="+companyId;
		$.ajax({
			url: uri,
			success : function(empNo) {
				$("#hdnEmployeeNoId").val(empNo);
				$("#tdEmpNumber").text(empNo);
			},
			error : function(error) {
			},
			dataType: "text"
		});
	} 
}
</script>
<title>Employee Record Form</title>
</head>
<body>
<table class="formTable" style="margin: 12px;">
	<tr>
		<td class="tblLabels2">* Company</td>
		<td class="tblLabels2" colspan="2">
			<form:select path="employee.companyId" id="companyId"
				cssClass="frmSelectClass" items="${companies}"
				itemLabel="name" itemValue="id" onchange="generateEmployeeNumber();">
			</form:select>
		</td>
		<td valign="top" align="right" colspan="2" rowspan="24">
			<div id="divPhotoContainer">
				<table style="border: none;">
					<tr>
						<td colspan="2" align="center">
							<div id="divCamera"></div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<form:errors path="referenceDocument.file" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td align="center">
							<input type="button" id="btnUpload" class="btnPhoto "value="Upload" onclick="uploadPhoto();" />
							<input type="file" id="fileData" onchange="loadPhotoToDiv(this);" style="display: none;"/>
							<input type="button" id="btnSnapshot" class="btnPhoto" value="Take" onclick="takePhoto(this);" />
						</td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="employee.companyId" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">* Department</td>
		<td class="tblLabels2 "colspan="4">
			<form:select path="employee.divisionId" id="divisionId"
				cssClass="frmSelectClass" items="${divisions}"
				itemLabel="name" itemValue="id">
			</form:select>
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="employee.divisionId" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">* Employment Status</td>
		<td class="tblLabels2">
			<form:select path="employee.employeeTypeId" id="employeeTypeId"
				cssClass="frmSmallSelectClass" items="${employeeTypes}"
				itemLabel="name" itemValue="id">
			</form:select>
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="employee.employeeTypeId" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">Contact Number</td>
		<td class="tblLabels2">
			<form:input path="employee.contactNo" cssClass="inputSmall" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="employee.contactNo" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">* Employee ID</td>
		<td class="tblLabels2" id="tdEmpNumber">
			${employeeProfile.strEmployeeNumber}
		</td>
	</tr>
	<tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<span id="spanBiometricId" class="error"></span>
			<form:errors path="strEmployeeNumber" cssClass="error" id="spanBioErrorMsgId" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">* Biometric ID</td>
		<td class="tblLabels2">
			<form:input path="employee.biometricId" id="biometricId"
					cssClass="inputSmall" maxlength="10" onblur="inputOnlyNumeric(this.id);"/>
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<span id="spanBiometricId" class="error"></span>
			<form:errors path="employee.biometricId" cssClass="error" id="spanBioErrorMsgId" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">RFID</td>
		<td class="tblLabels2">
			<form:input path="rfid" id="rfid"
					cssClass="inputSmall" onblur="inputOnlyNumeric(this.id);"/>
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">* Position</td>
		<td class="tblLabels2">
			<form:input path="employee.position.name" id="txtPosition" class="inputSmall"
				onkeydown="loadPositions();" onkeyup="loadPositions();" onblur="getPosition();" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<span id="spanPositionId" class="error"></span>
			<form:errors path="employee.positionId" cssClass="error" id="spanPosErrorMsgId" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">Blood Type</td>
		<td class="tblLabels2">
			<form:input path="bloodType" cssClass="inputSmall" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="bloodType" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">TIN No.</td>
		<td class="tblLabels2">
			<form:input path="tin" cssClass="inputSmall" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="tin" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">Philhealth No.</td>
		<td class="tblLabels2">
			<form:input path="philhealthNo" cssClass="inputSmall" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="philhealthNo" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">SSS No.</td>
		<td class="tblLabels2">
			<form:input path="sssNo" cssClass="inputSmall" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="sssNo" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"> HDMF No.</td>
		<td class="tblLabels2">
			<form:input path="hdmfNo" cssClass="inputSmall" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="hdmfNo" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">* Date Hired</td>
		<td class="tblLabels2">
			<form:input path="hiredDate"
				id="hiredDate" style="width: 120px;" cssClass="dateClass2" onblur="evalDate('hiredDate')"/> 
			<img id="imgHiredDate"
				src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('hiredDate')"
				style="cursor: pointer" style="float: right;" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2"><form:errors path="hiredDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="tblLabels2">* Employment Period</td>
		<td class="tblLabels2" colspan="4">
			<form:input path="employmentPeriodFrom"
				id="employmentPeriodFrom" style="width: 120px;" cssClass="dateClass2" onblur="evalDate('employmentPeriodFrom')"/> 
			<img id="imgEmploymentPeriodFrom"
				src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('employmentPeriodFrom')"
				style="cursor: pointer" style="float: right;" />
			&nbsp;&nbsp;To&nbsp;&nbsp;
			<form:input path="employmentPeriodTo"
				id="employmentPeriodTo" style="width: 120px;" cssClass="dateClass2" onblur="evalDate('employmentPeriodTo')"/> 
			<img id="imgEmploymentPeriodTo"
				src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('employmentPeriodTo')"
				style="cursor: pointer" style="float: right;" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="employmentPeriodFrom" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">* Employee Status</td>
		<td class="tblLabels2">
			<form:select path="employee.employeeStatusId" id="employeeStatusId"
				cssClass="frmSmallSelectClass" items="${employeeStatuses}" itemLabel="name" itemValue="id">
			</form:select>
		</td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2">
			<form:errors path="employee.employeeStatusId" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">Active</td>
		<td class="tblLabels2"><form:checkbox path="employee.active"/></td>
	</tr>
	<tr>
		<td class="tblLabels2"></td>
		<td class="tblLabels2"><form:errors path="employee.active" cssClass="error"/></td>
	</tr>
	<!-- Start of basic employee info -->
	<tr>
		<td class="tblLabels">* Family Name</td>
		<td class="tblLabels">* First Name</td>
		<td class="tblLabels">Middle Name</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:input path="employee.lastName" class="inputSmall"/>
		</td>
		<td class="tblLabels2">
			<form:input path="employee.firstName" class="inputSmall"/>
		</td>
		<td class="tblLabels2">
			<form:input path="employee.middleName" class="inputSmall"/>
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:errors path="employee.lastName" cssClass="error"/>
		</td>
		<td class="tblLabels2">
			<form:errors path="employee.firstName" cssClass="error"/>
		</td>
		<td class="tblLabels2">
			<form:errors path="employee.middleName" cssClass="error"/>
		</td>
	</tr>
	<tr>
		<td class="tblLabels" colspan="2">* Permanent Address</td>
	</tr>
	<tr>
		<td class="tblLabels2" colspan="2">
			<form:input path="permanentAddress" class="input" style="width: 360px"/>
		</td>
	</tr>
	<tr>
		<td class="tblLabels2" colspan="2">
			<form:errors path="permanentAddress" cssClass="error"/>
		</td>
	</tr>
	<tr>
		<td class="tblLabels">Birthdate</td>
		<td class="tblLabels">Birthplace</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:input path="employee.birthDate"
				id="birthdate" style="width: 120px;" cssClass="dateClass2" onblur="evalDate('birthdate');"/>
			<img id="imgBirthDate" src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('birthdate')" style="cursor: pointer" style="float: right;" />
		</td>
		<td class="tblLabels2">
			<form:input path="birthPlace" cssClass="inputSmall" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:errors path="employee.birthDate" cssClass="error" />
		</td>
		<td class="tblLabels" colspan="2">
			<form:errors path="birthPlace" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels">Gender</td>
		<td class="tblLabels">Civil Status</td>
		<td class="tblLabels">Email Address</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:select path="genderId" id="genderId"
				cssClass="frmSmallSelectClass" items="${genders}"
				itemLabel="name" itemValue="id">
			</form:select>
		</td>
		<td class="tblLabels2">
			<form:select path="civilStatusId" id="civilStatusId"
				cssClass="frmSmallSelectClass" items="${civilStatuses}"
				itemLabel="name" itemValue="id">
			</form:select>
		</td>
		<td class="tblLabels2">
			<form:input path="employee.emailAddress" class="inputSmall"/>
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:errors path="genderId" cssClass="error" />
		</td>
		<td class="tblLabels2">
			<form:errors path="civilStatusId" cssClass="error" />
		</td>
		<td class="tblLabels2">
			<form:errors path="employee.emailAddress" cssClass="error" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels">* Father's Name</td>
		<td class="tblLabels">Occupation</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:input path="employeeFamily.fatherName" cssClass="inputSmall" />
		</td>
		<td class="tblLabels2" colspan="2">
			<form:input path="employeeFamily.fatherOccupation"
					cssClass="inputSmall" style="width: 360px" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:errors path="employeeFamily.fatherName" cssClass="error" />
		</td>
		<td class="tblLabels2" colspan="2">
			<form:errors path="employeeFamily.fatherOccupation"
					cssClass="error" style="width: 360px" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels">* Mother's Name</td>
		<td class="tblLabels">Occupation</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:input path="employeeFamily.motherName" cssClass="inputSmall" />
		</td>
		<td class="tblLabels2" colspan="2">
			<form:input path="employeeFamily.motherOccupation"
					cssClass="inputSmall" style="width: 360px" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:errors path="employeeFamily.motherName" cssClass="error" />
		</td>
		<td class="tblLabels2" colspan="2">
			<form:errors path="employeeFamily.motherOccupation"
					cssClass="error" style="width: 360px" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels">Name of Spouse</td>
		<td class="tblLabels">Occupation</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:input path="employeeFamily.spouseName" cssClass="inputSmall" />
		</td>
		<td class="tblLabels2" colspan="2">
			<form:input path="employeeFamily.spouseOccupation"
					cssClass="inputSmall" style="width: 360px" />
		</td>
	</tr>
	<tr>
		<td class="tblLabels2">
			<form:errors path="employeeFamily.spouseName" cssClass="error" />
		</td>
		<td class="tblLabels2" colspan="2">
			<form:errors path="employeeFamily.spouseOccupation"
					cssClass="error" style="width: 360px" />
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<fieldset class="frmField_set" style="margin-left: 14px">
			<legend>Children</legend>
				<div id="childrenTable"></div>
				<table>
					<tr>
						<td colspan="4">
							<form:errors path="employeeChildrenMsg" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
		</td>
	</tr>
	<tr align="center">
		<td style="padding-top: 5px; text-align: right;" colspan="5" >
			<input type="button" id="btnSaveEmployeeProfile" value="Next" 
				onclick="saveEmployeeProf(1);" />
		</td>
	</tr>
</table>
</body>
</html>