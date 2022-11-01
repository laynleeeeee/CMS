<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Tool
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
$(document).ready(function(){
	initializeDocumentsTbl();
	
});

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsToolsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$fTRefDocsTable = $("#fTRefDocsTable").editableItem({
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

	$("#fTRefDocsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize,$("#referenceDocsErrMgs"), $("#fTRefDocsTable"));
	});

	$("#fTRefDocsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#fTRefDocsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}
</script>
</head>
<body>
<div class="formDivBigForms" id="dvFleetToolForm" >
	<form:form method="POST" commandName="fleetToolDto" id="fleetToolForm">
		<form:hidden path="fpEbObjectId" id="hdnToolRefObjectId"/>
		<form:hidden path="fleetToolConditionsJson" id="fleetToolConditionsJson"/>
		<form:hidden path="referenceDocumentsJson" id="referenceDocumentsToolsJson"/>
		<form:hidden path="pageNumber" id="hdnToolPageNumber"/>
		<fieldset class="frmField_set">
			<legend>Tools</legend>
			<div id="fleetToolsTbl" class="ebDataTable">
				<%@ include file="FleetToolItem.jsp"%>
			</div>
			<table>
				<tr>
					<td colspan="12">
						<form:errors path="fleetToolsErrMsg" id="fleetToolsErrMsg" cssClass="error" />
					</td>
				</tr>
			</table>
		</fieldset>
		<br />
		<fieldset class="frmField_set">
			<legend>Document</legend>
			<div id="fTRefDocsTable"></div>
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
				<td align="right" colspan="2"><input type="button" id="btnSaveFleetTools"
					value="Save" onclick="saveFleetTools();" />
				</td>
			</tr>
		</table>
	</form:form>
</div>
</body>
</html>