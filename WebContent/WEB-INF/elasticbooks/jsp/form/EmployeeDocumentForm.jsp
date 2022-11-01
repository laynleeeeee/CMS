<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Employee Leave Credit form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
var $documentsTable = null;

$(document).ready(function(){

	var isEdit = "${employeeDocument.id}" != 0;
	if(isEdit){
		disableCompany();
	} else {
		enableCompany();
	}

	$("#txtEmployee").val("${employeeDocument.employee.fullName}");
	initializeDocumentsTbl();

	if ("${employeeDocument.formWorkflow.complete}" == "true" || "${employeeDocument.formWorkflow.currentStatusId}" == 4) {
		$("#employeeDocumentForm :input").attr("disabled","disabled");
	}
});

function disableCompany() {
	$("#companyId").attr("disabled", "disabled");
}

function enableCompany() {
	$("#companyId").removeAttr("disabled");
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentJson").val());
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

	$("#documentsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize, $("#referenceDocsMgs"), $("#documentsTable"));
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

function showEmployees () {
	var employeeName = encodeURIComponent($.trim($("#txtEmployee").val()));
	var uri = contextPath + "/getEmployees/byName?companyId="+$("#companyId").val()+"&name="+employeeName;
	$("#txtEmployee").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnEmployeeId").val(ui.item.id);
			$("#spanEmployeeError").text("");
			var employeeName = ui.item.fullName;
			$(this).val(employeeName);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					$("#spanEmployeeError").text("");
					if (ui.item != null) {
						var employeeName = ui.item.fullName
						$(this).val(employeeName);
					}
				},
				error : function(error) {
					maxAllowableAmount = null;
					$("#spanEmployeeError").text("Invalid employee.");
					$("#txtEmployee").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		var employeeName = item.fullName;
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + employeeName + "</a>" )
			.appendTo( ul );
	};
}

var isSaving = false;
function saveEmployeeDocument() {
	var companyId = $("#companyId").val();
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		enableCompany();
		$("#referenceDocumentJson").val($documentsTable.getData());
		doPostWithCallBack ("employeeDocumentForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var employee = $("#txtEmployee").val();
				var status = $("#txtEmployeeDocumentStatus").val();
				if("${employeeDocument.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				}
				else {
					dojo.byId("editForm").innerHTML = data;
					disableCompany();
					$("#txtEmployeeDocumentStatus").val(status);
				}
				$("#txtEmployee").val(employee);
				$("#companyId").val(companyId);
				initializeDocumentsTbl();
				isSaving = false;
			}
			$("#btnSaveED").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function checkEmployee($employee) {
	var name = $.trim($($employee).val());
	if ($.trim($($employee).val()) == "") {
		$("#hdnEmployeeId").val("");
	}
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="employeeDocument"
			id="employeeDocumentForm">

			<div class="modFormLabel">
				Employee Document <span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId" />
			<form:hidden path="createdBy" />
			<form:hidden path="createdDate" />
			<form:hidden path="updatedBy" />
			<form:hidden path="formWorkflowId" />
			<form:hidden path="sequenceNo" />
			<form:hidden path="employeeId" id="hdnEmployeeId" />
			<form:hidden path="referenceDocumentJson" id="referenceDocumentJson" />
			<form:hidden path="ebObjectId" />
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number</td>
							<td class="value"><input type="text" id="txtSequenceNumber"
								class="textBoxLabel" readonly="readonly"
								value="${employeeDocument.sequenceNo}" /></td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status"
								value="${employeeDocument.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text"
								id="txtEmployeeDocumentStatus" class="textBoxLabel"
								readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Employee Document Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company/Branch</td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass"
									items="${companies}" itemLabel="name" itemValue="id" >
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="companyId" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels"> Date</td>
							<td class="value"><form:input path="date" id="date"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')" style="cursor: pointer"
								style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="date" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>

						<tr>
							<td class="labels">Employee</td>
							<td class="value">
								<input id="txtEmployee" class="input" onkeydown="showEmployees();" onkeyup="showEmployees();" onblur="checkEmployee(this);">
							</td>
						</tr>

						<tr>
							<td></td>
							<td colspan="2">
								<span id="spanEmployeeError" class="error" style="margin-left: 12px;"></span>
								<form:errors path="employeeId" cssClass="error" />
							</td>
						</tr>
						
						<tr>
							<td class="labels"> Document Type</td>
							<td class="value">
								<form:select path="documentTypeId"
									id="selDocumentTypes" cssClass="frmSelectClass">
									<form:options items="${documentTypes}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="documentTypeId" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>

						<tr>
							<td class="labels">Remarks</td>
							<td class="value">
								<form:textarea path="remarks" class="input" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="remarks" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
					</table>
				</fieldset>

				<fieldset class="frmField_set">
				<legend>Document</legend>
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
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right" colspan="2"><input type="button" id="btnSaveED"
							value="Save" onclick="saveEmployeeDocument();" />
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>