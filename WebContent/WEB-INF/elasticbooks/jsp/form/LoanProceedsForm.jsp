<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : Loan Proceeds form jsp page -->
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
var terms = new Array();
var PREV_RATE = 0;
var selectReceiptMethod = "${loanProceeds.receiptMethodId}";
$(document).ready(function() {
	loadDivision("${loanProceeds.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	var wtAcctSettingId = "";
	if ("${loanProceeds.id}" == 0) {
		filterSupplierAccts();
	} else {
		wtAcctSettingId = "${loanProceeds.wtAcctSettingId}";
		$("#supplierId").val("${loanProceeds.supplier.name}");
		filterSupplierAccts("${loanProceeds.supplierAccountId}", "${loanProceeds.termId}");
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${loanProceeds.amount}' />");
		$("#txtLoanAccount").val("${loanProceeds.loanAccount.accountName}");
		disableFormFields();
	}
	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
	initLpLinesTbl();
	filterReceiptMethods("${loanProceeds.receiptMethodId}");
	initializeDocumentsTbl();
	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
});

function filterReceiptMethods(receiptMethodId) {
	$("#receiptMethodId").empty();
	var divisionId =$("#divisionId").val();
	var companyId = $("#companyId").val();
	if (divisionId != null) {
		var uri = contextPath + "/getArReceiptMethods/byDivision?divisionId="+divisionId+"&companyId="+companyId+
				"&receiptMethodId="+receiptMethodId;
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

function assignReceiptMethod(select) {
	selectReceiptMethod = $(select).val();
}

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

function getCurrencyRate(elem) {
	var currencyId = $(elem).val();
	if (currencyId != "" && currencyId != null && currencyId != 0) {
		$.ajax({
			url: contextPath+"/getCurrency/getLatestRate?currencyId="+currencyId,
			success : function(currencyRate) {
				if(currencyRate.active == true){
				var rate = currencyRate.rate;
				$("#hdnCurrRateValue").val(rate);
				convertCurrencies(currencyId, rate);
				}
			},
			error : function(error) {
				// No currency rate found, resetting to default rate to PHP
				convertCurrencies(currencyId, 1.0);
			},
			dataType: "json"
		});
	} else {
		$("#hdnCurrRateValue").val("");
	}
}

function convertCurrencies(currencyId, rate) {
	if (currencyId == 1) {
		rate = 1.0;
		$("#hdnCurrRateValue").val(rate);
	}

	$("#lpLinesTbl tbody tr").each(function(row) {
		var grossAmount = accounting.unformat($(this).find(".grossAmount").val());
		grossAmount = (grossAmount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".grossAmount").val(formatDecimalPlaces((grossAmount / rate)));

		recomputeInvAmount();
	});

	var totalAmount = accounting.unformat($("#totalAmount").val());
	totalAmount = (totalAmount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
	$("#totalAmount").val(formatDecimalPlaces((totalAmount / rate)));

	PREV_RATE = rate;
}

function disableFormFields() {
	$("#currencyId").attr("disabled", "disabled");
	// If form workflow status = COMPLETED
	var invoiceStatus = "${loanProceeds.formWorkflow.complete}";
	if (invoiceStatus == "true" || "${loanProceeds.formWorkflow.currentStatusId}" == 4) {
		$("#loanProceedsForm :input").attr("disabled", "disabled");
	}
}

function setTerm(termId) {
	var supplierAccountId = $("#supplierAccountId").val();
	if (supplierAccountId != "") {
		var uri = contextPath+"/getTerm";
		if (termId != null) {
			uri += "?termId="+termId;
		} else {
			uri += "?supplierAccountId="+supplierAccountId;
		}
		$.ajax({
			url: uri,
			success : function (term) {
				if(term != null) {
					$("#termId").val(term.id).attr("selected", true);
				}
			},
			error : function (error) {
				console.log(error);
			},
			dataType: "json",
		});
	}
}

function filterSupplierAccts(selectedSupplierAcctId, termId) {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var supplierName = $.trim($("#supplierId").val());
	if (supplierName != "") {
		var supplierId = $("#hdnSupplierId").val();
		var uri = contextPath+"/getApSupplierAccts?supplierId="+supplierId
		+"&companyId="+companyId+"&divisionId="+divisionId;
		var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		postHandler = {
			doPost: function(data) {
				for (var index = 0; index < data.length; index++) {
					var rowObject =  data[index];
					var id = rowObject["id"];
					var hasValue = selectedSupplierAcctId != "" && selectedSupplierAcctId != null && typeof selectedSupplierAcctId != "undefied";
					var currSupplierAcctId = hasValue ? selectedSupplierAcctId : $("#supplierAccountId").val();
					if (id == currSupplierAcctId) {
						if ("${loanProceeds.id}" == 0) {
							$("#termId").val(rowObject["termId"]).attr('selected',true);
						}
						$("#hdnSupplierAcctCompanyId").val(rowObject["companyId"]);
						loadWtaxAcctSettings();
						break;
					}
				}
				// This is to remove any duplication.
				var found = [];
				$("#supplierAccountId option").each(function() {
					if ($.inArray(this.value, found) != -1)
						$(this).remove();
					found.push(this.value);
				});
				setTerm(termId);
			}
		};
		loadPopulate (uri, false, selectedSupplierAcctId, "supplierAccountId", optionParser, postHandler);
	} else {
		$("#supplierAccountId").empty();
		$("#hdnSupplierAcctCompanyId").val("");
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
	if ($.trim($("#supplierId").val()) == "") {
		$("#hdnSupplierId").val("");
	}
	if (isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize()) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		$("#btnSaveLoanProceeds").attr("disabled", "disabled");
		$("#lpLinesJson").val($lpLinesTbl.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#divisionId").removeAttr("disabled");
		$("#currencyId").removeAttr("disabled");
		unformatAmount();
		doPostWithCallBack ("loanProceedsForm", "form", function(data) {
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
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var supplier = $("#supplierId").val();
				var supplierAccountId = $("#supplierAccountId").val();
				var termId = $("#termId").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				var currentStatus = $("#txtInvoiceStatus").val();
				var receiptMethodId = $("#receiptMethodId").val();
				var loanAccountName = $("#txtLoanAccount").val();
				if ("${loanProceeds.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtInvoiceStatus").val(currentStatus);
				}
				$("#txtLoanAccount").val(loanAccountName);
				$("#spanDivisionLbl").text(spanDivisionLbl);
				loadDivision("${loanProceeds.divisionId}");
				$("#divisionId").attr("disabled","disabled");
				$("#supplierId").val(supplier);
				selectReceiptMethod = receiptMethodId;
				if (supplierAccountId != "" && supplierAccountId != null
						&& typeof supplierAccountId != "undefined") {
					filterSupplierAccts(supplierAccountId, termId);
				}
				formatAmount();
				initLpLinesTbl();
				filterReceiptMethods(receiptMethodId);
				initializeDocumentsTbl();
				loadWtaxAcctSettings(wtAcctSettingId);
				computeWtax();
			}
			isSaving = false;
		});
	} else if (checkExceededFileSize()) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function unformatAmount() {
	var totalAmount = $("#totalAmount").val();
	$("#totalAmount").val(accounting.unformat(totalAmount));
}

function formatAmount() {
	var totalAmount = $("#totalAmount").val();
	$("#totalAmount").val(formatDecimalPlaces(Number(totalAmount)));
}

function Term (id, days) {
	this.id = id;
	this.days = days;
}

function computeDueDate () {
	var glDateVal = $("#glDate").val();
	if (glDateVal == null || glDateVal == "") {
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
	var glDate = new Date(glDateVal);
	glDate.setDate(glDate.getDate() + parseInt(additionalDays));
	var invDueDate = "";
	if (!isNaN(glDate.getMonth()) && !isNaN( glDate.getDate()) && !isNaN(glDate.getFullYear())) {
		invDueDate = (glDate.getMonth()+1) + "/"+glDate.getDate() + "/" + glDate.getFullYear();
	}
}

function showSuppliers() {
	$("#supplierAccountId").empty();
	$("#spanCustomerError").text("");
	var supplierName = $("#supplierId").val();
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath + "/getSuppliers/new?name="+processSearchName($.trim(supplierName))+"&companyId="+companyId+"&divisionId="+divisionId;
	$("#supplierId").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnSupplierId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui) {
			$.ajax({
				url: uri,
				success : function(item) {
					$("#spanSupplierError").text("");
					getSupplier();
				},
				error : function(error) {
					$("#spanSupplierError").text("Please select supplier.");
					$("#supplierId").val("");
					$("#hdnSupplierId").val("");
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

function getSupplier() {
	var supplierName = $("#supplierId").val();
	if (supplierName != "") {
		var uri = contextPath + "/getSuppliers/new?name="+processSearchName($.trim(supplierName))+"&isExact=true";
		var divisionId = $("#divisionId").val();
		if (divisionId != "" && divisionId != "undefined") {
			uri += "&divisionId="+divisionId;
		}
		$.ajax({
			url: uri,
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#hdnSupplierId").val(supplier[0].id);
					$("#supplierId").val(supplier[0].name);
				} else {
					$("#hdnSupplierId").val("");
					$("#supplierId").val("");
				}
				filterSupplierAccts();
			},
			error : function(error) {
				$("#supplierId").val("");
				$("#hndSupplierId").val("");
				$("#supplierAccountId").empty();
			},
			dataType: "json"
		});
	}
}

function showDivisions($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var companyId = $("#companyId").val();
	var divisionNumber = "${loanProceeds.division.number}";
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
	var divisionId = $("#divisionId").val();
	var companyId = $("#companyId").val();
	var accountName = $($txtBox).val();
	var uri = contextPath+"/getAccounts/byName?accountName="+$.trim(accountName)+"&companyId="+companyId
			+"&divisionId="+divisionId+"&limit=10";
	$($txtBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$tr.find(".accountId").val(ui.item.id);
			$(this).val(ui.item.number);
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
	var divisionId = $("#divisionId").val();
	var hasCompanyId = companyId != "" && companyId != null && companyId != "0";
	var hasDivisionId = divisionId != "" && divisionId != null && divisionId != "0";
	var acctNumber = $($txtBox).val();
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

function initLpLinesTbl() {
	var lpLinesJson = JSON.parse($("#lpLinesJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$lpLinesTbl = $("#lpLinesTbl").editableItem({
		data: lpLinesJson,
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
			{"name" : "loadnProceedsId", "varType" : "int"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "grossAmount", "varType" : "double"},
			{"name" : "acctCombinationName", "varType" : "string"},
			{"name" : "companyName", "varType" : "string"},
			{"name" : "divisionName", "varType" : "string"},
			{"name" : "accountName", "varType" : "string"}
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
			{"title" : "loadnProceedsId",
				"cls" : "loadnProceedsId",
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
			{"title" : "accountName",
				"cls" : "accountName",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Division",
				"cls" : "divisionNumber tblInputText",
				"editor" : "text",
				"visible" : true ,
				"width" : "10%"},
			{"title" : "Account",
				"cls" : "accountNumber tblInputText",
				"editor" : "text",
				"visible" : true ,
				"width" : "10%"},
			{"title" : "Combination",
				"cls" : "acctCombinationName tblLabelText",
				"editor" : "label",
				"visible" : true ,
				"width" : "15%"},
			{"title" : "Gross Amount",
				"cls" : "grossAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%"},
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
				"width" : "10%"},
			{"title" : "Description", 
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%"}
		],
		"footer" : [
			{"cls" : "amount"}
		],
	});

	$("#lpLinesTbl").on("click", ".taxType", function() {
		loadTaxTypes();
	});

	$("#lpLinesTbl").on("focus", ".divisionNumber", function() {
		loadTaxTypes();
		setDefaultValue(this);
	});

	$("#lpLinesTbl").on("keydown", ".divisionNumber", function() {
		showDivisions(this);
	});

	$("#lpLinesTbl").on("blur", ".divisionNumber", function() {
		getDivision(this);
	});

	$("#lpLinesTbl").on("keydown", ".accountNumber", function() {
		showAccounts(this);
	});

	$("#lpLinesTbl").on("blur", ".accountNumber", function() {
		getAccount(this);
	});

	$("#lpLinesTbl").on("change", ".taxType", function() {
		$(this).closest("tr").find(".taxTypeId").val($(this).val());
		computeVatAmount($(this).closest("tr").find(".grossAmount"));
		computeWtax();
		computeFooter();
	});

	$("#lpLinesTbl").on("focus", ".grossAmount", function() {
		var amount = $(this).val();
		if (amount != "") {
			$(this).val(accounting.unformat(amount));
		}
	});

	$("#lpLinesTbl").on("blur", ".grossAmount", function() {
		$(this).val(formatDecimalPlaces($(this).val(), 6));
		computeVatAmount(this);
		computeWtax();
	});

	loadTaxTypes(); // load tax type when initializing the table
	resizeTbl("#lpLinesTbl", 8);
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

function loadTaxTypes() {
	$("#lpLinesTbl tbody tr").find(".taxType").each(function() {
		populateTaxTypes(this);
	});
}

function recomputeInvAmount() {
	$("#lpLinesTbl tbody tr").each(function() {
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

function computeFooter() {
	var totalCharges = 0;
	var totalVatAmount = 0;
	var loanAmount = accounting.unformat($("#totalAmount").val());
	$("#lpLinesTbl tbody tr").each(function() {
		totalCharges += accounting.unformat($(this).find(".amount").text());
		totalVatAmount += accounting.unformat($(this).find(".vatAmount").text());
	});
	$("#lpLinesTbl").find("tfoot tr").find(".amount").html(formatDecimalPlaces(totalCharges));
	var subtotal = loanAmount - totalCharges;
	var formattedSubTotal = formatDecimalPlaces(parseFloat(subtotal).toFixed(6));
	$("#subTotal").text(formattedSubTotal);
	var formattedTotalVAT = formatDecimalPlaces(parseFloat(totalVatAmount).toFixed(6));
	$("#totalVat").text(formattedTotalVAT);
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	var formattedWtaxAmount = formatDecimalPlaces(parseFloat(wtaxAmount).toFixed(6));
	$("#computedWTax").text(formattedWtaxAmount);
	var grandTotal = (accounting.unformat(formattedSubTotal) + accounting.unformat(formattedTotalVAT)) - accounting.unformat(formattedWtaxAmount)
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
	$("#totalAmount").val(formatDecimalPlaces(loanAmount));
}

function loadWtaxAcctSettings(wtAcctSettingId) {
	var supplierAcctCompanyId = $("#hdnSupplierAcctCompanyId").val();
	if (supplierAcctCompanyId != "") {
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
}

function computeWtax() {
	computeFooter();
	var wtAcctSettingId = $("#wtaxAcctSettingId").val();
	if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != "undefined") {
		var wtPercentageValue = 0;
		var uri = contextPath + "/getWithholdingTaxAcct/getWtPercentage?wtAccountSettingId="+wtAcctSettingId;
		$.ajax({
			url: uri,
			success : function(wt) {
				wtPercentageValue = (accounting.unformat(wt) / 100);
				var totalCharges = 0;
				$("#lpLinesTbl tbody tr").find(".amount").each(function() {
					totalCharges += accounting.unformat($(this).text());
				});
				var loanAmount = accounting.unformat($("#totalAmount").val());
				var computedWTax = (loanAmount - totalCharges) * wtPercentageValue;
				$("#hdnWtAmount").val(computedWTax);
				$("#computedWTax").text(formatDecimalPlaces(computedWTax));
				computeFooter();
			},
			error : function(error) {
				wtPercentageValue = 0;
			},
			dataType: "text"
		});
	} else {
		$("#hdnWtAmount").val("0.00");
		$("#computedWTax").html("0.00");
		computeFooter();
	}
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeWtax();
}

function showLoanAccounts($txtBox) {
	$("#hdnLoanAcctNumber").val("");//Reset account number
	var divisionId = $("#divisionId").val();
	var companyId = $("#companyId").val();
	var accountName = $($txtBox).val();
	var uri = contextPath+"/getAccounts/byName?accountName="+$.trim(accountName)+"&companyId="+companyId
			+"&divisionId="+divisionId+"&limit=10";
	$($txtBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnLoanAccountId").val(ui.item.id);
			$($txtBox).val(ui.item.accountName);
			$("#hdnLoanAcctNumber").val(ui.item.number);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number+"-"+item.accountName+ "</a>" )
			.appendTo( ul );
	};
}

function getLoanAccount($txtBox) {
	$("#loanAccountErr").text("");
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var acctName = $($txtBox).val();
	var hasCompanyId = companyId != "" && companyId != null && companyId != "0";
	var hasDivisionId = divisionId != "" && divisionId != null && divisionId != "0";
	if(acctName != "") {
		if (hasCompanyId && hasDivisionId) {
			var uri = contextPath+"/getAccounts/byExactName?accountName="+processSearchName(acctName)
			$.ajax({
				url: uri,
				success : function(account) {
					if (account != null && account != undefined) {
						$("#hdnLoanAccountId").val(account.id);
						$($txtBox).val(account.accountName);
						$("#hdnLoanAcctNumber").val(account.number);
					} else {
						$("#hdnLoanAccountId").val("");
						$("#hdnLoanAcctNumber").val("");
						$("#loanAccountErr").text("Invalid account.");
					}
				},
				error : function(error) {
					$("#hdnLoanAccountId").val("");
					$("#hdnLoanAcctNumber").val("");
					$("#loanAccountErr").text("Invalid account.");
				},
				dataType: "json"
			});
		} else {
			$("#hdnLoanAcctNumber").val("");
			$("#hdnLoanAccountId").val("");
			$("#loanAccountErr").text("Invalid account.");
		}
	}
}
</script>
<style type="text/css">
input.numeric {
	width: 172px;
}
</style>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="loanProceeds" id="loanProceedsForm">
			<div class="modFormLabel">Loan Proceeds<span id="spanDivisionLbl"> - ${loanProceeds.division.name}</span>
			<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="ebObjectId"/>
			<form:hidden path="supplierId" id="hdnSupplierId"/>
			<form:hidden path="supplierAccount.companyId" id="hdnSupplierAcctCompanyId"/>
			<form:hidden path="loanAccountId" id="hdnLoanAccountId"/>
			<form:hidden path="lpLinesJson" id="lpLinesJson"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<form:hidden path="loanProceedsTypeId"/>
			<form:hidden path="loanAccount.number" id="hdnLoanAcctNumber"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">LP No.</td>
							<td class="value">
								<form:input path="sequenceNumber" readonly="true" cssClass="textBoxLabel"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<td class="value">
								<c:set var="status" value="${loanProceeds.formWorkflow.currentFormStatus.description}"/>
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
					<legend>Loan Proceeds Header</legend>
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
							<td class="labels">* Lender</td>
							<td class="value">
								<input id=supplierId class="input" onkeydown="showSuppliers();" onblur="getSupplier();"/>
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
							<td class="value"><form:errors path="supplierId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Lender Account</td>
							<td class="value">
								<form:select path="supplierAccountId" id="supplierAccountId" cssClass="frmSelectClass" onchange="filterSupplierAccts();"></form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="supplierAccountId" cssClass="error"/></td>
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
							<td class="labels">* Loan Account</td>
							<td class="value">
								<input id="txtLoanAccount" class="input" onkeydown="showLoanAccounts(this);" 
									onblur="getLoanAccount(this);"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="loanAccountErr" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="loanAccountId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Term</td>
							<td class="value">
								<form:select path="termId" cssClass="frmSelectClass" id="termId" onchange="computeDueDate();">
									<form:options items="${terms}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="termId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="date" id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('date')" style="cursor: pointer"
									style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="date" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* GL Date</td>
							<td class="value">
								<form:input path="glDate" id="glDate" onblur="evalDate('glDate'); computeDueDate();"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('glDate')" style="cursor: pointer"
									style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="glDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Description</td>
							<td class="value">
								<form:textarea path="description" id="description" class="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="description" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Currency</td>
							<td class="value">
								<form:select path="currencyId" id="currencyId" cssClass="frmSelectClass" cssStyle="width: 172px;" onchange="getCurrencyRate(this);">
									<form:options items="${currencies}" itemValue="id" itemLabel="name"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="currencyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Loan Amount</td>
							<td class="value">
								<form:input path="amount" id="totalAmount" class="numeric" 
									cssStyle="width: 172px;" maxLength="13" onblur="computeFooter();"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="amount" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Loan Charge/s</legend>
					<div id="lpLinesTbl"></div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="lPlineMessage" cssClass="error"/></td>
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
						<td colspan="3">Loan Proceeds</td>
						<td></td>
						<td>
							<span id="grandTotal">0.00</span>
						</td>
					</tr>
					<tr>
						<td colspan="5">
							<form:errors path="wtAmount" cssClass="error" />
						</td>
					</tr>
					<tr>
						<td align="right" colspan="5">
							<input type="button" id="btnSaveLoanProceeds" value="Save" onclick="saveInvoice();"/>
						</td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>