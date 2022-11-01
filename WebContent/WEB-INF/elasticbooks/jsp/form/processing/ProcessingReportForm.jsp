<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Cash Sales form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.processinghandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<style type="text/css">
.textBoxLabel {
	border: none;
}
</style>
<script type="text/javascript">
var selectCompany = "${processingReport.companyId}";
var objectId = "${processingReport.id}";

$(document).ready (function () {
	$("#hdnObjectId").val(Number(objectId));
	if ("${processingReport.formWorkflow.complete}" == "true"
			|| Number("${processingReport.formWorkflow.currentStatusId}") == 4) {
		$("#processingReportForm :input").attr("disabled","disabled");
	}

	disableCompany();
	initializeTable ();
});

function enableCompany() {
	//Enable  company
	$("#companyId").attr("disabled",false);
}

function disableCompany() {
	//Disable  company
	if ("${processingReport.id}" != 0) {
		$("#companyId").attr("disabled","disabled");
		$("#companyId").append("<option selected='selected' value='"+"${processingReport.companyId}"+"'>"+
				"${processingReport.company.numberAndName}"+"</option>");
	}
}

function initRMItems () {
	var rawMaterialsJson =JSON.parse($("#rawMaterialsJson").val());
	var cPath = "${pageContext.request.contextPath}";

	$rawMaterialsTable = $("#rawMaterialsTable").editableItem({
		data: rawMaterialsJson,
		jsonProperties: [
		                 {"name" : "id", "varType" : "int"},
		                 {"name" : "processingReportId", "varType" : "int"},
		                 {"name" : "warehouseId", "varType" : "int"},
		                 {"name" : "itemId", "varType" : "int"},
		                 {"name" : "stockCode", "varType" : "string"},
		                 {"name" : "itemBagQuantity", "varType" : "double"},
		                 {"name" : "quantity", "varType" : "double"},
		                 {"name" : "amount", "varType" : "double"},
		                 {"name" : "availableStock", "varType" : "double"},
		                 {"name" : "availableStocks", "varType" : "double"},
		                 {"name" : "origQty", "varType" : "double"},
		                 {"name" : "unitCost", "varType" : "double"},
		                 {"name" : "referenceObjectId", "varType" : "int"},
		                 {"name" : "origRefObjectId", "varType" : "int"},
		                 {"name" : "refId", "varType" : "int"},
		                 {"name" : "ebObjectId", "varType" : "int"}
		                 ],
		contextPath: cPath,
		header: [
			{"title" : "id", 
				"cls" : "id", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "itemId", 
				"cls" : "itemId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "cashSaleId", 
				"cls" : "cashSaleId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "processingReportId", 
				"cls" : "processingReportId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code", 
				"cls" : "stockCode tblInputText", 
				"editor" : "text",
				"visible" : true,
				"width" : "8%", 
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						//Do nothing
					};
				})},
			{"title" : "Description", 
				"cls" : "description tblLabelText", 
				"editor" : "label",
				"visible" : true, 
				"width" : "12%"},
			{"title" : "warehouseId", 
				"cls" : "warehouseId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Warehouse", 
				"cls" : "warehouse tblSelectClass", 
				"editor" : "select",
				"visible" : true ,
				"width" : "10%"},
			{"title" : "Available <br> Stocks", 
				"cls" : "availableStock tblSelectClass", 
				"editor" : "select",
				"visible" : true, 
				"width" : "18%"},
			{"title" : "availableStocks", 
				"cls" : "availableStocks", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Bags",
				"cls" : "itemBagQuantity tblInputNumeric",
				"editor" : "text",
				"visible": true,
				"width" : "6%"},
			{"title" : "Qty", 
				"cls" : "quantity tblInputNumeric", 
				"editor" : "text",
				"visible" : true,
				"width" : "6%" },
			{"title" : "UOM", 
				"cls" : "uom txtUOM", 
				"editor" : "label",
				"visible" : true,
				"width" : "6%" },
			{"title" : "origQty", 
				"cls" : "origQty", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "referenceObjectId", 
				"cls" : "referenceObjectId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "refId", 
				"cls" : "refId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId", 
				"cls" : "ebObjectId", 
				"editor" : "hidden",
				"visible" : false}
		],
		"footer" : [],
	    "disableDuplicateStockCode" : false
	});

	$("#rawMaterialsTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#rawMaterialsTable").on("change", ".warehouse", function(){
		var itemId = $(this).closest("tr").find(".itemId").val();
		var refObjectId = $(this).closest("tr").find(".referenceObjectId").val();
		var ebObjectId = $(this).closest("tr").find(".ebObjectId").val();

		populateAvailableStock ($(this), $(this).val(), itemId, refObjectId, "${processingReport.id}", ebObjectId);
	});

	$("#rawMaterialsTable").on("change", ".availableStock", function(){
		setReferenceDetails($(this));
	});

	$("#rawMaterialsTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateQty($tr, $("#companyId").val());
	});
}

function initOMItems () {
	var otherMaterialsJson =JSON.parse($("#otherMaterialsJson").val());
	var cPath = "${pageContext.request.contextPath}";

	$otherMaterialsTable = $("#otherMaterialsTable").editableItem({
		data: otherMaterialsJson,
		jsonProperties: [
		                 {"name" : "id", "varType" : "int"},
		                 {"name" : "processingReportId", "varType" : "int"},
		                 {"name" : "warehouseId", "varType" : "int"},
		                 {"name" : "itemId", "varType" : "int"},
		                 {"name" : "stockCode", "varType" : "string"},
		                 {"name" : "itemBagQuantity", "varType" : "double"},
		                 {"name" : "quantity", "varType" : "double"},
		                 {"name" : "amount", "varType" : "double"},
		                 {"name" : "availableStock", "varType" : "double"},
		                 {"name" : "availableStocks", "varType" : "double"},
		                 {"name" : "origQty", "varType" : "double"},
		                 {"name" : "unitCost", "varType" : "double"},
		                 {"name" : "referenceObjectId", "varType" : "int"},
		                 {"name" : "origRefObjectId", "varType" : "int"},
		                 {"name" : "refId", "varType" : "int"},
		                 {"name" : "ebObjectId", "varType" : "int"},
		                 ],
		contextPath: cPath,
		header: [
			{"title" : "id", 
				"cls" : "id", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "itemId", 
				"cls" : "itemId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "cashSaleId",
				"cls" : "cashSaleId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "processingReportId",
				"cls" : "processingReportId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code",
				"cls" : "stockCode tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						//Do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true, 
				"width" : "12%"},
			{"title" : "warehouseId",
				"cls" : "warehouseId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Warehouse", 
				"cls" : "warehouse tblSelectClass",
				"editor" : "select",
				"visible" : true ,
				"width" : "10%"},
			{"title" : "Available <br> Stocks",
				"cls" : "availableStock tblSelectClass",
				"editor" : "select",
				"visible" : true, 
				"width" : "18%"},
			{"title" : "availableStocks",
				"cls" : "availableStocks",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Bags",
				"cls" : "itemBagQuantity tblInputNumeric",
				"editor" : "text",
				"visible": true,
				"width" : "6%"},
			{"title" : "Qty", 
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "6%" },
			{"title" : "UOM", 
				"cls" : "uom txtUOM",
				"editor" : "label",
				"visible" : true,
				"width" : "6%" },
			{"title" : "origQty", 
				"cls" : "origQty", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "referenceObjectId", 
				"cls" : "referenceObjectId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "refId", 
				"cls" : "refId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId", 
				"cls" : "ebObjectId", 
				"editor" : "hidden",
				"visible" : false},
		],
		"footer" : [],
	    "disableDuplicateStockCode" : false
	});

	$("#otherMaterialsTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherMaterialsTable").on("change", ".warehouse", function(){
		var itemId = $(this).closest("tr").find(".itemId").val();
		var refObjectId = $(this).closest("tr").find(".referenceObjectId").val();
		var ebObjectId = $(this).closest("tr").find(".ebObjectId").val();

		populateAvailableStock ($(this), $(this).val(), itemId, refObjectId, "${processingReport.id}", ebObjectId);
	});

	$("#otherMaterialsTable").on("change", ".availableStock", function(){
		setReferenceDetails($(this));
	});

	$("#otherMaterialsTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateQty($tr, $("#companyId").val());
	});
}

function initOtherCharges() {
	var prOtherChargesJson = JSON.parse($("#prOtherChargesJson").val());

	var cPath = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: prOtherChargesJson,
		jsonProperties: [
	               {"name" : "id", "varType" : "int"},
	               {"name" : "arLineSetupId", "varType" : "int"},
	               {"name" : "unitOfMeasurementId", "varType" : "int"},
	               {"name" : "quantity", "varType" : "int"},
	               {"name" : "upAmount", "varType" : "double"},
	               {"name" : "amount", "varType" : "double"},
	               {"name" : "arLineSetupName", "varType" : "string"},
	               {"name" : "unitMeasurementName", "varType" : "string"}
				],
		contextPath: cPath,
		header:[
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "arLineSetupId",
					"cls" : "arLineSetupId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "unitOfMeasurementId",
					"cls" : "unitOfMeasurementId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Charge",
					"cls" : "arLineSetupName tblInputText",
					"editor" : "text",
					"visible" : true,
					"width" : "25%",
					"handler" : new SalesTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Qty",
					"cls" : "quantity tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "15%" },
				{"title" : "UOM",
					"cls" : "unitMeasurementName tblInputText",
					"editor" : "text",
					"visible" : true,
					"width" : "20%",
					"handler" : new SalesTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "UP",
					"cls" : "upAmount tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "20%"},
				{"title" : "Amount",
					"cls" : "amount tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "20%"}
				],
				"footer" : [
					{"cls" : "amount"}
				],
				"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function(){
		var amount = $(this).closest("tr").find(".amount").val();
		updateOChargesAmt(amount);
	});

	$("#otherChargesTable").on("keyup", ".arLineSetupName", function(){
		console.log("Showing the list of AR Line Setups.");
		var currentALSetup = null;
		var companyId = $("#companyId").val();
		var arLineSetupName = encodeURIComponent($.trim($(this).val()));
		var uri = contextPath+"/getArLineSetups/byCompany?companyId="+companyId+"&name="+arLineSetupName;
		$($(this)).autocomplete({
			source: uri,
			select: function( event, ui ) {
				currentALSetup = ui.item;
				$(this).val (ui.item.name);
				//No validation for duplicate AR Line Setup
				var $alSetupId = $(this).closest("tr").find(".arLineSetupId");
				$($alSetupId).val(ui.item.id);
				return false;
			}, minLength: 1,
			change: function(event, ui){
				var setupName = $(this).val();
				if (currentALSetup != null && setupName == currentALSetup.name){
					return false;
				}
				var $alSetupId = $(this).closest("tr").find(".arLineSetupId");
				$($alSetupId).val(ui.item.id);
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, alSetup ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", alSetup )
				.append( "<a style='font-size: small;'>"+ alSetup.name + "</a>" )
				.appendTo( ul );
		};
	});
}

function initMainProducts() {
	var prMainProductJson =JSON.parse($("#prMainProductJson").val());
	var cPath = "${pageContext.request.contextPath}";

	$mainProductTable = $("#mainProductTable").editableItem({
		data: prMainProductJson,
		jsonProperties: [
		                 {"name" : "id", "varType" : "int"},
		                 {"name" : "processingReportId", "varType" : "int"},
		                 {"name" : "warehouseId", "varType" : "int"},
		                 {"name" : "itemId", "varType" : "int"},
		                 {"name" : "stockCode", "varType" : "string"},
		                 {"name" : "itemBagQuantity", "varType" : "double"},
		                 {"name" : "quantity", "varType" : "double"},
		                 {"name" : "amount", "varType" : "double"},
		                 {"name" : "origQty", "varType" : "double"},
		                 {"name" : "unitCost", "varType" : "double"},
		                 {"name" : "ebObjectId", "varType" : "int"},
		                 {"name" : "referenceObjectId", "varType" : "int"},
		                 {"name" : "origRefObjectId", "varType" : "int"}
		                 ],
		contextPath: cPath,
		header: [
			{"title" : "id", 
				"cls" : "id", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "cashSaleId",
				"cls" : "cashSaleId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "processingReportId",
				"cls" : "processingReportId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code", 
				"cls" : "stockCode tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "13%", 
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						//Do nothing
					};
				})},
			{"title" : "Description", 
				"cls" : "description tblLabelText", 
				"editor" : "label",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "warehouseId", 
				"cls" : "warehouseId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Warehouse", 
				"cls" : "warehouse tblSelectClass", 
				"editor" : "select",
				"visible" : true ,
				"width" : "10%"},
			{"title" : "Bags",
				"cls" : "itemBagQuantity tblInputNumeric",
				"editor" : "text",
				"visible": true,
				"width" : "12%"},
			{"title" : "Qty", 
				"cls" : "quantity tblInputNumeric", 
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "UOM", 
				"cls" : "uom txtUOM", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "origQty", 
				"cls" : "origQty", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId", 
				"cls" : "ebObjectId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "referenceObjectId", 
				"cls" : "referenceObjectId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible": false}
		],
		"footer" : [],
	    "disableDuplicateStockCode" : false
	});

	$("#mainProductTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#mainProductTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateProcessedQty($tr);
	});
}

function calculateProcessedQty($tr) {
	var bagQty = $($tr).find(".itemBagQuantity").val();
	if(bagQty != null && bagQty != "") {
		var qty = bagQty * 50;
		var $qty = $($tr).find(".quantity");
		if($($qty).val() == "") {
			$($qty).val(qty);
		}
	}
}

function initByProducts() {
	var prByProductJson =JSON.parse($("#prByProductJson").val());
	var cPath = "${pageContext.request.contextPath}";

	$byProductTable = $("#byProductTable").editableItem({
		data: prByProductJson,
		jsonProperties: [
		                 {"name" : "id", "varType" : "int"},
		                 {"name" : "processingReportId", "varType" : "int"},
		                 {"name" : "warehouseId", "varType" : "int"},
		                 {"name" : "itemId", "varType" : "int"},
		                 {"name" : "stockCode", "varType" : "string"},
		                 {"name" : "itemBagQuantity", "varType" : "double"},
		                 {"name" : "quantity", "varType" : "double"},
		                 {"name" : "amount", "varType" : "double"},
		                 {"name" : "origQty", "varType" : "double"},
		                 {"name" : "unitCost", "varType" : "double"},
		                 {"name" : "ebObjectId", "varType" : "int"},
		                 {"name" : "referenceObjectId", "varType" : "int"},
		                 {"name" : "origRefObjectId", "varType" : "int"}
		                 ],
		contextPath: cPath,
		header: [
			{"title" : "id", 
				"cls" : "id", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "cashSaleId", 
				"cls" : "cashSaleId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "processingReportId", 
				"cls" : "processingReportId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code", 
				"cls" : "stockCode tblInputText", 
				"editor" : "text",
				"visible" : true,
				"width" : "13%", 
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						//Do nothing
					};
				})},
			{"title" : "Description", 
				"cls" : "description tblLabelText", 
				"editor" : "label",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "warehouseId", 
				"cls" : "warehouseId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Warehouse", 
				"cls" : "warehouse tblSelectClass", 
				"editor" : "select",
				"visible" : true ,
				"width" : "10%"},
			{"title" : "Bags",
				"cls" : "itemBagQuantity tblInputNumeric",
				"editor" : "text",
				"visible": true,
				"width" : "12%"},
			{"title" : "Qty", 
				"cls" : "quantity tblInputNumeric", 
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "UOM", 
				"cls" : "uom txtUOM", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "origQty", 
				"cls" : "origQty", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId", 
				"cls" : "ebObjectId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "referenceObjectId", 
				"cls" : "referenceObjectId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible": false},
		],
		"footer" : [
				
	    ],
	    "disableDuplicateStockCode" : false
	});

	$("#byProductTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#byProductTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateProcessedQty($tr);
	});
}

function initializeTable() {
	initRMItems();
	initOMItems();
	initOtherCharges();
	initMainProducts();
	initByProducts();
}

function updateTotal (totalAmount) {
	$("#totalAmount").val(accounting.unformat(totalAmount));
}

var isSaving = false;
function saveProcessingReport() {
	if(isSaving == false) {
		isSaving = true;
		enableCompany();
		$("#rawMaterialsJson").val($rawMaterialsTable.getData());
		$("#otherMaterialsJson").val($otherMaterialsTable.getData());
		$("#prOtherChargesJson").val($otherChargesTable.getData());
		$("#prMainProductJson").val($mainProductTable.getData());
		$("#prByProductJson").val($byProductTable.getData());

		doPostWithCallBack ("processingReportForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				if("${processingReport.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#companyId").val(selectCompany);
					disableCompany();
				}

				initializeTable();
				isSaving = false;
			}
			$("#btnSaveProcessReport").removeAttr("disabled");
		});
	}
}

function handleCompanyOnChange() {
	$rawMaterialsTable.emptyTable();
	$otherMaterialsTable.emptyTable();
	$mainProductTable.emptyTable();
	$byProductTable.emptyTable();
	$otherChargesTable.emptyTable();
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="processingReport" id="processingReportForm">
			<div class="modFormLabel">
				Processing - ${processingType}
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<input type="hidden" value="2" id="hdnOrTypeId">
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNo"/>
			<form:hidden path="processingReportTypeId"/>
			<form:hidden path="rawMaterialsJson" id="rawMaterialsJson"/>
			<form:hidden path="otherMaterialsJson" id="otherMaterialsJson"/>
			<form:hidden path="prOtherChargesJson" id="prOtherChargesJson"/>
			<form:hidden path="prMainProductJson" id="prMainProductJson"/>
			<form:hidden path="prByProductJson" id="prByProductJson"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence No.</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${processingReport.sequenceNo}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${processingReport.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtCashSalesStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Processing Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass" onchange="handleCompanyOnChange();"
									items="${companies}" itemLabel="numberAndName" itemValue="id" >
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="companyId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="date" id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('date')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="date" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Ref No.</td>
							<td class="value"><form:input path="refNumber" id="refNumber" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="refNumber" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">Remarks</td>
							<td class="value"><form:textarea path="remarks" id="remarks" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="remarks" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						
					</table>
				</fieldset>
				<br>
				<fieldset class="frmField_set">
				<legend>Main Raw Materials</legend>
				<div id="rawMaterialsTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="rmMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errRMItems" cssClass="error"/>
						</td>
					</tr>
				</table>
				</fieldset>

				<br>
				<fieldset class="frmField_set">
				<legend>Other Materials</legend>
				<div id="otherMaterialsTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="omMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errOMItems" cssClass="error"/>
						</td>
					</tr>
				</table>
				</fieldset>

				<!-- Other Charges -->
				<br>
				<fieldset class="frmField_set">
				<legend>Other Charges</legend>
				<div id="otherChargesTable"></div>
				<input type="hidden" id="hdnOChargesAmt">
				<table>
					<tr>
						<td>
							<span id="otherChargesMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td>
							<form:errors path="errOtherCharges" cssClass="error"/>
						</td>
					</tr>
				</table>
				</fieldset>

				<!-- Main Product -->
				<br>
				<fieldset class="frmField_set">
				<legend>Main Product</legend>
				<div id="mainProductTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="mpMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errMainProduct" cssClass="error"/>
						</td>
					</tr>
				</table>
				</fieldset>

				<!-- By Product -->
				<br>
				<fieldset class="frmField_set">
				<legend>By Product</legend>
				<div id="byProductTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="bpMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errByProduct" cssClass="error"/>
						</td>
					</tr>
				</table>
				</fieldset>

				<!-- Grand Total -->
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSaveProcessReport" value="Save" onclick="saveProcessingReport();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>