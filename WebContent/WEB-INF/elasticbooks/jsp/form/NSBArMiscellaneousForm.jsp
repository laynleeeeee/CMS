<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: AR Miscellaneous form.
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
<script src="${pageContext.request.contextPath}/js/formatUtil.js" type="text/javascript" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.armisc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
input.numeric {
	width: 150px;
}

.tdProperties {
	border-right: 1px solid #000000;
}

.textBoxLabel, .txtserviceSettingName, .txtQuantity,
.txtUnitMeasurementName, .txtUpAmount, .txtAmount, .txtDescription, .txtNumber{
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}

.divVeil {
    background-color: #F2F1F0;
}

.imgDelete{
	margin-left: 2px;
}
</style>
<script type="text/javascript">
var selectCustomerAcct = "${arMiscellaneous.arCustomerAccountId}";
var currentCustAcctId = 0;
var $otherChargesTable = null;
var PREV_RATE = 0;
$(document).ready(function () {
	if ("${arMiscellaneous.formWorkflow.currentStatusId}" == 30
			|| "${arMiscellaneous.formWorkflow.currentStatusId}" == 4) {
		$("#btnSaveArMiscellaneous").attr("disabled", "disabled");
	}
	initializeOtherChargesTbl();
	updateOChargesAmt();
	updateTotalAmount();
	loadDivision("${arMiscellaneous.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	loadWtaxAcctSettings("${arMiscellaneous.wtAcctSettingId}");
	computeWtax();
	filterCustomerAccts("${arMiscellaneous.arCustomerAccountId}");
	initializeDocumentsTbl();
	formatMoney();
	if("${arMiscellaneous.id}" != 0){
		$("#currencyId").attr("disabled","disabled");
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

function loadWtaxAcctSettings(wtAcctSettingId) {
	var companyId = $("#companyId").val();
	if (companyId != "") {
		var $select = $("#wtaxAcctSettingId");
		$select.empty();
		var divisionId = $("#divisionId").val();
		var uri = contextPath+"/getWithholdingTaxAcct?companyId="+companyId+"&isCreditable=true";
		if (divisionId != null && divisionId != "" && typeof divisionId != "undefined") {
			uri += "&divisionId="+divisionId;
		}
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
				// This is to remove any duplication.
				var found = [];
				$("#wtaxAcctSettingId option").each(function() {
					if ($.inArray(this.value, found) != -1) {
						$(this).remove();
					}
					found.push(this.value);
				});
				if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != undefined) {
					$select.val(wtAcctSettingId);
				}
			}
		};
		loadPopulateObject(uri, false, wtAcctSettingId, $select, optionParser, postHandler, false, true);
	}
}

function computeWtax() {
	var wtAcctSettingId = $("#wtaxAcctSettingId").val();
	if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != "undefined") {
		var wtPercentageValue = 0;
		var uri = contextPath + "/getWithholdingTaxAcct/getWtPercentage?wtAccountSettingId="+wtAcctSettingId;
		$.ajax({
			url: uri,
			success : function(wt) {
				wtPercentageValue = (accounting.unformat(wt) / 100);
				var totalAmount = accounting.unformat($("#subTotal").text());
				var computedWTax = (totalAmount * wtPercentageValue).toFixed(6);
				computedWTax = formatDecimalPlaces(computedWTax);
				$("#hdnWtAmount").val(accounting.unformat(computedWTax));
				$("#computedWTax").html(computedWTax);
				updateTotalAmount();
			},
			error : function(error) {
				wtPercentageValue = 0;
			},
			dataType: "text"
		});
	} else {
		$("#hdnWtAmount").val("0.00");
		$("#computedWTax").html("0.00");
		updateOChargesAmt();
		updateTotalAmount();
	}
}

function assignWtaxAcctSetting(elem) {
	wtAcctSettingId = $(elem).val();
	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
}

function assignCustAcctId() {
	currentCustAcctId = $("#arCustomerAcctId option:selected").val();
}

function getCustomer () {
	var companyId = $("#companyId").val();
	var customerName = $("#txtCustomer").val();
	if(customerName != ""){
		$("#customerName").val(customerName);
		$("#spanCustomerError").text("");
		var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&isExact=true"+"&companyId="+companyId;
		var divisionId = $("#divisionId").val();
		if (divisionId != "" && divisionId != "undefined") {
			uri += "&divisionId="+divisionId;
		}
		$.ajax({
			url: uri,
			success : function(customer) {
				$("#spanCustomerError").text("");
				if (customer != null && customer[0] != undefined) {
					$("#hdnArCustomerId").val(customer[0].id);
					$("#txtCustomer").val(customer[0].name);
					filterCustomerAccts();
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#hdnArCustomerId").val("");
					$("#arCustomerAcctId").empty();
					$("#arCustomerIdError").text("");
				}
			},
			error : function(error) {
				$("#spanCustomerError").text("Invalid customer.");
				$("#txtCustomer").val("");
				$("#arCustomerIdError").text("");
			},
			dataType: "json"
		});
	} else {
		$("#hdnArCustomerId").val("");
		$("#arCustomerAcctId").empty();
	}
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

	$("#otherChargesTable tbody tr").each(function(row) {
		var upAmount = accounting.unformat($(this).find(".upAmount").val());
		upAmount = (upAmount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".upAmount").val(formatDecimalPlaces((upAmount / rate)));

		var vat = accounting.unformat($(this).find(".vatAmount").html());
		vat = (vat * (PREV_RATE != 0 ? PREV_RATE : 1.0)) / rate;
		$(this).find(".vatAmount").html(formatDecimalPlaces(vat));

		var amount = accounting.unformat($(this).find(".amount").val());
		amount = (amount * (PREV_RATE != 0 ? PREV_RATE : 1.0)) / rate;
		$(this).find(".amount").val(formatDecimalPlaces(amount));
	});
	var totalAmount = accounting.unformat($("#totalAmount").val());
	totalAmount = (totalAmount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
	$("#totalAmount").val(formatDecimalPlaces((totalAmount / rate)));

	updateOChargesAmt();
	updateTotalAmount();
	computeWtax();
	PREV_RATE = rate;
}

function showCustomers() {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $("#txtCustomer").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)
			+"&companyId="+companyId;
	var divisionId = $("#divisionId").val();
	if (divisionId != "" && divisionId != "undefined") {
		uri += "&divisionId="+divisionId;
	}
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnArCustomerId").val(ui.item.id);
			$(this).val(ui.item.name);
			filterCustomerAccts();
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function filterCustomerAccts(arCustomerAcctId) {
	$("#arCustomerAcctId").empty();
	var customerId = $("#hdnArCustomerId").val();
	var companyId = $("#companyId").val();
	var uri = contextPath + "/arMiscellaneous/getArCustomerAccounts?arCustomerId="+customerId+
				"&receiptMethodId="+$("#receiptMethodId").val()+"&companyId="+companyId;
	if(typeof arCustomerAcctId != "undefined") {
		uri += "&arCustomerAccountId="+arCustomerAcctId;
	}
	var divisionId = $("#divisionId").val();
	if (divisionId != "" && divisionId != "undefined") {
		uri += "&divisionId="+divisionId;
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
	loadPopulate (uri, false, selectCustomerAcct, "arCustomerAcctId", optionParser, postHandler);
}

function assignCustomerAcct (select) {
	selectCustomerAcct = $(select).val();
}

function updateTotalAmount() {
	var totalOtherCharges = 0;
	var totalWtVAT = 0;
	var totalVat = 0;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		totalOtherCharges += accounting.unformat($(this).find(".amount").val());
		totalVat += accounting.unformat($(this).find(".vatAmount").html());
		if ($(this).find(".taxTypeId").val() == 9) {
			totalWtVAT += ((accounting.unformat($(this).find(".amount").val())) * 0.05);
		}
	});
	$("#subTotal").html(formatDecimalPlaces(totalOtherCharges));
	$("#totalVat").html(formatDecimalPlaces(totalVat));
	$("#hdnWtVatAmountId").val(totalWtVAT);
	$("#withholdingVAT").html(formatDecimalPlaces(totalWtVAT));
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	$("#computedWTax").html(formatDecimalPlaces(wtaxAmount));
	var grandTotal = (totalOtherCharges + totalVat) - totalWtVAT - wtaxAmount
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

var isSaving = false;
function saveArMiscellaneous () {
	if(isSaving == false) {
		isSaving = true;
		$("#arMiscellaneousLinesJson").val($otherChargesTable.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#btnSaveArMiscellaneous").attr("disabled", "disabled");
		$("#divisionId").removeAttr("disabled");
		$("#currencyId").removeAttr("disabled");
		var companyId = $("#companyId").val();
		$("#hdnCompanyId").val(companyId);
		parseDoubles();
		var isCheck = false;
		if($("#arMiscellaneousTypeId").val() == 2 || $("#arMiscellaneousTypeId").val() == 3){
			isCheck = true;
		}
		if (isCheck) {
			$("#refNumber").removeAttr("disabled");
		}
		doPostWithCallBack ("arMiscellaneousForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if("${pId}" == 0)
					dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var customer = $("#txtCustomer").val();
				var companyId = $("#companyId").val();
				var currencyId = $("#currencyId").val();
				var rate = $("#hdnCurrRateValue").val();
				var currentStatus = $("#txtarMiscellaneousStatus").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				var arCustomerAcctId = $("#arCustomerAcctId").val();
				if("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtarMiscellaneousStatus").val(currentStatus);
				}
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#companyId").val(companyId);
				$("#txtCustomer").val(customer);
				loadDivision("${arMiscellaneous.divisionId}");
				$("#divisionId").attr("disabled","disabled");
				if("${arMiscellaneous.id}" != 0){
					$("#currencyId").attr("disabled","disabled");
				}
				if (isCheck) {
					$("#refNumber").removeAttr("disabled");
				}
				filterCustomerAccts(arCustomerAcctId);
				initializeOtherChargesTbl();
				updateOChargesAmt();
				loadWtaxAcctSettings(wtAcctSettingId);
				updateTotalAmount();
				computeWtax();
				formatMoney();
				initializeDocumentsTbl();
				isSaving = false;
			}
			$("#btnSaveArMiscellaneous").removeAttr("disabled");
		});
	}
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

function enableDisableCheck(value) {
	// If equals to check enable check number field
	if (value == 2 || value == 3)
		$("#refNumber").removeAttr("disabled");
	else
		$("#refNumber").attr("disabled", "disabled");
}

function receiptMehodOnChanged() {
	$("#txtCustomer").val("");
	$("#arCustomerId").val("");
}

function initializeOtherChargesTbl() {
	var otherChargesTable = JSON.parse($("#arMiscellaneousLinesJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: otherChargesTable,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "serviceSettingId", "varType" : "int"},
			{"name" : "unitOfMeasurementId", "varType" : "int"},
			{"name" : "quantity", "varType" : "int"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "serviceSettingName", "varType" : "string"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"}
		],
		contextPath: cPath,
		header:[
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "serviceSettingId",
				"cls" : "serviceSettingId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "unitOfMeasurementId",
				"cls" : "unitOfMeasurementId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "AR Line",
				"cls" : "serviceSettingName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "25%",
				"handler" : new SalesTableHandler (new function () {
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
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Gross Price",
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
				"width" : "15%"},
			{"title" : "Amount",
				"cls" : "amount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "20%"}
		],
		"footer" : [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function() {
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function() {
		var $upAmount = $(this).closest("tr").find(".upAmount");
		var upAmount = $upAmount.val();
		$upAmount.val(formatDecimalPlaces(upAmount, 6));
		updateOChargesAmt();
		updateTotalAmount();
		computeWtax();
	});

	$("#otherChargesTable").on("change", ".taxType", function() {
		updateOChargesAmt();
		updateTotalAmount();
		computeWtax();
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

function formatMoney() {
	var headerAmount = Number($("#totalAmount").val());
	$("#totalAmount").val(formatDecimalPlaces(headerAmount));
}

function parseDoubles() {
	var headerAmount = $.trim($("#totalAmount").val());
	if (headerAmount == "") {
		$("#totalAmount").val("0.00");
	} else {
		$("#totalAmount").val(Number(accounting.unformat(headerAmount)));
	}
}

function formatAmount(elem) {
	var headerAmount = accounting.unformat($(elem).val());
	$(elem).val(formatDecimalPlaces(Number(headerAmount)));
}

</script>
</head>
<body>
	<div id="divMiscellaneousList" style="display: none"></div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="arMiscellaneous" id="arMiscellaneousForm">
			<div class="modFormLabel">Other Receipt<span id="spanDivisionLbl"> - ${arMiscellaneous.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id" />
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<form:hidden path="customerName" id="customerName"/>
			<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="wtVatAmount" id="hdnWtVatAmountId"/>
			<form:hidden path="arMiscellaneousLinesJson" id="arMiscellaneousLinesJson"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number:</td>
							<td class="value">
								<form:input path="sequenceNo" readonly="true" cssClass="textBoxLabel"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status:</td>
							<c:set var="status" value="${arMiscellaneous.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtarMiscellaneousStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Other Receipt Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<select class="frmSelectClass" id="companyId">
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
							<td class="labels">* Type</td>
							<td class="value">
								<form:select path="arMiscellaneousTypeId" cssClass="frmSelectClass" 
									onchange="enableDisableCheck(this.value);">
									<form:options items="${arMiscellaneousTypes}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Check/Online Reference No.</td>
							<td class="value">
								<form:input path="refNumber" id="refNumber" class="inputSmall" disabled="true" />
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
								<form:select path="receiptMethodId" cssClass="frmSelectClass" onchange="receiptMehodOnChanged();">
									<form:options items="${receiptMethods}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" id="txtCustomer" class="input"
									onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="getCustomer();" />
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
								<form:errors path="arCustomerId" id="arCustomerIdError" cssClass="error" />
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
							<td></td>
							<td colspan="2">
								<form:errors path="maturityDate" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Description</td>
							<td class="value"><form:textarea path="description"
									id="description" class="input" /></td>
						</tr>
					
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="description" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Currency</td>
							<td class="value">
								<form:select path="currencyId" id="currencyId" cssClass="frmMediumSelectClass" onchange="getCurrencyRate(this);">
									<form:options items="${currencies}" itemValue="id" itemLabel="name"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="currencyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Amount</td>
							<td class="value">
								<form:input path="amount" id="totalAmount" class="numeric" style="text-align: right;" size="20" 
									onfocus="parseDoubles();" onblur="formatAmount(this);"/>
							</td> 
						</tr>
						
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="amount" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AR Lines</legend>
					<div id="otherChargesTable"></div>
					<table>
						<tr>
							<td><form:errors path="arMiscellaneousLines" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Document/s</legend>
					<div id="documentsTable"></div>
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
				</fieldset>
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
						<td colspan="3">Total VAT</td>
						<td></td>
						<td>
							<span id="totalVat"></span>
						</td>
					</tr>
					<tr>
						<td colspan="3">Withholding VAT</td>
						<td></td>
						<td>
							<span id="withholdingVAT"></span>
						</td>
					</tr>
					<tr>
						<td colspan="3">Withholding Tax</td>
						<td>
							<form:select path="wtAcctSettingId" id="wtaxAcctSettingId" cssClass="frmSmallSelectClass"
								cssStyle="width: 95%;" onchange="assignWtaxAcctSetting(this);">
							</form:select>
						</td>
						<td>
							<span id="computedWTax">0.00</span>
						</td>
					</tr>
					<tr>
						<td colspan="3">Total Amount Due</td>
						<td></td>
						<td>
							<span id="grandTotal">0.00</span>
						</td>
					</tr>
					<tr>
						<td colspan="5">
							<form:errors path="totalAmount" cssClass="error"/>
						</td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSaveArMiscellaneous" value="Save" onclick="saveArMiscellaneous();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>