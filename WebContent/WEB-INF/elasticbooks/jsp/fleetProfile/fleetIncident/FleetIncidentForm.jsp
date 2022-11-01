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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript">
var $fleetIncidentTbl = null;
var $fIRefDocsTable = null;

$(document).ready(function(){
	initializeFleetIncident();
	initializeDocumentsTbl();
});

function initializeFleetIncident() {
	var fleetIncidentJson = JSON.parse($("#fleetIncidentJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$fleetIncidentTbl = $("#fleetIncidentTbl").editableItem({
		data: fleetIncidentJson,
		jsonProperties: [
					{"name" : "id", "varType" : "int"},
					{"name" : "ebObjectId", "varType" : "int"},
					{"name" : "referenceObjectId", "varType" : "int"},
					{"name" : "date", "varType" : "date"},
					{"name" : "employeeName", "varType" : "string"},
					{"name" : "location", "varType" : "string"},
					{"name" : "cause", "varType" : "string"},
					{"name" : "insuranceClaims", "varType" : "string"},
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
			{"title" : "Employee Name",
				"cls" : "employeeName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "15%" },
			{"title" : "Date",
				"cls" : "date tblInputText",
				"editor" : "datePicker",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Location",
				"cls" : "location tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Cause",
				"cls" : "cause tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Insurance Claims",
				"cls" : "insuranceClaims tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "25%" },
			{"title" : "Remarks",
				"cls" : "remarks tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "45%" }

		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});

	$("#fleetIncidentTbl").on("blur", ".date", function(){
		evalDateByTxtBox($(this).closest("td").find(".date"));
	});
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJsonFI").val());
	var cPath = "${pageContext.request.contextPath}";
	$fIRefDocsTable = $("#fIRefDocsTable").editableItem({
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

	$("#fIRefDocsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize,$("#referenceDocsErrMgs"), $("#fIRefDocsTable"));
	});

	$("#fIRefDocsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#fIRefDocsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

var isSaving = false;
function saveFleetIncident(){
	$("#hdnFdRefObjectId").val(fpRefObjectId);
	$("#fleetIncidentJson").val($fleetIncidentTbl.getData());
	$("#referenceDocumentsJsonFI").val($fIRefDocsTable.getData());
	$("#btnSaveFleetIncident").attr("disabled", "disabled");
	if(isSaving == false && $("#referenceDocsErrMgs").html() == "" && !checkExceededFileSize($("#fIRefDocsTable"))) {
		isSaving = true;
		doPostWithCallBack ("fleetIncidentForm", "dvfleetIncidentForm", function (data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if (isSuccessfullySaved) {
				$("#dvfleetIncidentForm").html("");
				$("#successDialog").dialog({
					modal: true,
					buttons: {
						Close: function() {
							$(this).dialog("close");
							$(this).dialog("destroy");
							var url = contextPath + "/fleetIncident/?refObjectId="+fpRefObjectId;
							$("#divIncident").load(url);
						}
					}
				});
			} else {
				$("#dvfleetIncidentForm").html(data);
			}
			isSaving = false;
			$("#btnSaveFleetIncident").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#fIRefDocsTable"))) {
		$("#btnSaveFleetIncident").removeAttr("disabled");
		$("#spDocSizeErrMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}
</script>
</head>
<body>
<div class="formDivBigForms" id="dvfleetIncidentForm" >
	<form:form method="POST" commandName="fleetIncident" id="fleetIncidentForm">
		<form:hidden path="fpEbObjectId" id="hdnFdRefObjectId"/>
		<form:hidden path="fleetIncidentJson" id="fleetIncidentJson"/>
		<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJsonFI"/>
		<fieldset class="frmField_set">
			<legend>Incident</legend>
			<div id="fleetIncidentTbl"></div>
			<table>
				<tr>
					<td colspan="12">
						<form:errors path="fleetIncidentErrorMsg" cssClass="error" />
					</td>
				</tr>
			</table>
		</fieldset>
		<br>
		<fieldset class="frmField_set">
			<legend>Document</legend>
			<div id="fIRefDocsTable"></div>
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
				<td align="right" colspan="2"><input type="button" id="btnSaveFleetIncident"
					value="Save" onclick="saveFleetIncident();" />
				</td>
			</tr>
		</table>
	</form:form>
</div>
</body>
</html>