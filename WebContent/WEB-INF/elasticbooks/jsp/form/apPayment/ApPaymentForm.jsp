<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description: Accounts payable payment form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebForm.css"media="all">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"media="all">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
#apInvoice {
	cellspacing: 0;
	border: none;
}

#apInvoice thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#apInvoice tbody td {
	border-top: 1px solid #000000;
}

.textBoxLabel,.txtInvoiceNumber,.txtAmount {
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}

.tdProperties {
	border-right: 1px solid #000000;
}

.divVeil {
	background-color: #F2F1F0;
}

input.numeric {
	width: 150px;
}
</style>
<script type="text/javascript">
var minimumRows = 4;
var currentInvoiceNo = "";
var apInvoiceCurrentIndex = "${fn:length(apPayment.apInvoices)}";
var selectedSupplierAccountValue = 0;
var selectApInvoiceRow = 0;
var paymentStatusId = parseInt("${apPayment.formWorkflow.currentFormStatus.id}");
var noValue = 0;
var aPinvoices = null;
var isShowingInvoices = false;
var NOT_SAVED = 0;
var ISSUED = 10;
var NEGOTIABLE = 15;
var CLEARED = 16;
var CANCELLED = 4;
var checkbookTemplateId = null;
var PREV_RATE = 0;
var apPaymentLineTable = null;
var CLEARED_STATUS_ID = 16;
$(document).ready(function() {
	aPinvoices = new Array ();
	$("#payee").attr("disabled", "disabled");
	initDocumentsTbl();
	initApPaymentLineTbl(JSON.parse($("#apPaymentLineDtosJson").val()));
	if("${pId}" > 0){
		$("#bankAccountId").val("${apPayment.bankAccount.name}");
		$("#checkBookId").val("${apPayment.checkbook.name}");
		if("${apPayment.checkbook.checkbookTemplateId}" != null && "${apPayment.checkbook.checkbookTemplateId}" != 0){
			checkbookTemplateId = "${apPayment.checkbook.checkbookTemplateId}";
		}
		$("#checkNumber").val("${apPayment.checkNumber}");
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${apPayment.amount}' />");
		$("#supplierId").val("${apPayment.supplier.name}");
		filterSupplierAccounts("${apPayment.supplierAccountId}");
		//only enable editing when current status is cleared.
		if(paymentStatusId != CLEARED_STATUS_ID) {
			$("#btnSaveApPayment").attr("disabled", "disabled");
		}
		disableFields();
		$("#specifyPayee").attr("disabled", "disabled");
	} else {
		showCheckBooks();
	}
	//Display Voucher Number
	$("#txtVoucherNo").val("${voucherNo}");
		
	// Delete rows
	 $("tr .imgDelete").click(function () {
		 if("${pId}" > 0) {
			 alert("Cannot delete AP invoices!");
		 }
	});
});

function initDocumentsTbl() {
	var referenceDocumentJson = JSON.parse($("#referenceDocumentJson").val());
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

function enableDisablePayee () {
	if(specifyPayee.checked == true){
		$("#payee").removeAttr("disabled");
	} else {
		$("#payee").val("");
		$("#payee").attr("disabled", "disabled");
	}
}

function formatMoney (textbox) {
	var money = formatDecimalPlaces($(textbox).val());
	$(textbox).val(money.replace(/[$]/gi, ''));
}

function formatMonetaryVal() {
	formatMoney($("#totalAmount"));
	
	$("#apInvoice tbody tr").each(function(i) {
		formatMoney($(this).find(".txtAmount"));
    });
}

function disableFields() {
	$("#cbEdit").attr("disabled", "disabled");
	$("#specifyPayee").attr("disabled", "disabled");
	$("#paymentDate").attr("disabled", "disabled");
	$("#supplierId").attr("disabled", "disabled");
	$("#supplierAccountId").attr("disabled", "disabled");
	$("#bankAccountId").attr("disabled", "disabled");
	$("#checkBookId").attr("disabled", "disabled");
	$("#checkNumber").attr("disabled", "disabled");
	$("#imgDate2").hide();
	$("#checkDate").attr("disabled", "disabled");
	$("#totalAmount").attr("disabled", "disabled");
	$("#remarks").attr("disabled", "disabled");
	$("#currencyId").attr("disabled", "disabled");
	$(".referenceNumber").attr("disabled", "disabled");
	$(".amount").attr("disabled", "disabled");
}

function enableFields() {
	$("#specifyPayee").removeAttr("disabled");
	$("#payee").removeAttr("disabled");
	$("#paymentDate").removeAttr("disabled");
	$("#supplierId").removeAttr("disabled");
	$("#supplierAccountId").removeAttr("disabled");
	$("#bankAccountId").removeAttr("disabled");
	$("#checkBookId").removeAttr("disabled");
	$("#checkNumber").removeAttr("disabled");
	$("#checkDate").removeAttr("disabled");
	$("#totalAmount").removeAttr("disabled");
	$("#remarks").removeAttr("disabled");
	$("#currencyId").removeAttr("disabled");
	$(".referenceNumber").removeAttr("disabled");
	$(".amount").removeAttr("disabled");
}

$(function () {
	$(".imgCloseComboList").live("click", function() {
		$("#divInvoiceList").css("display", "none");
		isShowingInvoices = false;
	});

	$("#totalAmount").live("blur", function (e) {
		$("#totalAmount").val(formatDecimalPlaces($("#totalAmount").val()));
	});

	$("#checkNumber").bind("keyup keydown", function() {
		inputOnlyNumeric("checkNumber");
	});
});

function filterSupplierAccounts(supplierAcctId) {
	if($.trim($("#supplierId").val()) == "") {
		$("#supplierAccountId").empty();
		initApPaymentLineTbl();
	} else {
		var supplierId = $("#hdnSupplierId").val();
		var bankAccountId = $("#hdnBankAccountId").val();
		var divisionId =  $("#hdnDivisionId").val();
		var uri = contextPath+"/getSupplierAccounts?supplierId="+supplierId+"&divisionId="+divisionId;
		if(typeof supplierAcctId != "undefined") {
			uri += "&supplierAccountId="+supplierAcctId;
		}
		$("#supplierAccountId").empty();
		var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["numberAndName"];
			}
		};
		loadPopulate (uri, false, supplierAcctId, "supplierAccountId", optionParser, null);
	}
}

var isSaving = false;
function saveApPayment() {
	if ($.trim($("#supplierId").val()) == "") {
		$("#hdnSupplierId").val("");
	}
	if(isSaving == false) {
		isSaving = true;
		$("#btnSaveApPayment").attr("disabled", "disabled");
		$("#apPaymentLineDtosJson").val($apPaymentLineTable.getData());
		$("#referenceDocumentJson").val($documentsTable.getData());
		enableFields();//Enable fields before saving.
		$("#totalAmount").val(accounting.unformat($("#totalAmount").val()));//Remove formatting.
		doPostWithCallBack ("apPaymentDiv", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				var checkNo = $("#checkNumber").val();
				var selectedInvoices = "";
				var rowCount = $('#apInvoice tbody tr').length;
				if (paymentStatusId != 1) {
					formStatus.message = "Check "+checkNo+" has been successfully paid to Invoice/s: "+selectedInvoices;
				} else {
					formStatus.message = "Check "+checkNo+" has been cancelled";
				}
				updateTable (formStatus);
				if(checkbookTemplateId != null && checkbookTemplateId != 0){
					var supplierName = encodeURIComponent(specifyPayee.checked ? $.trim($("#payee").val()) : 
						$.trim($("#supplierId").val()));
					var currencyId = $("#currencyId").val();
					var isOk = confirm("Do you want to print check?\nPress ok to continue");
					if (isOk) {
						window.open(contextPath + "/checkPrinting/new?checkbookTemplateId="
								+checkbookTemplateId+"&name="+supplierName+"&amount="+$("#totalAmount").val()
								+"&currencyId="+currencyId+"&checkDate="+$("#checkDate").val());
					}
				}
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var checkNo = $("#checkNumber").val();
				var bankAccountId = $("#bankAccountId").val();
				var checkbookId = $("#checkBookId").val();
				var supplier = $("#supplierId").val();
				var supplierAccount = $("#supplierAccountId").val();
				var currencyId = $("#currencyId").val();
				var rate = $("#hdnCurrRateValue").val();
				if("${apPayment.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					updatePopupCss();
					disableFields();//Disable form fields
				}
				enableDisablePayee();
				$("#spanDivisionLbl").text(spanDivisionLbl);
				$("#bankAccountId").val(bankAccountId);
				$("#checkBookId").val(checkbookId);
				$("#checkNumber").val(checkNo);
				$("#supplierId").val(supplier);
				$("#supplierAccountId").val(supplierAccount);
				filterSupplierAccounts(supplierAccount);
				formatMonetaryVal();
				initDocumentsTbl();
				convertCurrencies(currencyId, rate);
				initApPaymentLineTbl(JSON.parse($("#apPaymentLineDtosJson").val()));
				$("#supplierId").val(supplier);
				isSaving = false;
			}
		});
	}
}

function showInvoices(searchImage, columnIndexNumber) {
	isShowingInvoices = true;
	var position = $(searchImage).closest("tr").find(".search").position();
	var offset = $(searchImage).closest("tr").find(".search").offset();
	selectApInvoiceRow = columnIndexNumber;
	var supplierAccountId = $("#supplierAccountId").val();
	if(supplierAccountId != null) {
		var path = contextPath + "/apPayment/loadInvoices?supplierAccountId="+supplierAccountId+"&divisionId="+"${apPayment.divisionId}"
			+"&ebObjectIds="+getInvoiceIds();
		$("#divInvoiceList").load(path);
		if (!$("#divInvoiceList").is(":visible")) {
			$("#divInvoiceList").css("background-color", "#FFFFFF")
			.css("border", "1px solid #999999")
			.css("cursor", "default")
			.css("position","absolute")
			.css("left", position.left+"px")
			.css("top", position.top+"px")
			.css("padding", "25px 25px 20px")
			.show();
		}
	} else {
		alertNoSupplierAcct();
	}
}

function alertNoSupplierAcct() {
	alert("No supplier account selected.");
}

function checkAndSetDecimal(txtObj) {
	var amount = $(txtObj).val();
    var amountLength = amount.length;
    var cntDec = 0;
    var lengthFirstDec = 0;
    var finalAmount = "";

    for (var i=0; i<amountLength; i++) {
        var char = amount.charAt(i);
        if (!isNaN(char) || (char == ".") || (char == "-")) {
			if (char == "-" && i==0)
				finalAmount += char;
            if (char == ".")
                cntDec++;
            if ((char == ".") && (cntDec == 1)) {
                lengthFirstDec = i + 1;
                finalAmount += char;
            }
            if (!isNaN(char))
                finalAmount += char;
        }
    }
    if ((cntDec == 1) && (amountLength > (lengthFirstDec + 2)))
        finalAmount = roundTo2Decimal(finalAmount);
    $(txtObj).val(finalAmount);
}

function showSuppliers () {
	var supplierName = encodeURIComponent($.trim($("#supplierId").val()));
	var divisionId = $("#hdnDivisionId").val();
	if($("#supplierId").val() != "") {
		var uri = contextPath + "/getSuppliers/new?name="+supplierName+"&divisionId="+divisionId;
		$("#supplierId").autocomplete({
			source: uri,
			select: function( event, ui ) {
				$("#hdnSupplierId").val(ui.item.id);
				$("#spanSupplierError").text("");
				$(this).val(ui.item.name);
				filterSupplierAccounts();
				return false;
			}, minLength: 2,
			change: function(event, ui){
				$.ajax({
					url: uri,
					success : function(item) {
						$("#spanSupplierError").text("");
						if (ui.item != null && ui.item != undefined) {
							$("#hdnSupplierId").val(ui.item.id);
							$(this).val(ui.item.name);
							initApPaymentLineTbl();
						}
					},
					error : function(error) {
						$("#spanSupplierError").text("Please select supplier.");
						$("#supplierId").val("");
					},
					dataType: "json",
				});
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>" +item.name + "</a>" )
				.appendTo( ul );
		};
	};
}

function getSupplier() {
	var supplierName = encodeURIComponent($.trim($("#supplierId").val()));
	$.ajax({
		url: contextPath + "/getSuppliers/new?name="+supplierName+"&isExact=true",
		success : function(supplier) {
			if (supplier != null && supplier[0] != undefined) {
				$("#hdnSupplierId").val(supplier[0].id);
				$("#supplierId").val(supplier[0].name);
			}else {
				$("#supplierId").val("");
				clearValues();
			}
			filterSupplierAccounts();
		},
		error : function(error) {
			$("#supplierId").val("");
			$("#hndSupplierId").val("");
			$("#supplierAccountId").empty();
		},
		dataType: "json"
	});
}

function clearValues() {
	$("#hdnCheckBookId").val("");
	$("#checkBookId").val("");
	$("#checkNumber").val("");
	$("#supplierId").val("");
	$("#supplierAccountId").empty();
	$("#apInvoice tbody").empty();
	$("#bankAccountId").val("");
	initApPaymentLineTbl();
}

function showBankAccounts() {
	var bankAcctName = $.trim($("#bankAccountId").val());
	var supplierAccountId = $("#supplierAccountId").val();
	var divisionId = $("#hdnDivisionId").val();
	var uri = contextPath + "/getBankAccounts/bySupplierAcc?name="+bankAcctName+"&limit=10&supplierAccountId="+supplierAccountId
			+"&divisionId="+divisionId;
	$("#bankAccountId").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnBankAccountId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null && ui.item != undefined) {
						$("#hdnBankAccountId").val(ui.item.id);
						$(this).val(ui.item.name);
						$("#spanBankAcct").text("");
						$("#spanCheckBook").text("");
					} else {
						$("#hdnBankAccountId").val("");
					}
				},
				dataType: "json",
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function getBankAccount() {
	var divisionId = $("#hdnDivisionId").val();
	var bankAcctName = $("#bankAccountId").val();
	var uri = contextPath + "/getBankAccounts/byName?name="+encodeURIComponent(bankAcctName)+"&divisionId="+divisionId;
	if(bankAcctName != "") {
		$.ajax({
			url: uri,
			success: function (item) {
				if (item != null && item != undefined) {
					$("#hdnBankAccountId").val(item.id);
					$("#bankAccountId").val(item.name);
				}
			},
			error : function(error) {
				$("#spanBankAcct").text("Invalid Bank Account.");
				$("#hdnBankAccountId").val("");
			},
			dataType: "json"
		});
	}
}

function showCheckBooks() {
	var bankAccount = $("#hdnBankAccountId").val();
	var checkBookName = processSearchName($.trim($("#checkBookId").val()));
	var uri =  contextPath + "/apPayment/getCheckbooks?bankAccountId="+encodeURIComponent(bankAccount)
			+"&checkBookName="+checkBookName;
	if(bankAccount != "") {
		$("#spanCheckBook").text("");
		$("#checkBookId").autocomplete({
			source: uri,
			select: function( event, ui ) {
				$("#hdnCheckBookId").val(ui.item.id);
				$(this).val(ui.item.name);
				return false;
			}, minLength: 2,
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>" +item.name + "</a>" )
				.appendTo( ul );
		};
	} else {
		if($("#checkBookId").val()){
			$("#spanCheckBook").text("No bank account selected.");
		}
	}
}

function getCheckBook() {
	var bankAccount = $("#hdnBankAccountId").val();
	var checkBookName = processSearchName($.trim($("#checkBookId").val()));
	var uri =  contextPath + "/getCheckbook?bankAccountId="+encodeURIComponent(bankAccount)
			+"&checkBookName="+checkBookName;
	if(checkBookName != "") {
		$.ajax({
			url: uri,
			success : function(item) {
				if (item != null && item != undefined) {
					$("#spanCheckBook").text("");
					$("#hdnCheckBookId").val(item.id);
					$(this).val(item.name);
					checkbookTemplateId = item.checkbookTemplateId;
					getCheckNumber();
				} else {
					if($("#checkBookId").val() != ""){
						$("#spanCheckBook").text("Invalid Check Book.");
						$("#checkBookId").val("");
						$("#hdnCheckBookId").val("");
						checkbookTemplateId = null;
						$("#checkNumber").val("");
					}
				}
			},
			error : function(error) {
				$("#hdnCheckBookId").val("");
				$("#checkBookId").val("");
			},
			dataType: "json",
		});
	}
}

function getCheckNumber() {
	var checkbookId = $("#hdnCheckBookId").val();
	if(checkbookId != null) {
		$.ajax({
			url: contextPath+"/apPayment/getCheckNumber?checkbookId="+checkbookId,
			success: function(cn) {
				if(cn == "Exhausted checkbook")
					alert("Checks for this checkbook are already exhausted. \nPlease select another checkbook.");
				else {
					checkNumber = cn;
					$("#checkNumber").val(checkNumber);
				}
			},
			error: function(error) {
				alert(error);
			}
		});
	} else {
		$("#checkNumber").val("");
	}
}

function getInvoiceNumber(elem) {
	$("#apInvoiceErrorMessage").html("");
	var invoiceNumber = $(elem).val();
	var supplierAccountId = $("#supplierAccountId").val();
	var currencyId = $("#currencyId").val();
	var uri = contextPath + "/apPayment/loadPaymentLines?supplierAccountId="+supplierAccountId
			+"&invoiceNumber="+encodeURIComponent(invoiceNumber)+"&divisionId="+"${apPayment.divisionId}"
			+"&currencyId="+currencyId+"&ebObjectIds="+getInvoiceIds();
	if(supplierAccountId != null) {
		if(invoiceNumber != "") {
			$("#apInvoiceErrorMessage").text("");
			$(elem).autocomplete({
				source: uri,
				select: function( event, ui ) {
					$(this).val(ui.item.referenceNumber);
					return false;
				}, minLength: 2,
				change: function(event, ui){
					$.ajax({
						url: uri,
						success : function(item) {
							if (ui.item != null) {
								$(this).val(ui.item.referenceNo);
								var apPaymentLineTypeId = ui.item.apPaymentLineTypeId;
								$(elem).closest("tr").find(".apPaymentLineTypeId").val(apPaymentLineTypeId);
								$(elem).closest("tr").find(".refenceObjectId").val(ui.item.refenceObjectId);
								$(elem).closest("tr").find(".currencyRateValue").val(ui.item.currencyRateValue);
								$(elem).closest("tr").find(".amount").val(formatAmt(ui.item.amount, apPaymentLineTypeId));
								if(apPaymentLineTypeId == 1) {
									loadSapRef(ui.item.refenceObjectId);
								}
								computeFooter();
							} else if($.trim(invoiceNumber) != "" && ui.item == null) {
								$("#apInvoiceErrorMessage").text("Invalid invoice number.");
							}
						},
						dataType: "json",
					});
				}
			}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
				return $( "<li>" )
					.data( "ui-autocomplete-item", item )
					.append( "<a style='font-size: small;'>" +item.referenceNumber + "</a>" )
					.appendTo( ul );
			};
		} else {
			//clear
			clearRow(elem);
		}
	} else {
		$("#supplierAccountId").val("");
		alertNoSupplierAcct();
	}
}

function clearRow(elem) {
	$(elem).closest("tr").find(".apPaymentLineTypeId").val("");
	$(elem).closest("tr").find(".refenceObjectId").val("");
	$(elem).closest("tr").find(".amount").val("");
}

function loadSapRef(refenceObjectId) {
	var currencyId = $("#currencyId").val();
	$.ajax({
		url: contextPath +"/apPayment/loadRefSap?invoiceEbObject="+refenceObjectId+"&currencyId="+currencyId+"&ebObjectIds="+getInvoiceIds(),
		success : function(sap) {
			var tblData = JSON.parse($apPaymentLineTable.getData());
			if (sap != null) {
				for(var index=0; index < sap.length; index++) {
					tblData.push(sap[index]);
				}
				initApPaymentLineTbl(tblData);
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json",
	});
}

function getInvoiceIds() {
	var ids = "";
	$("#apPaymentLineTbl tbody tr").each(function(){
		var refenceObjectId = $(this).closest("tr").find(".refenceObjectId").val();
		if(refenceObjectId.trim() != "") {
			ids +=refenceObjectId+";"
		}
	});
	return ids;
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
				console.log(error);
				// No currency rate found, resetting to default rate to PHP
				convertCurrencies(currencyId, 1.0);
			},
			dataType: "json"
		});
	} else {
		$("#hdnCurrRateId").val("");
		$("#hdnCurrRateValue").val("");
	}
	initApPaymentLineTbl();//Clear table
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
	computeFooter();
	PREV_RATE = rate;
}

function initApPaymentLineTbl(apPaymentLineDtosJson) {
	var cPath = "${pageContext.request.contextPath}";
	$("#apPaymentLineTbl").html("");
	$apPaymentLineTable = $("#apPaymentLineTbl").editableItem({
		data: apPaymentLineDtosJson,
		jsonProperties: [
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "apPaymentLineTypeId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "referenceNumber", "varType" : "string"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "paidAmount", "varType" : "double"},
			{"name" : "balance", "varType" : "double"},
			{"name" : "currencyRateValue", "varType" : "double"}
		],
		contextPath: cPath,
		header: [
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "apPaymentLineTypeId",
				"cls" : "apPaymentLineTypeId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Invoice Number",
				"cls" : "referenceNumber tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "60%"},
			{"title" : "Amount",
				"cls" : "amount tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "25%" },
			{"title" : "paidAmount",
				"cls" : "paidAmount",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "balance",
				"cls" : "balance",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "currencyRateValue",
				"cls" : "currencyRateValue",
				"editor" : "hidden",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});
	$("#apPaymentLineTbl").on("keypress", ".referenceNumber", function(){
		getInvoiceNumber(this);
	});

	$("#apPaymentLineTbl").on("blur", ".referenceNumber", function(){
		if($(this).val() === "") {
			clearRow(this);
		}
		computeFooter();
	});

	$("#apPaymentLineTbl").on("blur", ".amount", function(){
		computeFooter();
		var apPaymentLineTypeId = $(this).closest("tr").find(".apPaymentLineTypeId").val();
		$(this).val(formatAmt($(this).val(), apPaymentLineTypeId));
	});

	$("#apPaymentLineTbl").on("click", ".search", function(){
		showInvoices(this);
	});

	$("#apPaymentLineTbl tbody tr").each(function(){
		var apPaymentLineTypeId = $(this).closest("tr").find(".apPaymentLineTypeId").val();
		var amountElem = $(this).closest("tr").find(".amount");
		$(amountElem).val(formatAmt($(amountElem).val(), apPaymentLineTypeId));
		if("${pId}" != 0){
			$(this).find(".delRow").hide();
		}
	});

	appendTotalFooter();
	computeFooter();
	$("#apPaymentLineTbl table").attr("width", "100%");
}

function appendTotalFooter() {
	$("#apPaymentLineTbl table tfoot").append("<tr><td class='tfootProperties' colspan='3'> \
			Total</td><td class='tfootProperties' style='text-align:right'><span id='totalPayment'></span></td></tr>");
}

function computeFooter() {
	var amount = 0;//Initial Value
	$("#apPaymentLineTbl tbody tr").each(function(){
		var lineAmount = accounting.unformat($(this).closest("tr").find(".amount").val());
		var lineTypeId = $(this).closest("tr").find(".apPaymentLineTypeId").val();
		amount += Number(lineAmount);//Add amount of every line.
	});
	$("#totalPayment").text(formatAmt(amount));
}

function formatAmt(amount, lineTypeId) {
	var amt = accounting.unformat(amount);
	if (amt < 0) {
		// Get the absolute value and format the amount data.
		// Insert '()' for negative values
		return '(' + accounting.formatMoney(Math.abs(amt)) + ')';
	}
	return accounting.formatMoney(amt);
}

</script>
</head>
<body>
	<div id="divInvoiceList" style="display: none"></div>
	<div class="formDivBigForms">
		<form:form method="POST" commandName="apPayment" id="apPaymentDiv">
			<div class="modFormLabel">
				AP Payment<span id="spanDivisionLbl"> - ${apPayment.division.name}</span>
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="ebObjectId" />
			<form:hidden path="voucherNumber"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="supplierId" id="hdnSupplierId" />
			<form:hidden path="bankAccountId" id="hdnBankAccountId" />
			<form:hidden path="checkbookId" id="hdnCheckBookId" />
			<form:hidden path="id" />
			<form:hidden path="referenceDocumentJson" id="referenceDocumentJson" />
			<form:hidden path="apPaymentLineDtosJson" id="apPaymentLineDtosJson" />
			<form:hidden path="companyId" id="companyId"/>
			<form:hidden path="divisionId" id="hdnDivisionId"/>
			<form:hidden path="currencyRateId" id="hdnCurrRateId"/>
			<form:hidden path="currencyRateValue" id="hdnCurrRateValue"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Voucher No.</td>
							<td class="value"><input type="text" id="txtVoucherNo"
								class="textBoxLabel" readonly="readonly" /></td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<td class="value">
								<span id="spanFormStatusLbl">
									<c:choose>
										<c:when test="${apPayment.formWorkflow != null}">
											${apPayment.formWorkflow.currentFormStatus.description}
										</c:when>
										<c:otherwise>
											NEW
										</c:otherwise>
									</c:choose>
								</span>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Payment Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Payment Date</td>
							<td class="value"><form:input path="paymentDate"
									id="paymentDate" onblur="evalDate('paymentDate')"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate1"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('paymentDate')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="paymentDate"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Supplier Name</td>
							<td class="value"><input id=supplierId class="input"
								onkeydown="showSuppliers();" onkeyup="showSuppliers();"
								onblur="getSupplier();" />
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="supplierId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Supplier's Account</td>
							<td class="value"><form:select path="supplierAccountId"
									id="supplierAccountId" cssClass="frmSelectClass" onchange="initApPaymentLineTbl();"></form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="supplierAccountId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">Specify payee </td>
							<td class="value"><form:checkbox path="specifyPayee" id="specifyPayee" name="specifyPayee" onclick="enableDisablePayee();"/></td>
						</tr>
						<tr>
							<td class="labels">Payee </td>
							<td class="value"><form:input path="payee" class="input" id="payee"/></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payee"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Bank Account</td>
							<td class="value">
								<input id=bankAccountId class="input" onkeydown="showBankAccounts();" onblur="getBankAccount();" />
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="bankAccountId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td></td>
							<td><span id="spanBankAcct" class="error"
								style="margin-left: 12px;"></span></td>
						</tr>
						<tr>
							<td class="labels">* Checkbook Name</td>
							<td class="value"><input id=checkBookId class="input"
								onkeydown="showCheckBooks();" onkeyup="showCheckBooks();" onblur="getCheckBook();"/>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="checkbookId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td></td>
							<td><span id="spanCheckBook" class="error"
								style="margin-left: 12px;"></span></td>
						</tr>
						<tr>
							<td class="labels">* Check No.</td>
							<td class="value"><form:input path="checkNumber"
									id="checkNumber" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="checkNumber"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Check Date</td>
							<td class="value"><form:input path="checkDate"
									id="checkDate" onblur="evalDate('checkDate')"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('checkDate')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="checkDate"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Currency</td>
							<td class="value">
								<form:select path="currencyId" id="currencyId" class="frmSelectClass"
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
							<td class="value"><form:input path="amount" id="totalAmount"
									class="numeric" maxLength="13" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="amount" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">Official Receipt</td>
							<td class="value"><form:input path="officialReceipt" class="inputSmall"/></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="officialReceipt" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
						<tr>
						<td class="labels">Remarks</td>
						<td class="value">
							<form:textarea path="remarks" rows="3" id="remarks"
								cssStyle="width: 350px; resize: none;"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="remarks" cssClass="error"/></td>
					</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AP Invoices</legend>
					<div id="apPaymentLineTbl"></div>
				</fieldset>
				<fieldset class="frmField_set">
				<legend>Document</legend>
					<div id="documentsTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<form:errors path="referenceDocsMessage" cssClass="error"
								style="margin-top: 12px;" />
						</td>
					</tr>
					<tr>
						<td colspan="12"><span class="error" id="spanDocumentSize"></span></td>
					</tr>
					<tr>
						<td colspan="12"><span class="error" id="spanReferenceDoc" style="margin-top: 12px;"></span></td>
					</tr>
				</table>
				</fieldset>
				<br>
				<table class="frmField_set">
					<tr>
						<td><span id="apInvoiceErrorMessage" class="error"> <form:errors
									path="apInvoiceMessage" cssClass="error" /></span></td>
					</tr>
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error" /></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSaveApPayment"
							onclick="saveApPayment();" value="Save" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>