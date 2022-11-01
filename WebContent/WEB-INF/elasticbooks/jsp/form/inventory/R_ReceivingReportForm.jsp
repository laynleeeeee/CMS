<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Retail Receiving Report Form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.unitcosthandler.js"></script>
<style type="text/css">
.disableSelect{
	pointer-events: none;
	cursor: default;
}

.prevUC {
	float: right;
	width: 100%;
	text-align: right;
}
</style>
<script type="text/javascript">
var selectedCompanyId = -1;
var poReferenceWindow = null;
var $receivingReportTable = null;
var $otherChargesTable = null;
var selectedWarehouseId = "${apInvoice.receivingReport.warehouseId}";
var selectedSupplierAcct = "${apInvoice.supplierAccountId}";
var supplierAcctTerms =  new Array();
var terms =  new Array();
$(document).ready(function () {
	var termId = null;
	var dueDate = null;
	if("${apInvoice.id}" > 0) {
		termId = "${apInvoice.termId}";
		dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${apInvoice.dueDate}'/>";
		disableSelectFields("${apInvoice.formWorkflow.complete}");
		filterWarehouse (selectedWarehouseId);
		updateFieldsByFormStatus();
	} else {
		filterWarehouse ();
		computeDueDate();
	}
	//Populate term
	terms = [];
	<c:forEach items="${terms}" var="term">
		term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
	filterSupplierAccts(dueDate, termId);
});

function updateFieldsByFormStatus() {
	// If form workflow status = COMPLETED and CANCELLED
	var rrStatus = "${apInvoice.formWorkflow.complete}";
	if(rrStatus == "true" || "${apInvoice.formWorkflow.currentStatusId}" == 4) {
		disableFields(); //Disable all fields
		if(rrStatus)
			specialCaseEditing(); //Enable selected fields
	}
}

function Term (id, days) {
	this.id = id;
	this.days = days;
}

function disableSelectFields(isComplete) {
	//Disable the drop down lists
	disableAndSetCompany();
	if(isComplete == "true") {
		//Disable only if workflow is completed.
		$("#warehouseId").attr("disabled","disabled");
		$("#supplierAcctId").attr("disabled","disabled");
		$("#selectTermId").attr("disabled","disabled");
	}
}

function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${apInvoice.receivingReport.companyId}"+"'>"+
			"${apInvoice.receivingReport.company.numberAndName}"+"</option>");
}

function enableSelectFields() {
	//Enable the drop down lists
	$("#companyId").attr("disabled",false);
	$("#warehouseId").attr("disabled",false);
	$("#supplierAcctId").attr("disabled",false);
	$("#selectTermId").attr("disabled",false);
}

function initializeTable () {
	var rrItemsJson = JSON.parse($("#rrItemsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$("#receivingReportTable").html("");
	$receivingReportTable = $("#receivingReportTable").editableItem({
		data: rrItemsJson,
		jsonProperties: [
		                 {"name" : "id", "varType" : "int"},
		                 {"name" : "apInvoiceId", "varType" : "int"},
		                 {"name" : "itemId", "varType" : "int"},
		                 {"name" : "stockCode", "varType" : "string"},
		                 {"name" : "quantity", "varType" : "double"},
		                 {"name" : "unitCost", "varType" : "double"},
		                 {"name" : "srp", "varType" : "double"},
		                 ],
		contextPath: cPath,
		header: [
			{"title" : "id", 
				"cls" : "id", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "apInvoiceId", 
				"cls" : "apInvoiceId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "itemId", 
				"cls" : "itemId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code", 
				"cls" : "stockCode tblInputText", 
				"editor" : "text",
				"visible" : true,
				"width" : "10%",
				"handler" : new UnitCostTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "Existing <br> Stock",
				"cls" : "existingStock tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "UOM",
				"cls" : "uom tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Previous<br>Unit Cost",
				"cls" : "prevUC tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Gross Price", 
				"cls" : "unitCost tblInputNumeric", 
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "SRP", 
				"cls" : "srp tblLabelNumeric", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Amount", 
				"cls" : "amount tblLabelNumeric", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
		],
		"footer" : [
		        {"cls" : "amount"}
	    ],
	    "disableDuplicateStockCode" : false,
        "itemTableMessage": "rrMessage"
	});

	$("#receivingReportTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#receivingReportTable").on("blur", ".tblInputNumeric", function(){
		computeGrandTotal();
	});

	$("#receivingReportTable").on("blur", ".tblSelectClass", function(){
		computeGrandTotal();
	});
}

function initializetOtherCharges() {
	var otherChargesJson = JSON.parse($("#apInvoiceLinesJson").val());
	$("#otherChargesTable").html("");
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
					"width" : "20%",
					"handler" : new UnitCostTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "UP",
					"cls" : "upAmount tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "20%"},
				{"title" : "Amount",
					"cls" : "amount tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "20%"}
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
		computeGrandTotal();
	});
}

function updateAmountAndTotalAmount(grandTotal) {
	console.log("updating amount");
	//Update amount and total amount 
	var total = 0;
	$("#receivingReportTable tbody tr").each(function(row) {
		var qty = accounting.unformat($(this).find(".quantity").val());
		var uCost = accounting.unformat($(this).find(".unitCost").val());
		var totalAmount = qty * uCost;
		total += totalAmount;
		var $total = $(this).find(".amount");
		$total.html(accounting.formatMoney(totalAmount));
	});
	var $footerSpan = $("#receivingReportTable").find("tfoot tr .amount");
	$footerSpan.html(accounting.formatMoney(total));

	computeGrandTotal(grandTotal);
}

function computeGrandTotal(grandTotal) {
	if(typeof grandTotal == "undefined" || grandTotal <= 0) {
		var $itemTblTotal = $("#receivingReportTable").find("tfoot tr .amount");
		var $otherChargesTotal = $("#otherChargesTable").find("tfoot tr .amount");
		grandTotal = accounting.unformat($itemTblTotal.html())
					+ accounting.unformat($otherChargesTotal.html());
	}
	$("#grandTotal").html(accounting.formatMoney(grandTotal));
}

function SupplierAcctTerm (supplierAcctId, termId) {
	this.supplierAcctId = supplierAcctId;
	this.termId = termId;
}

function filterSupplierAccts(dueDate, termId) {
	$("#supplierAcctId").empty();
	if($.trim($("#supplierId").val()) == "") {
		$("#aSupplierId").val("");
	}else{
		selectedCompanyId = $("#companyId").val();
		var selectedSupplierId = $("#aSupplierId").val();
		var uri = contextPath+"/getApSupplierAccts?supplierId="+selectedSupplierId+"&companyId="+selectedCompanyId;
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		postHandler = {
				doPost: function(data) {
					$("#supplierAcctId").val(selectedSupplierAcct).attr("selected",true);
					var supplierAcctId = $("#supplierAcctId option:selected").val();
					supplierAcctTerms = [];
					for (var index = 0; index < data.length; index++){
						var rowObject =  data[index];
						var id = rowObject["id"];
						if (id == supplierAcctId){
							if(termId == null) {
								var defaultTerm = rowObject["termId"];
								console.log("defaultTerm :"+defaultTerm);
								$("#selectTermId").val(defaultTerm).attr("selected" ,true);
							}
						}
						var suppAcctTerm = new SupplierAcctTerm(id, rowObject["termId"]);
						supplierAcctTerms.push (suppAcctTerm);
					}

					//Compute due date
					if(dueDate == null) {
						computeDueDate();
					}
				}
		};
		loadPopulate (uri, false, selectedSupplierAcct, "supplierAcctId", optionParser, postHandler);
	}
}

function filterWarehouse (warehouseId) {
	var selectedCompanyId = $("#companyId").val();
	if(selectedCompanyId > 0) {
		$("#warehouseId").empty();
		var uri = contextPath+"/getWarehouse?companyId="+selectedCompanyId;
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
					$("#warehouseId option").each(function() {
						if($.inArray(this.value, found) != -1) 
							$(this).remove();
					  	found.push(this.value);
					});
					$("#warehouseId").val(selectedWarehouseId);
				}
		};
		loadPopulate (uri, false, selectedWarehouseId, "warehouseId", optionParser, postHandler);
	}

	initializetOtherCharges();
	initializeTable ();
	updateOChargesAmt();
	updateAmountAndTotalAmount("${apInvoice.amount}");
}

function assignWarehouse (select) {
	selectedWarehouseId = $(select).val();
}

function showSuppliers () {
	if($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Company is required");
	}else{
		var supplierName = encodeURIComponent($.trim($("#supplierId").val()));
		if($("#supplierId").val() != "") {
			var uri = contextPath + "/getSuppliers/new?name="+supplierName+ "&companyId=" + $("#companyId").val();	
			$("#supplierId").autocomplete({
				source: uri,
				select: function( event, ui ) {
					$("#aSupplierId").val(ui.item.id);
					$("#spanSupplierError").text("");
					$(this).val(ui.item.name);
					filterSupplierAccts();
					return false;
				}, minLength: 2,
				change: function(event, ui){
					$.ajax({
						url: uri,
						success : function(item) {
							$("#spanSupplierError").text("");
							if (ui.item != null && ui.item != undefined) {
								$("#aSupplierId").val(ui.item.id);
								$(this).val(ui.item.name);
							}
						},
						error : function(error) {
							$("#spanSupplierError").text("Please select supplier.");
							$("#supplierId").val("");
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
}

function getSupplier() {
	var supplierName = encodeURIComponent($.trim($("#supplierId").val()));
	if($("#companyId").val() != -1) {
		$.ajax({
			url: contextPath + "/getSuppliers/new?name="+supplierName+ "&companyId=" + $("#companyId").val() + "&isExact=true",
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#aSupplierId").val(supplier[0].id);
					$("#supplierId").val(supplier[0].name);
				}else
					$("#aSupplierId").val("");
				filterSupplierAccts();
			},
			error : function(error) {
				$("#supplierId").val("");
				$("#supplierAcctId").empty();
			},
			dataType: "json"
		});
	}
}

var isSaving = false;
function saveReceivingReport () {
	if(isSaving == false) {
		isSaving = true;
		enableSelectFields();
		$("#rrItemsJson").val($receivingReportTable.getData());
		$("#apInvoiceLinesJson").val($otherChargesTable.getData());
		$("#btnSaveReceivingReport").attr("disabled", "disabled");
		doPostWithCallBack ("rReceivingReportDiv", "form", function(data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved" ? true : false;
			if(isSuccessfullySaved) {
				var formNumber = $.trim(parsedData[1]);
				var formStatus = new Object();
				formStatus.title = "Receiving Report";
				formStatus.message ="Successfully saved RR No. " + formNumber;
				updateTable (formStatus);
				if("${apInvoice.id}" == 0)
					dojo.byId("form").innerHTML = "";
				isSaving = false;
			}else{
				var termId = $("#selectTermId").val();
				var dueDate = $("#dueDate").val();
				var supplier = $("#supplierId").val();
				var warehouseId = $("#warehouseId").val();
				var poNumber = $("#poNumber").val();
				var companyId = $("#companyId option:selected").val();
				if("${apInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val(companyId);
				}else{
					dojo.byId("editForm").innerHTML = data;
					disableSelectFields();
					updatePopupCss();
				}

				$("#supplierId").val(supplier);
				$("#poNumber").val(poNumber);
				filterWarehouse (warehouseId);
				filterSupplierAccts(dueDate, termId);
				isSaving = false;
			}
			updateAmountAndTotalAmount();
			updateFieldsByFormStatus();
			$("#btnSaveReceivingReport").removeAttr("disabled");
		});
	}
}

function specialCaseEditing() {
	$("#invoiceDate").removeAttr("disabled");
	$("#supplierInvoiceNo").removeAttr("disabled");
	$("#deliveryReceiptNo").removeAttr("disabled");
	$("#imgDate1").hide();
	$(".delrow").hide();
	$("#btnSaveReceivingReport").removeAttr("disabled");
}

function disableFields(){
	$("#poNumber").attr("readonly", true);
	$("#supplierId").attr("readonly", true);
	$("#imgDate1").hide();
	$("#rrDate").attr("readonly", true);
	$("#imgDate3").hide();
	$("#dueDate").attr("readonly", true);
	$(".delrow").hide();
	$(".stockCode").attr("disabled", "disabled");
	$(".quantity").attr("disabled", "disabled");
	$(".unitCost").attr("disabled", "disabled");
	$("#btnSaveReceivingReport").attr("disabled", "disabled");
}

function computeDueDate () {
	var glDateVal = $("#rrDate").val ();
	if (glDateVal == null || glDateVal == ""){
		return;
	}
	var additionalDays = 0;
	var currentSelTermId = $("#selectTermId option:selected").val();
	for (var i = 0; i < terms.length; i++) {
		var term = terms[i];
		if (term.id == currentSelTermId) {
			additionalDays = term.days;
			break;
		}
	}
	var glDate = new Date (glDateVal);
	glDate.setDate(glDate.getDate() + parseInt(additionalDays));
	$("#dueDate").val ((glDate.getMonth() + 1) +"/"+glDate.getDate()+"/"+glDate.getFullYear());
}

function assignSupplierAcct (select) {
	console.log("assign supplier account");
	selectedSupplierAcct = $(select).val();
	for (var i=0; i<supplierAcctTerms.length; i++) {
		if (selectedSupplierAcct == supplierAcctTerms[i].supplierAcctId) {
			$("#selectTermId").val(supplierAcctTerms[i].termId).attr("selected" ,true);
			break;
		}
	}
}

function companyIdOnChange () {
	filterWarehouse();
	$receivingReportTable.emptyTable();
	$otherChargesTable.emptyTable();
	$("#supplierId").val("");
	$("#supplierAcctId").empty();
	computeGrandTotal();
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="apInvoice" id="rReceivingReportDiv">
		<div class="modFormLabel">Receiving Report <span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="invoiceTypeId"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="sequenceNumber"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="receivingReport.id" id="receivingReportId"/>
		<form:hidden path="receivingReport.createdBy"/>
		<form:hidden path="receivingReport.createdDate"/>
		<form:hidden path="rrItemsJson" id="rrItemsJson"/>
		<form:hidden path="apInvoiceLinesJson" id="apInvoiceLinesJson"/>
		<form:hidden path="formWorkflow.currentFormStatus.description" id="workflowDescription"/>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">Receiving Report No.</td>
						<td class="value">
							<c:if test="${apInvoice.id > 0}">
								${apInvoice.receivingReport.formattedRRNumber}
							</c:if>
							<span id="sequenceNo"></span>
						</td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:choose>
								<c:when test="${apInvoice.id > 0}">
									${apInvoice.formWorkflow.currentFormStatus.description}
								</c:when>
								<c:otherwise>
									NEW
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Receiving Report Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Company </td>
						<td class="value">
							<form:select path="receivingReport.companyId" id="companyId" cssClass="frmSelectClass" onchange="companyIdOnChange();"
								items="${companies}" itemLabel="numberAndName" itemValue="id" >
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanCompanyError" class="error" style="margin-left: 12px;"></span>
							<form:errors path="receivingReport.companyId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Warehouse </td>
						<td class="value">
							<form:select path="receivingReport.warehouseId" id="warehouseId" class="frmSelectClass" onchange="assignWarehouse(this);"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value" >
							<span id="spanWarehouseError" class="error"></span>
							<form:errors path="receivingReport.warehouseId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">PO Number</td>
						<td class="value"><form:input path="receivingReport.poNumber" id="poNumber" class="standard" /></td>
					</tr>
					<tr>
						<td class="labels">* Date</td>
						<td class="value">
							<form:input path="glDate" onblur="evalDate('rrDate'); computeDueDate();" 
								id="rrDate" style="width: 120px;" cssClass="dateClass2"/>
							<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('rrDate')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="glDate" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Invoice Date</td>
						<td class="value">
							<form:input path="invoiceDate" onblur="evalDate('invoiceDate')"
								id="invoiceDate" style="width: 120px;" cssClass="dateClass2"/>
							<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('invoiceDate')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="invoiceDate" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Supplier</td>
						<td class="value">
							<form:input path="supplier.name" id="supplierId" class="input" onkeydown="showSuppliers();"
								onkeyup="showSuppliers();" onblur="getSupplier();" />
							<form:input path="supplierId" type="hidden" id="aSupplierId" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanSupplierError" class="error" style="margin left: 12px;"></span>
							<form:errors path="supplierId" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Supplier Account</td>
						<td class="value">
							<form:select path="supplierAccountId" id="supplierAcctId" class="frmSelectClass" onchange="assignSupplierAcct(this);"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanSupplierAcctError" class="error" style="margin left: 12px;"></span>
							<form:errors path="supplierAccountId" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Term </td>
						<td class="value"><form:select path="termId" cssClass="frmSelectClass" id="selectTermId" onchange="computeDueDate();">
							<form:options items="${terms}" itemLabel="name" itemValue="id"></form:options>
								</form:select></td>
					</tr>
					<tr>
						<td></td>
						<td><form:errors path="termId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Due Date</td>
						<td class="value">
						<form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate')"
							style="width: 120px;" class="dateClass2" />
							<img id="imgDate3" src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dueDate')" style="cursor: pointer" style="float: right;" /></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="dueDate" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">Supplier Invoice No</td>
						<td class="value"><form:input path="invoiceNumber" id="supplierInvoiceNo" class="standard" /></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<span id="spanSupplierInvoiceError" class="error" style="margin left: 12px;"></span>
							<form:errors path="invoiceNumber" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Delivery Receipt No</td>
						<td class="value"><form:input path="receivingReport.deliveryReceiptNo" id="deliveryReceiptNo" class="standard" /></td>
					</tr>
					<tr>
				 		<td></td>
				 		<td colspan="2">
				 			<span id="spanDeliveryReceiptError" class="error" style="margin left: 12px;"></span>
				 			<form:errors path="receivingReport.deliveryReceiptNo" cssClass="error" style="margin-left: 12px;"/>
				 		</td>
				 	</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Receiving Report Table</legend>
				<div id="receivingReportTable" >
				</div>
				<table>
					<tr>
						<td><span id="rrMessage" class="error"></span></td>
					</tr>
					<tr>
						<td>
							<form:errors path="aPlineMessage" cssClass="error"/>
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
							<form:errors path="apInvoiceLines" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>

			<!-- Grand Total -->
			<table class="frmField_set">
				<tr>
					<td>Grand Total</td>
					<td align="right"><span id="grandTotal">0.0</span></td>
				</tr>
				<tr>
					<td><form:errors path="amount" cssClass="error"/><span id="errorMessage" class="error"></span></td>
				</tr>
			</table>

			<table style="margin-top: 10px;" class="frmField_set">
				<tr><td ><input type="button" id="btnSaveReceivingReport" value="Save" onclick="saveReceivingReport();" style="float: right;"/> </td></tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>