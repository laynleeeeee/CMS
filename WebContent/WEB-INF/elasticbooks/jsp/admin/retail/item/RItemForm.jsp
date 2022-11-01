<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Retail Item Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ybl/inputUtil.js"></script>
<title>Item Form</title>
<style type="text/css">
.frmSelectClass {
	width: 220px;
}

.dataTable thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

.frmSmallSelectClass {
	border: 1px solid gray;
	padding: 4px;
	font-weight: bold;
	text-transform: uppercase;
	background-color: #0000;
	width: 135px;
}

.txtBCompanyNames, .txtSrp, .txtDCompanyNames, .txtNames, .txtValue, .txtWp{
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

$(document).ready(function() {
	$("#txtReorderingPoint").bind("keyup keydown", function(e) {
		// 46 = DELETE button. If Delete button is not pressed
		if(e.keyCode != 46){ 
			inputOnlyNumeric("txtReorderingPoint");
		}
	});

	var srpRows = minimumRows > srpCurrentIndex ? minimumRows - srpCurrentIndex : 1;
	addSRPLine(srpRows);
	var discountRows = minimumRows > discountCurrentIndex ? minimumRows - discountCurrentIndex : 1;
	addDiscountLine (discountRows);
	var addOnRows = minimumRows > addOnCurrentIndex ? minimumRows - addOnCurrentIndex : 1;
	addAddOnLine(addOnRows);

	if ("${item.id}" != 0) {
		$(".cbStatus").each(function () {
			var checked = $(this).val();
			if (checked == "false")
				$(this).attr("checked", false);
		});
	}

	$("#tblBranchFrm thead tr").find("th:last").css("border-right", "none");
	$("#tblBranchFrm tbody tr").find("td:last").css("border-right", "none");
	$("#tblBranchFrm tbody tr:last").css("border-bottom", "1px solid #000000");
	$("#tblBranchFrm tbody").find("td").addClass("tdProperties");

	$("#tblDiscountFrm thead tr").find("th:last").css("border-right", "none");
	$("#tblDiscountFrm tbody tr").find("td:last").css("border-right", "none");
	$("#tblDiscountFrm tbody tr:last").css("border-bottom", "1px solid #000000");
	$("#tblDiscountFrm tbody").find("td").addClass("tdProperties");

	$("#tblAddOnFrm thead tr").find("th:last").css("border-right", "none");
	$("#tblAddOnFrm tbody tr").find("td:last").css("border-right", "none");
	$("#tblAddOnFrm tbody tr:last").css("border-bottom", "1px solid #000000");
	$("#tblAddOnFrm tbody").find("td").addClass("tdProperties");

	formatMonetaryVal();
});

function addSRPLine (numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";

		//Empty TD
		newRow += "<td class='tdProperties' style='border-left: none; border-right: none;'>";
		newRow += "<span id='spanEmptyTd"+srpCurrentIndex+"'></span>";
		newRow += "</td>";

		//Company Name
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<input name='itemSrps["+srpCurrentIndex+"].companyId' id='hdnSpCompanyId"+srpCurrentIndex+"' type='hidden'/>";
		newRow += "<input name='itemSrps["+srpCurrentIndex+"].companyName' id='txtBCompanyName"+srpCurrentIndex+"' onkeyup='showSrpCompanyList(this.id);' onblur='removeDuplicateSrpCompany(this);' class='txtBCompanyNames' style='width: 100%;'/>";
		newRow += "</td>"; 

		//SRP
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input name='itemSrps["+srpCurrentIndex+"].srp' id='srp"+srpCurrentIndex+"' style='width: 100%; text-align: right;' class='txtSrp' \
					onkeydown='processCompNextRow(this, event);' onblur='formatMoney(this);'  maxLength='13'/>";
 		newRow += "</td>";

		newRow += "</tr>";
		$("#tblBranchFrm tbody").append(newRow);
		srpCurrentIndex++;
	}
}

function addDiscountLine (numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";

		//Company Name
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<input name='itemDiscounts["+discountCurrentIndex+"].companyId' id='hdnSpdCompId"+discountCurrentIndex+"' type='hidden'/>";
		newRow += "<input name='itemDiscounts["+discountCurrentIndex+"].companyName' id='txtDCompanyName"+discountCurrentIndex+"' onkeyup='showDiscountCompanyList(this.id);' class='txtDCompanyNames' style='width: 100%;'/>"
		newRow += "</td>";

		//Name
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='itemDiscounts["+discountCurrentIndex+"].name' id='txtName"+discountCurrentIndex+"' class='txtNames' style='width: 100%;'/>";
		newRow += "</td>";

		//Discount Type
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input id='hdnIDType"+discountCurrentIndex+"' type='hidden' name='itemDiscounts["+discountCurrentIndex+"].itemDiscountTypeId' value='1' />";
		newRow += "<select class='frmSmallSelectClass' onchange='assignDiscountType(this.value, hdnIDType"+discountCurrentIndex+")'> ";
		newRow += "<option value='1'>Percentage</option>";
		newRow += "<option value='2'>Amount</option>";
		newRow += "<option value='3'>Quantity</option>";
		newRow += "</select>";
		newRow += "</td>";

		//Percentage/Amount
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input name='itemDiscounts["+discountCurrentIndex+"].value' id='value"+discountCurrentIndex+"' style='width: 100%; text-align: right;' class='txtValue' \
					onkeydown='processDiscNextRow(this, event);' onblur='formatValue(this);' maxLength='13'/>";
		newRow += "</td>";

		//Active
		newRow += "<td class='tdProperties' style='border-right: none;'>";
		newRow += "<input type='checkbox' checked='checked' name='itemDiscounts["+discountCurrentIndex+"].active' id='status"+discountCurrentIndex+"' style='width: 100%; text-align: right;' class='cbStatus' />";
		newRow += "</td>";

		newRow += "</tr>";
		$("#tblDiscountFrm tbody").append(newRow);
		discountCurrentIndex++;
	}
}

function addAddOnLine (numOfRows) {
	for (var i=0; i<numOfRows; i++){
		var newRow = "<tr id='trReadOnly'>";

		//Company Name
		newRow += "<td class='tdProperties' style='border-left: none;'>";
		newRow += "<input name='itemAddOns["+addOnCurrentIndex+"].companyId' id='hdnSpaCompId"+addOnCurrentIndex+"' type='hidden'/>";
		newRow += "<input name='itemAddOns["+addOnCurrentIndex+"].companyName' id='txtACompanyName"+addOnCurrentIndex+"' onkeyup='showAddOnCompanyList(this.id);' class='txtBCompanyNames' style='width: 100%;'/>";
		newRow += "</td>";

		//Name
		newRow += "<td class='tdProperties'>";
		newRow += "<input name='itemAddOns["+addOnCurrentIndex+"].name' id='txtName"+addOnCurrentIndex+"' class='txtNames' style='width: 100%;'/>";
		newRow += "</td>";

		//Amount
		newRow += "<td class='tdProperties' valign='top'>";
		newRow += "<input name='itemAddOns["+addOnCurrentIndex+"].value' id='value"+addOnCurrentIndex+"' style='width: 100%; text-align: right;' class='txtValue' \
					onkeydown='processAddOnNextRow(this, event);' onblur='formatValue(this);' maxLength='13'/>";
		newRow += "</td>";

		//Active
		newRow += "<td class='tdProperties' style='border-right: none;'>";
		newRow += "<input type='checkbox' checked='checked' name='itemAddOns["+addOnCurrentIndex+"].active' id='status"+addOnCurrentIndex+"' style='width: 100%; text-align: right;' class='cbStatus' />";
		newRow += "</td>";

		newRow += "</tr>";
		$("#tblAddOnFrm tbody").append(newRow);
		addOnCurrentIndex++;
	}
}

function showCompanyList (elemId, hdnCompId, companyclass, spanError, isExcludeCompanies, isFilterCompanies) {
	var companyName = $.trim($("#"+elemId).val());
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
			$("#"+hdnCompId).val(ui.item.id);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.name);
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

function showSrpCompanyList(elemId) {
	var rowNumber = elemId.replace("txtBCompanyName", "");
	showCompanyList(elemId, "hdnSpCompanyId"+rowNumber, "txtBCompanyNames",
			"spanErrorItemSrps", true, false);
}

function showDiscountCompanyList(elemId) {
	var hasCompanies = false;
	$(".txtBCompanyNames").each(function (i) {
		if ($.trim($(this).val()) != "")
			hasCompanies = true;
	});

	if (hasCompanies) {
		var rowNumber = elemId.replace("txtDCompanyName", "");
		showCompanyList(elemId, "hdnSpdCompId"+rowNumber, "txtBCompanyNames",
				"spanErrorItemDiscounts", false, true);
	}
}

function showAddOnCompanyList(elemId) {
	var hasCompanies = false;
	$(".txtBCompanyNames").each(function (i) {
		if ($.trim($(this).val()) != "")
			hasCompanies = true;
	});

	if (hasCompanies) {
		var rowNumber = elemId.replace("txtACompanyName", "");
		showCompanyList(elemId, "hdnSpaCompId"+rowNumber, "txtBCompanyNames",
				"spanErrorItemDiscounts", false, true);
	}
}

function showDiscountList(elemId) {
	var dtName = $("#"+elemId).val();
	var uri = contextPath + "/getItemDiscountType/all?name="+dtName;
	$("#"+elemId).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.name);
					}
					$("#spanErrorItemDiscounts").text("");
				},
				error : function(error) {

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

function processCompNextRow(srp, event) {
	if(event.keyCode == 9 && !event.shiftKey) { //Tab key
		var srpId = $(srp).attr("id");
		var currentRow = Number(srpId.substr(2, srpId.length - 1)) + 1;
		var rowCount = $("#tblBranchFrm tbody tr").length;
		var companyName = $("#txtBCompanyName"+(currentRow-1)).val();
		if(typeof companyName != "undefined"|| companyName != "") {
			if(currentRow < rowCount) {
				var nextRow = $(srp).closest("tr").next();
				if(nextRow.hasClass("divVeil")) {
					enableEntries ($(nextRow));
				}
			} else {
				addSRPLine(1);
				$("#spanEmptyTd"+currentRow).focus();
			}
		}
	}
}

function processDiscNextRow(value, event) {
	if(event.keyCode == 9 && !event.shiftKey) { //Tab key
		var valueId = $(value).attr("id");
		var currentRow = Number(valueId.substr(5, valueId.length - 1)) + 1;
		var rowCount = $("#tblDiscountFrm tbody tr").length;
		var companyName = $("#txtDCompanyName"+(currentRow-1)).val();
		if(typeof companyName != "undefined"|| companyName != "") {
			if(currentRow < rowCount) {
				var nextRow = $(value).closest("tr").next();
				if(nextRow.hasClass("divVeil")) {
					enableEntries ($(nextRow));
				}
			} else {
				addDiscountLine(1);
			}
		}
	}
}

function processAddOnNextRow(value, event) {
	if(event.keyCode == 9 && !event.shiftKey) { //Tab key
		var valueId = $(value).attr("id");
		var currentRow = Number(valueId.substr(5, valueId.length - 1)) + 1;
		var rowCount = $("#tblAddOnFrm tbody tr").length;
		var companyName = $("#txtACompanyName"+(currentRow-1)).val();
		if(typeof companyName != "undefined"|| companyName != "") {
			if(currentRow < rowCount) {
				var nextRow = $(value).closest("tr").next();
				if(nextRow.hasClass("divVeil")) {
					enableEntries ($(nextRow));
				}
			} else {
				addAddOnLine(1);
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

function removeDuplicateSrpCompany(elem) {
	var companyName = $.trim($(elem).val());
	var row = $(elem).parent("td").parent("tr").index();
	$(".txtBCompanyNames").each(function (i) {
		if (companyName.toUpperCase() == $.trim($(this).val().toUpperCase()) && row != i) 
			$(elem).val("");
	});
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
	<fieldset class="frmField_set">
	<legend>Branches/Companies</legend>
		<table id="tblBranchFrm" class="dataTable">
			<thead>
				<tr>
					<th style="border-right: none;"></th>
					<th style="border-left: none;" width="70%">Name</th>
					<th width="30%">SRP</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${item.itemSrps}" var="itemSrp" varStatus="status">
					<tr>
						<td style="border-left: none; border-right: none;"></td>
						<td class='tdProperties' style="border-left: none;">
							<c:set var="companyName" value="${itemSrp.companyName}"/>
							<c:if test="${itemSrp.company ne null}">
								<c:set var="companyName" value="${itemSrp.company.name}"/>
							</c:if>
							<input type='hidden' name='itemSrps[${status.index}].id' value='${itemSrp.id}'/>
							<input type='hidden' name='itemSrps[${status.index}].companyId' value="${itemSrp.companyId}"
										id="hdnSpCompanyId${status.index}" />
							<input name='itemSrps[${status.index}].companyName' id='txtBCompanyName${status.index}' 
								onkeyup='showSrpCompanyList(this.id);' class='txtBCompanyNames' style='width: 100%;'
								onblur='removeDuplicateSrpCompany(this);' value='${fn:escapeXml(companyName)}'
								<c:if test='${itemSrp.id ne null and itemSrp.id ne 0}'>readonly='readonly'</c:if> />
						</td>

						<td class='tdProperties' valign='top'>
							<input name='itemSrps[${status.index}].srp' id='srp${status.index}' style='width: 100%; 
								text-align: right;' class='txtSrp' onblur='formatMoney(this);' maxLength='13'
			 					value='${itemSrp.srp}' />
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
					<th width="30%">Branch/Company Name</th>
					<th width="35%">Name</th>
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
								onkeyup='showDiscountCompanyList(this.id);' class='txtDCompanyNames' style='width: 100%;'
								value='${fn:escapeXml(companyName)}'
								<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>readonly='readonly'</c:if> />
						</td>

						<td class='tdProperties'>
							<input name='itemDiscounts[${status.index}].name' id='txtName${status.index}' 
								class='txtNames' style='width: 100%;' 
								value='${itemDiscount.name}'
								<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>readonly='readonly'</c:if> />
						</td>

						<td>
							<input id="hdnIDType${status.index}" 
								type='hidden' name='itemDiscounts[${status.index}].itemDiscountTypeId' 
								value='${itemDiscount.itemDiscountTypeId}' />
							<c:set var="itemDiscountTypeId" value='${itemDiscount.itemDiscountTypeId}'/>
							<select class="frmSmallSelectClass" 
								onchange="assignDiscountType(this.value, hdnIDType${status.index})"
								<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>
									style="pointer-events: none; cursor: default;"
								</c:if> >
								<c:forEach var="discountType" items="${itemDiscountTypes}">
									<option value="${discountType.id}" 
										<c:if test='${itemDiscountTypeId eq discountType.id}'>selected</c:if>>
										${discountType.name}
									</option>
								</c:forEach>
							</select>
						</td>

						<td class='tdProperties' valign='top'>
							<input name='itemDiscounts[${status.index}].value' id='value${status.index}' style='width: 100%; 
								text-align: right;' class='txtValue'
								onkeydown='processDiscNextRow(this, event);' onblur='formatValue(this);' maxLength='13'
								value='${itemDiscount.value}' 
								<c:if test='${itemDiscount.id ne null and itemDiscount.id ne 0}'>readonly='readonly'</c:if> />
						</td>

						<td class='tdProperties'>
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
							<input type='hidden' name='itemAddOns[${status.index}].id' value='${itemAddOn.id}'/>
							<input type="hidden" name='itemAddOns[${status.index}].companyId'
										value="${itemAddOn.companyId}" id="hdnSpaCompId${status.index}"/>
							<input name='itemAddOns[${status.index}].companyName' id='txtDCompanyName${status.index}' 
								onkeyup='showDiscountCompanyList(this.id);' class='txtDCompanyNames' style='width: 100%;'
								value='${fn:escapeXml(companyName)}'
								<c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td class='tdProperties'>
							<input name='itemAddOns[${status.index}].name' id='txtName${status.index}' 
								class='txtNames' style='width: 100%;' 
								value='${itemAddOn.name}'
								<c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td class='tdProperties' valign='top'>
							<input name='itemAddOns[${status.index}].value' id='value${status.index}' style='width: 100%; 
								text-align: right;' class='txtValue'
								onkeydown='processAddOnNextRow(this, event);' onblur='formatValue(this);' maxLength='13'
								value='${itemAddOn.value}' 
								<c:if test='${itemAddOn.id ne null and itemAddOn.id ne 0}'>readonly='readonly'</c:if> />
						</td>
						<td class='tdProperties'>
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
	<table class="buttonClss">
			<tr>
				<td align="right" >
					<input type="button" id="btnSaveRItem" value="${item.id eq 0 ? 'Save' : 'Update'}" onclick="saveRItem();"/>
					<input type="button" id="btnCancelSave" value="Cancel"/>
				</td>
			</tr>
	</table>
	</div>
	</form:form>
</div>
</body>
</html>