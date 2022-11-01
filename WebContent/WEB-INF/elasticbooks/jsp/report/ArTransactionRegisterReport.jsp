<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- 

	Description: AR Transaction Register Report.
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
<title>AR Transaction Register Report</title>
<script type="text/javascript">
var selectedCompanyId = -1;
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#asOfDate").val(newDate);
});

function getCommonParam () {
	var companyId = $("#companyId").val();
	var transactionClassificationId = $("#transactionClassificationId").val();
	var customerId = $("#customerId").val();
	var customerAcctId = $("#customerAcctId option:selected").val();
	var transactionNumber = processSearchName($("#transactionNumber").val());
	var transactionDateFrom = $("#transactionDateFrom").val();
	var transactionDateTo = $("#transactionDateTo").val();
	var glDateFrom = $("#glDateFrom").val();
	var glDateTo = $("#glDateTo").val();
	var dueDateFrom = $("#dueDateFrom").val();
	var dueDateTo = $("#dueDateTo").val();
	var amountFrom = $("#amountFrom").val();
	var amountTo = $("#amountTo").val();
	var sequenceNoFrom = $("#sequenceNoFrom").val();
	var sequenceNoTo = $("#sequenceNoTo").val();
	var transactionStatus = $("#transactionStatusId option:selected").val();
	var paymentStatusId = $("#paymentStatusId option:selected").val();
	var asOfDate = $("#asOfDate").val();
	var formatType = $("#formatType").val();
	var divisionId = $("#divisionId").val();

	return "?companyId=" + companyId + "&transactionClassificationId="+ transactionClassificationId 
			+ "&customerId=" + customerId
			+ "&customerAcctId=" + customerAcctId
			+ "&transactionNumber=" + transactionNumber
			+ "&transactionDateFrom=" + transactionDateFrom
			+ "&transactionDateTo=" + transactionDateTo
			+ "&glDateFrom=" + glDateFrom + "&glDateTo=" + glDateTo
			+ "&dueDateFrom=" + dueDateFrom + "&dueDateTo=" + dueDateTo
			+ "&amountFrom=" + amountFrom + "&amountTo=" + amountTo
			+ "&sequenceNoFrom=" + sequenceNoFrom + "&sequenceNoTo="
			+ sequenceNoTo + "&transactionStatusId="
			+ transactionStatus + "&paymentStatusId=" + paymentStatusId
			+ "&asOfDate=" + asOfDate + "&formatType=" + formatType
			+ "&divisionId=" + divisionId;
}

function generateTransactions() {
	$("#spanCompanyError").text("");
	$("#spanCustomerError").text("");
	$("#spanDateError").text("");

	if ($("#companyId option:selected").val() == -1) {
		$("#spanCompanyError").text("Company is required.");
	}

	if($("#customerId").val() == "") {
		if($("#customerId").val() != "") {
			$("#spanCustomerError").text("Invalid customer");
		} else {
			$("#customerId").val(-1);
		}
	}

	var asOfDate = $.trim($("#asOfDate").val());
	if (asOfDate == "") {
		$("#spanDateError").text("As of date is required.");
	} else {
		$("#spanDateError").text("");
	}

	if($("#companyId option:selected").val() != -1 && $("#customerId").val() != "" && asOfDate != "") {
		var url = contextPath + "/transactionRegister/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
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

$(function () {
	$("#sequenceNoFrom").bind("keyup keydown", function() {
		inputOnlyNumeric("sequenceNoFrom");
	});

	$("#sequenceNoTo").bind("keyup keydown", function() {
		inputOnlyNumeric("sequenceNoTo");
	});

	$("#amountFrom").live("keydown keyup", function () {
		inputOnlyNumeric($("#amountFrom"));
	});

	$("#amountTo").live("keydown keyup", function () {
		inputOnlyNumeric($("#amountTo"));
	});
});

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
	$("#customerAcctId").val("");
	clearAndAddEmpty();
}

</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" class="frmSelectClass" onchange="companyOnChange();">
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
			<input id="customerName" class="input" onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="getCustomer();"
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
		<td class="title2">Transaction Number </td>
		<td class="tdDateFilter">
			<input type="text" id="transactionNumber" class="dateClass2">
		</td>
	</tr>
	<tr>
		<td class="title2">Transaction Date </td>
		<td class="tdDateFilter">
			<input type="text" id="transactionDateFrom" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('transactionDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="transactionDateTo" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('transactionDateTo')" style="cursor:pointer"
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
			<input type="text" id="amountFrom" class="dateClass2">
			<span style="font-size: small;">To</span>
			<input type="text" id="amountTo" class="dateClass2">
		</td>
	</tr>
	<tr>
		<td class="title2">Sequence Number </td>
		<td class="tdDateFilter">
			<input type="text" id="sequenceNoFrom" class="dateClass2">
			<span style="font-size: small;">To</span>
			<input type="text" id="sequenceNoTo" class="dateClass2">
		</td>
	</tr>
	<tr>
		<td class="title2">Transaction Status</td>
		<td>
			<select id="transactionStatusId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="fs" items="${formStatuses}">
					<option value="${fs.id}">${fs.description}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Payment Status</td>
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
			<span id="spanDateError" class="error"></span>
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