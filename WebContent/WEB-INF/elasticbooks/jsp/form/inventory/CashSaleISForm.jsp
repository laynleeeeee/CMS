<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Cash Sales - Individual Selection form.
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
var selectCustomerAcct = "${cashSale.arCustomerAccountId}";
var selectCompany = "${cashSale.companyId}";
var currentCustAcctId = 0;
var $cashSaleTable = null;
$(document).ready (function () {
	disableAndSetCompany();
	filterCustomerAccts();
	initializeOtherChargesTbl();
	initializeTable ();
	if ("${cashSale.id}" != 0) {
		updateOChargesAmt();
		disableAndSetCompany();
		var totalAmount = $("#cashSaleTable").find("tfoot tr").find(".amount").html();
		updateTotal(totalAmount);
		computeGrandTotal();
	}
	enableDisableCheck();
	hideValues();

	if ("${cashSale.formWorkflow.complete}" == "true" || "${cashSale.formWorkflow.currentStatusId}" == 4) {
		$("#btnSaveCashSales").attr("disabled", "disabled");
		$("#cashSalesForm :input").attr("disabled","disabled");
	}
});

//Handle the on change event 
function StockCodeSalesHandler () {
	// Return functions
	this.processStockCode = function (input) {
		console.log("processing stock code");
	};
};

function initializeTable () {
	var cashSaleItemsJson =JSON.parse($("#cashSaleItemsJson").val());
	var cPath = "${pageContext.request.contextPath}";

	$cashSaleTable = $("#cashSaleTable").editableItem({
		data: cashSaleItemsJson,
		jsonProperties: [
		                 {"name" : "id", "varType" : "int"},
		                 {"name" : "cashSaleId", "varType" : "int"},
		                 {"name" : "warehouseId", "varType" : "int"},
		                 {"name" : "referenceObjectId", "varType" : "int"},
		                 {"name" : "origRefObjectId", "varType" : "int"},
		                 {"name" : "ebObjectId", "varType" : "int"},
		                 {"name" : "itemId", "varType" : "int"},
		                 {"name" : "itemSrpId", "varType" : "int"},
		                 {"name" : "itemDiscountId", "varType" : "int"},
		                 {"name" : "itemAddOnId", "varType" : "int"},
		                 {"name" : "stockCode", "varType" : "string"},
		                 {"name" : "itemBagQuantity", "varType" : "double"},
		                 {"name" : "quantity", "varType" : "double"},
		                 {"name" : "unitCost", "varType" : "double"},
		                 {"name" : "srp", "varType" : "double"},
		                 {"name" : "origSrp", "varType" : "double"},
		                 {"name" : "discount", "varType" : "double"},
		                 {"name" : "addOn", "varType" : "double"},
		                 {"name" : "amount", "varType" : "double"},
		                 {"name" : "origQty", "varType" : "double"},
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
				"visible": false},
			{"title" : "unitCost",
				"cls" : "unitCost",
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
						updateTotal(total);
						computeGrandTotal();
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
			{"title" : "Available <br> Bags/Stocks",
				"cls" : "availableStock tblSelectClass",
				"editor" : "select",
				"visible" : true, 
				"width" : "18%"},
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
			{"title" : "Add On", 
				"cls" : "addOn tblSelectClass", 
				"editor" : "select",
				"visible" : true,
				"width" : "10%"},
			{"title" : "SRP", 
				"cls" : "srp tblLabelNumeric", 
				"editor" : "label",
				"visible" : true, 
				"width" : "8%"},
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
				"width" : "10%"},
			{"title" : "Discount <br>(Computed)", 
				"cls" : "discount tblLabelNumeric", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
			{"title" : "Amount", 
				"cls" : "amount tblLabelNumeric", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
			{"title" : "origQty", 
				"cls" : "origQty", 
				"editor" : "hidden",
				"visible" : false},
		],
		"footer" : [
				{"cls" : "quantity"},
				{"cls" : "uom"},
				{"cls" : "addOn"},
				{"cls" : "srp"},
				{"cls" : "discountType"},
				{"cls" : "discount"},
		        {"cls" : "amount"}
	    ],
	    "disableDuplicateStockCode" : false
	});

	$("#cashSaleTable").on("change", ".availableStock", function(){
		setReferenceDetails($(this));
	});

	$("#cashSaleTable").on("change", ".discountType", function(){
		var discount = $(this).val();
		if(discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
	});

	$("#cashSaleTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#cashSaleTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateQty($tr, $("#companyId").val());
	});

	$("#cashSaleTable").on("focus", "tr", function(){
		computeGrandTotal();
	});

	$("#cashSaleTable").on("focusout", ".quantity", function(){
		computeGrandTotal();
	});

	$("#cashSaleTable").on("change", ".warehouse", function(){
		var formId = $("#hdnFormId").val();
		var itemId = $(this).closest("tr").find(".itemId").val();
		var ebObjectId = $(this).closest("tr").find(".ebObjectId").val();
		var $refObjectId = $(this).closest("tr").find(".referenceObjectId");
		var $orig = $(this).closest("tr").find(".origRefObjectId");
		if(ebObjectId != "" && ebObjectId != 0 && typeof ebObjectId != "undefined"
			&& ($($orig).length > 0 && $($orig).val() == "")) {
			populateAvailableStock ($(this), $(this).val(), itemId, $($refObjectId).val(), formId, ebObjectId);
		} else {
			populateAvailableStock ($(this), $(this).val(), itemId, null, formId, ebObjectId);
		}
	});

	$("#cashSaleTable").on("change", ".addOn", function(){
		computeGrandTotal();
	});

	$("#cashSaleTable").on("focusout", ".addOn", function(){
		computeGrandTotal();
	});

	$("#cashSaleTable").on("mouseleave", ".addOn", function(){
		computeGrandTotal();
	});

	$("#cashSaleTable").on("change", ".discount", function(){
		computeGrandTotal();
	});

	$("#cashSaleTable").on("focusout", ".discount", function(){
		computeGrandTotal();
	});

	$("#cashSaleTable").on("mouseleave", ".discount", function(){
		computeGrandTotal();
	});
}

function computeAndUpdateTAmount () {
	var totalAmount = 0;
	$("#cashSaleTable tbody tr").each(function (i) {
		var srp = accounting.unformat($(this).find(".srp").text());
		var discount = accounting.unformat($(this).find(".discount").text());
		var amount = parseFloat(srp) - parseFloat(discount);
		$(this).find(".amount").text(accounting.formatMoney(amount));
		totalAmount += amount;
	});
	 $("#cashSaleTable").find("tfoot tr").find(".amount").html(accounting.formatMoney(totalAmount));
	 updateTotal (totalAmount);
}

function totalQty() {
	var totalQty = 0;
	$("#cashSaleTable").find(" tbody tr ").each(function(row) {
		var quantity = $(this).find(".quantity").val();
		totalQty += accounting.unformat(quantity);
	});
	$("#cashSaleTable").find("tfoot tr").find(".quantity").html(accounting.formatMoney(totalQty));
	$("#cashSaleTable tfoot").css("text-align", "right");
}

function disableAndSetCompany() {
	//Disable and set company
	if ("${cashSale.id}" != 0) {
		$("#companyId").attr("disabled","disabled");
		$("#companyId").append("<option selected='selected' value='"+"${cashSale.companyId}"+"'>"+
				"${cashSale.company.numberAndName}"+"</option>");
	}
}

function enableCompany() {
	//Enable  company
	$("#companyId").attr("disabled",false);
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

function filterCustomerAccts(){
	$("#arCustomerAcctId").empty();

	if ($.trim($("#txtCustomer").val()) == "")
		$("#arCustomerId").val("");
	else {
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
	}
}

function assignCustomerAcct (select) {
	selectCustomerAcct = $(select).val();
}

function updateTotal (totalAmount) {
	$("#totalAmount").val(accounting.unformat(totalAmount));
}

var isSaving = false;
function saveCashSales() {
	if(isSaving == false) {
		isSaving = true;
		enableCompany();
		$("#btnSaveCashSales").attr("disabled", "disabled");
		$("#cashSaleItemsJson").val($cashSaleTable.getData());
		$("#csArLinesJson").val($otherChargesTable.getData());
		parseValues();
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
				var status = $("#txtCashSalesStatus").val();
				if("${cashSale.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				}
				else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtCashSalesStatus").val(status);
				}
				enableDisableCheck();
				$("#txtCustomer").val(customer);
				disableAndSetCompany();

				$("#refNumber").val(refNo);
				filterCustomerAccts();
				initializeOtherChargesTbl();
				initializeTable();
				updateOChargesAmt();
				computeGrandTotal($("#cash").val());
				$("#cash").val(accounting.formatMoney($("#cash").val()));
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

function parseValues (){
	$("#cash").val(accounting.unformat($("#cash").val()));
}

function formatMoney (textboxId) {
	var money = accounting.formatMoney($(textboxId).val());
	$(textboxId).val(money);
}

function computeChange() {
	hideValues();

	var totalAmount = $("#totalAmount").val();
	var otherCharges = $("#hdnOChargesAmt").val();
	totalAmount =  accounting.unformat(totalAmount) + accounting.unformat(otherCharges);

	var amount = $("#cash").val();
	var change = accounting.unformat(amount) - totalAmount;
	$("#cash").val( accounting.formatMoney(amount));
	$("#txtChange").val(accounting.formatMoney(change));
	totalQty();
}

function handleCompanyOnChange() {
	computeGrandTotal();
	hideValues();
	$("#txtCustomer").val("");
	$("#arCustomerId").val(null);
	$("#arCustomerAcctId").empty();
	$("#arCustomerAcctId").val(null);
	$("#txtChange").val("0.00");
	assignCompany(companyId);
	$("#otherChargesTable").html("");
	$("#cashSaleTable").html("");
	initializeOtherChargesTbl();
	initializeTable();
}

function initializeOtherChargesTbl() {
	console.log("initializing other charges.");
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
		computeGrandTotal();
	});
}

function computeGrandTotal() {
	var $itemTblTotal = $("#cashSaleTable").find("tfoot tr .amount");
	var $otherChargesTotal = $("#otherChargesTable").find("tfoot tr .amount");
	var grandTotal = accounting.unformat($itemTblTotal.html())
				+ accounting.unformat($otherChargesTotal.html());
	$("#grandTotal").html(accounting.formatMoney(grandTotal));
	computeChange();
}

function hideValues() {
	$("#cashSaleTable").find("tfoot tr .uom").hide();
	$("#cashSaleTable").find("tfoot tr .addOn").hide();
	$("#cashSaleTable").find("tfoot tr .srp").hide();
	$("#cashSaleTable").find("tfoot tr .discountType").hide();
	$("#cashSaleTable").find("tfoot tr .discount").hide();
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="cashSale" id="cashSalesForm">
			<div class="modFormLabel">Cash Sales - IS
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<input type="hidden" value="4" id="hdnOrTypeId">
			<form:hidden path="ebObjectId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="csNumber"/>
			<form:hidden path="arCustomerId"/>
			<form:hidden path="cashSaleItemsJson" id="cashSaleItemsJson"/>
			<form:hidden path="csArLinesJson" id="csArLinesJson"/>
			<form:hidden path="totalAmount"/>
			<form:hidden path="cashSaleTypeId" id="hdnSellingTypeId"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">CS - IS No.</td>
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

				<!-- Grand Total -->
				<table class="frmField_set">
					<tr>
						<td>Grand Total</td>
						<td align="right"><span id="grandTotal">0.0</span></td>
					</tr>
				</table>

				<!-- Cash and Change -->
				<br>
				<table class="frmField_set">
					<tr>
						<td style="font-size: 14; font-weight: bold;">Cash/Check</td>
						<td align="right">
							<form:input path="cash" class="input" 
								style=" text-align: right; font-size: 14; font-weight: bold;" 
								onblur="computeChange();"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2" align="right">
							<form:errors path="cash" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td style="font-size: 14; font-weight: bold;">Change</td>
						<td align="right">
							<input id="txtChange" class="input" readonly="readonly" 
								style="text-align: right; border: none; 
								font-size: 14; font-weight: bold;"/>
						</td>
					</tr>
				</table>
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