<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Employee Profile form jsp page for GVCH
-->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
#divCamera {
	width: 320px;
	height: 300px;
	border: 1px solid green;
}

.btnPhoto {
	width: 90px;
}

#btnSavePatientProfile, #btnCancelPatientProfile {
	font-weight: bold;
}

</style>
<script type="text/javascript">
$(document).ready(function () {
	var formPage = $("#hdnFormPage").val();
	var uri = contextPath + "/gvchEmployeeProfile/";
	if(formPage == 1) {
		$("#divEPBasicInfoForm").show("fast");
	} else if(formPage == 3) {
		$("#divEPEducForm").show("fast");
	} else {
		$("#divEPLastForm").show("fast");
	}
});

$(document).keyup(function(e) {
	if (e.keyCode == 27) {
		reloadEmployeeProfile();
	}
});

$('#btnClose').click(function(){
	reloadEmployeeProfile();
});

function loadEPBasicInfoForm() {
	$("#divEPEducForm").hide("fast");
	$("#divEPBasicInfoForm").show("fast");
	$("#hdnFormPage").val(1);
}

function loadEPEducForm() {
	$("#divEPLastForm").hide("fast");
	$("#divEPEducForm").show("fast");
	$("#hdnFormPage").val(3);
}

function loadPositions(){
	loadACItems("txtPosition","hdnPositionId", null, contextPath + "/getPositions",
			contextPath + "/getPosition?name="+$("#txtPosition").val(),"name",
			function () {
				// TODO : Add function.
			}, function() {
				// TODO : Add function.
			}, function() {
				// TODO : Add function.
			}, function() {
				$("#hdnPositionId").val("");
			});
	positionName = $("#txtPosition").val();
}

function getPosition() {
	var txtPosition = $.trim($("#txtPosition").val())
	var uri = "/getPosition?name=" + txtPosition;
	if(txtPosition == "") {
		$("#hdnPositionId").val("");
		$("#spanPositionId").text("");
	}
	$.ajax({
		url: contextPath + uri,
		success : function(position) {
			if (txtPosition != ""){
				$("#spanPositionId").text("");
				$("#spanPosErrorMsgId").html("");
				if (position != null && position != undefined) {
					$("#hdnPositionId").val(position.id);
				} else {
					$("#spanPositionId").text("Invalid position.");
					$("#hdnPositionId").val("");
					if ($("#spanPositionId").text() != "") {
						$("#spanPosErrorMsgId").html("");
					}
				}
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function reloadEmployeeProfile() {
	location.reload();
}
</script>
<title>Patient Record Form</title>
</head>
<body>
<div id="divForm" class="formDivBigForms">
<form:form method="POST" commandName="employeeProfile" id="employeeProfileForm">
	<div class="modFormLabel">
		Employee Profile
		<span class="btnClose" id="btnClose">[X]</span>
	</div>
	<form:hidden path="id" id="hdnId"/>
	<form:hidden path="createdBy" id="hdnEPCreatedBy"/>
	<form:hidden path="employeeId" id="hdnEPEmployeeId"/>
	<form:hidden path="employee.id" id="hdnEmployeeId"/>
	<form:hidden path="employee.createdBy" id="hdnEECreatedBy"/>
	<form:hidden path="employee.positionId" id="hdnPositionId"/>
	<form:hidden path="employeeChildrenJson" id="employeeChildrenJson"/>
	<form:hidden path="eeEmergencyContactJson" id="eeEmerContactJson"/>
	<form:hidden path="eeLicenseCertificateJson" id="eeLicenseCertJson"/>
	<form:hidden path="eeSeminarAttendedJson" id="eeSeminarAttendedJson"/>
	<form:hidden path="eeNationalCompetencyJson" id="eeNationalCompetencyJson"/>
	<form:hidden path="employeeFamily.id"/>
	<form:hidden path="formPage" id="hdnFormPage"/>
	<form:hidden path="employeeNumber" id="employeeNumber"/>
	<form:hidden path="ebObjectId" id="ebObjectId"/>
	<form:hidden path="referenceDocument.id" id="hdnRDId"/>
	<form:hidden path="referenceDocument.fileName" id="fileName"/>
	<form:hidden path="referenceDocument.fileSize" id="fileSize"/>
	<form:hidden path="referenceDocument.description" id="description"/>
	<form:hidden path="referenceDocument.file" id="photo"/>
	<form:hidden path="referenceDocument.ebObjectId" id="hdnRDEbObjectId"/>
	<form:hidden path="employeeEducationalAttainment.id" id="hdneEeEducAttainId"/>
	<form:hidden path="strEmployeeNumber" id="hdnEmployeeNoId"/>
	<div id="divEmpForm">
		<div id="divEPBasicInfoForm" style="display: none;">
			<%@ include file="EPBasicInfoForm.jsp"%>
		</div>
		<div id="divEPEducForm" style="display: none; width: auto;">
			<%@ include file="EPEducForm.jsp"%>
		</div>
		<div id="divEPLastForm" style="display: none;">
			<%@ include file="EPLastForm.jsp"%>
		</div>
	</div>
</form:form>
</div>
</body>
</html>