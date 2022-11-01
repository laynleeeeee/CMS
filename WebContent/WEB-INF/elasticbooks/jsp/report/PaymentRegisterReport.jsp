<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--

	Description: Payment Register Report.
 -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/commonUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/ZeroClipboard.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/TableTools.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	companyOnChange();
});

$(function () {
	$("#checkNoFrom").bind("keyup keydown", function() {
		inputOnlyNumeric("checkNoFrom");
	});

	$("#checkNoTo").bind("keyup keydown", function() {
		inputOnlyNumeric("checkNoTo");
	});

	$("#amountFrom").live("keydown keyup", function () {
		checkAndSetDecimal($("#amountFrom"));
	});

	$("#amountTo").live("keydown keyup", function () {
		checkAndSetDecimal($("#amountTo"));
	});

	$("#voucherNoFrom").live("keydown keyup", function () {
		checkAndSetDecimal($("#voucherNoFrom"));
	});

	$("#voucherNoTo").live("keydown keyup", function () {
		checkAndSetDecimal($("#voucherNoTo"));
	});
});

function searchPayments() {
	var url = contextPath + "/paymentRegister/generateReport"+getCommonParam();
	var supplierId = $.trim($("#supplierId").val());
	var companyId = $("#companyId").val();
	if(supplierId == "") {
		$("#hdnSupplierId").val("-1");
		$("#spanSupplierError").text("");
	} if(companyId == -1){
		$("#spanCompanyError").text("Company is required");
		$("#iFrame").attr('src', "");
	} if(companyId != -1) {
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}

function getCommonParam() {
	var companyId = $("#companyId").val();
	var divisionId = $("#slctDivisionId").val();
	var bankAccountId = $("#bankAccountId").val();
	var supplierId = $("#hdnSupplierId").val();
	var supplierAcctId = $("#supplierAcctId").val()
	var paymentDateFrom = $("#payDateFrom").val();
	var paymentDateTo = $("#payDateTo").val();
	var checkDateFrom = $("#checkDateFrom").val();
	var checkDateTo = $("#checkDateTo").val();
	var amountFrom = $("#amountFrom").val();
	var amountTo = $("#amountTo").val();
	var voucherNoFrom = $("#voucherNoFrom").val();
	var voucherNoTo = $("#voucherNoTo").val();
	var checkNoFrom = $("#checkNoFrom").val();
	var checkNoTo = $("#checkNoTo").val();
	var paymentStatusId = $("#paymentStatusId").val();
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&bankAccountId="+bankAccountId+"&supplierId="+(typeof supplierId != "undefined" ? supplierId : "-1")
			+ "&supplierAccountId="+(supplierAcctId != null && typeof supplierAcctId != "undefined" ? supplierAcctId : "-1")+"&paymentDateFrom="+paymentDateFrom+"&paymentDateTo="
			+ paymentDateTo+"&checkDateFrom="+checkDateFrom+"&checkDateTo="+checkDateTo+"&amountFrom="
			+ amountFrom+"&amountTo="+amountTo+"&voucherNoFrom="+voucherNoFrom+"&voucherNoTo="+voucherNoTo
			+ "&checkNoFrom="+checkNoFrom+"&checkNoTo="+checkNoTo+"&paymentStatusId="+ paymentStatusId + "&formatType=" + formatType;
}

function loadDivisions() {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions/byCompany?companyId="
			+companyId+"&isMainLevelOnly=true";
	$("#slctDivisionId").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},
		getLabel: function (rowObject) {
			return rowObject["number"] + " " + rowObject["name"];
		}
	};
	postHandler = {
			doPost: function(data) {
				// nothing
			}
	};
	loadPopulate (uri, true, null, "slctDivisionId", optionParser, postHandler);
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

function filterSuppliers() {
	$("#spanCompanyError").text("");
	$("#spanSupplierError").text("");
	var selectedCompanyId = $("#companyId").val();
	var supplierName =  $("#supplierId").val() != "" ? $.trim($("#supplierId").val()) : $("#hdnSupplierId").val(-1);
	var uri = contextPath+"/getSuppliers/new?name="+supplierName+"&companyId="+selectedCompanyId;
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

function getSupplier() {
	$("#spanCompanyError").text("");
	$("#spanSupplierError").text("");
	if($.trim($("#supplierId").val()) != ""){
		var supplierName = $.trim($("#supplierId").val());
		var uri = contextPath + "/getSuppliers/new?name="+encodeURIComponent(supplierName)
				+"&companyId="+$("#companyId").val()+"&isExact=true";
		$.ajax({
			url: uri,
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#hdnSupplierId").val(supplier[0].id);
					$("#supplierId").val(supplier[0].name);
					filterSupplierAccts();
				}  else {
					$("#spanSupplierError").text("Invalid supplier name.");
				}
			},
			dataType: "json"
		});
	} else {
		clearFields();
	}
}

function clearFields() {
	$("#hdnSupplierId").val("-1");
	$("#supplierAcctId").empty();
	$("#supplierAcctId").append("<option selected='selected' value=-1>ALL</option>");
}

function filterSupplierAccts() {
	$("#supplierAcctId").empty();
	var selectedCompanyId = $("#companyId").val();
	var supplierId = Number($("#hdnSupplierId").val()) != 0 ? $("#hdnSupplierId").val() : -1;
	var uri = contextPath+"/getApSupplierAccts?supplierId="+supplierId+"&companyId="+selectedCompanyId;
	var optionParser = {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	loadPopulate (uri, true, null, "supplierAcctId", optionParser, null);
}

function loadPaymentStatus(){
	var uri = contextPath+"/getPaymentStatus";
	$("#paymentStatusId").empty();
	var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["description"];
			}
		};
		loadPopulate (uri, true, null, "paymentStatusId", optionParser, null);
}

function filterBankAccounts() {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getBankAccounts?companyId="+companyId;
	$("#bankAccountId").empty();
	var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
	loadPopulate (uri, true, null, "bankAccountId", optionParser, null);
}

function companyOnChange() {
	loadDivisions();
	filterBankAccounts();
	// filterSuppliers();
	// filterSupplierAccts();
	loadPaymentStatus();
	$("#supplierId").val("");
	$("#hdnSupplierId").val("-1");
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId"  onchange="companyOnChange()" class="frmSelectClass">
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
		<td class="value">
			<select id="slctDivisionId" class="frmSelectClass"></select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value">
			<span id="spanDivisionError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Bank Account </td>
		<td>
			<select id="bankAccountId"  class="frmSelectClass">
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Check No.</td>
		<td>
			<input type="text" id="checkNoFrom" size="10%" class="dateClass2"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="checkNoTo" size="10%" class="dateClass2"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Supplier</td>
		<td><input type="hidden" id="hdnSupplierId"/>
		<input class="input" id="supplierId" onkeydown="filterSuppliers();" onkeyup="filterSuppliers();" onblur="getSupplier();"></td>
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
		<td class="title2">Payment Date </td>
		<td class="tdDateFilter">
			<input type="text" id="payDateFrom" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('payDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="payDateTo" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('payDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Check Date </td>
		<td class="tdDateFilter">
			<input type="text" id="checkDateFrom" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('checkDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="checkDateTo" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('checkDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Amount </td>
		<td class="tdDateFilter">
			<input type="text" id="amountFrom" class="dateClass2">
			<span style="font-size: small;">To</span>
			<input type="text" id="amountTo" class="dateClass2">
		</td>
	</tr>
	<tr>
		<td class="title2">Voucher No.</td>
		<td>
			<input type="text" id="voucherNoFrom" size="10%" class="dateClass2"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="voucherNoTo" size="10%" class="dateClass2"/>
		</td>

	<tr>
		<td class="title2">Payment Status </td>
		<td>
			<select id="paymentStatusId"  class="frmSelectClass"></select>
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
		<td colspan="3"><input type="button" value="Generate" onclick="searchPayments()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>