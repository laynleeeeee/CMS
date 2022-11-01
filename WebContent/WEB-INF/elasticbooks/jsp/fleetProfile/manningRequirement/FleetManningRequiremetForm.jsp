<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Manning Requirements Form
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
	background-color: red;
}

</style>
<script type="text/javascript">
var $documentsManningTable = null;
$(document).ready(function(){
	initializeDocumentsTbl();
});

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJsonManning").val());
	var cPath = "${pageContext.request.contextPath}";
	$documentsManningTable = $("#documentsManningTable").editableItem({
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
			{"title" : "Name",
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

	$("#documentsManningTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize,$("#referenceDocsMgs"), $("#documentsManningTable"));
	});

	$("#documentsManningTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsManningTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

var isSaving = false;
function saveManningForm(){
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsManningTable"))) {
		isSaving = true;
		$("#referenceDocumentsJsonManning").val($documentsManningTable.getData());
		$("#btnManningSave").attr("disabled", "disabled");
		doPostWithCallBack ("manningForm", "divManningRequirements", function (data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if(isSuccessfullySaved) {
				$("#successDialog").dialog({
					modal: true,
					buttons: {
						Close: function() {
							$(this).dialog("close");
							$(this).dialog("destroy");
						}
					}
				});
				cancelManningForm();
				searchManningRequirements();
			} else {
				dojo.byId("manningFormId").innerHTML = data;
				initializeDocumentsTbl();
			}
			isSaving = false;
			$("#btnManningSave").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#documentsManningTable"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function cancelManningForm() {
	$("#divManningRequirementForm").html("");
}
</script>
<title>Fleet Manning Requirements Form</title>
</head>
<body>
	<div class="formDivBigForms" id="manningFormId" >
		<form:form method="POST" commandName="manningRequirementDto" id="manningForm">
			<form:hidden path="fpEbObjectId"/>
			<form:hidden path="manningRequirement.id"/>
			<form:hidden path="manningRequirement.createdBy"/>
			<form:hidden path="manningRequirement.createdDate"/>
			<form:hidden path="manningRequirement.ebObjectId"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJsonManning" />
			<div class="modForm">
			<fieldset class="frmField_set">
				<legend>Basic Information</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Department</td>
						<td class="value">
							<form:select path="manningRequirement.department" id="department" class="frmSelectClass">
							<c:forEach var="department" items="${departments}">
								<form:option value="${department}">${department}</form:option>
							</c:forEach>
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="manningRequirement.department" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Position</td>
						<td class="value">
							<form:input path="manningRequirement.position" cssClass="input" id="position"/>
						</td>
					<tr>
						<td></td>
						<td colspan="2" class="value"><form:errors path="manningRequirement.position" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">License</td>
						<td class="value">
							<form:input path="manningRequirement.license" cssClass="input" id="license"/>
						</td>
					<tr>
						<td></td>
						<td colspan="2" class="value"><form:errors path="manningRequirement.license" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Number</td>
						<td class="value">
							<form:input path="manningRequirement.number" cssClass="input" id="number"/>
						</td>
					<tr>
						<td></td>
						<td colspan="2" class="value"><form:errors path="manningRequirement.number" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Remarks</td>
						<td class="value">
							<form:textarea path="manningRequirement.remarks" cssClass="input" id="remarks"/>
						</td>
					<tr>
						<td></td>
						<td colspan="2" class="value"><form:errors path="manningRequirement.remarks" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Active</td>
						<td class="value"><form:checkbox path="manningRequirement.active"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2" class="value"><form:errors path="manningRequirement.active" cssClass="error"/></td>
					</tr>
				</table>
				<br>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Documents Table</legend>
				<div id="documentsManningTable"></div>
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
						<td align="right" colspan="2"><input type="button" id="btnManningSave"
							value="Save" onclick="saveManningForm();" />
							<input type="button" id="btnCancelSave" value="Cancel" onclick="cancelManningForm();"/>
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>