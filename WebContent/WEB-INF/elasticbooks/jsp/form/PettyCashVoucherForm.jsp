<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Petty Cash Voucher form.
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
$(document).ready(function(){
	loadDivision("${pcv.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	initializeDocumentsTbl();
	formatAmount();
	if("${pcv.id}" != null){
		$("#txtUserCustodian").val("${pcv.userCustodian.custodianAccount.custodianName}");
	}
});

function loadDivision(divisionId) {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId	
		+"&divisionId="+divisionId;
	$("#divisionId").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},
		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	var postHandler = {
		doPost: function(data) {
			if (divisionId != 0 && divisionId != "" && divisionId != "undefined") {
				$("#divisionId").val(divisionId);
			}
		}
	};
	loadPopulate (uri, false, divisionId, "divisionId", optionParser, postHandler);
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

	$("#documentsTable").on("change", ".fileInput", function() {
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize);
	});

	$("#documentsTable tbody tr").each(function() {
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable").on("click", ".fileLink", function() {
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});

	$("#documentsTable table").attr("width", "100%");
}

function unformatAmount() {
	var amount = $("#amount").val();
	$("#amount").val(accounting.unformat(amount));
}

function formatAmount() {
	var amount = $("#amount").val();
	$("#amount").val(formatDecimalPlaces(Number(amount)));
}

var isSaving = false;
function savePettyCashVoucher() {
	$("#btnSave").attr("disabled", "disabled");
	if(!isSaving && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize()) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		var companyId = $("#companyId").val();
		$("#divisionId").removeAttr("disabled");
		$("#referenceDocumentsJson").val($documentsTable.getData());
		unformatAmount();
		doPostWithCallBack ("pettyCashVoucherForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var userCustodian = $("#txtUserCustodian").val();
				var status = $("#currentStatus").val();
				if("${pcv.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#currentStatus").val(status);
				}
				formatAmount();
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#txtUserCustodian").val(userCustodian);
				$("#companyId").val(companyId);
				loadDivision("${pcv.divisionId}");
				$("#divisionId").attr("disabled","disabled");
				initializeDocumentsTbl();
				isSaving = false;
			}
			$("#btnSave").removeAttr("disabled");
		});
	} else if (checkExceededFileSize()) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="pcv" id="pettyCashVoucherForm">
			<div class="modFormLabel">Petty Cash Voucher<span id="spanDivisionLbl"> - ${pcv.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="updatedBy"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson" />
			<form:hidden path="ebObjectId"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">PCV No.</td>
							<td class="value">
								<form:input path="sequenceNo" readonly="true" cssClass="textBoxLabel"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status"
								value="${pcv.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text"
								id="currentStatus" class="textBoxLabel"
								readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Petty Cash Voucher Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass"
									items="${companies}" itemLabel="name" itemValue="id" >
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Division</td>
							<td class="value">
								<form:select path="divisionId" id="divisionId" class="frmSelectClass"></form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="divisionId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Custodian</td>
							<td class="value">
								<form:select path="userCustodianId" id="userCustodianId" cssClass="frmSelectClass">
									<form:options items="${userCustodians}" itemValue="id" itemLabel="custodianAccount.custodianName" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="userCustodianId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value"><form:input path="pcvDate" id="date"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')" style="cursor: pointer"
								style="float: right;" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="pcvDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Requestor</td>
							<td class="value"><form:input path="requestor" id="requestor" class="input" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="requestor" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Reference</td>
							<td class="value"><form:input path="referenceNo" id="referenceNo" class="input" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="referenceNo" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Description</td>
							<td class="value">
								<form:textarea path="description" id="description" class="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="description" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Amount Requested</td>
							<td class="value">
								<form:input path="amount" id ="amount" class="numeric" cssStyle="width: 172px;" maxLength="13" onblur="formatAmount();"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="amount" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Document/s</legend>
					<div id="documentsTable"></div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><span class="error" id="spDocSizeMsg"></span></td>
					</tr>
					<tr>
						<td><form:errors path="referenceDocsMessage" cssClass="error" /></td>
					</tr>
					<tr>
						<td><span class="error" id="referenceDocsMgs"></span></td>
					</tr>
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error" /></td>
					</tr>
					<tr>
						<td colspan="3" align="right">
								<input type="button" value="Save" id="btnSave" onclick="savePettyCashVoucher();"/></td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>