<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Cash Sales Return - IS Form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.processinghandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
var $cashSaleReturnTable = null;
var csrReferenceWindow = null;
$(document).ready (function () {
	if("${cashSaleReturn.id}" != 0) {
		$("#btnCsReference").attr("disabled", "disabled");
		updateHeader();
		setUpDataTable();
		disableTableFields();
		if ("${cashSaleReturn.formWorkflow.complete}" == "true" || "${cashSaleReturn.formWorkflow.currentStatusId}" == 4) {
			$("#btnSave").attr("disabled", "disabled");
		}
	}
});

function setUpDataTable() {
	var csrItemsJson = JSON.parse($("#cashSaleReturnItemsJson").val());
	initializeTable(csrItemsJson);
	updateSelectedStocks();
}

function initializeTable(csrItemsJson) {
	$cashSaleReturnTable = $("#cashSaleReturnTable").editableItem({
		data: csrItemsJson,
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
						{"name" : "itemBagQuantity", "varType" : "double"},
						{"name" : "quantity", "varType" : "double"},
						{"name" : "srp", "varType" : "double"},
						{"name" : "origSrp", "varType" : "double"},
						{"name" : "discount", "varType" : "double"},
						{"name" : "addOn", "varType" : "double"},
						{"name" : "amount", "varType" : "double"},
						{"name" : "cashSaleItemId", "varType" : "int"},
						{"name" : "salesRefId", "varType" : "int"},
						{"name" : "unitCost", "varType" : "double"},
						{"name" : "refQuantity", "varType" : "double"},
						{"name" : "origQty", "varType" : "double"},
						{"name" : "origBagQty", "varType" : "double"},
				],
		contextPath: "${pageContext.request.contextPath}",
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
				"width" : "13%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						//Do nothing.
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
				"width" : "7%"},
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
			{"title" : "origBagQty",
				"cls" : "origBagQty",
				"editor" : "hidden",
				"visible" : false },
		],
		"footer" : [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false,
	});

	$("#cashSaleReturnTable").on("change", ".availableStock", function(){
		setReferenceDetails($(this));
	});

	$("#cashSaleReturnTable").on("change", ".warehouse", function(){
		var itemId = $(this).closest("tr").find(".itemId").val();
		var referenceObjectId = $(this).closest("tr").find(".referenceObjectId").val();
		var ebObjectId = $(this).closest("tr").find(".ebObjectId").val();
		populateAvailableStock ($(this), $(this).val(), itemId, referenceObjectId, null, ebObjectId);
	});

	$("#cashSaleReturnTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateQty($tr, $("#companyId").val());
	});
}

function updateSelectedStocks() {
	$("#cashSaleReturnTable tbody tr").each(function (row) {
		console.log("update selected stocks at row "+row);
		var csItemId = $(this).find(".cashSaleItemId").val();
// 		alert("CS Item id: "+csItemId+" eb object id: "+origRefObjectId);
		if(csItemId > 0) {
			var $selectedStocks = $(this).find(".availableStock");
			$selectedStocks.empty();
			//Remove the reference object id and unit cost
			$(this).find(".referenceObjectId").val(null);
			$(this).find(".unitCost").val(null);
		}
	});

	setReferences();
}

function setReferences() {
	$("#cashSaleReturnTable tbody tr").each(function (row) {
		console.log("set references at row "+row);
		var origRefObjectId = $(this).find(".origRefObjectId").val();
		var $selectedStocks = $(this).find(".availableStock");
		if(origRefObjectId > 0) {
			$.ajax({
				url: contextPath + "/getAvailableBags/origRef?orTypeId=4&sourceObjectId="+origRefObjectId,
				success : function(shortDesc) {
					$selectedStocks.append("<option selected='selected'>"
							+shortDesc+"</option>");
				}
			});
		}
	});
}

function showCsReference() {
	var url = "/cashSaleReturn/"+"${cashSaleReturn.cashSaleTypeId}"+"/csReference";
	csrReferenceWindow = window.open(contextPath + url,"popup","width=1000,height=500,scrollbars=1,resizable=no," +
	"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
}

function loadCSReference (cashSaleId) {
	$("#cashSaleReturnTable").html("");
	$.ajax({
		url: contextPath + "/cashSaleReturn?cashSaleId="+cashSaleId,
		success : function(csr) {
			loadHeader(csr.cashSaleId, csr.companyId, csr.company.name, csr.arCustomerId, csr.arCustomer.name, 
					csr.arCustomerAccountId, csr.arCustomerAccount.name, csr.salesInvoiceNo, csr.referenceNo);
			initializeTable(csr.cashSaleReturnItems);
			disableTableFields ();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	if (csrReferenceWindow != null) {
		csrReferenceWindow.close();
	}
	csrReferenceWindow = null;
}

function loadHeader(csId, companyId, companyName, arCustomerId, customerName,
		arCustomerAcctId, customerAcctName, salesInvoiceNo, referenceNo) {
	$("#cashSaleId").val(csId);
	$("#companyId").val(companyId);
	$("#spanCompany").val(companyName);
	$("#arCustomerId").val(arCustomerId);
	$("#spanCustomer").val(customerName);
	$("#arCustomerAccountId").val(arCustomerAcctId);
	$("#spanCustomerAccount").val(customerAcctName);
	$("#salesInvoiceNo").val(salesInvoiceNo);
	$("#spanSalesInvoiceNo").text(salesInvoiceNo);
	$("#spanReference").text(referenceNo);
}

function updateHeader() {
	$("#cashSaleId").val("${cashSaleReturn.cashSaleId}");
	$("#companyId").val("${cashSaleReturn.companyId}");
	$("#arCustomerId").val("${cashSaleReturn.arCustomerId}");
	$("#arCustomerAccountId").val("${cashSaleReturn.arCustomerAccountId}");
	$("#salesInvoiceNo").val("${cashSaleReturn.salesInvoiceNo}");
	$("#spanSalesInvoiceNo").text("${cashSaleReturn.salesInvoiceNo}");
	$("#spanReference").text("${cashSaleReturn.referenceNo}");
}

function disableTableFields () {
	$("#cashSaleReturnTable tbody tr").each(function (i) {
		var cashSaleItemId = $(this).find(".cashSaleItemId").val();
		if (typeof cashSaleItemId != "undefined" && cashSaleItemId != null 
				&& cashSaleItemId != "" && cashSaleItemId != 0) {
			$(this).find(".stockCode").attr("disabled", "disabled");
			$(this).find(".tblSelectClass").attr("disabled", "disabled");
		}
	});
	updateSelectedStocks();
}

function saveCashSalesReturn() {
	$("#btnSave").attr("disabled", "disabled");
	if ($cashSaleReturnTable != null)
		$("#cashSaleReturnItemsJson").val($cashSaleReturnTable.getData());
	doPostWithCallBack ("cashSalesReturnForm", "form", function (data) {
		if (data.substring(0,5) == "saved") {
			var objectId = data.split(";")[1];
			var formStatus = new Object();
			formStatus.objectId = objectId;
			updateTable (formStatus);
			dojo.byId("form").innerHTML = "";
		} else {
			var status = $("#txtCashSalesReturnStatus").val();
			var csId = $("#cashSaleId").val();
			var companyId = $("#companyId").val();
			var companyName = $("#spanCompany").val();
			var arCustomerId = $("#arCustomerId").val();
			var customerName = $("#spanCustomer").val();
			var arCustomerAcctId = $("#arCustomerAccountId").val();
			var customerAcctName = $("#spanCustomerAccount").val();
			var salesInvoiceNo = $("#salesInvoiceNo").val();
			var referenceNo = $("#spanReference").text();
			if("${cashSaleReturn.id}" == 0) {
				dojo.byId("form").innerHTML = data;
			} else {
				dojo.byId("editForm").innerHTML = data;
				$("#btnCsReference").attr("disabled", "disabled");
			}
			$("#txtCashSalesReturnStatus").val(status);
			setUpDataTable();
			disableTableFields ();
			loadHeader(csId, companyId, companyName, arCustomerId, customerName,
					arCustomerAcctId, customerAcctName, salesInvoiceNo, referenceNo);
		}
		$("#btnSave").removeAttr("disabled");
	});
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="cashSaleReturn" id="cashSalesReturnForm">
			<div class="modFormLabel">Cash Sales Return - IS
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<input type="hidden" value="6" id="hdnOrTypeId">
			<form:hidden path="ebObjectId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="cashSaleId" id="cashSaleId"/>
			<form:hidden path="companyId" id="companyId"/>
			<form:hidden path="arCustomerId" id="arCustomerId"/>
			<form:hidden path="arCustomerAccountId" id="arCustomerAccountId"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="csrNumber"/>
			<form:hidden path="salesInvoiceNo" id="salesInvoiceNo"/>
			<form:hidden path="cashSaleReturnItemsJson" id="cashSaleReturnItemsJson"/>
			<form:hidden path="totalAmount"/>
			<form:hidden path="cashSaleTypeId" id="hdnSellingTypeId"/>
			<br>
			<div class="modForm">
				<table class="frmField_set">
					<tr>
						<td>
							<input type="button" id="btnCsReference" 
								value="Cash Sales Reference" onclick="showCsReference();"/>
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
							<td class="labels">CSR - IS No.</td>
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
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSave" value="Save" onclick="saveCashSalesReturn();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>