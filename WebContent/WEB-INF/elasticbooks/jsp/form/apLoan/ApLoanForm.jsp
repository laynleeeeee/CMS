<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : AP Loan form jsp page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.pettycashhandler.js"></script>
<script type="text/javascript">
var PREV_RATE = 0;
$(document).ready(function() {
	loadDivisions("${aPInvoice.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	var wtAcctSettingId = "";
	if ("${pId}" != 0) {
		wtAcctSettingId = "${aPInvoice.wtAcctSettingId}";
		$("#supplierId").val("${aPInvoice.supplier.name}");
		var dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${aPInvoice.dueDate}'/>";
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${aPInvoice.amount}' />");
		disableFormFields();
		$("#currencyId").attr("disabled","disabled");
		computeBalance();
		$("#textLpNumber").val("${aPInvoice.lpNumber}");
		$("#browseRefTd a").hide();
	}
	initApLinesTbl();
	initializeDocumentsTbl();
	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
});

function loadDivisions(divisionId) {
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

function disableFormFields() {
	// If form workflow status = COMPLETED
	var invoiceStatus = "${aPInvoice.formWorkflow.complete}";
	if (invoiceStatus == "true" || "${aPInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#invoiceForm :input").attr("disabled", "disabled");
	}
}

function setDefaultValue($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var tblDesc = $tr.find(".description").val();
	if (tblDesc == "" || tblDesc == "0") {
		var amount = $tr.find(".amount").val();
		$tr.find(".amount").val(amount != "" ? amount : formatDecimalPlaces(0.0));
		$tr.find(".description").val($.trim($("#description").val()));
	}
}

var isSaving = false;
function saveInvoice() {
	var hasLpRef = $("#textLpNumber").val() != "";
	if (isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize() && hasLpRef) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		$("#btnSaveAPInvoice").attr("disabled", "disabled");
		$("#apLinesJson").val($apLinesTbl.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#divisionId").removeAttr("disabled");
		$("#currencyId").removeAttr("disabled");
		unformatAmount();
		doPostWithCallBack ("invoiceForm", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				var name = $("#invoiceNumber").val();
				updateTable (formStatus);
				if ("${pId}" == 0) {
					dojo.byId("form").innerHTML = "";
				}
			} else {
				var dueDate = $("#dueDate").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				var currentStatus = $("#txtInvoiceStatus").val();

				var divisionId = $("#divisionId").val();
				var supplierId = $("#hdnSupplierId").val();
				var supplierAccountId = $("#hdnSupplierAccountId").val();
				var currencyId = $("#hdnCurrencyId").val();
				var currencyRateId = $("#hdnCurrencyRateId").val();
				var currencyRateValue = $("#hdnCurrRateValue").val();
				var referenceObjectId = $("#referenceObjectId").val();

				var supplierName = $("#spnLenderName").text();
				var supplierAccountName = $("#spnLenderAcctName").text();
				var currencyName = $("#spnCurrencyName").text();
				var principalLoan = accounting.unformat($("#spnPrincipalLoan").text());
				var lpNumber = $("#textLpNumber").val();
				var loanAccountId = $("#hdnLoanAccountId").val();
				if ("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtInvoiceStatus").val(currentStatus);
				}
				if("${aPInvoice.id}" != 0){
					$("#currencyId").attr("disabled","disabled");
				}
				loadDivisions(divisionId);
				formatAmount();
				initApLinesTbl();
				initializeDocumentsTbl();
				loadWtaxAcctSettings(wtAcctSettingId);
				computeWtax();
				//Reload loan proceeds data.
				loadHeaderFields(referenceObjectId, companyId, null, divisionId, 
						null, supplierId, supplierName, supplierAccountId, supplierAccountName,
					currencyId, currencyRateId, currencyRateValue, currencyName, principalLoan, lpNumber, loanAccountId);
				$("#divisionId").attr("disabled","disabled");
			}
			isSaving = false;
		});
	} else if (checkExceededFileSize()) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}

	if(!hasLpRef) {
		$("#spnLpRefErr").text("Loan proceeds reference is required.");
	}
}

function unformatAmount() {
	var totalAmount = $("#principalPayment").val();
	$("#principalPayment").val(accounting.unformat(totalAmount));
	var principalAmount = $("#hdnPrincipalAmount").val();
	$("#hdnPrincipalAmount").val(accounting.unformat(principalAmount));
}

function formatAmount() {
	var totalAmount = $("#principalPayment").val();
	$("#principalPayment").val(formatDecimalPlaces(Number(totalAmount)));
}

function showDivisions($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var companyId = $("#companyId").val();
	var divisionNumber = "${aPInvoice.division.number}";
	var uri = contextPath+"/getDivisions/new?companyId="+companyId+"&divisionNumber="+divisionNumber
			+"&isActive=true&limit=1";
	$($txtBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$tr.find(".divisionId").val(ui.item.id);
			$(this).val(ui.item.number);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number+"-"+item.name+ "</a>" )
			.appendTo( ul );
	};
}

function getDivision($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var companyId = $("#companyId").val();
	var divisionNumber = $($txtBox).val();
	var hasCompanyId = companyId != "" && companyId != null && companyId != "0";
	var hasDivisionNo = divisionNumber != null && $.trim(divisionNumber) != "" && $.trim(divisionNumber) != "0";
	if (hasCompanyId && hasDivisionNo) {
		var uri = contextPath+"/getDivisions?divisionNumber="+divisionNumber;
		$.ajax({
			url: uri,
			success : function(division) {
				$("#acctCombinationErrorMsg").text("");
				if (division != null && division != undefined) {
					$tr.find(".divisionId").val(division.id);
					$tr.find(".divisionName").val(division.name);
					setAccountCombination($tr);
				}
			},
			error : function(error) {
				$($txtBox).val("")
				$tr.find(".divisionId").val("");
				$tr.find(".divisionName").val("");
				$tr.find(".accountName").val("");
				setAccountCombination($tr);
				$("#acctCombinationErrorMsg").text("Invalid division.");
				$($txtBox).focus();
			},
			dataType: "json"
		});
	} else {
		$($txtBox).val("");
		$tr.find(".divisionId").val("");
		$tr.find(".accountNumber").val("");
		$tr.find(".accountId").val("");
	}
}

function showAccounts($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var companyId = $("#companyId").val();
	var divisionId = $tr.find(".divisionId").val();
	var accountName = $($txtBox).val();
	var uri = contextPath+"/getAccounts/byName?accountName="+$.trim(accountName)+"&companyId="+companyId
			+"&divisionId="+divisionId+"&limit=10";
	$($txtBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$tr.find(".accountId").val(ui.item.id);
			$tr.find(".accountNumber").val(ui.item.number);
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
	var acctNumber = $($txtBox).closest("tr").find(".accountNumber").val();
	var hasAccountNo = acctNumber != null && $.trim(acctNumber) != "" && $.trim(acctNumber) != "0"
	if (hasCompanyId && hasDivisionId && hasAccountNo) {
		var uri = contextPath+"/getAccounts?companyId=&divisionId=&accountNumber="+acctNumber
		$.ajax({
			url: uri,
			success : function(account) {
				$("#acctCombinationErrorMsg").text("");
				if (account != null && account != undefined) {
					$tr.find(".accountId").val(account.id);
					$tr.find(".accountName").val(account.accountName);
					setAccountCombination($tr);
				}
			},
			error : function(error) {
				$($txtBox).val("")
				$tr.find(".accountId").val("");
				$tr.find(".accountName").val("");
				setAccountCombination($tr);
				$("#acctCombinationErrorMsg").text("Invalid account.");
				$($txtBox).focus();
			},
			dataType: "json"
		});
	} else {
		$($txtBox).val("");
		$tr.find(".accountId").val("");
	}
}

function setAccountCombination($tr) {
	var divisionName = $tr.find(".divisionName").val();
	var accountName = $tr.find(".accountName").val();
	$tr.find(".acctCombinationName").text(divisionName +  " - " + accountName);
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

function initApLinesTbl() {
	var apLinesJson = JSON.parse($("#apLinesJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$apLinesTbl = $("#apLinesTbl").editableItem({
		data: apLinesJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "accountCombinationId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "description", "varType" : "string"},
			{"name" : "companyNumber", "varType" : "string"},
			{"name" : "divisionNumber", "varType" : "string"},
			{"name" : "accountNumber", "varType" : "string"},
			{"name" : "companyId", "varType" : "int"},
			{"name" : "divisionId", "varType" : "int"},
			{"name" : "accountId", "varType" : "int"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "grossAmount", "varType" : "double"},
			{"name" : "acctCombinationName", "varType" : "string"},
			{"name" : "companyName", "varType" : "string"},
			{"name" : "divisionName", "varType" : "string"},
			{"name" : "accountName", "varType" : "string"},
			{"name" : "loanChargeTypeId", "varType" : "int"}
		],
		contextPath: cPath,
		header:[
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "accountCombinationId",
				"cls" : "accountCombinationId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "companyId",
				"cls" : "companyId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "divisionId",
				"cls" : "divisionId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "accountId",
				"cls" : "accountId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "taxTypeId",
				"cls" : "taxTypeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "companyName",
				"cls" : "companyName",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "divisionName",
				"cls" : "divisionName",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Loan Charges",
				"cls" : "accountName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "20%",
				"handler" : new PettyCashHandler (new function () {
				})},
			{"title" : "Division",
				"cls" : "divisionNumber tblInputText",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Account",
				"cls" : "accountNumber",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Combination",
				"cls" : "acctCombinationName tblLabelText",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "loanChargeTypeId",
				"cls" : "loanChargeTypeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Charge Type", 
				"cls" : "loanChargeType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "10%" },
			{"title" : "Gross Amount",
				"cls" : "grossAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "20%"},
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "15%"},
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Amount", 
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "20%"},
			{"title" : "Description", 
				"cls" : "description tblInputText",
				"editor" : "hidden",
				"visible" : false}
		],
		"footer" : [
			{"cls" : "amount"}
		],
	});

	$("#apLinesTbl").on("click", ".tblSelectClass", function() {
		loadSelections();
	});

	$("#apLinesTbl").on("focus", ".divisionNumber", function() {
		loadSelections();
		setDefaultValue(this);
	});

	$("#apLinesTbl").on("keydown", ".divisionNumber", function() {
		showDivisions(this);
	});

	$("#apLinesTbl").on("blur", ".divisionNumber", function() {
		getDivision(this);
	});

	$("#apLinesTbl").on("keydown", ".accountName", function() {
		showAccounts(this);
	});

	$("#apLinesTbl").on("focus", ".accountName", function() {
		//Set default value for division data based on the header division id.
		$(this).closest("tr").find(".divisionId").val("${aPInvoice.divisionId}");
		$(this).closest("tr").find(".divisionName").val("${aPInvoice.division.name}");
		$(this).closest("tr").find(".divisionNumber").val("${aPInvoice.division.number}");
	});

	$("#apLinesTbl").on("blur", ".accountName", function() {
		getAccount(this);
	});

	$("#apLinesTbl").on("change", ".taxType", function() {
		$(this).closest("tr").find(".taxTypeId").val($(this).val());
		computeVatAmount($(this).closest("tr").find(".grossAmount"));
		computeWtax();
	});

	$("#apLinesTbl").on("change", ".loanChargeType", function() {
		$(this).closest("tr").find(".loanChargeTypeId").val($(this).val());
	});

	$("#apLinesTbl").on("focus", ".grossAmount", function() {
		var amount = $(this).val();
		if (amount != "") {
			$(this).val(accounting.unformat(amount));
		}
	});

	$("#apLinesTbl").on("blur", ".grossAmount", function() {
		$(this).val(formatDecimalPlaces($(this).val(), 6));
		computeVatAmount(this);
		computeWtax();
		computeGrandTotal();
	});

	loadSelections(); // load tax type when initializing the table
	resizeTbl("#apLinesTbl", 6);
}

function resizeTbl(tableId, rowCount){
	$(tableId + " table").css("width", "100%");
	addTotalLabel(tableId)
	$(tableId).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function addTotalLabel($tbl){
	var $tr = $($tbl).find("tfoot tr td:nth-child(1)");
	$($tr).html("Total");
}

function computeVatAmount($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var taxTypeId = $tr.find(".taxType").val();
	var grossAmount = accounting.unformat($($txtBox).val());
	var isVatable = taxTypeId != "" && taxTypeId != null && taxTypeId != 2 && taxTypeId != 3 && taxTypeId != 7;
	var netOfVat = isVatable ? (grossAmount / 1.12).toFixed(6) : grossAmount;
	var vatAmount = (grossAmount - netOfVat).toFixed(6);
	$tr.find(".vatAmount").text(formatDecimalPlaces(vatAmount));
	var amount = grossAmount - vatAmount;
	$tr.find(".amount").text(formatDecimalPlaces(amount));
}

function loadSelections() {
	$("#apLinesTbl tbody tr").find(".loanChargeType").each(function() {
		populateLoanChargeTypes(this);
	});

	$("#apLinesTbl tbody tr").find(".taxType").each(function() {
		populateTaxTypes(this);
	});
}

function recomputeInvAmount() {
	$("#apLinesTbl tbody tr").each(function() {
		computeVatAmount($(this).closest("tr").find(".grossAmount"));
		computeWtax();
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

function populateLoanChargeTypes($txtBox) {
	var $loanChargeType = $txtBox;
	var loanChargeTypeId = $($txtBox).closest("tr").find(".loanChargeTypeId").val();
	var uri = contextPath + "/getLoanChargeType";
	if (typeof loanChargeTypeId != "undefined" && loanChargeTypeId != null
			&& loanChargeTypeId != "") {
		uri += "?loanChargeTypeId="+loanChargeTypeId;
	}
	$($txtBox).empty();
	var optionParser = {
		getValue: function (rowObject){
			if (rowObject != null) {
				return rowObject["id"];
			}
		},
		getLabel: function (rowObject){
			if (rowObject != null) {
				return rowObject["name"];
			}
		}
	};
	var postHandler = {
		doPost: function(data) {
			// This is to remove any duplication.
 			var found = [];
			$($txtBox).each(function() {
				if ($.inArray(this.value, found) != -1) {
					$(this).remove();
				}
				found.push(this.value);	
			});
			if (typeof loanChargeTypeId != "undefined" && loanChargeTypeId != null && loanChargeTypeId != "") {
				$($txtBox).val(loanChargeTypeId);
			}
		}
	};
	loadPopulateObject (uri, false, loanChargeTypeId, $txtBox, optionParser, postHandler, false, true);
}

function computeGrandTotal() {
	var totalCharges = 0;
	var totalVatAmount = 0;
	var principalPayment = accounting.unformat($("#principalPayment").val());
	$("#apLinesTbl tbody tr").each(function() {
		totalCharges += accounting.unformat($(this).find(".amount").text());
		totalVatAmount += accounting.unformat($(this).find(".vatAmount").text());
	});
	var subtotal = principalPayment + totalCharges;
	$("#apLinesTbl").find("tfoot tr").find(".amount").html(formatDecimalPlaces(totalCharges));

	var formattedSubTotal = formatDecimalPlaces(subtotal);
	$("#subTotal").text(formattedSubTotal);

	var formattedTotalVAT = formatDecimalPlaces(totalVatAmount);
	$("#totalVat").text(formattedTotalVAT);

	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	var formattedWtaxAmount = formatDecimalPlaces(wtaxAmount);
	$("#computedWTax").text(formattedWtaxAmount);

	var grandTotal = (accounting.unformat(formattedSubTotal) + accounting.unformat(formattedTotalVAT)) - accounting.unformat(formattedWtaxAmount)
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function loadWtaxAcctSettings(wtAcctSettingId) {
	var $select = $("#wtaxAcctSettingId");
	$select.empty();
	var divisionId = $("#divisionId").val();
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getWithholdingTaxAcct?companyId="+companyId+"&isCreditable=false";
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
				var totalCharges = 0;
				$("#apLinesTbl tbody tr").find(".amount").each(function() {
					totalCharges += accounting.unformat($(this).text());
				});
				var computedWTax = formatDecimalPlaces(totalCharges * wtPercentageValue);
				$("#hdnWtAmount").val(accounting.unformat(computedWTax));
				$("#computedWTax").text(computedWTax);
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
		computeGrandTotal();
	}
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeWtax();
}

function showLpReferences() {
	$("#divLpReferenceId").html("");
	$("#divLpReferenceId").load(contextPath+"/aPInvoiceForm/${aPInvoice.divisionId}/showLpReferences");
}

function loadLpReference(lpRefId) {
	$.ajax({
		url : contextPath + "/aPInvoiceForm/loadLpReference?lpId="+lpRefId,
		success : function(apLoan) {
			$("#divLpReferenceId").html("");
			$("#aClose")[0].click();
			loadHeaderFields(apLoan.referenceObjectId, apLoan.companyId, apLoan.company.name, apLoan.divisionId, 
					apLoan.division.name, apLoan.supplierId, apLoan.supplier.name, apLoan.supplierAccountId,
					apLoan.supplierAccount.name, apLoan.currencyId, apLoan.currencyRateId, apLoan.currencyRateValue,
					apLoan.currency.name, apLoan.principalLoan, apLoan.lpNumber, apLoan.loanAccountId);
			loadWtaxAcctSettings();
			$("#spnLpRefErr").text("");
		},
		complete : function(rr) {
			computeWtax();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function loadHeaderFields(referenceObjectId, companyId, companyName, divisionId, 
		divisionName, supplierId, supplierName, supplierAccountId, supplierAccountName,
		currencyId, currencyRateId, currencyRateValue, currencyName, principalLoan, lpNumber, loanAccountId) {
	$("#companyId").val(companyId);
	$("#divisionId").val(divisionId);
	$("#hdnSupplierId").val(supplierId);
	$("#hdnSupplierAccountId").val(supplierAccountId);
	$("#hdnCurrencyId").val(currencyId);
	$("#hdnCurrencyRateId").val(currencyRateId == 0 ? null : currencyRateId);
	$("#hdnCurrRateValue").val(currencyRateValue);
	$("#referenceObjectId").val(referenceObjectId);
	$("#hdnLoanAccountId").val(loanAccountId);
	$("#hdnPrincipalLoan").val(principalLoan);

	//Labels
	$("#spnLenderName").text(supplierName);
	$("#spnLenderAcctName").text(supplierAccountName);
	$("#spnCurrencyName").text(currencyName);
	$("#spnPrincipalLoan").text(formatDecimalPlaces(principalLoan));
	$("#textLpNumber").val(lpNumber);
	computeBalance();
}

function computeBalance() {
	var loan = accounting.unformat($("#spnPrincipalLoan").text());
	var payment = accounting.unformat($("#principalPayment").val());
	var balance = loan - payment;
	$("#spnbalance").text(formatDecimalPlaces(balance));
}

function formatNumeric(elem) {
	var value = accounting.unformat($(elem).val());
	$(elem).val(accounting.formatMoney(value));
}

</script>
<style type="text/css">
input.numeric {
	width: 172px;
}
</style>
</head>
<body>
	<div id="container" class="popupModal">
		<div id="divRrReferenceContainer" class="reveal-modal">
			<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
			<div id="divLpReferenceId"></div>
		</div>
	</div>
	<div id="divComboList" style="display: none"></div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="aPInvoice" id="invoiceForm">
			<div class="modFormLabel">${title}<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="ebObjectId"/>
			<form:hidden path="referenceObjectId"/>
			<form:hidden path="apLinesJson" id="apLinesJson"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<form:hidden path="invoiceTypeId"/>
			<form:hidden path="currencyId" id="hdnCurrencyId"/>
			<form:hidden path="currencyRateId" id="hdnCurrencyRateId"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<form:hidden path="supplierId" id="hdnSupplierId"/>
			<form:hidden path="supplierAccountId" id="hdnSupplierAccountId"/>
			<form:hidden path="loanAccountId" id="hdnLoanAccountId"/>
			<form:hidden path="principalLoan" id="hdnPrincipalLoan"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">APL No.</td>
							<td class="value">
								<form:input path="sequenceNumber" readonly="true" cssClass="textBoxLabel"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<td class="value">
								<c:set var="status" value="${aPInvoice.formWorkflow.currentFormStatus.description}"/>
								<c:if test="${status eq null}">
									<c:set var="status" value="NEW"/>
								</c:if>
								<input type="text" id="txtInvoiceStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}' />
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AP Loan Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company </td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass"
									items="${companies}" itemLabel="numberAndName" itemValue="id" >
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
							<td class="labels">Loan Proceeds Reference</td>
							<td class="value" id="browseRefTd">
								<form:input path="lpNumber" id="textLpNumber" class="inputSmall" readonly="true"/>
								<a href="#container" id="waOpen" data-reveal-id="divLpReferenceId"
									class="link_button" onclick="showLpReferences();">Browse LP</a>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="lpNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><span id="spnLpRefErr" class="error"></span></td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="glDate" id="glDate" onblur="evalDate('glDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('glDate')" style="cursor: pointer"
									style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="glDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Lender</td>
							<td class="value">
								<span id="spnLenderName">${aPInvoice.supplier.name}</span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="supplierId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Lender Account</td>
							<td class="value">
								<span id="spnLenderAcctName">${aPInvoice.supplierAccount.name}</span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="supplierAccountId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Currency</td>
							<td class="value">
								<span id="spnCurrencyName">${aPInvoice.currency.name}</span>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="currencyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Principal Loan</td>
							<td class="value ">
								<span id="spnPrincipalLoan">
									<fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value='${aPInvoice.principalLoan}'/>
								</span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"></td>
						</tr>
						<tr>
							<td class="labels">Principal Payment</td>
							<td class="value">
								<form:input path="principalPayment" id="principalPayment" 
									onblur="computeBalance();formatNumeric(this);computeGrandTotal();"
									class="numeric" cssStyle="width: 172px;" maxLength="13"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="principalPayment" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Outstanding balance</td>
							<td class="value ">
								<span id="spnbalance"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"></td>
						</tr>
						<tr>
							<td class="labels">Description</td>
							<td class="value">
								<form:textarea path="description" id="description" class="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="description" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Loan Details</legend>
					<div id="apLinesTbl"></div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="aPlineMessage" cssClass="error"/></td>
					</tr>
					<tr>
						<td><span id="acctCombinationErrorMsg" class="error"></span></td>
					</tr>
				</table>
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
						<td colspan="3"></td>
						<td></td>
						<td><form:errors path="formWorkflowId" cssClass="error" /></td>
					</tr>
					<tr>
						<td colspan="5">
							<form:errors path="apInvoiceErrMessage" cssClass="error" />
						</td>
					</tr>
					<tr>
						<td align="right" colspan="5">
							<input type="button" id="btnSaveAPInvoice" value="Save" onclick="saveInvoice();"/>
						</td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>