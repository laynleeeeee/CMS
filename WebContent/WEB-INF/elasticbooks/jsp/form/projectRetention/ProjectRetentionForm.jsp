<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Project Retention form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.projectRetentionHandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
.inputAmount {
	text-align: right;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	initDocumentsTbl();
	initializePrLineTable();
	if (Number("${projectRetention.id}") != 0) {
		loadWtaxAcctSettings("${projectRetention.wtAcctSettingId}");
		$("#txtSoNumber").val("${projectRetention.soNumber}");
		$("#aOpen").hide();
	}
	if ("${projectRetention.formWorkflow.complete}" == "true" ||
			"${projectRetention.formWorkflow.currentStatusId}" == 4) {
		$("#deliveryReceiptForm :input").attr("disabled", "disabled");
		$("#btnSaveProjectRetention").attr("disabled", "disabled");
		$("#imgDate").hide();
		$("#imgDueDate").hide();
	}
	computeWtax();
});

function loadWtaxAcctSettings(wtAcctSettingId) {
	var $select = $("#wtaxAcctSettingId");
	$select.empty();
	var divisionId = $("#divisionId").val();
	var uri = contextPath+"/getWithholdingTaxAcct?companyId="+$("#companyId").val()+"&isCreditable=true";
	if (divisionId != null && divisionId != "" && typeof divisionId != "undefined") {
		uri += "&divisionId="+divisionId;
	}
	var optionParser = {
		getValue: function (rowObject) {
			return rowObject["id"];
		},
		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	postHandler = {
		doPost: function() {
			// This is to remove any duplication.
			var found = [];
			$("#wtaxAcctSettingId option").each(function() {
				if ($.inArray(this.value, found) != -1) {
					$(this).remove();
				}
				found.push(this.value);
			});
			if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != undefined) {
				$select.val(wtAcctSettingId);
			}
		}
	};
	loadPopulateObject(uri, false, wtAcctSettingId, $select, optionParser, postHandler, false, true);
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeGrandTotal();
	computeWtax();
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
function saveProjectRetention() {
	if (!isSaving) {
		var soNumber = $.trim($("#txtSoNumber").val());
		if (soNumber != "") {
			isSaving = true;
			// reset JSON table values
			$("#projectRetentionLinesJson").val($prLine.getData());
			$("#referenceDocumentJson").val($documentsTable.getData());
			$("#currencyId").removeAttr("disabled");
			$("#btnSaveProjectRetention").attr("disabled", "disabled");
			$("#amount").val(accounting.unformat($("#amount").val()));//remove formatting.
			doPostWithCallBack ("projectRetentionForm", "form", function(data) {
				if (data.startsWith("saved")) {
					var objectId = data.split(";")[1];
					var formStatus = new Object();
					formStatus.objectId = objectId;
					updateTable (formStatus);
					dojo.byId("form").innerHTML = "";
				} else {
					var spanDivisionLbl = $("#spanDivisionLbl").text();
					var companyId = $("#companyId").val();
					var companyName = $("#spCompanyName").text();
					var divisionId = $("#divisionId").val();
					var divisionName = $("#spDivisionName").text();
					var soId = $("#hdnSalesOrderId").val();
					var soNumber = $("#txtSoNumber").val();
					var poNumber = $("#hdnPoNumber").text();
					var arCustomerId = $("#hdnArCustomerId").val();
					var arCustomerName = $("#spCustomerName").text();
					var arCustomerAcctId = $("#arCustomerAcctId").val();
					var arCustomerAcctName = $("#spCustomerAcctName").text();
					var currencyId = $("#currencyId").val();
					var currencyRateId = $("#hdnCurrRateId").val();
					var currencyRateValue = $("#hdnCurrRateValue").val();
					var wtAcctSettingId = $("#wtaxAcctSettingId").val();
					var amount = $("#amount").val();
					if ("${projectRetention.id}" == 0) {
						dojo.byId("form").innerHTML = data;
					} else {
						dojo.byId("editForm").innerHTML = data;
						$("#aOpen").hide();
					}
					$("#spanDivisionLbl").text(spanDivisionLbl);
					$("#currencyId").attr("disabled", "disabled");
					$("#amount").val(formatDecimalPlaces(amount));
					updateHeader(companyId, companyName, divisionId, divisionName, soId, soNumber, poNumber,
							arCustomerId, arCustomerName, arCustomerAcctId, arCustomerAcctName, currencyId, currencyRateId,
							currencyRateValue);
					initDocumentsTbl();
					initializePrLineTable();
					loadWtaxAcctSettings(wtAcctSettingId);
					computeGrandTotal();
					computeWtax();
				}
				isSaving = false;
				$("#btnSaveProjectRetention").removeAttr("disabled");
			});
		} 
		if(soNumber == "") {
			$("#spanSalesOrderMsg").text("Sales order reference is required.");
		}
	}
}

function showSOReferences() {
	var drTypeId = $.trim($("#hdnDrTypeId").val());
	var divisionId = $("#divisionId").val();
	$("#divSOReferenceId").load(contextPath+"/projectRetention/"+divisionId+"/showSoReferences");
}

function loadSalesOrderReference(salesOrderId) {
	if (salesOrderId != "") {
		var drTypeId = $.trim($("#hdnDrTypeId").val());
		$.ajax({
			url: contextPath+"/projectRetention/convSOToPr?salesOrderId="+salesOrderId,
			success : function(pr) {
				$("#spanSalesOrderMsg").html("");
				$("#divSOReferenceId").html("");
				$("#aClose")[0].click();
				updateHeader(pr.companyId, pr.company.name, pr.divisionId, pr.division.name, pr.salesOrderId,
						pr.soNumber, pr.poNumber, pr.arCustomerId, pr.arCustomer.name, pr.arCustomerAccountId,
						pr.arCustomerAccount.name, pr.currencyId, pr.currencyRateId, pr.currencyRateValue);
				setupPrLines(pr.projectRetentionLines);
				loadWtaxAcctSettings();
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function computeDueDate() {
	var termDays = $("#hdnTermDays").val();
	var dateVal = $("#date").val();
	if (dateVal == null || dateVal == "") {
		$("#dueDate").val("");
		return;
	}
	var date = new Date(dateVal);
	date.setDate(date.getDate() + parseInt(termDays));
	if (!isNaN(date.getMonth()) && !isNaN(date.getDate()) && !isNaN(date.getFullYear())) {
		$("#dueDate").val((date.getMonth()+1)+"/"+date.getDate()+"/"+date.getFullYear());
	} else {
		$("#dueDate").val("");
	}
}

function updateHeader(companyId, companyName, divisionId, divisionName, soId, soNumber, poNumber,
		arCustomerId, arCustomerName, arCustomerAcctId, arCustomerAcctName, currencyId, currencyRateId,
		currencyRateValue) {
	$("#companyId").val(companyId);
	$("#spCompanyName").text(companyName);
	$("#divisionId").val(divisionId);
	$("#spDivisionName").text(divisionName);
	$("#hdnSalesOrderId").val(soId);
	$("#txtSoNumber").val(soNumber);
	$("#hdnPoNumber").val(poNumber);
	$("#spPoNumber").text(poNumber);
	$("#hdnArCustomerId").val(arCustomerId);
	$("#spCustomerName").text(arCustomerName);
	$("#arCustomerAcctId").val(arCustomerAcctId);
	$("#spCustomerAcctName").text(arCustomerAcctName);
	$("#currencyId").val(currencyId);
	$("#hdnCurrRateId").val(currencyRateId == 0 ? "" : currencyRateId);
	$("#hdnCurrRateValue").val(currencyRateValue);
}

function initializePrLineTable() {
	var prLineJson = JSON.parse($("#projectRetentionLinesJson").val());
	setupPrLines(prLineJson);
}

function setupPrLines(prLineJson) {
	$("#prLineTable").html("");
	var path = "${pageContext.request.contextPath}";
	$prLine = $("#prLineTable").editableItem({
		data: prLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "referenceNo", "varType" : "string"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "currencyRateValue", "varType" : "double"}
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Particulars",
				"cls" : "referenceNo tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "35%",
				"handler" : new ProjectRetentionHandler(new function() {
					this.handleTotal = function(total) {
						// Do nothing
					};
				})},
			{"title" : "Gross Amount",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "15%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "15%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "15%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "15%"},
			{"title" : "currencyRateValue", 
				"cls" : "currencyRateValue", 
				"editor" : "hidden",
				"visible" : false },
		],
		"disableDuplicateStockCode" : false
	});

	$("#prLineTable").on("blur", ".tblInputNumeric", function() {
		computeGrandTotal();
		computeWtax();
		$(this).val(formatDecimalPlaces($(this).val()));
	});

	$("#prLineTable").on("change", ".tblSelectClass", function() {
		computeGrandTotal();
		computeWtax();
	});
	
	computeGrandTotal();
	computeWtax();
}

function computeWtax() {
	computeGrandTotal();
	var wtAcctSettingId = $("#wtaxAcctSettingId").val();
	if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != "undefined") {
		var wtPercentageValue = 0;
		var uri = contextPath + "/getWithholdingTaxAcct/getWtPercentage?wtAccountSettingId="+wtAcctSettingId;
		$.ajax({
			url: uri,
			success : function(wt) {
				wtPercentageValue = (accounting.unformat(wt) / 100);
				var totalAmount = accounting.unformat($("#subTotal").html());
				var computedWTax = (totalAmount * wtPercentageValue).toFixed(6);
				computedWTax = formatDecimalPlaces(computedWTax);
				$("#hdnWtAmount").val(accounting.unformat(computedWTax));
				$("#computedWTax").text(computedWTax);
				computeGrandTotal();
			},
			error : function(error) {
				wtPercentageValue = 0;
			},
			dataType: "text"
		});
	} else {
		$("#hdnWtAmount").val("0.00");
		$("#computedWTax").html("0.00");
		computeGrandTotal();
	}
}

function computeGrandTotal() {
	var totalPrLine = 0;
	var totalVAT = 0;
	$("#prLineTable").find(" tbody tr ").each(function(row) {
		totalPrLine += accounting.unformat($(this).find(".amount").html());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	var subTotal = totalPrLine;
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	$("#subTotal").text(formatDecimalPlaces(subTotal));
	$("#totalVAT").html(formatDecimalPlaces(totalVAT));
	$("#computedWTax").html(formatDecimalPlaces(wtaxAmount));
	var grandTotal = subTotal + totalVAT - wtaxAmount;
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function formatMoney(elem) {
	$(elem).val(formatDecimalPlaces($(elem).val()));
}
</script>
<style>
</style>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divSOReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divSOReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="projectRetention" id="projectRetentionForm">
	<div class="modFormLabel">Project Retention<span id="spanDivisionLbl"> - ${projectRetention.division.name}</span>
	<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="sequenceNo"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
		<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
		<form:hidden path="referenceDocumentJson" id="referenceDocumentJson" />
		<form:hidden path="projectRetentionLinesJson" id="projectRetentionLinesJson" />
		<form:hidden path="wtAmount" id="hdnWtAmount" />
		<form:hidden path="projectRetentionTypeId" />
		<br>
		<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="labels">PR No</td>
					<td class="value">
						<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"
									value="${projectRetention.sequenceNo}"/>
					</td>
				</tr>
				<tr>
							<td class="labels">Status</td>
							<td class="value">
								<span id="spanFormStatusLbl">
									<c:choose>
										<c:when test="${projectRetention.formWorkflow ne null}">
											${projectRetention.formWorkflow.currentFormStatus.description}
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
		<legend>Project Retention Header</legend>
			<table class="formTable">
				<tr>
					<td class="labels">* Company</td>
					<td class="value">
						<form:hidden path="companyId" id="companyId"/>
						<span id="spCompanyName">${projectRetention.company.name}</span>
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
						<span id="spDivisionName">${projectRetention.division.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">SO Reference</td>
					<td class="value">
						<form:hidden path="salesOrderId" id="hdnSalesOrderId"/>
						<input id="txtSoNumber" readonly="readonly"/>
						<a href="#container" id="aOpen" data-reveal-id="divSOReferenceId" class="link_button"
							onclick="showSOReferences();">Browse SO</a>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="salesOrderId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><span id="spanSalesOrderMsg" class="error"></span></td>
				</tr>
				<tr>
					<td class="labels">PO/PCR No.</td>
					<td class="value">
						<form:hidden path="poNumber" id="hdnPoNumber"/>
						<span id="spPoNumber">${projectRetention.poNumber}</span>
					</td>
				</tr>
				<tr>
					<td class="labels">* Customer</td>
					<td class="value">
						<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
						<span id="spCustomerName">${projectRetention.arCustomer.name}</span>
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
					<td class="labels">* Customer Account</td>
					<td class="value">
						<form:hidden path="arCustomerAccountId" id="arCustomerAcctId"/>
						<span id="spCustomerAcctName">${projectRetention.arCustomerAccount.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value">
						<form:errors path="arCustomerAccountId" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td class="labels">* Date</td>
					<td class="value">
						<form:input path="date" id="date" onblur="evalDate('date'); computeDueDate();"
							style="width: 120px;" class="dateClass2" />
						<img id="imgDate" src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('date')"
							style="cursor: pointer" style="float: right;" />
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="date" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">* Due Date</td>
					<td class="value">
						<form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate');"
							style="width: 120px;" class="dateClass2" />
						<img id="imgDueDate" src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dueDate');"
							style="cursor: pointer" style="float: right;" />
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="dueDate" id="errDueDate" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">* Amount</td>
					<td class="value">
						<form:input path="amount" id="amount" cssClass="inputSmall inputAmount" onblur="formatMoney(this)"/>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="amount" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">Currency</td>
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
					<td class="labels">Remarks</td>
					<td class="value">
						<form:textarea path="remarks" id="remarks" class="txtArea"/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Project Retention Detail</legend>
				<div id="prLineTable"></div>
			<table>
				<tr>
					<td colspan="12">
						<form:errors path="projectRetentionLines" cssClass="error"
							style="margin-top: 12px;" />
					</td>
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
					<td colspan="12"><span class="error" id="spanDocumentSize"></span></td>
				</tr>
				<tr>
					<td colspan="12"><span class="error" id="spanReferenceDoc" style="margin-top: 12px;"></span></td>
				</tr>
			</table>
		</fieldset>
		<table class="frmField_set">
			<tr>
				<td><form:errors path="commonErrorMsg" cssClass="error"/></td>
			</tr>
		</table>
		<br>
		<table class="frmField_set footerTotalAmountTbl">
			<tr>
				<td style="width: 22%;"></td>
				<td style="width: 22%;"></td>
				<td style="width: 22%;">Retention Amount</td>
				<td style="width: 22%;"></td>
				<td style="width: 12%;"><span id="subTotal"></span></td>
			</tr>
			<tr>
				<td colspan="3">VAT</td>
				<td></td>
				<td>
					<span id="totalVAT"></span>
				</td>
			</tr>
			<tr>
				<td colspan="3">Withholding Tax</td>
				<td>
					<form:select path="wtAcctSettingId" id="wtaxAcctSettingId" cssClass="frmSmallSelectClass"
						cssStyle="width: 95%;" onchange="assignWtaxAcctSetting(this);">
					</form:select>
				</td>
				<td>
					<span id="computedWTax">0.00</span>
				</td>
			</tr>
			<tr>
				<td colspan="3">Net Amount</td>
				<td></td>
				<td>
					<span id="grandTotal">0.00</span>
				</td>
			</tr>
		</table>
		<br>
		<table class="frmField_set">
			<tr>
				<td align="right"><input type="button" id="btnSaveProjectRetention"
						value="Save" onclick="saveProjectRetention();" /></td>
			</tr>
		</table>
		</div>
	</form:form>
</div>
</body>
</html>