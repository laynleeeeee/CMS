<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description: Purchase order form JSP page -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.itemhandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
var PREV_RATE = 0;
var IS_EXCLUDE_FG = true;
$(document).ready(function() {
	loadDivision("${rPurchaseOrder.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	filterSupplierAccts("${rPurchaseOrder.supplierAccountId}");
	initializeTable();
	initializetOtherCharges();
	initDocumentsTbl();
	disableFormFields();
});

function disableFormFields() {
	if ("${rPurchaseOrder.formWorkflow.currentStatusId}" == 4
			|| "${rPurchaseOrder.formWorkflow.currentStatusId}" == 3) {
		$("#btnSaveRPurchaseOrder").attr("disabled", "disabled");
	}
	if("${rPurchaseOrder.id}" != 0) {
		$("#currencyId").attr("disabled","disabled");
	}
}

function initializeTable() {
	var cPath = "${pageContext.request.contextPath}";
	var poItemsJson = JSON.parse($("#hdnPoItemsJson").val());
	$("#poItemDivTable").html("");
	$poItemTable = $("#poItemDivTable").editableItem({
		data : poItemsJson,
		jsonProperties : [
			{"name" : "id", "varType" : "int"},
			{"name" : "rPurchaseOrderId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"}
		],
		contextPath : cPath,
		header : [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "rPurchaseOrderId",
				"cls" : "rPurchaseOrderId",
				"editor" : "hidden",
				"visible" : false},
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
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible": true,
				"width" : "10%"},
			{"title" : "UOM",
				"cls" : "uom tblLabelText",
				"editor" : "label",
				"visible": true,
				"width" : "10%"},
			{"title" : "Previous<br>Unit Cost (PHP)",
				"cls" : "prevUC tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Gross Price",
				"cls" : "unitCost tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%" },
			{"title" : "taxTypeId",
				"cls" : "taxTypeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "10%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"}
			],
		"footer" : [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : true,
		"itemTableMessage" : "poItemErrors"
	});

	$("#poItemDivTable").on("focus", ".tblInputNumeric", function() {
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#poItemDivTable").on("blur", ".tblInputNumeric", function() {
		computeVatAmount(this, $(this).closest("tr").find(".taxType").val(), false);
		computeGrandTotal();
	});

	resizeTbl("#poItemDivTable", 10);
}

function loadSuppliers() {
	var supplierName = processSearchName($("#txtSupplier").val());
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath + "/getSuppliers/new?name="+supplierName+"&companyId="+companyId+"&divisionId="+divisionId;
	loadACItems("txtSupplier", "hdnSupplierId", null, uri, uri+"&isExact=true", "name",
		function() {
			$("#spanSupplierError").text("");
			validateSupplier();
		}, function() {
			$("#spanSupplierError").text("");
			validateSupplier();
		}, function() {
			$("#spanSupplierError").text("");
		}, function() {
			invalidSupplierMsg();
		}
	);
}

function invalidSupplierMsg(){
	$("#spanSupplierError").text("Invalid Supplier.");
}

function validateSupplier() {
	var supplier = $("#txtSupplier").val();
	if ($.trim(supplier) != "") {
		var selectedSupplierID = $("#hdnSupplierId").val();
		$.ajax({
			url: contextPath+"/getSupplier?name="+processSearchName(supplier),
			success : function(supplier) {
				if (supplier == null || typeof supplier == undefined) {
					invalidSupplierMsg();
				}
				filterSupplierAccts();
			},
			error : function(error) {
				console.log(error);
				invalidSupplierMsg();
			},
			dataType: "json"
		});

		if (selectedSupplierID == "") {
			invalidSupplierMsg();
		}
	}
}

function filterSupplierAccts(supplierAcctId) {
	var selectedCompanyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var selectedSupplierId = $("#hdnSupplierId").val();
	var txtSupplier = $("#txtSupplier").val();
	if ((selectedCompanyId != "" && selectedSupplierId != "") && txtSupplier != "") {
		var uri = contextPath+"/getApSupplierAccts?supplierId="+selectedSupplierId
				+"&companyId="+selectedCompanyId+"&divisionId="+divisionId;
		if(typeof supplierAcctId != "undefined") {
			uri += "&supplierAccountId="+supplierAcctId;
		}
		$("#supplierAccountId").empty();
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
				setSupAcctDefaultTerm();
			}
		};
		loadPopulate (uri, false, supplierAcctId, "supplierAccountId", optionParser, postHandler);
	} else if (txtSupplier == "") {
		//Empty the supplier account dropdown list and remove the supplier and supplier account id
		$("#supplierAccountId").empty();
		$("#hdnSupplierId").val(null);
	}
}

/**
 * Set the default term of  the selected supplier account.
 */
function setSupAcctDefaultTerm() {
	var supplierAcctId = $("#supplierAccountId").val();
	if(supplierAcctId != "") {
		$.ajax({
			url: contextPath+"/getTerm?supplierAccountId="+supplierAcctId,
			success : function(term) {
				if(term != null) {
					//set supplier account default term.
					$('#selectTermId option[value="'+term.id+'"]').attr("selected", "selected");
				}
			},
			error : function(error) {
				//No active term found.
				console.log(error);
			},
			dataType: "json"
		});
	}
}

var isSaving = false;
function saveRPurchaseOrder() {
	if (!isSaving && $("#spanReferenceDoc").html() == "" && !checkExceededFileSize()) {
		isSaving = true;
		$("#spanDocumentSize").text("");
		$("#hdnPoItemsJson").val($poItemTable.getData());
		$("#hdnPoLineJson").val($otherChargesTable.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#btnSaveRPurchaseOrder").attr("disabled", "disabled");
		$("#divisionId").removeAttr("disabled");
		$("#currencyId").removeAttr("disabled");
		doPostWithCallBack ("rPurchaseOrderForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var spanFormStatusLbl = $("#spanFormStatusLbl").text();
				var currencyId = $("#currencyId").val();
				var rate = $("#hdnCurrRateValue").val();
				var supplierName = $("#txtSupplier").val();
				var supplierAccountId = $("#supplierAccountId").val();
				if("${rPurchaseOrder.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#spanFormStatusLbl").text(spanFormStatusLbl);
				$("#txtSupplier").val(supplierName);
				loadDivision("${rPurchaseOrder.divisionId}");
				$("#divisionId").attr("disabled","disabled");
				filterSupplierAccts(supplierAccountId);
				initializeTable();
				initializetOtherCharges();
				initDocumentsTbl();
			}
			isSaving = false;
			$("#btnSaveRPurchaseOrder").removeAttr("disabled");
		});
	} else if (checkExceededFileSize()) {
		$("#spanDocumentSize").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function computeGrandTotal() {
	var totalVAT = 0;
	var totalNetOfVAT = 0;
	$("#poItemDivTable tbody tr").each(function(row) {
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
		totalNetOfVAT += accounting.unformat($(this).find(".amount").html());
	});

	$("#otherChargesTable tbody tr").each(function(row) {
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
		totalNetOfVAT += accounting.unformat($(this).find(".amount").val());
	});

	var formattedTotalVAT = formatDecimalPlaces(parseFloat(totalVAT).toFixed(6));
	var formattedSubTotal = formatDecimalPlaces(parseFloat(totalNetOfVAT).toFixed(6));
	var grandTotal = accounting.unformat(formattedSubTotal) + accounting.unformat(formattedTotalVAT);
	$("#subTotal").html(formattedSubTotal);
	$("#totalVAT").html(formattedTotalVAT);
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function companyOnChange() {
	$("#poItemDivTable").html("");
	$("#txtSupplier").val("");
	$("#hdnSupplierId").val("");
	$("#supplierAccountId").empty();
	$("#supplierAccountId").val("");
}

function initializetOtherCharges() {
	var cPath = "${pageContext.request.contextPath}";
	var poLineJson = JSON.parse($("#hdnPoLineJson").val());
	$("#otherChargesTable").html("");
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: poLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "apLineSetupId", "varType" : "int"},
			{"name" : "unitOfMeasurementId", "varType" : "int"},
			{"name" : "quantity", "varType" : "int"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "apLineSetupName", "varType" : "string"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"}
		],
		contextPath: cPath,
		header:[
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "apLineSetupId",
				"cls" : "apLineSetupId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "unitOfMeasurementId",
				"cls" : "unitOfMeasurementId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "AP Line",
				"cls" : "apLineSetupName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "20%",
				"handler" : new ItemTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%" },
			{"title" : "UOM",
				"cls" : "unitMeasurementName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%",
				"handler" : new ItemTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "UP",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%"},
			{"title" : "taxTypeId",
				"cls" : "taxTypeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "15%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Amount",
				"cls" : "amount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "15%"}
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
		updateOChargesAmt($(this).closest("tr").find(".amount").val());
		computeGrandTotal();
	});

	resizeTbl("#otherChargesTable", 8);
}

function resizeTbl(tableId, rowCount){
	var $tbl = $(tableId);
	$($tbl).attr("width", "100%");
	addTotalLabel($tbl)
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function addTotalLabel($tbl){
	var $tr = $($tbl).find("tfoot tr td:nth-child(1)");
	$($tr).html("Total");
}

function loadDivision(divisionId) {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+"&divisionId="+ (divisionId != null && divisionId == "" ? divisionId : 0);
	$("#divisionId").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},
		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	var postHandler = {
		doPost: function(data) {
			if (divisionId != 0 && divisionId != "" && divisionId != "undefined") {
				$("#divisionId").val(divisionId);
			}
		}
	};
	loadPopulate (uri, false, divisionId, "divisionId", optionParser, postHandler);
}

function getCurrencyRate(elem) {
	var currencyId = $(elem).val();
	if (currencyId != "" && currencyId != null && currencyId != 0) {
		$.ajax({
			url: contextPath+"/getCurrency/getLatestRate?currencyId="+currencyId,
			success : function(currencyRate) {
				$("#hdnCurrRateId").val(currencyRate.id);
				var rate = currencyRate.rate;
				$("#hdnCurrRateValue").val(rate);
				convertCurrencies(currencyId, rate);
			},
			error : function(error) {
				// No currency rate found, resetting to default rate to PHP
				convertCurrencies(1, 1.0);
			},
			dataType: "json"
		});
	} else {
		$("#hdnCurrRateId").val("");
		$("#hdnCurrRateValue").val("");
	}
}

function convertCurrencies(currencyId, rate) {
	currencyId = currencyId != "" ? currencyId : $("#currencyId").val();
	if (currencyId == 1) {
		// Set default rate to PHP
		rate = 1.0;
		$("#hdnCurrRateId").val("");
		$("#hdnCurrRateValue").val(rate);
	}

	var totalGoodsAmt = 0;
	var totalVAT = 0;
	$("#poItemDivTable tbody tr").each(function(row) {
		var unitCost = accounting.unformat($(this).find(".unitCost").val());
		unitCost = (unitCost * (PREV_RATE != 0 ? PREV_RATE : 1.0)) / rate;
		$(this).find(".unitCost").val(formatDecimalPlaces(unitCost));

		var vat = accounting.unformat($(this).find(".vatAmount").html());
		vat = (vat * (PREV_RATE != 0 ? PREV_RATE : 1.0)) / rate;
		$(this).find(".vatAmount").html(formatDecimalPlaces(vat));

		var amount = accounting.unformat($(this).find(".amount").html());
		amount = (amount * (PREV_RATE != 0 ? PREV_RATE : 1.0)) / rate;
		$(this).find(".amount").html(formatDecimalPlaces(amount));
		totalGoodsAmt += amount;
		totalVAT += vat;
	});
	$("#poItemDivTable").find("tfoot tr .amount").html(formatDecimalPlaces(totalGoodsAmt));

	var totalServiceAmt = 0;
	$("#otherChargesTable tbody tr").each(function(row) {
		var upAmount = accounting.unformat($(this).find(".upAmount").val());
		upAmount = (upAmount * (PREV_RATE != 0 ? PREV_RATE : 1.0)) / rate;
		$(this).find(".upAmount").val(formatDecimalPlaces(upAmount));

		var vat = accounting.unformat($(this).find(".vatAmount").html());
		vat = (vat * (PREV_RATE != 0 ? PREV_RATE : 1.0)) / rate;
		$(this).find(".vatAmount").html(formatDecimalPlaces(vat));

		var amount = accounting.unformat($(this).find(".amount").val());
		amount = (amount * (PREV_RATE != 0 ? PREV_RATE : 1.0)) / rate;
		$(this).find(".amount").val(formatDecimalPlaces(amount));
		totalServiceAmt += amount;
		totalVAT += vat;
	});
	$("#otherChargesTable").find("tfoot tr .amount").html(formatDecimalPlaces(totalServiceAmt));
	// Reset total amounts
	var totalNetOfVAT = totalGoodsAmt + totalServiceAmt;
	$("#subTotal").html(formatDecimalPlaces(totalNetOfVAT));
	$("#totalVAT").html(formatDecimalPlaces(totalVAT));
	var grandTotal = totalNetOfVAT + totalVAT;
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
	// Set previous rate
	PREV_RATE = rate;
}

function initDocumentsTbl() {
	var referenceDocumentJson = JSON.parse($("#referenceDocumentsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$documentsTable = $("#documentsTable").editableItem({
		data: referenceDocumentJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "referenceObjectId", "varType" : "int"},
			{"name" : "fileName", "varType" : "string"},
			{"name" : "description", "varType" : "string"},
			{"name" : "file", "varType" : "string"},
			{"name" : "fileInput", "varType" : "string"},
			{"name" : "fileSize", "varType" : "double"},
			{"name" : "fileExtension", "varType" : "string"}
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
				"visible" : false},
			{"title" : "fileExtension",
				"cls" : "fileExtension",
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
		convertDocToBase64($(this), fileSize, $("#spanReferenceDoc"), $("#documentsTable"));
	});

	$("#documentsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		console.log("convert base 64 to file");
		convBase64ToFile($($document).val(), fileName);
	});

	$("#documentsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable table").attr("width", "100%");
}

function checkExceededFileSize() {
	var totalFileSize = 0;
	var FILE_INCREASE = 0.40;
	$("#documentsTable tbody tr").find(".fileSize").each(function() {
		if ($.trim($(this).val()) != "") {
			var fileSize = parseFloat(accounting.unformat($(this).val()));
			totalFileSize += (fileSize + (fileSize * FILE_INCREASE));
		}
	});
	// Included the file increase
	// 14680064 = 10485760(10 MB) + (10485760 × .4)
	if (totalFileSize > 14680064) {
		return true;
	}
	return false;
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="rPurchaseOrder" id="rPurchaseOrderForm">
		<div class="modFormLabel">Purchase Order<span id="spanDivisionLbl"> - ${rPurchaseOrder.division.name}</span> 
			<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="poNumber"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="supplierId" id="hdnSupplierId"/>
			<form:hidden path="requestedById" id="hdnRequestedById"/>
			<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<form:hidden path="poItemsJson" id="hdnPoItemsJson"/>
			<form:hidden path="poLineJson" id="hdnPoLineJson"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">PO No.</td>
							<td class="value">
								<span id="spanPoNumber">
									<c:if test="${rPurchaseOrder.id > 0}">
										${rPurchaseOrder.poNumber}
									</c:if>
								</span>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<td class="value">
								<span id="spanFormStatusLbl">
									<c:choose>
										<c:when test="${rPurchaseOrder.formWorkflow != null}">
											${rPurchaseOrder.formWorkflow.currentFormStatus.description}
										</c:when>
										<c:otherwise>
											NEW
										</c:otherwise>
									</c:choose>
								</span>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Purchase Order Header</legend>
					<table class="formTable">
						<form:hidden path="id" />
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass"
									onchange="companyOnChange();">
									<form:options items="${companies}" itemLabel="name" itemValue="id"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Division</td>
							<td class="value">
								<form:select path="divisionId" id="divisionId" class="frmSelectClass"></form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="poDate" onblur="evalDate('poDate')" style="width: 120px;"
									cssClass="dateClass2"/>
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('poDate')" style="cursor: pointer"
									style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="poDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Estimated Delivery Date</td>
							<td class="value">
								<form:input path="estDeliveryDate" onblur="evalDate('estDeliveryDate')"
									style="width: 120px;" cssClass="dateClass2"/>
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('estDeliveryDate')" style="cursor: pointer"
									style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="estDeliveryDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">BMS No.</td>
							<td class="value">
								<form:input path="bmsNumber" id="bmsNumber" class="inputSmall"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="bmsNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Supplier</td>
							<td class="value">
								<form:input path="supplier.name" id="txtSupplier" value="${rPurchaseOrder.supplier.name}"
									onkeydown="loadSuppliers();" onblur="validateSupplier();" class="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanSupplierError" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="supplierId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Supplier Account</td>
							<td class="value">
								<form:select path="supplierAccountId" id="supplierAccountId" onchange="setSupAcctDefaultTerm();"
									class="frmSelectClass"></form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanSupplierAcctError"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="supplierAccountId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Contact Person</td>
							<td class="value">
								<form:input path="requesterName" id="requesterName" class="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="requesterName" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Term</td>
							<td class="value">
								<form:select path="termId" cssClass="frmSelectClass" id="selectTermId">
									<form:options items="${terms}" itemLabel="name" itemValue="id"></form:options>
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="termId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Remarks</td>
							<td class="value">
								<form:textarea path="remarks" rows="3"
									cssStyle="width: 350px; resize: none;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="remarks" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Currency</td>
							<td class="value">
								<form:select path="currencyId" id="currencyId" class="frmSelectClass"
									onchange="getCurrencyRate(this);">
									<form:options items="${currencies}" itemLabel="name" itemValue="id"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="currencyId" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Good/s</legend>
					<div id="poItemDivTable"></div>
					<table>
						<tr>
							<td><span id="poItemErrors" class="error"></span></td>
						</tr>
						<tr>
							<td><form:errors path="rPoItems" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Service/s</legend>
					<div id="otherChargesTable"></div>
					<table>
						<tr>
							<td>
								<form:errors path="poLines" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Document</legend>
					<div id="documentsTable"></div>
					<table>
						<tr>
							<td><form:errors path="referenceDocsMessage" cssClass="error"/></td>
						</tr>
						<tr>
							<td><span class="error" id="spanDocumentSize"></span></td>
						</tr>
						<tr>
							<td><span class="error" id="spanReferenceDoc"></span></td>
						</tr>
					</table>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="poMessage" cssClass="error"/></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right">Sub Total</td>
						<td align="right"><span id="subTotal">0.0</span></td>
					</tr>
					<tr>
						<td align="right">Total VAT</td>
						<td align="right"><span id="totalVAT">0.0</span></td>
					</tr>
					<tr>
						<td align="right">Total Amount Due</td>
						<td align="right"><span id="grandTotal">0.0</span></td>
					</tr>
					<tr>
						<td><form:errors path="grandTotal" cssClass="error"/></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSaveRPurchaseOrder"
							value="Save" onclick="saveRPurchaseOrder();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>