<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--


	Description: Stock Adjustment form for Inventory Retail.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.unitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<style type="text/css">
.spanAdjustedQty {
	float: right;
	width: 100%;
	text-align: right;
}
</style>
<script type="text/javascript">
var $adjustmentItemTable = null;
$(document).ready(function() {
	if("${stockAdjustment.id}" == 0) {
		filterWarehouses();
		filterStockAdjustmentTypes();
		initializeTable();
	} else {
		//Disable and set values
		disableAndSetCompany();
		filterWarehouses("${stockAdjustment.warehouseId}", true);
		if("${stockAdjustment.formWorkflow.complete}" == "true") {
			$("#stockAdjustmentForm :input").attr("disabled", true);
			$("#adjustmentItemTable").find("table tbody tr .delrow").attr("onclick", "");
			$("#warehouseId").append("<option selected='selected' value="+"${stockAdjustment.warehouseId}"
					+">"+"${stockAdjustment.warehouse.name}"+"</option>");
		}
		filterStockAdjustmentTypes("${stockAdjustment.stockAdjustmentTypeId}", true);
		if("${stockAdjustment.formWorkflow.complete}" == "true" || "${stockAdjustment.formWorkflow.currentStatusId}" == 4) {
			$("#stockAdjustmentForm :input").attr("disabled", true);
			$("#adjustmentItemTable").find("table tbody tr .delrow").attr("onclick", "");
		}
	}

	if("${stockAdjustment.id}" > 0)
		computeTotal();
});

function getTblColumns(typeId) {
	var header = null;
	if(typeId == 1) {
		header = [{"title" : "id",
			"cls" : "id",
			"editor" : "hidden",
			"visible": false},
	    {"title" : "stockAdjustmentId",
			"cls" : "stockAdjustmentId",
			"editor" : "hidden",
			"visible": false},
	    {"title" : "itemId",
			"cls" : "itemId",
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
	    {"title" : "Stock Code",
			"cls" : "stockCode tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "12%",
			"handler" : new UnitCostTableHandler (new function () {
				this.handleTotal = function (total) {
					// Do nothing
				};
			})},
	    {"title" : "Description",
			"cls" : "description tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "19%"},
	    {"title" : "Existing <br> Stocks",
			"cls" : "existingStock tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
	    {"title" : "UOM",
			"cls" : "uom spanUom tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
	    {"title" : "Adjustment <br> Qty",
			"cls" : "quantity tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "12%"},
	    {"title" : "Gross Price",
			"cls" : "unitCost tblInputNumeric hdnColumn",
			"editor" : "text",
			"visible": true,
			"width" : "14%"},
	    {"title" : "Total",
			"cls" : "amount tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "12%"},]
	} else {
		header = [{"title" : "id",
			"cls" : "id",
			"editor" : "hidden",
			"visible": false},
	    {"title" : "stockAdjustmentId",
			"cls" : "stockAdjustmentId",
			"editor" : "hidden",
			"visible": false},
	    {"title" : "itemId",
			"cls" : "itemId",
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
	    {"title" : "Stock Code",
			"cls" : "stockCode tblInputText",
			"editor" : "text",
			"visible": true,
			"width" : "12%",
			"handler" : new UnitCostTableHandler (new function () {
				this.handleTotal = function (total) {
					// Do nothing
				};
			})},
	    {"title" : "Description",
			"cls" : "description tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "19%"},
	    {"title" : "Existing <br> Stocks",
			"cls" : "existingStock tblLabelNumeric",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
	    {"title" : "UOM",
			"cls" : "uom spanUom tblLabelText",
			"editor" : "label",
			"visible": true,
			"width" : "8%"},
	    {"title" : "Adjustment <br> Qty",
			"cls" : "quantity tblInputNumeric",
			"editor" : "text",
			"visible": true,
			"width" : "12%"},
		 {"title" : "origQty",
				"cls" : "origQty",
				"editor" : "hidden",
				"visible": false}]
	}
	return header;
}

function initializeTable() {
	var header = getTblColumns("${typeId}");
	var adjustmentItemsJson = JSON.parse($("#adjustmentItemsJson").val());
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
                 {"name" : "quantity", "varType" : "int"},
                 {"name" : "unitCost", "varType" : "double"},
                 {"name" : "stockCode", "varType" : "string"},
                 {"name" : "origQty", "varType" : "double"}],
		contextPath: cPath,
		"header": header,
		"disableDuplicateStockCode" : false,
		"itemTableMessage" : ""
	});

	$("#adjustmentItemTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#adjustmentItemTable").on("blur", ".unitCost", function() {
		$(this).val(formatDecimalPlaces($(this).val(), 4));
	});
}

function companyIdOnChange () {
	$adjustmentItemTable.emptyTable();
	filterWarehouses();
	filterStockAdjustmentTypes();
}

function filterWarehouses(warehouseId, initializeDatatable) {
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
					if(warehouseId != "")
						$("#warehouseId").val(warehouseId);
				}
		};
		loadPopulate (uri, false, null, "warehouseId", optionParser, postHandler);
	}

	if(initializeDatatable) {
		initializeTable();
		computeTotal();
	}
}

function filterStockAdjustmentTypes(stockAdjustmentTypeId, initializeDatatable) {
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

		loadPopulate (uri, false, null, "stockAdjustmentTypeId", optionParser, postHandler);
	}
}

var isSaving = false;
function saveStockAdjustment() {
	if(isSaving == false) {
		isSaving = true;
		$("#adjustmentItemsJson").val($adjustmentItemTable.getData());
		enableCompanyAndWarehouseId();
		$("#btnSaveStockAdjustment").attr("disabled", "disabled");
		doPostWithCallBack ("stockAdjustmentForm", "form", function(data) {
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
				if("${stockAdjustment.id}" == 0) {
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

function computeTotal() {
	$("#adjustmentItemTable tbody tr").each(function(row) {
		var qty = accounting.unformat($(this).closest("tr").find(".quantity").val());
		var uCost = accounting.unformat($(this).closest("tr").find(".unitCost").val());
		var $total = $(this).find(".amount");
		$total.text(formatDecimalPlaces(qty * uCost));
	});
}

function enableCompanyAndWarehouseId() {
	//Disable  company and warehouse
	$("#companyId").attr("disabled", false);
	$("#warehouseId").attr("disabled", false);
}

function disableAndSetCompany() {
	//Disable  company and warehouse
	$("#companyId").attr("disabled","disabled");
	if ("${typeId}" != 1) {
		$("#warehouseId").attr("disabled","disabled");
	}
}

</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="stockAdjustment" id="stockAdjustmentForm">
		<div class="modFormLabel">Stock Adjustment 
			<c:choose>
				<c:when test="${typeId == 1}"> - IN</c:when>
				<c:otherwise> - OUT</c:otherwise>
			</c:choose>
			<span class="btnClose" id="btnClose">[X]</span>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="saNumber"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="adjustmentItemsJson" id="adjustmentItemsJson"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">SA
							<c:choose>
								<c:when test="${typeId == 1}">I</c:when>
								<c:otherwise>O</c:otherwise>
							</c:choose>No.</td>
						<td class="value"><span id="spanSaNumber">
							<c:if test="${stockAdjustment.id > 0}">
									${stockAdjustment.formattedSANumber}</c:if></span></td>
					</tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:choose>
								<c:when test="${stockAdjustment.formWorkflow ne null}">
									<span id="spanFormStatus">${stockAdjustment.formWorkflow.currentFormStatus.description}</span>
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
						<td class="value"><form:input path="saDate" style="width: 120px;"
								cssClass="dateClass2" id="date" onblur="evalDate('date')"/>
								<img id="saDate" src="${pageContext.request.contextPath}/images/cal.gif"
										onclick="javascript:NewCssCal('date')" style="cursor: pointer" style="float: right;" />
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
					<td><form:errors path="formWorkflowId" cssClass="error"/></td>
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