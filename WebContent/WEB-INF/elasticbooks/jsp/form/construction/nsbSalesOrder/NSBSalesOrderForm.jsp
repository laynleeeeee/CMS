<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Sales Order form jsp page
 -->
<!DOCTYPE html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.salesorderhandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
var soObj = null;
var soReferenceWindow = null;
var PREV_RATE = 0;
var selectCustomerAcct = "${salesOrder.arCustomerAcctId}";
var arCustAccts = new Array();
$(document).ready(function() {
	loadDivision("${salesOrder.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	initSOItems(JSON.parse($("#hdnSoItemJson").val()));
	initSOLines(JSON.parse($("#hdnSoLineJson").val()));
	initSOTLines(JSON.parse($("#hdnSotLineJson").val()));
	initSOELines(JSON.parse($("#hdnSoeLineJson").val()));
	loadWtaxAcctSettings("${salesOrder.wtAcctSettingId}");
	getCustomerTypes("${salesOrder.customerTypeId}", "${salesOrder.arCustomerId}");
	computeGrandTotal();
	computeWtax();
	enableDisableAdvPayment();
	if (Number("${salesOrder.id}") > 0) {
		$("#aOpen").hide();
	}
	if (Number("${salesOrder.salesQuotationId}") > 0) {
		$("#txtSQReferenceNo").val("${salesOrder.refSQNumber}");
	}
	filterCustomerAccts("${salesOrder.arCustomerAcctId}");
	disableFormFields();
	initializeDocumentsTbl();
	if("${salesOrder.id}" != 0){
		$("#currencyId").attr("disabled","disabled");
	}
});

function disableFormFields() {
	var isComplete = "${salesOrder.formWorkflow.complete}";
	if (isComplete == "true" || "${salesOrder.formWorkflow.currentStatusId}" == 4) {
		$("#salesOrderFormId :input").attr("disabled", "disabled");
		$("#imgDate2").hide();
	}
}

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

function initSOItems(soItemJson) {
	$("#soItemTbl").html("");
	var hasSQRef = $("#hdnSQId").val() != "";
	var path = "${pageContext.request.contextPath}";
	$soItemTbl = $("#soItemTbl").editableItem({
		data: soItemJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "salesOrderId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "grossAmount", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "itemDiscountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "discount", "varType" : "discount"},
			{"name" : "memo", "varType" : "string"}
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "salesOrderId",
				"cls" : "salesOrderId",
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
			{"title" : "itemId",
				"cls" : "itemId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Stock Code",
				"cls" : "stockCode tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						computeGrandTotal();
					};
			})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Qty", 
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "6%"},
			{"title" : "UOM", 
				"cls" : "uom txtUOM",
				"editor" : "label",
				"visible" : true,
				"width" : "6%"},
			{"title" : "Gross Price",
				"cls" : "grossAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "7%"},
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
				"visible" : false},
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "8%"},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "6%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Memo",
				"cls" : "memo tblInputText",
				"editor" : "text",
				"visible" : false,
				"width" : "10%"}
		],
		footer: [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false,
		"addEmptyRow": !hasSQRef
	});

	$("#soItemTbl").on("change", ".discountType", function() {
		var discountTypeId = $(this).val();
		$(this).closest("tr").find(".itemDiscountTypeId").val(discountTypeId);
		if (discountTypeId == "") {
			$(this).closest("tr").find(".discount").html("");
			$(this).closest("tr").find(".discountValue").val("0.00");
		}
		recomputeRowAmount(this, false);
	});

	$("#soItemTbl").on("blur", ".discountValue", function() {
		var value = $(this).val();
		if (value == "") {
			$(this).closest("tr").find(".discount").html("");
		}
		recomputeRowAmount(this, false);
	});

	$("#soItemTbl").on("focus", ".tblInputNumeric", function() {
		var value = $(this).val();
		if ($.trim(value) != "") {
			$(this).val(accounting.unformat(value));
		}
	});

	$("#soItemTbl").on("blur", ".tblInputNumeric", function() {
		var $grossAmount = $(this).closest("tr").find(".grossAmount");
		var grossAmount = $grossAmount.val();
		$grossAmount.val(formatDecimalPlaces(grossAmount, 6));
		recomputeRowAmount(this, false);
	});

	$("#soItemTbl").on("change", ".taxType", function() {
		recomputeRowAmount(this, false);
	});
	$("#soItemTbl").on("blur", ".stockCode", function() {
		recomputeRowAmount(this, false);
	});
	resizeTbl("#soItemTbl", 12);
}

function resizeTbl(tableId, rowCount){
	var $tbl = $(tableId);
	$($tbl).attr("width", "100%");
	addTotalLabel($tbl)
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function addTotalLabel($tbl){
	var $tr = $($tbl).find("tfoot tr td:nth-child(1)");
	$($tr).html("Total");
}

function recomputeRowAmount(elem, isOtherCharges) {
	var discountTypeId = $(elem).closest("tr").find(".discountType").val();
	var discount = computeDiscount(elem, discountTypeId, isOtherCharges);
	$(elem).closest("tr").find(".discount").html(formatDecimalPlaces(discount));

	var taxTypeId = $(elem).closest("tr").find(".taxType").val();
	computeVatAmount(elem, taxTypeId, isOtherCharges, discount);
	computeGrandTotal();
	computeWtax();
}

function getCurrencyRate(elem) {
	var currencyId = $(elem).val();
	if (currencyId != "" && currencyId != null && currencyId != 0) {
		$.ajax({
			url: contextPath+"/getCurrency/getLatestRate?currencyId="+currencyId,
			success : function(currencyRate) {
				if(currencyRate.active == true){
					$("#hdnCurrRateId").val(currencyRate.id);
					var rate = currencyRate.rate;
					$("#hdnCurrRateValue").val(rate);
					convertCurrencies(currencyId, rate);
				}
			},
			error : function(error) {
				// No currency rate found, resetting to default rate to PHP
				convertCurrencies(1, 1.0);
			},
			dataType: "json"
		});
	} else {
		$("#hdnCurrRateId").val("");
		$("#hdnCurrRateValue").val("");
	}
}

function convertCurrencies(currencyId, rate) {
	currencyId = currencyId != "" ? currencyId : $("#currencyId").val();
	if (currencyId == 1) {
		// Set default rate to PHP
		rate = 1.0;
		$("#hdnCurrRateId").val("");
		$("#hdnCurrRateValue").val(rate);
	}

	if(cbIsDeposit.checked == true){
		var advancePaymentCost = accounting.unformat($("#txtAdvancePayment").val());
		advancePaymentCost = (advancePaymentCost * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$("#txtAdvancePayment").val(formatDecimalPlaces((advancePaymentCost / rate)));
	}

	$("#soItemTbl tbody tr").each(function(row) {
		var unitCost = accounting.unformat($(this).find(".grossAmount").val());
		unitCost = (unitCost * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".grossAmount").val(formatDecimalPlaces((unitCost / rate)));

		var discountValue = accounting.unformat($(this).find(".discountValue").val());
		discountValue = (discountValue * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".discountValue").val(formatDecimalPlaces((discountValue / rate)));

		var vat = accounting.unformat($(this).find(".vatAmount").html());
		vat = (vat * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".vatAmount").html(formatDecimalPlaces((vat / rate)));

		var amount = accounting.unformat($(this).find(".amount").html());
		amount = (amount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".amount").html(formatDecimalPlaces((amount / rate)));

		recomputeRowAmount(this,false);
		computeGrandTotal();
		computeWtax();
	});

	$("#otherChargesTable tbody tr").each(function(row) {
		var upAmount = accounting.unformat($(this).find(".upAmount").val());
		upAmount = (upAmount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".upAmount").val(formatDecimalPlaces((upAmount / rate)));

		var discountValue = accounting.unformat($(this).find(".discountValue").val());
		discountValue = (discountValue * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".discountValue").val(formatDecimalPlaces((discountValue / rate)));

		var vat = accounting.unformat($(this).find(".vatAmount").html());
		vat = (vat * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".vatAmount").html(formatDecimalPlaces((vat / rate)));

		var totalAmount = accounting.unformat($(this).find(".amount").val());
		totalAmount = (totalAmount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".amount").val(formatDecimalPlaces((totalAmount / rate)));

		recomputeRowAmount(this,true);
		computeGrandTotal();
		computeWtax();
	});
	PREV_RATE = rate;
}

function loadWtaxAcctSettings(wtAcctSettingId) {
	var $select = $("#wtaxAcctSettingId");
	$select.empty();
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath+"/getWithholdingTaxAcct?companyId="+companyId+"&isCreditable=true";
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

function computeWtax() {
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
				$("#computedWTax").html(computedWTax);
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
	var totalItemAmount = 0;
	var totalVAT = 0;
	var totalWtVAT = 0;
	$("#soItemTbl").find(" tbody tr ").each(function(row) {
		totalItemAmount += accounting.unformat($(this).find(".amount").html());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
		if ($(this).find(".taxTypeId").val() == 9) {
			totalWtVAT += ((accounting.unformat($(this).find(".amount").html())) * 0.05);
		}
	});
	$("#soItemTbl").find("tfoot tr .amount").html(formatDecimalPlaces(totalItemAmount));

	var totalOtherChargesAmt = 0;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		totalOtherChargesAmt += accounting.unformat($(this).find(".amount").val());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
		if ($(this).find(".taxTypeId").val() == 9) {
			totalWtVAT += ((accounting.unformat($(this).find(".amount").val())) * 0.05);
		}
	});
	$("#otherChargesTable").find("tfoot tr .amount").html(formatDecimalPlaces(totalOtherChargesAmt));

	var subTotal = totalItemAmount + totalOtherChargesAmt;
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	$("#subTotal").html(formatDecimalPlaces(subTotal));
	$("#totalVAT").html(formatDecimalPlaces(totalVAT));
	$("#hdnWtVatAmountId").val(totalWtVAT);
	$("#withholdingVAT").html(formatDecimalPlaces(totalWtVAT));
	$("#computedWTax").html(formatDecimalPlaces(wtaxAmount));
	var grandTotal = (subTotal + totalVAT) - totalWtVAT - wtaxAmount;
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
	$("#hdnAmountId").val(grandTotal);
}

function initSOLines (soLineJson){
	$otherChargesTable =initChargesines ("otherChargesTable", soLineJson)
}

function initSOELines (soLineJson){
	$equipmentChargesTable =initChargesines ("equipmentChargesTable", soLineJson)
}

function initSOTLines (soLineJson){
	$truckingChargesTable =initChargesines ("truckingChargesTable", soLineJson)
}

function initChargesines(tblId, soLineJson) {
	$("#"+tblId).html("");
	var path = "${pageContext.request.contextPath}";
	var $chargesTable = $("#"+tblId).editableItem({
		data: soLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "salesOrderId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "serviceSettingId", "varType" : "int"},
			{"name" : "serviceSettingName", "varType" : "string"},
			{"name" : "description", "varType" : "string"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "unitOfMeasurementId", "varType" : "int"},
			{"name" : "discountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "upAmount", "varType" : "double"}
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "salesOrderId",
				"cls" : "salesOrderId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "serviceSettingId",
				"cls" : "serviceSettingId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Service",
				"cls" : "serviceSettingName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
			})},
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "unitOfMeasurementId",
				"cls" : "unitOfMeasurementId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "UOM",
				"cls" : "unitMeasurementName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Gross Price",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%"},
			{"title" : "discountTypeId",
				"cls" : "discountTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "8%"},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%"},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
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
				"cls" : "amount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%"}
		],
		footer: [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#"+tblId).on("change", ".discountType", function() {
		var discountTypeId = $(this).val();
		$(this).closest("tr").find(".discountTypeId").val(discountTypeId);
		if (discountTypeId == "") {
			$(this).closest("tr").find(".discount").html("");
			$(this).closest("tr").find(".discountValue").val("0.00");
		}
		recomputeRowAmount(this, true);
	});

	$("#"+tblId).on("focusout", ".discountType", function() {
		var discountTypeId = $(this).val();
		$(this).closest("tr").find(".itemDiscountTypeId").val(discountTypeId);
		if (discountTypeId == "") {
			$(this).closest("tr").find(".discount").html("");
			$(this).closest("tr").find(".discountValue").val("0.00");
		}
		recomputeRowAmount(this, true);
	});

	$("#"+tblId).on("blur", ".discountValue", function() {
		var value = $(this).val();
		if (value == "") {
			$(this).closest("tr").find(".discount").html("");
		}
		recomputeRowAmount(this, true);
	});

	$("#"+tblId).on("focus", ".tblInputNumeric", function() {
		var value = $(this).val();
		if ($.trim(value) != "") {
			$(this).val(accounting.unformat(value));
		}
		
	});

	$("#"+tblId).on("blur", ".tblInputNumeric", function() {
		var $upAmount = $(this).closest("tr").find(".upAmount");
		var upAmount = $upAmount.val();
		$upAmount.val(formatDecimalPlaces(upAmount, 6));
		recomputeRowAmount(this, true);
	});

	$("#"+tblId).on("change", ".taxType", function() {
		recomputeRowAmount(this, true);
	});

	$("#"+tblId+" tbody tr").each(function() {
		recomputeRowAmount(this, true);
	});

	resizeTbl("#"+tblId, 12);
	return $chargesTable;
}

function resizeTbl(tableId, rowCount){
	var $tbl = $(tableId);
	$($tbl).attr("width", "100%");
	addTotalLabel($tbl);
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function addTotalLabel($tbl){
	var $tr = $($tbl).find("tfoot tr td:nth-child(1)");
	$($tr).html("Total");
}

function showCustomers() {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $("#txtCustomer").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)
			+"&companyId="+companyId;
	var divisionId = $("#divisionId").val();
	if (divisionId != "" && divisionId != "undefined") {
		uri += "&divisionId="+divisionId;
	}
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnArCustomerId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function getCustomer() {
	var companyId = $("#companyId").val();
	var customerName = $("#txtCustomer").val();
	var divisionId = $("#divisionId").val();
	if (customerName != "") {
		$("#customerName").val(customerName);
		$("#spanCustomerError").text("");
		var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&isExact=true"+"&companyId="+companyId+"&divisionId="+divisionId;
		$.ajax({
			url: uri,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					var customerId = customer[0].id;
					$("#hdnArCustomerId").val(customerId);
					$("#txtCustomer").val(customer[0].name);
					getCustomerTypes(customer[0].customerTypeId, customerId);
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#arCustomerIdError").text("");
					$("#hdnArCustomerId").val("");
					$("#customerTypeId").empty();
					$("#arCustomerAcctId").empty();
				}
				filterCustomerAccts();
			},
			dataType: "json"
		});
	} else {
		$("#hdnArCustomerId").val("");
		$("#arCustomerIdError").text("");
		$("#customerTypeId").empty();
		$("#arCustomerAcctId").empty();
	}
}

function filterCustomerAccts(arCustomerAccountId) {
	$("#arCustomerAcctId").empty();
	if ($.trim($("#txtCustomer").val()) == "") {
		$("#hdnArCustomerId").val("");
	} else {
		var customerId = $("#hdnArCustomerId").val();
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getArCustomerAccounts/includeSavedInactive?arCustomerId="+customerId+"&companyId="+companyId;
		var divisionId = $("#divisionId").val();
		if (divisionId != "" && divisionId != "undefined") {
			uri += "&divisionId="+divisionId;
		}
		if(typeof arCustomerAccountId != "undefined") {
			uri += "&arCustomerAccountId="+arCustomerAccountId;
		}
		var optionParser = {
				getValue: function (rowObject){
					if (rowObject != null)
						return rowObject["id"];
				},

				getLabel: function (rowObject){
					if (rowObject != null)
						return rowObject["name"];
				}
		};
		var postHandler = {
				doPost: function(data) {
					selectCustomerAcct = $("#arCustomerAcctId").val();
					arCustAccts = new Array();
					for(var index = 0; index < data.length; index++){
						//Set term based on the selected customer account
						if(data[index].id == selectCustomerAcct) {
							$("#termId").val(data[index].termId);
						}
						var arCustAcct = new ArCustAcct(data[index].id, data[index].termId);
						arCustAccts.push(arCustAcct);
					}
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAcctId", optionParser, postHandler);
	}
}

function ArCustAcct (arCustAcctId, termId, arLine) {
	this.arCustAcctId = arCustAcctId;
	this.termId = termId;
}

function assignTerm() {
	selectCustomerAcct = $("#arCustomerAcctId").val();
	for(var index=0; index < arCustAccts.length; index++) {
		if(arCustAccts[index].arCustAcctId == selectCustomerAcct) {
			$("#termId").val(arCustAccts[index].termId);
		}
	}
}

function getCustomerTypes(customerTypeId, arCustomerId) {
	$("#customerTypeId").empty();
	if (arCustomerId != "") {
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getCustomerType/byCustomerId?customerTypeId="+customerTypeId+"&arCustomerId="+arCustomerId;
		var optionParser = {
			getValue: function (rowObject){
				if (rowObject != null)
					return rowObject["id"];
			},
			getLabel: function (rowObject){
				if (rowObject != null)
					return rowObject["name"];
			}
		};
		postHandler = {
			doPost: function(data) {
				if (customerTypeId != null && typeof customerTypeId != "undefined") {
					$("#customerTypeId").val(customerTypeId);
				}
				// This is to remove any duplication.
				var found = [];
				$("#customerTypeId option").each(function() {
					if($.inArray(this.value, found) != -1) 
						$(this).remove();
					found.push(this.value);
				});
			}
		};
		loadPopulate (uri, false, customerTypeId, "customerTypeId", optionParser, postHandler);
	}
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

function unformatTableValues() {
	$("#soItemTbl tbody tr").each(function(row) {
		var discount = accounting.unformat($(this).find(".discount").html());
		$(this).find(".discount").html(discount);
	});

	$("#otherChargesTable tbody tr").each(function(row) {
		var discount = accounting.unformat($(this).find(".discount").html());
		$(this).find(".discount").html(discount);
	});
}

function formatTableValues() {
	$("#soItemTbl tbody tr").each(function(row) {
		var discount = accounting.unformat($(this).find(".discount").html());
		$(this).find(".discount").html(formatDecimalPlaces(discount));
	});

	$("#otherChargesTable tbody tr").each(function(row) {
		var discount = accounting.unformat($(this).find(".discount").html());
		$(this).find(".discount").html(formatDecimalPlaces(discount));
	});
}

var isSaving = false;
function saveSOForm() {
	if (!isSaving) {
		isSaving = true;
		var advPayment = $("#txtAdvancePayment").val();
		$("#txtAdvancePayment").val(accounting.unformat(advPayment));
		unformatTableValues();
		var companyId = $("#companyId").val();
		var soTypeId = $("#soTypeId").val();
		$("#hdnCompanyId").val(companyId);
		$("#hdnSOTypeId").val(soTypeId);
		$("#hdnSoItemJson").val($soItemTbl.getData());
		$("#hdnSoLineJson").val($otherChargesTable.getData());
		$("#hdnSotLineJson").val($truckingChargesTable.getData());
		$("#hdnSoeLineJson").val($equipmentChargesTable.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#btnSalesOrder").attr("disabled", "disabled");
		$("#divisionId").removeAttr("disabled");
		$("#currencyId").removeAttr("disabled");
		doPostWithCallBack ("salesOrderFormId", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				var currentStatus = $("#txtCurrentStatusId").val();
				var customerTypeId = $("#customerTypeId").val();
				var customerId = $("#hdnArCustomerId").val();
				var currencyId = $("#currencyId").val();
				var rate = $("#hdnCurrRateValue").val();
				var arCustomerAcctId = $("#arCustomerAcctId").val();
				if ("${salesOrder.id}" == 0){
					dojo.byId("form").innerHTML = data;
					if (soObj != null) {
						updateHeader(soObj);
					}
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtCurrentStatusId").val(currentStatus);
				}
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#companyId").val(companyId);
				$("#soTypeId").val(soTypeId);
				getCustomerTypes(customerTypeId, customerId);
				loadDivision("${salesOrder.divisionId}");
				filterCustomerAccts(arCustomerAcctId);
				$("#divisionId").attr("disabled","disabled");
				if("${salesOrder.id}" != 0){
					$("#currencyId").attr("disabled","disabled");
				}
				initSOItems(JSON.parse($("#hdnSoItemJson").val()));
				initSOLines(JSON.parse($("#hdnSoLineJson").val()));
				initSOTLines(JSON.parse($("#hdnSotLineJson").val()));
				initSOELines(JSON.parse($("#hdnSoeLineJson").val()));
				formatTableValues();
				loadWtaxAcctSettings(wtAcctSettingId);
				computeGrandTotal();
				computeWtax();
				$("#txtAdvancePayment").val(formatDecimalPlaces(advPayment));
				initializeDocumentsTbl();
			}
			$("#btnSalesOrder").removeAttr("disabled");
			isSaving = false;
		});
	}
}

function showSQReferences() {
	var companyId = $("#companyId").val();
	var url = contextPath+"/salesOrder/sqReference?companyId="+companyId;
	$("#divSQReferenceId").load(url);
}

function loadSQReference (sqId) {
	$.ajax({
		url: contextPath + "/salesOrder/loadSQReference?sqId="+sqId,
		success : function(salesOrder) {
			$("#divSQReferenceId").html("");
			$("#aClose")[0].click();
			soObj = salesOrder;
			updateHeader(salesOrder);
			initSOItems(salesOrder.soItems);
			initSOLines(salesOrder.soLines);
			initSOTLines(salesOrder.sotLines);
			initSOELines(salesOrder.soeLines);
			computeGrandTotal();
			loadWtaxAcctSettings(salesOrder.wtAcctSettingId);
			computeWtax();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	if (soReferenceWindow != null) {
		soReferenceWindow.close();
	}
	soReferenceWindow = null;
}

function updateHeader (so) {
	$("#hdnSQId").val(so.salesQuotationId);
	$("#txtSQReferenceNo").val(so.refSQNumber);
	$("#companyId").val(so.companyId);
	$("#spanCompany").val(so.company.name);
	var arCustomerId = so.arCustomerId;
	$("#hdnArCustomerId").val(arCustomerId);
	$("#txtCustomer").val(so.arCustomer.name);
	filterCustomerAccts();
	getCustomerTypes(so.customerTypeId, arCustomerId);
	$("#shipTo").text(so.shipTo);
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeGrandTotal();
	computeWtax();
}

function enableDisableAdvPayment () {
	if(cbIsDeposit.checked == true){
		$("#txtAdvancePayment").removeAttr("disabled");
	} else {
		$("#txtAdvancePayment").val("");
		$("#txtAdvancePayment").attr("disabled", "disabled");
	}
}

$(function () {
	$("#txtAdvancePayment").live("blur", function (e) {
		$("#txtAdvancePayment").val(formatDecimalPlaces($("#txtAdvancePayment").val()));
	});
});

function populateEncodedShipTo(arCustomerId) {
	if (arCustomerId != "") {
		// populate lastest ship to text for the selected customer
		$.ajax({
			url: contextPath + "/salesOrder/getCustomerShipTo?arCustomerId="+arCustomerId,
			success : function(shipTo) {
				if (shipTo != null && shipTo != undefined) {
					$("#shipTo").val(shipTo);
				}
			},
			error : function(error) {
				console.log(error);
				$("#shipTo").val("");
			},
			dataType: "text"
		});
	}
}
</script>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divSQReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divSQReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="salesOrder" id="salesOrderFormId">
			<div class="modFormLabel">Sales Order<span id="spanDivisionLbl"> - ${salesOrder.division.name}</span>
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="updatedBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNumber"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="customerName" id="customerName"/>
			<form:hidden path="soTypeId" id="hdnSOTypeId"/>
			<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
			<form:hidden path="amount" id="hdnAmountId"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<form:hidden path="soItemJson" id="hdnSoItemJson"/>
			<form:hidden path="soLineJson" id="hdnSoLineJson"/>
			<form:hidden path="soeLineJson" id="hdnSoeLineJson"/>
			<form:hidden path="sotLineJson" id="hdnSotLineJson"/>
			<form:hidden path="salesQuotationId" id="hdnSQId"/>
			<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="wtVatAmount" id="hdnWtVatAmountId"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">SO No.</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"
									value="${salesOrder.sequenceNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${salesOrder.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtCurrentStatusId" class="textBoxLabel"
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Sales Order Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<select class="frmSelectClass" id="companyId" onchange="companyOnChange(this);">
									<c:forEach items="${companies}" var="company">
										<option value="${company.id}">${company.name}</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error"/>
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
						<tr class="hidden">
							<td class="labels">Sales Quotation Reference</td>
							<td class="value">
								<input id="txtSQReferenceNo" readonly="readonly"/>
								<a href="#container" id="aOpen" data-reveal-id="divSQReferenceId" class="link_button"
									onclick="showSQReferences();">Browse SQ</a>
							</td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="date" id="formDate" onblur="evalDate('formDate');" 
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('formDate')" 
									style="cursor: pointer" style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="date" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Estimated Delivery Date</td>
							<td class="value">
								<form:input path="deliveryDate" id="formDeliveryDate" onblur="evalDate('formDeliveryDate');" 
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('formDeliveryDate')" 
									style="cursor: pointer" style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="deliveryDate" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* SO Type</td>
							<td class="value">
								<select class="frmSelectClass" id="soTypeId">
									<c:forEach items="${soTypes}" var="soType">
										<option value="${soType.id}">${soType.name}</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="soTypeId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">PO/PCR No.</td>
							<td class="value">
								<form:input path="poNumber" cssClass="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="poNumber" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" cssClass="input" id="txtCustomer"
									onkeyup="showCustomers();" onblur="getCustomer();"/>
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
								<form:errors path="arCustomerId" id="arCustomerIdError" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer Account</td>
							<td class="value">
								<form:select path="arCustomerAcctId" id="arCustomerAcctId" cssClass="frmSelectClass"
									onchange="assignTerm();" >
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="arCustomerAcctId" cssClass="error"/>
							</td>
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
						<tr class="hidden">
							<td class="labels">* Cluster</td>
							<td class="value">
								<form:select path="customerTypeId" id="customerTypeId" cssClass="frmSelectClass">
								</form:select>
							</td>
						</tr>
						<tr class="hidden">
							<td class="labels"></td>
							<td class="value">
								<form:errors path="customerTypeId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Ship To</td>
							<td class="value">
								<form:textarea path="shipTo" id="shipTo" cssClass="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="shipTo" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Remarks</td>
							<td class="value">
								<form:textarea path="remarks" cssClass="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="remarks" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Currency</td>
							<td class="value">
								<form:select path="currencyId" id="currencyId" cssClass="frmMediumSelectClass" onchange="getCurrencyRate(this);">
									<form:options items="${currencies}" itemValue="id" itemLabel="name"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="currencyId" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Deposit</legend>
					<table class="formTable" id="checkOption">
						<tr>
							<td class="labels">Deposit Required</td>
							<td class="value"><form:checkbox path="deposit" id="cbIsDeposit" onclick="enableDisableAdvPayment();"/></td>
						</tr>
						<tr>
							<td class="labels">Advance Payment Amount</td>
							<td class="value"><form:input path="advancePayment" cssClass="input"
								id="txtAdvancePayment" cssStyle="text-align: right; width: 185px;"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="advancePayment" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Good/s</legend>
					<div id="soItemTbl"></div>
					<table>
						<tr>
							<td><form:errors path="soItems" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Service/s</legend>
					<div id="otherChargesTable"></div>
					<table>
						<tr>
							<td><form:errors path="soLines" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Document/s</legend>
					<div id="documentsTable"></div>
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
					</table>
				</fieldset>
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
							<span id="totalVAT"></span>
						</td>
					</tr>
					<tr>
						<td colspan="3">Withholding VAT</td>
						<td></td>
						<td>
							(<span id="withholdingVAT"></span>)
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
							(<span id="computedWTax">0.00</span>)
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
						<td style="text-align: right" colspan="6"><form:errors path="amount" cssClass="error"/></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSalesOrder" value="Save" onclick="saveSOForm();" /></td>
					</tr>
				</table>
			</div>
	</form:form>
</div>
</body>
</html>