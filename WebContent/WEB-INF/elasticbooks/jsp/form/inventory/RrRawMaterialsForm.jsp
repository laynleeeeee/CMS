<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Receiving Report for Raw Materials form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.rawmaterialshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
var terms = new Array();
var $receivingReportTable = null;
var $otherChargesTable = null;
var selectedSupplierAcct = "${rrRawMaterial.supplierAccountId}";
$(document).ready(function () {
	disableAndSetCompany();
	var whId = "${rrRawMaterial.receivingReport.warehouseId}";
	$("#warehouseId").val(whId);
	//Populate term
	terms = [];
	<c:forEach items="${terms}" var="term">
		term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
	if("${rrRawMaterial.id}" > 0) {
		filterWarehouses(whId);
		filterSupplierAccts("${rrRawMaterial.dueDate}", "${rrRawMaterial.termId}", selectedSupplierAcct);
		computeGrandTotal("${rrRawMaterial.amount}");
	} else {
		filterWarehouses();
		computeDueDate();
	}
});

function disableSelectFields() {
	var isComplete = "${rrRawMaterial.formWorkflow.complete}";
	var currentStatusId = "${rrRawMaterial.formWorkflow.currentStatusId}";
	if(isComplete == "true" || currentStatusId == 4) {
		//Disable the input and select of the tables
		$("#rrRawMaterialsItemTbl table tbody tr td").find("input, select").attr("disabled", true);
		$("#otherChargesTable table tbody tr td").find("input").attr("disabled", true);
		if(isComplete == "true") {
			//Disable only if workflow is completed.
			$("#warehouseId").attr("disabled", true);
			$("#poNumber").attr("disabled", true);
			$("#rrDate").attr("disabled",true);
			$("#supplierId").attr("disabled", true);
			$("#supplierAcctId").attr("disabled", true);
			$("#dueDate").attr("disabled",true);
			$("#selectTermId").attr("disabled",true);
			$("#imgDate1").hide();
			$("#imgDate3").hide();
			$(".delrow").hide();
			$("#btnSave").attr("disabled", true);
		} else if (currentStatusId == 4) {
			$("#divForm :input").attr("disabled", true);
			$("#btnSave").attr("disabled", true);
		}
	}
}

function Term (id, days) {
	this.id = id;
	this.days = days;
}

function disableAndSetCompany() {
	//Disable and set company
	if("${rrRawMaterial.id}" > 0) {
		$("#companyId").attr("disabled","disabled");
		$("#companyId").append("<option selected='selected' value='"+"${rrRawMaterial.receivingReport.companyId}"+"'>"+
				"${rrRawMaterial.receivingReport.company.numberAndName}"+"</option>");
	}
}


function enableSelectFields() {
	//Enable the drop down lists
	$("#companyId").attr("disabled",false);
	$("#warehouseId").attr("disabled",false);
	$("#supplierAcctId").attr("disabled",false);
	$("#rrDate").attr("disabled",false);
	$("#dueDate").attr("disabled",false);
	$("#selectTermId").attr("disabled",false);
}

function filterWarehouses(warehouseId) {
	var companyId = $("#companyId").val();
	if(companyId > 0) {
		var uri = contextPath+"/getWarehouse?companyId="+companyId;
		$("#warehouseId").empty();
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
					if(warehouseId != "") {
						$("#warehouseId").val(warehouseId);
					}
				}
		};
		loadPopulate (uri, false, warehouseId, "warehouseId", optionParser, postHandler);
	}

	initializetOtherCharges();
	initializeTable();
	updateOChargesAmt();
	disableSelectFields();
}

function companyIdOnChange () {
	filterWarehouses();
	$receivingReportTable.emptyTable();
	$otherChargesTable.emptyTable();
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

function SupplierAcctTerm (supplierAcctId, termId) {
	this.supplierAcctId = supplierAcctId;
	this.termId = termId;
}

function filterSupplierAccts(dueDate, termId, supplierAcctId) {
	$("#supplierAcctId").empty();
	var selectedCompanyId = $("#companyId").val();
	var selectedSupplierId = $("#aSupplierId").val();
	if(selectedSupplierId != "" && typeof selectedSupplierId != "undefined") {
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
				$("#supplierAcctId").val(supplierAcctId).attr("selected",true);
				var supplierAcctId = $("#supplierAcctId option:selected").val();
				supplierAcctTerms = [];
				//Set default supplier account
				for (var index = 0; index < data.length; index++){
					var rowObject =  data[index];
					var id = rowObject["id"];
					if (id == supplierAcctId){
						if(termId == null) {
							var defaultTerm = rowObject["termId"];
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
		loadPopulate (uri, false, supplierAcctId, "supplierAcctId", optionParser, postHandler);
	} else {
		$("#aSupplierId").val("");
	}
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

function initializeTable() {
	var rrItemsJson = JSON.parse($("#rrItemsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$("#rrRawMaterialsItemTbl").html("");
	$receivingReportTable = $("#rrRawMaterialsItemTbl").editableItem({
		data: rrItemsJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "apInvoiceId", "varType" : "int"},
				{"name" : "itemId", "varType" : "int"},
				{"name" : "stockCode", "varType" : "string"},
				{"name" : "quantity", "varType" : "double"},
				{"name" : "unitCost", "varType" : "double"},
				{"name" : "buyingAddOnId", "varType" : "int"},
				{"name" : "buyingDiscountId", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"}
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
				"width" : "13%",
				"handler" : new RawMaterialsHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "Existing <br> Stocks",
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
			{"title" : "buyingDiscountId",
				"cls" : "buyingDiscountId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "unitCost",
				"cls" : "unitCost",
				"editor" : "hidden",
					"visible" : false },
			{"title" : "buyingAddOnId",
				"cls" : "buyingAddOnId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Add On", 
				"cls" : "slctAddOn tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Buying Price",
				"cls" : "buyingPrice tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "Discount",
				"cls" : "slctDiscount tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "10%"},
			{"title" : "Discount <br>(Computed)",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%"},
			{"title" : "Amount",
				"cls" : "amount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "8%"}
		],
		"footer" : [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": "rrMessage"
	});

	$("#rrRawMaterialsItemTbl").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#rrRawMaterialsItemTbl").on("blur", ".tblInputNumeric", function(){
		computeGrandTotal();
	});

	$("#rrRawMaterialsItemTbl").on("blur", ".tblSelectClass", function(){
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
					"handler" : new RawMaterialsHandler (new function () {
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
					"handler" : new RawMaterialsHandler (new function () {
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

function computeGrandTotal(grandTotal) {
	if(typeof grandTotal == "undefined") {
		var $itemTblTotal = $("#rrRawMaterialsItemTbl").find("tfoot tr .amount");
		var $otherChargesTotal = $("#otherChargesTable").find("tfoot tr .amount");
		grandTotal = accounting.unformat($itemTblTotal.html())
					+ accounting.unformat($otherChargesTotal.html());
	}
	$("#grandTotal").html(accounting.formatMoney(grandTotal));
}

var isSaving = false;
function saveRrRawMaterials() {
	if(isSaving == false) {
		isSaving = true;
		enableSelectFields();
		$("#apInvoiceLinesJson").val($otherChargesTable.getData());
		$("#rrItemsJson").val($receivingReportTable.getData());
		$("#btnSave").attr("disabled", "disabled");
		doPostWithCallBack ("rrRawMaterialsDiv", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if("${apInvoice.id}" == 0)
					dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var termId = $("#selectTermId").val();
				var dueDate = $("#dueDate").val();
				var companyId = $("#companyId").val();
				var supplier = $("#supplierId").val();
				var warehouseId = $("#warehouseId").val();
				var poNumber = $("#poNumber").val();
				var grandTotal = $("#grandTotal").text();
				if("${rrRawMaterial.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val(companyId);
				}else{
					dojo.byId("editForm").innerHTML = data;
					disableSelectFields();
					updatePopupCss();
				}

				disableAndSetCompany();
				$("#supplierId").val(supplier);
				$("#poNumber").val(poNumber);
				filterWarehouses (warehouseId);
				filterSupplierAccts(dueDate, termId);
				computeGrandTotal(grandTotal);
				isSaving = false;
			}
			$("#btnSave").removeAttr("disabled");
		});
	}
}


</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="rrRawMaterial" id="rrRawMaterialsDiv">
		<div class="modFormLabel">Receiving Report - Raw Materials IS<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="ebObjectId"/>
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
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">RR - RM IS No.</td>
						<td class="value">
							<c:if test="${rrRawMaterial.id > 0}">
								${rrRawMaterial.receivingReport.formattedRRNumber}
							</c:if>
							<span id="sequenceNo"></span>
						</td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:choose>
								<c:when test="${rrRawMaterial.id > 0}">
									${rrRawMaterial.formWorkflow.currentFormStatus.description}
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
				<legend>RR - Raw Materials Header</legend>
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
							<form:select path="receivingReport.warehouseId" id="warehouseId" class="frmSelectClass"></form:select>
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
					</tr><tr>
						<td></td>
						<td colspan="2">
							<form:errors path="receivingReport.poNumber" cssClass="error" style="margin-left: 12px;"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Date</td>
						<td class="value">
							<form:input path="glDate" onblur="evalDate('rrDate');" 
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
							<form:input path="invoiceDate" onblur="evalDate('invoiceDate'); computeDueDate();"
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
						<td><form:errors path="termId" cssClass="error" style="margin-left: 12px;"/></td>
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

			<!-- Raw Materials Table -->
			<fieldset class="frmField_set">
				<legend>RR - Raw Materials Table</legend>
				<div id="rrRawMaterialsItemTbl" >
				</div>
				<table>
					<tr>
						<td><span id="rrMessage" class="error"></span></td>
					</tr>
					<tr>
						<td>
							<form:errors path="rrItems" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>

			<!-- Other Charges -->
			<fieldset class="frmField_set">
				<legend>Other Charges</legend>
				<div id="otherChargesTable"></div>
				<input type="hidden" id="hdnOChargesAmt">
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
				<tr><td ><input type="button" id="btnSave" value="Save" onclick="saveRrRawMaterials();" style="float: right;"/> </td></tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>