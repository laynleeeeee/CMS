<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: AR invoice jsp form for NSB.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.drsaleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
var $tblDeliveryReceiptItem = null;
var $tblDRSerialItem  = null;
$(document).ready(function() {
	initializeSerializedItemTable();
	initializeNonSerializedDRItemTable();
	initializeDrLineTable();
	initDocumentsTbl();
	if (Number("${arInvoice.id}") > 0) {
		$("#txtDRRefNumber").val("${arInvoice.drNumber}");
		$("#aOpen").hide();
		loadWtaxAcctSettings("${arInvoice.wtAcctSettingId}");
		$("#hdnTermDays").val("${arInvoice.term.days}");
		$("#recoupment").val(Number("${arInvoice.recoupment}"));
		$("#retention").val(Number("${arInvoice.retention}"));
		computeWtax();
		// getRemainingCapBalance("${arInvoice.deliveryRecieptId}", "${arInvoice.id}");
	}

	if("${arInvoice.formWorkflow.complete}" == "true" ||
			"${arInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#arInvoiceForm :input").attr("disabled", "disabled");
		$("#imgDate").hide();
		$("#imgDueDate").hide();
	}
});

function initializeSerializedItemTable() {
	var serialArItemsJson =  JSON.parse($("#serialArItemsJson").val());
	setupSerialItems(serialArItemsJson);
}

function setupSerialItems(serialArItemsJson) {
	$("#tblDRSerialItem").html("");
	var cPath = "${pageContext.request.contextPath}";
	$tblDRSerialItem = $("#tblDRSerialItem").editableItem({
		data: serialArItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "itemSrpId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "serialNumber", "varType" : "string"},
			{"name" : "existingStocks", "varType" : "double"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "srp", "varType" : "double"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "itemDiscountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"}
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
				"visible" : false},
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "itemId", 
				"cls" : "itemId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemSrpId", 
				"cls" : "itemSrpId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Stock Code",
				"cls" : "stockCode tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%", 
				"handler" : new SalesTableHandler(new function() {
					this.handleTotal = function (total) {
						// do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "22%"},
			{"title" : "warehouseId",
				"cls" : "warehouseId",
				"editor" : "text",
				"visible" : false},
			{"title" : "Warehouse",
				"cls" : "warehouse tblSelectClass",
				"editor" : "select",
				"visible" : true ,
				"width" : "7%"},
			{"title" : "Existing <br> Stocks",
				"cls" : "existingStocks tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Serial Number",
				"cls" : "serialNumber tblInputText",
				"editor" : "text",
				"visible" : true, 
				"width" : "7%"},
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
				"cls" : "srp tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "6%"},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "select",
				"visible" : false,
				"width" : "6%"},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "6%"},
			{"title" : "Discount",
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
				"width" : "6%"},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "6%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "6%"}
		],
		footer: [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#tblDRSerialItem tr td a").each(function() {
		hideElem(this);
		computeGrandTotal();
		computeWtax();
	});

	$("#tblDRSerialItem :input").attr("disabled", "disabled");
}

function initializeNonSerializedDRItemTable() {
	var nonSerialArItemsJson = JSON.parse($("#nonSerialArItemsJson").val());
	setupNonSerialItems(nonSerialArItemsJson);
}

function setupNonSerialItems(nonSerialArItemsJson) {
	$("#tblDeliveryReceiptItem").html("");
	var cPath = "${pageContext.request.contextPath}";
	$tblDeliveryReceiptItem = $("#tblDeliveryReceiptItem").editableItem({
		data: nonSerialArItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "itemSrpId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "serialNumber", "varType" : "string"},
			{"name" : "existingStocks", "varType" : "double"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "srp", "varType" : "double"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "itemDiscountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"}
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
				"visible" : false},
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "itemId", 
				"cls" : "itemId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemSrpId", 
				"cls" : "itemSrpId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Stock Code",
				"cls" : "stockCode tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%", 
				"handler" : new SalesTableHandler(new function() {
					this.handleTotal = function (total) {
						// do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "22%"},
			{"title" : "warehouseId",
				"cls" : "warehouseId",
				"editor" : "text",
				"visible" : false},
			{"title" : "Warehouse",
				"cls" : "warehouse tblSelectClass",
				"editor" : "select",
				"visible" : true ,
				"width" : "7%"},
			{"title" : "Existing <br> Stocks",
				"cls" : "existingStocks tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "6%"},
			{"title" : "UOM",
				"cls" : "uom txtUOM",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Gross Price",
				"cls" : "srp tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "select",
				"visible" : false,
				"width" : "7%"},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "7%"},
			{"title" : "Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "7%"},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"}
		],
		footer: [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#tblDeliveryReceiptItem tr td a").each(function() {
		hideElem(this);
		computeGrandTotal();
		computeWtax();
	});

	$("#tblDeliveryReceiptItem :input").attr("disabled", "disabled");
	resizeTbl("#tblDeliveryReceiptItem", 12);
}

var isSaving = false;
function saveArInvoice() {
	if (!isSaving && $("#hdnDrRefIds").val() != "") {
		isSaving = true;
		// enabling item and line table
		$("#tblDeliveryReceiptItem :input").removeAttr("disabled");
		$("#tblDRSerialItem :input").removeAttr("disabled");
		$("#otherChargesTable :input").removeAttr("disabled");
		// reset JSON table values
		$("#serialArItemsJson").val($tblDRSerialItem.getData());
		$("#nonSerialArItemsJson").val($tblDeliveryReceiptItem.getData());
		$("#ariLineJson").val($otherChargesTable.getData());
		$("#referenceDocumentJson").val($documentsTable.getData());
		$("#btnSaveArInvoice").attr("disabled", "disabled");
		// Remove formatting
		$("#retention").val(accounting.unformat($("#retention").val()));
		$("#recoupment").val(accounting.unformat($("#recoupment").val()));
		doPostWithCallBack ("arInvoiceForm", "form", function(data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var id = $("#hdnId").val();
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var companyId = $("#companyId").val();
				var companyName = $("#spCompanyName").text();
				var customerId = $("#hdnArCustomerId").val();
				var customerName = $("#spCustomerName").text();
				var customerAcctId = $("#hdnArCustomerAcctId").val();
				var customerAcctName = $("#spCustomerAcctName").text();
				var termId = $("#hdnTermId").val();
				var termName = $("#spTermName").text();
				var txtCurrentStatusId = $("#txtCurrentStatusId").val();
				var termDays = $("#hdnTermDays").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				var drNumber = $("#txtDRRefNumber").val();
				var remarks = $("#remarks").val();
				var divisionId = $("#divisionId").val();
				var divisionName = $("#spDivisionName").text();
				var currencyId =  $("#currencyId").val();
				var currencyName = $("#spCurrencyName").text();
				var currencyRateId = $("#currencyRateId").val();
				var currencyRateValue = $("#currencyRateValue").val();
				var drObjIds = $("#hdnDrRefIds").val();
				if ("${arInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#aOpen").hide();
				}
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#txtCurrentStatusId").val(txtCurrentStatusId);
				$("#hdnDrRefIds").val(drObjIds);
				updateHeader(drNumber, companyId, companyName, customerId, customerName, customerAcctId, customerAcctName,
						termId, termName, termDays, remarks, divisionId, divisionName, currencyId, currencyName,
						currencyRateId, currencyRateValue);
				loadWtaxAcctSettings(wtAcctSettingId);
				initializeSerializedItemTable();
				initializeNonSerializedDRItemTable();
				initializeDrLineTable();
				initDocumentsTbl();
				getRemainingCapBalance(drObjIds, id);
			}
			isSaving = false;
			$("#btnSaveArInvoice").removeAttr("disabled");
		});
	} else {
		$("#spnDeliveryRecieptId").text("DR Reference/s is required.");
	}
}

function showDRReferences() {
	var arInvoiceTypeId = $("#hdnArInvoiceTypeId").val();
	$("#divDrReferenceId").load(contextPath+"/arInvoice/showReferenceForm?arInvoiceTypeId="+arInvoiceTypeId);
}

function loadDRReference(drRefIds) {
	var drObjIds = "";
	var keys = Object.keys(drRefIds);
	for (var i = 0; i< keys.length; i++) {
		if (i == 0) {
			drObjIds += keys[i];
		} else {
			drObjIds += ", " + keys[i];
		}
	}
	if (drObjIds != "") {
		$.ajax({
			url: contextPath + "/arInvoice/loadDRReferenceForm?referenceDrIds="+drObjIds,
			success : function(ari) {
				$("#hdnDrRefIds").val(drObjIds);
				$("#divDrReferenceId").html("");
				$("#spnDeliveryRecieptId").text("");
				$("#aClose")[0].click();
				updateHeader(ari.drNumber, ari.companyId, ari.company.name, ari.arCustomerId,
						ari.arCustomer.name, ari.arCustomerAccountId, ari.arCustomerAccount.name, ari.termId, ari.term.name,
						ari.term.days, ari.remarks, ari.divisionId, ari.division.name, ari.currencyId, ari.currency.name,
						ari.currencyRateId, ari.currencyRateValue, ari.wtVatAmount);
				loadWtaxAcctSettings(ari.wtAcctSettingId);
				setupSerialItems(ari.serialArItems);
				setupNonSerialItems(ari.nonSerialArItems);
				setupDrLines(ari.ariLines);
				getRemainingCapBalance(drObjIds, null);
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

function updateHeader(drNumber, companyId, companyName, arCustomerId, arCustomerName,
		arCustomerAcctId, arCustomerAcctName, termId, termName, termDays, remarks, divisionId, divisionName,
		currencyId, currencyName, currencyRateId, currencyRateValue, withholdingVAT) {
	$("#txtDRRefNumber").val(drNumber);
	$("#companyId").val(companyId);
	$("#spCompanyName").text(companyName);
	$("#hdnArCustomerId").val(arCustomerId);
	$("#spCustomerName").text(arCustomerName);
	$("#hdnArCustomerAcctId").val(arCustomerAcctId);
	$("#spCustomerAcctName").text(arCustomerAcctName);
	$("#hdnTermId").val(termId);
	$("#spTermName").text(termName);
	$("#remarks").val(remarks);
	$("#hdnTermDays").val(termDays);
	$("#divisionId").val(divisionId);
	$("#spDivisionName").text(divisionName);
	$("#currencyId").val(currencyId);
	$("#spCurrencyName").text(currencyName);
	$("#currencyRateId").val(currencyRateId == 0 ? "" : currencyRateId);
	$("#currencyRateValue").val(currencyRateValue);
	computeDueDate();
}

function hideElem(elem) {
	$(elem).css("display", "none");
}

function resizeTbl(tableId, rowCount){
	var $tbl = $(tableId);
	$($tbl).attr("width", "100%");
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
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
				var computedWTax = formatDecimalPlaces((totalAmount * wtPercentageValue).toFixed(6));
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
	var totalSerialItemAmt = 0;
	var totalVAT = 0;
	var totalWtVAT = 0;
	$("#tblDRSerialItem").find(" tbody tr ").each(function(row) {
		var amount = accounting.unformat($(this).find(".amount").html());
		totalSerialItemAmt += amount;
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
		if ($(this).find(".taxTypeId").val() == 9) {
			totalWtVAT += (amount * 0.05);
		}
	});
	$("#tblDRSerialItem").find("tfoot tr .amount").html(formatDecimalPlaces(totalSerialItemAmt));

	var totalNonSerialItemAmt = 0;
	$("#tblDeliveryReceiptItem").find(" tbody tr ").each(function(row) {
		var amount = accounting.unformat($(this).find(".amount").html());
		totalNonSerialItemAmt += amount
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
		if ($(this).find(".taxTypeId").val() == 9) {
			totalWtVAT += (amount * 0.05);
		}
	});
	$("#tblDeliveryReceiptItem").find("tfoot tr .amount").html(formatDecimalPlaces(totalNonSerialItemAmt));

	var totalOtherChargesAmt = 0;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		var amount = accounting.unformat($(this).find(".amount").val());
		totalOtherChargesAmt += amount;
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
		if ($(this).find(".taxTypeId").val() == 9) {
			totalWtVAT += (amount * 0.05);
		}
	});
	$("#otherChargesTable").find("tfoot tr .amount").html(formatDecimalPlaces(totalOtherChargesAmt));

	var totalAriTruckingAmt = 0;
	$("#arInvoiceTruckingLinesTbl").find(" tbody tr ").each(function(row) {
		totalAriTruckingAmt += accounting.unformat($(this).find(".amount").val());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#arInvoiceTruckingLinesTbl").find("tfoot tr .amount").html(formatDecimalPlaces(totalAriEquipmentAmt));

	var totalAriEquipmentAmt = 0;
	$("#arInvoiceEquipmentLinesTbl").find(" tbody tr ").each(function(row) {
		totalAriEquipmentAmt += accounting.unformat($(this).find(".amount").val());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#arInvoiceEquipmentLinesTbl").find("tfoot tr .amount").html(formatDecimalPlaces(totalAriEquipmentAmt));

	var retention = accounting.unformat($("#retention").val());
	var recoupment = accounting.unformat($("#recoupment").val());

	var formattedRetention = formatDecimalPlaces(retention);
	$("#retention").val(formattedRetention);

	var formattedRecoupment = formatDecimalPlaces(recoupment);
	$("#recoupment").val(formattedRecoupment);

	var subTotal = totalSerialItemAmt + totalNonSerialItemAmt + totalOtherChargesAmt + totalAriTruckingAmt + totalAriEquipmentAmt;
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	$("#hdnWtVatAmount").val(totalWtVAT);

	var formattedTotalWtVAT = formatDecimalPlaces(totalWtVAT);
	$("#withholdingVAT").html(formattedTotalWtVAT);

	var formattedSubTotal = formatDecimalPlaces(subTotal);
	$("#subTotal").text(formattedSubTotal);

	var formattedTotalVAT = formatDecimalPlaces(totalVAT);
	$("#totalVAT").html(formattedTotalVAT);

	var formattedWtaxAmount = formatDecimalPlaces(wtaxAmount);
	$("#computedWTax").html(formattedWtaxAmount);

	var grandTotal = accounting.unformat(formattedSubTotal) + accounting.unformat(formattedTotalVAT) - accounting.unformat(formattedTotalWtVAT)
		- accounting.unformat(formattedWtaxAmount) - accounting.unformat(formattedRetention) - accounting.unformat(formattedRecoupment);
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
	$("#hdnAmountId").val(grandTotal);
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeGrandTotal();
	computeWtax();
}

function initializeDrLineTable() {
	var ariLineJson = JSON.parse($("#ariLineJson").val());
	setupDrLines(ariLineJson);
}

function setupDrLines(ariLineJson) {
	$("#otherChargesTable").html("");
	var path = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: ariLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "salesQuotationId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "serviceSettingId", "varType" : "int"},
			{"name" : "serviceSettingName", "varType" : "string"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "unitOfMeasurementId", "varType" : "int"},
			{"name" : "discountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "percentile", "varType" : "double"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "description", "varType" : "string"}
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "salesQuotationId",
				"cls" : "salesQuotationId",
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
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "Service",
				"cls" : "serviceSettingName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "31%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
			})},
			{"title" : "description",
				"cls" : "description",
				"editor" : "hidden",
				"visible": false},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "percentile",
				"cls" : "percentile",
				"editor" : "hidden",
				"visible" : false},
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
				"visible" : false,
				"width" : "8%"},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "8%"},
			{"title" : "Discount",
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
				"width" : "10%"},
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

	$("#otherChargesTable tr td a").each(function() {
		hideElem(this);
		computeGrandTotal();
		computeWtax();
	});

	$("#otherChargesTable :input").attr("disabled", "disabled");
	resizeTbl("#otherChargesTable", 9);
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

function getRemainingCapBalance(deliveryReceiptIds, arInvoiceId) {
	if (deliveryReceiptIds != "") {
		var uri = contextPath + "/arInvoice/getRemainingCapBalance?deliveryReceiptIds="+deliveryReceiptIds;
		if (arInvoiceId != null && arInvoiceId != "" && arInvoiceId != 0 && arInvoiceId != "undefined") {
			uri += "&arInvoiceId="+arInvoiceId;
		}
		$.ajax({
			url: uri,
			success : function(balance) {
				$("#spnRecoupment").text("Recoupment ("+formatDecimalPlaces(Number(balance))+")");
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}
</script>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divDrReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divDrReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="arInvoice" id="arInvoiceForm">
	<div class="modFormLabel">AR Invoice <span id="spanDivisionLbl"> - ${arInvoice.division.name}</span>
	<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id" id="hdnId"/>
		<form:hidden path="sequenceNo"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="serialArItemsJson" id="serialArItemsJson"/>
		<form:hidden path="nonSerialArItemsJson" id="nonSerialArItemsJson"/>
		<form:hidden path="ariLineJson" id="ariLineJson"/>
		<form:hidden path="amount" id="hdnAmountId"/>
		<form:hidden path="wtAmount" id="hdnWtAmount"/>
		<form:hidden path="arInvoiceTruckingLinesJson" id="arInvoiceTruckingLinesJson"/>
		<form:hidden path="arInvoiceEquipmentLinesJson" id="arInvoiceEquipmentLinesJson"/>
		<form:hidden path="arInvoiceTypeId" id="hdnArInvoiceTypeId"/>
		<form:hidden path="strDrRefIds" id="hdnDrRefIds"/>
		<form:hidden path="currencyRateId" id="currencyRateId"/>
		<form:hidden path="currencyRateValue" id="currencyRateValue"/>
		<form:hidden path="referenceDocumentJson" id="referenceDocumentJson" />
		<form:hidden path="wtVatAmount" id="hdnWtVatAmount" />
		<br>
		<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="labels">ARI No</td>
					<td class="value">
						<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"
									value="${arInvoice.sequenceNo}"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Status</td>
					<c:set var="status" value="${arInvoice.formWorkflow.currentFormStatus.description}"/>
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
		<legend>Delivery Receipt Header</legend>
			<table class="formTable">
				<tr>
					<td class="labels">* Company</td>
					<td class="value">
						<form:hidden path="companyId" id="companyId"/>
						<span id="spCompanyName">${arInvoice.company.name}</span>
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
						<span id="spDivisionName">${arInvoice.division.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">* DR Reference</td>
					<td class="value">
						<input id="txtDRRefNumber" readonly="readonly"/>
						<a href="#container" id="aOpen" data-reveal-id="divDrReferenceId" class="link_button"
							onclick="showDRReferences();">Browse DR</a>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="strDrRefIds" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><span id="spnDeliveryRecieptId" class="error"></span></td>
				</tr>
				<tr>
					<td class="labels">* Customer</td>
					<td class="value">
						<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
						<span id="spCustomerName">${arInvoice.arCustomer.name}</span>
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
						<form:hidden path="arCustomerAccountId" id="hdnArCustomerAcctId"/>
						<span id="spCustomerAcctName">${arInvoice.arCustomerAccount.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value">
						<form:errors path="arCustomerAccountId" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td class="labels">* Term</td>
					<td class="value">
						<form:hidden path="termId" id="hdnTermId"/>
						<input type="hidden" id="hdnTermDays"/>
						<span id="spTermName">${arInvoice.term.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="termId" cssClass="error"/></td>
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
					<td class="labels">Ship To</td>
					<td class="value">
						<form:textarea path="remarks" id="remarks" class="txtArea"/>
					</td>
				</tr>
				<tr>
					<td class="labels">* Currency</td>
					<td class="value">
						<form:hidden path="currencyId" id="currencyId"/>
						<span id="spCurrencyName">${arInvoice.currency.name}</span>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset id="serialItemsTbl" class="frmField_set">
			<legend>Serialized Good/s</legend>
			<div id="tblDRSerialItem" class="tblSerial"></div>
			<table>
				<tr>
					<td>
						<span id="serialItemsMessage" class="error"></span>
					</td>
				</tr>
				<tr>
					<td>
						<form:errors path="serialErrMsg" cssClass="error"/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset id="nonSerialItemsTbl" class="frmField_set">
			<legend>Non-Serialized Good/s</legend>
			<div id="tblDeliveryReceiptItem"></div>
			<table>
				<tr>
					<td>
						<span id="deliveryItemsMessage" class="error"></span>
					</td>
				</tr>
				<tr>
					<td>
						<form:errors path="nonSerialErrMsg" id="nonSerialErrMsg" cssClass="error"/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Service/s</legend>
			<div id="otherChargesTable"></div>
			<table>
				<tr>
					<td><form:errors path="ariLines" cssClass="error"/></td>
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
				<td style="width: 22%;">Total VAT</td>
				<td style="width: 22%;"></td>
				<td style="width: 12%;"><span id="totalVAT"></span></td>
			</tr>
			<tr>
				<td colspan="3">Sub Total</td>
				<td></td>
				<td>
					<span id="subTotal"></span>
				</td>
			</tr>
			<tr>
				<td colspan="3">Withholding VAT</td>
				<td></td>
				<td>
					<span id="withholdingVAT"></span>
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
				<td colspan="3"><span id="spnRecoupment">Recoupment</span></td>
				<td></td>
				<td>
					<form:input path="recoupment" id="recoupment" class="input number" onblur="computeGrandTotal();"
					style="text-align: right; width: 185px;"/>
				</td>
			</tr>
			<tr>
				<td colspan="5"><form:errors path="recoupment" cssClass="error"/></td>
			</tr>
			<tr>
				<td colspan="3">Retention</td>
				<td></td>
				<td>
					<form:input path="retention" id="retention" class="input number" onblur="computeGrandTotal();"
					style="text-align: right; width: 185px;"/>
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
					<form:errors path="amount" cssClass="error"/>
				</td>
			</tr>
		</table>
		<table class="frmField_set">
			<tr>
				<td align="right"><input type="button" id="btnSaveArInvoice"
						value="Save" onclick="saveArInvoice();" /></td>
			</tr>
		</table>
		</div>
	</form:form>
</div>
</body>
</html>