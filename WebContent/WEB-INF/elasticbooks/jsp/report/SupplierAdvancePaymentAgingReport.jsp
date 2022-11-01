<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Supplier Advance Payment Aging Report.
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
$(document).ready(function() {
	clearSupplier();
	var newDateTo = parseServerDate(new Date);
	$("#dateTo").val(newDateTo);
});

function filterSupplierAccts() {
	$("#supplierAcctId").empty();
	if ($.trim($("#supplierName").val()) == "") {
		$("#supplierId").val("") ;
		clearSupplier();
	} else {
		var supplierId = $("#supplierId").val();
		if (supplierId != "") {
			var selectedCompanyId = $("#companyId").val();
			var uri = contextPath+"/getSupplierAccounts?supplierId="+supplierId+"&companyId="+selectedCompanyId;
			var optionParser = {
				getValue: function (rowObject) {
					return rowObject["id"];
				},
				getLabel: function (rowObject) {
					return rowObject["name"];
				}
			};
			loadPopulate (uri, true, null, "supplierAcctId", optionParser, null);
		} else {
			clearSupplier();
		}
	}
}

function clearSupplier() {
	$("#supplierAcctId").empty();
	var option = "<option selected='selected' value='-1'>ALL</option>";
	$("#supplierAcctId").append(option);
}

function companyOnChange() {
	clearSupplier();
	$("#supplierId").val("");
	$("#supplierName").val("");
	$("#supplierAcctId").val("");
}

function showSupplier() {
	$("#spanSupplierError").text("");
	var companyId = $("#companyId").val();
	var supplierName = $("#supplierName").val();
	var uri = contextPath + "/getSuppliers/new?name="+processSearchName(supplierName)
			+"&companyId="+companyId;
	var divisionId = $("#divisionId").val();
	if (divisionId != "" && divisionId != "undefined" && divisionId != -1) {
		uri += "&divisionId="+divisionId;
	}
	$("#supplierName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#supplierId").val(ui.item.id);
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

function getSupplier() {
	var companyId = $("#companyId").val();
	var supplierName = $.trim($("#supplierName").val());
	var divisionId = $("#divisionId").val();
	if (supplierName != "") {
		$("#spanSupplierError").text("");
		var uri = contextPath + "/getSuppliers/new?name="+processSearchName(supplierName)
				+"&isExact=true"+"&companyId="+companyId;
		if (divisionId != "" && divisionId != "undefined" && divisionId != -1) {
			uri += "&divisionId="+divisionId;
		}
		$.ajax({
			url: uri,
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					var supplierId = supplier[0].id;
					$("#supplierId").val(supplierId);
					$("#supplierName").val(supplier[0].name);
				} else {
					$("#spanSupplierError").text("Invalid supplier.");
					$("#supplierId").val("");
					$("#supplierAcctId").empty();
				}
				filterSupplierAccts();
			},
			dataType: "json"
		});
	} else {
		$("#hdnSupplierId").val("");
		$("#supplierIdError").text("");
	}
}

function getCommonParam() {
	var companyId = $("#companyId").val();
	var supplierId = $("#supplierId").val();
	if (supplierId == "" || supplierId == null){
		supplierId = -1;
	}
	var supplierAcctId = $("#supplierAcctId").val() != -1 ? $("#supplierAcctId").val() : -1;
	var divisionId = $("#divisionId option:selected").val();
	var statusId = $("#statusId option:selected").val();
	var ageBasis = $("#ageBasis option:selected").val();
	var bmsNumber = processSearchName($("#bmsNumber").val());
	if (bmsNumber == "" || bmsNumber == null){
		bmsNumber = -1;
	}
	var dateTo = $("#dateTo").val();
	var formatType = $("#formatType").val();
	return "?companyId="+companyId +"&supplierId="+supplierId+"&supplierAcctId="+supplierAcctId
			+"&divisionId="+divisionId+"&statusId="+statusId+"&ageBasis="+ageBasis+"&bmsNumber="+bmsNumber
			+"&dateTo="+dateTo+"&formatType="+formatType;
}

function generateSupplierAdvPaymentAging(){
	var companyId = $("#companyId").val();
	var dateTo = $.trim($("#dateTo").val());
	var hasError = false;
	if (companyId == -1) {
		$("#spanCompanyError").text("Company is required.");
		hasError = true;
	}
	if ($("#supplierName").val() != "" && $("#supplierId").val() == "") {
		$("#spanSupplierError").text("Invalid supplier.");
		hasError = true;
	}
	if (dateTo == "") {
		$("#spanDateError").text("As of date is required.");
		hasError = true;
	}
	var url = "";
	if (!hasError) {
		$("#spanCompanyError").text("");
		$("#spanSupplierError").text("");
		$("#spanDateError").text("");
		url = contextPath + "/supplierAdvancePaymentAging/generateReport"+getCommonParam();
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company</td>
		<td><select id="companyId" onchange="companyOnChange();" class="frmSelectClass">
			<c:forEach var="company" items="${companies}">
				<option value="${company.id}">${company.name}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td>
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" onchange="companyOnChange();" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="divisions" items="${divisions}">
					<option value="${divisions.id}">${divisions.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Supplier</td>
		<td>
			<input id="supplierName" class="input" onkeypress="showSupplier();" onblur="getSupplier();">
			<input type="hidden" id="supplierId">
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td>
			<span id="spanSupplierError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Supplier Account</td>
		<td>
			<select id="supplierAcctId" class="frmSelectClass">
			<option selected='selected' value=-1>ALL</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">BMS No. </td>
		<td class="tdDateFilter">
			<input type="text" id="bmsNumber" class="input">
		</td>
	</tr>
	<tr>
		<td class="title2">As of Date</td>
		<td class="tdDateFilter">
			<input type="text" id="dateTo" maxlength="10" class="dateClass2"
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td>
			<span id="spanDateError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Age Basis </td>
		<td>
			<select id="ageBasis" class="frmSelectClass">
				<option value=1>Due Date</option>
				<option value=2>Transaction Date</option>
				<option value=3 selected='selected'>GL Date</option>
				<option value=4>Clearing Date</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Status</td>
		<td>
			<select id="statusId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="fs" items="${formStatuses}">
					<option value="${fs.id}">${fs.description}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Format</td>
		<td class="value"><select id="formatType" class="frmSelectClass">
				<option value="pdf">PDF</option>
				<option value="xls">EXCEL</option>
		</select></td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateSupplierAdvPaymentAging()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>