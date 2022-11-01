<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Daily item sales main page.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxValidation.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
	filterWarehouses();
});

function getCommonParam () {
	var companyId = $("#companyId option:selected").val();
	var stockCode = processSearchName($.trim($("#stockCode").val()));
	var invoiceNo = processSearchName($.trim($("#invoiceNo").val()));
	var strDateFrom = $("#dateFrom").val();
	var strDateTo = $("#dateTo").val();
	var itemCategoryId = $("#itemCategoryId").val() != "" ? $("#itemCategoryId").val() : -1;
	var formatType = $("#formatType").val();
	var warehouseId = $("#warehouseId").val();
	return "?companyId="+companyId+"&stockCode="+stockCode+"&invoiceNo="+invoiceNo+
			"&dateFrom="+strDateFrom+"&dateTo="+strDateTo+"&formatType="+
			formatType+"&itemCategoryId="+itemCategoryId+"&warehouseId="+warehouseId;
}

function loadItems() {
	var companyId = $("#companyId").val();
	var itemCategoryId = $("#itemCategoryId").val() != "" ? $("#itemCategoryId").val() : -1;
	var stockCode = processSearchName($.trim($("#stockCode").val()));
	var uri = contextPath+"/getRItems/filter?companyId="+companyId+
			"&stockCode="+stockCode;
	if(itemCategoryId != -1){
		uri = uri+"&itemCategoryId="+itemCategoryId;
	}
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
	var stockCode = $.trim($("#stockCode").val());
	if (stockCode != "") {
		$.ajax({
			url: contextPath+"/"+"/getItem?stockCode="+processSearchName(stockCode),
			success: function (item) {
				$("#spanSCError").text("");
				$(elem).val(item.stockCode);
				$("#hdnItemId").val(item.id);
				
			},
			error : function(error) {
				$("#hdnItemId").val("");
				$("#spanSCError").text("Invalid stock code.");
			},
			dataType: "json"
		});
	}
}

function genDailyItemSales() {
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var invalidItem = $("#hdnItemId").val() == "" && $.trim($("#stockCode").val()) != "";
	var noDate = (dateFrom == "") || (dateTo == "");
	var invalidDateRange = new Date(dateFrom) > new Date(dateTo);
	console.log("noDate "+noDate);
	console.log("invalidDateRange "+invalidDateRange);
	if(noDate) {
		$("#spanDateError").text("Date from and to is required.");
	} else if(invalidDateRange) {
		$("#spanDateError").text("Invalid date range.");
	} else {
		$("#spanDateError").text("");
	}
	if (!invalidItem && !noDate && !invalidDateRange) {
		var url = contextPath + "/dailyItemSales/generate"+getCommonParam();
		$('#reportDailyItemSale').attr('src',url);
		$('#reportDailyItemSale').load();
	}
}

function loadItemCategories() {
	var companyId = $("#companyId").val();
	var name = processSearchName($.trim($("#txtItemCategory").val()));
	var uri = contextPath+"/getItemCategories?name="+name+"&companyId="+companyId;
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
		var companyId = $("#companyId").val();
		var sourceUrl = "/getItemCategories/perCategory?term="+itemCategory
				+ "&companyId="+companyId;

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

function filterWarehouses(warehouseId) {
	var companyId = $("#companyId").val();
	if(companyId != "") {
		var uri = contextPath+"/getWarehouse?companyId="+companyId;
		$("#warehouseId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		postHandler = {
				doPost: function() {
					if(warehouseId != "")
						$("#warehouseId").val(warehouseId);
				}
		};
		loadPopulate (uri, true, null, "warehouseId", optionParser, postHandler);
	}
}

</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Branch</td>
		<td class="value">
			<select id="companyId" class="frmSelectClass" onchange="filterWarehouses();">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Warehouse</td>
		<td class="value">
			<select id="warehouseId" class="frmSelectClass">
				<option value="-1">ALL</option>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
		<span id="warehouseError" class="error" ></span>
	</tr>
	<tr>	
		<td class="title2">Item Category:</td>
		<td class="value">
			<input type="text" id="txtItemCategory" class="input" onkeyup="loadItemCategories();" 
				onkeydown="loadItemCategories();" onblur="getItemCategory();"/>
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
		<td class="title2">Stock Code/Description:</td>
		<td class="value">
			<input type="text" id="stockCode" class="input" onkeyup="loadItems();" 
				onkeydown="loadItems();" onblur="getItem(this);"/>
			<input type="hidden" id="hdnItemId"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="spanSCError" class="error"></span></td>
	</tr>
	
	<tr>
		<td class="title2">Invoice No.</td>
		<td class="value">
			<input id="invoiceNo" class="input">
		</td>
	</tr>
	
	<tr>
		<td class="title2">Date</td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2"
					onblur="evalDate('dateFrom')" value="${currentDate}">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2"
					onblur="evalDate('dateTo')" value="${currentDate}">
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
		<td colspan="3"><input type="button" value="Generate" onclick="genDailyItemSales();"/></td>
	</tr>
</table>
<div>
	<iframe id="reportDailyItemSale"></iframe>
</div>
<hr class="thin2">
</body>
</html>