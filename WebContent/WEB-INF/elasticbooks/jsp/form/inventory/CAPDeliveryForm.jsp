<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Customer Advance Payment Delivery form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
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
	if ("${capDelivery.id}" != 0) {
		disableFields();
		initializeOtherChargesTbl();
		initializeTable();
		updateOChargesAmt();
		computeGrandTotal();
	}
});

function disableFields() {
	$("#btnReference").attr("disabled", true);
	var isComplete = "${capDelivery.formWorkflow.complete}";
	var currentStatusId = "${capDelivery.formWorkflow.currentStatusId}";
	if(isComplete == "true" || currentStatusId == 4) {
		$("#btnCapDelivery").attr("disabled", true);
		$("#imgDeliveryDate").attr("disabled", true);
		$("#deliveryDate").attr("readonly", true);
		$("#capDeliveryForm :input").attr("disabled","disabled");
		$("#deliveryItemsTable :input").attr("disabled","disabled");
	}
}

function initializeTable () {
	var deliveryItemsJson = JSON.parse($("#deliveryItemsJson").val());
	setupTableData(deliveryItemsJson);
}

function setupTableData (deliveryItemsJson) {
	var cPath = "${pageContext.request.contextPath}";
	$deliveryTable = $("#deliveryItemsTable").editableItem({
		data: deliveryItemsJson,
		jsonProperties: [
		                 {"name" : "id", "varType" : "int"},
		                 {"name" : "customerAdvancePaymentId", "varType" : "int"},
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
		                 {"name" : "cashSaleItemId", "varType" : "int"},
		                 {"name" : "unitCost", "varType" : "double"},
		                 {"name" : "refQuantity", "varType" : "double"},
		                 {"name" : "origQty", "varType" : "double"},
		                 {"name" : "origWarehouseId", "varType" : "int"}
		                 ],
		contextPath: cPath,
		header: [
			{"title" : "id", 
				"cls" : "id", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "customerAdvancePaymentId", 
				"cls" : "customerAdvancePaymentId", 
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
				"width" : "13%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						computeGrandTotal();
					};
				})},
			{"title" : "Description", 
				"cls" : "description tblLabelText", 
				"editor" : "label",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "Existing <br> Stock", 
				"cls" : "existingStock tblLabelNumeric", 
				"editor" : "label",
				"visible" : true, 
				"width" : "7%"},
			{"title" : "warehouseId", 
				"cls" : "warehouseId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Warehouse", 
				"cls" : "warehouse tblSelectClass", 
				"editor" : "select",
				"visible" : true ,
				"width" : "10%"},
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
			{"title" : "cashSaleItemId", 
				"cls" : "cashSaleItemId", 
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

	$("#deliveryItemsTable").on("change", ".discountType", function(){
		var discount = $(this).val();
		if(discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
	});

	$("#deliveryItemsTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#deliveryItemsTable").on("blur", ".tblInputNumeric", function(){
		computeGrandTotal();
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

function showReferencePopup() {
	referenceWindow = window.open(contextPath + "/capDelivery/1/loadReferences","popup","width=1000,height=500,scrollbars=1,resizable=no," +
	"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
}

function loadReference(referenceId) {
	$("#deliveryItemsTable").html("");
	$.ajax({
		url: contextPath + "/capDelivery?capId="+referenceId,
		success : function(capDev) {
			deliveryObj = capDev;
			console.log(capDev);
			updateHeader(capDev.customerAdvancePaymentId, capDev.companyId, capDev.company.numberAndName,
					capDev.arCustomerId, capDev.arCustomer.name, capDev.arCustomerAcctId,
					capDev.arCustomerAccount.name, capDev.salesInvoiceNo);
			setUpOtherCharges(capDev.deliveryArLines);
			setupTableData(capDev.deliveryItems);
			updateOChargesAmt();
			computeGrandTotal();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	if (referenceWindow != null)
		referenceWindow.close();
	referenceWindow = null;
}

function updateHeader(capId, companyId, companyName, customerId, customerName,
		customerAcctId, customerAcctName, salesInvoiceNo) {
	$("#customerAdvancePaymentId").val(capId);
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
	var $itemTblTotal = $("#deliveryItemsTable").find("tfoot tr .amount");
	var $otherChargesTotal = $("#otherChargesTable").find("tfoot tr .amount");
	var grandTotal = accounting.unformat($itemTblTotal.html())
				+ accounting.unformat($otherChargesTotal.html());
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
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				loadHeader();
				if("${capDelivery.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableFields();
				}
				initializeOtherChargesTbl();
				initializeTable();
				updateOChargesAmt();
				computeGrandTotal();
				disableTableFields ();
				isSaving = false;
			}
			$("#btnCapDelivery").removeAttr("disabled");
		});
	}
}

function disableTableFields () {
	$("#deliveryItemsTable tbody tr").each(function (i) {
		var capId = $(this).find(".customerAdvancePaymentId").val();
		if (typeof capId != "undefined" && capId != null 
				&& capId != "" && capId != 0) {
			$(this).find(".stockCode").attr("readonly", "readonly");
			$(this).find(".warehouse").attr("disabled", "disabled");
			$(this).find(".discountType").attr("disabled", "disabled");
		}
	});
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="capDelivery" id="capDeliveryForm">
			<div class="modFormLabel">Paid in Advance Delivery<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id" id="hdnFormId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="capdNumber"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="customerAdvancePaymentId"/>
			<form:hidden path="companyId" id="companyId"/>
			<form:hidden path="arCustomerId" id="arCustomerId"/>
			<form:hidden path="arCustomerAcctId" id="arCustomerAcctId"/>
			<form:hidden path="deliveryItemsJson" id="deliveryItemsJson"/>
			<form:hidden path="deliveryArLinesJson" id="deliveryArLinesJson"/>
			<form:hidden path="customerAdvancePaymentTypeId"/>
			<form:hidden path="ebObjectId"/>
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

				<!-- Grand Total -->
				<table class="frmField_set">
					<tr>
						<td>Grand Total</td>
						<td align="right"><span id="grandTotal">0.0</span></td>
					</tr>
					<tr>
						<td><form:errors path="errorMessage" cssClass="error"/><span id="errorMessage" class="error"></span></td>
					</tr>
				</table>
				
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