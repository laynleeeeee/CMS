<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Delivery Receipt - Goods form
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
	initializeSerializedItemTable();
	initializeNonSerializedDRItemTable();
	if (Number("${deliveryReceipt.id}") != 0) {
		$("#txtATWRefNumber").val("${deliveryReceipt.soNumber}");
		$("#aOpen").hide();
		$("#hdnTermDays").val("${deliveryReceipt.term.days}");
	}
	if ("${deliveryReceipt.formWorkflow.complete}" == "true" ||
			"${deliveryReceipt.formWorkflow.currentStatusId}" == 4) {
		$("#deliveryReceiptForm :input").attr("disabled", "disabled");
		$("#imgDate").hide();
		$("#imgDueDate").hide();
	}
});

function initializeSerializedItemTable() {
	var serialDrItemsJson =  JSON.parse($("#serialDrItemsJson").val());
	setupSerialItems(serialDrItemsJson);
}

function setupSerialItems(serialDrItemsJson) {
	$("#tblDRSerialItem").html("");
	var cPath = "${pageContext.request.contextPath}";
	$tblDRSerialItem = $("#tblDRSerialItem").editableItem({
		data: serialDrItemsJson,
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
				"width" : "11%", 
				"handler" : new SalesTableHandler(new function() {
					this.handleTotal = function (total) {
						// do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "13%"},
			{"title" : "warehouseId",
				"cls" : "warehouseId",
				"editor" : "text",
				"visible" : false},
			{"title" : "Warehouse",
				"cls" : "warehouse tblSelectClass",
				"editor" : "select",
				"visible" : true ,
				"width" : "15%"},
			{"title" : "Existing <br> Stocks",
				"cls" : "existingStocks tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Serial Number",
				"cls" : "serialNumber tblInputText",
				"editor" : "text",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%"},
			{"title" : "UOM",
				"cls" : "uom txtUOM",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Gross Price", 
				"cls" : "srp tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "discountValue", 
				"cls" : "discountValue", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : false},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false
	});

	$("#tblDRSerialItem").on("change", ".warehouse", function(){
			assignESByWarehouse($(this));
 	});

	$("#tblDRSerialItem").on("focus", ".tblInputNumeric", function() {
		var value = $(this).val();
		if ($.trim(value) != "") {
			$(this).val(accounting.unformat(value));
		}
		disableInputFields(this, "spanSerialItemError");
	});

	$("#tblDRSerialItem").on("focus", ".tblInputText", function() {
		disableInputFields(this, "spanSerialItemError");
	});

	// Disable stock code fields.
	$("#tblDRSerialItem tr td .stockCode").each(function() {
		$(this).attr("disabled", "disabled");
	});
}

function initializeNonSerializedDRItemTable() {
	var nonSerialDrItemsJson = JSON.parse($("#nonSerialDrItemsJson").val());
	setupNonSerialItems(nonSerialDrItemsJson);
}

function setupNonSerialItems(nonSerialDrItemsJson) {
	$("#tblDeliveryReceiptItem").html("");
	var cPath = "${pageContext.request.contextPath}";
	$tblDeliveryReceiptItem = $("#tblDeliveryReceiptItem").editableItem({
		data: nonSerialDrItemsJson,
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
				"width" : "11%", 
				"handler" : new SalesTableHandler(new function() {
					this.handleTotal = function (total) {
						// do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "13%"},
			{"title" : "warehouseId",
				"cls" : "warehouseId",
				"editor" : "text",
				"visible" : false},
			{"title" : "Warehouse",
				"cls" : "warehouse tblSelectClass",
				"editor" : "select",
				"visible" : true ,
				"width" : "15%"},
			{"title" : "Existing <br> Stocks",
				"cls" : "existingStocks tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%"},
			{"title" : "UOM",
				"cls" : "uom txtUOM",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Gross Price",
				"cls" : "srp tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "discountValue", 
				"cls" : "discountValue", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : false},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : false},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false
	});

	$("#tblDeliveryReceiptItem").on("change", ".warehouse", function(){
		assignESByWarehouse($(this));
	});
		
	$("#tblDeliveryReceiptItem").on("focus", ".tblInputNumeric", function() {
		var value = $(this).val();
		if ($.trim(value) != "") {
			$(this).val(accounting.unformat(value));
		}
		disableInputFields(this, "spanNonSerialItemError");
 	});
 
	$("#tblDeliveryReceiptItem").on("focus", ".tblInputText", function() {
		disableInputFields(this, "spanNonSerialItemError");
	});

	// Disable stock code fields.
	$("#tblDeliveryReceiptItem tr td .stockCode").each(function() {
		$(this).attr("disabled", "disabled");
	});
}

function disableInputFields(elem, spanIdName) {
	var $tr = $(elem).closest("tr");
	var $stockCode = $tr.find(".stockCode");
	$stockCode.attr("disabled", "disabled");
	if ($.trim($stockCode.val()) == "") {
		$("#"+spanIdName).text("Adding of new item/s without SO is not allowed.");
	}
}

var isSaving = false;
function saveDeliveryReceipt() {
	if (!isSaving) {
		var atwNumber = $.trim($("#txtATWRefNumber").val());
		if (atwNumber != "") {
			isSaving = true;
			// enabling item and line table
			$("#tblDeliveryReceiptItem :input").removeAttr("disabled");
			$("#tblDRSerialItem :input").removeAttr("disabled");
			// reset JSON table values
			$("#serialDrItemsJson").val($tblDRSerialItem.getData());
			$("#nonSerialDrItemsJson").val($tblDeliveryReceiptItem.getData());
			$("#btnSaveDeliveryReceipt").attr("disabled", "disabled");
			doPostWithCallBack ("deliveryReceiptForm", "form", function(data) {
				if (data.startsWith("saved")) {
					var objectId = data.split(";")[1];
					var formStatus = new Object();
					formStatus.objectId = objectId;
					updateTable (formStatus);
					dojo.byId("form").innerHTML = "";
				} else {
					var soId = $("#hdnSalesOrderId").val();
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
					var remarks = $("#remarks").val();
					if ("${deliveryReceipt.id}" == 0) {
						dojo.byId("form").innerHTML = data;
					} else {
						dojo.byId("editForm").innerHTML = data;
						$("#aOpen").hide();
					}
					$("#txtCurrentStatusId").val(txtCurrentStatusId);
					updateHeader(soId, atwNumber, companyId, companyName, customerId, customerName,
							customerAcctId, customerAcctName, termId, termName, termDays, remarks);
					initializeSerializedItemTable();
					initializeNonSerializedDRItemTable();
				}
				isSaving = false;
				$("#btnSaveDeliveryReceipt").removeAttr("disabled");
			});
		} else {
			$("#spanSalesOrderMsg").text("Sales order reference is required.");
		}
	}
}

function showATWReferences() {
	var drTypeId = $.trim($("#hdnDrTypeId").val());
	$("#divATWReferenceId").load(contextPath+"/deliveryReceipt/showReferenceForm?drTypeId="+drTypeId);
}

function loadSalesOrderReference(salesOrderId) {
	if (salesOrderId != "") {
		var drTypeId = $.trim($("#hdnDrTypeId").val());
		$.ajax({
			url: contextPath+"/deliveryReceipt/convertSOtoDR?salesOrderId="+salesOrderId+"&drTypeId="+drTypeId,
			success : function(dr) {
				$("#spanSalesOrderMsg").html("");
				$("#divATWReferenceId").html("");
				$("#aClose")[0].click();
				updateHeader(dr.salesOrderId, dr.atwNumber, dr.companyId, dr.company.name, dr.arCustomerId,
						dr.arCustomer.name, dr.arCustomerAccountId, dr.arCustomerAccount.name, dr.termId, dr.term.name,
						dr.term.days, dr.remarks);
				setupSerialItems(dr.serialDrItems);
				setupNonSerialItems(dr.nonSerialDrItems);
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

function updateHeader(atwId, atwNumber, companyId, companyName, arCustomerId,
		arCustomerName, arCustomerAcctId, arCustomerAcctName, termId, termName, termDays, remarks) {
	$("#hdnSalesOrderId").val(atwId);
	$("#txtATWRefNumber").val(atwNumber);
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
</script>
<style>
</style>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divATWReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divATWReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="deliveryReceipt" id="deliveryReceiptForm">
	<div class="modFormLabel">Delivery Receipt - Goods<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="sequenceNo"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="deliveryReceiptTypeId" id="hdnDrTypeId"/>
		<form:hidden path="serialDrItemsJson" id="serialDrItemsJson"/>
		<form:hidden path="nonSerialDrItemsJson" id="nonSerialDrItemsJson"/>
		<br>
		<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="labels">Sequence Number</td>
					<td class="value">
						<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"
									value="${deliveryReceipt.sequenceNo}"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Status</td>
					<c:set var="status" value="${deliveryReceipt.formWorkflow.currentFormStatus.description}"/>
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
		<legend>Delivery Receipt - Goods Header</legend>
			<table class="formTable">
				<tr>
					<td class="labels">* SO Reference</td>
					<td class="value">
						<form:hidden path="salesOrderId" id="hdnSalesOrderId"/>
						<input id="txtATWRefNumber" readonly="readonly"/>
						<a href="#container" id="aOpen" data-reveal-id="divATWReferenceId" class="link_button"
							onclick="showATWReferences();">Browse SO</a>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="authorityToWithdrawId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><span id="spanSalesOrderMsg" class="error"></span></td>
				</tr>
				<tr>
					<td class="labels">* Company</td>
					<td class="value">
						<form:hidden path="companyId" id="companyId"/>
						<span id="spCompanyName">${deliveryReceipt.company.name}</span>
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
						<span id="spCustomerName">${deliveryReceipt.arCustomer.name}</span>
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
						<span id="spCustomerAcctName">${deliveryReceipt.arCustomerAccount.name}</span>
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
						<span id="spTermName">${deliveryReceipt.term.name}</span>
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
		<fieldset id="serialItemsTbl" class="frmField_set hidden">
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
				<tr>
					<td>
						<span id="spanSerialItemError" class="error"></span>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset id="nonSerialItemsTbl" class="frmField_set">
			<legend>Goods Table</legend>
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
				<tr>
					<td>
						<span id="spanNonSerialItemError" class="error"></span>
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
		<table class="frmField_set">
			<tr>
				<td align="right"><input type="button" id="btnSaveDeliveryReceipt"
						value="Save" onclick="saveDeliveryReceipt();" /></td>
			</tr>
		</table>
		</div>
	</form:form>
</div>
</body>
</html>