<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp"%>
	<!--

		Description: Account Sales -IS form.
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
<script type="text/javascript">
var selectCustomerAcct = "${accountSale.customerAcctId}";
var currentCustAcctId = 0;
var termid=0;
var terms = new Array ();
var $accountSaleTable = null;
var arCustAcctTerms = new Array();
var defaultArLine = null;
var selectCompany = "${accountSale.companyId}";
var sellingPriceHeader = null;
$(document).ready (function () {
	setTerm();
	$("#hdnCompanyId").val($("#companyId").val());
	disableAndSetCompany();
	initializeOtherChargesTbl();
	initializeTable();
	var dueDate = null;
	if ("${accountSale.id}" != 0) {
		updateOChargesAmt();
		computeGrandTotal();
		$("#termId").val("${accountSale.term.id}").attr("selected" ,true);
		termid = $("#termId").val();
		dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${accountSale.dueDate}'/>";
	} else {
		computeDueDate();
	}
	hideValues();
	filterCustomerAccts(dueDate);
	if ("${accountSale.formWorkflow.complete}" == "true" || "${accountSale.formWorkflow.currentStatusId}" == 4) {
		$("#divForm :input").attr("disabled", "disabled");
		$("#accountSaleForm :input").attr("disabled","disabled");
	}
});

function setTerm() {
	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
}

function initializeTable (sellingPriceHeader) {
	var accountSaleItemsJson = JSON.parse($("#accountSalesItemsJson").val());

	var cPath = "${pageContext.request.contextPath}";
	$accountSaleTable = $("#accountSaleItem").editableItem({
		data: accountSaleItemsJson,
		jsonProperties: [
		               {"name" : "id", "varType" : "int"},
		               {"name" : "itemId", "varType" : "int"},
		               {"name" : "warehouseId", "varType" : "int"},
		               {"name" : "referenceObjectId", "varType" : "int"},
		               {"name" : "origRefObjectId", "varType" : "int"},
		               {"name" : "ebObjectId", "varType" : "int"},
		               {"name" : "itemDiscountId", "varType" : "int"},
		               {"name" : "itemAddOnId", "varType" : "int"},
		               {"name" : "stockCode", "varType" : "string"},
		               {"name" : "itemBagQuantity", "varType" : "double"},
		               {"name" : "quantity", "varType" : "int"},
		               {"name" : "itemSrpId", "varType" : "int"},
		               {"name" : "discount", "varType" : "double"},
		               {"name" : "srp", "varType" : "double"},
		               {"name" : "origSrp", "varType" : "double"},
		               {"name" : "amount", "varType" : "double"},
		               {"name" : "unitCost", "varType" : "double"},
		               {"name" : "origQty", "varType" : "double"},
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
				{"title" : "Stock Code",
					"cls" : "stockCode tblInputText",
					"editor" : "text",
					"visible" : true,
					"width" : "10%",
					"handler" : new SalesTableHandler (new function () {
						this.handleTotal = function (total) {
							computeGrandTotal();
						};
					})},
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
				{"title" : "unitCost",
					"cls" : "unitCost",
					"editor" : "hidden",
					"visible": false},
				{"title" : "Description", 
					"cls" : "description tblLabelText",
					"editor" : "label",
					"visible" : true, 
					"width" : "12%"},
				{"title" : "warehouseId",
					"cls" : "warehouseId",
					"editor" : "text",
					"visible" : false},
				{"title" : "Warehouse", 
					"cls" : "warehouse tblSelectClass", 
					"editor" : "select",
					"visible" : true ,
					"width" : "10%"},
				{"title" : "Selected <br> Bags/Stocks",
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
					"width" : "6%" },
				{"title" : "itemSrpId",
					"cls" : "itemSrpId",
					"editor" : "hidden",
					"visible" : false},
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
				{"title" : "unitCost", 
					"cls" : "unitCost", 
					"editor" : "hidden",
					"visible" : false },
				{"title" : "origQty", 
					"cls" : "origQty", 
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

	$("#accountSaleItem").on("change", ".availableStock", function(){
		setReferenceDetails($(this));
	});

	$("#accountSaleItem").on("change", ".discountType", function(){
		var discount = $(this).val();
		if(discount == "") {
			$(this).closest("tr").find(".discount").html("");
		}
		
	});

	$("#accountSaleItem").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#accountSaleItem").on("blur", ".tblInputNumeric", function(){
		computeGrandTotal();
	});

	$("#accountSaleItem").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateQty($tr, $("#companyId").val());
	});

	$("#accountSaleItem").on("change", ".warehouse", function(){
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

	$("#accountSaleItem").on("change", ".addOn", function(){
		computeGrandTotal();
	});

	$("#accountSaleItem").on("focusout", ".addOn", function(){
		computeGrandTotal();
	});

	$("#accountSaleItem").on("mouseleave", ".addOn", function(){
		computeGrandTotal();
	});

	$("#accountSaleItem").on("change", ".discount", function(){
		computeGrandTotal();
	});

	$("#accountSaleItem").on("focusout", ".discount", function(){
		computeGrandTotal();
	});

	$("#accountSaleItem").on("mouseleave", ".discount", function(){
		computeGrandTotal();
	});
}

function computeGrandTotal() {
	hideValues();
	totalQty();
	var $itemTblTotal = $("#accountSaleItem").find("tfoot tr .amount");
	var $otherChargesTotal = $("#otherChargesTable").find("tfoot tr .amount");
	var grandTotal = accounting.unformat($itemTblTotal.html())
				+ accounting.unformat($otherChargesTotal.html());
	$("#grandTotal").html(accounting.formatMoney(grandTotal));
}

function disableAndSetCompany() {
	//Disable and set company
	if ("${accountSale.id}" != 0) {
		$("#companyId").attr("disabled","disabled");
		$("#companyId").append("<option selected='selected' value='"+"${accountSale.companyId}"+"'>"+
				"${accountSale.company.numberAndName}"+"</option>");
	}
}

function Term (id, days) {
	this.id = id;
	this.days = days;
}

function ArCustAcctTerm (arCustAcctId, termId) {
	this.arCustAcctId = arCustAcctId;
	this.termId = termId;
}

function getCustomer () {
	var companyId = $("#companyId").val();
	if($.trim($("#txtCustomer").val()) != ""){
		$.ajax({
			url: contextPath + "/getArCustomers/new?name="+$.trim($("#txtCustomer").val())+"&isExact=true"+
				"&companyId="+companyId,
			success : function(customer) {
				$("#spanCustomerError").text("");
				if (customer != null && customer[0] != undefined) {
					$("#customerId").val(customer[0].id);
					$("#txtCustomer").val(customer[0].name);
					setCreditLimit(customer[0].maxAmount);
				}
				getAvailableBalance();
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
}

function showCustomers () {
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getArCustomers/new?name="+$.trim($("#txtCustomer").val())+"&companyId="+companyId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#customerId").val(ui.item.id);
			$("#spanCustomerError").text("");
			$(this).val(ui.item.name);
			setCreditLimit(ui.item.maxAmount);
			getAvailableBalance();
			filterCustomerAccts();
			return false;
		}, minLength: 2,
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function filterCustomerAccts(dueDate){
	$("#arCustomerAcctId").empty();

	if ($.trim($("#txtCustomer").val()) == "")
		$("#customerId").val("");
	else {
		var customerId = $("#customerId").val();
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getArCustomerAccounts?arCustomerId="+customerId+
				"&companyId="+companyId;
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
					if ("${arTransaction.id}" == 0) {
						defaultArLine = null;
					}
					var customerAcctId = $("#arCustomerAcctId option:selected").val();
					arCustAcctTerms = new Array();
					for (var index = 0; index < data.length; index++){
						var rowObject =  data[index];
						var id = rowObject["id"];
						if (id == customerAcctId){
							if ("${arTransaction.id}" == 0) {
								if(termid==0){
									var defaultTerm = rowObject["termId"];
									$("#termId").val(defaultTerm).attr("selected" ,true);
								}
							}
						}
						var arCustAcctTerm = new ArCustAcctTerm(id, rowObject["termId"], rowObject["defaultArLineSetup"]);
						arCustAcctTerms.push (arCustAcctTerm);
						termid=0;
					}

					//Compute due date based from the number of days of the term if dueDate is null,
					//Otherwise, no changes for the value of due date.
					if(dueDate == null) {
						computeDueDate();
					}
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAcctId", optionParser, postHandler);
	}
 }

function assignCustomerAcct (select) {
	selectCustomerAcct = $(select).val();

	for (var i=0; i<arCustAcctTerms.length; i++) {
		var cat = arCustAcctTerms[i];
		if (cat.arCustAcctId == selectCustomerAcct) {
			$("#termId").val(cat.termId);
		}
	}
	computeDueDate();
}

function computeDueDate () {
	var dateVal = $("#transactionDate").val ();
	if (dateVal == null || dateVal == ""){
		$("#dueDate").val ("");
		return;
	}
	var additionalDays = 0;
	var currentSelTermId = $("#termId option:selected").val();
	for (var i = 0; i < terms.length; i++) {
		var term = terms[i];
		if (term.id == currentSelTermId) {
			additionalDays = term.days;
			break;
		}
	}
	var date = new Date (dateVal);
	date.setDate(date.getDate() + parseInt(additionalDays));
	if(!isNaN( date.getMonth()) && !isNaN( date.getDate()) && !isNaN( date.getFullYear())){
		$("#dueDate").val ((date.getMonth() + 1) +"/"+date.getDate()+"/"+date.getFullYear());
	}else{
		$("#dueDate").val ("");
	}
}

function getAvailableBalance () {
	var customerId = $("#customerId").val();
	if (customerId != "") {
		$.ajax({
			url: contextPath + "/accountSale/availableBalance?arCustomerId="+customerId,
			success : function(item) {
				if (item != null) {
					console.log ("Available balance: " + item);
					$("#availableBalance").val(item);
					$("#spanAB").text(accounting.formatMoney(item));
				}
			},
			error : function(error) {
				$("#spanAB").text("");
			},
			dataType: "text"
		});
	}
}

function setCreditLimit (creditLimit) {
	$("#spanCL").text(accounting.formatMoney(creditLimit));
}

function setCompanyId() {
	var companyId = $("#companyId").val();
	$("#hdnCompanyId").val(companyId);
}

function getTotalAmount () {
	var totalAmount = $("#accountSaleItem").find("tfoot tr").find(".amount").html();
	$("#amount").val(accounting.unformat(totalAmount));
	$("#totalAmount").val(accounting.unformat(totalAmount));
}

var isSaving = false;
function saveAccountSales () {
	if (isSaving == false) {
		isSaving = true;
		setCompanyId();
		$("#accountSalesItemsJson").val($accountSaleTable.getData());
		$("#arLinesJson").val($otherChargesTable.getData());
		$("#btnAccountSales").attr("disabled", "disabled");
		getTotalAmount();
		doPostWithCallBack ("accountSaleForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var dueDate = $("#dueDate").val();
				var customer = $("#txtCustomer").val();
				var creditLimit = $("#spanCL").text();
				var availableBalance = $("#spanAB").text();
				var status = $("#txtAccountSalesStatus").val();
				if("${accountSale.id}" == 0){
					var companyId = $("#hdnCompanyId").val();
					dojo.byId("form").innerHTML = data;
					$("#companyId").val(companyId);
					$("#hdnCompanyId").val(companyId);
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtAccountSalesStatus").val(status);
				}
				termid = $("#termId").val();
				$("#txtCustomer").val(customer);
				$("#spanCL").text(creditLimit);
				$("#spanAB").text(availableBalance);
				disableAndSetCompany();
				filterCustomerAccts(dueDate);
				initializeOtherChargesTbl();
				updateOChargesAmt();
				initializeTable();
				computeGrandTotal();
				isSaving = false;
			}
			$("#btnAccountSales").removeAttr("disabled");
		});
	}
}

// Other Charges
function initializeOtherChargesTbl () {
	console.log("initializing other charges.");
	var otherChargesTable = JSON.parse($("#arLinesJson").val());

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

	$("#otherChargesTable").on("change", ".addOn", function(){
		var amount = $(this).closest("tr").find(".amount").val();
		updateOChargesAmt(amount);
		computeGrandTotal();
	});
}

function CompanyIdOnClick(companyId){
	$("#hdnCompanyId").val(companyId);
	$("#txtCustomer").val("");
	$("#arCustomerAcctId").empty();
	$("#otherChargesTable").html("");
	$("#accountSaleItem").html("");
	hideValues();
	initializeOtherChargesTbl();
	initializeTable(sellingPriceHeader);
}

function totalQty() {
	var totalQty = 0;
	$("#accountSaleItem").find(" tbody tr ").each(function(row) {
		var quantity = $(this).find(".quantity").val();
		totalQty += accounting.unformat(quantity);
	});
	$("#accountSaleItem").find("tfoot tr").find(".quantity").html(accounting.formatMoney(totalQty));
	$("#accountSaleItem tfoot").css("text-align", "right");
}

function hideValues() {
	$("#accountSaleItem").find("tfoot tr .uom").hide();
	$("#accountSaleItem").find("tfoot tr .addOn").hide();
	$("#accountSaleItem").find("tfoot tr .srp").hide();
	$("#accountSaleItem").find("tfoot tr .discountType").hide();
	$("#accountSaleItem").find("tfoot tr .discount").hide();
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="accountSale" id="accountSaleForm">
			<div class="modFormLabel">Account Sale - IS
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<input type="hidden" value="5" id="hdnOrTypeId">
			<form:hidden path="accountSalesItemsJson" id="accountSalesItemsJson" />
			<form:hidden path="ebObjectId" />
			<form:hidden path="arLinesJson" id="arLinesJson" />
			<form:hidden path="createdBy" />
			<form:hidden path="updatedBy" />
			<form:hidden path="formWorkflowId" />
			<form:hidden path="sequenceNumber" />
			<form:hidden path="transactionTypeId" id="hdnSellingTypeId" />
			<form:hidden path="customerId" />
			<form:hidden path="companyId" id="hdnCompanyId" />
			<form:hidden path="amount" />
			<form:hidden path="totalAmount" />
			<form:hidden path="availableBalance" />
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">AS - IS No.</td>
							<td class="value"><input type="text" id="txtSequenceNumber"
								class="textBoxLabel" readonly="readonly"
								value="${accountSale.sequenceNumber}" /></td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status"
								value="${accountSale.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text"
								id="txtAccountSalesStatus" class="textBoxLabel"
								readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Account Sales - IS Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value"><select class="frmSelectClass"
								id="companyId" onchange="CompanyIdOnClick(companyId);">
									<c:forEach items="${companies}" var="company">
										<option value="${company.id}">${company.numberAndName}</option>
									</c:forEach>
							</select></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="companyId" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" cssClass="input" id="txtCustomer"
									onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="getCustomer();"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><span id="spanCustomerError" class="error"
								style="margin-left: 12px;"></span> <form:errors
									path="customerId" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">* Customer Account</td>
							<td class="value"><form:select path="customerAcctId"
									id="arCustomerAcctId" cssClass="frmSelectClass"
									onchange="assignCustomerAcct (this);">
								</form:select></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="customerAcctId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">Credit Limit</td>
							<td class="value"><span id="spanCL"> <fmt:formatNumber
										type="number" minFractionDigits="2" maxFractionDigits="2"
										value="${accountSale.arCustomer.maxAmount}" />
							</span></td>
						</tr>
						<tr>
							<td class="labels">Available Balance</td>
							<td class="value"><span id="spanAB"> <fmt:formatNumber
										type="number" minFractionDigits="2" maxFractionDigits="2"
										value="${accountSale.availableBalance}" />
							</span></td>
						</tr>
						<tr>
							<td class="labels">Term</td>
							<td class="value"><form:select path="termId" id="termId"
									cssClass="frmSelectClass" onchange="computeDueDate();">
									<form:options items="${terms}" itemLabel="name" itemValue="id" />
								</form:select></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="termId" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value"><form:input path="transactionDate"
									id="transactionDate"
									onblur="evalDate('transactionDate'); computeDueDate();"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('transactionDate')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="transactionDate"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Due Date</td>
							<td class="value"><form:input path="dueDate" id="dueDate"
									onblur="evalDate('dueDate')" style="width: 120px;"
									class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('dueDate')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="dueDate" cssClass="error"
									style="margin-left: 12px;" /></td>
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
					<legend>Account Sale Item Table</legend>
					<div id="accountSaleItem"></div>
					<table>
						<tr>
							<td colspan="12"><span id="csMessage" class="error"></span>
							</td>
						</tr>
						<tr>
							<td colspan="12"><form:errors path="errorASItems"
									cssClass="error" /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Other Charges</legend>
					<div id="otherChargesTable"></div>
					<input type="hidden" id="hdnOChargesAmt">
					<table>
						<tr>
							<td colspan="7"><form:errors path="otherChargesMessage"
									cssClass="error" /></td>
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
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnAccountSales"
							value="Save" onclick="saveAccountSales();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>