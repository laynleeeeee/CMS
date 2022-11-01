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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
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
$(document).ready(function() {
	updateTotalAmount();
	aPinvoices = new Array ();
	$("#payee").attr("disabled", "disabled");
	if("${pId}" > 0){
		$("#specifyPayee").attr("disabled", "disabled");
		$("#bankAccountId").val("${apPayment.bankAccount.name}");
		$("#checkBookId").val("${apPayment.checkbook.name}");
		if("${apPayment.checkbook.checkbookTemplateId}" != null && "${apPayment.checkbook.checkbookTemplateId}" != 0){
			checkbookTemplateId = "${apPayment.checkbook.checkbookTemplateId}";
		}
		$("#checkNumber").val("${apPayment.checkNumber}");
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${apPayment.amount}' />");
		$("#btnSaveApPayment").attr("disabled", "disabled");
		$("#supplierId").val("${apPayment.supplier.name}");
		filterSupplierAccounts();
		disableFormFields();
		var rowCount = $("#apInvoice tbody tr").length;
		if(apInvoiceCurrentIndex < minimumRows) {
			var rowsToAdd = minimumRows - rowCount;
			addAPInvoice(rowsToAdd);
		}
	} else {
		showCheckBooks();
		addAPInvoice(4);
	}
	countRows();

	//Disable all rows except for the first row
	$("#apInvoice tbody tr:not(:first)").each(function(i) {
		disableApInvoices($(this));
	});
	//Display Voucher Number
	$("#txtVoucherNo").val("${voucherNo}");
		
	// Delete rows
	 $("tr .imgDelete").click(function () {
		 if("${pId}" > 0) {
			 alert("Cannot delete AP invoices!");
		 }
	});
});

function enableDisablePayee () {
	if(specifyPayee.checked == true){
		$("#payee").removeAttr("disabled");
	} else {
		$("#payee").val("");
		$("#payee").attr("disabled", "disabled");
	}
}

function disableFields(){
	$("#paymentDate").attr('readonly',true);
	$("#imgDate1").hide();
	$("#bankAccountId").attr('disabled',true);
	$("#checkbookId").attr('disabled',true);
	$("#checkNumber").attr('readonly', true);
	$("#checkDate").attr('readonly', true);
	$("#imgDate2").hide();
	$("#supplierId").attr('disabled',true);
	$("#supplierAccountId").attr('disabled',true);
	$("#totalAmount").attr('readonly', true);
	$(".txtInvoiceNumber").attr("disabled", "disabled");
	$(".txtAmount").attr("disabled", "disabled");
	$(".imgDelete").attr("onclick", "").unbind("onclick");
	$(".imgSearchInvoices").attr("onclick", "").unbind("onclick");
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

function disableFormFields() {
	//Disable edit checkbox to disallow editing.
	$("#cbEdit").attr("disabled", "disabled");
}

function deleteRow(deleteImage, columnIndexNumber) {
	 var deleteRow = $(deleteImage).closest("tr");
	 delete aPinvoices[columnIndexNumber];
	 $(deleteRow).remove();
	 var rowCount = $('#apInvoice tbody tr').length;
	 if(rowCount < minimumRows){
		 var rowsToAdd = minimumRows - rowCount;
		 addAPInvoice(rowsToAdd);
	 }
	 // Always enable the first line.
	 enableApInvoices ($("#apInvoice tbody tr:first"));
	 processApInvoiceTableRow(rowCount);
	 updateTotalAmount();
}

function updateAmount (txtInvoiceNumber) {
	var id = $(txtInvoiceNumber).attr("id");
	var currentRowNumber = id.replace("invoiceNumber", "");
	var invoiceNumber = $("#invoiceNumber"+currentRowNumber).val();
	var supplierAcctId = $("#supplierAccountId option:selected").val();
	if($.trim(invoiceNumber) != "" && supplierAcctId != null) {
		invoiceNumber = processSearchName(invoiceNumber);
		var uri = "/apPayment/getInvoice?supplierAccountId="+supplierAcctId+"&invoiceNumber="+invoiceNumber;
		$.ajax({
			url: contextPath+uri,
			success : function(invoice) {
				currentInvoiceNo = invoice.invoiceNumber == "" ? invoiceNumber : invoice.invoiceNumber;
				var amount = invoice.balance == 0 ?  invoice.amount : invoice.balance;
				$("#apInvoice tbody tr").each(function(i) {
				var selectedInvoiceNo = $(this).find(".txtInvoiceNumber").val();
				var selectInvoiceNoRow = $(this).find(".txtInvoiceNumber").attr("id").replace("invoiceNumber", "");
					if (i == currentRowNumber) {
						if(isExistingInvoice(selectedInvoiceNo, currentInvoiceNo, selectInvoiceNoRow) == true) {
							alertDuplicateInvoice();
							if (isShowingInvoices != true)
								$("#invoiceNumber"+currentRowNumber).focus();
							return false;
						} else {
							$("#apInvoiceErrorMessage").text("");
							$("#apInvoiceId"+currentRowNumber).val(invoice.id);
							if (aPinvoices.length == 0) {
								$("#amount"+currentRowNumber).val(amount);
								aPinvoices.push (invoice);
							} else {
								var storedInvoice = aPinvoices[currentRowNumber];
								if (storedInvoice == null || typeof storedInvoice == "undefined") {
									$("#apInvoiceId"+currentRowNumber).val(invoice.id);
									$("#amount"+currentRowNumber).val(amount);
									aPinvoices.push(invoice);
								} else if (storedInvoice.invoiceNumber.toUpperCase().localeCompare(currentInvoiceNo.toUpperCase()) == 0 || 
										storedInvoice.invoiceNumber == currentInvoiceNo) {
									$("#amount"+currentRowNumber).val(storedInvoice.amount);
								} else if (storedInvoice.invoiceNumber != currentInvoiceNo) {
									$("#apInvoiceId"+currentRowNumber).val(invoice.id);
									$("#amount"+currentRowNumber).val(amount);
									aPinvoices[currentRowNumber] = invoice;
								}
							}
						}
					} else {
						if((currentInvoiceNo.toUpperCase().localeCompare(selectedInvoiceNo.toUpperCase()) == 0
								&& currentRowNumber != selectInvoiceNoRow)) {
							alertDuplicateInvoice();
							return false;
						} else if(currentRowNumber == selectInvoiceNoRow) {
							if(currentInvoiceNo.toUpperCase() != selectedInvoiceNo.toUpperCase()){
								$("#apInvoiceId"+selectInvoiceNoRow).val(invoice.id);
								$("#amount"+selectInvoiceNoRow).val(amount);
								aPinvoices[selectInvoiceNoRow] = invoice;
								return false;
							} else if(currentInvoiceNo.toUpperCase() == selectedInvoiceNo.toUpperCase()){
								var storedInvoice = aPinvoices[currentRowNumber];
								if(typeof storedInvoice == "undefined" || storedInvoice.invoiceNumber.toUpperCase() != selectedInvoiceNo.toUpperCase()) {
									$("#apInvoiceId"+selectInvoiceNoRow).val(invoice.id);
									$("#amount"+selectInvoiceNoRow).val(amount);
									aPinvoices[selectInvoiceNoRow] = invoice;
								} else {
									$("#amount"+selectInvoiceNoRow).val(storedInvoice.amount);
								}
								return false;
							}
						}
					}
				});
			},
			error : function(error) {
				var rowNumber = Number(currentRowNumber);
				$("#apInvoice tbody tr").each(function(i) {
					var indexNo = $(this).find(".imgSearchInvoices").attr("id").replace("imgSearch", "");
					if(rowNumber == indexNo) {
						return false;
					}
				});
			},
			dataType: "json",
		});
	} else if(supplierAcctId == null) {
		alertNoSupplierAcct();
	}
	updateTotalAmount();
}

function setAndUpdateAmount(txtAmount) {
	var id = $(txtAmount).attr("id");
	var currentRowNumber = id.replace("amount", "");
	var inputedAmount = $("#amount"+currentRowNumber).val();
	var invoice = aPinvoices[currentRowNumber];
	if(typeof invoice != "undefined") {
		invoice.amount = inputedAmount;
		aPinvoices[currentRowNumber] = invoice;
	}
	//Formats the amount
	$(txtAmount).val(formatDecimalPlaces($(txtAmount).val()));
	updateTotalAmount();
}

$(function () {
	$(".txtAmount").live("keydown", function(e) {
		if (e.which == 9 && !e.shiftKey) { // tab code
			var id = $(this).attr("id");
			var rowNumber = Number (id.replace ("amount", ""));
			var currentRow = $(this).closest("tr");
			var nextRow = $(currentRow).next(); // next element of tr
			// Check if the index of the amount is equal to the current index global variable or if next row is existing
			if (rowNumber + 1 == apInvoiceCurrentIndex || nextRow.length < 1) {
				addAPInvoice(1);
				if (isShowingInvoices != true)
					$(this).focus();
				countRows();
			} else {
				enableApInvoices ($(currentRow).next());
			}
		}
	});

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

function countRows() {
	// Count the number of rows
	 $("#apInvoice tbody tr").each(function(i) {
       $.trim($(this).find("td").eq(1).html(i+1));
   });
}

function filterSupplierAccounts() {
	if($.trim($("#supplierId").val()) == "") {
		$("#supplierAccountId").empty();
	} else {
		var supplierId = $("#hdnSupplierId").val();
		var bankAccountId = $("#hdnBankAccountId").val();
		var uri = contextPath+"/apPayment/loadSupplierAccounts?supplierId="+supplierId+"&bankAccountId="+bankAccountId;
		$("#supplierAccountId").empty();
		var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["numberAndName"];
			}
		};
		loadPopulate (uri, false, null, "supplierAccountId", optionParser, null);
	}
}

function addAPInvoice (numOfRows) {
	for (var i=0; i<numOfRows; i++) {
		var newRow = "<tr>";
		//Delete and search for AP Invoice
		newRow += "<td class='tdProperties' align='center'>";
		newRow += "<img class='imgDelete' id='imgDelete"+apInvoiceCurrentIndex+"' onclick='deleteRow(this, \""+apInvoiceCurrentIndex+"\");' \
			src='${pageContext.request.contextPath}/images/delete_active.png' />";
		newRow += "&nbsp;";
		newRow += "<img class='imgSearchInvoices' id='imgSearch"+apInvoiceCurrentIndex+"' onclick='showInvoices(this, \""+apInvoiceCurrentIndex+"\");' \
			src='${pageContext.request.contextPath}/images/search_active.png' />";
		newRow += "</td>";

		//Number
		newRow += "<td class='tdProperties'><input type='hidden' value="+(apInvoiceCurrentIndex + 1)+" id='number"+apInvoiceCurrentIndex+"'/></td>";

		//AP Invoice Id
		newRow += "<td style=' display: none;'>";
		newRow += "<input name='apInvoices["+apInvoiceCurrentIndex+"].invoiceId' id='apInvoiceId"+apInvoiceCurrentIndex+"' class='txtApInvoiceId' style='visibility: hidden;'/>";
		newRow += "</td>";

		//Invoice
		newRow += "<td class='tdProperties' valign='top' style='white-space: nowrap;'>";
		newRow += "<input name='apInvoices["+apInvoiceCurrentIndex+"].invoiceNumber' onkeyup='getInvoiceNumber(\""+apInvoiceCurrentIndex+"\");' \
			onblur='getInvoiceNumber(\""+apInvoiceCurrentIndex+"\"); updateAmount(this);' id='invoiceNumber"+apInvoiceCurrentIndex+"' style='width: 550px;' class='txtInvoiceNumber'/>";
		newRow +="</td>";

		//Amount
		newRow += "<td class='tdProperties' align='right' valign='top' style='width: 50px;'>";
		newRow += "<input name='apInvoices["+apInvoiceCurrentIndex+"].amount' id='amount"+apInvoiceCurrentIndex+"' style='width: 250px; text-align: right;' \
			onblur='setAndUpdateAmount(this);' id='amount"+apInvoiceCurrentIndex+"' class='txtAmount'/>";
		newRow += "</td>";

		newRow += "</tr>";
		$("#apInvoice tbody").append(newRow);
		$("#apInvoice tbody tr:last").find("td:last").css("border-right", "none");
		apInvoiceCurrentIndex++;
	}
}

var isSaving = false;
function saveApPayment() {
	if ($.trim($("#supplierId").val()) == "") {
		$("#hdnSupplierId").val("");
	}
	if(isSaving == false) {
		isSaving = true;
		parseAmount();
		$("#btnSaveApPayment").attr("disabled", "disabled");
		var xhrArgs = {
			url: contextPath+"/apPayment/form",
			form : dojo.byId("apPayment"),
			handleAs : "text",
			load : function (data) {
				if (data.substring(0,5) == "saved") {
					var objectId = data.split(";")[1];
					var formStatus = new Object();
					formStatus.objectId = objectId;
					var checkNo = $("#checkNumber").val();
					var selectedInvoices = "";
					var rowCount = $('#apInvoice tbody tr').length;
					if (paymentStatusId != 1) {
						$("#apInvoice tbody tr").each(function(i) {
							var invoices = $(this).find(".txtInvoiceNumber").val();
							if($.trim(invoices) != "") {
								i++;
								if((rowCount-1) == i)
									selectedInvoices += invoices+".";
								else
									selectedInvoices += invoices+", ";
							}
						});
						formStatus.message = "Check "+checkNo+" has been successfully paid to Invoice/s: "+selectedInvoices;
					} else {
						formStatus.message = "Check "+checkNo+" has been cancelled";
					}
					updateTable (formStatus);
					if(checkbookTemplateId != null && checkbookTemplateId != 0){
						var supplierName = encodeURIComponent(specifyPayee.checked ? $.trim($("#payee").val()) : 
							$.trim($("#supplierId").val()));
						var isOk = confirm("Do you want to print check?\nPress ok to continue");
						if (isOk) {
							window.open(contextPath + "/checkPrinting/new?checkbookTemplateId="
									+checkbookTemplateId+"&name="+supplierName+"&amount="+$("#totalAmount").val()
									+"&checkDate="+$("#checkDate").val());
						}
					}
					dojo.byId("form").innerHTML = "";
					isSaving = false;
				} else {
					var checkNo = $("#checkNumber").val();
					var bankAccountId = $("#bankAccountId").val();
					var checkbookId = $("#checkBookId").val();
					var supplier = $("#supplierId").val();
					dojo.byId("form").innerHTML = data;
					$("#txtPaymentStatus").val("Negotiable");
					$("#bankAccountId").val(bankAccountId);
					$("#checkBookId").val(checkbookId);
					$("#checkNumber").val(checkNo);
					$("#supplierId").val(supplier);
					filterSupplierAccounts();
					deleteRows();
					var rowCount = $("#apInvoice tbody tr").length;
					if(rowCount < minimumRows) {
						var rowsToAdd = minimumRows - rowCount;
						addAPInvoice(rowsToAdd);
						processApInvoiceTableRow(rowCount);
					} else {
						processApInvoiceTableRow(rowCount);
					}
					parseAmount();
					formatMonetaryVal();
					updateTotalAmount();
					$("#supplierId").val(supplier);
					// Always enable the first line.
					enableApInvoices ($("#apInvoice tbody tr:first"));
				}
			},
			error : function(error) {
				$("#form").html ("Error occured while saving.");
			}
		};
//	 	Call the asynchronous xhrPost
		dojo.xhrPost(xhrArgs);
		isSaving = false;
	}
}

function parseAmount() {
	if ($.trim($("#totalAmount").val()) == "")
		$("#totalAmount").val("0.0");
	else {
		var formatAmt = $("#totalAmount").val().replace(/\,/g,"");
		$("#totalAmount").val(parseFloat(formatAmt,10));
	}
	$("#apInvoice tbody tr").each(function() {
		var apLineAmount = $(this).find(".txtAmount").val();
		if(apLineAmount != "") {
			var formatAmt = apLineAmount.replace(/\,/g,"");
			$(this).find(".txtAmount").val(parseFloat(formatAmt,10));
		}
	});
}

function updateTotalAmount() {
	var total = 0;
	$("#apInvoice tbody tr").each(function(i) {
		var formatAmt = $(this).find(".txtAmount").val().replace(/\,/g,"");
		total += formatAmt != "" ? parseFloat(formatAmt) : 0;
	});
	$("#spanTotalAmount").html(formatDecimalPlaces(total));
}

function processApInvoiceTableRow (setRows) {
    var index = 0;
    $("#apInvoice tbody tr").each(function(i) {
		$.trim($(this).find("td").eq(1).html(i+1));
		if (index >= setRows) {
			disableApInvoices($(this));
		}
		index++;
    });
}

function deleteRows() {
	$("#apInvoice tbody tr").each(function() {
		if (!$.trim($(this).find(".txtInvoiceNumber").val()) && $(this).find(".txtAmount").val() == 0.0)
			$(this).remove();
	});
}

function disableApInvoices (elem) {
	$(elem).closest("tr").find(".txtInvoiceNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtAmount").attr("disabled", "disabled");
	$(elem).closest("tr").find(".imgSearchInvoices").attr("onclick", "").unbind("onclick");
	$(elem).addClass("divVeil");
}

function enableApInvoices (elem) {
	$(elem).closest("tr").find(".txtInvoiceNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtAmount").removeAttr("disabled");
	var currentRow = $(elem).find(".imgSearchInvoices").attr("id").replace("imgSearch", "");
	$(elem).closest("tr").find(".imgSearchInvoices").attr("onclick", "showInvoices(this,"+currentRow+");");
	$(elem).removeClass("divVeil");
}

function showInvoices(searchImage, columnIndexNumber) {
	isShowingInvoices = true;
	var position = $(searchImage).closest("tr").find(searchImage).position();
	var offset = $(searchImage).closest("tr").find(searchImage).offset();
	selectApInvoiceRow = columnIndexNumber;
	var supplierAccountId = $("#supplierAccountId option:selected").val();
	if(supplierAccountId != null) {
		var path = contextPath + "/apPayment/loadInvoices?supplierAccountId="+supplierAccountId;
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

function doAfterSelection(invoiceNumber, amount, balance, invoiceId) {
	var invoiceAmt = balance == 0 ? amount : balance;
	$("#apInvoice tbody tr").each(function(i) {
		var selectInvoiceNo = $(this).find(".txtInvoiceNumber").val();
		var selectInvoiceNoRow = $(this).find(".txtInvoiceNumber").attr("id").replace("invoiceNumber", "");
		if(i == selectApInvoiceRow) {
			if(isExistingInvoice(selectInvoiceNo, invoiceNumber, selectApInvoiceRow) == true) {
				alertDuplicateInvoice();
				return false;
			} else {
				var storedInvoice = aPinvoices[selectApInvoiceRow];
				var invoice = new Object();
				if (storedInvoice == null || typeof storedInvoice == "undefined") {
					$("#invoiceNumber"+selectApInvoiceRow).val(invoiceNumber);
					$("#apInvoiceId"+selectApInvoiceRow).val(invoiceId);
					$("#amount"+selectApInvoiceRow).val(invoiceAmt);
					invoice.invoiceNumber = invoiceNumber;
					invoice.amount = invoiceAmt;
					aPinvoices.push(invoice);
				} else if (storedInvoice.invoiceNumber.toUpperCase().localeCompare(invoiceNumber.toUpperCase()) == 0 ||
						storedInvoice.invoiceNumber == invoiceNumber) {
					$("#amount"+selectApInvoiceRow).val(storedInvoice.amount);
				} else if (storedInvoice.invoiceNumber.toUpperCase() != invoiceNumber.toUpperCase()) {
					$("#invoiceNumber"+selectApInvoiceRow).val(invoiceNumber);
					$("#apInvoiceId"+selectApInvoiceRow).val(invoiceId);
					$("#amount"+selectApInvoiceRow).val(invoiceAmt);
					invoice.invoiceNumber = invoiceNumber;
					invoice.amount = invoiceAmt;
					aPinvoices[selectApInvoiceRow] = invoice;
				}
			}
			closeInvoiceSelector(selectApInvoiceRow);
		} else {
			if(invoiceNumber.toUpperCase().localeCompare(selectInvoiceNo.toUpperCase()) == 0) {
				if(selectApInvoiceRow != selectInvoiceNoRow) {
					alertDuplicateInvoice();
					return false;
				} else {
					closeInvoiceSelector(selectApInvoiceRow);
				}
			} else if(invoiceNumber.toUpperCase() != selectInvoiceNo.toUpperCase() && selectApInvoiceRow == selectInvoiceNoRow){
				//Validation of invoice after an error. The row number after the error validation is not the same as the initial reload.
				$("#invoiceNumber"+selectApInvoiceRow).val(invoiceNumber);
				$("#apInvoiceId"+selectApInvoiceRow).val(invoiceId);
				$("#amount"+selectApInvoiceRow).val(invoiceAmt);
				var invoice = new Object();
				invoice.invoiceNumber = invoiceNumber;
				invoice.amount = invoiceAmt;
				aPinvoices[selectApInvoiceRow] = invoice;
				closeInvoiceSelector(selectApInvoiceRow);
			}
		}
	});
}

function closeInvoiceSelector(selectApInvoiceRow) {
	$("#divInvoiceList").css("display", "none");
	$("#amount"+selectApInvoiceRow).focus();
	isShowingInvoices = false;
	updateTotalAmount();
	return false;
}

function isExistingInvoice(selectInvoiceNo, invoiceNumber, selectedRow) {
	if (aPinvoices.length != 0) {
		for (var index = 0; index < aPinvoices.length; index++) {
			var storedInvoice = aPinvoices[index];
			if(typeof storedInvoice == "undefined" && selectInvoiceNo.toUpperCase() == invoiceNumber.toUpperCase())
				return true;
			else if (storedInvoice.invoiceNumber.toUpperCase().localeCompare(invoiceNumber.toUpperCase()) == 0
					&& selectedRow != index)
				return true;
		}
	}
}

function alertDuplicateInvoice() {
	alert("Invoice Number has already been selected. \nPlease choose another.");
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
	var bankAccountId = $("#hdnBankAccountId").val();
	if($("#supplierId").val() != "" && bankAccountId != "") {
		var uri = contextPath + "/getSuppliers/byBank?name="+supplierName+"&bankAccountId="+bankAccountId;	
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
	addAPInvoice(4);
	countRows();
	$("#apInvoice tbody tr:not(:first)").each(function(i) {
		disableApInvoices($(this));
	});
}

function showBankAccounts() {
	var bankAcctName = $.trim($("#bankAccountId").val());
	var uri = contextPath + "/getBankAccounts/all?name="+bankAcctName+"&limit=10";
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
						clearValues();
					} else {
						$("#hdnBankAccountId").val("");
						clearValues();
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
	var bankAcctName = $("#bankAccountId").val();
	var uri = contextPath + "/getBankAccounts/byName?name="+encodeURIComponent(bankAcctName);
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
				$("#bankAccountId").val("");
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

function getInvoiceNumber(columnIndexNumber) {
	$("#apInvoiceErrorMessage").html("");
	selectApInvoiceRow = columnIndexNumber;
	var row = $("#"+selectApInvoiceRow).parent("td").parent("tr");
	var rowNumber = row.rowNumber;
	var invoiceNumber = $("#invoiceNumber"+selectApInvoiceRow).val();
	var supplierAccountId = $("#supplierAccountId").val();
	var uri = contextPath + "/getApInvoices/?supplierAccountId="+supplierAccountId
			+"&invoiceNumber="+encodeURIComponent(invoiceNumber);
	var invoiceIds = "";
	$(".txtApInvoiceId").each(function(i) {
		if(i != rowNumber){
			invoiceIds += $(this).val() + ";";
		}
	});
	uri += "&invoiceIds="+invoiceIds;
	if(supplierAccountId != null) {
		$("#apInvoiceErrorMessage").text("");
		$("#invoiceNumber"+selectApInvoiceRow).autocomplete({
			source: uri,
			select: function( event, ui ) {
				$("#apInvoiceId"+selectApInvoiceRow).val(ui.item.id);
				$(this).val(ui.item.referenceNo);
				return false;
			}, minLength: 2,
			change: function(event, ui){
				$.ajax({
					url: uri,
					success : function(item) {
						if (ui.item != null) {
							$("#apInvoiceId"+selectApInvoiceRow).val(ui.item.id);
							$(this).val(ui.item.referenceNo);
							$("#amount"+selectApInvoiceRow).val(ui.item.amount);
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
				.append( "<a style='font-size: small;'>" +item.referenceNo + "</a>" )
				.appendTo( ul );
		};
	} else {
		$("#supplierAccountId").val("");
	}
}
</script>
</head>
<body>
	<div id="divInvoiceList" style="display: none"></div>
	<div class="formDivBigForms">
		<form:form method="POST" commandName="apPayment">
			<form:hidden path="ebObjectId" />
			<form:hidden path="formWorkflowId" />
			<form:hidden path="supplierId" id="hdnSupplierId" />
			<form:hidden path="bankAccountId" id="hdnBankAccountId" />
			<form:hidden path="checkbookId" id="hdnCheckBookId" />
			<div class="modFormLabel">
				AP Payment <span class="btnClose" id="btnClose">[X]</span>
			</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Voucher Number</td>
							<td class="value"><input type="text" id="txtVoucherNo"
								class="textBoxLabel" readonly="readonly" /></td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status"
								value="${apPayment.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
							</c:if>
							<td class="value"><input type="text" id="txtPaymentStatus"
								class="textBoxLabel" readonly="readonly" value='${status}' /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Payment Header</legend>
					<table class="formTable">
						<form:hidden path="id" />
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
							<td class="labels">* Bank Account</td>
							<td class="value"><input id=bankAccountId class="input"
								onkeydown="showBankAccounts();" onkeyup="showBankAccounts();"
								onblur="getBankAccount();" />
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
							<td class="labels">* Check Book</td>
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
							<td class="labels">* Check Number</td>
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
									id="supplierAccountId" cssClass="frmSelectClass"></form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="supplierAccountId"
									cssClass="error" style="margin-left: 12px;" /></td>
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
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Check format option</legend>
					<table class="formTable" id="checkOption">
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
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AP Invoices</legend>
					<table class="dataTable" id="apInvoice">
						<thead>
							<tr>
								<th width="7%"></th>
								<th width="3%">#</th>
								<th width="60%">Invoice Number</th>
								<th width="30%" style="border-right: none;">Amount</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${apPayment.apInvoices}" var="apInvoice"
								varStatus="status">
								<tr>
									<!-- Delete and Search -->
									<td class="tdProperties" align="center" style="white-space: nowrap;"><img
										class="imgDelete" id="imgDelete${status.index}"
										onclick="deleteRow(this,${status.index});"
										src="${pageContext.request.contextPath}/images/delete_active.png" />
										<img class="imgSearchInvoices" id="imgSearch${status.index}"
										onclick="showInvoices(this,${status.index});"
										src="${pageContext.request.contextPath}/images/search_active.png" /></td>
									<!-- Number -->
									<td class="tdProperties"><input type="hidden"
										value="${status.index + 1}" /></td>
									<!-- AP Invoice ID -->
									<td style="display: none;"><input
										name="apInvoices[${status.index}].invoiceId"
										id="apInvoiceId${status.index}" value="${apInvoice.invoiceId}"
										class='txtApInvoiceId' /></td>
									<!-- Invoice Number -->
									<td class="tdProperties" valign="top"
										style="white-space: nowrap;"><input
										name="apInvoices[${status.index}].invoiceNumber"
										id="invoiceNumber${status.index}" onblur="updateAmount(this);"
										onkeydown='getInvoiceNumber(${status.index});'
										value="${apInvoice.invoiceNumber}" style="width: 550px;"
										class="txtInvoiceNumber" /></td>
									<!-- Amount -->
									<td class="tdProperties" align="right" valign="top" style="width: 50px;"><input
										name="apInvoices[${status.index}].amount"
										style="width: 250px; text-align: right;"
										onblur="setAndUpdateAmount(this);"
										value="<fmt:formatNumber type='number' minFractionDigits='6' maxFractionDigits='6' value='${apInvoice.amount}' />"
										id="amount${status.index}" class="txtAmount" />
									</td>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="3"></td>
								<td style="text-align: right;"><span id="spanTotalAmount">0.0</span></td>
							</tr>
						</tfoot>
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