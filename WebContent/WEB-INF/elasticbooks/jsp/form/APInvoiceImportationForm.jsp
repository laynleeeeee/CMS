<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : Accounts payable invoice form jsp page -->
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
$(document).ready(function() {
	loadDivision("${aPInvoice.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	var wtAcctSettingId = "";
	if ("${pId}" == 0) {
		filterSupplierAccts();
	} else {
		wtAcctSettingId = "${aPInvoice.wtAcctSettingId}";
		$("#supplierId").val("${aPInvoice.supplier.name}");
		var dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${aPInvoice.dueDate}'/>";
		filterSupplierAccts("${aPInvoice.supplierAccountId}", "${aPInvoice.termId}", dueDate);
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${aPInvoice.amount}' />");
		disableFormFields();
		$("#currencyId").attr("disabled","disabled");
	}
	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
	initApLinesTbl();
	initializeDocumentsTbl();
	recomputeInvAmount();
	formatDoubles();
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

	$("#apLinesTbl tbody tr").each(function(row) {
		var vatAmount = accounting.unformat($(this).find(".vatAmount").val());
		vatAmount = (vatAmount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
		$(this).find(".vatAmount").val(formatDecimalPlaces((vatAmount / rate)));

		recomputeInvAmount();
	});

	var totalAmount = accounting.unformat($("#totalAmount").val());
	totalAmount = (totalAmount * (PREV_RATE != 0 ? PREV_RATE : 1.0));
	$("#totalAmount").val(formatDecimalPlaces((totalAmount / rate)));

	PREV_RATE = rate;
}

function disableFormFields() {
	// If form workflow status = COMPLETED
	var invoiceStatus = "${aPInvoice.formWorkflow.complete}";
	if (invoiceStatus == "true" || "${aPInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#invoiceForm :input").attr("disabled", "disabled");
	}
}

function setTerm(termId, dueDate) {
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
				$("#termId").val(term.id).attr("selected", true);
				if (dueDate == null) {
					computeDueDate();
				}
			},
			error : function (error) {
				console.log(error);
			},
			dataType: "json",
		});
	}
}

function filterSupplierAccts(selectedSupplierAcctId, termId, dueDate) {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var supplierName = $.trim($("#supplierId").val());
	$("#supplierAccountId").empty();
	if (supplierName != "") {
		var supplierId = $("#hdnSupplierId").val();
		var uri = contextPath+"/getApSupplierAccts?supplierId="+supplierId
					+"&companyId="+companyId+"&divisionId="+divisionId;
		if(typeof selectedSupplierAcctId != "undefined"){
			uri += "&supplierAccountId="+selectedSupplierAcctId;
		}
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
					var hasValue = selectedSupplierAcctId != "" && selectedSupplierAcctId != null && typeof selectedSupplierAcctId != "undefined";
					var currSupplierAcctId = hasValue ? selectedSupplierAcctId : $("#supplierAccountId").val();
					if (id == currSupplierAcctId) {
						if ("${aPInvoice.id}" == 0) {
							$("#termId").val(rowObject["termId"]).attr('selected',true);
						}
						$("#hdnSupplierAcctCompanyId").val(rowObject["companyId"]);
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
				setTerm(termId, dueDate);
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
		var vatAmount = $tr.find(".vatAmount").val();
		$tr.find(".vatAmount").val(vatAmount != "" ? vatAmount : formatDecimalPlaces(0.0));
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
		$("#btnSaveAPInvoice").attr("disabled", "disabled");
		$("#apLinesJson").val($apLinesTbl.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#divisionId").removeAttr("disabled");
		$("#currencyId").removeAttr("disabled");
		unformatAmount();
		parseDoubles();
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
				var supplier = $("#supplierId").val();
				var supplierAccountId = $("#supplierAccountId").val();
				var termId = $("#termId").val();
				var dueDate = $("#dueDate").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				var currentStatus = $("#txtInvoiceStatus").val();
				if ("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#txtInvoiceStatus").val(currentStatus);
				}
				loadDivision("${aPInvoice.divisionId}");
				$("#divisionId").attr("disabled","disabled");
				if("${aPInvoice.id}" != 0){
					$("#currencyId").attr("disabled","disabled");
				}
				$("#supplierId").val(supplier);
				if (supplierAccountId != "" && supplierAccountId != null
						&& typeof supplierAccountId != "undefined") {
					filterSupplierAccts(supplierAccountId, termId, dueDate);
				}
				formatAmount();
				formatDoubles();
				initApLinesTbl();
				initializeDocumentsTbl();
				recomputeInvAmount();
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
		$("#dueDate").val("");
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
	$("#dueDate").val(invDueDate);
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
					$("#supplierAccountId").empty();
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
	var divisionId = $tr.find(".divisionId").val();
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
			{"title" : "grossAmount",
				"cls" : "grossAmount",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "amount",
				"cls" : "amount",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Division",
				"cls" : "divisionNumber tblInputText",
				"editor" : "text",
				"visible" : true ,
				"width" : "15%"},
			{"title" : "Account",
				"cls" : "accountNumber tblInputText",
				"editor" : "text",
				"visible" : true ,
				"width" : "15%"},
			{"title" : "Combination",
				"cls" : "acctCombinationName tblLabelText",
				"editor" : "label",
				"visible" : true ,
				"width" : "20%"},
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "15%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "15%"},
			{"title" : "Description", 
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "15%"}
		],
	});

	$("#apLinesTbl").on("click", ".taxType", function() {
		loadTaxTypes();
	});

	$("#apLinesTbl").on("focus", ".divisionNumber", function() {
		loadTaxTypes();
		setDefaultValue(this);
	});

	$("#apLinesTbl").on("keydown", ".divisionNumber", function() {
		showDivisions(this);
	});

	$("#apLinesTbl").on("blur", ".divisionNumber", function() {
		getDivision(this);
	});

	$("#apLinesTbl").on("keydown", ".accountNumber", function() {
		showAccounts(this);
	});

	$("#apLinesTbl").on("blur", ".accountNumber", function() {
		getAccount(this);
	});

	$("#apLinesTbl").on("focus", ".vatAmount", function() {
		var amount = $(this).val();
		if (amount != "") {
			$(this).val(accounting.unformat(amount));
		}
	});

	$("#apLinesTbl").on("change", ".vatAmount", function() {
		computeTotalAmount();
	});

	$("#apLinesTbl").on("blur", ".vatAmount", function() {
		computeTotalAmount();
	});

	loadTaxTypes(); // load tax type when initializing the table
}

function loadTaxTypes() {
	$("#apLinesTbl tbody tr").find(".taxType").each(function() {
		populateTaxTypes(this);
	});
}

function recomputeInvAmount() {
	$("#apLinesTbl tbody tr").each(function() {
		computeTotalAmount();
	});
}

function populateTaxTypes($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var taxTypeId = $tr.find(".taxTypeId").val();
	var uri = contextPath + "/getTaxTypes/new?isReceivable=false"+"&isImportation=true";
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

function computeTotalAmount() {
	var totalVatAmount = 0;
	$("#apLinesTbl tbody tr").each(function() {
		totalVatAmount += accounting.unformat($(this).find(".vatAmount").val());
	});
	$("#grandTotal").html(formatDecimalPlaces(totalVatAmount));
}

function formatDouble(elem) {
	var headerAmount = accounting.unformat($(elem).val());
	$(elem).val(formatDecimalPlaces(Number(headerAmount)));
	headerAmount = null;
}

function parseDouble(element) {
	var headerAmount = $(element).val();
	if (headerAmount == "") {
		$(element).val("0.00");
	} else {
		$(element).val(Number(accounting.unformat(headerAmount)));
	}
	headerAmount = null;
}

function formatDoubles() {
	formatDouble("#dutiableValueTxt");
	formatDouble("#chargesFromCustomTxt");
	formatDouble("#taxableImportTxt");
	formatDouble("#exemptImportTxt");
	formatDouble("#totalLandedCostTxt");
}

function parseDoubles() {
	parseDouble("#dutiableValueTxt");
	parseDouble("#chargesFromCustomTxt");
	parseDouble("#taxableImportTxt");
	parseDouble("#exemptImportTxt");
	parseDouble("#totalLandedCostTxt");
}
</script>
<style type="text/css">
input.numeric {
	width: 172px;
}
</style>
</head>
<body>
	<div id="divComboList" style="display: none"></div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="aPInvoice" id="invoiceForm">
			<div class="modFormLabel">${title}<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="ebObjectId"/>
			<form:hidden path="invoiceImportationDetails.id"/>
			<form:hidden path="supplierId" id="hdnSupplierId"/>
			<form:hidden path="supplierAccount.companyId" id="hdnSupplierAcctCompanyId"/>
			<form:hidden path="apLinesJson" id="apLinesJson"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<form:hidden path="invoiceTypeId"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">API-I No.</td>
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
					<legend>AP Invoice - Importation Header</legend>
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
							<td class="labels">* SI No. / SOA Ref No.</td>
							<td class="value"><form:input path="invoiceNumber" class="input" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="invoiceNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">BMS No.</td>
							<td class="value"><form:input path="bmsNumber" class="input" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="bmsNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Supplier</td>
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
							<td class="labels">* Supplier Account</td>
							<td class="value">
								<form:select path="supplierAccountId" id="supplierAccountId" cssClass="frmSelectClass"
									onchange="filterSupplierAccts(this.value);"></form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="supplierAccountId" cssClass="error"/></td>
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
							<td class="labels">* Invoice Date</td>
							<td class="value">
								<form:input path="invoiceDate" id="invoiceDate" onblur="evalDate('invoiceDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('invoiceDate')" style="cursor: pointer"
									style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="invoiceDate" cssClass="error"/></td>
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
							<td class="labels">* Due Date</td>
							<td class="value">
								<form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate3" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('dueDate')"
									style="cursor: pointer" style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="dueDate" cssClass="error"/></td>
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
							<td class="labels">* Amount</td>
							<td class="value">
								<form:input path="amount" id="totalAmount" class="numeric" onblur="formatDouble(this);" cssStyle="width: 172px;" maxLength="13"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="amount" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Importation Details</legend>
					<table>
						<td>
							<table>
								<tr>
									<td class="labels">Import Entry No.</td>
									<td class="value"><form:input path="invoiceImportationDetails.importEntryNo" class="input" /></td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.importEntryNo" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">Assessment Release Date</td>
									<td class="value">
										<form:input path="invoiceImportationDetails.assessmentReleaseDate" id="assessmentReleaseDate" onblur="evalDate('assessmentReleaseDate'); computeDueDate();"
											style="width: 120px;" class="dateClass2" />
										<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
											onclick="javascript:NewCssCal('assessmentReleaseDate')" style="cursor: pointer"
											style="float: right;"/>
									</td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.assessmentReleaseDate" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">Registered Name</td>
									<td class="value"><form:input path="invoiceImportationDetails.registeredName" class="input" /></td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.registeredName" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">Importation Date</td>
									<td class="value">
										<form:input path="invoiceImportationDetails.importationDate" id="importationDate" onblur="evalDate('importationDate'); computeDueDate();"
											style="width: 120px;" class="dateClass2" />
										<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
											onclick="javascript:NewCssCal('importationDate')" style="cursor: pointer"
											style="float: right;"/>
									</td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.importationDate" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">Country of Origin</td>
									<td class="value"><form:input path="invoiceImportationDetails.countryOfOrigin" class="input" /></td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.countryOfOrigin" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">Total Landed Cost</td>
									<td class="value"><form:input path="invoiceImportationDetails.totalLandedCost" class="numeric" id="totalLandedCostTxt" onblur="formatDouble(this);" cssStyle="width: 350px;"/></td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.totalLandedCost" cssClass="error"/></td>
								</tr>
							</table>
						</td>
						<td>
							<table>
								<tr>
									<td class="labels">Dutiable Value</td>
									<td class="value"><form:input path="invoiceImportationDetails.dutiableValue" class="numeric" id="dutiableValueTxt" onblur="formatDouble(this);" cssStyle="width: 350px;"/></td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.dutiableValue" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">Charges Before Release from Custom</td>
									<td class="value"><form:input path="invoiceImportationDetails.chargesFromCustom" class="numeric" id="chargesFromCustomTxt" onblur="formatDouble(this);" cssStyle="width: 350px;"/></td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.chargesFromCustom" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">Taxable Import</td>
									<td class="value"><form:input path="invoiceImportationDetails.taxableImport" class="numeric" id="taxableImportTxt" onblur="formatDouble(this);" cssStyle="width: 350px;"/></td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.taxableImport" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">Exempt Import</td>
									<td class="value"><form:input path="invoiceImportationDetails.exemptImport" class="numeric" id="exemptImportTxt" onblur="formatDouble(this);" cssStyle="width: 350px;"/></td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.exemptImport" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">OR No.</td>
									<td class="value"><form:input path="invoiceImportationDetails.orNumber" class="input" /></td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.orNumber" cssClass="error"/></td>
								</tr>
								<tr>
									<td class="labels">Date of Payment</td>
									<td class="value">
										<form:input path="invoiceImportationDetails.paymentDate" id="paymentDate" onblur="evalDate('paymentDate'); computeDueDate();"
											style="width: 120px;" class="dateClass2" />
										<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
											onclick="javascript:NewCssCal('paymentDate')" style="cursor: pointer"
											style="float: right;"/>
									</td>
								</tr>
								<tr>
									<td class="labels"></td>
									<td class="value"><form:errors path="invoiceImportationDetails.paymentDate" cssClass="error"/></td>
								</tr>
							</table>
						</td>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AP Lines</legend>
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
						<td style="width: 22%;"></td>
						<td style="width: 22%;"></td>
						<td style="width: 12%;"></td>
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
							<form:errors path="wtAmount" cssClass="error" />
						</td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr></tr>
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