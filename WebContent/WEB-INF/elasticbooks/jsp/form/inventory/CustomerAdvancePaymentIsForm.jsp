<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Form for Customer Advance Payment - Individual Selection.
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
var selectCustomerAcct = "${customerAdvancePayment.arCustomerAccountId}";
var selectCompany = "${customerAdvancePayment.companyId}";
var currentCustAcctId = 0;
var $capTable = null;
var CAP_TYPE_ID = null;
$(document).ready (function () {
	CAP_TYPE_ID = "${customerAdvancePayment.customerAdvancePaymentTypeId}";
	if ("${customerAdvancePayment.id}" != 0) {
		$("#cash").val('<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerAdvancePayment.cash}" />');
	} else {
		$("#hdnCompanyId").val($("#companyId").val());
	}
	disableAndSetCompany();
	filterCustomerAccts();
	initializeOtherChargesTbl();
	initializeTable ();
	if ("${customerAdvancePayment.id}" != 0) {
		updateOChargesAmt();
		disableAndSetCompany();
		var totalAmount = $("#capTable").find("tfoot tr").find(".amount").html();
		updateTotal(totalAmount);
		computeChange();
	}
	enableDisableCheck();

	if ("${customerAdvancePayment.formWorkflow.complete}" == "true" || 
			"${customerAdvancePayment.formWorkflow.currentStatusId}" == 4) {
		$("#customerAdvancePaymentsForm :input").attr("disabled","disabled");
	}
});

function initializeTable () {
	var capItemsJson = JSON.parse($("#capItemsJson").val());
	var cPath = "${pageContext.request.contextPath}";

	$capTable = $("#capTable").editableItem({
		data: capItemsJson,
		jsonProperties: [
                 {"name" : "id", "varType" : "int"},
                 {"name" : "customerAdvancePaymentId", "varType" : "int"},
                 {"name" : "ebObjectId", "varType" : "int"},
                 {"name" : "referenceObjectId", "varType" : "int"},
                 {"name" : "origRefObjectId", "varType" : "int"},
                 {"name" : "warehouseId", "varType" : "int"},
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
                 {"name" : "origQty", "varType" : "double"}
			],
		contextPath: cPath,
		header: [{"title" : "id", 
			"cls" : "id", 
			"editor" : "hidden",
			"visible" : false },
		{"title" : "customerAdvancePaymentId", 
			"cls" : "customerAdvancePaymentId", 
			"editor" : "hidden",
			"visible" : false },
		{"title" : "ebObjectId",
			"cls" : "ebObjectId",
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
		{"title" : "itemId",
			"cls" : "itemId",
			"editor" : "hidden",
			"visible" : false },
		{"title" : "Stock Code",
			"cls" : "stockCode tblInputText",
			"editor" : "text",
			"visible" : true,
			"width" : "13%",
			"handler" : new SalesTableHandler (new function () {
				this.handleTotal = function (total) {
					updateTotal(total);
					computeChange();
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
		{"title" : "Available <br> Bags/Stocks", 
			"cls" : "availableStock tblSelectClass", 
			"editor" : "select",
			"visible" : true, 
			"width" : "7%"},
		{"title" : "Bags",
			"cls" : "itemBagQuantity tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "8%"},
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
			"visible" : false}
	],
		"footer" : [
		        {"cls" : "amount"}
	    ],
	    "disableDuplicateStockCode" : false
	});

	$("#capTable").on("change", ".discountType", function(){
		var discount = $(this).val();
		if(discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
	});

	$("#capTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#capTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateQty($tr, $("#companyId").val());
	});

	$("#capTable").on("change", ".warehouse", function(){
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

	$("#capTable").on("change", ".availableStock", function(){
		setReferenceDetails($(this));
	});

	$("#capTable").on("change", ".addOn, .discount", function(){
		computeChange();
	});

	$("#capTable").on("focusout", ".addOn, .discount, .quantity", function(){
		computeChange();
	});

	$("#capTable").on("mouseleave", ".addOn, .discount", function(){
		computeChange();
	});

	$("#capTable").on("focus", "tr", function(){
		computeChange();
	});
}

function disableAndSetCompany() {
	//Disable and set company
	if ("${customerAdvancePayment.id}" != 0) {
		$("#companyId").attr("disabled","disabled");
		$("#companyId").append("<option selected='selected' value='"+"${customerAdvancePayment.companyId}"+"'>"+
				"${customerAdvancePayment.company.numberAndName}"+"</option>");
	}
}

function getCustomer () {
	var companyId = $("#companyId").val();
	$.ajax({
		url: contextPath + "/getArCustomers/new?name="+$("#txtCustomer").val()+"&isExact=true"+
				"&companyId="+companyId,
		success : function(customer) {
			$("#spanCustomerError").text("");
			if (customer != null && customer[0] != undefined) {
				$("#hdnArCustomerId").val(customer[0].id);
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
			$("#hdnArCustomerId").val(ui.item.id);
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
function saveCAPs() {
	if(isSaving == false) {
		isSaving = true;
		$("#capItemsJson").val($capTable.getData());
		$("#capArLinesJson").val($otherChargesTable.getData());
		parseValues();
		doPostWithCallBack ("customerAdvancePaymentsForm", "form", function (data) {
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
				var status = $("#txtCapStatus").val();
				if("${customerAdvancePayment.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val($("#hdnCompanyId").val());
				}
				else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtCapStatus").val(status);
				}
				enableDisableCheck();
				$("#txtCustomer").val(customer);
				disableAndSetCompany();

				$("#refNumber").val(refNo);
				filterCustomerAccts();
				initializeOtherChargesTbl();
				initializeTable();
				updateOChargesAmt();
				computeChange($("#cash").val());
				$("#cash").val(accounting.formatMoney($("#cash").val()));
				isSaving = false;
			}
			$("#btnSaveCAPs").removeAttr("disabled");
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
	}
}

function parseValues (){
	$("#cash").val(accounting.unformat($("#cash").val()));
}

function formatMoney (textboxId) {
	var money = accounting.formatMoney($(textboxId).val());
	$(textboxId).val(money.replace(/[$]/gi, ''));
}

function computeChange() {
	computeGrandTotal();
	var totalAmount = $("#totalAmount").val();
	var otherCharges = $("#hdnOChargesAmt").val();
	totalAmount =  accounting.unformat(totalAmount) + accounting.unformat(otherCharges);

	var amount = $("#cash").val();
	var change = accounting.unformat(amount) - totalAmount;
	$("#cash").val( accounting.formatMoney(amount));
	$("#txtChange").val(accounting.formatMoney(change));
}

function handleCompanyOnChange() {
	$capTable.emptyTable();
	clearCustAndAccount();
	computeGrandTotal();
}

function clearCustAndAccount() {
	$("#hdnArCustomerId").val("");
	$("#txtCustomer").val("");
	$("#arCustomerAcctId").empty();
}

function assignCompany(companyId) {
	$("#otherChargesTable").html("");
	$("#capTable").html("");
	$("#hdnCompanyId").val(companyId);
	clearCustAndAccount();
	initializeOtherChargesTbl();
	initializeTable();
}

function initializeOtherChargesTbl() {
	var otherChargesTable = JSON.parse($("#capArLinesJson").val());

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
		computeChange();
	});
}

function computeGrandTotal() {
	var $itemTblTotal = $("#capTable").find("tfoot tr .amount");
	var $otherChargesTotal = $("#otherChargesTable").find("tfoot tr .amount");
	var grandTotal = accounting.unformat($itemTblTotal.html())
				+ accounting.unformat($otherChargesTotal.html());
	$("#grandTotal").html(accounting.formatMoney(grandTotal));
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="customerAdvancePayment" id="customerAdvancePaymentsForm">
			<div class="modFormLabel">Customer Advance Payment - IS<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="capNumber"/>
			<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
			<form:hidden path="capItemsJson" id="capItemsJson"/>
			<form:hidden path="capArLinesJson" id="capArLinesJson"/>
			<form:hidden path="totalAmount"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="customerAdvancePaymentTypeId" id="hdnCustomerAdvPaymentTypeId"/>
			<input type="hidden" value="11" id="hdnOrTypeId">
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">CAP - IS No.</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${customerAdvancePayment.capNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${customerAdvancePayment.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtCapStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Customer Advance Payment Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<select id="companyId" class="frmSelectClass" onchange="assignCompany(this.value);">
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
									<form:options items="${capTypes}" itemLabel="name" itemValue="id" />
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
									onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="getCustomer();" />
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
				<legend>Customer Advance Payment Item Table</legend>
				<div id="capTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="csMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errorCAPItems" cssClass="error"/>
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
							<form:errors path="capArLineMesage" cssClass="error"/>
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
								style="text-align: right; font-size: 14; font-weight: bold;"
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
						<td align="right"><input type="button" id="btnSaveCAPs" value="Save" onclick="saveCAPs();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>