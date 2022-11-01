<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Withdrawal slip Form for JYEI.
 -->
<!-- <!DOCTYPE> -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.stockouthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.serialItemunitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
.disableSelect{
	pointer-events: none;
	cursor: default;
}

.quantity {
	text-align: right;
}

</style>
<script type="text/javascript">
var $withdrawalItemsTable = null;
var $documentsTable = null;
var hasPOReference = false;
var $tblSerializedItem = null;
var poReferenceWindow = null;
var IS_PAKYAWAN_SUBCON_ONLY = false;
var typeId = $("#requisitionTypeId").val();
$(document).ready(function () {
	initializeDocumentsTbl();
	initializeTable(JSON.parse($("#withdrawalItemsJson").val()));
	initializeSerializedItemTable(JSON.parse($("#serialItemsJson").val()));
	if ("${wsDto.withdrawalSlip.id}" != 0) {
		filterWarehouses("${wsDto.withdrawalSlip.warehouseId}");
		var customerAcctId = "${wsDto.withdrawalSlip.customerAcctId}";
		var accountId = "${wsDto.withdrawalSlip.accountId}";
		if (customerAcctId != null && customerAcctId != "") {
			filterCustomerAccts(customerAcctId, accountId);
		} else {
			loadAccounts(accountId);
		}
		if($("#codeVesselName").val() != "") {
			getFleetProfile();
		}

		var rfReference = $("#poNumber").val();
		if (rfReference != "" && rfReference != null){
			toggleCompanyAndPONumber(true);
			hasPOReference = true;
		} else {
			$("#companyId").attr("disabled", true);
		}

		disableHeaderFields();
		disableItemFields();
	} else {
		filterWarehouses();
	}
	disableAllFields();
});

function disableAllFields() {
	var isComplete = "${wsDto.withdrawalSlip.formWorkflow.complete}";
	var currentStatusId = "${wsDto.withdrawalSlip.formWorkflow.currentStatusId}";
	if(isComplete == "true" || currentStatusId == 4) {
		$("#withdrawalSlipFormId :input").attr("disabled","disabled");
	}
}

function disableHeaderFields() {
	$("#companyId").attr("disabled", "disabled");
	$("#aOpen").removeAttr('href');
	$("#aOpen").removeAttr('onclick');
}

function enableFields() {
	$("#companyId").removeAttr("disabled");
}

function companyOnChange (){
	filterWarehouses();
	initializeTable();
	initializeDocumentsTbl();
	initializeSerializedItemTable();
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
	$("#refenceObjectId").val("");
	$("#accountId").empty();
}

function filterWarehouses(warehouseId) {
	var companyId = $("#companyId").val();
	if(companyId > 0) {
		var uri = contextPath+"/getWarehouse/withInactive?companyId="+companyId+"&itemId=0";
		if(warehouseId != null && typeof warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
		}
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
	var isAddRow = $("#poNumber").val() == "";
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
		"addEmptyRow" : isAddRow,
		"itemTableMessage": "wsMessage"
	});

	$("#withdrawalSlipTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
		if(!isAddRow) {
			disableInputFields(this, "wsMessage");
		}
	});

	$("#withdrawalSlipTable").on("focus", ".tblInputText", function() {
		if(!isAddRow) {
			disableInputFields(this, "wsMessage");
		}
	});

	$("#withdrawalSlipTable").on("blur", ".tblInputNumeric", function() {
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.formatMoney($(this).val()));
		}
		if(!isAddRow) {
			disableInputFields(this, "wsMessage");
		}
	});
}

function showRFReference() {
	var companyId = $("#companyId").val();
	$("#divWSRFReference").load(contextPath + "/jyeiWithdrawalSlip/"+typeId
			+"/rfReference?companyId="+companyId+"&isExcludePrForms=true");
}

function loadRFReference (rfId) {
	$.ajax({
		url: contextPath + "/jyeiWithdrawalSlip/?rfId="+rfId,
		success : function(wsDto) {
			$("#spanAccountErrorMsg").text("");
			$("#divWSRFReference").html("");
			$("#aClose")[0].click();
			updateHeader(wsDto.withdrawalSlip.companyId, wsDto.withdrawalSlip.poNumber,
					wsDto.withdrawalSlip.refenceObjectId, wsDto.withdrawalSlip.requesterName,
					wsDto.withdrawalSlip.fleetName, wsDto.withdrawalSlip.customerName,
					wsDto.withdrawalSlip.warehouseId);
			initializeTable(wsDto.withdrawalSlip.withdrawalSlipItems);
			initializeSerializedItemTable(wsDto.serialItems);
			disableItemFields();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function disableInputFields(elem, spanIdName) {
	var $tr = $(elem).closest("tr");
	var $stockCode = $tr.find(".stockCode");
	$stockCode.attr("disabled", "disabled");
	if($.trim($stockCode.val()) == "") {
		$("#"+spanIdName).text("Adding of new item/s not in RF reference is not allowed.");
	}
}

function disableItemFields() {
	$("#tblSerializedItem tbody tr").each(function(row) {
		var stockCode = $(this).find(".stockCode").val();
		if($.trim(stockCode) != "") {
			var $qty = $(this).find(".quantity");
			$($qty).parent().css("text-align","right");
			$($qty).val(1);
		}
	});

	$("#withdrawalSlipTable tbody tr").each(function(row) {
		var stockCode = $(this).find(".stockCode").val();
	});
}

function formatMoney($elem) {
	if($.trim($($elem).val()) != "") {
		$($elem).val(accounting.formatMoney($($elem).val()));
	}
}

function updateHeader (companyId, rfNumber, refenceObjectId, requesterName,
		fleetName, customerName, warehouseId) {
	$("#companyId").val(companyId);
	companyOnChange();
	$("#poNumber").val(rfNumber);
	$("#refenceObjectId").val(refenceObjectId);
	if(fleetName != null && fleetName != "") {
		$("#codeVesselName").val(fleetName);
		getFleetProfile();
	} else if (customerName != null && customerName != "") {
		$("#txtFrmCustomerName").val(customerName);
		getCustomer();
	}
	$("#requesterName").val(requesterName);
	$("#plateNo").val("");
	toggleCompanyAndPONumber(true);
	filterWarehouses(warehouseId)
	hasPOReference = true;
}

function showFleetProfiles () {
	var companyId = $("#companyId").val();
	var codeVesselName = encodeURIComponent($.trim($("#codeVesselName").val()));
	var uri = contextPath + "/getFleetProfile?codeVesselName="+codeVesselName+"&isExact=false&isActive=true";
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
	$("#codeVesselNameErrMsg").text("");
	var companyId = $("#companyId").val();
	var codeVesselName = encodeURIComponent($.trim($("#codeVesselName").val()));
	var uri = "/getFleetProfile?codeVesselName="+codeVesselName+"&isExact=true";
	$("#codeVesselNameErrMsg").text("");
	if (codeVesselName != "") {
		$.ajax({
			url: contextPath + uri,
			success : function(fleetProfile) {
				if (fleetProfile != null && typeof fleetProfile != undefined) {
					$("#fleetProfileId").val(fleetProfile.id);
					$("#codeVesselName").val(fleetProfile.codeVesselName);
					$("#divisionId").val(fleetProfile.divisionId);
					$("#plateNo").val(fleetProfile.plateNo);
				}
				if($("#txtFrmCustomerName").val() == ""){
					loadAccounts();
				}
			},
			error : function(error) {
				$("#fleetProfileId").val("");
				$("#divisionId").val("");
				if($("#txtFrmCustomerName").val() == ""){
					loadAccounts();
				}
				console.log(error);
				$("#codeVesselNameErrMsg").text("Invalid Fleet.");
				$("#plateNo").val("");
			},
			dataType: "json"
		});
		$("#errorFleet").text("");
	} else {
		$("#fleetProfileId").val("");
		$("#plateNo").val("");
		if ($.trim($("#txtFrmCustomerName").val()) == ""){
			$("#accountId").empty();
		}
	}
}

function loadAccounts(accountId) {
	$("#spanAccountErrorMsg").text("");
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	$("#accountId").empty();
	if (divisionId != null && divisionId != "") {
		var uri = contextPath+"/getAccounts?companyId="+companyId+"&divisionId="+divisionId;
		if (accountId != null && accountId != "" && typeof accountId != undefined) {
			uri += "&accountId="+accountId;
		}
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
					if (accountId != null && accountId != "" && typeof accountId != undefined) {
						$("#accountId").val(accountId);
					}
				}
			};
		loadPopulate (uri, false, accountId, "accountId", optionParser, postHandler);
	}
	var selectedAcctId = $("#accountId option:selected").val();
	if (selectedAcctId == null || selectedAcctId == "") {
		$("#trAcctErrorMsgId").hide();
		$("#spanAccountErrorMsg").text("No account combination setup for the selected fleet/customer account.");
	}
}

function showCustomers () {
	var customerName = $.trim($("#txtFrmCustomerName").val());
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getArCustomers/new?name="+encodeURIComponent(customerName)+"&companyId="+companyId+"&isExact=false";
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

function getCustomer() {
	$("#spanCustomerError").text("");
	var customerName = encodeURIComponent($.trim($("#txtFrmCustomerName").val()));
	var companyId = $("#companyId").val();
	var hdnCustomerId = $("#hdnArCustomerId").val();
	var noCustomerMatch = false;
	if (customerName != "") {
		var companyId = $("#companyId").val();
		$.ajax({
			url: contextPath + "/getArCustomers/new?name="+customerName+"&isExact=true"
					+"&companyId="+companyId,
			async: false,
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
				$("#accountId").empty();
			},
			complete : function () {
				if (noCustomerMatch) {
					$("#spanCustomerError").text("Invalid customer/project.");
					$("#accountId").empty();
				}
				filterCustomerAccts();
			},
			dataType: "json"
		});
		$("#customerErrorM").text("");
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
	if (isSaving == false) {
		isSaving = true;
		enableFields();
		toggleCompanyAndPONumber(false);
		withdrawalItemsJson = $("#withdrawalItemsJson").val();
		$("#withdrawalItemsJson").val($withdrawalItemsTable.getData());
		$("#referenceDocumentJson").val($documentsTable.getData());
		$("#serialItemsJson").val($tblSerializedItem.getData());
		$("#btnSaveWithdrawalSlip").attr("disabled", "disabled");
		doPostWithCallBack ("withdrawalSlipFormId", "divWSForm", function(data) {
			if(data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("divWSForm").innerHTML = "";
				isSaving = false;
			} else {
				var date = $("#date").val();
				var warehouseId = $("#warehouseId").val();
				var arCustomerAcctId = $("#arCustomerAcctId").val();
				var poNumber = $("#poNumber").val();
				var accountId = $("#accountId option:selected").val();
				var companyId = $("#companyId option:selected").val();
				var codeVesselName = $.trim($("#codeVesselName").val());
				var hdnFleetProfileId = $("#fleetProfileId").val();
				var plateNo = $("#plateNo").val();
				var customerName = $("#txtFrmCustomerName").val();
				if ("${withdrawalSlip.id}" == 0) {
					dojo.byId("divWSForm").innerHTML = data;
					$("#companyId").val(companyId);
				} else {
					dojo.byId("editForm").innerHTML = data;
					updatePopupCss();
					disableHeaderFields();
				}
				if (codeVesselName != ""){
					$("#codeVesselName").val(codeVesselName);
					$("#plateNo").val(plateNo);
					loadAccounts(accountId);
				} 
				if(customerName != "" ) {
					filterCustomerAccts(arCustomerAcctId, accountId);
				}
				$("#spanAccountErrorMsg").text("");
				$("#trAcctErrorMsgId").show();
				$("#poNumber").val(poNumber);
				if (hasPOReference) {
					if(poNumber != "" && poNumber != null){
						toggleCompanyAndPONumber(true);
					} else {
						$("#companyId").attr("disabled", true);
					}
				}
				clearValues(hdnFleetProfileId);
				filterWarehouses(warehouseId);
				initializeTable(JSON.parse($("#withdrawalItemsJson").val()));
				initializeDocumentsTbl();
				initializeSerializedItemTable(JSON.parse($("#serialItemsJson").val()));
				disableItemFields();
				isSaving = false;
			}
			$("#btnSaveWithdrawalSlip").removeAttr("disabled");
		});
	}
}

function clearValues(hdnFleetProfileId) {
	if(hdnFleetProfileId == "") {
		$("#codeVesselName").val("");
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
	if (txtEmployee != ""){
		var uri = contextPath + "/getEmployees/bycCompanyAndName?companyId="+ companyId + "&name=" + txtEmployee;
		$("#hdnRequestedById").val("");
		$("#spanRequestedByError").text("");
		$("#requestedByError").text("");
		$.ajax({
			url: uri,
			success : function(employee) {
				if (employee != null && typeof employee[0] != "undefined") {
					$("#hdnRequestedById").val(employee[0].id);
				} else {
					$("#spanRequestedByError").text("Invalid employee.");
					$("#hdnRequestedById").val("");
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

function filterCustomerAccts(customerAcctId, accountId){
	$("#arCustomerAcctId").empty();
	var customerId = $("#hdnArCustomerId").val();
	if (customerId != "") {
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getArCustomerAccounts/includeSavedInactive?arCustomerId="+customerId
				+"&companyId="+companyId;
		if (customerAcctId != null && customerAcctId != "" && typeof customerAcctId != undefined) {
			uri += "&arCustomerAccountId="+customerAcctId;
		}
		var optionParser = {
			getValue: function (rowObject){
				if (rowObject != null) {
					$("#divisionId").val(rowObject.defaultDebitAC.divisionId);
					return rowObject["id"];
				}
			},
			getLabel: function (rowObject){
				if (rowObject != null) {
					return rowObject["name"];
				}
			}
		};
		postHandler = {
			doPost: function(data) {
				if (customerAcctId != null && customerAcctId != "" && typeof customerAcctId != undefined) {
					$("#arCustomerAcctId").val(customerAcctId);
				}
				loadDefaultCustomerWSAcct(accountId);
			}
		};
		loadPopulate (uri, false, customerAcctId, "arCustomerAcctId", optionParser, postHandler);
	} else {
		$("#hdnArCustomerId").val("");
	}
 }
 
function loadDefaultCustomerWSAcct(accountId) {
	$("#accountId").empty();
	var arCustomerAcctId = $("#arCustomerAcctId").val();
	if (arCustomerAcctId != null) {
		if (accountId != null && accountId != "" && typeof accountId != "undefined") {
			loadAccounts(accountId);
		} else {
			var uri = contextPath+"/getAccounts/withdrawalCustAcct?arCustomerAcctId="+arCustomerAcctId;
			$.ajax({
				url: uri,
				async: false,
				success : function(accounts) {
					if (accounts.length > 0) {
						accountId = accounts[0].id;
					}
					loadAccounts(accountId);
				},
				error : function(error) {
					console.log(error);
				},
				dataType: "json"
			});
		}
	}
}

function toggleCompanyAndPONumber(isDisabled) {
	$("#companyId").attr("disabled", isDisabled);
	$("#codeVesselName").attr("readonly", isDisabled);
	$("#txtFrmCustomerName").attr("readonly", isDisabled);
	$("#requesterName").attr("readonly", isDisabled);
	$("#arCustomerAcctId").attr("disabled", isDisabled);
	if($("#txtFrmCustomerName").val() != ""){
		$("#codeVesselName").attr("readonly", false);
	}
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$("#documentsTable").html("");
	$documentsTable = $("#documentsTable").editableItem({
		data: refDocsJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "referenceObjectId", "varType" : "int"},
				{"name" : "fileName", "varType" : "string"},
				{"name" : "description", "varType" : "string"},
				{"name" : "file", "varType" : "string"},
				{"name" : "fileInput", "varType" : "string"},
				{"name" : "fileSize", "varType" : "double"},
		],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "fileName",
				"cls" : "fileName",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Name",
				"cls" : "docName tblInputText",
				"editor" : "label",
				"visible" : true,
				"width" : "25%" },
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "45%" },
			{"title" : "file",
				"cls" : "file",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "File",
				"cls" : "fileInput tblInputFile",
				"editor" : "file",
				"visible" : true,
				"width" : "25%" },
			{"title" : "fileSize",
				"cls" : "fileSize",
				"editor" : "hidden",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});

	$("#documentsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize, $("#referenceDocsMgs"), $("#documentsTable"));
	});

	$("#documentsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

function initializeSerializedItemTable (serialItemJson) {
	var serialItemsJson = serialItemJson;
	var cPath = "${pageContext.request.contextPath}";
	var isAddRow = $("#poNumber").val() == "";

	$("#tblSerializedItem").html("");
	$tblSerializedItem = $("#tblSerializedItem").editableItem({
		data: serialItemsJson,
		jsonProperties: [
							{"name" : "id", "varType" : "int"},
							{"name" : "ebObjectId", "varType" : "int"},
							{"name" : "referenceObjectId", "varType" : "int"},
							{"name" : "origRefObjectId", "varType" : "int"},
							{"name" : "warehouseId", "varType" : "int"},
							{"name" : "itemId", "varType" : "int"},
							{"name" : "itemSrpId", "varType" : "int"},
							{"name" : "itemDiscountId", "varType" : "int"},
							{"name" : "itemAddOnId", "varType" : "int"},
							{"name" : "stockCode", "varType" : "string"},
							{"name" : "serialNumber", "varType" : "string"},
							{"name" : "description", "varType" : "string"},
							{"name" : "existingStock", "varType" : "double"},
							{"name" : "quantity", "varType" : "int"},
							{"name" : "srp", "varType" : "double"},
							{"name" : "unitCost", "varType" : "double"},
							{"name" : "discount", "varType" : "double"},
							{"name" : "addOn", "varType" : "double"},
							{"name" : "amount", "varType" : "double"}
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
				"width" : "11%", 
				"handler" : new SerialItemUnitCostTblHandler(new function(){
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true, 
				"width" : "13%"},
			{"title" : "Available <br> Stocks", 
				"cls" : "existingStock tblLabelNumeric",
				"editor" : "label",
				"visible" : true, 
				"width" : "7%"},
			{"title" : "Qty", 
				"cls" : "quantity tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%" },
			{"title" : "Serial Number",
				"cls" : "serialNumber tblInputText",
				"editor" : "text",
				"visible" : true, 
				"width" : "13%"},
			{"title" : "UOM", 
				"cls" : "uom txtUOM", 
				"editor" : "label",
				"visible" : true,
				"width" : "7%" },
		],
		"disableDuplicateStockCode" : false,
		"addEmptyRow" : isAddRow
	});

	$("#tblSerializedItem").on("focus", ".tblInputText", function(){
		if(!isAddRow) {
			disableInputFields(this, "serializedItemErrors");
		}
	});

	$("#tblSerializedItem").on("keydown", ".serialNumber", function(){
		loadItemSerialNumbers(this);
	});

	$("#tblSerializedItem").on("blur", ".serialNumber", function(){
		var $qty = $(this).closest("tr").find(".quantity");
		$($qty).css("width","100%");
		$($qty).parent().css("text-align","right");
		$(".quantity").trigger("blur");
		computeGrandTotal();
	});

	$("#tblSerializedItem").on("blur", ".quantity", function() {
		computeGrandTotal();
		if(!isAddRow) {
			disableInputFields(this, "serializedItemErrors");
		}
	});

	$("#tblSerializedItem").on("change", ".warehouse", function(){
		assignESByWarehouse($(this).closest("td").find(".warehouse"));
		var $serialNumber = $(this).closest("tr").find(".serialNumber");
		if(typeof $serialNumber != "undefined") {
			$serialNumber.val("");
		}
	});

	$("#tblSerializedItem").on("change", ".addOn", function() {
		computeGrandTotal();
		if(!isAddRow) {
			disableInputFields(this, "serializedItemErrors");
		}
	});

	$("#tblSerializedItem").on("change", ".discountType", function() {
		computeGrandTotal();
		if(!isAddRow) {
			disableInputFields(this, "serializedItemErrors");
		}
	});
}

$("#warehouseId").on("change", function() {
	$(".stockCode").trigger("blur");
});
</script>
</head>
<body>
<div id="rfContainer" class="popupModal">
<div id="divWSRFReferenceContainer" class="reveal-modal">
	<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
	<div id="divWSRFReference"></div>
</div>
</div>
<div id="divWSForm" class="formDivBigForms">
	<form:form method="POST" commandName="wsDto" id="withdrawalSlipFormId">
		<div class="modFormLabel">
			Withdrawal Slip
			<span class="btnClose" id="btnClose">[X]</span>
		</div>
		
		<form:hidden path="withdrawalSlip.id"/>
		<form:hidden path="withdrawalSlip.createdBy"/>
		<form:hidden path="withdrawalSlip.createdDate"/>
		<form:hidden path="withdrawalSlip.employeeId" id="hdnRequestedById"/>
		<form:hidden path="withdrawalSlip.wsNumber"/>
		<form:hidden path="withdrawalSlip.formWorkflowId"/>
		<form:hidden path="withdrawalSlip.withdrawalItemsJson" id="withdrawalItemsJson"/>
		<form:hidden path="withdrawalSlip.purchaseOderId" id="purchaseOderId"/>
		<form:hidden path="withdrawalSlip.ebObjectId" id="hdnRefObjectId"/>
		<form:hidden path="withdrawalSlip.refenceObjectId" id="refenceObjectId"/>
		<form:hidden path="withdrawalSlip.fleetId" id="fleetProfileId"/>
		<form:hidden path="withdrawalSlip.divisionId" id="divisionId"/>
		<form:hidden path="withdrawalSlip.arCustomerId" id="hdnArCustomerId"/>
		<form:hidden path="jyeiWithdrawalSlip.requisitionTypeId" id="requisitionTypeId"/>
		<form:hidden path="jyeiWithdrawalSlip.id"/>
		<form:hidden path="referenceDocumentJson" id="referenceDocumentJson" />
		<form:hidden path="serialItemsJson" id="serialItemsJson"/>
		<input type="hidden" id="isStockIn" value="${isStockIn}"/>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">Sequence No.</td>
						<td class="value">
							<c:if test="${wsDto.withdrawalSlip.id > 0}">
								${wsDto.withdrawalSlip.wsNumber}
							</c:if>
							<span id="sequenceNo"></span>
						</td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:choose>
								<c:when test="${wsDto.withdrawalSlip.id > 0}">
									${wsDto.withdrawalSlip.formWorkflow.currentFormStatus.description}
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
				<legend>Withdrawal Slip Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Company </td>
						<td class="value">
							<form:select path="withdrawalSlip.companyId" id="companyId" cssClass="frmSelectClass"
								onchange="companyOnChange();" items="${companies}" itemLabel="numberAndName"
								itemValue="id" >
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="withdrawalSlip.companyId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Requisition Form Reference</td>
						<td class="value"><form:input path="withdrawalSlip.poNumber" id="poNumber" readonly="true" class="standard" />
							<a href="#rfContainer" id="aOpen" data-reveal-id="divWSRFReference"
									 class="link_button" onclick="showRFReference();">Browse RF</a>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="withdrawalSlip.poNumber" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Warehouse </td>
						<td class="value">
							<form:select path="withdrawalSlip.warehouseId" id="warehouseId" class="frmSelectClass"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value" >
							<span id="spanWarehouseError" class="error"></span>
							<form:errors path="withdrawalSlip.warehouseId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Customer/Project</td>
						<td class="value">
							<form:input path="withdrawalSlip.customerName" id="txtFrmCustomerName" onkeydown="showCustomers();"
								onkeyup="showCustomers();" onblur="getCustomer();" class="input"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<span class="error" id="spanCustomerError" style="margin left: 12px;"></span>
							<form:errors path="withdrawalSlip.arCustomerId" id="customerErrorM" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Customer Account</td>
						<td class="value">
							<form:select path="withdrawalSlip.customerAcctId" id="arCustomerAcctId" cssClass="frmSelectClass">
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="withdrawalSlip.customerAcctId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Fleet ID</td>
						<td class="value">
						<form:input path="withdrawalSlip.fleetName" id="codeVesselName" class="input"
							onkeydown="showFleetProfiles();" onblur="getFleetProfile();"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<span id="codeVesselNameErrMsg" class="error" style="margin left: 12px;"></span>
							<form:errors path="withdrawalSlip.fleetId" id="errorFleet" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Plate No.</td>
						<td class="value">
						<input type="text" id="plateNo" class="input" readonly="readonly" style="border:0px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Account Name</td>
						<td class="value">
							<form:select path="withdrawalSlip.accountId" id="accountId" class="frmSelectClass"></form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<span id="spanAccountErrorMsg" class="Error"></span>
						</td>
					</tr>
					<tr id="trAcctErrorMsgId">
						<td class="labels"></td>
						<td class="value">
							<form:errors path="withdrawalSlip.accountId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Withdrawal Date</td>
						<td class="value">
							<form:input path="withdrawalSlip.date" onblur="evalDate('date');" 
								id="date" style="width: 120px;" cssClass="dateClass2"/>
							<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="withdrawalSlip.date" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Requested By</td>
						<td class="value"><form:input path="withdrawalSlip.requesterName" id="requesterName" class="input" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<span id="spanRequestedByError" class="error" style="margin left: 12px;"></span>
							<form:errors path="withdrawalSlip.requesterName" id="requestedByError" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Remarks</td>
						<td class="value"><form:textarea path="withdrawalSlip.remarks" id="remarks" class="input" /></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="withdrawalSlip.remarks" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Withdrawn By</td>
						<td class="value"><form:input path="jyeiWithdrawalSlip.withdrawnBy" id="withdrawnBy" class="input" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<span id="spanWithdrawnByError" class="error" style="margin left: 12px;"></span>
							<form:errors path="jyeiWithdrawalSlip.withdrawnBy" id="withdrawnByError" cssClass="error"/>
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
						<form:errors path="withdrawalSlip.withdrawalSlipItems" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
					<legend>Serialized Items Table</legend>
					<div id="tblSerializedItem" class="tblSerial"></div>
					<table>
						<tr>
							<td colspan="12">
								<span id="serializedItemErrors" class="error"></span>
								<span id="invalidSerialItemErrorMsg" class="error"></span>
								<form:errors path="siMessage" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
			<fieldset class="frmField_set">
			<legend>Document</legend>
				<div id="documentsTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<form:errors path="referenceDocsMessage" cssClass="error"
								style="margin-top: 12px;" />
						</td>
					</tr>
					<tr>
						<td colspan="12"><span class="error" id="spDocSizeMsg"></span></td>
					</tr>
					<tr>
						<td colspan="12"><span class="error" id="referenceDocsMgs" style="margin-top: 12px;"></span></td>
					</tr>
				</table>
			</fieldset>
			<table style="margin-top: 10px;" class="frmField_set">
				<tr><td ><input type="button" id="btnSaveWithdrawalSlip" value="Save" onclick="saveWithdrawalSlip();" style="float: right;"/> </td></tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>