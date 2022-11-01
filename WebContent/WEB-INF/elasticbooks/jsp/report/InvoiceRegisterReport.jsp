<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AP Invoice Register Report.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/commonUtil.js"></script>

<script type="text/javascript">
var selectedCompanyId = 0;
$(document).ready(function() {
	companyOnChange();
	var newDate = parseServerDate(new Date);
	$("#asOfDate").val(newDate);
});

$(function () {
	$("#sequenceNoFrom").bind("keyup keydown", function() {
		inputOnlyNumeric("sequenceNoFrom");
	});

	$("#sequenceNoTo").bind("keyup keydown", function() {
		inputOnlyNumeric("sequenceNoTo");
	});

	$("#amountFrom").live("keydown keyup", function () {
		checkAndSetDecimal($("#amountFrom"));
	});

	$("#amountTo").live("keydown keyup", function () {
		checkAndSetDecimal($("#amountTo"));
	});
});

function filterSuppliers() {
	$("#spanCompanyError").text("");
	$("#spanSupplierError").text("");
	selectedCompanyId = $("#companyId").val();
	var selectedDivisionId = $("#divisionId").val();
	var supplierName =  $("#supplierId").val() != "" ? $.trim($("#supplierId").val()) : $("#hdnSupplierId").val(-1);
	var uri = contextPath+"/getSuppliers/new?name="+supplierName+"&companyId="+selectedCompanyId
		+(selectedDivisionId != -1 ? "&divisionId="+selectedDivisionId : "");
	$("#supplierId").autocomplete({
		source: uri,
		select: function(event, ui) {
			$("#hdnSupplierId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui) {
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#hdnSupplierId").val(ui.item.id);
						$(this).val(ui.item.name);
						filterSupplierAccts();
					} else {
						var supplier = $.trim($("#supplierId").val()) ;
						if(supplier != "") {
							showSupplierErrorMsg();
						}
					}
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
		.data( "ui-autocomplete-item", item )
		.append( "<a style='font-size: small;'>"+item.name +"</a>" )
		.appendTo( ul );
	};
}


function validateSupplier() {
	var supplier = $.trim($("#supplierId").val());
	if(supplier != "") {
			$.ajax({
				url: contextPath+"/getSupplier?name="+processSearchName(supplier),
				success: function (supplier) {
					if(supplier != null) {
						$("#supplierId").val(supplier.name);
						$("#hdnSupplierId").val(supplier.id);
					}
				},
				error : function(error) {
					showSupplierErrorMsg();
				},
				dataType: "json"
			});
	} else {
		appendAll();
	}
}

function showSupplierErrorMsg() {
	$("#spanSupplierError").text("Invalid supplier name.");
	$("#hndSupplierId").val("");
	$("#supplierId").val("");
}

function filterSupplierAccts() {
	selectedCompanyId = $("#companyId").val();
	var selectedDivisionId = $("#divisionId").val();
	var supplierId = Number($("#hdnSupplierId").val()) != 0 ? $("#hdnSupplierId").val() : -1;
	if(supplierId != -1) {
	$("#supplierAcctId").empty();
		var uri = contextPath+"/getApSupplierAccts?supplierId="+supplierId+"&companyId="+selectedCompanyId
				+(selectedDivisionId != -1 ? "&divisionId="+selectedDivisionId : "");
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, true, null, "supplierAcctId", optionParser, null);
	} else {
		appendAll();
	}
}

function appendAll() {
	$("#supplierAcctId").empty();
	$("#supplierAcctId").append("<option selected='selected' value=-1>ALL</option>");
}

function generateInvoices(){
	var companyId = $("#companyId option:selected").val();
	var supplierId = $("#supplierId").val();
	if(supplierId == "") {
		$("#hdnSupplierId").val(-1);
		$("#spanSupplierError").text("");
	} if(companyId == -1) {
		$("#spanCompanyError").text("Company is required.");
		$("#iFrame").attr('src', "");
	} if($("#asOfDate").val() == "") {
		$("#spanAsOfDateError").text("As of date is required.");
		$("#iFrame").attr('src', "");
	} if(companyId != -1 && $("#asOfDate").val() != "") {
		var url = contextPath + "/invoiceRegister/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}

function getCommonParam () {
	var companyId = $("#companyId option:selected").val();
	var divisionId = $("#divisionId").val();
	var invoiceTypeId = $("#invoiceTypeId option:selected").val();
	var supplierId = $("#hdnSupplierId").val();
	var supplierAcctId = $("#supplierAcctId option:selected").val();
	var termId = $("#termId option:selected").val();
	var invoiceDateFrom = $("#invoiceDateFrom").val();
	var invoiceDateTo = $("#invoiceDateTo").val();
	var invoiceNumber = processSearchName($("#invoiceNumber").val());
	var glDateFrom = $("#glDateFrom").val();
	var glDateTo = $("#glDateTo").val();
	var dueDateFrom = $("#dueDateFrom").val();
	var dueDateTo = $("#dueDateTo").val();
	var amountFrom = $("#amountFrom").val();
	var amountTo = $("#amountTo").val();
	var sequenceNoFrom = $("#sequenceNoFrom").val();
	var sequenceNoTo = $("#sequenceNoTo").val();
	var invoiceStatusId = $("#invoiceStatusId option:selected").val();
	var paymentStatusId = $("#paymentStatusId option:selected").val();
	var asOfDate = $("#asOfDate").val();
	var formatType = $("#formatType").val();

	var url = "?companyId="+companyId+"&divisionId="+divisionId+"&invoiceTypeId="+invoiceTypeId+"&supplierId="+supplierId+
			"&supplierAccountId="+supplierAcctId+"&termId="+termId+"&invoiceNumber="+invoiceNumber+
			"&fromInvoiceDate="+invoiceDateFrom+"&toInvoiceDate="+invoiceDateTo+"&fromGLDate="+glDateFrom+
			"&toGLDate="+glDateTo+"&fromDueDate="+dueDateFrom+"&toDueDate="+dueDateTo+"&fromAmount="+amountFrom+
			"&toAmount="+amountTo+"&fromSequenceNumber="+sequenceNoFrom+"&toSequenceNumber="+sequenceNoTo+
			"&invoiceStatusId="+invoiceStatusId+"&paymentStatusId="+paymentStatusId+"&asOfDate=" + asOfDate +
			"&formatType=" + formatType;
	return url;
}

function checkAndSetDecimal(txtObj) {
	var amount = $(txtObj).val();
    var amountLength = amount.length;
    var cntDec = 0;
    var lengthFirstDec = 0;
    var finalAmount = "";

    for (var i=0; i<amountLength; i++) {
        var char = amount.charAt(i);
        if (!isNaN(char) || (char == ".") || (char == "-")) {
			if (char == "-" && i==0)
				finalAmount += char;
            if (char == ".")
                cntDec++;
            if ((char == ".") && (cntDec == 1)) {
                lengthFirstDec = i + 1;
                finalAmount += char;
            }
            if (!isNaN(char))
                finalAmount += char;
        }
    }
    if ((cntDec == 1) && (amountLength > (lengthFirstDec + 2)))
        finalAmount = roundTo2Decimal(finalAmount);
    $(txtObj).val(finalAmount);
}

function companyOnChange() {
	$("#hndSupplierId").val("");
	$("#supplierId").val("");
	$("#supplierAcctId").val("");
	filterSuppliers();
	filterSupplierAccts();
}

function divisionOnChange() {
	$("#supplierAcctId").val("");
	$("#hndSupplierId").val("");
	$("#supplierId").val("");
	filterSuppliers();
	filterSupplierAccts();
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td><select id="companyId" onchange="companyOnChange()" class="frmSelectClass">
			<option selected='selected' value=-1>Please select a company</option>
			<c:forEach var="company" items="${companies}">
				<option value="${company.id}">${company.name}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
		<tr>
		<td class="title2">Division </td>
		<td><select id="divisionId" onchange="divisionOnChange()" class="frmSelectClass">
			<option selected='selected' value=-1>ALL</option>
			<c:forEach var="division" items="${divisions}">
				<option value="${division.id}">${division.name}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Invoice Type </td>
		<td>
			<select id="invoiceTypeId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="invoiceType" items="${invoiceTypes}">
					<option value="${invoiceType.id}">${invoiceType.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Supplier</td>
		<td><input type="hidden" id="hdnSupplierId"/>
		<input class="input" id="supplierId" onblur="validateSupplier();"
			onkeydown="filterSuppliers();" onkeyup="filterSuppliers();"></td>
	</tr>
		<tr>
		<td></td>
		<td colspan="2">
			<span id="spanSupplierError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Supplier Account</td>
		<td>
			<select id="supplierAcctId" class="frmSelectClass"></select>
		</td>
	</tr>
	<tr>
		<td class="title2">Terms</td>
		<td>
			<select id="termId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="term" items="${terms}">
					<option value="${term.id}">${term.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Invoice Number </td>
		<td><input type="text" id="invoiceNumber" class="input"></td>
	</tr>
	<tr>
		<td class="title2">Invoice Date </td>
		<td class="tdDateFilter">
			<input type="text" id="invoiceDateFrom" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('invoiceDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="invoiceDateTo" maxlength="10" class="dateClass2"
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('invoiceDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">GL Date </td>
		<td class="tdDateFilter">
			<input type="text" id="glDateFrom" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('glDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="glDateTo" maxlength="10" class="dateClass2"
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('glDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Due Date </td>
		<td class="tdDateFilter">
			<input type="text" id="dueDateFrom" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dueDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dueDateTo" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dueDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Amount </td>
		<td class="tdDateFilter">
			<input type="text" id="amountFrom" class="dateClass2" style="text-align: right;">
			<span style="font-size: small;">To</span>
			<input type="text" id="amountTo" class="dateClass2" style="text-align: right;">
		</td>
	</tr>
	<tr>
		<td class="title2">Sequence Number </td>
		<td class="tdDateFilter">
			<input type="text" id="sequenceNoFrom" class="dateClass2" style="text-align: right;">
			<span style="font-size: small;">To</span>
			<input type="text" id="sequenceNoTo" class="dateClass2" style="text-align: right;">
		</td>
	</tr>
	<tr>
		<td class="title2">Invoice Status </td>
		<td>
			<select id="invoiceStatusId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="invoiceStatus" items="${invoiceStatuses}">
					<option value="${invoiceStatus.id}">${invoiceStatus.description}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Payment Status </td>
		<td><select id="paymentStatusId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="status" items="${statuses}">
					<option value="${status.value}">${status.description}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">As of Date </td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('asOfDate')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanAsOfDateError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Format:</td>
		<td class="value"><select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
		</select></td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateInvoices()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>