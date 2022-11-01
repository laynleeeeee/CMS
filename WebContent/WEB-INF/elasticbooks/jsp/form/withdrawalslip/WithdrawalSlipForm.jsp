<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Withdrawal slip Form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.stockouthandler.js"></script>
<style type="text/css">
.disableSelect{
	pointer-events: none;
	cursor: default;
}

</style>
<script type="text/javascript">
var $withdrawalItemsTable = null;
var hasPOReference = false;
$(document).ready(function () {
	if("${withdrawalSlip.id}" != 0){
		filterWarehouses("${withdrawalSlip.warehouseId}");
		filterCustomerAccts("${withdrawalSlip.customerAcctId}");
		if("${withdrawalSlip.customerAcctId}" != ""){
			loadAccountsFromCustAcct("${withdrawalSlip.accountId}");
		} else {
			loadAccounts("${withdrawalSlip.accountId}");
		}
		if($("#poNumber").val() != "" && $("#poNumber").val() != null){
			toggleCompanyAndPONumber(true);
			hasPOReference = true;
		} else {
			$("#companyId").attr("disabled", true);
		}
		disableHeaderFields();
	} else {
		filterWarehouses();
	}
	initializeTable(JSON.parse($("#withdrawalItemsJson").val()));
	disableAllFields();
});

function disableAllFields() {
	var isComplete = "${withdrawalSlip.formWorkflow.complete}";
	var currentStatusId = "${withdrawalSlip.formWorkflow.currentStatusId}";
	if(isComplete == "true" || currentStatusId == 4) {
		$("#withdrawalSlipForm :input").attr("disabled","disabled");
	}
}

function disableHeaderFields() {
	$("#companyId").attr("disabled", "disabled");
	$("#btnPoReference").attr("disabled", "disabled");
}

function enableFields() {
	$("#companyId").removeAttr("disabled");
}

function companyOnChange (){
	filterWarehouses();
	initializeTable();
	$("#hdnArCustomerId").val("");
	$("#txtFrmCustomerName").val("");
	$("#codeVesselName").val("");
	$("#divisionId").val("");
	$("#requestedById").val("");
	$("#hdnRequestedById").val("");
	$("#requestedById").text("");
	$("#arCustomerAcctId").empty();
	$("#arCustomerAcctId").text("");
	$("#poNumber").val("");
	$("#purchaseOderId").val("");
	$("#refenceObjectPOId").val("");
	$("#accountId").empty();
	$("#accountId").text("");
}

function filterWarehouses(warehouseId) {
	var companyId = $("#companyId").val();
	if(companyId > 0) {
		var uri = contextPath+"/getWarehouse/byUserCompany?companyId="+companyId+"&isActiveOnly="+true+
		(typeof warehouseId != "undefined" ? "&warehouseId="+warehouseId : "");
		$("#warehouseId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		postHandler = {
				doPost: function() {
					if(warehouseId != "") {
						$("#warehouseId").val(warehouseId);
					}
				}
		};
		loadPopulate (uri, false, null, "warehouseId", optionParser, postHandler);
	}
}

function initializeTable (withdrawalItemsJson) {
	var withdrawalItemsJson = withdrawalItemsJson;
	var cPath = "${pageContext.request.contextPath}";
	$("#withdrawalSlipTable").html("");
	$withdrawalItemsTable = $("#withdrawalSlipTable").editableItem({
		data: withdrawalItemsJson,
		jsonProperties: [
		                 {"name" : "id", "varType" : "int"},
		                 {"name" : "ebObjectId", "varType" : "int"},
		                 {"name" : "refenceObjectId", "varType" : "int"},
		                 {"name" : "itemId", "varType" : "int"},
		                 {"name" : "stockCode", "varType" : "string"},
		                 {"name" : "quantity", "varType" : "double"},
		                 {"name" : "unitCost", "varType" : "double"},
		                 {"name" : "origQty", "varType" : "double"},
		                 {"name" : "srp", "varType" : "double"},
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
				"visible" : false },
			{"title" : "refenceObjectId", 
				"cls" : "refenceObjectId", 
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
				"handler" : new StockOutTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "Existing <br> Stocks",
				"cls" : "existingStock tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "UOM",
				"cls" : "uom tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "amount", 
				"cls" : "amount", 
				"editor" : "hidden",
				"visible" : false,
				"width" : "8%"},
			{"title" : "unitCost",
				"cls" : "unitCost",
				"editor" : "hidden",
				"visible": false},
			{"title" : "origQty",
				"cls" : "origQty",
				"editor" : "hidden",
				"visible": false}
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": "wsMessage"
	});
	$("#withdrawalSlipTable").on("blur", ".quantity", function(){
		formatMoney(this);
	});
}

function showPOReference() {
	var url = "/withdrawalSlip/poReference";
	poReferenceWindow = window.open(contextPath + url,"popup","width=1000,height=500,scrollbars=1,resizable=no," +
	"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
}

function loadPOReference (poId) {
	$.ajax({
		url: contextPath + "/withdrawalSlip/?poId="+poId,
		success : function(ws) {
			updateHeader(ws.companyId, ws.poNumber, ws.purchaseOderId, ws.refenceObjectId);
			initializeTable(ws.withdrawalSlipItems);
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	poReferenceWindow.close();
	isPO = true;
}

function formatMoney($elem) {
	if($.trim($($elem).val()) != "") {
		$($elem).val(accounting.formatMoney($($elem).val()));
	}
}

function updateHeader (companyId, poNumber, purchaseOderId, refenceObjectId) {
	$("#companyId").val(companyId);
	companyOnChange();
	$("#poNumber").val(poNumber);
	$("#purchaseOderId").val(purchaseOderId);
	$("#refenceObjectPOId").val(refenceObjectId);
	toggleCompanyAndPONumber(true);
	hasPOReference = true;
}

function showFleetProfiles () {
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getFleetProfile?companyId="+ companyId + "&codeVesselName=" + $.trim($("#codeVesselName").val())+"&isExact=false";
	$("#codeVesselName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#fleetProfileId").val(ui.item.id);
			var codeVesselName = ui.item.codeVesselName;
			$(this).val(codeVesselName);
			$("#divisionId").val(ui.item.divisionId);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + item.codeVesselName + "</a>" )
			.appendTo( ul );
	};
}

function getFleetProfile() {
	var companyId = $("#companyId").val();
	var codeVesselName = $.trim($("#codeVesselName").val());
	var uri = "/getFleetProfile?companyId="+ companyId + "&codeVesselName=" + codeVesselName+"&isExact=true";
	$("#codeVesselNameErrMsg").text("");
	if (codeVesselName != "") {
		$.ajax({
			url: contextPath + uri,
			success : function(fleetProfile) {
				if (fleetProfile != null && typeof fleetProfile != undefined) {
					console.log(fleetProfile);
					$("#fleetProfileId").val(fleetProfile.id);
					$("#codeVesselName").text(fleetProfile.name);
					$("#divisionId").val(fleetProfile.divisionId);
				} else {
					$("#codeVesselNameErrMsg").text("Invalid Fleet.");
					$("#divisionId").val("");
				}
				loadAccounts();
			},
			error : function(error) {
				$("#divisionId").val("");
				loadAccounts();
				console.log(error);
				$("#codeVesselNameErrMsg").text("Invalid Fleet.");
			},
			dataType: "json"
		});
		$("#customerErrorM").text("");
		$("#errorFleet").text("");
		$("#spanCustomerError").text("");
		$("#txtFrmCustomerName").val("");
		$("#arCustomerAcctId").empty();
		$("#hdnArCustomerId").val("");
	} else {
		$("#fleetProfileId").val("");
		if($.trim($("#txtFrmCustomerName").val()) == ""){
			$("#accountId").empty();
			$("#accountId").text("");
		}
	}
}

function loadAccounts(accountId) {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	if(divisionId) {
		var uri = contextPath+"/getAccounts?companyId="+companyId+"&divisionId="+divisionId+
		(typeof accountId != "undefined" ? "&accountId="+accountId : "");
		$("#accountId").empty();
		var optionParser =  {
				getValue: function (rowObject) {
					return rowObject["id"];
				},
				getLabel: function (rowObject) {
					return rowObject["number"] + " - "+rowObject["accountName"];
				}
			};
			postHandler = {
					doPost: function(data) {
						if(accountId != 0) {
							$("#accountId").val(accountId);
						}
					}
			};
		loadPopulate (uri, false, accountId, "accountId", optionParser, postHandler);
	} else {
		$("#accountId").empty();
		$("#accountId").text("");
	}
}

function showCustomers () {
	var customerName = $.trim($("#txtFrmCustomerName").val());
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getArCustomers/new?name="+customerName+"&companyId="+companyId;
	$("#txtFrmCustomerName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnArCustomerId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function getCustomer () {
	var customerName = encodeURIComponent($.trim($("#txtFrmCustomerName").val()));
	var noCustomerMatch = false;
	if (customerName != "") {
		var companyId = $("#companyId").val();
		$.ajax({
			url: contextPath + "/getArCustomers/new?name="+customerName+"&isExact=true"+
					"&companyId="+companyId,
			success : function(customer) {
				$("#spanCustomerError").text("");
				if (customer != null && customer.length != 0) {
					$("#hdnArCustomerId").val(customer[0].id);
					$("#txtFrmCustomerName").val(customer[0].name);
				} else {
					noCustomerMatch = true;
				}
			},
			error : function(error) {
				$("#spanCustomerError").text("Invalid customer/project.");
				filterCustomerAccts();
				$("#accountId").empty();
				$("#accountId").text("");
			},
			complete : function () {
				if (noCustomerMatch) {
					$("#spanCustomerError").text("Invalid customer/project.");
					$("#accountId").empty();
					$("#accountId").text("");
				}
				filterCustomerAccts();
			},
			dataType: "json"
		});
		$("#codeVesselName").val("");
		$("#customerErrorM").text("");
		$("#codeVesselNameErrMsg").text("");
		$("#fleetProfileId").val("");
		$("#errorFleet").text("");
	} else {
		filterCustomerAccts();
		if($.trim($("#codeVesselName").val()) == ""){
			$("#accountId").empty();
			$("#accountId").text("");
		}
	}
}

var isSaving = false;
function saveWithdrawalSlip () {
	if(isSaving == false) {
		isSaving = true;
		enableFields();
		toggleCompanyAndPONumber(false);
		withdrawalItemsJson = $("#withdrawalItemsJson").val();
		$("#withdrawalItemsJson").val($withdrawalItemsTable.getData());
		$("#btnSaveWithdrawalSlip").attr("disabled", "disabled");
		doPostWithCallBack ("withdrawalSlipForm", "form", function(data) {
			if(data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var date = $("#date").val();
				var warehouseId = $("#warehouseId").val();
				var arCustomerAcctId = $("#arCustomerAcctId").val();
				var poNumber = $("#poNumber").val();
				var accountId = $("#accountId").val();
				var companyId = $("#companyId option:selected").val();
				var codeVesselName = $.trim($("#codeVesselName").val());
				if("${withdrawalSlip.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val(companyId);
				} else {
					dojo.byId("editForm").innerHTML = data;
					updatePopupCss();
					disableHeaderFields();
				}
				if(codeVesselName != ""){
					$("#codeVesselName").val(codeVesselName);
					loadAccounts(accountId);
				} else {
					filterCustomerAccts(arCustomerAcctId, accountId);
				}
				$("#poNumber").val(poNumber);
				console.log(typeof hasPOReference);
				if(hasPOReference) {
					if(poNumber != "" && poNumber != null){
						toggleCompanyAndPONumber(true);
					} else {
						$("#companyId").attr("disabled", true);
					}
				}
				filterWarehouses(warehouseId);
				initializeTable(JSON.parse($("#withdrawalItemsJson").val()));
				isSaving = false;
			}
			$("#btnSaveWithdrawalSlip").removeAttr("disabled");
		});
	}
}

function showEmployees() {
	var companyId = $("#companyId").val();
	var employeeName = $.trim($("#requestedById").val());
	var uri = contextPath + "/getEmployees/byName?companyId="+ companyId
			+ "&name=" + processSearchName(employeeName) + "&isExact=false";
	$("#requestedById").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnRequestedById").val(ui.item.id);
			var employeeName = ui.item.lastName + ", "
				+ ui.item.firstName + " " + ui.item.middleName;
			$(this).val(employeeName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		var employeeName = item.lastName + ", "
			+ item.firstName + " " + item.middleName;
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + employeeName + "</a>" )
			.appendTo( ul );
	};
}

function getEmployee() {
	var companyId = $("#companyId").val();
	var txtEmployee = $.trim($("#requestedById").val());
	if(txtEmployee != ""){
		var uri = contextPath + "/getEmployees/bycCompanyAndName?companyId="+ companyId + "&name=" + txtEmployee;
		$("#hdnRequestedById").val("");
		$("#spanRequestedByError").text("");
		$("#requestedByError").text("");
		$.ajax({
			url: uri,
			success : function(employee) {
				if (txtEmployee != "") {
					console.log(employee);
					if (employee != null && typeof employee[0] != "undefined") {
						$("#hdnRequestedById").val(employee[0].id);
					} else {
						$("#spanRequestedByError").text("Invalid employee.");
						$("#hdnRequestedById").val("");
					}
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$("#spanRequestedByError").text("");
		$("#hdnRequestedById").val("");
	}
}

function filterCustomerAccts(selectCustomerAcct, accountId){
	$("#arCustomerAcctId").empty();
	if ($.trim($("#txtFrmCustomerName").val()) == "") {
		$("#hdnArCustomerId").val("");
	} else {
		var customerId = $("#hdnArCustomerId").val();
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getArCustomerAccounts/includeSavedInactive?arCustomerId="+customerId+
				"&companyId="+companyId+(typeof selectCustomerAcct != "undefined" ? "&arCustomerAccountId="+ selectCustomerAcct : "");
		var optionParser = {
				getValue: function (rowObject){
					if (rowObject != null)
						return rowObject["id"];
				},

				getLabel: function (rowObject){
					if (rowObject != null) {
						return rowObject["name"];
					}
				}
		};
		postHandler = {
				doPost: function(data) {
					loadAccountsFromCustAcct(accountId);
					$("#arCustomerAcctId").val(selectCustomerAcct);
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAcctId", optionParser, postHandler);
	}
 }
 
function loadAccountsFromCustAcct(accountId) {
	var arCustomerAcctId = $("#arCustomerAcctId").val();
		$("#accountId").empty();
	if(arCustomerAcctId != null) {
		var uri = contextPath+"/getAccounts/withdrawalCustAcct?arCustomerAcctId="+arCustomerAcctId;
		var optionParser =  {
				getValue: function (rowObject) {
					if (rowObject != null) {
						return rowObject["id"];
					}
					return "";
				},
				getLabel: function (rowObject) {
					if (rowObject != null) {
						return rowObject["number"] + " - "+rowObject["accountName"];
					}
					return "";
				}
			};
			postHandler = {
					doPost: function(data) {
						if(accountId != 0) {
							$("#accountId").val(accountId);
						}
					}
			};
		loadPopulate (uri, false, null, "accountId", optionParser, postHandler);
	}
}

function toggleCompanyAndPONumber(isDisabled){
	$("#companyId").attr("disabled", isDisabled);
	$("#poNumber").attr("readonly", isDisabled);
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="withdrawalSlip" id="withdrawalSlipForm">
		<div class="modFormLabel">Withdrawal Slip <span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="employeeId" id="hdnRequestedById"/>
		<form:hidden path="wsNumber"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="withdrawalItemsJson" id="withdrawalItemsJson"/>
		<form:hidden path="purchaseOderId" id="purchaseOderId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="refenceObjectId" id="refenceObjectPOId"/>
		<form:hidden path="fleetId" id="fleetProfileId"/>
		<form:hidden path="divisionId" id="divisionId"/>
		<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
		<div class="modForm">
		<table class="frmField_set">
				<tr>
					<td>
						<input type="button" id="btnPoReference" 
							value="Purchase Order Reference" onclick="showPOReference();"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td class="value">
						<form:errors path="purchaseOderId" cssClass="error"/>
					</td>
				</tr>
			</table>
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">Sequence No.</td>
						<td class="value">
							<c:if test="${withdrawalSlip.id > 0}">
								${withdrawalSlip.wsNumber}
							</c:if>
							<span id="sequenceNo"></span>
						</td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:choose>
								<c:when test="${withdrawalSlip.id > 0}">
									${withdrawalSlip.formWorkflow.currentFormStatus.description}
								</c:when>
								<c:otherwise>
									NEW
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Purchase Order Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Company </td>
						<td class="value">
							<form:select path="companyId" id="companyId" cssClass="frmSelectClass" onchange="companyOnChange();"
								items="${companies}" itemLabel="numberAndName" itemValue="id" >
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="companyId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Warehouse </td>
						<td class="value">
							<form:select path="warehouseId" id="warehouseId" class="frmSelectClass"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value" >
							<span id="spanWarehouseError" class="error"></span>
							<form:errors path="warehouseId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">PO No.</td>
						<td class="value"><form:input path="poNumber" id="poNumber" class="standard" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="poNumber" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Customer/Project</td>
						<td class="value">
							<form:input path="customerName" id="txtFrmCustomerName" onkeydown="showCustomers();"
								onkeyup="showCustomers();" onblur="getCustomer();" class="input"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<span class="error" id="spanCustomerError" style="margin left: 12px;"></span>
							<form:errors path="arCustomerId" id="customerErrorM" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Customer Account</td>
						<td class="value">
							<form:select path="customerAcctId" id="arCustomerAcctId" cssClass="frmSelectClass" onchange="loadAccountsFromCustAcct();">
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="customerAcctId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Fleet</td>
						<td class="value">
						<form:input path="fleetName" id="codeVesselName" class="input" onkeydown="showFleetProfiles();"
								onkeyup="showFleetProfiles();" onblur="getFleetProfile();"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<span id="codeVesselNameErrMsg" class="error" style="margin left: 12px;"></span>
							<form:errors path="fleetId" id="errorFleet" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Account Name</td>
						<td class="value">
							<form:select path="accountId" id="accountId" class="frmSelectClass"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="accountId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Date</td>
						<td class="value">
							<form:input path="date" onblur="evalDate('date');" 
								id="date" style="width: 120px;" cssClass="dateClass2"/>
							<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="date" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Requested By</td>
						<td class="value"><form:input path="requestedBy" id="requestedById" class="input" onkeypress="showEmployees();" onblur="getEmployee();"/></td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<span id="spanRequestedByError" class="error" style="margin left: 12px;"></span>
							<form:errors path="requestedBy" id="requestedByError" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Remarks</td>
						<td class="value"><form:textarea path="remarks" id="remarks" class="input" /></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="remarks" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Withdrawal Slip Table</legend>
				<div id="withdrawalSlipTable" >
				</div>
				<table>
					<tr>
						<td><span id="wsMessage" class="error"></span>
						<form:errors path="withdrawalSlipItems" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<table style="margin-top: 10px;" class="frmField_set">
				<tr>
					<td><form:errors path="formWorkflowId" cssClass="error"/></td>
				</tr>
				<tr><td ><input type="button" id="btnSaveWithdrawalSlip" value="Save" onclick="saveWithdrawalSlip();" style="float: right;"/> </td></tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>