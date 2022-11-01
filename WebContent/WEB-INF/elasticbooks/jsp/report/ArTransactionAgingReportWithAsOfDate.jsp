<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Ar Transaction Aging Report with additional as of date parameter.
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
var selectedCompanyId = 0;
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#asOfDate").val(newDate);
	filterCustomerAccts();
	getArAccounts();
});

function getArAccounts() {
	var divisionId = $("#divisionId").val();
	var companyId = $("#companyId").val();
	if(companyId != "") {
		var uri = contextPath+"/customerBalancesSummary/getArAccounts?companyId="+companyId+"&divisionId="+divisionId;
		$("#slctClassificationId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject;
			},

			getLabel: function (rowObject) {
				return rowObject;
			}
		};
		loadPopulate (uri, true, null, "slctClassificationId", optionParser, null);
	}
}

function filterCustomerAccts() {
	selectedCompanyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var customerId = $("#customerId").val();
	$("#customerAcctId").empty();
	var uri = contextPath+"/getArCustomerAccounts?arCustomerId="+customerId+"&companyId="+selectedCompanyId
			+(divisionId == -1 ? "" : "&divisionId="+divisionId);
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

function companyOnChange() {
	clearSearchFilters();
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
	$("#customerAcctId").val("");
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
		$("#hdncustomerId").val("");
		$("#customerIdError").text("");
		$("#customerTypeId").empty();
		$("#arCustomerAcctId").empty();
	}
}

function getCommonParam() {
	var companyId = $("#companyId option:selected").val();
	var divisionId = $("#divisionId").val();
	var transactionClassificationId = $("#transactionClassificationId").val();
	var customerId = $("#customerId").val();
	if(customerId == "" || customerId == null){
		 customerId = -1;
	}

	var customerAcctId = $("#customerAcctId option:selected").val();
	var showTrans = $("#showTrans option:selected").val();
	var ageBasisVal = $("#ageBasis option:selected").val();
	var asOfDate = $("#asOfDate").val();
	var formatType = $("#formatType").val();
	var classificationId = encodeURIComponent($("#slctClassificationId option:selected").val());

	var url = "?typeId=2&companyId="+companyId +"&customerId="+customerId+
		"&customerAcctId="+customerAcctId + "&showTrans="+showTrans+"&ageBasis="+ ageBasisVal +
		"&asOfDate=" + asOfDate + "&formatType=" + formatType + "&divisionId=" + divisionId+
		"&transactionClassificationId="+ transactionClassificationId+"&classificationId="+classificationId ;
	return url;
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
		<td><select id="companyId" onchange="companyOnChange(); getArAccounts();" class="frmSelectClass">
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
			<select id="divisionId" class="frmSelectClass" onchange="divisionOnChange();getArAccounts();">
				<option selected='selected' value=-1>All</option>
				<c:forEach var="division" items="${division}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Classification </td>
		<td>
			<select id="slctClassificationId" class="frmSelectClass">
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Transaction Type </td>
		<td>
			<select id="transactionClassificationId"  class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="transactionClassification" items="${transactionClassifications}">
					<option value="${transactionClassification.id}">${transactionClassification.name}</option>
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
				<option value=1>Due Date</option>
				<option value=2>Transaction Date</option>
				<option value=3 selected='selected'>GL Date</option>
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