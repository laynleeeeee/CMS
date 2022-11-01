<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description: Supplier advance payment form JSP page -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.unitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	if (Number("${supplierAdvPayment.id}") > 0) {
		loadHeaderFields("${supplierAdvPayment.rpurchaseOrderId}", "${supplierAdvPayment.poNumber}",
				"${supplierAdvPayment.companyId}", "${supplierAdvPayment.companyName}",
				"${supplierAdvPayment.divisionId}", "${supplierAdvPayment.divisionName}",
				"${supplierAdvPayment.supplierId}", "${supplierAdvPayment.supplierName}",
				"${supplierAdvPayment.supplierAcctId}", "${supplierAdvPayment.supplierAcctName}",
				"${supplierAdvPayment.bmsNumber}", "${supplierAdvPayment.currencyId}",
				"${supplierAdvPayment.currencyRateId}", "${supplierAdvPayment.currencyRateValue}",
				"${supplierAdvPayment.currency.name}", "${supplierAdvPayment.termDays}");
	} else {
		computeDueDate(0);
		$("#spanDivisionNameId").text("${supplierAdvPayment.division.name}");
	}
	initDocumentsTbl();
	initPoLineTbl();
	populateFooterAmt();
});

function showPoReferences() {
	$("#divPoReferenceId").html("");
	$("#divPoReferenceId").load(contextPath+"/supplierAdvPayment/"+"${supplierAdvPayment.divisionId}"+"/showPoReferences");
}

function loadPurchaseOrderRef(purchaseOrderRefId) {
	var uri = contextPath + "/supplierAdvPayment/loadPoReference?purchaseOrderRefId="+purchaseOrderRefId;
	if ("${supplierAdvPayment.id}" > 0) {
		uri += "&advPaymentId="+"${supplierAdvPayment.id}";
	}
	$.ajax({
		url: uri,
		success : function(advPayment) {
			$("#divPoReferenceId").html("");
			$("#aClose")[0].click();
			loadHeaderFields(advPayment.rpurchaseOrderId, advPayment.poNumber, advPayment.companyId, advPayment.companyName,
					advPayment.divisionId, advPayment.divisionName, advPayment.supplierId, advPayment.supplierName, advPayment.supplierAcctId,
					advPayment.supplierAcctName, advPayment.bmsNumber, advPayment.currencyId, advPayment.currencyRateId,
					advPayment.currencyRateValue, advPayment.currency.name, advPayment.termDays);
			setPoLineTbl(advPayment.advPaymentLines);
			populateFooterAmt();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function loadHeaderFields(rpurchaseOrderId, poNumber, companyId, companyName, divisionId, divisionName,
		supplierId, supplierName, supplierAcctId, supplierAcctName, bmsNumber, currencyId, currencyRateId,
		currencyRateValue, currencyName, termDays) {
	$("#hdnPurchaseOrderId").val(rpurchaseOrderId);
	$("#txtPoReferenceId").val(poNumber);
	$("#companyId").val(companyId);
	$("#spanCompanyNameId").text(companyName);
	$("#divisionId").val(divisionId);
	$("#spanDivisionNameId").text(divisionName);
	$("#supplierId").val(supplierId);
	$("#spanSupplierNameId").text(supplierName);
	$("#supplierAcctId").val(supplierAcctId);
	$("#spanSupplierAcctNameId").text(supplierAcctName);
	$("#bmsNumber").val(bmsNumber);
	$("#currencyId").val(currencyId);
	$("#hdnCurrRateId").val(currencyRateId == 0 ? "" : currencyRateId);
	$("#hdnCurrRateValue").val(currencyRateValue);
	$("#hdnTermDaysId").val(termDays);
	$("#txtCurrency").text(currencyName);
	computeDueDate(termDays);
}

function computeDueDate(termDays) {
	termDays = (termDays != "" && termDays != null && termDays != "undefined") ? termDays : 0;
	var glDate = $("#glDate").val();
	if (glDate == null || glDate == ""){
		return;
	}
	var newGlDate = new Date(glDate);
	newGlDate.setDate(newGlDate.getDate() + parseInt(termDays));
	$("#dueDate").val((newGlDate.getMonth() + 1) + "/" + newGlDate.getDate() + "/" + newGlDate.getFullYear());
}

function initPoLineTbl() {
	var advPaymentLineJson = JSON.parse($("#advPaymentLineJsonId").val());
	setPoLineTbl(advPaymentLineJson);
}

function setPoLineTbl(advPaymentLineJson) {
	$("#poLineTbl").html("");
	var cPath = "${pageContext.request.contextPath}";
	$poLineTbl = $("#poLineTbl").editableItem({
		data: advPaymentLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "referenceNo", "varType" : "string"},
			{"name" : "amount", "varType" : "double"},
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
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Particulars",
				"cls" : "referenceNo tblInputText",
				"editor" : "label",
				"visible" : true,
				"width" : "50%" },
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "45%" }
		],
	});

	$("#poLineTbl tbody tr").each(function() {
		$(this).find(".delRow").hide();
		var amount = $(this).closest("tr").find(".amount").text();
		$(this).find(".amount").text(formatNumbers(accounting.unformat(amount)));
	});

	appendTotalFooter();

	$("#poLineTbl table").attr("width", "100%");
}

function appendTotalFooter() {
	$("#poLineTbl table tfoot").append("<tr align='right'><td class='tdProperties tfootProperties' colspan='3'> \
			Advance Payment</td><td class='tdProperties tfootProperties'><span id='advPayment'></span></td></tr>");
	$("#poLineTbl table tfoot").append("<tr align='right'><td class='tdProperties tfootProperties' colspan='3'> \
			Total Unpaid PO Costs</td><td class='tdProperties tfootProperties'><span id='unpaidCost'></span></td></tr>");
}

function populateFooterAmt() {
	var amount = accounting.unformat($("#amount").val());
	var lineAmount = 0;
	$("#poLineTbl tbody tr").each(function() {
		lineAmount += accounting.unformat($(this).find(".amount").html());
	});
	$("#advPayment").text(formatNumbers(-amount));
	$("#unpaidCost").text(formatNumbers(lineAmount - amount));
	$("#amount").val(formatDecimalPlaces(amount));
}

function formatNumbers(value) {
	var formattedVal = formatDecimalPlaces(value);
	if(Number(value) < 0) {
		formattedVal = "(" + formatDecimalPlaces(Math.abs(value)) +")";
	}
	return formattedVal;
}

function checkExceededFileSize() {
	var totalFileSize = 0;
	var FILE_INCREASE = 0.40;
	$("#documentsTable tbody tr").find(".fileSize").each(function() {
		if ($.trim($(this).val()) != "") {
			var fileSize = parseFloat(accounting.unformat($(this).val()));
			totalFileSize += (fileSize + (fileSize * FILE_INCREASE));
		}
	});
	// Included the file increase
	// 14680064 = 10485760(10 MB) + (10485760 × .4)
	if (totalFileSize > 14680064) {
		return true;
	}
	return false;
}

function initDocumentsTbl() {
	var referenceDocumentJson = JSON.parse($("#referenceDocumentJsonId").val());
	var cPath = "${pageContext.request.contextPath}";
	$documentsTable = $("#documentsTable").editableItem({
		data: referenceDocumentJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "referenceObjectId", "varType" : "int"},
			{"name" : "fileName", "varType" : "string"},
			{"name" : "description", "varType" : "string"},
			{"name" : "file", "varType" : "string"},
			{"name" : "fileInput", "varType" : "string"},
			{"name" : "fileSize", "varType" : "double"},
			{"name" : "fileExtension", "varType" : "string"}
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
				"visible" : false},
			{"title" : "fileExtension",
				"cls" : "fileExtension",
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
		convertDocToBase64($(this), fileSize, $("#spanReferenceDoc"), $("#documentsTable"));
	});

	$("#documentsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		console.log("convert base 64 to file");
		convBase64ToFile($($document).val(), fileName);
	});

	$("#documentsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable table").attr("width", "100%");
}

var isSaving = false;
function saveAdvPayment() {
	if (!isSaving && $("#spanReferenceDoc").html() == "" && !checkExceededFileSize()) {
		$("#spanDocumentSize").text("");
		isSaving = true;
		$("#advPaymentLineJsonId").val($poLineTbl.getData());
		$("#referenceDocumentJsonId").val($documentsTable.getData());
		$("#btnSaveForm").attr("disabled", "disabled");
		$("#currencyId").removeAttr("disabled");
		var amount = $("#amount").val();
		$("#amount").val(accounting.unformat(amount));
		doPostWithCallBack ("supplierAdvPaymentFormId", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable(formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var spanFormStatusLbl = $("#spanFormStatusLbl").text();
				var rpurchaseOrderId = $("#hdnPurchaseOrderId").val();
				var poNumber = $("#txtPoReferenceId").val();
				var companyId = $("#companyId").val();
				var companyName = $("#spanCompanyNameId").text();
				var divisionId = $("#divisionId").val();
				var divisionName = $("#spanDivisionNameId").text();
				var supplierId = $("#supplierId").val();
				var supplierName = $("#spanSupplierNameId").text();
				var supplierAcctId = $("#supplierAcctId").val();
				var supplierAcctName = $("#spanSupplierAcctNameId").text();
				var bmsNumber = $("#bmsNumber").val();
				var currencyId = $("#currencyId").val();
				var currencyRateId = $("#hdnCurrRateId").val();
				var currencyRateValue = $("#hdnCurrRateValue").val();
				var termDays = $("#hdnTermDaysId").val();
				var advPayment = $("#advPayment").text();
				var unpaidCost = $("#unpaidCost").text();
				var currencyName = $("#txtCurrency").text();
				if ("${supplierAdvPayment.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				 $("#spanDivisionLbl").text(spanDivisionLbl);
				 $("#spanFormStatusLbl").text(spanFormStatusLbl);
				loadHeaderFields(rpurchaseOrderId, poNumber, companyId, companyName, divisionId,
						divisionName, supplierId, supplierName, supplierAcctId, supplierAcctName,
						bmsNumber, currencyId, currencyRateId, currencyRateValue, currencyName, termDays);
				initDocumentsTbl();
				initPoLineTbl();
				$("#advPayment").text(advPayment);
				$("#unpaidCost").text(unpaidCost);
				$("#currencyId").attr("disabled", "disabled");
				$("#amount").val(formatDecimalPlaces(amount));
			}
			$("#btnSaveForm").removeAttr("disabled");
			isSaving = false;
		});
	} else if (checkExceededFileSize()) {
		$("#spanDocumentSize").text("Total size for documents upload must not exceed 10 MB.");
	}
}
</script>
<style type="text/css">
.tfootProperties {
	padding: 3px;
	font-size: small;
}

.inputAmount {
	text-align: right;
}

.remove-border {
	border: none;
}

.span-label-value {
	padding: 4px;
}
</style>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divPoReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divPoReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="supplierAdvPayment" id="supplierAdvPaymentFormId">
		<div class="modFormLabel">Supplier Advance Payment<span id="spanDivisionLbl"> - ${supplierAdvPayment.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="sequenceNumber"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="rpurchaseOrderId" id="hdnPurchaseOrderId"/>
		<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
		<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
		<form:hidden path="advPaymentLineJson" id="advPaymentLineJsonId"/>
		<form:hidden path="referenceDocumentJson" id="referenceDocumentJsonId"/>
		<form:hidden path="termDays" id="hdnTermDaysId"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">SAP No.</td>
						<td class="value">
							<span id="spanSequenceNo">
								<c:if test="${supplierAdvPayment.id gt 0}">
									${supplierAdvPayment.sequenceNumber}
								</c:if>
							</span>
						</td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<span id="spanFormStatusLbl">
								<c:choose>
									<c:when test="${supplierAdvPayment.formWorkflow ne null}">
										${supplierAdvPayment.formWorkflow.currentFormStatus.description}
									</c:when>
									<c:otherwise>
										NEW
									</c:otherwise>
								</c:choose>
							</span>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Supplier Advance Payment Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Company</td>
						<td class="value">
							<form:hidden path="companyId" id="companyId"/>
							<form:label path="companyName" id="spanCompanyNameId"
								class="span-label-value"></form:label>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="companyId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Division</td>
						<td class="value">
							<form:hidden path="divisionId" id="divisionId"/>
							<form:label path="divisionName" id="spanDivisionNameId"
								class="span-label-value"></form:label>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* PO Reference</td>
						<td class="value">
							<form:input path="poNumber" id="txtPoReferenceId" class="inputSmall" readonly="true"/>
							<c:if test="${supplierAdvPayment.id ne null}">
								<a href="#container" id="waOpen" data-reveal-id="divPoReferenceId"
									class="link_button" onclick="showPoReferences();">Browse PO</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="rpurchaseOrderId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Date</td>
						<td class="value">
							<form:input path="date" onblur="evalDate('date')" style="width: 120px;"
								cssClass="dateClass2"/>
							<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')" style="cursor: pointer"
								style="float: right;"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="date" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Supplier</td>
						<td class="value">
							<form:hidden path="supplierId" id="supplierId"/>
							<form:label path="supplierName" id="spanSupplierNameId"
								class="span-label-value"></form:label>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="supplierId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Supplier Account</td>
						<td class="value">
							<form:hidden path="supplierAcctId" id="supplierAcctId"/>
							<form:label path="supplierAcctName" id="spanSupplierAcctNameId"
								class="span-label-value"></form:label>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="supplierAcctId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">BMS No.</td>
						<td class="value">
							<form:input path="bmsNumber" id="bmsNumber" cssClass="inputSmall remove-border"
								readonly="true"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="bmsNumber" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Invoice Date</td>
						<td class="value">
							<form:input path="invoiceDate" id="invoiceDate" onblur="evalDate('invoiceDate')" style="width: 120px;"
								cssClass="dateClass2"/>
							<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('invoiceDate')" style="cursor: pointer"
								style="float: right;"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="invoiceDate" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* GL Date</td>
						<td class="value">
							<form:input path="glDate" id="glDate" onblur="evalDate('glDate')" style="width: 120px;"
								cssClass="dateClass2"/>
							<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('glDate')" style="cursor: pointer"
								style="float: right;"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="glDate" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Due Date</td>
						<td class="value">
							<form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate')" style="width: 120px;"
								cssClass="dateClass2"/>
							<img id="imgDate3" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('dueDate')" style="cursor: pointer"
								style="float: right;"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="dueDate" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Reference No.</td>
						<td class="value">
							<form:input path="referenceNo" id="referenceNo" class="inputSmall"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="referenceNo" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Requestor</td>
						<td class="value">
							<form:input path="requestor" id="requestor" class="input"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="requestor" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Remarks</td>
						<td class="value">
							<form:textarea path="remarks" rows="3"
								cssStyle="width: 350px; resize: none;"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="remarks" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Currency</td>
						<td class="value">
							<form:select path="currencyId" id="currencyId" cssClass="frmMediumSelectClass" disabled="true">
								<form:options items="${currencies}" itemValue="id" itemLabel="name"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="currencyId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Advance Payment</td>
						<td class="value">
							<form:input path="amount" id="amount" cssClass="inputSmall inputAmount"
								onblur="populateFooterAmt();"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="amount" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Purchase Order Table</legend>
				<div id="poLineTbl"></div>
				<table>
					<tr>
						<td><span id="advPaymentLines" class="error"></span></td>
					</tr>
					<tr>
						<td><form:errors path="advPaymentLines" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Document/s</legend>
				<div id="documentsTable"></div>
				<table>
					<tr>
						<td><form:errors path="referenceDocsMessage" cssClass="error"/></td>
					</tr>
					<tr>
						<td><span class="error" id="spanDocumentSize"></span></td>
					</tr>
					<tr>
						<td><span class="error" id="spanReferenceDoc"></span></td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table class="frmField_set">
				<tr>
					<td align="right">
						<input type="button" id="btnSaveForm" value="Save" onclick="saveAdvPayment();"/>
					</td>
				</tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>