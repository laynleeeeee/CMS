<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Work order form jsp page
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.constructionsaleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
var $woPurchasedItemTbl = null;
$(document).ready(function() {
	initWorkOrderLines();
	initWorkInstructionTbl();
	initNonSerializedItemTbl();
	initializeDocumentsTbl();
	initWoPurchasedItemTbl();
	if("${workOrder.id}" != 0) {
		$("#aOpen").hide();
		$("#waOpen").hide();
		$("#txtSORefNumber").val("${workOrder.soNumber}");
		$("#txtWORefNumber").val("${workOrder.refWoNumber}");
	}
	disableFormFields();
});

function disableFormFields() {
	var isComplete = "${workOrder.formWorkflow.complete}";
	var currentStatusId = "${workOrder.formWorkflow.currentStatusId}";
	if (isComplete == "true" || currentStatusId == 3 || currentStatusId == 4) {
		$("#workOrderFormId :input").attr("disabled", "disabled");
		$("#imgDate1").hide();
		$("#imgDate2").hide();
	}
}

function initNonSerializedItemTbl() {
	$("#nonSerializedItemTbl").html("");
	var woItemJson = JSON.parse($("#hdnWoItemJson").val());
	var path = "${pageContext.request.contextPath}";
	$nonSerializedItemTbl = $("#nonSerializedItemTbl").editableItem({
		data: woItemJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
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
			{"title" : "workOrderId",
				"cls" : "workOrderId",
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
	});
}


function initWoPurchasedItemTbl() {
	var woPurchasedItems = JSON.parse($("#woPurchasedItemsJson").val());
	setupWopItemsTbl(woPurchasedItems);
}

function setupWopItemsTbl(woPurchasedItems) {
	$("#woPurchasedItemTbl").html("");
	var path = "${pageContext.request.contextPath}";
	$woPurchasedItemTbl = $("#woPurchasedItemTbl").editableItem({
		data: woPurchasedItems,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
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

	$("#woPurchasedItemTbl tr td a").each(function() {
		hideElem(this);
	});

	$("#woPurchasedItemTbl :input").attr("disabled", "disabled");
}

function unformatTableValues() {
	$("#woPurchasedItemTbl tbody tr").each(function(row) {
		var discount = accounting.unformat($(this).find(".discount").html());
		$(this).find(".discount").html(discount);
	});
}

var isSaving = false;
function saveWorkOrderForm() {
	if (!isSaving) {
		enableCustElems();
		unformatTableValues();
		var salesOrderId = $("#hdnSalesOrderId").val();
		var refDocMsg = $("#referenceDocsMgs").html();
		var hasExceededFileSize = checkExceededFileSize();
		var hasSaleOrderRef = salesOrderId != "";
		if (!isSaving && refDocMsg == "" && !hasExceededFileSize && hasSaleOrderRef) {
			isSaving = true;
			$("#otherChargesTable :input").removeAttr("disabled");
			$("#hdnWoLineJson").val($otherChargesTable.getData());
			$("#hdnWoInstructionJson").val($workInstructionTbl.getData());
			$("#hdnWoItemJson").val($nonSerializedItemTbl.getData());
			$("#hdnReferenceDocumentsJsonId").val($documentsTable.getData());
			$("#woPurchasedItemsJson").val($woPurchasedItemTbl.getData());
			$("#btnSaveWorkOrder").attr("disabled", "disabled");
			doPostWithCallBack ("workOrderFormId", "form", function (data) {
				if (data.substring(0,5) == "saved") {
					var objectId = data.split(";")[1];
					var formStatus = new Object();
					formStatus.objectId = objectId;
					updateTable (formStatus);
					dojo.byId("form").innerHTML = "";
				} else {
					var woNumber = $("#txtWORefNumber").val();
					var soNumber = $("#txtSORefNumber").val();
					var companyId = $("#companyId").val();
					var companyName = $("#spCompanyName").text();
					var customerId = $("#hdnArCustomerId").val();
					var customerName = $("#spCustomerName").text();
					var customerAcctId = $("#hdnArCustomerAcctId").val();
					var customerAcctName = $("#spCustomerAcctName").text();
					var currentStatus = $("#txtCurrentStatusId").val();
					var workOrderId = $("#hdnWorkOrderId").val();
					if ("${workOrder.id}" == 0){
						dojo.byId("form").innerHTML = data;
						if (workOrderId != "") {
							$("#aOpen").hide();
						}
					} else {
						dojo.byId("editForm").innerHTML = data;
						$("#txtCurrentStatusId").val(currentStatus);
						$("#aOpen").hide();
						$("#waOpen").hide();
					}
					$("#txtWORefNumber").val(woNumber);
					updateHeader(salesOrderId, soNumber, companyId, companyName, customerId,
							customerName, customerAcctId, customerAcctName);
					initWorkOrderLines();
					initWorkInstructionTbl();
					initNonSerializedItemTbl();
					initializeDocumentsTbl();
					initWoPurchasedItemTbl();
				}
				$("#btnSaveWorkOrder").removeAttr("disabled");
				isSaving = false;
			});
		} else if (hasExceededFileSize) {
			$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
		} else if (!hasSaleOrderRef) {
			$("#spanSalesOrderMsg").text("Sales order reference is required.");
		}
	}
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

function initWorkOrderLines() {
	var woLineJson = JSON.parse($("#hdnWoLineJson").val());
	setupWorkOrderLines(woLineJson);
}

function setupWorkOrderLines(woLineJson) {
	$("#otherChargesTable").html("");
	var path = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: woLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "salesQuotationId", "varType" : "int"},
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
			{"name" : "discount", "varType" : "double"},
			{"name" : "refenceObjectId", "varType" : "int"}
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "salesQuotationId",
				"cls" : "salesQuotationId",
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
				"width" : "8%"},
			{"title" : "Amount",
				"cls" : "amount tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "10%"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable tr td a").each(function() {
		hideElem(this);
	});

	$("#otherChargesTable :input").attr("disabled", "disabled");
}

function hideElem(elem) {
	$(elem).css("display", "none");
}

function showWOSOReferences() {
	$("#divWOSOReferenceId").html("");
	$("#divWOSOReferenceId").load(contextPath+"/workOrder/showReferenceForm");
}

function loadWOSOReference(woSoRefId) {
	$.ajax({
		url: contextPath + "/workOrder/loadSOReferenceForm?woSoRefId="+woSoRefId,
		success : function(wo) {
			$("#spanSalesOrderMsg").text("");
			$("#divWOSOReferenceId").html("");
			$("#aClose")[0].click();
			updateHeader(wo.salesOrderId, wo.soNumber, wo.companyId, wo.company.name, wo.arCustomerId,
					wo.arCustomer.name, wo.arCustomerAcctId, wo.arCustomerAccount.name);
			setupWorkOrderLines(wo.woLines);
			setupWopItemsTbl(wo.woPurchasedItems);
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function updateHeader(salesOrderId, soNumber, companyId, companyName, arCustomerId,
		arCustomerName, arCustomerAcctId, arCustomerAcctName) {
	$("#hdnSalesOrderId").val(salesOrderId);
	$("#txtSORefNumber").val(soNumber);
	$("#companyId").val(companyId);
	$("#spCompanyName").text(companyName);
	$("#hdnArCustomerId").val(arCustomerId);
	$("#spCustomerName").text(arCustomerName);
	$("#hdnArCustomerAcctId").val(arCustomerAcctId);
	$("#spCustomerAcctName").text(arCustomerAcctName);
}

function initWorkInstructionTbl() {
	$("#workInstructionTbl").html("");
	var woInstructionJson = JSON.parse($("#hdnWoInstructionJson").val());
	var path = "${pageContext.request.contextPath}";
	$workInstructionTbl = $("#workInstructionTbl").editableItem({
		data: woInstructionJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "workOrderId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "workDescription", "varType" : "string"}
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "workOrderId",
				"cls" : "workOrderId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Instruction",
				"cls" : "workDescription tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "1200px",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// do nothing;
					};
				})
			},
		],
		"disableDuplicateStockCode" : false
	});

	resizeTbl("#workInstructionTbl", 2);
}

function resizeTbl(tableId, rowCount) {
	$(tableId).attr("width", "100%");
	$(tableId).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function showWorkOrderRefForm() {
	$("#divWOSOReferenceId").html("");
	$("#divWOSOReferenceId").load(contextPath+"/workOrder/showWorkOrderRefForm");
}

function loadRefWorkOrder(refWorkOrderId) {
	$.ajax({
		url: contextPath + "/workOrder/loadRefWorkOrder?refWorkOrderId="+refWorkOrderId,
		success : function(wo) {
			$("#spanSalesOrderMsg").text("");
			$("#divWOSOReferenceId").html("");
			$("#aClose")[0].click();
			$("#hdnWorkOrderId").val(wo.id);
			$("#txtWORefNumber").val(wo.refWoNumber);
			updateHeader(wo.salesOrderId, wo.soNumber, wo.companyId, wo.company.name, wo.arCustomerId,
					wo.arCustomer.name, wo.arCustomerAcctId, wo.arCustomerAccount.name);
			setupWorkOrderLines(wo.woLines);
			setupWopItemsTbl(wo.woPurchasedItems);
			$("#aOpen").hide();
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
	if ($.trim($stockCode.val()) == "") {
		$("#"+spanIdName).text("Adding of new item/s without SO is not allowed.");
	}
}
</script>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divWOSOReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divWOSOReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="workOrder" id="workOrderFormId">
		<div class="modFormLabel">Work Order
			<span class="btnClose" id="btnClose">[X]</span>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="ebObjectId" id="hdnRefObjectId"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="sequenceNumber"/>
		<form:hidden path="woInstructionJson" id="hdnWoInstructionJson"/>
		<form:hidden path="woItemJson" id="hdnWoItemJson"/>
		<form:hidden path="woLineJson" id="hdnWoLineJson"/>
		<form:hidden path="referenceDocumentsJson" id="hdnReferenceDocumentsJsonId"/>
		<form:hidden path="woPurchasedItemsJson" id="woPurchasedItemsJson"/>
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
								value="${workOrder.sequenceNumber}"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<c:set var="status" value="${workOrder.formWorkflow.currentFormStatus.description}"/>
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
				<legend>Work Order Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">WO Reference</td>
						<td class="value">
							<form:hidden path="refWorkOrderId" id="hdnWorkOrderId"/>
							<input id="txtWORefNumber" readonly="readonly"/>
							<a href="#container" id="waOpen" data-reveal-id="divWOSOReferenceId" class="link_button"
								onclick="showWorkOrderRefForm();">Browse WO</a>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="refWorkOrderId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><span id="spanWorkOrderMsg" class="error"></span></td>
					</tr>
					<tr>
						<td class="labels">* SO Reference</td>
						<td class="value">
							<form:hidden path="salesOrderId" id="hdnSalesOrderId"/>
							<input id="txtSORefNumber" readonly="readonly"/>
							<a href="#container" id="aOpen" data-reveal-id="divWOSOReferenceId" class="link_button"
								onclick="showWOSOReferences();">Browse SO</a>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="salesOrderId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><span id="spanSalesOrderMsg" class="error"></span></td>
					</tr>
					<tr>
						<td class="labels">* Company</td>
						<td class="value">
							<form:hidden path="companyId" id="companyId"/>
							<span id="spCompanyName">${workOrder.company.name}</span>
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
							<span id="spCustomerName">${workOrder.arCustomer.name}</span>
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
							<form:hidden path="arCustomerAcctId" id="hdnArCustomerAcctId"/>
							<span id="spCustomerAcctName">${workOrder.arCustomerAccount.name}</span>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="arCustomerAcctId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Date</td>
						<td class="value">
							<form:input path="date" id="date" onblur="evalDate('date');"
								style="width: 120px;" class="dateClass2" />
							<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('date')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="date" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Target End Date</td>
						<td class="value">
							<form:input path="targetEndDate" id="targetEndDate" onblur="evalDate('targetEndDate');"
								style="width: 120px;" class="dateClass2" />
							<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('targetEndDate')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="targetEndDate" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Work Description</td>
						<td class="value">
							<form:textarea path="workDescription" id="workDescription" cssClass="txtArea"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="workDescription" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Purchased Items Table</legend>
				<div id="woPurchasedItemTbl"></div>
				<table>
					<tr>
						<td>
							<form:errors path="woPurchasedItems" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td>
							<span id="spanWopiError" class="error"></span>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Service Table</legend>
				<div id="otherChargesTable"></div>
				<table>
					<tr>
						<td><form:errors path="woLines" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Work Instruction</legend>
				<div id="workInstructionTbl"></div>
				<table>
					<tr>
						<td>
							<form:errors path="woInstructions" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Materials Needed</legend>
				<div id="nonSerializedItemTbl"></div>
				<table>
					<tr>
						<td>
							<form:errors path="woItems" cssClass="error"/>
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
					<td align="right"><input type="button" id="btnSaveWorkOrder" value="Save" onclick="saveWorkOrderForm();" /></td>
				</tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>