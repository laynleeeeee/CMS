
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

.footerTblCls {
	text-align: right;
	font-size: 14;
	font-weight: bold;
	padding: 0;
	width: 350px;
}
</style>
<script type="text/javascript">
var $cashSaleReturnTable = null;
var csrReferenceWindow = null;
var csrObj = null;
var sellingPriceHeader = null;
$(document).ready (function () {
	var sellingTypeId = "${cashSaleReturn.cashSaleTypeId}";
	sellingPriceHeader = sellingTypeId == 2 ? "WP" : "SRP";
	if ("${cashSaleReturn.id}" != 0) { 
		loadHeader();
		initializeOtherChargesTbl(JSON.parse($("#csrArLinesJson").val()));
		initializeTable(sellingPriceHeader);
		disableFields();
		disableTableFields ();
		updateOChargesAmt();
		computeGrandTotal();
	}

	if ("${cashSaleReturn.formWorkflow.complete}" == "true"
			|| "${cashSaleReturn.formWorkflow.currentStatusId}" == 4) {
		$("#cashSalesReturnForm :input").attr("disabled","disabled");
	}
});

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

	$("#cashSaleReturnTable").on("change", ".discountType", function(){
		var discount = $(this).val();
		if (discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
	});

	$("#cashSaleReturnTable").on("change", ".warehouse", function(){
		assignESByWarehouse($(this));
	});

	$("#cashSaleReturnTable").on("focus", ".quantity", function(){
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#cashSaleReturnTable").on("blur", ".quantity", function(){
		computeGrandTotal();
	});
}

function getTotalAmount () {
	var totalAmount = $("#cashSaleReturnTable").find("tfoot tr").find(".amount").html();
	$("#totalAmount").val(accounting.unformat(totalAmount));
}

function saveCashSales() {
	$("#btnSaveCashSales").attr("disabled", true);
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
			dojo.byId("form").innerHTML = "";
		} else {
			var status = $("#txtCashSalesReturnStatus").val();
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
			disableTableFields();
			updateOChargesAmt();
			computeGrandTotal();
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
	var money = accounting.formatMoney($(textboxId).val());
	$(textboxId).val(money.replace(/[$]/gi, ''));
}

function showCsReference() {
	var url = "/cashSaleReturn/"+"${cashSaleReturn.cashSaleTypeId}"+"/csReference";
	csrReferenceWindow = window.open(contextPath + url,"popup","width=1000,height=500,scrollbars=1,resizable=no," +
	"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
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
			disableTableFields ();
			updateOChargesAmt();
			computeGrandTotal();
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
		var cashSaleItemId = $(this).find(".cashSaleItemId").val();
		var refCashSaleReturnItemId = $(this).find(".refCashSaleReturnItemId").val();
		var hasRefCSItemId = typeof cashSaleItemId != "undefined" && cashSaleItemId != null
			&& cashSaleItemId != "" && cashSaleItemId != 0;
		var hasRefCSRItemId = typeof refCashSaleReturnItemId != "undefined"
			&& refCashSaleReturnItemId != null && refCashSaleReturnItemId != ""
			&& refCashSaleReturnItemId != 0;
		if (hasRefCSItemId || hasRefCSRItemId) {
			$(this).find(".stockCode").attr("readonly", "readonly");
			$(this).find(".warehouse").attr("disabled", "disabled");
			$(this).find(".discountType").attr("disabled", "disabled");
			$(this).find(".addOn").attr("disabled", "disabled");
			$(this).find(".taxType").attr("disabled", "disabled");
		}
	});
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

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function(){
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function(){
		updateOChargesAmt();
		computeGrandTotal();
	});

	$("#otherChargesTable").on("change", ".taxType", function(){
		updateOChargesAmt();
		computeGrandTotal();
	});
}

function computeGrandTotal() {
	computeTotalItemVat();
	var totalVat = accounting.unformat($("#totalVat").html());
	var totalItemAmt = accounting.unformat($("#cashSaleReturnTable").find("tfoot tr .amount").html());
	var totalOtherCharges = accounting.unformat($("#otherChargesTable").find("tfoot tr .amount").html());
	var subTotal = totalItemAmt + totalOtherCharges;
	var totalVat = accounting.unformat($("#totalVat").html());
	$("#subTotal").html(accounting.formatMoney(subTotal));
	var grandTotal = subTotal + totalVat;
	$("#grandTotal").html(accounting.formatMoney(grandTotal));
}

function computeTotalItemVat() {
	var totalItemVat = 0;
	$("#cashSaleReturnTable").find(" tbody tr ").each(function(row) {
		var quantity = accounting.unformat($(this).find(".quantity").val());
		var vat = accounting.unformat($(this).find(".vatAmount").html());
		if (quantity < 0) {
			vat = vat * -1; // negate the if return
		}
		totalItemVat += vat;
	});

	var totalOcVat = 0;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		totalOcVat += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#totalVat").html(accounting.formatMoney(totalOcVat + totalItemVat));
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
				<br>
				<!-- Grand Total -->
				<table class="frmField_set">
					<tr>
						<td style="font-size: 14; font-weight: bold;">Sub Total</td>
						<td align="right">
							<span id="subTotal" class="footerTblCls"></span>
						</td>
					</tr>
					<tr>
						<td style="font-size: 14; font-weight: bold;">Total VAT</td>
						<td align="right">
							<span id="totalVat" class="footerTblCls"></span>
						</td>
					</tr>
					<tr>
						<td style="font-size: 14; font-weight: bold;">Total Amount Due</td>
						<td align="right">
							<span id="grandTotal" class="footerTblCls"></span>
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