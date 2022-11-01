<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Cash Sales - Processing form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.salesprocessinghandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<style type="text/css">
.textBoxLabel {
	border: none;
}
</style>
<script type="text/javascript">
var selectCustomerAcct = "${csProcessing.arCustomerAccountId}";
var selectCompany = "${csProcessing.companyId}";
var currentCustAcctId = 0;
var $cashSaleTable = null;
$(document).ready (function () {
	var sellingTypeId = "${csProcessing.cashSaleTypeId}";
	disableAndSetCompany();
	filterCustomerAccts();
	initializeTable ();
	if ("${csProcessing.id}" != 0) {
		disableAndSetCompany();
		var totalAmount = $("#cashSaleTable").find("tfoot tr").find(".amount").html();
		updateTotal(totalAmount);
		computeChange();
	} else {
		$("#hdnCompanyId").val($("#companyId").val());
	}
	enableDisableCheck();
	hideValues();
	totalQty();
	if ("${csProcessing.formWorkflow.complete}" == "true" || "${csProcessing.formWorkflow.currentStatusId}" == 4)
		$("#cashSalesForm :input").attr("disabled","disabled");
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
		                 {"name" : "origQty", "varType" : "double"},
		     			 {"name" : "taxTypeId", "varType" : "int"},
		                 {"name" : "origWarehouseId", "varType" : "int"}
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
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
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
		        {"cls" : "amount"}
	    ],
	    "disableDuplicateStockCode" : false
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

	$("#cashSaleTable").on("focus", "tr", function(){
		computeChange();
	});

	$("#cashSaleTable").on("focusout", ".quantity", function(){
		computeChange();
	});

	$("#cashSaleTable").on("focusout", ".tblInputText", function(){
		totalQty();
	});

	$("#cashSaleTable").on("change", ".warehouse", function(){
		assignESByWarehouse($(this));
	});

	$("#cashSaleTable").on("change", ".tblSelectClass", function(){
		computeChange();
	});

	$("#cashSaleTable").on("focusout", ".tblSelectClass", function(){
		computeChange();
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
	$("#cashSaleTable tfoot").css("text-align", "right");
	$("#cashSaleTable").find("tfoot tr").find(".quantity").html(accounting.formatMoney(totalQty));
}

function disableAndSetCompany() {
	//Disable and set company
	if ("${csProcessing.id}" != 0) {
		$("#companyId").attr("disabled","disabled");
		$("#companyId").append("<option selected='selected' value='"+"${csProcessing.companyId}"+"'>"+
				"${csProcessing.company.numberAndName}"+"</option>");
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
		$("#cashSaleItemsJson").val($cashSaleTable.getData());
		//updateTotal();
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
				if("${csProcessing.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val($("#hdnCompanyId").val());
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
				initializeTable();
				computeChange($("#cash").val());
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

	var totalAmount = $("#grandTotal").text();
	totalAmount =  accounting.unformat(totalAmount);

	var amount = $("#cash").val();
	var change = accounting.unformat(amount) - totalAmount;
	$("#cash").val( accounting.formatMoney(amount));
	$("#txtChange").val(accounting.formatMoney(change));
	totalQty();
	computeGrandTotal();
}

function handleCompanyOnChange() {
	$cashSaleTable.emptyTable();
	computeChange();
}

function assignCompany(companyId) {
	$("#hdnCompanyId").val(companyId);
}

function CompanyIdOnClick(companyId){
	assignCompany(companyId);
	$("#cashSaleTable").html("");
	hideValues();
	initializeTable();
}

function computeGrandTotal() {
	var $itemTblTotal = $("#cashSaleTable").find("tfoot tr .amount");
	var grandTotal = accounting.unformat($itemTblTotal.html());
	$("#grandTotal").html(accounting.formatMoney(grandTotal));
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
		<form:form method="POST" commandName="csProcessing" id="cashSalesForm">
			<div class="modFormLabel">Cash Sales - Processing
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="csNumber"/>
			<form:hidden path="arCustomerId"/>
			<form:hidden path="cashSaleItemsJson" id="cashSaleItemsJson"/>
			<form:hidden path="csArLinesJson" id="csArLinesJson"/>
			<form:hidden path="totalAmount"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="cashSaleTypeId" id="hdnSellingTypeId"/>
			<form:hidden path="ebObjectId"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">
								<c:choose>
									<c:when test="${csProcessing.cashSaleTypeId == 6}">CS No.</c:when>
								</c:choose>
							</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${csProcessing.csNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${csProcessing.formWorkflow.currentFormStatus.description}"/>
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
								<select id="companyId" class="frmSelectClass" onchange="CompanyIdOnClick(this.value);">
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
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
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
