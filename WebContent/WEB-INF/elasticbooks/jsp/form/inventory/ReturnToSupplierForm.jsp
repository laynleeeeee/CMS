<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Return to Supplier jsp form page
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.unitcosthandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<style type="text/css">
.disableSelect{
	pointer-events: none;
	cursor: default;
}
</style>
<script type="text/javascript">
var selectedCompanyId = -1;
var rtsReferenceWindow = null;
var $returnToSupplierTable = null;
$(document).ready(function () {
	var wtAcctSettingId = "";
	if ("${apInvoice.id}" != 0) {
		disableFields();
		wtAcctSettingId = "${apInvoice.wtAcctSettingId}";
		var invDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${apInvoice.invoiceDate}'/>";
		updateHeader("${apInvoice.returnToSupplier.apInvoiceRefId}",
				"${apInvoice.returnToSupplier.companyId}",
				"${apInvoice.returnToSupplier.warehouseId}",
				"${apInvoice.returnToSupplier.company.name}",
				"${apInvoice.returnToSupplier.warehouse.name}",
				"${apInvoice.returnToSupplier.rrNumber}",
				"${apInvoice.supplier.name}", "${apInvoice.supplierAccount.name}",
				"${apInvoice.invoiceNumber}", invDate);
		initializeTable();
		updateExistingStocks();
		// if form workflow status = COMPLETED and CANCELLED
		var status = "${apInvoice.formWorkflow.complete}";
		if (status == "true" || "${apInvoice.formWorkflow.currentStatusId}" == 4) {
			$("#rReturnToSupplierDiv :input").attr("disabled", "disabled");
			disableFields();
		}
	}
	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
	disableTableFields();
});

function disableElement(element) {
	$(element).attr("disabled", "disabled");
}

function disableTableFields(){
	disableElement(".stockCode");
	disableElement(".taxType");
	disableElement("#wtaxAcctSettingId");
}

function enableFields() {
	$("#wtaxAcctSettingId").removeAttr("disabled");
}

function computeRtsTotal() {
	// update amount and total amount
	var total = 0;
	var totalVat = 0;
	$("#returnToSupplierTable tbody tr").each(function(row) {
		var $tr = $(this).closest("tr");
		var quantity = accounting.unformat($tr.find(".quantity").val());
		var unitCost = accounting.unformat($tr.find(".unitCost").val());
		var vat = accounting.unformat($tr.find(".vatAmount").html());
		total += ((unitCost * quantity) - vat);
		totalVat += vat;
	});
	computeRTSGrandTotal(total, totalVat);
}

function computeRTSGrandTotal(total, totalVat) {
	$("#returnToSupplierTable").find("tfoot tr .amount").html(formatDecimalPlaces(total));
	$("#subTotal").html(formatDecimalPlaces(total));
	$("#totalVat").html(formatDecimalPlaces(totalVat));
	var wtAmount = $("#hdnWtAmount").val();
	var grandTotal = (total + totalVat) - wtAmount;
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function initializeTable () {
	var rtsItemsJson = JSON.parse($("#rtsItemsJson").val());
	setupTableData(rtsItemsJson);
}

function setupTableData (rtsItemsJson) {
	var cPath = "${pageContext.request.contextPath}";
	$("#returnToSupplierTable").html("");
	$returnToSupplierTable = $("#returnToSupplierTable").editableItem({
		data: rtsItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "rReceivingReportItemId", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "unitCost", "varType" : "double"},
			{"name" : "existingStock", "varType" : "double"},
			{"name" : "origQty", "varType" : "double"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"}
		],
			contextPath: cPath,
			header: [
				{"title" : "id", 
					"cls" : "id", 
					"editor" : "hidden",
					"visible" : false },
				{"title" : "rReceivingReportItemId", 
					"cls" : "rReceivingReportItemId", 
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
					"width" : "12%",
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
					"width" : "10%"},
				{"title" : "Qty", 
					"cls" : "quantity tblInputNumeric", 
					"editor" : "text",
					"visible" : true,
					"width" : "10%" },
				{"title" : "UOM", 
					"cls" : "uom tblLabelText", 
					"editor" : "label",
					"visible" : true,
					"width" : "8%" },
				{"title" : "Gross Price",
					"cls" : "unitCost tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "10%" },
				{"title" : "taxTypeId", 
					"cls" : "taxTypeId", 
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Tax Type", 
					"cls" : "taxType tblSelectClass",
					"editor" : "select",
					"visible" : true,
					"width" : "10%" },
				{"title" : "VAT Amount",
					"cls" : "vatAmount tblLabelNumeric",
					"editor" : "label",
					"visible" : true,
					"width" : "10%"},
				{"title" : "Amount",
					"cls" : "amount tblLabelNumeric",
					"editor" : "label",
					"visible" : true,
					"width" : "10%"},
				{"title" : "origQty", 
					"cls" : "origQty", 
					"editor" : "hidden",
					"visible" : false }
			],
			"footer" : [
				{"cls" : "amount"}
			],
			"disableDuplicateStockCode" : false,
			"addEmptyRow" : false,
			"itemTableMessage": "",
	});

	$("#returnToSupplierTable").on("focus", ".tblInputNumeric", function(){
		if ($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#returnToSupplierTable").on("blur", ".tblInputNumeric", function() {
		computeVatAmount(this, $(this).closest("tr").find(".taxType").val(), false);
		computeWtax();
	});

	$("#returnToSupplierTable tbody tr").find(".unitCost").each(function() {
		$(this).attr("readonly", "readonly")
	});
}

var isSaving = false;
function saveReturnToSupplier() {
	if (!isSaving) {
		isSaving = true;
		if ($returnToSupplierTable != null) {
			$("#rtsItemsJson").val($returnToSupplierTable.getData());
		}
		$("#btnSaveRTS").attr("disabled", "disabled");
		enableFields();
		doPostWithCallBack ("rReturnToSupplierDiv", "form", function(data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var apInvoiceRefId = $("#apInvoiceRefId").val();
				var companyId = $("#companyId").val();
				var warehouseId = $("#warehouseId").val();
				var company = $("#spanCompany").text();
				var warehouse = $("#spanWarehouse").text();
				var rrNumber = $("#spanRrNumber").text();
				var supplier = $("#spanSupplier").text();
				var supplierAcct = $("#spanSupplierAccount").text();
				var invoiceNo = $("#spanInvoiceNumber").text();
				var invoiceDate = $("#spanInvoiceDate").text();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				if ("${apInvoice.id}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					var rtsNumber = $("#rtsNumber").html();
					dojo.byId("editForm").innerHTML = data;
					$("#rtsNumber").html(rtsNumber);
					disableFields();
				}
				updateHeader(apInvoiceRefId, companyId, warehouseId, company, warehouse,
						rrNumber, supplier, supplierAcct, invoiceNo, invoiceDate);
				initializeTable();
				updateExistingStocks();
				loadWtaxAcctSettings(wtAcctSettingId);
				computeWtax();
			}
			isSaving = false;
			$("#btnSaveRTS").removeAttr("disabled");
			disableTableFields();
		});
	}
}
function disableFields(){
	disableElement("#btnRrReference");
}

function showRrReference() {
	rtsReferenceWindow = window.open(contextPath + "/retailReturnToSupplier/rtsReference","popup","width=1000,height=500,scrollbars=1,resizable=no," +
	"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
}

function updateHeader (apInvoiceRefId, companyId, warehouseId, companyName, warehouseName,
		rrNumber, supplierName, supplierAccountName, invoiceNumber, invoiceDate) {
	$("#apInvoiceRefId").val(apInvoiceRefId);
	$("#companyId").val(companyId);
	$("#warehouseId").val(warehouseId);
	$("#spanCompany").text(companyName);
	$("#spanWarehouse").text(warehouseName);
	$("#spanRrNumber").text(rrNumber);
	$("#spanSupplier").text(supplierName);
	$("#spanSupplierAccount").text(supplierAccountName);
	$("#spanInvoiceNumber").text(invoiceNumber);
	$("#spanInvoiceDate").text(invoiceDate);
}

function loadRRReference (apInvoiceId) {
	$.ajax({
		url: contextPath + "/retailReturnToSupplier?receivingReportId="+apInvoiceId,
		success : function(rts) {
			updateHeader(rts.apInvoiceRefId, rts.companyId, rts.warehouseId, rts.company.name, rts.warehouse.name,
					rts.rrNumber, rts.apInvoice.supplier.name, rts.apInvoice.supplierAccount.name,
					rts.apInvoice.invoiceNumber, rts.invoiceDate);
			setupTableData(rts.apInvoice.rtsItems);
			loadWtaxAcctSettings(rts.apInvoice.wtAcctSettingId);
			computeWtax();
			disableTableFields();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
	rtsReferenceWindow.close();
	$("#btnSaveRTS").removeAttr("disabled");
}

function updateExistingStocks() {
	$("#returnToSupplierTable tbody tr").each(function () {
		var row = $(this);
		var stockCode = $(row).find(".stockCode").val();
		var warehouseId = $("#warehouseId").val();
		if (warehouseId != "" && warehouseId != null && typeof warehouseId != "undefined"
				&& stockCode != null && stockCode != "") {
			$.ajax({
				url: contextPath + "/getItem/existingStockBySC?stockCode="+stockCode
					+"&warehouseId="+warehouseId,
				success : function(existingStock) {
					if (existingStock != null) {
						$(row).find(".existingStock").text(formatDecimalPlaces(existingStock));
					}
				},
				error : function(error) {
					console.log(error);
				},
				dataType: "text"
			});
		}
	});
}

function loadWtaxAcctSettings(wtAcctSettingId) {
	var companyId = $("#companyId").val();
	if (companyId != "") {
		var $select = $("#wtaxAcctSettingId");
		$select.empty();
		var uri = contextPath+"/getWithholdingTaxAcct?companyId="+companyId+"&isCreditable=false";
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
				// This is to remove any duplication.
				var found = [];
				$("#wtaxAcctSettingId option").each(function() {
					if ($.inArray(this.value, found) != -1) {
						$(this).remove();
					}
					found.push(this.value);
				});
				if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != undefined) {
					$select.val(wtAcctSettingId);
				}
			}
		};
		loadPopulateObject(uri, false, wtAcctSettingId, $select, optionParser, postHandler, false, true);
	}
}

function computeWtax() {
	var wtAcctSettingId = $("#wtaxAcctSettingId").val();
	if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != "undefined") {
		var wtPercentageValue = 0;
		var uri = contextPath + "/getWithholdingTaxAcct/getWtPercentage?wtAccountSettingId="+wtAcctSettingId;
		$.ajax({
			url: uri,
			success : function(wt) {
				wtPercentageValue = (accounting.unformat(wt) / 100);
				var totalAmount = 0;
				$("#returnToSupplierTable tbody tr").find(".amount").each(function() {
					totalAmount += accounting.unformat($(this).text());
				});
				var computedWTax = (totalAmount * wtPercentageValue).toFixed(6);
				computedWTax = formatDecimalPlaces(computedWTax);
				$("#hdnWtAmount").val(accounting.unformat(computedWTax));
				$("#computedWTax").html(computedWTax);
				computeRtsTotal();
			},
			error : function(error) {
				wtPercentageValue = 0;
			},
			dataType: "text"
		});
	} else {
		$("#hdnWtAmount").val("0.00");
		$("#computedWTax").html("0.00");
		computeRtsTotal();
	}
}
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="apInvoice" id="rReturnToSupplierDiv">
		<div class="modFormLabel">Return To Supplier <span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="invoiceTypeId"/>
		<form:hidden path="sequenceNumber"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="returnToSupplier.id"/>
		<form:hidden path="returnToSupplier.createdBy" />
		<form:hidden path="returnToSupplier.createdDate"/>
		<form:hidden path="rtsItemsJson" id="rtsItemsJson"/>
		<form:hidden path="returnToSupplier.companyId" id="companyId"/>
		<form:hidden path="returnToSupplier.warehouseId" id="warehouseId"/>
		<form:hidden path="returnToSupplier.apInvoiceRefId" id="apInvoiceRefId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="wtAmount" id="hdnWtAmount"/>
		<div class="modForm">
		<table class="frmField_set">
			<tr>
				<td><input type="button" id="btnRrReference" value="RR Reference" onclick="showRrReference();"/></td>
			</tr>
		</table>
		<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">RTS No.</td>
						<td class="value"><span  id="rtsNumber">
							<c:if test="${apInvoice.id > 0}">
								${apInvoice.returnToSupplier.formattedRTSNumber}
							</c:if>
						</span></td>
					</tr>
					<tr>
						<td class="labels">Status</td>
						<td class="value">
							<c:choose>
								<c:when test="${apInvoice.formWorkflow != null}">
									${apInvoice.formWorkflow.currentFormStatus.description}
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
				<legend>Return To Supplier Header</legend>
				<table class="formTable">
					<tr>
						<td class="labels">Company </td>
						<td class="value"><span id="spanCompany"></span></td>
					</tr>
					<tr>
				 		<td></td>
				 		<td  class="value" colspan="2">
				 			<form:errors path="returnToSupplier.companyId" cssClass="error"/>
				 		</td>
				 	</tr>
					<tr>
						<td class="labels">Warehouse </td>
						<td class="value"><span id="spanWarehouse"></span></td>
					</tr>
					<tr>
						<td class="labels">RR No. </td>
						<td class="value"><span id="spanRrNumber"></span></td>
					</tr>
					<tr>
						<td class="labels">Supplier</td>
						<td class="value"><span id="spanSupplier"></span></td>
					</tr>
				 	<tr>
						<td class="labels">Supplier Account</td>
						<td class="value"><span id="spanSupplierAccount"></span></td>
					</tr>
					<tr>
						<td class="labels">Supplier Invoice No. </td>
						<td class="value"><span id="spanInvoiceNumber"></span></td>
					</tr>
					<tr>
						<td class="labels">Invoice Date</td>
						<td class="value"><span id="spanInvoiceDate"></span></td>
					</tr>
					<tr>
						<td class="labels">* Date </td>
						<td class="value">
							<form:input path="glDate" onblur="evalDate('rtsDate')" 
								id="rtsDate" style="width: 120px;" cssClass="dateClass2"/>
							<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('rtsDate')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
				 		<td></td>
				 		<td  class="value" colspan="2">
				 			<form:errors path="glDate" cssClass="error"/>
				 		</td>
				 	</tr>
				</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Return To Supplier Table</legend>
				<div id="returnToSupplierTable" >
				</div>
				<table>
					<tr>
						<td><span id="rtsMessage" class="error"></span></td>
					</tr>
					<tr>
						<td>
							<form:errors path="aPlineMessage" cssClass="error"/>
						</td>
					</tr>
				</table>
		</fieldset>
		<table class="frmField_set">
			<tr>
				<td><form:errors path="formWorkflowId" cssClass="error"/></td>
			</tr>
		</table>
		<br>
		<!-- Grand Total -->
		<table class="frmField_set footerTotalAmountTbl">
			<tr>
				<td style="width: 22%;"></td>
				<td style="width: 22%;"></td>
				<td style="width: 22%;">Sub Total</td>
				<td style="width: 22%;"></td>
				<td style="width: 12%;"><span id="subTotal"></span></td>
			</tr>
			<tr>
				<td colspan="3">Total VAT</td>
				<td></td>
				<td>
					<span id="totalVat"></span>
				</td>
			</tr>
			<tr>
				<td colspan="3">Withholding Tax</td>
				<td>
					<form:select path="wtAcctSettingId" id="wtaxAcctSettingId" cssClass="frmSmallSelectClass"
						cssStyle="width: 95%;" onchange="computeWtax();">
					</form:select>
				</td>
				<td>
					<span id="computedWTax">0.00</span>
				</td>
			</tr>
			<tr>
				<td colspan="3">Total Amount Due</td>
				<td></td>
				<td>
					<span id="grandTotal">0.00</span>
				</td>
			</tr>
			<tr>
				<td colspan="5">
					<form:errors path="amount" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td colspan="5">
					<span id="errorMessage" class="error"></span>
				</td>
			</tr>
		</table>
		<br>
		<table style="margin-top: 10px;" class="frmField_set">
			<tr><td ><input type="button" id="btnSaveRTS" value="Save" onclick="saveReturnToSupplier();" style="float: right;"/> </td></tr>
		</table>
	</div>
	</form:form>
</div>
</body>
</html>