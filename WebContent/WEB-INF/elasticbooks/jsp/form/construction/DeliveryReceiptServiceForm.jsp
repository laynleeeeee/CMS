<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Delivery Receipt - Service form.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.drsaleshandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	initializeDrLineTable();
	if(Number("${deliveryReceipt.id}") != 0) {
		$("#txtATWRefNumber").val("${deliveryReceipt.soNumber}");
		$("#aOpen").hide();
		$("#hdnTermDays").val("${deliveryReceipt.term.days}");
	}
	if("${deliveryReceipt.formWorkflow.complete}" == "true" ||
			"${deliveryReceipt.formWorkflow.currentStatusId}" == 4) {
		$("#deliveryReceiptForm :input").attr("disabled", "disabled");
		$("#imgDate").hide();
		$("#imgDueDate").hide();
	}
});

var isSaving = false;
function saveDeliveryReceipt() {
	if (!isSaving) {
		var atwNumber = $.trim($("#txtATWRefNumber").val());
		if (atwNumber != "") {
			isSaving = true;
			// enabling item and line table
			$("#otherChargesTable :input").removeAttr("disabled");
			// reset JSON table values
			$("#drLineJson").val($otherChargesTable.getData());
			$("#btnSaveDeliveryReceipt").attr("disabled", "disabled");
			doPostWithCallBack ("deliveryReceiptForm", "form", function(data) {
				if (data.startsWith("saved")) {
					var objectId = data.split(";")[1];
					var formStatus = new Object();
					formStatus.objectId = objectId;
					updateTable (formStatus);
					dojo.byId("form").innerHTML = "";
				} else {
					var soId = $("#hdnSalesOrderId").val();
					var companyId = $("#companyId").val();
					var companyName = $("#spCompanyName").text();
					var customerId = $("#hdnArCustomerId").val();
					var customerName = $("#spCustomerName").text();
					var customerAcctId = $("#hdnArCustomerAcctId").val();
					var customerAcctName = $("#spCustomerAcctName").text();
					var termId = $("#hdnTermId").val();
					var termName = $("#spTermName").text();
					var txtCurrentStatusId = $("#txtCurrentStatusId").val();
					var termDays = $("#hdnTermDays").val();
					var remarks = $("#remarks").val();
					if ("${deliveryReceipt.id}" == 0) {
						dojo.byId("form").innerHTML = data;
					} else {
						dojo.byId("editForm").innerHTML = data;
						$("#aOpen").hide();
					}
					$("#txtCurrentStatusId").val(txtCurrentStatusId);
					updateHeader(soId, atwNumber, companyId, companyName, customerId, customerName,
							customerAcctId, customerAcctName, termId, termName, termDays, remarks);
					initializeDrLineTable();
				}
				isSaving = false;
				$("#btnSaveDeliveryReceipt").removeAttr("disabled");
			});
		} else {
			$("#spanSalesOrderMsg").text("Sales order reference is required.");
		}
	}
}

function showATWReferences() {
	var drTypeId = $.trim($("#hdnDrTypeId").val());
	$("#divATWReferenceId").load(contextPath+"/deliveryReceipt/showReferenceForm?drTypeId="+drTypeId);
}

function loadSalesOrderReference(salesOrderId) {
	if (salesOrderId != "") {
		var drTypeId = $.trim($("#hdnDrTypeId").val());
		$.ajax({
			url: contextPath+"/deliveryReceipt/convertSOtoDR?salesOrderId="+salesOrderId+"&drTypeId="+drTypeId,
			success : function(dr) {
				$("#spanSalesOrderMsg").html("");
				$("#divATWReferenceId").html("");
				$("#aClose")[0].click();
				updateHeader(dr.salesOrderId, dr.atwNumber, dr.companyId, dr.company.name, dr.arCustomerId,
						dr.arCustomer.name, dr.arCustomerAccountId, dr.arCustomerAccount.name, dr.termId, dr.term.name,
						dr.term.days, dr.remarks);
				setupDrLines(dr.drLines);
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function computeDueDate() {
	var termDays = $("#hdnTermDays").val();
	var dateVal = $("#date").val();
	if (dateVal == null || dateVal == "") {
		$("#dueDate").val("");
		return;
	}
	var date = new Date(dateVal);
	date.setDate(date.getDate() + parseInt(termDays));
	if (!isNaN(date.getMonth()) && !isNaN(date.getDate()) && !isNaN(date.getFullYear())) {
		$("#dueDate").val((date.getMonth()+1)+"/"+date.getDate()+"/"+date.getFullYear());
	} else {
		$("#dueDate").val("");
	}
}

function updateHeader(atwId, atwNumber, companyId, companyName, arCustomerId,
		arCustomerName, arCustomerAcctId, arCustomerAcctName, termId, termName, termDays, remarks) {
	$("#hdnSalesOrderId").val(atwId);
	$("#txtATWRefNumber").val(atwNumber);
	$("#companyId").val(companyId);
	$("#spCompanyName").text(companyName);
	$("#hdnArCustomerId").val(arCustomerId);
	$("#spCustomerName").text(arCustomerName);
	$("#hdnArCustomerAcctId").val(arCustomerAcctId);
	$("#spCustomerAcctName").text(arCustomerAcctName);
	$("#hdnTermId").val(termId);
	$("#spTermName").text(termName);
	$("#remarks").val(remarks);
	$("#hdnTermDays").val(termDays);
	computeDueDate();
}

function initializeDrLineTable() {
	var drLineJson = JSON.parse($("#drLineJson").val());
	setupDrLines(drLineJson);
}

function setupDrLines(drLineJson) {
	$("#otherChargesTable").html("");
	var path = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: drLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "salesQuotationId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "arLineSetupId", "varType" : "int"},
			{"name" : "arLineSetupName", "varType" : "string"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "unitOfMeasurementId", "varType" : "int"},
			{"name" : "discountTypeId", "varType" : "int"},
			{"name" : "discountValue", "varType" : "double"},
			{"name" : "quantity", "varType" : "double"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "discount", "varType" : "double"},
			{"name" : "refenceObjectId", "varType" : "int"}
		],
		contextPath: path,
		header: [ 
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "salesQuotationId",
				"cls" : "salesQuotationId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "arLineSetupId",
				"cls" : "arLineSetupId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Service",
				"cls" : "arLineSetupName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "75%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
			})},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "unitOfMeasurementId",
				"cls" : "unitOfMeasurementId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "UOM",
				"cls" : "unitMeasurementName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "8%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Gross Price",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "8%"},
			{"title" : "discountTypeId",
				"cls" : "discountTypeId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Discount<br>Type",
				"cls" : "discountType tblSelectClass",
				"editor" : "select",
				"visible" : false,
				"width" : "8%"},
			{"title" : "Discount<br>Value",
				"cls" : "discountValue tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "8%"},
			{"title" : "Computed<br>Discount",
				"cls" : "discount tblLabelNumeric",
				"editor" : "label",
				"visible" : false,
				"width" : "8%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : false,
				"width" : "8%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : false,
				"width" : "8%"},
			{"title" : "Amount",
				"cls" : "amount tblInputNumeric",
				"editor" : "text",
				"visible" : false,
				"width" : "10%"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function() {
		disableInputFields(this, "spanOtherChargesTbl");
	});

	// Disable AR line setup name field/s.
	$("#otherChargesTable tr td .arLineSetupName").each(function() {
		$(this).attr("disabled", "disabled");
	});
}

function disableInputFields(elem, spanIdName) {
	var $tr = $(elem).closest("tr");
	var $arLineSetupName = $tr.find(".arLineSetupName");
	$arLineSetupName.attr("disabled", "disabled");
	if ($.trim($arLineSetupName.val()) == "") {
		$("#"+spanIdName).text("Adding of new service/s without SO is not allowed.");
	}
}
</script>
<style>
</style>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divATWReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divATWReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="deliveryReceipt" id="deliveryReceiptForm">
	<div class="modFormLabel">Delivery Receipt - Service<span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="sequenceNo"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="deliveryReceiptTypeId" id="hdnDrTypeId"/>
		<form:hidden path="serialDrItemsJson" id="serialDrItemsJson"/>
		<form:hidden path="nonSerialDrItemsJson" id="nonSerialDrItemsJson"/>
		<form:hidden path="drLineJson" id="drLineJson"/>
		<br>
		<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="labels">Sequence Number</td>
					<td class="value">
						<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"
									value="${deliveryReceipt.sequenceNo}"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Status</td>
					<c:set var="status" value="${deliveryReceipt.formWorkflow.currentFormStatus.description}"/>
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
		<legend>Delivery Receipt Header</legend>
			<table class="formTable">
				<tr>
					<td class="labels">* SO Reference</td>
					<td class="value">
						<form:hidden path="salesOrderId" id="hdnSalesOrderId"/>
						<input id="txtATWRefNumber" readonly="readonly"/>
						<a href="#container" id="aOpen" data-reveal-id="divATWReferenceId" class="link_button"
							onclick="showATWReferences();">Browse SO</a>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="authorityToWithdrawId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><span id="spanSalesOrderMsg" class="error"></span></td>
				</tr>
				<tr>
					<td class="labels">* Company</td>
					<td class="value">
						<form:hidden path="companyId" id="companyId"/>
						<span id="spCompanyName">${deliveryReceipt.company.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="companyId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">* Customer</td>
					<td class="value">
						<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
						<span id="spCustomerName">${deliveryReceipt.arCustomer.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value">
						<span id="spanCustomerError" class="error"></span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value">
						<form:errors path="arCustomerId" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td class="labels">* Customer Account</td>
					<td class="value">
						<form:hidden path="arCustomerAccountId" id="hdnArCustomerAcctId"/>
						<span id="spCustomerAcctName">${deliveryReceipt.arCustomerAccount.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value">
						<form:errors path="arCustomerAccountId" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td class="labels">* Term</td>
					<td class="value">
						<form:hidden path="termId" id="hdnTermId"/>
						<input type="hidden" id="hdnTermDays"/>
						<span id="spTermName">${deliveryReceipt.term.name}</span>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="termId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">* Date</td>
					<td class="value">
						<form:input path="date" id="date" onblur="evalDate('date'); computeDueDate();"
							style="width: 120px;" class="dateClass2" />
						<img id="imgDate" src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('date')"
							style="cursor: pointer" style="float: right;" />
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="date" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">* Due Date</td>
					<td class="value">
						<form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate');"
							style="width: 120px;" class="dateClass2" />
						<img id="imgDueDate" src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dueDate');"
							style="cursor: pointer" style="float: right;" />
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="dueDate" id="errDueDate" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">Ship To</td>
					<td class="value">
						<form:textarea path="remarks" id="remarks" class="txtArea"/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Service Table</legend>
			<div id="otherChargesTable"></div>
			<table>
				<tr>
					<td><form:errors path="drLines" cssClass="error"/></td>
				</tr>
				<tr>
					<td>
						<span id="spanOtherChargesTbl" class="error"></span>
					</td>
				</tr>
			</table>
		</fieldset>
		<table class="frmField_set">
			<tr>
				<td><form:errors path="commonErrorMsg" cssClass="error"/></td>
			</tr>
		</table>
		<br>
		<table class="frmField_set">
			<tr>
				<td align="right"><input type="button" id="btnSaveDeliveryReceipt"
						value="Save" onclick="saveDeliveryReceipt();" /></td>
			</tr>
		</table>
		</div>
	</form:form>
</div>
</body>
</html>