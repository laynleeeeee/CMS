<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Account Sales form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.poitemhandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<style type="text/css">
</style>
<script type="text/javascript">
var selectCustomerAcct = "${accountSales.arCustomerAccountId}";
$(document).ready(function() {
	if ("${accountSales.formWorkflow.currentStatusId}" == 4
			|| "${accountSales.formWorkflow.currentStatusId}" == 3) {
		$("#btnSave").attr("disabled", "disabled");
	}
	if("${accountSales.id}" > 0) {
		disableAndSetCompany();
	}
	initializeTable();
	totalQty();
});

function initializeTable() {
	var poItemsJson = JSON.parse($("#hdnPoItemsJson").val());
	$poItemTable = $("#poItemDivTable").editableItem({
		data : poItemsJson,
		jsonProperties : [
				{"name" : "id", "varType" : "int"},
				{"name" : "accountSalesId", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "itemId", "varType" : "int"},
				{"name" : "quantity", "varType" : "double"},
				{"name" : "origQty", "varType" : "double"},
				{"name" : "stockCode", "varType" : "string"},
				{"name" : "warehouseId", "varType" : "int"}],
		contextPath : "${pageContext.request.contextPath}",
		header : [
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "accountSalesId",
					"cls" : "accountSalesId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "ebObjectId",
					"cls" : "ebObjectId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "itemId",
					"cls" : "itemId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "Stock Code",
					"cls" : "stockCode tblInputText",
					"editor" : "text",
					"visible": true,
					"width" : "10%",
					"handler" : new ItemTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Description",
					"cls" : "description tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "15%"},
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
					"visible": true,
					"width" : "8%"},
				{"title" : "origQty",
					"cls" : "origQty",
					"editor" : "hidden",
					"visible": false},
				{"title" : "UOM",
					"cls" : "uom tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "8%"},
				], "footer" : [
					{"cls" : "quantity"}
			],
		"disableDuplicateStockCode" : false,
		"itemTableMessage" : "poItemErrors",
	});

	$("#poItemDivTable").on("focus", ".tblInputNumeric", function(){
		totalQty();
	});

	$("#poItemDivTable").on("focusout", ".tblInputNumeric", function(){
		totalQty();
	});

	filterArCustomerAccts();
	$("#poItemDivTable").on("change", ".warehouse", function(){
		assignESByWarehouse($(this));
	});
}

function totalQty() {
	var totalQty = 0;
	$("#poItemDivTable").find(" tbody tr ").each(function(row) {
		var quantity = $(this).find(".quantity").val();
		totalQty += accounting.unformat(quantity);
	});
	$("#poItemDivTable").find("tfoot tr").find(".quantity").html(accounting.formatMoney(totalQty));
	$("#poItemDivTable tfoot").css("text-align", "right");
}

function loadArCustomers() {
	var arCustomerName = processSearchName($("#txtArCustomer").val());
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getArCustomers/new?name="+arCustomerName + "&companyId="+companyId;
	loadACItems("txtArCustomer", "hdnArCustomerId", null, uri,
			uri+"&isExact=true", "name",
			function() {
				$("#spanArCustomerError").text("");
			}, function() {
				$("#spanArCustomerError").text("");
			}, function() {
				$("#spanArCustomerError").text("");
			}, function() {
				invalidArCustomerMsg();
			}
	);
}

function invalidArCustomerMsg(){
	$("#spanArCustomerError").text("Invalid Customer.");
}

function validateArCustomer() {
	var txtArCustomer = processSearchName($("#txtArCustomer").val());
	var selectedArCustomerID = $("#hdnArCustomerId").val();
	$.ajax({
		url: contextPath+"/getArCustomers/new?name="+txtArCustomer+"&isExact=true",
		success : function(customer) {
			console.log(customer);
			if (customer != null && customer[0] != undefined) {
				$("#hdnArCustomerId").val(customer[0].id);
				$("#txtArCustomer").val(customer[0].name);
			} else {
				invalidArCustomerMsg();
				$("#txtArCustomer").val("");
				$("#hdnArCustomerId").val("");
			}
			//filter arCustomer accounts
			filterArCustomerAccts();
		},error : function(error) {
			console.log(error);
			invalidArCustomerMsg();
		},
		dataType: "json"
	});
}


function filterArCustomerAccts(){
	$("#arCustomerAccountId").empty();
	if ($.trim($("#txtArCustomer").val()) == "") {
		$("#hdnArCustomerId").val("");
	} else {
		var customerId = $("#hdnArCustomerId").val();
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getArCustomerAccounts?arCustomerId="+customerId+
				"&companyId="+companyId;
		console.log(uri);
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
		var postHandler = {
				doPost: function(data) {
					selectCustomerAcct = $("#arCustomerAccountId").val();
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAccountId", optionParser, postHandler);
	}
 }

function saveAccountSales() {
	setCompanyId();
	$("#hdnPoItemsJson").val($poItemTable.getData());
	$("#btnSaveAccountSales").attr("disabled", "disabled");
	doPostWithCallBack ("accountSalesForm", "form", function (data) {
		if (data.startsWith("saved")) {
			var objectId = data.split(";")[1];
			var formStatus = new Object();
			formStatus.objectId = objectId;
			updateTable (formStatus);
			dojo.byId("form").innerHTML = "";
		} else {
			var companyId = $("#companyId option:selected").val();
			var arCustomerName = $("#txtArCustomer").val();
			var spanPONumber = $("#spanPoNumber").text();
			var currFormStatus = $("#formStatus").text();
			if("${accountSales.id}" == 0) {
				dojo.byId("form").innerHTML = data;
				$("#companyId").val(companyId);
			} else {
				dojo.byId("editForm").innerHTML = data;
				disableAndSetCompany();
			}
			$("#txtArCustomer").val(arCustomerName);
			$("#spanPoNumber").text(spanPONumber);
			$("#formStatus").text(currFormStatus);
			filterArCustomerAccts();
			initializeTable();
		}
	});
}

function setCompanyId() {
	var companyId = $("#companyId").val();
	$("#hdnCompanyId").val(companyId);
}

function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${accountSales.companyId}"+"'>"+
	"${accountSales.company.numberAndName}"+"</option>");
}

function companyOnChange() {
	$("#txtArCustomer").val("");
	$("#hdnArCustomerId").val("");
	$("#poItemDivTable").html("");
	initializeTable();
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="accountSales" id="accountSalesForm">
		<div class="modFormLabel">Sales Order <span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="poNumber"/>
			<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
			<form:hidden path="poItemsJson" id="hdnPoItemsJson"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="ebObjectId"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sales Order No.</td>
							<td class="value"><span id="spanPoNumber">
								<c:if test="${accountSales.id > 0}">
									${accountSales.formattedPONumber}</c:if></span></td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<td class="value" id="formStatus">
							<c:choose>
								<c:when test="${accountSales.formWorkflow != null}">
									${accountSales.formWorkflow.currentFormStatus.description}
								</c:when>
								<c:otherwise>
									NEW
								</c:otherwise>
							</c:choose></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Sales Order Header</legend>
					<table class="formTable">
						<form:hidden path="id" />
						<tr>
							<td class="labels">* Company</td>
							<td class="value"><select class="frmSelectClass" id="companyId" onchange="companyOnChange();">
								<c:forEach items="${companies}" var="company">
									<option value="${company.id}">${company.numberAndName}</option>
								</c:forEach>
							</select></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Date </td>
							<td class="value"><form:input path="poDate" onblur="evalDate('poDate')" style="width: 120px;" cssClass="dateClass2"/>
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
										onclick="javascript:NewCssCal('poDate')" style="cursor: pointer" style="float: right;" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="poDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" id="txtArCustomer" value="${accountSales.arCustomer.name}"
									onkeydown="loadArCustomers();" onblur="validateArCustomer();" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<span id="spanArCustomerError" class="error">
									<form:errors path="arCustomerId" cssClass="error"/>
								</span>
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer Account</td>
							<td class="value">
								<form:select path="arCustomerAccountId" id="arCustomerAccountId" cssClass="frmSelectClass">
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><span id="spanArCustomerAcctError">
								<form:errors path="arCustomerAccountId" cssClass="error"/></span></td>
						</tr>
						<tr>
							<td class="labels" align="right">Remarks</td>
							<td class="value" colspan="2"><form:textarea path="remarks" rows="3"
									style="width: 350px; resize: none;" /></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="remarks" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Sales Order Items</legend>
					<div id="poItemDivTable"></div>
				</fieldset>

				<table class="frmField_set">
					<tr>
						<td><span id="poItemErrors" class="error"></span></td>
					</tr>
					<tr>
						<td><form:errors path="poMessage" cssClass="error" /></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSave"
							value="Save" onclick="saveAccountSales();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
	</form:form>
</div>
</body>
</html>