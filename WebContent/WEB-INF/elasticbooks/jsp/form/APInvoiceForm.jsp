<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 


	Description: Accounts payable invoice form.
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

#aPLine {
	cellspacing: 0;
	border: none;
}

#aPLine thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#aPLine tbody td {
	border-top: 1px solid #000000;
}
.imgDelete{
	margin-left: 2px;
}
</style>
<script type="text/javascript">
var minimumRows = 4;
var aPLineCurrentIndex = "${fn:length(aPInvoice.aPlines)}";
var accountCombination = null;
var terms = new Array ();
var selectAPLineRow = 0;
var accountCombinationCache = new Array ();
var invoiceStatusId = "${aPInvoice.formWorkflow.currentFormStatus.id}";
var NOT_SAVED = 0;
var CREATED = 1;
var APPROVED = 3;
var CANCELLED = 4;
var isEdit = "${isEdit}";
var isShowingAccountCombi = false;
var $documentsTable = null;
var totalSizeInBytes = 0;
$(document).ready (function () {
	disableFormFields();
	if("${pId}" == 0) {
		addAPLine (4);
		//Disable all rows except for the first row
		$("#aPLine tbody tr:not(:first)").each(function(i) {
			disableEntries($(this));
		});
		filterSuplierAccounts();
	} else {
		$("#supplierId").val("${aPInvoice.supplier.name}");
		var dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${aPInvoice.dueDate}'/>";
		filterSuplierAccounts("${aPInvoice.supplierAccountId}", "${aPInvoice.termId}", dueDate);
		// If form workflow status = COMPLETED
		var invoiceStatus = "${aPInvoice.formWorkflow.complete}";
		if(invoiceStatus == "true" || "${aPInvoice.formWorkflow.currentStatusId}" == 4)
			$("#invoiceForm :input").attr("disabled", "disabled");
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${aPInvoice.amount}' />");
		formatMonetaryVal();
	}
	$("#seqNumber").val("${sequenceNo}");
	updateTotalAmount();
	reloadCombi();

	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>

	// Count the number of rows
	 $("#aPLine tbody tr").each(function(i) {
        $.trim($(this).find("td").eq(1).html(i+1));
    });
	initializeDocumentsTbl();
});

function formatMoney (textbox) {
	var money = accounting.formatMoney($(textbox).val());
	$(textbox).val(money.replace(/[$]/gi, ''));
}

function formatMonetaryVal() {
	formatMoney($("#totalAmount"));
	
	$("#aPLine tbody tr").each(function(i) {
		formatMoney($(this).find(".txtAmount"));
    });
}

function disableFormFields() {
	//Disable empty rows when editing
	if ("${aPInvoice.id}" != 0) {
		$("#aPLine tbody tr").each(function(i) {
			var cNumber = $(this).find(".txtCompanyNumber").val();
			var dNumber = $(this).find(".txtDivisionNumber").val();
			var aNumber = $(this).find(".txtAccountNumber").val();
			if (cNumber == "" && dNumber == "" && aNumber == "") 
				disableEntries($(this));
		});
	}
}

function deleteRow(deleteImage) {
	var invoiceStat = $("#txtInvoiceStatus").val();
	 if(invoiceStat.localeCompare("APPROVED") == 0){
		 alert("Cannot delete AP Line on approved invoices!");
	 }else if(invoiceStat.localeCompare("CANCELLED") == 0){
		 alert("Cannot delete AP Line on cancelled invoices!");
	 }else if(invoiceStat.localeCompare("CREATED") == 0 || invoiceStat.localeCompare("NEW") == 0){
		 var toBeDeletedRow = $(deleteImage).closest("tr");
		 $(toBeDeletedRow).remove();
		 var rowCount = $('#aPLine tbody tr').length;
		 if(rowCount < minimumRows){
			 var rowsToAdd = minimumRows - rowCount;
			 addAPLine(rowsToAdd);
		 }
		 updateApLineIndex();
		 processAplineTableRow (rowCount);
		 updateTotalAmount();
		 // Always enable the first line.
		 enableEntries ($("#aPLine tbody tr:first"));
	 }
}

function disableFields(){
	$("#invoiceTypeId").attr('disabled',true);
	$("#invoiceNumber").attr('readonly', true);
	$("#supplierId").attr('disabled',true);
	$("#supplierAccountId").attr('disabled',true);
	$("#termId").attr('disabled',true);
	$("#imgDate1").hide();
	$("#imgDate2").hide();
	$("#imgDate3").hide();
	$("#invoiceDate").attr('readonly', true);
	$("#glDate").attr('readonly', true);
	$("#dueDate").attr('readonly', true);
	$("#description").attr('readonly', true);
	$("#totalAmount").attr('readonly', true);
	$(".txtCompanyNumber").attr("readonly", true);
	$(".txtDivisionNumber").attr("readonly", true);
	$(".txtAccountNumber").attr("readonly", true);
	$(".txtAmount").attr("readonly", true);
	$(".txtDescription").attr("readonly", true);
	$(".imgSearchAcctCombi").attr("onclick", "").unbind("onclick");
}

function enableFields(){
	$("#btnSaveAPInvoice").attr('disabled',false);
	$("#invoiceTypeId").attr('disabled',false);
	$("#invoiceNumber").attr('readonly', false);
	$("#supplierId").attr('disabled',false);
	$("#supplierAccountId").attr('disabled',false);
	$("#termId").attr('disabled',false);
	$("#imgDate1").hide();
	$("#imgDate2").hide();
	$("#imgDate3").hide();
	$("#invoiceDate").attr('readonly', false);
	$("#glDate").attr('readonly', false);
	$("#dueDate").attr('readonly', false);
	$("#description").attr('readonly', false);
	$("#totalAmount").attr('readonly', false);
}


function reloadCombi() {
	//Update the account combination description
	var rowCount = $('#aPLine tbody tr').length;
	for (var row = 0; row < rowCount; row ++) {
		if($("#company"+row).val() != "" || $("#division"+row).val() != "" || $("#account"+row).val() != "") {
			var companyNumber = $("#company"+row).val();
			var divisionNumber = $("#division"+row).val();
			var accountNumber = $("#account"+row).val();
				processAccountCombinationName(row, companyNumber, divisionNumber, accountNumber);
		}
	}
	var rowsToAdd = minimumRows - rowCount;
	if(rowCount < minimumRows){
		addAPLine(rowsToAdd);
		processAplineTableRow(rowCount);
	}
}

function disableEntries (elem) {
	$(elem).closest("tr").find(".txtCompanyNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtDivisionNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtAccountNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtAmount").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtDescription").attr("disabled", "disabled");
	$(elem).closest("tr").find(".imgSearchAcctCombi").attr("onclick", "").unbind("onclick");
	$(elem).addClass("divVeil");
}

function enableEntries (elem) {
	$(elem).closest("tr").find(".txtCompanyNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtDivisionNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtAccountNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtAmount").removeAttr("disabled");
	$(elem).closest("tr").find(".txtDescription").removeAttr("disabled");
	var currentRow = $(elem).find(".imgSearchAcctCombi").attr("id").replace("imgSearch", "");
	$(elem).closest("tr").find(".imgSearchAcctCombi").attr("onclick", "showAcctCombiSelection(this,"+currentRow+");");
	$(elem).removeClass("divVeil");
}

function validateCompanyNo(txtCompanyNumber) {
	var id = $(txtCompanyNumber).attr("id");
	var currentRowNumber = id.replace("company", "");
	var companyNumber = $("#company"+currentRowNumber).val();
	if($.trim(companyNumber) != "") {
		loadAjaxData (contextPath+"/getCompany?companyNumber="+companyNumber, "json",
			function(company){
				$("#spanCompany"+currentRowNumber).val("").css("color", "black").text(company.name + " - ");
			},
			function (error) {
				$("#spanCompany"+currentRowNumber).val("").css("color", "red").text('Invalid Company - ');
				if (isShowingAccountCombi != true)
				$("#company"+currentRowNumber).focus();
				console.log(error);
		});
	}
}

function validateDivisionNo(txtDivisionNumber) {
	var id = $(txtDivisionNumber).attr("id");
	var currentRowNumber = id.replace("division", "");
	var divisionNumber = $("#division"+currentRowNumber).val();
	if ($.trim(divisionNumber) != "" && $("#spanCompany"+currentRowNumber).text() != "Invalid Company - ") {
		loadAjaxData (contextPath+"/getDivisions?companyId=&divisionNumber="+divisionNumber, "json",
				function(division){
					$("#spanDivision"+currentRowNumber).val("").css("color", "black").text(division.name + " - ");
				},
				function (error) {
					$("#spanDivision"+currentRowNumber).val("").css("color", "red").text('Invalid Division - ');
					if (isShowingAccountCombi != true)
					$("#division"+currentRowNumber).focus();
					console.log(error);
				});
	} else {
		$(this).focus();
		$("#spanDivision"+currentRowNumber).val("");
	}
}

function validateAccountNo(txtAccountNumber) {
	var id = $(txtAccountNumber).attr("id");
	var currentRowNumber = id.replace("account", "");
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
		if (e.which == 9 && !e.shiftKey) { // tab code
			var id = $(this).attr("id");
			var rowNumber = Number (id.replace ("description", "")) + 1;
			var rowCount = $('#aPLine tbody tr').length;
			if(rowNumber < rowCount) {
				var currentRow = $(this).closest("tr");
				enableEntries($(currentRow).next());
				$("#imgDelete"+rowNumber).focus();
			} else {
				addAPLine(1);
				// Get the last row number then add 1.
				$("#aPLine tbody tr").each(function(i) {
					$.trim($(this).find("td").eq(1).html(i+1));
				});
				$("#imgDelete"+rowNumber).focus();
			}
		}
	});
});

//Fix the indexing of table rows
function updateApLineIndex() {
	var currentRow = 0;
	$("#aPLine tbody tr").each(function(row) {
		currentRow = row;
		$(this).find(".imgDelete").attr("id", "imgDelete"+row);
		$(this).find(".imgSearchAcctCombi").attr("id", "imgSearch"+row);
		$(this).find(".imgSearchAcctCombi").attr("onclick", "showAcctCombiSelection(this,"+row+");");
		$(this).find(".txtCompanyNumber").attr("id", "company"+row).attr("name", "aPlines["+row+"].companyNumber");
		$(this).find(".txtCompanyNumber").attr("onfocus", "setDefaultValue(this,"+row+");");
		$(this).find(".txtDivisionNumber").attr("id", "division"+row).attr("name", "aPlines["+row+"].divisionNumber");
		$(this).find(".txtAccountNumber").attr("id", "account"+row).attr("name", "aPlines["+row+"].accountNumber");
		$(this).find(".spanCompanyNames").attr("id", "spanCompany"+row);
		$(this).find(".spanDivisionNames").attr("id", "spanDivision"+row);
		$(this).find(".spanAccountNames").attr("id", "spanAccount"+row);
		$(this).find(".txtAmount").attr("id", "amount"+row).attr("name", "aPlines["+row+"].amount");
		$(this).find(".txtDescription").attr("id", "description"+row).attr("name", "aPlines["+row+"].description");
	});
	aPLineCurrentIndex = currentRow + 1;
}

function updateTotalAmount() {
	var total = 0;
	var formatAmt = 0;
	console.log("updating total amount.");
	$("#aPLine tbody tr").each(function(row) {
		var amount = $("#amount"+row).val();
		if(typeof amount != "undefined") {
			formatAmt = amount.replace(/\,/g,"");
			total += $.trim(formatAmt) != "" ? parseFloat(formatAmt) : 0;
		}
	});
	$("#spanTotalAmount").html(accounting.formatMoney(total));
}

function getTermAndAcctCombi(termId, dueDate) {
	var supplierAccountId = $("#supplierAccountId").val();
	if(supplierAccountId != "") {
		var uri = contextPath+"/getTerm";
		if(termId != null) {
			//If term id is present, get term by id.
			uri += "?termId="+termId;
		} else {
			//Get the default term of the supplier account.
			uri += "?supplierAccountId="+supplierAccountId;
		}
		$.ajax({
			url: uri,
			success : function (term) {
				$("#termId").val(term.id).attr("selected", true);
				if(dueDate == null) {
					computeDueDate();
				}
				$.ajax({
					url: contextPath+"/aPInvoiceForm/getAccountCombination?supplierAccountId="+supplierAccountId,
					success: function(ac){
						accountCombination = ac;
						accountCombinationCache.push (accountCombination);
					},
					error : function (error) {
						console.log(error);
					},
					dataType: "json",
				});
			},
			error : function (error) {
				console.log(error);
			},
			dataType: "json",
		});
	}
}

function filterSuplierAccounts (selectSupplierAcct, termId, dueDate) {
	if($.trim($("#supplierId").val()) != "") {
		var supplierId = $("#hdnSupplierId").val();
		var getSupplierAcctsUrl = contextPath+"/getSupplierAccounts?supplierId="+supplierId;
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
						var aCId = 0;
						for (var index = 0; index < data.length; index++){
							var rowObject =  data[index];
							var id = rowObject["id"];
							if (id == selectSupplierAcct){
								aCId = rowObject["defaultDebitACId"];
								if ("${aPInvoice.id}" == 0) {
									var defaultTerm = rowObject["termId"];
									$("#termId").val(defaultTerm).attr('selected',true);
								}
								break;
							}
						}
						var uri = contextPath + "/getAccountCombination?aCId="+aCId;
						$.ajax({
							url: uri,
							success: function(responseText){
								accountCombination = responseText[0];
								accountCombinationCache.push (accountCombination);
							},
							dataType: "json",
						});
						getTermAndAcctCombi(termId, dueDate);
					}
			};
			loadPopulate (getSupplierAcctsUrl, false, selectSupplierAcct, "supplierAccountId", optionParser, postHandler);
	} else {
		$("#supplierAccountId").empty();
	}
}

function updateCompanyDescription(textBox) {
	if ($(textBox).val() != "") {
		var companyNumber = $(textBox).val();
		var company = getCompanyFromCache(companyNumber);
		if (company != null) {
			var id = $(textBox).attr("id");
			$(textBox).val(company.companyNumber);
			$("#spanCompany"+id).text (company.name + " - ");
		}
	}
}

function getCompanyFromCache (companyNumber) {
	if (accountCombinationCache.length > 0) {
		for (var index = 0; index < accountCombinationCache.length; index++){
			var accountCombination = accountCombinationCache[index];
			if (accountCombination.company.companyNumber == companyNumber){
				return accountCombination.company;
			}
		}
	}
	return null;
}

function setDefaultValue (textBox, currentIndexNumber) {
	//Set default value for description
	if($("#description").val() != "") {
		var amount = $("#amount"+currentIndexNumber).val();
		$("#amount"+currentIndexNumber).val(amount != "" ? amount : accounting.formatMoney(0.0));
		$("#description"+currentIndexNumber).val($("#description").val());
	}
}

function addAPLine (numOfRows) {
	for (var i=0; i<numOfRows; i++) {
		var newRow = "<tr id='trReadOnly'>";
		//Delete and search for account combination
		newRow += "<td class='tdProperties' align='left'>";
		newRow += "<img class='imgDelete' id='imgDelete"+aPLineCurrentIndex+"' src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deleteRow(this);'/>";
		newRow += "&nbsp;";
		newRow += "<img class='imgSearchAcctCombi' id='imgSearch"+aPLineCurrentIndex+"' onclick='showAcctCombiSelection(this, \""+aPLineCurrentIndex+"\");' src='${pageContext.request.contextPath}/images/search_active.png' />";
		newRow += "</td>";

		//Number
		newRow += "<td class='tdProperties'><input type='hidden' value="+(aPLineCurrentIndex + 1)+"/></td>";

		//Company 
		newRow += "<td class='tdProperties' valign='top' style='white-space: nowrap;'>";
		newRow += "<input name='aPlines["+aPLineCurrentIndex+"].companyNumber' id='company"+aPLineCurrentIndex+"' onfocus='setDefaultValue(this, \""+aPLineCurrentIndex+"\");' onkeydown='showCompanies(this, \""+aPLineCurrentIndex+"\");' onkeyup='showCompanies(this, \""+aPLineCurrentIndex+"\");' onblur='validateCompanyNo(this);' style='width: 55px;' class='txtCompanyNumber' maxLength='5'/>";
		newRow += "<input type='hidden' id='hdnCompanyId"+aPLineCurrentIndex+"' class='hdnCompanyIds' name='aPlines["+aPLineCurrentIndex+"].companyId'>";
		newRow +="</td>";

		//Division
		newRow += "<td class='tdProperties' valign='top' style='white-space: nowrap;'>";
		newRow += "<input name='aPlines["+aPLineCurrentIndex+"].divisionNumber' id='division"+aPLineCurrentIndex+"' onkeydown='showDivisions(this, \""+aPLineCurrentIndex+"\");' onkeyup='showDivisions(this, \""+aPLineCurrentIndex+"\");' onblur='validateDivisionNo(this);' style='width: 42px;' class='txtDivisionNumber' maxLength='5'/>";
		newRow += "<input type='hidden' id='hdnDivisionId"+aPLineCurrentIndex+"' class='hdnDivisionIds' name='aPlines["+aPLineCurrentIndex+"].divisionId'>";
		newRow +="</td>";

		//Account
		newRow += "<td class='tdProperties' valign='top' style='white-space: nowrap;'>";
		newRow += "<input name='aPlines["+aPLineCurrentIndex+"].accountNumber' id='account"+aPLineCurrentIndex+"' onkeydown='showAccts(this, \""+aPLineCurrentIndex+"\");' onkeyup='showAccts(this, \""+aPLineCurrentIndex+"\");' onblur='validateAccountNo(this);' style='width: 80px;' class='txtAccountNumber' maxLength='10'/>";
		newRow += "<input type='hidden' id='hdnAccountId"+aPLineCurrentIndex+"' class='hdnAccountIds' name='aPlines["+aPLineCurrentIndex+"].accountId'>";
		newRow +="</td>";

		//Account combination
		newRow += "<td class='tdProperties'>";
		newRow += "<span id='spanCompany"+aPLineCurrentIndex+"' class='spanCompanyNames'></span> ";
		newRow += "<span id='spanDivision"+aPLineCurrentIndex+"' class='spanDivisionNames'></span> ";
		newRow += "<span id='spanAccount"+aPLineCurrentIndex+"' class='spanAccountNames'></span>";
		newRow +="</td>";

		//Amount
		newRow += "<td class='tdProperties' valign='top' style='width: 50px;'>";
		newRow += "<input name='aPlines["+aPLineCurrentIndex+"].amount' id='amount"+aPLineCurrentIndex+"' style='width: 150px; text-align: right;' class='txtAmount' maxLength='13'/>";
		newRow += "</td>";

		//Description
		newRow += "<td class='tdProperties' valign='top' style='border-right: none;'>";
		newRow += "<input name='aPlines["+aPLineCurrentIndex+"].description' id='description"+aPLineCurrentIndex+"' style='width: 200px;' class='txtDescription' maxLength='250'/>";
		newRow += "</td>";
		newRow += "</tr>";
		$("#aPLine tbody").append(newRow);
		aPLineCurrentIndex++;
	}
}

var isSaving = false;
function saveInvoice() {
	$("#referenceDocumentsJson").val($documentsTable.getData());
	if ($.trim($("#supplierId").val()) == "") {
		$("#hdnSupplierId").val("");
	}
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize()) {
		$("#spDocSizeMsg").text("");
		isSaving = true;
		parseAmount();
		$("#btnSaveAPInvoice").attr("disabled", "disabled");
		if (invoiceStatusId == APPROVED) {
			enableFields();
		}
		doPostWithCallBack ("invoiceForm", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				var name = $("#invoiceNumber").val();
				updateTable (formStatus);
				if("${pId}" == 0)
					dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var selectSupplierAcct = $("#supplierAccountId option:selected").val();
				var dueDate = $("#dueDate").val();
				var termId = $("#termId").val();
				var supplier = $("#supplierId").val();

				if("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}

				$("#txtInvoiceStatus").val("CREATED");
				$("#supplierId").val(supplier);
				if(typeof selectSupplierAcct != "undefined"  || selectSupplierAcct != null) {
					filterSuplierAccounts(selectSupplierAcct, termId, dueDate);
				}
				parseAmount();
				formatAmount();
				reloadCombi();
				deleteRows();
				updateApLineIndex ();
				var recountedRows = $("#aPLine tbody tr").length;
	            if(recountedRows < minimumRows) {
	               var rowsToAdd = minimumRows - recountedRows;
	               addAPLine(rowsToAdd);
	               processAplineTableRow(recountedRows);
	            }
	            $("#aPLine tbody tr").each(function(i) {
	                $.trim($(this).find("td").eq(1).html(i+1));
	            });
	            // Always enable the first row of the AP Line.
	            enableEntries($("#aPLine tbody tr:first"));
	            updateTotalAmount();
	            $("#seqNumber").val("${sequenceNo}");
	            initializeDocumentsTbl();
	            if (isEdit == false){
					if(invoiceStatusId == 2)
						$("#btnSaveAPInvoice").val ("Cancel");
					else
						$("#btnSaveAPInvoice").val ("Approve");
	            }
	            isSaving = false;
			}
		});
	} else if (checkExceededFileSize()) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function deleteRows() {
	 $("#aPLine tbody tr").each(function() {
		if (!$.trim($(this).find(".txtCompanyNumber").val()) && !$.trim($(this).find(".txtDivisionNumber").val())
				&& !$.trim($(this).find(".txtAccountNumber").val()) && $(this).find(".txtAmount").val() == 0.0
				&& !$.trim($(this).find(".txtDescription").val()))
			$(this).remove();
     });
}

function parseAmount() {
	console.log("parsing amount");
	if ($.trim($("#totalAmount").val()) == "")
		$("#totalAmount").val("0.0");
	else {
		var formatAmt = $("#totalAmount").val().replace(/\,/g,"");
		$("#totalAmount").val(parseFloat(formatAmt,10));
	}
	$("#aPLine tbody tr").each(function(row) {
		var apLineAmount = $("#amount"+row).val();
		if(apLineAmount != "" && typeof apLineAmount != "undefined") {
			var formatAmt = apLineAmount.replace(/\,/g,"");
			$(this).find(".txtAmount").val(parseFloat(formatAmt,10));
		}
	});
}

function formatAmount() {
	if($("#totalAmount").val() != "") {
		$("#totalAmount").val(Number($("#totalAmount").val()));
		$("#totalAmount").val(accounting.formatMoney($("#totalAmount").val()));
	}
	$("#aPLine tbody tr").each(function() {
		var txtAmount = $(this).find("td").eq(6).find(".txtAmount");
		if(txtAmount.val() != "") {
			$(txtAmount).val(Number(txtAmount.val()));
			$(txtAmount).val(accounting.formatMoney($(txtAmount).val()));
		}
	});
}

function processAplineTableRow (setRows) {
    var index = 0;
    $("#aPLine tbody tr").each(function(i) {
		$.trim($(this).find("td").eq(1).html(i+1));
		if (index >= setRows) {
			disableEntries($(this));
		}
		index++;
    });
}

function Term (id, days) {
	this.id = id;
	this.days = days;
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
	if(!isNaN( glDate.getMonth()) && !isNaN( glDate.getDate()) && !isNaN(glDate.getFullYear())){
		$("#dueDate").val ((glDate.getMonth() + 1) +"/"+glDate.getDate()+
				"/"+glDate.getFullYear());
	}else{
		$("#dueDate").val ("");
	}
}

function checkAndSetDecimal2(txtObj) {
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

function doAfterSelection (companyNumber, divisionNumber, accountNumber) {
	isShowingAccountCombi = false;
	$("#company"+selectAPLineRow).val(companyNumber);
	$("#division"+selectAPLineRow).val(divisionNumber);
	$("#account"+selectAPLineRow).val(accountNumber);
	processAccountCombinationName (selectAPLineRow, companyNumber, divisionNumber, accountNumber);
	$("#amount"+selectAPLineRow).focus();
	$("#divComboList").css("display", "none");
}

function processAccountCombinationName (apLineRow, companyNumber, divisionNumber, accountNumber) {
	var cachedCombination = getCombination (companyNumber, divisionNumber, accountNumber);
	if (cachedCombination == null) {
		var uri = contextPath + "/getAccountCombination?companyNumber="
		+companyNumber+"&divisionNumber="+divisionNumber+"&accountNumber="+accountNumber;
		$.ajax({
			url: uri,
			success: function(responseText){
				var accountCombination = responseText[0];
				accountCombinationCache.push (accountCombination);
				setAccountCombinationName (apLineRow, accountCombination, companyNumber, divisionNumber, accountNumber);
			},
			dataType: "json",
		});
	} else {
		setAccountCombinationName (apLineRow, cachedCombination, companyNumber, divisionNumber, accountNumber);
	}
}

function setAccountCombinationName (apLineRow, accountCombination, companyNumber, divisionNumber, accountNumber) {
	if(accountCombination != null) {
		if($.trim(companyNumber) != ""){
			$("#spanCompany"+apLineRow).text (accountCombination.company.name + " - ");
		}
		if($.trim(divisionNumber) != ""){
			$("#spanDivision"+apLineRow).text (accountCombination.division.name + " - ");
		}
		if($.trim(accountNumber) != ""){
			$("#spanAccount"+apLineRow).text (accountCombination.account.accountName);
		}
	}
}

function getCombination (companyNumber, divisionNumber, accountNumber) {
	if (accountCombinationCache.length > 0) {
		for (var index = 0; index < accountCombinationCache.length; index++){
			var accountCombination = accountCombinationCache[index];
			if(accountCombination != null) {
				if (accountCombination.company.companyNumber == companyNumber &&
						accountCombination.division.number == divisionNumber &&
						accountCombination.account.number == accountNumber) {
					return accountCombination;
				}
			}
		}
	}
	return null;
}

function loadAjaxData (uri, type, callbackFunction, errorFunction) {
	$.ajax({
		url: uri,
		success : callbackFunction,
		error : errorFunction,
		dataType: type,
	});
}

function showSuppliers () {
	$("#supplierAccountId").empty();
	var supplierName = encodeURIComponent($.trim($("#supplierId").val()));
	if($("#supplierId").val() != "") {
		var uri = contextPath + "/getSuppliers/new?name="+supplierName;
		$("#supplierId").autocomplete({
			source: uri,
			select: function( event, ui ) {
				$("#hdnSupplierId").val(ui.item.id);
				$("#spanSupplierError").text("");
				$(this).val(ui.item.name);
				return false;
			}, minLength: 2,
			change: function(event, ui){
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
}

function getSupplier() {
	var supplierName = encodeURIComponent($.trim($("#supplierId").val()));
	$.ajax({
		url: contextPath + "/getSuppliers/new?name="+supplierName+"&isExact=true",
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

function showCompanies($textBox, columnIndexNumber){
	selectAPLineRow = columnIndexNumber;
	var companyName = $.trim($($textBox).val());
	var supplierId = $("#hdnSupplierId").val();
	var uri = contextPath + "/getSuppliers/getCompanies?supplierId="+supplierId+"&companyName="+companyName;
	$($textBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnCompanyId"+selectAPLineRow).val(ui.item.id);
			$(this).val(ui.item.companyNumber);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#hdnCompanyId"+selectAPLineRow).val(ui.item.id);
						$(this).val(ui.item.companyNumber);
					}
				},
				dataType: "json",
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.companyNumber+"-"+item.name+ "</a>" )
			.appendTo( ul );
	};
}

function showDivisions($textBox, columnIndexNumber) {
	selectAPLineRow = columnIndexNumber;
	var companyId = $("#hdnCompanyId"+selectAPLineRow).val();
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

function showAccts($textBox, columnIndexNumber) {
	selectAPLineRow = columnIndexNumber;
	var accountName = $.trim($($textBox).val());
	var companyId = $("#hdnCompanyId"+selectAPLineRow).val();
	var divisionId = $("#hdnDivisionId"+selectAPLineRow).val();
	var uri = contextPath + "/getAccounts/byName?accountName="+accountName+"&companyId="
			+companyId+"&divisionId="+divisionId+"&limit=10";
	$($textBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnAccountId"+selectAPLineRow).val(ui.item.id);
			$(this).val(ui.item.number);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#hdnAccountId"+selectAPLineRow).val(ui.item.id);
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
</script>
</head>
<body>
	<div id="divComboList" style="display: none"></div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="aPInvoice" id="invoiceForm">
			<div class="modFormLabel">AP Invoice <span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId" />
			<form:hidden path="sequenceNumber"/>
			<form:hidden path="supplierId" id="hdnSupplierId"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="ebObjectId"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number:</td>
							<td class="value">
								<input type="text" id="seqNumber" class="textBoxLabel" readonly="readonly"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status:</td>
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
					<legend>Invoice Header</legend>
					<table class="formTable">
						<form:hidden path="id" />
						<tr>
							<td class="labels">* Invoice Type</td>
							<td class="value"><form:select path="invoiceTypeId"
									id="invoiceTypeId" cssClass="frmSelectClass">
									<form:options items="${invoiceTypes}" itemLabel="name"
										itemValue="id" />
								</form:select></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="invoiceTypeId"
									cssClass="error" style="margin-left: 12px;"/></td>
						</tr>
						<tr>
							<td class="labels">* Invoice No</td>
							<td class="value"><form:input path="invoiceNumber"
									id="invoiceNumber" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="invoiceNumber" 
									cssClass="error" style="margin-left: 12px;"/></td>
						</tr>
						<tr>
							<td class="labels">* Supplier Name</td>
							<td class="value">
							<input id=supplierId class="input"
								onkeydown="showSuppliers();" onkeyup="showSuppliers();"
								onblur="getSupplier();" /> 
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="supplierId"
									cssClass="error" style="margin-left: 12px;"/></td>
						</tr>
						<tr>
							<td class="labels">* Supplier's Account</td>
							<td class="value"><form:select path="supplierAccountId"
									id="supplierAccountId" cssClass="frmSelectClass"
									onchange="getTermAndAcctCombi();">
								</form:select></td>
						</tr>

						<tr>
							<td></td>
							<td colspan="2"><form:errors path="supplierAccountId"
									cssClass="error" style="margin-left: 12px;"/></td>
						</tr>
						<tr>
							<td class="labels">* Term</td>
							<td class="value"><form:select path="termId"
									cssClass="frmSelectClass" id="termId"
									onchange="computeDueDate();">
									<form:options items="${terms}" itemLabel="name" itemValue="id" />
								</form:select></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="termId" cssClass="error" style="margin-left: 12px;"/></td>
						</tr>
						<tr>
							<td class="labels">* Invoice Date</td>
							<td class="value"><form:input path="invoiceDate"
									id="invoiceDate" onblur="evalDate('invoiceDate')"
									style="width: 120px;" class="dateClass2" /> <img id="imgDate1"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('invoiceDate')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="invoiceDate"
									cssClass="error" style="margin-left: 12px;"/></td>
						</tr>

						<tr>
							<td class="labels">* GL Date</td>
							<td class="value"><form:input path="glDate" id="glDate"
									onblur="evalDate('glDate'); computeDueDate();" style="width: 120px;"
									class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('glDate')" style="cursor: pointer"
								style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="glDate" cssClass="error" style="margin-left: 12px;"/></td>
						</tr>

						<tr>
							<td class="labels">* Due Date</td>
							<td class="value"><form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate')"
								style="width: 120px;" class="dateClass2" /> <img id="imgDate3"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('dueDate')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="dueDate" cssClass="error" style="margin-left: 12px;"/></td>
						</tr>

						<tr>
							<td class="labels">* Description</td>
							<td class="value"><form:textarea path="description"
									id="description" class="input" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="description"
									cssClass="error" style="margin-left: 12px;"/></td>
						</tr>
						<tr>
							<td class="labels">* Amount</td>
							<td class="value">
								<form:input path="amount" id="totalAmount" class="numeric" size="20" maxLength="13"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="amount" cssClass="error" style="margin-left: 12px;"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AP Lines</legend>
					<table id="aPLine" class="dataTable">
						<thead>
							<tr>
								<th width="7%"></th>
								<th width="3%">#</th>
								<th width="5%">Company</th>
								<th width="5%">Division</th>
								<th width="5">Account</th>
								<th width="35%">Combination</th>
								<th width="18%">Amount</th>
								<th width="25%" style="border-right: none;">Description</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${aPInvoice.aPlines}" var="aPLine" varStatus="status">
								<tr>
									<!-- Delete -->
									<td class="tdProperties" align="left"><img class='imgDelete' src='${pageContext.request.contextPath}/images/delete_active.png' onclick="deleteRow(this);"/>
										<img class='imgSearchAcctCombi' id="imgSearch${status.index}" onclick='showAcctCombiSelection(this,${status.index});'
											src='${pageContext.request.contextPath}/images/search_active.png' /></td>
									<!-- Number -->
									<td class="tdProperties"><input type="hidden" value="${status.index + 1}"/></td>
									<!-- Company -->
									<td class="tdProperties" valign='top' style='white-space: nowrap;'>
										<input type="hidden" id='hdnCompanyId${status.index}' name='aPlines[${status.index}].companyId' value="${aPLine.companyId}" class="hdnCompanyIds" />
										<input type='text' name='aPlines[${status.index}].companyNumber' id='company${status.index}' value="${aPLine.companyNumber}"
										onfocus='setDefaultValue(this, ${status.index});' onblur="validateCompanyNo(this);" style='width: 55px;' class='txtCompanyNumber' maxLength='5'
										onkeydown='showCompanies(this, ${status.index});' onkeyup='showCompanies(this, ${status.index});'/></td>
									<!-- Division -->
									<td class="tdProperties" valign='top' style='white-space: nowrap;'>
										<input type="hidden" id='hdnDivisionId${status.index}' name='aPlines[${status.index}].divisionId' value="${aPLine.divisionId}" class="hdnDivisionIds"/>
										<input type='text' name='aPlines[${status.index}].divisionNumber' id='division${status.index}' value="${aPLine.divisionNumber}"
											onblur="validateDivisionNo(this);" style='width: 42px;' class='txtDivisionNumber' maxLength='5'
											onkeydown='showDivisions(this, ${status.index});' onkeyup='showDivisions(this, ${status.index});'/>
									</td>
									<!-- Account -->
									<td class="tdProperties" valign='top' style='white-space: nowrap;'>
										<input type='text' name='aPlines[${status.index}].accountNumber' id='account${status.index}' value="${aPLine.accountNumber}"
											onkeydown='showAccts(this, ${status.index});' onkeyup='showAccts(this, ${status.index});'
											onblur="validateAccountNo(this);" style='width: 80px;' class='txtAccountNumber' maxLength='10' />
									</td>
									<!-- Account Combination -->
									<td class="tdProperties"><span id='spanCompany${status.index}' class='spanCompanyNames'></span>
										<span id='spanDivision${status.index}' class='spanDivisionNames'></span>
										<span id='spanAccount${status.index}' class='spanAccountNames'></span>
									</td>
									<!-- Amount -->
									<td class="tdProperties" valign='top' style='width: 50px;'>
										<input type='text' name='aPlines[${status.index}].amount' 
										value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${aPLine.amount}' />" 
										style='width: 150px; text-align: right;'
											id="amount${status.index}" class='txtAmount' maxLength='13'/>
									</td>
									<!-- Description -->
									<td class="tdProperties" valign='top'>
										<input type='text' name='aPlines[${status.index}].description' id='description${status.index}' value="${aPLine.description}"
											style="width: 200px;" class='txtDescription' maxLength='250' />
									</td>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="6"></td>
								<td style="text-align: right;"><span id="spanTotalAmount">0.0</span></td>
							</tr>
						</tfoot>
					</table>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="aPlineMessage" cssClass="error" /></td>
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
						<td align="right" colspan="4"><input type="button" id="btnSaveAPInvoice"
							value="Save" onclick="saveInvoice();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>