<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
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
	initializePCVLLinesTable();
	hideValues();
	initializeDocumentsTbl();
	disableTable();
	if("${pcvl.id}" != 0){
		formatAmount();
		computeGrandTotal();
		enableTable();
		$("#txtPCVNumber").val("${pcvl.pcvNumber}");
	}
});

function showPCVReferences() {
	var divisionId = "${pcvl.divisionId}";
	$("#divPCVReferenceId").load(contextPath+"/pettyCashVoucherLiquidation/"+divisionId+"/showPCVReferences");
}

function loadPCVReference(pcvReferenceId) {
	var uri = contextPath+"/pettyCashVoucherLiquidation/getPCVById?pettyCashVoucherId="+pcvReferenceId;
	if (pcvReferenceId != "") {
		$.ajax({
			url: uri,
			success : function(pcvl) {
				$("#divPCVReferenceId").html("");
				$("#aClose")[0].click();
				updateHeader(pcvl.companyId, pcvl.company.name, pcvl.divisionId, pcvl.division.name, pcvl.pcvNumber, pcvl.pettyCashVoucherId,
						pcvl.userCustodianId, pcvl.userCustodianName, pcvl.requestor, pcvl.referenceNo, pcvl.description, pcvl.amount, pcvl.pcvDateString);
				$("#spAmount").text(formatDecimalPlaces(Number(pcvl.amount)));
				enableTable();
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function updateHeader(companyId, companyName, divisionId, divisionName, sequenceNo, pcvId, userCustodianId, userCustodianName,
		requestor, referenceNo, description, amount, pcvDateString) {
	$("#companyId").val(companyId);
	$("#spCompanyName").text(companyName);
	$("#divisionId").val(divisionId);
	$("#spDivisionName").text(divisionName);
	$("#hdnPettyCashVoucherId").val(pcvId);
	$("#txtPCVNumber").val(sequenceNo);
	$("#hdnPcvNumber").val(sequenceNo);
	$("#userCustodianId").val(userCustodianId);
	$("#spCustodianName").text(userCustodianName);
	$("#spRequestor").text(requestor);
	$("#hdnRequestor").val(requestor);
	$("#spReference").text(referenceNo);
	$("#hdnReferenceNo").val(referenceNo);
	$("#spDescription").text(description);
	$("#hdnDescription").val(description);
	$("#spAmount").text(amount);
	$("#hdnAmount").val(amount);
	$("#hdnPcvDateString").val(pcvDateString);
}

function setDefaultValues($txtBox) {
	var $tr = $($txtBox).closest("tr");
	$tr.find(".pcvDateString").text($("#hdnPcvDateString").val());
	$tr.find(".divisionName").text($("#spDivisionName").text());
	$tr.find(".divisionId").val($("#divisionId").val());
}

function loadTaxTypes() {
	$("#pcvlLinesTable tbody tr").find(".taxType").each(function() {
		populateTaxTypes(this);
	});
}

function populateTaxTypes($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var taxTypeId = $tr.find(".taxTypeId").val();
	var uri = contextPath + "/getTaxTypes/new?isReceivable=false";
	if (typeof taxTypeId != "undefined" && taxTypeId != null && taxTypeId != "") {
		uri += "&taxTypeId=" + taxTypeId;
	}
	$($txtBox).empty();
	var optionParser = {
		getValue : function(rowObject) {
			if (rowObject != null) {
				return rowObject["id"];
			}
		},

		getLabel : function(rowObject) {
			if (rowObject != null) {
				return rowObject["name"];
			}
		}
	};
	var postHandler = {
		doPost : function(data) {
			// This is to remove any duplication.
			var found = [];
			$($txtBox).each(function() {
				if ($.inArray(this.value, found) != -1)
					$(this).remove();
				found.push(this.value);
			});
			if (taxTypeId != "" && taxTypeId != null && typeof taxTypeId != "undefined") {
				$($txtBox).val(taxTypeId);
			}
		}
	};
	// Prepend empty value for Discount and Add ons
	loadPopulateObject(uri, false, taxTypeId, $($txtBox), optionParser, postHandler, false, true);
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
	var hasAccountName = accountName != null && $.trim(accountName) != "" && $.trim(accountName) != "0"
	if (hasCompanyId && hasDivisionId && hasAccountName) {
		var uri = contextPath+"/getAccounts/byExactName?accountName="+accountName;
		$.ajax({
			url: uri,
			success : function(account) {
				$("#accountErrorMsg").text("");
				if (account != null && account != undefined) {
					$tr.find(".accountId").val(account.id);
					$tr.find(".accountName").val(account.accountName);
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

function showSuppliers($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var supplierName = $($txtBox).val();
	var uri = contextPath + "/getSuppliers/new?name="+processSearchName($.trim(supplierName))+"&companyId="+companyId+"&divisionId="+divisionId;
	$($txtBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$tr.find(".supplierId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>" +item.name+ "</a>" )
				.appendTo( ul );
		};
}

function getSupplier($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var supplierName = $($txtBox).val();
	if (supplierName != "") {
		var uri = contextPath + "/getSuppliers/new?name="+processSearchName($.trim(supplierName))+"&isExact=true";
		var divisionId = $("#divisionId").val();
		if (divisionId != "" && divisionId != "undefined") {
			uri += "&divisionId="+divisionId;
		}
		$.ajax({
			url: uri,
			success : function(supplier) {
				$("#supplierErrorMsg").text("");
				if (supplier != null && supplier[0] != undefined) {
					$tr.find(".supplierId").val(supplier[0].id);
					$tr.find(".supplierName").text(supplier[0].name);
					$tr.find(".supplierTin").text(processTin(supplier[0].tin));
					$tr.find(".brgyStreet").text(supplier[0].streetBrgy);
					$tr.find(".city").text(supplier[0].cityProvince);
				} else {
					$($txtBox).val("")
					$tr.find(".supplierId").val("");
					$tr.find(".supplierName").text("");
					$tr.find(".supplierTin").text("");
					$tr.find(".brgyStreet").text("");
					$tr.find(".city").text("");
					$("#supplierErrorMsg").text("Invalid supplier.");
				}
			},
			error : function(error) {
				$($txtBox).val("")
				$tr.find(".supplierId").val("");
				$tr.find(".supplierName").text("");
				$tr.find(".supplierTin").text("");
				$tr.find(".brgyStreet").text("");
				$tr.find(".city").text("");
				$("#supplierErrorMsg").text("Invalid supplier.");
			},
			dataType: "json"
		});
	}
}

function processTin(tinNumber){
	var interval = 3;
	var tin = "";
	var length = tinNumber.length - 1;
	var endIndex = interval;
	if(length - 1 >= endIndex) {
		tin = tinNumber.substring(0, endIndex);
		while(endIndex <= length) {
			if(endIndex + interval > length) {
				tin += "-" + tinNumber.substring(endIndex, length + 1);
			} else {
				tin += "-" + tinNumber.substring(endIndex, endIndex + interval);
			}
			endIndex += interval;
		}
	}
	return tin;
}

function computeGrandTotal() {
	var totalVAT = 0;
	var totalNetOfVAT = 0;
	var totalUpAmount = 0;
	$("#pcvlLinesTable tbody tr").each(function(row) {
		totalUpAmount += accounting.unformat($(this).find(".upAmount").val());
		totalVAT += accounting.unformat($(this).find(".vatAmount").html());
		totalNetOfVAT += accounting.unformat($(this).find(".amount").html());
	});

	var totalLiquidation = totalNetOfVAT + totalVAT;
	$("#subTotal").html(formatDecimalPlaces(totalNetOfVAT));
	$("#totalVAT").html(formatDecimalPlaces(totalVAT));
	$("#totalLiquidation").html(formatDecimalPlaces(totalLiquidation));
	var totalCashReturned = $("#hdnAmount").val();
	var grandTotal = formatDecimalPlaces(totalCashReturned - totalLiquidation);
	$("#totalCashReturned").html(grandTotal);
	$("#hdnGrandTotal").val(accounting.unformat(grandTotal));
	//footer
	$("#pcvlLinesTable").find("tfoot tr").find(".srp").html(accounting.formatMoney(totalUpAmount));//up amount
	$("#pcvlLinesTable").find("tfoot tr").find(".vatAmount").html(accounting.formatMoney(totalVAT));
	$("#pcvlLinesTable").find("tfoot tr").find(".amount").html(accounting.formatMoney(totalNetOfVAT));
}

function initializePCVLLinesTable() {
	var pcvlLines = JSON.parse($("#pcvlLinesJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$pcvlLinesTable = $("#pcvlLinesTable").editableItem({
		data: pcvlLines,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "pcvDateString", "varType" : "string"},
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
			{"name" : "upAmount", "varType" : "double"},
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
			{"title" : "Issue Date",
				"cls" : "pcvDateString tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "6%",
				"handler" : new PettyCashHandler (new function () {
				})},
			{"title" : "BMS No.",
				"cls" : "bmsNumber tblInputText",
				"editor" : "text",
				"visible" : true,
				"width": "7%"},
			{"title" : "DR/SI",
				"cls" : "orNumber tblInputText",
				"editor" : "text",
				"visible" : true,
				"width": "7%"},
			{"title" : "supplierId",
				"cls" : "supplierId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Supplier",
				"cls" : "supplierName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width": "7%"},
			{"title" : "TIN",
				"cls" : "supplierTin tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "7%"},
			{"title" : "Barangay, Street",
				"cls" : "brgyStreet tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "7%"},
			{"title" : "City, Province, and Zip Code",
				"cls" : "city tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width": "7%"},
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
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
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "7%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
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
			{"cls" : "srp"},
			{"cls" : "taxType"},
			{"cls" : "vatAmount"},
			{"cls" : "amount"}
		],
	});
	$("#pcvlLinesTable").on("focus", ".tblInputText", function() {
		setDefaultValues(this);
		loadTaxTypes();
	});

	$("#pcvlLinesTable").on("focus", ".tblInputNumeric", function() {
		setDefaultValues(this);
		loadTaxTypes();
	});

	$("#pcvlLinesTable").on("change", ".taxType", function() {
		$(this).closest("tr").find(".taxTypeId").val($(this).val());
		computeVatAmount($(this).closest("tr").find(".upAmount"));
		computeGrandTotal();
	});

	$("#pcvlLinesTable").on("blur", ".upAmount", function() {
		$(this).val(formatDecimalPlaces($(this).val()));
		computeVatAmount($(this).closest("tr").find(".upAmount"));
		computeGrandTotal();
	});

	$("#pcvlLinesTable").on("focus", ".upAmount", function() {
		var amount = $(this).val();
		if (amount != "") {
			$(this).val(accounting.unformat(amount));
		}
	});

	$("#pcvlLinesTable").on("keydown", ".accountName", function() {
		showAccounts(this);
	});

	$("#pcvlLinesTable").on("blur", ".accountName", function() {
		getAccount(this);
	});

	$("#pcvlLinesTable").on("keydown", ".supplierName", function() {
		showSuppliers(this);
	});

	$("#pcvlLinesTable").on("blur", ".supplierName", function() {
		getSupplier(this);
	});

	$("#pcvlLinesTable").on("click", ".taxType", function() {
		loadTaxTypes();
	});
	loadTaxTypes();
	resizeTbl("#pcvlLinesTable", 12);
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

function disableTable(){
	$('#pcvlLinesTable').find('input, select').attr('disabled','disabled');
}

function enableTable(){
	$('#pcvlLinesTable').find('input, select').removeAttr("disabled");
}

function hideValues() {
	$("#pcvlLinesTable").find("tfoot tr .taxType").hide();
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
	var cashReturned = $("#cashReturned").val();
	$("#cashReturned").val(accounting.unformat(cashReturned));
}

function formatAmount() {
	unformatAmount();
	var cashReturned = $("#cashReturned").val();
	$("#cashReturned").val(formatDecimalPlaces(Number(cashReturned)));
	var amount = $("#hdnAmount").val();
	$("#spAmount").text(formatDecimalPlaces(Number(amount)));
}

var isSaving = false;
function savePettyCashVoucherLiquidation() {
	$("#btnSave").attr("disabled", "disabled");
	if(!isSaving && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		$("#pcvlLinesJson").val($pcvlLinesTable.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		unformatAmount();
		doPostWithCallBack ("pettyCashVoucherLiquidationForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var companyId = $("#companyId").val();
				var companyName = $("#spCompanyName").text();
				var divisionId = $("#divisionId").val();
				var divisionName = $("#spDivisionName").text();
				var sequenceNo = $("#txtPCVNumber").val();
				var pcvId = $("#hdnPettyCashVoucherId").val();
				var userCustodianId = $("#userCustodianId").val();
				var userCustodianName = $("#spCustodianName").text();
				var requestor = $("#spRequestor").text();
				var referenceNo = $("#spReference").text();
				var description = $("#spDescription").text();
				var amount = $("#hdnAmount").val();
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var status = $("#currentStatus").val();
				var pcvDateString = $("#hdnPcvDateString").val();
				if("${pcvl.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#currentStatus").val(status);
				}
				updateHeader(companyId, companyName, divisionId, divisionName, sequenceNo, pcvId, userCustodianId, userCustodianName,
						requestor, referenceNo, description, amount, pcvDateString);
				formatAmount();
				$("#spanDivisionLbl").text(spanDivisionLbl);
				initializePCVLLinesTable();
				initializeDocumentsTbl();
				disableTable();
				if(pcvId != 0){
					enableTable();
				}
				computeGrandTotal();
				hideValues();
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
	<div id="container" class="popupModal">
		<div id="divPCVReferenceContainer" class="reveal-modal">
			<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
			<div id="divPCVReferenceId"></div>
		</div>
	</div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="pcvl" id="pettyCashVoucherLiquidationForm">
			<div class="modFormLabel">Petty Cash Voucher Liquidation<span id="spanDivisionLbl"> - ${pcvl.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="updatedBy"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="pcvlLinesJson" id="pcvlLinesJson" />
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson" />
			<form:hidden path="pcvDateString" id="hdnPcvDateString" />
			<form:hidden path="grandTotal" id="hdnGrandTotal" />
			<form:hidden path="pcvNumber" id="hdnPcvNumber" />
			<form:hidden path="ebObjectId"/>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">PCVL No.</td>
							<td class="value">
								<form:input path="sequenceNo" readonly="true" cssClass="textBoxLabel"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${pcvl.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text" id="currentStatus" class="textBoxLabel" readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Petty Cash Voucher Liquidation Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:hidden path="companyId" id="companyId"/>
								<span id="spCompanyName">${pcvl.company.name}</span>
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
								<form:hidden path="divisionId" id="divisionId"/>
								<span id="spDivisionName">${pcvl.division.name}</span>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="divisionId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* PCV Reference</td>
							<td class="value">
								<form:hidden path="pettyCashVoucherId" id="hdnPettyCashVoucherId"/>
								<input id="txtPCVNumber" readonly="readonly" />
								<a href="#container" id="aOpen" data-reveal-id="divPCVReferenceId" class="link_button"
									onclick="showPCVReferences();">Browse PCV</a>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="pettyCashVoucherId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Custodian</td>
							<td class="value">
								<form:hidden path="userCustodianId" id="userCustodianId"/>
								<span id="spCustodianName">${pcvl.userCustodian.custodianAccount.custodianName}</span>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="userCustodianId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value"><form:input path="pcvlDate" id="date"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif" 
								onclick="javascript:NewCssCal('date')" style="cursor: pointer"
								style="float: right;" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="pcvlDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Requestor</td>
							<td class="value">
								<form:hidden path="requestor" id="hdnRequestor"/>
								<span id="spRequestor">${pcvl.requestor}</span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="requestor" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Reference</td>
							<td class="value">
								<form:hidden path="referenceNo" id="hdnReferenceNo"/>
								<span id="spReference">${pcvl.referenceNo}</span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="referenceNo" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Description</td>
							<td class="value">
								<form:hidden path="description" id="hdnDescription"/>
								<span id="spDescription">${pcvl.description}</span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="description" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Amount Requested</td>
							<td class="value">
								<form:hidden path="amount" id="hdnAmount"/>
								<span id="spAmount">${pcvl.amount}</span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="amount" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Total Cash Returned</td>
							<td class="value">
								<form:input path="cashReturned" id="cashReturned" class="numeric" onblur="formatAmount();" cssStyle="width: 172px;" maxLength="13"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="cashReturned" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
				<legend>Petty Cash Voucher Liquidation Particulars</legend>
				<div id="pcvlLinesTable"></div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="pcvlErrorMessage" cssClass="error"/></td>
					</tr>
					<tr>
						<td><span id="supplierErrorMsg" class="error"></span></td>
					</tr>
					<tr>
						<td><span id="accountErrorMsg" class="error"></span></td>
					</tr>
				</table>
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
						<td align="right">Sub Total</td>
						<td align="right"><span id="subTotal">0.0</span></td>
					</tr>
					<tr>
						<td align="right">Total VAT</td>
						<td align="right"><span id="totalVAT">0.0</span></td>
					</tr>
					<tr>
						<td align="right">Total Liquidation</td>
						<td align="right"><span id="totalLiquidation">0.0</span></td>
					</tr>
					<tr>
						<td align="right">Total Cash Returned</td>
						<td align="right"><span id="totalCashReturned">0.0</span></td>
					</tr>
				</table>
				<table class="frmField_set">
				<tr>
					<td align="right"><form:errors path="grandTotal" cssClass="error"/></td>
				</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td colspan="3" align="right">
								<input type="button" value="Save" id="btnSave" onclick="savePettyCashVoucherLiquidation();"/></td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>