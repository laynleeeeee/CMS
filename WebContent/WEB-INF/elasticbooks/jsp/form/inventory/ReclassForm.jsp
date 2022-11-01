<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Reclass form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<style type="text/css">
.toItemTd {
	background-color: #F2F1F0;
}

.fromExistingStock, .toExistingStock {
	float: right;
	width: 100%;
	text-align: right;
}
</style>
<script type="text/javascript">
var $repackingItemsTable = null;
$(document).ready (function () {
	loadDivision("${repacking.divisionId}");
	$("#divisionId").attr("disabled","disabled");
	if("${repacking.id}" == 0) {
		$("#spanFormStatus").text("NEW");
		filterWarehouses();
	} else {
		disableAndSetCompany();
		var warehouseId = "${repacking.warehouseId}";
		filterWarehouses(warehouseId);
		if("${repacking.formWorkflow.complete}" == "true" || "${repacking.formWorkflow.currentStatusId}" == 4)
			$("#repackingForm :input").attr("disabled","disabled");
	}
	initializeTable();
	initializeDocumentsTbl();
});

function loadDivision(divisionId) {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+"&divisionId="+divisionId;
	$("#divisionId").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},
		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	var postHandler = {
		doPost: function(data) {
			if (divisionId != 0 && divisionId != "" && divisionId != "undefined") {
				$("#divisionId").val(divisionId);
			}
		}
	};
	loadPopulate (uri, false, divisionId, "divisionId", optionParser, postHandler);
}

function initializeTable() {
	var repackingItemsJson = JSON.parse($("#hdnRepackingItemsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$repackingItemsTable = $("#repackingItemDivTable").editableItem({
		data : repackingItemsJson,
		jsonProperties : [
				{"name" : "id", "varType" : "int"},
				{"name" : "repackingId", "varType" : "int"},
				{"name" : "fromItemId", "varType" : "int"},
				{"name" : "toItemId", "varType" : "int"},
				{"name" : "origItemId", "varType" : "int"},
				{"name" : "origUnitCost", "varType" : "double"},
				{"name" : "quantity", "varType" : "double"},
				{"name" : "repackedQuantity", "varType" : "double"},
				{"name" : "fromStockCode", "varType" : "string"},
				{"name" : "toStockCode", "varType" : "string"},
				{"name" : "fromExistingStock", "varType" : "double"}, 
				{"name" : "toExistingStock", "varType" : "double"},
				{"name" : "origQty", "varType" : "double"},
				{"name" : "unitCost", "varType" : "double"},
				{"name" : "repackedUnitCost", "varType" : "double"},
				{"name" : "toOrigQty", "varType" : "double"}
		],
		contextPath : cPath,
		header : [
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "repackingId",
					"cls" : "repackingId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "fromItemId",
					"cls" : "fromItemId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "toItemId",
					"cls" : "toItemId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "origItemId",
					"cls" : "origItemId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "origUnitCost",
					"cls" : "origUnitCost",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "From<br>Stock Code",
					"cls" : "fromStockCode tblInputText",
					"editor" : "text",
					"visible": true,
					"width" : "10%"},
				{"title" : "Description",
					"cls" : "fromDescription tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "13%"},
				{"title" : "Existing <br> Stocks",
					"cls" : "fromExistingStock tblLabelNumeric",
					"editor" : "label",
					"visible": true,
					"width" : "5%"},
				{"title" : "Qty",
					"cls" : "quantity tblInputNumeric",
					"editor" : "text",
					"visible": true,
					"width" : "5%"},
				{"title" : "UOM",
					"cls" : "fromUom tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "5%"},
				{"title" : "Unit Cost",
					"cls" : "fromUnitCost tblLabelNumeric",
					"editor" : "label",
					"visible": true,
					"width" : "5%"},
				{"title" : "Total",
						"cls" : "fromTotal tblLabelNumeric",
						"editor" : "label",
						"visible": true,
						"width" : "5%"},
				{"title" : "To Stock Code",
					"cls" : "toStockCode tblInputText toItemTd",
					"editor" : "text",
					"visible": true,
					"width" : "10%",
					"tdCls" : "toItemTd"},
				{"title" : "Description",
					"cls" : "toDescription tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "12%",
					"tdCls" : "toItemTd"},
				{"title" : "Existing <br> Stocks",
					"cls" : "toExistingStock tblLabelNumeric",
					"editor" : "label",
					"visible": true,
					"width" : "5%",
					"tdCls" : "toItemTd"},
				{"title" : "Repacked<br>Qty",
					"cls" : "repackedQuantity tblInputNumeric toItemTd",
					"editor" : "text",
					"visible": true,
					"width" : "5%",
					"tdCls" : "toItemTd"},
				{"title" : "UOM",
					"cls" : "toUom tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "5%",
					"tdCls" : "toItemTd"},
				{"title" : "Unit Cost",
					"cls" : "toUnitCost tblLabelNumeric toItemTd",
					"editor" : "label",
					"visible": true,
					"width" : "5%",
					"tdCls" : "toItemTd"},
				{"title" : "Total",
					"cls" : "toTotal tblLabelNumeric toItemTd",
					"editor" : "label",
					"visible": true,
					"width" : "5%",
					"tdCls" : "toItemTd"},
				{"title" : "unitCost",
					"cls" : "unitCost",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "repackedUnitCost",
					"cls" : "repackedUnitCost",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "origQty",
					"cls" : "origQty",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "toOrigQty",
					"cls" : "toOrigQty",
					"editor" : "hidden",
					"visible" : false}
		],
		disableDuplicateStockCode : true,
		itemTableMessage : "repackingItemErrors"
	});

	$("#repackingItemDivTable").on("focus", ".quantity, .repackedQuantity", function() {
		$(this).val(accounting.unformat($(this).val()));
	});

	$("#repackingItemDivTable").on("blur", ".quantity, .repackedQuantity", function() {
		computeRepackedCost($(this));
	});

	$("#repackingItemDivTable").on("keydown keyup", ".fromStockCode", function(){
		loadItems($(this), true, $("#companyId").val());
	});

	$("#repackingItemDivTable").on("keydown keyup", ".toStockCode", function(){
		loadItems($(this), false, $("#companyId").val());
	});

	$("#repackingItemDivTable").on("blur", ".fromStockCode", function(){
		getRItem($(this), true, $(this).val(), false);
	});

	$("#repackingItemDivTable").on("blur", ".toStockCode", function(){
		getRItem($(this), false, $(this).val(), false);
	});

	reloadRItems();
}

var stockCode = null;
var currentItem = null;
var warehouseId = null;
var companyId = null;
function loadItems($txtBox, isFromSC) {
	warehouseId = $("#selectWarehouseId").val();
	stockCode = $($txtBox).val();
	var uri = contextPath+"/getRetailSerialItem/getRetailItems?isSerialized=false&isActive=true";
	if(warehouseId != undefined)
		uri += "&warehouseId="+warehouseId;
	uri += "&stockCode="+encodeURIComponent(stockCode);
	if(warehouseId != null) {
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				currentItem = ui.item.item;
				$(this).val (ui.item.item.stockCode);
				setValues($txtBox, isFromSC, currentItem);
				return false;
			}, minLength: 2,
			change: function(event, ui){
				stockCode = $(this).val();
				if (currentItem != null && stockCode == currentItem.stockCode){
					return false;
				}

				if(stockCode != "") {
					getRItem($txtBox, isFromSC, stockCode);
				}
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, row ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", row )
				.append( "<a style='font-size: small;'>"+ row.item.stockCode + "-" + row.item.description + "</a>" )
				.appendTo( ul );
		};
	}
}

function getRItem($txtBox, isFromSC, stockCode, isReloadItem) {
	var $row = $($txtBox).closest("tr");
	var warehouseId = $("#selectWarehouseId").val();
	var stockCode = $.trim(stockCode);
	var origItemId = $row.find(".origItemId").val();
	if (stockCode != "" && warehouseId != "") {
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getItem/withInactive?stockCode="+encodeURIComponent(stockCode)+"&warehouseId="+warehouseId;
		$.ajax({
			url: uri,
			success : function(item) {
				$("#repackingItemErrors").text("");
				var tableId = $row.find(".id").val();
				setValues($txtBox, isFromSC, item);
				if (isFromSC) {
					if ((tableId == 0 || tableId == "")){
						if(origItemId != item.id){
							getWeightedAverageCost($txtBox, item.id);
						}
					} else {
						if(origItemId != item.id){
							getWeightedAverageCost($txtBox, item.id);
						} else {
							var quantity = accounting.unformat($row.find(".quantity").val());
							var unitCost = accounting.unformat($row.find(".origUnitCost").val());
							$row.find(".fromUnitCost").html(formatDecimalPlaces(unitCost));
							$row.find(".fromTotal").html(formatDecimalPlaces(quantity * unitCost));
						}
					}
				}
			}, error : function(error) {
				if (!isReloadItem) {
					$("#repackingItemErrors").css("color", "red").text('Invalid Stock Code');
					$($txtBox).focus();
				}
			},
			dataType: "json"
		});
	} else {
		if (isFromSC) {
			$row.find(".fromUnitCost").html("0.00");
		} else {
			$row.find(".toUnitCost").html("0.00");
		}
	}
}

function computeRepackedCost($txtBox) {
	var $row = $($txtBox).closest("tr");
	var quantity = accounting.unformat($row.find(".quantity").val());
	var saveFromCost = accounting.unformat($row.find(".origUnitCost").val());
	var fromUnitCost = saveFromCost > 0 ? saveFromCost : accounting.unformat($row.find(".fromUnitCost").text());
	var repackedQty = accounting.unformat($row.find(".repackedQuantity").val());
	var repackedCost = 0;
	if (repackedQty != "" && repackedQty != 0) {
		repackedCost = (quantity * fromUnitCost) / repackedQty;
	}
	$row.find(".fromUnitCost").text(formatDecimalPlaces(parseFloat(fromUnitCost).toFixed(6)));
	$row.find(".toUnitCost").text(formatDecimalPlaces(parseFloat(repackedCost).toFixed(6)));
	$row.find(".fromTotal").text(formatDecimalPlaces(parseFloat(quantity * fromUnitCost).toFixed(6)));
	$row.find(".toTotal").text(formatDecimalPlaces(parseFloat(repackedQty * repackedCost).toFixed(6)));
}

function getWeightedAverageCost($txtBox, itemId) {
	var $row = $($txtBox).closest("tr");
	var companyId = $("#companyId").val();
	var warehouseId = $("#selectWarehouseId").val();
	var uri = contextPath + "/getItem/getWeightedAverageWithRate?companyId="+companyId
			+"&warehouseId="+warehouseId+"&itemId="+itemId;
	$.ajax({
		url: uri,
		success : function(unitCost) {
			var quantity = accounting.unformat($row.find(".quantity").val());
			$row.find(".fromUnitCost").text(formatDecimalPlaces(unitCost));
			$row.find(".fromTotal").text(formatDecimalPlaces(quantity * unitCost));
		},
		error : function(error) {
			console.log(error);
		}
	});
}

function setValues($txtBox, isFromSC, item) {
	if (isFromSC) {
		$($txtBox).closest("tr").find(".fromItemId").val(item.id);
		$($txtBox).closest("tr").find(".fromDescription").text(item.description);
		$($txtBox).closest("tr").find(".fromExistingStock").text(accounting.formatMoney(item.existingStocks));
		$($txtBox).closest("tr").find(".fromUom").text(item.unitMeasurement.name);
	} else {
		$($txtBox).closest("tr").find(".toItemId").val(item.id);
		$($txtBox).closest("tr").find(".toDescription").text(item.description);
		$($txtBox).closest("tr").find(".toExistingStock").text(accounting.formatMoney(item.existingStocks));
		$($txtBox).closest("tr").find(".toUom").text(item.unitMeasurement.name);
	}
	validateDuplicateStockcode($txtBox);
}

function validateDuplicateStockcode($initFromItemIdOrToItemId) {
	var $fromItemId =$($initFromItemIdOrToItemId).closest("tr").find(".fromItemId");
	if ($fromItemId == undefined)
		return;

	var $toItemId =$($initFromItemIdOrToItemId).closest("tr").find(".toItemId");
	if ($toItemId == undefined)
		return;

	var fromItemId = $fromItemId.val();
	var toItemId = $toItemId.val();

	if(fromItemId == toItemId)
		$("#repackingItemErrors").text("From and To stockcode must not be equal.");
}

function companyIdOnChange() {
	$repackingItemsTable.emptyTable();
	filterWarehouses();
}

function filterWarehouses(warehouseId) {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	if(companyId > 0) {
		var uri = contextPath+"/getWarehouse/new?companyId="+companyId+"&divisionId="+divisionId
				+(typeof warehouseId != "undefined" || warehouseId != null? "&warehouseId="+warehouseId : "");
		$("#selectWarehouseId").empty();
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
					populateExistingStock ($("#selectWarehouseId").val(), "fromExistingStock", "fromStockCode");
					populateExistingStock ($("#selectWarehouseId").val(), "toExistingStock", "toStockCode");
					if(warehouseId != "") {
						$("#selectWarehouseId").val(warehouseId);
					}
					reloadRItems();
				}
		};
		loadPopulate (uri, false, null, "selectWarehouseId", optionParser, postHandler);
	}
}

function populateExistingStock (warehouseId, existingStock, sCode) {
	$("." + existingStock).each(function () {
		var $existingStock = $(this);
		var stockCode = $(this).closest("tr").find("." + sCode).val();

		var companyId = $("#companyId").val();
		var uri = contextPath + "/getItem?stockCode="+encodeURIComponent(stockCode)+"&companyId="+companyId+
		(typeof warehouseId != "undefined" || warehouseId != null? "&warehouseId="+warehouseId : "")
		if(stockCode != "") {
			$.ajax({
				url: uri,
				success : function(item) {
					if (item != null) {
						var formatES = accounting.formatMoney(item.existingStocks);
						$($existingStock).text(formatES);
					}
				},error : function(error) {
					console.log(error);
				},
				dataType: "json"
			});
		}
	});
}

var isSaving = false;
function saveRepacking() {
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#documentsTable"))) {
		$("#btnSaveRepacking").attr("disabled", "disabled");
		setCompanyAndWarehouseId();
		$("#spDocSizeMsg").text("");
		isSaving = true;
		$("#hdnRepackingItemsJson").val($repackingItemsTable.getData());
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#divisionId").removeAttr("disabled");
		doPostWithCallBack ("repackingForm", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if("${repacking.id}" == 0){
					dojo.byId("form").innerHTML = "";
				}
				isSaving = false;
			} else {
				//Reload the form
				var companyId = $("#companyId").val();
				var warehouseId = $("#selectWarehouseId").val();
				var spanDivisionLbl = $("#spanDivisionLbl").text();
				var currentStatus = $("#txtCurrentStatusId").val();
				var formattedRNumber = $("#spanRNumber").val();
				if("${repacking.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#spanFormStatus").text("NEW");
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableAndSetCompany();
					$("#txtCurrentStatusId").val(currentStatus);
					$("#spanRNumber").val(formattedRNumber);
				}
				$("#companyId").val(companyId);
				$("#spanDivisionLbl").text(spanDivisionLbl);
				loadDivision("${repacking.divisionId}");
				$("#divisionId").attr("disabled","disabled");
				filterWarehouses(warehouseId);
				initializeTable();
				reloadRItems();
				initializeDocumentsTbl();
			}
			$("#btnSaveRepacking").removeAttr("disabled");
			isSaving = false;
		});
	} else if (checkExceededFileSize($("#documentsTable"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function reloadRItems() {
	$("#repackingItemDivTable tbody tr").each(function(row) {
		var $fromSC = $(this).find(".fromStockCode");
		getRItem($fromSC, true, $($fromSC).val(), true);

		var $toSC = $(this).find(".toStockCode");
		getRItem($toSC, false, $($toSC).val(), true);

		computeRepackedCost(this);
	});
}

function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${repacking.companyId}"+"'>"+
			"${repacking.company.numberAndName}"+"</option>");
	$("#selectWarehouseId").attr("disabled","disabled");
}

function setCompanyAndWarehouseId() {
	//Set only company and warehouse id only when form is new.
	var isNew = $("#hdnFormId").val() > 0 ? false : true;
	if(isNew) {
		$("#hdnCompanyId").val($("#companyId").val());
		$("#hdnWarehouseId").val($("#selectWarehouseId").val());
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

	$("#documentsTable").on("change", ".fileInput", function() {
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize);
	});

	$("#documentsTable tbody tr").each(function() {
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable").on("click", ".fileLink", function() {
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});

	$("#documentsTable table").attr("width", "100%");
}

</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="repacking" id="repackingForm">
		<div class="modFormLabel">Reclass<span id="spanDivisionLbl"> - ${repacking.division.name}</span> 
		<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id" id="hdnFormId"/>
		<form:hidden path="repackingTypeId"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="rNumber"/>
		<form:hidden path="warehouseId" id="hdnWarehouseId"/>
		<form:hidden path="companyId" id="hdnCompanyId"/>
		<form:hidden path="repackingItemsJson" id="hdnRepackingItemsJson"/>
		<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">RC No.</td>
						<td class="value">
							<input type="text" id="spanRNumber" class="textBoxLabel" readonly="readonly"
									value="${repacking.rNumber}"/>
					</tr>
					<tr>
						<td class="labels">Status</td>
							<c:set var="status" value="${repacking.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtCurrentStatusId" class="textBoxLabel"
									readonly="readonly" value='${status}'/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Reclass Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Company</td>
						<td class="value"><select class="frmSelectClass" id="companyId" onchange="companyIdOnChange();">
								<c:forEach items="${companies}" var="company">
									<option value="${company.id}">${company.numberAndName}</option>
								</c:forEach>
							</select></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="companyId"
								cssClass="error"/></td>
					</tr>
					<tr>
							<td class="labels">* Division</td>
							<td class="value">
								<form:select path="divisionId" id="divisionId" class="frmSelectClass"></form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="divisionId" cssClass="error" />
							</td>
						</tr>
					<tr>
						<td class="labels">* Warehouse</td>
						<td class="value"><select class=frmSelectClass id="selectWarehouseId">
								</select></td>
					</tr>
					<tr>
						<td></td>
						<td><form:errors path="warehouseId" cssClass="error"
								style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">* Date </td>
						<td class="value"><form:input path="rDate" style="width: 120px;"
								cssClass="dateClass2" id="date" onblur="evalDate('date')"/>
							<img id="rDate" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('date')" style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="rDate"
								cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels" align="right">Remarks</td>
						<td class="value" colspan="2"><form:textarea path="remarks" rows="3"
								style="width: 350px; resize: none;" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="remarks" cssClass="error" /></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Reclass Table</legend>
				<div id="repackingItemDivTable"></div>
			</fieldset>
			<table class="frmField_set">
				<tr>
					<td><span id="repackingItemErrors" class="error"></span></td>
				</tr>
				<tr>
					<td><form:errors path="repackingMessage" cssClass="error" /></td>
				</tr>
				<tr>
					<td><form:errors path="formWorkflowId" cssClass="error"/></td>
				</tr>
			</table>
			<fieldset class="frmField_set">
				<legend>Document/s</legend>
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
					<td><form:errors path="formWorkflowId" cssClass="error" /></td>
				</tr>
				<tr>
					<td colspan="3" align="right">
							<input type="button" value="Save" id="btnSaveRepacking" onclick="saveRepacking();"/></td>
				</tr>
			</table>
			<br>
		</div>
		<hr class="thin" />
	</form:form>
</div>
</body>
</html>