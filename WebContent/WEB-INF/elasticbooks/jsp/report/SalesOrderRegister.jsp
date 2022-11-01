<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Sales Order Register main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	appendAll();
});

function getArCustomer() {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $.trim($("#customerName").val());
	var divisionId = $("#divisionId").val();
	if (customerName != "") {
		var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&isExact=true"+"&companyId="+companyId;
			+(divisionId != -1 ? "&divisionId="+divisionId : "");
		$.ajax({
			url: uri,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					$("#arCustomerId").val(customer[0].id);
					$("#customerName").val(customer[0].name);
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#arCustomerId").val("");
					$("#arCustomerAccountId").empty();
				}
				filterArcustomerAccount();
			},
			dataType: "json"
		});
	} else {
		$("#arCustomerIdError").text("");
		$("#arCustomerId").val("");
		appendAll();
	}
}

function clearCustomerAndAcct() {
	$("#arCustomerId").val("");
	$("#arCustomerAccountId").empty();
	$("#arCustomerAccountId").append("<option value=-1>ALL</option>");
}

function showArCustomers () {
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $("#customerName").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)+"&companyId="+companyId
		+(divisionId != -1 ? "&divisionId="+divisionId : "");
	$("#customerName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#arCustomerId").val(ui.item.id);
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

function filterArcustomerAccount() {
	$("#arCustomerAccountId").empty();
	if($.trim($("#customerName").val()) == "") {
		$("#arCustomerId").val("");
		clearCustomerAndAcct ();
	}else{
		selectedCompanyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var selectedCustomerId = $("#arCustomerId").val();
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
		var items = 0;
		postHandler = {
				doPost: function(data) {
					// This is to remove any duplication.
					var found = [];
					$("#arCustomerAccountId option").each(function() {
						if($.inArray(this.value, found) != -1)
							$(this).remove();
						found.push(this.value);
					});
					items = parseInt($("#arCustomerAccountId > option").length);
					if (items == 1 && $("#arCustomerAccountId").val() == -1)
						clearCustomerAndAcct ();
				}
		};
		loadPopulate (uri, true, null, "arCustomerAccountId", optionParser, postHandler);
	}
}

function generateSORegReport() {
	var soFromNo = $.trim($("#soFrom").val()) != "" ? $.trim($("#soFrom").val()) : 0;
	var soToNo = $.trim($("#soTo").val()) != "" ? $.trim($("#soTo").val()) : 0;
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var isValidFilters = true;
	if ($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Company is required.");
		isValidFilters = false;
	} else if ($("#spanCustomerError").text() != "") {
		isValidFilters = false;
	} else if (Number(soFromNo) > Number(soToNo)) {
		$("#spanSoNumberError").text("Invalid sales order number range.");
		isValidFilters = false;
	} else if (dateFrom == "" && dateTo == "") {
		$("#spanDateError").text("Date range is required.");
		isValidFilters = false;
	} else if (dateFrom == "" || dateTo == "") {
		$("#spanDateError").text("Invalid date range.");
		isValidFilters = false;
	} else if (new Date(dateFrom) > new Date(dateTo)) {
		$("#spanDateError").text("Invalid date range.");
		isValidFilters = false;}
	var url = "";
	if (isValidFilters) {
		$("#spanCompanyError").text("");
		$("#spanCustomerError").text("");
		$("#spanSoNumberError").text("");
		$("#spanDateError").text("");
		url = contextPath + "/salesOrderRegister/generatePDF"+getCommonParam();
	}
	$('#reportSalesOrder').attr('src',url);
	$('#reportSalesOrder').load();
}

function appendAll() {
	$("#arCustomerAccountId").empty();
	$("#arCustomerAccountId").append("<option selected='selected'  value=-1>ALL</option>");
}

function getCommonParam() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var arCustomerId = $("#arCustomerId").val();
	var arCustomerAccountId = $("#arCustomerAccountId").val();
	var soType = $("#soType").val();
	var soFrom = $("#soFrom").val();
	var soTo = $("#soTo").val();
	var popcrNo = $("#popcrNo").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var statusId = $("#statusId").val();
	var formatType = $("#formatType").val();
	return "?companyId=" + companyId  + "&arCustomerId=" + (arCustomerId != "" ? arCustomerId : -1)
			+ "&arCustomerAccountId=" + arCustomerAccountId + "&soType=" + soType + "&soFrom=" + soFrom
			+ "&soTo=" + soTo + "&dateFrom=" + dateFrom + "&dateTo=" + dateTo+ "&popcrNo=" + popcrNo
			+ "&statusId=" + statusId + "&formatType=" + formatType+ "&divisionId="+ divisionId;
}

function selectOnChange() {
	clearCustomerAndAcct();
	$("#customerName").val("");
	filterArcustomerAccount();
}
</script>
</head>
<body>
	<table>

		<tr>
			<td class="title2">Company</td>
			<td><select id="companyId" class="frmSelectClass" onchange="selectOnChange();">
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanCompanyError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Division</td>
			<td><select id="divisionId" class="frmSelectClass" onchange="selectOnChange();">
					<option selected='selected' value=-1>All</option>
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Customer</td>
			<td>
				<input id="customerName" class="input" onkeypress="showArCustomers();" onblur="getArCustomer();">
				<input type="hidden" id="arCustomerId">
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanCustomerError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Customer Account</td>
			<td><select id="arCustomerAccountId" class="frmSelectClass"></select></td>
		</tr>
		<tr>
			<td class="title2">SO Type</td>
			<td>
			<select id="soType" class="frmSelectClass">
				<option value=-1 selected="selected">ALL</option>
				<option value=1>PO</option>
				<option value=2>PCR</option>
			</select>
		</td>
		</tr>
		<tr>
			<td class="title2">SO Number </td>
			<td class="tdDateFilter">
				<input type="text" id="soFrom" onblur = "inputOnlyNumeric('soFrom');" maxlength="10" class="dateClass2">
				<span style="font-size: small;">To</span>
				<input type="text" id="soTo" onblur = "inputOnlyNumeric('soTo');" maxlength="10" class="dateClass2">
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanSoNumberError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">PO Number</td>
			<td>
				<input type="text" id="popcrNo" class="input">
			</td>
		</tr>
		<tr>
			<td class="title2">SO Date: </td>
			<td class="tdDateFilter">
				<input type="text" id="dateFrom" maxlength="10" class="dateClass2" value="${currentDate}"
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
							style="float: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="dateTo" maxlength="10" class="dateClass2" value="${currentDate}"
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
							style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanDateError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Status</td>
			<td><select id="statusId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="stat" items="${deliveryStatuses}">
						<option value="${stat.value}">${stat.description}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Format:</td>
			<td class="value"><select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select></td>
		</tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate"
				onclick="generateSORegReport()" /></td>
		</tr>
	</table>
	<div>
		<iframe id="reportSalesOrder"></iframe>
	</div>
	<hr class="thin2">
</body>
</html>