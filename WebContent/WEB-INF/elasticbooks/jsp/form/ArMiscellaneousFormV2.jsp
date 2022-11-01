<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: AR Miscellaneous form.
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
<script src="${pageContext.request.contextPath}/js/formatUtil.js" type="text/javascript" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.saleshandler.js"></script>
<style type="text/css">
input.numeric {
	width: 150px;
}

.tdProperties {
	border-right: 1px solid #000000;
}

.textBoxLabel, .txtArLineSetupName, .txtQuantity,
.txtUnitMeasurementName, .txtUpAmount, .txtAmount, .txtDescription, .txtNumber{
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}

.divVeil {
    background-color: #F2F1F0;
}

.imgDelete{
	margin-left: 2px;
}
</style>
<script type="text/javascript">
var selectCustomerAcct = "${arMiscellaneous.arCustomerAccountId}";
var currentCustAcctId = 0;
var $otherChargesTable = null;
var wtAcctSettingId = "${arMiscellaneous.wtAcctSettingId}";
$(document).ready (function () {
	$("#txtSequenceNumber").val("${sequenceNo}");
	filterCustomerAccts();
	initializeOtherChargesTbl();
	updateOChargesAmt();
	updateTotalAmount();
	if ("${arMiscellaneous.id}" != 0) {
		$("#arMiscellaneousForm :input").attr("disabled","disabled"); // Other charges must not be edited after creation
	}
});

function loadWtaxAcctSettings(wtAcctSettingId) {
	var companyId = $("#companyId").val();
	if (companyId != "") {
		var $select = $("#wtaxAcctSettingId");
		$select.empty();
		var uri = contextPath+"/getWithholdingTaxAcct?companyId="+companyId+"&isCreditable=true";
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
	var totalOcVat = 0;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		totalOcVat += accounting.unformat($(this).find(".vatAmount").html());
	});
	var totalAmount = accounting.unformat($("#subTotal").html());
	if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != "undefined") {
		var wtPercentageValue = 0;
		var uri = contextPath + "/getWithholdingTaxAcct/getWtPercentage?wtAccountSettingId="+wtAcctSettingId;
		$.ajax({
			url: uri,
			success : function(wt) {
				wtPercentageValue = (accounting.unformat(wt) / 100);
				var computedWTax = totalAmount * wtPercentageValue;
				$("#hdnWtAmount").val(accounting.unformat(formatNumber(computedWTax)));
				$("#computedWTax").html(formatNumber(computedWTax));
				$("#grandTotal").html(formatNumber((totalAmount+totalOcVat)-computedWTax));
			},
			error : function(error) {
				wtPercentageValue = 0;
			},
			dataType: "text"
		});
	} else {
		$("#hdnWtAmount").val("");
		$("#computedWTax").html("0.00");
		$("#grandTotal").html(formatNumber((totalAmount+totalOcVat)));
	}
}

function assignWtaxAcctSetting(elem) {
	wtAcctSettingId = $(elem).val();
	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
}

function formatNumber(value) {
	return accounting.formatMoney(value, '', 2);
}

function assignCustAcctId() {
	currentCustAcctId = $("#arCustomerAcctId option:selected").val();
}

function getCustomer () {
	$.ajax({
		url: contextPath + "/getArCustomers?name="+$("#txtCustomer").val() + "&isExact=true",
		success : function(customer) {
			$("#spanCustomerError").text("");
			if (customer != null && customer[0] != undefined) {
				$("#arCustomerId").val(customer[0].id);
				$("#txtCustomer").val(customer[0].name);
			}
			filterCustomerAccts();
		},
		error : function(error) {
			$("#spanCustomerError").text("Invalid customer.");
			$("#txtCustomer").val("");
		},
		dataType: "json"
	});
}

function showCustomers () {
	var rmId = $("#receiptMethodId").val();
	var uri = contextPath + "/getArCustomers/byReceiptMethod?name="+$("#txtCustomer").val()
			+ "&receiptMethodId="+rmId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#arCustomerId").val(ui.item.id);
			$("#spanCustomerError").text("");
			$(this).val(ui.item.name);
			filterCustomerAccts();
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					$("#spanCustomerError").text("");
					if (ui.item != null) {
						$(this).val(ui.item.name);
					}
					filterCustomerAccts();
				},
				error : function(error) {
					$("#spanCustomerError").text("Invalid customer.");
					$("#txtCustomer").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function filterCustomerAccts(){
	$("#arCustomerAcctId").empty();
	if ($.trim($("#txtCustomer").val()) == "") {
		$("#arCustomerId").val("");
	} else {
		var customerId = $("#arCustomerId").val();
		var uri = contextPath + "/arMiscellaneous/getArCustomerAccounts?arCustomerId="+customerId+
				"&receiptMethodId="+$("#receiptMethodId").val();
		
		var optionParser = {
				getValue: function (rowObject){
					if (rowObject != null)
						return rowObject["id"];
				},
				
				getLabel: function (rowObject){
					if (rowObject != null)
						return rowObject["name"];
				}
		};
		
		postHandler = {
				doPost: function(data) {	
					$("#arCustomerAcctId").val(selectCustomerAcct).attr("selected",true);
					var customerAcctId = $("#arCustomerAcctId option:selected").val();
					$.ajax({
						url: contextPath + "/getCompany/arCustomerAcct?arCustomerAccountId="+$("#arCustomerAcctId").val(),
						success : function(company) {
							if (company != null && company != undefined) {
								$("#companyId").val(company.id);
							}
							loadWtaxAcctSettings(wtAcctSettingId);
						},
						error : function(error) {
							$("#companyId").val("");
						},
						dataType: "json"
					});
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAcctId", optionParser, postHandler);
	}
}

function assignCustomerAcct (select) {
	selectCustomerAcct = $(select).val();
}

function updateTotalAmount() {
	var totalAmount = $("#otherChargesTable").find("tfoot tr").find(".amount").html();
	$("#spanTotalAmount").html(accounting.formatMoney(totalAmount));
	$("#subTotal").html(accounting.formatMoney(totalAmount));
	var totalOcVat = null;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		totalOcVat += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#totalVat").html(accounting.formatMoney(totalOcVat));
	computeWtax();
}

var isSaving = false;
function saveArMiscellaneous () {
	if(isSaving == false) {
		isSaving = true;
		$("#arMiscellaneousLinesJson").val($otherChargesTable.getData());
		$("#btnSaveArMiscellaneous").attr("disabled", "disabled");
		parseDoubles();
		doPostWithCallBack ("arMiscellaneousForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if("${pId}" == 0)
					dojo.byId("form").innerHTML = "";
				isSaving = false;
			} else {
				var customer = $("#txtCustomer").val();
				var isCheck = $("#arMiscellaneousTypeId").val() == 2;
				var companyId = $("#companyId").val();
				if("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
				}
				if (isCheck) {
					$("#refNumber").removeAttr("disabled");
				}
				$("#companyId").val(companyId);
				$("#txtCustomer").val(customer);
				filterCustomerAccts();
				initializeOtherChargesTbl();
				updateOChargesAmt();
				loadWtaxAcctSettings(wtAcctSettingId);
				updateTotalAmount();
				formatMoney();
				isSaving = false;
			}
			$("#btnSaveArMiscellaneous").removeAttr("disabled");
		});
	}
}

function enableDisableCheck(value) {
	// If equals to check enable check number field
	if (value == 2)
		$("#refNumber").removeAttr("disabled");
	else
		$("#refNumber").attr("disabled", "disabled");
}

function receiptMehodOnChanged() {
	$("#txtCustomer").val("");
	$("#arCustomerId").val("");
	filterCustomerAccts();
}

function initializeOtherChargesTbl() {
	var otherChargesTable = JSON.parse($("#arMiscellaneousLinesJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$otherChargesTable = $("#otherChargesTable").editableItem({
		data: otherChargesTable,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "arLineSetupId", "varType" : "int"},
			{"name" : "unitOfMeasurementId", "varType" : "int"},
			{"name" : "quantity", "varType" : "int"},
			{"name" : "upAmount", "varType" : "double"},
			{"name" : "amount", "varType" : "double"},
			{"name" : "arLineSetupName", "varType" : "string"},
			{"name" : "unitMeasurementName", "varType" : "string"},
			{"name" : "taxTypeId", "varType" : "int"},
			{"name" : "vatAmount", "varType" : "double"}
		],
		contextPath: cPath,
		header:[
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "arLineSetupId",
				"cls" : "arLineSetupId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "unitOfMeasurementId",
				"cls" : "unitOfMeasurementId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "AR Line",
				"cls" : "arLineSetupName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "25%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
			})},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%" },
			{"title" : "UOM",
				"cls" : "unitMeasurementName tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "10%",
				"handler" : new SalesTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Gross Price",
				"cls" : "upAmount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "10%"},
			{"title" : "taxTypeId", 
				"cls" : "taxTypeId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Tax Type", 
				"cls" : "taxType tblSelectClass",
				"editor" : "select",
				"visible" : true,
				"width" : "15%" },
			{"title" : "VAT Amount",
				"cls" : "vatAmount tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "15%"},
			{"title" : "Amount",
				"cls" : "amount tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "20%"}
		],
		"footer" : [
			{"cls" : "amount"}
		],
		"disableDuplicateStockCode" : false
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function() {
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
			updateTotalAmount();
		}
	});

	$("#otherChargesTable").on("blur", ".tblInputNumeric", function() {
		var amount = $(this).closest("tr").find(".amount").val();
		updateOChargesAmt(amount);
		updateTotalAmount();
	});

	$("#otherChargesTable").on("change", ".taxType", function() {
		var amount = $(this).closest("tr").find(".amount").val();
		updateOChargesAmt(amount);
		updateTotalAmount();
	});
}

function formatMoney() {
	var headerAmount = Number($("#totalAmount").val());
	$("#totalAmount").val(accounting.formatMoney(headerAmount));
}

function parseDoubles() {
	var headerAmount = $.trim($("#totalAmount").val());
	if (headerAmount == "") {
		$("#totalAmount").val("0.00");
	} else {
		$("#totalAmount").val(Number(accounting.unformat(headerAmount)));
	}
}

function formatAmount(elem) {
	var headerAmount = accounting.unformat($(elem).val());
	$(elem).val(accounting.formatMoney(Number(headerAmount)));
}

</script>
</head>
<body>
	<div id="divMiscellaneousList" style="display: none"></div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="arMiscellaneous" id="arMiscellaneousForm">
			<div class="modFormLabel">Other Receipt<span class="btnClose" id="btnClose">[X]</span></div>
			<form:hidden path="createdBy"/>
			<form:hidden path="formWorkflowId"/>
			<form:hidden path="sequenceNo"/>
			<form:hidden path="arCustomerId"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="wtAmount" id="hdnWtAmount"/>
			<input type="hidden" id="companyId"/>
			<form:hidden path="arMiscellaneousLinesJson" id="arMiscellaneousLinesJson"/>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number:</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status:</td>
							<c:set var="status" value="${arMiscellaneous.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtarMiscellaneousStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AR Miscellaneous Header</legend>
					<table class="formTable">
						<form:hidden path="id" />
						<tr>
							<td class="labels">* Type</td>
							<td class="value">
								<form:select path="arMiscellaneousTypeId" cssClass="frmSelectClass" 
									onchange="enableDisableCheck(this.value);">
									<form:options items="${arMiscellaneousTypes}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Check Number</td>
							<td class="value">
								<form:input path="refNumber" id="refNumber" class="inputSmall" disabled="true" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="refNumber" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Receipt Method</td>
							<td class="value">
								<form:select path="receiptMethodId" cssClass="frmSelectClass" onchange="receiptMehodOnChanged();">
									<form:options items="${receiptMethods}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Customer</td>
							<td class="value">
								<form:input path="arCustomer.name" id="txtCustomer" class="input"
									onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="getCustomer();" />
							</td>
						</tr>
						
						<tr>
							<td></td>
							<td colspan="2">
								<span id="spanCustomerError" class="error" style="margin-left: 12px;"></span>
								<form:errors path="arCustomerId" cssClass="error" />
							</td>
						</tr>
					
						<tr>
							<td class="labels">* Customer Account</td>
							<td class="value">
								<form:select path="arCustomerAccountId" id="arCustomerAcctId" cssClass="frmSelectClass" onchange="assignCustomerAcct (this);">
								</form:select>
							</td>
						</tr>
						
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="arCustomerAccountId" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Receipt No.</td>
							<td class="value"><form:input path="receiptNumber" id="receiptNumber" class="inputSmall" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="receiptNumber" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Receipt Date</td>
							<td class="value">
								<form:input path="receiptDate" id="receiptDate" onblur="evalDate('receiptDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('receiptDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="receiptDate"	cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Maturity Date</td>
							<td class="value">
								<form:input path="maturityDate" id="maturityDate" onblur="evalDate('maturityDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('maturityDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="maturityDate" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Description</td>
							<td class="value"><form:textarea path="description"
									id="description" class="input" /></td>
						</tr>
					
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="description" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>
						
						<tr>
							<td class="labels">* Amount</td>
							<td class="value">
								<form:input path="amount" id="totalAmount" class="numeric" style="text-align: right;" size="20" 
									onfocus="parseDoubles();" onblur="formatAmount(this);"/>
							</td> 
						</tr>
						
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="amount" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>AR Lines</legend>
					<div id="otherChargesTable"></div>
					<input type="hidden" id="hdnOChargesAmt">
					<table>
						<tr>
							<td colspan="7">
								<form:errors path="arMiscLineMessage" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
				<br>
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
								cssStyle="width: 95%;" onchange="assignWtaxAcctSetting(this);">
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
				</table>
				<br>
				<table class="frmField_set">
					<tr>
						<td><form:errors path="formWorkflowId" cssClass="error"/></td>
					</tr>
					<tr>
						<td align="right"><input type="button" id="btnSaveArMiscellaneous" value="Save" onclick="saveArMiscellaneous();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>