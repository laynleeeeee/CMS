<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Customer Account History Report.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
var selectedCompanyId = -1;
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function clearSpanError() {
	$("#spanCompanyError").text("");
	$("#spanCustomerError").text("");
	$("#spanCustomerAcctError").text("");
	$("#spanDateError").text("");
}

function generateReport() {
	var companyId = $("#companyId option:selected").val();
	var divisionId = $("#divisionId").val();
	var customerAcctId = $("#customerAcctId").val();
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var currencyId = $("#currencyId").val();
	var formatType = $("#formatType").val();

	if (companyId == -1) {
		$("#spanCompanyError").text("Company is required.");
		$("#iFrame").attr('src', "");
	}
	if($("#customerId").val() == "" && $("#customerId").val() == "") {
		$("#spanCustomerError").text("Customer is required.");
		$("#iFrame").attr('src', "");
	}
	if(customerAcctId == -1) {
		$("#spanCustomerAcctError").text("Customer Account is required.");
		$("#iFrame").attr('src', "");
	}
	if(dateFrom == "" || dateTo == "") {
		$("#spanDateError").text("Dates from and to are required.");
		$("#iFrame").attr('src', "");
	}

	if(companyId != -1 && customerAcctId != -1 && $("#customerId").val() != "" && dateFrom != "" && dateTo != "") {
		var url = contextPath + "/customerAccountHistory/generatePDF?companyId="+companyId
				+"&customerAcctId="+customerAcctId+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType="+formatType
				+"&divisionId="+divisionId+"&currencyId="+currencyId;
		$('#iFrame').attr('src',url);
		$('#iFrame').load();
		clearSpanError();
	}
}

function showCustomers() {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $("#customerName").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)
			+"&companyId="+companyId;
	var divisionId = $("#divisionId").val();
	if (divisionId != "" && divisionId != "undefined" && divisionId != -1) {
		uri += "&divisionId="+divisionId;
	}
	$("#customerName").autocomplete({
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

function getCustomer() {
	var companyId = $("#companyId").val();
	var customerName = $("#customerName").val();
	var divisionId = $("#divisionId").val();
	if (customerName != "") {
		$("#spanCustomerError").text("");
		var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&isExact=true"+"&companyId="+companyId;
		if (divisionId != "" && divisionId != "undefined" && divisionId != -1) {
			uri += "&divisionId="+divisionId;
		}
		$.ajax({
			url: uri,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					var customerId = customer[0].id;
					$("#customerId").val(customerId);
					$("#customerName").val(customer[0].name);
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#customerId").val("");
					$("#arCustomerAcctId").empty();
				}
				filterCustomerAccts();
			},
			dataType: "json"
		});
	} else {
		$("#hdnArCustomerId").val("");
		$("#arCustomerIdError").text("");
		$("#customerTypeId").empty();
		$("#arCustomerAcctId").empty();
	}
}

function filterCustomerAccts() {
	$("#customerAcctId").empty();
	if($.trim($("#customerName").val()) == "") {
		$("#customerId").val("");
		clearAndAddEmpty ();
	} else {
		selectedCompanyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var selectedCustomerId = $("#customerId").val();
		var uri = contextPath+"/getArCustomerAccounts?arCustomerId="+selectedCustomerId+"&companyId="+selectedCompanyId
		+(divisionId == -1 ? "" : "&divisionId="+divisionId);;
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		postHandler = {
				doPost: function(data) {
					// This is to remove any duplication.
					var found = [];
					$("#customerAcctId option").each(function() {
						if($.inArray(this.value, found) != -1)
							$(this).remove();
						found.push(this.value);
					});
					if (parseInt($("#customerAcctId > option").length) == 0)
						clearAndAddEmpty ();
				}
		};
		loadPopulate (uri, false, null, "customerAcctId", optionParser, postHandler);
	}
}

function companyOnChange() {
	clearSearchFilters();
	clearAndAddEmpty();
	$("#divisionId").val("");
}

function clearSearchFilters() {
	$("#customerAcctId").val("");
	$("#customerId").val("");
	$("#customerName").val("");
}

function divisionOnChange() {
	clearSearchFilters();
	filterCustomerAccts();
}
function clearAndAddEmpty () {
	$("#customerAcctId").empty();
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" class="frmSelectClass" onchange="companyOnChange();">
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
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" class="frmSelectClass" onchange="divisionOnChange();">
				<option selected='selected' value=-1>All</option>
				<c:forEach var="division" items="${division}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer</td>
		<td>
			<input id="customerName" class="input" onkeypress="showCustomers();" onblur="getCustomer();">
			<input type="hidden" id="customerId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCustomerError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer Account</td>
		<td>
			<select id="customerAcctId" class="frmSelectClass">
				<option selected='selected' value="">Please select a customer account</option>
				<c:forEach var="customerAcct" items="${customerAccts}">
					<option value="${customerAcct.id}">${customerAcctt.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCustomerAcctError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Date </td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDateError" class="error"></span>
		</td>
	</tr>
		<tr>
		<td class="title2">Currency</td>
		<td>
			<select id="currencyId" class="frmSelectClass" onchange="">
				<c:forEach var="currency" items="${currency}">
					<option value="${currency.id}">${currency.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
			<td class="title2">Format:</td>
			<td class="value">
				<select id="formatType" class="frmSelectClass">
						<option value="pdf">PDF</option>
						<option value="xls">EXCEL</option>
				</select>
			</td>
		</tr>
	<tr>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
<hr class="thin2">
</body>
</html>