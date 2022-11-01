<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Delivery Receipt form for nsb.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.constructionsaleshandler.js"></script>
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
	if (Number("${deliveryReceipt.id}") != 0) {
		$("#txtATWRefNumber").val("${deliveryReceipt.soNumber}");
		$("#aOpen").hide();
		$("#hdnTermDays").val("${deliveryReceipt.term.days}");
	}
	if ("${deliveryReceipt.formWorkflow.complete}" == "true" ||
			"${deliveryReceipt.formWorkflow.currentStatusId}" == 4) {
		$("#deliveryReceiptForm :input").attr("disabled", "disabled");
		$("#btnSaveDeliveryReceipt").attr("disabled", "disabled");
		$("#imgDate").hide();
		$("#imgDueDate").hide();
	}
});

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

function initializeSerializedItemTable() {
	var serialDrItemsJson =  JSON.parse($("#serialDrItemsJson").val());
	setupSerialItems(serialDrItemsJson);
}

function setupSerialItems(serialDrItemsJson) {
	$("#tblDRSerialItem").html("");
	var cPath = "${pageContext.request.contextPath}";
	$tblDRSerialItem = $("#tblDRSerialItem").editableItem({
		data: serialDrItemsJson,
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
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "origWarehouseId", "varType" : "int"}
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
				"width" : "11%", 
				"handler" : new SalesTableHandler(new function() {
					this.handleTotal = function (total) {
						// do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "13%"},
			{"title" : "warehouseId",
				"cls" : "warehouseId",
				"editor" : "text",
				"visible" : false},
			{"title" : "origWarehouseId",
				"cls" : "origWarehouseId",
				"editor" : "text",
				"visible" : false},
			{"title" : "Warehouse",
				"cls" : "warehouse tblSelectClass",
				"editor" : "select",
				"visible" : true ,
				"width" : "15%"},
			{"title" : "Existing <br> Stocks",
				"cls" : "existingStocks tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Serial Number",
				"cls" : "serialNumber tblInputText",
				"editor" : "text",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "Qty",
				"cls" : "quantity tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "UOM",
				"cls" : "uom txtUOM",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Gross Price", 
				"cls" : "srp tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "discountValue", 
				"cls" : "discountValue", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : false},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false
	});

	$("#tblDRSerialItem").on("change", ".warehouse", function(){
			assignESByWarehouse($(this));
 	});

	$("#tblDRSerialItem").on("focus", ".tblInputNumeric", function() {
		var value = $(this).val();
		if ($.trim(value) != "") {
			$(this).val(accounting.unformat(value));
		}
		disableInputFields(this, "spanSerialItemError", false);
	});

	$("#tblDRSerialItem").on("focus", ".tblInputText", function() {
		disableInputFields(this, "spanSerialItemError", false);
	});

	// Disable stock code fields.
	$("#tblDRSerialItem tr td .stockCode").each(function() {
		$(this).attr("disabled", "disabled");
	});
}

function initializeNonSerializedDRItemTable() {
	var nonSerialDrItemsJson = JSON.parse($("#nonSerialDrItemsJson").val());
	setupNonSerialItems(nonSerialDrItemsJson);
}

function setupNonSerialItems(nonSerialDrItemsJson) {
	$("#tblDeliveryReceiptItem").html("");
	var cPath = "${pageContext.request.contextPath}";
	$tblDeliveryReceiptItem = $("#tblDeliveryReceiptItem").editableItem({
		data: nonSerialDrItemsJson,
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
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "origQty", "varType" : "double"},
			{"name" : "origWarehouseId", "varType" : "int"}
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
				"width" : "11%", 
				"handler" : new SalesTableHandler(new function() {
					this.handleTotal = function (total) {
						// do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "13%"},
			{"title" : "warehouseId",
				"cls" : "warehouseId",
				"editor" : "text",
				"visible" : false},
			{"title" : "origWarehouseId",
				"cls" : "origWarehouseId",
				"editor" : "text",
				"visible" : false},
			{"title" : "Warehouse",
				"cls" : "warehouse tblSelectClass",
				"editor" : "select",
				"visible" : true ,
				"width" : "15%"},
			{"title" : "Existing <br> Stocks",
				"cls" : "existingStocks tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%"},
			{"title" : "origQty",
				"cls" : "origQty tblInputNumeric",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "UOM",
				"cls" : "uom txtUOM",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Gross Price",
				"cls" : "srp tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "discountValue", 
				"cls" : "discountValue", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : false},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false
	});

	$("#tblDeliveryReceiptItem").on("change", ".warehouse", function(){
		assignESByWarehouse($(this));
	});
		
	$("#tblDeliveryReceiptItem").on("focus", ".tblInputNumeric", function() {
		var value = $(this).val();
		if ($.trim(value) != "") {
			$(this).val(accounting.unformat(value));
		}
		disableInputFields(this, "spanNonSerialItemError", false);
 	});
 
	$("#tblDeliveryReceiptItem").on("focus", ".tblInputText", function() {
		disableInputFields(this, "spanNonSerialItemError", false);
	});

	// Disable stock code fields.
	$("#tblDeliveryReceiptItem tr td .stockCode").each(function() {
		$(this).attr("disabled", "disabled");
	});
}

function disableInputFields(elem, spanIdName, isService) {
	var $tr = $(elem).closest("tr");
	var $name = null;
	var tableLbl = "";
	if(isService) {
		$name = $tr.find(".serviceSettingName");
		$tr.find(".description").attr("disabled", "disabled");
		tableLbl = "service/s";
	} else {
		$name = $tr.find(".stockCode");
		tableLbl = "item/s";
	}
	$name.attr("disabled", "disabled");
	if ($.trim($name.val()) == "") {
		$("#"+spanIdName).text("Adding of new "+tableLbl+" without SO is not allowed.");
	}
}

var isSaving = false;
function saveDeliveryReceipt() {
	if (!isSaving) {
		var atwNumber = $.trim($("#txtATWRefNumber").val());
		var hasSalesRepErr = $("#salesRepErr").text() != "";
		if (atwNumber != "" && !hasSalesRepErr) {
			isSaving = true;
			// enabling item and line table
			$("#tblDeliveryReceiptItem :input").removeAttr("disabled");
			$("#tblDRSerialItem :input").removeAttr("disabled");
			$("#otherChargesTable :input").removeAttr("disabled");
			// reset JSON table values
			$("#serialDrItemsJson").val($tblDRSerialItem.getData());
			$("#nonSerialDrItemsJson").val($tblDeliveryReceiptItem.getData());
			$("#drLineJson").val($otherChargesTable.getData());
			$("#referenceDocumentJson").val($documentsTable.getData());
			$("#btnSaveDeliveryReceipt").attr("disabled", "disabled");
			doPostWithCallBack ("deliveryReceiptForm", "form", function(data) {
				if (data.startsWith("saved")) {
					var objectId = data.split(";")[1];
					var formStatus = new Object();
					formStatus.objectId = objectId;
					updateTable (formStatus);
					dojo.byId("form").innerHTML = "";
				} else {
					var spanDivisionLbl = $("#spanDivisionLbl").text();
					var currencyId = $("#hdnCurrId").val();
					var currRateId = $("#hdnCurrRateId").val();
					var currRateValue = $("#hdnCurrRateValue").val();
					var divisionId = $("#divisionId").val();
					var divisionName = $("#spDivisionName").text();
					var poNumber = $("#hdnPoNumber").val();
					var soId = $("#hdnSalesOrderId").val();
					var companyId = $("#companyId").val();
					var companyName = $("#spCompanyName").text();
					var customerId = $("#hdnArCustomerId").val();
					var customerName = $("#spCustomerName").text();
					var customerAcctId = $("#arCustomerAcctId").val();
					var customerAcctName = $("#spCustomerAcctName").text();
					var termId = $("#hdnTermId").val();
					var termName = $("#spTermName").text();
					var txtCurrentStatusId = $("#txtCurrentStatusId").val();
					var termDays = $("#hdnTermDays").val();
					var remarks = $("#remarks").val();
					var salesRep = $("#salesRep").val();
					var wtAcctSettingId = $("#wtAcctSettingId").val();
					var wtAmount = $("#wtAmount").val();
					var wtVatAmount = $("#wtVatAmount").val();
					if ("${deliveryReceipt.id}" == 0) {
						dojo.byId("form").innerHTML = data;
					} else {
						dojo.byId("editForm").innerHTML = data;
						$("#aOpen").hide();
					}
					$("#spanDivisionLbl").text(spanDivisionLbl);
					$("#salesRep").val(salesRep);
					$("#txtCurrentStatusId").val(txtCurrentStatusId);
					updateHeader(soId, atwNumber, companyId, companyName, customerId, customerName,
							customerAcctId, customerAcctName, termId, termName, termDays, remarks,
							currencyId, currRateId, currRateValue, divisionId, divisionName, poNumber,
							wtAcctSettingId, wtAmount, wtVatAmount);
					initializeSerializedItemTable();
					initializeNonSerializedDRItemTable();
					initializeDrLineTable();
					initDocumentsTbl();
				}
				isSaving = false;
				$("#btnSaveDeliveryReceipt").removeAttr("disabled");
			});
		} 
		if(atwNumber == "") {
			$("#spanSalesOrderMsg").text("Sales order reference is required.");
		}
	}
}

function showATWReferences() {
	var drTypeId = $.trim($("#hdnDrTypeId").val());
	var divisionId = $("#divisionId").val();
	$("#divATWReferenceId").load(contextPath+"/deliveryReceipt/showReferenceForm?drTypeId="+drTypeId+"&divisionId="+divisionId);
}

function loadSalesOrderReference(salesOrderId) {
	if (salesOrderId != "") {
		var drTypeId = $.trim($("#hdnDrTypeId").val());
		$.ajax({
			url: contextPath+"/deliveryReceipt/convertSOtoDR?salesOrderId="+salesOrderId+"&drTypeId="+drTypeId,
			success : function(dr) {
				$("#spanSalesOrderMsg").html("");
				$("#divATWReferenceId").html("");
				$("#aClose")[0].click();
				updateHeader(dr.salesOrderId, dr.atwNumber, dr.companyId, dr.company.name, dr.arCustomerId,
						dr.arCustomer.name, dr.arCustomerAccountId, dr.arCustomerAccount.name, dr.termId, dr.term.name,
						dr.term.days, dr.remarks, dr.currencyId, dr.currencyRateId, dr.currencyRateValue, dr.division.id,
						dr.division.name, dr.poNumber, dr.wtAcctSettingId, dr.wtAmount, dr.wtVatAmount);
				setupSerialItems(dr.serialDrItems);
				setupNonSerialItems(dr.nonSerialDrItems);
				setupDrLines(dr.drLines);
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

function updateHeader(atwId, atwNumber, companyId, companyName, arCustomerId,
		arCustomerName, arCustomerAcctId, arCustomerAcctName, termId, termName, termDays, remarks,
		currencyId, currencyRateId, currencyRateValue, divisionId, divisionName, poNumber, wtAcctSettingId,
		wtAmount, wtVatAmount) {
	$("#hdnSalesOrderId").val(atwId);
	$("#txtATWRefNumber").val(atwNumber);
	$("#companyId").val(companyId);
	$("#spCompanyName").text(companyName);
	$("#hdnArCustomerId").val(arCustomerId);
	$("#spCustomerName").text(arCustomerName);
	$("#arCustomerAcctId").val(arCustomerAcctId);
	$("#spCustomerAcctName").text(arCustomerAcctName);
	$("#hdnTermId").val(termId);
	$("#spTermName").text(termName);
	$("#remarks").val(remarks);
	$("#hdnTermDays").val(termDays);
	$("#hdnCurrId").val(currencyId);
	$("#hdnCurrRateId").val(currencyRateId == 0 ? "" : currencyRateId);
	$("#hdnCurrRateValue").val(currencyRateValue);
	$("#divisionId").val(divisionId);
	$("#spDivisionName").text(divisionName);
	$("#hdnPoNumber").val(poNumber);
	$("#spPoNumber").text(poNumber);
	$("#wtAcctSettingId").val(wtAcctSettingId  == 0 ? "" : wtAcctSettingId);
	$("#wtAmount").val(wtAmount);
	$("#wtVatAmount").val(wtVatAmount);
	computeDueDate();
}

function initializeDrLineTable() {
	var drLineJson = JSON.parse($("#drLineJson").val());
	setupDrLines(drLineJson);
}

function setupDrLines(drLineJson) {
	$("#otherChargesTable").html("");
	var path = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: drLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "salesQuotationId", "varType" : "int"},
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
			{"name" : "percentile", "varType" : "double"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "refenceObjectId", "varType" : "int"}
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
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
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
				"width" : "30%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
			})},
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "30%" },
			{"title" : "Percentile",
				"cls" : "percentile tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "13%" },
			{"title" : "Temp Qty",
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
				"width" : "8%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Gross Price",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : false,
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
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : false,
				"width" : "8%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : false,
				"width" : "8%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : false,
				"width" : "8%"},
			{"title" : "Amount",
				"cls" : "amount tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "10%"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function() {
		disableInputFields(this, "spanOtherChargesTbl", true);
	});

	$("#otherChargesTable").on("focus", ".tblInputText", function() {
		disableInputFields(this, "spanOtherChargesTbl", true);
	});

	// Disable AR line setup name field/s.
	$("#otherChargesTable tr td .serviceSettingName").each(function() {
		$(this).attr("disabled", "disabled");
	});

	$("#otherChargesTable").on("blur", ".percentile, .quantity", function() {
		if (drLineJson != null && drLineJson != "") {
			computePercentile(this);
		}
	});

	$("#otherChargesTable tbody tr").each(function() {
		$(this).closest("tr").find(".serviceSettingName").attr("disabled", "disabled");
		$(this).closest("tr").find(".description").attr("disabled", "disabled");
		if(drLineJson != null && drLineJson != "") {
			var qty = accounting.unformat($(this).closest("tr").find(".quantity").val());
			var percentile = accounting.unformat($(this).closest("tr").find(".percentile").val()) / 100;
			if (!isNaN(Number(qty))) {
				var origQty = qty / percentile;
				$(this).closest("tr").find(".origQty").val(origQty);
			}
			if(qty == 0) {
				$(this).closest("tr").find(".quantity").attr("disabled","disabled");
				$(this).closest("tr").find(".percentile").attr("disabled","disabled");
			}
		}
	});
}

function computePercentile(elem) {
	var origQty = $(elem).closest("tr").find(".origQty").val();
	var isQtyElem = $(elem).attr("class").includes("quantity");//True if the edited element is quantity.
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
	$($percentile).val(formatDecimalPlaces($($percentile).val(), 9));

	var $quantity = $(elem).closest("tr").find(".quantity");
	$($quantity).val(formatDecimalPlaces($($quantity).val(), 9));
}

function showSalesPersonnels() {
	var companyId = $("#companyId").val();
	var name = $("#salesRep").val();
	$("#salesRep").autocomplete({
		source: contextPath + "/getSalesPersonnels?name="+processSearchName(name)+"&companyId="+companyId,
		select: function( event, ui ) {
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

function getSalesPersonnel() {
	$("#salesRepErr").text("");
	var companyId = $("#companyId").val();
	var name = $("#salesRep").val();
	if (name != "") {
		$.ajax({
			url: contextPath + "/getSalesPersonnels?name="+processSearchName(name)
					+"&isExact=true"+"&companyId="+companyId,
			success : function(salesPersonnel) {
				if (salesPersonnel != null && salesPersonnel[0] != undefined) {
					$("#hdnSalesPersonnelId").val(salesPersonnel[0].id);
					$("#salesRep").val(salesPersonnel[0].name);
				} else {
					$("#salesRepErr").text("Invalid sales representative.");
					$("#hdnSalesPersonnelId").val("");
				}
			},
			dataType: "json"
		});
	} else {
		$("#hdnSalesPersonnelId").val("");
	}
}
</script>
<style>
</style>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divATWReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divATWReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="deliveryReceipt" id="deliveryReceiptForm">
	<div class="modFormLabel">Delivery Receipt <span id="spanDivisionLbl"> - ${deliveryReceipt.division.name}</span>
	<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="sequenceNo"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="deliveryReceiptTypeId" id="hdnDrTypeId"/>
		<form:hidden path="serialDrItemsJson" id="serialDrItemsJson"/>
		<form:hidden path="nonSerialDrItemsJson" id="nonSerialDrItemsJson"/>
		<form:hidden path="drLineJson" id="drLineJson"/>
		<form:hidden path="currencyId" id="hdnCurrId"/>
		<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
		<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
		<form:hidden path="salesPersonnelId" id="hdnSalesPersonnelId"/>
		<form:hidden path="referenceDocumentJson" id="referenceDocumentJson" />
		<form:hidden path="wtAcctSettingId" id="wtAcctSettingId" />
		<form:hidden path="wtAmount" id="wtAmount" />
		<form:hidden path="wtVatAmount" id="wtVatAmount" />
		<input type="hidden" id="isStockIn" value="${isStockIn}"/>
		<br>
		<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="labels">Sequence Number</td>
					<td class="value">
						<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"
									value="${deliveryReceipt.sequenceNo}"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Status</td>
					<c:set var="status" value="${deliveryReceipt.formWorkflow.currentFormStatus.description}"/>
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
					<td class="labels">Company</td>
					<td class="value">
						<form:hidden path="companyId" id="companyId"/>
						<span id="spCompanyName">${deliveryReceipt.company.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="companyId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">Division</td>
					<td class="value">
						<form:hidden path="divisionId" id="divisionId"/>
						<span id="spDivisionName">${deliveryReceipt.division.name}</span>
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
						<input id="txtATWRefNumber" readonly="readonly"/>
						<a href="#container" id="aOpen" data-reveal-id="divATWReferenceId" class="link_button"
							onclick="showATWReferences();">Browse SO</a>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="authorityToWithdrawId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><span id="spanSalesOrderMsg" class="error"></span></td>
				</tr>
				<tr>
					<td class="labels">PO/PCR No.</td>
					<td class="value">
						<form:hidden path="poNumber" id="hdnPoNumber"/>
						<span id="spPoNumber">${deliveryReceipt.poNumber}</span>
					</td>
				</tr>
				<tr>
					<td class="labels">Customer</td>
					<td class="value">
						<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
						<span id="spCustomerName">${deliveryReceipt.arCustomer.name}</span>
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
						<span id="spCustomerAcctName">${deliveryReceipt.arCustomerAccount.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value">
						<form:errors path="arCustomerAccountId" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Term</td>
					<td class="value">
						<form:hidden path="termId" id="hdnTermId"/>
						<input type="hidden" id="hdnTermDays"/>
						<span id="spTermName">${deliveryReceipt.term.name}</span>
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
					<td class="labels">DR Reference No.</td>
					<td class="value"><form:input path="drRefNumber" id="drRefNumber" class="input"/>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="drRefNumber" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">* Sales Representative</td>
					<td class="value"><form:input path="salesPersonnelName" id="salesRep" class="input"
						onkeypress="showSalesPersonnels();"
						onblur="getSalesPersonnel();" />
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="salesPersonnelId" cssClass="error"/>
					<span id="salesRepErr" class="error"></span></td>
				</tr>
			</table>
		</fieldset>
		<fieldset id="serialItemsTbl" class="frmField_set">
			<legend>Serialized Items Table</legend>
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
				<tr>
					<td>
						<span id="spanSerialItemError" class="error"></span>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset id="nonSerialItemsTbl" class="frmField_set">
			<legend>Goods Table</legend>
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
				<tr>
					<td>
						<span id="spanNonSerialItemError" class="error"></span>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Service Table</legend>
			<div id="otherChargesTable"></div>
			<table>
				<tr>
					<td><form:errors path="drLines" cssClass="error"/></td>
				</tr>
				<tr>
					<td>
						<span id="spanOtherChargesTbl" class="error"></span>
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
		<table class="frmField_set">
			<tr>
				<td align="right"><input type="button" id="btnSaveDeliveryReceipt"
						value="Save" onclick="saveDeliveryReceipt();" /></td>
			</tr>
		</table>
		</div>
	</form:form>
</div>
</body>
</html>