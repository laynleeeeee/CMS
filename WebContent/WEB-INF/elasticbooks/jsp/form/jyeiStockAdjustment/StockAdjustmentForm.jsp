<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: JYEI Stock Adjustment form for Inventory Retail.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.serialitemunitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.unitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
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
$(document).ready(function() {
	if (typeof formatNumber == 'function') {
		formatNumber = null;
		computeGrandTotal = null;
	}
	if("${stockAdjustmentDto.stockAdjustment.id}" == 0) {
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
	}

	if("${stockAdjustmentDto.stockAdjustment.id}" > 0){
		computeTotal(true);
		computeTotal(false);
	}
});

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
			"handler" : new UnitCostTableHandler (new function () {
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
		{"title" : "Unit Cost",
			"cls" : "unitCost tblInputNumeric hdnColumn",
			"editor" : "text",
			"visible": true,
			"width" : "14%"},
		{"title" : "Total",
			"cls" : "amount tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "12%"},]
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
			"handler" : new UnitCostTableHandler (new function () {
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
			"visible": false}]
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
			"width" : "12%"},
		{"title" : "Unit Cost",
			"cls" : "unitCost tblInputNumeric hdnColumn",
			"editor" : "text",
			"visible": true,
			"width" : "14%"},
		{"title" : "Total",
			"cls" : "amount tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "12%"},
		{"title" : "warehouseId",
			"cls" : "warehouseId",
			"editor" : "hidden",
			"visible" : false},
		{"title" : "quantity",
			"cls" : "quantity hidden",
			"editor" : "hidden",
			"visible": false}]
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
			"width" : "14%"}]
	}
	return header;
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
                 {"name" : "quantity", "varType" : "int"},
                 {"name" : "serialNumber", "varType" : "string"},
                 {"name" : "unitCost", "varType" : "double"},
                 {"name" : "stockCode", "varType" : "string"},
                 {"name" : "origQty", "varType" : "double"},
                 {"name" : "warehouseId", "varType" : "int"},
                 {"name" : "amount", "varType" : "double"}],
		contextPath: cPath,
		"header": header,
		"disableDuplicateStockCode" : false,
		"itemTableMessage" : ""
	});

	$("#adjustmentItemTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#serializedAdjustmentItemTable").on("blur", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
			var qty = 1;
			if($("#isStockIn").val() != "true"){
				qty = -1;
			}
			$(this).closest("tr").find(".quantityTxt").text(qty);
			$(this).closest("tr").find(".quantity").val(qty);
			computeItemTotal(this);
		}
	});

	$("#serializedAdjustmentItemTable").on("blur", ".tblInputText", function(){
		if($.trim($(this).val()) != "") {
			var qty = 1;
			if($("#isStockIn").val() != "true"){
				qty = -1;
			}
			$(this).closest("tr").find(".quantityTxt").text(qty);
			$(this).closest("tr").find(".quantity").val(qty);
			computeItemTotal(this);
		}
	});

	if(isSerialized){
		$serializedItemTable = $itemTable;
	} else {
		$adjustmentItemTable = $itemTable;
	}
}

function computeItemTotal(elem) {
	var qty = 1;
	if($("#isStockIn").val() != "true"){
		qty = -1;
	}
	$(this).closest("tr").find(".quantityTxt").text(qty);
	$(this).closest("tr").find(".quantity").val(qty);
	var quantity = accounting.unformat($(elem).closest("tr").find(".quantity").val());
	var unitCost = accounting.unformat($(elem).closest("tr").find(".unitCost").val());
	$(elem).closest("tr").find(".amount").html(accounting.formatMoney(unitCost*quantity));
}

function companyIdOnChange () {
	onchangeWarehouse();
	filterWarehouses ();
	filterStockAdjustmentTypes();
}

function filterWarehouses(warehouseId, initializeDatatable) {
	var companyId = $("#companyId").val();
	if(companyId > 0) {
		var uri = contextPath+"/getWarehouse?companyId="+companyId;
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

	if(initializeDatatable) {
		initializeTable(true);
		initializeTable(false);
		computeTotal(true);
		computeTotal(false);
	}
}

function filterStockAdjustmentTypes(stockAdjustmentTypeId, initializeDatatable) {
	var companyId = $("#companyId").val();
	if(companyId > 0) {
		var uri = contextPath+"/getSAdjustmentTypes?companyId="+companyId;
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
		enableCompanyAndWarehouseId();
		$("#btnSaveStockAdjustment").attr("disabled", "disabled");
		doPostWithCallBack ("stockAdjustmentForm", "form", function(data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
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
				$("#spanSaNumber").text(spanSaNumber);
				$("#isStockIn").val(isStockIn);
				filterWarehouses(warehouseId, true);
				filterStockAdjustmentTypes(stockAdjustmentTypeId, true);
				isSaving = false;
			}
		});
		$("#btnSaveStockAdjustment").removeAttr("disabled");
	}
}

function computeTotal(isSerialize) {
	var itemTable = isSerialize ? "serializedAdjustmentItemTable" : "adjustmentItemTable";
	$("#"+itemTable+" tbody tr").each(function(row) {
		var qty = accounting.unformat($(this).closest("tr").find(".quantity").val());
		if(isSerialize && ($.trim($(this).closest("tr").find(".tblInputNumeric").val()) != "" || $.trim($(this).closest("tr").find(".tblInputText").val()) != "")){
			qty = 1;
			if($("#isStockIn").val() != "true"){
				qty = -1;
			}
			$(this).closest("tr").find(".quantityTxt").text(qty);
			$(this).closest("tr").find(".quantity").val(qty);
		}
		var uCost = accounting.unformat($(this).closest("tr").find(".unitCost").val());
		var $total = $(this).find(".amount");
		$total.text(accounting.formatMoney(qty * uCost));
	});
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
									${stockAdjustmentDto.stockAdjustment.formattedSANumber}</c:if></span></td>
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
				</table>
			</fieldset>
			<fieldset class="frmField_set hidden">
				<legend>Serialized Item Table</legend>
				<div id="serializedAdjustmentItemTable"></div>
				<table>
					<tr>
						<td>
							<span id="serializedItemErrors" class="error"></span>
							<form:errors path="siMessage" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Goods Table</legend>
				<div id="adjustmentItemTable"></div>
				<table>
					<tr>
						<td>
							<form:errors path="stockAdjustment.saMessage" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
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