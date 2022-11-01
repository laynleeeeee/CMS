<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: Equipment Utilization Form.
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
var $tblDeliveryReceiptItem = null;
var $tblDRSerialItem  = null;
$(document).ready(function() {
	initEULineTable();
	if(Number("${deliveryReceipt.id}") != 0) {
		$("#txtSORefNumber").val("${deliveryReceipt.soNumber}");
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

function hideElem(elem) {
	$(elem).css("display", "none");
}

var isSaving = false;
function saveDeliveryReceipt() {
	if($("#hdnSoRefId").val() != "") {
		if (!isSaving) {
			isSaving = true;
			$("#otherChargesTable :input").removeAttr("disabled");
			$("#euLineJson").val($otherChargesTable.getData());
			$("#btnSaveDeliveryReceipt").attr("disabled", "disabled");
			doPostWithCallBack ("deliveryReceiptForm", "form", function(data) {
				if (data.startsWith("saved")) {
					var objectId = data.split(";")[1];
					var formStatus = new Object();
					formStatus.objectId = objectId;
					updateTable (formStatus);
					dojo.byId("form").innerHTML = "";
				} else {
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
					var soId = $("#hdnSoRefId").val();
					var soNumber = $("#txtSORefNumber").val();
					var remarks = $("#remarks").val();
					if ("${deliveryReceipt.id}" == 0) {
						dojo.byId("form").innerHTML = data;
					} else {
						dojo.byId("editForm").innerHTML = data;
						$("#aOpen").hide();
					}
					$("#txtCurrentStatusId").val(txtCurrentStatusId);
					updateHeader(soId, soNumber, companyId, companyName, customerId, customerName,
							customerAcctId, customerAcctName, termId, termName, termDays, remarks);
					initEULineTable();
				}
				isSaving = false;
				$("#btnSaveDeliveryReceipt").removeAttr("disabled");
			});
		}
	} else {
		$("#atwIdErr").text("Sales order reference is required.");
	}
}

function showSOReferences() {
	$("#divSOReferenceId").load(contextPath+"/deliveryReceipt/showSOTruckingRefsForm?typeId=3");
}

function loadSOReference(sotRefId) {
	$.ajax({
		url: contextPath + "/deliveryReceipt/loadSOTReferenceForm?sotRefId="+sotRefId+"&typeId=3",
		success : function(dr) {
			$("#divSOReferenceId").html("");
			$("#aClose")[0].click();
			updateHeader(dr.salesOrderId, dr.soNumber, dr.companyId, dr.company.name, dr.arCustomerId,
					dr.arCustomer.name, dr.arCustomerAccountId, dr.arCustomerAccount.name, dr.termId, dr.term.name,
					dr.term.days, dr.remarks);
			setupEuLines(dr.euLines);
			computeDueDate();
			$("#atwIdErr").text("");
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
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

function updateHeader(soId, soNumber, companyId, companyName, arCustomerId,
		arCustomerName, arCustomerAcctId, arCustomerAcctName, termId, termName, termDays, remarks) {
	$("#hdnSoRefId").val(soId);
	$("#txtSORefNumber").val(soNumber);
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
}

function initEULineTable() {
	var euLineJson = JSON.parse($("#euLineJson").val());
	setupEuLines(euLineJson);
}

function setupEuLines(euLineJson) {
	$("#otherChargesTable").html("");
	var path = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: euLineJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "deliveryReceiptId", "varType" : "int"},
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
			{"title" : "deliveryReceiptId",
				"cls" : "deliveryReceiptId",
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

	$("#otherChargesTable tr td a").each(function() {
		hideElem(this);
	});

	$("#otherChargesTable :input").attr("disabled", "disabled");
}
</script>
<style>
</style>
</head>
<body>
<div id="container" class="popupModal">
	<div id="divATWReferenceContainer" class="reveal-modal">
		<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
		<div id="divSOReferenceId"></div>
	</div>
</div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="deliveryReceipt" id="deliveryReceiptForm">
	<div class="modFormLabel">Equipment Utilization<span class="btnClose" id="btnClose">[X]</span></div>
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
		<form:hidden path="euLineJson" id="euLineJson"/>
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
		<legend>Equipment Utilization Header</legend>
			<table class="formTable">
				<tr>
					<td class="labels">* SO Reference</td>
					<td class="value">
						<form:hidden path="salesOrderId" id="hdnSoRefId"/>
						<input id="txtSORefNumber" readonly="readonly"/>
						<a href="#container" id="aOpen" data-reveal-id="divSOReferenceId" class="link_button"
							onclick="showSOReferences();">Browse SO</a>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="authorityToWithdrawId" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><span id="atwIdErr" class="error"></span></td>
				</tr>
				<tr>
					<td class="labels">Company</td>
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
					<td class="labels">Customer</td>
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
					<td class="labels">Customer Account</td>
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
					<td class="labels">Term</td>
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
			<legend>Equipment Service Table</legend>
			<div id="otherChargesTable"></div>
			<table>
				<tr>
					<td><form:errors path="drLines" cssClass="error"/></td>
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