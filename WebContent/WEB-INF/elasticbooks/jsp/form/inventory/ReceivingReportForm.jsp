<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Retail Receiving Report Form with Purchase Order Reference.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.serialitemunitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
.disableSelect{
	pointer-events: none;
	cursor: default;
}

.prevUC {
	float: right;
	width: 100%;
	text-align: right;
}
</style>
<script type="text/javascript">
var selectedCompanyId = -1;
var $receivingReportTable = null;
var $otherChargesTable = null;
var $serializedItemTable = null;
var selectedWarehouseId = "${apInvoice.receivingReport.warehouseId}";
var selectedSupplierAcct = "${apInvoice.supplierAccountId}";
var supplierAcctTerms =  new Array();
var terms =  new Array();
var poReferenceWindow = null;
var isPO = false;
var $documentsTable = null;
var defaultDivisionId = "${apInvoice.receivingReport.divisionId}";
$(document).ready(function () {
	var termId = null;
	var dueDate = null;
	var wtAcctSettingId = "";
	loadDivision(defaultDivisionId);
	$("#divisionId").attr("disabled","disabled");
	$("#supplierId").attr("disabled","disabled");
	$("#supplierAcctId").attr("disabled","disabled");
	if ("${apInvoice.id}" > 0) {
		$("#btnPoReference").attr("disabled", "disabled");
		disableSelectFields("${apInvoice.formWorkflow.complete}");
		filterWarehouse(selectedWarehouseId);
		updateFieldsByFormStatus();
		if ($("#purchaseOderId").val() > 0) {
			isPO = true;
			disableSupplier();
		}
		wtAcctSettingId = "${apInvoice.wtAcctSettingId}";
	} else {
		filterWarehouse();
		computeDueDate();
	}
	initDocumentsTbl();
	initRRTables();
	filterSupplierAccts(dueDate, termId);
});

function updateFieldsByFormStatus() {
	// If form workflow status = COMPLETED and CANCELLED
	var rrStatus = "${apInvoice.formWorkflow.complete}";
	if (rrStatus == "true" || "${apInvoice.formWorkflow.currentStatusId}" == 4) {
		disableFields(); //Disable all fields
		if (rrStatus == "true") {
			specialCaseEditing(); //Enable selected fields
		}
	}
}

function Term (id, days) {
	this.id = id;
	this.days = days;
}

function disableSelectFields(isComplete) {
	//Disable the drop down lists
	disableAndSetCompany();
	if (isComplete == "true") {
		//Disable only if workflow is completed.
		$("#warehouseId").attr("disabled","disabled");
		$("#supplierAcctId").attr("disabled","disabled");
		$("#selectTermId").attr("disabled","disabled");
		$("#wtaxAcctSettingId").attr("disabled","disabled");
	}
}

function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${apInvoice.receivingReport.companyId}"+"'>"+
			"${apInvoice.receivingReport.company.numberAndName}"+"</option>");
}

function enableSelectFields() {
	// Enable the drop down lists
	$("#rReceivingReportDiv :input").removeAttr("disabled");
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

function disableInputFields(elem, spanIdName, isOtherCharges) {
	var $tr = $(elem).closest("tr");
	var $name = null;
	var tableLbl = "";
	if(isOtherCharges) {
		$name = $tr.find(".apLineSetupName");
		tableLbl = "service/s";
	} else {
		$name = $tr.find(".stockCode");
		tableLbl = "item/s";
	}
	$name.attr("disabled", "disabled");
	if ($.trim($name.val()) == "") {
		$("#"+spanIdName).text("Adding of new "+ tableLbl + " is not allowed.");
	}
}

function initializeTable (rrItemsJson) {
	var rrItemsJson = JSON.parse($("#rrItemsJson").val());
	setupTableData (rrItemsJson);
}

function setupTableData (rrItemsJson) {
	var cPath = "${pageContext.request.contextPath}";
	$("#receivingReportTable").html("");
	$receivingReportTable = $("#receivingReportTable").editableItem({
		data: rrItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "apInvoiceId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "referenceId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "srp", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "itemDiscountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "origQty", "varType" : "double"}
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
			{"title" : "referenceId",
				"cls" : "referenceId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "apInvoiceId", 
				"cls" : "apInvoiceId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "itemId", 
				"cls" : "itemId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code", 
				"cls" : "stockCode tblInputText", 
				"editor" : "text",
				"visible" : true,
				"width" : "10%",
				"handler" : new SerialItemUnitCostTblHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "Existing <br> Stock",
				"cls" : "existingStock tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "origQty",
				"cls" : "origQty tblInputNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "UOM",
				"cls" : "uom tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Previous<br>Unit Cost",
				"cls" : "prevUC tblLabelNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Gross Price", 
				"cls" : "unitCost tblInputNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "SRP", 
				"cls" : "srp tblLabelNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Amount", 
				"cls" : "amount tblLabelNumeric",
				"editor" : "hidden",
				"visible" : false}
		],
	    "disableDuplicateStockCode" : false,
        "itemTableMessage": "rrMessage"
	});

	$("#receivingReportTable").on("focus", ".stockCode", function(){
		disableInputFields(this, "rrMessage");
	});

	$("#receivingReportTable").on("change", ".discountType", function() {
		var discountTypeId = $(this).val();
		$(this).closest("tr").find(".itemDiscountTypeId").val(discountTypeId);
		if (discountTypeId == "") {
			$(this).closest("tr").find(".discount").html("");
			$(this).closest("tr").find(".discountValue").val("0.00");
		}
	});

	$("#receivingReportTable").on("blur", ".quantity", function() {
		computeVAT($(this), $(this).closest("tr").find(".taxTypeId").val(), 0.0);
	});

	$("#receivingReportTable").on("blur", ".discountValue", function() {
		var value = $(this).val();
		if (value == "") {
			$(this).closest("tr").find(".discount").html("");
		}
	});

	$("#receivingReportTable").on("focus", ".tblInputNumeric", function() {
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#receivingReportTable").on("blur", ".tblInputNumeric", function() {
		var $unitCost = $(this).closest("tr").find(".unitCost");
		$unitCost.val(formatDecimalPlaces($unitCost.val(), 4));
	});
	resizeTbl("#receivingReportTable", 15);
}

function computeVAT($select, taxTypeId, discount) {
	var $tr = $($select).closest("tr");
	if (typeof discount == "undefined") {
		discount = 0.0;
	}
	var quantity =  accounting.unformat($tr.find(".quantity").val());
	var qtyDiscount = Math.abs(quantity != 0 ? (discount / quantity) : 0);
	var unitCost = accounting.unformat($tr.find(".unitCost").val());
	unitCost = unitCost - qtyDiscount; //Subtract discount before computing VAT.
	var isVatable = taxTypeId == 1 || taxTypeId == 4 || taxTypeId == 5 || taxTypeId == 6 || taxTypeId == 8 || taxTypeId == 9 || taxTypeId == 10;
	var grossAmount = quantity * unitCost;
	var netOfVAT = isVatable ? formatDecimalPlaces(grossAmount / 1.12) : grossAmount;
	var vat = formatDecimalPlaces(grossAmount - accounting.unformat(netOfVAT));
	$tr.find(".vatAmount").val(vat);
	var netAmount = parseFloat((grossAmount - accounting.unformat(vat)).toFixed(6));
	$tr.find(".amount").val(formatDecimalPlaces(netAmount));
}

function initializetOtherCharges() {
	var otherChargesJson = JSON.parse($("#apInvoiceLinesJson").val());
	setupOtherChargesTbl(otherChargesJson);
}

function setupOtherChargesTbl(otherChargesJson) {
	$("#otherChargesTable").html("");
	var cPath = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: otherChargesJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "apLineSetupId", "varType" : "int"},
				{"name" : "unitOfMeasurementId", "varType" : "int"},
				{"name" : "quantity", "varType" : "int"},
				{"name" : "upAmount", "varType" : "double"},
				{"name" : "amount", "varType" : "double"},
				{"name" : "apLineSetupName", "varType" : "string"},
				{"name" : "unitMeasurementName", "varType" : "string"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "taxTypeId", "varType" : "int"},
				{"name" : "discountTypeId", "varType" : "int"},
				{"name" : "discountValue", "varType" : "double"},
				{"name" : "discount", "varType" : "double"},
				{"name" : "vatAmount", "varType" : "double"},
				{"name" : "percentile", "varType" : "double"},
				{"name" : "refenceObjectId", "varType" : "int"},
				{"name" : "apInvoiceId", "varType" : "int"}
				],
		contextPath: cPath,
		header:[
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "apLineSetupId",
					"cls" : "apLineSetupId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "unitOfMeasurementId",
					"cls" : "unitOfMeasurementId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "ebObjectId",
					"cls" : "ebObjectId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "AP Line",
					"cls" : "apLineSetupName tblInputText",
					"editor" : "text",
					"visible" : true,
					"width" : "25%",
					"handler" : new SerialItemUnitCostTblHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Percentile",
					"cls" : "percentile tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "10%" },
				{"title" : "Qty",
					"cls" : "quantity tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "10%" },
				{"title" : "Temp Qty",
					"cls" : "origQty",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "UOM",
					"cls" : "unitMeasurementName tblInputText",
					"editor" : "text",
					"visible" : true,
					"width" : "10%",
					"handler" : new SerialItemUnitCostTblHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Gross Price",
					"cls" : "upAmount tblInputNumeric",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "discountTypeId",
					"cls" : "discountTypeId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "Discount<br>Type",
					"cls" : "discountType tblSelectClass",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "Discount<br>Value",
					"cls" : "discountValue tblInputNumeric",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "Computed<br>Discount",
					"cls" : "discount tblLabelNumeric",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "taxTypeId", 
					"cls" : "taxTypeId", 
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Tax Type", 
					"cls" : "taxType tblSelectClass",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "VAT Amount",
					"cls" : "vatAmount tblLabelNumeric",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "Amount",
					"cls" : "amount tblInputNumeric",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "refenceObjectId",
					"cls" : "refenceObjectId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "apInvoiceId",
					"cls" : "apInvoiceId",
					"editor" : "hidden",
					"visible" : false }
				],
				"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable").on("focus", ".apLineSetupName", function(){
		disableInputFields(this, "otherChargesMessage", true);
	});

	$("#otherChargesTable").on("blur", ".percentile, .quantity", function() {
		if (otherChargesJson != null && otherChargesJson != "") {
			computePercentile(this);
			updateOChargesAmt();
		}
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function() {
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function() {
		var $upAmount = $(this).closest("tr").find(".upAmount");
		$upAmount.val(formatDecimalPlaces($upAmount.val(), 4));
	});

	$("#otherChargesTable tbody tr").each(function() {
		if(otherChargesJson != null && otherChargesJson != "") {
			var qty = accounting.unformat($(this).closest("tr").find(".quantity").val());
			var percentile = accounting.unformat($(this).closest("tr").find(".percentile").val()) / 100;
			if(!isNaN(Number(qty))) {
				var origQty = qty / percentile;
				$(this).closest("tr").find(".origQty").val(origQty);
			}
			if(qty == 0) {
				$(this).closest("tr").find(".quantity").attr("disabled","disabled");
				$(this).closest("tr").find(".percentile").attr("disabled","disabled");
			}
		}
	});

	resizeTbl("#otherChargesTable", 11);
	formatNumbers("otherChargesTable");
}

function computePercentile(elem) {
	var origQty = $(elem).closest("tr").find(".origQty").val();
	var isQtyElem = $(elem).attr("class").includes("quantity");//True if the edited element is quantity.
	if(!isNaN(Number(origQty))) {
		if(isQtyElem) {
			var qty = $(elem).val();
			var percentile = (qty / origQty) * 100;
			$(elem).closest("tr").find(".percentile").val(percentile);
		} else {
			var percentile = $(elem).val();
			var computedQty = origQty * (percentile / 100);
			$(elem).closest("tr").find(".quantity").val(computedQty);
		}
	}
	//Format values
	//Percentile
	var pElem = $(elem).closest("tr").find(".percentile");
	$(pElem).val(accounting.formatMoney($(pElem).val()));
	//Qty
	var qElem = $(elem).closest("tr").find(".quantity");
	$(qElem).val($(qElem).val());
}

function resizeTbl(tableId, rowCount){
	var $tbl = $(tableId);
	$($tbl).attr("width", "100%");
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function SupplierAcctTerm (supplierAcctId, termId) {
	this.supplierAcctId = supplierAcctId;
	this.termId = termId;
}

function disableSupFields() {
	$("#supplierAcctId").attr("disabled", "disabled");
	$("#supplierId").attr("disabled", "disabled");
}

function filterSupplierAccts(dueDate, termId, supplierAcctId) {
	$("#supplierAcctId").empty();
	if ($.trim($("#supplierId").val()) == "") {
		$("#aSupplierId").val("");
	} else {
		selectedCompanyId = $("#companyId").val();
		var selectedSupplierId = $("#aSupplierId").val();
		var divisionId = $("#divisionId").val();
		var uri = contextPath+"/getApSupplierAccts?supplierId="+selectedSupplierId+"&companyId="+selectedCompanyId
				+"&divisionId="+divisionId;
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		postHandler = {
				doPost: function(data) {
					$("#supplierAcctId").val(selectedSupplierAcct).attr("selected",true);
					var supplierAcctId = $("#supplierAcctId option:selected").val();
					supplierAcctTerms = [];
					for (var index = 0; index < data.length; index++){
						var rowObject =  data[index];
						var id = rowObject["id"];
						if (id == supplierAcctId){
							if (termId == null) {
								var defaultTerm = rowObject["termId"];
								$("#selectTermId").val(defaultTerm).attr("selected" ,true);
							}
						}
						var suppAcctTerm = new SupplierAcctTerm(id, rowObject["termId"]);
						supplierAcctTerms.push (suppAcctTerm);
						if (isPO){
							$("#selectTermId").val(termId).attr("selected" ,true);
						}
					}

					//Compute due date
					if (dueDate == null) {
						computeDueDate();
					}
				}
		};
		loadPopulate (uri, false, supplierAcctId, "supplierAcctId", optionParser, postHandler);
	}
}

function filterWarehouse (warehouseId) {
	var selectedCompanyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	if (selectedCompanyId > 0) {
		$("#warehouseId").empty();
		var uri = contextPath+"/getWarehouse/new?companyId="+selectedCompanyId+"&divisionId="+divisionId;
		if(warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
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
					$("#warehouseId option").each(function() {
						if ($.inArray(this.value, found) != -1) 
							$(this).remove();
					  	found.push(this.value);
					});
					$("#warehouseId").val(selectedWarehouseId);
				}
		};
		loadPopulate (uri, false, selectedWarehouseId, "warehouseId", optionParser, postHandler);
	}
}
function initRRTables() {
	initializeTable ();
	initializeSerializedItemTable();
	initializetOtherCharges();
}

function assignWarehouse (select) {
	selectedWarehouseId = $(select).val();
	reloadExistingStocks();
}

function reloadExistingStocks() {
	//Non-serialized items
	$("#receivingReportTable tbody tr").each(function() {
		var itemId = $(this).closest("tr").find(".itemId").val();
		var existingStock = $(this).closest("tr").find(".existingStock");
		setExistingStocks(itemId, existingStock);
	});
	//Serialized items
	$("#serializedItemTable tbody tr").each(function() {
		var itemId = $(this).closest("tr").find(".itemId").val();
		var existingStock = $(this).closest("tr").find(".existingStock");
		setExistingStocks(itemId, existingStock);
	});
}

function setExistingStocks(itemId, elem) {
	var existingStocks = 0;
	var warehouseId = $("#warehouseId").val();
	if(itemId != "") {
		$.ajax({
			url: contextPath + "/getItem/existingStocks?itemId="+itemId+ "&warehouseId=" + warehouseId,
			success : function(es) {
				if (es != null) {
					existingStocks = es;
				} else {
					existingStocks = 0.00;
				}
				$(elem).text(formatDecimalPlaces(existingStocks));
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function showSuppliers () {
	if ($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Company is required");
	} else {
		var supplierName = encodeURIComponent($.trim($("#supplierId").val()));
		if ($("#supplierId").val() != "") {
			var uri = contextPath + "/getSuppliers/new?name="+supplierName+ "&companyId=" + $("#companyId").val();
			$("#supplierId").autocomplete({
				source: uri,
				select: function( event, ui ) {
					$("#aSupplierId").val(ui.item.id);
					$("#spanSupplierError").text("");
					$(this).val(ui.item.name);
					filterSupplierAccts();
					return false;
				}, minLength: 2,
				change: function(event, ui){
					$.ajax({
						url: uri,
						success : function(item) {
							$("#spanSupplierError").text("");
							if (ui.item != null && ui.item != undefined) {
								$("#aSupplierId").val(ui.item.id);
								$(this).val(ui.item.name);
							}
						},
						error : function(error) {
							$("#spanSupplierError").text("Please select supplier.");
							$("#supplierId").val("");
						},
						dataType: "json"
					});
				}
			}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
				return $( "<li>" )
					.data( "ui-autocomplete-item", item )
					.append( "<a style='font-size: small;'>" +item.name + "</a>" )
					.appendTo( ul );
			};
		}
	}
}

function getSupplier() {
	var supplierName = encodeURIComponent($.trim($("#supplierId").val()));
	if ($("#companyId").val() != -1) {
		$.ajax({
			url: contextPath + "/getSuppliers/new?name="+supplierName+ "&companyId=" + $("#companyId").val() + "&isExact=true",
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#aSupplierId").val(supplier[0].id);
					$("#supplierId").val(supplier[0].name);
				}else
					$("#aSupplierId").val("");
				filterSupplierAccts();
			},
			error : function(error) {
				$("#supplierId").val("");
				$("#supplierAcctId").empty();
			},
			dataType: "json"
		});
	}
}

var isSaving = false;
function saveReceivingReport () {
	var hasPO = $.trim($("#poNumber").val()) != "";
	if (isSaving == false && hasPO && !checkExceededFileSize()) {
		isSaving = true;
		enableSelectFields();
		$("#rrItemsJson").val($receivingReportTable.getData());
		$("#apInvoiceLinesJson").val($otherChargesTable.getData());
		$("#serialItemsJson").val($serializedItemTable.getData());
		$("#referenceDocumentJson").val($documentsTable.getData());
		$("#btnSaveReceivingReport").attr("disabled", "disabled");
		$("#divisionId").removeAttr("disabled");
		$("#supplierAcctId").removeAttr("disabled");
		$("#supplierId").removeAttr("disabled");
		doPostWithCallBack ("rReceivingReportDiv", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if ("${apInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = "";
				}
				isSaving = false;
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var termId = $("#selectTermId").val();
				var dueDate = $("#dueDate").val();
				var supplier = $("#supplierId").val();
				var warehouseId = $("#warehouseId").val();
				var poNumber = $("#poNumber").val();
				var companyId = $("#companyId option:selected").val();
				var wtaxAcctSettingId = $("#wtaxAcctSettingId").val();
				var currencyId = $("#hdnCurrencyId").val();
				var currencyRateId = $("#hdnCurrencyRateId").val();
				var currencyRateValue = $("#hdnCurrencyRateValue").val();
				if ("${apInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val(companyId);
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableSelectFields();
					updatePopupCss();
				}
				$("#hdnCurrencyId").val(currencyId);
				$("#hdnCurrencyRateId").val(currencyRateId);
				$("#hdnCurrencyRateValue").val(currencyRateValue);
				$("#spanDivisionLbl").text(spanDivisionLbl);
				loadDivision(defaultDivisionId);
				$("#divisionId").attr("disabled","disabled");
				initDocumentsTbl();
				$("#supplierId").val(supplier);
				$("#poNumber").val(poNumber);
				filterWarehouse (warehouseId);
				initRRTables();
				filterSupplierAccts(dueDate, termId);
				disableSupplier();
				isSaving = false;
			}
			updateFieldsByFormStatus();
			$("#btnSaveReceivingReport").removeAttr("disabled");
		});
	} else if(!hasPO) {
		$("#spanPoNumber").text("PO Number is required.");
	}

	if(checkExceededFileSize()) {
		$("#spanDocumentSize").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function specialCaseEditing() {
	$("#imgDate1").hide();
}

function disableFields(){
	$("#poNumber").attr("readonly", true);
	$("#bmsNumber").attr("readonly", true);
	$("#supplierId").attr("readonly", true);
	$("#imgDate1").hide();
	$("#rrDate").attr("readonly", true);
	$("#imgDate3").hide();
	$("#dueDate").attr("readonly", true);
	$(".delrow").hide();
	$(".tblInputText").attr("disabled","disabled");
	$(".tblInputNumeric").attr("disabled","disabled");
	$(".fileInput").attr("disabled","disabled");
	$("#btnSaveReceivingReport").attr("disabled","disabled");
	$(".link_button").hide();
}

function computeDueDate () {
	var glDateVal = $("#rrDate").val ();
	if (glDateVal == null || glDateVal == ""){
		return;
	}
	var additionalDays = 0;
	var currentSelTermId = $("#selectTermId option:selected").val();
	for (var i = 0; i < terms.length; i++) {
		var term = terms[i];
		if (term.id == currentSelTermId) {
			additionalDays = term.days;
			break;
		}
	}
	var glDate = new Date (glDateVal);
	glDate.setDate(glDate.getDate() + parseInt(additionalDays));
	$("#dueDate").val ((glDate.getMonth() + 1) +"/"+glDate.getDate()+"/"+glDate.getFullYear());
}

function assignSupplierAcct (select) {
	selectedSupplierAcct = $(select).val();
	for (var i=0; i<supplierAcctTerms.length; i++) {
		if (selectedSupplierAcct == supplierAcctTerms[i].supplierAcctId) {
			$("#selectTermId").val(supplierAcctTerms[i].termId).attr("selected" ,true);
			break;
		}
	}
}

function companyIdOnChange () {
	filterWarehouse();
	$receivingReportTable.emptyTable();
	$otherChargesTable.emptyTable();
	$("#supplierId").val("");
	$("#supplierAcctId").empty();
}

function showPOReference() {
	$("#divPOReferenceId").load(contextPath+"/retailReceivingReport/"+defaultDivisionId+"/poReference");
}

function loadPOReference(poId) {
	$.ajax({
		url: contextPath + "/retailReceivingReport/?poId="+poId,
		success : function(rr) {
			$("#divPOReferenceId").html("");
			$("#aClose")[0].click();
			updateHeader(rr.companyId, rr.apInvoice.supplierId, rr.apInvoice.supplierAccountId,
					rr.poNumber, rr.apInvoice.termId, rr.apInvoice.supplier.name, rr.apInvoice.purchaseOderId,
					rr.apInvoice.bmsNumber, rr.apInvoice.currencyId, rr.apInvoice.currencyRateId, rr.apInvoice.currencyRateValue);
			setupTableData(rr.apInvoice.rrItems);
			setupSerialiedTableData(rr.serialItems);
			setupOtherChargesTbl(rr.apInvoice.apInvoiceLines);
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	isPO = true;
	disableSupplier();
}

function disableSupplier() {
	if (isPO) {
		$("#supplierId").attr("readonly", true);
		$("#supplierId").removeAttr('onblur');
		$("#supplierAcctId").attr("disabled", "disabled");
		$("#poNumber").attr("readonly", true);
		$("#bmsNumber").attr("readonly", true);
	}
}

function updateHeader (companyId, supplierId, supplierAcctId,
		poNumber, termId, supplierName, purchaseOderId, bmsNumber, currencyId,
		currencyRateId, currencyRateValue) {
	$("#aSupplierId").val(supplierId);
	$("#supplierId").val(supplierName);
	$("#companyId").val(companyId);
	$("#poNumber").val(poNumber);
	$("#purchaseOderId").val(purchaseOderId);
	$("#bmsNumber").val(bmsNumber);
	$("#hdnCurrencyId").val(currencyId);
	$("#hdnCurrencyRateId").val(currencyRateId != 0 ? currencyRateId : null);
	$("#hdnCurrencyRateValue").val(currencyRateValue);
	filterWarehouse();
	filterSupplierAccts(null, termId, supplierAcctId);
	disableSupFields();
}

function initializeSerializedItemTable () {
	var serialItemsJson = JSON.parse($("#serialItemsJson").val());
	setupSerialiedTableData(serialItemsJson);
}

function setupSerialiedTableData(serialItemsJson) {
	$("#serializedItemTable").html("");
	var cPath = "${pageContext.request.contextPath}";
	$serializedItemTable = $("#serializedItemTable").editableItem({
		data: serialItemsJson,
		jsonProperties: [
          {"name" : "id", "varType" : "int"},
          {"name" : "apInvoiceId", "varType" : "int"},
          {"name" : "itemId", "varType" : "int"},
          {"name" : "ebObjectId", "varType" : "int"},
          {"name" : "refenceObjectId", "varType" : "int"},
          {"name" : "stockCode", "varType" : "string"},
          {"name" : "quantity", "varType" : "double"},
          {"name" : "unitCost", "varType" : "double"},
          {"name" : "srp", "varType" : "double"},
          {"name" : "itemDiscountTypeId", "varType" : "int"},
          {"name" : "discountValue", "varType" : "double"},
          {"name" : "serialNumber", "varType" : "string"},
          {"name" : "taxTypeId", "varType" : "int"},
          {"name" : "discount", "varType" : "double"},
          {"name" : "vatAmount", "varType" : "double"}
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
			{"title" : "itemId", 
				"cls" : "itemId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code", 
				"cls" : "stockCode tblInputText", 
				"editor" : "text",
				"visible" : true,
				"width" : "8%",
				"handler" : new SerialItemUnitCostTblHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible": true,
				"width" : "13%"},
			{"title" : "Existing <br> Stock",
				"cls" : "existingStock tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Qty",
				"cls" : "quantityTxt tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Serial Number",
				"cls" : "serialNumber tblInputText",
				"editor" : "text",
				"visible": true,
				"width" : "10%"},
			{"title" : "UOM",
				"cls" : "uom tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Previous<br>Unit Cost",
				"cls" : "prevUC tblLabelNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Gross Amount", 
				"cls" : "unitCost tblInputNumeric", 
				"editor" : "hidden",
				"visible" : false,
				"width" : "8%" },
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "SRP", 
				"cls" : "srp tblLabelNumeric", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Amount", 
				"cls" : "amount tblLabelNumeric", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Quantity",
				"cls" : "quantity",
				"editor" : "hidden",
				"visible": false},
		],
		"disableDuplicateStockCode" : true,
	});

	$("#serializedItemTable").on("blur", ".tblInputText", function(){
		if($.trim($(this).val()) != "") {
			$(this).closest("tr").find(".quantityTxt").text(1);
			$(this).closest("tr").find(".quantity").val(1);
		}
	});

	$("#serializedItemTable").on("focus", ".stockCode", function(){
		disableInputFields(this, "spanSerialItemMessage", false);
	});

	formatNumbers("serializedItemTable");
}

function formatNumbers(tableName) {
	$("#"+tableName).find(" tbody tr ").each(function(row) {
		var $quantity = $(this).closest("tr").find(".quantity");
		var $txtQuantity = $(this).closest("tr").find(".quantityTxt");
		var quantity = $.trim($($quantity).val());
		if (quantity != "") {
			$($quantity).val(quantity);
			$($txtQuantity).text(quantity);
		}

		var $unitCost = $(this).closest("tr").find(".unitCost");
		var unitCost = $.trim($($unitCost).val());
		if(unitCost != "") {
			$($unitCost).val(unitCost);
		}
	});
}

function loadDivision(divisionId) {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+"&divisionId="+ (divisionId != null && divisionId != "" ? divisionId : 0);
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
</script>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divPOReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divPOReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="apInvoice" id="rReceivingReportDiv">
		<div class="modFormLabel">Receiving Report <span id="spanDivisionLbl"> - ${apInvoice.receivingReport.division.name}</span>
		<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="divisionId" id="hdnDivisionId"/>
		<form:hidden path="companyId" id="hdnCompanyId"/>
		<form:hidden path="currencyId" id="hdnCurrencyId"/>
		<form:hidden path="currencyRateId" id="hdnCurrencyRateId"/>
		<form:hidden path="currencyRateValue" id="hdnCurrencyRateValue"/>
		<form:hidden path="invoiceTypeId"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="sequenceNumber"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="receivingReport.id" id="receivingReportId"/>
		<form:hidden path="receivingReport.createdBy"/>
		<form:hidden path="receivingReport.createdDate"/>
		<form:hidden path="rrItemsJson" id="rrItemsJson"/>
		<form:hidden path="apInvoiceLinesJson" id="apInvoiceLinesJson"/>
		<form:hidden path="receivingReport.serialItemsJson" id="serialItemsJson"/>
		<form:hidden path="formWorkflow.currentFormStatus.description" id="workflowDescription"/>
		<form:hidden path="purchaseOderId" id="purchaseOderId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="wtAmount" id="hdnWtAmount"/>
		<form:hidden path="referenceDocumentsJson" id="referenceDocumentJson" />
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">RR No.</td>
						<td class="value">
							<c:if test="${apInvoice.id > 0}">
								${apInvoice.sequenceNumber}
							</c:if>
							<span id="sequenceNo"></span>
						</td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:choose>
								<c:when test="${apInvoice.id > 0}">
									${apInvoice.formWorkflow.currentFormStatus.description}
								</c:when>
								<c:otherwise>
									NEW
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Receiving Report Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Company </td>
						<td class="value">
							<form:select path="receivingReport.companyId" id="companyId" cssClass="frmSelectClass" onchange="companyIdOnChange();"
								items="${companies}" itemLabel="numberAndName" itemValue="id" >
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanCompanyError" class="error" style="margin-left: 12px;"></span>
							<form:errors path="receivingReport.companyId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Division</td>
						<td class="value">
							<form:select path="receivingReport.divisionId" id="divisionId" class="frmSelectClass"></form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Warehouse </td>
						<td class="value">
							<form:select path="receivingReport.warehouseId" id="warehouseId" class="frmSelectClass" onchange="assignWarehouse(this);"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value" >
							<span id="spanWarehouseError" class="error"></span>
							<form:errors path="receivingReport.warehouseId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* PO Number</td>
						<td class="value">
							<form:input path="receivingReport.poNumber" id="poNumber" class="standard"  readonly="true"/>
							<a href="#container" id="aOpen" data-reveal-id="divPOReferenceId" class="link_button"
								onclick="showPOReference();">Browse PO</a>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value" >
							<span id="spanPoNumber" class="error"></span>
						</td>
					</tr>
					<tr>
						<td class="labels">BMS Number</td>
						<td class="value"><form:input path="receivingReport.bmsNumber" id="bmsNumber" class="standard" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value" >
							<form:errors path="receivingReport.bmsNumber" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Date</td>
						<td class="value">
							<form:input path="glDate" onblur="evalDate('rrDate'); computeDueDate();" 
								id="rrDate" style="width: 120px;" cssClass="dateClass2"/>
							<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('rrDate')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="glDate" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Supplier</td>
						<td class="value">
							<form:input path="supplier.name" id="supplierId" class="input" onkeydown="showSuppliers();"
								onkeyup="showSuppliers();" onblur="getSupplier();" />
							<form:input path="supplierId" type="hidden" id="aSupplierId" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanSupplierError" class="error" style="margin left: 12px;"></span>
							<form:errors path="supplierId" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Supplier Account</td>
						<td class="value">
							<form:select path="supplierAccountId" id="supplierAcctId" class="frmSelectClass" onchange="assignSupplierAcct(this);"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanSupplierAcctError" class="error" style="margin left: 12px;"></span>
							<form:errors path="supplierAccountId" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Supplier Invoice No</td>
						<td class="value"><form:input path="invoiceNumber" id="supplierInvoiceNo" class="standard" /></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanSupplierInvoiceError" class="error" style="margin left: 12px;"></span>
							<form:errors path="invoiceNumber" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Delivery Receipt No</td>
						<td class="value"><form:input path="receivingReport.deliveryReceiptNo" id="deliveryReceiptNo" class="standard" /></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanDeliveryReceiptError" class="error" style="margin left: 12px;"></span>
							<form:errors path="receivingReport.deliveryReceiptNo" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Remarks</td>
						<td class="value">
							<form:textarea path="receivingReport.remarks" rows="3"
								cssStyle="width: 350px; resize: none;"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="receivingReport.remarks" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Serialized Good/s</legend>
				<div id="serializedItemTable"></div>
				<table>
					<tr>
						<td><span id="spanSerialItemMessage" class="error"></span></td>
					</tr>
					<tr>
						<td>
							<form:errors path="receivingReport.siMessage" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Non-Serialized Good/s</legend>
				<div id="receivingReportTable" ></div>
				<table>
					<tr>
						<td><span id="rrMessage" class="error"></span></td>
					</tr>
					<tr>
						<td>
							<form:errors path="aPlineMessage" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<!-- Other Charges -->
			<fieldset class="frmField_set">
				<legend>Service/s</legend>
				<div id="otherChargesTable"></div>
				<table>
					<tr>
						<td>
							<span id="otherChargesMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td>
							<form:errors path="apInvoiceLines" cssClass="error"/>
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
					<td>
						<form:errors path="receivingReport.commonErrMsg" cssClass="error"/>
						<form:errors path="formWorkflowId" cssClass="error"/>
					</td>
				</tr>
			</table>
			<br>
			<table style="margin-top: 10px;" class="frmField_set">
				<tr>
					<td>
						<input type="button" id="btnSaveReceivingReport" value="Save"
							onclick="saveReceivingReport();" style="float: right;"/>
					</td>
				</tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>