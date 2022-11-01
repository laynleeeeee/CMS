<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Stockcard per item search page.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxValidation.js"></script>
<title>Stockcard per item Report</title>
<script type="text/javascript">
$(document).ready(function() {
	$("#dateFrom").val("${currentDate}");
	$("#dateTo").val("${currentDate}");
});

function filterDivisionOnCompany(){
	var companyId = $("#companyId").val();
	if(companyId != ""){
		var uri = contextPath+"/getDivisions?companyId="+companyId;
		console.log("URI = ", uri);
		$("#divisionId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};

		loadPopulate (uri, false, null, "divisionId", optionParser, null);
		filterWarehouseOnDivision();
	}
}

function filterWarehouseOnDivision(){
	var divisionId = $("#divisionId option:selected").val();
	console.log("test",divisionId);
	if(divisionId != "") {
		var uri = contextPath+"/getWarehouse/new?divisionId="+divisionId;
		console.log("URI  = ",uri)
		$("#warehouseId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, false, null, "warehouseId", optionParser, null);

	}
}

function generateReport(){
	var typeId = "${typeId}";
	var itemId = $("#hdnItemId").val();
	var item = $.trim($("#stockCode").val());
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId option:selected").val();
	var warehouseId = $("#warehouseId").val();
	var dateFrom = new Date($("#dateFrom").val());
	var dateTo = new Date($("#dateTo").val());
	var companyName = $("#companyId option:selected").text();
	companyName = companyName.slice(0,1); // Get only the first letter
	var formatType = $("#formatType").val();
	var hasFilterError = false;

	if($.trim($("#dateFrom").val()) == "" || $.trim($("#dateTo").val()) == "") {
		$('#reportStockCard').attr('src',"");
		$("#spanErrorDate").text("Date from and to are required fields.");
		hasFilterError = true;
	} else if (dateFrom > dateTo) {
		$('#reportStockCard').attr('src',"");
		$("#spanErrorDate").text("Invalid date range.");
		hasFilterError = true;
	} else if (dateFrom == 'Invalid Date' || dateTo == 'Invalid Date') {
		$('#reportStockCard').attr('src',"");
		$("#spanErrorDate").text("Invalid date.");
		hasFilterError = true;
	}
	if (itemId == "" && item != "") {
		$('#reportStockCard').attr('src',"");
		$("#spanSCError").text("Invalid stock code.");
		hasFilterError = true;
	} else if (itemId == "" && item =="") {
		$('#reportStockCard').attr('src',"");
		$("#spanSCError").text("Stock code is required.");
		hasFilterError = true;
	}
	if (warehouseId == null) {
		$("#spanWareHouseErrMsg").text("Warehouse is required.");
		$('#reportStockCard').attr('src',"");
		hasFilterError = true;
	}

	if (!hasFilterError) {
		var url = "rStockcardPDF?itemId="+itemId+"&companyId="+companyId+"&divisionId="+divisionId+"&warehouseId="
				+warehouseId+"&dateFrom="+$("#dateFrom").val()+"&dateTo="+$("#dateTo").val()
				+"&companyName="+companyName+"&typeId="+typeId+"&formatType="+formatType;
		$('#reportStockCard').attr('src',url);
		$('#reportStockCard').load();
		$("#spanErrorDate").text("");
		$("#spanSCError").text("");
		$("#spanStockCardMsg").text("");
		$("#spanWareHouseErrMsg").text("");
	}
}

function loadItemCategories() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var name = processSearchName($.trim($("#itemCategory").val()));
	var uri = contextPath+"/getItemCategories?name="+name+"&companyId="+companyId+"&divisionId="+divisionId;
	$("#itemCategory").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#itemCategoryId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#itemCategoryId").val(ui.item.id);
						$(this).val(ui.item.name);
					} else {
						$("#itemCategoryId").val("");
					}
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

function validateItemCategory() {
	var itemCategory = $.trim($("#itemCategory").val());
	if(itemCategory != "") {
		itemCategory = processSearchName(itemCategory);
		var companyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var sourceUrl = "/getItemCategories/perCategory?term="+itemCategory
				+ "&companyId="+companyId+ "&divisionId="+divisionId;

		validateInput(sourceUrl,
		function(itemCategory) {
			//success
			$("#spanItemCategoryMsg").text("");
			$("#itemCategoryId").val(itemCategory.id);
		}, function(itemCategory) {
			//error
			$("#spanItemCategoryMsg").text("Invalid item category.");
		});
	} else {
		$("#itemCategoryId").val("");
	}
}

function loadItems() {
	var icId = $("#itemCategoryId").val() != "" ? $("#itemCategoryId").val() : $("#itemCategoryId").val(-1);
	var stockCode = $.trim($("#stockCode").val());
	var uri = contextPath+"/getRItems/filter?itemCategoryId="+icId+"&stockCode="+processSearchName(stockCode);
	$("#stockCode").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnItemId").val(ui.item.id);
			$(this).val(ui.item.stockCode);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#hdnItemId").val(ui.item.id);
						$(this).val(ui.item.stockCode);
					} else {
						$("#hdnItemId").val("");
						$(this).val("");
					}
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.stockCode+ "-" + item.description + "</a>" )
			.appendTo( ul );
	};
}

function warehouseOnChange() {
	var warehouseId = $("#warehouseId").val();
	if (warehouseId != -1) {
		$("#spanWareHouseErrMsg").text("");
	}
}

</script>
</head>
<body>
<table>
	<tr>
		<td>
			<br/>
		</td>
	</tr>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" class="frmSelectClass" onchange="filterDivisionOnCompany();">
			<option selected ='selected' value=-1>Please select company</option>
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Division </td>
		<td>
			<select id="divisionId" class="frmSelectClass" onchange="filterWarehouseOnDivision();">
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Warehouse </td>
		<td>
			<select id="warehouseId" class="frmSelectClass">
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanWareHouseErrMsg" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Item Category </td>
		<td>
			<input type="hidden" id="itemCategoryId" />
			<input id="itemCategory" class="input" onkeydown="loadItemCategories();"
				onkeyup="loadItemCategories();" onblur="validateItemCategory();"/>
		</td>
	</tr>
	<tr>
			<td></td>
			<td><span id="spanItemCategoryMsg" class="error"></span></td>
	</tr>
	<tr>
			<td class="title2">Stock Code/Description:</td>
			<td class="value"><input type="text" id="stockCode" class="input" onkeyup="loadItems();" 
				onkeydown="loadItems();"/>
			<input type="hidden" id="hdnItemId"/></td>
		</tr>
		<tr>
			<td></td>
			<td><span id="spanSCError" class="error"></span></td>
		</tr>
	<tr>
			<td class="title2">Date: </td>
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
		<td><span id="spanErrorDate" class="error"></span></td>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="reportStockCard"></iframe>
</div>
</body>
</html>