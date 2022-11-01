<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR Transaction form
 -->

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
<script src="${pageContext.request.contextPath}/js/formatUtil.js" type="text/javascript" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<style type="text/css">
input.numeric {
	width: 150px;
}

.tdProperties {
	border-right: 1px solid #000000;
}

.textBoxLabel, .txtArLineSetupName, .txtQuantity,
.txtUnitMeasurementName, .txtUpAmount, .txtAmount, .txtDescription, .txtNumber{
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}

.divVeil {
    background-color: #F2F1F0;
}

#aRLine {
	cellspacing: 0;
	border: none;
}

#aRLine thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#aRLine tbody td {
	border-top: 1px solid #000000;
	border-bottom: 1px solid #000000;
}
.imgDelete{
	margin-left: 2px;
}
</style>
<script type="text/javascript">
var minimumRows = 4;
var selectCustomerAcct = "${arTransaction.customerAcctId}";
var accountCombination = null;
var accountCombinationCache = new Array();
var terms = new Array();
var selectARLineRow = 0;
var aRLineCurrentIndex = "${fn:length(arTransaction.arLines)}";
var arCustAcctTerms = new Array();
var defaultArLine = null;
var arLineSetups = null;
var uOMs = null;
var maxAllowableAmount = null;
var isAllowed = false;
var isTotalAmtAllowed = null;
var totalTAmount = null;

$(document).ready(function() {
	var dueDate = null;
	var termId = null;
	initArLines();

	if("${arTransaction.id}" != 0) {
		termId = "${arTransaction.termId}";
		dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${arTransaction.dueDate}'/>";
		disableAndSetCompany();
		formatMonetaryVal();
		maxAllowableAmount = parseFloat("${arTransaction.arCustomer.maxAmount}");
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arTransaction.amount}'/>");
		var status = parseInt("${arTransaction.formWorkflow.currentStatusId}");
		if (status == 17 || status == 4)
			$("#transactionForm :input").attr("disabled","disabled");
	} else {
		computeDueDate();
	}

	filterCustomerAccts(dueDate, termId);
	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>

	updateTotalAmount();
	updateArLineIndex();
});

function disableAndSetCompany() {
	//Disable and set company
	if ("${arTransaction.id}" != 0) {
		$("#companyId").attr("disabled","disabled");
		$("#companyId").append("<option selected='selected' value='"+"${arTransaction.companyId}"+"'>"+
				"${arTransaction.company.numberAndName}"+"</option>");
	}
}

function disableEntries (elem) {
	$(elem).closest("tr").find(".txtArLineSetupName").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtQuantity").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtUnitMeasurementName").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtUpAmount").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtAmount").attr("disabled", "disabled");
	$(elem).addClass("divVeil");
}

function enableEntries (elem) {
	$(elem).closest("tr").find(".txtArLineSetupName").removeAttr("disabled");
	$(elem).closest("tr").find(".txtQuantity").removeAttr("disabled");
	$(elem).closest("tr").find(".txtUnitMeasurementName").removeAttr("disabled");
	$(elem).closest("tr").find(".txtUpAmount").removeAttr("disabled");
	$(elem).closest("tr").find(".txtAmount").removeAttr("disabled");
	$(elem).removeClass("divVeil");
}

function initArLines () {
	var length = $("#aRLine tbody tr").length;
	if (minimumRows > length)
		addARLine (minimumRows - length);
	//Disable all rows except for the first row
	$("#aRLine tbody tr:not(:first)").each(function(i) {
		if ($(this).find(".txtArLineSetupName").val() == "") {
			disableEntries($(this));
			$(this).addClass("divVeil");
		}
	});
	updateTotalAmount();
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

function getCustomer () {
	var customerName = processSearchName($.trim($("#txtCustomer").val()));
	if (customerName != "") {
		$.ajax({
			url: contextPath + "/getArCustomers?name="+customerName+ "&isExact=true",
			success : function(customer) {
				$("#spanCustomerError").text("");
				if (customer != null && customer[0] != undefined) {
					$("#customerId").val(customer[0].id);
					$("#txtCustomer").val(customer[0].name);
					maxAllowableAmount = parseFloat(customer[0].maxAmount);
					isAllowed = false;
					filterCustomerAccts();
				}else{
					$("#customerId").val("");
					$("#spanCustomerError").text("Invalid AR Customer.");
				}
			},
			error : function(error) {
				maxAllowableAmount = null;
				$("#spanCustomerError").text("Invalid AR Customer.");
			},
			dataType: "json"
		});
	}
}

function showCustomers () {
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getArCustomers/new?name="+$("#txtCustomer").val()+"&companyId="+companyId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#customerId").val(ui.item.id);
			$("#spanCustomerError").text("");
			$(this).val(ui.item.name);
			filterCustomerAccts();
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					$("#spanCustomerError").text("");
					if (ui.item != null) {
						$(this).val(ui.item.name);
						maxAllowableAmount = parseFloat(ui.item.maxAmount);
						isAllowed = false;
						filterCustomerAccts();
					}else{
						$("#spanCustomerError").text("Invalid AR Customer.");
						$("#customerId").val("");
						$("#customerAcctId").empty();
					}
				},
				error : function(error) {
					maxAllowableAmount = null;
					$("#spanCustomerError").text("Invalid AR Customer.");
					$("#customerId").val("");
					$("#txtCustomer").val("");
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

function filterCustomerAccts(dueDate, termId){
	$("#customerAcctId").empty();
	var companyId = $("#companyId").val();
	if ($.trim($("#txtCustomer").val()) == "") {
		$("#customerId").val("");
	} else if(companyId != ""){
		var customerId = $("#customerId").val();
		var uri = contextPath + "/getArCustomerAccounts?arCustomerId="+customerId+"&companyId="+companyId;
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
					$("#customerAcctId").val(selectCustomerAcct).attr("selected",true);
					var customerAcctId = $("#customerAcctId option:selected").val();
					arCustAcctTerms = new Array();
					for (var index = 0; index < data.length; index++){
						var rowObject =  data[index];
						var id = rowObject["id"];
						if (id == customerAcctId){
							if (termId == null) {
								var defaultTerm = rowObject["termId"];
								$("#termId").val(defaultTerm).attr("selected" ,true);
							}
							defaultArLine = rowObject["defaultArLineSetup"];
						}
						var arCustAcctTerm = new ArCustAcctTerm(id, rowObject["termId"], rowObject["defaultArLineSetup"]);
						arCustAcctTerms.push (arCustAcctTerm);
					}

					loadTotalTransactionAmount ();
					//Compute the due date based on the GL Date and the term selected.
					if(dueDate == null) {
						computeDueDate ();
					}
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "customerAcctId", optionParser, postHandler);
	}
}

function loadTotalTransactionAmount () {
	$.ajax({
		url: contextPath + "/arTransactionForm/getTotalTAmount?arCustomerAcctId="+$("#customerAcctId").val(),
		success : function(item) {
			totalTAmount = item;
			console.log (totalTAmount);
		},
		error : function(error) {
			console.log ("Error in retrieving data.");
		},
		dataType: "text"
	});
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
	loadTotalTransactionAmount ();
}

function formatMonetaryVal() {
	formatMoney($("#totalAmount"));
	$("#aRLine tbody tr").each(function(i) {
		formatMoney($(this).find(".txtQuantity"));
		formatMoney($(this).find(".txtUpAmount"));
		formatMoney($(this).find(".txtAmount"));
	});
}

function loadDefaultArLine(elem, rowNumber) {
	var row = $(elem).closest("tr");
	if (!$(elem).hasClass(".divVeil") &&  $(row).find(".hdnArLineSetupIds").val() == "") {
		if (defaultArLine != null && $.trim($(elem).val()) == "") {
			$(row).find(".hdnArLineSetupIds").val(defaultArLine.id);
			$(elem).val(defaultArLine.name);
		}
	}
}

function ArLineSetup (id, name) {
	this.id = id;
	this.name = name;
}

function UOM (id, name) {
	this.id = id;
	this.name = name;
}

function addARLine (numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";
		// Delete and search for AR Line Setup
		newRow += "<td class='tdProperties' align='center'>";
		newRow += "<img class='imgDelete' id='imgDelete"+aRLineCurrentIndex+"' src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deleteRow(this);' />";
		newRow += "</td>";

		//Number
		newRow += "<td class='tdProperties' align='center'><input id='txtNumber"+aRLineCurrentIndex+"' class='txtNumber' style='width: 100%; text-align: center;'  readonly='readonly'/></td>";

		//AR Line
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input id='txtArLineSetup"+aRLineCurrentIndex+"' name='arLines["+aRLineCurrentIndex+"].arLineSetupName' \
			onkeydown='showARLineList(this.id);' onfocus='loadDefaultArLine(this, "+aRLineCurrentIndex+");'  \
			class='txtArLineSetupName' style='width: 100%;'/>";
		newRow += "<input type='hidden' id='arLineSetupId"+aRLineCurrentIndex+"' name='arLines["+aRLineCurrentIndex+"].arLineSetupId' class='hdnArLineSetupIds' />";
		newRow += "</td>";

		//QTY
		newRow += "<td class='tdProperties' valign='top' style='width: 20px;'>";
		newRow += "<input name='arLines["+aRLineCurrentIndex+"].quantity' id='quantity"+aRLineCurrentIndex+"' style='width: 100%; text-align: right;' \
			class='txtQuantity' maxLength='7' onblur='formatMoney(this); computeAmount(this);'";
		newRow += "</td>";

		//UOM
		newRow += "<td class='tdProperties' align='center'>";
		newRow += "<input id='txtUnitMeasurement"+aRLineCurrentIndex+"' onkeydown='showUOMList(this.id);' name='arLines["+aRLineCurrentIndex+"].unitMeasurementName' class='txtUnitMeasurementName' style='width: 100%; text-align: center;'/>";
		newRow += "<input type='hidden' id='unitOfMeasurementId"+aRLineCurrentIndex+"' name='arLines["+aRLineCurrentIndex+"].unitOfMeasurementId' class='hdnUnitMeasurementIds' />";
		newRow += "</td>";

		//UP
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input name='arLines["+aRLineCurrentIndex+"].upAmount' id='upAmount"+aRLineCurrentIndex+"' style='width: 100%; text-align: right;' \
			class='txtUpAmount' maxLength='13' onblur='formatMoney(this); computeAmount(this);'";
		newRow += "</td>";

		//Amount
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input name='arLines["+aRLineCurrentIndex+"].amount' id='amount"+aRLineCurrentIndex+"' style='width: 100%; text-align: right;' class='txtAmount' \
					onkeydown='processNextRow(this, event);' onblur='checkMaxTotalAmount(this); computeAmount(this); formatMoney(this);' maxLength='13'/>";
		newRow += "</td>";
		newRow += "</tr>";

		$("#aRLine tbody").append(newRow);
		aRLineCurrentIndex++;
	}
}

function showARLineList(elemId) {
	if ($("#customerAcctId").val() != null && $("#customerAcctId").val() != "") {
		var uri = contextPath + "/getArLineSetups?name="+$("#"+elemId).val() +
				"&arCustAcctId="+$("#customerAcctId").val();
		var row = $("#"+elemId).parent("td").parent("tr");
		$("#"+elemId).autocomplete({
			source: uri,
			select: function( event, ui ) {
				$(row).find(".hdnArLineSetupIds").val(ui.item.id);
				$(this).val(ui.item.name);
				return false;
			}, minLength: 2,
			change: function(event, ui){
				$.ajax({
					url: uri,
					success : function(item) {
						if (ui.item != null) {
							$(row).find(".hdnArLineSetupIds").val(ui.item.id);
							$(this).val(ui.item.name);
						}
						$("#spanArLineError").text("");
					},
					error : function(error) {
						$("#spanArLineError").text("");
						$("#spanArLineError").text("Invalid AR Line at row " + ($(row).index()+1) + ".");
						$(row).find(".hdnArLineSetupIds").val("");
						setTimeout(function() {
							$("#"+elemId).focus();
						}, 0);
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

function showUOMList(elemId) {
	var uri = contextPath + "/getUnitMeasurements?name="+$("#"+elemId).val();
	var row = $("#"+elemId).parent("td").parent("tr");
	$("#"+elemId).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(row).find(".hdnUnitMeasurementIds").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(row).find(".hdnUnitMeasurementIds").val(ui.item.id);
						$(this).val(ui.item.name);
					}
					$("#spanUOMError").text("");
				},
				error : function(error) {
					$("#spanUOMError").text("");
					$("#spanUOMError").text("Invalid Unit of Measure at row " + ($(row).index()+1) + ".");
					$(row).find(".hdnUnitMeasurementIds").val("");
					setTimeout(function() {
						$("#"+elemId).focus();
					}, 0);
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

function formatMoney (textboxId) {
	var money = accounting.formatMoney($(textboxId).val());
	$(textboxId).val(money.replace(/[$]/gi, ''));
}

function computeAmount (elem) {
	var quantity = $(elem).parent("td").parent("tr").closest("tr").find(".txtQuantity").val();
	var unitPrice = $(elem).parent("td").parent("tr").closest("tr").find(".txtUpAmount").val();
	var txtAmount = $(elem).parent("td").parent("tr").closest("tr").find(".txtAmount");
	if ($.trim($(txtAmount).val()) == "" || $(txtAmount).val() == "0.00") {
		if (quantity != "" && unitPrice != "") {
			quantity = quantity.replace(/\,/g,"");
			unitPrice = unitPrice.replace(/\,/g,"");
			var amount = (quantity * unitPrice);
			amount = accounting.formatMoney(amount).replace(/[$]/gi, '');
			$(txtAmount).val(amount);
			updateTotalAmount();
		}
	}
	updateTotalAmount();
}

function parseDoubles() {
	if ($.trim($("#totalAmount").val()) == "")
		$("#totalAmount").val("0.0");
	else {
		var formatAmt = removeComma($("#totalAmount").val());
		$("#totalAmount").val(parseFloat(formatAmt,10));
	}

	$(".txtQuantity").each(function(i) {
		var quantity = $(this).val();
		$(this).val(removeComma(quantity));
	});

	$(".txtUpAmount").each(function(i) {
		var upAmount = $(this).val();
		$(this).val(removeComma(upAmount));
	});

	$(".txtAmount").each(function(i) {
		var amount = $(this).val();
		$(this).val(removeComma(amount));
	});
}

function updateTotalAmount() {
	var total = 0;
	$("#aRLine tbody tr").each(function(i) {
		var formatAmt = $(this).find(".txtAmount").val().replace(/\,/g,"");
		total += formatAmt != "" ? parseFloat(formatAmt) : 0;
	});
	$("#spanTotalAmount").html(accounting.formatMoney(total));
}

var isSaving = false;
function saveTransaction(){
	if(isSaving == false) {
		isSaving = true;
		$("#companyId").removeAttr("disabled");
		$("#btnSaveARTransaction").attr("disabled","disabled");
		parseDoubles();
		$("#aRLine tbody tr").each(function(i) {
			if ($(this).hasClass("divVeil"))
				$(this).remove();
		});

		doPostWithCallBack("transactionForm", "form", function(data){
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if("${pId}" == 0)
					dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var termId = $("#termId").val();
				var dueDate = $("#dueDate").val();
				var customer = $("#txtCustomer").val();
				if("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableAndSetCompany();
				}
				$("#txtCustomer").val(customer);
				parseDoubles();
				filterCustomerAccts(dueDate, termId);
				initArLines ();
				formatMonetaryVal();
				updateTotalAmount();
				updateArLineIndex();
				isSaving = false;
			}
			$("#btnSaveARTransaction").removeAttr("disabled");
		});
	}
}

function processNextRow(amount, event) {
	if(event.keyCode == 9 && !event.shiftKey) { //Tab key
		var amountId = $(amount).attr("id");
		var currentRow = Number(amountId.substr(6, amountId.length - 1)) + 1;
		var rowCount = $("#aRLine tbody tr").length;
		var arLineSetupName = $("#txtArLineSetup"+(currentRow-1)).val();
		if(typeof arLineSetupName != "undefined"|| arLineSetupName != "") {
			if(currentRow < rowCount) {
				var nextRow = $(amount).closest("tr").next();
				if(nextRow.hasClass("divVeil")) {
					if (isTotalAmtAllowed == null || isTotalAmtAllowed) {
						enableEntries ($(nextRow));
						$("#imgDelete"+currentRow).focus();
					}
				}
			} else {
				if (isTotalAmtAllowed == null || isTotalAmtAllowed) {
					addARLine(1);
					updateArLineIndex();
					$("#txtNumber"+currentRow).focus();
				}
			}
		} else {
			if ($.trim($("#txtArLineSetup"+(currentRow-1)).val()) == "")
				$("#spanArLineError").text("Please select an AR Line.");
			$("#txtNumber"+(currentRow-1)).focus();
		}
	}
}

function deleteRow(deleteImg) {
	var row = $(deleteImg).attr("id").replace("imgDelete", "");
	var toBeDeletedRow = $(deleteImg).closest("tr");
	$(toBeDeletedRow).remove();
	var rowCount = $("#aRLine tbody tr").length;
	if(rowCount < minimumRows){
		var rowsToAdd = minimumRows - rowCount;
		addARLine(rowsToAdd);
		processArLineRow(rowCount);
	}
	enableEntries ($("#aRLine tbody tr:first"));
	if(row == 0 && rowCount == 0)
		addARLine(1);
	enableEntries ($("#aRLine tbody tr:first"));
	updateTotalAmount();
	updateArLineIndex();
}

function processArLineRow (setRows) {
	$("#aRLine tbody tr").each(function(index) {
		if (index >= setRows && index != 0)
			disableEntries($(this));
	});
}

//Fix the indexing of table rows
function updateArLineIndex () {
	var currentRow = 0;
	$("#aRLine tbody tr").each(function(row) {
		currentRow = row;
		$(this).find(".imgDelete").attr("id", "imgDelete"+row);
		$(this).find(".txtNumber").attr("id", "txtNumber"+row).val(row+1);
		$(this).find(".txtArLineSetupName").attr("id", "txtArLineSetup"+row).attr("name", "arLines["+row+"].arLineSetupName");
		$(this).find(".hdnArLineSetupIds").attr("id", "arLineSetupId"+row).attr("name", "arLines["+row+"].arLineSetupId");
		$(this).find(".txtQuantity").attr("id", "quantity"+row).attr("name", "arLines["+row+"].quantity");
		$(this).find(".txtUnitMeasurementName").attr("id", "txtUnitMeasurement"+row).attr("name", "arLines["+row+"].unitMeasurementName");
		$(this).find(".hdnUnitMeasurementIds").attr("id", "unitOfMeasurementId"+row).attr("name", "arLines["+row+"].unitOfMeasurementId");
		$(this).find(".txtUpAmount").attr("id", "upAmount"+row).attr("name", "arLines["+row+"].upAmount");
		$(this).find(".txtAmount").attr("id", "amount"+row).attr("name", "arLines["+row+"].amount");
	});
	aRLineCurrentIndex = currentRow + 1;
}

function checkMaxAmount(elem) {
	if (maxAllowableAmount != 0) {
		value =  parseFloat(totalTAmount) + parseFloat($(elem).val());
		if (!isNaN(value) && maxAllowableAmount != null && !isAllowed) {
			if (value > maxAllowableAmount) {
				var confirmation = confirm(
					"The amount has exceeded the customer's maximum allowable amount of " + accounting.formatMoney(maxAllowableAmount) + 
					". Do you wish to continue?");
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

function checkMaxTotalAmount (elem) {
	if (maxAllowableAmount != 0) {
		var total = totalTAmount != null ? parseFloat(totalTAmount) : 0;
		$("#aRLine tbody tr").each(function(i) {
			var amount = $(this).find(".txtAmount").val();
			total += amount != "" && !isNaN(amount) ? parseFloat(amount) : 0;
		});
		if (total > maxAllowableAmount && !isAllowed) {
			var confirmation = confirm(
				"The total amount has exceeded the customer's maximum allowable amount of " + accounting.formatMoney(maxAllowableAmount) + 
				". Do you wish to continue?");
			if (confirmation) {
				isAllowed = true;
				processNextRow(elem, event);
			} else {
				isAllowed = false;
				setTimeout(function() {
					$(elem).val("");
					$(elem).focus();
				}, 0);
			}
		}
	}
}
function clearCustomerName(){
	$("#txtCustomer").val("");
	$("#customerAcctId").empty();
	$("#aRLine tbody").empty();
	initArLines ();
	updateArLineIndex();
}
</script>
<title>AR Transaction Form</title>
</head>
<body>
<div id="divARLineList" style=" display: none"></div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="arTransaction" id="transactionForm">
		<div class="modFormLabel">AR Transaction <span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="ebObjectId" />
		<form:hidden path="createdBy"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="customerId"/>
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
								items="${companies}" itemLabel="numberAndName" itemValue="id" onchange="clearCustomerName();">
							</form:select>
						</td>
					</tr>

					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="companyId" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Transaction Type</td>
						<td class="value"> 
							<form:select path="transactionTypeId" id="transactionTypeId" cssClass="frmSelectClass">
								<form:options items="${transactionTypes}" itemLabel="name" itemValue="id" />
							</form:select>
						</td>
					</tr>

					<tr>
						<td class="labels">* Transaction No</td>
						<td class="value">
							<form:input path="transactionNumber" cssClass="inputSmall" />
						</td>
					</tr>

					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="transactionNumber" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Customer Name</td>
						<td class="value">
							<form:input path="arCustomer.name" id="txtCustomer" class="input" onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="getCustomer();"/>
						</td>
					</tr>

					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanCustomerError" class="error" style="margin-left: 12px;"></span>
							<form:errors path="customerId" cssClass="error" id="customerIdErr"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Customer Account</td>
						<td class="value">
							<form:select path="customerAcctId" id="customerAcctId" cssClass="frmSelectClass"  onchange="assignCustomerAcct (this);">
							</form:select>
						</td>
					</tr>

					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="customerAcctId" cssClass="error" style="margin-left: 12px;" />
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
						<td></td>
						<td colspan="2">
							<form:errors path="transactionDate" cssClass="error" style="margin-left: 12px;"/>
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
						<td></td>
						<td colspan="2">
							<form:errors path="glDate" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Due Date</td>
						<td class="value">
						<form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate')"
							style="width: 120px;" class="dateClass2" />
							<img id="imgDate3"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dueDate')"
							style="cursor: pointer" style="float: right;" /></td>
					</tr>

					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="dueDate" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Description</td>
						<td class="value"><form:textarea path="description"
									id="description" class="input" /></td>
					</tr>

					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="description" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Amount</td>
						<td class="value">
							<form:input path="amount" id="totalAmount" class="numeric" size="20" onblur="checkMaxAmount(this); formatMoney(this);" maxLength="13"/>
						</td>
					</tr>

					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="amount" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>AR Lines</legend>
				<table id="aRLine" class="dataTable">
					<thead>
						<tr>
							<th width="5%"></th>
							<th width="3%">#</th>
							<th width="25%">AR LINE</th>
							<th width="15%">QTY</th>
							<th width="19%">UOM</th>
							<th width="15%">UP</th>
							<th width="21%">AMOUNT</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${arTransaction.arLines}" var="arLine" varStatus="status">
							<tr>
								<!-- DELETE & SEARCH -->
								<td class="tdProperties" align="center">
									<input type="hidden" name='arLines[${status.index}].id' value="${arLine.id}">
									<img class="imgDelete" src='${pageContext.request.contextPath}/images/delete_active.png' onclick="deleteRow(this);" />
								</td>
								<!-- NUMBER -->
								<td class='tdProperties' align='center'><input id="txtNumber${status.index}" class='txtNumber' style='width: 100%; text-align: center;'  readonly='readonly'/>
								<!-- AR LINE -->
								<td class="tdProperties" valign="top">
									<c:set var="arLineSetupName" value="${arLine.arLineSetupName}"/>
									<c:if test="${arLine.arLineSetup != null}">
										<c:set var="arLineSetupName" value="${arLine.arLineSetup.name}"/>
									</c:if>
									<input id='txtArLineSetup${status.index}' name='arLines[${status.index}].arLineSetupName'
										onkeydown='showARLineList(this.id);' onfocus='loadDefaultArLine(this, ${status.index});'
										onblur='setArLine(this, ${status.index});'
										value='${fn:escapeXml(arLineSetupName)}' class='txtArLineSetupName' style='width: 100%;' />
									<input type="hidden" id='arLineSetupId${status.index}' name='arLines[${status.index}].arLineSetupId'
										value="${arLine.arLineSetupId}" class='hdnArLineSetupIds'  />
								</td>
								<!-- QTY -->
								<td class="tdProperties">
									<input name='arLines[${status.index}].quantity' id='quantity${status.index}'
										value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arLine.quantity}' />"
										style='width: 100%; text-align: right;' class='txtQuantity' maxLength='7'
										onblur='formatMoney(this); computeAmount(this);'/>
								</td>
								<!-- UOM -->
								<td class="tdProperties" align="center">
									<c:set var="unitMeasurementName" value="${arLine.unitMeasurementName}"/>
									<c:if test="${arLine.unitMeasurement ne null}">
										<c:set var="unitMeasurementName" value="${arLine.unitMeasurement.name}"/>
									</c:if>
									<input  id='txtUnitMeasurement${status.index}' onkeydown='showUOMList(this.id);'
										name="arLines[${status.index}].unitMeasurementName"
										value="${unitMeasurementName}" class='txtUnitMeasurementName' style='width: 100%; text-align: center;' />
									<input type="hidden" id='unitOfMeasurementId${status.index}'
										value="${arLine.unitOfMeasurementId}" name='arLines[${status.index}].unitOfMeasurementId'
										class='hdnUnitMeasurementIds'/>
								</td>
								<!-- UP -->
								<td class='tdProperties' valign='top'>
									<input name='arLines[${status.index}].upAmount' id='upAmount${status.index}'
										value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arLine.upAmount}' />"
										style='width: 100%; text-align: right;' class='txtUpAmount' maxLength='13'
										onblur='formatMoney(this); computeAmount(this);'/>
								</td>
								<!-- AMOUNT -->
								<td class='tdProperties' valign='top'>
									<input name='arLines[${status.index}].amount' id='amount${status.index}'
										value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arLine.amount}' />"
										style='width: 100%; text-align: right;' class='txtAmount' maxLength="13"
										onkeydown='processNextRow(this, event);'
										onblur='checkMaxTotalAmount(this); computeAmount(this); formatMoney(this);' maxLength="13"/>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="6"></td>
							<td style="text-align: right;"><span id="spanTotalAmount">0.0</span></td>
						</tr>
						<tr>
							<td colspan="7">
								<span id="spanArLineError" class="error"></span>
								<span id="spanUOMError" class="error"></span>
								<form:errors path="arLineError" cssClass="error" />
							</td>
						</tr>
					</tfoot>
				</table>
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSaveARTransaction"
							value="Save" onclick="saveTransaction();" /></td>
					</tr>
				</table>
			</fieldset>
		</div>
	</form:form>
</div>
</body>
</html>