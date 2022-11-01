<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 


	Description: Repacking form.
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
	initializeTable();
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
});

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
				{"name" : "quantity", "varType" : "double"},
				{"name" : "repackedQuantity", "varType" : "double"},
				{"name" : "fromStockCode", "varType" : "string"},
				{"name" : "toStockCode", "varType" : "string"},
				{"name" : "fromExistingStock", "varType" : "double"}, 
				{"name" : "toExistingStock", "varType" : "double"},
				{"name" : "origQty", "varType" : "double"}],
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
				{"title" : "From<br>Stock Code",
					"cls" : "fromStockCode tblInputText",
					"editor" : "text",
					"visible": true,
					"width" : "10%"},
				{"title" : "Description",
					"cls" : "fromDescription tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "15%"},
				{"title" : "Existing <br> Stocks",
					"cls" : "fromExistingStock tblLabelNumeric",
					"editor" : "label",
					"visible": true,
					"width" : "7%"},
				{"title" : "Qty",
					"cls" : "quantity tblInputNumeric",
					"editor" : "text",
					"visible": true,
					"width" : "10%"},
				{"title" : "UOM",
					"cls" : "fromUom tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "7%"},
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
					"width" : "15%",
					"tdCls" : "toItemTd"},
				{"title" : "Existing <br> Stocks",
					"cls" : "toExistingStock tblLabelNumeric",
					"editor" : "label",
					"visible": true,
					"width" : "7%",
					"tdCls" : "toItemTd"},
				{"title" : "Repacked<br>Qty",
					"cls" : "repackedQuantity tblInputNumeric toItemTd",
					"editor" : "text",
					"visible": true,
					"width" : "10%",
					"tdCls" : "toItemTd"},
				{"title" : "UOM",
					"cls" : "toUom tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "7%",
					"tdCls" : "toItemTd"},
				{"title" : "origQty",
					"cls" : "origQty",
					"editor" : "hidden",
					"visible" : false}],
		disableDuplicateStockCode : true,
		itemTableMessage : "repackingItemErrors"
	});

	$("#repackingItemDivTable").on("focus", ".quantity, .repackedQuantity", function(){
		$(this).val(accounting.unformat($(this).val()));
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
}

var stockCode = null;
var currentItem = null;
var warehouseId = null;
var companyId = null;
function loadItems($txtBox, isFromSC) {
// 	console.log("Loading list of items");
	var companyId = $("#companyId").val();
	warehouseId = $("#selectWarehouseId").val();
	stockCode = $($txtBox).val();
	var uri = contextPath+"/getRItems/filter?companyId="+companyId;
	if(warehouseId != undefined)
		uri += "&warehouseId="+warehouseId;
	uri += "&stockCode="+stockCode;
	console.log(uri);
	if(warehouseId != null) {
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				currentItem = ui.item;
				$(this).val (ui.item.stockCode);
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
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>"+ item.stockCode + "-" +item.description + "</a>" )
				.appendTo( ul );
		};
	}
}

function getRItem($txtBox, isFromSC, stockCode, isReloadItem) {
// 	console.log("Get selected item by stockcode :" + stockCode);
	var companyId = $("#companyId").val() ;
	var warehouseId = "";
	stockCode = $.trim(stockCode);
	var uri = contextPath + "/getItem?stockCode="+encodeURIComponent(stockCode)+"&companyId="+companyId;
	if(warehouseId != undefined)
		warehouseId = $("#selectWarehouseId").val();
		uri += "&warehouseId="+warehouseId;
	if(stockCode != "" && warehouseId != "") {
		$.ajax({
			url: uri,
			success : function(item) {
				setValues($txtBox, isFromSC, item);
			},error : function(error) {
				if(!isReloadItem) {
					console.log(error);
					$("#repackingItemErrors").css("color", "red").text('Invalid Stock Code');
					$($txtBox).focus();
				}
			},
			dataType: "json"
		});
	}
}

function setValues($txtBox, isFromSC, item) {
	if(isFromSC) {
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
	if(companyId > 0) {
		var uri = contextPath+"/getWarehouse?companyId="+companyId;
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
		var uri = contextPath + "/getItem?stockCode="+encodeURIComponent(stockCode)+"&companyId="+companyId;
		if(typeof warehouseId != "undefined" && warehouseId != "")
			uri += "&warehouseId="+warehouseId;
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

function saveRepacking() {
	$("#btnSaveRepacking").attr("disabled", "disabled");
	setCompanyAndWarehouseId();
	$("#hdnRepackingItemsJson").val($repackingItemsTable.getData());
	doPostWithCallBack ("repackingForm", "form", function(data) {
		if (data.substring(0,5) == "saved") {
			var objectId = data.split(";")[1];
			var formStatus = new Object();
			formStatus.objectId = objectId;
			updateTable (formStatus);
			if("${repacking.id}" == 0)
				dojo.byId("form").innerHTML = "";
		} else {
			//Reload the form
			var companyId = $("#companyId option:selected").val();
			var warehouseId = $("#selectWarehouseId").val();
			if("${repacking.id}" == 0) {
				dojo.byId("form").innerHTML = data;
				$("#companyId").val(companyId);
				$("#spanFormStatus").text("NEW");
			} else {
				dojo.byId("editForm").innerHTML = data;
				disableAndSetCompany();
			}

			filterWarehouses(warehouseId);
			initializeTable();
			reloadRItems();
		}
		$("#btnSaveRepacking").removeAttr("disabled");
	});
}

function reloadRItems() {
	$("#repackingItemDivTable").find(" table tbody tr ").each(function(row) {
		var $txtBox = $(this);
		var $fromSC = $($txtBox).find(".fromStockCode");
		getRItem($fromSC, true, $($fromSC).val(), true);

		var $toSC = $($txtBox).find(".toStockCode");
		getRItem($toSC, false, $($toSC).val(), true);
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
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="repacking" id="repackingForm">
		<div class="modFormLabel">Repacking <span class="btnClose" id="btnClose">[X]</span></div>
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
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">RP No.</td>
						<td class="value"><span id="spanRNumber">
							<c:if test="${repacking.id > 0}">
								${repacking.formattedRNumber}</c:if></span></td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value"><span id="spanFormStatus">
							<c:if test="${repacking.formWorkflow != null}">
								${repacking.formWorkflow.currentFormStatus.description}</c:if></span></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Repacking Header</legend>
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
						<td class="value"><form:errors path="remarks"
								cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Repacking Table</legend>
				<div id="repackingItemDivTable"></div>
			</fieldset>
			<table class="frmField_set">
				<tr>
					<td><span id="repackingItemErrors" class="error">
							<form:errors path="repackingMessage" cssClass="error" /></span></td>
				</tr>
				<tr>
					<td><form:errors path="formWorkflowId" cssClass="error"/></td>
				</tr>
				<tr>
					<td colspan="3" align="right">
							<input type="button" value="Save" id="btnSaveRepacking" onclick="saveRepacking();"/></td>
				</tr>
			</table>
		</div>
		<hr class="thin" />
	</form:form>
</div>
</body>
</html>