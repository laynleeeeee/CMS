<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: Sales Delivery Efficiency report filter/generator jsp page
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
var currentMonth = $(currentMonth).val();
var currentYear = $(currentYear).val();

function generateReport() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var customerId = $("#customerId").val();
	var monthFrom = $("#slctMonthFrom").val();
	var yearFrom = $("#slctYearFrom").val();
	var monthTo = $("#slctMonthTo").val();
	var yearTo = $("#slctYearTo").val();
	var formatType = $("#formatType").val();
	var isValidFilter = true;
	if (companyId < 0) {
		$("#companyError").text("Company is required.");
		isValidFilter = false;
	}
	//Customer
	if($("#spanCustomerError").text().trim() != "") {
		isValidFilter = false;
	}
	if(yearFrom > yearTo){
		$("#dateError").text("Invalid date range.");
		isValidFilter = false;
	}
	if((yearFrom == yearTo) && (parseInt(monthTo) < parseInt(monthFrom))){
		$("#dateError").text("Invalid date range.");
		isValidFilter = false;
	}

	var url = "";
	if (isValidFilter) {
		clearSpanMsg();
		url = contextPath+"/salesDeliveryEfficiency/generateReport?companyId="+companyId
				+"&divisionId="+divisionId+"&customerId="+customerId+"&monthFrom="+monthFrom+"&yearFrom="+yearFrom+"&monthTo="+monthTo
				+"&yearTo="+yearTo+"&formatType="+formatType;
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}

function companyDivOnChange() {
	$("#iFrame").attr('src', "");
	clearCustomer();
	clearSpanMsg();
}

function clearCustomer() {
	$("#customerId").val();
	$("#customerName").val("");
}

function clearSpanMsg() {
	$("#companyError").text("");
	$("#dateError").text("");
	$("#spanCustomerError").text("");
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
	$("#spanCustomerError").text("");
	if (customerName != "") {
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
				}
			},
			dataType: "json"
		});
	} else {
		$("#customerId").val("");
	}
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company</td>
		<td>
			<select id="companyId" onchange="companyDivOnChange();" class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="companyError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" onchange="companyDivOnChange();" class="frmSelectClass">
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
		<td  class="title2">Month</td>
		<td>
			<select id="slctMonthFrom" class="frmSelectClass" style="width: 165px">
				<c:forEach var="mm" items="${months}">
					<option value="${mm.month}"
					<c:if test="${mm.month eq currentMonth}">selected="selected"</c:if>
					>${mm.name}</option>
				</c:forEach>
			</select>
			<span style="font-size: small;">Year</span>
			<select id="slctYearFrom"  class="frmSelectClass" style="width: 150px">
				<c:forEach var="yy" items="${years}">
					<option value="${yy}"
					<c:if test="${yy eq currentYear}">selected="selected"</c:if>
					>${yy}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td  class="title2">To</td>
		<td>
			<select id="slctMonthTo" class="frmSelectClass" style="width: 165px">
				<c:forEach var="mm" items="${months}">
					<option value="${mm.month}"
					<c:if test="${mm.month eq currentMonth}">selected="selected"</c:if>
					>${mm.name}</option>
				</c:forEach>
			</select>
			<span style="font-size: small;">Year</span>
			<select id="slctYearTo"  class="frmSelectClass" style="width: 150px">
				<c:forEach var="yy" items="${years}">
					<option value="${yy}"
					<c:if test="${yy eq currentYear}">selected="selected"</c:if>
					>${yy}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="dateError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Format</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
			</select>
		</td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>