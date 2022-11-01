<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Profile Form
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<style>

#btnSaveFleetDriver, #btnCancelFleetDriver {
	font-weight: bold;
}

.tdRed{
	color: red;
}

</style>
<script type="text/javascript">
$(document).ready(function () {
	initTables();
});

function initTables () {
	initDrivers();
	initializeDocumentsTbl();
	initDriverTblColor();
}

function addClassColor($tr, className){
	$($tr).addClass(className);
	$($tr).find("input").addClass(className);
}

function removeClassColor($tr, className){
	$($tr).removeClass(className);
	$($tr).find("input").removeClass(className);
}

function checkAndchangedColorTr($td){
	var enteredDate = new Date($($td).val());
	enteredDate.setMonth(enteredDate.getMonth());

	// Get the date 1 months prior to current date.
	var priorDate = new Date(new Date().setMonth(new Date().getMonth() + 1));

	// Check if the date entered is valid.
	if(!isNaN(enteredDate.getTime()) && !isNaN(priorDate.getTime())){
		if(enteredDate <= priorDate){
			addClassColor($($td).closest("tr"), "tdRed");
		} else {
			removeClassColor($($td).closest("tr"), "tdRed");
		}
	}
}

function initDrivers() {
	var fleetDriversJson = JSON.parse($("#fleetDriversJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$fleetDriverTable = $("#fleetDriverTable").editableItem({
		data: fleetDriversJson,
		jsonProperties: [
                 {"name" : "ebObjectId", "varType" : "int"},
                 {"name" : "date", "varType" : "date"},
                 {"name" : "name", "varType" : "string"},
                 {"name" : "licenseNo", "varType" : "string"},
                 {"name" : "expirationDate", "varType" : "date"},
                 {"name" : "remarks", "varType" : "string"},
        ],
		contextPath: cPath,
		header: [
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Date",
				"cls" : "date tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Driver",
				"cls" : "name tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "25%" },
			{"title" : "License No.",
				"cls" : "licenseNo tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "25%" },
			{"title" : "Expiration Date",
				"cls" : "expirationDate tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Remarks",
				"cls" : "remarks tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "25%" },
			
	    ],
	    "disableDuplicateStockCode" : false,
        "itemTableMessage": ""
	});

	$("#fleetDriverTable").on("blur", ".date", function(){
		evalDateByTxtBox($(this).closest("td").find(".date"));
	});

	$("#fleetDriverTable").on("blur", ".expirationDate", function(){
		evalDateByTxtBox($(this).closest("td").find(".expirationDate"));
		checkAndchangedColorTr($(this));
	});
}

function initDriverTblColor(){
	$('#fleetDriverTable tbody tr').each(function(){
		checkAndchangedColorTr($(this).find(".expirationDate"));
	});
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$fDRefDocsTable = $("#fDRefDocsTable").editableItem({
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

	$("#fDRefDocsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize);
	});

	$("#fDRefDocsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#fDRefDocsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

function convertDocToBase64($fileObj, sizeinbytes) {
	$("#referenceDocsMgs").html("");
	var $fileName = $($fileObj).closest("tr").find(".fileName");
	$($fileName).val("");
	$($fileObj).closest("tr").find(".fileLink").html("");
	var value = $.trim($($fileObj).val());
	var fileNames = value.split("\\");
	var isDuplicate = false;
	var name = null;
	var names = null;
	$("#fDRefDocsTable tbody tr").each(function(){
		name = $.trim($(this).find(".fileName").val());
		if(fileNames.slice(-1)[0] == name){
			isDuplicate = true;
		}
	});
	if(isDuplicate){
		$("#referenceDocsMgs").html("Duplicate file.");
	} else {
		if (value != "") {
			var file = $($fileObj)[0].files[0];
			var $file = $($fileObj).closest("tr").find(".file");
			var $docName = $($fileObj).closest("tr").find(".docName");
			var FR= new FileReader();
			FR.onload = function(e) {
				$($file).val(e.target.result);
			};
			FR.onprogress = function (e){
				if (e.lengthComputable) {
					var percentLoaded = Math.round((e.loaded / e.total) * 100);
					if (percentLoaded < 100) {
						$($docName).html(percentLoaded + '%' + " loading...");
					}
				}
			}
			FR.onloadend = function (e) {
				var $td = $($fileObj).closest("tr").find(".docName").parent("td");
				$($docName).html("");
				$($fileName).val($.trim(fileNames.slice(-1)[0]));
				$($td).append("<a href='#' class='fileLink'>" + fileNames.slice(-1)[0] + "</a>");
			}
			FR.readAsDataURL( file );
		}
	}
}

function checkExceededFileSize () {
	var totalFileSize = 0;
	var FILE_INCREASE = 0.40;
	$("#fDRefDocsTable tbody tr").find(".fileSize").each(function(){
		if ($.trim($(this).val()) != "") {
			var fileSize = parseFloat(accounting.unformat($(this).val()));
			totalFileSize += (fileSize + (fileSize * FILE_INCREASE));
		}
	});
	// 14680064 = 10485760(10 MB) + (10485760 × .4)
	// Included the file increase
	if (totalFileSize > 14680064) {
		return true;
	}
	return false;
}

var isSaving = false;
function saveFleetDriver() {
	$("#hdnFdRefObjectId").val(fpRefObjectId);
	$("#hdnFpEbObjectId").val(fpRefObjectId);
	$("#fleetDriversJson").val($fleetDriverTable.getData());
	$("#referenceDocumentsJson").val($fDRefDocsTable.getData());
	if(isSaving == false) {
		isSaving = true;
		doPostWithCallBack ("fleetDriverForm", "divDriverForm", function(data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if(isSuccessfullySaved) {
				$("#divDriverForm").html("");
				$("#successDialog").dialog({
					modal: true,
					buttons: {
						Close: function() {
							$(this).dialog("close");
							$(this).dialog("destroy");
							var url = contextPath + "/fleetDriver/?refObjectId="+fpRefObjectId;
							$("#divDriver").load(url);
						}
					}
				});
			} else {
				$("#fdForm").html(data);
			}
			isSaving = false;
		});
	}
}
</script>
<title>Fleet Form</title>
</head>
<body>
<div class="formDivBigForms" id="fdForm" >
<form:form method="POST" commandName="fleetDriverDto" id="fleetDriverForm">
	<form:hidden path="fpEbObjectId" id="hdnFpEbObjectId"/>
	<form:hidden path="fleetDriversJson" id="fleetDriversJson"/>
	<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>

	<fieldset class="frmField_set">
		<legend>Driver Information</legend>
		<div id="fleetDriverTable"></div>
		<table>
			<tr>
				<td colspan="12">
					<form:errors path="fleetDriversErrMsg" cssClass="error"/>
				</td>
			</tr>
		</table>
	</fieldset>
	<br />
	<fieldset class="frmField_set">
		<legend>Document</legend>
		<div id="fDRefDocsTable"></div>
		<table>
			<tr>
				<td colspan="12">
					<form:errors path="referenceDocsMessage" cssClass="error"/>
				</td>
			</tr>
		</table>
	</fieldset>
	<br />
	<div class="frmField_set">
		<input type="button" id="btnSaveFleetDriver"  value="Save" style="float: right;" onclick="saveFleetDriver();" />
	</div>
</form:form>
</div>
</body>
</html>