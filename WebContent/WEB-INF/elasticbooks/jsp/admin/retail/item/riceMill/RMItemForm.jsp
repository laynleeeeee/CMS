<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../../jsp/include.jsp" %>
<!--

	Description: Retail Item for rice milling form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/tabsUtil.js"></script>
<style type="text/css">
.txtSrpCompanyNames, .txtBpCompanyNames, .txtSrp,
.txtDCompanyNames, .txtBpDCompanyNames, .txtACompanyNames,
.txtBpACompanyNames, .txtNames, .txtValue, .txtWp {
	width: 100%;
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}

.divVeil {
	background-color: #F2F1F0;
}
</style>
<script type="text/javascript">
var minimumRows = 4;
var srpCurrentIndex = "${fn:length(item.itemSrps)}";
var discountCurrentIndex = "${fn:length(item.itemDiscounts)}";
var addOnCurrentIndex = "${fn:length(item.itemAddOns)}";
var bPriceCurrIndex = "${fn:length(item.buyingPrices)}";
var bDiscCurrIndex = "${fn:length(item.buyingDiscounts)}";
var bAddOnCurrIndex = "${fn:length(item.buyingAddOns)}";

var SP_COMPANY_CLASS = "txtSrpCompanyNames";
var SP_DISCOUNT_CLASS = "txtDCompanyNames";
var SP_ADD_ON_CLASS = "txtACompanyNames";
var BP_COMPANY_CLASS = "txtBpCompanyNames";
var BP_DISCOUNT_CLASS = "txtBpDCompanyNames";
var BP_ADD_ON_CLASS = "txtBpACompanyNames";
$(document).ready (function () {
	$("#txtReorderingPoint").bind("keyup keydown", function(e) {
		// 46 = DELETE button. If Delete button is not pressed
		if(e.keyCode != 46){
			inputOnlyNumeric("txtReorderingPoint");
		}
	});

	if ("${item.id}" != 0) {
		$(".cbStatus").each(function () {
			var checked = $(this).val();
			if (checked == "false")
				$(this).attr("checked", false);
		});
	}

	//Load Selling Details
	addSRPLine(getRows(srpCurrentIndex));
	addDiscountLine (getRows(discountCurrentIndex));
	addAddOnLine(getRows(addOnCurrentIndex));

	//Load Buying Details
	addBuyingPriceLine(getRows(bPriceCurrIndex));
	addBuyingDiscount (getRows(bDiscCurrIndex));
	addBuyingAddOnLine(getRows(bAddOnCurrIndex));

	formatMonetaryVal();
	hideShowContents("divTabSellingDetails", true);
// 	console.log("On load. price index: "+bPriceCurrIndex+" disc index: "+bDiscCurrIndex+" addon index: "+bAddOnCurrIndex);
});

function getRows(index) {
	var rows = minimumRows > index ? minimumRows - index : 1;
// 	console.log("rows: "+rows);
	return rows;
}

function hideShowContents(divElemId, isSelling) {
	$("#"+divElemId).show();
	if(isSelling == true) {
		$("#divTabBuyingDetails").hide();
	} else {
		$("#divTabSellingDetails").hide();
	}
	processTabOnClick();
}

function addSRPLine (numOfRows) {
	addPriceLine("itemSrps", "tblBranchFrm", "txtBCompanyName", SP_COMPANY_CLASS,
			"hdnSpCompanyId", true, srpCurrentIndex, numOfRows);
}

function addBuyingPriceLine(numOfRows) {
	addPriceLine("buyingPrices", "tblBuyingBranchFrm", "txtBpCompanyName", BP_COMPANY_CLASS,
			"hdnBpCompanyId", false, bPriceCurrIndex, numOfRows);
}

function addPriceLine (tableName, tblElemName, companyElemId, companyElemClass,
		hdnCompId, isSelling, currentIndex, numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";

		//Empty TD
		newRow += "<td class='tdProperties' style='border-left: none; border-right: none;'>";
		newRow += "<span id='spanEmptyTd"+currentIndex+"'></span>";
		newRow += "</td>";

		//Company Name
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<input name='"+tableName+"["+currentIndex+"].companyId' id='"+hdnCompId+""+currentIndex+"' type='hidden'/>";
		newRow += "<input name='"+tableName+"["+currentIndex+"].companyName' id='"+companyElemId+""+currentIndex+"' \
			onkeyup='showSrpCompanyList(this.id, "+isSelling+", "+hdnCompId+""+currentIndex+");' onblur='removeDuplicateSrpCompany(this, "+isSelling+");' \
			class='"+companyElemClass+"' style='width: 100%;'/>";
		newRow += "</td>";

		if(isSelling) {
			//SRP
			newRow += "<td class='tdProperties'>";
			newRow += "<input name='"+tableName+"["+currentIndex+"].srp' id='srp"+currentIndex+"' style='width: 100%; text-align: right;' class='txtSrp' ";
			newRow += "onblur='formatMoney(this);' maxLength='13'/>";
			newRow += "</td>";
		} else {
			//Buying price
			newRow += "<td class='tdProperties' style='border-right: none;'>";
			newRow += "<input name='"+tableName+"["+currentIndex+"].buyingPrice' id='bp"+currentIndex+"' style='width: 100%; text-align: right;' class='txtSrp' ";
			newRow += "onkeydown='processCompNextRow(this, event, "+isSelling+");' ";
			newRow += "onblur='formatMoney(this);' maxLength='13'/>";
			newRow += "</td>";
		}

		newRow += "</tr>";
		$("#"+tblElemName+" tbody").append(newRow);
		currentIndex++;
	}

	//Set the current index
	isSelling ? srpCurrentIndex = (currentIndex-1) : bPriceCurrIndex = (currentIndex-1);
// 	console.log("isSelling: "+isSelling+" srp index: "+srpCurrentIndex+" bp: "+bPriceCurrIndex);
}

function addDiscountLine(numOfRows) {
	addDiscount ("itemDiscounts", "tblDiscountFrm", "txtDCompanyName", SP_DISCOUNT_CLASS,
			"hdnSpdCompId", "hdnSpDiscTypeId", true, discountCurrentIndex, numOfRows);
}

function addBuyingDiscount(numOfRows) {
	addDiscount ("buyingDiscounts", "tblBuyingDiscountFrm", "txtBpDiscCompName", BP_DISCOUNT_CLASS,
			"hdnBpdCompId", "hdnBpDiscTypeId", false, bDiscCurrIndex, numOfRows);
}

function addDiscount (tableName, tblElemId, companyElemId, companyElemClass,
		hdnCompId, hdnDiscTypeId, isSelling, currentIndex, numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";

		//Company Name
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<input name='"+tableName+"["+currentIndex+"].companyId' id='"+hdnCompId+""+currentIndex+"' type='hidden'/>"; //Hidden Company Id
		newRow += "<input name='"+tableName+"["+currentIndex+"].companyName' id='"+companyElemId+""+currentIndex+"' \
					onkeyup='showDiscountCompanyList(this.id, "+isSelling+", "+hdnCompId+""+currentIndex+");' class='"+companyElemClass+"' style='width: 100%;'/>";
		newRow += "</td>";

		//Name
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='"+tableName+"["+currentIndex+"].name' id='txtName"+currentIndex+"' class='txtNames' style='width: 100%;'/>";
		newRow += "</td>";

		//Discount Type
		newRow += "<td class='tdProperties'>";
		newRow += "<input id='"+hdnDiscTypeId+""+currentIndex+"' type='hidden' name='"+tableName+"["+currentIndex+"].itemDiscountTypeId' value='1' />";
		newRow += "<select class='frmSmallSelectClass' onchange='assignDiscountType(this.value, "+hdnDiscTypeId+""+currentIndex+")'> ";
		newRow += "<c:forEach var='discType' items='${itemDiscountTypes}'>";
		newRow += "<option value='${discType.id}'>${discType.name}</option>";
		newRow += "</c:forEach>";
		newRow += "</select>";
		newRow += "</td>";

		//Percentage/Amount
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='"+tableName+"["+currentIndex+"].value' id='value"+currentIndex+"' style='width: 100%; text-align: right;' class='txtValue' \
			 		onkeydown='processDiscNextRow(this, event, "+isSelling+");' onblur='formatValue(this);' maxLength='13'/>";
		newRow += "</td>";

		//Active
		newRow += "<td class='tdProperties' style='border-right: none;'>";
		newRow += "<input type='checkbox' checked='checked' name='"+tableName+"["+currentIndex+"].active' id='status"+currentIndex+"' \
					style='width: 100%; text-align: right;' class='cbStatus' />";
		newRow += "</td>";

		newRow += "</tr>";
		$("#"+tblElemId+" tbody").append(newRow);
		currentIndex++;
	}

	//Set the current index
	isSelling ? discountCurrentIndex = (currentIndex-1) : bDiscCurrIndex = (currentIndex-1);
}

function addAddOnLine (numOfRows) {
	addAddOn ("itemAddOns", "tblAddOnFrm","txtACompanyName", SP_ADD_ON_CLASS,
			"hdnSpaCompId", addOnCurrentIndex, true, numOfRows);
}

function addBuyingAddOnLine (numOfRows) {
	addAddOn ("buyingAddOns", "tblBuyingAddOnFrm", "txtBACompanyName", BP_ADD_ON_CLASS,
			"hdnBpaCompId", bAddOnCurrIndex, false, numOfRows);
}

function addAddOn (tableName, tblElemId, companyElemId, companyElemClass,
		hdnCompId, currentIndex, isSelling, numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";

		//Company Name
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<input name='"+tableName+"["+currentIndex+"].companyId' id='"+hdnCompId+""+currentIndex+"' type='hidden'/>";
		newRow += "<input name='"+tableName+"["+currentIndex+"].companyName' id='"+companyElemId+""+currentIndex+"' \
					onkeyup='showAddOnCompanyList(this.id, "+isSelling+", "+hdnCompId+""+currentIndex+");' class='"+companyElemClass+"' style='width: 100%;'/>";
		newRow += "</td>";

		//Name
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='"+tableName+"["+currentIndex+"].name' id='txtName"+currentIndex+"' class='txtNames' style='width: 100%;'/>";
		newRow += "</td>";

		//Amount
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='"+tableName+"["+currentIndex+"].value' id='value"+currentIndex+"' style='width: 100%; text-align: right;' class='txtValue' \
					onkeydown='processAddOnNextRow(this, event, "+isSelling+");' onblur='formatValue(this);' maxLength='13'/>";
		newRow += "</td>";

		//Active
		newRow += "<td class='tdProperties' style='border-right: none;'>";
		newRow += "<input type='checkbox' checked='checked' name='"+tableName+"["+currentIndex+"].active' id='status"+currentIndex+"' \
					style='width: 100%; text-align: right;' class='cbStatus' />";
		newRow += "</td>";

		newRow += "</tr>";
		$("#"+tblElemId+" tbody").append(newRow);
		currentIndex++;
	}

	//Set the current index
	isSelling ? addOnCurrentIndex = (currentIndex-1) : bAddOnCurrIndex = (currentIndex-1);
}

function showCompanyList (elemId, companyclass, hdnCompId, spanError, isExcludeCompanies, isFilterCompanies) {
	var companyName = $.trim($("#"+elemId).val());
// 	console.log(companyName+" hdn compId: "+hdnCompId);
	var uri = contextPath + "/getCompany/filter?companyName=" + companyName;
	var row = $("#"+elemId).parent("td").parent("tr");
	var rowNumber = row.rowNumber;
	var cNames = "";
	$("." + companyclass).each(function (i) {
		if (i != rowNumber)
			cNames +=  $.trim($(this).val()) != "" ? encodeURIComponent($.trim($(this).val())) + ";" : "";
	});
	if (isExcludeCompanies)
		uri += "&companyNames="+cNames;
	if (isFilterCompanies)
		uri += "&filterCNames="+cNames;

	$("#"+elemId).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(this).val(ui.item.name);
			$(hdnCompId).val(ui.item.id);
// 			console.log("SELECT. set the id: "+ui.item.id+" to hdnCompId: "+hdnCompId);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.name);
						$(hdnCompId).val(ui.item.id);
// 						console.log("SUCCESS. set the id: "+ui.item.id+" to hdnCompId: "+hdnCompId);
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

function showSrpCompanyList(elemId, isSelling, hdnCompId) {
// 	console.log(hdnCompId);
// 	console.log("showSrpCompanyList. hdn compId:"+hdnCompId);
	showCompanyList(elemId, getCompElemName(isSelling), hdnCompId, "spanErrorItemSrps", true, false);
}

function showDiscountCompanyList(elemId, isSelling, hdnCompId) {
	var companyClass = getCompElemName(isSelling);
	var hasCompanies = false;
	$("."+companyClass).each(function (i) {
		if ($.trim($(this).val()) != "")
			hasCompanies = true;
	});
// 	console.log("showDiscountCompanyList. isSelling: "+isSelling+" has companies: "+hasCompanies+" comp class: "+companyClass);
	if (hasCompanies) {
		showCompanyList(elemId, companyClass, hdnCompId, "spanErrorItemDiscounts", false, true);
	}
}

function showAddOnCompanyList(elemId, isSelling, hdnCompId) {
	var hasCompanies = false;
	var companyClass = getCompElemName(isSelling);
	$("."+companyClass).each(function (i) {
		if ($.trim($(this).val()) != "")
			hasCompanies = true;
	});

// 	console.log("showAddOnCompanyList. isSelling: "+isSelling+" has companies: "+hasCompanies+" comp class: "+companyClass);
	if (hasCompanies) {
		showCompanyList(elemId, companyClass, hdnCompId, "spanErrorItemAddOns", false, true);
	}
}

function processCompNextRow(srp, event, isSelling) {
	var tblName = "itemSrps";
	var tblElemName = "tblBranchFrm";
	var elemId = "txtBCompanyName";
	var hdnCompId = "hdnSpCompanyId";
	if(!isSelling) {
		tblName = "buyingPrices";
		tblElemName = "tblBuyingBranchFrm";
		elemId= "txtBpCompanyName";
		hdnCompId = "hdnBpCompanyId";
	}
	var srpId = $(srp).attr("id");
	var currentIndex = isSelling ? Number(srpId.replace("wp", "")) : Number(srpId.replace("bp", ""));
// 	console.log(currentIndex+" tblName: "+tblName+" tblElem: "+tblElemName+" elemId: "
// 			+elemId+" hdnCompId: "+hdnCompId+" srpId: "+srpId+" currentIndex: "+currentIndex);
	if(event.keyCode == 9 && !event.shiftKey) { //Tab key
		var rowCount = $("#"+tblElemName+" tbody tr").length;
		var companyName = $("#"+elemId+currentIndex).val();
		if(isSelling == false) {
			companyName = $("#"+elemId+currentIndex).val();
		}
		if(typeof companyName != "undefined"|| companyName != "") {
// 			console.log("currentIndex "+(currentIndex+1)+" rowCount"+rowCount);
			if((currentIndex+1) < rowCount) {
				var nextRow = $(srp).closest("tr").next();
				if(nextRow.hasClass("divVeil")) {
					enableEntries ($(nextRow));
				}
				return;
			} else {
				addPriceLine(tblName, tblElemName, elemId, isSelling ? SP_COMPANY_CLASS : BP_COMPANY_CLASS,
						hdnCompId, isSelling, (currentIndex+1), 1);
				$("#spanEmptyTd"+(currentIndex+1)).focus();
			}
		}
	}
}

function processDiscNextRow(value, event, isSelling) {
	var tblName = "itemDiscounts";
	var tblElemName = "tblDiscountFrm";
	var elemId= "txtDCompanyName";
	var hdnCompId = "hdnSpdCompId";
	var hdnDiscTypeId = "hdnSpDiscTypeId";
	if(!isSelling) {
		tblName = "buyingDiscounts";
		tblElemName = "tblBuyingDiscountFrm";
		elemId = "txtBpDiscCompName";
		hdnCompId = "hdnBpdCompId";
		hdnDiscTypeId = "hdnBpDiscTypeId";
	}

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
				addDiscount (tblName, tblElemName, elemId, isSelling ? SP_DISCOUNT_CLASS : BP_DISCOUNT_CLASS,
						hdnCompId, hdnDiscTypeId, isSelling, (currentIndex+1), 1);
			}
		}
	}
}

function processAddOnNextRow(value, event, isSelling) {
	var tblName = "itemAddOns";
	var tblElemName = "tblAddOnFrm";
	var compElemId = "txtACompanyName";
	var hdnCompId = "hdnSpaCompId";
	if(!isSelling) {
		tblName = "buyingAddOns";
		tblElemName = "tblBuyingAddOnFrm";
		compElemId = "txtBACompanyName";
		hdnCompId = "hdnBpaCompId";
	}

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
				addAddOn (tblName, tblElemName, compElemId, isSelling ? SP_ADD_ON_CLASS : BP_ADD_ON_CLASS,
						hdnCompId, (currentIndex+1), isSelling, 1);
			}
		}
	}
}

function assignDiscountType(selectValue, elem) {
// 	console.log("Assign discount type: "+selectValue);
	$(elem).val(selectValue);
	var textbox = $(elem).parent("td").parent("tr").closest("tr").find(".txtValue");
	if (selectValue  != 1) {
		formatMoney(textbox);
	} else {
		formatPercentage(textbox);
	}
}

function removeDuplicateSrpCompany(elem, isSelling) {
	var companyElemId = getCompElemName(isSelling);
	var companyName = $.trim($(elem).val());
	var row = $(elem).parent("td").parent("tr").index();
	$("."+companyElemId).each(function (i) {
		if (companyName.toUpperCase() == $.trim($(this).val().toUpperCase()) && row != i){
			$(elem).val("");
		}
	});
}

function getCompElemName(isSelling) {
	return isSelling ? SP_COMPANY_CLASS : BP_COMPANY_CLASS;
}
</script>
</head>
<body>
<div class="formBigDiv">
	<form:form method="POST" commandName="item" id="rItemForm">
		<div class="modFormLabel">Item</div>
	<br>
	<div class="modForm">
	<fieldset class="frmField_set">
	<legend>Item Information </legend>
		<table class="formTable">
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<tr>
				<td class="labels">* Stock Code</td>
				<td class="value"><form:input path="stockCode" cssClass="numeric"
						cssStyle="width: 120px;"/></td>
			</tr>
			<tr>
				<td class="value"> </td>
				<td>
					<form:errors path="stockCode" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">* Unit of Measure</td>
				<td class="value">
					<form:select path="unitMeasurementId" cssClass="frmSelectClass">
						<form:options items="${unitMeasurements}" itemValue="id" itemLabel="name" />
					</form:select>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<form:errors path="unitMeasurementId" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">* Category</td>
				<td class="value">
					<form:select path="itemCategoryId" cssClass="frmSelectClass">
						<form:options items="${itemCategories}" itemValue="id" itemLabel="name" />
					</form:select>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<form:errors path="itemCategoryId" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">* Description</td>
				<td class="value"><form:input path="description" cssClass="input"/></td>
			</tr>
			<tr>
				<td class="value"> </td>
				<td>
					<form:errors path="description" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Reordering Point</td>
				<td class="value">
					<form:input path="reorderingPoint" id="txtReorderingPoint" cssClass="numeric"
							maxlength="10" type="text"/>
				</td>
			</tr>
			<tr>
				<td class="labels">Active</td>
				<td class="value">
					<form:checkbox path="active"/>
				</td>
			</tr>
		</table>
		<br>
	</fieldset>
	<br>
	<div id="divSGComponents" class="divTab frmField_set">
		<ul class="tabs" style="padding-bottom: 0px; border-bottom: 1px;">
			<li class="tab-link current" data-tab="divTabSellingDetails" 
				onclick="hideShowContents('divTabSellingDetails', true); ">Selling</li>
			<li class="tab-link" data-tab="divTabBuyingDetails" 
				onclick="hideShowContents('divTabBuyingDetails', false);">Buying</li>
		</ul>
	</div>
	<fieldset class="frmField_set">
		<div id="divTabSellingDetails" class="tab-content current">
		<fieldset class="frmField_set">
			<legend>Branches/Companies</legend>
				<table id="tblBranchFrm" class="dataTable">
					<thead>
						<tr>
							<th style="border-right: none;"></th>
							<th style="border-left: none;" width="70%">Name</th>
							<th width="30%">Selling Price</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${item.itemSrps}" var="itemSrp" varStatus="status">
							<tr>
								<td style="border-left: none; border-right: none;"></td>
								<td class='tdProperties' style="border-left: none;">
									<c:set var="companyName" value="${itemSrp.companyName}" />
									<c:if test="${itemSrp.company ne null}">
										<c:set var="companyName" value="${itemSrp.company.name}"/>
									</c:if>
									<input type='hidden' name='itemSrps[${status.index}].id' value='${itemSrp.id}' />
									<input type='hidden' name='itemSrps[${status.index}].companyId' value="${itemSrp.companyId}"
										id="hdnSpCompanyId${status.index}" />
									<input name='itemSrps[${status.index}].companyName' id='txtBCompanyName${status.index}'
										onkeyup='showSrpCompanyList(this.id, true, hdnSpCompanyId${status.index});'
										class='txtSrpCompanyNames' style='width: 100%;' onblur='removeDuplicateSrpCompany(this, true);' value='${fn:escapeXml(companyName)}'
										<c:if test='${itemSrp.id ne null and itemSrp.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class='tdProperties'>
									<input name='itemSrps[${status.index}].srp' id='srp${status.index}' style='width: 100%;
										text-align: right;' class='txtSrp' onblur='formatMoney(this);' maxLength='13' value='${itemSrp.srp}' />
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2"><form:errors path="errorItemSrps" cssClass="error"/> </td>
						</tr>
					</tfoot>
				</table>
				<br>
			</fieldset>
			<fieldset class="frmField_set">
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
						<c:forEach items="${item.itemDiscounts}" var="itemDiscount" varStatus="status">
							<tr class="<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>divVeil</c:if>">
								<td class='tdProperties' style="border-left: none;">
									<c:set var="companyName" value="${itemDiscount.companyName}"/>
									<c:if test="${itemDiscount.company ne null}">
										<c:set var="companyName" value="${itemDiscount.company.name}"/>
									</c:if>
									<input type='hidden' name='itemDiscounts[${status.index}].id' value='${itemDiscount.id}'/>
									<input type='hidden' name='itemDiscounts[${status.index}].companyId' value="${itemDiscount.companyId}"
										id="hdnSpdCompId${status.index}"/>
									<input name='itemDiscounts[${status.index}].companyName' id='txtDCompanyName${status.index}'
										onkeyup='showDiscountCompanyList(this.id, true, hdnSpdCompId${status.index});'
										class='txtDCompanyNames' style='width: 100%;' value='${fn:escapeXml(companyName)}'
										<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class='tdProperties'>
									<input name='itemDiscounts[${status.index}].name' id='txtName${status.index}' 
										class='txtNames' style='width: 100%;' value='${itemDiscount.name}'
										<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td>
									<input id="hdnSpDiscTypeId${status.index}" value='${itemDiscount.itemDiscountTypeId}'
										type='hidden' name='itemDiscounts[${status.index}].itemDiscountTypeId' />
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
									<input name='itemDiscounts[${status.index}].value' id='value${status.index}' style='width: 100%;
										text-align: right;' class='txtValue' value='${itemDiscount.value}' maxLength='13'
					 					onkeydown='processDiscNextRow(this, event, true);' onblur='formatValue(this);'
					 					<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class="tdProperties" style="border-right: none;">
									<input type='checkbox' checked='checked' name='itemDiscounts[${status.index}].active' 
										id='status${status.index}' style='width: 100%; text-align: right;' class='cbStatus' 
										value='${itemDiscount.active}' />
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="5"><form:errors path="errorItemDiscounts" cssClass="error"/> </td>
						</tr>
					</tfoot>
				</table>
				<br>
			</fieldset>

			<fieldset class="frmField_set">
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
						<c:forEach items="${item.itemAddOns}" var="itemAddOn" varStatus="status">
							<tr class="<c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>divVeil</c:if>">
								<td class='tdProperties' style="border-left: none;">
									<c:set var="companyName" value="${itemAddOn.companyName}"/>
									<c:if test="${itemAddOn.company ne null}">
										<c:set var="companyName" value="${itemAddOn.company.name}"/>
									</c:if>
									<input type="hidden" name='itemAddOns[${status.index}].id' value='${itemAddOn.id}'/>
									<input type="hidden" name='itemAddOns[${status.index}].companyId'
										value="${itemAddOn.companyId}" id="hdnSpaCompId${status.index}"/>
									<input name='itemAddOns[${status.index}].companyName' id='txtACompanyName${status.index}' style='width: 100%;'
										onkeyup='showAddOnCompanyList(this.id, true, hdnSpaCompId${status.index});' class='txtACompanyNames'
										value='${fn:escapeXml(companyName)}' <c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class='tdProperties'>
									<input name='itemAddOns[${status.index}].name' id='txtName${status.index}'
										class='txtNames' style='width: 100%;' value='${itemAddOn.name}'
										<c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class='tdProperties'>
									<input name='itemAddOns[${status.index}].value' id='value${status.index}' style='width: 100%; 
										text-align: right;' class='txtValue' value='${itemAddOn.value}' maxLength='13'
					 					onkeydown='processAddOnNextRow(this, event);' onblur='formatValue(this);'
					 					<c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class="tdProperties" style="border-right: none;">
									<input type='checkbox' checked='checked' name='itemAddOns[${status.index}].active'
										id='status${status.index}' style='width: 100%; text-align: right;' class='cbStatus'
										value='${itemAddOn.active}' />
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="5"><form:errors path="errorItemAddOns" cssClass="error"/> </td>
						</tr> 
					</tfoot>
				</table>
				<br>
			</fieldset>
		</div>
		<div id="divTabBuyingDetails" class="tab-content">
			<fieldset class="frmField_set">
				<legend>Branches/Companies</legend>
				<table id="tblBuyingBranchFrm" class="dataTable">
					<thead>
						<tr>
							<th style="border-right: none;"></th>
							<th style="border-left: none;" width="65%">Name</th>
							<th width="35%">Buying Price</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${item.buyingPrices}" var="bp" varStatus="status">
							<tr>
								<td style="border-left: none; border-right: none;"></td>
								<td class='tdProperties' style="border-left: none;">
									<c:set var="companyName" value="${bp.companyName}"/>
									<c:if test="${bp.company ne null}">
										<c:set var="companyName" value="${bp.company.name}"/>
									</c:if>
									<input type='hidden' name='buyingPrices[${status.index}].id' value='${bp.id}'/>
									<input type='hidden' name='buyingPrices[${status.index}].companyId'
										value="${bp.companyId}" id="hdnBpCompanyId${status.index}"/>
									<input name='buyingPrices[${status.index}].companyName' id='txtBpCompanyName${status.index}'
										onkeyup='showSrpCompanyList(this.id, false, txtBpCompanyName${status.index});'
										class='txtBpCompanyNames' style='width: 100%;' value='${fn:escapeXml(companyName)}'
										onblur='removeDuplicateSrpCompany(this, false);'
										<c:if test='${bp.id ne null and bp.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class="tdProperties" style="border-right: none;">
									<input name='buyingPrices[${status.index}].buyingPrice' id='bp${status.index}' style='width: 100%;
										text-align: right;' class='txtSrp' onblur='formatMoney(this);' maxLength='13'
					 					onkeydown="processCompNextRow(this, event, false);" value='${bp.buyingPrice}' />
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="5"><form:errors path="buyingPrices" cssClass="error"/> </td>
						</tr>
					</tfoot>
				</table>
				<br>
			</fieldset>
			<fieldset class="frmField_set">
			<legend>Discounts</legend>
				<table id="tblBuyingDiscountFrm" class="dataTable">
					<thead>
						<tr>
							<th width="30%">Branch/Company Name</th>
							<th width="35%">Name</th>
							<th width="20">Discount Type</th>
							<th width="15">Percentage/Amount</th>
							<th width="5%">Active</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${item.buyingDiscounts}" var="bd" varStatus="status">
							<tr class="<c:if test='${bd.id ne null and bd.id ne 0}'>divVeil</c:if>">
								<td class='tdProperties' style="border-left: none;">
									<c:set var="companyName" value="${bd.companyName}"/>
									<c:if test="${bd.company ne null}">
										<c:set var="companyName" value="${bd.company.name}"/>
									</c:if>
									<input type='hidden' name='buyingDiscounts[${status.index}].id' value='${bd.id}'/>
									<input type='hidden' name='buyingDiscounts[${status.index}].companyId'
										value="${bd.companyId}" id="hdnBpdCompId${status.index}"/>
									<input name='buyingDiscounts[${status.index}].companyName' id='txtBpDiscCompName${status.index}'
										onkeyup='showDiscountCompanyList(this.id, false, hdnBpdCompId${status.index});'
										class='txtBpDCompanyNames' style='width: 100%;' value='${fn:escapeXml(companyName)}'
										<c:if test='${bd.id ne null and bd.id ne 0}'>readonly='readonly'</c:if> />
								</td>

								<td class='tdProperties'>
									<input name='buyingDiscounts[${status.index}].name' id="txtName${status.index}"
										class='txtNames' style='width: 100%;' value='${bd.name}'
										<c:if test='${bd.id ne null and bd.id ne 0}'>readonly='readonly'</c:if> />
								</td>

								<td>
									<input id="hdnBpDiscTypeId${status.index}" value="${bd.itemDiscountTypeId}"
										type='hidden' name='buyingDiscounts[${status.index}].itemDiscountTypeId' />
									<c:set var="itemDiscountTypeId" value='${bd.itemDiscountTypeId}'/>
									<select class="frmSmallSelectClass"
										onchange="assignDiscountType(this.value, hdnBpDiscTypeId${status.index})"
										<c:if test='${bd.id ne null and bd.id ne 0}'>
											style="pointer-events: none; cursor: default;"
										</c:if> >
										<c:forEach var="discountType" items="${itemDiscountTypes}">
											<option value="${discountType.id}" 
												<c:if test='${itemDiscountTypeId eq discountType.id}'>selected</c:if>> ${discountType.name}
											</option>
										</c:forEach>
									</select>
								</td>

								<td class="tdProperties">
									<input name="buyingDiscounts[${status.index}].value" id="value${status.index}"
										style="width: 100%; text-align: right;" class="txtValue" onblur="formatValue(this);"
					 					onkeydown="processDiscNextRow(this, event, false);" maxLength="13" value="${bd.value}"
					 					<c:if test='${bd.id ne null and bd.id ne 0}'>readonly='readonly'</c:if> />
								</td>

								<td class="tdProperties" style="border-right: none;">
									<input type="checkbox" checked="checked" name="buyingDiscounts[${status.index}].active"
										id="status${status.index}" style="width: 100%; text-align: right;" class="cbStatus" value="${bd.active}" />
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="5"><form:errors path="buyingDiscounts" cssClass="error"/> </td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
			<legend>Add Ons</legend>
				<table id="tblBuyingAddOnFrm" class="dataTable">
					<thead>
						<tr>
							<th width="35%">Branch/Company Name</th>
							<th width="40%">Name</th>
							<th width="15">Amount</th>
							<th width="5%">Active</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${item.buyingAddOns}" var="ba" varStatus="status">
							<tr class="<c:if test='${ba.id ne null and ba.id ne 0}'>divVeil</c:if>">
								<td class='tdProperties' style="border-left: none;">
									<c:set var="companyName" value="${ba.companyName}"/>
									<c:if test="${ba.company ne null}">
										<c:set var="companyName" value="${ba.company.name}"/>
									</c:if>
									<input type="hidden" name="buyingAddOns[${status.index}].id" value="${ba.id}"/>
									<input type="hidden" name="buyingAddOns[${status.index}].companyId"
										value="${ba.companyId}" id="hdnBpaCompId${status.index});"/>
									<input name="buyingAddOns[${status.index}].companyName" id="txtBACompanyName${status.index}" 
										onkeyup="showAddOnCompanyList(this.id, false, hdnBpaCompId${status.index});"
										class="txtBpACompanyNames" style='width: 100%;' value='${fn:escapeXml(companyName)}'
										<c:if test='${ba.id ne null and ba.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class='tdProperties'>
									<input name='buyingAddOns[${status.index}].name' id='txtName${status.index}'
										class='txtNames' style='width: 100%;' value='${ba.name}'
										<c:if test='${ba.id ne null and ba.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class='tdProperties'>
									<input name='buyingAddOns[${status.index}].value' id='value${status.index}' style='width: 100%; 
										text-align: right;' class='txtValue' value='${ba.value}' maxLength='13'
					 					onkeydown='processAddOnNextRow(this, event, false);' onblur='formatValue(this);'
					 					<c:if test='${ba.id ne null and ba.id ne 0}'>readonly='readonly'</c:if> />
								</td>
								<td class="tdProperties" style="border-right: none;">
									<input type='checkbox' checked='checked' name='buyingAddOns[${status.index}].active'
										id='status${status.index}' style='width: 100%; text-align: right;' class="cbStatus"
										value='${ba.active}' />
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="5"><form:errors path="buyingAddOns" cssClass="error"/> </td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
		</div>
	</fieldset>
	<table class="buttonClss">
		<tr>
			<td align="right" >
				<input type="button" id="btnSaveRItem" value="${item.id eq 0 ? 'Save' : 'Update'}" onclick="saveRItem();"/>
				<input type="button" id="btnCancelSave" value="Cancel" onclick="cancelSave();"/>
			</td>
		</tr>
	</table>
	</div>
	</form:form>
</div>
</body>
</html>