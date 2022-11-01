<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Stock Adjustment form for Individual Selection
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	if("${stockAdjustmentIS.id}" == 0) {
		filterWarehouses(null, false);
		filterStockAdjustmentTypes();
	} else {
		disableAndSetCompany();
		filterWarehouses("${stockAdjustmentIS.warehouseId}", true);
		filterStockAdjustmentTypes("${stockAdjustmentIS.stockAdjustmentTypeId}");

		if("${stockAdjustmentIS.formWorkflow.complete}" == "true"  || "${stockAdjustmentIS.formWorkflow.currentStatusId}" == 4) {
			$("#btnSaveStockAdjustment").attr("disabled", "disabled");
		}
	}
});

function filterWarehouses(warehouseId, isReloadItems) {
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

	initializeTable();
	computeTotal();
	if(isReloadItems) {
		//Load only items when isReloadItems is set to true
		reloadRItems();
	}
}

function filterStockAdjustmentTypes(stockAdjustmentTypeId) {
	var companyId = $("#companyId").val();
	if(companyId > 0) {
		var uri = contextPath+"/getSAdjustmentTypes?companyId="+companyId;
		$("#stockAdjustmentTypeId").empty();
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
					if(warehouseId != "")
						$("#stockAdjustmentTypeId").val(stockAdjustmentTypeId);
				}
		};

		loadPopulate (uri, false, stockAdjustmentTypeId, "stockAdjustmentTypeId", optionParser, postHandler);
	}
}

function initializeTable() {
	$adjustmentItemTable = $("#adjustmentItemTable").html("");
	var adjustmentItemsJson = JSON.parse($("#adjustmentItemsJson").val());
	var typeId = "${typeId}";
	var cPath = "${pageContext.request.contextPath}";
	$adjustmentItemTable = $("#adjustmentItemTable").editableItem({
		data: adjustmentItemsJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "stockAdjustmentId", "varType" : "int"},
				{"name" : "referenceObjectId", "varType" : "int"},
				{"name" : "origRefObjectId", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "typeId", "varType" : "int"},
				{"name" : "itemId", "varType" : "int"},
				{"name" : "itemBagQuantity", "varType" : "double"},
				{"name" : "quantity", "varType" : "double"},
				{"name" : "unitCost", "varType" : "double"},
				{"name" : "stockCodeIs", "varType" : "string"},
				{"name" : "origQty", "varType" : "double"}
			],
		contextPath: cPath,
		"header": getTblHeaders(typeId),
		"disableDuplicateStockCode" : false,
		"itemTableMessage" : ""
	});

	$("#adjustmentItemTable").on("keydown keyup", ".stockCodeIs", function(){
		loadItems($(this));
	});

	$("#adjustmentItemTable").on("blur", ".stockCodeIs", function(){
		getRItem($(this), $($(this)).val() , false);
	});

	$("#adjustmentItemTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#adjustmentItemTable").on("change", ".slctAvailBags", function(){
		setReferenceDetails($(this));
	});

	$("#adjustmentItemTable").on("blur", ".tblInputNumeric", function(){
		computeTotal();
	});

	$("#adjustmentItemTable").on("blur", ".itemBagQuantity", function(){
		var $tr = $(this).closest("tr");
		calculateQty($tr, $("#companyId").val());
	});
}

function getTblHeaders(typeId) {
	var header = null;
	if(typeId == 3) {
		header = [{"title" : "id",
			"cls" : "id",
			"editor" : "hidden",
			"visible": false},
		{"title" : "stockAdjustmentId",
			"cls" : "stockAdjustmentId",
			"editor" : "hidden",
			"visible": false},
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
		{"title" : "typeId",
			"cls" : "typeId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "itemId",
			"cls" : "itemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "Stock Code",
			"cls" : "stockCodeIs tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "12%"},
		{"title" : "Description",
			"cls" : "description tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "19%"},
		{"title" : "Existing <br> Stocks", 
			"cls" : "existingStock tblLabelNumeric",
			"editor" : "label",
			"visible" : true,
			"width" : "10%"},
		{"title" : "UOM",
			"cls" : "uom spanUom tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
		{"title" : "Bags",
			"cls" : "itemBagQuantity tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "12%"},
		{"title" : "Adjustment <br> Qty",
			"cls" : "quantity tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "12%"},
		 {"title" : "origQty",
			"cls" : "origQty",
			"editor" : "hidden",
			"visible": false},
		{"title" : "Unit Cost",
			"cls" : "unitCost tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "14%"},
		{"title" : "Total",
			"cls" : "amount tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "12%"}
		];
	} else {
		header = [{"title" : "id",
			"cls" : "id",
			"editor" : "hidden",
			"visible": false},
		{"title" : "stockAdjustmentId",
			"cls" : "stockAdjustmentId",
			"editor" : "hidden",
			"visible": false},
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
		{"title" : "typeId",
			"cls" : "typeId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "unitCost",
			"cls" : "unitCost",
			"editor" : "hidden",
			"visible": false},
		{"title" : "itemId",
			"cls" : "itemId",
			"editor" : "hidden",
			"visible": false},
		{"title" : "Stock Code",
			"cls" : "stockCodeIs tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "16%"},
		{"title" : "Description",
			"cls" : "description tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "20%"},
		{"title" : "Available <br> Bags/Stocks", 
			"cls" : "slctAvailBags tblSelectClass",
			"editor" : "select",
			"visible" : true,
			"width" : "16%"},
		{"title" : "UOM",
			"cls" : "uom spanUom tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "16%"},
		{"title" : "Bags",
			"cls" : "itemBagQuantity tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "16%"},
		{"title" : "Adjustment <br> Qty",
			"cls" : "quantity tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "16%"},
		 {"title" : "origQty",
			"cls" : "origQty",
			"editor" : "hidden",
			"visible": false}
		];
	}
	return header;
}

var currentItem = null;
function loadItems($txtBox) {
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	if(warehouseId != undefined) {
		var stockCode = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath;
		if("${typeId}" == 4) {
			uri += "/getRItems/filter?companyId="+companyId
					+"&warehouseId="+warehouseId+"&stockCode="+stockCode;
		} else {
			uri += "/getBuyingDetails/items?companyId="+companyId
					+"&stockCode="+stockCode;
		}
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				currentItem = ui.item;
				$(this).val (ui.item.stockCode);
				setValues($txtBox, currentItem, warehouseId);
				return false;
			}, minLength: 2,
			change: function(event, ui) {
				stockCode = $(this).val();
				if (currentItem != null && stockCode == currentItem.stockCode){
					return false;
				}

				if(stockCode != "") {
					getRItem($txtBox, stockCode, false);
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

function setValues($txtBox, item, warehouseId) {
	//Set the type of Stock Adjustment. 3 if IN, 4 if OUT.
	$($txtBox).closest("tr").find(".typeId").val("${typeId}");
	$($txtBox).closest("tr").find(".itemId").val(item.id);
	$($txtBox).closest("tr").find(".description").text(item.description);
	$($txtBox).closest("tr").find(".uom").text(item.unitMeasurement.name);

	if("${typeId}" == 4) {
		var $ebObject = $($txtBox).closest("tr").find(".ebObjectId");
		var $refObj = $($txtBox).closest("tr").find(".referenceObjectId");
		var $orig = $($txtBox).closest("tr").find(".origRefObjectId");
		if("${stockAdjustmentIS.id}" > 0 && $($ebObject).val() != "" && $($orig).val() == "") {
			$.ajax({
				url: contextPath + "/getRefObject/id?ebObjectId="+$($ebObject).val()+"&orTypeId=3",
				success : function(refId) {
					$refObj.val(refId);
					getAvailableBags($txtBox, warehouseId);
				},
				dataType: "json"
			});
		} else {
			//Populate only if Stock Adjustment OUT
			getAvailableBags($txtBox, warehouseId, $refObj.val());
		}
	} else {
		var $existingStocks = $($txtBox).closest("tr").find(".existingStock");
		var formatES = accounting.formatMoney(item.existingStocks);
		$existingStocks.html(formatES);
	}
}

function setReferenceDetails($txtBox) {
	var $availableStock = $($txtBox).closest("tr").find(".slctAvailBags");
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

function reloadRItems() {
	$("#adjustmentItemTable").find(" table tbody tr").each(function(row) {
		var $txtBox = $(this);
		var $stockCodeIs = $($txtBox).find(".stockCodeIs");
		getRItem($txtBox, $($stockCodeIs).val() , true);
	});
}

function getRItem($txtBox, stockCodeIs, isReloadItem) {
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	var uri = contextPath;
	if("${typeId}" == 4) {
		uri += "/getItem/withInactive?stockCode="+encodeURIComponent(stockCodeIs)
				+"&companyId="+companyId;
	} else {
		uri += "/getBuyingDetails/item?companyId="+companyId
				+"&warehouseId="+warehouseId+"&stockCode="+stockCodeIs;
	}
	if(stockCodeIs != "" && warehouseId != "") {
		$.ajax({
			url: uri,
			success : function(item) {
				setValues($txtBox, item, warehouseId);
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

function computeTotal() {
	$("#adjustmentItemTable tbody tr").each(function(row) {
		var qty = accounting.unformat($(this).closest("tr").find(".quantity").val());
		var uCost = accounting.unformat($(this).closest("tr").find(".unitCost").val());
		var $total = $(this).find(".amount");
		$total.text(accounting.formatMoney(qty * uCost));
	});
}


function companyIdOnChange() {
	$adjustmentItemTable = $("#adjustmentItemTable").html("");
	filterWarehouses();
	filterStockAdjustmentTypes();
}

var isSaving = false;
function saveStockAdjustment() {
	if(isSaving == false) {
		isSaving = true;
		$("#adjustmentItemsJson").val($adjustmentItemTable.getData());
		enableCompany();
		$("#btnSaveStockAdjustment").attr("disabled", "disabled");
		doPostWithCallBack ("stockAdjustmentIsForm", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var warehouseId = $("#warehouseId").val();
				var stockAdjustmentTypeId = $("#stockAdjustmentTypeId").val();
				if("${stockAdjustmentIS.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#spanFormStatus").text("NEW");
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableAndSetCompany();
				}
				filterWarehouses(warehouseId, true);
				filterStockAdjustmentTypes(stockAdjustmentTypeId, true);
				isSaving = false;
			}
		});
		$("#btnSaveStockAdjustment").removeAttr("disabled");
	}
}

function enableCompany() {
	//Enable  company
	$("#companyId").attr("disabled",false);
}

function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${stockAdjustmentIS.companyId}"+"'>"+
			"${stockAdjustmentIS.company.numberAndName}"+"</option>");
}

function getAvailableBags($txtBox, warehouseId, refObjectId) {
	var $availableBag = $($txtBox).closest("tr").find(".slctAvailBags");
	var itemId = parseInt($($txtBox).closest("tr").find(".itemId").val());
	var ebObjectId = $($txtBox).closest("tr").find(".ebObjectId").val();
	var companyId = $("#companyId").val();

	if (typeof $availableBag != "undefined") {
		$.ajax({
			url: contextPath + "/getAvailableBags?companyId="+companyId+"&itemId="+itemId
					+"&warehouseId="+warehouseId+"&ebObjectId="+ebObjectId,
			success : function(availableBags) {
				$availableBag.empty();
				for ( var index = 0; index < availableBags.length; index++) {
					var availableBag = availableBags[index];
					option = "<option ";
					if(refObjectId != null) {
						console.log(refObjectId);
						if(refObjectId == availableBag.sourceObjId) {
							option += " selected='selected'";
						}
					}
					option += " id='" + availableBag.sourceObjId + "'";
					option += " value='" + availableBag.unitCost + "'>";
					var label = availableBag.source + " - " + availableBag.totalBags + " - " + availableBag.totalStocks;
					option += label + "</option>";
					$($availableBag).append(option);
				}
				setReferenceDetails($txtBox);
			},error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
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
	<form:form method="POST" commandName="stockAdjustmentIS" id="stockAdjustmentIsForm">
		<div class="modFormLabel">Stock Adjustment
			<c:choose>
				<c:when test="${typeId == 3}"> IN</c:when>
				<c:otherwise> OUT</c:otherwise>
			</c:choose> - IS
			<span class="btnClose" id="btnClose">[X]</span>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="saNumber"/>
		<form:hidden path="typeId"/>
		<form:hidden path="adjustmentItemsJson" id="adjustmentItemsJson"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">SA
						<c:choose>
							<c:when test="${typeId == 3}"> I</c:when>
							<c:otherwise> O</c:otherwise>
						</c:choose> - IS No.</td>
						<td class="value"><span id="spanSaNumber">
							<c:if test="${stockAdjustmentIS.id > 0}">
									${stockAdjustmentIS.formattedSANumber}</c:if></span></td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:choose>
								<c:when test="${stockAdjustmentIS.formWorkflow ne null}">
									<span id="spanFormStatus">${stockAdjustmentIS.formWorkflow.currentFormStatus.description}</span>
								</c:when>
								<c:otherwise>
									<span id="spanFormStatus">NEW</span>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Stock Adjustment Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Company</td>
						<td class="value">
							<form:select path="companyId" id="companyId" cssClass="frmSelectClass" onchange="companyIdOnChange();"
								items="${companies}" itemLabel="numberAndName" itemValue="id" >
							</form:select>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="companyId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Warehouse</td>
						<td class="value">
							<form:select path="warehouseId" class="frmSelectClass">
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="warehouseId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Adjustment Type</td>
						<td class="value">
							<form:select path="stockAdjustmentTypeId" id="stockAdjustmentTypeId" class="frmSelectClass">
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="stockAdjustmentTypeId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Date </td>
						<td class="value"><form:input path="saDate" style="width: 120px;" cssClass="dateClass2"/>
								<img id="saDate" src="${pageContext.request.contextPath}/images/cal.gif"
										onclick="javascript:NewCssCal('saDate')" style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="saDate" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels" align="right">Remarks</td>
						<td  class="value" colspan="2"><form:textarea path="remarks" rows="3"
									style="width: 350px; resize: none;" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="remarks" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Stock Adjustment Table</legend>
				<div id="adjustmentItemTable"></div>
			</fieldset>
			<table class="frmField_set">
				<tr>
					<td><span id="saItemErrors" class="error">
						<form:errors path="saMessage" cssClass="error"/></span></td>
				</tr>
				<tr>
					<td align="right"><input type="button" id="btnSaveStockAdjustment"
							value="Save" onclick="saveStockAdjustment();" /></td>
				</tr>
			</table>
		</div>
		<hr class="thin" />
	</form:form>
</div>
</body>
</html>