<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Daily cash sales main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/retrieveArCustomer.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#date").val(newDate);
});

function getCommonParam () {
	var companyId = $("#companyId option:selected").val();
	var salesInvoiceNo = processSearchName($("#txtInvoiceNo").val());
	var customerName = processSearchName($("#txtCustomer").val());
	var strDate = $("#date").val();
	return "?companyId="+companyId+"&salesInvoiceNo="+salesInvoiceNo+"&customerName="+
			customerName+"&date="+strDate+"&formatType=pdf";
}

function showArCustomers () {
	showCustomers ("txtCustomer", "arCustomerId", "companyId",
			"spanCustomerError", "spanCompanyError", "false");
}

function getArCustomers () {
	getCustomer ("txtCustomer", "arCustomerId", "companyId",
			"spanCustomerError", "spanCompanyError", "true");
	filterCustomerAccts();
}

function searchDailySales() {
	if($.trim($("#date").val()) == "") {
		$("#spanDateError").text("Date is required.");
	}else {
		var url = contextPath + "/dailySales/generate"+getCommonParam();
		$('#reportDailyCashSale').attr('src',url);
		$('#reportDailyCashSale').load();
	}
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Branch </td>
		<td>
			<select id="companyId" class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Invoice No.:</td>
		<td>
			<input id="txtInvoiceNo" class="input" />
		</td>
	</tr>
	
	<tr>
		<td class="title2">Customer Name:</td>
		<td>
			<input id="txtCustomer" class="input" 
				onkeydown="showArCustomers();" onkeyup="showArCustomers();" onblur="getArCustomers();" />
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCustomerError" class="error" style="margin-left: 12px;"></span>
		</td>
	</tr>
	
	<tr>
		<td class="title2">Date </td>
		<td class="tdDateFilter">
			<input type="text" id="date" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('date')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDateError" class="error" ></span>
		</td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchDailySales();"/></td>
	</tr>
</table>
<div>
	<iframe id="reportDailyCashSale"></iframe>
</div>
<hr class="thin2">
</body>
</html>