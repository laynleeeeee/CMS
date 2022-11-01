<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Ar Transaction Aging Report.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/retrieveArCustomer.js"></script>
<script type="text/javascript">
var selectedCompanyId = 0;
$(document).ready(function() {
	filterCustomerAccts();
});

function filterCustomerAccts() {
	selectedCompanyId = $("#companyId").val();
	var customerId = $("#arCustomerId").val();
	$("#customerAcctId").empty();
	var uri = contextPath+"/getArCustomerAccounts?arCustomerId="+customerId+"&companyId="+selectedCompanyId;
	var optionParser = {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	loadPopulate (uri, true, null, "customerAcctId", optionParser, null);
}

function clearCustomer () {
	$("#customerId").val("");
	$("#ArCustomerId").val("");
	$("#customerAcctId").empty();
	filterCustomerAccts();
}

function getArCustomer () {
	getCustomer ("customerId", "arCustomerId", "companyId",
			"spanCustomerError", "spanCompanyError", "true");
	filterCustomerAccts();
}

function showArCustomers () {
	showCustomers ("customerId", "arCustomerId", "companyId",
			"spanCustomerError", "spanCompanyError", "false");
}

function getCommonParam() {
	var companyId = $("#companyId option:selected").val();
	var transTypeId = $("#transTypeId option:selected").val();
	var customerId = $("#arCustomerId").val();
	if(customerId == "" || customerId == null){
		 customerId = -1;
	}
		
	var customerAcctId = $("#customerAcctId option:selected").val();
	var showTrans = $("#showTrans option:selected").val();
	var ageBasisVal = $("#ageBasis option:selected").val();
	var formatType = $("#formatType").val();
	
	return "?typeId=1&companyId="+companyId +"&transTypeId="+transTypeId+"&customerId="+customerId+
	"&customerAcctId="+customerAcctId + "&showTrans="+showTrans+"&ageBasis="+ ageBasisVal +
	"&formatType=" + formatType;
}

function generateTransactionAging(){
	var companyId = $("#companyId option:selected").val();
	if(companyId == -1){
		$("#spanCompanyError").text("Company is required");
	}else{
		$("#spanCompanyError").text("");
		var url = contextPath + "/transactionAging/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}
</script>

</head>
<body>
<table>
	<tr>
		<td class="title2">Company</td>
		<td><select id="companyId" onchange="clearCustomer();" class="frmSelectClass">
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
		<td class="title2">Transaction Type</td>
		<td>
			<select id="transTypeId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="invoiceType" items="${invoiceTypes}">
					<option value="${invoiceType.id}">${invoiceType.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer</td>
		<td>
			<input id="customerId" class="input" onkeydown="showArCustomers();" onkeyup="showArCustomers();" onblur="getArCustomer();">
			<input type="hidden" id="arCustomerId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCustomerError" class="error" style="margin-left: 12px;"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer Account</td>
		<td>
			<select id="customerAcctId" class="frmSelectClass"></select>
		</td>
	</tr>
	<tr>
		<td class="title2">Show Transaction?</td>
		<td>
			<select id="showTrans" class="frmSelectClass">
				<option selected='selected' value=true>Yes</option>
				<option value=false>No</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Age Basis </td>
		<td>
			<select id="ageBasis" class="frmSelectClass">
				<option selected='selected' value=1>Due Date</option>
				<option value=2>Transaction Date</option>
				<option value=3>GL Date</option>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateTransactionAging()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>