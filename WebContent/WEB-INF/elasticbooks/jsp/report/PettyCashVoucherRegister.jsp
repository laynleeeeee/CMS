<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Petty Cash Voucher Register main page.
 -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function showCustodians() {
	var companyId = $("#companyId").val();
	if (companyId < 0) {
		$("#spanCompanyError").text("Company is required.");
	} else {
		var custodianName = $.trim($("#txtCustodianName").val());
		var divisionId = $("#divisionId").val();
		var uri = contextPath+"/getUserCustodians?name="+processSearchName(custodianName)
				+"&companyId="+companyId+(divisionId != "" ? "&divisionId="+divisionId : "")+"&isExact=false"
		$("#txtCustodianName").autocomplete({
			source: uri,
			select: function( event, ui ) {
				$("#hdnCustodianId").val(ui.item.id);
				$(this).val(ui.item.custodianAccount.custodianName);
				return false;
			}, minLength: 2,
			change: function(event, ui){
				$.ajax({
					url: uri,
					success : function(item) {
						if (ui.item != null && ui.item != undefined) {
							$("#hdnCustodianId").val(ui.item.id);
							$(this).val(ui.item.custodianAccount.custodianName);
							clearValues();
						} else {
							if ($.trim($("#txtCustodianName").val()) != "") {
								$("#spanCustodianError").text("Invalid custodian.");
								$("#hdnCustodianId").val("");
							}
						}
					},
					error : function(error) {
						if ($.trim($("#txtCustodianName").val()) != "") {
							$("#spanCustodianError").text("Invalid custodian.");
							$("#hdnCustodianId").val("");
						}
					},
					dataType: "json"
				});
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>" +item.custodianAccount.custodianName+"</a>" )
				.appendTo( ul );
		};
	}
}

function generatePCVReport() {
	var hasValidFilters = true;
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	if ($("#companyId").val() == -1) {
		$("#spanCompanyError").text("Company is required.");
		hasValidFilters = false;
	}
	if ($.trim($("#spanCustodianError").text()) != "") {
		hasValidFilters = false;
	}
	if (dateFrom == "" && dateTo == "") {
		$("#spanDateError").text("Date from and to is required.");
		hasValidFilters = false;
	} else if (dateFrom != "" && dateTo == "") {
		$("#spanDateError").text("Invalid date range.");
		hasValidFilters = false;
	} else if (dateFrom == "" && dateTo != "") {
		$("#spanDateError").text("Invalid date range.");
		hasValidFilters = false;
	} else if (new Date(dateFrom) > new Date(dateTo)) {
		$("#spanDateError").text("Invalid date range.");
		hasValidFilters = false;
	}
	var url = "";
	if (hasValidFilters) {
		clearValues();
		url = contextPath+"/pettyCashVoucherRgstr/generatePDF"+getCommonParam();
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}

function clearValues() {
	$("#spanCompanyError").text("");
	$("#spanCustodianError").text("");
	$("#spanDateError").text("");
	$("#spanDivisionError").text("");
}

function getCommonParam() {
	var custodianId = $("#hdnCustodianId").val() != "" ? $("#hdnCustodianId").val() : -1;
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var requestor = $.trim($("#requestor").val());
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var statusId = $.trim($("#statusId").val());
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&custodianId="+custodianId
			+"&requestor="+processSearchName(requestor)+"&dateFrom="+dateFrom+"&dateTo="+dateTo
			+"&statusId="+statusId+"&formatType="+formatType;
}

function clearAndAddEmpty() {
	$("#supplierAcctId").empty();
	var option = "<option selected='selected' value='-1'>ALL</option>";
	$("#supplierAcctId").append(option);
}

function selectOnChange() {
	$("#hdnCustodianId").val("");
	$("#txtCustodianName").val("");
	$("#requestor").val("");
	clearValues();
}
</script>
</head>
<body>
	<table>
		<tr>
			<td class="title2">Company</td>
			<td>
				<select id="companyId" onchange="selectOnChange();" class="frmSelectClass">
					<option selected='selected' value=-1>Please select a company</option>
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td class="title2"></td>
			<td><span id="spanCompanyError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Division</td>
			<td>
				<select id="divisionId" onchange="selectOnChange();" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td class="title2"></td>
			<td><span id="spanDivisionError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Custodian </td>
			<td>
				<input id="txtCustodianName" class="input" onkeydown="showCustodians();" onkeyup="showCustodians();">
				<input type="hidden" id="hdnCustodianId"/>
			</td>
		</tr>
		<tr>
			<td class="title2"></td>
			<td><span id="spanCustodianError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Requestor</td>
			<td><input class="input" id="requestor"></td>
		</tr>
		<tr>
			<td class="title2">Date</td>
			<td class="tdDateFilter">
				<input type="text" id="dateFrom" maxlength="10" class="dateClass2"
					onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
					style="float: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="dateTo" maxlength="10" class="dateClass2"
					onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
					style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td class="title2"></td>
			<td><span id="spanDateError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Transaction Status</td>
			<td>
				<select id="statusId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="pcvrStatuses" items="${pcvrStatuses}">
						<option value="${pcvrStatuses.id}">${pcvrStatuses.description}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td class="title2">Format:</td>
			<td>
				<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
				</select>
			</td>
		</tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate" onclick="generatePCVReport();"/></td>
		</tr>
	</table>
	<hr class="thin2">
	<div>
		<iframe id="iFrame"></iframe>
	</div>
</body>
</html>