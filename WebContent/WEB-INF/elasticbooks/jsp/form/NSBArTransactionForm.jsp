<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : AR Transaction form page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/formatUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.saleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
var defaultArLine = null;
var selectCustomerAcct = "${arTransaction.customerAcctId}";
var terms = new Array();
var arCustAcctTerms = new Array();
var maxAllowableAmount = null;
var isAllowed = false;
var totalTrAmount = null;
var PREV_RATE = 0;
$(document).ready(function() {
	var wtAcctSettingId = "";
	var dueDate = null;
	var termId = null;
	loadDivision("${arTransaction.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	initializeOtherChargesTbl();
	if ("${arTransaction.id}" != 0) {
		wtAcctSettingId = "${arTransaction.wtAcctSettingId}";
		disableAndSetCompany();
		termId = "${arTransaction.termId}";
		dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${arTransaction.dueDate}'/>";
		maxAllowableAmount = parseFloat("${arTransaction.arCustomer.maxAmount}");
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arTransaction.amount}'/>");
		updateOChargesAmt();
		computeGrandTotal();
		var status = parseInt("${arTransaction.formWorkflow.currentStatusId}");
		if (status == 17 || status == 4) {
			$("#transactionForm :input").attr("disabled","disabled");
		}
		$("#currencyId").attr("disabled","disabled");
	} else {
		computeDueDate();
	}
	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
	filterCustomerAccts(dueDate, termId);
	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
	initializeDocumentsTbl();
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

function disableAndSetCompany() {
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${arTransaction.companyId}"+"'>"+
			"${arTransaction.company.numberAndName}"+"</option>");
}

function ArCustAcctTerm (arCustAcctId, termId, arLine) {
	this.arCustAcctId = arCustAcctId;
	this.termId = termId;
	this.arLine = arLine;
}

function Term (id, days) {
	this.id = id;
	this.days = days;
}

function getCustomer() {
	var companyId = $("#companyId").val();
	var customerName = $.trim($("#txtCustomer").val());
	if (customerName != "") {
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
					$("#customerId").val(customer[0].id);
					$("#txtCustomer").val(customer[0].name);
					maxAllowableAmount = parseFloat(customer[0].maxAmount);
					isAllowed = false;
				} else {
					$("#customerId").val("");
					$("#customerIdErr").text("");
					$("#spanCustomerError").text("Invalid customer name.");
				}
				filterCustomerAccts();
			},
			error : function(error) {
				maxAllowableAmount = null;
				$("#customerIdErr").text("");
				$("#spanCustomerError").text("Invalid customer name.");
			},
			dataType: "json"
		});
	}
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
			$("#customerId").val(ui.item.id);
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

function filterCustomerAccts(dueDate, termId){
	$("#arCustomerAcctId").empty();
	var companyId = $("#companyId").val();
	if ($.trim($("#txtCustomer").val()) == "") {
		$("#customerId").val("");
	} else if (companyId != "") {
		var customerId = $("#customerId").val();
		var uri = contextPath + "/getArCustomerAccounts/includeSavedInactive?arCustomerId="+customerId+
		"&companyId="+companyId;
		var divisionId = $("#divisionId").val();
		if (divisionId != "" && divisionId != "undefined") {
			uri += "&divisionId="+divisionId;
		}
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
					if ("${arTransaction.id}" == 0) {
						$(".hdnArLineSetupIds").val("");
						defaultArLine = null;
					}
					$("#arCustomerAcctId").val(selectCustomerAcct).attr("selected",true);
					var arCustomerAcctId = $("#arCustomerAcctId option:selected").val();
					arCustAcctTerms = new Array();
					for (var index = 0; index < data.length; index++){
						var rowObject =  data[index];
						var id = rowObject["id"];
						if (id == arCustomerAcctId){
							if (termId == null) {
								var defaultTerm = rowObject["termId"];
								$("#termId").val(defaultTerm).attr("selected" ,true);
							}
							defaultArLine = rowObject["defaultArLineSetup"];
						}
						var arCustAcctTerm = new ArCustAcctTerm(id, rowObject["termId"], rowObject["defaultArLineSetup"]);
						arCustAcctTerms.push (arCustAcctTerm);
					}

					//Compute the due date based on the GL Date and the term selected.
					if (dueDate == null) {
						computeDueDate ();
					}
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAcctId", optionParser, postHandler);
	}
}

function assignCustomerAcct (select) {
	selectCustomerAcct = $(select).val();
	for (var i=0; i<arCustAcctTerms.length; i++) {
		if (selectCustomerAcct == arCustAcctTerms[i].arCustAcctId) {
			$(".hdnArLineSetupIds").val("");
			defaultArLine = arCustAcctTerms[i].arLine;
			$("#termId").val(arCustAcctTerms[i].termId).attr("selected" ,true);
			break;
		}
	}
	isAllowed = false;
}

function computeDueDate () {
	var glDateVal = $("#glDate").val ();
	if (glDateVal == null || glDateVal == ""){
		$("#dueDate").val ("");
		return;
	}
	var additionalDays = 0;
	var currentSelTermId = $("#termId option:selected").val();
	for (var i = 0; i < terms.length; i++) {
		var term = terms[i];
		if (term.id == currentSelTermId) {
			additionalDays = term.days;
			break;
		}
	}
	var glDate = new Date (glDateVal);
	glDate.setDate(glDate.getDate() + parseInt(additionalDays));
	if (!isNaN( glDate.getMonth()) && !isNaN( glDate.getDate()) && !isNaN(glDate.getFullYear())){
		$("#dueDate").val ((glDate.getMonth() + 1) +"/"+glDate.getDate()+
				"/"+glDate.getFullYear());
	} else {
		$("#dueDate").val ("");
	}
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

var isSaving = false;
function saveTransaction(){
	if (isSaving == false) {
		isSaving = true;
		$("#companyId").removeAttr("disabled");
		$("#btnSaveARTransaction").attr("disabled","disabled");
		$("#arServiceLinesJson").val($otherChargesTable.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		parseDoubles();
		$("#divisionId").removeAttr("disabled");
		$("#currencyId").removeAttr("disabled");
		doPostWithCallBack("transactionForm", "form", function(data){
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if ("${pId}" == 0) {
					dojo.byId("form").innerHTML = "";
				}
				isSaving = false;
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var termId = $("#termId").val();
				var dueDate = $("#dueDate").val();
				var customer = $("#txtCustomer").val();
				var currencyId = $("#currencyId").val();
				var rate = $("#hdnCurrRateValue").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				var currentStatus = $("#txtTransactionStatus").val();
				if ("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtTransactionStatus").val(currentStatus);
					disableAndSetCompany();
				}
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#txtCustomer").val(customer);
				loadDivision("${arTransaction.divisionId}");
				$("#divisionId").attr("disabled","disabled");
				if ("${arTransaction.id}" != 0){
					$("#currencyId").attr("disabled","disabled");
				}
				filterCustomerAccts(dueDate, termId);
				initializeOtherChargesTbl();
				loadWtaxAcctSettings(wtAcctSettingId);
				updateOChargesAmt();
				computeGrandTotal();
				computeWtax();
				formatMoney();
				initializeDocumentsTbl();
				isSaving = false;
			}
			$("#btnSaveARTransaction").removeAttr("disabled");
		});
	}
}

function checkMaxAmount(elem) {
	if (maxAllowableAmount != 0) {
		value = parseFloat(totalTrAmount) + parseFloat($(elem).val());
		if (!isNaN(value) && maxAllowableAmount != null && !isAllowed) {
			if (value > maxAllowableAmount) {
				var confirmation = confirm("The amount has exceeded the customer's maximum allowable amount of "
						+ formatDecimalPlaces(maxAllowableAmount) + ". Do you wish to continue?");
				if (confirmation)
					isAllowed = true;
				else {
					isAllowed = false;
					$(elem).val("");
				}
			}
		}
	}
}

function clearFormFields() {
	$("#txtCustomer").val("");
	$("#arCustomerAcctId").empty();
	$otherChargesTable.emptyTable();
}

function initializeOtherChargesTbl() {
	var otherChargesTable = JSON.parse($("#arServiceLinesJson").val());
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
		computeGrandTotal();
		computeWtax();
	});

	$("#otherChargesTable").on("change", ".taxType", function() {
		updateOChargesAmt();
		computeGrandTotal();
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
	computeGrandTotal();
	computeWtax();
	PREV_RATE = rate;
}

function computeGrandTotal() {
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
	var formattedSubTotal = formatDecimalPlaces(parseFloat(totalOtherCharges).toFixed(6));
	$("#subTotal").html(formattedSubTotal);
	var formattedTotalVAT = formatDecimalPlaces(parseFloat(totalVat).toFixed(6));
	$("#totalVat").html(formatDecimalPlaces(formattedTotalVAT));
	var formattedTotalWtVAT = formatDecimalPlaces(parseFloat(totalWtVAT).toFixed(6));
	$("#withholdingVAT").html(formattedTotalWtVAT);
	$("#hdnWtVatAmountId").val(accounting.unformat(formattedTotalWtVAT));
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	var formattedWtaxAmount = formatDecimalPlaces(parseFloat(wtaxAmount).toFixed(6));
	$("#computedWTax").html(formattedWtaxAmount);
	$("#hdnWtAmount").val(accounting.unformat(formattedWtaxAmount));
	var grandTotal = (accounting.unformat(formattedSubTotal) + accounting.unformat(formattedTotalVAT))
		-	accounting.unformat(formattedTotalWtVAT) - accounting.unformat(formattedWtaxAmount)
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function loadWtaxAcctSettings(wtAcctSettingId) {
	var $select = $("#wtaxAcctSettingId");
	$select.empty();
	var companyId = $("#companyId").val();
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
				computedWTax = formatDecimalPlaces(computedWTax,4);
				$("#hdnWtAmount").val(accounting.unformat(computedWTax));
				$("#computedWTax").html(computedWTax);
				computeGrandTotal();
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
		computeGrandTotal();
	}
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeWtax();
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
</script>
<title>AR Transaction Form</title>
</head>
<body>
<div id="divARLineList" style=" display: none"></div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="arTransaction" id="transactionForm">
		<div class="modFormLabel">AR Transaction<span id="spanDivisionLbl"> - ${arTransaction.division.name}</span>
		<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="ebObjectId" />
		<form:hidden path="createdBy"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="customerId"/>
		<form:hidden path="customerName" id="customerName"/>
		<form:hidden path="arLinesJson" id="arLinesJson"/>
		<form:hidden path="arServiceLinesJson" id="arServiceLinesJson"/>
		<form:hidden path="wtAmount" id="hdnWtAmount"/>
		<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
		<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
		<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
		<form:hidden path="wtVatAmount" id="hdnWtVatAmountId"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">Sequence Number:</td>
						<td class="value">
							<form:input path="sequenceNumber" readonly="true" cssClass="textBoxLabel"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Status:</td>
						<c:set var="status" value="${arTransaction.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
						<td class="value">
							<input type="text" id="txtTransactionStatus" class="textBoxLabel"
									readonly="readonly" value='${status}'/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Transaction Header</legend>
				<table class="formTable" border=0>
					<tr>
						<td class="labels">* Company </td>
						<td class="value">
							<form:select path="companyId" id="companyId" cssClass="frmSelectClass"
								items="${companies}" itemLabel="numberAndName" itemValue="id" onchange="clearFormFields();">
							</form:select>
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
						<td class="labels"></td>
						<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Transaction Type</td>
						<td class="value"> 
							<form:select path="transactionClassificationId" cssClass="frmSelectClass">
								<form:options items="${transactionClassifications}" itemLabel="name" itemValue="id" />
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="labels">* Reference No.</td>
						<td class="value">
							<form:input path="transactionNumber" cssClass="inputSmall" />
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="transactionNumber" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Customer Name</td>
						<td class="value">
							<form:input path="arCustomer.name" id="txtCustomer" class="input" onkeydown="showCustomers();" onblur="getCustomer();"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<span id="spanCustomerError" class="error"></span>
							<form:errors path="customerId" cssClass="error" id="customerIdErr"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Customer Account</td>
						<td class="value">
							<form:select path="customerAcctId" id="arCustomerAcctId" cssClass="frmSelectClass"
								onchange="assignCustomerAcct (this);"></form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="customerAcctId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Term</td>
						<td class="value">
							<form:select path="termId" id="termId" cssClass="frmSelectClass" onchange="computeDueDate();">
								<form:options items="${terms}" itemLabel="name" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="termId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Transaction Date</td>
						<td class="value">
							<form:input path="transactionDate"
									id="transactionDate" onblur="evalDate('transactionDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('transactionDate')"
							style="cursor: pointer" style="float: right;" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="transactionDate" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* GL Date</td>
						<td class="value">
						<form:input path="glDate" id="glDate"
								onblur="evalDate('glDate');  computeDueDate();" style="width: 120px;"
								class="dateClass2" />
								<img id="imgDate2"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('glDate')" style="cursor: pointer"
							style="float: right;" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="glDate" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Due Date</td>
						<td class="value">
						<form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate')"
							style="width: 120px;" class="dateClass2" />
							<img id="imgDate3" src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dueDate')"
							style="cursor: pointer" style="float: right;" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="dueDate" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Description</td>
						<td class="value"><form:textarea path="description"
									id="description" class="input" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="description" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Currency</td>
						<td class="value">
							<form:select path="currencyId" id="currencyId" class="frmSelectClass" cssStyle="width: 172px;"
								onchange="getCurrencyRate(this);">
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
							<form:input path="amount" id="totalAmount" class="numeric" cssStyle="width: 172px;"
								onfocus="parseDoubles();" onblur="formatAmount(this);"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="amount" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>AR Lines</legend>
				<div id="otherChargesTable"></div>
				<input type="hidden" id="hdnOChargesAmt"/>
				<form:errors path="arLineError" cssClass="error" id="arLineError"/>
				<span id="spanUOMError" class="error"></span>
				<span id="spanArLineError" class="error"></span>
			</fieldset>
			<table class="frmField_set">
				<tr>
					<td><form:errors path="formWorkflowId" cssClass="error"/></td>
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
			<!-- Grand Total -->
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
					<td align="right"><input type="button" id="btnSaveARTransaction" value="Save" onclick="saveTransaction();"/></td>
				</tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>