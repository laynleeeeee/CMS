<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description: Direct payment form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
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
<style type="text/css">
input.numeric {
	width: 150px;
}

.tdProperties {
	border-right: 1px solid #000000;
}

.textBoxLabel, .txtCompanyNumber, .txtDivisionNumber,
.txtAccountNumber, .txtAmount, .txtDescription{
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}

.divVeil {
    background-color: #F2F1F0;
}

#paymentLine {
	cellspacing: 0;
	border: none;
}

#paymentLine thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#paymentLine tbody td {
	border-top: 1px solid #000000;
}
.imgDelete{
	margin-left: 2px;
}
</style>
<script type="text/javascript">
var minimumRows = 4;
var currentInvoiceNo = "";
var paymentLineCurrentIndex = "${fn:length(directPaymentDto.directPayment.paymentLines)}";
var savedSupplierAcctId = "${directPaymentDto.payment.bankAccountId}";
var paymentStatusId = parseInt("${directPaymentDto.payment.formWorkflow.currentFormStatus.id}");
var terms = new Array ();
var selectpaymentLineRow = 0;
var $documentsTable = null;
var totalSizeInBytes = 0;
var isShowingAccountCombi = false;
var accountCombinationCache = new Array ();
$(document).ready(function() {
	updateTotalAmount();
	$("#payee").attr("disabled", "disabled");
	if("${pId}" > 0) {
		$("#directPaymentTypeId").val("${directPaymentDto.directPayment.directPaymentTypeId}");
		$("#txtBankAcctId").val("${directPaymentDto.payment.bankAccount.name}");
		$("#checkBookId").val("${directPaymentDto.payment.checkbook.name}");
		$("#checkNumber").val("${directPaymentDto.payment.checkNumber}");
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${directPaymentDto.payment.amount}' />");
		$("#supplierId").val("${directPaymentDto.payment.supplier.name}");
		filterSupplierAccounts();
		// Disable fields if the form workflow status is completed or cancelled
		if("${directPaymentDto.payment.formWorkflow.complete}" == "true"
				|| "${directPaymentDto.payment.formWorkflow.currentStatusId}" == 3
				|| "${directPaymentDto.payment.formWorkflow.currentStatusId}" == 4) {
			$("#directPaymentFormId :input").attr("disabled", "disabled");
			$("#specifyPayee").attr("disabled", "disabled");
		}
		countRows();
	} else {
		showCheckBooks();
		reloadTable();
	}
	reloadCombi();
	initializeDocumentsTbl();
	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>

	//Display Voucher Number
	$("#txtVoucherNo").val("${voucherNo}");

	// Delete rows
	$("tr .imgDelete").click(function () {
		if("${pId}" > 0) {
			alert("Cannot delete AP invoices!");
		}
	});
	if($("#specifyPayee").is(":checked")) {
		$("#payee").removeAttr("disabled");
	}
	hideFields();
	disableFormFields();
});

function disableEntries (elem) {
	$(elem).closest("tr").find(".txtDivisionNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtAccountNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtAmount").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtDescription").attr("disabled", "disabled");
	$(elem).addClass("divVeil");
}

function enableEntries (elem) {
	$(elem).closest("tr").find(".txtDivisionNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtAccountNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtAmount").removeAttr("disabled");
	$(elem).closest("tr").find(".txtDescription").removeAttr("disabled");
	$(elem).removeClass("divVeil");
}

//Fix the indexing of table rows
function updateApLineIndex() {
	var currentRow = 0;
	$("#paymentLine tbody tr").each(function(row) {
		currentRow = row;
		$(this).find(".imgDelete").attr("id", "imgDelete"+row);
		$(this).find(".txtDivisionNumber").attr("id", "division"+row).attr("name", "directPayment.paymentLines["+row+"].divisionNumber");
		$(this).find(".txtDivisionNumber").attr("onfocus", "setDefaultValue(this,"+row+");");
		$(this).find(".txtAccountNumber").attr("id", "account"+row).attr("name", "directPayment.paymentLines["+row+"].accountNumber");
		$(this).find(".spanDivisionNames").attr("id", "spanDivision"+row);
		$(this).find(".spanAccountNames").attr("id", "spanAccount"+row);
		$(this).find(".txtAmount").attr("id", "amount"+row).attr("name", "directPayment.paymentLines["+row+"].amount");
		$(this).find(".txtDescription").attr("id", "description"+row).attr("name", "directPayment.paymentLines["+row+"].description");
	});
	paymentLineCurrentIndex = currentRow + 1;
}

$(function () {
	$(".imgCloseComboList").live("click", function() {
		$("#divComboList").css("display", "none");
	});

	$("#totalAmount").live("blur", function (e) {
		$("#totalAmount").val(accounting.formatMoney($("#totalAmount").val()));
	});

	$(".txtAmount").live("blur", function () {
		updateTotalAmount();
		$(this).val(accounting.formatMoney($(this).val()));
	});

	$(".txtDescription").live("keydown", function (e) {
		if (e.which == 9 && !e.shiftKey) {
			var id = $(this).attr("id");
			var rowNumber = Number (id.replace ("description", ""));
			var currentRow = $(this).closest("tr");
			var nextRow = $(currentRow).next();
			if(rowNumber + 1 == paymentLineCurrentIndex || nextRow.length < 1) {
				addDirectPaymentLine(1);
				countRows();
				disableEntries();
			} else {
				enableEntries($(currentRow).next());
			}
		}
	});
});

function Term (id, days) {
	this.id = id;
	this.days = days;
}

function computeDueDate () {
	var paymentDate = $("#paymentDate").val ();
	if (paymentDate == null || paymentDate == ""){
		$("#checkDate").val ("");
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
	var paymentDate = new Date (paymentDate);
	paymentDate.setDate(paymentDate.getDate() + parseInt(additionalDays));
	if(!isNaN( paymentDate.getMonth()) && !isNaN( paymentDate.getDate()) && !isNaN(paymentDate.getFullYear())){
		$("#checkDate").val ((paymentDate.getMonth() + 1) +"/"+paymentDate.getDate()+
				"/"+paymentDate.getFullYear());
	} else {
		$("#checkDate").val ("");
	}
}

function enableDisablePayee(elem) {
	var value = $(elem).is(":checked");
	if(value == true) {
		$("#payee").removeAttr("disabled");
	} else {
		$("#payee").val("");
		$("#payee").attr("disabled", "disabled");
	}
}

function disableFields(){
	$("#paymentDate").attr('readonly',true);
	$("#imgDate1").hide();
	$("#txtBankAcctId").attr('disabled',true);
	$("#checkbookId").attr('disabled',true);
	$("#checkNumber").attr('readonly', true);
	$("#checkDate").attr('readonly', true);
	$("#imgDate2").hide();
	$("#supplierId").attr('disabled',true);
	$("#supplierAccountId").attr('disabled',true);
	$("#totalAmount").attr('readonly', true);
	$(".txtAmount").attr("disabled", "disabled");
	$(".imgDelete").attr("onclick", "").unbind("onclick");
	$(".imgSearchInvoices").attr("onclick", "").unbind("onclick");
}

function formatMoney (textbox) {
	var money = accounting.formatMoney($(textbox).val());
	$(textbox).val(money.replace(/[$]/gi, ''));
}

function formatMonetaryVal() {
	formatMoney($("#totalAmount"));

	$("#paymentLine tbody tr").each(function(i) {
		formatMoney($(this).find(".txtAmount"));
	});
}

function disableFormFields() {
	if("${pId}" > 0) {
		$("#directPaymentTypeId").attr("disabled", "disabled");

		// Disable empty rows when editing
		$("#paymentLine tbody tr").each(function() {
			var dNumber = $(this).find(".txtDivisionNumber").val();
			var aNumber = $(this).find(".txtAccountNumber").val();
			if($.trim(dNumber) == "" && $.trim(aNumber)  == "") {
				disableEntries($(this));
			}
		});
	}
}

function enableFormFields() {
	$("#directPaymentTypeId").removeAttr("disabled");
}

function deleteRow(deleteImage, columnIndexNumber) {
	var deleteRow = $(deleteImage).closest("tr");
	$(deleteRow).remove();
	var rowCount = $('#paymentLine tbody tr').length;
	if(rowCount < minimumRows){
		var rowsToAdd = minimumRows - rowCount;
		addDirectPaymentLine(rowsToAdd);
	}
	processApInvoiceTableRow(rowCount);
	updateApLineIndex ();
	updateTotalAmount();
	// Always enable the first line.
	enableEntries($("#paymentLine tbody tr:first"));
}

function countRows() {
	// Count the number of rows
	$("#paymentLine tbody tr").each(function(i) {
		$.trim($(this).find("td").eq(1).html(i+1));
	});
}

function filterSupplierAccounts() {
	if($.trim($("#supplierId").val()) == "") {
		$("#supplierAccountId").empty();
	} else {
		var supplierId = $("#hdnSupplierId").val();
		var bankAccountId = $("#hdnBankAccountId").val();
		var uri = "";
		if(bankAccountId != ""){
			uri = contextPath+"/apPayment/loadSupplierAccounts?supplierId="+supplierId+"&bankAccountId="+bankAccountId;
		} else {
			uri = contextPath+"/getSupplierAccounts?supplierId="+supplierId;
		}
		$("#supplierAccountId").empty();
		var optionParser =  {
			getValue: function (rowObject) {
			$("#hdnCompanyNumber").val(rowObject["company"].companyNumber);
			$("#hdnCompanyId").val(rowObject["companyId"]);
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["numberAndName"];
			}
		};
		postHandler = {
				doPost: function(data) {
					setCompany();
					reloadCombi();
				}
		};
		loadPopulate (uri, false, null, "supplierAccountId", optionParser, postHandler);
	}
}

var isSaving = false;
function saveApPayment() {
	$("#referenceDocumentsJson").val($documentsTable.getData());
	if($.trim($("#supplierId").val()) == "") {
		$("#hdnSupplierId").val("");
	}
	deleteRows();
	parseAmount();
	enableFormFields();
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize()) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		parseAmount();
		$("#btnSaveApPayment").attr("disabled", "disabled");
		doPostWithCallBack ("directPaymentFormId", "form", function(data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved" ? true : false;
			if (isSuccessfullySaved) {
				var objectId = $.trim(parsedData[1]);
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var directPaymentTypeId = $("#directPaymentTypeId").val();
				var checkNo = $("#checkNumber").val();
				var txtBankAcctId = $("#txtBankAcctId").val();
				var hdnBankAccountId = $("#hdnBankAccountId").val();
				var checkbookId = $("#checkBookId").val();
				var supplier = $("#supplierId").val();
				var txtVoucherNo = $("#txtVoucherNo").val();
				var txtPaymentStatus = $("#txtPaymentStatus").val();
				if("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				$("#txtVoucherNo").val(txtVoucherNo);
				$("#txtPaymentStatus").val(txtPaymentStatus);
				$("#directPaymentTypeId").val(directPaymentTypeId);
				$("#txtBankAcctId").val(txtBankAcctId);
				$("#hdnBankAccountId").val(hdnBankAccountId);
				$("#checkBookId").val(checkbookId);
				$("#checkNumber").val(checkNo);
				$("#supplierId").val(supplier);
				filterSupplierAccounts();
				var rowCount = $("#paymentLine tbody tr").length;
				if(rowCount < minimumRows) {
					var rowsToAdd = minimumRows - rowCount;
					addDirectPaymentLine(rowsToAdd);
				}
				disableFormFields();
				reloadCombi();
				updateTotalAmount();
				formatMonetaryVal();
				hideFields();
				$("#supplierId").val(supplier);
				initializeDocumentsTbl();
				updateApLineIndex();
				processApInvoiceTableRow(rowCount);
				// Always enable the first line.
				enableEntries($("#paymentLine tbody tr:first"));
				isSaving = false;
			}
			$("#btnSaveApPayment").removeAttr("disabled");
		});
	} else if(checkExceededFileSize()) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function parseAmount() {
	if ($.trim($("#totalAmount").val()) == "")
		$("#totalAmount").val("0.0");
	else {
		var formatAmt = $("#totalAmount").val().replace(/\,/g,"");
		$("#totalAmount").val(parseFloat(formatAmt,10));
	}
	$("#paymentLine tbody tr").each(function() {
		var paymentLineAmount = $(this).find(".txtAmount").val();
		if(paymentLineAmount != "") {
			var formatAmt = paymentLineAmount.replace(/\,/g,"");
			$(this).find(".txtAmount").val(parseFloat(formatAmt,10));
		}
	});
}

function updateTotalAmount() {
	var total = 0;
	$("#paymentLine tbody tr").each(function(i) {
		var formatAmt = $(this).find(".txtAmount").val().replace(/\,/g,"");
		total += formatAmt != "" ? parseFloat(formatAmt) : 0;
	});
	$("#spanTotalAmount").html(accounting.formatMoney(total));
}

function deleteRows() {
	$("#paymentLine tbody tr").each(function() {
		if (!$.trim($(this).find(".txtDivisionNumber").val())
				&& !$.trim($(this).find(".txtAccountNumber").val()) 
				&& ($(this).find(".txtAmount").val() == 0.0 || !$.trim($(this).find(".txtAmount").val()))
				&& !$.trim($(this).find(".txtDescription").val())) {
			$(this).remove();
		}
	});
}

function processApInvoiceTableRow (setRows) {
	var index = 0;
	$("#paymentLine tbody tr").each(function(i) {
		$.trim($(this).find("td").eq(1).html(i+1));
		if (index >= setRows) {
			disableEntries($(this));
		}
		index++;
	});
}

function showAccts($textBox, columnIndexNumber) {
	selectpaymentLineRow = columnIndexNumber;
	var accountName = $.trim($($textBox).val());
	var companyId = $("#hdnCompanyId").val();
	var divisionId = $("#hdnDivisionId"+selectpaymentLineRow).val();
	var uri = contextPath + "/getAccounts/byName?accountName="+accountName+"&companyId="
			+companyId+"&divisionId="+divisionId+"&limit=10";
	$($textBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnAccountId"+selectpaymentLineRow).val(ui.item.id);
			$(this).val(ui.item.number);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#hdnAccountId"+selectpaymentLineRow).val(ui.item.id);
						$(this).val(ui.item.number);
					} else {
						$(this).val("");
					}
				},
				dataType: "json",
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number+"-"+item.accountName+ "</a>" )
			.appendTo( ul );
	};
}

function alertNoSupplierAcct() {
	alert("No supplier account selected.");
}

function showSuppliers () {
	var supplierName = encodeURIComponent($.trim($("#supplierId").val()));
	var bankAccountId = $("#hdnBankAccountId").val();
	if(bankAccountId != "") {
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
	}
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

function showBankAccounts() {
	var bankAcctName = $.trim($("#txtBankAcctId").val());
	var uri = contextPath + "/getBankAccounts/all?name="+bankAcctName+"&limit=10";
	$("#txtBankAcctId").autocomplete({
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
						$("#spanBankAcct").text("");
						$("#spanCheckBook").text("");
						$("#hdnBankAccountId").val(ui.item.id);
						$(this).val(ui.item.name);
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
	$("#spanBankAcct").text("");
	var bankAcctName = $.trim($("#txtBankAcctId").val());
	var uri = contextPath + "/getBankAccounts/byName?name="+encodeURIComponent(bankAcctName);
	if(bankAcctName != "") {
		$.ajax({
			url: uri,
			success: function (item) {
				if (item != null && item != undefined) {
					$("#hdnBankAccountId").val(item.id);
					$("#txtBankAcctId").val(item.name);
				}
				if($("#hdnBankAccountId").val() != savedSupplierAcctId) {
					$("#supplierId").val("");
					$("#hndSupplierId").val("");
					$("#supplierAccountId").empty();
					reloadTable();
				}
			},
			error : function(error) {
				var errorMsg = "Invalid Bank Account.";
				if($("#directPaymentTypeId").val() == 1 && bankAcctName != "") {
					errorMsg = "Invalid Payment Account.";
				}
				$("#spanBankAcct").text(errorMsg);
				$("#errorBankAcctId").html("");
				clearFormFields();
			},
			dataType: "json"
		});
	} else {
		clearFormFields();
	}
}

function clearFormFields() {
	$("#hdnBankAccountId").val("");
	$("#txtBankAcctId").val("");
	$("#supplierId").val("");
	$("#hndSupplierId").val("");
	$("#supplierAccountId").empty();
}

function reloadTable() {
	$("#hdnCompanyNumber").val("");
	$("#hdnCompanyId").val("");

	// Delete table for reload
	$("#paymentLine tbody tr").each(function() {
		$(this).remove();
	});

	// Recreate table
	addDirectPaymentLine(4);
	$("#paymentLine tbody tr:not(:first)").each(function(i) {
		disableEntries($(this));
	});
	countRows();
	updateTotalAmount();
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
					getCheckNumber();
				} else {
					if($("#checkBookId").val() != ""){
						$("#spanCheckBook").text("Invalid Check Book.");
						$("#checkBookId").val("");
						$("#hdnCheckBookId").val("");
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

function showAcctCombiSelection (searchImage, columnIndexNumber) {
	isShowingAccountCombi = true;
	var position = $(searchImage).closest("tr").find(searchImage).position();
	var offset = $(searchImage).closest("tr").find(searchImage).offset();
	selectAPLineRow = columnIndexNumber;
	var path = contextPath + "/accountCombinationSelector";
	$("#divComboList").load(path); 
	if (!$("#divComboList").is(":visible")) {
		$("#divComboList").css("background-color", "#FFFFFF")
		.css("border", "1px solid #999999")
		.css("cursor", "default")
		.css("position","absolute")
		.css("left", position.left+"px")
		.css("top", position.top+"px")
		.css("padding", "25px 25px 20px")
		.css("z-index", "1234")
		.show();
	}
}

function setDefaultValue(textBox, currentIndexNumber) {
	//Set default value for description
	if($("#description").val() != "") {
		var amount = $("#amount"+currentIndexNumber).val();
		$("#amount"+currentIndexNumber).val(amount != "" ? amount : accounting.formatMoney(0.0));
		$("#description"+currentIndexNumber).val($("#description").val());
	}
}

function addDirectPaymentLine(numOfRows) {
	for (var i=0; i<numOfRows; i++) {
		var newRow = "<tr id='trReadOnly'>";
		//Delete and search for account combination
		newRow += "<td class='tdProperties' align='left' style='border-left: none;'>";
		newRow += "<img class='imgDelete' id='imgDelete"+paymentLineCurrentIndex+"' src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deleteRow(this,\""+paymentLineCurrentIndex+"\");'/>";
		newRow += "</td>";

		//Number
		newRow += "<td class='tdProperties'><input type='hidden' value="+(paymentLineCurrentIndex + 1)+"/></td>";

		//Division
		newRow += "<td class='tdProperties' valign='top' style='white-space: nowrap;'>";
		newRow += "<input name='directPayment.paymentLines["+paymentLineCurrentIndex+"].divisionNumber' id='division"+paymentLineCurrentIndex+"' onfocus='setDefaultValue(this, \""+paymentLineCurrentIndex+"\");' onkeydown='showDivisions(this, \""+paymentLineCurrentIndex+"\");' onkeyup='showDivisions(this, \""+paymentLineCurrentIndex+"\");' onblur='validateDivisionNo(this);' style='width: 50px;' class='txtDivisionNumber' maxLength='5'/>";
		newRow += "<input type='hidden' id='hdnDivisionId"+paymentLineCurrentIndex+"' class='hdnDivisionIds' name='directPayment.paymentLines["+paymentLineCurrentIndex+"].divisionId'>";
		newRow +="</td>";

		//Account
		newRow += "<td class='tdProperties' valign='top' style='white-space: nowrap;'>";
		newRow += "<input name='directPayment.paymentLines["+paymentLineCurrentIndex+"].accountNumber' id='account"+paymentLineCurrentIndex+"' onkeydown='showAccts(this, \""+paymentLineCurrentIndex+"\");' onkeyup='showAccts(this, \""+paymentLineCurrentIndex+"\");' onblur='validateAccountNo(this);' style='width: 200px' class='txtAccountNumber' maxLength='10'/>";
		newRow += "<input type='hidden' id='hdnAccountId"+paymentLineCurrentIndex+"' class='hdnAccountIds' name='directPayment.paymentLines["+paymentLineCurrentIndex+"].accountId'>";
		newRow +="</td>";

		//Account combination
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<span id='spanCompany"+paymentLineCurrentIndex+"' class='spanCompanyNames'></span> ";
		newRow += "<span id='spanDivision"+paymentLineCurrentIndex+"' class='spanDivisionNames'></span> ";
		newRow += "<span id='spanAccount"+paymentLineCurrentIndex+"' class='spanAccountNames'></span>";
		newRow +="</td>";

		//Amount
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input name='directPayment.paymentLines["+paymentLineCurrentIndex+"].amount' id='amount"+paymentLineCurrentIndex+"' style='width: 100%; text-align: right;' class='txtAmount' />";
		newRow += "</td>";

		//Description
		newRow += "<td class='tdProperties' valign='top' style='border-right: none;'>";
		newRow += "<input name='directPayment.paymentLines["+paymentLineCurrentIndex+"].description' id='description"+paymentLineCurrentIndex+"' style='width: 100%;' class='txtDescription' maxLength='250'/>";
		newRow += "</td>";
		newRow += "</tr>";
		$("#paymentLine tbody").append(newRow);
		paymentLineCurrentIndex++;
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

	$("#documentsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize);
	});

	$("#documentsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable").on("click", ".fileLink", function(){
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
	$("#documentsTable tbody tr").each(function(){
		name = $.trim($(this).find(".fileName").val());
		if(fileNames.slice(-1)[0] == name){
			isDuplicate = true;
		}
	});
	if(isDuplicate){
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
			FR.onprogress = function (e){
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
	$("#documentsTable tbody tr").find(".fileSize").each(function(){
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

function setCompany(){
	var supplierAccountId = $("#supplierAccountId").val();
	var uri = contextPath + "/getCompany/supplierAcct?supplierAccountId="+supplierAccountId;
	if(supplierAccountId != "") {
		$.ajax({
			url: uri,
			success: function (item) {
				if (item != null && item != undefined) {
					$("#hdnCompanyId").val(item.id);
					$("#hdnCompanyNumber").val(item.companyNumber);
				}
			},
			error : function(error) {
				$("#hdnCompanyId").val("");
				$("#hdnCompanyNumber").val("");
			},
			dataType: "json"
		});
	}
}

function loadAjaxData (uri, type, callbackFunction, errorFunction) {
	$.ajax({
		url: uri,
		success : callbackFunction,
		error : errorFunction,
		dataType: type,
	});
}

function validateAccountNo(txtAccountNumber) {
	var id = $(txtAccountNumber).attr("id");
	var currentRowNumber = id.replace("account", "");
	$("#spanAccount"+currentRowNumber).text('');
	var acctNumber = $("#account"+currentRowNumber).val();
	if ($.trim(acctNumber) != "" && $("#spanCompany"+currentRowNumber).text() != "Invalid Company - "
			&& $("#spanDivision"+currentRowNumber).text() != "Invalid Division - ") {
		loadAjaxData (contextPath+"/getAccounts?companyId=&divisionId=&accountNumber="+acctNumber, "json",
				function(account){
					$("#spanAccount"+currentRowNumber).val("").css("color", "black").text(account.accountName);
				},
				function (error) {
					$("#spanAccount"+currentRowNumber).val("").css("color", "red").text('Invalid Account');
					if (isShowingAccountCombi != true)
						$("#account"+currentRowNumber).focus();
					console.log(error);
				});
	} else {
		$(this).focus();
		$("#spanAccount"+currentRowNumber).val("");
	}
}

function validateDivisionNo(txtDivisionNumber) {
	var id = $(txtDivisionNumber).attr("id");
	var currentRowNumber = id.replace("division", "");
	$("#spanDivision"+currentRowNumber).text('');
	var divisionNumber = $("#division"+currentRowNumber).val();
	if ($.trim(divisionNumber) != "" && $("#spanCompany"+currentRowNumber).text() != "Invalid Company - ") {
		loadAjaxData (contextPath+"/getDivisions?companyId=&divisionNumber="+divisionNumber, "json",
				function(division) {
					$("#spanDivision"+currentRowNumber).val("").css("color", "black").text(division.name + " - ");
				},
				function (error) {
					$("#spanDivision"+currentRowNumber).val("").css("color", "red").text('Invalid Division - ');
					if (isShowingAccountCombi != true) {
						$("#division"+currentRowNumber).focus();
					}
					console.log(error);
				});
	} else {
		$(this).focus();
		$("#spanDivision"+currentRowNumber).val("");
	}
}

function showDivisions($textBox, columnIndexNumber) {
	selectAPLineRow = columnIndexNumber;
	var companyId = $("#hdnCompanyId").val();
	var division = $($textBox).val();
	var uri = contextPath + "/getDivisions/new?companyId="+companyId+"&divisionNumber="+division+"&isActive=true&limit=10";
	$($textBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnDivisionId"+selectAPLineRow).val(ui.item.id);
			$(this).val(ui.item.number);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#hdnDivisionId"+selectAPLineRow).val(ui.item.id);
						$(this).val(ui.item.number);
					}
				},
				dataType: "json",
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number+"-"+item.name+ "</a>" )
			.appendTo( ul );
	};
}


function hideFields() {
	if($("#directPaymentTypeId").val() == 1) {
		console.log("hide values!");
		$("#ckDateV").hide();
		$("#ckDateL").hide();
		$("#cbL").hide();
		$("#cbV").hide();
		$("#cnL").hide();
		$("#cnV").hide();
		$("#baL").text("* Payment Account");
		$("#divPayeeId").hide();
	} else {
		console.log("show values!");
		$("#ckDateV").show();
		$("#ckDateL").show();
		$("#cbL").show();
		$("#cbV").show();
		$("#cnL").show();
		$("#cnV").show();
		$("#baL").text("* Bank Account");
		$("#divPayeeId").show();
	}
}

function clearFieldValues() {
	if($("#directPaymentTypeId").val() == 1) {
		$("#checkNumber").val("");
		$("#checkDate").val("");
		$("#errorCheckBookId").html("");
		$("#errorCheckNoId").html("");
	}
	$("#spanBankAcct").text("");
	$("#errorBankAcctId").html("");
	$("#txtBankAcctId").val("");
}

function processAccountCombinationName (paymentLineRow, divisionNumber, accountNumber) {
	var cachedCombination = getCombination (divisionNumber, accountNumber);
	if (cachedCombination == null) {
		var uri = contextPath + "/getAccountCombination?companyNumber="
		+$("#hdnCompanyNumber").val()+"&divisionNumber="+divisionNumber+"&accountNumber="+accountNumber;
		$.ajax({
			url: uri,
			success: function(responseText){
				var accountCombination = responseText[0];
				accountCombinationCache.push (accountCombination);
				setAccountCombinationName (paymentLineRow, accountCombination, divisionNumber, accountNumber);
			},
			dataType: "json",
		});
	} else {
		setAccountCombinationName (paymentLineRow, cachedCombination, divisionNumber, accountNumber);
	}
}

function setAccountCombinationName (paymentLineRow, accountCombination, divisionNumber, accountNumber) {
	if(accountCombination != null) {
		if($.trim(divisionNumber) != ""){
			$("#spanDivision"+paymentLineRow).text (accountCombination.division.name + " - ");
		}
		if($.trim(accountNumber) != ""){
			$("#spanAccount"+paymentLineRow).text (accountCombination.account.accountName);
		}
	}
}

function getCombination (divisionNumber, accountNumber) {
	if (accountCombinationCache.length > 0) {
		for (var index = 0; index < accountCombinationCache.length; index++){
			var accountCombination = accountCombinationCache[index];
			if(accountCombination != null) {
				if (accountCombination.division.number == divisionNumber &&
						accountCombination.account.number == accountNumber) {
					return accountCombination;
				}
			}
		}
	}
	return null;
}

function reloadCombi() {
	//Update the account combination description
	var rowCount = $('#paymentLine tbody tr').length;
	for (var row = 0; row < rowCount; row ++) {
		if($("#division"+row).val() != "" || $("#account"+row).val() != "") {
			var divisionNumber = $("#division"+row).val();
			var accountNumber = $("#account"+row).val();
			processAccountCombinationName(row, divisionNumber, accountNumber);
		}
	}
	var rowsToAdd = minimumRows - rowCount;
	if(rowCount < minimumRows){
		addDirectPaymentLine(rowsToAdd);
	}
}
</script>
</head>
<body>
	<div id="divInvoiceList" style="display: none"></div>
	<div class="formDivBigForms">
		<form:form method="POST" commandName="directPaymentDto" id="directPaymentFormId">
			<form:hidden path="directPayment.id" />
			<form:hidden path="directPayment.ebObjectId" />
			<form:hidden path="payment.id" />
			<form:hidden path="payment.formWorkflowId" />
			<form:hidden path="payment.supplierId" id="hdnSupplierId" />
			<form:hidden path="payment.bankAccountId" id="hdnBankAccountId" />
			<form:hidden path="payment.checkbookId" id="hdnCheckBookId" />
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="payment.voucherNumber" />
			<form:hidden path="payment.createdBy" />
			<div class="modFormLabel">
				Direct Payment <span class="btnClose" id="btnClose">[X]</span>
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
								value="${directPaymentDto.payment.formWorkflow.currentFormStatus.description}" />
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
						<form:input type="hidden" path="payment.companyId" id="hdnCompanyId"/>
						<tr>
							<td class="labels">* Payment Type</td>
							<td class="value">
								<form:select path="directPayment.directPaymentTypeId"
									cssClass="frmSelectClass" id="directPaymentTypeId" onchange="hideFields(); clearFieldValues();">
									<form:options items="${paymentTypes}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels">* Payment Date</td>
							<td class="value"><form:input path="payment.paymentDate"
									id="paymentDate" onblur="evalDate('paymentDate')"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate1"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('paymentDate')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payment.paymentDate"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">Invoice No</td>
							<td class="value"><form:input path="directPayment.invoiceNo"
									id="invoiceNumber" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="directPayment.invoiceNo" 
									cssClass="error" style="margin-left: 12px;"/></td>
						</tr>
						<tr>
						<td class="labels" id="baL">* Bank Account"</td>
						<td class="value"><input id="txtBankAcctId" class="input"
								onkeydown="showBankAccounts();" onkeyup="showBankAccounts();"
								onblur="getBankAccount();" />
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payment.bankAccountId" id="errorBankAcctId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td></td>
							<td><span id="spanBankAcct" class="error"
								style="margin-left: 12px;"></span></td>
						</tr>
						<tr>
							<td class="labels" id="cbL">* Check Book</td>
							<td class="value" id="cbV"><input id=checkBookId class="input"
								onkeydown="showCheckBooks();" onkeyup="showCheckBooks();" onblur="getCheckBook();"/>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payment.checkbookId" id="errorCheckBookId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td></td>
							<td><span id="spanCheckBook" class="error"
								style="margin-left: 12px;"></span></td>
						</tr>
						<tr>
							<td class="labels" id="cnL">* Check Number</td>
							<td class="value" id="cnV"><form:input path="payment.checkNumber"
									id="checkNumber" class="inputSmall" onkeydown="inputOnlyNumeric('checkNumber');"
									onkeyup="inputOnlyNumeric('checkNumber');"/></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payment.checkNumber" id="errorCheckNoId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Term</td>
							<td class="value"><form:select path="directPayment.termId"
									cssClass="frmSelectClass" id="termId"
									onchange="computeDueDate();">
									<form:options items="${terms}" itemLabel="name" itemValue="id" />
								</form:select></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="directPayment.termId" cssClass="error" style="margin-left: 12px;"/></td>
						</tr>
						<tr>
							<td class="labels" id="ckDateL">* Check Date</td>
							<td class="value" id="ckDateV"><form:input path="payment.checkDate"
									id="checkDate" onblur="evalDate('checkDate')"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('checkDate')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payment.checkDate"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Supplier Name</td>
							<td class="value">
								<input id="supplierId" class="input"
								onkeydown="showSuppliers();" onkeyup="showSuppliers();"
								onblur="getSupplier();" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payment.supplierId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Supplier's Account</td>
							<td class="value">
								<input type="hidden" id="hdnCompanyNumber"/>
								<form:select path="payment.supplierAccountId"
									id="supplierAccountId" cssClass="frmSelectClass" onchange="setCompany();">
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payment.supplierAccountId"
									cssClass="error" style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">* Description</td>
							<td class="value"><form:textarea path="directPayment.description"
									id="description" class="input" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="directPayment.description"
									cssClass="error" style="margin-left: 12px;"/></td>
						<tr>
							<td class="labels">* Amount</td>
							<td class="value">
								<form:input path="payment.amount" id="totalAmount" class="numeric"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="payment.amount" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
					</table>
				</fieldset>
				<c:choose>
					<c:when test="${directPaymentDto.directPayment.id > 0}">
						<c:if test="${directPaymentDto.directPayment.directPaymentTypeId eq 2}">
							<fieldset class="frmField_set">
								<legend>Check format option</legend>
								<table class="formTable" id="checkOption">
									<tr>
										<td class="labels">Specify payee </td>
										<td class="value"><form:checkbox path="payment.specifyPayee" id="specifyPayee" onclick="enableDisablePayee(this);"/></td>
									</tr>
									<tr>
										<td class="labels">Payee </td>
										<td class="value"><form:input path="payment.payee" class="input" id="payee"/></td>
									</tr>
									<tr>
										<td class="labels"></td>
										<td class="value"><form:errors path="payment.payee" cssClass="error"/></td>
									</tr>
								</table>
							</fieldset>
						</c:if>
					</c:when>
					<c:otherwise>
						<div id="divPayeeId">
							<fieldset class="frmField_set">
								<legend>Check format option</legend>
								<table class="formTable" id="checkOption">
									<tr>
										<td class="labels">Specify payee </td>
										<td class="value"><form:checkbox path="payment.specifyPayee" id="specifyPayee" onclick="enableDisablePayee(this);"/></td>
									</tr>
									<tr>
										<td class="labels">Payee </td>
										<td class="value"><form:input path="payment.payee" class="input" id="payee"/></td>
									</tr>
									<tr>
										<td class="labels"></td>
										<td class="value"><form:errors path="payment.payee" cssClass="error"/></td>
									</tr>
								</table>
							</fieldset>
						</div>
					</c:otherwise>
				</c:choose>
				<fieldset class="frmField_set">
					<legend>AP Lines</legend>
					<table id="paymentLine" class="dataTable">
						<thead>
							<tr>
								<th width="3%"></th>
								<th width="3%">#</th>
								<th width="5%">Division</th>
								<th width="5">Account</th>
								<th width="40%">Combination</th>
								<th width="23%">Amount</th>
								<th width="25%" style="border-right: none;">Description</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${directPaymentDto.directPayment.paymentLines}" var="paymentLine" varStatus="status">
								<tr>
									<!-- Delete -->
									<td class="tdProperties" align="left" style='border-left: none;'>
										<img class='imgDelete' src='${pageContext.request.contextPath}/images/delete_active.png' onclick="deleteRow(this, ${status.index});"/>
									</td>
									<!-- Number -->
									<td class="tdProperties"><input type="hidden" value="${status.index + 1}"/></td>
									<!-- Division -->
									<td class="tdProperties" valign='top' style='white-space: nowrap;'>
										<input type="hidden" id='hdnDivisionId${status.index}' name='directPayment.paymentLines[${status.index}].divisionId' value="${paymentLine.divisionId}" class="hdnDivisionIds"/>
										<input type='text' name='directPayment.paymentLines[${status.index}].divisionNumber' id='division${status.index}'
											value="${paymentLine.divisionNumber}" onfocus='setDefaultValue(this, ${status.index});'
											onblur="validateDivisionNo(this);" style='width: 50px;' class='txtDivisionNumber' maxLength='5'
											onkeydown='showDivisions(this, ${status.index});' onkeyup='showDivisions(this, ${status.index});'/>
									</td>
									<!-- Account -->
									<td class="tdProperties" valign='top' style='white-space: nowrap;'>
										<input type='text' name='directPayment.paymentLines[${status.index}].accountNumber' id='account${status.index}' value="${paymentLine.accountNumber}"
											onkeydown='showAccts(this, ${status.index});' onkeyup='showAccts(this, ${status.index});'
											onblur="validateAccountNo(this);" style='width: 200px;' class='txtAccountNumber' maxLength='10' />
									</td>
									<!-- Account Combination -->
									<td class="tdProperties">
										<span id='spanDivision${status.index}' class='spanDivisionNames'></span>
										<span id='spanAccount${status.index}' class='spanAccountNames'></span>
									</td>
									<!-- Amount -->
									<td class="tdProperties" valign='top'>
										<input type='text' name='directPayment.paymentLines[${status.index}].amount' 
											value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${paymentLine.amount}' />" 
											style='width: 100%; text-align: right;' id="amount${status.index}" class='txtAmount'/>
									</td>
									<!-- Description -->
									<td class="tdProperties" valign='top' style='border-right: none;'>
										<input type='text' name='directPayment.paymentLines[${status.index}].description' id='description${status.index}' value="${paymentLine.description}"
											style='width: 100%;' class='txtDescription' maxLength='250' />
									</td>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="5"></td>
								<td style="text-align: right;"><span id="spanTotalAmount">0.0</span></td>
							</tr>
						</tfoot>
					</table>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><span id="apInvoiceErrorMessage" class="error"> <form:errors
									path="payment.apInvoiceMessage" cssClass="error" /></span></td>
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
						<td align="right" colspan="4"><input type="button" id="btnSaveApPayment"
							value="Save" onclick="saveApPayment();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>