<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

	 Description: Fleet Incident
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.fleet.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<style type="text/css">
.tdRed {
	color: red;
}
</style>
<script type="text/javascript">
var $fleetCaptainTbl = null;
var $fleetMdmTbl = null;
var $fCMRefDocsTable = null;
var CAPTAIN_TYPE = 37;

$(document).ready(function () {
	initializeFleetCaptain();
	$('#fleetCaptainTbl tbody tr .captainMdm').each(function(){
		var captainMdmVal = $(this).closest("tr").find(".orTypeId").val();
		setPositionTypes($(this), captainMdmVal);
		if(captainMdmVal == ""){
			$(this).closest("tr").find(".orTypeId").val(CAPTAIN_TYPE);
		}
	});
	initializeDocumentsTbl();
	initTblColor();
});

function setPositionTypes($select, positionTypeId){
	$($select).html("");
	var positionType = "<option value='37'";

	if(positionTypeId == 37){
		positionType += " selected = 'selected'";
	}

	positionType += ">Captain</option>";
	positionType += "<option value='38'";

	if(positionTypeId == 38){
		positionType += " selected = 'selected'";
	}
	positionType += ">MDM</option>";

	$($select).append(positionType);
}

function addClassColor($tr, className){
	$($tr).addClass(className);
}

function removeClassColor($tr, className){
	$($tr).removeClass(className);
}

function initTblColor(){
	$('#fleetCaptainTbl tbody tr').each(function(){
		checkAndchangedColorTr($(this).find(".licenseNoExpirationDate"), ".licenseNo", ".licenseNoExpirationDate");
		checkAndchangedColorTr($(this).find(".seamansBookExpirationDate"), ".seamansBook", ".seamansBookExpirationDate");
		checkAndchangedColorTr($(this).find(".fishesriesExpirationDate"), ".fisheries", ".fishesriesExpirationDate");
		checkAndchangedColorTr($(this).find(".passportExpirationDate"), ".passport", ".passportExpirationDate");
	});
}

function checkAndchangedColorTr($td, elemStr, elemDate){

	// Get the entered date.
	var enteredDate = new Date($($td).val());
	enteredDate.setMonth(enteredDate.getMonth());

	// Get the date 3 months prior to current date.
	var priorDate = new Date(new Date().setMonth(new Date().getMonth() + 3));

	// Check if the date entered is valid.
	if(!isNaN(enteredDate.getTime()) && !isNaN(priorDate.getTime())){
		var $tr = $($td).closest("tr");
		if(enteredDate <= priorDate){
			addClassColor($($tr).find(elemStr), "tdRed");
			addClassColor($($tr).find(elemDate), "tdRed");
			addClassColor($($tr).find(elemStr).closest("td"), "tdRed");
			addClassColor($($tr).find(elemDate).closest("td"), "tdRed");
		} else {
			removeClassColor($($tr).find(elemStr), "tdRed");
			removeClassColor($($tr).find(elemDate), "tdRed");
			removeClassColor($($tr).find(elemStr).closest("td"), "tdRed");
			removeClassColor($($tr).find(elemDate).closest("td"), "tdRed");
		}
	}
}

function initializeFleetCaptain() {
	var fleetCaptainsJson = JSON.parse($("#fleetCaptainsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$fleetCaptainTbl = $("#fleetCaptainTbl").editableItem({
		data: fleetCaptainsJson,
		jsonProperties: [
					{"name" : "id", "varType" : "int"},
					{"name" : "ebObjectId", "varType" : "int"},
					{"name" : "referenceObjectId", "varType" : "int"},
					{"name" : "date", "varType" : "date"},
					{"name" : "name", "varType" : "string"},
					{"name" : "orTypeId", "varType" : "string"},
					{"name" : "position", "varType" : "string"},
					{"name" : "licenseNo", "varType" : "string"},
					{"name" : "licenseNoExpirationDate", "varType" : "date"},
					{"name" : "seamansBook", "varType" : "string"},
					{"name" : "seamansBookExpirationDate", "varType" : "date"},
					{"name" : "fisheries", "varType" : "string"},
					{"name" : "fishesriesExpirationDate", "varType" : "date"},
					{"name" : "passport", "varType" : "string"},
					{"name" : "passportExpirationDate", "varType" : "date"},
					{"name" : "remarks", "varType" : "string"}
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
			{"title" : "referenceObjectId",
				"cls" : "referenceObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Date",
				"cls" : "date tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "5%" },
			{"title" : "Name",
				"cls" : "name tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "orTypeId",
				"cls" : "orTypeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Type",
				"cls" : "captainMdm tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "5%"},
			{"title" : "Position",
				"cls" : "position tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "License <br> No",
				"cls" : "licenseNo tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Expiration <br> Date",
				"cls" : "licenseNoExpirationDate tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "5%" },
			{"title" : "Seaman's <br> Book",
				"cls" : "seamansBook tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Expiration <br> Date",
				"cls" : "seamansBookExpirationDate tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "5%" },
			{"title" : "Fisheries",
				"cls" : "fisheries tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Expiration <br> Date",
				"cls" : "fishesriesExpirationDate tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "5%" },
			{"title" : "Passport",
				"cls" : "passport tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Expiration <br> Date",
				"cls" : "passportExpirationDate tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "5%" },
			{"title" : "Remarks",
				"cls" : "remarks tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" }

		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});

	$("#fleetCaptainTbl").on("blur", ".date", function(){
		evalDateByTxtBox($(this).closest("td").find(".date"));
	});

	$("#fleetCaptainTbl").on("blur", ".licenseNoExpirationDate", function(){
		checkAndchangedColorTr($(this), ".licenseNo", ".licenseNoExpirationDate");
		evalDateByTxtBox($(this).closest("td").find(".licenseNoExpirationDate"));
	});

	$("#fleetCaptainTbl").on("blur", ".seamansBookExpirationDate", function(){
		checkAndchangedColorTr($(this), ".seamansBook", ".seamansBookExpirationDate");
		evalDateByTxtBox($(this).closest("td").find(".seamansBookExpirationDate"));
	});

	$("#fleetCaptainTbl").on("blur", ".fishesriesExpirationDate", function(){
		checkAndchangedColorTr($(this), ".fisheries", ".fishesriesExpirationDate");
		evalDateByTxtBox($(this).closest("td").find(".fishesriesExpirationDate"));
	});

	$("#fleetCaptainTbl").on("blur", ".passportExpirationDate", function(){
		checkAndchangedColorTr($(this), ".passport", ".passportExpirationDate");
		evalDateByTxtBox($(this).closest("td").find(".passportExpirationDate"));
	});

	$("#fleetCaptainTbl").on("change", ".captainMdm", function(){
		$(this).closest("tr").find(".orTypeId").val($(this).val());
	});

	$("#fleetCaptainTbl").on("focus", ".captainMdm", function(){
		$(this).closest("tr").find(".orTypeId").val($(this).val());
	});
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsCMJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$fCMRefDocsTable = $("#fCMRefDocsTable").editableItem({
		data: refDocsJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "referenceObjectId", "varType" : "int"},
				{"name" : "fileName", "varType" : "string"},
				{"name" : "description", "varType" : "string"},
				{"name" : "file", "varType" : "string"},
				{"name" : "fileInput", "varType" : "string"},
				{"name" : "fileSize", "varType" : "double"},
		],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "fileName",
				"cls" : "fileName",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "File Name",
				"cls" : "docName tblInputText",
				"editor" : "label",
				"visible" : true,
				"width" : "25%" },
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "45%" },
			{"title" : "file",
				"cls" : "file",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "File",
				"cls" : "fileInput tblInputFile",
				"editor" : "file",
				"visible" : true,
				"width" : "25%" },
			{"title" : "fileSize",
				"cls" : "fileSize",
				"editor" : "hidden",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});

	$("#fCMRefDocsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize,$("#referenceDocsErrMgs"), $("#fCMRefDocsTable"));
	});

	$("#fCMRefDocsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#fCMRefDocsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

var isSaving = false;
function saveFleetCM(){
	$("#hdnFdRefObjectId").val(fpRefObjectId);
	$("#fleetCaptainsJson").val($fleetCaptainTbl.getData());
	$("#referenceDocumentsCMJson").val($fCMRefDocsTable.getData());
	$("#btnSaveFleetCM").attr("disabled", "disabled");
	if(isSaving == false && $("#referenceDocsErrMgs").html() == "" && !checkExceededFileSize($("#fIRefDocsTable"))) {
		isSaving = true;
		doPostWithCallBack ("fleetCaptainMdmForm", "dvfleetCaptainMdmForm", function (data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if (isSuccessfullySaved) {
				$("#successDialog").dialog({
					modal: true,
					buttons: {
						Close: function() {
							$(this).dialog("close");
							$(this).dialog("destroy");
							var url = contextPath + "/captainMdm/?refObjectId="+fpRefObjectId;
							$("#divCaptain").load(url);
						}
					}
				});
			} else {
				$("#dvfleetCaptainMdmForm").html(data);
			}
			isSaving = false;
			$("#btnSaveFleetCM").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#fIRefDocsTable"))) {
		$("#btnSaveFleetCM").removeAttr("disabled");
		$("#spDocSizeErrMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}
</script>
</head>
<body>
<div class="formDivBigForms" id="dvfleetCaptainMdmForm" >
	<form:form method="POST" commandName="fleetCaptainMdm" id="fleetCaptainMdmForm">
		<form:hidden path="fpEbObjectId" id="hdnFdRefObjectId"/>
		<form:hidden path="fleetCaptainsJson" id="fleetCaptainsJson"/>
		<form:hidden path="referenceDocumentsJson" id="referenceDocumentsCMJson"/>
		<fieldset class="frmField_set">
			<legend>BC/MDM</legend>
			<div id="fleetCaptainTbl"></div>
			<table>
				<tr>
					<td colspan="12">
						<form:errors path="fleetCaptainErrMsg" cssClass="error" />
					</td>
				</tr>
			</table>
		</fieldset>
		<br>
		<fieldset class="frmField_set">
			<legend>Document</legend>
			<div id="fCMRefDocsTable"></div>
			<table>
				<tr>
					<td colspan="12">
						<form:errors path="referenceDocsMessage" cssClass="error" />
					</td>
				</tr>
				<tr>
					<td colspan="12"><span class="error" id="spDocSizeErrMsg"></span></td>
				</tr>
				<tr>
					<td colspan="12"><span class="error" id="referenceDocsErrMgs" style="margin-top: 12px;"></span></td>
				</tr>
			</table>
		</fieldset>
		<table class="frmField_set">
			<tr>
				<td align="right" colspan="2"><input type="button" id="btnSaveFleetCM"
					value="Save" onclick="saveFleetCM();" />
				</td>
			</tr>
		</table>
	</form:form>
</div>
</body>
</html>