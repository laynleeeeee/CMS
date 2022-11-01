<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Insurance Permit Renewal Form
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
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
var $iprTable = null;
var $documentsTable = null;
var fleetTypeId = null;
$(document).ready(function(){
	initializeIPRTbl();
	initializeDocumentsTbl();
	fleetTypeId = "${insurancePermitRenewalDto.fleetTypeId}";
	initIprTblColor();
});

function addClassColor($tr, className){
	$($tr).addClass(className);
	$($tr).find("input").addClass(className);
}

function removeClassColor($tr, className){
	$($tr).removeClass(className);
	$($tr).find("input").removeClass(className);
}

function initializeIPRTbl() {
	var refDocsJson = JSON.parse($("#insurancePermitRenewalJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$iprTable = $("#iprTableId").editableItem({
		data: refDocsJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "refNo", "varType" : "string"},
				{"name" : "description", "varType" : "string"},
				{"name" : "issuanceDate", "varType" : "date"},
				{"name" : "expirationDate", "varType" : "date"},
				{"name" : "remarks", "varType" : "string"},
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
			{"title" : "Ref. No.",
				"cls" : "refNo tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "35%" },
			{"title" : "Issuance Date",
				"cls" : "issuanceDate tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "12%" },
			{"title" : "Expiration Date",
				"cls" : "expirationDate tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "12%" },
			{"title" : "Remarks",
				"cls" : "remarks tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "35%" }
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});

	$("#iprTableId").on("blur", ".expirationDate", function(){
		checkAndchangedColorTr($(this));
		evalDateByTxtBox($(this).closest("td").find(".expirationDate"));
	});

	$("#iprTableId").on("blur", ".issuanceDate", function(){
		evalDateByTxtBox($(this).closest("td").find(".issuanceDate"));
	});
}

function initIprTblColor(){
	$('#iprTableId tbody tr').each(function(){
		checkAndchangedColorTr($(this).find(".expirationDate"));
	});
}

function checkAndchangedColorTr($td){
	var enteredDate = new Date($($td).val());
	enteredDate.setMonth(enteredDate.getMonth());

	var monthsToAdd = fleetTypeId == "1" ? 1 : 3;

	// Get the date 3 months prior to current date.
	var priorDate = new Date(new Date().setMonth(new Date().getMonth() + monthsToAdd));

	// Check if the date entered is valid.
	if(!isNaN(enteredDate.getTime()) && !isNaN(priorDate.getTime())){
		if(enteredDate <= priorDate){
			addClassColor($($td).closest("tr"), "tdRed");
		} else {
			removeClassColor($($td).closest("tr"), "tdRed");
		}
	}
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJsonId").val());
	var cPath = "${pageContext.request.contextPath}";
	$documentsTable = $("#documentsTable").editableItem({
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
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
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
			{"title" : "Upload Image",
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

	$("#documentsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize,$("#referenceDocsMgs"), $("#documentsTable"));
	});

	$("#documentsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

var isSaving = false;
function saveIprForm(){
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsTable"))) {
		isSaving = true;
		var tempFleetTypeId = fleetTypeId;
		$("#referenceDocumentsJsonId").val($documentsTable.getData());
		$("#insurancePermitRenewalJson").val($iprTable.getData());
		$("#btnIprSave").attr("disabled", "disabled");
		doPostWithCallBack ("insurancePermitRenewalForm", "divInsurance", function (data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if(isSuccessfullySaved) {
				$("#successDialog").dialog({
					modal: true,
					buttons: {
						Close: function() {
							$(this).dialog("close");
							$(this).dialog("destroy");
							var url = contextPath + "/insurancePermitRenewal/?refObjectId="+fpRefObjectId;
							$("#divInsurance").load(url);
						}
					}
				});
			} else {
				var fleetTypeId = tempFleetTypeId;
				dojo.byId("iprFormId").innerHTML = data;
				initializeIPRTbl();
				initializeDocumentsTbl();
				initIprTblColor();
			}
			isSaving = false;
			$("#btnIprSave").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}
</script>
<title>Fleet Insurance Permit and Renewals Form</title>
</head>
<body>
	<div class="formDivBigForms" id="iprFormId" >
		<form:form method="POST" commandName="insurancePermitRenewalDto" id="insurancePermitRenewalForm">
			<form:hidden path="fpEbObjectId"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJsonId" />
			<form:hidden path="insurancePermitRenewalJson" id="insurancePermitRenewalJson" />
			<div>
			<fieldset class="frmField_set">
				<legend>Details</legend>
				<div id="iprTableId"></div>
				<table>
					<tr>
						<td colspan="12">
							<form:errors path="fleetIprErrMsg" cssClass="error"
								style="margin-top: 12px;" />
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
				<legend>Documents Table</legend>
				<div id="documentsTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<form:errors path="referenceDocsMessage" cssClass="error"
								style="margin-top: 12px;" />
						</td>
					</tr>
					<tr>
						<td colspan="12"><span class="error" id="spDocSizeMsg"></span></td>
					</tr>
					<tr>
						<td colspan="12"><span class="error" id="referenceDocsMgs" style="margin-top: 12px;"></span></td>
					</tr>
				</table>
				</fieldset>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right" colspan="2"><input type="button" id="btnIprSave"
							value="Save" onclick="saveIprForm();" />
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>