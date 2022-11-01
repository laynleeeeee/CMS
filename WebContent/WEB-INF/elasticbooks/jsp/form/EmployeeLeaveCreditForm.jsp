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
var $leavesTable = null;
var prevAvailableLeaves = null;
var isEdit = false;

$(document).ready(function(){
	if("${employeeLeaveCredit.id}" != 0){
		isEdit = true;
		disableCompany();
	}
	if ("${employeeLeaveCredit.formWorkflow.complete}" == "true" || "${employeeLeaveCredit.formWorkflow.currentStatusId}" == 4) {
		$("#employeeLeaveCreditForm :input").attr("disabled","disabled");
	}
	initializeDocumentsTbl();
	InitializeLeavesTbl();
	$(".availableLeaves").css("float", "right");
});

function getEmployee(object){
	var companyId = $("#companyId").val();
	var row = $(object).parent().closest("tr");
	var $employeeName =  $(row).find(".employeeName");
	var uri = "/getEmployees/bycCompanyAndName?companyId="+ companyId + "&name=" + encodeURIComponent($employeeName.val());
	if($employeeName.val() == ""){
		$(row).find(".employeeId").val("");
	}
	$.ajax({
		url: contextPath + uri,
		success : function(employee) {
			if ($.trim($employeeName.val()) != ""){
				if (employee != null && employee[0] != undefined) {
					$("#errorLeaves").css("color", "red").text('');
					$(row).find(".employeeId").val(employee[0].id);
					var typeOfLeaveId = $("#typeOfLeaveId").val();
					getAvailableLeaves(object, typeOfLeaveId, employee[0].id);
				} else {
					$(row).find(".employeeId").val("");
					$(row).find(".availableLeaves").html("");
					$("#errorLeaves").css("color", "red").text('Invalid Employee.');
				}
			}
		},
		error : function(error) {
			$(row).find(".employeeId").val("");
			$(row).find(".availableLeaves").html("");
			$("#errorLeaves").css("color", "red").text('Invalid Employee.');
			console.log(error);
		},
		dataType: "json"
	});
}

function showEmployees(object){
	var companyId = $("#companyId").val();
	var row = $(object).parent().closest("tr");
	var $employee =  $(row).find(".employeeName");
	var uri = contextPath + "/getEmployees/byName?companyId="+companyId
			+"&name="+encodeURIComponent($.trim($employee.val()))+"&divisionId="+$("#divisionId").val();
	$(object).autocomplete({
		source: uri,
		select: function( event, ui ) {
			employee = ui.item.lastName + ", " 
			+ ui.item.firstName + " " + ui.item.middleName
			$(this).val(employee);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(employee);
					}
				},
				error : function(error) {
					console.log(error)
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>"+ item.lastName + ", " 
				+ item.firstName + " " + item.middleName + "</a>" )
			.appendTo( ul );
	};
}

function getAvailableLeaves(object, typeOfLeaveId, employeeId){
	var uri = contextPath + "/getEmployees/getAvailableLeave?employeeId="+employeeId+"&typeOfLeaveId="+Number(typeOfLeaveId)+"&isRequestForm=false";
	var $availableLeaves = $(object).closest("tr").find(".availableLeaves");
	if(typeOfLeaveId != "" && employeeId != "") {
		$.ajax({
			url: uri,
			success : function(totalLeaves) {
				$availableLeaves.css("float", "right");
				$availableLeaves.text(totalLeaves);
				prevAvailableLeaves = totalLeaves;
			},error : function(error) {
				$availableLeaves.html("");
				console.log(error);
			},
			dataType: "text"
		});
	} 
}

function InitializeLeavesTbl() {
	var leavesJson = JSON.parse($("#leavesJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$leavesTable = $("#leavesTable").editableItem({
		data: leavesJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "referenceObjectId", "varType" : "int"},
				{"name" : "employeeId", "varType" : "int"},
				{"name" : "employeeName", "varType" : "string"},
				{"name" : "availableLeaves", "varType" : "double"},
				{"name" : "deductDebit", "varType" : "double"},
				{"name" : "addCredit", "varType" : "double"},
		],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "employeeId",
				"cls" : "employeeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Employee Name",
				"cls" : "employeeName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "40%" },
			{"title" : "Available <br> Leaves", 
				"cls" : "availableLeaves tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "20%" },
			{"title" : "Deduct(Debit)", 
				"cls" : "deductDebit tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "20%" },
			{"title" : "Add(Credit)", 
				"cls" : "addCredit tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "20%" }
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});
	
	$("#leavesTable").on("keydown keyup", ".employeeName", function(){
		showEmployees(this);
	});
	
	$("#leavesTable").on("blur", ".employeeName", function(){
		getEmployee(this);
		var row = $(this).parent().closest("tr");
		if($(this).val() != ""){
			var employeeId = $(row).find(".employeeId").val();
			var typeOfLeaveId = $("#typeOfLeaveId").val();
			getAvailableLeaves(this, typeOfLeaveId, employeeId);
		}
	});
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocJson").val());
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

function disableCompany() {
	$("#companyId").attr("disabled", "disabled");
}

function enableCompany() {
	$("#companyId").removeAttr("disabled");
}

var isSaving = false;
function saveEmployeeLeaveCredit(){
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		enableCompany();
		$("#leavesJson").val($leavesTable.getData());
		$("#referenceDocJson").val($documentsTable.getData());
		$("#btnSave").attr("disabled", "disabled");
		doPostWithCallBack ("employeeLeaveCreditForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				if(!isEdit){
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				initializeDocumentsTbl();
				InitializeLeavesTbl();
				$(".availableLeaves").css("float", "right");
				isSaving = false;
			}
			$("#btnSave").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function CompanyIdOnClick(){
 	$documentsTable.emptyTable();
 	$leavesTable.emptyTable();
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="employeeLeaveCredit"
			id="employeeLeaveCreditForm">

			<div class="modFormLabel">
				Employee Leave Credit <span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId" />
			<form:hidden path="createdBy" />
			<form:hidden path="createdDate" />
			<form:hidden path="updatedBy" />
			<form:hidden path="formWorkflowId" />
			<form:hidden path="sequenceNumber" />
			<form:hidden path="leavesJson" id="leavesJson" />
			<form:hidden path="referenceDocJson" id="referenceDocJson" />
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number</td>
							<td class="value"><input type="text" id="txtSequenceNumber"
								class="textBoxLabel" readonly="readonly"
								value="${employeeLeaveCredit.sequenceNumber}" /></td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status"
								value="${employeeLeaveCredit.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text"
								id="txtEmployeeLeaveCreditStatus" class="textBoxLabel"
								readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Employee Leave Credit Header</legend>
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
							<td></td>
							<td colspan="2"><form:errors path="date" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Leave Type</td>
							<td class="value">
								<form:select path="typeOfLeaveId" id="typeOfLeaveId" cssClass="frmSelectClass"
									items="${typeOfLeaves}" itemLabel="name" itemValue="id" >
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="typeOfLeave" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">Remarks</td>
							<td class="value"><form:textarea path="remarks"
									id="description" class="input" /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Leaves Table</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company/Branch</td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass" onchange="CompanyIdOnClick();"
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
							<td class="labels">* Division/Department</td>
							<td class="value">
								<form:select path="divisionId" id="divisionId" cssClass="frmSelectClass" onchange="CompanyIdOnClick();"
									items="${divisions}" itemLabel="name" itemValue="id" >
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="divisionId" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
					</table>
					<div id="leavesTable"></div>
					<table>
						<tr>
							<td colspan="12">
								<span id="errorLeaves" class="error">
								<form:errors path="leavesMessage" cssClass="error"/>
								</span>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
				<legend>Documents Table</legend>
				<div id="documentsTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<form:errors path="referenceDocsMessage" cssClass="error" />
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
							value="Save" onclick="saveEmployeeLeaveCredit();" />
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>