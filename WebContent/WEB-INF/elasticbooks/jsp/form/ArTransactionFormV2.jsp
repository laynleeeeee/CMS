<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : AR Transaction form page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/formatUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.saleshandler.js"></script>
<script type="text/javascript">
var defaultArLine = null;
var selectCustomerAcct = "${arTransaction.customerAcctId}";
var terms = new Array();
var arCustAcctTerms = new Array();
var maxAllowableAmount = null;
var isAllowed = false;
var totalTrAmount = null;
$(document).ready(function() {
	var wtAcctSettingId = "";
	var dueDate = null;
	var termId = null;
	initializeOtherChargesTbl();
	if ("${arTransaction.id}" != 0) {
		wtAcctSettingId = "${arTransaction.wtAcctSettingId}";
		disableAndSetCompany();
		termId = "${arTransaction.termId}";
		dueDate = "<fmt:formatDate pattern='MM/dd/yyyy' value='${arTransaction.dueDate}'/>";
		maxAllowableAmount = parseFloat("${arTransaction.arCustomer.maxAmount}");
		$("#totalAmount").val("<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${arTransaction.amount}'/>");
		updateOChargesAmt();
		computeGrandTotal();
		var status = parseInt("${arTransaction.formWorkflow.currentStatusId}");
		if (status == 17 || status == 4) {
			$("#transactionForm :input").attr("disabled","disabled");
		}
	} else {
		computeDueDate();
	}
	<c:forEach items="${terms}" var="term">
		var term = new Term ("${term.id}", "${term.days}");
		terms.push(term);
	</c:forEach>
	filterCustomerAccts(dueDate, termId);
	loadWtaxAcctSettings(wtAcctSettingId);
	computeWtax();
});

function disableAndSetCompany() {
	$("#companyId").attr("disabled","disabled");
	$("#companyId").append("<option selected='selected' value='"+"${arTransaction.companyId}"+"'>"+
			"${arTransaction.company.numberAndName}"+"</option>");
}

function ArCustAcctTerm (arCustAcctId, termId, arLine) {
	this.arCustAcctId = arCustAcctId;
	this.termId = termId;
	this.arLine = arLine;
}

function Term (id, days) {
	this.id = id;
	this.days = days;
}

function getCustomer() {
	var customerName = $.trim($("#txtCustomer").val());
	if (customerName != "") {
		$.ajax({
			url: contextPath + "/getArCustomers?name="+processSearchName(customerName)+"&isExact=true",
			success : function(customer) {
				$("#spanCustomerError").text("");
				if (customer != null && customer[0] != undefined) {
					$("#customerId").val(customer[0].id);
					$("#txtCustomer").val(customer[0].name);
					maxAllowableAmount = parseFloat(customer[0].maxAmount);
					isAllowed = false;
					filterCustomerAccts();
				} else {
					$("#customerId").val("");
					$("#spanCustomerError").text("Invalid customer name.");
				}
			},
			error : function(error) {
				maxAllowableAmount = null;
				$("#spanCustomerError").text("Invalid customer name.");
			},
			dataType: "json"
		});
	}
}

function showCustomers() {
	var customerName = $("#txtCustomer").val();
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&companyId="+companyId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#customerId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function filterCustomerAccts(dueDate, termId){
	$("#arCustomerAcctId").empty();
	var companyId = $("#companyId").val();
	if ($.trim($("#txtCustomer").val()) == "") {
		$("#customerId").val("");
	} else if (companyId != "") {
		var customerId = $("#customerId").val();
		var uri = contextPath + "/getArCustomerAccounts?arCustomerId="+customerId+"&companyId="+companyId;
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
					if ("${arTransaction.id}" == 0) {
						$(".hdnArLineSetupIds").val("");
						defaultArLine = null;
					}
					$("#arCustomerAcctId").val(selectCustomerAcct).attr("selected",true);
					var arCustomerAcctId = $("#arCustomerAcctId option:selected").val();
					arCustAcctTerms = new Array();
					for (var index = 0; index < data.length; index++){
						var rowObject =  data[index];
						var id = rowObject["id"];
						if (id == arCustomerAcctId){
							if (termId == null) {
								var defaultTerm = rowObject["termId"];
								$("#termId").val(defaultTerm).attr("selected" ,true);
							}
							defaultArLine = rowObject["defaultArLineSetup"];
						}
						var arCustAcctTerm = new ArCustAcctTerm(id, rowObject["termId"], rowObject["defaultArLineSetup"]);
						arCustAcctTerms.push (arCustAcctTerm);
					}

					loadTotalTransactionAmount();
					//Compute the due date based on the GL Date and the term selected.
					if (dueDate == null) {
						computeDueDate ();
					}
				}
		};
		loadPopulate (uri, false, selectCustomerAcct, "arCustomerAcctId", optionParser, postHandler);
	}
}

function loadTotalTransactionAmount() {
	$.ajax({
		url: contextPath + "/arTransactionForm/getTotalTrAmount?arCustomerAcctId="+$("#arCustomerAcctId").val(),
		success : function(item) {
			totalTrAmount = item;
		},
		error : function(error) {
			totalTrAmount = 0.0;
			console.log("Error in retrieving data.");
		},
		dataType: "text"
	});
}

function assignCustomerAcct (select) {
	selectCustomerAcct = $(select).val();
	for (var i=0; i<arCustAcctTerms.length; i++) {
		if (selectCustomerAcct == arCustAcctTerms[i].arCustAcctId) {
			$(".hdnArLineSetupIds").val("");
			defaultArLine = arCustAcctTerms[i].arLine;
			$("#termId").val(arCustAcctTerms[i].termId).attr("selected" ,true);
			break;
		}
	}
	isAllowed = false;
	loadTotalTransactionAmount();
}

function loadDefaultArLine(elem) {
	if (defaultArLine != null) {
		$(elem).closest("tr").find(".arLineSetupId").val(defaultArLine.id);
		$(elem).val(defaultArLine.name);
	}
}

function computeDueDate () {
	var glDateVal = $("#glDate").val ();
	if (glDateVal == null || glDateVal == ""){
		$("#dueDate").val ("");
		return;
	}
	var additionalDays = 0;
	var currentSelTermId = $("#termId option:selected").val();
	for (var i = 0; i < terms.length; i++) {
		var term = terms[i];
		if (term.id == currentSelTermId) {
			additionalDays = term.days;
			break;
		}
	}
	var glDate = new Date (glDateVal);
	glDate.setDate(glDate.getDate() + parseInt(additionalDays));
	if (!isNaN( glDate.getMonth()) && !isNaN( glDate.getDate()) && !isNaN(glDate.getFullYear())){
		$("#dueDate").val ((glDate.getMonth() + 1) +"/"+glDate.getDate()+
				"/"+glDate.getFullYear());
	} else {
		$("#dueDate").val ("");
	}
}

function formatMoney() {
	var headerAmount = Number($("#totalAmount").val());
	$("#totalAmount").val(formatDecimalPlaces(headerAmount));
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
	$(elem).val(formatDecimalPlaces(Number(headerAmount)));
}

var isSaving = false;
function saveTransaction(){
	if (isSaving == false) {
		isSaving = true;
		$("#companyId").removeAttr("disabled");
		$("#btnSaveARTransaction").attr("disabled","disabled");
		$("#arLinesJson").val($otherChargesTable.getData());
		parseDoubles();
		doPostWithCallBack("transactionForm", "form", function(data){
			if (data.substring(0,5) == "saved") {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				if ("${pId}" == 0) {
					dojo.byId("form").innerHTML = "";
				}
				isSaving = false;
			} else {
				var termId = $("#termId").val();
				var dueDate = $("#dueDate").val();
				var customer = $("#txtCustomer").val();
				var wtAcctSettingId = $("#wtaxAcctSettingId").val();
				if ("${pId}" == 0) {
					dojo.byId("form").innerHTML = data;
				} else {
					dojo.byId("editForm").innerHTML = data;
					disableAndSetCompany();
				}
				$("#txtCustomer").val(customer);
				filterCustomerAccts(dueDate, termId);
				initializeOtherChargesTbl();
				loadWtaxAcctSettings(wtAcctSettingId);
				updateOChargesAmt();
				computeGrandTotal();
				formatMoney();
				isSaving = false;
			}
			$("#btnSaveARTransaction").removeAttr("disabled");
		});
	}
}

function checkMaxAmount(elem) {
	if (maxAllowableAmount != 0) {
		value = parseFloat(totalTrAmount) + parseFloat($(elem).val());
		if (!isNaN(value) && maxAllowableAmount != null && !isAllowed) {
			if (value > maxAllowableAmount) {
				var confirmation = confirm("The amount has exceeded the customer's maximum allowable amount of "
						+ formatDecimalPlaces(maxAllowableAmount) + ". Do you wish to continue?");
				if (confirmation)
					isAllowed = true;
				else {
					isAllowed = false;
					$(elem).val("");
				}
			}
		}
	}
}

function checkMaxTotalAmount(elem) {
	if (maxAllowableAmount != 0) {
		var total = totalTrAmount != null ? parseFloat(totalTrAmount) : 0;
		$("#otherChargesTable tbody tr").each(function(i) {
			var amount = $(this).find(".upAmount").val();
			total += amount != "" && !isNaN(amount) ? parseFloat(amount) : 0;
		});
		if (total > maxAllowableAmount && !isAllowed) {
			var confirmation = confirm("The total amount has exceeded the customer's maximum allowable amount of "
					+ formatDecimalPlaces(maxAllowableAmount) + ". Do you wish to continue?");
			if (confirmation) {
				isAllowed = true;
			} else {
				isAllowed = false;
				setTimeout(function() {
					$(elem).val("");
					$(elem).focus();
				}, 0);
			}
		}
	}
}

function clearFormFields() {
	$("#txtCustomer").val("");
	$("#arCustomerAcctId").empty();
	$otherChargesTable.emptyTable();
}

function initializeOtherChargesTbl() {
	var otherChargesTable = JSON.parse($("#arLinesJson").val());
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

	$("#otherChargesTable").on("focus", ".arLineSetupName", function() {
		if ($.trim($(this).val()) == "") {
			loadDefaultArLine(this);
		}
	});

	$("#otherChargesTable").on("blur", ".arLineSetupName", function() {
		if ($.trim($(this).val()) != "") {
			getArLineSetup(this)
		}
	});

	$("#otherChargesTable").on("focus", ".tblInputNumeric", function() {
		if($.trim($(this).val()) != "") {
			$(this).val(accounting.unformat($(this).val()));
		}
	});

	$("#otherChargesTable").on("blur", ".upAmount", function() {
		var $upAmount = $(this).closest("tr").find(".upAmount");
		var upAmount = $upAmount.val();
		if ($.trim(upAmount) != "") {
			checkMaxTotalAmount(this);
		}
		$upAmount.val(formatDecimalPlaces(upAmount, 6));

		updateOChargesAmt();
		computeGrandTotal();
		computeWtax();
	});

	$("#otherChargesTable").on("change", ".taxType", function() {
		updateOChargesAmt();
		computeGrandTotal();
		computeWtax();
	});
}

function getArLineSetup(elem) {
	$("#spanArLineError").text("");
	var companyId = $("#companyId").val();
	var arLineSetup = $(elem).val();
	$.ajax({
		url: contextPath+"/getArLineSetups/getALSetup?companyId="+companyId+"&name="+processSearchName(arLineSetup),
		success : function(arLine) {
			var arLineSetupId = "";
			if (arLine != null && arLine != undefined) {
				arLineSetupId = arLine.id;
			} else {
				$("#spanArLineError").text("Invalid AR line setup.");
			}
			$(elem).closest("tr").find(".arLineSetupId").val(arLineSetupId);
		},
		error : function(error) {
			$(elem).closest("tr").find(".arLineSetupId").val("");
			$("#spanArLineError").text("Invalid AR line setup.");
		},
		dataType: "json"
	});
}

function computeGrandTotal() {
	var totalOtherCharges = accounting.unformat($("#otherChargesTable").find("tfoot tr .amount").html());
	$("#subTotal").html(formatDecimalPlaces(totalOtherCharges));
	var totalVat = 0;
	$("#otherChargesTable").find(" tbody tr ").each(function(row) {
		totalVat += accounting.unformat($(this).find(".vatAmount").html());
	});
	$("#totalVat").html(formatDecimalPlaces(totalVat));
	var wtaxAmount = accounting.unformat($("#hdnWtAmount").val());
	$("#computedWTax").html(formatDecimalPlaces(wtaxAmount));
	var grandTotal = (totalOtherCharges + totalVat) - wtaxAmount
	$("#grandTotal").html(formatDecimalPlaces(grandTotal));
}

function loadWtaxAcctSettings(wtAcctSettingId) {
	var $select = $("#wtaxAcctSettingId");
	$select.empty();
	var uri = contextPath+"/getWithholdingTaxAcct?companyId="+$("#companyId").val()+"&isCreditable=true";
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

function computeWtax() {
	var wtAcctSettingId = $("#wtaxAcctSettingId").val();
	if (wtAcctSettingId != "" && wtAcctSettingId != null && typeof wtAcctSettingId != "undefined") {
		var wtPercentageValue = 0;
		var uri = contextPath + "/getWithholdingTaxAcct/getWtPercentage?wtAccountSettingId="+wtAcctSettingId;
		$.ajax({
			url: uri,
			success : function(wt) {
				wtPercentageValue = (accounting.unformat(wt) / 100);
				var totalAmount = accounting.unformat($("#subTotal").text());
				var computedWTax = (totalAmount * wtPercentageValue).toFixed(6);
				computedWTax = formatDecimalPlaces(computedWTax);
				$("#hdnWtAmount").val(accounting.unformat(computedWTax));
				$("#computedWTax").html(computedWTax);
				computeGrandTotal();
			},
			error : function(error) {
				wtPercentageValue = 0;
			},
			dataType: "text"
		});
	} else {
		$("#hdnWtAmount").val("0.00");
		$("#computedWTax").html("0.00");
		updateOChargesAmt();
		computeGrandTotal();
	}
}

function assignWtaxAcctSetting(elem) {
	var selectedValue = $(elem).val();
	loadWtaxAcctSettings(selectedValue);
	computeWtax();
}
</script>
<title>AR Transaction Form</title>
</head>
<body>
<div id="divARLineList" style=" display: none"></div>
<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="arTransaction" id="transactionForm">
		<div class="modFormLabel">AR Transaction <span class="btnClose" id="btnClose">[X]</span></div>
		<form:hidden path="id"/>
		<form:hidden path="ebObjectId" />
		<form:hidden path="createdBy"/>
		<form:hidden path="formWorkflowId"/>
		<form:hidden path="customerId"/>
		<form:hidden path="arLinesJson" id="arLinesJson"/>
		<form:hidden path="wtAmount" id="hdnWtAmount"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="labels">Sequence Number:</td>
						<td class="value">
							<form:input path="sequenceNumber" readonly="true" cssClass="textBoxLabel"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Status:</td>
						<c:set var="status" value="${arTransaction.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
						<td class="value">
							<input type="text" id="txtTransactionStatus" class="textBoxLabel"
									readonly="readonly" value='${status}'/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Transaction Header</legend>
				<table class="formTable" border=0>
					<tr>
						<td class="labels">* Company </td>
						<td class="value">
							<form:select path="companyId" id="companyId" cssClass="frmSelectClass"
								items="${companies}" itemLabel="numberAndName" itemValue="id" onchange="clearFormFields();">
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
						<td class="labels">* Transaction Type</td>
						<td class="value"> 
							<form:select path="transactionTypeId" id="transactionTypeId" cssClass="frmSelectClass">
								<form:options items="${transactionTypes}" itemLabel="name" itemValue="id" />
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="labels">* Transaction No</td>
						<td class="value">
							<form:input path="transactionNumber" cssClass="inputSmall" />
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="transactionNumber" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Customer Name</td>
						<td class="value">
							<form:input path="arCustomer.name" id="txtCustomer" class="input" onkeydown="showCustomers();" onblur="getCustomer();"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<span id="spanCustomerError" class="error"></span>
							<form:errors path="customerId" cssClass="error" id="customerIdErr"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Customer Account</td>
						<td class="value">
							<form:select path="customerAcctId" id="arCustomerAcctId" cssClass="frmSelectClass"
								onchange="assignCustomerAcct (this);"></form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="customerAcctId" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Term</td>
						<td class="value">
							<form:select path="termId" id="termId" cssClass="frmSelectClass" onchange="computeDueDate();">
								<form:options items="${terms}" itemLabel="name" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="labels">* Transaction Date</td>
						<td class="value">
							<form:input path="transactionDate"
									id="transactionDate" onblur="evalDate('transactionDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('transactionDate')"
							style="cursor: pointer" style="float: right;" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="transactionDate" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* GL Date</td>
						<td class="value">
						<form:input path="glDate" id="glDate"
								onblur="evalDate('glDate');  computeDueDate();" style="width: 120px;"
								class="dateClass2" />
								<img id="imgDate2"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('glDate')" style="cursor: pointer"
							style="float: right;" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="glDate" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Due Date</td>
						<td class="value">
						<form:input path="dueDate" id="dueDate" onblur="evalDate('dueDate')"
							style="width: 120px;" class="dateClass2" />
							<img id="imgDate3" src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dueDate')"
							style="cursor: pointer" style="float: right;" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="dueDate" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Description</td>
						<td class="value"><form:textarea path="description"
									id="description" class="input" /></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="description" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Amount</td>
						<td class="value">
							<form:input path="amount" id="totalAmount" class="numeric" cssStyle="width: 172px;"
								onfocus="parseDoubles();" onblur="checkMaxAmount(this); formatAmount(this);"/>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="amount" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>AR Lines</legend>
				<div id="otherChargesTable"></div>
				<input type="hidden" id="hdnOChargesAmt"/>
				<form:errors path="arLineError" cssClass="error"/>
				<span id="spanUOMError" class="error"></span>
				<span id="spanArLineError" class="error"></span>
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
				<tr>
					<td colspan="5">
						<form:errors path="totalAmount" cssClass="error"/>
					</td>
				</tr>
			</table>
			<br>
			<table class="frmField_set">
				<tr>
					<td align="right"><input type="button" id="btnSaveARTransaction" value="Save" onclick="saveTransaction();"/></td>
				</tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>