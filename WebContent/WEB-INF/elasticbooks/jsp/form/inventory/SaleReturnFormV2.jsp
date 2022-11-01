<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Sales return form.
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
.textBoxLabel {
	border: none;
}
</style>
<script type="text/javascript">
var terms = new Array ();
var $accountSaleTable = null;
var $otherChargesTable = null;
var asrObj = null;
var asrReferenceWindow = null;
var sellingPriceHeader = null;
$(document).ready (function () {
	var wtAcctSettingId = "";
	sellingPriceHeader = "SRP";
	if ("${salesReturn.transactionTypeId}" == 9) {
		sellingPriceHeader = "WP";
	}

	if ("${salesReturn.id}" != 0) {
		wtAcctSettingId = "${salesReturn.wtAcctSettingId}";
		loadHeader();
		initializeOtherChargesTbl(JSON.parse($("#arLinesJson").val()));
		initializeTable(sellingPriceHeader);
		loadWtaxAcctSettings(wtAcctSettingId);
		disableFields();
		disableTableFields();
	}

	if ("${salesReturn.formWorkflow.complete}" == "true" || "${salesReturn.formWorkflow.currentStatusId}" == 4) {
		$("#divForm :input").attr("disabled", "disabled");
	}

	updateOChargesAmt();
	computeGrandTotal();
	computeWtax();
});

function loadWtaxAcctSettings(wtAcctSettingId) {
	var $select = $("#wtaxAcctSettingId");
	$select.empty();
	var uri = contextPath+"/getWithholdingTaxAcct?companyId="+$("#hdnCompanyId").val()+"&isCreditable=true";
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
				$("#saleReturnItemTable tbody tr").each(function(row) {
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

function initializeTable(sellingPriceHeader) {
	var accountSaleItemsJson = JSON.parse($("#accountSalesItemsJson").val());
	setupTableData (accountSaleItemsJson, sellingPriceHeader);
}

function setupTableData (accountSaleItemsJson, sellingPriceHeader) {
	var cPath = "${pageContext.request.contextPath}";
	$accountSaleTable = $("#saleReturnItemTable").editableItem({
		data: accountSaleItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "transactionTypeId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "referenceObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
			{"name" : "itemDiscountId", "varType" : "int"},
			{"name" : "itemAddOnId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "quantity", "varType" : "int"},
			{"name" : "itemSrpId", "varType" : "int"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "addOn", "varType" : "double"},
			{"name" : "srp", "varType" : "double"},
			{"name" : "origSrp", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "refAccountSaleItemId", "varType" : "int"},
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
			{"title" : "transactionTypeId", 
				"cls" : "transactionTypeId", 
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
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible": false},
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
			{"title" : "itemSrpId",
				"cls" : "itemSrpId",
				"editor" : "hidden",
				"visible" : false},
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
			{"title" : "refAccountSaleItemId",
				"cls" : "refAccountSaleItemId",
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
		"footer": [
			{"cls" : "amount"}
		],
			"disableDuplicateStockCode" : false,
	});

	$("#saleReturnItemTable").on("focus", ".stockCode", function() {
		$(this).attr("readonly", "readonly");
	});

	$("#saleReturnItemTable").on("focus", ".quantity", function() {
		var refSaleItemId = $(this).closest("tr").find(".salesRefId").val();
		if (refSaleItemId == null || refSaleItemId == "") {
			$(this).attr("readonly", "readonly");
		}
	});

	$("#saleReturnItemTable").on("change", ".discountType", function(){
		var discount = $(this).val();
		if (discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
	});

	$("#saleReturnItemTable").on("change", ".warehouse", function(){
		assignESByWarehouse($(this));
	});

	$("#saleReturnItemTable").on("change", ".taxType", function() {
		computeGrandTotal();
		computeWtax();
	});

	$("#saleReturnItemTable").on("focusout", ".quantity", function() {
		computeGrandTotal();
		computeWtax();
	});
}

function setTotalAmount() {
	var grandTotal = $("#grandTotal").html();
	$("#hdnAmount").val(accounting.unformat(grandTotal));
	$("#hdnTotalAmount").val(accounting.unformat(grandTotal));
}

var isSaving = false;
function saveSaleReturnForm() {
	if (!isSaving) {
		isSaving = true;
		$("#btnAccountSales").attr("disabled", "disabled");
		enableFields();
		if ($accountSaleTable != null) {
			$("#accountSalesItemsJson").val($accountSaleTable.getData());
		}
		if ($otherChargesTable != null) {
			$("#arLinesJson").val($otherChargesTable.getData());
		}
		setTotalAmount();
		doPostWithCallBack ("salesReturnForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if ("${salesReturn.id}" == 0) {
					dojo.byId("form").innerHTML = "";
				}
			} else {
				var currentStatus = $("#txtCurrentFormStatus").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				if ("${salesReturn.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					if (asrObj != null) {
						updateHeader(asrObj);
					}
				} else {
					dojo.byId("editForm").innerHTML = data;
					loadHeader();
					disableFields();
				}
				$("#txtCurrentFormStatus").val(currentStatus);
				initializeOtherChargesTbl(JSON.parse($("#arLinesJson").val()));
				initializeTable(sellingPriceHeader);
				loadWtaxAcctSettings(wtAcctSettingId);
				updateOChargesAmt();
				computeGrandTotal();
				computeWtax();
				disableTableFields();
			}
			$("#btnAccountSales").removeAttr("disabled");
			isSaving = false;
		});
	}
}

function showAsReference() {
	var url = contextPath + "/saleReturn/"+"${salesReturn.transactionTypeId}"+"/asReference";
	asrReferenceWindow = window.open(url,"popup","width=1000,height=500,scrollbars=1,resizable=no," +
	"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
}

function loadHeader() {
	if ("${salesReturn.id}" != 0) {
		$("#accountSaleId").val("${salesReturn.accountSaleId}");
		$("#spanReference").text("${salesReturn.transactionNumber}");
		$("#hdnCompanyId").val("${salesReturn.companyId}");
		$("#hdnCustomerId").val("${salesReturn.customerId}");
		$("#hdnCustomerAcctId").val("${salesReturn.customerAcctId}");
		$("#hdnTermId").val("${salesReturn.termId}");
		$("#spanTerm").text("${salesReturn.term.name}");
	}
}

function updateHeader (salesReturn) {
	$("#accountSaleId").val(salesReturn.accountSaleId);
	$("#spanReference").text(salesReturn.transactionNumber);
	$("#hdnCompanyId").val(salesReturn.companyId);
	$("#spanCompany").val(salesReturn.company.name);
	$("#hdnCustomerId").val(salesReturn.customerId);
	$("#spanCustomer").val(salesReturn.arCustomer.name);
	$("#hdnCustomerAcctId").val(salesReturn.customerAcctId);
	$("#spanCustomerAccount").val(salesReturn.arCustomerAccount.name);
	$("#hdnTermId").val(salesReturn.termId);
	$("#spanTerm").text(salesReturn.term.name);
}

function loadASReference (arTransactionId) {
	$("#saleReturnItemTable").html("");
	$("#otherChargesTable").html("");
	$.ajax({
		url: contextPath + "/saleReturn?arTransactionId="+arTransactionId,
		success : function(saleReturn) {
			asrObj = saleReturn;
			updateHeader(saleReturn);
			initializeOtherChargesTbl(saleReturn.arLines);
			setupTableData(saleReturn.accountSaleItems, sellingPriceHeader);
			loadWtaxAcctSettings(saleReturn.wtAcctSettingId);
			updateOChargesAmt();
			computeGrandTotal();
			computeWtax();
			disableTableFields();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	if (asrReferenceWindow != null) {
		asrReferenceWindow.close();
	}
	asrReferenceWindow = null;
}

function disableTableFields() {
	$("#saleReturnItemTable tbody tr").each(function(i) {
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

function enableFields() {
	$("#saleReturnItemTable tbody tr").each(function(i) {
		$(this).find(".warehouse").removeAttr("disabled");
		$(this).find(".discountType").removeAttr("disabled");
		$(this).find(".addOn").removeAttr("disabled");
		$(this).find(".taxType").removeAttr("disabled");
	});

	$("#otherChargesTable tbody tr").each(function(i) {
		$(this).find(".taxType").removeAttr("disabled");
	});

	$("#wtaxAcctSettingId").removeAttr("disabled");
}

function disableFields() {
	$(btnAsReference).attr("disabled", "disabled");
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
			{"title" : "Gross Price",
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

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function(){
		var $upAmount = $(this).closest("tr").find(".upAmount");
		$upAmount.val(formatDecimalPlaces($upAmount.val(), 4));

		updateOChargesAmt();
		computeGrandTotal();
		computeWtax();
	});

	$("#otherChargesTable").on("change", ".taxType", function() {
		updateOChargesAmt();
		computeGrandTotal();
		computeWtax();
	});
}

function computeGrandTotal() {
	getTotalQty();
	var subTotal = accounting.unformat($("#saleReturnItemTable").find("tfoot tr .amount").html())
		+ accounting.unformat($("#otherChargesTable").find("tfoot tr .amount").html());
	$("#subTotal").html(formatDecimalPlaces(subTotal));

	getTotalVAT();
	var totalVat = accounting.unformat($("#totalVat").html());

	var wtaxAmount = $("#hdnWtAmount").val();
	$("#computedWTax").html(formatDecimalPlaces(wtaxAmount));

	var grandTotal = (subTotal + totalVat) - wtaxAmount;
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function getTotalQty() {
	var totalQty = 0;
	$("#saleReturnItemTable").find(" tbody tr ").each(function(row) {
		var quantity = $(this).find(".quantity").val();
		totalQty += accounting.unformat(quantity);
	});
	$("#saleReturnItemTable").find("tfoot tr").find(".quantity").html(formatDecimalPlaces(totalQty));
	$("#saleReturnItemTable tfoot").css("text-align", "right");
}

function getTotalVAT() {
	var totalVAT = 0;
	$("#saleReturnItemTable").find(" tbody tr ").each(function(row) {
		var quantity = accounting.unformat($(this).find(".quantity").val());
		var vat = accounting.unformat($(this).find(".vatAmount").html());
		if (quantity < 0) {
			vat = vat * -1; // negate the if return
		}
		totalVAT += vat;
	});

	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
	});

	$("#totalVat").html(formatDecimalPlaces(totalVAT));
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	updateOChargesAmt();
	computeGrandTotal();
	computeWtax();
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="salesReturn" id="salesReturnForm">
			<div class="modFormLabel">Account Sales Return
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="updatedBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNumber"/>
			<form:hidden path="accountSaleId"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="transactionTypeId" id="hdnSellingTypeId"/>
			<form:hidden path="customerId" id="hdnCustomerId"/>
			<form:hidden path="customerAcctId" id="hdnCustomerAcctId"/>
			<form:hidden path="termId" id="hdnTermId"/>
			<form:hidden path="amount" id="hdnAmount"/>
			<form:hidden path="totalAmount" id="hdnTotalAmountId"/>
			<form:hidden path="availableBalance" id="hdnAvailableBalanceId"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<form:hidden path="accountSalesItemsJson" id="accountSalesItemsJson"/>
			<form:hidden path="arLinesJson" id="arLinesJson"/>
			<br>
			<div class="modForm">
				<table class="frmField_set">
					<tr>
						<td>
							<input type="button" id="btnAsReference" 
								value="Account Sale Reference" onclick="showAsReference();"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<form:errors path="accountSaleId" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
				</table>
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">
								<c:choose>
									<c:when test="${salesReturn.transactionTypeId == 5}">ASR No.</c:when>
									<c:otherwise>ASR - W No.</c:otherwise>
								</c:choose>
							</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${salesReturn.sequenceNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${salesReturn.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtCurrentFormStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Account Sales Return Header</legend>
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
							<td class="labels">Term</td>
							<td class="value">
								<span id="spanTerm"></span>
							</td>
						</tr>

						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="transactionDate" id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('date')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="transactionDate" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>

						<tr>
							<td class="labels">Remarks</td>
							<td class="value"><form:textarea path="description"
										id="description" class="input" /></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<fieldset class="frmField_set">
				<legend>Account Sales Return Item Table</legend>
					<div id="saleReturnItemTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="csMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errorASItems" cssClass="error"/>
						</td>
					</tr>
				</table>
				</fieldset>
				<c:if test="${salesReturn.transactionTypeId == 5}">
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
									<form:errors path="otherChargesMessage" cssClass="error"/>
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
				</c:if>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnAccountSales" value="Save" onclick="saveSaleReturnForm();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>