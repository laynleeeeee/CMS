<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable2.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.pettycashhandler.js"></script>
<script type="text/javascript">
var pcvDate = null;
var divisionName = "";
$(document).ready(function(){
	loadDivision("${apInvoice.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	initializePCRLinesTable();
	initializeApLines();
	hideValues();
	initializeDocumentsTbl();
	updatePCFAmount();
	if ("${apInvoice.id}" != 0) {
		formatAmount();
		computeGrandTotal();
	}
});

function loadDivision(divisionId) {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
		+"&divisionId="+divisionId;
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

function updatePCFAmount() {
	var companyId = $("#companyId").val();
	var userCustodianId = $("#userCustodianId").val();
	var pcrDate = $("#date").val();
	var uri = contextPath+"/pettyCashReplenishment/getPCFAmount?companyId="+companyId
			+"&userCustodianId="+userCustodianId+"&pcrDate="+pcrDate;
	if (companyId != "" && (userCustodianId != "" && userCustodianId != null) && pcrDate != "") {
		$.ajax({
			url: uri,
			success : function(pcfAmount) {
				if(pcfAmount != null){
					$("#pcfAmount").html(formatDecimalPlaces(pcfAmount));
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function updatePCVLTable() {
	var divisionId = $("#divisionId").val();
	var userCustodianId = $("#userCustodianId").val();
	var uri = contextPath+"/pettyCashReplenishment/populateTable?divisionId="+divisionId;
	var apInvoiceId = "${apInvoice.id}";
	if (userCustodianId != "" && userCustodianId != null) {
		uri += "&userCustodianId="+userCustodianId;
	}
	if (apInvoiceId != "" && apInvoiceId != 0) {
		uri += "&apInvoiceId="+apInvoiceId;
	}
	if (userCustodianId != "") {
		$.ajax({
			url: uri,
			success : function(pcrls) {
				if(pcrls != null){
					setPCRLinesTable(pcrls);
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function computeVatAmount($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var taxTypeId = $tr.find(".taxType").val();
	var grossAmount = accounting.unformat($($txtBox).val());
	var isVatable = taxTypeId != "" && taxTypeId != 2 && taxTypeId != 3 && taxTypeId != 7;
	var netOfVat = isVatable ? (grossAmount / 1.12).toFixed(6) : grossAmount;
	var vatAmount = (grossAmount - netOfVat).toFixed(6);
	$tr.find(".vatAmount").text(formatDecimalPlaces(vatAmount));
	var amount = grossAmount - vatAmount;
	$tr.find(".amount").text(formatDecimalPlaces(amount));
}

function showAccounts($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var companyId = $("#companyId").val();
	var divisionId = $tr.find(".divisionId").val();
	var accountName = $($txtBox).val();
	var uri = contextPath+"/getAccounts/byName?accountName="+processSearchName($.trim(accountName))+"&companyId="+companyId
			+"&divisionId="+divisionId+"&limit=10";
	$($txtBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$tr.find(".accountId").val(ui.item.id);
			$(this).val(ui.item.accountName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number+"-"+item.accountName+ "</a>" )
			.appendTo( ul );
	};
}

function getAccount($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var companyId = $("#companyId").val();
	var divisionId = $tr.find(".divisionId").val();
	var hasCompanyId = companyId != "" && companyId != null && companyId != "0";
	var hasDivisionId = divisionId != "" && divisionId != null && divisionId != "0";
	var accountName = $($txtBox).val();
	var hasAccountName = accountName != null && $.trim(accountName) != "" && $.trim(accountName) != "0";
	if (hasCompanyId && hasDivisionId && hasAccountName) {
		var uri = contextPath+"/getAccounts/byExactName?accountName="+processSearchName(accountName);
		$.ajax({
			url: uri,
			success : function(account) {
				$("#accountErrorMsg").text("");
				if (account != null && account != undefined) {
					$tr.find(".accountId").val(account.id);
					$tr.find(".accountName").val(account.accountName);
				} else {
					$($txtBox).val("")
					$tr.find(".accountId").val("");
					$tr.find(".accountName").val("");
				}
			},
			error : function(error) {
				$($txtBox).val("")
				$tr.find(".accountId").val("");
				$tr.find(".accountName").val("");
				$("#accountErrorMsg").text("Invalid account.");
			},
			dataType: "json"
		});
	} else {
		$($txtBox).val("");
		$tr.find(".accountId").val("");
	}
}

function computeGrandTotal() {
	var totalVAT = 0;
	var totalNetOfVAT = 0;
	var totalUpAmount = 0;
	$("#pcrLinesTable tbody tr").each(function(row) {
		totalUpAmount += accounting.unformat($(this).find(".grossAmount").html());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
		totalNetOfVAT += accounting.unformat($(this).find(".amount").html());
	});

	var totalAmount = totalNetOfVAT + totalVAT;
	$("#subTotal").html(formatDecimalPlaces(totalNetOfVAT));
	$("#totalVAT").html(formatDecimalPlaces(totalVAT));
	$("#totalAmount").html(formatDecimalPlaces(totalAmount));
	//footer
	$("#pcrLinesTable").find("tfoot tr").find(".grossAmount").html(accounting.formatMoney(totalUpAmount));//up amount
	$("#pcrLinesTable").find("tfoot tr").find(".vatAmount").html(accounting.formatMoney(totalVAT));
	$("#pcrLinesTable").find("tfoot tr").find(".amount").html(accounting.formatMoney(totalNetOfVAT));
	//summary table footer
	$("#summarizedApLinesTable").find("tfoot tr").find(".grossAmount").html(accounting.formatMoney(totalUpAmount));//up amount
	$("#summarizedApLinesTable").find("tfoot tr").find(".vatAmount").html(accounting.formatMoney(totalVAT));
	$("#summarizedApLinesTable").find("tfoot tr").find(".amount").html(accounting.formatMoney(totalNetOfVAT));
	//
	$("#hdnAmount").val(accounting.unformat(totalAmount));
}

function initializePCRLinesTable() {
	var pcrlsJson = JSON.parse($("#pcrlsJson").val());
	setPCRLinesTable(pcrlsJson);
}

function setPCRLinesTable(pcrlsJson) {
	var cPath = "${pageContext.request.contextPath}";
	$("#pcrLinesTable").html("");
	$pcrLinesTable = $("#pcrLinesTable").editableItem({
		data: pcrlsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "pcvlId", "varType" : "integer"},
			{"name" : "pcvllId", "varType" : "integer"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "pcvlDateString", "varType" : "string"},
			{"name" : "sequenceNo", "varType" : "integer"},
			{"name" : "bmsNumber", "varType" : "string"},
			{"name" : "orNumber", "varType" : "string"},
			{"name" : "supplierId", "varType" : "int"},
			{"name" : "supplierName", "varType" : "string"},
			{"name" : "supplierTin", "varType" : "string"},
			{"name" : "brgyStreet", "varType" : "string"},
			{"name" : "city", "varType" : "string"},
			{"name" : "description", "varType" : "string"},
			{"name" : "divisionId", "varType" : "int"},
			{"name" : "divisionName", "varType" : "string"},
			{"name" : "accountId", "varType" : "int"},
			{"name" : "accountName", "varType" : "string"},
			{"name" : "grossAmount", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "taxName", "varType" : "string"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"}
		],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "pcvlId",
				"cls" : "pcvlId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "pcvllId",
				"cls" : "pcvllId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Issue Date",
				"cls" : "pcvlDateString tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%",
				"handler" : new PettyCashHandler (new function () {
			})},
			{"title" : "PCVL No.",
				"cls" : "sequenceNo tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%"},
			{"title" : "BMS No.",
				"cls" : "bmsNumber tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%"},
			{"title" : "DR/SI",
				"cls" : "orNumber tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%"},
			{"title" : "supplierId",
				"cls" : "supplierId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Supplier",
				"cls" : "supplierName tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%"},
			{"title" : "TIN",
				"cls" : "supplierTin tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%"},
			{"title" : "Barangay, Street",
				"cls" : "brgyStreet tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%"},
			{"title" : "City, Province, and Zip Code",
				"cls" : "city tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%"},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "7%"},
			{"title" : "Division",
				"cls" : "divisionName tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%"},
			{"title" : "divisionId",
				"cls" : "divisionId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "accountId",
				"cls" : "accountId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Account",
				"cls" : "accountName tblInputText",
				"editor" : "text",
				"visible" : true ,
				"width" : "6%"},
			{"title" : "Gross Amount",
				"cls" : "grossAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Tax Type", 
				"cls" : "taxName tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"}
		],
		"footer" : [
			{"cls" : "grossAmount"},
			{"cls" : "taxName"},
			{"cls" : "vatAmount"},
			{"cls" : "amount"}
		],
	});

	$("#pcrLinesTable").on("keydown", ".accountName", function() {
		showAccounts(this);
	});

	$("#pcrLinesTable").on("blur", ".accountName", function() {
		getAccount(this);
		updateSummaryTable();
	});

	$("#pcrLinesTable tbody tr").on("click",".delrow", function() {
		deleteMethod(this);
	});

	computeGrandTotal();
	hideValues();
	resizeTbl("#pcrLinesTable", 13);
	updateSummaryTable();
}

function deleteMethod(elem) {
	var sequenceNo = $.trim($(elem).closest("tr").find(".sequenceNo").text());
	var linesCount = 0;
	var deletedLinesCount = 0;
	$("#pcrLinesTable").find(" tbody tr ").each(function(row) {
		var sequenceNoToDelete = $.trim($(this).find(".sequenceNo").text());
		var idd = $(this).find(".id").val();
		var $row = $(this).closest("tr");
		linesCount++;
		if(sequenceNoToDelete != "" && sequenceNo != ""){
			if (sequenceNoToDelete == sequenceNo) {
				$row.remove();
				computeGrandTotal();
				deletedLinesCount++;
			}
		}
	});
	if(deletedLinesCount > 1){
		alert("The deleted line has other related PCVL line that needs to be deleted as well.");
		alert("The deleted line will remain as unreplenished and will still appear on the next Petty Cash Replenishment transaction.");
	} else if (deletedLinesCount == 1 || linesCount == deletedLinesCount){
		alert("The deleted line will remain as unreplenished and will still appear on the next Petty Cash Replenishment transaction.");
	}
	//reinitialize table if row is all deleted
	if(linesCount == deletedLinesCount){
		initializePCRLinesTable();
	}
	updateSummaryTable();
}

function updateSummaryTable() {
	var companyId = $("#companyId").val();
	var divisionId = "${apInvoice.divisionId}";
	var accounts = "";
	var index = 0;
	$("#pcrLinesTable").find(" tbody tr ").each(function(row) {
		if (index == 0) {
			accounts += $(this).find(".pcvllId").val() + "-" + $(this).find(".accountId").val();
		} else {	
			accounts += (";" +$(this).find(".pcvllId").val() + "-" + $(this).find(".accountId").val());
		}
	index++;
	});
	if (accounts != "") {
		$.ajax({
			url: contextPath + "/pettyCashReplenishment/populateSummaryTable?accounts="+accounts+"&companyId="+companyId+"&divisionId="+divisionId,
			success : function(apLinesDto) {
				if(apLinesDto != null){
					setApLines(apLinesDto.apLines);
					setSummarizedApLines(apLinesDto.summarizedApLines);
					computeGrandTotal();
				}
			},
			error : function(error) {
				initializeApLines();
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function initializeApLines(){
	var apLinesJson = JSON.parse($("#apLinesJson").val());
	var summarizedApLinesJson = JSON.parse($("#summarizedApLinesJson").val());
	setApLines(apLinesJson);
	setSummarizedApLines(summarizedApLinesJson);
}

function setApLines(apLinesJson) {
	var cPath = "${pageContext.request.contextPath}";
	$("#apLinesTable").html("");
	$apLinesTable = $("#apLinesTable").editableItem({
		data: apLinesJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "accountCombinationId", "varType" : "int"},
			{"name" : "accountId", "varType" : "int"},
			{"name" : "accountNumber", "varType" : "string"},
			{"name" : "accountName", "varType" : "string"},
			{"name" : "grossAmount", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"}
		],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "accountCombinationId",
				"cls" : "accountCombinationId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "accountId",
				"cls" : "accountId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Account Number",
				"cls" : "accountNumber tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "18%"},
			{"title" : "Account",
				"cls" : "accountName tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "20%"},
			{"title" : "Gross Amount",
				"cls" : "grossAmount tblLabelNumberic",
				"editor" : "label",
				"visible" : true,
				"width": "20%"},
			{"title" : "taxTypeId",
				"cls" : "taxTypeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumberic",
				"editor" : "label",
				"visible" : true,
				"width": "20%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "20%"}
		],
	});
	resizeTbl("#apLinesTable", 4);
}

function setSummarizedApLines(summarizedApLinesJson) {
	var cPath = "${pageContext.request.contextPath}";
	$("#summarizedApLinesTable").html("");
	$summarizedApLinesTable = $("#summarizedApLinesTable").editableItem({
		data: summarizedApLinesJson,
		jsonProperties: [
			{"name" : "accountNumber", "varType" : "string"},
			{"name" : "accountName", "varType" : "string"},
			{"name" : "grossAmount", "varType" : "double"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"}
		],
		contextPath: cPath,
		header: [
			{"title" : "Account Number",
				"cls" : "accountNumber tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "18%"},
			{"title" : "Account",
				"cls" : "accountName tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "20%"},
			{"title" : "Gross Amount",
				"cls" : "grossAmount tblLabelNumberic",
				"editor" : "label",
				"visible" : true,
				"width": "20%"},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumberic",
				"editor" : "label",
				"visible" : true,
				"width": "20%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "20%"}
		],
		"footer" : [
			{"cls" : "grossAmount"},
			{"cls" : "vatAmount"},
			{"cls" : "amount"}
		],
	});
	$("#summarizedApLinesTable tbody tr").each(function() {
		$(this).find(".delRow").hide();
	});
	resizeTbl("#summarizedApLinesTable", 4);
}

function resizeTbl(tableId, rowCount){
	var $tbl = $(tableId);
	$(tableId + " table").css("width", "100%");
	addTotalLabel($tbl)
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function addTotalLabel($tbl){
	var $tr = $($tbl).find("tfoot tr td:nth-child(1)");
	$($tr).html("Total");
}

function hideValues() {
	$("#pcrLinesTable").find("tfoot tr .taxName").hide();
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

function unformatAmount() {
	var pcfAmount = $("#pcfAmount").val();
	$("#pcfAmount").val(accounting.unformat(pcfAmount));
}

function formatAmount() {
	unformatAmount();
	var pcfAmount = $("#pcfAmount").val();
	$("#pcfAmount").val(formatDecimalPlaces(Number(pcfAmount)));
	var amount = $("#hdnAmount").val();
	$("#totalAmount").text(formatDecimalPlaces(Number(amount)));
}

var isSaving = false;
function savePettyCashReplenishment() {
	$("#btnSave").attr("disabled", "disabled");
	if(!isSaving && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		$("#pcrlsJson").val($pcrLinesTable.getData());
		$("#apLinesJson").val($apLinesTable.getData());
		$("#divisionId").removeAttr("disabled");
		$("#referenceDocumentsJson").val($documentsTable.getData());
		unformatAmount();
		doPostWithCallBack ("pettyCashReplenishmentForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var companyId = $("#companyId").val();
				var divisionId = $("#divisionId").val();
				var userCustodianId = $("#userCustodianId").val();
				var amount = $("#hdnAmount").val();
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var status = $("#currentStatus").val();
				if("${apInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#currentStatus").val(status);
				}
				formatAmount();
				$("#spanDivisionLbl").text(spanDivisionLbl);
				initializePCRLinesTable();
				initializeApLines();
				initializeDocumentsTbl();
				computeGrandTotal();
				hideValues();
				loadDivision("${apInvoice.divisionId}");
				updatePCVLTable();
				updatePCFAmount();
				$("#divisionId").attr("disabled","disabled");
				isSaving = false;
			}
			$("#btnSave").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="apInvoice" id="pettyCashReplenishmentForm">
			<div class="modFormLabel">Petty Cash Voucher Replenishment<span id="spanDivisionLbl"> - ${apInvoice.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="updatedBy"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="invoiceTypeId"/>
			<form:hidden path="pcrlsJson" id="pcrlsJson" />
			<form:hidden path="apLinesJson" id="apLinesJson" />
			<form:hidden path="summarizedApLinesJson" id="summarizedApLinesJson" />
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson" />
			<form:hidden path="ebObjectId"/>
			<form:hidden path="amount" id="hdnAmount"/>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">PCR No.</td>
							<td class="value">
								<form:input path="sequenceNumber" readonly="true" cssClass="textBoxLabel"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${apInvoice.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text" id="currentStatus" class="textBoxLabel" readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Petty Cash Replenishment Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass"
									items="${companies}" itemLabel="name" itemValue="id" >
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Division</td>
							<td class="value">
								<form:select path="divisionId" id="divisionId" class="frmSelectClass"></form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="divisionId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Custodian</td>
							<td class="value">
								<form:select path="userCustodianId" id="userCustodianId" cssClass="frmSelectClass" 
									onclick="updatePCVLTable(); updatePCFAmount();">
									<option value="">Please select a custodian</option>
									<form:options items="${userCustodians}" itemValue="id" itemLabel="custodianAccount.custodianName" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="userCustodianId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value"><form:input path="glDate" id="date"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif" 
								onclick="javascript:NewCssCal('date')" style="cursor: pointer"
								style="float: right;" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="glDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* PCF Amount</td>
							<td class="value">
								<span id="pcfAmount">0.00</span>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
				<legend>Petty Cash Voucher Liquidation Details</legend>
				<div id="pcrLinesTable"></div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="pcrlErrorMessage" cssClass="error"/></td>
					</tr>
					<tr>
						<td><span id="accountErrorMsg" class="error"></span></td>
					</tr>
				</table>
				<fieldset class="frmField_set">
					<legend>Summary</legend>
					<div id="apLinesTable" style="display: none;"></div>
					<div id="summarizedApLinesTable"></div>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Document/s</legend>
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
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right">Net Amount</td>
						<td align="right"><span id="subTotal">0.0</span></td>
					</tr>
					<tr>
						<td align="right">Total VAT</td>
						<td align="right"><span id="totalVAT">0.0</span></td>
					</tr>
					<tr>
						<td align="right">Total Liquidation</td>
						<td align="right"><span id="totalAmount">0.0</span></td>
					</tr>
				</table>
				<table class="frmField_set">
				<tr>
					<td align="right"><form:errors path="balance" cssClass="error"/></td>
				</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td colspan="3" align="right">
								<input type="button" value="Save" id="btnSave" onclick="savePettyCashReplenishment();"/></td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>