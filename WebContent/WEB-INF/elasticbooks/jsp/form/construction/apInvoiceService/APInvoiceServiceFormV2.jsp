<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>

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
$(document).ready(function() {
	var wtAcctSettingId = "";
	if ("${apInvoiceItemDto.apInvoice.id}" == 0) {
		filterSuplierAccounts();
	} else {
		wtAcctSettingId = "${apInvoiceItemDto.apInvoice.wtAcctSettingId}";
		$("#supplierId").val("${apInvoiceItemDto.apInvoice.supplier.name}");
		var dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${apInvoiceItemDto.apInvoice.dueDate}'/>";
		filterSuplierAccounts("${apInvoiceItemDto.apInvoice.supplierAccountId}", "${apInvoiceItemDto.apInvoice.termId}", dueDate);
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${apInvoiceItemDto.apInvoice.amount}' />");
		disableFormFields();
	}

	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>

	initApLinesTbl();
	initializeDocumentsTbl();
	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
});

function disableFormFields() {
	// If form workflow status = COMPLETED
	var invoiceStatus = "${apInvoiceItemDto.apInvoice.formWorkflow.complete}";
	if (invoiceStatus == "true" || "${apInvoiceItemDto.apInvoice.formWorkflow.currentStatusId}" == 4) {
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

function filterSuplierAccounts(selectedSupplierAcctId, termId, dueDate) {
	var supplierName = $.trim($("#supplierId").val());
	if (supplierName != "") {
		var supplierId = $("#hdnSupplierId").val();
		var uri = contextPath+"/getSupplierAccounts?supplierId="+supplierId;
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
						if ("${apInvoiceItemDto.apInvoice.id}" == 0) {
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
		$("#btnSaveAPInvoice").attr("disabled", "disabled");
		$("#apLinesJson").val($apLinesTbl.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		unformatAmount();
		doPostWithCallBack ("invoiceForm", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				var name = $("#invoiceNumber").val();
				updateTable (formStatus);
				if ("${apInvoiceItemDto.apInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = "";
				}
			} else {
				var supplier = $("#supplierId").val();
				var supplierAccountId = $("#supplierAccountId").val();
				var termId = $("#termId").val();
				var dueDate = $("#dueDate").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				if ("${apInvoiceItemDto.apInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				$("#supplierId").val(supplier);
				if (supplierAccountId != "" && supplierAccountId != null
						&& typeof supplierAccountId != "undefined") {
					filterSuplierAccounts(supplierAccountId, termId, dueDate);
				}
				formatAmount();
				initApLinesTbl();
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
	var supplierName = $("#supplierId").val();
	var uri = contextPath + "/getSuppliers/new?name="+processSearchName($.trim(supplierName));
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
		$.ajax({
			url: contextPath + "/getSuppliers/new?name="+processSearchName($.trim(supplierName))+"&isExact=true",
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#hdnSupplierId").val(supplier[0].id);
					$("#supplierId").val(supplier[0].name);
				} else {
					$("#hdnSupplierId").val("");
					$("#supplierId").val("");
				}
				filterSuplierAccounts();
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
	var companyId = $tr.find(".companyId").val();
	var divisionNumber = $($txtBox).val();
	var uri = contextPath+"/getDivisions/new?companyId="+companyId+"&divisionNumber="+divisionNumber
			+"&isActive=true&limit=10";
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
	var companyId = $tr.find(".companyId").val();
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
	var companyId = $tr.find(".companyId").val();
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
	var companyId = $tr.find(".companyId").val();
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
	var companyName = $tr.find(".companyName").val();
	var divisionName = $tr.find(".divisionName").val();
	var accountName = $tr.find(".accountName").val();
	$tr.find(".acctCombinationName").text(companyName + " - " + divisionName +  " - " + accountName);
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
	// 14680064 = 10485760(10 MB) + (10485760 ?? .4)
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
			{"title" : "Company",
				"cls" : "companyNumber tblInputText",
				"editor" : "text",
				"visible" : true ,
				"width" : "10%"},
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

	$("#apLinesTbl").on("focus", ".companyNumber", function() {
		loadTaxTypes();
		setDefaultValue(this);
	});

	$("#apLinesTbl").on("click", ".taxType", function() {
		loadTaxTypes();
	});

	$("#apLinesTbl").on("keydown", ".companyNumber", function() {
		showCompanies(this);
	});

	$("#apLinesTbl").on("blur", ".companyNumber", function() {
		getCompany(this);
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

	$("#apLinesTbl").on("change", ".taxType", function() {
		$(this).closest("tr").find(".taxTypeId").val($(this).val());
		computeVatAmount($(this).closest("tr").find(".grossAmount"));
		computeWtax();
	});

	$("#apLinesTbl").on("focus", ".grossAmount", function() {
		var amount = $(this).val();
		if (amount != "") {
			$(this).val(accounting.unformat(amount));
		}
	});

	$("#apLinesTbl").on("blur", ".grossAmount", function() {
		$(this).val(formatDecimalPlaces($(this).val(), 4));
		computeVatAmount(this);
		computeWtax();
	});

	loadTaxTypes(); // load tax type when initializing the table
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

function showCompanies($txtBox) {
	var supplierId = $("#hdnSupplierId").val();
	var companyName = $.trim($($txtBox).val());
	var $companyId = $($txtBox).closest("tr").find(".companyId");
	var uri = contextPath + "/getSuppliers/getCompanies?supplierId="+supplierId+"&companyName="+companyName;
	$($txtBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$companyId.val(ui.item.id);
			$(this).val(ui.item.companyNumber);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.companyNumber+"-"+item.name+ "</a>" )
			.appendTo( ul );
	};
}

function getCompany($txtBox) {
	var $tr = $($txtBox).closest("tr");
	var companyNumber = $($txtBox).val();
	var uri = contextPath+"/getCompany?companyNumber="+companyNumber;
	if (companyNumber != null && $.trim(companyNumber) != "" && $.trim(companyNumber) != "0") {
		$.ajax({
			url: uri,
			success : function(company) {
				if (company != null && company != undefined) {
					$("#acctCombinationErrorMsg").text("");
					$tr.find(".companyId").val(company.id);
					$tr.find(".companyName").val(company.name);
					setAccountCombination($tr);
				}
			},
			error : function(error) {
				$($txtBox).val("");
				$tr.find(".companyId").val("");
				$tr.find(".companyName").val("");
				$tr.find(".divisionName").val("");
				$tr.find(".accountName").val("");
				setAccountCombination($tr);
				$("#acctCombinationErrorMsg").text("Invalid company.");
				$($txtBox).focus();
			},
			dataType: "json"
		});
	} else {
		$($txtBox).val("");
		$tr.find(".companyId").val("");
		$tr.find(".divisionNumber").val("");
		$tr.find(".divisionId").val("");
		$tr.find(".accountNumber").val("");
		$tr.find(".accountId").val("");
	}
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

function computeTotalAmount() {
	var totalAmount = 0;
	var totalVatAmount = 0;
	$("#apLinesTbl tbody tr").each(function() {
		totalAmount += accounting.unformat($(this).find(".amount").text());
		totalVatAmount += accounting.unformat($(this).find(".vatAmount").text());
	});
	$("#apLinesTbl").find("tfoot tr").find(".amount").html(formatDecimalPlaces(totalAmount));
	$("#subTotal").text(formatDecimalPlaces(totalAmount));
	$("#totalVat").text(formatDecimalPlaces(totalVatAmount));
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	$("#computedWTax").text(formatDecimalPlaces(wtaxAmount));
	var grandTotal = (totalAmount + totalVatAmount) - wtaxAmount
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function loadWtaxAcctSettings(wtAcctSettingId) {
	var supplierAcctCompanyId = $("#hdnSupplierAcctCompanyId").val();
	if (supplierAcctCompanyId != "") {
		var $select = $("#wtaxAcctSettingId");
		$select.empty();
		var uri = contextPath+"/getWithholdingTaxAcct?companyId="+supplierAcctCompanyId+"&isCreditable=false";
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
				var totalAmount = 0;
				$("#apLinesTbl tbody tr").find(".amount").each(function() {
					totalAmount += accounting.unformat($(this).text());
				});
				var computedWTax = totalAmount * wtPercentageValue;
				$("#hdnWtAmount").val(computedWTax);
				$("#computedWTax").text(formatDecimalPlaces(computedWTax));
				computeTotalAmount();
			},
			error : function(error) {
				wtPercentageValue = 0;
			},
			dataType: "text"
		});
	} else {
		$("#hdnWtAmount").val("0.00");
		$("#computedWTax").html("0.00");
		computeTotalAmount();
	}
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeWtax();
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
		<form:form method="POST" commandName="apInvoiceItemDto" id="invoiceForm">
			<div class="modFormLabel">AP Invoice - Service<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="apInvoice.id"/>
			<form:hidden path="apInvoice.createdBy"/>
			<form:hidden path="apInvoice.createdDate"/>
			<form:hidden path="apInvoice.formWorkflowId" />
			<form:hidden path="apInvoice.ebObjectId"/>
			<form:hidden path="apInvoice.sequenceNumber"/>
			<form:hidden path="apInvoice.invoiceTypeId" id="invoiceTypeId"/>
			<form:hidden path="apInvoice.supplierId" id="hdnSupplierId"/>
			<form:hidden path="apInvoice.supplierAccount.companyId" id="hdnSupplierAcctCompanyId"/>
			<form:hidden path="apInvoice.apLinesJson" id="apLinesJson"/>
			<form:hidden path="apInvoice.referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="apInvoice.wtAmount" id="hdnWtAmount"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number</td>
							<td class="value">
								<input type="text" id="seqNumber" class="textBoxLabel" readonly="readonly"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<td class="value">
								<c:set var="status" value="${apInvoiceItemDto.apInvoice.formWorkflow.currentFormStatus.description}"/>
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
					<legend>Invoice Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* SOA No</td>
							<td class="value"><form:input path="apInvoice.invoiceNumber" id="invoiceNumber" class="input" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="apInvoice.invoiceNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Supplier Name</td>
							<td class="value">
								<input id=supplierId class="input" onkeydown="showSuppliers();" onblur="getSupplier();"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="apInvoice.supplierId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Supplier's Account</td>
							<td class="value">
								<form:select path="apInvoice.supplierAccountId" id="supplierAccountId" cssClass="frmSelectClass" onchange="filterSuplierAccounts();"></form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="apInvoice.supplierAccountId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Term</td>
							<td class="value">
								<form:select path="apInvoice.termId" cssClass="frmSelectClass" id="termId" onchange="computeDueDate();">
									<form:options items="${terms}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="apInvoice.termId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Invoice Date</td>
							<td class="value">
								<form:input path="apInvoice.invoiceDate" id="invoiceDate" onblur="evalDate('invoiceDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('invoiceDate')" style="cursor: pointer"
									style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="apInvoice.invoiceDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* GL Date</td>
							<td class="value">
								<form:input path="apInvoice.glDate" id="glDate" onblur="evalDate('glDate'); computeDueDate();"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('glDate')" style="cursor: pointer"
									style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="apInvoice.glDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Due Date</td>
							<td class="value">
								<form:input path="apInvoice.dueDate" id="dueDate" onblur="evalDate('dueDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate3" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('dueDate')"
									style="cursor: pointer" style="float: right;"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="apInvoice.dueDate" cssClass="error"/></td>
						</tr>

						<tr>
							<td class="labels">* Description</td>
							<td class="value">
								<form:textarea path="apInvoice.description" id="description" class="input"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="apInvoice.description" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Amount</td>
							<td class="value">
								<form:input path="apInvoice.amount" id="totalAmount" class="numeric" size="20" maxLength="13"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="apInvoice.amount" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AP Lines</legend>
					<div id="apLinesTbl"></div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="apInvoice.aPlineMessage" cssClass="error"/></td>
					</tr>
					<tr>
						<td><span id="acctCombinationErrorMsg" class="error"></span></td>
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
							<form:select path="apInvoice.wtAcctSettingId" id="wtaxAcctSettingId" cssClass="frmSmallSelectClass"
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
							<form:errors path="apInvoice.wtAmount" cssClass="error" />
						</td>
					</tr>
				</table>
				<br>
				<fieldset class="frmField_set">
					<legend>Document</legend>
					<div id="documentsTable"></div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><span class="error" id="spDocSizeMsg"></span></td>
					</tr>
					<tr>
						<td><form:errors path="apInvoice.referenceDocsMessage" cssClass="error" /></td>
					</tr>
					<tr>
						<td><span class="error" id="referenceDocsMgs"></span></td>
					</tr>
					<tr>
						<td align="right" colspan="4">
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