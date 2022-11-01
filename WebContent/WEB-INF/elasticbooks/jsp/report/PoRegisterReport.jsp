<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- 

	Description: Purchase Order Register Report.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/retrieveArCustomer.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	loadDivisions();
	filterSupplierAccts();
});

function generateReport () {
	var companyId = $("#companyId").val();
	var divisionId = $("#slctDivisionId").val();
	var supplierId = $("#supplierId").val();
	var supplierAccountId = $("#supplierAccountId").val();
	var termId = $("#termId").val();
	var poDateFrom = $("#poDateFrom").val();
	var poDateTo = $("#poDateTo").val();
	var rrDateFrom = $("#rrDateFrom").val();
	var rrDateTo = $("#rrDateTo").val();
	var statusId = $("#statusId").val();
	var deliveryStatus = $("#deliveryStatus").val();
	var formatType = $("#formatType").val();

	var uri = "?companyId=" + companyId + "&divisionId=" + divisionId + "&supplierId=" + supplierId
				+ "&supplierAccountId=" + supplierAccountId
				+ "&termId=" + termId + "&poDateFrom=" + poDateFrom + "&poDateTo=" + poDateTo
				+ "&rrDateFrom=" + rrDateFrom + "&rrDateTo=" + rrDateTo + "&statusId=" + statusId
				+ "&deliveryStatus=" + deliveryStatus + "&formatType=" + formatType;

	var hasError = false;
	var errDateMessage = "Invalid date range.";
	//PO Date
	$("#spanPoDateErr").text("");
	if(poDateFrom == "" || poDateTo == "") {
		//Incomplete date.
		if(poDateFrom != "" || poDateTo != "") {
			$("#spanPoDateErr").text(errDateMessage);
			hasError = true;
		}
	} else if(Date.parse(poDateFrom) > Date.parse(poDateTo)) {
		$("#spanPoDateErr").text(errDateMessage);
		hasError = true;
	}
	//RR Date
	$("#spanRrDateErr").text("");
	if(rrDateFrom == "" || rrDateTo == "") {
		//Incomplete date.
		if(rrDateFrom != "" || rrDateTo != "") {
			$("#spanRrDateErr").text(errDateMessage);
			hasError = true;
		}
	} else if(Date.parse(rrDateFrom) > Date.parse(rrDateTo)) {
		$("#spanRrDateErr").text(errDateMessage);
		hasError = true;
	}
	//Company
	$("#spanCompanyErr").text("");
	if(companyId == -1) {
		$("#spanCompanyErr").text("Company is required.");
		hasError = true;
	}

	$("#spanSupplierErr").text("");
	if ($("#txtSupplier").val() != "" && $("#supplierId").val() == "") {
		$("#spanSupplierErr").text("Invalid supplier");
		hasError = true;
	}

	var url = "";
	if (!hasError) {
		url = contextPath + "/poRegister/generateReport" + uri;
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}

function companyOnChange(isReloadDivision) {
	if (isReloadDivision) {
		loadDivisions();
	}
	$("#spanSupplierErr").text("");
	$("#supplierId").val("");
	$("#txtSupplier").val("");
	filterSupplierAccts();
}

function loadDivisions() {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions/byCompany?companyId="
			+companyId+"&isMainLevelOnly=true";
	$("#slctDivisionId").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},
		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};
	postHandler = {
		doPost: function(data) {
			// nothing
		}
	};
	loadPopulate (uri, true, null, "slctDivisionId", optionParser, postHandler);
}

function showSuppliers() {
	$("#spanSupplierErr").text("");
	var companyId = $("#companyId").val();
	var divisionId = $("#slctDivisionId").val();
	var supplierName =  $("#txtSupplier").val() != "" ? $.trim($("#txtSupplier").val()) : $("#supplierId").val("");
	var uri = contextPath+"/getSuppliers/new?name="+supplierName+"&companyId="+companyId+(divisionId > 0 ? "&divisionId="+divisionId : "");
	$("#txtSupplier").autocomplete({
		source: uri,
		select: function(event, ui) {
			$("#supplierId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
		.data( "ui-autocomplete-item", item )
		.append( "<a style='font-size: small;'>"+item.name +"</a>" )
		.appendTo( ul );
	};
}

function getSuppliers() {
	var companyId = $("#companyId").val();
	var divisionId = $("#slctDivisionId").val();
	var supplierName = $.trim($("#txtSupplier").val());
	if (supplierName != "") {
		var uri = contextPath + "/getSuppliers/new?name="+encodeURIComponent(supplierName)
			+"&companyId="+companyId+(divisionId > 0 ? "&divisionId="+divisionId : "")+"&isExact=true";
		$.ajax({
			url: uri,
			success: function (supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#txtSupplier").val(supplier[0].name);
					$("#supplierId").val(supplier[0].id);
				} else {
					$("#spanSupplierErr").text("Invalid supplier.");
					$("#supplierId").val("");
				}
				filterSupplierAccts();
			},
			dataType: "json"
		});
	} else {
		$("#supplierId").val("");
		filterSupplierAccts();
	}
}

function filterSupplierAccts() {
	var companyId = $("#companyId").val();
	var divisionId = $("#slctDivisionId").val();
	var supplierId = Number($("#supplierId").val()) != 0 ? $("#supplierId").val() : "";
	$("#supplierAccountId").empty();
	$("#supplierAccountId").append("<option selected='selected' value=-1>ALL</option>");
	if (supplierId != "") {
		var uri = contextPath+"/getApSupplierAccts?supplierId="+supplierId+"&companyId="+companyId
				+(divisionId > 0 ? "&divisionId="+divisionId : "")+"&activeOnly=true";
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, false, null, "supplierAccountId", optionParser, null);
	}
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" class="frmSelectClass" onchange="companyOnChange(true);">
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
			<span id="spanCompanyErr" class="error"></span>
		</td>
	</tr>
	<tr>
			<td class="title2">Division</td>
			<td class="value">
				<select id="slctDivisionId" onchange="companyOnChange(false);"
					class="frmSelectClass"></select>
			</td>
		</tr>
		<tr>
			<td class="title2"></td>
			<td class="value"><span id="spanDivisionError" class="error"></span>
			</td>
		</tr>
	<tr>
		<td class="title2">Supplier</td>
		<td>
			<input id="txtSupplier" class="input" onkeypress="showSuppliers();" onblur="getSuppliers();">
			<input type="hidden" id="supplierId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanSupplierErr" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Supplier Account</td>
		<td>
			<select id="supplierAccountId" class="frmSelectClass">
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanSupplierAccErr" class="error" ></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Term </td>
		<td>
			<select id="termId" class="frmSelectClass" onchange="">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="term" items="${terms}">
					<option value="${term.id}">${term.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">PO Date </td>
		<td class="tdDateFilter">
			<input type="text" id="poDateFrom" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('poDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="poDateTo" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('poDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanPoDateErr" class="error" ></span>
		</td>
	</tr>
		<tr>
		<td class="title2">RR Date </td>
		<td class="tdDateFilter">
			<input type="text" id="rrDateFrom" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('rrDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="rrDateTo" maxlength="10" class="dateClass2" 
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('rrDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanRrDateErr" class="error" ></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Status</td>
		<td><select id="statusId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="status" items="${formStatuses}">
					<option value="${status.id}">${status.description}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Delivery Status</td>
		<td>
			<select id="deliveryStatus" class="frmSelectClass">
				<option selected='selected' value="ALL">ALL</option>
				<option value="UNSERVED">UNSERVED</option>
				<option value="FULLY SERVED">FULLY SERVED</option>
				<option value="PARTIALLY SERVED">PARTIALLY SERVED</option>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>