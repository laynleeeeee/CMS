<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Paid in advance delivery jsp form page version 2
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.saleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
var referenceWindow = null;
var $otherChargesTable = null;
var $deliveryTable = null;
var deliveryObj = null;
$(document).ready (function () {
	initializeOtherChargesTbl();
	initializeTable();
	loadWtaxAcctSettings("${capDelivery.wtAcctSettingId}");
	updateOChargesAmt();
	computeWtax();
	if ("${capDelivery.id}" != 0) {
		disableFields();
	}
});

function disableFields() {
	$("#btnReference").attr("disabled", true);
	var isComplete = "${capDelivery.formWorkflow.complete}";
	var currentStatusId = "${capDelivery.formWorkflow.currentStatusId}";
	if (isComplete == "true" || currentStatusId == 4) {
		$("#btnCapDelivery").attr("disabled", true);
		$("#imgDeliveryDate").attr("disabled", true);
		$("#deliveryDate").attr("readonly", true);
		$("#capDeliveryForm :input").attr("disabled","disabled");
		$("#deliveryItemsTable :input").attr("disabled","disabled");
		$("#otherChargesTable :input").attr("disabled","disabled");
	}
}

function initializeTable() {
	var deliveryItemsJson = JSON.parse($("#deliveryItemsJson").val());
	setupTableData(deliveryItemsJson);
}

function setupTableData(deliveryItemsJson) {
	var cPath = "${pageContext.request.contextPath}";
	$deliveryTable = $("#deliveryItemsTable").editableItem({
		data: deliveryItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "capItemId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "referenceId", "varType" : "int"},
			{"name" : "referenceObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
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
			{"title" : "capItemId",
				"cls" : "capItemId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "itemId",
				"cls" : "itemId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "referenceObjectId",
				"cls" : "referenceObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "referenceId",
				"cls" : "referenceId",
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
				"width" : "8%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						computeWtax();
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
			{"title" : "Existing <br> Stock",
				"cls" : "existingStock tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "5%"},
			{"title" : "warehouseId",
				"cls" : "warehouseId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Warehouse",
				"cls" : "warehouse tblSelectClass",
				"editor" : "select",
				"visible" : true ,
				"width" : "9%"},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "5%" },
			{"title" : "UOM",
				"cls" : "uom txtUOM",
				"editor" : "label",
				"visible" : true,
				"width" : "7%" },
			{"title" : "Add On",
				"cls" : "addOn tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "9%"},
			{"title" : "SRP",
				"cls" : "srp tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
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
				"width" : "7%"},
			{"title" : "Discount <br>(Computed)",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "taxTypeId",
				"cls" : "taxTypeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type",
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "9%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
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

	$("#deliveryItemsTable").on("change",".discountType", function(){
		var discount = $(this).val();
		if (discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
	});

	$("#deliveryItemsTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#deliveryItemsTable").on("blur", ".tblInputNumeric", function(){
		computeWtax();
	});
}

function initializeOtherChargesTbl() {
	var otherChargesJson = JSON.parse($("#deliveryArLinesJson").val());
	setUpOtherCharges(otherChargesJson);
}

function setUpOtherCharges(otherChargesJson) {
	$("#otherChargesTable").html("");
	var cPath = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: otherChargesJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "arLineSetupId", "varType" : "int"},
			{"name" : "unitOfMeasurementId", "varType" : "int"},
			{"name" : "quantity", "varType" : "int"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "arLineSetupName", "varType" : "string"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"}
		],
		contextPath: cPath,
		header: [
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
			{"title" : "AR Line",
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
				"width" : "15%" },
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
			{"title" : "Gross Price",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "15%"},
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
				"width" : "15%"}
		],
		footer: [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function() {
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function() {
		updateOChargesAmt();
		computeWtax();
	});

	$("#otherChargesTable").on("change", ".taxType", function() {
		updateOChargesAmt();
		computeWtax();
	});
}

function showReferencePopup() {
	referenceWindow = window.open(contextPath + "/capDelivery/1/loadReferences","popup","width=1000,height=500,scrollbars=1,resizable=no,"
			+ "toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
}

function loadReference(referenceId) {
	$("#deliveryItemsTable").html("");
	$.ajax({
		url: contextPath + "/capDelivery?capId="+referenceId,
		success : function(capDev) {
			deliveryObj = capDev;
			updateHeader(capDev.customerAdvancePaymentId, capDev.companyId, capDev.company.numberAndName,
					capDev.arCustomerId, capDev.arCustomer.name, capDev.arCustomerAcctId,
					capDev.arCustomerAccount.name, capDev.salesInvoiceNo);
			setUpOtherCharges(capDev.deliveryArLines);
			setupTableData(capDev.deliveryItems);
			loadWtaxAcctSettings();
			updateOChargesAmt();
			computeWtax();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	if (referenceWindow != null) {
		referenceWindow.close();
	}
	referenceWindow = null;
}

function updateHeader(capId, companyId, companyName, customerId, customerName,
		customerAcctId, customerAcctName, salesInvoiceNo) {
	$("#hdnCustomerAdvancePaymentId").val(capId);
	$("#companyId").val(companyId);
	$("#spanCompany").val(companyName);
	$("#arCustomerId").val(customerId);
	$("#spanCustomer").val(customerName);
	$("#arCustomerAcctId").val(customerAcctId);
	$("#spanCustomerAccount").val(customerAcctName);
	$("#salesInvoiceNo").val(salesInvoiceNo);
}

function loadHeader() {
	var capId = $("#customerAdvancePaymentId").val();
	var companyId = $("#companyId").val();
	var companyName = $("#spanCompany").val();
	var customerId = $("#arCustomerId").val();
	var customerName = $("#spanCustomer").val();
	var customerAcctId = $("#arCustomerAcctId").val();
	var customerAcctName = $("#spanCustomerAccount").val();
	var salesInvoiceNo = $("#salesInvoiceNo").val();
	updateHeader(capId, companyId, companyName, customerId, customerName,
			customerAcctId, customerAcctName, salesInvoiceNo);
}

function computeGrandTotal() {
	var subTotal = 0;
	var totalVAT = 0;
	$("#deliveryItemsTable tbody tr").each(function() {
		subTotal += accounting.unformat($(this).find(".amount").html());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#otherChargesTable tbody tr").each(function() {
		subTotal += accounting.unformat($(this).find(".amount").val());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#subTotal").html(accounting.formatMoney(subTotal));
	$("#totalVat").html(accounting.formatMoney(totalVAT));
	var wtAmount = accounting.unformat($("#hdnWtAmount").val());
	var grandTotal = (subTotal + totalVAT) - wtAmount;
	$("#grandTotal").html(accounting.formatMoney(grandTotal));
}

var isSaving = false;
function saveCAPDelivery () {
	if(isSaving == false) {
		isSaving = true;
		$("#btnCapDelivery").attr("disabled", true);
		if ($deliveryTable != null)
			$("#deliveryItemsJson").val($deliveryTable.getData());
		if ($otherChargesTable != null) {
			$("#deliveryArLinesJson").val($otherChargesTable.getData());
		}
		doPostWithCallBack ("capDeliveryForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if ("${capDelivery.id}" == 0) {
					dojo.byId("form").innerHTML = "";
				}
			} else {
				var wtaxAcctSettingId = $("#wtaxAcctSettingId").val();
				loadHeader();
				if ("${capDelivery.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableFields();
				}
				initializeOtherChargesTbl();
				initializeTable();
				updateOChargesAmt();
				loadWtaxAcctSettings(wtaxAcctSettingId);
				computeWtax();
			}
			$("#btnCapDelivery").removeAttr("disabled");
			isSaving = false;
		});
	}
}

function disableTableFields() {
	$("#deliveryItemsTable tbody tr").each(function (i) {
		var capItemId = $(this).find(".capItemId").val();
		console.log("capItemId "+capItemId);
		if (typeof capItemId != "undefined" && capItemId != null && capItemId != "" && capItemId != 0) {
			$(this).find(".stockCode").attr("readonly", "readonly");
			$(this).find(".warehouse").attr("disabled", "disabled");
			$(this).find(".discountType").attr("disabled", "disabled");
		}
	});
}

function loadWtaxAcctSettings(wtAcctSettingId) {
	var companyId = $("#companyId").val();
	if (companyId != "") {
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
				var totalAmount = accounting.unformat($("#subTotal").text());
				$("#deliveryItemsTable tbody tr").each(function(row) {
					totalAmount += accounting.unformat($(this).find(".discount").html());
				});
				var computedWTax = totalAmount * wtPercentageValue;
				$("#hdnWtAmount").val(accounting.unformat(formatNumber(computedWTax)));
				$("#computedWTax").html(formatNumber(computedWTax));
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

function formatNumber(value) {
	return accounting.formatMoney(value, '', 2);
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeWtax();
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="capDelivery" id="capDeliveryForm">
			<div class="modFormLabel">Paid in Advance Delivery<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id" id="capDeliveryFormId"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="capdNumber"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="customerAdvancePaymentId" id="hdnCustomerAdvancePaymentId"/>
			<form:hidden path="customerAdvancePaymentTypeId"/>
			<form:hidden path="companyId" id="companyId"/>
			<form:hidden path="arCustomerId" id="arCustomerId"/>
			<form:hidden path="arCustomerAcctId" id="arCustomerAcctId"/>
			<form:hidden path="deliveryItemsJson" id="deliveryItemsJson"/>
			<form:hidden path="deliveryArLinesJson" id="deliveryArLinesJson"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<br>
			<div class="modForm">
				<table class="frmField_set">
					<tr>
						<td>
							<input type="button" id="btnReference" 
								value="Customer Advance Payment Reference" onclick="showReferencePopup();"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<form:errors path="customerAdvancePaymentId" cssClass="error"/>
						</td>
					</tr>
				</table>
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">PIAD No.</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${capDelivery.capdNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${capDelivery.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="spanFormStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Paid in Advance Delivery Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Company</td>
							<td class="value">
								<form:input path="company.name" id="spanCompany" class="textBoxLabel2"
									readonly="true" value="${capDelivery.company.name}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" id="spanCustomer" class="textBoxLabel2"
									value="${capDelivery.arCustomer.name}" readonly="true" />
							</td>
						</tr>
						<tr>
							<td class="labels">Customer Account</td>
							<td class="value">
								<form:input path="arCustomerAccount.name" id="spanCustomerAccount" class="textBoxLabel2"
									value="${capDelivery.arCustomerAccount.name}" readonly="true" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Sales Invoice No.</td>
							<td class="value">
								<form:input path="salesInvoiceNo" id="salesInvoiceNo" cssClass="inputSmall"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="salesInvoiceNo" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Delivery Date </td>
							<td class="value">
								<form:input path="deliveryDate" onblur="evalDate('deliveryDate')" 
									id="deliveryDate" style="width: 120px;" cssClass="dateClass2"/>
								<img id="imgDeliveryDate" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('deliveryDate')"
									style="cursor: pointer" style="float: right;" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="deliveryDate" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<fieldset class="frmField_set">
					<legend>Delivery Items Table</legend>
					<div id="deliveryItemsTable"></div>
					<table>
						<tr>
							<td colspan="12">
								<span id="spanDeliveryItems" class="error"></span>
							</td>
						</tr>
						<tr>
							<td colspan="12">
								<form:errors path="deliveryItems" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<!-- Other Charges -->
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
								<form:errors path="deliveryArLines" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
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
					<tr>
						<td colspan="5">
							<span id="errorMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="5">
							<form:errors path="errorMessage" cssClass="error"/>
						</td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnCapDelivery" value="Save" onclick="saveCAPDelivery();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>