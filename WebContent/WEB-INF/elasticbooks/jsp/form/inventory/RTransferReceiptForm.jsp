<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description:Transfer Receipt form for Inventory Retail.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.unitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	if("${rTransferReceipt.id}" == 0) {
		$("#spanFormStatus").text("NEW");
		filterWarehouses(true);
		initializeTable();
	} else {
		disableAndSetCompany();
		//Disable all input and delete image
		if("${rTransferReceipt.formWorkflow.complete}" == "true" || "${rTransferReceipt.formWorkflow.currentStatusId}" == 4) {
			$("#rTRForm :input").attr("disabled","disabled");
			$("#trItemTable tbody tr").find(".imgDelete").attr("onclick", "");
		}
		//Select warehouses
		var wFromId = "${rTransferReceipt.warehouseFromId}";
		var wToId = "${rTransferReceipt.warehouseToId}";
		filterWarehouses(true, true, wFromId, wToId , true);
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
				{"name" : "quantity", "varType" : "double"},
				{"name" : "stockCode", "varType" : "string"},
				{"name" : "origQty", "varType" : "double"}],
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
				 {"title" : "Stock Code",
					"cls" : "stockCode tblInputText",
					"editor" : "text",
					"visible": true,
					"width" : "20%",
					"handler" : new UnitCostTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Description",
					"cls" : "description tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "30%"},
			    {"title" : "Existing <br> Stocks",
					"cls" : "existingStock tblLabelNumeric",
					"editor" : "label",
					"visible": true,
					"width" : "10%"},
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
}

function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${rTransferReceipt.companyId}"+"'>"+
			"${rTransferReceipt.company.numberAndName}"+"</option>");
	$("#warehouseId").attr("disabled","disabled");
}

function filterWarehouses(isFrom, isSetWarehouse, wFromId, wToId, isInitializeTbl) {
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
						filterWarehouses(false, isSetWarehouse, wFromId, wToId, isInitializeTbl);
						if(wFromId != null)
							$("#warehouseId").val(wFromId);
					} else {
						if(isSetWarehouse) {
							//Set the selected warehouse to after an error validation
							$("#selectWToId").val(wToId);
						}
						//Initialize table after the warehouse is loaded.
						if(isInitializeTbl)
							initializeTable();
					}
				}
		};
		loadPopulate (uri, false, null, selectId, optionParser, postHandler);
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
			filterWarehouses(true, true, wFromId, wToId, true);
		}
	});
}

function setWarehouseValues() {
	var wToId = $("#selectWToId option:selected").val();
	$("#hdnWToId").val(wToId);
	var isNew = $("#hdnFormId").val() > 0 ? false : true;
	if(isNew) {
		var wFromId = $("#warehouseId option:selected").val();
		$("#hdnWFromId").val(wFromId);
	}
}

function setCompanyId() {
	var companyId = $("#companyId").val();
	$("#hdnCompanyId").val(companyId);
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="rTransferReceipt" id="rTRForm">
		<div class="modFormLabel">Transfer Receipt <span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="id" id="hdnFormId"/>
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
							<td class="labels">TR No. </td>
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
							<td class="value"><form:input path="trDate" style="width: 120px;"
									cssClass="dateClass2" id="date" onblur="evalDate('date')"/>
								<img id="txtTrDate" src="${pageContext.request.contextPath}/images/cal.gif"
										onclick="javascript:NewCssCal('date')" style="cursor: pointer" style="float: right;" />
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