<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description: Personnel action notice form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebForm.css"media="all">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"media="all">
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
	if ("${personnelActionNotice.id}" != 0) {
		disableSlctField();
		$("#txtEmployee").val("${personnelActionNotice.employeeFullName}");
		$("#spanPositionId").text("${personnelActionNotice.employeePosition}");
		$("#spanDivisionId").text("${personnelActionNotice.employeeDivision}");
		$("#spanDateHired").text("${personnelActionNotice.hiredDate}");
	}
	initializeDocumentsTbl();

	if ("${personnelActionNotice.formWorkflow.complete}" == "true" 
			|| "${personnelActionNotice.formWorkflow.currentStatusId}" == 4) {
		$("#personnelActionNoticeFormId :input").attr("disabled","disabled");
		$("#btnSave").attr("disabled", "disabled");
	}
});

function disableSlctField() {
	$("#companyId").attr("disabled", "disabled");
}

function enableSlctField() {
	$("#companyId").removeAttr("disabled");
}

function getEmployee () {
	var companyId = $("#companyId").val();
	var txtEmployee = encodeURIComponent($.trim($("#txtEmployee").val()));
	var uri = "/getEmployees/bycCompanyAndName?companyId="+ companyId + "&name=" + txtEmployee;
	if(txtEmployee == "") {
		$("#hdnEmployeeId").val("");
	}
	$.ajax({
		url: contextPath + uri,
		success : function(employee) {
			if (txtEmployee != ""){
				$("#spanEmployeeError").text("");
				$("#spanEmployeeReqError").html("");
				if (employee != null && employee[0] != undefined) {
					$("#hdnEmployeeId").val(employee[0].id);
				} else {
					$("#spanEmployeeError").text("Invalid employee.");
					$("#hdnEmployeeId").val("");
					if ($("#spanEmployeeError").text() != "") {
						$("#spanEmployeeReqError").html("");
					}
				}
			}
			getEmployeePosition();
			getEmployeeDivision();
			getHiredDate();
		},
		error : function(error) {
			$("#spanEmployeeError").text("Invalid employee.");
			$("#hdnEmployeeId").val("");
		},
		dataType: "json"
	});
}

function showEmployees () {
	var companyId = $("#companyId").val();
	var employeeName = encodeURIComponent($.trim($("#txtEmployee").val()));
	var uri = contextPath + "/getEmployees/byName?companyId="+ companyId + "&name=" + employeeName;
	$("#txtEmployee").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnEmployeeId").val(ui.item.id);
			var employeeName = ui.item.lastName + ", "
					+ ui.item.firstName + " " + ui.item.middleName;
			$(this).val(employeeName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + item.fullName + "</a>" )
			.appendTo( ul );
	};
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJson").val());
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

function getEmployeePosition() {
	var employeeId = $("#hdnEmployeeId").val();
	var uri = "/getEmployees/getPosition?employeeId="+employeeId;
	if (employeeId != "" && employeeId != undefined) {
		$.ajax({
			url: contextPath + uri,
			success : function(position) {
				if (position != null && typeof position != undefined) {
					console.log(position);
					$("#spanPositionId").text(position.name);
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$("#spanPositionId").text("");
	}
}

function getEmployeeDivision() {
	var employeeId = $("#hdnEmployeeId").val();
	var uri = "/getEmployees/getDivision?employeeId="+employeeId;
	if (employeeId != "" && employeeId != undefined) {
		$.ajax({
			url: contextPath + uri,
			success : function(division) {
				if (division != null && typeof division != undefined) {
					console.log(division);
					$("#spanDivisionId").text(division.name);
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$("#spanDivisionId").text("");
	}
}

function getHiredDate() {
	var employeeId = $("#hdnEmployeeId").val();
	var uri = "/getEmployees/getHiredDate?employeeId="+employeeId;
	if (employeeId != "" && employeeId != undefined) {
		$.ajax({
			url: contextPath + uri,
			success : function(hiredDate) {
				$("#spanDateHired").text(hiredDate);
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "text"
		});
	} else {
		$("#spanDateHired").text("");
	}
}

function changeCompany() {
	$("#hdnEmployeeId").val("")
	$("#txtEmployee").val("");
	$("#spanPositionId").text("");
	$("#spanDivisionId").text("");
	$("#spanDateHired").text("");
}

var isSaving = false;
function saveForm(){
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		enableSlctField();
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#btnSave").attr("disabled", "disabled");
		doPostWithCallBack ("personnelActionNoticeFormId", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var status = $("#txtPANStatus").val();
				if("${personnelActionNotice.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtPANStatus").val(status);
					disableSlctField();
				}
				if($("#hdnEmployeeId").val() == null || $("#hdnEmployeeId").val() == "") {
					$("#txtEmployee").val("");
				}
				initializeDocumentsTbl();
				getEmployeePosition();
				getEmployeeDivision();
				getHiredDate();
				isSaving = false;
			}
			$("#btnSave").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}
</script>
</head>
<body>
	<div class="formDivBigForms">
		<form:form method="POST" commandName="personnelActionNotice" id="personnelActionNoticeFormId">
			<div class="modFormLabel">Personnel Action Notice 
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId" />
			<form:hidden path="createdBy" />
			<form:hidden path="createdDate" />
			<form:hidden path="updatedBy" />
			<form:hidden path="formWorkflowId" />
			<form:hidden path="sequenceNo" />
			<form:hidden path="employeeId" id="hdnEmployeeId" />
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson" />
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number</td>
							<td class="value">
								<c:choose>
									<c:when test="${personnelActionNotice.id == 0 }">
										<input type="text" id="txtSequenceNo"
											class="textBoxLabel" readonly="readonly" />
									</c:when>
									<c:otherwise>
										${personnelActionNotice.sequenceNo}
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status"
								value="${personnelActionNotice.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text" id="txtPANStatus"
								class="textBoxLabel" readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Personnel Action Notice Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company/Branch </td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass" onChange="changeCompany();">
									<form:options items="${companies}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Employee</td>
							<td class="value">
								<form:input path="employeeFullName" id="txtEmployee" class="input" onkeydown="showEmployees();"
								onkeyup="showEmployees();" onblur="getEmployee();" value="${personnelActionNotice.employee.fullName}" />
							</td>
						</tr>
	
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanEmployeeError" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors id="spanEmployeeReqError" path="employeeId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">Position</td>
							<td class="value"><span id="spanPositionId"></span></td>
						 </tr>
						 <tr>
							<td class="labels">Division/Department</td>
							<td class="value"><span id="spanDivisionId"></span></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Action Notice</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Date</td>
							<td class="value"><form:input path="date" id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')" style="cursor: pointer"
								style="float: right;" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="date" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">Date Hired</td>
							<td class="value"><span id="spanDateHired"></span></td>
						</tr>
						<c:choose>
							<c:when test="${personnelActionNotice.id == 0 }">
								<tr>
							<td colspan="2" class="labels" style="padding-left: 94px; width: 600px">
								This is to notify all concerned of the respective action taken 
								for the above-mentioned employee.</td>
							</tr>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan="3" class="labels" style="padding-left: 146px; width: 600px; text-align: left;">
										This is to notify all concerned of the respective action taken 
										for the above-mentioned employee.</td>
								</tr>
							</c:otherwise>
						</c:choose>
						<tr>
							<td class="labels">* Type of Action Notice</td>
							<td class="value">
								<form:select path="actionNoticeId" id="actionNoticeId" cssClass="frmSelectClass" >
									<form:options items="${actionNotices}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="actionNoticeId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">Justification</td>
							<td class="value"><form:textarea path="justification" id="description" class="input" /></td>
						</tr>
					</table>
				</fieldset>
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
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right" colspan="2"><input type="button" id="btnSave"
							value="Save" onclick="saveForm();" />
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>