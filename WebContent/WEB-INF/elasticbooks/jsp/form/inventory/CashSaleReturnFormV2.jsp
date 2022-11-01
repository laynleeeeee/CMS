
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Cash Sales Return form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.saleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<style type="text/css">
.disableSelect{
	pointer-events: none;
	cursor: default;
}

.textBoxLabel {
	border: none;
}
</style>
<script type="text/javascript">
var $cashSaleReturnTable = null;
var csrReferenceWindow = null;
var csrObj = null;
var sellingPriceHeader = null;
$(document).ready (function () {
	var wtAcctSettingId = "";
	var sellingTypeId = "${cashSaleReturn.cashSaleTypeId}";
	sellingPriceHeader = sellingTypeId == 2 ? "WP" : "SRP";
	if ("${cashSaleReturn.id}" != 0) { 
		wtAcctSettingId = "${cashSaleReturn.wtAcctSettingId}";
		loadHeader();
		initializeOtherChargesTbl(JSON.parse($("#csrArLinesJson").val()));
		initializeTable(sellingPriceHeader);
		disableFields();
		disableTableFields();
	}

	if ("${cashSaleReturn.formWorkflow.complete}" == "true"
			|| "${cashSaleReturn.formWorkflow.currentStatusId}" == 4) {
		$("#cashSalesReturnForm :input").attr("disabled","disabled");
	}

	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
});

function loadWtaxAcctSettings(wtAcctSettingId) {
	var companyId = $("#companyId").val();
	if (companyId != null && typeof companyId != "undefined" && companyId != "") {
		var $select = $("#wtaxAcctSettingId");
		$select.empty();
		var uri = contextPath+"/getWithholdingTaxAcct?companyId="+companyId+"&isCreditable=true";
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
	updateOChargesAmt();
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
				$("#cashSaleReturnTable tbody tr").each(function(row) {
					totalAmount += accounting.unformat($(this).find(".discount").html());
				});
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
		$("#wtaxAcctSettingId").attr("disabled", "disabled");
	}
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeWtax();
}

function initializeTable (sellingPriceHeader) {
	var cashSaleReturnItemsJson = JSON.parse($("#cashSaleReturnItemsJson").val());
	setupTableData(sellingPriceHeader, cashSaleReturnItemsJson);
}

function setupTableData (sellingPriceHeader, cashSaleReturnItemsJson) {
	var cPath = "${pageContext.request.contextPath}";
	$cashSaleReturnTable = $("#cashSaleReturnTable").editableItem({
		data: cashSaleReturnItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "cashSaleReturnId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
			{"name" : "referenceObjectId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "itemSrpId", "varType" : "int"},
			{"name" : "itemDiscountId", "varType" : "int"},
			{"name" : "itemAddOnId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "srp", "varType" : "double"},
			{"name" : "origSrp", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "addOn", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "cashSaleItemId", "varType" : "int"},
			{"name" : "refCashSaleReturnItemId", "varType" : "int"},
			{"name" : "salesRefId", "varType" : "int"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "refQuantity", "varType" : "double"},
			{"name" : "origQty", "varType" : "double"},
			{"name" : "origWarehouseId", "varType" : "int"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"}
		],
		contextPath: cPath,
		header: [
			{"title" : "id", 
				"cls" : "id", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "cashSaleReturnId", 
				"cls" : "cashSaleReturnId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "referenceObjectId",
				"cls" : "referenceObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "itemId", 
				"cls" : "itemId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code", 
				"cls" : "stockCode tblInputText", 
				"editor" : "text",
				"visible" : true,
				"width" : "10%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Description", 
				"cls" : "description tblLabelText", 
				"editor" : "label",
				"visible" : true, 
				"width" : "12%"},
			{"title" : "Existing <br> Stock", 
				"cls" : "existingStock tblLabelNumeric", 
				"editor" : "label",
				"visible" : true, 
				"width" : "6%"},
			{"title" : "warehouseId", 
				"cls" : "warehouseId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Warehouse", 
				"cls" : "warehouse tblSelectClass", 
				"editor" : "select",
				"visible" : true ,
				"width" : "8%"},
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
			{"title" : "Add On", 
				"cls" : "addOn tblSelectClass", 
				"editor" : "select",
				"visible" : true,
				"width" : "8%"},
			{"title" : sellingPriceHeader, 
				"cls" : "srp tblLabelNumeric", 
				"editor" : "label",
				"visible" : true, 
				"width" : "6%"},
			{"title" : "origSrp", 
				"cls" : "origSrp", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemSrpId", 
				"cls" : "itemSrpId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "itemDiscountId", 
				"cls" : "itemDiscountId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "itemAddOnId", 
				"cls" : "itemAddOnId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Discount",
				"cls" : "discountType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "8%"},
			{"title" : "Discount <br>(Computed)",
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
				"width" : "6%"},
			{"title" : "Amount", 
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
			{"title" : "cashSaleItemId", 
				"cls" : "cashSaleItemId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "refCashSaleReturnItemId",
				"cls" : "refCashSaleReturnItemId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "salesRefId",
				"cls" : "salesRefId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "refQuantity",
				"cls" : "refQuantity",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "origQty", 
				"cls" : "origQty", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "origWarehouseId",
				"cls" : "origWarehouseId",
				"editor" : "hidden",
				"visible" : false}
		],
		"footer" : [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false,
	});

	$("#cashSaleReturnTable").on("focus", ".stockCode", function() {
		$(this).attr("readonly", "readonly");
	});

	$("#cashSaleReturnTable").on("focus", ".quantity", function() {
		var refSaleItemId = $(this).closest("tr").find(".salesRefId").val();
		if (refSaleItemId == null || refSaleItemId == "") {
			$(this).attr("readonly", "readonly");
		}
	});

	$("#cashSaleReturnTable").on("change", ".discountType", function(){
		var discount = $(this).val();
		if (discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
		computeWtax();
	});

	$("#cashSaleReturnTable").on("change", ".warehouse", function(){
		assignESByWarehouse($(this));
	});

	$("#cashSaleReturnTable").on("focus", ".quantity", function(){
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
		computeWtax();
	});

	$("#cashSaleReturnTable").on("blur", ".quantity", function(){
		computeWtax();
	});
}

function getTotalAmount () {
	var totalAmount = $("#cashSaleReturnTable").find("tfoot tr").find(".amount").html();
	$("#totalAmount").val(accounting.unformat(totalAmount));
}

function saveCashSales() {
	$("#btnSaveCashSales").attr("disabled", true);
	$("#wtaxAcctSettingId").attr("disabled", false);
	if ($cashSaleReturnTable != null) {
		$("#cashSaleReturnItemsJson").val($cashSaleReturnTable.getData());
	}
	if ($otherChargesTable != null) {
		$("#csrArLinesJson").val($otherChargesTable.getData());
	}
	doPostWithCallBack ("cashSalesReturnForm", "form", function (data) {
		if (data.substring(0,5) == "saved") {
			var objectId = data.split(";")[1];
			var formStatus = new Object();
			formStatus.objectId = objectId;
			updateTable (formStatus);
			if ("${cashSaleReturn.id}" == 0) {
				dojo.byId("form").innerHTML = "";
			}
		} else {
			var status = $("#txtCashSalesReturnStatus").val();
			var wtAcctSettingId = $("#wtaxAcctSettingId").val();
			if ("${cashSaleReturn.id}" == 0) {
				dojo.byId("form").innerHTML = data;
				if (csrObj != null) {
					updateHeader(csrObj);
				}
			} else {
				dojo.byId("editForm").innerHTML = data;
				loadHeader();
				disableFields();
			}
			$("#txtCashSalesReturnStatus").val(status);
			initializeOtherChargesTbl(JSON.parse($("#csrArLinesJson").val()));
			initializeTable(sellingPriceHeader);
			loadWtaxAcctSettings(wtAcctSettingId);
			computeWtax();
			disableTableFields();
		}
		$("#btnSaveCashSales").removeAttr("disabled");
	});
}

function enableDisableCheck(value) {
	// If equals to check enable check number field
	if (value == 2) {
		$("#refNumber").removeAttr("readonly");
	} else {
		$("#refNumber").attr("readonly", "readonly");
	}
}

function formatMoney (textboxId) {
	var money = formatDecimalPlaces($(textboxId).val());
	$(textboxId).val(money.replace(/[$]/gi, ''));
}

function showCsReference() {
	var url = "/cashSaleReturn/"+"${cashSaleReturn.cashSaleTypeId}"+"/csReference";
	csrReferenceWindow = window.open(contextPath + url,"popup","width=1000,height=500,scrollbars=1,resizable=no,"
			+"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
}

function loadHeader() {
	if ("${cashSaleReturn.id}" != 0) {
		$("#cashSaleId").val("${cashSaleReturn.cashSaleId}");
		$("#refCashSaleReturnId").val("${cashSaleReturn.refCashSaleReturnId}");
		$("#hdnSellingTypeId").val("${cashSaleReturn.cashSaleTypeId}");
		$("#companyId").val("${cashSaleReturn.companyId}");
		$("#arCustomerId").val("${cashSaleReturn.arCustomerId}");
		$("#arCustomerAcctId").val("${cashSaleReturn.arCustomerAccountId}");
		$("#salesInvoiceNo").val("${cashSaleReturn.salesInvoiceNo}");
		$("#spanSalesInvoiceNo").text("${cashSaleReturn.salesInvoiceNo}");
		$("#spanReference").text("${cashSaleReturn.referenceNo}");
	}
}

function updateHeader (cashSaleReturn) {
	$("#cashSaleId").val(cashSaleReturn.cashSaleId);
	$("#refCashSaleReturnId").val(cashSaleReturn.refCashSaleReturnId);
	$("#companyId").val(cashSaleReturn.companyId);
	$("#spanReference").text(cashSaleReturn.referenceNo);
	$("#spanCompany").val(cashSaleReturn.company.name);
	$("#arCustomerId").val(cashSaleReturn.arCustomerId);
	$("#spanCustomer").val(cashSaleReturn.arCustomer.name);
	$("#arCustomerAcctId").val(cashSaleReturn.arCustomerAccountId);
	$("#spanCustomerAccount").val(cashSaleReturn.arCustomerAccount.name);
	$("#salesInvoiceNo").val(cashSaleReturn.salesInvoiceNo);
	$("#spanSalesInvoiceNo").text(cashSaleReturn.salesInvoiceNo);
}

function loadCSReference (cashSaleId, refType, cashSaleTypeId) {
	$("#cashSaleReturnTable").html("");
	$("#otherChargesTable").html("");
	$.ajax({
		url: contextPath + "/cashSaleReturn/loadReturnItems?cashSaleId="+cashSaleId
		+"&refType="+refType+"&typeId="+cashSaleTypeId,
		success : function(cashSaleReturn) {
			csrObj = cashSaleReturn;
			updateHeader(cashSaleReturn);
			initializeOtherChargesTbl(cashSaleReturn.cashSaleReturnArLines);
			setupTableData(sellingPriceHeader, cashSaleReturn.cashSaleReturnItems);
			loadWtaxAcctSettings(cashSaleReturn.wtAcctSettingId);
			computeWtax();
			disableTableFields();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	if (csrReferenceWindow != null)
		csrReferenceWindow.close();
	csrReferenceWindow = null;
}

function disableTableFields() {
	$("#cashSaleReturnTable tbody tr").each(function (i) {
		$(this).find(".stockCode").attr("readonly", "readonly");
		$(this).find(".warehouse").attr("disabled", "disabled");
		$(this).find(".discountType").attr("disabled", "disabled");
		$(this).find(".addOn").attr("disabled", "disabled");
		$(this).find(".taxType").attr("disabled", "disabled");
	});

	$("#otherChargesTable tbody tr").each(function(i) {
		$(this).find(".arLineSetupName").attr("readonly", "readonly");
		$(this).find(".unitMeasurementName").attr("readonly", "readonly");
		$(this).find(".taxType").attr("disabled", "disabled");
	});

	$("#wtaxAcctSettingId").attr("disabled", "disabled");
}

function disableFields(){
	$(btnCsReference).attr("disabled", "disabled");
}

function initializeOtherChargesTbl(otherChargesTable) {
	var cPath = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: otherChargesTable,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "arLineSetupId", "varType" : "int"},
			{"name" : "unitOfMeasurementId", "varType" : "int"},
			{"name" : "quantity", "varType" : "int"},
			{"name" : "cashSaleALRefId", "varType" : "int"},
			{"name" : "cashSaleRetALRefId", "varType" : "int"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "arLineSetupName", "varType" : "string"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "referenceObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"}
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
			{"title" : "referenceObjectId",
				"cls" : "referenceObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "cashSaleALRefId",
				"cls" : "cashSaleALRefId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "cashSaleRetALRefId",
				"cls" : "cashSaleRetALRefId",
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
			{"title" : "AR Line",
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
				"width" : "10%" },
			{"title" : "UOM",
				"cls" : "unitMeasurementName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "UP",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%"},
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

	$("#otherChargesTable").on("focus", ".arLineSetupName", function(){
		$(this).attr("readonly", "readonly");
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function(){
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function() {
		var $upAmount = $(this).closest("tr").find(".upAmount");
		$upAmount.val(formatDecimalPlaces($upAmount.val(), 4));

		computeWtax();
	});

	$("#otherChargesTable").on("change", ".taxType", function(){
		computeWtax();
	});
}

function computeGrandTotal() {
	getTotalVAT();
	var totalVat = accounting.unformat($("#totalVat").html());
	var totalItemAmt = accounting.unformat($("#cashSaleReturnTable").find("tfoot tr .amount").html());
	var totalOtherCharges = accounting.unformat($("#otherChargesTable").find("tfoot tr .amount").html());
	var subTotal = totalItemAmt + totalOtherCharges;
	var totalVat = accounting.unformat($("#totalVat").html());
	$("#subTotal").html(formatDecimalPlaces(subTotal));
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	$("#computedWTax").html(formatDecimalPlaces(wtaxAmount));
	var grandTotal = subTotal + totalVat - wtaxAmount;
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function getTotalVAT() {
	var totalVat = 0;
	$("#cashSaleReturnTable").find(" tbody tr ").each(function(row) {
		var quantity = accounting.unformat($(this).find(".quantity").val());
		var vat = accounting.unformat($(this).find(".vatAmount").html());
		if (quantity < 0) {
			vat = -vat; // negate the if return
		}
		totalVat += vat;
	});

	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		totalVat += accounting.unformat($(this).find(".vatAmount").html());
	});

	$("#totalVat").html(formatDecimalPlaces(totalVat));
}

</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="cashSaleReturn" id="cashSalesReturnForm">
			<div class="modFormLabel">Cash Sales Return
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<input type="hidden" value="6" id="hdnOrTypeId">
			<form:hidden path="ebObjectId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="cashSaleId" id="cashSaleId"/>
			<form:hidden path="refCashSaleReturnId" id="refCashSaleReturnId"/>
			<form:hidden path="companyId" id="companyId"/>
			<form:hidden path="arCustomerId" id="arCustomerId"/>
			<form:hidden path="arCustomerAccountId" id="arCustomerAcctId"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="csrNumber"/>
			<form:hidden path="salesInvoiceNo"/>
			<form:hidden path="cashSaleReturnItemsJson" id="cashSaleReturnItemsJson"/>
			<form:hidden path="totalAmount"/>
			<form:hidden path="cashSaleTypeId" id="hdnSellingTypeId"/>
			<form:hidden path="csrArLinesJson" id="csrArLinesJson"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<br>
			<div class="modForm">
				<table class="frmField_set">
					<tr>
						<td>
							<input type="button" id="btnCsReference" 
								value="Cash Sale Reference" onclick="showCsReference();"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<form:errors path="cashSaleId" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
				</table>
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">
								<c:choose>
									<c:when test="${cashSaleReturn.cashSaleTypeId == 1}">CSR No.</c:when>
									<c:otherwise>CSR - W No.</c:otherwise>
								</c:choose>
							</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${cashSaleReturn.csrNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${cashSaleReturn.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtCashSalesReturnStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Cash Sales Return Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Reference</td>
							<td class="value">
								<span id="spanReference"></span>
							</td>
						</tr>
						<tr>
							<td class="labels">Company</td>
							<td class="value">
								<form:input path="company.name" id="spanCompany" class="textBoxLabel2" readonly="true" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="companyId" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" id="spanCustomer"
									class="textBoxLabel2" readonly="true" />
							</td>
						</tr>
						<tr>
							<td class="labels">Customer Account</td>
							<td class="value">
								<form:input path="arCustomerAccount.name" id="spanCustomerAccount"
									class="textBoxLabel2" readonly="true" />
							</td>
						</tr>
						<tr>
							<td class="labels">Sales Invoice No.</td>
							<td class="value">
								<span id="spanSalesInvoiceNo"></span>
							</td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="date" id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('date')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="date" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<br>
				<fieldset class="frmField_set">
				<legend>Cash Sales Return Item Table</legend>
				<div id="cashSaleReturnTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="csMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errorCSRItems" cssClass="error"/>
						</td>
					</tr>
				</table>
				</fieldset>
				<c:if test = "${cashSaleReturn.cashSaleTypeId == 1}">
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
								<form:errors path="csrArLineMesage" cssClass="error"/>
							</td>
						</tr>
					</table>
					</fieldset>
				</c:if>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
				</table>
				<br>
				<!-- Grand Total -->
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
								cssStyle="width: 95%;" onchange="assignWtaxAcctSetting(this);">
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
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSaveCashSales" value="Save" onclick="saveCashSales();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>