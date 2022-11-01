<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Purchase Order form for Inventory Retail.
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.itemhandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<style type="text/css">
.prevUC {
	float: right;
	width: 100%;
	text-align: right;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	if ("${rPurchaseOrder.id}" > 0) {
		disableAndSetCompany();
	}
	filterSupplierAccts();
	initializeTable(JSON.parse($("#hdnPoItemsJson").val()));
	initializetOtherCharges(JSON.parse($("#hdnPoLineJson").val()));
	computeGrandTotal();
	if ("${rPurchaseOrder.formWorkflow.currentStatusId}" == 4
			|| "${rPurchaseOrder.formWorkflow.currentStatusId}" == 3) {
		$("#btnSaveRPurchaseOrder").attr("disabled", "disabled");
	}
});

function initializeTable(poItemsJson) {
	$("#poItemDivTable").html("");
	var isAddRow = $("#hdnStrPrReferences").val() == "";
	$poItemTable = $("#poItemDivTable").editableItem({
		data : poItemsJson,
		jsonProperties : [
				{"name" : "id", "varType" : "int"},
				{"name" : "rPurchaseOrderId", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "refenceObjectId", "varType" : "int"},
				{"name" : "itemId", "varType" : "int"},
				{"name" : "quantity", "varType" : "double"},
				{"name" : "unitCost", "varType" : "double"},
				{"name" : "stockCode", "varType" : "string"}],
		contextPath : "${pageContext.request.contextPath}",
		header : [
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "rPurchaseOrderId",
					"cls" : "rPurchaseOrderId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "ebObjectId",
					"cls" : "ebObjectId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "refenceObjectId",
					"cls" : "refenceObjectId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "itemId",
					"cls" : "itemId",
					"editor" : "hidden",
					"visible" : false},
				{"title" : "Stock Code",
					"cls" : "stockCode tblInputText",
					"editor" : "text",
					"visible": true,
					"width" : "10%",
					"handler" : new ItemTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "Description",
					"cls" : "description tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "15%"},
				{"title" : "Qty",
					"cls" : "quantity tblInputNumeric",
					"editor" : "text",
					"visible": true,
					"width" : "8%"},
				{"title" : "UOM",
					"cls" : "uom tblLabelText",
					"editor" : "label",
					"visible": true,
					"width" : "8%"},
				{"title" : "Previous<br>Unit Cost",
					"cls" : "prevUC tblLabelNumeric",
					"editor" : "label",
					"visible" : true,
					"width" : "8%" },
				{"title" : "Gross Price",
					"cls" : "unitCost tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "8%" },
				{"title" : "Amount",
					"cls" : "amount tblLabelNumeric",
					"editor" : "label",
					"visible" : true,
					"width" : "8%"},
				],
		"footer" : [
				{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : true,
		"addEmptyRow" : isAddRow,
		"itemTableMessage" : "poItemErrors"
	});

	$("#poItemDivTable").on("focus", ".tblInputNumeric", function() {
		var unitCost = $(this).closest("tr").find(".unitCost").val();
		if ($.trim(unitCost) != "") {
			$(this).closest("tr").find(".unitCost").val(accounting.unformat(unitCost));
		}
	});

	$("#poItemDivTable").on("blur", ".tblInputNumeric", function() {
		var $unitCost = $(this).closest("tr").find(".unitCost");
		$unitCost.val(formatDecimalPlaces($unitCost.val(), 4));
		computeGrandTotal();
	});
}

function loadSuppliers() {
	var supplierName = processSearchName($("#txtSupplier").val());
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getSuppliers/new?name="+supplierName + "&companyId="+companyId;
	loadACItems("txtSupplier", "hdnSupplierId", null, uri,
			uri+"&isExact=true", "name",
			function() {
				$("#spanSupplierError").text("");
				validateSupplier();
			}, function() {
				$("#spanSupplierError").text("");
				validateSupplier();
			}, function() {
				$("#spanSupplierError").text("");
			}, function() {
				invalidSupplierMsg();
			}
	);
}

function invalidSupplierMsg(){
	$("#spanSupplierError").text("Invalid Supplier.");
}

function validateSupplier() {
	var supplier = processSearchName($("#txtSupplier").val());
	var selectedSupplierID = $("#hdnSupplierId").val();
	$.ajax({
		url: contextPath+"/getSupplier?name="+supplier,
		success : function(supplier) {
			if (supplier == null && $("#txtSupplier").val() != "") {
				invalidSupplierMsg();
			}
			filterSupplierAccts();
		},
		error : function(error) {
			console.log(error);
			invalidSupplierMsg();
		},
		dataType: "json"
	});
	if(supplier != "" && selectedSupplierID == "") {
		invalidSupplierMsg();
	}
}

function filterSupplierAccts() {
	var selectedCompanyId = $("#companyId").val();
	var selectedSupplierId = $("#hdnSupplierId").val();
	var txtSupplier = $("#txtSupplier").val();
	if ((selectedCompanyId != "" && selectedSupplierId != "") && txtSupplier != "") {
		var uri = contextPath+"/getApSupplierAccts?supplierId="+selectedSupplierId+
				"&companyId="+selectedCompanyId;
		var selectedSupplierAcctId = $("#hdnSupplierAcctId").val();
		$("#selectSupplierAcctId").empty();
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
				//Set the id of the supplier account
				setSupplierAcct();
			}
		};
		loadPopulate (uri, false, selectedSupplierAcctId, "selectSupplierAcctId", optionParser, postHandler);
	} else if(txtSupplier == "") {
		//Empty the supplier account dropdown list and remove the supplier and supplier account id
		$("#selectSupplierAcctId").empty();
		$("#hdnSupplierId").val(null);
		$("#hdnSupplierAcctId").val(null);
	}
}

function setSupplierAcct() {
	var supplierAcctId = $("#selectSupplierAcctId").val();
	$("#hdnSupplierAcctId").val(supplierAcctId);
	$("#selectTermId").val();
}

var isSaving = false;
function saveRPurchaseOrder() {
	if (!isSaving) {
		isSaving = true;
		setCompanyId();
		var prReferences = $("#hdnStrPrReferences");
		$("#hdnPoItemsJson").val($poItemTable.getData());
		$("#hdnPoLineJson").val($otherChargesTable.getData());
		$("#btnSaveRPurchaseOrder").attr("disabled", "disabled");
		doPostWithCallBack ("rPurchaseOrderForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var companyId = $("#companyId option:selected").val();
				var supplierName = $("#txtSupplier").val();
				var txtPrRef = $("#txtPrRef").val();
				var tdProject = $("#tdProject").text();
				var tdFleet = $("#tdFleet").text();
				if("${rPurchaseOrder.id}" == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val(companyId);
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableAndSetCompany();
				}
				$("#txtSupplier").val(supplierName);
				$("#txtPrRef").val(txtPrRef);
				$("#tdProject").text(tdProject);
				$("#tdFleet").text(tdFleet);
				filterSupplierAccts();
				initializeTable(JSON.parse($("#hdnPoItemsJson").val()));
				initializetOtherCharges(JSON.parse($("#hdnPoLineJson").val()));
			}
			isSaving = false;
			$("#btnSaveRPurchaseOrder").removeAttr("disabled");
		});
	}
}

function setCompanyId() {
	var companyId = $("#companyId").val();
	$("#hdnCompanyId").val(companyId);
}

function disableAndSetCompany() {
	//Disable and set company
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${rPurchaseOrder.companyId}"+"'>"+
	"${rPurchaseOrder.company.numberAndName}"+"</option>");
}

function computeGrandTotal() {
	var poItemTotalAmt = $("#poItemDivTable").find("tfoot tr .amount").html();
	var poLineTotalAmt = $("#otherChargesTable").find("tfoot tr .amount").html();
	var grandTotal = accounting.unformat(poItemTotalAmt) + accounting.unformat(poLineTotalAmt);
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function showEmployees() {
	var companyId = $("#companyId").val();
	var employeeName = $.trim($("#requestedById").val());
	var uri = contextPath + "/getEmployees/byName?companyId="+ companyId
			+ "&name=" + processSearchName(employeeName) + "&isExact=false";
	$("#requestedById").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnRequestedById").val(ui.item.id);
			var employeeName =ui.item.lastName + ", "
				+ ui.item.firstName + " " + ui.item.middleName;
			$(this).val(employeeName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		var employeeName = item.lastName + ", "
			+ item.firstName + " " + item.middleName;
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + employeeName + "</a>" )
			.appendTo( ul );
	};
}

function getEmployee() {
	var companyId = $("#companyId").val();
	var txtEmployee = $.trim($("#requestedById").val())
	var uri = contextPath + "/getEmployees/bycCompanyAndName?companyId="+ companyId + "&name=" + txtEmployee;
	$("#hdnRequestedById").val("");
	$("#spanRequestedByError").text("");
	$.ajax({
		url: uri,
		success : function(employee) {
			if (txtEmployee != "") {
				if (employee != null && typeof employee[0] != "undefined") {
					$("#hdnRequestedById").val(employee[0].id);
				} else {
					$("#spanRequestedByError").text("Invalid employee.");
				}
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function showPRReference() {
	IS_INITIAL_LOAD = true;
	var companyId = $("#companyId").val();
	var url = contextPath + "/retailPurchaseOrder/prReference?companyId="+companyId;
	$("#divPrReference").load(url);
}

function loadPrReference(slctdPrForms) {
	$("#aClose")[0].click();
	var prObjIds = "";
	for (var i = 0; i< slctdPrForms.length; i++) {
		prObjIds += slctdPrForms[i] + ";";
	}
	var uri = contextPath + "/retailPurchaseOrder/loadRequests?prObjIds="+prObjIds;
	$("#hdnPoIdReferences").val(prObjIds);
	$.ajax({
		url: uri,
		success : function(po) {
			clearRfRef();
			updateHeader(po);
			initializeTable(po.purchaseOrder.rPoItems);
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function checkUnitcost($input, index) {
	$td = $($input).closest("tr").find("#tdPrevUc"+index);
	var prevUC = accounting.unformat($.trim($($td).text()));
	if(isNaN(prevUC)) {
		return;
	}
	var value = accounting.unformat($.trim($($input).val()));
	$($input).val(formatMaxDecimalPlaces(value));
	if(value == 0 || prevUC == 0){
		$($input).css("background-color", "white");
		$($input).parent().css("background-color", "white");
	}else if(prevUC > 0 && typeof prevUC != undefined) {
		if(value > prevUC) {
			$($input).css("background-color", "ff9999");
			$($input).parent().css("background-color", "#ff9999");
		} else if(value < prevUC) {
			$($input).css("background-color", "ffff99");
			$($input).parent().css("background-color", "#ffff99");
		} else {
			$($input).css("background-color", "white");
			$($input).parent().css("background-color", "white");
		}
	}
}

function getPrevUC() {
	var supplierAcctId = $("#selectSupplierAcctId").val();
	if(supplierAcctId != "" && supplierAcctId != "undefined"
		&& supplierAcctId != undefined) {
		$("#poItemTbl tbody tr").each(function (index) {
			var itemId = $(this).find(".poiItemId").val();
			var $prevUC = $(this).find(".prevUnitCost");
			var $unitCost = $(this).find("#unitCost");
			var uri = contextPath + "/retailPurchaseOrder/getLatestUC?itemId="+itemId
				+"&supplierAcctId="+supplierAcctId;
			$.ajax({
				url: uri,
				success : function(unitCost) {
					$prevUC.text(unitCost);
				},
				complete : function() {
					checkUnitcost($unitCost, index);
				},
			});
		});
	}
}

function updateHeader(po) {
	var strPrNumbers = po.strPrNumbers.replace(',', '');
	$("#hdnStrPrReferences").val(strPrNumbers);
	$("#txtPrRef").val(po.strPrNumbersComma);
	$("#tdProject").text(po.projectName);
	$("#tdFleet").text(po.fleetProfileCode);
	$("#txtRemarks").val(po.remarks);
	$("#warehouseId").val(po.warehouseId);
	$("#companyId").attr("disabled", "disabled");
}

function clearRfRef() {
	$("#divPrReference").html("");
}

function rePopulatePOItemsUnitCost(poItemsJSON) {
	var poItems = JSON.parse(poItemsJSON);
	$("#poItemDivTable tbody tr").each(function(row) {
		var itemId = $(this).closest("tr").find(".hdnItemIds").val();
		var $unitCost = $(this).closest("tr").find(".unitCostCls");
		$.each(poItems, function(itmKey, itmVal) {
			if(row === itmKey && itmVal.itemId === itemId) {
				$($unitCost).val(itmVal.unitCost);
			}
		});
	});
}

function builPOItemsJSON() {
	var json = "[";
	$("#poItemTbl tbody tr").each(function (i) {
		var itemId = $(this).find(".hdnItemIds").val();
		var stockCode = $(this).find("#stockCode").html();
		var quantity = $(this).find("#quantity").val();
		var unitCost = $(this).find("#unitCost").val();
		var ebObjectId = $(this).find("#ebObjectId").val();
		var refenceObjectId = $(this).find("#refenceObjectId").val();
		var origRefObjectId = $(this).find("#origRefObjectId").val();
		var isLastRow = $(this).is(":last-child");

		json += "{";

		json += '"itemId"' + ":" + '"' + itemId + '"' + ",";
		json += '"stockCode"' + ":" + '"' + stockCode + '"' + ",";
		json += '"quantity"' + ":" + '"' + quantity + '"' + ",";
		json += '"unitCost"' + ":" + '"' + unitCost + '"' + ",";
		json += '"ebObjectId"' + ":" + '"' + ebObjectId + '"' + ",";
		json += '"refenceObjectId"' + ":" + '"' + refenceObjectId + '"';
		json += "}";

		if (!isLastRow) {
			json += ",";
		}
	});
	json += "]";
	return json;
}

function companyOnChange() {
	$("#poItemDivTable").html("");
	$("#hdnStrPrReferences").val("");
	$("#txtPrRef").val("");
	$("#tdProject").text("");
	$("#tdFleet").text("");
	$("#txtSupplier").val("");
	$("#hdnSupplierId").val("");
	$("#selectSupplierAcctId").empty();
	$("#hdnSupplierAcctId").val("");
}

function initializetOtherCharges(poLineJson) {
	$("#otherChargesTable").html("");
	var cPath = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: poLineJson,
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
					"handler" : new ItemTableHandler (new function () {
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
					"width" : "23%",
					"handler" : new ItemTableHandler (new function () {
						this.handleTotal = function (total) {
							// Do nothing
						};
					})},
				{"title" : "UP",
					"cls" : "upAmount tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "19%"},
				{"title" : "Amount",
					"cls" : "amount tblInputNumeric",
					"editor" : "text",
					"visible" : true,
					"width" : "23%"}
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
		var amount = $(this).closest("tr").find(".amount").val();
		updateOChargesAmt(amount);
		computeGrandTotal()
	});

	resizeTbl("#otherChargesTable", 6);
}

function resizeTbl(tableId, rowCount){
	var $tbl = $(tableId);
	$($tbl).attr("width", "100%");
	addTotalLabel($tbl)
	$($tbl).find("tfoot tr td:nth-child(1)").attr("colspan", rowCount);
}

function addTotalLabel($tbl){
	var $tr = $($tbl).find("tfoot tr td:nth-child(1)");
	$($tr).html("Total");
}
</script>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divPRReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divPrReference"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="rPurchaseOrder" id="rPurchaseOrderForm">
		<div class="modFormLabel">Purchase Order <span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="poNumber"/>
			<form:hidden path="divisionId" id="hdnDivisionId"/>
			<form:hidden path="supplierId" id="hdnSupplierId"/>
			<form:hidden path="supplierAccountId" id="hdnSupplierAcctId"/>
			<form:hidden path="companyId" id="hdnCompanyId"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="requestedById" id="hdnRequestedById"/>
			<form:hidden path="prReference" id="hdnStrPrReferences"/>
			<form:hidden path="poItemsJson" id="hdnPoItemsJson"/>
			<form:hidden path="poLineJson" id="hdnPoLineJson"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Purchase Order No.</td>
							<td class="value"><span id="spanPoNumber">
								<c:if test="${rPurchaseOrder.id > 0}">
									${rPurchaseOrder.formattedPONumber}</c:if></span></td>
						</tr>
						<tr>
							<td class="labels">Status</td>
							<td class="value">
							<c:choose>
								<c:when test="${rPurchaseOrder.formWorkflow != null}">
									${rPurchaseOrder.formWorkflow.currentFormStatus.description}
								</c:when>
								<c:otherwise>
									NEW
								</c:otherwise>
							</c:choose></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Purchase Order Header</legend>
					<table class="formTable">
						<form:hidden path="id" />
						<tr>
							<td class="labels">* Company</td>
							<td class="value"><select class="frmSelectClass" id="companyId" onblur="validateSupplier();"
									onchange="companyOnChange();">
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
							<td class="labels">* Date </td>
							<td class="value"><form:input path="poDate" onblur="evalDate('poDate')" style="width: 120px;" cssClass="dateClass2"/>
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
										onclick="javascript:NewCssCal('poDate')" style="cursor: pointer" style="float: right;" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="poDate" cssClass="error"/></td>
						</tr>
						<tr class="hidden">
							<td class="labels">* Purchase Requisition</td>
							<td class="value">
								<input id="txtPrRef"  readonly="readonly" value="${rPurchaseOrder.strPrReferences}"/>
								<form:hidden path="strPrReferences" id="hdnPrReferences" />
								<a href="#container" id="aOpen" data-reveal-id="divPRReference" class="link_button" onclick="showPRReference();">Browse PR</a>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="strPrReferences" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Supplier</td>
							<td class="value">
								<form:input path="supplier.name" id="txtSupplier" value="${rPurchaseOrder.supplier.name}"
									onkeydown="loadSuppliers();" onblur="validateSupplier();" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<span id="spanSupplierError" class="error">
									<form:errors path="supplierId" cssClass="error"/>
								</span>
							</td>
						</tr>
						<tr>
							<td class="labels">* Supplier Account</td>
							<td class="value"><select class="frmSelectClass" id="selectSupplierAcctId" onchange="setSupplierAcct();">
									</select></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><span id="spanSupplierAcctError">
								<form:errors path="supplierAccountId" cssClass="error"/></span></td>
						</tr>
						<tr class="hidden">
							<td class="labels">Project</td>
							<td class="value" id="tdProject">
								${rPurchaseOrder.strCustomerName}
							</td>
						</tr>

						<tr class="hidden">
							<td class="labels">Fleet</td>
							<td class="value" id="tdFleet">
								${rPurchaseOrder.strFleetCode}
							</td>
						</tr>
						<tr>
							<td class="labels">* Term </td>
							<td class="value"><form:select path="termId" cssClass="frmSelectClass" id="selectTermId">
								<form:options items="${terms}" itemLabel="name" itemValue="id"></form:options>
									</form:select></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="termId" cssClass="error"/></td>
						</tr>
						<tr style="display: none;">
							<td class="labels"> Requested By</td>
							<td class="value"><form:input path="employeeName" id="requestedById"
								onkeyup="showEmployees();" onkeydown="showEmployees();" onblur="getEmployee();" cssClass="input"/></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><span id="spanRequestedByError" class="error">
								<form:errors path="requestedById" cssClass="error"/></span></td>
						</tr>
						<tr>
							<td class="labels" align="right">Remarks</td>
							<td class="value" colspan="2"><form:textarea path="remarks" rows="3"
									style="width: 350px; resize: none;" /></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="remarks" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Purchase Order Items</legend>
					<div id="poItemDivTable"></div>
					<table>
						<tr>
							<td><span id="poItemErrors" class="error"></span></td>
						</tr>
						<tr>
							<td><form:errors path="poMessage" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Other Charges</legend>
					<div id="otherChargesTable"></div>
					<table>
						<tr>
							<td>
								<form:errors path="poLines" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<br>
				<!-- Grand Total -->
				<table class="frmField_set">
					<tr>
						<td>Total Amount Due</td>
						<td align="right"><span id="grandTotal">0.0</span></td>
					</tr>
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td><span id="poItemErrors" class="error"></span></td>
					</tr>
					<tr>
						<td><form:errors path="poMessage" cssClass="error" /></td>
					</tr>
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSaveRPurchaseOrder"
							value="Save" onclick="saveRPurchaseOrder();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>