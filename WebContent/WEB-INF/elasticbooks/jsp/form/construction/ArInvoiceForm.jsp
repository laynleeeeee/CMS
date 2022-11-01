<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR invoice jsp form
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
<script type="text/javascript">
var $tblDeliveryReceiptItem = null;
var $tblDRSerialItem  = null;
$(document).ready(function() {
	if (Number("${arInvoice.id}") > 0) {
		$("#txtDRRefNumber").val("${arInvoice.drNumber}");
		$("#aOpen").hide();
		loadWtaxAcctSettings("${arInvoice.wtAcctSettingId}");
		initializeSerializedItemTable();
		initializeNonSerializedDRItemTable();
		$("#hdnTermDays").val("${arInvoice.term.days}");
		computeGrandTotal();
		computeWtax();
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
	if (!isSaving && $("#hdnStrDrRefIds").val() != "") {
		isSaving = true;
		// enabling item and line table
		$("#tblDeliveryReceiptItem :input").removeAttr("disabled");
		$("#tblDRSerialItem :input").removeAttr("disabled");
		// reset JSON table values
		$("#serialArItemsJson").val($tblDRSerialItem.getData());
		$("#nonSerialArItemsJson").val($tblDeliveryReceiptItem.getData());
		$("#btnSaveArInvoice").attr("disabled", "disabled");
		doPostWithCallBack ("arInvoiceForm", "form", function(data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
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
				if ("${arInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#aOpen").hide();
				}
				$("#txtCurrentStatusId").val(txtCurrentStatusId);
				updateHeader(drNumber, companyId, companyName, customerId, customerName,
						customerAcctId, customerAcctName, termId, termName, termDays);
				$("#remarks").val(remarks);
				loadWtaxAcctSettings(wtAcctSettingId);
				initializeSerializedItemTable();
				initializeNonSerializedDRItemTable();
			}
			isSaving = false;
			$("#btnSaveArInvoice").removeAttr("disabled");
		});
	} else {
		$("#spnDeliveryRecieptId").text("DR Reference is required.");
	}
}

function showDRReferences() {
	var arInvoiceTypeId = $("#hdnArInvoiceTypeId").val();
	$("#divDrReferenceId").load(contextPath+"/arInvoice/showReferenceForm?arInvoiceTypeId="+arInvoiceTypeId);
}

function loadDRReference(slctdDrRefIds) {
	var drRefIds = "";
	var keys = Object.keys(slctdDrRefIds);
	for (var i = 0; i < keys.length; i++) {
		drRefIds += keys[i] + ";";
	}
	if (drRefIds != "") {
		$.ajax({
			url: contextPath + "/arInvoice/loadDRReferenceForm?drRefIds="+drRefIds,
			success : function(ari) {
				$("#hdnStrDrRefIds").val(drRefIds);
				$("#divDrReferenceId").html("");
				$("#spnDeliveryRecieptId").text("");
				$("#aClose")[0].click();
				updateHeader(ari.drNumber, ari.companyId, ari.company.name, ari.arCustomerId,
						ari.arCustomer.name, ari.arCustomerAccountId, ari.arCustomerAccount.name, ari.termId, ari.term.name,
						ari.term.days, ari.remarks);
				loadWtaxAcctSettings(ari.wtAcctSettingId);
				setupSerialItems(ari.serialArItems);
				setupNonSerialItems(ari.nonSerialArItems);
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
		arCustomerAcctId, arCustomerAcctName, termId, termName, termDays, remarks) {
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
				// Add total item discounts
				$("#tblDRSerialItem tbody tr").each(function(row) {
					totalAmount += accounting.unformat($(this).find(".discount").html());
				});
				$("#tblDeliveryReceiptItem tbody tr").each(function(row) {
					totalAmount += accounting.unformat($(this).find(".discount").html());
				});
				$("#otherChargesTable tbody tr").each(function(row) {
					totalAmount += accounting.unformat($(this).find(".discount").html());
				});
				$("#arInvoiceEquipmentLinesTbl tbody tr").each(function(row) {
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
	var totalSerialItemAmt = 0;
	var totalVAT = 0;
	$("#tblDRSerialItem").find(" tbody tr ").each(function(row) {
		totalSerialItemAmt += accounting.unformat($(this).find(".amount").html());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#tblDRSerialItem").find("tfoot tr .amount").html(formatDecimalPlaces(totalSerialItemAmt));

	var totalNonSerialItemAmt = 0;
	$("#tblDeliveryReceiptItem").find(" tbody tr ").each(function(row) {
		totalNonSerialItemAmt += accounting.unformat($(this).find(".amount").html());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#tblDeliveryReceiptItem").find("tfoot tr .amount").html(formatDecimalPlaces(totalNonSerialItemAmt));

	var totalOtherChargesAmt = 0;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		totalOtherChargesAmt += accounting.unformat($(this).find(".amount").val());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
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

	$("#totalVAT").html(formatDecimalPlaces(totalVAT));
	var subTotal = totalSerialItemAmt + totalNonSerialItemAmt + totalOtherChargesAmt + totalAriTruckingAmt + totalAriEquipmentAmt;
	var wtaxAmount = $("#hdnWtAmount").val();
	var grandTotal = subTotal - wtaxAmount;
	$("#subTotal").html(formatDecimalPlaces(subTotal));
	$("#computedWTax").html(formatDecimalPlaces(wtaxAmount));
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
	$("#hdnAmountId").val(grandTotal);
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeGrandTotal();
	computeWtax();
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
	<div class="modFormLabel">AR Invoice - Goods
	<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
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
		<form:hidden path="strDrRefIds" id="hdnStrDrRefIds"/>
		<br>
		<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="labels">Sequence Number</td>
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
					<td class="labels">* DR-I Reference</td>
					<td class="value">
						<input id="txtDRRefNumber" readonly="readonly"/>
						<a href="#container" id="aOpen" data-reveal-id="divDrReferenceId" class="link_button"
							onclick="showDRReferences();">Browse Reference</a>
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
			</table>
		</fieldset>
		<fieldset id="nonSerialItemsTbl" class="frmField_set">
			<legend>Non-Serialized Items Table</legend>
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
				<td align="right"><input type="button" id="btnSaveArInvoice"
						value="Save" onclick="saveArInvoice();" /></td>
			</tr>
		</table>
		</div>
	</form:form>
</div>
</body>
</html>