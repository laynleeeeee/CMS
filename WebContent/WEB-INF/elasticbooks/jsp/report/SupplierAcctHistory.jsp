<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--


	Description: Search page for Supplier Account History Report.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
function showSuppliers () {
	if ($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Company is required");
	} else {
		var supplierName = $.trim($("#txtSupplierName").val());
		var selectedDivisionId =  $("#divisionId").val();
		if($("#txtSupplierName").val() != "") {
			$("#hdnSupplierId").val("");
			var uri = contextPath + "/getSuppliers/new?name="+encodeURIComponent(supplierName)+ "&companyId="
					+ $("#companyId").val()+(selectedDivisionId != -1 ? "&divisionId="+selectedDivisionId : "")
			$("#txtSupplierName").autocomplete({
				source: uri,
				select: function( event, ui ) {
					$("#hdnSupplierId").val(ui.item.id);
					$("#supplierError").text("");
					$(this).val(ui.item.name);
					filterSupplierAccts();
					return false;
				}, minLength: 2,
				change: function(event, ui){
					$.ajax({
						url: uri,
						success : function(item) {
							if (ui.item != null && ui.item != undefined) {
								$("#hdnSupplierId").val(ui.item.id);
								$(this).val(ui.item.name);
								clearSpanMsg();
							} else {
								$("#supplierError").text("Please select supplier.");
								$("#hdnSupplierId").val("");
								$("#supplierAcctId").empty();
							}
						},
						error : function(error) {
							$("#supplierError").text("Please select supplier.");
							$("#supplierAcctId").empty();
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
	}
}

function getSupplier() {

	var supplierName = $.trim($("#txtSupplierName").val());
	var selectedDivisionId =  $("#divisionId").val();
	var uri = contextPath + "/getSuppliers/new?name="+encodeURIComponent(supplierName)
			+"&companyId="+$("#companyId").val()+"&isExact=true"+(selectedDivisionId != -1 ? "&divisionId="+selectedDivisionId : "");
	if($("#companyId").val() != -1) {
		$.ajax({
			url: uri,
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#hdnSupplierId").val(supplier[0].id);
					$("#txtSupplierName").val(supplier[0].name);
					clearSpanMsg();
				}
				filterSupplierAccts()
			},
			error:function(){
				if(supplierName != "") {
					$("#supplierError").text("Invalid supplier.");
					$("#iFrame").attr('src', "");
					$("#hdnSupplierId").val("");
					$("#supplierAcctId").empty();
				}
				$("#hdnSupplierId").val("");
			},
			dataType: "json"
		});
	}
}

function filterSupplierAccts() {
	var selectedCompanyId = $("#companyId").val();
	var selectedSupplierId = $("#hdnSupplierId").val();
	var	selectedDivisionId = ""
	if($("#divisionId").val() != -1){
		selectedDivisionId = $("#divisionId").val();
	}
	if(selectedSupplierId != "") {
		var uri = contextPath+"/getApSupplierAccts?supplierId="+selectedSupplierId+"&companyId="+selectedCompanyId+
		"&divisionId="+selectedDivisionId;
		$("#supplierAcctId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, false, null, "supplierAcctId", optionParser, null);
	}
}

function generateReport() {
	var supplierName = encodeURIComponent($.trim($("#txtSupplierName").val()));
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var currencyId = $("#currencyId").val();
	var supplierId = $("#hdnSupplierId").val();
	var supplierAcctId = $("#supplierAcctId").val();
	var asOfDate = $("#asOfDate").val();
	var format = $("#formatType").val();
	var isValidFilters = true;

	if (companyId == null) {
		$("#companyError").text("Company is a required field.");
		isValidFilters = false;
	}
	if (supplierName == "") {
		$("#supplierError").text("Supplier is a required field.");
		$("#supplierAcctId").text("");
		$("#supplierAcctId").empty();
		isValidFilters = false;
	}
	if (supplierAcctId == null) {
		$("#supplierAcctError").text("Supplier Account is a required field.");
		isValidFilters = false;
	}
	if (asOfDate == "") {
		$("#dateError").text("Date from and/or to are required fields.");
		isValidFilters = false;
	}
	var url = "";
	if (isValidFilters) {
		url = contextPath+"/supplierAcctHistPDF?companyId="+companyId+
			"&supplierAcctId="+supplierAcctId+"&divisionId="+divisionId+"&currencyId="+currencyId+"&asOfDate="+asOfDate+"&formatType="+format;
		clearSpanMsg();
	}
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
}

function companyOnChange() {
	$("#hdnSupplierId").val("");null
	$("#txtSupplierName").val("");
	$("#supplierAcctId").empty();
	clearSpanMsg();
}

function divisionOnChange() {
	$("#hdnSupplierId").val("");
	$("#txtSupplierName").val("");
	$("#supplierAcctId").empty();
	clearSpanMsg();
}

function clearSpanMsg() {
	$("#companyError").text("");
	$("#supplierError").text("");
	$("#supplierAcctError").text("");
	$("#dateError").text("");
}

</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td><select id="companyId" onchange="companyOnChange();"
			class="frmSelectClass">
			<c:forEach var="company" items="${companies}">
				<option value="${company.id}">${company.name}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Division</td>
		<td><select id="divisionId" onchange="divisionOnChange();"
			class="frmSelectClass">
			<option selected='selected' value=-1>ALL</option>
			<c:forEach var="division" items="${divisions}">
				<option value="${division.id}">${division.name}</option>
			</c:forEach>
		</select></td>
	</tr>
	<tr>
		<td></td>
		<td><span id="companyError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Supplier </td>
		<td>
			<input id="txtSupplierName" class="input" onkeydown="showSuppliers();" onkeyup="showSuppliers();"
				onblur="getSupplier();">
			<input type="hidden" id="hdnSupplierId"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="supplierError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Supplier Account</td>
		<td><select id="supplierAcctId"
			class="frmSelectClass">
			<c:forEach var="supplierAcct" items="${supplierAccts}">
				<option value="${supplierAcct.id}">${supplierAcct.name}</option>
			</c:forEach>
		</select></td>
	</tr>
	<tr>
		<td></td>
		<td><span id="supplierAcctError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">As of Date </td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2" value="${currentDate}"
			onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('asOfDate')" style="cursor:pointer" style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Currency</td>
		<td><select id="currencyId"
			class="frmSelectClass">
			<c:forEach var="currency" items="${currencies}">
				<option value="${currency.id}" >${currency.name}</option>
			</c:forEach>
		</select></td>
	</tr>
	<tr>
		<td></td>
		<td><span id="dateError" class="error"></span></td>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateReport()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>