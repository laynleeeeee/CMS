
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--

	Description: Delivery Receipt Register Report.
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
<title>Delivery Receipt Register</title>
<script type="text/javascript">
var selectedCompanyId = -1;
$(document).ready (function () {
	var newDateFrom = parseServerDate(new Date);
	$("#drDateFrom").val(newDateFrom);
	var newDateTo = parseServerDate(new Date);
	$("#drDateTo").val(newDateTo);
});

function getCommonParam () {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var customerId = $("#customerId").val();
	var customerAcctId = $("#customerAcctId option:selected").val();
	var soNumber = processSearchName($("#soNumber").val());
	var poPcrNumber = processSearchName($("#poPcrNumber").val());
	var drDateFrom = $("#drDateFrom").val();
	var drDateTo = $("#drDateTo").val();
	var drNumberFrom = $("#drNumberFrom").val();
	var drNumberTo = $("#drNumberTo").val();
	var deliveryReceiptStatus = $("#deliveryReceiptStatus option:selected").val();
	var formatType = $("#formatType").val();

	return "?companyId=" + companyId + "&customerId="+ customerId + "&divisionId=" + divisionId
			+ "&customerAcctId="+ customerAcctId + "&soNumber="+ soNumber
			+ "&poPcrNumber="+ poPcrNumber + "&drDateFrom="+ drDateFrom
			+ "&drDateTo="+ drDateTo + "&drNumberFrom="+ drNumberFrom
			+ "&drNumberTo="+ drNumberTo + "&deliveryReceiptStatus="+ deliveryReceiptStatus
			+ "&formatType=" + formatType;
}

function generateTransactions() {
	var drNumberFrom = $.trim($("#drNumberFrom").val()) != "" ? $.trim($("#drNumberFrom").val()) : 0;
	var drNumberTo = $.trim($("#drNumberTo").val()) != "" ? $.trim($("#drNumberTo").val()) : 0;
	var drDateFrom = $.trim($("#drDateFrom").val());
	var drDateTo = $.trim($("#drDateTo").val());
	var isValidFilters = true;
	if ($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Company is required.");
		isValidFilters = false;
	} else if($("#customerId").val() == "") {
		if($("#customerId").val() != "") {
					$("#spanCustomerError").text("Invalid customer");
				} else {
					$("#customerId").val(-1);
					}
		isValidFilters = false;
	} else if (Number(drNumberFrom) > Number(drNumberTo)) {
		$("#spanDrNumberError").text("Invalid delivery receipt number range.");
		isValidFilters = false;
	} else if (drDateFrom == "" && drDateTo == "") {
		$("#spanDateError").text("Date range is required.");
		isValidFilters = false;
	} else if (drDateFrom == "" || drDateTo == "") {
		$("#spanDateError").text("Invalid date range.");
		isValidFilters = false;
	} else if (new Date(drDateFrom) > new Date(drDateTo)) {
		$("#spanDateError").text("Invalid date range.");
		isValidFilters = false;}
	var url = "";
	if (isValidFilters) {
		$("#spanCompanyError").text("");
		$("#spanCustomerError").text("");
		$("#spanDrNumberError").text("");
		$("#spanDateError").text("");
		url = contextPath + "/deliveryReceiptRegister/generateReport" + getCommonParam();
	}
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
}



function filterCustomerAccts() {
	$("#customerAcctId").empty();
	if($.trim($("#customerName").val()) == "") {
		$("#customerId").val("") ;
		clearAndAddEmpty();
	} else {
		selectedCompanyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var selectedCustomerId = $("#customerId").val();
		var uri = contextPath+"/getArCustomerAccounts?arCustomerId="+selectedCustomerId+"&companyId="+selectedCompanyId
					+(divisionId == -1 ? "" : "&divisionId="+divisionId);
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
						found.push("ALL");
						found.push(this.value);
					});
				}
		};
		loadPopulate (uri, true, null, "customerAcctId", optionParser, postHandler);
	}
}

function clearAndAddEmpty () {
	$("#customerAcctId").empty();
	var option = "<option selected='selected' value='-1'>ALL</option>";
	$("#customerAcctId").append(option);
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

function companyOnChange() {
	clearSearchFilters();
	clearAndAddEmpty();
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

function customerOnChange() {
	$("#customerAcctId").empty();
	var option = "<option selected='selected' value='-1'>ALL</option>";
	$("#customerAcctId").append(option);
	$("#customerId").val("");

}

$(function () {
	$("#drNumberFrom").bind("keyup keydown", function() {
		inputOnlyNumeric("drNumberFrom");
	});

	$("#drNumberTo").bind("keyup keydown", function() {
		inputOnlyNumeric("drNumberTo");
	});
});

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
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer</td>
		<td>
			<input id="customerName" class="input" onkeypress="showCustomers();" onblur="getCustomer();"
			onchange="customerOnChange();">
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
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="customerAcct" items="${customerAccts}">
					<option value="${customerAcct.id}">${customerAcctt.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">SO Number </td>
		<td class="tdDateFilter">
			<input type="text" id="soNumber" class="input">
		</td>
	</tr>
		<tr>
		<td class="title2">PO/PCR Number </td>
		<td class="tdDateFilter">
			<input type="text" id="poPcrNumber" class="input">
		</td>
	</tr>
		<tr>
		<td class="title2">DR Number </td>
		<td class="tdDateFilter">
			<input type="text" id="drNumberFrom" class="dateClass2">
			<span style="font-size: small;">To</span>
			<input type="text" id="drNumberTo" class="dateClass2">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="1"><span id="spanDrNumberError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">DR Date </td>
		<td class="tdDateFilter">
			<input type="text" id="drDateFrom" maxlength="10" class="dateClass2"
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('drDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="drDateTo" maxlength="10" class="dateClass2"
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('drDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="1"><span id="spanDateError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Status</td>
		<td>
			<select id="deliveryReceiptStatus" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="fs" items="${formStatuses}">
					<option value="${fs.id}">${fs.description}</option>
				</c:forEach>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateTransactions()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>