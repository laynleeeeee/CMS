<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description:Transfer Receipt form for Individual Selection.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	if("${rTransferReceipt.id}" == 0) {
		$("#spanFormStatus").text("NEW");
		filterWarehouses(true, false, null, null, true, false);
	} else {
		disableAndSetCompany();
		//Disable all input and delete image
		if("${rTransferReceipt.formWorkflow.complete}" == "true"
				|| "${rTransferReceipt.formWorkflow.currentStatusId}" == 4) {
			$("#rTRForm :input").attr("disabled","disabled");
			$("#trItemTable tbody tr").find(".imgDelete").attr("onclick", "");
		}
		//Select warehouses
		var wFromId = "${rTransferReceipt.warehouseFromId}";
		var wToId = "${rTransferReceipt.warehouseToId}";
		filterWarehouses(true, true, wFromId, wToId , true, true);
	}
});

function initializeTable() {
	var receiptItemsJson = JSON.parse($("#hdnReceiptItemsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$receiptItemTable = $("#receiptItemDivTable").editableItem({
		data : receiptItemsJson,
		jsonProperties : [
				{"name" : "id", "varType" : "int"},
				{"name" : "rTransferReceiptId", "varType" : "int"},
				{"name" : "itemId", "varType" : "int"},
				{"name" : "referenceObjectId", "varType" : "int"},
				{"name" : "origRefObjectId", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "quantity", "varType" : "double"},
				{"name" : "stockCodeIs", "varType" : "string"},
				{"name" : "unitCost", "varType" : "double"},
				{"name" : "origQty", "varType" : "double"},
				{"name" : "itemBagQuantity", "varType" : "double"}
			],
		contextPath : cPath,
		header : [
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "rTransferReceiptId",
					"cls" : "rTransferReceiptId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "itemId",
					"cls" : "itemId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "referenceObjectId",
					"cls" : "referenceObjectId",
					"editor" : "hidden",
					"visible": false},
				{"title" : "origRefObjectId",
					"cls" : "origRefObjectId",
					"editor" : "hidden",
					"visible": false},
				{"title" : "ebObjectId",
					"cls" : "ebObjectId",
					"editor" : "hidden",
					"visible": false},
				{"title" : "Stock Code",
					"cls" : "stockCodeIs tblInputText",
					"editor" : "text",
					"visible": true,
					"width" : "20%"},
				{"title" : "Description",
					"cls" : "description tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "30%"},
				{"title" : "Available <br> Bags/Stocks",
					"cls" : "availableStock tblSelectClass",
					"editor" : "select",
					"visible" : true, 
					"width" : "7%"},
				{"title" : "Bags",
					"cls" : "itemBagQuantity tblInputNumeric",
					"editor" : "text",
					"visible": true,
					"width" : "12%"},
				{"title" : "Qty",
					"cls" : "quantity tblInputNumeric",
					"editor" : "text",
					"visible": true,
					"width" : "15%"},
				{"title" : "UOM",
					"cls" : "uom tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "10%"},
				{"title" : "unitCost",
					"cls" : "unitCost",
					"editor" : "hidden",
					"visible": false},
				{"title" : "origQty",
					"cls" : "origQty",
					"editor" : "hidden",
					"visible" : false}],
		disableDuplicateStockCode : true,
		itemTableMessage : "trItemErrors"
	});

	$("#receiptItemDivTable").on("focus", ".quantity", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#receiptItemDivTable").on("change", ".availableStock", function(){
		setReferenceDetails($(this));
	});

	$("#receiptItemDivTable").on("keydown keyup", ".stockCodeIs", function(){
		loadItems($(this));
	});

	$("#receiptItemDivTable").on("blur", ".stockCodeIs", function(){
		var $stockCode = $(this).closest("tr").find(".stockCodeIs");
		getItem ($stockCode, false);
	});

	$("#receiptItemDivTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateQty($tr, $("#companyId").val());
	});
}

function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${rTransferReceipt.companyId}"+"'>"+
			"${rTransferReceipt.company.numberAndName}"+"</option>");
}

function filterWarehouses(isFrom, isSetWarehouse, wFromId, wToId, isInitializeTbl, isReloadRItems) {
	var companyId = $("#companyId").val();
	var selectId = isFrom == true ? "warehouseId" : "selectWToId";
	if(companyId > 0) {
		var uri = contextPath+"/getWarehouse?companyId="+companyId;
		$("#"+selectId).empty();
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
					if(isFrom) {
						filterWarehouses(false, isSetWarehouse, wFromId, wToId,
								isInitializeTbl, isReloadRItems);
						if(wFromId != null)
							$("#warehouseId").val(wFromId);
					} else {
						if(isSetWarehouse) {
							//Set the selected warehouse to after an error validation
							$("#selectWToId").val(wToId);
						}
						//Initialize table after the warehouse is loaded.
						if(isInitializeTbl) {
							initializeTable();
							if(isReloadRItems) {
								reloadRItems();
							}
						}
					}
				}
		};
		loadPopulate (uri, false, null, selectId, optionParser, postHandler);
	}
}

var currentItem = null;
function loadItems($txtBox) {
	$($txtBox).closest("tr").find(".availableStock").empty();
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	if(warehouseId != undefined) {
		var stockCode = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath + "/getRItems/filter?companyId="+companyId
				+"&warehouseId="+warehouseId+"&stockCode="+stockCode;
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				currentItem = ui.item;
				$(this).val(currentItem.stockCode);
				setValues($txtBox, currentItem.stockCode);
				return false;
			}, minLength: 2,
			change: function(event, ui) {
				stockCode = $(this).val();
				if (currentItem != null && stockCode == currentItem.stockCode){
					return false;
				}
				$($txtBox).val (ui.stockCode);
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>"+ item.stockCode + "-" +item.description + "</a>" )
				.appendTo( ul );
		};
	}
}

function reloadRItems() {
	$("#receiptItemDivTable").find(" table tbody tr").each(function(row) {
		var $stockCodeIs = $(this).find(".stockCodeIs");
		if($.trim($stockCodeIs.val()) != "") {
			getItem($stockCodeIs);
		}
	});
}

function getItem ($txtBox) {
	var stockCode = $.trim($($txtBox).val());
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getItem/withInactive?stockCode="+encodeURIComponent(stockCode)
			+"&companyId="+companyId+"&isWholesale=false";
	if(stockCode != "") {
		$.ajax({
			url: uri,
			success : function(item) {
				$($txtBox).val(item.stockCode);
				$($txtBox).closest("tr").find(".description").text(item.description);
				$($txtBox).closest("tr").find(".uom").text(item.unitMeasurement.name);
				$($txtBox).closest("tr").find(".itemId").val(item.id);
				setValues($txtBox, stockCode);
			},
			error : function(error) {
				$($txtBox).val(stockCode);
				$("#trItemErrors").text('Invalid Stock Code');
				$($txtBox).focus();
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function setValues($txtBox, stockCode) {
	var $ebObject = $($txtBox).closest("tr").find(".ebObjectId");
	var $refObjectId = $($txtBox).closest("tr").find(".referenceObjectId");
	var $orig = $($txtBox).closest("tr").find(".origRefObjectId");
// 	console.log("set values. ref id: "+$($refObjectId).val()+" === eb object id: "+$($ebObject).val()+" === orig "+$orig.val());
	if("${rTransferReceipt.id}" > 0 && $($ebObject).val() != "" && $($orig).val() == "") {
		//OR Type is hard coded to 8 for Transfer Receipt Relatiionship
		$.ajax({
			url: contextPath + "/getRefObject/id?ebObjectId="+$($ebObject).val()+"&orTypeId=8",
			success : function(refId) {
				$($refObjectId).val(refId);
				populateAvailableStock($txtBox, stockCode, $($refObjectId).val());
			},
			dataType: "json"
		});
	} else {
		populateAvailableStock($txtBox, null);
	}
}

function populateAvailableStock ($txtBox, refObjectId) {
	if(refObjectId == null) {
		refObjectId = $($txtBox).closest("tr").find(".referenceObjectId").val();
	}
	var warehouseId = $("#warehouseId").val();
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var ebObjectId = $($txtBox).closest("tr").find(".ebObjectId").val();
	var $availableStock = $($txtBox).closest("tr").find(".availableStock");
	if (typeof $availableStock != "undefined" && itemId != "") {
		$($availableStock).empty();
		var companyId = $("#companyId").val();
		$.ajax({
			url: contextPath + "/getAvailableBags?companyId="+companyId+"&itemId="+itemId
					+"&warehouseId="+warehouseId+"&ebObjectId="+ebObjectId,
			success : function(availableStocks) {
				for ( var index = 0; index < availableStocks.length; index++) {
					var availableStock = availableStocks[index];
					var option = "<option ";
					if(refObjectId != null) {
						if(refObjectId == availableStock.sourceObjId) {
							option += " selected='selected'";
						}
					}
					option += " id='" + availableStock.sourceObjId + "'";
					option += " value='" + availableStock.unitCost + "'>";
					var label = availableStock.source + " - " + availableStock.totalBags + " - " + availableStock.totalStocks;
					option += label + "</option>";
					$($availableStock).append(option);
				}
				setReferenceDetails($($availableStock));
			},
			dataType: "json"
		});
	}
}

function setReferenceDetails($txtBox) {
	var $availableStock = $($txtBox).closest("tr").find(".availableStock");
	if($($availableStock).val() != undefined) {
		//Set the reference object id
		var ebObjectId = $($availableStock).children(":selected").attr("id");
		var $referenceObjectId = $($txtBox).closest("tr").find(".referenceObjectId");
		$($referenceObjectId).val(ebObjectId);
		//Set the unit cost
		var unitCost = $($availableStock).val();
		var $unitCost = $($txtBox).closest("tr").find(".unitCost");
		$($unitCost).val(unitCost);
	}
}

function saveRTransferReceipt() {
	$("#btnSaveTR").attr("disabled", "disabled");
	setWarehouseValues();
	setCompanyId();
	$("#hdnReceiptItemsJson").val($receiptItemTable.getData());
	doPostWithCallBack ("rTRForm", "form", function(data) {
		if (data.substring(0,5) == "saved") {
			var objectId = data.split(";")[1];
			var formStatus = new Object();
			formStatus.objectId = objectId;
			updateTable (formStatus);
			if("${rTransferReceipt.id}" == 0)
				dojo.byId("form").innerHTML = "";
		} else {
			//Reload the form
			var companyId = $("#companyId option:selected").val();
			var wFromId = $("#warehouseId option:selected").val();
			var wToId = $("#selectWToId option:selected").val();
			if("${rTransferReceipt.id}" == 0) {
				dojo.byId("form").innerHTML = data;
				$("#companyId").val(companyId);
				$("#spanFormStatus").text("NEW");
			} else {
				dojo.byId("editForm").innerHTML = data;
				disableAndSetCompany();
			}
			filterWarehouses(true, true, wFromId, wToId, true, true);
		}
	});
}

function setWarehouseValues() {
	var wFromId = $("#warehouseId option:selected").val();
	var wToId = $("#selectWToId option:selected").val();
	$("#hdnWFromId").val(wFromId);
	$("#hdnWToId").val(wToId);
}

function setCompanyId() {
	var companyId = $("#companyId").val();
	$("#hdnCompanyId").val(companyId);
}

function calculateQty($tr, companyId) {
	var $qty = $($tr).find(".quantity");
	if($($qty).val() == 0 || $($qty).val() == "") {
		var refObjectId = $($tr).find(".referenceObjectId").val();
		var itemId = $($tr).find(".itemId").val();
		var warehouseId = $("#warehouseId").val();
		var bagsToWithraw = $($tr).find(".itemBagQuantity").val();
		$.ajax({
			url: contextPath + "/getAvailableBags/proportionQty?companyId="+companyId+"&itemId="+itemId+
				"&warehouseId="+warehouseId+"&refObjectId="+refObjectId+"&bagsToWithdraw="+bagsToWithraw,
			success : function(qty) {
				$($qty).val(accounting.formatMoney(qty));
			}, error : function(error) {
			},
			dataType: "text"
		});	
	}
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="rTransferReceipt" id="rTRForm">
		<div class="modFormLabel">Transfer Receipt - IS<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="trNumber"/>
			<form:hidden path="warehouseFromId" id="hdnWFromId"/>
			<form:hidden path="warehouseToId" id="hdnWToId"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="receiptItemsJson" id="hdnReceiptItemsJson"/>
			<form:hidden path="transferReceiptTypeId"/>
			<form:hidden path="ebObjectId"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">TR - IS No. </td>
							<td class="value"><span id="spanTrNumber">
								<c:if test="${rTransferReceipt.id > 0}">
									${rTransferReceipt.formattedTRNumber}</c:if></span></td>
						</tr>
						<tr>
							<td class="labels">Status </td>
							<td class="value"><span id="spanFormStatus">
								<c:if test="${rTransferReceipt.formWorkflow != null}">
									${rTransferReceipt.formWorkflow.currentFormStatus.description}</c:if></span></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Transfer Receipt Header</legend>
					<table class="formTable">
						<form:hidden path="id" />
						<tr>
							<td class="labels">* Company </td>
							<td class="value"><select class="frmSelectClass" id="companyId" onchange="filterWarehouses(true);">
									<c:forEach items="${companies}" var="company">
										<option value="${company.id}">${company.numberAndName}</option>
									</c:forEach>
								</select></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
						<tr>
							<td class="labels">* Warehouse From</td>
							<td class="value"><select class=frmSelectClass id="warehouseId">
									</select></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><span id="spanWFromIdError">
								<form:errors path="warehouseFromId" cssClass="error"/></span></td>
						</tr>
						<tr>
							<td class="labels">* To</td>
							<td class="value"><select class="frmSelectClass" id="selectWToId">
									</select></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><span id="spanWToIdError">
								<form:errors path="warehouseToId" cssClass="error"/></span></td>
						</tr>
						<tr>
							<td class="labels">* Date </td>
							<td class="value"><form:input path="trDate" style="width: 120px;" cssClass="dateClass2"/>
								<img id="txtTrDate" src="${pageContext.request.contextPath}/images/cal.gif"
										onclick="javascript:NewCssCal('trDate')" style="cursor: pointer" style="float: right;" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="trDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Delivery Receipt No.</td>
							<td class="value"><form:input path="drNumber" id="txtDrNumber" cssClass="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><span id="spanDrNumber">
								<form:errors path="drNumber" cssClass="error"/></span></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Transfer Receipt Items</legend>
					<div id="receiptItemDivTable"></div>
				</fieldset>
				<table class="frmField_set">
					<tr>
						<td><span id="trItemErrors" class="error">
								<form:errors path="trMessage" cssClass="error" /></span></td>
					</tr>
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
					<tr>
						<td colspan="3" align="right">
								<input type="button" value="Save" id="btnSaveTR" onclick="saveRTransferReceipt();"/></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
	</form:form>
</div>
</body>
</html>