<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Form Deduction form.
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
<style type="text/css">
.txtDate, .txtFDLAmount {
	width: 100%;
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}
</style>
<script type="text/javascript">
var minimumRows = 1;
var fdlIndex = "${fn:length(formDeduction.formDeductionLines)}";
var $documentsTable = null;
var formTitle = null;
var isEdit = false;
$(document).ready(function() {
	if ("${formDeduction.formWorkflow.complete}" == "true" || "${formDeduction.formWorkflow.currentStatusId}" == 4) {
		$("#formDeductionForm :input").attr("disabled","disabled");
	}
	initializeDocumentsTbl();
	isEdit = "${formDeduction.id}" != 0;
	setFormTitle("${formDeduction.formDeductionTypeId}");
	if(isEdit){
		$("#txtAmount").val(accounting.formatMoney("${formDeduction.totalDeductionAmount}"));
		$("#spanPositionId").html("${formDeduction.employeePosition}");
		disableCompany();
		computeAmount();
	} else {
		$("#tblDeductionLine").hide();
	}
	getEmployeeDivision();
});

function setFormTitle(typeId){
	if(typeId == 1){
		formTitle = "Authority to Deduct";
	} else {
		formTitle = "Cash Bond Contract";
	}
}

function addFDLLine (numOfRows, selectedIndex) {
	if((selectedIndex+1) == fdlIndex){
		for (var i=0; i<numOfRows; i++){
			var newRow = "<tr id='trReadOnly'>";

			//Delete icon
			newRow += "<td class='tdProperties' align='center'>";
			newRow += "<img class='imgPDelete' id='imgPDelete"+fdlIndex+"' src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deletePRow(this);' />";
			newRow += "</td>";

			newRow += "<td class='tdProperties'><span class='rowCount'>"+(Number(fdlIndex)+1)+"</span></td>";

			//Calendar
			newRow += "<td class='tdProperties' align='center'>";
			newRow += "<img class='imgCalendar' id='imgCalendar"+fdlIndex+"' src='${pageContext.request.contextPath}/images/cal.gif' onclick='pickDate(this)'/>";
			newRow += "</td>";

			//Date
			newRow += "<td class='tdProperties'>";
			newRow += "<input name='formDeductionLines["+fdlIndex+"].date' id='txtDate"+fdlIndex+"' class='txtDate' style='width: 100%;'/>";
			newRow += "</td>";

			//Amount
			newRow += "<td class='tdProperties'>";
			newRow += "<input name='formDeductionLines["+fdlIndex+"].amount' id='txtFDLAmount"+fdlIndex+"' class='txtFDLAmount' style='width: 100%; text-align: right;' onblur='formatMoney(this); computeAmount();' onfocus='addFDLLine(1,"+fdlIndex+");' />";
			newRow += "</td>";

			newRow += "</tr>";
			$("#tblDeductionLine tbody").append(newRow);
			fdlIndex++;
		}
	}
}

function initLines() {
	var amount = $("#txtAmount").val();
	var noOfPayroll = $("#txtNoOfPayroll").val();
	if(amount != "" && noOfPayroll != "") {
		$("#tblDeductionLine").show();
		loadLine(noOfPayroll, "");
	}
}

function loadLine(noPayrollDeduction, dates) {
	var date = $("#startDate").val();
	var amountToDeduct = $("#txtAmount").val();
	var uri = contextPath+"/formDeduction/1/deductionLine?date="+date+"&amountToDeduct="+accounting.unformat(amountToDeduct)+"&noPayrollDeduction="+noPayrollDeduction;
	if(dates != ""){
		uri += "&dates="+dates;
	}
	$.get(uri, function(data) {
		$("#tblDeductionLine tbody").html("");
		fdlIndex = 0;
		data = $.parseJSON(data);
		$.each(data, function(i, item) {
			var newRow = "<tr id='trReadOnly'>";

			//Delete icon
			newRow += "<td class='tdProperties' align='center'>";
			newRow += "<img class='imgPDelete' id='imgPDelete"+fdlIndex+"' src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deletePRow(this);' />";
			newRow += "</td>";

			newRow += "<td class='tdProperties'><span class='rowCount'>"+(Number(fdlIndex)+1)+"</span></td>";

			//Calendar
			newRow += "<td class='tdProperties' align='center'>";
			newRow += "<img class='imgCalendar' id='imgCalendar"+fdlIndex+"' src='${pageContext.request.contextPath}/images/cal.gif' onclick='pickDate(this)'/>";
			newRow += "</td>";

			//Date
			newRow += "<td class='tdProperties'>";
			newRow += "<input name='formDeductionLines["+fdlIndex+"].date' id='txtDate"+fdlIndex+"' class='txtDate' style='width: 100%;' value='"+item.deductionDate+"'/>";
			newRow += "</td>";

			//Amount
			newRow += "<td class='tdProperties'>";
			newRow += "<input name='formDeductionLines["+fdlIndex+"].amount' id='txtFDLAmount"+fdlIndex+"' value='"+accounting.formatMoney(item.amount)+"' class='txtFDLAmount' style='width: 100%; text-align: right;' onblur='formatMoney(this); computeAmount(false);' onfocus='addFDLLine(1,"+fdlIndex+");' />";
			newRow += "</td>";

			newRow += "</tr>";
			$("#tblDeductionLine tbody").append(newRow);
			fdlIndex++;
		});
		computeAmount(true);
	});
}

function pickDate(elem) {
	var $txtDate = $(elem).closest("tr").find(".txtDate");
	NewCssCal($($txtDate).attr("id"));
}

function deletePRow(deleteImg) {
	var hasData = true;
	var $txtDate = $(deleteImg).closest("tr").find(".txtDate");
	var $txtAmount = $(deleteImg).closest("tr").find(".txtFDLAmount");
	if($txtDate.val() == "" && ($txtAmount.val() == "" || $txtAmount.val() == "0.00")){
		hasData = false;
	}
	var row = $(deleteImg).attr("id").replace("imgPDelete", "");
	var toBeDeletedRow = $(deleteImg).closest("tr");
	$(toBeDeletedRow).remove();
	var rowCount = $('#tblDeductionLine tbody tr').length;
	if(row == 0 && rowCount == 0) {
		addFDLLine(1);
	}
	updateFDLIndex(hasData);
}
function updateFDLIndex (hasData) {
	var currentRow = 0;
	var dates = "";
	var date = "";
	$("#tblDeductionLine tbody tr").each(function(row) {
		currentRow = row;
		$(this).find(".imgPDelete").attr("id", "imgPDelete"+row);
		$(this).find(".rowCount").text(Number(row)+1);
		$(this).find(".hdnDate").attr("id", "hdnDate"+row).attr("name", "formDeductionLines["+row+"].date");
		$(this).find(".imgCalendar").attr("id", "imgCalendar"+row);
		$(this).find(".txtDate").attr("id", "txtDate"+row).attr("name", "formDeductionLines["+row+"].date");
		$(this).find(".txtFDLAmount").attr("id", "txtFDLAmount"+row).attr("name", "formDeductionLines["+row+"].amount");
		date = $(this).find(".txtDate").attr("id", "txtDate"+row).val();
		if(date != ""){
			dates +=date+";";
		}
	});
	fdlIndex = currentRow+1;
	if(hasData){
		loadLine(fdlIndex, dates);
	}
}

function computeAmount(isAdjust) {
	var currentRow = 0;
	var totalAmount = 0;
	var amountToDeduct = accounting.unformat($("#txtAmount").val());
	var $firstInput = null;
	$("#tblDeductionLine tbody tr").each(function(row) {
		currentRow = row;
		var $amount = $(this).find(".txtFDLAmount").attr("id", "txtFDLAmount"+row).attr("name", "formDeductionLines["+row+"].amount");
		if (row == 0) {
			$firstInput = $amount;
		}
		totalAmount += Number(accounting.unformat($($amount).val()));
	});
	var difference = amountToDeduct - totalAmount;
	if (difference != 0 && isAdjust) {
		var firstInputVal = accounting.unformat($($firstInput).val());
		$($firstInput).val(accounting.formatMoney((parseFloat(firstInputVal) + parseFloat(accounting.unformat(difference))).toFixed(2)));
		totalAmount += Number(accounting.unformat(difference));
	}
	computeTotalAmt();
}

function formatMoney(elem) {
	$(elem).val(accounting.formatMoney($(elem).val()));
}

function showEmployees () {
	var companyId = $("#companyId").val();
	var employeeName = encodeURIComponent($.trim($("#txtEmployee").val()));
	var uri = contextPath + "/getEmployees/byName?companyId="+ companyId + "&name="+employeeName;
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

function disableCompany() {
	$("#companyId").attr("disabled", "disabled");
}

function enableCompany() {
	$("#companyId").removeAttr("disabled");
	$(".txtDate").each(function(i) {
		console.log('removing attr');
		$(this).removeAttr("readonly");
	});
}

function getEmployee () {
	var companyId = $("#companyId").val();
	var txtEmployee = encodeURIComponent($.trim($("#txtEmployee").val()));
	var uri = "/getEmployees/bycCompanyAndName?companyId="+ companyId + "&name=" + txtEmployee;
	$("#formEmployeeError").text("");
	$("#hdnEmployeeId").val("");
	$("#spanPositionId").html("");
	$("#spanEmployeeError").text("");
	$.ajax({
		url: contextPath + uri,
		success : function(employee) {
			if (txtEmployee != "") {
				if (employee != null && typeof employee[0] != "undefined") {
					$("#hdnEmployeeId").val(employee[0].id);
					getEmployeePosition();
				} else {
					$("#spanEmployeeError").text("Invalid employee.");
				}
			}
			getEmployeeDivision();
		},
		error : function(error) {
			console.log(error);
			getEmployeeDivision();
		},
		dataType: "json"
	});
}
function getEmployeePosition() {
	var employeeId = $("#hdnEmployeeId").val();
	var uri = "/getEmployees/getPosition?employeeId="+employeeId;
	if (employeeId != "") {
		$.ajax({
			url: contextPath + uri,
			success : function(position) {
				if (position != null && typeof position != "undefined") {
					$("#spanPositionId").html(position.name);
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

function companyIdOnChange(companyId){
 	$("#hdnCompanyId").val(companyId);
 	$documentsTable.emptyTable();
 	$("#txtEmployee").val("");
 	$("#spanPositionId").html("");
}

function parseDoubles() {
	var amountToDeduct = $("#txtAmount").val();
	$("#txtAmount").val(accounting.unformat(amountToDeduct));

	$(".txtFDLAmount").each(function(i) {
		var amount = $(this).val();
		$(this).val(accounting.unformat(amount));
	});
}

function formatMonetaryVal() {
	var amountToDeduct = $("#txtAmount").val();
	$("#txtAmount").val(accounting.formatMoney(amountToDeduct));

	$(".txtFDLAmount").each(function(i) {
		formatMoney($(this));
	});
}

function computeTotalAmt() {
	var totalAmount = 0;
	$(".txtFDLAmount").each(function(i) {
		totalAmount += accounting.unformat($(this).val());
	});
	$("#totalAmount").html(accounting.formatMoney(totalAmount));
}

var isSaving = false;
function saveForm(){
	$("#spanEmployeeError").text("");
	var amountToDeduct = $("#txtAmount").val();
	var totalAmount = $("#totalAmount").html();
	var noPayroll = $("#txtNoOfPayroll").val();
	if(accounting.unformat(totalAmount) != accounting.unformat(amountToDeduct)){
		$("#linesError").css("color", "red").text('Amount to Deduct and Total amount must be equal.');
	} else {
		if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsTable"))) {
			$("#spDocSizeMsg").text("");
			isSaving = true;
			enableCompany();
			parseDoubles();
			$("#txtAmount").val(accounting.unformat(amountToDeduct));
			$("#referenceDocJson").val($documentsTable.getData());
			$("#btnSave").attr("disabled", "disabled");
			doPostWithCallBack ("formDeductionForm", "form", function (data) {
				if (data.startsWith("saved")) {
					var objectId = data.split(";")[1];
					var formStatus = new Object();
					formStatus.objectId = objectId;
					updateTable (formStatus);
					dojo.byId("form").innerHTML = "";
					isSaving = false;
				} else {
					var empPosition = $("#spanPositionId").html();
					if(!isEdit){
						dojo.byId("form").innerHTML = data;
					} else {
						dojo.byId("editForm").innerHTML = data;
						disableCompany();
					}
					$("#spanPositionId").html(empPosition);
					computeTotalAmt();
					parseDoubles();
					formatMonetaryVal();
					initializeDocumentsTbl();
					isSaving = false;
					getEmployeeDivision();
				}
				$("#btnSave").removeAttr("disabled");
			});
		} else if (checkExceededFileSize($("#documentsTable"))) {
			$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
		}
	}
}
function getEmployeeDivision() {
	var employeeId = $("#hdnEmployeeId").val();
	var uri = "/getEmployees/getDivision?employeeId="+employeeId;
	if (employeeId != "") {
		$.ajax({
			url: contextPath + uri,
			success : function(division) {
				if (division != null && typeof division != undefined) {
					$("#spanDivisionId").text(division.name);
					$("#hdnDivisionId").val(division.id);
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$("#spanDivisionId").text("");
		$("#hdnDivisionId").val("");
	}
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="formDeduction" id="formDeductionForm">
		<div class="modFormLabel">
			<c:choose>
				<c:when test="${formDeduction.formDeductionTypeId == 1}">Authority to Deduct</c:when>
				<c:otherwise>Cash Bond Contract</c:otherwise>
			</c:choose>
			<span class="btnClose" id="btnClose">[X]</span>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="employeeId" id="hdnEmployeeId" />
		<form:hidden path="divisionId" id="hdnDivisionId" />
		<form:hidden path="sequenceNumber" />
		<form:hidden path="formDeductionTypeId" id="hdnformDeductionTypeId" />
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
							value="${formDeduction.sequenceNumber}" /></td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<c:set var="status"
							value="${formDeduction.formWorkflow.currentFormStatus.description}" />
						<c:if test="${status eq null}">
							<c:set var="status" value="NEW" />
						</c:if>
						<td class="value"><input type="text"
								id="txtStatus" class="textBoxLabel"
								readonly="readonly" value='${status}' />
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
			<legend>
				<c:choose>
					<c:when test="${formDeduction.formDeductionTypeId == 1}">Authority to Deduct</c:when>
					<c:otherwise>Cash Bond Contract</c:otherwise>
				</c:choose>&nbsp;Header
			</legend>
			<table class="formTable">
				<tr>
					<td class="labels">* Date </td>
					<td class="value"><form:input path="formDate" style="width: 120px;" cssClass="dateClass2" onblur="evalDate('formDate')"/>
							<img id="formDate" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('formDate')"
									style="cursor: pointer" style="float: right;" />
					</td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="formDate" cssClass="error"
							style="margin-left: 12px;" /></td>
				</tr>
				<tr>
					<td class="labels">* Company/Branch</td>
					<td class="value">
						<form:select path="companyId" id="companyId" cssClass="frmSelectClass" onchange="companyIdOnChange(companyId);"
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
					<td class="labels">* Employee</td>
					<td class="value">
						<form:input path="employeeFullName" id="txtEmployee" class="input" onkeydown="showEmployees();" onkeyup="showEmployees();" onblur="getEmployee();"/>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value">
						<span id="spanEmployeeError" class="error" ></span>
						<form:errors path="employeeId" cssClass="error" id="formEmployeeError"/>
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
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="divisionId" cssClass="error" /></td>
				</tr>
			</table>
			</fieldset>
			<fieldset class="frmField_set">
			<legend>Deduction Table</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Start Date</td>
						<td class="value"><form:input path="startDate" style="width: 120px;" cssClass="dateClass2" onblur="evalDate('startDate')"/>
								<img id="startDate" src="${pageContext.request.contextPath}/images/cal.gif"
										onclick="javascript:NewCssCal('startDate')"
										style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="startDate" cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* Deduction or Loan Type</td>
						<td class="value">
							<form:select path="deductionTypeId" id="deductionTypeId" cssClass="frmSelectClass">
								<form:options items="${deductionTypes}" itemValue="id" itemLabel="name"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="deductionTypeId" cssClass="error"
								style="margin-left: 12px;" /></td>
					</tr>
					<tr>
						<td class="labels">Amount to Deduct</td>
						<td class="value">
							<form:input path="totalDeductionAmount" id="txtAmount" class="numeric" size="20" maxLength="13" onblur="formatMoney(this);"/>
						</td>
					</tr>
					<tr>
						<td class="labels">No. of Payroll Deduction</td>
						<td class="value">
							<form:input path="noOfPayrollDeduction" id="txtNoOfPayroll" class="input numeric" onblur="initLines();"/>
						</td>
					</tr>
					<c:choose>
						<c:when test="${formDeduction.formDeductionTypeId == 1}">
							<tr>
								<td class="labels">Remarks</td>
								<td class="value"><form:textarea path="remarks" id="remarks" class="input" /></td>
							</tr>
						</c:when>
					</c:choose>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<table id="tblDeductionLine" class="dataTable">
								<thead>
									<tr>
										<th></th>
										<th>#</th>
										<th></th>
										<th class="thTitle">Date</th>
										<th class="thTitle">Amount</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${formDeduction.formDeductionLines}" var="fdl" varStatus="status">
										<tr>
											<td class='tdProperties' align='center'>
												<c:set var="date" value="${fdl.date}"/>
												<input type='hidden' name='formDeductionLines[${status.index}].id'
													value='${fdl.id}'/>
												<img class='imgPDelete' id='imgPDelete${status.index}'
													src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deletePRow(this);' />
											</td>
											<td class='tdProperties'><span class="rowCount">${status.index+1}</span></td>
											<td class='tdProperties' align='center'>
												<input type='hidden' class='hdnDate' id='hdnDate${status.index}'
													value="<fmt:formatDate pattern='MM/dd/yyyy'  value='${fdl.date}'/>"/>
												<img class='imgCalendar' id='imgCalendar${status.index}' src='${pageContext.request.contextPath}/images/cal.gif' onclick='pickDate(this)'/>
											</td>
											<td class='tdProperties' align='center'>
												<input name='formDeductionLines[${status.index}].date' id='txtDate${status.index}' class='txtDate' style='width: 100%;'
													value="<fmt:formatDate pattern='MM/dd/yyyy' value='${fdl.date}'/>"/>
											</td>
											<td class='tdProperties' valign='top'>
												<input name='formDeductionLines[${status.index}].amount' id='txtFDLAmount${status.index}' style='width: 100%; 
													text-align: right;' class='txtFDLAmount' onblur='formatMoney(this); computeAmount(false);' maxLength='13'
													onfocus='addFDLLine(1,${status.index});' value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${fdl.amount}'/>"/>
											</td>
										</tr>
									</c:forEach>
								</tbody>
								<tfoot>
									<tr>
										<td colspan="3"><b>Total</b></td>
										<td colspan="2" style="text-align: right;"><b><span id="totalAmount">0.00</span></b></td>
									</tr>
									<tr>
										<td colspan="4"><span id="linesError" class="error"><form:errors path="formDeductionMessage" cssClass="error"/></span></td>
									</tr>
								</tfoot>
							</table>
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
						value="Save" onclick="saveForm();" />
					</td>
				</tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>