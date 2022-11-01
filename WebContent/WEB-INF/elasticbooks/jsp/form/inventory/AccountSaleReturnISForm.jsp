<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description: Account sales return form for individual selection.
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
var $accountSaleTable = null;
var asrObj = null;
var asrReferenceWindow = null;
$(document).ready (function () {
	if ("${salesReturn.id}" != 0) {
		loadHeader();
		initializeTable();
		disableFields();
		disableTableFields();
		if ("${salesReturn.formWorkflow.complete}" == "true" ||
				"${salesReturn.formWorkflow.currentStatusId}" == 4) {
			$("#btnAccountSales").attr("disabled", "disabled");
		}
	}
});

function initializeTable () {
	var accountSaleItemsJson = JSON.parse($("#accountSalesItemsJson").val());
	setupTableData (accountSaleItemsJson);
}

function setupTableData (accountSaleItemsJson) {
	var cPath = "${pageContext.request.contextPath}";
	$accountSaleTable = $("#saleReturnItemTable").editableItem({
		data: accountSaleItemsJson,
		jsonProperties: [
		               {"name" : "id", "varType" : "int"},
		               {"name" : "itemId", "varType" : "int"},
		               {"name" : "warehouseId", "varType" : "int"},
		               {"name" : "ebObjectId", "varType" : "int"},
		               {"name" : "referenceObjectId", "varType" : "int"},
		               {"name" : "origRefObjectId", "varType" : "int"},
		               {"name" : "itemDiscountId", "varType" : "int"},
		               {"name" : "stockCode", "varType" : "string"},
		               {"name" : "itemBagQuantity", "varType" : "double"},
		               {"name" : "quantity", "varType" : "int"},
		               {"name" : "itemSrpId", "varType" : "int"},
		               {"name" : "itemDiscountId", "varType" : "int"},
		               {"name" : "itemAddOnId", "varType" : "int"},
		               {"name" : "discount", "varType" : "double"},
		               {"name" : "addOn", "varType" : "double"},
		               {"name" : "srp", "varType" : "double"},
		               {"name" : "origSrp", "varType" : "double"},
		               {"name" : "amount", "varType" : "double"},
		               {"name" : "refAccountSaleItemId", "varType" : "int"},
		               {"name" : "salesRefId", "varType" : "int"},
		               {"name" : "unitCost", "varType" : "double"},
		               {"name" : "refQuantity", "varType" : "double"},
		               {"name" : "origBagQty", "varType" : "double"},
		               {"name" : "origQty", "varType" : "double"}
		               ],
		contextPath: cPath,
		header:[
				{"title" : "id",
					"cls" : "id",
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
				{"title" : "ebObjectId",
					"cls" : "ebObjectId",
					"editor" : "hidden",
					"visible": false},
				{"title" : "origRefObjectId",
					"cls" : "origRefObjectId",
					"editor" : "hidden",
					"visible": false},
				{"title" : "Stock Code",
					"cls" : "stockCode tblInputText",
					"editor" : "text",
					"visible" : true,
					"width" : "13%",
					"handler" : new SalesTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Description",
					"cls" : "description tblLabelText",
					"editor" : "label",
					"visible" : true,
					"width" : "15%"},
				{"title" : "warehouseId",
					"cls" : "warehouseId",
					"editor" : "text",
					"visible" : false},
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
				{"title" : "itemDiscountId",
					"cls" : "itemDiscountId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "itemAddOnId",
					"cls" : "itemAddOnId",
					"editor" : "hidden",
					"visible" : false },
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
				{"title" : "itemSrpId",
					"cls" : "itemSrpId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "SRP",
					"cls" : "srp tblLabelNumeric",
					"editor" : "label",
					"visible" : true,
					"width" : "8%"},
				{"title" : "origSrp",
					"cls" : "origSrp",
					"editor" : "hidden",
					"visible" : false},
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
				{"title" : "origBagQty",
					"cls" : "origBagQty",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "origQty",
					"cls" : "origQty",
					"editor" : "hidden",
					"visible" : false }
			],
			"footer" : [
				{"cls" : "amount"}
			],
				"disableDuplicateStockCode" : false,
	});

	$("#saleReturnItemTable").on("change", ".availableStock", function(){
		setReferenceDetails($(this));
	});

	$("#saleReturnItemTable").on("change", ".warehouse", function(){
		var itemId = $(this).closest("tr").find(".itemId").val();
		var referenceObjectId = $(this).closest("tr").find(".referenceObjectId").val();
		var ebObjectId = $(this).closest("tr").find(".ebObjectId").val();
		populateAvailableStock ($(this), $(this).val(), itemId, referenceObjectId, null, ebObjectId);
	});

	$("#saleReturnItemTable").on("change", ".discountType", function(){
		var discount = $(this).val();
		if(discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
	});

	$("#saleReturnItemTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateQty($tr, $("#companyId").val());
	});
}

function updateSelectedStocks() {
	$("#saleReturnItemTable tbody tr").each(function (row) {
		console.log("update selected stocks at row "+row);
		var asItemId = $(this).find(".refAccountSaleItemId").val();
		if(asItemId > 0) {
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
	$("#saleReturnItemTable tbody tr").each(function (row) {
		console.log("set references at row "+row);
		var asiEbObjectId = $(this).find(".origRefObjectId").val();
		var $selectedStocks = $(this).find(".availableStock");
		if(asiEbObjectId > 0) {
			$.ajax({
				url: contextPath + "/getAvailableBags/origRef?orTypeId=5&sourceObjectId="+asiEbObjectId,
				success : function(shortDesc) {
					$selectedStocks.append("<option selected='selected'>"
							+shortDesc+"</option>");
				}
			});
		}
	});
}

function getTotalAmount () {
	var totalAmount = $("#saleReturnItemTable").find("tfoot tr").find(".amount").html();
	$("#amount").val(accounting.unformat(totalAmount));
	$("#totalAmount").val(accounting.unformat(totalAmount));
}

function saveAccountSales () {
	if ($accountSaleTable != null)
		$("#accountSalesItemsJson").val($accountSaleTable.getData());
	$("#btnAccountSales").attr("disabled", "disabled");
	getTotalAmount();
	doPostWithCallBack ("salesReturnForm", "form", function (data) {
		if (data.substring(0,5) == "saved") {
			var objectId = data.split(";")[1];
			var formStatus = new Object();
			formStatus.objectId = objectId;
			updateTable (formStatus);
			dojo.byId("form").innerHTML = "";
		} else {
			var status = $("#txtSalesReturnStatus").val();
			var creditLimit = $("#spanCL").text();
			var availableBalance = $("#spanAB").text();
			if("${salesReturn.id}" == 0) {
				dojo.byId("form").innerHTML = data;
				if (asrObj != null)
					updateHeader(asrObj);
			} else {
				dojo.byId("editForm").innerHTML = data;
				loadHeader();
				disableFields();
			}
			$("#txtSalesReturnStatus").val(status);
			$("#spanCL").text(creditLimit);
			$("#spanAB").text(availableBalance);
			initializeTable();
			disableTableFields ();
		}
		$("#btnAccountSales").removeAttr("disabled");
	});
}

function showAsReference() {
	var url = contextPath + "/saleReturn/"+"${salesReturn.transactionTypeId}"+"/asReference";
	asrReferenceWindow = window.open(url,"popup","width=1000,height=500,scrollbars=1,resizable=no," +
	"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
}

function loadHeader() {
	$("#accountSaleId").val("${salesReturn.accountSaleId}");
	$("#spanReference").text("${salesReturn.transactionNumber}");
	$("#companyId").val("${salesReturn.companyId}");
	$("#customerId").val("${salesReturn.customerId}");
	$("#customerAcctId").val("${salesReturn.customerAcctId}");
	$("#termId").val("${salesReturn.termId}");
	$("#spanTerm").text("${salesReturn.term.name}");
}

function updateHeader (salesReturn) {
	$("#accountSaleId").val(salesReturn.accountSaleId);
	$("#spanReference").text(salesReturn.transactionNumber);
	$("#companyId").val(salesReturn.companyId);
	$("#spanCompany").val(salesReturn.company.name);
	$("#customerId").val(salesReturn.customerId);
	$("#spanCustomer").val(salesReturn.arCustomer.name);
	$("#customerAcctId").val(salesReturn.customerAcctId);
	$("#spanCustomerAccount").val(salesReturn.arCustomerAccount.name);
	$("#termId").val(salesReturn.termId);
	$("#spanTerm").text(salesReturn.term.name);
}

function loadASReference (arTransactionId) {
	$("#saleReturnItemTable").html("");
	$.ajax({
		url: contextPath + "/saleReturn?arTransactionId="+arTransactionId,
		success : function(saleReturn) {
			asrObj = saleReturn;
			updateHeader(saleReturn);
			setupTableData(saleReturn.accountSaleItems);
			disableTableFields ();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	if (asrReferenceWindow != null)
		asrReferenceWindow.close();
	asrReferenceWindow = null;
}

function disableTableFields () {
	$("#saleReturnItemTable tbody tr").each(function (i) {
		var refAccountSaleItemId = $(this).find(".refAccountSaleItemId").val();
		if (typeof refAccountSaleItemId != "undefined" && refAccountSaleItemId != null
				&& refAccountSaleItemId != "" && refAccountSaleItemId != 0) {
			$(this).find(".stockCode").attr("disabled", "disabled");
			$(this).find(".tblSelectClass").attr("disabled", "disabled");
		}
	});
	updateSelectedStocks();
}

function disableFields(){
	$(btnAsReference).attr("disabled", "disabled");
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="salesReturn" id="salesReturnForm">
			<div class="modFormLabel">Account Sales Return - IS<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="accountSalesItemsJson" id="accountSalesItemsJson"/>
			<form:hidden path="id" id="hdnFormId"/>
			<input type="hidden" value="7" id="hdnOrTypeId">
			<form:hidden path="ebObjectId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="updatedBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNumber"/>
			<form:hidden path="accountSaleId"/>
			<form:hidden path="companyId" id="companyId"/>
			<form:hidden path="transactionTypeId"/>
			<form:hidden path="customerId"/>
			<form:hidden path="customerAcctId"/>
			<form:hidden path="termId"/>
			<form:hidden path="amount"/>
			<form:hidden path="totalAmount"/>
			<form:hidden path="availableBalance"/>
			<br>
			<div class="modForm">
				<table class="frmField_set">
					<tr>
						<td>
							<input type="button" id="btnAsReference"
								value="Account Sale - IS Reference" onclick="showAsReference();"/>
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
							<td class="labels">ASR - IS No.</td>
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
								<input type="text" id="txtSalesReturnStatus" class="textBoxLabel" 
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
								<form:input path="transactionDate" id="date" onblur="evalDate('transactionDate')"
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
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnAccountSales" value="Save" onclick="saveAccountSales();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>