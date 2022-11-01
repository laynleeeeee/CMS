<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.saleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<style type="text/css">
.textBoxLabel {
	border: none;
}
</style>
<script type="text/javascript">
var selectCustomerAcct = "${cashSale.arCustomerAccountId}";
var selectCompany = "${cashSale.companyId}";
var currentCustAcctId = 0;
var $cashSaleTable = null;
var sellingPriceHeader = null;
$(document).ready (function () {
	var wtAcctSettingId = "";
	var sellingTypeId = "${cashSale.cashSaleTypeId}";
	sellingPriceHeader = sellingTypeId == 2 ? "WP" : "SRP";
	filterCustomerAccts();
	initializeOtherChargesTbl();
	initializeTable (sellingPriceHeader);
	if ("${cashSale.id}" != 0) {
		wtAcctSettingId = "${cashSale.wtAcctSettingId}";
		disableAndSetCompany();
	} else {
		$("#hdnCompanyId").val($("#companyId").val());
	}
	enableDisableCheck();
	hideValues();
	if ("${cashSale.formWorkflow.complete}" == "true" || "${cashSale.formWorkflow.currentStatusId}" == 4) {
		$("#cashSalesForm :input").attr("disabled","disabled");
	}

	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
});

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
	totalQty();
	computeTotalItemVat();
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
				$("#cashSaleTable tbody tr").each(function(row) {
					totalAmount += accounting.unformat($(this).find(".discount").html());
				});
				var computedWTax = (totalAmount * wtPercentageValue).toFixed(6);
				computedWTax = formatDecimalPlaces(computedWTax);
				$("#hdnWtAmount").val(accounting.unformat(computedWTax));
				$("#computedWTax").html(computedWTax);
				computeTotalAmountAndChange();
			},
			error : function(error) {
				wtPercentageValue = 0;
			},
			dataType: "text"
		});
	} else {
		$("#hdnWtAmount").val("0.00");
		$("#computedWTax").html("0.00");
		computeTotalAmountAndChange();
	}
}

// Handle the on change event 
function StockCodeSalesHandler () {
	// Return functions
	this.processStockCode = function (input) {
		console.log("processing stock code");
	};
};

function initializeTable (sellingPriceHeader) {
	var cashSaleItemsJson =JSON.parse($("#cashSaleItemsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$cashSaleTable = $("#cashSaleTable").editableItem({
		data: cashSaleItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "cashSaleId", "varType" : "int"},
			{"name" : "referenceObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "itemSrpId", "varType" : "int"},
			{"name" : "itemDiscountId", "varType" : "int"},
			{"name" : "itemAddOnId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "quantity", "varType" : "int"},
			{"name" : "srp", "varType" : "double"},
			{"name" : "origSrp", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "addOn", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "existingStock", "varType" : "double"},
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
			{"title" : "cashSaleId", 
				"cls" : "cashSaleId", 
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
				"visible" : false},
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
						computeWtax();
					};
				})},
			{"title" : "Description", 
				"cls" : "description tblLabelText", 
				"editor" : "label",
				"visible" : true, 
				"width" : "12%"},
			{"title" : "Existing <br> Stocks", 
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
			{"title" : "origQty", 
				"cls" : "origQty", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "origWarehouseId",
				"cls" : "origWarehouseId",
				"editor" : "hidden",
				"visible" : false}
		],
		"footer" : [
			{"cls" : "quantity"},
			{"cls" : "uom"},
			{"cls" : "addOn"},
			{"cls" : "srp"},
			{"cls" : "discountType"},
			{"cls" : "discount"},
			{"cls" : "taxType"},
			{"cls" : "vatAmount"},
			{"cls" : "amount"}
		],
	"disableDuplicateStockCode" : false
	});

	$("#cashSaleTable").on("change", ".discountType", function() {
		var discount = $(this).val();
		if(discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
		computeWtax();
	});

	$("#cashSaleTable").on("focus", ".tblInputNumeric", function() {
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
		computeWtax();
	});

	$("#cashSaleTable").on("focus", "tr", function() {
		computeWtax();
	});

	$("#cashSaleTable").on("blur", ".quantity", function() {
		computeWtax();
	});

	$("#cashSaleTable").on("change", ".warehouse", function() {
		assignESByWarehouse($(this));
	});

	$("#cashSaleTable").on("change", ".tblSelectClass", function() {
		computeWtax();
	});

	$("#cashSaleTable").on("focusout", ".tblSelectClass", function() {
		computeWtax();
	});
}

function totalQty() {
	var totalQty = 0;
	$("#cashSaleTable").find(" tbody tr ").each(function(row) {
		var quantity = $(this).find(".quantity").val();
		totalQty += accounting.unformat(quantity);
	});
	$("#cashSaleTable").find("tfoot tr").find(".quantity").html(formatDecimalPlaces(totalQty));
	$("#cashSaleTable tfoot").css("text-align", "right");
}

function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${cashSale.companyId}"+"'>"+
			"${cashSale.company.numberAndName}"+"</option>");
}

function getCustomer () {
	var companyId = $("#companyId").val();
	$.ajax({
		url: contextPath + "/getArCustomers/new?name="+$("#txtCustomer").val()+"&isExact=true"+
				"&companyId="+companyId,
		success : function(customer) {
			$("#spanCustomerError").text("");
			if (customer != null && customer[0] != undefined) {
				$("#arCustomerId").val(customer[0].id);
				$("#txtCustomer").val(customer[0].name);
			}
			filterCustomerAccts();
		},
		error : function(error) {
			$("#spanCustomerError").text("Invalid customer.");
			$("#txtCustomer").val("");
			$("#arCustomerAcctId").empty();
		},
		dataType: "json"
	});
}

function showCustomers () {
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getArCustomers/new?name="+$("#txtCustomer").val()+"&companyId="+companyId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#arCustomerId").val(ui.item.id);
			$("#spanCustomerError").text("");
			$(this).val(ui.item.name);
			filterCustomerAccts();
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					$("#spanCustomerError").text("");
					if (ui.item != null) {
						$(this).val(ui.item.name);
					}
					filterCustomerAccts();
				},
				error : function(error) {
					$("#spanCustomerError").text("Invalid customer.");
					$("#txtCustomer").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function filterCustomerAccts() {
	$("#arCustomerAcctId").empty();
	var customerName = $.trim($("#txtCustomer").val());
	if (customerName != "") {
		var customerId = $("#arCustomerId").val();
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
					// This is to remove any duplication.
					var found = [];
					$("#arCustomerAcctId option").each(function() {
						if($.inArray(this.value, found) != -1) 
							$(this).remove();
						found.push(this.value);
					});
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAcctId", optionParser, postHandler);
	} else {
		$("#arCustomerId").val("");
	}
}

function assignCustomerAcct (select) {
	selectCustomerAcct = $(select).val();
}

function updateTotal(totalAmount) {
	$("#hdnTotalAmountId").val(accounting.unformat(totalAmount));
}

var isSaving = false;
function saveCashSales() {
	if(isSaving == false) {
		isSaving = true;
		$("#cashSaleItemsJson").val($cashSaleTable.getData());
		$("#csArLinesJson").val($otherChargesTable.getData());
		$("#cash").val(accounting.unformat($("#cash").val()));
		doPostWithCallBack ("cashSalesForm", "form", function (data) {
			var refNo = $("#refNumber").val();
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var customer = $("#txtCustomer").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				if ("${cashSale.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val($("#hdnCompanyId").val());
				} else {
					var status = $("#txtCashSalesStatus").val();
					dojo.byId("editForm").innerHTML = data;
					$("#txtCashSalesStatus").val(status);
					disableAndSetCompany();
				}
				enableDisableCheck();
				$("#txtCustomer").val(customer);
				$("#refNumber").val(refNo);
				filterCustomerAccts();
				initializeOtherChargesTbl();
				initializeTable(sellingPriceHeader);
				loadWtaxAcctSettings(wtAcctSettingId);
				computeWtax();
				$("#cash").val(formatDecimalPlaces($("#cash").val()));
				isSaving = false;
			}
			$("#btnSaveCashSales").removeAttr("disabled");
		});
	}
}

function enableDisableCheck() {
	// If equals to check enable check number field
	var value = $("#arReceiptTypeId").val();
	if (value == 2) {
		$("#refNumber").removeAttr("readonly");
	} else {
		$("#refNumber").attr("readonly", "readonly");
		$("#refNumber").val("");
	}
}

function formatMoney (textboxId) {
	var money = formatDecimalPlaces($(textboxId).val());
	$(textboxId).val(money);
}

function computeTotalAmountAndChange() {
	hideValues();
	var subTotal = accounting.unformat($("#subTotal").html());
	var totalVat = accounting.unformat($("#totalVat").html());
	var wtaxAmount = $("#hdnWtAmount").val();
	$("#computedWTax").html(formatDecimalPlaces(wtaxAmount));

	var grandTotal = subTotal + totalVat - wtaxAmount;
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));

	var cashAmount = accounting.unformat($("#cash").val());
	var change = cashAmount - grandTotal;
	$("#spanChange").html(formatDecimalPlaces(change));
}

function computeTotalItemVat() {
	var totalItemVat = 0;
	$("#cashSaleTable").find(" tbody tr ").each(function(row) {
		var vat = accounting.unformat($(this).find(".vatAmount").html());
		totalItemVat += vat;
	});

	var totalOcVat = 0;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		var vat = accounting.unformat($(this).find(".vatAmount").html());
		totalOcVat += vat;
	});
	$("#totalVat").html(formatDecimalPlaces(totalOcVat + totalItemVat));
}

function handleCompanyOnChange() {
	$cashSaleTable.emptyTable();
	$otherChargesTable.emptyTable();
	computeWtax();
}

function assignCompany(companyId) {
	$("#hdnCompanyId").val(companyId);
}

function initializeOtherChargesTbl() {
	var otherChargesTable = JSON.parse($("#csArLinesJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: otherChargesTable,
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

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function() {
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
		computeWtax();
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function() {
		var $upAmount = $(this).closest("tr").find(".upAmount");
		$upAmount.val(formatDecimalPlaces($upAmount.val(), 4));

		computeWtax();
	});

	$("#otherChargesTable").on("change", ".taxType", function() {
		computeWtax();
	});
}

function companyOnchange(companyId){
	assignCompany(companyId);
	$("#otherChargesTable").html("");
	$("#cashSaleTable").html("");
	hideValues();
	initializeOtherChargesTbl();
	initializeTable(sellingPriceHeader);
}

function computeGrandTotal() {
	var itemTblTotal = $("#cashSaleTable").find("tfoot tr .amount").html();
	updateTotal(itemTblTotal);
	var otherChargesTotal = $("#otherChargesTable").find("tfoot tr .amount").html();
	var grandTotal = accounting.unformat(itemTblTotal) + accounting.unformat(otherChargesTotal);
	$("#subTotal").html(formatDecimalPlaces(grandTotal));
}

function hideValues() {
	$("#cashSaleTable").find("tfoot tr .uom").hide();
	$("#cashSaleTable").find("tfoot tr .addOn").hide();
	$("#cashSaleTable").find("tfoot tr .srp").hide();
	$("#cashSaleTable").find("tfoot tr .discountType").hide();
	$("#cashSaleTable").find("tfoot tr .discount").hide();
	$("#cashSaleTable").find("tfoot tr .taxType").hide();
	$("#cashSaleTable").find("tfoot tr .vatAmount").hide();
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
		<form:form method="POST" commandName="cashSale" id="cashSalesForm">
			<div class="modFormLabel">Cash Sales Order
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="csNumber"/>
			<form:hidden path="arCustomerId"/>
			<form:hidden path="cashSaleItemsJson" id="cashSaleItemsJson"/>
			<form:hidden path="csArLinesJson" id="csArLinesJson"/>
			<form:hidden path="totalAmount" id="hdnTotalAmountId"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="cashSaleTypeId" id="hdnSellingTypeId"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">
								<c:choose>
									<c:when test="${cashSale.cashSaleTypeId == 1}">CS No.</c:when>
									<c:otherwise>CS - W No.</c:otherwise>
								</c:choose>
							</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${cashSale.csNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${cashSale.formWorkflow.currentFormStatus.description}"/>
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
					<legend>Cash Sales Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<select id="companyId" class="frmSelectClass" onchange="companyOnchange(this.value);">
									<c:forEach var="company" items="${companies}" >
										<option value="${company.id}">${company.numberAndName}</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="companyId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Type</td>
							<td class="value">
								<form:select path="arReceiptTypeId" id="arReceiptTypeId" cssClass="frmSelectClass" 
									onchange="enableDisableCheck();">
									<form:options items="${cashSalesTypes}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="arReceiptTypeId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Check Number</td>
							<td class="value">
								<form:input path="refNumber" id="refNumber" class="inputSmall" readonly="true"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="refNumber" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" id="txtCustomer" class="input"
									onkeydown="showCustomers();" onkeyup="showCustomers();"
									onblur="getCustomer();" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<span id="spanCustomerError" class="error" style="margin-left: 12px;"></span>
								<form:errors path="arCustomerId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer Account</td>
							<td class="value">
								<form:select path="arCustomerAccountId" id="arCustomerAcctId" cssClass="frmSelectClass" onchange="assignCustomerAcct (this);">
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="arCustomerAccountId" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Sales Invoice No.</td>
							<td class="value"><form:input path="salesInvoiceNo" id="salesInvoiceNo" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="salesInvoiceNo" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Receipt Date</td>
							<td class="value">
								<form:input path="receiptDate" id="receiptDate" onblur="evalDate('receiptDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('receiptDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="receiptDate"	cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Maturity Date</td>
							<td class="value">
								<form:input path="maturityDate" id="maturityDate" onblur="evalDate('maturityDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('maturityDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="maturityDate" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<br>
				<fieldset class="frmField_set">
				<legend>Cash Sales Item Table</legend>
				<div id="cashSaleTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="csMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errorCSItems" cssClass="error"/>
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
							<form:errors path="csArLineMesage" cssClass="error"/>
						</td>
					</tr>
				</table>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
				</table>
				<!-- Cash and Change -->
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
					<tr>
						<td colspan="3">Cash/Check</td>
						<td colspan="2">
							<form:input path="cash" class="inputSmall" id="cash" onblur="computeTotalAmountAndChange();"
								cssStyle="font-size: 14; font-weight: bold; text-align: right; width: 97%;"/>
						</td>
					</tr>
					<tr>
						<td colspan="3"></td>
						<td></td>
						<td align="right">
							<form:errors path="cash" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td colspan="3">Change</td>
						<td></td>
						<td align="right">
							<span id="spanChange" class="footerTblCls"></span>
						</td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnCashSales" value="Save" onclick="saveCashSales();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>
