<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: AR Miscellaneous form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
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

#aRMLine {
	cellspacing: 0;
	border: none;
}

#aRMLine thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#aRMLine tbody td {
	border-top: 1px solid #000000;
	border-bottom: 1px solid #000000;
}
.imgDelete{
	margin-left: 2px;
}
</style>
<script type="text/javascript">
var minimumRows = 4;
var selectCustomerAcct = "${arMiscellaneous.arCustomerAccountId}";
var aRMLineCurrentIndex = "${fn:length(arMiscellaneous.arMiscellaneousLines)}";
var arCustAcctTerms = new Array();
var selectARLineRow = 0;
var currentCustAcctId = 0;
var defaultArLine = null;
var arLineSetups = null;
var uOMs = null;

$(document).ready (function () {
	$("#txtSequenceNumber").val("${sequenceNo}"); // Display Sequence Number
	initArLines();
	filterCustomerAccts();
	updateTotalAmount();
	updateArLineIndex();
	if ("${arMiscellaneous.id}" != 0) {
		formatMonetaryVal();
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arMiscellaneous.amount}'/>");
		$("#arMiscellaneousForm :input").attr("disabled","disabled");
	}
});

function initArLines () {
	var length = $("#aRMLine tbody tr").length;
	if (minimumRows > length)
		addARLine (minimumRows - length);
	//Disable all rows except for the first row
	$("#aRMLine tbody tr:not(:first)").each(function(i) {
		var aLName = $.trim($(this).find(".txtArLineSetupName").val());
		var qty = $.trim($(this).find(".txtQuantity").val());
		var uom = $.trim($(this).find(".txtUnitMeasurementName").val());
		var unitPrice = $.trim($(this).find(".txtUnitPrice").val());
		var amount = $.trim($(this).find(".txtAmount").val());
		if (aLName == "" && (qty == "" || qty == "0.00") && uom == "" && 
				(unitPrice == "" || unitPrice == "0.00") && 
				(amount == "" || amount == "0.00")) {
			disableEntries($(this));
			$(this).addClass("divVeil");
		}
	});
	updateTotalAmount();
}

function addTableRow() {
	$("#aRMLine thead tr").find("th:last").css("border-right", "none");
	$("#aRMLine tbody tr").find("td:last").css("border-right", "none");
	$("#aRMLine tbody tr").css("border-bottom", "1px solid #000000");
	$("#aRMLine tbody").find("td").addClass("tdProperties");
}

function assignCustAcctId() {
	currentCustAcctId = $("#arCustomerAccountId option:selected").val();
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
		newRow += "<img class='imgDelete' id='imgDelete"+aRMLineCurrentIndex+"' src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deleteRow(this);' />";
		newRow += "</td>";
		
		//Number
		newRow += "<td class='tdProperties' align='center'><input id='txtNumber"+aRMLineCurrentIndex+"' class='txtNumber' style='width: 100%; text-align: center;'  readonly='readonly'/></td>"; 

		//AR Line
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input id='txtArLineSetup"+aRMLineCurrentIndex+"' name='arMiscellaneousLines["+aRMLineCurrentIndex+"].arLineSetupName' \
			onkeydown='showARLineList(this.id);' onfocus='loadDefaultArLine(this);'  \
			class='txtArLineSetupName' style='width: 100%;'/>";
		newRow += "<input type='hidden' id='arLineSetupId"+aRMLineCurrentIndex+"' name='arMiscellaneousLines["+aRMLineCurrentIndex+"].arLineSetupId' class='hdnArLineSetupIds' />";
		newRow += "</td>";
		
		//QTY
		newRow += "<td class='tdProperties' valign='top' style='width: 20px;'>";
		newRow += "<input name='arMiscellaneousLines["+aRMLineCurrentIndex+"].quantity' id='quantity"+aRMLineCurrentIndex+"' style='width: 100%; text-align: right;' \
			class='txtQuantity' maxLength='7' onblur='formatMoney(this); computeAmount(this);'";
		newRow += "</td>";
		
		//UOM
		newRow += "<td class='tdProperties' align='center'>";
		newRow += "<input id='txtUnitMeasurement"+aRMLineCurrentIndex+"' name='arMiscellaneousLines["+aRMLineCurrentIndex+"].unitMeasurementName' onkeydown='showUOMList(this.id, "+aRMLineCurrentIndex+");' class='txtUnitMeasurementName' style='width: 100%; text-align: center;'/>";
		newRow += "<input type='hidden' id='unitOfMeasurementId"+aRMLineCurrentIndex+"' name='arMiscellaneousLines["+aRMLineCurrentIndex+"].unitOfMeasurementId' class='hdnUnitMeasurementIds' />";
		newRow += "</td>";
		
		//UP
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input name='arMiscellaneousLines["+aRMLineCurrentIndex+"].upAmount' id='upAmount"+aRMLineCurrentIndex+"' style='width: 100%; text-align: right;' \
			class='txtUpAmount' maxLength='13' onblur='formatMoney(this); computeAmount(this);'";
		newRow += "</td>";
		
		//Amount
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input name='arMiscellaneousLines["+aRMLineCurrentIndex+"].amount' id='amount"+aRMLineCurrentIndex+"' style='width: 100%; text-align: right;' class='txtAmount' \
			 		onkeydown='processNextRow(this, event);' onblur='computeAmount(this); formatMoney(this);' maxLength='13' /> ";
		newRow += "</td>";
		newRow += "</tr>";
		
		$("#aRMLine tbody").append(newRow);
		aRMLineCurrentIndex++;
	}
}

function getCustomer () {
	$.ajax({
		url: contextPath + "/getArCustomers?name="+$("#txtCustomer").val() + "&isExact=true",
		success : function(customer) {
			$("#spanCustomerError").text("");
			if (customer != null && customer[0] != undefined) {
				$("#arCustomerId").val(customer[0].id);
				$("#txtCustomer").val(customer[0].name);
			}
			filterCustomerAccts();
		},
		error : function(error) {
			$("#spanCustomerError").text("Invalid customer.");
			$("#txtCustomer").val("");
		},
		dataType: "json"
	});
}

function showCustomers () {
	var rmId = $("#receiptMethodId").val();
	var uri = contextPath + "/getArCustomers/byReceiptMethod?name="+$("#txtCustomer").val()
			+ "&receiptMethodId="+rmId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#arCustomerId").val(ui.item.id);
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
					if (ui.item != null) 
						$(this).val(ui.item.name);
					filterCustomerAccts();
				},
				error : function(error) {
					$("#spanCustomerError").text("Invalid customer.");
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

function filterCustomerAccts(){
	$("#arCustomerAccountId").empty();
	
	if ($.trim($("#txtCustomer").val()) == "")
		$("#arCustomerId").val("");
	else {
		var customerId = $("#arCustomerId").val();
		var uri = contextPath + "/arMiscellaneous/getArCustomerAccounts?arCustomerId="+customerId+
				"&receiptMethodId="+$("#receiptMethodId").val();
		
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
					$("#arCustomerAccountId").val(selectCustomerAcct).attr("selected",true);
					var customerAcctId = $("#arCustomerAccountId option:selected").val();
					arCustAcctTerms = new Array();
					for (var index = 0; index < data.length; index++){
						var rowObject =  data[index];
						var id = rowObject["id"];
						if (id == customerAcctId)
							defaultArLine = rowObject["defaultArLineSetup"];
						var arCustAcctTerm = new ArCustAcctLine(id, rowObject["defaultArLineSetup"]);
						arCustAcctTerms.push (arCustAcctTerm);
					}
					
					// This is to remove any duplication.
					var found = [];
					$("#arCustomerAccountId option").each(function() {
						if($.inArray(this.value, found) != -1) 
							$(this).remove();
					  	found.push(this.value);
					});
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAccountId", optionParser, postHandler);
	}
}

function ArCustAcctLine (arCustAcctId, arLine) {
	this.arCustAcctId = arCustAcctId;
	this.arLine = arLine;
}

function showARLineList(elemId) {
	if ($("#arCustomerAccountId").val() != null && $("#arCustomerAccountId").val() != "") {
		var uri = contextPath + "/getArLineSetups?name="+$("#"+elemId).val();
		var row = $(elemId).parent("td").parent("tr");
		if ($("#arCustomerAccountId").val() != null && $("#arCustomerAccountId").val() != "")
			uri += "&arCustAcctId="+$("#arCustomerAccountId").val();
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
						$("#"+elemId).focus();
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
					$("#"+elemId).focus();
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

function formatMoney (textboxId) {
	var money = accounting.formatMoney($("#" + textboxId).val());
	$("#" + textboxId).val(money.replace(/[$]/gi, ''));
}

function processNextRow(amount, event) {
	if(event.keyCode == 9 && !event.shiftKey) { //Tab key
		var amountId = $(amount).attr("id");
		var currentRow = Number(amountId.substr(6, amountId.length - 1)) + 1;
		var rowCount = $("#aRMLine tbody tr").length;
		var arLineSetupName = $("#txtArLineSetup"+(currentRow-1)).val();
		if(typeof arLineSetupName != "undefined" && arLineSetupName != "") {
			if(currentRow < rowCount) {
				var nextRow = $(amount).closest("tr").next();
				if(nextRow.hasClass("divVeil")) {
					enableEntries ($(nextRow));
					$("#imgDelete"+currentRow).focus();
				}
			} else {
				addARLine(1);
				updateArLineIndex();
				$("#txtNumber"+currentRow).focus();
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
	var rowCount = $("#aRMLine tbody tr").length;	
	if(rowCount < minimumRows){
		var rowsToAdd = minimumRows - rowCount;
		addARLine(rowsToAdd);
		processArLineRow(rowCount);
	}
	enableEntries ($("#aRMLine tbody tr:first"));
	if(row == 0 && rowCount == 0)
		addARLine(1);
	enableEntries ($("#aRMLine tbody tr:first"));
	updateTotalAmount();
	updateArLineIndex();
}

function processArLineRow (setRows) {
	$("#aRMLine tbody tr").each(function(index) {
		if (index >= setRows && index != 0)
			disableEntries($(this));
	});
}

//Fix the indexing of table rows
function updateArLineIndex () {
	var currentRow = 0;
	$("#aRMLine tbody tr").each(function(row) {
		currentRow = row;
		$(this).find(".imgDelete").attr("id", "imgDelete"+row);
		$(this).find(".txtNumber").attr("id", "txtNumber"+row).val(row+1);
		$(this).find(".txtArLineSetupName").attr("id", "txtArLineSetup"+row).attr("name", "arMiscellaneousLines["+row+"].arLineSetupName");
		$(this).find(".hdnArLineSetupIds").attr("id", "arLineSetupId"+row).attr("name", "arMiscellaneousLines["+row+"].arLineSetupId");
		$(this).find(".txtQuantity").attr("id", "quantity"+row).attr("name", "arMiscellaneousLines["+row+"].quantity");
		$(this).find(".txtUnitMeasurementName").attr("id", "txtUnitMeasurement"+row).attr("name", "arMiscellaneousLines["+row+"].unitMeasurementName");
		$(this).find(".hdnUnitMeasurementIds").attr("id", "unitOfMeasurementId"+row).attr("name", "arMiscellaneousLines["+row+"].unitOfMeasurementId");
		$(this).find(".txtUpAmount").attr("id", "upAmount"+row).attr("name", "arMiscellaneousLines["+row+"].upAmount");
		$(this).find(".txtAmount").attr("id", "amount"+row).attr("name", "arMiscellaneousLines["+row+"].amount");
	});
	aRMLineCurrentIndex = currentRow + 1;
}

function disableEntries (elem) {
	$(elem).closest("tr").find(".txtArLineSetupName").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtQuantity").val("").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtUnitMeasurementName").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtUpAmount").val("").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtAmount").val("").attr("disabled", "disabled");
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

function parseAmount() {
	if ($.trim($("#totalAmount").val()) == "")
		$("#totalAmount").val("0.0");
	else {
		var formatAmt = removeComma($("#totalAmount").val());
		$("#totalAmount").val(parseFloat(formatAmt,10));
	}
	$("#aRMLine tbody tr").each(function() {
		var arLineAmount = $(this).find(".txtAmount").val();
		if(arLineAmount != "") {
			var formatAmt = removeComma(arLineAmount);
			$(this).find(".txtAmount").val(parseFloat(formatAmt,10));
		}
		
		var quantity = $(this).find(".txtQuantity").val();
		if(quantity != "") {
			var formatQty = removeComma(quantity);
			$(this).find(".txtQuantity").val(parseFloat(formatQty,10));
		}
		
		var arLineUpAmount = $(this).find(".txtUpAmount").val();
		if(arLineUpAmount != "") {
			var formatUpAmt = removeComma(arLineUpAmount);
			$(this).find(".txtUpAmount").val(parseFloat(formatUpAmt,10));
		}
	});
}

function loadDefaultArLine(elem) {
	var row = $(elem).closest("tr");
	if (!$(elem).hasClass(".divVeil") &&  $(row).find(".txtArLineSetupName").val() == "") {
		if (defaultArLine != null && $.trim($(elem).val()) == "") 
			$(elem).val(defaultArLine.name);
	}
}

function assignCustomerAcct (select) {
	selectCustomerAcct = $(select).val();
	for (var i=0; i<arCustAcctTerms.length; i++) {
		if (selectCustomerAcct == arCustAcctTerms[i].arCustAcctId) {
			defaultArLine = arCustAcctTerms[i].arLine;
			break;
		}
	}
}

function formatMonetaryVal() {
	formatMoney("#totalAmount");
	
	$("#aRMLine tbody tr").each(function(i) {
		formatMoney($(this).find(".txtQuantity"));
		formatMoney($(this).find(".txtUpAmount"));
		formatMoney($(this).find(".txtAmount"));
    });
}

function formatMoney (textboxId) {
	var money = accounting.formatMoney($(textboxId).val());
	$(textboxId).val(money.replace(/[$]/gi, ''));
}

function computeAmount (elem) {
	var quantity = $(elem).parent("td").parent("tr").closest("tr").find(".txtQuantity").val();
	var unitPrice = $(elem).parent("td").parent("tr").closest("tr").find(".txtUpAmount").val();
	var txtAmount = $(elem).parent("td").parent("tr").closest("tr").find(".txtAmount");
	if ($.trim($(txtAmount).val()) == "" || $.trim($(txtAmount).val()) == "0.00") {
		if (quantity != "" && unitPrice != "") {
			quantity = removeComma(quantity);
			unitPrice = removeComma(unitPrice);
			var amount = (quantity * unitPrice);
			amount = accounting.formatMoney(amount).replace(/[$]/gi, '');
			$(txtAmount).val(amount);
			updateTotalAmount();
		}
	}
	updateTotalAmount();
}

function updateTotalAmount() {
	var total = 0;
	$("#aRMLine tbody tr").each(function(i) {
		var formatAmt = $(this).find(".txtAmount").val().replace(/\,/g,"");
		total += formatAmt != "" ? parseFloat(formatAmt) : 0;
	});
	$("#spanTotalAmount").html(accounting.formatMoney(total));
}

var isSaving = false;
function saveArMiscellaneous () {
	if(isSaving == false) {
		isSaving = true;
		parseAmount();
		$("#btnSaveArMiscellaneous").attr("disabled", "disabled");
		doPostWithCallBack ("arMiscellaneousForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if("${pId}" == 0)
					dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var customer = $("#txtCustomer").val();
				var isCheck = $("#arMiscellaneousTypeId").val() == 2;
				if("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				if (isCheck)
					$("#refNumber").removeAttr("disabled");
				$("#txtCustomer").val(customer);
				filterCustomerAccts();
				parseAmount();
				initArLines ();
				formatMonetaryVal();
				updateTotalAmount();
				updateArLineIndex();
				isSaving = false;
			}
			$("#btnSaveArMiscellaneous").removeAttr("disabled");
		});
	}
}

function enableDisableCheck(value) {
	// If equals to check enable check number field
	if (value == 2)
		$("#refNumber").removeAttr("disabled");
	else
		$("#refNumber").attr("disabled", "disabled");
}

function receiptMehodOnChanged() {
	$("#txtCustomer").val("");
	$("#arCustomerId").val("");
	filterCustomerAccts();
	$("#aRMLine tbody").empty();
	initArLines ();
	updateArLineIndex();
}

</script>
</head>
<body>
	<div id="divMiscellaneousList" style="display: none"></div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="arMiscellaneous" id="arMiscellaneousForm">
			<div class="modFormLabel">Other Receipt<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNo"/>
			<form:hidden path="arCustomerId"/>
			<form:hidden path="ebObjectId"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number:</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status:</td>
							<c:set var="status" value="${arMiscellaneous.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtarMiscellaneousStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AR Miscellaneous Header</legend>
					<table class="formTable">
						<form:hidden path="id" />
						<tr>
							<td class="labels">* Type</td>
							<td class="value">
								<form:select path="arMiscellaneousTypeId" cssClass="frmSelectClass" 
									onchange="enableDisableCheck(this.value);">
									<form:options items="${arMiscellaneousTypes}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Check Number</td>
							<td class="value">
								<form:input path="refNumber" id="refNumber" class="inputSmall" disabled="true" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="refNumber" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Receipt Method</td>
							<td class="value">
								<form:select path="receiptMethodId" cssClass="frmSelectClass" onchange="receiptMehodOnChanged();">
									<form:options items="${receiptMethods}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" id="txtCustomer" class="input"
									onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="getCustomer();" />
							</td>
						</tr>
						
						<tr>
							<td></td>
							<td colspan="2">
								<span id="spanCustomerError" class="error" style="margin-left: 12px;"></span>
								<form:errors path="arCustomerId" cssClass="error" />
							</td>
						</tr>
					
						<tr>
							<td class="labels">* Customer Account</td>
							<td class="value">
								<form:select path="arCustomerAccountId" id="arCustomerAccountId" cssClass="frmSelectClass" onchange="assignCustomerAcct (this);">
								</form:select>
							</td>
						</tr>
						
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="arCustomerAccountId" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Receipt No.</td>
							<td class="value"><form:input path="receiptNumber" id="receiptNumber" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="receiptNumber" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Receipt Date</td>
							<td class="value">
								<form:input path="receiptDate" id="receiptDate" onblur="evalDate('receiptDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('receiptDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="receiptDate"	cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Maturity Date</td>
							<td class="value">
								<form:input path="maturityDate" id="maturityDate" onblur="evalDate('maturityDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('maturityDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="maturityDate" cssClass="error" style="margin-left: 12px;"/>
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
								<form:input path="amount" id="totalAmount" class="numeric" style="text-align: right;" size="20" 
									onblur="formatMoney(this);" maxLength="13" />
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
					<table id="aRMLine" class="dataTable">
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
							<c:forEach items="${arMiscellaneous.arMiscellaneousLines}" var="arLine" varStatus="status">
								<tr>
									<!-- DELETE & SEARCH -->
									<td class="tdProperties" align="center">
										<input type="hidden" name='arMiscellaneousLines[${status.index}].id' value="${arLine.id}">
										<img class="imgDelete" src='${pageContext.request.contextPath}/images/delete_active.png' onclick="deleteRow(this);" />
									</td>
									<!-- NUMBER -->
									<td class='tdProperties' align='center'><input id="txtNumber${status.index}" class='txtNumber' style='width: 100%; text-align: center;'  readonly='readonly'/> 
									<!-- AR LINE -->
									<td class="tdProperties" valign="top">
										<c:set var="arLineSetupName" value="${arLine.arLineSetupName}"/>
										<c:if test="${arLine.arLineSetup ne null}">
											<c:set var="arLineSetupName" value="${arLine.arLineSetup.name}"/>
										</c:if>
										<input id='txtArLineSetup${status.index}' name='arMiscellaneousLines[${status.index}].arLineSetupName' onkeydown='showARLineList(this.id);' 
											onfocus='loadDefaultArLine(this);'
											value='${fn:escapeXml(arLineSetupName)}' class='txtArLineSetupName' style='width: 100%;' />
										<input type="hidden" id='arLineSetupId${status.index}' name='arMiscellaneousLines[${status.index}].arLineSetupId' 
											value="${arLine.arLineSetupId}" class='hdnArLineSetupIds'  />
									</td>
									<!-- QTY -->
									<td class="tdProperties">
										<input name='arMiscellaneousLines[${status.index}].quantity' id='quantity${status.index}' 
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
											name="arMiscellaneousLines[${status.index}].unitMeasurementName"
											value="${unitMeasurementName}" class='txtUnitMeasurementName' style='width: 100%; text-align: center;' />
										<input type="hidden" id='unitOfMeasurementId${status.index}' 
											value="${arLine.unitOfMeasurementId}" name='arMiscellaneousLines[${status.index}].unitOfMeasurementId' 
											class='hdnUnitMeasurementIds'/>
									</td>
									<!-- UP -->
									<td class='tdProperties' valign='top'>
										<input name='arMiscellaneousLines[${status.index}].upAmount' id='upAmount${status.index}' 
											value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arLine.upAmount}' />"
											style='width: 100%; text-align: right;' class='txtUpAmount' maxLength='13' 
											onblur='formatMoney(this); computeAmount(this);'/>	
									</td>
									<!-- AMOUNT -->
									<td class='tdProperties' valign='top'>
										<input name='arMiscellaneousLines[${status.index}].amount' id='amount${status.index}' 
											value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arLine.amount}' />" 
											style='width: 100%; text-align: right;' class='txtAmount' maxLength="13" 
											onkeydown='processNextRow(this, event);' 
											onblur='computeAmount(this); formatMoney(this);'
											maxLength="13" />
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
									<form:errors path="arMiscLineMessage" cssClass="error" />
								</td>
							</tr>
						</tfoot>
					</table>
				</fieldset>
				
				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSaveArMiscellaneous" value="Save" onclick="saveArMiscellaneous();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>