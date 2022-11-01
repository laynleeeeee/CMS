<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>

<!-- Description: AP Invoice - Goods form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebForm.css"media="all">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.unitcosthandler.js"></script>
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

.footerTable {
 	color: black;
	text-align: right;
	font-family: sans-serif;
	font-style: normal;
	font-size: small;
	font-weight: bold;
	margin-left: auto;
	margin-right: auto;
}
</style>
<script type="text/javascript">
var terms = new Array();
var minimumRows = 4;
var invoiceLineCurrentIndex = "${fn:length(apInvoiceItemDto.apInvoice.apInvoiceItems)}";
var apInvoices = null;
var isShowingInvoices = false;
var selectedInvoiceRow = 0;
var SUPPLIER_ACCT_ID = 0;
var invoiceIds = "";
$(document).ready(function() {
	apInvoices = new Array();
	initializetOtherCharges();
	initializeDocumentsTbl();
	if("${apInvoiceItemDto.apInvoice.id}" > 0) {
		var dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${apInvoiceItemDto.apInvoice.dueDate}'/>";
		SUPPLIER_ACCT_ID = "${apInvoiceItemDto.apInvoice.supplierAccountId}";
		filterSuplierAccounts(SUPPLIER_ACCT_ID, "${apInvoiceItemDto.apInvoice.termId}", dueDate);

		var rowCount = $("#apInvoice tbody tr").length;
		if(invoiceLineCurrentIndex < minimumRows) {
			var rowsToAdd = minimumRows - rowCount;
			addApInvoices(rowsToAdd);
		}
		$("#txtTotalAmount").val(formatNumber(parseFloat("${apInvoiceItemDto.apInvoice.amount}")));
		var wtAcctSettingId = "${apInvoiceItemDto.apInvoice.wtAcctSettingId}";
		filterWtAcctSettings(wtAcctSettingId);
		$("#wtAcctSettingId").val(wtAcctSettingId);

		updateTotalAmount();
		updateOChargesAmt();

		var isComplete = "${apInvoiceItemDto.apInvoice.formWorkflow.complete}";
		var currentStatusId = "${apInvoiceItemDto.apInvoice.formWorkflow.currentStatusId}";
		if(isComplete == "true" || currentStatusId == 4) {
			$("#apInvoiceItemFormId :input").attr("disabled", "disabled");
		}
	} else {
		addApInvoices(minimumRows);
	}

	countRows();
	$("#apInvoice tbody tr:not(:first)").each(function(i) {
		disableEntries($(this));
	});

	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
});

function Term(id, days) {
	this.id = id;
	this.days = days;
}

$(function() {
	$(".txtInvoiceNumber").live("keydown", function(e) {
		if (e.which == 9 && !e.shiftKey) {
			var id = $(this).attr("id");
			var rowNumber = (id.replace ("invoiceNumber", ""));
			var currentRow = $(this).closest("tr");
			var nextRow = $(currentRow).next();
			if(++rowNumber == invoiceLineCurrentIndex || nextRow.length < 1) {
				addApInvoices(1);
				if(isShowingInvoices != true) {
					$(this).focus();
				}
				countRows();
			} else {
				enableEntries(nextRow);
			}
		}
	});

	$("#txtTotalAmount").live("blur", function (e) {
		var totalAmount = $("#txtTotalAmount").val();
		$("#txtTotalAmount").val(formatNumber(totalAmount));
	});

	$(".imgCloseComboList").live("click", function() {
		$("#divInvoiceList").css("display", "none");
		isShowingInvoices = false;
	});
});


function formatNumber(value) {
	return accounting.formatMoney(value, '', 4);
}

function addApInvoices(numOfRows) {
	for(var i = 0; i < numOfRows; i++) {
		var newRow = "<tr>";
		// Delete and search for invoices
		newRow += "<td class='tdProperties' align='center'>";
		newRow += "<img class='imgDelete' id='imgDelete"+invoiceLineCurrentIndex+"' onclick='deleteRow(this, \""+invoiceLineCurrentIndex+"\");' src='${pageContext.request.contextPath}/images/delete_active.png' />";
		newRow += "&nbsp;&nbsp;&nbsp;&nbsp;";
		newRow += "<img class='imgSearchInvoices' id='imgSearch"+invoiceLineCurrentIndex+"' onclick='showInvoices(this, \""+invoiceLineCurrentIndex+"\");' src='${pageContext.request.contextPath}/images/search_active.png' />";
		newRow += "</td>";

		// Row count
		newRow += "<td class='tdProperties'><input type='hidden' value="+(invoiceLineCurrentIndex + 1)+" id='number"+invoiceLineCurrentIndex+"'/></td>";

		// AP invoice id
		newRow += "<td style='display: none;'>";
		newRow += "<input name='apInvoice.apInvoiceItems["+invoiceLineCurrentIndex+"].apInvoiceId' id='apInvoiceId"+invoiceLineCurrentIndex+"' class='txtApInvoiceId' style='visibility: hidden;'/>";
		newRow += "</td>";

		// Invoice number
		newRow += "<td class='tdProperties' valign='top' style='white-space: nowrap;'>";
		newRow += "<input name='apInvoice.apInvoiceItems["+invoiceLineCurrentIndex+"].invoiceNumber' onkeydown='loadInvoiceNumbers(\""+invoiceLineCurrentIndex+"\");' onblur='setInvoiceNumber(\""+invoiceLineCurrentIndex+"\"); computeWtaxRowAmount();' id='invoiceNumber"+invoiceLineCurrentIndex+"' style='width: 100%;' class='txtInvoiceNumber'/>";
		newRow += "</td>";

		// Amount
		newRow += "<td class='tdProperties' align='right' valign='top' style='width: 50px;'>";
		newRow += "<input name='apInvoice.apInvoiceItems["+invoiceLineCurrentIndex+"].amount' id='amount"+invoiceLineCurrentIndex+"' style='width: 100%; text-align: right;' onblur='setAndUpdateAmount(this);' class='txtAmount' readonly='readonly'/>";
		newRow += "</td>";

		newRow += "</tr>";
		$("#apInvoice tbody").append(newRow);
		$("#apInvoice tbody tr:last").find("td:last").css("border-right", "none");
		invoiceLineCurrentIndex++;
	}
}

function setAndUpdateAmount(elem) {
	var id = $(elem).attr("id");
	var currentRowNumber = id.replace("amount", "");
	var inputedAmount = $("#amount"+currentRowNumber).val();
	var invoice = apInvoices[currentRowNumber];
	if(typeof invoice != "undefined") {
		invoice.amount = inputedAmount;
		apInvoices[currentRowNumber] = invoice;
	}
	$(elem).val(formatNumber($(elem).val()));
}

function countRows() {
	$("#apInvoice tbody tr").each(function(i) {
		$.trim($(this).find("td").eq(1).html(++i));
	});
}

function updateTotalAmount() {
	var total = 0;
	$("#apInvoice tbody tr").each(function(i) {
		var $amount = $(this).find(".txtAmount");
		total += accounting.unformat($amount.val());
	});
	$("#spanTotalAmount").html(formatNumber(total));
}

function isExistingInvoice(selectInvoiceNo, invoiceNumber, selectedRow) {
	var selectInvNoSource = selectInvoiceNo.split(" ");
	if(apInvoices.length != 0) {
		for (var index = 0; index < apInvoices.length; index++) {
			var storedInvoice = apInvoices[index];
			if(typeof storedInvoice == "undefined" && selectInvoiceNo.toUpperCase() == invoiceNumber.toUpperCase()) {
				return true;
			} else if(storedInvoice.invoiceNumber.toUpperCase().localeCompare(invoiceNumber.toUpperCase()) == 0
					&& storedInvoice.shortDescription.toUpperCase().localeCompare(selectInvNoSource[0]) == 0
					&& selectedRow != index) {
				return true;
			}
		}
	}
}

function alertDuplicateInvoice() {
	alert("Invoice number has already been selected. \nPlease choose another.");
}

function alertNoSupplierAcct() {
	alert("No supplier account selected.");
}

function loadInvoiceNumbers(columnIndexNumber) {
	$("#apInvoiceErrorMessage").text("");
	selectedInvoiceRow = columnIndexNumber;
	var row = $("#"+selectedInvoiceRow).parent("td").parent("tr");
	var rowNumber = row.rowNumber;
	var invoiceNumber = $("#invoiceNumber"+selectedInvoiceRow).val();
	var supplierAccountId = $("#supplierAccountId").val();
	var uri = contextPath+"/apInvoiceItem/getReceivingReports?supplierAccountId="+supplierAccountId
			+ "&invoiceNumber="+processSearchName(invoiceNumber);
	$(".txtApInvoiceId").each(function(i) {
		if(i != rowNumber){
			invoiceIds += $(this).val() + ";";
		}
	});
	uri += "&invoiceIds="+invoiceIds+"&formId="+$("#hdnFormId").val()+"&isExact=false";
	if(supplierAccountId != null) {
		$("#invoiceNumber"+selectedInvoiceRow).autocomplete({
			source: uri,
			select: function( event, ui ) {
				$(this).val(ui.item.referenceNo);
				$("#apInvoiceId"+selectedInvoiceRow).val(ui.item.id);
				$("#amount"+selectedInvoiceRow).val(ui.item.amount);
				return false;
			}, minLength: 2
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

function setInvoiceNumber(columnIndexNumber) {
	$("#apInvoiceErrorMessage").text("");
	selectedInvoiceRow = columnIndexNumber;
	var row = $("#"+selectedInvoiceRow).parent("td").parent("tr");
	var rowNumber = row.rowNumber;
	var supplierAccountId = $("#supplierAccountId").val();
	var invoiceNumber = $.trim($("#invoiceNumber"+selectedInvoiceRow).val());
	if(invoiceNumber != "") {
		var uri = contextPath+"/apInvoiceItem/getReceivingReports?supplierAccountId="+supplierAccountId
			+ "&invoiceNumber="+processSearchName(invoiceNumber);
		$(".txtApInvoiceId").each(function(i) {
			if(i != rowNumber){
				invoiceIds += $(this).val() + ";";
			}
		});
		uri += "&invoiceIds="+invoiceIds+"&formId="+$("#hdnFormId").val()+"&isExact=true";
		$.ajax({
			url: uri,
			success : function(invoices) {
				if (invoices != null && invoices[0] != undefined) {
					$("#invoiceNumber"+selectedInvoiceRow).val(invoices[0].referenceNo);
					$("#apInvoiceId"+selectedInvoiceRow).val(invoices[0].id);
					$("#amount"+selectedInvoiceRow).val(invoices[0].amount);
				} else {
					$("#apInvoiceId"+selectedInvoiceRow).val("");
					if(invoiceNumber != "") {
						$("#apInvoiceErrorMessage").text("Invalid invoice.");
					}
				}
			},
			dataType: "json"
		});
	}
}

function deleteRow(elem, columnIndexNumber) {
	$("#apInvoiceErrorMessage").text("");
	var deleteRow = $(elem).closest("tr");
	delete apInvoices[columnIndexNumber];
	$(deleteRow).remove();
	var rowCount = $('#apInvoice tbody tr').length;
	if(rowCount < minimumRows) {
		var rowsToAdd = minimumRows - rowCount;
		addApInvoices(rowsToAdd);
	}
	enableEntries($("#apInvoice tbody tr:first"));
	processApInvoiceTableRow(rowCount);
	updateTotalAmount();
}

function processApInvoiceTableRow(setRows) {
	var index = 0;
	$("#apInvoice tbody tr").each(function(i) {
		$.trim($(this).find("td").eq(1).html(i+1));
		if(index >= setRows) {
			disableEntries($(this));
		}
		index++;
	});
}

function showInvoices(elem, columnIndexNumber) {
	isShowingInvoices = true;
	var position = $(elem).closest("tr").find(elem).position();
	var offset = $(elem).closest("tr").find(elem).offset();
	selectedInvoiceRow = columnIndexNumber;
	var supplierAccountId = $("#supplierAccountId").val();
	if(supplierAccountId != "" && typeof supplierAccountId != undefined
		&& supplierAccountId != null) {
		var path = contextPath+"/apInvoiceItem/loadReceivingReports?supplierAccountId="+supplierAccountId
				+"&formId="+$("#hdnFormId").val();
		$("#divInvoiceList").load(path);
		if(!$("#divInvoiceList").is(":visible")) {
			$("#divInvoiceList").css("background-color", "#FFFFFF")
			.css("border", "1px solid #999999")
			.css("cursor", "default")
			.css("position","absolute")
			.css("left", position.left + "px")
			.css("top", position.top + "px")
			.css("padding", "25px 25px 20px")
			.show();
		}
	} else {
		alertNoSupplierAcct();
	}
}

function disableEntries(elem) {
	$(elem).closest("tr").find(".txtInvoiceNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtAmount").attr("disabled", "disabled");
	$(elem).closest("tr").find(".imgSearchInvoices").attr("onclick", "").unbind("onclick");
	$(elem).addClass("divVeil");
}

function enableEntries(elem) {
	$(elem).closest("tr").find(".txtInvoiceNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtAmount").removeAttr("disabled");
	var currentRow = $(elem).find(".imgSearchInvoices").attr("id").replace("imgSearch", "");
	$(elem).closest("tr").find(".imgSearchInvoices").attr("onclick", "showInvoices(this,"+currentRow+");");
	$(elem).removeClass("divVeil");
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
				{"name" : "fileSize", "varType" : "double"}
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
			{"title" : "Upload Image",
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
		convertDocToBase64($(this), fileSize, $("#referenceDocsMgs"), $("#documentsTable"));
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

	resizeTable("#documentsTable");
}

function resizeTable(tableId){
	var $tbl = $(tableId+" table");
	$($tbl).attr("width", "100%");
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", "6");
}

function enableTableEntries() {
	$("#apInvoice tbody tr").each(function() {
		$(this).find(".txtInvoiceNumber").removeAttr("disabled");
		$(this).find(".txtAmount").removeAttr("disabled");
		$(this).removeClass("divVeil");
	});
}

var isSaving = false;
function saveForm() {
	$("#spDocSizeMsg").text("");
	$("#referenceDocumentsJson").val($documentsTable.getData());
	if ($.trim($("#supplierId").val()) == "") {
		$("#hdnSupplierId").val("");
	}
	var apLineErrorMsg = $("#apInvoiceErrorMessage").text();
	var refDocErrorMsg = $("#referenceDocsMgs").text();
	var hasExceededFileSize = checkExceededFileSize($("#documentsTable"));
	if(isSaving == false && refDocErrorMsg == "" && !hasExceededFileSize && apLineErrorMsg == "") {
		isSaving = true;
		parseAmount();
		enableTableEntries();
		$("#btnSaveAPInvoice").attr("disabled", "disabled");
		$("#apInvoiceLinesJson").val($otherChargesTable.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		doPostWithCallBack ("apInvoiceItemFormId", "form", function(data) {
			if(data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable(formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var selectSupplierAcct = $("#supplierAccountId option:selected").val();
				var dueDate = $("#dueDate").val();
				var termId = $("#termId").val();
				var supplier = $("#supplierId").val();
				var status = $("#txtInvoiceStatus").val();
				var wtAcctSettingId = $("#wtAcctSettingId").val();
				if("${apInvoiceItemDto.apInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				$("#txtInvoiceStatus").val(status);
				$("#supplierId").val(supplier);
				filterWtAcctSettings(wtAcctSettingId);
				if(typeof selectSupplierAcct != "undefined"  || selectSupplierAcct != null) {
					filterSuplierAccounts(selectSupplierAcct, termId, dueDate);
				}
				deleteRows();
				var rowCount = $("#apInvoice tbody tr").length;
				if(rowCount < minimumRows) {
					var rowsToAdd = minimumRows - rowCount;
					addApInvoices(rowsToAdd);
				}
				processApInvoiceTableRow(rowCount);
				formatAmount();
				updateTotalAmount();
				$("#apInvoice tbody tr:not(:first)").each(function(i) {
					disableEntries($(this));
				});
				initializetOtherCharges();
				initializeDocumentsTbl();
				updateOChargesAmt();
			}
			isSaving = false;
		});
	} else if(hasExceededFileSize) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function deleteRows() {
	$("#apInvoice tbody tr").each(function() {
		if(!$.trim($(this).find(".txtInvoiceNumber").val())
				&& $(this).find(".txtAmount").val() == 0.0)
			$(this).remove();
	});
}


function formatAmount() {
	if($("#txtTotalAmount").val() != "") {
		var totalAmount = $("#txtTotalAmount").val();
		$("#txtTotalAmount").val(formatNumber(parseFloat(totalAmount)));
	}
	$("#aPLine tbody tr").each(function() {
		var $amount = $(this).find("td").eq(3).find(".txtAmount");
		var lineAmount = accounting.unformat($amount.val());
		if (lineAmount != "") {
			$amount.val(formatNumber(parseFloat(lineAmount)));
		}
	});
}

function parseAmount() {
	var totatAmount = accounting.unformat($("#txtTotalAmount").val());
	$("#txtTotalAmount").val(totatAmount);

	$("#apInvoice tbody tr").each(function() {
		var lineAmount = accounting.unformat($(this).find(".txtAmount").val());
		if(lineAmount != "") {
			$(this).find(".txtAmount").val(lineAmount);
		}
	});
}

function doAfterSelection(invoiceNumber, amount, balance, invoiceId) {
	var invoiceAmt = balance == 0 ? amount : balance;
	$("#apInvoice tbody tr").each(function(i) {
		var selectInvoiceNo = $(this).find(".txtInvoiceNumber").val();
		var selectInvoiceNoRow = $(this).find(".txtInvoiceNumber").attr("id").replace("invoiceNumber", "");
		if(i == selectedInvoiceRow) {
			if(isExistingInvoice(selectInvoiceNo, invoiceNumber, selectedInvoiceRow) == true) {
				alertDuplicateInvoice();
				return false;
			} else {
				var storedInvoice = apInvoices[selectedInvoiceRow];
				var invoice = new Object();
				if (storedInvoice == null || typeof storedInvoice == "undefined") {
					$("#invoiceNumber"+selectedInvoiceRow).val(invoiceNumber);
					$("#apInvoiceId"+selectedInvoiceRow).val(invoiceId);
					$("#amount"+selectedInvoiceRow).val(invoiceAmt);
					invoice.invoiceNumber = invoiceNumber;
					invoice.amount = invoiceAmt;
					apInvoices.push(invoice);
				} else if (storedInvoice.invoiceNumber.toUpperCase().localeCompare(invoiceNumber.toUpperCase()) == 0 ||
						storedInvoice.invoiceNumber == invoiceNumber) {
					$("#amount"+selectedInvoiceRow).val(storedInvoice.amount);
				} else if (storedInvoice.invoiceNumber.toUpperCase() != invoiceNumber.toUpperCase()) {
					$("#invoiceNumber"+selectedInvoiceRow).val(invoiceNumber);
					$("#apInvoiceId"+selectedInvoiceRow).val(invoiceId);
					$("#amount"+selectedInvoiceRow).val(invoiceAmt);
					invoice.invoiceNumber = invoiceNumber;
					invoice.amount = invoiceAmt;
					apInvoices[selectedInvoiceRow] = invoice;
				}
			}
			closeInvoiceSelector(selectedInvoiceRow);
		} else {
			if(invoiceNumber != null && typeof invoiceNumber != undefined) {
				if(invoiceNumber.toUpperCase().localeCompare(selectInvoiceNo.toUpperCase()) == 0) {
					if(selectedInvoiceRow != selectInvoiceNoRow) {
						alertDuplicateInvoice();
						return false;
					} else {
						closeInvoiceSelector(selectedInvoiceRow);
					}
				} else if(invoiceNumber.toUpperCase() != selectInvoiceNo.toUpperCase() && selectedInvoiceRow == selectInvoiceNoRow){
					$("#invoiceNumber"+selectedInvoiceRow).val(invoiceNumber);
					$("#apInvoiceId"+selectedInvoiceRow).val(invoiceId);
					$("#amount"+selectedInvoiceRow).val(invoiceAmt);
					var invoice = new Object();
					invoice.invoiceNumber = invoiceNumber;
					invoice.amount = invoiceAmt;
					apInvoices[selectedInvoiceRow] = invoice;
					closeInvoiceSelector(selectedInvoiceRow);
				}
			} else {
				alert("No invoice/s available.");
				return false;
			}
		}
	});
}

function closeInvoiceSelector(selectedInvoiceRow) {
	$("#divInvoiceList").css("display", "none");
	$("#amount"+selectedInvoiceRow).focus();
	isShowingInvoices = false;
	updateTotalAmount();
	return false;
}

function showSuppliers() {
	$("#spanSupplierError").text("");
	$("#supplierAccountId").empty();
	var supplierName = processSearchName($.trim($("#supplierId").val()));
	if($("#supplierId").val() != "") {
		var uri = contextPath+"/getSuppliers/new?name="+supplierName;
		$("#supplierId").autocomplete({
			source: uri,
			select: function( event, ui ) {
				$("#hdnSupplierId").val(ui.item.id);
				$(this).val(ui.item.name);
				return false;
			}, minLength: 2,
			change: function(event, ui){
				$.ajax({
					url: uri,
					success : function(item) {
						getSupplier();
					},
					error : function(error) {
						$("#spanSupplierError").text("Please select a valid supplier.");
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
	var supplierName = processSearchName($.trim($("#supplierId").val()));
	$.ajax({
		url: contextPath+"/getSuppliers/new?name="+supplierName+"&isExact=true",
		success : function(supplier) {
			if (supplier != null && supplier[0] != undefined) {
				$("#hdnSupplierId").val(supplier[0].id);
				$("#supplierId").val(supplier[0].name);
				$("#supplierVatTypeId").val(supplier[0].busRegTypeId);
				if($("#hdnSupplierId").val() != SUPPLIER_ACCT_ID) {
					clearApInvoices();
					SUPPLIER_ACCT_ID = $("#hdnSupplierId").val();
				}
			} else {
				$("#hdnSupplierId").val("");
				$("#supplierId").val("");
				$("#supplierVatTypeId").val("");
				clearApInvoices();
			}
			filterSuplierAccounts();
		},
		error : function(error) {
			$("#supplierId").val("");
			$("#hndSupplierId").val("");
			$("#supplierVatTypeId").val("");
			$("#supplierAccountId").empty();
		},
		dataType: "json"
	});
}

function clearApInvoices() {
	invoiceIds = "";
	$("#apInvoice tbody").html("");
	addApInvoices(minimumRows);
	processApInvoiceTableRow(minimumRows);
	$("#apInvoice tbody tr:not(:first)").each(function(i) {
		disableEntries($(this));
	});
	updateTotalAmount();
}

function filterSuplierAccounts(supplierAcctId, termId, dueDate) {
	$("#supplierAccountId").empty();
	if($.trim($("#supplierId").val()) != "") {
		var supplierId = $("#hdnSupplierId").val();
		var uri= contextPath+"/getSupplierAccounts?supplierId="+supplierId;
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
				var slctdSupplierAcctId = $("#supplierAccountId option:selected").val();
				if (supplierAcctId != "" && supplierAcctId != null
						&& typeof supplierAcctId != undefined) {
					slctdSupplierAcctId = supplierAcctId; // Reset values
				}

				for(var index = 0; index < data.length; index++) {
					var rowObject =  data[index];
					var id = rowObject["id"];
					if(id == slctdSupplierAcctId) {
						$("#companyId").val(rowObject["companyId"]);
						if("${apInvoiceItemDto.apInvoice.id}" == 0) {
							var defaultTerm = rowObject["termId"];
							$("#termId").val(defaultTerm).attr('selected', true);
						}
						break;
					}
				}

				// Remove duplicate values
				var found = [];
				$("#supplierAccountId option").each(function() {
					if($.inArray(this.value, found) != -1) {
						$(this).remove();
					}
					found.push(this.value);
				});
				getTermAndComputeDueDate(termId, dueDate);
			}
		};
		loadPopulate(uri, false, supplierAcctId, "supplierAccountId", optionParser, postHandler);
	}
}

function getTermAndComputeDueDate(termId, dueDate) {
	var supplierAccountId = $("#supplierAccountId").val();
	if (supplierAccountId != "" && supplierAccountId != null) {
		var uri = contextPath+"/getTerm";
		if(termId != null) {
			uri += "?termId="+termId;
		} else {
			// Get the default term of the supplier account.
			uri += "?supplierAccountId="+supplierAccountId;
		}
		$.ajax({
			url: uri,
			success : function(term) {
				$("#termId").val(term.id).attr('selected', true);
				if(dueDate == null) {
					computeDueDate();
				}
				onchangeSupplierAcct();
			},
			error : function (error) {
				console.log(error);
			},
			dataType: "json",
		});
	}
}

function computeDueDate() {
	var glDateVal = $("#glDate").val();
	if(glDateVal == null || glDateVal == "") {
		$("#dueDate").val("");
		return;
	}
	var additionalDays = 0;
	var termId = $("#termId option:selected").val();
	for(var i = 0; i < terms.length; i++) {
		var term = terms[i];
		if (term.id == termId) {
			additionalDays = term.days;
			break;
		}
	}
	var glDate = new Date(glDateVal);
	glDate.setDate(glDate.getDate() + parseInt(additionalDays));
	if(!isNaN( glDate.getMonth()) && !isNaN( glDate.getDate()) && !isNaN(glDate.getFullYear())){
		$("#dueDate").val((glDate.getMonth() + 1) + "/" + glDate.getDate()
			+ "/" + glDate.getFullYear());
	} else {
		$("#dueDate").val ("");
	}
}

function filterWtAcctSettings(wtAcctSettingId) {
	var $selectWtAcct = $("#wtAcctSettingId");
	var selectedCompanyId = $("#companyId").val();
	var supplierVatTypeId = $("#supplierVatTypeId").val();
	$selectWtAcct.empty();
	if (selectedCompanyId > 0 && (supplierVatTypeId != "" && typeof supplierVatTypeId != "undefined")) {
		var uri = contextPath+"/getWithholdingTaxAcct?companyId="+selectedCompanyId+"&isCreditable=false";
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
					$("#wtAcctSettingId option").each(function() {
						if($.inArray(this.value, found) != -1) {
							$(this).remove();
						}
						found.push(this.value);
					});
					if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != undefined) {
						$selectWtAcct.val(wtAcctSettingId);
					}
				}
		};
		loadPopulateObject(uri, false, wtAcctSettingId, $selectWtAcct, optionParser, postHandler, false, true);
	}
	computeWtaxRowAmount();
}

function assignWtAcctSetting(select) {
	var selectedWtAcctSettingId = $(select).val();
	filterWtAcctSettings(selectedWtAcctSettingId);
}

function computeWTax(wtAcctSettingId) {
	var supplierVatTypeId = $("#supplierVatTypeId").val();
	var wtPercentageValue = 0;
	if((wtAcctSettingId != "" && typeof wtAcctSettingId != "undefined" && wtAcctSettingId != null) 
			&& (supplierVatTypeId != "" && typeof supplierVatTypeId != "undefined")) {
		var uri = contextPath + "/getWithholdingTaxAcct/getWtPercentage?wtAccountSettingId="+wtAcctSettingId;
		$.ajax({
			url: uri,
			success : function(wt) {
				wtPercentageValue = (accounting.unformat(wt) / 100);
				// Non-serialized items
				var totalAmount = 0;
				$("#apInvoice tbody tr").each(function() {
					var amount = accounting.unformat($(this).find(".txtAmount").val());
					if(supplierVatTypeId == 2) {
						totalAmount += (amount / 1.12);
					} else {
						totalAmount += amount;
					}
				});

				// Other charges
				var totalOtherCharges = 0
				var otherChargesTotal = accounting.unformat($("#otherChargesTable").find("tfoot tr .amount").html());
				if(supplierVatTypeId == 2) {
					totalOtherCharges = (otherChargesTotal / 1.12);
				} else {
					totalOtherCharges = otherChargesTotal;
				}

				var totalInvoice = totalAmount + totalOtherCharges;
				var computedWTax = totalInvoice * wtPercentageValue;
				$("#computedWTax").text(formatNumber(computedWTax));
				$("#hdnWtAmount").val(accounting.unformat(computedWTax));

				updateTotalAmount();
				var totalInvoices = accounting.unformat($("#spanTotalAmount").html());
				var grandTotal = totalInvoices + otherChargesTotal;
				var netAmount = grandTotal - accounting.unformat($("#computedWTax").text());
				$("#netAmount").val(netAmount);
				$("#grandTotal").html(formatNumber(netAmount));
			},
			error : function(error) {
				wtPercentageValue = 0;
			},
			dataType: "text"
		});
	} else {
		updateTotalAmount();
		$("#computedWTax").html("0.0000");
		var totalAmount = accounting.unformat($("#spanTotalAmount").html());
		var otherChargesTotal = accounting.unformat($("#otherChargesTable").find("tfoot tr .amount").html());
		var grandTotal = totalAmount + otherChargesTotal;
		$("#netAmount").val(grandTotal);
		$("#grandTotal").html(formatNumber(grandTotal));
	}
}

function computeWtaxRowAmount() {
	var wtAcctSettingId = $("#wtAcctSettingId").val();
	computeWTax(wtAcctSettingId);
}

function onchangeSupplierAcct() {
	var wt = $("#wtAcctSettingId").val();
	var supplierAccountId = $("#supplierAccountId option:selected").val();
	if (supplierAccountId != "" && supplierAccountId != null
			&& typeof supplierAccountId != undefined) {
		var uri = contextPath+"/getSupplierAccounts/byId?supplierAccountId="+supplierAccountId;
		$.ajax({
			url: uri,
			success : function (supplierAcct) {
				$("#companyId").val(supplierAcct.companyId);
				filterWtAcctSettings(wt);
			},
			error : function (error) {
				console.log(error);
			},
			dataType: "json",
		});
	}
}

function initializetOtherCharges() {
	var otherChargesJson = JSON.parse($("#apInvoiceLinesJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: otherChargesJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "apLineSetupId", "varType" : "int"},
				{"name" : "unitOfMeasurementId", "varType" : "int"},
				{"name" : "quantity", "varType" : "int"},
				{"name" : "upAmount", "varType" : "double"},
				{"name" : "amount", "varType" : "double"},
				{"name" : "apLineSetupName", "varType" : "string"},
				{"name" : "unitMeasurementName", "varType" : "string"},
				{"name" : "ebObjectId", "varType" : "int"}
				],
		contextPath: cPath,
		header:[
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "apLineSetupId",
					"cls" : "apLineSetupId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "unitOfMeasurementId",
					"cls" : "unitOfMeasurementId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "ebObjectId",
					"cls" : "ebObjectId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "AP Line",
					"cls" : "apLineSetupName tblInputText",
					"editor" : "text",
					"visible" : true,
					"width" : "25%",
					"handler" : new UnitCostTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Qty",
					"cls" : "quantity tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "15%" },
				{"title" : "UOM",
					"cls" : "unitMeasurementName tblInputText",
					"editor" : "text",
					"visible" : true,
					"width" : "23%",
					"handler" : new UnitCostTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "UP",
					"cls" : "upAmount tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "19%"},
				{"title" : "Amount",
					"cls" : "amount tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "23%"}
				],
				"footer" : [
					{"cls" : "amount"}
				],
				"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function(){
		var amount = $(this).closest("tr").find(".amount").val();
		updateOChargesAmt(amount);
		computeWtaxRowAmount();
	});
	resizeOtherChargesTbl();
}

function resizeOtherChargesTbl(){
	var $tbl = $("#otherChargesTable table");
	$($tbl).attr("width", "100%");
	addTotalLabel($tbl)
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", "6");
}

function addTotalLabel($tbl){
	var $tr = $($tbl).find("tfoot tr td:nth-child(1)");
	$($tr).html("Total");
}
</script>
</head>
<body>
<div id="divInvoiceList" style="display: none"></div>
<div class="formDivBigForms">
	<form:form method="POST" commandName="apInvoiceItemDto" id="apInvoiceItemFormId">
		<div class="modFormLabel">AP Invoice - Goods
			<span class="btnClose" id="btnClose">[X]</span>
		</div>
		<form:hidden path="apInvoice.id" id="hdnFormId"/>
		<form:hidden path="apInvoice.createdBy"/>
		<form:hidden path="apInvoice.formWorkflowId" />
		<form:hidden path="apInvoice.invoiceTypeId"/>
		<form:hidden path="apInvoice.ebObjectId"/>
		<form:hidden path="apInvoice.sequenceNumber"/>
		<form:hidden path="apInvoice.supplierId" id="hdnSupplierId"/>
		<form:hidden path="apInvoice.supplierAccount.companyId" id="companyId"/>
		<form:hidden path="apInvoice.supplier.busRegTypeId" id="supplierVatTypeId"/>
		<form:hidden path="apInvoice.referenceDocumentsJson" id="referenceDocumentsJson"/>
		<form:hidden path="apInvoice.apInvoiceLinesJson" id="apInvoiceLinesJson"/>
		<form:hidden path="apInvoice.wtAmount" id="hdnWtAmount"/>
		<form:hidden path="netAmount" id="netAmount"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">Sequence Number</td>
						<td class="value">
							<c:choose>
								<c:when test="${apInvoiceItemDto.apInvoice.id eq 0}">
									<input type="text" id="txtSequenceNo"
										class="textBoxLabel" readonly="readonly" />
								</c:when>
								<c:otherwise>
									${apInvoiceItemDto.apInvoice.sequenceNumber}
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:set var="status" value="${apInvoiceItemDto.apInvoice.formWorkflow.currentFormStatus.description}" />
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW" />
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
						<td class="labels">* SOA No.</td>
						<td class="value"><form:input path="apInvoice.invoiceNumber" id="invoiceNumber" class="input" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="apInvoice.invoiceNumber" cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* Supplier Name</td>
						<td class="value">
							<form:input path="apInvoice.supplier.name" id="supplierId" class="input" onkeydown="showSuppliers();" onblur="getSupplier();" />
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="apInvoice.supplierId" cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* Supplier's Account</td>
						<td class="value"><form:select path="apInvoice.supplierAccountId" id="supplierAccountId"
							cssClass="frmSelectClass" onchange="getTermAndComputeDueDate();"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="apInvoice.supplierAccountId" cssClass="error" /></td>
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
						<td class="value"><form:errors path="apInvoice.termId" cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* SOA Date</td>
						<td class="value"><form:input path="apInvoice.invoiceDate"
							id="invoiceDate" onblur="evalDate('invoiceDate')"
							style="width: 120px;" class="dateClass2" /> <img id="imgDate1"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('invoiceDate')"
							style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="apInvoice.invoiceDate"
								cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* GL Date</td>
						<td class="value">
							<form:input path="apInvoice.glDate" id="glDate"
								onblur="evalDate('glDate'); computeDueDate();"
								style="width: 120px;" class="dateClass2" /> <img id="imgDate2"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('glDate')" style="cursor: pointer"
								style="float: right;" />
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="apInvoice.glDate" cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* Due Date</td>
						<td class="value"><form:input path="apInvoice.dueDate" id="dueDate" onblur="evalDate('dueDate')"
							style="width: 120px;" class="dateClass2" /> <img id="imgDate3"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dueDate')"
							style="cursor: pointer" style="float: right;" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="apInvoice.dueDate" cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* Description</td>
						<td class="value"><form:textarea path="apInvoice.description"
								id="description" class="input" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="apInvoice.description"
								cssClass="error" /></td>
					</tr>
					<tr>
						<td class="labels">* Amount</td>
						<td class="value">
							<form:input path="apInvoice.amount" id="txtTotalAmount" class="numeric" size="20"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="apInvoice.amount" cssClass="error" /></td>
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
						<c:forEach items="${apInvoiceItemDto.apInvoice.apInvoiceItems}" var="apInvoice" varStatus="status">
							<tr>
								<td class="tdProperties" align="center" style="white-space: nowrap;">
									<img class="imgDelete" id="imgDelete${status.index}"
										onclick="deleteRow(this, ${status.index});"
										src="${pageContext.request.contextPath}/images/delete_active.png" />
									&nbsp;&nbsp;&nbsp;<img class="imgSearchInvoices" id="imgSearch${status.index}"
										onclick="showInvoices(this, ${status.index});"
										src="${pageContext.request.contextPath}/images/search_active.png" />
								</td>
								<td class="tdProperties"><input type="hidden" value="${status.index + 1}" /></td>
								<td style="display: none;">
									<input name="apInvoice.apInvoiceItems[${status.index}].apInvoiceId"
										id="apInvoiceId${status.index}" value="${apInvoice.apInvoiceId}"
										class='txtApInvoiceId' />
								</td>
								<td class="tdProperties" valign="top" style="white-space: nowrap;">
									<input name="apInvoice.apInvoiceItems[${status.index}].invoiceNumber"
										id="invoiceNumber${status.index}" onkeydown='loadInvoiceNumbers(${status.index});'
										onblur="setInvoiceNumber(${status.index}); computeWtaxRowAmount();"
										value="${apInvoice.invoiceNumber}" style="width: 100%;"
										class="txtInvoiceNumber" />
								</td>
								<td class="tdProperties" align="right" valign="top" style="width: 50px;">
									<input name="apInvoice.apInvoiceItems[${status.index}].amount"
										style="width: 100%; text-align: right; border-right: 0;"
										onblur="setAndUpdateAmount(this);" value="<fmt:formatNumber type='number'
											minFractionDigits='4' maxFractionDigits='4' value='${apInvoice.amount}' />"
									id="amount${status.index}" class="txtAmount" readonly="readonly" />
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
				<table>
					<tr>
						<td>
							<span id="apInvoiceErrorMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td>
							<form:errors path="apInvoice.apInvoiceItems" cssClass="error" />
						</td>
					</tr>
				</table>
			</fieldset>
			<!-- Other Charges -->
			<fieldset class="frmField_set">
				<legend>Other Charges</legend>
				<div id="otherChargesTable"></div>
				<table>
					<tr>
						<td>
							<span id="otherChargesMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td>
							<form:errors path="apInvoice.apInvoiceLines" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Documents</legend>
				<div id="documentsTable"></div>
				<table>
					<tr>
						<td><form:errors path="apInvoice.referenceDocuments" cssClass="error" /></td>
					</tr>
					<tr>
						<td><span class="error" id="spDocSizeMsg"></span></td>
					</tr>
					<tr>
						<td><span class="error" id="referenceDocsMgs"></span></td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table class="footerTable">
				<tr>
					<td style="width: 80%">Withholding Tax&nbsp;&nbsp;</td>
					<td style="width: 5%">
						<form:select path="apInvoice.wtAcctSettingId" id="wtAcctSettingId" cssClass="frmSmallSelectClass"
							onchange="assignWtAcctSetting(this);">
						</form:select>
					</td>
					<td style="width: 9%"><span id="computedWTax">0.0</span></td>
					<td style="width: 6%"></td>
				</tr>
				<tr>
					<td style="width: 80%">Total Amount Due&nbsp;&nbsp;</td>
					<td style="width: 5%"></td>
					<td style="width: 9%"><span id="grandTotal">0.0</span></td>
					<td style="width: 6%"></td>
				</tr>
			</table>
			<table class="frmField_set">
				<tr>
					<td>
						<input type="button" id="btnSave" value="Save" onclick="saveForm();" style="float: right;"/>
					</td>
				</tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>