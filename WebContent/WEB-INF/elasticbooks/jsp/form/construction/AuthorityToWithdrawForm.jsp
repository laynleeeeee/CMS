<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Authority to withdraw form jsp page
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.drsaleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
var $otherChargesTable = null;
$(document).ready(function() {
	initSerializedItemTbl();
	initNonSerializedItemTbl();
	initializeDocumentsTbl();
	initOtherChargesTbl();
	filterCustomerAccts("${authorityToWithdraw.arCustomerAcctId}");
	$("#txtSOReferenceNo").val("${authorityToWithdraw.soNumber}");
	disableFormFields();
});

function disableFormFields() {
	var isComplete = "${authorityToWithdraw.formWorkflow.complete}";
	if (isComplete == "true" || "${authorityToWithdraw.formWorkflow.currentStatusId}" == 4) {
		$("#authorityToWithdrawFormId :input").attr("disabled", "disabled");
		$("#imgDate").hide();
	}
	hideBrowseRefBtn();
	disableCustomerElems();//Disable customer and customer acct fields.
}

function hideBrowseRefBtn() {
	if("${authorityToWithdraw.id}" != 0) {
		$("#aOpen").hide();//Hide sales order reference button.
	}
}

function showCustomers() {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $("#txtCustomer").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&companyId="+companyId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnArCustomerId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function getCustomer() {
	var companyId = $("#companyId").val();
	var customerName = $("#txtCustomer").val();
	if (customerName != "") {
		$("#spanCustomerError").text("");
		$.ajax({
			url: contextPath + "/getArCustomers/new?name="+processSearchName(customerName)
					+"&isExact=true"+"&companyId="+companyId,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					var customerId = customer[0].id;
					$("#hdnArCustomerId").val(customerId);
					$("#txtCustomer").val(customer[0].name);
					filterCustomerAccts();
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#hdnArCustomerId").val("");
					$("#arCustomerAcctId").empty();
				}
			},
			dataType: "json"
		});
	} else {
		$("#hdnArCustomerId").val("");
		$("#arCustomerAcctId").empty();
	}
}

function filterCustomerAccts(arCustomerAcctId) {
	$("#arCustomerAcctId").empty();
	var customerId = $("#hdnArCustomerId").val();
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
			if (arCustomerAcctId != null && typeof arCustomerAcctId != "undefined") {
				$("#arCustomerAcctId").val(arCustomerAcctId);
			}
			// This is to remove any duplication.
			var found = [];
			$("#arCustomerAcctId option").each(function() {
				if($.inArray(this.value, found) != -1) 
					$(this).remove();
				found.push(this.value);
			});
		}
	};
	loadPopulate (uri, false, arCustomerAcctId, "arCustomerAcctId", optionParser, postHandler);
}


function initNonSerializedItemTbl() {
	var atwItemJson = JSON.parse($("#hdnAtwItemJson").val());
	setupNonSITableData(atwItemJson);
}

function setupNonSITableData(atwItemJson) {
	$("#nonSerializedItemTbl").html("");
	var path = "${pageContext.request.contextPath}";
	$nonSerializedItemTbl = $("#nonSerializedItemTbl").editableItem({
		data: atwItemJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "origWarehouseId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "existingStocks", "varType" : "double"},
			{"name" : "stockCode", "varType" : "string"}
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "authorityToWithdrawId",
				"cls" : "authorityToWithdrawId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemId",
				"cls" : "itemId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Stock Code",
				"cls" : "stockCode tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "20%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// do nothing;
					};
				})
			},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "20%"},
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
				"width" : "10%"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#nonSerializedItemTbl").on("focus", ".tblInputNumeric", function() {
		var value = $(this).val();
		if ($.trim(value) != "") {
			$(this).val(accounting.unformat(value));
		}
		disableInputFields(this, "spanNonSerialItemError");
	});

	$("#nonSerializedItemTbl").on("focus", ".tblInputText", function() {
		disableInputFields(this, "spanNonSerialItemError");
	});

	// Disable stock code fields.
	$("#nonSerializedItemTbl tr td .stockCode").each(function() {
		$(this).attr("disabled", "disabled");
	});
}

function initSerializedItemTbl() {
	var serialItemJson = JSON.parse($("#hdnSerialItemJson").val());
	setupSITableData(serialItemJson);
}

function setupSITableData(serialItemJson) {
	$("#tblSerializedItem").html("");
	var path = "${pageContext.request.contextPath}";
	$tblSerializedItem = $("#tblSerializedItem").editableItem({
		data: serialItemJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "authorityToWithdrawId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "origWarehouseId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "existingStocks", "varType" : "double"},
			{"name" : "serialNumber", "varType" : "string"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "itemSrpId", "varType" : "int"},
			{"name" : "srp", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "active", "varType" : "boolean"},
			{"name" : "itemDiscountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "boolean"},
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemSrpId",
				"cls" : "itemSrpId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemDiscountTypeId",
				"cls" : "itemDiscountTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "discountValue",
				"cls" : "discountValue",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "srp",
				"cls" : "srp",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "discount",
				"cls" : "discount",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "taxTypeId",
				"cls" : "taxTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "vatAmount",
				"cls" : "vatAmount",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "active",
				"cls" : "active",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "authorityToWithdrawId",
				"cls" : "authorityToWithdrawId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemId",
				"cls" : "itemId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Stock Code",
				"cls" : "stockCode tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "20%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// do nothing;
					};
				})
			},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "20%"},
			{"title" : "warehouseId",
				"cls" : "warehouseId",
				"editor" : "text",
				"visible" : false},
			{"title" : "Warehouse",
				"cls" : "warehouse tblSelectClass",
				"editor" : "select",
				"visible" : true ,
				"width" : "10%"},
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
				"width" : "10%"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#tblSerializedItem").on("focus", ".tblInputNumeric", function() {
		var value = $(this).val();
		if ($.trim(value) != "") {
			$(this).val(accounting.unformat(value));
		}
		disableInputFields(this, "spanSerialItemError");
	});

	$("#tblSerializedItem").on("focus", ".tblInputText", function() {
		disableInputFields(this, "spanSerialItemError");
	});

	$("#tblSerializedItem").on("blur", ".serialNumber", function(){
		assignESByWarehouse($(this).closest("td").find(".warehouse"));
		var $qty = $(this).closest("tr").find(".quantity");
		$($qty).text("1");
		$($qty).css("width","100%");
		$($qty).parent().css("text-align","right");
		$(".quantity").trigger("blur");
	});

	$("#tblSerializedItem").on("change", ".warehouse", function(){
		assignESByWarehouse($(this).closest("td").find(".warehouse"));
		var $serialNumber = $(this).closest("tr").find(".serialNumber");
		if (typeof $serialNumber != "undefined") {
			$serialNumber.val("");
		}
	});

	// Disable stock code fields.
	$("#tblSerializedItem tr td .stockCode").each(function() {
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

function companyOnChange(elem) {
	var companyId = $(elem).val();
	$("#hdnCompanyId").val(companyId);
}

var isSaving = false;
function saveATWForm() {
	if (!isSaving) {
		var refSalesOrderId = $("#hdnSalesOrderId").val();
		var hasSaleOrderRef = refSalesOrderId != "";
		var hasNoRefDocMsg = $("#referenceDocsMgs").html() == "";
		var hasExceededFileSize = checkExceededFileSize();
		if (!isSaving && hasNoRefDocMsg && !hasExceededFileSize && hasSaleOrderRef) {
			isSaving = true;
			enableCustElems();
			var companyId = $("#companyId").val();
			$("#hdnCompanyId").val(companyId);
			$("#hdnAtwItemJson").val($nonSerializedItemTbl.getData());
			$("#hdnSerialItemJson").val($tblSerializedItem.getData());
			$("#hdnReferenceDocumentsJsonId").val($documentsTable.getData());
			$("#atwLinesJson").val($otherChargesTable.getData());
			console.log($("#atwLinesJson").val());
			$("#btnSaveAtw").attr("disabled", "disabled");
			doPostWithCallBack ("authorityToWithdrawFormId", "form", function (data) {
				if (data.substring(0,5) == "saved") {
					var objectId = data.split(";")[1];
					var formStatus = new Object();
					formStatus.objectId = objectId;
					updateTable (formStatus);
					dojo.byId("form").innerHTML = "";
				} else {
					var soRefNumber = $("#txtSOReferenceNo").val();
					var currentStatus = $("#txtCurrentStatusId").val();
					var arCustomerAcctId = $("#arCustomerAcctId").val();
					if ("${authorityToWithdraw.id}" == 0){
						dojo.byId("form").innerHTML = data;
					} else {
						dojo.byId("editForm").innerHTML = data;
						$("#txtCurrentStatusId").val(currentStatus);
					}
					$("#companyId").val(companyId);
					$("#txtSOReferenceNo").val(soRefNumber);
					filterCustomerAccts(arCustomerAcctId);
					initSerializedItemTbl();
					initNonSerializedItemTbl();
					initializeDocumentsTbl();
					initOtherChargesTbl();
					disableCustomerElems();
					hideBrowseRefBtn();
				}
				$("#btnSaveAtw").removeAttr("disabled");
				isSaving = false;
			});
		} else if (hasExceededFileSize) {
			$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
		} else if (!hasSaleOrderRef) {
			$("#spanSalesOrderMsg").text("Sales order reference is required.");
		}
	}
}

function showSOReferences() {
	var companyId = $("#companyId").val();
	var url = contextPath+"/authorityToWithdaw/showReferenceForm?companyId="+companyId;
	$("#divSOReferenceId").load(url);
}

function loadSOReference(soRefId) {
	$.ajax({
		url: contextPath + "/authorityToWithdaw/loadSOReferenceForm?soRefId="+soRefId,
		success : function(so) {
			$("#divSOReferenceId").html("");
			$("#aClose")[0].click();
			updateHeader(so);
			console.log(so);
			setupSITableData(so.serialItems);
			setupNonSITableData(so.atwItems);
			setupOtherChargesTbl(so.atwLines);
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function updateHeader(so) {
	$("#hdnSalesOrderId").val(so.salesOrder.id);
	$("#hdnArCustomerId").val(so.salesOrder.arCustomerId);
	$("#txtCustomer").val(so.salesOrder.arCustomer.name);
	$("#txtSOReferenceNo").val(so.salesOrder.sequenceNumber);
	$("#shipTo").val(so.salesOrder.shipTo);
	filterCustomerAccts(so.salesOrder.arCustomerAcctId);
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#hdnReferenceDocumentsJsonId").val());
	var cPath = "${pageContext.request.contextPath}";
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
				"width" : "300px" },
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "600px" },
			{"title" : "file",
				"cls" : "file",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "File",
				"cls" : "fileInput tblInputFile",
				"editor" : "file",
				"visible" : true,
				"width" : "300px" },
			{"title" : "fileSize",
				"cls" : "fileSize",
				"editor" : "hidden",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});

	$("#documentsTable").on("change", ".fileInput", function() {
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize);
	});

	$("#documentsTable tbody tr").each(function() {
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable").on("click", ".fileLink", function() {
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});

	resizeTbl("#documentsTable", 3);
}

function resizeTbl(tableId, rowCount) {
	$(tableId).attr("width", "100%");
	$(tableId).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function convertDocToBase64($fileObj, sizeinbytes) {
	$("#referenceDocsMgs").html("");
	var $fileName = $($fileObj).closest("tr").find(".fileName");
	$($fileName).val("");
	$($fileObj).closest("tr").find(".fileLink").html("");
	var value = $.trim($($fileObj).val());
	var fileNames = value.split("\\");
	var isDuplicate = false;
	var name = null;
	var names = null;
	$("#documentsTable tbody tr").each(function() {
		name = $.trim($(this).find(".fileName").val());
		if (fileNames.slice(-1)[0] == name) {
			isDuplicate = true;
		}
	});
	if (isDuplicate) {
		$("#referenceDocsMgs").html("Duplicate file.");
	} else {
		if (value != "") {
			var file = $($fileObj)[0].files[0];
			var $file = $($fileObj).closest("tr").find(".file");
			var $docName = $($fileObj).closest("tr").find(".docName");
			var FR= new FileReader();
			FR.onload = function(e) {
				$($file).val(e.target.result);
			};
			FR.onprogress = function (e) {
				if (e.lengthComputable) {
					var percentLoaded = Math.round((e.loaded / e.total) * 100);
					if (percentLoaded < 100) {
						$($docName).html(percentLoaded + '%' + " loading...");
					}
				}
			}
			FR.onloadend = function (e) {
				var $td = $($fileObj).closest("tr").find(".docName").parent("td");
				$($docName).html("");
				$($fileName).val($.trim(fileNames.slice(-1)[0]));
				$($td).append("<a href='#' class='fileLink'>" + fileNames.slice(-1)[0] + "</a>");
			}
			FR.readAsDataURL( file );
		}
	}
}

function checkExceededFileSize () {
	var totalFileSize = 0;
	var FILE_INCREASE = 0.40;
	$("#documentsTable tbody tr").find(".fileSize").each(function() {
		if ($.trim($(this).val()) != "") {
			var fileSize = parseFloat(accounting.unformat($(this).val()));
			totalFileSize += (fileSize + (fileSize * FILE_INCREASE));
		}
	});
	// 14680064 = 10485760(10 MB) + (10485760 × .4)
	// Included the file increase
	if (totalFileSize > 14680064) {
		return true;
	}
	return false;
}

function disableCustomerElems() {
	$("#txtCustomer").attr('disabled', 'disabled');
	$("#arCustomerAcctId").attr('disabled', 'disabled');
}

function enableCustElems() {
	$("#txtCustomer").removeAttr('disabled');
	$("#arCustomerAcctId").removeAttr('disabled');
}

/**
 * This function will process the driver's full name in "lastName, firstName middleName" format.
 */
function processDriverName(firstName, lastName, middleName) {
	return lastName + ", " + firstName + " " + middleName;
}

function loadDrivers () {
	var driverName = encodeURIComponent($.trim($("#txtDriver").val()));
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getDrivers?name="+driverName+"&companyId="+companyId;
	$("#txtDriver").autocomplete({
		source: uri,
		select: function( event, ui ) {
			var name = processDriverName(ui.item.firstName, ui.item.lastName, ui.item.middleName);
			$(this).val(name);
			$("#hdnDriverId").val(ui.item.id);
			return false;
			$("#driverNameErr").html("");
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					$("#driverNameErr").html("");
					$("#spnDriverErr").text('');
					if (ui.item != null) {
						var name = processDriverName(ui.item.firstName, ui.item.lastName, ui.item.middleName);
						$(this).val(name);
					} else if($.trim($("#txtDriver").val()) != "") {
						$("#spnDriverErr").text('Invalid driver.');//Validation will only trigger if the driver input field is not empty.
						$("#hdnDriverId").val("");//Remove id.
					} else {
						$("#hdnDriverId").val("");//Remove id.
					}
				},
				error : function(error) {
					$("#spnDriverErr").text('Invalid driver.');
					$("#txtDriver").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		var name = processDriverName(item.firstName, item.lastName, item.middleName);
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + name + "</a>" )
			.appendTo( ul );
	};
}

function checkDriver() {
	if($.trim($("#txtDriver").val()) == "") {
		$("#hdnDriverId").val("");//Remove id.
	}
}

function showFleets() {
	var companyId = $("#companyId").val();
	var plateNo = $("#plateNo").val();
	var uri = contextPath + "/getFleetProfile/byPlateNo?plateNo="+processSearchName(plateNo)+"&companyId="+companyId;
	$("#plateNo").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#fleetProfileId").val(ui.item.id);
			$(this).val(ui.item.plateNo);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.plateNo + "</a>" )
			.appendTo( ul );
	};
}

/**
 * This function will populate the driver fields after loading fleet profile if driver fields are empty.
 * @param driver The driver domain object.
 */
function populateDriver(driver) {
	//Check if driver id is empty and driver is not null.
	if($("#hdnDriverId").val() == ""
			&& (driver != "" || driver != "undefined")) {
		$("#txtDriver").val(processDriverName(driver.firstName, driver.lastName, driver.middleName));//Set driver name.
		$("#hdnDriverId").val(driver.id);//Set driver id.
	}
}

function getFleet() {
	$("#fleetProfErr").html("");//Clear java validation.
	var companyId = $("#companyId").val();
	var plateNo = $("#plateNo").val();
	$("#spanPlateNoErr").text("");
	if (plateNo != "") {
		$.ajax({
			url: contextPath + "/getFleetProfile/byPlateNo?plateNo="+processSearchName(plateNo)
					+"&companyId="+companyId+"&isExact=true",
			success : function(fleet) {
				if (fleet != null && fleet[0] != undefined) {
					$("#hdnFleetProfileId").val(fleet[0].id);
					$("#plateNo").val(fleet[0].plateNo);
					populateDriver(fleet[0].driver);
				} else {
					$("#spanPlateNoErr").text("Invalid plate number.");
					$("#hdnFleetProfileId").val("");
				}
			},
			dataType: "json"
		});
	} else {
		$("#hdnFleetProfileId").val("");
	}
}

function initOtherChargesTbl() {
	var otherCharges = JSON.parse($("#atwLinesJson").val());
	setupOtherChargesTbl(otherCharges);
}

function setupOtherChargesTbl(otherCharges) {
	$("#atwLinesTbl").html("");
	var path = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#atwLinesTbl").editableItem({
		data: otherCharges,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "authorityToWithdrawId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "arLineSetupId", "varType" : "int"},
			{"name" : "arLineSetupName", "varType" : "string"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "unitOfMeasurementId", "varType" : "int"},
			{"name" : "discountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "discount", "varType" : "double"}
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "authorityToWithdrawId",
				"cls" : "authorityToWithdrawId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "arLineSetupId",
				"cls" : "arLineSetupId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Service",
				"cls" : "arLineSetupName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "75%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
			})},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "unitOfMeasurementId",
				"cls" : "unitOfMeasurementId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "UOM",
				"cls" : "unitMeasurementName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Gross Price",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "8%"},
			{"title" : "discountTypeId",
				"cls" : "discountTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "select",
				"visible" : false,
				"width" : "8%"},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "8%"},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : false,
				"width" : "8%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : false,
				"width" : "8%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : false,
				"width" : "10%"},
			{"title" : "Amount",
				"cls" : "amount tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "10%"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#atwLinesTbl tr td a").each(function() {
		hideElem(this);//Hide delete icon
	});

	$("#atwLinesTbl :input").attr("disabled", "disabled");
	resizeTbl("#atwLinesTbl", 11);
}

function hideElem(elem) {
	$(elem).css("display", "none");
}
</script>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divSOReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divSOReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="authorityToWithdraw" id="authorityToWithdrawFormId">
		<div class="modFormLabel">Authority To Withdraw
			<span class="btnClose" id="btnClose">[X]</span>
			<form:hidden path="id"/>
			<form:hidden path="ebObjectId" id="hdnRefObjectId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="updatedBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNumber"/>
			<form:hidden path="soNumber"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
			<form:hidden path="atwItemJson" id="hdnAtwItemJson"/>
			<form:hidden path="serialItemJson" id="hdnSerialItemJson"/>
			<form:hidden path="salesOrderId" id="hdnSalesOrderId"/>
			<form:hidden path="referenceDocumentsJson" id="hdnReferenceDocumentsJsonId"/>
			<form:hidden path="fleetProfileId" id="hdnFleetProfileId"/>
			<form:hidden path="driverId" id="hdnDriverId"/>
			<form:hidden path="atwLinesJson" id="atwLinesJson"/>
			<input type="hidden" id="isStockIn" value="${isStockIn}"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"
									value="${authorityToWithdraw.sequenceNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${authorityToWithdraw.formWorkflow.currentFormStatus.description}"/>
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
					<legend>Authority To Withdraw Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<select class="frmSelectClass" id="companyId" onchange="companyOnChange(this);">
									<c:forEach items="${companies}" var="company">
										<option value="${company.id}">${company.name}</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="date" id="formDate" onblur="evalDate('formDate');" 
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('formDate')" 
									style="cursor: pointer" style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="date" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Sales Order Reference</td>
							<td class="value">
								<input id="txtSOReferenceNo" readonly="readonly"/>
								<a href="#container" id="aOpen" data-reveal-id="divSOReferenceId" class="link_button"
									onclick="showSOReferences();">Browse SO</a>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value" colspan="2">
								<form:errors path="salesOrderId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value" colspan="2">
								<span id="spanSalesOrderMsg" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" cssClass="input" id="txtCustomer"
									onkeyup="showCustomers();" onblur="getCustomer();"/>
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
								<form:errors path="arCustomerId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer Account</td>
							<td class="value">
								<form:select path="arCustomerAcctId" id="arCustomerAcctId" cssClass="frmSelectClass">
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="arCustomerAcctId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Ship To</td>
							<td class="value">
								<form:textarea path="shipTo" id="shipTo" cssClass="txtArea"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="shipTo" cssClass="error"/>
							</td>
						</tr>
						<!-- Fleet -->
						<tr>
							<td class="labels">Plate Number</td>
							<td class="value">
								<form:input path="fleetProfile.plateNo" id="plateNo" class="input" onkeypress="showFleets();"
									onblur="getFleet();" />
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanPlateNoErr" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="fleetProfileId" id="fleetProfErr" cssClass="error" />
							</td>
						</tr>
						<!-- Driver -->
						<tr>
							<td class="labels">Driver</td>
							<td class="value">
								<form:input path="driverName" id="txtDriver" class="input" onkeypress="loadDrivers();" 
									onblur="checkDriver();"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spnDriverErr" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="driverName" id="driverNameErr" cssClass="error" />
							</td>
						</tr>
						<!-- Remarks -->
						<tr>
							<td class="labels">Remarks</td>
							<td class="value">
								<form:textarea path="remarks" id="remarks" cssClass="txtArea"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Serialized Items Table</legend>
					<div id="tblSerializedItem" class="tblSerial"></div>
					<table>
						<tr>
							<td>
								<form:errors path="serialItems" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td>
								<span id="spanSerialItemError" class="error"></span>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Non-Serialized Items Table</legend>
					<div id="nonSerializedItemTbl"></div>
					<table>
						<tr>
							<td>
								<form:errors path="atwItems" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td>
								<span id="spanNonSerialItemError" class="error"></span>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Service Table</legend>
					<div id="atwLinesTbl"></div>
					<table>
						<tr>
							<td>
								<form:errors path="atwLines" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="commonErrorMsg" cssClass="error"/></td>
					</tr>
				</table>
				<fieldset class="frmField_set">
					<legend>Document</legend>
					<div id="documentsTable"></div>
					<table>
						<tr>
							<td><form:errors path="referenceDocuments" cssClass="error" /></td>
						</tr>
						<tr>
							<td><span class="error" id="spDocSizeMsg"></span></td>
						</tr>
						<tr>
							<td><span class="error" id="referenceDocsMgs"></span></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSaveAtw" value="Save" onclick="saveATWForm();" /></td>
					</tr>
				</table>
			</div>
		</div>
	</form:form>
</div>
</body>
</html>