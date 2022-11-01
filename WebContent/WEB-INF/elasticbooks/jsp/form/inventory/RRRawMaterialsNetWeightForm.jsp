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
<script type="text/javascript">
var terms = new Array();
var $receivingReportTable = null;
var $otherChargesTable = null;
var $netKgTbl = null;
var $bagDiscountTbl = null;
var supplierAcctTerms =  new Array();
$(document).ready(function () {
	var termId = null;
	var dueDate = null;
	var supplierAcctId = null;
	disableAndSetCompany();
	$("#warehouseId").val(whId);
	if("${rrRawMaterial.id}" > 0) {
		termId = "${apInvoice.termId}";
		dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${apInvoice.dueDate}'/>";
		var whId = "${rrRawMaterial.receivingReport.warehouseId}";
		filterWarehouses(whId);
		populateSupplierAcctId();
	} else {
		filterWarehouses();
		computeDueDate();
	}
	//Populate term
	terms = [];
	<c:forEach items="${terms}" var="term">
		term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
	filterSupplierAccts(supplierAcctId, termId, dueDate);
});

function disableSelectFields() {
	var isComplete = "${rrRawMaterial.formWorkflow.complete}";
	var currentStatusId = "${rrRawMaterial.formWorkflow.currentStatusId}";
	if(isComplete == "true" || currentStatusId == 4) {
		//Disable the input and select of the tables
		$("#netKgTbl table tbody tr td").find("input").attr("disabled", true);
		$("#bagDiscountTbl table tbody tr td").find("input").attr("disabled", true);
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
			$("#txtStockCode").attr("disabled",true);
			$("#buyingPrice").attr("disabled",true);
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
	$("#buyingPrice").attr("disabled",false);
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
	updateOChargesAmt();
	initializeRriBagQty();
	initializeBagDiscountTbl();
	computeTotalBagsTbl("netKgTbl");
	alignRightFooter("netKgTbl");
	rightAlignSpan ();
	computeAmount ();
	computeGrandTotal();
	disableSelectFields();
}

function populateSupplierAcctId () {
	var supplierAcctId = "${rrRawMaterial.supplierAccountId}";
	var termId = "${rrRawMaterial.termId}";
	var dueDate = "${rrRawMaterial.dueDate}";
	filterSupplierAccts(supplierAcctId, termId, dueDate);
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

function filterSupplierAccts(supplierAcctId, termId, dueDate) {
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

function loadItems($txtbox) {
	var companyId = $("#companyId").val();
	var stockCode = $.trim($($txtbox).val());
	var uri = contextPath+"/getBuyingDetails/items?companyId="+companyId+"&stockCode="+processSearchName(stockCode);
	var $description = $("#tdItemDesription");
	var $uom = $("#tdUom");
	var $itemId = $("#hdnItemId");
	var $existingStocks = $("#tdExistingStocks");
	$($txtbox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(this).val(ui.item.stockCode);
			$($description).text(ui.item.description);
			$($uom).text(ui.item.unitMeasurement.name);
			$($itemId).val(ui.item.id);
			$($existingStocks).text(accounting.formatMoney(ui.item.existingStocks));
			return false;
		}, minLength: 2,
		change: function(event, ui){
			var item = ui.item;
			if(item != null && typeof item != undefined) {
				$(this).val(item.stockCode);
				$($itemId).val(item.id);
			}
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.stockCode+"-"+ item.description+"</a>" )
			.appendTo( ul );
	};
}

function getItem ($txtbox) {
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	var stockCode = $.trim($($txtbox).val());
	var $description = $("#tdItemDesription");
	var $uom = $("#tdUom");
	var $itemId = $("#hdnItemId");
	var $existingStocks = $("#tdExistingStocks");
	$("#stockCodeErrMsg").text("");
	$("#spanStockCodeErrMsg").text("");
	var uri=contextPath+"/getBuyingDetails/item?stockCode="+processSearchName(stockCode)+"&companyId="+companyId+"&warehouseId="+warehouseId;
	if (stockCode != "") {
		$.ajax({
			url: uri,
			success: function (item) {
				if (item != null) {
					$(this).val(item.stockCode);
					$($description).text(item.description);
					$($uom).text(item.unitMeasurement.name);
					$($itemId).val(item.id);
					$($existingStocks).text(accounting.formatMoney(item.existingStocks));
					getBuyingPrice();
				} else {
					$($txtbox).val("");
					$("#stockCodeErrMsg").text("");
					$("#spanStockCodeErrMsg").text("Invalid stock code.");
					$($txtbox).focus();
				}
			},
			error : function(error) {
				clearItemFields($description, $uom, $itemId, $existingStocks);
				$($txtbox).val("");
				$("#stockCodeErrMsg").text("");
				$("#spanStockCodeErrMsg").text("Invalid stock code.");
				$($txtbox).focus();
			},
			dataType: "json"
		});
	} else {
		clearItemFields($description, $uom, $itemId, $existingStocks);
	}
}

function clearItemFields($description, $uom, $itemId, $existingStocks) {
	$($description).text("");
	$($uom).text("");
	$($itemId).val(null);
	$($existingStocks).text("");
	$("#buyingPrice").val("");
	$("#hdnOrigBuyingPrice").val("");
	$("#tdAmount").html("");
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
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
		var amount = $(this).closest("tr").find(".amount").val();
		updateOChargesAmt(amount);
		computeGrandTotal();
	});

	$("#otherChargesTable").on("blur", ".tblInputText", function(){
		var uom = $.trim($(this).closest("tr").find(".unitMeasurementName").val());
		if( uom == null|| uom == ""){
			$(this).closest("tr").find(".unitOfMeasurementId").val("");
		}
	});

	$("#otherChargesTable").children().attr("width","100%");
}

function computeGrandTotal() {
	computeSubTotal();
	var $totalAmount = $("#tdAmount");
	var $otherChargesTotal = $("#otherChargesTable").find("tfoot tr .amount");
	grandTotal = accounting.unformat($totalAmount.html()) + accounting.unformat($otherChargesTotal.html());
	$("#spanOtherCharges").html(accounting.formatMoney($otherChargesTotal.html()));
	$("#grandTotal").html(accounting.formatMoney(grandTotal));
}

var isSaving = false;
function saveRrRawMaterials() {
	if(isSaving == false && $("#bagDiscountErrMsg").text() == "") {
		isSaving = true;
		enableSelectFields();
		var buyingPrice = $("#buyingPrice").val();
		var origBuyingPrice = $("#hdnOrigBuyingPrice").val();
		$("#buyingPrice").val(accounting.unformat(buyingPrice));
		$("#hdnOrigBuyingPrice").val(accounting.unformat(origBuyingPrice));
		$("#apInvoiceLinesJson").val($otherChargesTable.getData());
		$("#rriBagQuantitiesJson").val($netKgTbl.getData());
		$("#rriBagDiscountsJson").val($bagDiscountTbl.getData());
		$("#totalQty").val(accounting.unformat($("#netKgTbl tfoot tr").find(".netWeight").text()));
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
				var companyId = $("#companyId").val();
				var supplier = $("#supplierId").val();
				var supplierAcctId = $("#supplierAcctId").val();
				var warehouseId = $("#warehouseId").val();
				var poNumber = $("#poNumber").val();
				var grandTotal = $("#grandTotal").text();

				var stockCode = $("#txtStockCode").val();
				var itemId = $("#hdnItemId").val();
				var description = $("#tdItemDesription").html();
				var uom = $("#tdUom").html();
				var discountComputed = $("#tdDiscount").html();
				var dueDate = $("#dueDate").val();
				var termId = $("#selectTermId").val();

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
				$("#supplierAcctId").val(supplierAcctId);
				$("#poNumber").val(poNumber);
				$("#txtStockCode").val(stockCode);
				$("#hdnItemId").val(itemId);
				$("#tdItemDesription").html(description);
				$("#tdUom").html(uom);
				$("#buyingPrice").val(accounting.formatMoney(buyingPrice));
				$("#hdnOrigBuyingPrice").val(accounting.formatMoney(origBuyingPrice));
				filterWarehouses (warehouseId);
				filterSupplierAccts(supplierAcctId, termId, dueDate);
				rightAlignSpan();
				computeGrandTotal();
				isSaving = false;
			}
			$("#btnSave").removeAttr("disabled");
		});
	}
}


function getBuyingPrice () {
	var companyId = $("#companyId").val();
	var itemId = $("#hdnItemId").val();
	$.ajax({
		url: contextPath + "/getBuyingDetails/price?companyId="+companyId+"&itemId="+itemId,
		success : function(bp) {
			if(bp != null) {
				$("#buyingPrice").val(accounting.formatMoney(bp.buyingPrice));
				$("#hdnOrigBuyingPrice").val(accounting.formatMoney(bp.buyingPrice));
				computeAmount();
				computeGrandTotal();
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function populateSelect (uri, selectedValue, $select, postHandler) {
	$($select).empty();
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
	loadPopulateObject (uri, false, selectedValue, $select, optionParser, postHandler, true, true);
}

function initializeRriBagQty() {
	var rriBagQuantitiesJson = JSON.parse($("#rriBagQuantitiesJson").val());
	$("#netKgTbl").html("");
	var cPath = "${pageContext.request.contextPath}";
	$netKgTbl = $("#netKgTbl").editableItem({
		data: rriBagQuantitiesJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "quantity", "varType" : "int"},
				{"name" : "bagQuantity", "varType" : "double"},
				{"name" : "netWeight", "varType" : "double"},
				{"name" : "ebObjectId", "varType" : "int"}
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
				{"title" : "Bags",
					"cls" : "bagQuantity tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "15%",
					"handler" : new RawMaterialsHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Kilos",
					"cls" : "quantity tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "20%" },
				{"title" : "MTS",
					"cls" : "netWeight tblLabelNumeric",
					"editor" : "label",
					"visible" : true,
					"width" : "20%"}
				],
				"footer" : [
					{"cls" : "bagQuantity"},
					{"cls" : "quantity"},
					{"cls" : "netWeight"},
				],
				"disableDuplicateStockCode" : false
	});

	$("#netKgTbl").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#netKgTbl").on("blur", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.formatMoney($(this).val()));
		}

		var $tr = $(this).closest("tr");
		computeNetKg($tr);
		computeGrandTotal();
	});
	alignRightFooter("netKgTbl");
	$("#netKgTbl").children().attr("width","100%");
}

function alignRightFooter(tblId) {
	$("#"+tblId+" tfoot td").each(function(){
		$(this).attr("align", "right");
	});
}

function computeTotalBagsTbl(tblId) {
	var totalBags = 0;
	var totalKgQty = 0;
	var totalNetKg = 0;

	var $spanTotalBags = $("#"+tblId+" tfoot tr").find(".bagQuantity");
	var $spanTotalKgQty = $("#"+tblId+" tfoot tr").find(".quantity");
	var $spanTotalNetKg = $("#"+tblId+" tfoot tr").find(".netWeight");

	$("#"+tblId).find(" tbody tr").each(function(){
		totalBags += accounting.unformat($(this).find(".bagQuantity").val());
		totalKgQty += accounting.unformat($(this).find(".quantity").val());
		totalNetKg += accounting.unformat($(this).find(".netWeight").text());
	});

	$($spanTotalBags).text(accounting.formatMoney(totalBags));
	$($spanTotalKgQty).text(accounting.formatMoney(totalKgQty));
	$($spanTotalNetKg).text(accounting.formatMoney(totalNetKg));
	$("#weight").text(accounting.formatMoney(totalNetKg));
}

function computeNetKg ($tr) {
	var $netKg = $($tr).find(".netWeight");
	var $qty = $($tr).find(".quantity");

	var bagQty = accounting.unformat($($tr).find(".bagQuantity").val());
	var qty = accounting.unformat($($qty).val());
	var netKg = qty - ( bagQty / 2) ;
	$($netKg).parent().attr("align", "right");
	$($netKg).text(accounting.formatMoney(netKg));
}

function rightAlignSpan () {
	$("#netKgTbl").find("tbody tr").each(function(){
		$(this).find(".netWeight").parent().attr("align", "right");
	});
}

function initializeBagDiscountTbl() {
	var rriBagDiscountsJson = JSON.parse($("#rriBagDiscountsJson").val());
	$("#bagDiscountTbl").html("");
	var cPath = "${pageContext.request.contextPath}";
	$bagDiscountTbl = $("#bagDiscountTbl").editableItem({
		data: rriBagDiscountsJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "bagQuantity", "varType" : "double"},
				{"name" : "discountQuantity", "varType" : "double"},
				{"name" : "ebObjectId", "varType" : "int"}
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
				{"title" : "Bags",
					"cls" : "bagQuantity tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "15%",
					"handler" : new RawMaterialsHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Discounts",
					"cls" : "discountQuantity tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "20%" },
				{"title" : "Total",
					"cls" : "total tblLabelNumeric",
					"editor" : "label",
					"visible" : true,
					"width" : "20%" },
				],
				"footer" : [
					{"cls" : "total"}
				],
				"disableDuplicateStockCode" : false
	});

	$("#bagDiscountTbl").on("blur", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
		var $tr = $(this).closest("tr");
		computeGrandTotal();
		validateTotalBagsDiscount();
	});

	alignRightFooter("bagDiscountTbl");
	rightAlignSpan();
	$("#bagDiscountTbl").children().attr("width","100%");
}

function computeTotalDiscount() {
	var bags = 0.0;
	var discount = 0.0;
	var totalDiscount = 0.0;
	$("#bagDiscountTbl table tbody").find("tr").each(function (){
		var $total = $(this).find(".total");
		bags = accounting.unformat($(this).find(".bagQuantity").val());
		discount = accounting.unformat($(this).find(".discountQuantity").val());
		$($total).text(accounting.formatMoney(bags * discount));
		$($total).parent().attr("align", "right");
		totalDiscount += accounting.unformat($($total).text());
	});

	$("#bagDiscountTbl table tfoot").find(".total").text(accounting.formatMoney(totalDiscount));
}

function validateTotalBagsDiscount () {
	var totalBagsQty = Number($("#netKgTbl tfoot td").find(".bagQuantity").text());
	var totalBagDiscount = Number($("#bagDiscountTbl tfoot td").find(".bagQuantity").text());

	if(totalBagsQty < totalBagDiscount) {
		$("#bagDiscountErrMsg").text("Bag Discount Qty must not Exceed Bag Qty.")
	} else {
		$("#bagDiscountErrMsg").text("");
	}
}

function computeAmount () {
	var $bagDiscount = $("#bagDiscountTbl tfoot td");
	var totalBagDiscount = accounting.unformat($("#bagDiscountTbl tfoot td").find(".total").text());
	var totalNetKg = accounting.unformat($("#netKgTbl tfoot td").find(".netWeight").text());
	var buyingPrice = accounting.unformat($("#buyingPrice").val());

	var amount = (totalNetKg - totalBagDiscount) * buyingPrice;

	$("#tdAmount").html(accounting.formatMoney(amount));
	$("#discount").html(accounting.formatMoney(totalBagDiscount));
	$("#netWeight").html(accounting.formatMoney(totalNetKg - totalBagDiscount));
	$("#spanBuyingPrice").html(accounting.formatMoney(buyingPrice));
	$("#spanAmount").html(accounting.formatMoney(amount));
	$("#buyingPrice").val(accounting.formatMoney(buyingPrice));
}

function computeSubTotal() {
	computeTotalBagsTbl("netKgTbl");
	alignRightFooter("netKgTbl");
	computeTotalDiscount();
	computeAmount();
}

function assignSupplierAcct (select) {
	selectedSupplierAcct = $(select).val();
	for (var i=0; i<terms.length; i++) {
		if (selectedSupplierAcct == supplierAcctTerms[i].supplierAcctId) {
			$("#selectTermId").val(supplierAcctTerms[i].termId).attr("selected" ,true);
			break;
		}
	}
}

function SupplierAcctTerm (supplierAcctId, termId) {
	this.supplierAcctId = supplierAcctId;
	this.termId = termId;
}

</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="rrRawMaterial" id="rrRawMaterialsDiv">
		<div class="modFormLabel">Receiving Report - Raw Materials Palay<span class="btnClose" id="btnClose">[X]</span></div>
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
		<form:hidden path="rriBagQuantitiesJson" id="rriBagQuantitiesJson"/>
		<form:hidden path="rriBagDiscountsJson" id="rriBagDiscountsJson"/>
		<form:hidden path="rrRawMatItemDto.quantity" id="totalQty"/>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">RR-RM P No.</td>
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
						<td class="labels">* Scale Sheet No.</td>
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
						<td class="labels">* Stock Code</td>
						<td class="value">
							<form:input id="txtStockCode" path="rrRawMatItemDto.stockCode" cssClass="standard"
								onkeydown="loadItems(this);" onkeyup="loadItems(this);" onblur="getItem(this);"/>
							<form:hidden id="hdnItemId" path="rrRawMatItemDto.itemId" />
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="errorMsgStockCode" id="stockCodeErrMsg" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<span id="spanStockCodeErrMsg" class="error"></span>
						</td>
					</tr>
					<tr>
						<td class="labels">Description</td>
						<td class="value" id="tdItemDesription">
							${rrRawMaterial.rrRawMatItemDto.description}
						</td>
					</tr>

					<tr>
						<td class="labels">Unit Of Measurement</td>
						<td class="value" id="tdUom">
							${rrRawMaterial.rrRawMatItemDto.uom}
						</td>
					</tr>

					<tr>
						<td class="labels">Buying Price
							<input id="hdnOrigBuyingPrice" type="hidden"/>
						</td>
						<td class="value" id="tdBuyingPrice">
							<form:input path="rrRawMatItemDto.buyingPrice" cssStyle="text-align: right;" class="standard"  id="buyingPrice" onblur="computeGrandTotal();"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="errorMsgBuyingPrice" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Amount</td>
						<td class="value" >
							<span id="tdAmount" style="display: block; text-align: right; width: 171px;"></span>
						</td>
					</tr>

				</table>
			</fieldset>
			<!-- Weight -->
			<fieldset class="frmField_set">
				<legend>Weight</legend>
				<div id="netKgTbl">
				</div>
				<table>
					<tr>
						<td>
							<form:errors path="rriBagQtyErrMsg" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<!-- Discount -->
			<fieldset class="frmField_set">
				<legend>Discount</legend>
				<div id="bagDiscountTbl">
				</div>
				<table>
					<tr>
						<td>
							<span id="bagDiscountErrMsg" class="error"></span>
							<form:errors path="rriBagDiscountErrMsg" cssClass="error"/>
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
					<td align="right" width="90%">Weight</td>
					<td align="right" width="10%"><span id="weight">0.0</span></td>
				</tr>
				<tr>
					<td align="right" width="90%">Discount</td>
					<td align="right" width="10%"><span id="discount">0.0</span></td>
				</tr>
				<tr>
					<td align="right" width="90%">Net Weight</td>
					<td align="right" width="10%"><span id="netWeight">0.0</span></td>
				</tr>
				<tr>
					<td align="right" width="90%">Buying Price</td>
					<td align="right" width="10%"><span id="spanBuyingPrice">0.0</span></td>
				</tr>
				<tr>
					<td align="right" width="90%">Amount</td>
					<td align="right" width="10%"><span id="spanAmount">0.0</span></td>
				</tr>
				<tr>
					<td align="right" width="90%">Other Charges</td>
					<td align="right" width="10%"><span id="spanOtherCharges">0.0</span></td>
				</tr>
				<tr>
					<td align="right" width="90%">Grand Total</td>
					<td align="right" width="10%"><span id="grandTotal">0.0</span></td>
				</tr>
				<tr>
					<td align="right" colspan="2">
						<form:errors path="amount" cssClass="error"/>
						<span id="errorMessage" class="error"></span>
					</td>
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