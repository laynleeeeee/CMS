<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Employee profile - employee query jsp page for GVCH
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
#btnSaveEmployeeProfile, #btnCancelPatientProfile {
	font-weight: bold;
}

.tblQuestions {
	align: right;
 	color: black;
	width: 89%;
	padding: 4px;
	text-align: left;
	font-family: sans-serif;
	font-style: normal;
	font-size: normal;
	margin-left:auto;
	margin-right:auto;
}

.answerFields{
	border: 1px solid gray;
	padding: 3px;
	width: 80%;
}
</style>
<script type="text/javascript">
var $seminarAttendedTbl = null;
var $licencesCertTbl = null;
var $eeEmerContactTbl = null;
var $nationalCompetencyTbl = null;
$(document).ready(function() {
	if ($("#divEPLastForm").is(":visible")) {
		$("#hdnFormPage").val(4);
	}
	initEmerContactTbl();
	initSeminarAttendedTbl();
	initLicenseCertificateTbl();
	initNationalCompTbl();
});

function initLicenseCertificateTbl() {
	var licenseCertificateJson = JSON.parse($("#eeLicenseCertJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$licencesCertTbl = $("#licencesCertTblId").editableItem({
		data: licenseCertificateJson,
		jsonProperties: [
                 {"name" : "id", "varType" : "int"},
                 {"name" : "ebObjectId", "varType" : "int"},
                 {"name" : "accreditationType", "varType" : "string"},
        ],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Type",
				"cls" : "accreditationType tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "60%" }
	    ],
	    "disableDuplicateStockCode" : false,
        "itemTableMessage": ""
	});

	$("#licencesCertTblId").on('blur', '.dateIssued', function (){
		var $row = $(this).closest("tr");
		var $col = $(this).closest("td");
		var $target = $($col).find("input");
		var id = "txtDateIssued" + $($row).find(".rowNumber").text() + $col[0].cellIndex;
		$($target).attr("id", id);
		evalDate(id);
	});
}

function initEmerContactTbl() {
	var eeEmerContactJson = JSON.parse($("#eeEmerContactJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$eeEmerContactTbl = $("#emerContactTblId").editableItem({
		data: eeEmerContactJson,
		jsonProperties: [
                 {"name" : "id", "varType" : "int"},
                 {"name" : "ebObjectId", "varType" : "int"},
                 {"name" : "name", "varType" : "string"},
                 {"name" : "address", "varType" : "string"},
                 {"name" : "contactNo", "varType" : "string"}
        ],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Contact Person",
				"cls" : "name tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "50%" },
			{"title" : "Contact Address",
				"cls" : "address tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "30%" },
			{"title" : "Contact Number",
				"cls" : "contactNo tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "20%" }
	    ],
	    "disableDuplicateStockCode" : false,
        "itemTableMessage": ""
	});
}

function initSeminarAttendedTbl() {
	var eeSeminarAttendedJson = JSON.parse($("#eeSeminarAttendedJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$seminarAttendedTbl = $("#seminarAttendedTblId").editableItem({
		data: eeSeminarAttendedJson,
		jsonProperties: [
                 {"name" : "id", "varType" : "int"},
                 {"name" : "ebObjectId", "varType" : "int"},
                 {"name" : "courseTitle", "varType" : "string"},
                 {"name" : "seminarDate", "varType" : "date"}
        ],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Course/Title",
				"cls" : "courseTitle tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "40%" },
			{"title" : "Date Attended",
				"cls" : "seminarDate tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "20%" }
	    ],
	    "disableDuplicateStockCode" : false,
        "itemTableMessage": ""
	});
}

function initNationalCompTbl() {
	var eeNatCmptncyJson = JSON.parse($("#eeNationalCompetencyJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$nationalCompetencyTbl = $("#nationalCompetencyTblId").editableItem({
		data: eeNatCmptncyJson,
		jsonProperties: [
                 {"name" : "id", "varType" : "int"},
                 {"name" : "ebObjectId", "varType" : "int"},
                 {"name" : "description", "varType" : "string"},
        ],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "50%" }
	    ],
	    "disableDuplicateStockCode" : false,
        "itemTableMessage": ""
	});
}
</script>
<title>Employee Last Page</title>
</head>
<body>
<br>
<fieldset class="frmField_set">
<legend>LICENSES (PRC, Civil Service, etc)</legend>
	<div id="licencesCertTblId"></div>
	<table>
		<tr>
			<td>
				<form:errors path="licenseCertificates" cssClass="error"/>
			</td>
		</tr>
	</table>
</fieldset>
<fieldset class="frmField_set">
<legend>NATIONAL COMPETENCIES (NC I or NC II)/TRAINER'S METHODOLOGY (TM)</legend>
	<div id="nationalCompetencyTblId"></div>
	<table>
		<tr>
			<td>
				<form:errors path="nationalCompetencies" cssClass="error"/>
			</td>
		</tr>
	</table>
</fieldset>
<fieldset class="frmField_set">
<legend>TRAININGS AND SEMINARS ATTENDED</legend>
<div id="seminarAttendedTblId"></div>
<table>
	<tr>
		<td>
			<form:errors path="seminarAttendeds" cssClass="error"/>
		</td>
	</tr>
</table>
</fieldset>
<fieldset class="frmField_set">
<legend>IN CASE OF EMERGENCY</legend>
	<div id="emerContactTblId"></div>
	<table>
		<tr>
			<td>
				<form:errors path="emergencyContacts" cssClass="error"/>
			</td>
		</tr>
	</table>
</fieldset>
<br>
<table class="tblQuestions">
	<tr>
		<td></td>
		<td style="padding-top: 5px; text-align: right;" colspan="5"  >
			<input type="button" id="btnLoadProfile" value="Previous" 
				onclick="loadEPEducForm();" />
			<input type="button" id="btnSaveFinal" value="Save" 
				onclick="saveEmployeeProf(4);" />
		</td>
	</tr>
</table>
</body>
</html>