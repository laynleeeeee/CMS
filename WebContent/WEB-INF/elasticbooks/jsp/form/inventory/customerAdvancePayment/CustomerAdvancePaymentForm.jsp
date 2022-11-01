<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Customer Advance Payment form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.saleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
.textBoxLabel {
	border: none;
}
</style>
<script type="text/javascript">
var selectCustomerAcct = "${customerAdvancePayment.arCustomerAccountId}";
var selectCompany = "${customerAdvancePayment.companyId}";
var currentCustAcctId = 0;
var $capTable = null;
var PREV_RATE = 0;
$(document).ready (function () {
	$("#txtSoRefNumber").attr("disabled","disabled");
	if ("${customerAdvancePayment.id}" != 0) {
		$("#cash").val('<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerAdvancePayment.cash}" />');
	}
	initDocumentsTbl();
	initSoLineTbl();
	populateFooterAmt();

	if ("${customerAdvancePayment.formWorkflow.complete}" == "true" || 
			"${customerAdvancePayment.formWorkflow.currentStatusId}" == 4) {
		$("#customerAdvancePaymentsForm :input").attr("disabled","disabled");
	}
});

function initSoLineTbl() {
	var capLinesJson = JSON.parse($("#capLinesJson").val());
	setSoLineTbl(capLinesJson);
}

function setSoLineTbl(capLinesJson) {
	$("#salesOrderTbl").html("");
	var cPath = "${pageContext.request.contextPath}";
	$soLineTbl = $("#salesOrderTbl").editableItem({
		data: capLinesJson,
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

	$("#salesOrderTbl tbody tr").each(function() {
		$(this).find(".delRow").hide();
		var amount = $(this).closest("tr").find(".amount").text();
		$(this).find(".amount").text(formatNumbers(accounting.unformat(amount)));
	});
	appendTotalFooter();
	$("#salesOrderTbl table").attr("width", "100%");
}

function appendTotalFooter() {
	$("#salesOrderTbl table tfoot").append("<tr align='right'><td class='tdProperties tfootProperties' colspan='3'> \
			Advance Payment</td><td class='tdProperties tfootProperties'><span id='advPayment'></span></td></tr>");
	$("#salesOrderTbl table tfoot").append("<tr align='right'><td class='tdProperties tfootProperties' colspan='3'> \
			Remaining Balance</td><td class='tdProperties tfootProperties'><span id='unpaidCost'></span></td></tr>");
}

function populateFooterAmt() {
	var amount = accounting.unformat($("#amount").val());
	var lineAmount = 0;
	$("#salesOrderTbl tbody tr").each(function() {
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
	var referenceDocumentJson = JSON.parse($("#referenceDocumentJson").val());
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
function saveCAPs() {
	if(!isSaving && $("#spanReferenceDoc").html() == "" && !checkExceededFileSize()) {
		isSaving = true;
		$("#capLinesJson").val($soLineTbl.getData());
		$("#referenceDocumentJson").val($documentsTable.getData());
		$("#currencyId").removeAttr("disabled");
		$("#txtSoRefNumber").removeAttr("disabled");
		parseValues();
		doPostWithCallBack ("customerAdvancePaymentsForm", "form", function (data) {
			var refNo = $("#refNumber").val();
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var status = $("#txtCapStatus").val();
				var companyId = $("#companyId").val();
				var companyName = $("#spCompanyName").text();
				var divisionId = $("#divisionId").val();
				var divisionName = $("#spDivisionName").text();
				var soId = $("#hdnSalesOrderId").val();
				var soNumber = $("#txtSoRefNumber").val();
				var poNumber = $("#hdnPoNumber").val();
				var poNumber = $("#spPoNumber").text();
				var arCustomerId = $("#hdnArCustomerId").val();
				var arcustomerName = $("#spCustomerName").text();
				var arCustAcctId = $("#arCustomerAcctId").val();
				var arCustAcctName = $("#spCustomerAcctName").text();
				var currencyId = $("#currencyId").val();
				var currencyRateId = $("#hdnCurrRateId").val();
				var currencyRateValue = $("#hdnCurrRateValue").val();
				if ("${customerAdvancePayment.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val($("#hdnCompanyId").val());
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtCapStatus").val(status);
				}
				updateHeader(companyId, companyName, divisionId, divisionName, soId, soNumber, poNumber,
					arCustomerId, arcustomerName, arCustAcctId, arCustAcctName, currencyId,
					currencyRateId, currencyRateValue);
				initDocumentsTbl();
				initSoLineTbl();
				populateFooterAmt();
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#refNumber").val(refNo);
				$("#currencyId").attr("disabled", "disabled");
				$("#txtSoRefNumber").attr("disabled","disabled");
				isSaving = false;
			}
			$("#btnSaveCAPs").removeAttr("disabled");
		});
	} else if (checkExceededFileSize()) {
		$("#spanDocumentSize").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function parseValues (){
	$("#amount").val(accounting.unformat($("#amount").val()));
}

function formatMoney (textboxId) {
	var money = accounting.formatMoney($(textboxId).val());
	$(textboxId).val(money.replace(/[$]/gi, ''));
}

function clearCustAndAccount() {
	$("#hdnArCustomerId").val("");
	$("#txtCustomer").val("");
	$("#arCustomerAcctId").empty();
}

function assignCompany(companyId) {
	$("#otherChargesTable").html("");
	$("#capTable").html("");
	$("#hdnCompanyId").val(companyId);
	clearCustAndAccount();
	initializeOtherChargesTbl();
	initializeTable();
}

function showSoReferences() {
	var drTypeId = $.trim($("#hdnDrTypeId").val());
	var divisionId = $("#divisionId").val();
	console.log(contextPath+"/customerAdvancePayment/"+divisionId+"/showSoReferences");
	$("#divSoReferenceId").load(contextPath+"/customerAdvancePayment/"+divisionId+"/showSoReferences");
}

function loadSalesOrderReference(salesOrderId) {
	var typeId = $("#hdnCapId").val();
	var uri = contextPath+"/customerAdvancePayment/convSOToCAP?salesOrderId="+salesOrderId+"&typeId="+typeId;
	if("${customerAdvancePayment.id}" != 0) {
		uri += "&capId=${customerAdvancePayment.id}";
	}
	if (salesOrderId != "") {
		$.ajax({
			url: uri,
			success : function(cap) {
				$("#spanSalesOrderMsg").html("");
				$("#divSoReferenceId").html("");
				$("#aClose")[0].click();
				console.log(cap);
				updateHeader(cap.companyId, cap.company.name, cap.divisionId, cap.division.name, cap.salesOrderId,
						cap.referenceNo, cap.poNumber, cap.arCustomerId, cap.arCustomer.name, cap.arCustomerAccountId, 
						cap.arCustomerAccount.name, cap.currencyId, cap.currencyRateId, cap.currencyRateValue);
				setSoLineTbl(cap.capLines);
				populateFooterAmt();
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function updateHeader(companyId, companyName, divisionId, divisionName, soId, soNumber, poNumber,
		arCustomerId, arcustomerName, arCustAcctId, arCustAcctName, currencyId, currencyRateId, currencyRateValue) {
	$("#companyId").val(companyId);
	$("#spCompanyName").text(companyName);
	$("#divisionId").val(divisionId);
	$("#spDivisionName").text(divisionName);
	$("#hdnSalesOrderId").val(soId);
	$("#txtSoRefNumber").val(soNumber);
	$("#hdnPoNumber").val(poNumber);
	$("#spPoNumber").text(poNumber);
	$("#hdnArCustomerId").val(arCustomerId);
	$("#spCustomerName").text(arcustomerName);
	$("#arCustomerAcctId").val(arCustAcctId);
	$("#spCustomerAcctName").text(arCustAcctName);
	$("#currencyId").val(currencyId);
	$("#hdnCurrRateId").val(currencyRateId == 0 ? "" : currencyRateId );
	$("#hdnCurrRateValue").val(currencyRateValue);
}
</script>
</head>
<body>
	<div id="container" class="popupModal">
		<div id="divSoReferenceContainer" class="reveal-modal">
			<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
			<div id="divSoReferenceId"></div>
		</div>
	</div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="customerAdvancePayment" id="customerAdvancePaymentsForm">
			<div class="modFormLabel">Customer Advance Payment <span id="spanDivisionLbl"> - ${customerAdvancePayment.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id" id="hdnFormId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="capNumber"/>
			<form:hidden path="capItemsJson" id="capItemsJson"/>
			<form:hidden path="capArLinesJson" id="capArLinesJson"/>
			<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<form:hidden path="referenceDocumentJson" id="referenceDocumentJson" />
			<form:hidden path="capLinesJson" id="capLinesJson" />
			<form:hidden path="customerAdvancePaymentTypeId" id="hdnCapId" />
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">CAP No.</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${customerAdvancePayment.capNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${customerAdvancePayment.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtCapStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Customer Advance Payment Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:hidden path="companyId" id="companyId"/>
								<span id="spCompanyName">${customerAdvancePayment.company.name}</span>
							</td>
						</tr>
						<tr>
							<td class="labels">Division</td>
							<td class="value">
								<form:hidden path="divisionId" id="divisionId"/>
								<span id="spDivisionName">${customerAdvancePayment.division.name}</span>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="divisionId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">SO Reference</td>
							<td class="value">
								<form:hidden path="salesOrderId" id="hdnSalesOrderId"/>
								<form:input path="soNumber" id="txtSoRefNumber"/>
								<a href="#container" id="aOpen" data-reveal-id="divSoReferenceId" class="link_button"
									onclick="showSoReferences();">Browse SO</a>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="salesOrderId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">PO/PCR No.</td>
							<td class="value">
								<form:hidden path="poNumber" id="hdnPoNumber"/>
								<span id="spPoNumber">${customerAdvancePayment.poNumber}</span>
							</td>
						</tr>
						<tr>
							<td class="labels">Customer</td>
							<td class="value">
								<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
								<span id="spCustomerName">${customerAdvancePayment.arCustomer.name}</span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanCustomerError" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="arCustomerId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Customer Account</td>
							<td class="value">
								<form:hidden path="arCustomerAccountId" id="arCustomerAcctId"/>
								<span id="spCustomerAcctName">${customerAdvancePayment.arCustomerAccount.name}</span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="arCustomerAccountId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Reference No.</td>
							<td class="value">
								<form:input path="refNumber" id="refNumber" class="inputSmall"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="refNumber" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Receipt Date</td>
							<td class="value">
								<form:input path="receiptDate" id="receiptDate" onblur="evalDate('receiptDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('receiptDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="receiptDate"	cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Maturity Date</td>
							<td class="value">
								<form:input path="maturityDate" id="maturityDate" onblur="evalDate('maturityDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('maturityDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="maturityDate" cssClass="error" style="margin-left: 12px;"/>
							</td>
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
								<form:input path="amount" id="amount" class="numeric"
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
					<legend>Sales Order Table</legend>
					<div id="salesOrderTbl"></div>
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
							<td colspan="12"><span class="error" id="spanDocumentSize"></span></td>
						</tr>
						<tr>
							<td colspan="12"><span class="error" id="spanReferenceDoc" style="margin-top: 12px;"></span></td>
						</tr>
					</table>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSaveCAPs" value="Save" onclick="saveCAPs();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>