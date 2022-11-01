<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: NSB Stock Adjustment form for Inventory Retail.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.nocomputationforquantity.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.serialitemunitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
.quantityTxt {
	float: right;
	width: 100%;
	text-align: right;
}
</style>
<script type="text/javascript">
var $adjustmentItemTable = null;
var $serializedItemTable = null;
var PREV_RATE = 0;
$(document).ready(function() {
	loadDivision("${stockAdjustmentDto.stockAdjustment.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	if (typeof formatNumber == 'function') {
		formatNumber = null;
		computeGrandTotal = null;
	}
	if ("${stockAdjustmentDto.stockAdjustment.id}" == 0) {
		filterWarehouses();
		filterStockAdjustmentTypes();
		initializeTable(true);
		initializeTable(false);
	} else {
		//Disable and set values
		disableAndSetCompany();
		filterWarehouses("${stockAdjustmentDto.stockAdjustment.warehouseId}", true);
		if("${stockAdjustmentDto.stockAdjustment.formWorkflow.complete}" == "true") {
			$("#stockAdjustmentForm :input").attr("disabled", true);
			$("#adjustmentItemTable").find("table tbody tr .delrow").attr("onclick", "");
			$("#warehouseId").append("<option selected='selected' value="+"${stockAdjustmentDto.stockAdjustment.warehouseId}"
					+">"+"${stockAdjustmentDto.stockAdjustment.warehouse.name}"+"</option>");
		}
		filterStockAdjustmentTypes("${stockAdjustmentDto.stockAdjustment.stockAdjustmentTypeId}", true);
		if("${stockAdjustment.formWorkflow.complete}" == "true") {
			$("#stockAdjustmentForm :input").attr("disabled", true);
			$("#adjustmentItemTable").find("table tbody tr .delrow").attr("onclick", "");
		}
		if ("${isStockIn}" == "true") {
			$("#currencyId").attr("disabled","disabled");
		}
	}

	if ("${stockAdjustmentDto.stockAdjustment.id}" > 0) {
		if ("${isStockIn}" == "true") {
			computeTotal(true);
			computeTotal(false);
		} else {
			computeOutTotal(false);
			computeOutTotal(true);
		}
	}
	initializeDocumentsTbl();
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

function getTblColumns(typeId) {
	var header = null;
	if(typeId == 1) {
		header = [{"title" : "id",
			"cls" : "id",
			"editor" : "hidden",
			"visible": false},
		{"title" : "stockAdjustmentId",
			"cls" : "stockAdjustmentId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "itemId",
			"cls" : "itemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origItemId",
			"cls" : "origItemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origUnitCost",
			"cls" : "origUnitCost",
			"editor" : "hidden",
			"visible": false},
		{"title" : "referenceObjectId",
			"cls" : "referenceObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origRefObjectId",
			"cls" : "origRefObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "ebObjectId",
			"cls" : "ebObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "typeId",
			"cls" : "typeId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "Stock Code",
			"cls" : "stockCode tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "12%",
			"handler" : new SerialItemUnitCostTblHandler (new function () {
				this.handleTotal = function (total) {
					// Do nothing
				};
			})},
		{"title" : "Description",
			"cls" : "description tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "10%"},
		{"title" : "Customer PO No.",
			"cls" : "poNumber tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "13%"},
		{"title" : "Existing <br> Stocks",
			"cls" : "existingStock tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
		{"title" : "UOM",
			"cls" : "uom spanUom tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
		{"title" : "origQty",
			"cls" : "origQty",
			"editor" : "hidden",
			"visible": false},
		{"title" : "Adjustment <br> Qty",
			"cls" : "quantity tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "10%"},
		{"title" : "Unit Cost",
			"cls" : "unitCost tblInputNumeric hdnColumn",
			"editor" : "text",
			"visible": true,
			"width" : "14%"},
		{"title" : "Total",
			"cls" : "amount tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "10%"},]
	} else if(typeId == 2) {
		header = [{"title" : "id",
			"cls" : "id",
			"editor" : "hidden",
			"visible": false},
		{"title" : "stockAdjustmentId",
			"cls" : "stockAdjustmentId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "itemId",
			"cls" : "itemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origItemId",
			"cls" : "origItemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origUnitCost",
			"cls" : "origUnitCost",
			"editor" : "hidden",
			"visible": false},
		{"title" : "referenceObjectId",
			"cls" : "referenceObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origRefObjectId",
			"cls" : "origRefObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "ebObjectId",
			"cls" : "ebObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "typeId",
			"cls" : "typeId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "Stock Code",
			"cls" : "stockCode tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "12%",
			"handler" : new SerialItemUnitCostTblHandler (new function () {
				this.handleTotal = function (total) {
					// Do nothing
				};
			})},
		{"title" : "Description",
			"cls" : "description tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "19%"},
		{"title" : "Existing <br> Stocks",
			"cls" : "existingStock tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
		{"title" : "UOM",
			"cls" : "uom spanUom tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
		{"title" : "Adjustment <br> Qty",
			"cls" : "quantity tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "12%"},
		{"title" : "origQty",
			"cls" : "origQty",
			"editor" : "hidden",
			"visible": false},
		{"title" : "Unit Cost",
			"cls" : "unitCost tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "14%"},
		{"title" : "Total",
			"cls" : "amount tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "12%"},]
	}
	return header;
}

function getSerializedTblColumns(typeId) {
	var header = null;
	if(typeId == 1) {
		header = [{"title" : "id",
			"cls" : "id",
			"editor" : "hidden",
			"visible": false},
		{"title" : "stockAdjustmentId",
			"cls" : "stockAdjustmentId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "itemId",
			"cls" : "itemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origItemId",
			"cls" : "origItemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origUnitCost",
			"cls" : "origUnitCost",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origRefObjectId",
			"cls" : "origRefObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "referenceObjectId",
			"cls" : "referenceObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "ebObjectId",
			"cls" : "ebObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "typeId",
			"cls" : "typeId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "Stock Code",
			"cls" : "stockCode tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "12%",
			"handler" : new SerialItemUnitCostTblHandler (new function () {
				this.handleTotal = function (total) {
					// Do nothing
				};
			})},
		{"title" : "Description",
			"cls" : "description tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "10%"},
		{"title" : "Customer PO No.",
			"cls" : "poNumber tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "13%"},
		{"title" : "Available <br> Stocks",
			"cls" : "existingStock tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
		{"title" : "Serial Number",
			"cls" : "serialNumber tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "12%"},
		{"title" : "UOM",
			"cls" : "uom spanUom tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
		{"title" : "Qty",
			"cls" : "quantityTxt tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "10%"},
		{"title" : "Unit Cost",
			"cls" : "unitCost tblInputNumeric hdnColumn",
			"editor" : "text",
			"visible": true,
			"width" : "12%"},
		{"title" : "Total",
			"cls" : "amount tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "12%"},
		{"title" : "warehouseId",
			"cls" : "warehouseId",
			"editor" : "hidden",
			"visible" : false},
		{"title" : "origQty",
			"cls" : "origQty",
			"editor" : "hidden",
			"visible": false},
		{"title" : "quantity",
			"cls" : "quantity hidden",
			"editor" : "hidden",
			"visible": false}
		]
	} else if(typeId == 2) {
		header = [{"title" : "id",
			"cls" : "id",
			"editor" : "hidden",
			"visible": false},
		{"title" : "stockAdjustmentId",
			"cls" : "stockAdjustmentId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "itemId",
			"cls" : "itemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origItemId",
			"cls" : "origItemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origUnitCost",
			"cls" : "origUnitCost",
			"editor" : "hidden",
			"visible": false},
		{"title" : "origRefObjectId",
			"cls" : "origRefObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "referenceObjectId",
			"cls" : "referenceObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "ebObjectId",
			"cls" : "ebObjectId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "typeId",
			"cls" : "typeId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "Stock Code",
			"cls" : "stockCode tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "12%",
			"handler" : new SerialItemUnitCostTblHandler (new function () {
				this.handleTotal = function (total) {
					// Do nothing
				};
			})},
		{"title" : "Description",
			"cls" : "description tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "19%"},
		{"title" : "Available <br> Stocks",
			"cls" : "existingStock tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
		{"title" : "Serial Number",
			"cls" : "serialNumber tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "12%"},
		{"title" : "UOM",
			"cls" : "uom spanUom tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
		{"title" : "Adjustment <br> Qty",
			"cls" : "quantityTxt tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "12%"},
		{"title" : "origQty",
			"cls" : "origQty",
			"editor" : "hidden",
			"visible": false},
		{"title" : "warehouseId",
			"cls" : "warehouseId",
			"editor" : "hidden",
			"visible" : false},
		{"title" : "quantity",
			"cls" : "quantity hidden",
			"editor" : "hidden",
			"visible": false},
		{"title" : "unitCost",
			"cls" : "unitCost",
			"editor" : "hidden",
			"visible": false,
			"width" : "14%"},
		{"title" : "Unit Cost",
			"cls" : "unitCost tblInputNumeric hdnColumn",
			"editor" : "label",
			"visible": true,
			"width" : "14%"},
		{"title" : "Total",
			"cls" : "amount tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "12%"}]
	}
	return header;
}

function getRItem($txtBox, stockCode) {
	var warehouseId = $("#warehouseId").val();
	var stockCode = $.trim(stockCode);
	var currencyId = $("#currencyId").val() != undefined ? $("#currencyId").val() : 1;
	var $row = $($txtBox).closest("tr");
	var origItemId = $row.find(".origItemId").val();
	var serialNumber = $($txtBox).closest("tr").find(".serialNumber").val();
	var isSerialized = typeof serialNumber != "undefined";
	if (stockCode != "" && warehouseId != "" && !isSerialized) {
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getItem?stockCode="+encodeURIComponent(stockCode)
				+"&warehouseId="+warehouseId;
		$.ajax({
			url: uri,
			success : function(item) {
				var tableId = $row.find(".id").val();
				if (tableId == 0 || tableId == "") {
					if (origItemId != item.id) {
						getWeightedAverageCost($txtBox, item.id, currencyId);
					}
				} else {
					if (origItemId != item.id) {
						getWeightedAverageCost($txtBox, item.id, currencyId);
					} else {
						var quantity = accounting.unformat($row.find(".quantity").val());
						var unitCost = accounting.unformat($row.find(".origUnitCost").val());
						$row.find(".unitCost").html(formatDecimalPlaces(unitCost, 6));
						$row.find(".amount").html(formatDecimalPlaces(Math.abs(quantity) * unitCost));
					}
				}
			}, error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function getWeightedAverageCost($txtBox, itemId, currencyId) {
	var $row = $($txtBox).closest("tr");
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	var uri = contextPath + "/getItem/getWeightedAverageWithRate?companyId="+companyId
			+"&warehouseId="+warehouseId+"&itemId="+itemId+"&currencyId="+currencyId;
	$.ajax({
		url: uri,
		success : function(unitCost) {
			var quantity = accounting.unformat($row.find(".quantity").val());
			$row.find(".unitCost").html(formatDecimalPlaces(unitCost, 6));
			$row.find(".amount").html(formatDecimalPlaces(Math.abs(quantity) * unitCost));
			if ("${isStockIn}" == "true") {
				computeTotal(true);
				computeTotal(false);
			} else {
				computeOutTotal(false);
				computeOutTotal(true);
			}
		},
		error : function(error) {
			console.log(error);
		}
	});
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

function initializeTable(isSerialized) {
	var header = !isSerialized ? getTblColumns("${typeId}") : getSerializedTblColumns("${typeId}");
	var itemsJson = !isSerialized ? JSON.parse($("#adjustmentItemsJson").val()) : JSON.parse($("#serialItemsJson").val());
	var tblName = !isSerialized ? "adjustmentItemTable" : "serializedAdjustmentItemTable";
	var cPath = "${pageContext.request.contextPath}";
	var $itemTable = $("#"+tblName).editableItem({
		data: itemsJson,
		jsonProperties: [
           {"name" : "id", "varType" : "int"},
           {"name" : "stockAdjustmentId", "varType" : "int"},
           {"name" : "referenceObjectId", "varType" : "int"},
           {"name" : "origRefObjectId", "varType" : "int"},
           {"name" : "ebObjectId", "varType" : "int"},
           {"name" : "typeId", "varType" : "int"},
           {"name" : "itemId", "varType" : "int"},
           {"name" : "origItemId", "varType" : "int"},
           {"name" : "origUnitCost", "varType" : "double"},
           {"name" : "quantity", "varType" : "int"},
           {"name" : "serialNumber", "varType" : "string"},
           {"name" : "unitCost", "varType" : "double"},
           {"name" : "stockCode", "varType" : "string"},
           {"name" : "origQty", "varType" : "double"},
           {"name" : "warehouseId", "varType" : "int"},
           {"name" : "poNumber", "varType" : "string"},
           {"name" : "amount", "varType" : "double"}
        ],
		contextPath: cPath,
		"header": header,
		"footer" : [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage" : ""
	});

	$("#adjustmentItemTable").on("focus", ".tblInputNumeric", function() {
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#adjustmentItemTable").on("focusout", ".quantity, .unitCost", function() {
		computeItemTotal(this, false);
	});

	$("#adjustmentItemTable").on("blur", ".stockCode, .quantity, .unitCost", function(){
		if ($.trim($(this).val()) != "") {
			if($("#isStockIn").val() != "true") {
				reloadRItems();
			}
		}
		computeTotalStockAdjAmount("adjustmentItemTable");
	});

	$("#serializedAdjustmentItemTable").on("blur", ".tblInputNumeric, .tblInputText", function() {
		var thisVal = $(this).val();
		if ($.trim(thisVal) != "") {
			if (!isNaN(thisVal)) {
				$(this).val(accounting.unformat($(this).val()));
			}
			var qty = $("#isStockIn").val() != "true" ? -1 : 1;
			$(this).closest("tr").find(".quantityTxt").text(qty);
			$(this).closest("tr").find(".quantity").val(qty);
		}
		if ($("#isStockIn").val() != "true") {
			reloadSRItems();
		}
		computeItemTotal(this, true);
	});

	if (isSerialized) {
		$serializedItemTable = $itemTable;
	} else {
		$adjustmentItemTable = $itemTable;
	}

	if ($("#isStockIn").val() != "true") {
		reloadSRItems();
		reloadRItems();
	}

	resizeTbl("adjustmentItemTable", $("#isStockIn").val() != "true" ? 8 : 9);
	resizeTbl("serializedAdjustmentItemTable", $("#isStockIn").val() != "true" ? 9 : 10);
}

function resizeTbl(tableId, rowCount) {
	var $tbl = $("#"+tableId);
	$($tbl).attr("width", "100%");
	addTotalLabel($tbl)
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function addTotalLabel($tbl){
	var $tr = $($tbl).find("tfoot tr td:nth-child(1)");
	$($tr).html("Total");
}

function computeTotalStockAdjAmount(tableId) {
	var totalAmount = 0;
	$("#"+tableId+" tbody tr").each(function(row) {
		var rowAmount = $(this).closest("tr").find(".amount").text();
		totalAmount += accounting.unformat(rowAmount);
	});
	$("#"+tableId).find("tfoot tr").find(".amount").html(formatDecimalPlaces(totalAmount));
}

function reloadSRItems() {
	$("#serializedAdjustmentItemTable tbody tr").each(function(row) {
		var $ssc = $(this).find(".stockCode");
		getRItem($ssc, $($ssc).val());
	});
}

function reloadRItems(){
	$("#adjustmentItemTable tbody tr").each(function(row) {
		var $sc = $(this).find(".stockCode");
		getRItem($sc, $($sc).val());
	});
}

function computeItemTotal(elem, isSerialized) {
	var isStockOut = $("#isStockIn").val() != "true";
	var quantity = accounting.unformat($(elem).closest("tr").find(".quantity").val());
	var unitCost = isStockOut ? accounting.unformat($(elem).closest("tr").find(".unitCost").text())
			: accounting.unformat($(elem).closest("tr").find(".unitCost").val());
	$(elem).closest("tr").find(".amount").html(accounting.formatMoney(Math.abs(quantity) * unitCost));
	if (isSerialized) {
		computeTotalStockAdjAmount("serializedAdjustmentItemTable");
	} else {
		computeTotalStockAdjAmount("adjustmentItemTable");
	}
}

function companyIdOnChange () {
	onchangeWarehouse();
	filterWarehouses();
	filterStockAdjustmentTypes();
}

function filterWarehouses(warehouseId, initializeDatatable) {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	if(companyId > 0) {
		var uri = contextPath+"/getWarehouse/new?companyId="+companyId+"&divisionId="+divisionId;
		if(warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
		}
		$("#warehouseId").empty();
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
					if(warehouseId != "")
						$("#warehouseId").val(warehouseId);
				}
		};
		loadPopulate (uri, false, null, "warehouseId", optionParser, postHandler);
	}

	if (initializeDatatable) {
		initializeTable(true);
		initializeTable(false);
		if ("${isStockIn}" == "true") {
			computeTotal(true);
			computeTotal(false);
		} else {
			computeOutTotal(false);
			computeOutTotal(true);
		}
	}
}

function filterStockAdjustmentTypes(stockAdjustmentTypeId, initializeDatatable) {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	if(companyId > 0) {
		var uri = contextPath+"/getSAdjustmentTypes?companyId="+companyId+"&divisionId="+divisionId;
		if(typeof stockAdjustmentTypeId == "undefined"){
			uri += "&activeOnly=true";
		}
		$("#stockAdjustmentTypeId").empty();
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
					if(warehouseId != "")
						$("#stockAdjustmentTypeId").val(stockAdjustmentTypeId);
				}
		};

		loadPopulate (uri, false, null, "stockAdjustmentTypeId", optionParser, postHandler);
	}
}

var isSaving = false;
function saveStockAdjustment() {
	if(isSaving == false) {
		isSaving = true;
		$("#adjustmentItemsJson").val($adjustmentItemTable.getData());
		$("#serialItemsJson").val($serializedItemTable.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		enableCompanyAndWarehouseId();
		$("#btnSaveStockAdjustment").attr("disabled", "disabled");
		$("#divisionId").removeAttr("disabled");
		if ("${isStockIn}" == "true") {
			$("#currencyId").removeAttr("disabled");
		}
		doPostWithCallBack ("stockAdjustmentForm", "form", function(data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var warehouseId = $("#warehouseId").val();
				var spanSaNumber = $("#spanSaNumber").text();
				var isStockIn = $("#isStockIn").val();
				var stockAdjustmentTypeId = $("#stockAdjustmentTypeId").val();
				if("${stockAdjustmentDto.stockAdjustment.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#spanFormStatus").text("NEW");
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableAndSetCompany();
				}
				$("#spanDivisionLbl").text(spanDivisionLbl);
				loadDivision("${stockAdjustmentDto.stockAdjustment.divisionId}");
				$("#spanSaNumber").text(spanSaNumber);
				$("#isStockIn").val(isStockIn);
				filterWarehouses(warehouseId, true);
				if ("${stockAdjustmentDto.stockAdjustment.id}" == 0) {
					filterStockAdjustmentTypes();
				} else {
					filterStockAdjustmentTypes(stockAdjustmentTypeId, true);
				}
				initializeDocumentsTbl();
				$("#divisionId").attr("disabled","disabled");
				if("${stockAdjustmentDto.stockAdjustment.id}" != 0 && "${isStockIn}" == "true") {
					$("#currencyId").attr("disabled","disabled");
				}
				isSaving = false;
			}
		});
		$("#btnSaveStockAdjustment").removeAttr("disabled");
	}
}

function computeTotal(isSerialize) {
	var itemTable = isSerialize ? "serializedAdjustmentItemTable" : "adjustmentItemTable";
	var totalAmount = 0;
	$("#"+itemTable+" tbody tr").each(function(row) {
		var qty = accounting.unformat($(this).closest("tr").find(".quantity").val());
		if (isSerialize && ($.trim($(this).closest("tr").find(".tblInputNumeric").val()) != ""
				|| $.trim($(this).closest("tr").find(".tblInputText").val()) != "")) {
			$(this).closest("tr").find(".quantityTxt").text(qty);
			$(this).closest("tr").find(".quantity").val(qty);
		}
		var uCost = accounting.unformat($(this).closest("tr").find(".unitCost").val());
		var $total = $(this).find(".amount");
		totalAmount += (qty * uCost);
		$total.text(accounting.formatMoney(qty * uCost));
	});
	$("#"+itemTable+" tfoot tr").find(".amount").html(formatDecimalPlaces(totalAmount));
}

function computeOutTotal(isSerialize) {
	var itemTable = isSerialize ? "serializedAdjustmentItemTable" : "adjustmentItemTable";
	var totalAmount = 0;
	$("#"+itemTable+" tbody tr").each(function(row) {
		var qty = accounting.unformat($(this).closest("tr").find(".quantity").val());
		if (isSerialize && ($.trim($(this).closest("tr").find(".tblInputNumeric").val()) != ""
				|| $.trim($(this).closest("tr").find(".tblInputText").val()) != "")) {
			$(this).closest("tr").find(".quantityTxt").text(qty);
			$(this).closest("tr").find(".quantity").val(qty);
		}
		var uCost = accounting.unformat($(this).closest("tr").find(".unitCost").text());
		var $total = $(this).find(".amount");
		totalAmount += (Math.abs(qty) * uCost);
		$total.html(accounting.formatMoney(Math.abs(qty) * uCost));
	});
	$("#"+itemTable+" tfoot tr").find(".amount").html(formatDecimalPlaces(totalAmount));
}

function enableCompanyAndWarehouseId() {
	//Disable  company and warehouse
	$("#companyId").attr("disabled",false);	
	$("#warehouseId").attr("disabled",false);
}

function disableAndSetCompany() {
	//Disable  company and warehouse
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${stockAdjustmentDto.stockAdjustment.companyId}"+"'>"+
			"${stockAdjustmentDto.stockAdjustment.company.numberAndName}"+"</option>");
	if("${typeId}" > 1) {
		$("#warehouseId").attr("disabled","disabled");
	}
}

function onchangeWarehouse() {
	$("#serializedAdjustmentItemTable").html("");
	$("#adjustmentItemTable").html("");
	initializeTable(true);
	initializeTable(false);
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
	if ($("#isStockIn").val() == "true") {
		$("#serializedAdjustmentItemTable tbody tr").each(function(row) {
			var unitCost = accounting.unformat($(this).find(".unitCost").val());
			unitCost = (unitCost * (PREV_RATE != 0 ? PREV_RATE : 1.0));
			$(this).find(".unitCost").val(formatDecimalPlaces((unitCost / rate), 6));
			computeTotal(true);
		});
	
		$("#adjustmentItemTable tbody tr").each(function(row) {
			var unitCost = accounting.unformat($(this).find(".unitCost").val());
			unitCost = (unitCost * (PREV_RATE != 0 ? PREV_RATE : 1.0));
			$(this).find(".unitCost").val(formatDecimalPlaces((unitCost / rate), 6));
			computeTotal(false);
		});
	} else {
		$("#serializedAdjustmentItemTable tbody tr").each(function(row) {
			var unitCost = accounting.unformat($(this).find(".unitCost").text());
			unitCost = (unitCost * (PREV_RATE != 0 ? PREV_RATE : 1.0));
			$(this).find(".unitCost").html(formatDecimalPlaces((unitCost / rate), 6));
			computeOutTotal(true);
		});
		$("#adjustmentItemTable tbody tr").each(function(row) {
			var unitCost = accounting.unformat($(this).find(".unitCost").text());
			unitCost = (unitCost * (PREV_RATE != 0 ? PREV_RATE : 1.0));
			$(this).find(".unitCost").html(formatDecimalPlaces((unitCost / rate), 6));
			computeOutTotal(false);
		});
	}
	PREV_RATE = rate;
}

</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="stockAdjustmentDto" id="stockAdjustmentForm">
		<div class="modFormLabel">Stock Adjustment 
			<c:choose>
				<c:when test="${typeId == 1}"> - IN</c:when>
				<c:otherwise> - OUT</c:otherwise>
			</c:choose>
			<span id="spanDivisionLbl"> - ${stockAdjustmentDto.stockAdjustment.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span>
		</div>
		<form:hidden path="stockAdjustment.id"/>
		<form:hidden path="stockAdjustment.ebObjectId" id="hdnRefObjectId"/>
		<form:hidden path="stockAdjustment.createdBy"/>
		<form:hidden path="stockAdjustment.createdDate"/>
		<form:hidden path="stockAdjustment.formWorkflowId"/>
		<form:hidden path="stockAdjustment.saNumber"/>
		<form:hidden path="stockAdjustment.adjustmentItemsJson" id="adjustmentItemsJson"/>
		<form:hidden path="serialItemsJson" id="serialItemsJson"/>
		<form:hidden path="stockAdjustment.currencyRateId" id="hdnCurrRateId"/>
		<form:hidden path="stockAdjustment.currencyRateValue" id="hdnCurrRateValue"/>
		<form:hidden path="stockAdjustment.referenceDocumentsJson" id="referenceDocumentsJson"/>
		<input type="hidden" id="isStockIn" value="${isStockIn}"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">SA
							<c:choose>
								<c:when test="${typeId == 1}">I</c:when>
								<c:otherwise>O</c:otherwise>
							</c:choose>No.</td>
						<td class="value"><span id="spanSaNumber">
							<c:if test="${stockAdjustmentDto.stockAdjustment.id > 0}">
									${stockAdjustmentDto.stockAdjustment.saNumber}</c:if></span></td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:choose>
								<c:when test="${stockAdjustmentDto.stockAdjustment.id > 0}">
									${stockAdjustmentDto.stockAdjustment.formWorkflow.currentFormStatus.description}
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
				<legend>Stock Adjustment Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Company</td>
						<td class="value">
							<form:select path="stockAdjustment.companyId" id="companyId" cssClass="frmSelectClass" onchange="companyIdOnChange();"
								items="${companies}" itemLabel="numberAndName" itemValue="id" >
							</form:select>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="stockAdjustment.companyId" cssClass="error"/></td>
					</tr>
					<tr>
							<td class="labels">* Division</td>
							<td class="value">
								<form:select path="stockAdjustment.divisionId" id="divisionId" class="frmSelectClass"></form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="stockAdjustment.divisionId" cssClass="error" />
							</td>
						</tr>
					<tr>
						<td class="labels">* Warehouse</td>
						<td class="value">
							<form:select path="stockAdjustment.warehouseId" id="warehouseId" onchange="onchangeWarehouse();" class="frmSelectClass">
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="stockAdjustment.warehouseId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Adjustment Type</td>
						<td class="value">
							<form:select path="stockAdjustment.stockAdjustmentTypeId" id="stockAdjustmentTypeId" class="frmSelectClass">
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="stockAdjustment.stockAdjustmentTypeId" cssClass="error"/></td>
					</tr>
					<tr>
							<td class="labels">BMS No</td>
							<td class="value"><form:input path="stockAdjustment.bmsNumber" class="inputMedium" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="stockAdjustment.bmsNumber" cssClass="error"/></td>
						</tr>
					<tr>
						<td class="labels">* Date </td>
						<td class="value"><form:input path="stockAdjustment.saDate" id="saDate" onblur="evalDate('saDate')"
							style="width: 120px;" class="dateClass2" />
							<img id="imgDate3" src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('saDate')" style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="stockAdjustment.saDate" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels" align="right">Remarks</td>
						<td  class="value" colspan="2"><form:textarea path="stockAdjustment.remarks" rows="3"
									style="width: 350px; resize: none;" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="stockAdjustment.remarks" cssClass="error"/></td>
					</tr>
					<c:if test="${typeId == 1}">
						<tr>
							<td class="labels">* Currency</td>
							<td class="value">
								<form:select path="stockAdjustment.currencyId" id="currencyId" cssClass="frmMediumSelectClass"
									onchange="getCurrencyRate(this);">
									<form:options items="${currencies}" itemValue="id" itemLabel="name"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="stockAdjustment.currencyId" cssClass="error"/></td>
						</tr>
					</c:if>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Serialized Good/s</legend>
				<div id="serializedAdjustmentItemTable"></div>
				<table>
					<tr>
						<td>
							<span id="serializedItemErrors" class="error"></span>
							<span id="invalidSerialItemErrorMsg" class="error"></span>
							<form:errors path="siMessage" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Non-Serialized Good/s</legend>
				<div id="adjustmentItemTable"></div>
				<table>
					<tr>
						<td>
							<form:errors path="stockAdjustment.saMessage" cssClass="error"/>
						</td>
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
					<td><form:errors path="stockAdjustment.referenceDocsMessage" cssClass="error" /></td>
				</tr>
				<tr>
					<td><span class="error" id="referenceDocsMgs"></span></td>
				</tr>
				<tr>
					<td><form:errors path="stockAdjustment.formWorkflowId" cssClass="error" /></td>
				</tr>
			</table>
			<table class="frmField_set">
				<tr>
					<td align="right"><input type="button" id="btnSaveStockAdjustment"
							value="Save" onclick="saveStockAdjustment();" /></td>
				</tr>
			</table>
		</div>
		<hr class="thin" />
	</form:form>
</div>
</body>
</html>