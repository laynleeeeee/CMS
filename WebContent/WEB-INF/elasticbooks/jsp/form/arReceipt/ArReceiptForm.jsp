<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR Receipt form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.projectRetentionHandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
input.numeric {
	width: 150px;
}

.tdProperties {
	border-right: 1px solid #000000;
}

.textBoxLabel, .txtTransactionNo, .txtAmount, .txtNumber{
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}

.divVeil {
    background-color: #F2F1F0;
}

#tblArRTransaction {
	cellspacing: 0;
	border: none;
}

#tblArRTransaction thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#tblArRTransaction tbody td {
	border-top: 1px solid #000000;
	border-bottom: 1px solid #000000;
}
.imgDelete{
	margin-left: 2px;
}
</style>
<script type="text/javascript">
var minimumRows = 4;
var selectCustomerAcct = "${arReceipt.arCustomerAccountId}";
var arTransactionCurrentIndex = "${fn:length(arReceipt.arRTransactions)}";
var currentCustAcctId = 0;
var arTransactionDtos = new Array();
var arTransactions = null;
var maxAllowableAmount = null;
var isAllowed = false;
var INVALID_TR_NO = "Invalid Transaction Number.";
var selectCompany = "${arReceipt.companyId}";
var selectReceiptMethod = "${arReceipt.receiptMethodId}";
var PREV_RATE = 0;
$(document).ready (function () {
	loadDivision("${arReceipt.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	initArReceiptLineTbl();
	if("${arReceipt.id}" != 0) {
		disableHeaderFields ();
		disableFormFields();
		formatMonetaryVal();
		disableAndSetCompany();
		// Disabled edit when form status = CANCELLED
		if("${arReceipt.formWorkflow.currentStatusId}" == 4 || "${arReceipt.formWorkflow.complete}" == "true") {
			$("#arReceiptForm :input").attr("disabled","disabled");
		}
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arReceipt.amount}'/>");
		if("${arReceipt.arReceiptLines[1]}" == "") {
			getRemainingCapBalance("${arReceipt.arReceiptLines[0].refenceObjectId}", "${arReceipt.id}");
		}
	} else {
		$("#hdnCompanyId").val($("#companyId").val());
		convertCurrencies($("#currencyId").val(), 1.0);
	}
	$("#refNumber").attr("readonly", "readonly");
	disableAndSetCompany();

	filterReceiptMethods(selectReceiptMethod);
	filterCustomerAccts();

	//Display Sequence Number
	$("#txtSequenceNumber").val("${sequenceNo}");
	computeGrandTotal();
	initializeDocumentsTbl();
});

function filterReceiptMethods(receiptMethodId) {
	$("#receiptMethodId").empty();
	var divisionId =$("#divisionId").val();
	var companyId = $("#companyId").val();
	if (divisionId != null) {
		var uri = contextPath + "/getArReceiptMethods/byDivision?divisionId="+divisionId+"&companyId="+companyId
				+"&receiptMethodId="+receiptMethodId;
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
					$("#receiptMethodId option").each(function() {
						if($.inArray(this.value, found) != -1)
							$(this).remove();
						found.push(this.value);
					});
				}
		};
		loadPopulate (uri, false, selectReceiptMethod, "receiptMethodId", optionParser, postHandler);
	}
}

function disableAndSetCompany() {
	//Disable and set company
	if ("${arReceipt.id}" != 0) {
		$("#companyId").attr("disabled","disabled");
		$("#companyId").append("<option selected='selected' value='"+"${arReceipt.companyId}"+"'>"+
				"${arReceipt.company.numberAndName}"+"</option>");
	}
}

function disableFormFields() {
	$("#imgDate1").hide();
	$("#imgDate2").hide();
	$("#recoupment").attr("readonly", "readonly");
	$("#retention").attr("readonly", "readonly");
	$("#btnSaveArReceipt").attr("disabled", "disabled");
}

function disableHeaderFields () {
	$("#arReceiptTypeId").css("pointer-events", "none");
	$("#refNumber").attr("readonly", "readonly");
	$("#txtCustomer").attr("readonly", "readonly");
	$("#hdnArCustomerId").attr("readonly", "readonly");
	$("#txtCustomer").attr("readonly", "readonly");
	$("#arCustomerAcctId").css("pointer-events", "none");
	$("#receiptNumber").attr("readonly", "readonly");
	$("#receiptDate").attr("readonly", "readonly");
	$("#imgDate1").hide();
	$("#maturityDate").attr("readonly", "readonly");
	$("#imgDate2").hide();
	$("#totalAmount").attr("readonly", "readonly");
	$("#currencyId").attr("disabled", "disabled");
	$("#receiptMethodId").attr("disabled", "disabled");
}

function assignCustAcctId() {
	currentCustAcctId = $("#arCustomerAcctId option:selected").val();
}

function formatMoney (textbox) {
	var money = formatDecimalPlaces($(textbox).val());
	$(textbox).val(money.replace(/[$]/gi, ''));
}

function parseAmount() {
	if ($.trim($("#totalAmount").val()) == "")
		$("#totalAmount").val("0.0");
	else {
		var formatAmt = $("#totalAmount").val().replace(/\,/g,"");
		$("#totalAmount").val(parseFloat(formatAmt,10));
	}
	$("#totalTAmount").val(accounting.unformat($("#spanTotalAmount").text()));
	$("#recoupment").val(accounting.unformat($("#recoupment").val()));
	$("#retention").val(accounting.unformat($("#retention").val()));
}

function getCustomer() {
	$.ajax({
		url: contextPath + "/getArCustomers?name="+$("#txtCustomer").val() + "&isExact=true",
		success : function(customer) {
			$("#spanCustomerError").text("");
			if (customer != null && customer[0] != undefined) {
				$("#hdnArCustomerId").val(customer[0].id);
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
	var divisionId = $("#divisionId").val();
	var uri = contextPath + "/getArCustomers/new?name="+$.trim($("#txtCustomer").val())+"&companyId="+companyId
			+"&isExact=false&divisionId="+ divisionId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnArCustomerId").val(ui.item.id);
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
						maxAllowableAmount = parseFloat(ui.item.maxAmount);
						isAllowed = false;
					}
					filterCustomerAccts();
					setArReceiptLineTbl();
				},
				error : function(error) {
					setArReceiptLineTbl();
					maxAllowableAmount = null;
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

function filterCustomerAccts() {
	$("#arCustomerAcctId").empty();
	if ($.trim($("#txtCustomer").val()) == "") {
		$("#hdnArCustomerId").val("");
	} else {
		var customerId = $("#hdnArCustomerId").val();
		var companyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var arCustomerAcctId = $();
		var uri = contextPath + "/getArCustomerAccounts/includeSavedInactive?arCustomerId="+customerId+
			"&companyId="+companyId+"&divisionId="+divisionId;
		//This is only for editing. This will add the saved inactive ar customer account.
		if(selectCustomerAcct != null && selectCustomerAcct != 0) {
			uri += "&arCustomerAccountId="+ selectCustomerAcct;
		}
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
					if($.inArray(this.value, found) != -1) {
						$(this).remove();
					}
					found.push(this.value);
				});
			}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAcctId", optionParser, postHandler);
	}
}

function assignCustomerAcct(select) {
	selectCustomerAcct = $(select).val();
}

function formatMonetaryVal() {
	formatMoney("#totalAmount");
}

function formatMoney(textboxId) {
	var money = formatDecimalPlaces($(textboxId).val());
	$(textboxId).val(money.replace(/[$]/gi, ''));
}

var isSaving = false;
function saveArReceipt() {
	if (isSaving == false && !checkExceededFileSize()) {
		isSaving = true;
		var refNo = $("#refNumber").val();
		$("#arReceiptLineJson").val($arReceiptLineTbl.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		parseAmount();
		$("#btnSaveArReceipt").attr("disabled", "disabled");
		$("#divisionId").removeAttr("disabled");
		$("#currencyId").removeAttr("disabled");
		$("#receiptMethodId").removeAttr("disabled");
		doPostWithCallBack ("arReceiptForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var customer = $("#txtCustomer").val();
				var isCheck = $("#arReceiptTypeId").val() == 2;
				var status = $("#txtArReceiptStatus").val();
				var currencyId = $("#currencyId").val();
				var rate = $("#hdnCurrRateValue").val();
				var receiptMethodId = $("#receiptMethodId").val();
				var spnRecoupment = $("#spnRecoupment").text();
				if ("${arReceipt.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val($("#hdnCompanyId").val());
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				if (isCheck) {
					$("#refNumber").removeAttr("disabled");
				}
				$("#txtCustomer").val(customer);
				$("#refNumber").val(refNo);
				$("#txtArReceiptStatus").val(status);
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#spnRecoupment").text(spnRecoupment);
				disableAndSetCompany();
				$("#receiptMethodId").val(receiptMethodId);
				selectReceiptMethod = receiptMethodId;
				parseAmount();
				formatMonetaryVal();
				if ("${arReceipt.id}" != 0) {
					disableHeaderFields ();
				}
				initArReceiptLineTbl();
				loadDivision("${arReceipt.divisionId}");
				$("#divisionId").attr("disabled","disabled");
				filterCustomerAccts();
				computeGrandTotal();
				filterReceiptMethods(selectReceiptMethod);
				convertCurrencies(currencyId, rate);
				initializeDocumentsTbl();
				isSaving = false;
			}
			$("#btnSaveArReceipt").removeAttr("disabled");
		});
	}
	if (checkExceededFileSize()) {
		$("#referenceDocsMgs").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function enableDisableCheck(value) {
	// If equals to check enable check number field
	if (value != 1) {
		$("#refNumber").removeAttr("readonly");
	} else {
		$("#refNumber").attr("readonly", "readonly");
	}
}

function computeGrandTotal() {
	var subTotal = 0;
	$("#arReceiptLineTbl").find(" tbody tr ").each(function(row) {
		var amount = accounting.unformat($(this).find(".amount").val());
		var refTypeId = $(this).find(".arReceiptLineTypeId").val();
		subTotal += amount;
	});
	$("#subTotal").html(formatNumbers(subTotal));
	var retention = accounting.unformat($("#retention").val());
	var recoupment = accounting.unformat($("#recoupment").val());
	$("#retention").val(formatDecimalPlaces(retention));
	$("#recoupment").val(formatDecimalPlaces(recoupment));
	var grandTotal = subTotal - retention - recoupment;
	$("#remainingBalanceId").html(grandTotal);
	$("#grandTotal").html(formatNumbers(grandTotal));
	disableEnableRecoupmentRetention();
}

function companyOnChange() {
	$("#hdnArCustomerId").val("");
	$("#txtCustomer").val("");
	$("#arCustomerAcctId").empty();
	filterReceiptMethods();
}

function resizeTbl(tableId, rowCount) {
	$(tableId).attr("width", "100%");
	$(tableId).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
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
				convertCurrencies(currencyId, 1.0);
			},
			dataType: "json"
		});
	} else {
		$("#hdnCurrRateId").val("");
		$("#hdnCurrRateValue").val("");
	}
	setArReceiptLineTbl();
}

function convertCurrencies(currencyId, rate) {
	if (currencyId == 1) {
		rate = 1.0;
		$("#hdnCurrRateId").val("");
		$("#hdnCurrRateValue").val(rate);
	}
	
	var totalAmt = accounting.unformat($("#totalAmount").val());
	totalAmt = (totalAmt * (PREV_RATE != 0 ? PREV_RATE : 1.0));
	$("#totalAmount").val(formatDecimalPlaces((totalAmt / rate)))

	$("#apPaymentLineTbl tbody tr").each(function(row) {
		var amount = accounting.unformat($(this).find(".amount").val());
		amount = (amount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".amount").val(formatDecimalPlaces((amount / rate)));
	});
	PREV_RATE = rate;
}

function loadDivision(divisionId) {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+"&divisionId="+ (divisionId != null && divisionId != "" ? divisionId : 0);
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

function initArReceiptLineTbl() {
	var arReceiptLineJson = JSON.parse($("#arReceiptLineJson").val());
	setArReceiptLineTbl(arReceiptLineJson);
}

function setArReceiptLineTbl(arReceiptLineJson) {
	$("#arReceiptLineTbl").html("");
	var cPath = "${pageContext.request.contextPath}";
	$arReceiptLineTbl = $("#arReceiptLineTbl").editableItem({
		data: arReceiptLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "arReceiptId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "arReceiptLineTypeId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "referenceNo", "varType" : "string"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "currencyRateValue", "varType" : "double"}
		],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "arReceiptId",
				"cls" : "arReceiptId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "arReceiptLineTypeId",
				"cls" : "arReceiptLineTypeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "AR Transaction / CAP",
				"cls" : "referenceNo tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "50%",
				"handler" : new ProjectRetentionHandler(new function() {
					this.handleTotal = function(total) {
						// Do nothing
					};
				})},
			{"title" : "Amount",
				"cls" : "amount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "45%" },
			{"title" : "currencyRateValue",
				"cls" : "currencyRateValue",
				"editor" : "hidden",
				"visible" : false },
		],
	});

	$("#arReceiptLineTbl").on("keypress", ".referenceNo", function(){
		showTransactions(this);
	});

	$("#arReceiptLineTbl").on("blur", ".amount", function(){
		computeGrandTotal();
		$(this).val(formatNumbers($(this).val()));
		disableEnableRecoupmentRetention();
		getRemainingCapBalance($(this).closest("tr").find(".refenceObjectId").val());
	});

	$("#arReceiptLineTbl").on("blur", ".referenceNo", function(){
		computeGrandTotal();
		disableEnableRecoupmentRetention();
		getRemainingCapBalance($(this).closest("tr").find(".refenceObjectId").val());
	});

	$("#arReceiptLineTbl tbody tr").each(function() {
		var amount = $(this).closest("tr").find(".amount").val();
		$(this).find(".amount").val(formatNumbers(amount));
	});
	computeGrandTotal();
	$("#arReceiptLineTbl table").attr("width", "100%");
}

function formatNumbers(value) {
	var formattedVal = accounting.unformat(value);
	if(Number(formattedVal) < 0) {
		formattedVal = "(" + formatDecimalPlaces(Math.abs(formattedVal)) +")";
	} else {
		formattedVal = formatDecimalPlaces(formattedVal);
	}
	return formattedVal;
}

function showTransactions (elem) {
	var companyId = $("#companyId").val();
	var customerAcctId = $("#arCustomerAcctId").val();
	var divisionId = $("#divisionId").val();
	var currencyId = $("#currencyId").val();
	var transNumber = encodeURIComponent($.trim($(elem).val()));
	var excludeObjId = $(elem).closest("tr").find(".refenceObjectId").val();
	var uri = contextPath + "/arReceipt/getArReceiptLines?transNumber="+transNumber
			+"&customerAcctId="+customerAcctId+"&companyId="+companyId+"&divisionId="+divisionId
			+"&currencyId="+currencyId+"&refObjIds="+getRefObjIds(excludeObjId);
	if(customerAcctId != null) {
		$(elem).autocomplete({
			source: uri,
			select: function( event, ui ) {
				$(this).val(ui.item.referenceNo);
				$(this).closest("tr").find(".amount").val(formatNumbers(ui.item.amount));
				var referenceObjectId = ui.item.refenceObjectId;
				$(this).closest("tr").find(".refenceObjectId").val(referenceObjectId);
				var receiptLineType = ui.item.arReceiptLineTypeId;
				$(this).closest("tr").find(".arReceiptLineTypeId").val(receiptLineType);
				var currencyRateValue = ui.item.currencyRateValue;
				$(this).closest("tr").find(".currencyRateValue").val(currencyRateValue);
				return false;
			}, minLength: 2,
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>" +item.referenceNo + "</a>" )
				.appendTo( ul );
		};
	}
}

function getRefObjIds(excludeObjId) {
	var ids = "";
	var index = 0;
	$("#arReceiptLineTbl tbody tr").each(function() {
		var id = $(this).closest("tr").find(".refenceObjectId").val();
		if(id.trim() != "" && id.trim() != excludeObjId) {
			ids += index == 0 ? id : ","+id;
			index++;
		}
	});
	return ids;
}

function assignReceiptMethod(select) {
	selectReceiptMethod = $(select).val();
}

function disableEnableRecoupmentRetention() {
	var totalTrans = countTransactions(false);
	if(totalTrans != 1) {
		$("#recoupment").attr("disabled", "disabled");
		$("#retention").attr("disabled", "disabled");
	} else {
		$("#recoupment").removeAttr("disabled");
		$("#retention").removeAttr("disabled");
	}
}

function countTransactions() {
	var noOfTrans = 0;
	$("#arReceiptLineTbl tbody tr").each(function() {
		var refObjectId = $(this).closest("tr").find(".refenceObjectId").val();
		if(refObjectId != "") {
			noOfTrans++;
		}
	});
	return noOfTrans;
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJson").val());
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

function getRemainingCapBalance(ariRefObjectId, arReceiptId) {
	var totalTrans = countTransactions(true);
	if(totalTrans == 1) {
		if (ariRefObjectId != "") {
			$.ajax({
				url: contextPath + "/arReceipt/getRemainingCapBalance?ariRefObjectId="+ariRefObjectId
						+(typeof arReceiptId == "undefined" ? "" : "&arReceiptId="+arReceiptId),
				success : function(balance) {
					if(balance > 0) {
						$("#spnRecoupment").text("Recoupment ("+formatDecimalPlaces(Number(balance))+")");
					} else {
						removeRemainingCapBalance();
					}
				},
				error : function(error) {
					console.log(error);
				},
				dataType: "json"
			});
		}
	} else {
		removeRemainingCapBalance();
	}
}

function removeRemainingCapBalance() {
	$("#spnRecoupment").text("Recoupment");
}
</script>
</head>
<body>
	<div id="divTransactionList" style="display: none"></div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="arReceipt" id="arReceiptForm">
			<div class="modFormLabel">Account Collection <span id="spanDivisionLbl"> - ${arReceipt.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNo"/>
			<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="ebObjectId" id="hdnEbObjectId"/>
			<form:hidden path="totalTAmount" id="totalTAmount"/>
			<form:hidden path="acArLinesJson" id="acArLinesJson"/>
			<form:hidden path="arAdvPaymentJson" id="arAdvPaymentJson"/>
			<form:hidden path="remainingBalance" id="remainingBalanceId"/>
			<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<form:hidden path="referenceDocumentJson" id="referenceDocumentsJson"/>
			<form:hidden path="arReceiptLineJson" id="arReceiptLineJson"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">AC No.</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"
									value="${arReceipt.sequenceNo}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${arReceipt.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtArReceiptStatus" class="textBoxLabel"
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Account Collection Header</legend>
					<table class="formTable">
						<form:hidden path="id" />
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<select id="companyId" class="frmSelectClass"
									onchange="assignCompany(this.value); companyOnChange();">
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
							<td class="labels">* Type</td>
							<td class="value">
								<form:select path="arReceiptTypeId" cssClass="frmSelectClass"
									onchange="enableDisableCheck(this.value);">
									<form:options items="${arReceiptTypes}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="arReceiptTypeId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Check/Online Reference No.</td>
							<td class="value">
								<form:input path="refNumber" id="refNumber" class="inputSmall" disabled="disabled"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="refNumber" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Receipt Method</td>
							<td class="value">
								<form:select path="receiptMethodId" id="receiptMethodId" cssClass="frmSelectClass"
									onchange="assignReceiptMethod (this); filterCustomerAccts();">
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="receiptMethodId" cssClass="error" style="margin-left: 12px;"/>
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
							<td class="labels">* Receipt No.</td>
							<td class="value"><form:input path="receiptNumber" id="receiptNumber" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="receiptNumber" cssClass="error" style="margin-left: 12px;" />
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
						<tr>
							<td class="labels">* Maturity Date</td>
							<td class="value">
								<form:input path="maturityDate" id="maturityDate" onblur="evalDate('maturityDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('maturityDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="maturityDate" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Currency</td>
							<td class="value">
								<form:select path="currencyId" id="currencyId" class="frmSelectClass"
									onchange="getCurrencyRate(this);setArReceiptLineTbl();">
									<form:options items="${currencies}" itemLabel="name" itemValue="id"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="currencyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Amount</td>
							<td class="value">
								<form:input path="amount" id="totalAmount" class="numeric" style="text-align: right;" size="20" 
									onblur="formatMoney(this);"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="amount" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AR Transaction</legend>
					<div id="arReceiptLineTbl"></div>
					<table>
						<tr>
							<td>
								<form:errors path="arReceiptLineJson" class="error" />
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set" style="border: none;">
					<table>
						<tr>
							<td>
								<form:errors path="acArLineMesage" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Document</legend>
					<div id="documentsTable"></div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><span class="error" id="spDocSizeMsg"></span></td>
					</tr>
					<tr>
						<td><form:errors path="referenceDocsMessage" cssClass="error" /></td>
					</tr>
					<tr>
						<td><span class="error" id="referenceDocsMgs"></span></td>
					</tr>
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error" /></td>
					</tr>
				</table>
				<!-- Grand Total -->
				<br>
				<table class="frmField_set footerTotalAmountTbl">
					<tr>
						<td style="width: 22%;"></td>
						<td style="width: 22%;"></td>
						<td style="width: 22%;">Sub Total</td>
						<td style="width: 22%;"></td>
						<td style="width: 12%;"><span id="subTotal"></span></td>
					</tr>
					<tr>
						<td colspan="3"><span id="spnRecoupment">Recoupment</span></td>
						<td></td>
						<td>
							<form:input path="recoupment" id="recoupment" class="input number" onblur="computeGrandTotal();"
							style="text-align: right; width: 185px;"/>
						</td>
					</tr>
					<tr>
						<td colspan="3"></td>
						<td></td>
						<td><form:errors path="recoupment" cssClass="error" /></td>
					</tr>
					<tr>
						<td colspan="3">Retention</td>
						<td></td>
						<td>
							<form:input path="retention" id="retention" class="input number" onblur="computeGrandTotal();"
							style="text-align: right; width: 185px;"/>
						</td>
					</tr>
					<tr>
						<td colspan="3">Amount Collected</td>
						<td></td>
						<td align="right"><span id="grandTotal">0.0</span></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSaveArReceipt" value="Save" onclick="saveArReceipt();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>