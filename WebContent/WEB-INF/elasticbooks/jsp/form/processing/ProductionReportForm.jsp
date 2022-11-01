<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Production Report form.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<c:choose>
	<c:when test="${processingReport.processingReportTypeId == 6}">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.mixinghandler.js"></script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.productionhandler.js"></script>
	</c:otherwise>
</c:choose>
<style type="text/css">
.textBoxLabel {
	border: none;
}
</style>
<script type="text/javascript">
var selectCompany = "${processingReport.companyId}";
var objectId = "${processingReport.id}";

$(document).ready (function () {
	$("#hdnObjectId").val(Number(objectId));
	if ("${processingReport.formWorkflow.complete}" == "true"
			|| Number("${processingReport.formWorkflow.currentStatusId}") == 4) {
		$("#processingReportForm :input").attr("disabled","disabled");
	}
	disableCompany();
	initializeTable();
	alignRight();
});

function enableCompany() {
	//Enable  company
	$("#companyId").attr("disabled",false);
}

function disableCompany() {
	//Disable  company
	if ("${processingReport.id}" != 0) {
		$("#companyId").attr("disabled","disabled");
		$("#companyId").append("<option selected='selected' value='"+"${processingReport.companyId}"+"'>"+
				"${processingReport.company.numberAndName}"+"</option>");
	}
}

function initRMItems () {
	var rawMaterialsJson =JSON.parse($("#rawMaterialsJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$rawMaterialsTable = $("#rawMaterialsTable").editableItem({
		data: rawMaterialsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "processingReportId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "availableStocks", "varType" : "double"},
			{"name" : "origQty", "varType" : "double"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "referenceObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"},
			{"name" : "refId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"}
		],
		contextPath: cPath,
		header: [
			{"title" : "id", 
				"cls" : "id", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "cashSaleId", 
				"cls" : "cashSaleId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "processingReportId", 
				"cls" : "processingReportId", 
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
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						//Do nothing
					};
				})},
			{"title" : "Description", 
				"cls" : "description tblLabelText", 
				"editor" : "label",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "warehouseId", 
				"cls" : "warehouseId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Warehouse", 
				"cls" : "warehouse tblSelectClass", 
				"editor" : "select",
				"visible" : true ,
				"width" : "10%"},
			{"title" : "Existing <br> Stocks",
				"cls" : "availableStocks tblInputNumeric",
				"editor" : "label",
				"visible" : true, 
				"width" : "7%"},
			{"title" : "Qty", 
				"cls" : "quantity tblInputNumeric", 
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "UOM", 
				"cls" : "uom txtUOM", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "origQty", 
				"cls" : "origQty", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "referenceObjectId", 
				"cls" : "referenceObjectId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible": false},
			{"title" : "refId", 
				"cls" : "refId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId", 
				"cls" : "ebObjectId", 
				"editor" : "hidden",
				"visible" : false}
		],
		"footer" : [],
		"disableDuplicateStockCode" : false
	});

	$("#rawMaterialsTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#rawMaterialsTable").on("focusout", ".stockCode", function(){
		var warehouseId = $(this).closest("tr").find(".warehouse").val();
		var stockCode = $(this).val();
		if(stockCode != "" && warehouseId != null){
			getExistingStocks($(this), encodeURIComponent($.trim(stockCode)), warehouseId);
		}
	});

	$("#rawMaterialsTable").on("change", ".warehouse", function(){
		var stockCode = $(this).closest("tr").find(".stockCode").val();
		var warehouseId = $(this).val();
		getExistingStocks($(this), encodeURIComponent($.trim(stockCode)), warehouseId);
	});
}

function alignRight() {
	$("#rawMaterialsTable").find("table tbody tr").each(function() {
		var $availableStocks = $(this).closest("tr").find(".availableStocks");
		$($availableStocks).closest("tr").find("td").eq(5).css("text-align", "right");
	});
}

function initMainProducts() {
	var prMainProductJson =JSON.parse($("#prMainProductJson").val());
	var cPath = "${pageContext.request.contextPath}";

	$mainProductTable = $("#mainProductTable").editableItem({
		data: prMainProductJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "processingReportId", "varType" : "int"},
			{"name" : "warehouseId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "origQty", "varType" : "double"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "referenceObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"}
		],
		contextPath: cPath,
		header: [
			{"title" : "id", 
				"cls" : "id", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "cashSaleId", 
				"cls" : "cashSaleId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "processingReportId", 
				"cls" : "processingReportId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code", 
				"cls" : "stockCode tblInputText", 
				"editor" : "text",
				"visible" : true,
				"width" : "13%", 
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						//Do nothing
					};
				})},
			{"title" : "Description", 
				"cls" : "description tblLabelText", 
				"editor" : "label",
				"visible" : true, 
				"width" : "15%"},
			{"title" : "warehouseId", 
				"cls" : "warehouseId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Warehouse", 
				"cls" : "warehouse tblSelectClass", 
				"editor" : "select",
				"visible" : true ,
				"width" : "10%"},
			{"title" : "Qty", 
				"cls" : "quantity tblInputNumeric", 
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "UOM", 
				"cls" : "uom txtUOM", 
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "origQty", 
				"cls" : "origQty", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "unitCost", 
				"cls" : "unitCost", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId", 
				"cls" : "ebObjectId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "referenceObjectId", 
				"cls" : "referenceObjectId", 
				"editor" : "hidden",
				"visible" : false},
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible": false}
		],
		"footer" : [],
		"disableDuplicateStockCode" : false
	});

	$("#mainProductTable").on("focus", ".tblInputNumeric", function(){
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});
}

function initializeTable() {
	initRMItems();
	initMainProducts();
}

function updateTotal (totalAmount) {
	$("#totalAmount").val(accounting.unformat(totalAmount));
}

var isSaving = false;
function saveProcessingReport() {
	if(isSaving == false) {
		isSaving = true;
		enableCompany();
		$("#rawMaterialsJson").val($rawMaterialsTable.getData());
		$("#prMainProductJson").val($mainProductTable.getData());
		doPostWithCallBack("processingReportForm", "form", function (data) {
			var refNo = $("#refNumber").val();
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				if("${processingReport.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					$("#companyId").val(selectCompany);
					disableCompany();
				}
				$("#refNumber").val(refNo);
				initializeTable();
				alignRight();
				isSaving = false;
			}
			$("#btnSaveProcessReport").removeAttr("disabled");
		});
	}
}

function handleCompanyOnChange() {
	$rawMaterialsTable.emptyTable();
	$mainProductTable.emptyTable();
}
</script>
</head>
<body>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="processingReport" id="processingReportForm">
			<div class="modFormLabel">
				${processingType}
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="id" id="hdnFormId"/>
			<input type="hidden" value="2" id="hdnOrTypeId">
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNo"/>
			<form:hidden path="processingReportTypeId"/>
			<form:hidden path="rawMaterialsJson" id="rawMaterialsJson"/>
			<form:hidden path="prMainProductJson" id="prMainProductJson"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence No.</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${processingReport.sequenceNo}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<c:set var="status" value="${processingReport.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtCashSalesStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass" onchange="handleCompanyOnChange();"
									items="${companies}" itemLabel="numberAndName" itemValue="id" >
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="companyId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="date" id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('date')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="date" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Ref No.</td>
							<td class="value"><form:input path="refNumber" id="refNumber" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="refNumber" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">Remarks</td>
							<td class="value"><form:textarea path="remarks" id="remarks" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="remarks" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
					</table>
				</fieldset>
				<c:if test="${processingReport.processingReportTypeId ne 6}">
					<fieldset class="frmField_set">
						<legend>Main Recipe</legend>
						<div id="rawMaterialsTable"></div>
						<table>
							<tr>
								<td colspan="12">
									<span id="rmMessage" class="error"></span>
								</td>
							</tr>
							<tr>
								<td colspan="12">
									<form:errors path="errRMItems" cssClass="error"/>
								</td>
							</tr>
						</table>
					</fieldset>
				</c:if>
				<!-- Main Product -->
				<fieldset class="frmField_set">
				<legend>
					<c:choose>
						<c:when test="${processingReport.processingReportTypeId eq 6}">
							Main Recipe
						</c:when>
						<c:otherwise>
							Main Product 
						</c:otherwise>
					</c:choose>
				</legend>
				<div id="mainProductTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="mpMessage" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errMainProduct" cssClass="error"/>
						</td>
					</tr>
				</table>
				<c:if test="${processingReport.processingReportTypeId eq 6}">
					<table>
						<tr>
							<td colspan="12">
								<span id="rmMessage" class="error"></span>
							</td>
						</tr>
						<tr>
							<td colspan="12">
								<form:errors path="errRMItems" cssClass="error"/>
							</td>
						</tr>
					</table>
				</c:if>
				</fieldset>
				<!-- Grand Total -->
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSaveProcessReport" value="Save" onclick="saveProcessingReport();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>