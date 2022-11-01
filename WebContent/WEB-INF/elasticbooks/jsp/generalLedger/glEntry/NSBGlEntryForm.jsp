<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: General ledger entry form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.itemhandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery1.7.2min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<title>General Ledger Entry Form</title>
<style type="text/css">
.debitAmount, .creditAmount, #debitAmount, #creditAmount {
	float: right;
	width: 100%;
	text-align: right;
}
</style>
<script type="text/javascript">
var glStatusId = "${glStatusId}";
var glEntrySourceId  = "${glEntrySourceId}";
var isEdit = "${isEdit}";
var rowObj = null;
var currentFormStatus = "${generalLedgerDto.generalLedger.formWorkflow.currentStatusId}";
var currentGLIndex = 0;
var PREV_RATE = 0;
if ("${pId}" > 0) {
	if ("${flag}" == "edit") {
		$.noConflict();
	}
	// Form status 6 for posted and 4 for cancelled.
	if (currentFormStatus == "6" || currentFormStatus == "4") {
		$("#generalLedgerDto :input").attr("disabled", "disabled");
	}
} else {
	$.noConflict();
}

$(document).ready (function () {
	loadDivision("${generalLedgerDto.divisionId}");
	$("#slctDivisionId").attr("disabled","disabled");
	initializeTable();
	initDocumentsTbl();
	$("#spanEntrySource").text(glEntrySourceId == 1 ? "General Ledger" : "");
	$("#glEntrySourceId").val(glEntrySourceId);

	if ("${pId}" > 0) {
		disableAndSetCompany();
	}
	updateTotalValue();
});


function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${generalLedgerDto.companyId}"+"'>"+
	"${generalLedgerDto.generalLedger.company.numberAndName}"+"</option>");
}

function loadDivision(divisionId) {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+"&divisionId="+ (divisionId != null && divisionId == "" ? divisionId : 0);
	$("#slctDivisionId").empty();
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
				$("#slctDivisionId").val(divisionId);
			}
		}
	};
	loadPopulate (uri, false, divisionId, "slctDivisionId", optionParser, postHandler);
}

function initializeTable() {
	var glLinesJson = JSON.parse($("#hdnGlEntriesJson").val());
	$("#glLinesDivTable").html("");
	$glLinesTable = $("#glLinesDivTable").editableItem({
		data : glLinesJson,
		jsonProperties : [ {
			"name" : "id",
			"varType" : "int"
		}, {
			"name" : "generalLedgerId",
			"varType" : "int"
		}, {
			"name" : "ebObjectId",
			"varType" : "int"
		}, {
			"name" : "divisionId",
			"varType" : "int"
		}, {
			"name" : "accountId",
			"varType" : "int"
		}, {
			"name" : "divisionName",
			"varType" : "string"
		}, {
			"name" : "accountName",
			"varType" : "string"
		}, {
			"name" : "accountCombinationId",
			"varType" : "int"
		}, {
			"name" : "combination",
			"varType" : "string"
		}, {
			"name" : "debit",
			"varType" : "boolean"
		}, {
			"name" : "debitAmount",
			"varType" : "double"
		}, {
			"name" : "creditAmount",
			"varType" : "double"
		}, {
			"name" : "description",
			"varType" : "string"
		}, ],
		contextPath : "${pageContext.request.contextPath}",
		header : [ {
			"title" : "id",
			"cls" : "id",
			"editor" : "hidden",
			"visible" : false
		}, {
			"title" : "generalLedgerId",
			"cls" : "generalLedgerId",
			"editor" : "hidden",
			"visible" : false
		}, {
			"title" : "ebObjectId",
			"cls" : "ebObjectId",
			"editor" : "hidden",
			"visible" : false
		}, {
			"title" : "accountCombinationId",
			"cls" : "accountCombinationId",
			"editor" : "hidden",
			"visible" : false
		}, {
			"title" : "divisionId",
			"cls" : "divisionId",
			"editor" : "hidden",
			"visible" : false
		}, {
			"title" : "accountId",
			"cls" : "accountId",
			"editor" : "hidden",
			"visible" : false
		}, {
			"title" : "debit",
			"cls" : "debit",
			"editor" : "hidden",
			"visible" : false
		}, {
			"title" : "Division",
			"cls" : "divisionName tblInputText",
			"editor" : "text",
			"visible" : true,
			"width" : "15%"
		}, {
			"title" : "Account",
			"cls" : "accountName tblInputText",
			"editor" : "text",
			"visible" : true,
			"width" : "15%"
		}, {
			"title" : "Combination",
			"cls" : "combination tblLabelText",
			"editor" : "label",
			"visible" : true,
			"width" : "15%"
		}, {
			"title" : "Debit",
			"cls" : "debitAmount tblInputNumeric",
			"editor" : "text",
			"visible" : true,
			"width" : "10%"
		}, {
			"title" : "Credit",
			"cls" : "creditAmount tblInputNumeric",
			"editor" : "text",
			"visible" : true,
			"width" : "10%"
		}, {
		"title" : "Description",
			"cls" : "description tblInputText",
			"editor" : "text",
			"visible" : true,
			"width" : "20%" 
		}],
		"footer" : [
			{"cls" : "debitAmount"},
			{"cls" : "creditAmount"}
		],
		"itemTableMessage" : "glLinesErrors"
	});

	$("#glLinesDivTable").on("keypress", ".divisionName", function() {
		showDivisions($(this));
	});

	$("#glLinesDivTable").on("blur", ".divisionName", function() {
		getDivision($(this));
		var divName = $.trim($($(this)).val());
		var divId = $($(this)).closest("tr").find(".divisionId").val();
		if (divName != "" && divId == "") {
			$($(this)).focus();
		}
	});

	$("#glLinesDivTable").on("keypress", ".accountName", function() {
		showAccounts($(this));
	});

	$("#glLinesDivTable").on("blur", ".accountName", function() {
		generateAcctNumber($(this));
	});

	$("#glLinesDivTable").on("blur", ".debitAmount", function () {
		$(this).val(accounting.formatMoney($(this).val()));
		updateTotalValue();
	});

	$("#glLinesDivTable").on("blur", ".creditAmount", function () {
		$(this).val(accounting.formatMoney($(this).val()));
		updateTotalValue();
	});
}

// Division Autocomplete
function showDivisions(textBox) {
	var division = $.trim($(textBox).closest("tr").find(".divisionName").val());
	var companyId = $("#companyId").val();
	var parentDivId = $("#slctDivisionId").val();
	var uri = contextPath + "/getDivisions/getChildrenByName?parentDivisionId="+parentDivId
			+"&name="+processSearchName(division)+"&isExact=false";
	$(textBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(textBox).closest("tr").find(".divisionId").val(ui.item.id);
			$(textBox).val(ui.item.number);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number+"-"+item.name+ "</a>" )
			.appendTo( ul );
	};
}

function getDivision(textBox) {
	var division = $.trim($(textBox).closest("tr").find(".divisionName").val());
	if (division != "") {
		var companyId = $("#companyId").val();
		var parentDivId = $("#slctDivisionId").val();
		var uri = 
		$.ajax({
			url: contextPath + "/getDivisions/getChildrenByName?parentDivisionId="+parentDivId
					+"&name="+processSearchName(division)+"&isExact=true",
			success : function(div) {
				$("#spanCustomerError").text("");
				if (div != null && div[0] != undefined) {
					$(textBox).closest("tr").find(".divisionId").val(div[0].id);
					$(textBox).val(div[0].number);
					setDescriptionInLines(textBox);
				} else {
					$(textBox).closest("tr").find(".divisionId").val("");
					$(textBox).closest("tr").find(".accountId").val("");
					$(textBox).closest("tr").find(".accountName").val("");
					$(textBox).closest("tr").find(".combination").text("");
				}
			},
			dataType: "json"
		});
	} else {
		$(textBox).closest("tr").find(".divisionId").val("");
		$(textBox).closest("tr").find(".accountId").val("");
		$(textBox).closest("tr").find(".accountName").val("");
		$(textBox).closest("tr").find(".combination").text("");
		$(textBox).focus();
	}
}

// Account Autocomplete
function showAccounts(textBox) {
	var accountName = $.trim($(textBox).closest("tr").find(".accountName").val());
	var companyId = $("#companyId").val();
	var divisionId = $(textBox).closest("tr").find(".divisionId").val();
	var uri = contextPath + "/getAccounts/byName?accountName="+processSearchName(accountName)
			+"&companyId="+companyId+"&divisionId="+divisionId+"&limit=10";
	$(textBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(textBox).closest("tr").find(".accountId").val(ui.item.id);
			$(textBox).val(ui.item.number);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(textBox).closest("tr").find(".accountId").val(ui.item.id);
						$(this).val(ui.item.number);
					} else {
						removeAcctValues(textBox);
					}
				},
				dataType: "json",
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number+"-"+item.accountName+ "</a>" )
			.appendTo( ul );
	};
}

function generateAcctNumber(textBox) {
	var companyId = $("#companyId").val();
	var divisionId = $(textBox).closest("tr").find(".divisionId").val();
	var accountId = $(textBox).closest("tr").find(".accountId").val();
	var isValidEntries = true;
	var params = "";
	if (companyId != "" && (divisionId != "" && divisionId != 0) && (accountId != "" && accountId != 0)) {
		params = "/byCombination?companyId="+companyId+"&divisionId="+divisionId+"&accountId="+accountId;
	} else if (companyId == "" && divisionId == "") {
		alert("Select a company and a division.");
		isValidEntries = false;
	} else if (accountId == "" || accountId == 0) {
		isValidEntries = false;
	}

	if (isValidEntries) {
		loadAjaxData(params, "json", function (responseText) {
			var ac = responseText[0];
			$(textBox).closest("tr").find(".accountCombinationId").val(ac["id"]);
			var comp = ac["company"];
			var div = ac["division"];
			var acct = ac["account"];
			var acctCombi = comp["name"]+" - "+div["name"]+" - "+acct["accountName"];
			$(textBox).closest("tr").find(".combination").text(acctCombi);
			setDescriptionInLines(textBox);
		}, function(error) {
			if ($(textBox).val() != "") {
				$(textBox).closest("tr").find(".accountId").val("");
				console.log(error);
			}
		});
	} else {
		removeAcctValues(textBox);
	}
}

function loadAjaxData (params, type, callbackFunction, errorFunction) {
	$.ajax({
		url: contextPath+"/getAccountCombination"+params,
		success : callbackFunction,
		error : errorFunction,
		dataType: type,
	});
}

function removeAcctValues(textBox) {
	$(textBox).closest("tr").find(".accountCombinationId").val("");
	$(textBox).closest("tr").find(".accountId").val("");
	$(textBox).closest("tr").find(".combination").text("");
}

function removeDivValues(textBox) {
	$(textBox).closest("tr").find(".accountCombinationId").val("");
	$(textBox).closest("tr").find(".divisionId").val("");
	$(textBox).closest("tr").find(".combination").text("");
	removeAcctValues(textBox);
}

function setDescriptionInLines(textBox) {
	var headerDescription = $("#txtComment").val();
	var description = $.trim($(textBox).closest("tr").find(".description").val());
	if (description == "") {
		$(textBox).closest("tr").find(".description").val(headerDescription);
	}
}

var isSaving = false;
function saveGeneralLedger () {
	if (!isSaving && $("#spanReferenceDoc").html() == "" && !checkExceededFileSize()) {
		parseDebitandCredit();
		trimDescription();
		$("#companyId").removeAttr("disabled");
		$("#slctDivisionId").removeAttr("disabled");
		$("#btnSaveGlEntry").attr("disabled", "disabled");
		$("#hdnGlEntriesJson").val($glLinesTable.getData);
		$("#referenceDocumentsJson").val($documentsTable.getData());
		if ($("#glStatusId").val() == 0) {
			$("#glStatusId").val(1);
		}
		var jvNumber = $("#jvNumber").val();
		$("#currencyId").removeAttr("disabled");
		doPostWithCallBack ("generalLedgerDto", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if("${pId}" == 0)
					dojo.byId("form").innerHTML = "";
				$("#glLinesDivTable tbody tr").remove();
				glEntries = '';
				$("#btnSaveGlEntry").removeAttr("disabled");
			} else {
				parseDebitandCredit();
				if("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#companyId").attr("disabled","disabled");
					$("#currencyId").attr("disabled","disabled");
				}
				loadDivision("${generalLedgerDto.divisionId}");
				$("#slctDivisionId").attr("disabled","disabled");
				initializeTable();
				initDocumentsTbl();
				$("#txtSequenceNo").val("${sequenceNo}");
				$("#jvNumber").val(jvNumber);
				updateTotalValue();
			}
			$("#spanEntrySource").text("General Ledger");
			isSaving = false;
		});
	} else if (checkExceededFileSize()) {
		$("#spanDocumentSize").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function formatMoney (textbox) {
	var money = accounting.formatMoney($(textbox).val());
	$(textbox).val(money.replace(/[$]/gi, ''));
}

// Updates the amount in the footer of the gl entries table
function updateTotalValue() {
	$("#glLinesDivTable tfoot").remove();
	$("#glLinesDivTable table").append("<tfoot><tr><td colspan='5'></td><td><span id='debitAmount'></span></td><td><span id='creditAmount'></span></td></tr></tfoot>");

	var totalDebit = updateTotal(".debitAmount");
	$("#glLinesDivTable table tfoot").closest("tr").find(".debitAmount").html(formatDecimalPlaces(totalDebit));
	$("#debitAmount").text(formatDecimalPlaces(totalDebit));

	var totalCredit = updateTotal(".creditAmount");
	$("#glLinesDivTable table tfoot").closest("tr").find(".creditAmount").html(formatDecimalPlaces(totalCredit));
	$("#creditAmount").text(formatDecimalPlaces(totalCredit));
}

function updateTotal(elem) {
	var total = 0;
	$("#glLinesDivTable tbody tr").each(function(i) {
		var value = $.trim($(this).find(elem).val());
		value = accounting.unformat(value);
		total += value != "" ? parseFloat(value) : 0;
	});
	return total;
}

function parseDebitandCredit() {
	$(".debitAmount").each(function(i) {
		var debit = $(this).val();
		$(this).val(debit.replace(/\,/g,""));
	});

	$(".creditAmount").each(function(i) {
		var credit = $(this).val();
		$(this).val(credit.replace(/\,/g,""));
	});
}

function trimDescription() {
	// Remove extra spaces before saving
	// If empty, this will pass the value as null to the validator
	$("#glLinesDivTable tbody tr").each(function(i) {
		var description = $.trim($(this).find(".description").val());
		$(this).find(".description").val(description);
	});
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

function getCurrencyRate(elem) {
	var currencyId = $(elem).val();
	if (currencyId != "" && currencyId != null && currencyId != 0) {
		$.ajax({
			url: contextPath+"/getCurrency/getLatestRate?currencyId="+currencyId,
			success : function(currencyRate) {
				if(currencyRate.active == true){
					$("#hdnCurrRateId").val(currencyRate.id);
					var rate = currencyRate.rate;
					$("#hdnCurrRateValue").val(rate);
					convertCurrencies(currencyId, rate);
				}
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

	$("#glLinesDivTable tbody tr").each(function(row) {
		var debit = accounting.unformat($(this).find(".debitAmount").val());
		debit = (debit * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".debitAmount").val(formatDecimalPlaces((debit / rate)));

		var credit = accounting.unformat($(this).find(".creditAmount").val());
		credit = (credit * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".creditAmount").val(formatDecimalPlaces((credit / rate)));
	});

	var totalDebit = updateTotal(".debitAmount");
// 	$("#glLinesDivTable table tfoot").closest("tr").find(".debitAmount").html(formatDecimalPlaces(totalDebit));
	$("#debitAmount").text(formatDecimalPlaces(totalDebit));

	var totalCredit = updateTotal(".creditAmount");
// 	$("#glLinesDivTable table tfoot").closest("tr").find(".creditAmount").html(formatDecimalPlaces(totalCredit));
	$("#creditAmount").text(formatDecimalPlaces(totalDebit));

	PREV_RATE = rate;
}
</script>
</head>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="generalLedgerDto">
		<div class="modFormLabel">
			General Journal<span id="spanDivisionLbl"> - ${generalLedgerDto.divisionName}</span> 
			<span class="btnClose" id="btnClose">[X]</span>
		</div>
		<br>
		<div class="modForm">
			<form:hidden path="generalLedger.id" />
			<form:hidden path="generalLedger.ebObjectId" id="hdnEbObjectId" />
			<form:hidden path="generalLedger.sequenceNo" />
			<form:hidden path="generalLedger.glStatusId" id="glStatusId" />
			<form:hidden path="generalLedger.glEntrySourceId" id="glEntrySourceId" />
			<form:hidden path="generalLedger.createdBy" />
			<form:hidden path="generalLedger.formWorkflowId" />
			<form:hidden path="generalLedger.divisionId" id="hdnDivisionHeader" />
			<form:hidden path="divisionName" id="hdnDivisionName" />
			<form:hidden path="glEntriesJson" id="hdnGlEntriesJson"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="generalLedger.currencyRateId" id="hdnCurrRateId"/>
			<form:hidden path="generalLedger.currencyRateValue" id="hdnCurrRateValue"/>
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table id="tblHeader" class="formTable">
					<tr>
						<td class="labels">Journal Voucher No.</td>
						<td class="value"><span id="spanSequenceNo"><c:if test="${generalLedgerDto.generalLedger.id > 0}">
										${generalLedgerDto.generalLedger.sequenceNo}
									</c:if></span>
						</td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value"><span id="spanStatus">
							<c:choose>
							<c:when test="${generalLedgerDto.generalLedger.formWorkflow != null}">
								${generalLedgerDto.generalLedger.formWorkflow.currentFormStatus.description}
							</c:when>
							<c:otherwise>
								NEW
							</c:otherwise>
							</c:choose>
						</span></td>
					</tr>
					<tr>
						<td class="labels">Entry Source</td>
						<td class="value"><span id="spanEntrySource"></span></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>GL Header</legend>
				<table id="tblHeader" class="formTable">
					<tr>
						<td class="labels">* Company</td>
						<td class="value">
							<form:select path="companyId" id="companyId" cssClass="frmSelectClass">
								<form:options items="${companies}" itemLabel="name" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="companyId"
								cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* Division</td>
						<td class="value"><form:select path="divisionId"
								id="slctDivisionId" class="frmSelectClass"></form:select></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="divisionId"
								cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* Date</td>
						<td class="value"><form:input path="generalLedger.glDate"
								id="glDate" onblur="evalDate('glDate')" style="width: 120px;"
								class="dateClass2" /> <img id="imgDate"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('glDate')" style="cursor: pointer"
							style="float: right;" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="generalLedger.glDate" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="descriptionLabel">* Description</td>
						<td class="value"><form:textarea path="generalLedger.comment"
								id="txtComment" cols="70" rows="5" class="addressClass" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="generalLedger.comment" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Currency</td>
						<td class="value">
							<form:select path="generalLedger.currencyId" id="currencyId" class="frmSelectClass"
								onchange="getCurrencyRate(this);">
								<form:options items="${currencies}" itemLabel="name" itemValue="id"/>
							</form:select>
	 					</td>
	 				</tr>
	 				<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="generalLedger.currencyId" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>GL Lines</legend>
				<div id="glLinesDivTable"></div>
				<table>
					<tr>
						<td><span id="gLlineErrors" class="error"></span></td>
					</tr>
					<tr>
						<td><form:errors path="glEntrySize" cssClass="error" /></td>
					</tr>
					<tr>
						<td><form:errors path="glEntry.companyId" cssClass="error" /></td>
					</tr>
					<tr>
						<td><form:errors path="glEntry.accountCombinationId" cssClass="error" /></td>
					</tr>
					<tr>
						<td><form:errors path="glEntry.amount" cssClass="error" /></td>
					</tr>
					<tr>
						<td><form:errors path="glEntry.description" cssClass="error" /></td>
					</tr>
					<tr>
						<td><form:errors path="gLlineMessage" cssClass="error" /></td>
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
					<td><form:errors path="generalLedger.formWorkflowId"
							cssClass="error" /></td>
				</tr>
			</table>
			<br>
			<div align="right" style="margin-right: 15px;" class="buttonClss">
				<input type="button" id="btnSaveGlEntry" value="Save"
					onclick="saveGeneralLedger();" />
			</div>
		</div>
	</form:form>
</div>
</body>
</html>