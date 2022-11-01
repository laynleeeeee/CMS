<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: Sales output filter/generator jsp page
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
function generateReport() {
	clearDateValidations();
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var customerId = $("#customerId").val();
	var customerAcctId = $("#customerAcctId").val();
	var salesPersonnelId = $("#salesPersonnelId").val();
	var monthFrom = $("#slctMonthFrom").val();
	var yearFrom = $("#slctYearFrom").val();
	var monthTo = $("#slctMonthTo").val();
	var yearTo = $("#slctYearTo").val();
	var txtPoNumber = $("#txtPoNumber").val();
	var soDateFrom = $("#soDateFrom").val();
	var soDateTo = $("#soDateTo").val();
	var drDateFrom = $("#drDateFrom").val();
	var drDateTo = $("#drDateTo").val();
	var siDateFrom = $("#siDateFrom").val();
	var siDateTo = $("#siDateTo").val();
	var poNumber = $("#txtPoNumber").val();
	var formatType = $("#formatType").val();

	var isValidFilter = !hasInvalidDates();
	if (companyId < 0) {
		$("#companyError").text("Company is required.");
		isValidFilter = false;
	}

	if($("#spnSalesOfficerErr").text().trim() != "") {
		isValidFilter = false;
	}

	if($("#spanCustomerError").text().trim() != "") {
		isValidFilter = false;
	}

	var url = "";
	if (isValidFilter) {
		clearSpanMsg();
		url = contextPath+"/salesOutput/generatePDF?companyId="+companyId+"&divisionId="+divisionId+"&customerId="+customerId
			+"&customerAcctId="+customerAcctId+"&salesPersonnelId="+salesPersonnelId+"&monthFrom="+monthFrom+"&yearFrom="+yearFrom
			+"&monthTo="+monthTo+"&yearTo="+yearTo+"&txtPoNumber="+txtPoNumber+"&soDateFrom="+soDateFrom+"&soDateTo="+soDateTo
			+"&drDateFrom="+drDateFrom+"&drDateTo="+drDateTo+"&ariDateFrom="+siDateFrom+"&ariDateTo="+siDateTo
			+"&poNumber="+poNumber+"&formatType="+formatType;
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}

function clearDateValidations() {
	$("#dateError").text("");
	$("#soDateErr").text("");
	$("#drDateErr").text("");
	$("#siDateErr").text("");
}

function hasInvalidDates() {
	var soDateFrom = $("#soDateFrom").val();
	var soDateTo = $("#soDateTo").val();
	var drDateFrom = $("#drDateFrom").val();
	var drDateTo = $("#drDateTo").val();
	var siDateFrom = $("#siDateFrom").val();
	var siDateTo = $("#siDateTo").val();
	var monthFrom = $("#slctMonthFrom").val();
	var yearFrom = $("#slctYearFrom").val();
	var monthTo = $("#slctMonthTo").val();
	var yearTo = $("#slctYearTo").val();

	var hasInvalidDate = false;

	if(!isValidDate(soDateFrom, soDateTo)) {
		$("#soDateErr").text("Invalid date range.");
		hasInvalidDate = true;
	}

	if(!isValidDate(drDateFrom, drDateTo)) {
		$("#drDateErr").text("Invalid date range.");
		hasInvalidDate = true;
	}

	if(!isValidDate(siDateFrom, siDateTo)) {
		$("#siDateErr").text("Invalid date range.");
		hasInvalidDate = true;
	}

	if(!isValidDate(monthFrom, yearFrom)) {
		$("#dateError").text("Invalid date range.");
		hasInvalidDate = true;
	}

	if(!isValidDate(monthTo, yearTo)) {
		$("#dateError").text("Invalid date range.");
		hasInvalidDate = true;
	}

	if(monthFrom != "" && yearFrom != "" && monthTo == "" && yearTo == "") {
		$("#dateError").text("Invalid date range.");
		hasInvalidDate = true;
	}

	if(monthFrom == "" && yearFrom == "" && monthTo != "" && yearTo != "") {
		$("#dateError").text("Invalid date range.");
		hasInvalidDate = true;
	}

	if(soDateFrom != "" && soDateTo != "" && monthFrom != "" && yearFrom != "" && monthTo != "" && yearTo != "") {
		$("#dateError").text("Either Month-Year or SO date filter should be used.");
		hasInvalidDate = true;
	}

	if(soDateFrom == "" && soDateTo == "" && drDateFrom == "" && drDateTo == "" && siDateFrom == "" && siDateTo == ""
		 && monthFrom == "" && yearFrom == "" && monthTo == "" && yearTo == "") {
		$("#siDateErr").text("At least 1 valid date range is required.");
		hasInvalidDate = true;
	}
	return hasInvalidDate;
}

function isValidDate(dateFrom, dateTo) {
	var isValidDate = true;
	if(dateFrom != "" && dateTo == "") {
		isValidDate = false;
	}

	if(dateFrom == "" && dateTo != "") {
		isValidDate = false;
	}

	if(dateFrom != "" && dateTo != "") {
		if(dateFrom.split("/").length > 1) {
			if(new Date(dateFrom) > new Date(dateTo)) {
				isValidDate = false;
			}
		}
	}
	return isValidDate;
}

function companyDivOnChange() {
	$("#iFrame").attr('src', "");
	clearFields();
	clearSpanMsg();
}

function clearSpanMsg() {
	$("#companyError").text("");
	$("#dateError").text("");
	$("#deliveryDateErr").text("");
	$("#spanCustomerError").text("");
	$("#dateError").text("");
}

function showSalesPersonnels() {
	var companyId = $("#companyId").val();
	var name = $("#txtSalesOfficer").val();
	$("#txtSalesOfficer").autocomplete({
		source: contextPath + "/getSalesPersonnels?name="+processSearchName(name)+"&companyId="+companyId,
		select: function( event, ui ) {
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

function getSalesPersonnel() {
	$("#spnSalesOfficerErr").text("");
	var companyId = $("#companyId").val();
	var name = $("#txtSalesOfficer").val();
	if (name != "") {
		$.ajax({
			url: contextPath + "/getSalesPersonnels?name="+processSearchName(name)
					+"&isExact=true"+"&companyId="+companyId,
			success : function(salesPersonnel) {
				if (salesPersonnel != null && salesPersonnel[0] != undefined) {
					$("#salesPersonnelId").val(salesPersonnel[0].id);
					$("#txtSalesOfficer").val(salesPersonnel[0].name);
				} else {
					$("#spnSalesOfficerErr").text("Invalid sales representative.");
					$("#salesPersonnelId").val("");
				}
			},
			dataType: "json"
		});
	} else {
		$("#salesPersonnelId").val("");
	}
}

function clearFields() {
	$("#salesPersonnelId").val("");
	$("#txtSalesOfficer").val("");
	$("#customerId").val("");
	$("#customerName").val("");
	filterCustomerAccts();
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
	$("#spanCustomerError").text("");
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
		clearAndAddAll ();
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
						clearAndAddAll ();
				}
		};
		loadPopulate (uri, true, null, "customerAcctId", optionParser, postHandler);
	}
}

function clearAndAddAll() {
	$("#customerAcctId").empty();
	var option = "<option selected='selected' value='-1'>ALL</option>";
	$("#customerAcctId").append(option);
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
				<option value="-1">ALL</option>
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
		<td class="title2">Customer Account</td>
		<td>
			<select id="customerAcctId" class="frmSelectClass">
				<option value="-1">ALL</option>
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
		<td class="title2">Requestor</td>
		<td>
			<input id="txtSalesOfficer" class="input" onkeypress="showSalesPersonnels();" onblur="getSalesPersonnel();">
			<input type="hidden" id="salesPersonnelId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spnSalesOfficerErr" class="error"></span>
		</td>
	</tr>
	<tr>
		<td  class="title2">Month</td>
		<td>
			<select id="slctMonthFrom" class="frmSelectClass" style="width: 165px">
				<option value=""></option>
				<c:forEach var="mm" items="${months}">
					<option value="${mm.month}">${mm.name}</option>
				</c:forEach>
			</select>
			<span style="font-size: small;">Year</span>
			<select id="slctYearFrom"  class="frmSelectClass" style="width: 150px">
				<option value=""></option>
				<c:forEach var="yy" items="${years}">
					<option value="${yy}">${yy}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td  class="title2">To</td>
		<td>
			<select id="slctMonthTo" class="frmSelectClass" style="width: 165px">
				<option value=""></option>
				<c:forEach var="mm" items="${months}">
					<option value="${mm.month}">${mm.name}</option>
				</c:forEach>
			</select>
			<span style="font-size: small;">Year</span>
			<select id="slctYearTo"  class="frmSelectClass" style="width: 150px">
				<option value=""></option>
				<c:forEach var="yy" items="${years}">
					<option value="${yy}">${yy}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="dateError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Customer PO No.</td>
		<td>
			<input id="txtPoNumber" class="input">
		</td>
	</tr>
	<tr>
		<td class="title2">SO Date</td>
		<td class="tdDateFilter">
			<input type="text" id="soDateFrom" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('soDateFrom')" style="cursor:pointer"
				style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="soDateTo" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('soDateTo')" style="cursor:pointer"
				style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="soDateErr" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">DR Date</td>
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
		<td><span id="drDateErr" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">SI Date</td>
		<td class="tdDateFilter">
			<input type="text" id="siDateFrom" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('siDateFrom')" style="cursor:pointer"
				style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="siDateTo" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
			<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('siDateTo')" style="cursor:pointer"
				style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="siDateErr" class="error"></span></td>
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