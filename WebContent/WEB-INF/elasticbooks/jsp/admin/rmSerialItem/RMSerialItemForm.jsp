<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../../jsp/include.jsp" %>
<!--

	Description: Retail Item with serialize checkbox.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/tabsUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebForm.css"media="all">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"media="all">
<style type="text/css">
.txtSrpCompanyNames, .txtBpCompanyNames, .txtSrp,
.txtDCompanyNames, .txtBpDCompanyNames, .txtACompanyNames,
.txtBpACompanyNames, .txtNames, .txtValue, .txtWp, .srpDivision {
	width: 100%;
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}

.divVeil {
	background-color: #F2F1F0;
}
.hide {
	display: none;
}
</style>
<script type="text/javascript">
var minimumRows = 4;
var srpCurrentIndex = "${fn:length(serialItemSetupDto.item.itemSrps)}";
var discountCurrentIndex = "${fn:length(serialItemSetupDto.item.itemDiscounts)}";
var addOnCurrentIndex = "${fn:length(serialItemSetupDto.item.itemAddOns)}";
var bPriceCurrIndex = "${fn:length(serialItemSetupDto.item.buyingPrices)}";
var bDiscCurrIndex = "${fn:length(serialItemSetupDto.item.buyingDiscounts)}";
var bAddOnCurrIndex = "${fn:length(serialItemSetupDto.item.buyingAddOns)}";
var SP_COMPANY_CLASS = "txtSrpCompanyNames";
var SP_DISCOUNT_CLASS = "txtDCompanyNames";
var SP_ADD_ON_CLASS = "txtACompanyNames";
var BP_COMPANY_CLASS = "txtBpCompanyNames";
var BP_DISCOUNT_CLASS = "txtBpDCompanyNames";
var BP_ADD_ON_CLASS = "txtBpACompanyNames";
$(document).ready (function () {
	$("#txtReorderingPoint, #txtMaxOrderingPoint").bind("keyup keydown", function(e) {
		inputOnlyNumeric("txtReorderingPoint");
		inputOnlyNumeric("txtMaxOrderingPoint");
	});

	//Load Selling Details
	addSRPLine(getRows(srpCurrentIndex));
	addDiscountLine (getRows(discountCurrentIndex));
	addAddOnLine(getRows(addOnCurrentIndex));

	formatMonetaryVal();
});

function getRows(index) {
	var rows = minimumRows > index ? minimumRows - index : 1;
	return rows;
}


function addSRPLine (numOfRows) {
	addPriceLine("itemSrps", "tblBranchFrm", "txtBCompanyName", SP_COMPANY_CLASS,
			"hdnSpCompanyId", "hdnDivisionId", srpCurrentIndex, numOfRows);
}


function addPriceLine (tableName, tblElemName, companyElemId, companyElemClass,
		hdnCompId, hdnDivisionId, currentIndex, numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";

		//Empty TD
		newRow += "<td class='tdProperties' style='border-left: none; border-right: none;'>";
		newRow += "<img src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deleteRow(this)'/>";
		newRow += "</td>";
		
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<span id='spanEmptyTd"+currentIndex+"'></span>";
		newRow += "</td>";

		//Company Name
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].companyId' id='"+hdnCompId+""+currentIndex+"' type='hidden'/>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].companyName' id='"+companyElemId+""+currentIndex+"' \
			onkeyup='showSrpCompanyList(this.id, "+hdnCompId+""+currentIndex+");' class='"+companyElemClass+"' style='width: 100%;'/>";
		newRow += "</td>";

		//Division
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].divisionId' id='"+hdnDivisionId+""+currentIndex+"' type='hidden'/>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].divisionName' id='divisionName"+currentIndex+"' style='width: 100%;' class='srpDivision'";
		newRow += "onblur='getDivision(this,"+currentIndex+");' onkeypress='showDivisions(this,"+currentIndex+");'/>";
		newRow += "</td>";

		//SRP
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].srp' id='srp"+currentIndex+"' onkeyup='processCompNextRow(this, event);' \
			style='width: 100%; text-align: right;' class='txtSrp' onblur='formatMoney(this);' maxLength='13'/>";
		newRow += "</td>";

		newRow += "</tr>";
		$("#"+tblElemName+" tbody").append(newRow);
		currentIndex++;
	}

	//Set the current index
	srpCurrentIndex = (currentIndex-1);
}

function addDiscountLine(numOfRows) {
	addDiscount ("itemDiscounts", "tblDiscountFrm", "txtDCompanyName", SP_DISCOUNT_CLASS,
			"hdnSpdCompId", "hdnSpDiscTypeId", discountCurrentIndex, numOfRows);
}


function addDiscount (tableName, tblElemId, companyElemId, companyElemClass,
		hdnCompId, hdnDiscTypeId, currentIndex, numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";

		//Company Name
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].companyId' id='"+hdnCompId+""+currentIndex+"' type='hidden'/>"; //Hidden Company Id
		newRow += "<input name='item."+tableName+"["+currentIndex+"].companyName' id='"+companyElemId+""+currentIndex+"' \
					onkeyup='showDiscountCompanyList(this.id, "+hdnCompId+""+currentIndex+");' class='"+companyElemClass+"' style='width: 100%;'/>";
		newRow += "</td>";

		//Name
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].name' id='txtName"+currentIndex+"' class='txtNames' style='width: 100%;'/>";
		newRow += "</td>";

		//Discount Type
		newRow += "<td class='tdProperties'>";
		newRow += "<input id='"+hdnDiscTypeId+""+currentIndex+"' type='hidden' name='item."+tableName+"["+currentIndex+"].itemDiscountTypeId' value='1' />";
		newRow += "<select class='frmSmallSelectClass' onchange='assignDiscountType(this.value, "+hdnDiscTypeId+""+currentIndex+")'> ";
		newRow += "<c:forEach var='discType' items='${itemDiscountTypes}'>";
		newRow += "<option value='${discType.id}'>${discType.name}</option>";
		newRow += "</c:forEach>";
		newRow += "</select>";
		newRow += "</td>";

		//Percentage/Amount
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].value' id='value"+currentIndex+"' style='width: 100%; text-align: right;' class='txtValue' \
			 		onkeydown='processDiscNextRow(this, event);' onblur='formatValue(this);' maxLength='13'/>";
		newRow += "</td>";

		//Active
		newRow += "<td class='tdProperties' style='border-right: none;'>";
		newRow += "<input type='checkbox' checked='checked' name='item."+tableName+"["+currentIndex+"].active' id='status"+currentIndex+"' \
					style='width: 100%; text-align: right;' class='cbStatus' />";
		newRow += "</td>";

		newRow += "</tr>";
		$("#"+tblElemId+" tbody").append(newRow);
		currentIndex++;
	}

	//Set the current index
	discountCurrentIndex = (currentIndex-1);
}

function addAddOnLine (numOfRows) {
	addAddOn ("itemAddOns", "tblAddOnFrm","txtACompanyName", SP_ADD_ON_CLASS,
			"hdnSpaCompId", addOnCurrentIndex, true, numOfRows);
}

function addAddOn (tableName, tblElemId, companyElemId, companyElemClass,
		hdnCompId, currentIndex, numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";

		//Company Name
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].companyId' id='"+hdnCompId+""+currentIndex+"' type='hidden'/>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].companyName' id='"+companyElemId+""+currentIndex+"' \
					onkeyup='showAddOnCompanyList(this.id, "+hdnCompId+""+currentIndex+");' class='"+companyElemClass+"' style='width: 100%;'/>";
		newRow += "</td>";

		//Name
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].name' id='txtName"+currentIndex+"' class='txtNames' style='width: 100%;'/>";
		newRow += "</td>";

		//Amount
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='item."+tableName+"["+currentIndex+"].value' id='value"+currentIndex+"' style='width: 100%; text-align: right;' class='txtValue' \
					onkeydown='processAddOnNextRow(this, event);' onblur='formatValue(this);' maxLength='13'/>";
		newRow += "</td>";

		//Active
		newRow += "<td class='tdProperties' style='border-right: none;'>";
		newRow += "<input type='checkbox' checked='checked' name='item."+tableName+"["+currentIndex+"].active' id='status"+currentIndex+"' \
					style='width: 100%; text-align: right;' class='cbStatus' />";
		newRow += "</td>";

		newRow += "</tr>";
		$("#"+tblElemId+" tbody").append(newRow);
		currentIndex++;
	}

	//Set the current index
	addOnCurrentIndex = (currentIndex-1);
}

function showCompanyList (elemId, companyclass, hdnCompId, spanError, isExcludeCompanies, isFilterCompanies) {
	var companyName = $.trim($("#"+elemId).val());
	var uri = contextPath + "/getCompany/filter?companyName=" + companyName;
// Disabled exclusion of duplicate company
// 	var row = $("#"+elemId).parent("td").parent("tr");
// 	var rowNumber = row.rowNumber;
// 	var cNames = "";
// 	$("." + companyclass).each(function (i) {
// 		if (i != rowNumber)
// 			cNames +=  $.trim($(this).val()) != "" ? encodeURIComponent($.trim($(this).val())) + ";" : "";
// 	});
// 	if (isExcludeCompanies)
// 		uri += "&companyNames="+cNames;
// 	if (isFilterCompanies)
// 		uri += "&filterCNames="+cNames;
	$("#"+elemId).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(this).val(ui.item.name);
			$(hdnCompId).val(ui.item.id);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.name);
						$(hdnCompId).val(ui.item.id);
					}
					$("#" + spanError).text("");
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

function showSrpCompanyList(elemId, hdnCompId) {
	showCompanyList(elemId, SP_COMPANY_CLASS, hdnCompId, "spanErrorItemSrps", true, false);
}

function showDiscountCompanyList(elemId, hdnCompId) {
	var companyClass = SP_COMPANY_CLASS;
	var hasCompanies = false;
	$("."+companyClass).each(function (i) {
		if ($.trim($(this).val()) != "")
			hasCompanies = true;
	});
	if (hasCompanies) {
		showCompanyList(elemId, companyClass, hdnCompId, "spanErrorItemDiscounts", false, true);
	}
}

function showAddOnCompanyList(elemId, hdnCompId) {
	var hasCompanies = false;
	var companyClass = SP_COMPANY_CLASS;
	$("."+companyClass).each(function (i) {
		if ($.trim($(this).val()) != "")
			hasCompanies = true;
	});

	if (hasCompanies) {
		showCompanyList(elemId, companyClass, hdnCompId, "spanErrorItemAddOns", false, true);
	}
}

function processCompNextRow(srp, event) {
	var srpId = $(srp).attr("id");
	var rowCount = $("#tblBranchFrm tbody tr").length;
	var currentIndex = Number(srpId.replace("srp", ""));
	if (event.keyCode == 9 && !event.shiftKey) {
		var companyName = $("#txtBCompanyName"+currentIndex).val();
		var currentRowNo = currentIndex + 1
		if (currentRowNo >= rowCount) {
			addPriceLine("itemSrps", "tblBranchFrm", "txtBCompanyName", SP_COMPANY_CLASS,
					"hdnSpCompanyId", "hdnDivisionId", currentRowNo, 1);
			$("#spanEmptyTd"+(currentIndex+1)).focus();
		}
	}
}

function processDiscNextRow(value, event) {
	var tblName = "itemDiscounts";
	var tblElemName = "tblDiscountFrm";
	var elemId= "txtDCompanyName";
	var hdnCompId = "hdnSpdCompId";
	var hdnDiscTypeId = "hdnSpDiscTypeId";
	var valueId = $(value).attr("id");
	var currentIndex = Number(valueId.replace("value", ""));
	if(event.keyCode == 9 && !event.shiftKey) { //Tab key
		var rowCount = $("#"+tblElemName+" tbody tr").length;
		var companyName = $("#"+elemId+currentIndex).val();
		if(typeof companyName != "undefined"|| companyName != "") {
			if((currentIndex+1) < rowCount) {
				var nextRow = $(value).closest("tr").next();
				if(nextRow.hasClass("divVeil")) {
					enableEntries ($(nextRow));
				}
			} else {
				addDiscount (tblName, tblElemName, elemId, SP_DISCOUNT_CLASS,
						hdnCompId, hdnDiscTypeId, (currentIndex+1), 1);
			}
		}
	}
}

function processAddOnNextRow(value, event) {
	var tblName = "itemAddOns";
	var tblElemName = "tblAddOnFrm";
	var compElemId = "txtACompanyName";
	var hdnCompId = "hdnSpaCompId";
	var valueId = $(value).attr("id");
	var currentIndex = Number(valueId.replace("value", ""));
	if(event.keyCode == 9 && !event.shiftKey) { //Tab key
		var rowCount = $("#"+tblElemName+" tbody tr").length;
		var companyName = $("#"+currentIndex).val();
		if(typeof companyName != "undefined"|| companyName != "") {
			if((currentIndex+1) < rowCount) {
				var nextRow = $(value).closest("tr").next();
				if(nextRow.hasClass("divVeil")) {
					enableEntries ($(nextRow));
				}
			} else {
				addAddOn (tblName, tblElemName, compElemId, SP_ADD_ON_CLASS,
						hdnCompId, (currentIndex+1), 1);
			}
		}
	}
}

function assignDiscountType(selectValue, elem) {
	$(elem).val(selectValue);
	var textbox = $(elem).parent("td").parent("tr").closest("tr").find(".txtValue");
	if (selectValue  != 1) {
		formatMoney(textbox);
	} else {
		formatPercentage(textbox);
	}
}

function removeDuplicateSrpCompany(elem, rowIndex) {
	var companyElemId = SP_COMPANY_CLASS;
	var companyName = $.trim($(elem).val());
	var row = $(elem).parent("td").parent("tr").index();
	$("."+companyElemId).each(function (i) {
		if (companyName.toUpperCase() == $.trim($(this).val().toUpperCase()) && row != i){
			$(elem).val("");
		}
	});
	if(companyName == "") {
		//Clear division details.
		$("#hdnDivisionId"+rowIndex).val("");
		$("#divisionName"+rowIndex).val("");
	}
}

//Bin number (warehouse)
function showWarehouse(elem) {
	var txtWarehouse = $.trim($(elem).val());
	var warehouseId = $("#warehouseId").val();
	var uri = contextPath+"/getWarehouse/getParentWarehouses?name="+processSearchName(txtWarehouse)
			+"&isExact=false&isActive=true";
	if (warehouseId != "" && warehouseId != null && warehouseId != 0) {
		uri += "&warehouseId="+warehouseId;
	}
	$(elem).autocomplete({
		source: uri,
		select: function(event, ui) {
			$("#warehouseId").val(ui.item.id);
			$(elem).val(ui.item.name);
			return false;
		}, minLength: 2
	}).data("ui-autocomplete")._renderItem = function(ul, item) {
		return $("<li>").data("ui-autocomplete-item", item)
			.append("<a style='font-size: small;'>"+item.name+"</a>")
			.appendTo(ul);
	};
}

function getWarehouse(elem) {
	//Clear bin number validation
	$("#spanWarehouseErr").text("");
	$("#warehouseNameErr").text("");

	var txtWarehouse = $.trim($(elem).val());
	if (txtWarehouse != "") {
		var uri = contextPath+"/getWarehouse/getParentWarehouses?name="+processSearchName(txtWarehouse)
				+"&isExact=true&isActive=false";
		$.ajax({
			url: uri,
			success : function(warehouse) {
				if (warehouse != null && warehouse[0] != undefined) {
					$("#warehouseId").val(warehouse[0].id);
					$(elem).val(warehouse[0].name);
				} else {
					$("#warehouseId").val("");
					$("#spanWarehouseErr").text("Invalid bin number.");
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$("#warehouseId").val("");
	}
}

//Barcode
function generateBarcode() {
	$("#barcodeError").text("");
	if($("#barcode").val() == null || $("#barcode").val() == "") {
		var itemCategoryId = $("#itemCategoryId").val();
		var uri = contextPath + "/admin/rmItems/generateBarcode?itemCategoryId="+itemCategoryId;
		$.ajax({
			url: uri,
			success : function(item) {
				if (item != null) {
					$("#barcode").val(item);
				}
			},
			dataType: "text"
		});
	}
}

function printBarcode() {
	var barcode = $("#barcode").val();
	$("#barcodeError").text("");
	if($("#barcode").val() != null && $("#barcode").val() != "") {
		window.open(contextPath + "/admin/rmItems/printBarcode?barcode="+barcode);
	} else {
		$("#barcodeError").text("Please generate barcode first.");
	}
}


function showDivisions(elem, rowIndex) {
	var divisionName = processSearchName($.trim($(elem).val()));
	var companyName = $.trim($("#txtBCompanyName"+rowIndex).val());
	if(companyName != "") {
		var uri = contextPath+"/getDivisions/byName?divisionName="+divisionName+"&isExact=false";
		$(elem).autocomplete({
			source: uri,
			select: function( event, ui ) {
				$(this).val(ui.item.name);
				return false;
			}, minLength: 2,
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>" +item.name + "</a>" )
				.appendTo( ul );
		};
	}
}

function getDivision(elem, rowIndex) {
	var divisionName = processSearchName($.trim($(elem).val()));
	if($.trim($(elem).val()) != "") {
		$.ajax({
			url: contextPath+"/getDivisions/byName?divisionName="+divisionName+"&isExact=false",
			success : function(division) {
				if (division != null && division[0] != undefined) {
					$("#hdnDivisionId"+rowIndex).val(division[0].id);
					$(elem).val(division[0].name);
				} else {
					$("#hdnDivisionId"+rowIndex).val("");
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function deleteRow(deleteImg) {
	var $toBeDeletedRow = $(deleteImg).closest("tr");
	$($toBeDeletedRow).remove();
	var rowCount = $("#tblBranchFrm").find("tbody tr").length;
	if(rowCount < 1){
		addSRPLine (1);
	}
}
</script>
</head>
<body>
<div class="formBigDiv">
	<form:form method="POST" commandName="serialItemSetupDto" id="rItemForm">
		<div class="modFormLabel">Item</div>
	<br>
	<div class="modForm">
	<fieldset class="frmField_set">
	<legend>Item Information </legend>
		<table class="formTable">
			<form:hidden path="item.id"/>
			<form:hidden path="item.createdBy"/>
			<form:hidden path="item.createdDate"/>
			<form:hidden path="serialItemSetup.id" />
			<tr>
				<td class="labels">* Stock Code</td>
				<td class="value"><form:input path="item.stockCode" id="stockCode" cssClass="numeric"
						cssStyle="width: 120px;"/></td>
			</tr>
			<tr>
				<td class="value"> </td>
				<td class="value">
					<form:errors path="item.stockCode" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">* Unit of Measure</td>
				<td class="value">
					<form:select path="item.unitMeasurementId" cssClass="frmSelectClass">
						<form:options items="${unitMeasurements}" itemValue="id" itemLabel="name" />
					</form:select>
				</td>
			</tr>
			<tr>
				<td></td>
				<td class="value">
					<form:errors path="item.unitMeasurementId" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">* Item Category</td>
				<td class="value">
					<form:select path="item.itemCategoryId" cssClass="frmSelectClass" id="itemCategoryId">
						<form:options items="${itemCategories}" itemValue="id" itemLabel="name" />
					</form:select>
				</td>
			</tr>
			<tr>
				<td></td>
				<td class="value">
					<form:errors path="item.itemCategoryId" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Manufacturer's Part Number</td>
				<td class="value"><form:input path="item.manufacturerPartNo" id="stockCode" cssClass="numeric"
						cssStyle="width: 120px;"/></td>
			</tr>
			<tr>
				<td></td>
				<td class="value">
					<form:errors path="item.manufacturerPartNo" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">* Description</td>
				<td class="value"><form:input path="item.description" cssClass="input"/></td>
			</tr>
			<tr>
				<td class="value"> </td>
				<td class="value">
					<form:errors path="item.description" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Reordering Point</td>
				<td class="value">
					<form:input path="item.reorderingPoint" id="txtReorderingPoint" cssClass="numeric"
							maxlength="10" type="text"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Maximum Ordering Point</td>
				<td class="value">
					<form:input path="item.maxOrderingPoint" id="txtMaxOrderingPoint" cssClass="numeric"
							maxlength="10" type="text"/>
				</td>
			</tr>
			<tr>
				<td class="labels">* VAT Type</td>
				<td class="value">
					<form:select path="item.itemVatTypeId" class="frmSmallSelectClass">
						<form:options items="${vatTypes}" itemLabel="name" itemValue="id"/>
					</form:select>
				</td>
			</tr>
			<tr>
				<tr>
				<td class="label"></td>
				<td class="value">
					<form:errors path="item.itemVatTypeId" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Barcode</td>
				<td class="value"><form:input path="item.barcode" maxlength="12" id="barcode" cssClass="numeric"
						cssStyle="width: 120px; margin-right: 5px; text-align: left;" onkeydown="inputOnlyNumeric(this);"/>
						<input type="button" value="Generate" onclick="generateBarcode();" />
						<input type="button" id="btnPrintBarcode" value="Print Barcode" onclick="printBarcode();"/></td>
			</tr>
			<tr>
				<td class="value"> </td>
				<td class="value">
					<span id="barcodeError" class="error"></span>
					<form:errors path="item.barcode" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Serialize</td>
				<td class="value">
					<form:checkbox path="serialItemSetup.serializedItem"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Active</td>
				<td class="value">
					<form:checkbox path="item.active"/>
				</td>
			</tr>
		</table>
		<br>
	</fieldset>
	<fieldset class="frmField_set">
	<legend>Branches/Companies</legend>
		<table id="tblBranchFrm" class="dataTable">
			<thead>
				<tr>
					<th width="5px;" style="border-right: none;"></th>
					<th ></th>
					<th style="border-left: none;" width="40%">Name</th>
					<th style="border-left: none;" width="30%">Division</th>
					<th width="30%">Selling Price</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${serialItemSetupDto.item.itemSrps}" var="itemSrp" varStatus="status">
					<tr>
						<td style="border-left: none;">
							<img src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deleteRow(this)'/>
						</td>
						<td class='tdProperties' style="border-left: none;"></td>
						<td class='tdProperties' style="border-left: none;">
							<c:set var="companyName" value="${itemSrp.companyName}" />
							<c:if test="${itemSrp.company ne null}">
								<c:set var="companyName" value="${itemSrp.company.name}"/>
							</c:if>
							<input type='hidden' name='item.itemSrps[${status.index}].id' value='${itemSrp.id}' />
							<input type='hidden' name='item.itemSrps[${status.index}].companyId' value="${itemSrp.companyId}"
								id="hdnSpCompanyId${status.index}" />
							<input name='item.itemSrps[${status.index}].companyName' id='txtBCompanyName${status.index}'
								onkeyup='showSrpCompanyList(this.id, hdnSpCompanyId${status.index});'
								class='txtSrpCompanyNames' style='width: 100%;' value='${fn:escapeXml(companyName)}'
								<c:if test='${itemSrp.id ne null and itemSrp.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td class='tdProperties'>
							<input type='hidden' name='item.itemSrps[${status.index}].divisionId' value="${itemSrp.divisionId}"
								id="hdnDivisionId${status.index}" />
							<input name='item.itemSrps[${status.index}].divisionName' id='divisionName${status.index}' style='width: 100%;'
								class="srpDivision" onkeypress='showDivisions(this, ${status.index});' onblur='getDivision(this, ${status.index});'
								value='${fn:escapeXml(itemSrp.divisionName)}'/>
						</td>
						<td class='tdProperties'>
							<input name='item.itemSrps[${status.index}].srp' id='srp${status.index}' style='width: 100%;
								text-align: right;' class='txtSrp' onblur='formatMoney(this);' maxLength='13' value='${itemSrp.srp}' />
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4"><form:errors path="item.errorItemSrps" cssClass="error"/> </td>
				</tr>
			</tfoot>
		</table>
		<br>
	</fieldset>
	<br>
	<fieldset class="frmField_set hide">
		<legend>Discounts</legend>
		<table id="tblDiscountFrm" class="dataTable">
			<thead>
				<tr>
					<th width="35%">Branch/Company Name</th>
					<th width="30%">Name</th>
					<th width="20">Discount Type</th>
					<th width="15">Percentage/Amount</th>
					<th width="5%">Active</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${serialItemSetupDto.item.itemDiscounts}" var="itemDiscount" varStatus="status">
					<tr class="<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>divVeil</c:if>">
						<td class='tdProperties' style="border-left: none;">
							<c:set var="companyName" value="${itemDiscount.companyName}"/>
							<c:if test="${itemDiscount.company ne null}">
								<c:set var="companyName" value="${itemDiscount.company.name}"/>
							</c:if>
							<input type='hidden' name='item.itemDiscounts[${status.index}].id' value='${itemDiscount.id}'/>
							<input type='hidden' name='item.itemDiscounts[${status.index}].companyId' value="${itemDiscount.companyId}"
								id="hdnSpdCompId${status.index}"/>
							<input name='item.itemDiscounts[${status.index}].companyName' id='txtDCompanyName${status.index}'
								onkeyup='showDiscountCompanyList(this.id, hdnSpdCompId${status.index});'
								class='txtDCompanyNames' style='width: 100%;' value='${fn:escapeXml(companyName)}'
								<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td class='tdProperties'>
							<input name='item.itemDiscounts[${status.index}].name' id='txtName${status.index}' 
								class='txtNames' style='width: 100%;' value='${itemDiscount.name}'
								<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td>
							<input id="hdnSpDiscTypeId${status.index}" value='${itemDiscount.itemDiscountTypeId}'
								type='hidden' name='item.itemDiscounts[${status.index}].itemDiscountTypeId' />
							<c:set var="itemDiscountTypeId" value='${itemDiscount.itemDiscountTypeId}'/>
							<select class="frmSmallSelectClass" onchange="assignDiscountType(this.value, hdnSpDiscTypeId${status.index})"
								<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>
									style="pointer-events: none; cursor: default;"
								</c:if> >
								<c:forEach var="discountType" items="${itemDiscountTypes}">
									<option value="${discountType.id}" 
										<c:if test='${itemDiscountTypeId eq discountType.id}'>selected</c:if>> ${discountType.name}
									</option>
								</c:forEach>
							</select>
						</td>
						<td class='tdProperties'>
							<input name='item.itemDiscounts[${status.index}].value' id='value${status.index}' style='width: 100%;
								text-align: right;' class='txtValue' value='${itemDiscount.value}' maxLength='13'
			 					onkeydown='processDiscNextRow(this, event);' onblur='formatValue(this);'
			 					<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td class="tdProperties" style="border-right: none;">
							<input type='checkbox' checked='checked' name='item.itemDiscounts[${status.index}].active' 
								id='status${status.index}' style='width: 100%; text-align: right;' class='cbStatus' 
								value='${itemDiscount.active}' />
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="5"><form:errors path="item.errorItemDiscounts" cssClass="error"/> </td>
				</tr>
			</tfoot>
		</table>
		<br>
	</fieldset>
	<br>		
	<fieldset class="frmField_set hide">
		<legend>Add Ons</legend>
		<table id="tblAddOnFrm" class="dataTable">
			<thead>
				<tr>
					<th width="30%">Branch/Company Name</th>
					<th width="47%">Name</th>
					<th width="13">Amount</th>
					<th width="5%">Active</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${serialItemSetupDto.item.itemAddOns}" var="itemAddOn" varStatus="status">
					<tr class="<c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>divVeil</c:if>">
						<td class='tdProperties' style="border-left: none;">
							<c:set var="companyName" value="${itemAddOn.companyName}"/>
							<c:if test="${itemAddOn.company ne null}">
								<c:set var="companyName" value="${itemAddOn.company.name}"/>
							</c:if>
							<input type="hidden" name='item.itemAddOns[${status.index}].id' value='${itemAddOn.id}'/>
							<input type="hidden" name='item.itemAddOns[${status.index}].companyId'
								value="${itemAddOn.companyId}" id="hdnSpaCompId${status.index}"/>
							<input name='item.itemAddOns[${status.index}].companyName' id='txtACompanyName${status.index}' style='width: 100%;'
								onkeyup='showAddOnCompanyList(this.id, hdnSpaCompId${status.index});' class='txtACompanyNames'
								value='${fn:escapeXml(companyName)}' <c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td class='tdProperties'>
							<input name='item.itemAddOns[${status.index}].name' id='txtName${status.index}'
								class='txtNames' style='width: 100%;' value='${itemAddOn.name}'
								<c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td class='tdProperties'>
							<input name='item.itemAddOns[${status.index}].value' id='value${status.index}' style='width: 100%; 
								text-align: right;' class='txtValue' value='${itemAddOn.value}' maxLength='13'
			 					onkeydown='processAddOnNextRow(this, event);' onblur='formatValue(this);'
			 					<c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td class="tdProperties" style="border-right: none;">
							<input type='checkbox' checked='checked' name='item.itemAddOns[${status.index}].active'
								id='status${status.index}' style='width: 100%; text-align: right;' class='cbStatus'
								value='${itemAddOn.active}' />
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="5"><form:errors path="item.errorItemAddOns" cssClass="error"/> </td>
				</tr> 
			</tfoot>
		</table>
		<br>
	</fieldset>		
	<br>		
	<table class="buttonClss">
		<tr>
			<td align="right" >
				<input type="button" id="btnSaveRItem" value="Save" onclick="saveRItem();"/>
				<input type="button" id="btnCancelSave" value="Cancel" onclick="cancelSave();"/>
			</td>
		</tr>
	</table>
	</div>
	</form:form>
</div>
</body>
</html>