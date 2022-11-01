<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Supplier advance payment register jsp report filter
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
var selectedSupplierAcct = -1;
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
	getDivisions();
});

function getDivisions() {
	var companyId = $("#companyId").val();
	if (companyId != "") {
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

function showSuppliers () {
	var companyId = $("#companyId").val();
	if (companyId == -1) {
		$("#spanCompanyError").text("Company is required");
	} else {
		var divisionId = $("#divisionId").val();
		var supplierName = encodeURIComponent($.trim($("#txtSupplier").val()));
		var uri = contextPath + "/getSuppliers/new?name="+supplierName+"&companyId="+companyId
				+(divisionId != -1 ? "&divisionId="+divisionId : "");	
		$("#txtSupplier").autocomplete({
			source: uri,
			select: function( event, ui ) {
				$("#aSupplierId").val(ui.item.id);
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
}

function getSupplier() {
	$("#spanSupplierError").text("");
	var supplierName = $.trim($("#txtSupplier").val());
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	if (companyId != -1 && supplierName != "") {
		$.ajax({
			url: contextPath + "/getSuppliers/new?name="+encodeURIComponent(supplierName)+"&companyId="+companyId+"&isExact=true",
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#supplierId").val(supplier[0].id);
					$("#txtSupplier").val(supplier[0].name);
				} else {
					$("#spanSupplierError").text("Invalid supplier.");
					$("#supplierId").val("");
				}
				filterSupplierAccts();
			},
			error : function(error) {
				$("#supplierId").val("");
				$("#supplierAcctId").empty();
			},
			dataType: "json"
		});
	}
}

function filterSupplierAccts(dueDate, termId) {
	$("#supplierAcctId").empty();
	$("#supplierAcctId").prepend("<option value='-1'>ALL</option>");
	if ($.trim($("#supplierId").val()) == "") {
		$("#supplierId").val("");
	} else {
		var companyId = $("#companyId").val();
		var selectedSupplierId = $("#supplierId").val();
		var uri = contextPath+"/getApSupplierAccts?supplierId="+selectedSupplierId+"&companyId="+companyId;
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
				$("#supplierAcctId").val(selectedSupplierAcct).attr("selected",true);
				// This is to remove any duplication.
				var found = [];
				$("#supplierAcctId option").each(function() {
					if($.inArray(this.value, found) != -1)
						$(this).remove();
					found.push(this.value);
				});

			}
		};
		loadPopulate (uri, false, selectedSupplierAcct, "supplierAcctId", optionParser, postHandler);
	}
}

function getCommonParam () {
	var companyId = $("#companyId option:selected").val() != "" ?
			$("#companyId option:selected").val() : -1;
	var divisionId = $("#divisionId option:selected").val() != "" ?
			$("#divisionId option:selected").val() : -1;
	var supplierId = $("#supplierId").val() != "" ? $("#supplierId").val() : -1;
	var supplierAcctId = $("#supplierAcctId").val() != null ?
			$("#supplierAcctId").val() : -1;
	var bmsNumber = $("#bmsNumber").val() != "" ? $("#bmsNumber").val() : "";
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var status = $("#status option:selected").val() != "" ?
			$("#status option:selected").val() : -1;
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&supplierId="+supplierId+"&supplierAcctId="+supplierAcctId+
			"&bmsNumber="+bmsNumber+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&status="+status+"&formatType="+formatType;
}

function genReport() {
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var hasFilterError = false;
	if ($.trim($("#companyId").val()) == -1) {
		$("#spanCompanyError").text("Company is required.");
		hasFilterError = true;
	} else {
		$("#spanCompanyError").text("");
	}
	if (dateFrom == "" || dateTo == "") {
		$("#spanErrorDate").text("Date from and to are required fields.");
		hasFilterError = true;
	} else if (new Date(dateFrom) > new Date(dateTo)) {
		$("#spanErrorDate").text("Invalid date range.");
		hasFilterError = true;
	}
	if ($.trim($("#supplierId").val()) == "" && $.trim($("#txtSupplier").val()) != "") {
		$("#spanSupplierError").text("Invalid supplier.");
		hasFilterError = true;
	}
	var url = "";
	if (!hasFilterError) {
		$("#spanSupplierError").text("");
		$("#spanErrorDate").text("");
		url = contextPath + "/supplierAdvPaymentRgstr/generatePDF"+getCommonParam();
	}
	$('#reportSupplierAdvancePayment').attr('src', url);
	$('#reportSupplierAdvancePayment').load();
}

function selectOnChange() {
	$("#supplierId").val("");
	$("#txtSupplier").val("");
	$("#supplierAcctId").empty();
}
</script>
<title>Supplier Advance Payment Register</title>
</head>
<body>
	<table>
	<tr>
		<td class="title2">Company</td>
		<td class="value">
			<select id="companyId" class="frmSelectClass" onchange="selectOnChange();">
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
		<td class="title2">Division</td>
		<td class="value">
			<select id="divisionId" class="frmSelectClass" onchange="selectOnChange();">
			<option selected ='selected' value=-1>ALL </option>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDivisionError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Supplier</td>
		<td class="value">
			<input type="hidden" id="supplierId"/>
			<input id="txtSupplier" class="input" onkeypress="showSuppliers();"
				onblur="getSupplier();">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanSupplierError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Supplier's Account</td>
		<td class="value">
			<select id="supplierAcctId" class="frmSelectClass">
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanSupplierAccountError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">BMS No.</td>
		<td class="value">
			<input id=bmsNumber class="input" style="width:180px">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanSupplierError" class="error"></span>
		</td>
	</tr>	
	<tr>
		<td class="title2">Date</td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" onblur="evalDate('dateFrom');" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" onblur="evalDate('dateTo');"  maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanErrorDate" class="error" ></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Status</td>
		<td class="value">
			<select id="status" class="frmSelectClass">
			<option selected = 'selected' value=-1>ALL</option>
				<c:forEach var="sapRegisterStatus" items="${sapRegisterStatuses}">
					<option value="${sapRegisterStatus.id}">${sapRegisterStatus.description}</option>
				</c:forEach>
			</select>
		</td>
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
		<td colspan="3"><input type="button" value="Generate" onclick="genReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="reportSupplierAdvancePayment"></iframe>
</div>
</body>
</html>