<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : Repacking - item conversion form jsp -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.unitcosthandler.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
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
var $rawMaterialTable = null;
var $finishedGoodTable = null;
$(document).ready (function () {
	initializeRmTable();
	initializeFgTable();
	if ("${repacking.id}" > 0) {
		disableSelectFields();
	}
	filterWarehouses("${repacking.warehouseId}");
	disableAllInputFields();
});

function disableAllInputFields() {
	if ("${repacking.formWorkflow.complete}" == "true" || "${repacking.formWorkflow.currentStatusId}" == 4) {
		$("#itemConvertionForm :input").attr("disabled","disabled");
	}
}

function initializeRmTable() {
	$("#divRawMaterialTblId").html("");
	var rawMaterialJson = JSON.parse($("#hdnRawMaterialJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$rawMaterialTable = $("#divRawMaterialTblId").editableItem({
		data : rawMaterialJson,
		jsonProperties : [
				{"name" : "id", "varType" : "int"},
				{"name" : "repackingId", "varType" : "int"},
				{"name" : "itemId", "varType" : "int"},
				{"name" : "stockCode", "varType" : "string"},
				{"name" : "quantity", "varType" : "double"},
				{"name" : "existingStocks", "varType" : "double"},
				{"name" : "origQty", "varType" : "double"}
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
				{"title" : "itemId",
					"cls" : "itemId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "Stock Code",
					"cls" : "stockCode tblInputText",
					"editor" : "text",
					"visible": true,
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
					"width" : "8%" }
				],
		disableDuplicateStockCode : true,
		"itemTableMessage" : "repackingItemErrors"
	});
}

function initializeFgTable() {
	$("#divFinishedGoodTblId").html("");
	var finishedGoodJson = JSON.parse($("#hdnFinishedGoodJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$finishedGoodTable = $("#divFinishedGoodTblId").editableItem({
		data : finishedGoodJson,
		jsonProperties : [
				{"name" : "id", "varType" : "int"},
				{"name" : "repackingId", "varType" : "int"},
				{"name" : "itemId", "varType" : "int"},
				{"name" : "stockCode", "varType" : "string"},
				{"name" : "quantity", "varType" : "double"},
				{"name" : "existingStocks", "varType" : "double"},
				{"name" : "origQty", "varType" : "double"}
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
				{"title" : "itemId",
					"cls" : "itemId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "Stock Code",
					"cls" : "stockCode tblInputText",
					"editor" : "text",
					"visible": true,
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
					"width" : "8%" }
				],
		disableDuplicateStockCode : true,
		"itemTableMessage" : "repackingItemErrors"
	});
}

function slctCompanyOnChange() {
	$repackingItemsTable.emptyTable();
	filterWarehouses();
}

function filterWarehouses(warehouseId) {
	var companyId = $("#companyId").val();
	if (companyId > 0) {
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
					if (warehouseId != "" && typeof warehouseId != "undefined") {
						$("#warehouseId").val(warehouseId);
					}
				}
		};
		loadPopulate (uri, false, null, "warehouseId", optionParser, postHandler);
	}
}

var isSaving = false;
function saveRepacking() {
	if (!isSaving) {
		isSaving = true;
		enableSelectFields();
		$("#btnSaveRepacking").attr("disabled", "disabled");
		$("#hdnRawMaterialJson").val($rawMaterialTable.getData());
		$("#hdnFinishedGoodJson").val($finishedGoodTable.getData());
		doPostWithCallBack ("itemConvertionForm", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if ("${repacking.id}" == 0) {
					dojo.byId("form").innerHTML = "";
				}
			} else {
				var formCurrentStatus = $("#txtIcFormStatus").val();
				var warehouseId = $("#warehouseId").val();
				if ("${repacking.id}" == 0) {
					var companyId = $("#companyId option:selected").val();
					dojo.byId("form").innerHTML = data;
					$("#companyId").val(companyId);
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableSelectFields();
				}
				$("#txtIcFormStatus").val(formCurrentStatus);
				filterWarehouses(warehouseId);
				initializeRmTable();
				initializeFgTable();
			}
			isSaving = false;
			$("#btnSaveRepacking").removeAttr("disabled");
		});
	}
}

function disableSelectFields() {
	$("#companyId").attr("disabled","disabled");
	$("#warehouseId").attr("disabled","disabled");
}

function enableSelectFields() {
	$("#companyId").removeAttr("disabled");
	$("#warehouseId").removeAttr("disabled");
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="repacking" id="itemConvertionForm">
		<div class="modFormLabel">Item Conversion<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id" id="hdnFormId"/>
		<form:hidden path="repackingTypeId"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="rNumber"/>
		<form:hidden path="rpRawMaterialJson" id="hdnRawMaterialJson"/>
		<form:hidden path="rpFinishedGoodJson" id="hdnFinishedGoodJson"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">IC No.</td>
						<td class="value"><span id="spanRNumber">
							<c:if test="${repacking.id > 0}">
								${repacking.formattedRNumber}</c:if></span></td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:set var="status" value="${repacking.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<input type="text" id="txtIcFormStatus" class="textBoxLabel"
									readonly="readonly" value='${status}'/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Item Conversion Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* Company</td>
						<td class="value">
							<form:select path="companyId" id="companyId" cssClass="frmSelectClass"
								onchange="slctCompanyOnChange();" items="${companies}"
								itemLabel="numberAndName" itemValue="id">
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="companyId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Warehouse</td>
						<td class="value">
							<form:select path="warehouseId" id="warehouseId" cssClass="frmSelectClass"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="warehouseId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Date</td>
						<td class="value">
							<form:input path="rDate" style="width: 120px;" cssClass="dateClass2" id="date" onblur="evalDate('date')"/>
							<img id="rDate" src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('date')" style="cursor: pointer" style="float: right;"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="rDate" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels" align="right">Remarks</td>
						<td class="value">
							<form:textarea path="remarks" class="input"/></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="remarks" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Raw Material Table</legend>
				<div id="divRawMaterialTblId"></div>
				<table>
					<tr>
						<td>
							<form:errors path="repackingRawMaterials" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Finished Good Table</legend>
				<div id="divFinishedGoodTblId"></div>
				<table>
					<tr>
						<td>
							<form:errors path="repackingFinishedGoods" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<table class="frmField_set">
				<tr>
					<td>
						<span id="repackingItemErrors" class="error"></span>
					</td>
				</tr>
				<tr>
					<td>
						<form:errors path="repackingMessage" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td><form:errors path="formWorkflowId" cssClass="error"/></td>
				</tr>
				<tr>
					<td align="right">
						<input type="button" value="Save" id="btnSaveRepacking" onclick="saveRepacking();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>