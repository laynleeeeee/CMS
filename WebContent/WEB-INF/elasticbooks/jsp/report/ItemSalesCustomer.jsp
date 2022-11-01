<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Item Sales Customer main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/retrieveArCustomer.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxValidation.js"></script>
<script type="text/javascript">
var selectCustomerAcct = null;
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
	filterDivisionbyCompany();
});

function assignCustomerAcct (select) {
	selectCustomerAcct = $(select).val();
}

function filterDivisionbyCompany() {
	var companyId = $("#companyId").val();
	if(companyId != "") {
		var uri = contextPath+"/getDivisions?companyId="+companyId;
		$("#divisionId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, true, null, "divisionId", optionParser, null);
	}
}

function getCommonParam () {
	var companyId = $("#companyId option:selected").val() != "" ? 
			$("#companyId option:selected").val() : -1;
	var arCustomerId = $("#hdnCustomerId").val() != "" ? $("#hdnCustomerId").val() : -1;
	var arCustomerAcctId = $("#customerAcctId").val() != "" ? $("#customerAcctId").val() : -1;
	var itemCategoryId = $("#itemCategoryId").val() != "" ? $("#itemCategoryId").val() : -1;
	var itemId = $("#hdnItemId").val() != "" ? $("#hdnItemId").val() : -1;
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var formatType = $("#formatType").val();
	var divisionId = $ ("#divisionId").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&arCustomerId="+arCustomerId+"&arCustomerAcctId="+arCustomerAcctId+
			"&itemCategoryId="+itemCategoryId+"&itemId="+itemId+"&dateFrom="+dateFrom+
			"&dateTo="+dateTo+"&formatType="+formatType;
}

function loadItemCategories() {
	var divisionId = $("#divisionId").val();
	$("#stockCode").val("");
	var name = processSearchName($.trim($("#txtItemCategory").val()));
	var uri = contextPath+"/getItemCategories?name="+name+"&companyId=-1"+"&divisionId="+divisionId;
	$("#txtItemCategory").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#itemCategoryId").val(ui.item.id);
			$("#spanItemCategoryError").text("");
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#spanItemCategoryError").text("");
						$(this).val(ui.item.name);
					}
				},
				error : function(error) {
					$("#itemCategoryId").val("");
					$("#spanCustomerError").text("Invalid item category.");
					$("#txtItemCategory").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function getItemCategory() {
	var itemCategory = $.trim($("#txtItemCategory").val());
	if(itemCategory != "") {
		itemCategory = processSearchName(itemCategory);
		var sourceUrl = "/getItemCategories/perCategory?term="+itemCategory
				+ "&companyId=-1";
		validateInput(sourceUrl,
		function(itemCategory) {
			//success
			$("#spanItemCategoryError").text("");
			$("#itemCategoryId").val(itemCategory.id);
		}, function(itemCategory) {
			//error
			$("#spanItemCategoryError").text("Invalid item category.");
		});
	} else {
		$("#itemCategoryId").val("");
	}
}

function loadItems() {
	var companyId = $("#companyId").val();
	var itemCategoryId = $("#itemCategoryId").val();
	var stockCode = processSearchName($.trim($("#stockCode").val()));
	var uri = contextPath+"/getRItems/filter?itemCategoryId="+itemCategoryId+"&stockCode="+processSearchName(stockCode);
	loadACList("stockCode", "hdnItemId", uri,
			contextPath + "/getItem?stockCode=", "stockCodeAndDesc", "stockCode",
			function(item) {
				//Select
				$("#spanSCError").text("");
			}, function(item) {
				//Change
				$("#spanSCError").text("");
			}, function() {
				//Success
				$("#spanSCError").text("");
			}, function() {
				//Error
				$("#spanSCError").text("Invalid stock code.");
			}
	);
}

function getItem (elem) {
	var companyId = $("#companyId").val();
	var stockCode = $.trim($("#stockCode").val());
	var itemCategoryId = $("#itemCategoryId").val();
	if (stockCode != "") {
		$.ajax({
			url: contextPath+"/getItem?stockCode="+processSearchName(stockCode)+
					"&companyId="+companyId+"&itemCategoryId="+itemCategoryId,
			success: function (item) {
				if (item != null) {
					$("#spanItemCategoryError").text("");
					$("#spanSCError").text("");
					$(elem).val(item.stockCode);
					$("#hdnItemId").val(item.id);
				}
			},
			error : function(error) {
				$("#hdnItemId").val("");
				$("#spanSCError").text("Invalid stock code.");
			},
			dataType: "json"
		});
	} else {
		$("#hdnItemId").val("");
	}
}

function checkItemCategory () {
	if ($.trim($("#hdnItemId").val()) != "" && $("#itemCategoryId").val() != "") {
		var itemId = $("#hdnItemId").val();
		var itemCategoryId = $("#itemCategoryId").val();
		$.ajax({
			url: contextPath + "/itemSalesCustomer/belongsToCategory?itemId="+itemId+"&itemCategoryId="+itemCategoryId,
			success: function (responseText) {
				if (responseText != null && typeof responseText != "undefined") {
					return responseText;
				}
			},
			dataType: "text"
			
		});
	}
}

function genItemSalesCustomer() {
	var invalidCompany = $.trim($("#companyId").val()) == "";
	$("#spanCustomerError").text(invalidCompany ? "Company is required." : "");
	
	var invalidCustomer = $.trim($("#hdnCustomerId").val()) == "" || $.trim($("#customerName").val()) == "";
	$("#spanCustomerError").text(invalidCustomer ? "Valid customer name is required." : "");
		
	var invalidDateRange = $.trim($("#dateFrom").val()) == "" || $.trim($("#dateTo").val()) == "";
	$("#spanDateError").text(invalidDateRange ? "Complete and valid date range is required." : "");

	var itemCategoryId = $.trim($("#itemCategoryId").val());
	var itemId = $.trim($("#hdnItemId").val());
	if (itemCategoryId != "" && itemId != "") {
		var belongsToCategory = null;
		$.ajax({
			url: contextPath + "/itemSalesCustomer/belongsToCategory?itemId="+itemId+"&itemCategoryId="+itemCategoryId,
			success : function (responseText) {
				belongsToCategory = $.trim(responseText) == "true";
			},
			complete : function () {
				//$("#spanItemError").text(!belongsToCategory ? 
				//		"Item must belong to the selected item category." : "");
				if (!invalidCompany && !invalidCustomer && !invalidDateRange && belongsToCategory) {
					var uri = contextPath + "/itemSalesCustomer/generate"+getCommonParam()
					$('#reportItemSalesCust').attr('src',uri);
				}
			},
			dataType: "text"
			
		});
	} else {
		if (!invalidCompany && !invalidCustomer && !invalidDateRange) {
			var uri = contextPath + "/itemSalesCustomer/generate"+getCommonParam();
			$('#reportItemSalesCust').attr('src',uri);
			$('#reportItemSalesCust').load();
		}
	}
	
}

function getCustomers() {
	$("#customerAcctId").empty();
	$("#customerAcctId").append("<option selected='selected' value=-1>ALL</option>");
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
					$("#hdnCustomerId").val(customerId);
					$("#customerName").val(customer[0].name);
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#hdnCustomerId").val("");
					//$("#arCustomerAcctId").empty();
				}
				filterCustomerAccts();
			},
			dataType: "json"
		});
	} else {
		$("#hndCustomerId").val("");
		$("#spanCustomerError").text("");
	}
}

function showCustomers(){
	$("#spanCustomerError").text("");
	var companyId = $("#companyId").val();
	var customerName = $.trim($("#customerName").val());
	var uri = contextPath + "/getArCustomers/new?name="+processSearchName(customerName)
			+"&companyId="+companyId;
	var divisionId = $("#divisionId").val();
	if (divisionId != "" && divisionId != "undefined" && divisionId != -1) {
		uri += "&divisionId="+divisionId;
	}
	$("#customerName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnCustomerId").val(ui.item.id);
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

function filterCustomerAccts() {
	$("#customerAcctId").empty();
	$("#customerAcctId").append("<option selected='selected' value=-1>ALL</option>");
	if($.trim($("#customerName").val()) == "") {
		$("#customerId").val("");
	} else {
		selectedCompanyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var selectedCustomerId = $("#hdnCustomerId").val();
		var uri = contextPath+"/getArCustomerAccounts?arCustomerId="+selectedCustomerId+"&companyId="+selectedCompanyId+"&activeOnly=true"
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
						found.push(this.value);
					});
			}
		};
		loadPopulate (uri, false, null, "customerAcctId", optionParser, postHandler);
	}
}

function companyOnChange() {
	filterDivisionbyCompany();
	$("#customerName").val("");
	$("#hdnCustomerId").val("");
	$("#customerAcctId").empty();
	$("#customerAcctId").append("<option selected='selected' value=-1>ALL</option>");
}

function clearCustomer() {
	$("#customerName").val("");
	$("#customerAcctId").val("");
	$("#txtItemCategory").val("");
	$("#stockCode").val("");
	$("#customerName").empty();
	$('#reportItemSalesCust').attr('src', "");
	$('#reportItemSalesCust').load();
}
function clearItems(){
	$("#stockCode").val("");
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company</td>
		<td class="value">
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
		<td class="title2">Division </td>
		<td>
		<select id="divisionId" class="frmSelectClass" onchange="clearCustomer();">
				<option selected='selected' value=-1>ALL</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Customer</td>
		<td class="value">
			<input id="customerName" class="input" onkeypress="showCustomers();" onblur="getCustomers();">
			<input type="hidden" id="hdnCustomerId"/>
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
			<option selected='selected' value="-1">All</option>
			</select>
		</td>
	</tr>
	
	<tr>	
		<td class="title2">Item Category</td>
		<td class="value">
			<input type="text" id="txtItemCategory" class="input" onkeypress="loadItemCategories();"
				onblur="getItemCategory();" onchange="clearItems();"/>
			<input type="hidden" id="itemCategoryId"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanItemCategoryError" class="error" ></span>
		</td>
	</tr>
	
	<tr>	
		<td class="title2">Item</td>
		<td class="value">
			<input type="text" id="stockCode" class="input" onkeypress="loadItems();"/>
			<input type="hidden" id="hdnItemId"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanItemError" class="error" ></span>
		</td>
	</tr>
	
	<tr>
		<td class="title2">GL Date </td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDateError" class="error" ></span>
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
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="genItemSalesCustomer();"/></td>
	</tr>
</table>
<div>
	<iframe id="reportItemSalesCust"></iframe>
</div>
<hr class="thin2">
</body>
</html>