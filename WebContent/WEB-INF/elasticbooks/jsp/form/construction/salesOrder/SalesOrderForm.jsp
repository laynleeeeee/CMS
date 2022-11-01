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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.constructionsaleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
var soObj = null;
var soReferenceWindow = null;
$(document).ready(function() {
	initSOItems(JSON.parse($("#hdnSoItemJson").val()));
	initSOLines(JSON.parse($("#hdnSoLineJson").val()));
	initSOTLines(JSON.parse($("#hdnSotLineJson").val()));
	initSOELines(JSON.parse($("#hdnSoeLineJson").val()));
	loadWtaxAcctSettings("${salesOrder.wtAcctSettingId}");
	filterCustomerAccts("${salesOrder.arCustomerAcctId}");
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
	disableFormFields();
});

function disableFormFields() {
	var isComplete = "${salesOrder.formWorkflow.complete}";
	if (isComplete == "true" || "${salesOrder.formWorkflow.currentStatusId}" == 4) {
		$("#salesOrderFormId :input").attr("disabled", "disabled");
		$("#imgDate2").hide();
	}
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
		recomputeRowAmount(this, false);
	});

	$("#soItemTbl").on("change", ".taxType", function() {
		recomputeRowAmount(this, false);
	});
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

function loadWtaxAcctSettings(wtAcctSettingId) {
	var $select = $("#wtaxAcctSettingId");
	$select.empty();
	var uri = contextPath+"/getWithholdingTaxAcct?companyId="+$("#companyId").val()+"&isCreditable=true";
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
				// Add total item discounts
				$("#soItemTbl tbody tr").each(function(row) {
					totalAmount += accounting.unformat($(this).find(".discount").html());
				});
				$("#otherChargesTable tbody tr").each(function(row) {
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
	}
}

function computeGrandTotal() {
	var totatItemAmt = 0;
	var totalVAT = 0;
	$("#soItemTbl").find(" tbody tr ").each(function(row) {
		totatItemAmt += accounting.unformat($(this).find(".amount").html());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#soItemTbl").find("tfoot tr .amount").html(formatDecimalPlaces(totatItemAmt));

	var itemOcAmt = 0;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		itemOcAmt += accounting.unformat($(this).find(".amount").val());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#otherChargesTable").find("tfoot tr .amount").html(formatDecimalPlaces(itemOcAmt));

	var equipmentAmt = 0;
	$("#equipmentChargesTable").find(" tbody tr ").each(function(row) {
		equipmentAmt += accounting.unformat($(this).find(".amount").val());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#equipmentChargesTable").find("tfoot tr .amount").html(formatDecimalPlaces(equipmentAmt));

	var truckingAmt = 0;
	$("#truckingChargesTable").find(" tbody tr ").each(function(row) {
		truckingAmt += accounting.unformat($(this).find(".amount").val());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#truckingChargesTable").find("tfoot tr .amount").html(formatDecimalPlaces(truckingAmt));

	$("#totalVAT").html(formatDecimalPlaces(totalVAT));
	var subTotal = totatItemAmt + itemOcAmt + equipmentAmt + truckingAmt;
	var wtaxAmount = $("#hdnWtAmount").val();
	var grandTotal = subTotal - wtaxAmount;
	$("#subTotal").html(formatDecimalPlaces(subTotal));
	$("#computedWTax").html(formatDecimalPlaces(wtaxAmount));
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
			{"name" : "arLineSetupId", "varType" : "int"},
			{"name" : "arLineSetupName", "varType" : "string"},
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
			{"title" : "arLineSetupId",
				"cls" : "arLineSetupId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Service",
				"cls" : "arLineSetupName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "15%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
			})},
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
		recomputeRowAmount(this, true);
	});

	$("#"+tblId).on("change", ".taxType", function() {
		recomputeRowAmount(this, true);
	});

	$("#"+tblId+" tbody tr").each(function() {
		recomputeRowAmount(this, true);
	});

	resizeTbl("#"+tblId, 11);
	return $chargesTable;
}

function resizeTbl(tableId, rowCount){
	var $tbl = $(tableId);
	$($tbl).attr("width", "100%");
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function showCustomers() {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $("#txtCustomer").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&companyId="+companyId;
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
	if (customerName != "") {
		$("#spanCustomerError").text("");
		$.ajax({
			url: contextPath + "/getArCustomers/new?name="+processSearchName(customerName)
					+"&isExact=true"+"&companyId="+companyId,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					var customerId = customer[0].id;
					$("#hdnArCustomerId").val(customerId);
					$("#txtCustomer").val(customer[0].name);
					filterCustomerAccts();
					getCustomerTypes(customer[0].customerTypeId, customerId);
					populateEncodedShipTo(customerId);
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#hdnArCustomerId").val("");
					$("#customerTypeId").empty();
					$("#arCustomerAcctId").empty();
				}
			},
			dataType: "json"
		});
	} else {
		$("#hdnArCustomerId").val("");
		$("#customerTypeId").empty();
		$("#arCustomerAcctId").empty();
	}
}

function filterCustomerAccts(arCustomerAcctId) {
	$("#arCustomerAcctId").empty();
	var customerId = $("#hdnArCustomerId").val();
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getArCustomerAccounts?arCustomerId="+customerId+"&companyId="+companyId;
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
			if (arCustomerAcctId != null && typeof arCustomerAcctId != "undefined") {
				$("#arCustomerAcctId").val(arCustomerAcctId);
			}
			// This is to remove any duplication.
			var found = [];
			$("#arCustomerAcctId option").each(function() {
				if($.inArray(this.value, found) != -1) 
					$(this).remove();
				found.push(this.value);
			});
		}
	};
	loadPopulate (uri, false, arCustomerAcctId, "arCustomerAcctId", optionParser, postHandler);
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
		$("#hdnCompanyId").val(companyId);
		$("#hdnSoItemJson").val($soItemTbl.getData());
		$("#hdnSoLineJson").val($otherChargesTable.getData());
		$("#hdnSotLineJson").val($truckingChargesTable.getData());
		$("#hdnSoeLineJson").val($equipmentChargesTable.getData());
		$("#btnSalesOrder").attr("disabled", "disabled");
		doPostWithCallBack ("salesOrderFormId", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				var currentStatus = $("#txtCurrentStatusId").val();
				var arCustomerAcctId = $("#arCustomerAcctId").val();
				var customerTypeId = $("#customerTypeId").val();
				var customerId = $("#hdnArCustomerId").val();
				if ("${salesOrder.id}" == 0){
					dojo.byId("form").innerHTML = data;
					if (soObj != null) {
						updateHeader(soObj);
					}
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtCurrentStatusId").val(currentStatus);
				}
				$("#companyId").val(companyId);
				filterCustomerAccts(arCustomerAcctId);
				getCustomerTypes(customerTypeId, customerId);
				initSOItems(JSON.parse($("#hdnSoItemJson").val()));
				initSOLines(JSON.parse($("#hdnSoLineJson").val()));
				initSOTLines(JSON.parse($("#hdnSotLineJson").val()));
				initSOELines(JSON.parse($("#hdnSoeLineJson").val()));
				formatTableValues();
				loadWtaxAcctSettings(wtAcctSettingId);
				computeGrandTotal();
				computeWtax();
				$("#txtAdvancePayment").val(formatDecimalPlaces(advPayment));
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
	filterCustomerAccts(so.arCustomerAcctId);
	getCustomerTypes(so.customerTypeId, arCustomerId)
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
			<div class="modFormLabel">Sales Order
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="updatedBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNumber"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
			<form:hidden path="amount" id="hdnAmountId"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<form:hidden path="soItemJson" id="hdnSoItemJson"/>
			<form:hidden path="soLineJson" id="hdnSoLineJson"/>
			<form:hidden path="soeLineJson" id="hdnSoeLineJson"/>
			<form:hidden path="sotLineJson" id="hdnSotLineJson"/>
			<form:hidden path="salesQuotationId" id="hdnSQId"/>
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
								<form:errors path="arCustomerId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer Account</td>
							<td class="value">
								<form:select path="arCustomerAcctId" id="arCustomerAcctId" cssClass="frmSelectClass">
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="arCustomerAcctId" cssClass="error"/>
							</td>
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
							<td colspan="2"><form:errors path="advancePayment" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Goods Table</legend>
					<div id="soItemTbl"></div>
					<table>
						<tr>
							<td><form:errors path="soItems" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Service Table</legend>
					<div id="otherChargesTable"></div>
					<table>
						<tr>
							<td><form:errors path="soLines" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set hidden">
					<legend>Trucking Service Table</legend>
					<div id="truckingChargesTable"></div>
					<table>
						<tr>
							<td><form:errors path="sotLines" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set hidden">
					<legend>Equipment Service Table</legend>
					<div id="equipmentChargesTable"></div>
					<table>
						<tr>
							<td><form:errors path="soeLines" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="frmField_set footerTotalAmountTbl">
					<tr>
						<td style="width: 22%;"></td>
						<td style="width: 22%;"></td>
						<td style="width: 22%; display: none;">Sub Total</td>
						<td style="width: 22%;"></td>
						<td style="width: 12%; display: none;"><span id="subTotal"></span></td>
					</tr>
					<tr>
						<td colspan="3">Total VAT</td>
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
						<td align="right"><input type="button" id="btnSalesOrder" value="Save" onclick="saveSOForm();" /></td>
					</tr>
				</table>
			</div>
	</form:form>
</div>
</body>
</html>