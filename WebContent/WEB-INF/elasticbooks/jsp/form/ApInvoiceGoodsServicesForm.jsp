<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : AP invoice goods/services form jsp page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.serialitemunitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
var PREV_RATE = 0;
var terms = new Array();
$(document).ready(function() {
	var wtAcctSettingId = "";
	if ("${apInvoiceGs.id}" > 0) {
		loadHeaderFields("${apInvoiceGs.referenceObjectId}", "${apInvoiceGs.companyId}", "${apInvoiceGs.company.name}",
				"${apInvoiceGs.divisionId}", "${apInvoiceGs.division.name}", "${apInvoiceGs.rrNumber}",
				"${apInvoiceGs.bmsNumber}", "${apInvoiceGs.poNumber}", "${apInvoiceGs.supplierId}", "${apInvoiceGs.supplier.name}",
				"${apInvoiceGs.supplierAccountId}", "${apInvoiceGs.supplierAccount.name}", "${apInvoiceGs.warehouseId}",
				"${apInvoiceGs.currencyId}", "${apInvoiceGs.currencyRateId}", "${apInvoiceGs.currencyRateValue}");
		wtAcctSettingId = "${apInvoiceGs.wtAcctSettingId}";
	} else {
		$("#spanDivisionNameId").text("${apInvoiceGs.division.name}");
	}
	initSerializedItemTbl();
	initNonSerializedItemTbl();
	initOtherChargesTbl();
	initDocumentsTbl();
	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
	//Add terms data from dropdown to terms array.
	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
});

function showRrReferences() {
	$("#divRrReferenceId").html("");
	$("#divRrReferenceId").load(contextPath+"/apInvoiceGS/"+"${apInvoiceGs.divisionId}"+"/showRrReferences");
}

function loadRrReference(rrReferenceId) {
	$.ajax({
		url : contextPath + "/apInvoiceGS/loadRrReference?rrReferenceId="+rrReferenceId,
		success : function(rr) {
			$("#divRrReferenceId").html("");
			$("#aClose")[0].click();
			loadHeaderFields(rr.referenceObjectId, rr.companyId, rr.company.name, rr.divisionId, rr.division.name,
					rr.rrNumber, rr.bmsNumber, rr.poNumber, rr.supplierId, rr.supplier.name, rr.supplierAccountId,
					rr.supplierAccount.name, rr.warehouseId, rr.currencyId, rr.currencyRateId, rr.currencyRateValue);
			setupSerializedItemTblData(rr.serialItems);
			setupNonSerializedItemTblData(rr.apInvoiceGoods);
			setupServiceTblData(rr.apInvoiceLines);
			loadWtaxAcctSettings();
			$("#termId").val(rr.termId);
			computeDueDate(rr.termId);
		},
		complete : function(rr) {
			computeWtax();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function loadHeaderFields(referenceObjectId, companyId, companyName, divisionId, divisionName, rrNumber, bmsNumber,
		poNumber, supplierId, supplierName, supplierAcctId, supplierAcctName, warehouseId, currencyId, currencyRateId,
		currencyRateValue) {
	$("#hdnReferenceObjectId").val(referenceObjectId);
	$("#txtRrReferenceId").val(rrNumber);
	$("#companyId").val(companyId);
	$("#spanCompanyNameId").text(companyName);
	$("#divisionId").val(divisionId);
	$("#spanDivisionNameId").text(divisionName);
	$("#bmsNumber").val(bmsNumber);
	$("#poNumber").val(poNumber);
	$("#supplierId").val(supplierId);
	$("#spanSupplierNameId").text(supplierName);
	$("#supplierAcctId").val(supplierAcctId);
	$("#spanSupplierAcctNameId").text(supplierAcctName);
	$("#warehouseId").val(warehouseId);
	$("#currencyId").val(currencyId);
	$("#currencyId").attr("disabled", "disabled");
	$("#hdnCurrRateId").val(currencyRateId);
	$("#hdnCurrRateValue").val(currencyRateValue);
	PREV_RATE = currencyRateValue;
}

function initSerializedItemTbl() {
	var serialItemsJson = JSON.parse($("#serialItemsJson").val());
	setupSerializedItemTblData(serialItemsJson);
}

function setupSerializedItemTblData(serialItemsJson) {
	$("#serializedItemTable").html("");
	var cPath = "${pageContext.request.contextPath}";
	$serializedItemTable = $("#serializedItemTable").editableItem({
		data: serialItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "serialNumber", "varType" : "string"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "itemDiscountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"}
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
				"handler" : new SerialItemUnitCostTblHandler(new function() {
					this.handleTotal = function(total) {
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
				"cls" : "quantityTxt tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Quantity",
				"cls" : "quantity",
				"editor" : "hidden",
				"visible": false},
			{"title" : "Serial Number",
				"cls" : "serialNumber tblLabelText",
				"editor" : "label",
				"visible": true,
				"width" : "10%"},
			{"title" : "UOM",
				"cls" : "uom tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Previous<br>Unit Cost",
				"cls" : "prevUC tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%" },
			{"title" : "Unit Cost", 
				"cls" : "unitCost tblInputNumeric", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Gross Amount", 
				"cls" : "grossPrice tblLabelNumeric", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "6%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "8%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
			{"title" : "Amount", 
				"cls" : "amount tblLabelNumeric", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
		],
		"disableDuplicateStockCode" : true,
	});

	$("#serializedItemTable").on("change", ".discountType", function() {
		var discountTypeId = $(this).val();
		$(this).closest("tr").find(".itemDiscountTypeId").val(discountTypeId);
		if (discountTypeId == "") {
			$(this).closest("tr").find(".discount").html("");
			$(this).closest("tr").find(".discountValue").val("0.00");
		}
		recomputeRowAmount(this, false);
	});

	$("#serializedItemTable").on("blur", ".discountValue", function() {
		var value = $(this).val();
		if (value == "") {
			$(this).closest("tr").find(".discount").html("");
		}
		recomputeRowAmount(this, false);
	});

	$("#serializedItemTable").on("blur", ".tblInputText", function(){
		if ($.trim($(this).val()) != "") {
			$(this).closest("tr").find(".quantityTxt").text(1);
			$(this).closest("tr").find(".quantity").val(1);
		}
	});

	$("#serializedItemTable").on("blur", ".tblInputNumeric", function() {
		recomputeRowAmount(this, false);
	});

	$("#serializedItemTable").on("change", ".taxType", function() {
		recomputeRowAmount(this, false);
	});

	$("#serializedItemTable").on("focus", ".stockCode", function(){
		disableInputFields(this, "spanSerialItemMessage");
	});

	$("#serializedItemTable tbody tr").each(function() {
		setTblGrossPrice(this, false);
		$(this).closest("tr").find(".taxType").attr("disabled","disabled");
	});

	formatNumbers("serializedItemTable");
}

function disableInputFields(elem, spanIdName) {
	var $tr = $(elem).closest("tr");
	var $stockCode = $tr.find(".stockCode");
	$stockCode.attr("disabled", "disabled");
	if ($.trim($stockCode.val()) == "") {
		$("#"+spanIdName).text("Adding of new item/s is not allowed.");
	}
}

function initNonSerializedItemTbl() {
	var apInvoiceGoodsJson = JSON.parse($("#apInvoiceGoodsJson").val());
	setupNonSerializedItemTblData(apInvoiceGoodsJson);
}

function setupNonSerializedItemTblData(apInvoiceGoodsJson) {
	$("#nonSerializedItemTable").html("");
	var cPath = "${pageContext.request.contextPath}";
	$nonSerializedItemTable = $("#nonSerializedItemTable").editableItem({
		data: apInvoiceGoodsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "itemDiscountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"}
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
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
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
				"handler" : new SerialItemUnitCostTblHandler(new function() {
					this.handleTotal = function(total) {
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
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Unit Cost", 
				"cls" : "unitCost tblInputNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Gross Price", 
				"cls" : "grossPrice tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "6%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "8%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
			{"title" : "Amount", 
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
		],
		"disableDuplicateStockCode" : false
	});

	$("#nonSerializedItemTable").on("change", ".discountType", function() {
		var discountTypeId = $(this).val();
		$(this).closest("tr").find(".itemDiscountTypeId").val(discountTypeId);
		if (discountTypeId == "") {
			$(this).closest("tr").find(".discount").html("");
			$(this).closest("tr").find(".discountValue").val("0.00");
		}
		recomputeRowAmount(this, false);
	});

	$("#nonSerializedItemTable").on("blur", ".discountValue", function() {
		var value = $(this).val();
		if (value == "") {
			$(this).closest("tr").find(".discount").html("");
		}
		recomputeRowAmount(this, false);
	});

	$("#nonSerializedItemTable").on("focus", ".tblInputNumeric", function() {
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#nonSerializedItemTable").on("blur", ".tblInputNumeric", function() {
		var $unitCost = $(this).closest("tr").find(".unitCost");
		$unitCost.val(formatDecimalPlaces($unitCost.val()));
		recomputeRowAmount(this, false);
	});

	$("#nonSerializedItemTable").on("change", ".taxType", function() {
		recomputeRowAmount(this, false);
	});

	$("#nonSerializedItemTable").on("focus", ".stockCode", function(){
		disableInputFields(this, "spanNonSerialItemMessage");
	});

	$("#nonSerializedItemTable tbody tr").each(function() {
		setTblGrossPrice(this, false);
		$(this).closest("tr").find(".taxType").attr("disabled","disabled");
	});

	resizeTbl("#nonSerializedItemTable", 14);
}

function recomputeRowAmount(elem, isOtherCharges) {
	var discountTypeId = $(elem).closest("tr").find(".discountType").val();
	var discount = computeDiscount(elem, discountTypeId, isOtherCharges);
	$(elem).closest("tr").find(".discount").html(formatDecimalPlaces(discount));
	var taxTypeId = $(elem).closest("tr").find(".taxType").val();
	setTblGrossPrice(elem, isOtherCharges);
	computeVatAmount(elem, taxTypeId, isOtherCharges, discount);
	computeWtax();
}

function setTblGrossPrice (elem, isOtherCharges) {
	var unitCost = null;
	if(isOtherCharges) {
		unitCost = $(elem).closest("tr").find(".upAmount").val();
	} else {
		unitCost = $(elem).closest("tr").find(".unitCost").val();
	}
	$(elem).closest("tr").find(".grossPrice").text(unitCost);
}

function initOtherChargesTbl() {
	var otherChargesJson = JSON.parse($("#apInvoiceLinesJson").val());
	setupServiceTblData(otherChargesJson);
}

function setupServiceTblData(otherChargesJson) {
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
			{"name" : "refenceObjectId", "varType" : "int"}
		],
		contextPath: cPath,
		header:[
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
			{"title" : "apLineSetupId",
				"cls" : "apLineSetupId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "AP Line",
				"cls" : "apLineSetupName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "25%",
				"handler" : new SerialItemUnitCostTblHandler(new function() {
					this.handleTotal = function(total) {
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
			{"title" : "origQty",
				"cls" : "origQty",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "unitOfMeasurementId",
				"cls" : "unitOfMeasurementId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "UOM",
				"cls" : "unitMeasurementName tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "10%",
				"handler" : new SerialItemUnitCostTblHandler(new function() {
					this.handleTotal = function(total) {
						// Do nothing
					};
				})},
			{"title" : "UP Amount",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Gross Price",
				"cls" : "grossPrice tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "discountTypeId",
				"cls" : "discountTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "6%"},
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
				"width" : "10%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "15%"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable").on("change", ".discountType", function() {
		var discountTypeId = $(this).val();
		$(this).closest("tr").find(".discountTypeId").val(discountTypeId);
		if (discountTypeId == "") {
			$(this).closest("tr").find(".discount").html("");
			$(this).closest("tr").find(".discountValue").val("0.00");
		}
		recomputeRowAmount(this, true);
	});

	$("#otherChargesTable").on("blur", ".discountValue", function() {
		var value = $(this).val();
		if (value == "") {
			$(this).closest("tr").find(".discount").html("");
		}
		recomputeRowAmount(this, true);
	});

	$("#otherChargesTable").on("blur", ".percentile, .quantity", function() {
		if (otherChargesJson != null && otherChargesJson != "") {
			computePercentile(this);
		}
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function() {
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function() {
		var $upAmount = $(this).closest("tr").find(".upAmount");
		$upAmount.val(formatDecimalPlaces($upAmount.val()));
		recomputeRowAmount(this, true);
	});

	$("#otherChargesTable").on("change", ".taxType", function() {
		recomputeRowAmount(this, true);
	});

	$("#otherChargesTable tbody tr").each(function() {
		if (otherChargesJson != null && otherChargesJson != "") {
			var qty = accounting.unformat($(this).closest("tr").find(".quantity").val());
			var percentile = accounting.unformat($(this).closest("tr").find(".percentile").val()) / 100;
			if (!isNaN(Number(qty))) {
				var origQty = qty / percentile;
				$(this).closest("tr").find(".origQty").val(origQty);
			}
			if (qty == 0) {
				$(this).closest("tr").find(".quantity").attr("disabled","disabled");
				$(this).closest("tr").find(".percentile").attr("disabled","disabled");
			}
		}
		setTblGrossPrice(this, true);
		$(this).closest("tr").find(".taxType").attr("disabled","disabled");
	});

	$("#otherChargesTable").on("focus", ".apLineSetupName", function(){
		$(this).attr("disabled", "disabled");
		if ($.trim($(this).val()) == "") {
			$("#spanOtherChargesMessage").text("Adding of new service/s is not allowed.");
		}
	});

	resizeTbl("#otherChargesTable", 12);
	formatNumbers("otherChargesTable");
}

function computePercentile(elem) {
	var origQty = $(elem).closest("tr").find(".origQty").val();
	var isQtyElem = $(elem).attr("class").includes("quantity"); // True if the edited element is quantity.
	if (!isNaN(Number(origQty))) {
		if (isQtyElem) {
			var qty = $(elem).val();
			var percentile = (qty / origQty) * 100;
			$(elem).closest("tr").find(".percentile").val(percentile);
		} else {
			var percentile = $(elem).val();
			var computedQty = origQty * (percentile / 100);
			$(elem).closest("tr").find(".quantity").val(computedQty);
		}
	}
	var $percentile = $(elem).closest("tr").find(".percentile");
	$($percentile).val(accounting.formatMoney($($percentile).val()));

	var $quantity = $(elem).closest("tr").find(".quantity");
	$($quantity).val($($quantity).val());
}

function resizeTbl(tableId, rowCount){
	var $tbl = $(tableId);
	$($tbl).attr("width", "100%");
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
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
		$(this).closest("tr").find(".quantityTxt").css("text-align", "right");

		var $unitCost = $(this).closest("tr").find(".unitCost");
		var unitCost = $.trim($($unitCost).val());
		if (unitCost != "") {
			$($unitCost).val(accounting.formatMoney(unitCost));
		}
	});
}

function computeGrandTotal() {
	var totalVat = 0;
	var totalAmount = 0;
	$("#serializedItemTable tbody tr").each(function(row) {
		totalVat += accounting.unformat($(this).find(".vatAmount").html());
		totalAmount += accounting.unformat($(this).find(".amount").html());
	});
	$("#nonSerializedItemTable tbody tr").each(function(row) {
		totalVat += accounting.unformat($(this).find(".vatAmount").html());
		totalAmount += accounting.unformat($(this).find(".amount").html());
	});
	$("#otherChargesTable tbody tr").each(function(row) {
		totalVat += accounting.unformat($(this).find(".vatAmount").html());
		totalAmount += accounting.unformat($(this).find(".amount").html());
	});
	var formattedSubTotal = formatDecimalPlaces(parseFloat(totalAmount).toFixed(6));
	var formattedTotalVAT = formatDecimalPlaces(parseFloat(totalVat).toFixed(6));
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	var formattedWtaxAmount = formatDecimalPlaces(parseFloat(wtaxAmount).toFixed(6));
	var grandTotal = (accounting.unformat(formattedSubTotal) + accounting.unformat(formattedTotalVAT)) - accounting.unformat(formattedWtaxAmount);
	$("#subTotal").html(formattedSubTotal);
	$("#totalVat").html(formattedTotalVAT);
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
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
	var referenceDocumentJson = JSON.parse($("#referenceDocumentsJson").val());
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

function loadWtaxAcctSettings(wtAcctSettingId) {
	var companyId = $("#companyId").val();
	if (companyId != "") {
		var $select = $("#wtaxAcctSettingId");
		$select.empty();
		var divisionId = $("#divisionId").val();
		var uri = contextPath+"/getWithholdingTaxAcct?companyId="+companyId+"&isCreditable=false";
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
}

function computeWtax() {
	computeGrandTotal();
	// computing withholding tax
	var wtAcctSettingId = $("#wtaxAcctSettingId").val();
	if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != "undefined") {
		var wtPercentageValue = 0;
		var uri = contextPath + "/getWithholdingTaxAcct/getWtPercentage?wtAccountSettingId="+wtAcctSettingId;
		$.ajax({
			url: uri,
			success : function(wt) {
				wtPercentageValue = (accounting.unformat(wt) / 100);
				var totalAmount = accounting.unformat($("#subTotal").html());
				var computedWTax = formatDecimalPlaces((totalAmount * wtPercentageValue).toFixed(6));
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

var isSaving = false;
function saveInvoiceForm() {
	if (!isSaving && $("#spanReferenceDoc").html() == "" && !checkExceededFileSize()) {
		$("#spanDocumentSize").text("");
		isSaving = true;
		$("#serialItemsJson").val($serializedItemTable.getData());
		$("#apInvoiceGoodsJson").val($nonSerializedItemTable.getData());
		$("#apInvoiceLinesJson").val($otherChargesTable.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		var amount = $("#amount").val();
		$("#amount").val(accounting.unformat(amount));
		$("#currencyId").removeAttr("disabled");
		$("#btnSaveForm").attr("disabled", "disabled");
		doPostWithCallBack ("apInvoiceGsFormId", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable(formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var spanFormStatusLbl = $("#spanFormStatusLbl").text();
				var referenceObjectId = $("#hdnReferenceObjectId").val();
				var rrNumber = $("#txtRrReferenceId").val();
				var companyId = $("#companyId").val();
				var companyName = $("#spanCompanyNameId").text();
				var divisionId = $("#divisionId").val();
				var divisionName = $("#spanDivisionNameId").text();
				var bmsNumber = $("#bmsNumber").val();
				var poNumber = $("#poNumber").val();
				var supplierId = $("#supplierId").val();
				var supplierName = $("#spanSupplierNameId").text();
				var supplierAcctId = $("#supplierAcctId").val();
				var supplierAcctName = $("#spanSupplierAcctNameId").text();
				var warehouseId = $("#warehouseId").val();
				var currencyId = $("#currencyId").val();
				var currencyRateId = $("#hdnCurrRateId").val();
				var currencyRateValue = $("#hdnCurrRateValue").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				if ("${apInvoiceGs.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#spanFormStatusLbl").text(spanFormStatusLbl);
				loadHeaderFields(referenceObjectId, companyId, companyName, divisionId, divisionName, rrNumber, bmsNumber,
						poNumber, supplierId, supplierName, supplierAcctId, supplierAcctName, warehouseId, currencyId, currencyRateId,
						currencyRateValue);
				$("#amount").val(formatDecimalPlaces(amount));
				initSerializedItemTbl();
				initNonSerializedItemTbl();
				initOtherChargesTbl();
				initDocumentsTbl();
				loadWtaxAcctSettings(wtAcctSettingId);
				computeWtax();
			}
			$("#btnSaveForm").removeAttr("disabled");
			isSaving = false;
		});
	} else if (checkExceededFileSize()) {
		$("#spanDocumentSize").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function computeDueDate() {
	var glDateVal = $("#glDate").val();
	if(glDateVal == null || glDateVal == "") {
		$("#dueDate").val("");
		return;
	}
	var additionalDays = 0;
	var termId = $("#termId option:selected").val();
	for(var i = 0; i < terms.length; i++) {
		var term = terms[i];
		if (term.id == termId) {
			additionalDays = term.days;
			break;
		}
	}
	var glDate = new Date(glDateVal);
	glDate.setDate(glDate.getDate() + parseInt(additionalDays));
	if(!isNaN( glDate.getMonth()) && !isNaN( glDate.getDate()) && !isNaN(glDate.getFullYear())){
		$("#dueDate").val((glDate.getMonth() + 1) + "/" + glDate.getDate()
			+ "/" + glDate.getFullYear());
	} else {
		$("#dueDate").val ("");
	}
}

function Term(id, days) {
	this.id = id;
	this.days = days;
}
</script>
<style type="text/css">
input.numeric {
	width: 172px;
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
.grossPrice, .upAmount {
	text-align: right;
	float: right;
}
</style>
</head>
<body>
	<div id="container" class="popupModal">
		<div id="divRrReferenceContainer" class="reveal-modal">
			<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
			<div id="divRrReferenceId"></div>
		</div>
	</div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="apInvoiceGs" id="apInvoiceGsFormId">
			<div class="modFormLabel">AP Invoice Goods/Services<span id="spanDivisionLbl"> - ${apInvoiceGs.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id"/>
			<form:hidden path="invoiceTypeId"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="sequenceNumber"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="serialItemsJson" id="serialItemsJson"/>
			<form:hidden path="apInvoiceGoodsJson" id="apInvoiceGoodsJson"/>
			<form:hidden path="apInvoiceLinesJson" id="apInvoiceLinesJson"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<form:hidden path="referenceObjectId" id="hdnReferenceObjectId"/>
			<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<form:hidden path="warehouseId" id="warehouseId"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">API-G/S No.</td>
							<td class="value">
								<span id="spanSequenceNo">
									<c:if test="${apInvoiceGs.id ne 0}">
										${apInvoiceGs.sequenceNumber}
									</c:if>
								</span>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<td class="value">
								<span id="spanFormStatusLbl">
									<c:choose>
										<c:when test="${apInvoiceGs.formWorkflow ne null}">
											${apInvoiceGs.formWorkflow.currentFormStatus.description}
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
					<legend>AP Invoice Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:hidden path="companyId" id="companyId"/>
								<form:label path="company.name" id="spanCompanyNameId"
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
								<form:label path="division.name" id="spanDivisionNameId"
									class="span-label-value"></form:label>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* RR Reference</td>
							<td class="value">
								<form:input path="rrNumber" id="txtRrReferenceId" class="inputSmall" readonly="true"/>
								<c:if test="${apInvoiceGs.id eq 0}">
									<a href="#container" id="waOpen" data-reveal-id="divRrReferenceId"
										class="link_button" onclick="showRrReferences();">Browse RR</a>
								</c:if>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="rrNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* SI/SOA No.</td>
							<td class="value">
								<form:input path="invoiceNumber" id="invoiceNumber" cssClass="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="invoiceNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">BMS No.</td>
							<td class="value">
								<form:input path="bmsNumber" id="bmsNumber" cssClass="input remove-border"
									readonly="true"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="bmsNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">PO No.</td>
							<td class="value">
								<form:input path="poNumber" id="poNumber" cssClass="input remove-border" 
									readonly="true"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="poNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Supplier</td>
							<td class="value">
								<form:hidden path="supplierId" id="supplierId"/>
								<form:label path="supplier.name" id="spanSupplierNameId"
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
								<form:hidden path="supplierAccountId" id="supplierAcctId"/>
								<form:label path="supplierAccount.name" id="spanSupplierAcctNameId"
									class="span-label-value"></form:label>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="supplierAccountId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Term</td>
							<td class="value">
								<form:select path="termId" cssClass="frmSelectClass" id="termId" onchange="computeDueDate();">
									<form:options items="${terms}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="termId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* SI/SOA Date</td>
							<td class="value">
								<form:input path="invoiceDate" id="invoiceDate" onblur="evalDate('invoiceDate')"
									style="width: 120px;" class="dateClass2" />
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
								<form:input path="glDate" id="glDate" onblur="evalDate('glDate'); computeDueDate();"
									style="width: 120px;" class="dateClass2" />
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
								<form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate3" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('dueDate')"
									style="cursor: pointer" style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="dueDate" cssClass="error"/></td>
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
							<td class="labels">* Currency</td>
							<td class="value">
								<form:select path="currencyId" id="currencyId" cssClass="frmMediumSelectClass">
									<form:options items="${currencies}" itemValue="id" itemLabel="name"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="currencyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Amount</td>
							<td class="value">
								<form:input path="amount" id="amount" cssClass="inputSmall inputAmount"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="amount" cssClass="error"/></td>
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
							<td><form:errors path="serialItems" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Non-Serialized Good/s</legend>
					<div id="nonSerializedItemTable"></div>
					<table>
						<tr>
							<td><span id="spanNonSerialItemMessage" class="error"></span></td>
						</tr>
						<tr>
							<td><form:errors path="apInvoiceGoods" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Services</legend>
					<div id="otherChargesTable"></div>
					<table>
						<tr>
							<td><span id="spanOtherChargesMessage" class="error"></span></td>
						</tr>
						<tr>
							<td><form:errors path="apInvoiceLines" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Document</legend>
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
				<table class="frmField_set">
					<tr>
						<td><form:errors path="apInvoiceErrMessage" cssClass="error"/></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set footerTotalAmountTbl">
					<tr>
						<td style="width: 22%;"></td>
						<td style="width: 22%;"></td>
						<td style="width: 22%;">Sub Total</td>
						<td style="width: 22%;"></td>
						<td style="width: 12%;"><span id="subTotal"></span></td>
					</tr>
					<tr>
						<td colspan="3">Total VAT</td>
						<td></td>
						<td>
							<span id="totalVat"></span>
						</td>
					</tr>
					<tr>
						<td colspan="3">Withholding Tax</td>
						<td>
							<form:select path="wtAcctSettingId" id="wtaxAcctSettingId" cssClass="frmSmallSelectClass"
								cssStyle="width: 95%;" onchange="computeWtax();">
							</form:select>
						</td>
						<td>
							<span id="computedWTax">0.00</span>
						</td>
					</tr>
					<tr>
						<td colspan="3">Total Amount Due</td>
						<td></td>
						<td>
							<span id="grandTotal">0.00</span>
						</td>
					</tr>
					<tr>
						<td colspan="5">
							<form:errors path="wtAmount" cssClass="error" />
						</td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right">
							<input type="button" id="btnSaveForm" value="Save" onclick="saveInvoiceForm();"/>
						</td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>