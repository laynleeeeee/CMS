<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AP Invoice Aging Report.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/commonUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/ZeroClipboard.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/TableTools.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
var selectedCompanyId = 0;
$(document).ready(function() {
	companyOnChange();
});

function filterSuppliers() {
	$("#spanCompanyError").text("");
	$("#spanSupplierError").text("");
	selectedCompanyId = $("#companyId").val();
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
	var supplierId = Number($("#hdnSupplierId").val()) != 0 ? $("#hdnSupplierId").val() : -1;
	if(supplierId != -1) {
	$("#supplierAcctId").empty();
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
	} else {
		appendAll();
	}
}

function appendAll() {
	$("#supplierAcctId").empty();
	$("#supplierAcctId").append("<option selected='selected' value=-1>ALL</option>");
}

function getCommonParam() {
	var companyId = $("#companyId option:selected").val();
	var invoiceTypeId = $("#invoiceTypeId option:selected").val();
	var supplierId = $("#hdnSupplierId").val();
	var supplierAcctId = $("#supplierAcctId").val();
	var showInvoices = $("#showInv option:selected").val();
	var ageBasisVal = $("#ageBasis option:selected").val();
	var formatType = $("#formatType").val();

	var url =  "?typeId=1&companyId="+companyId +"&invoiceTypeId="+invoiceTypeId+"&supplierId="+supplierId+
		"&supplierAccountId="+supplierAcctId + "&showInvoices="+showInvoices+"&ageBasis="+ ageBasisVal +
		"&formatType=" + formatType;
	return url;
}

function generateInvoiceAging(){
	var companyId = $("#companyId option:selected").val();
	var supplierId = $("#supplierId").val();
	if(supplierId == "") {
		$("#hdnSupplierId").val(-1);
		$("#spanSupplierError").text("");
	}
	if(companyId == -1) {
		$("#spanCompanyError").text("Company is required");
		$("#iFrame").attr('src', "");
	} if(companyId != -1) {
		var url = contextPath + "/invoiceAging/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}

function companyOnChange() {
	filterSuppliers();
	filterSupplierAccts();
	$("#hndSupplierId").val("");
	$("#supplierId").val("");
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
		<td class="title2">Show Invoices? </td>
		<td>
			<select id="showInv" class="frmSelectClass">
				<option selected='selected' value=true>Yes</option>
				<option value=false>No</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Age Basis </td>
		<td>
			<select id="ageBasis" class="frmSelectClass">
				<option selected='selected' value=1>Invoice Date</option>
				<option value=2>GL Date</option>
			</select>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateInvoiceAging()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>