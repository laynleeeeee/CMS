<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Unliquidated Petty Cash Voucher Aging main page.
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
	$("#asOfDate").val(newDate);
});

function showCustodians() {
	var custodianName = $.trim($("#txtCustodianName").val());
	if (custodianName == "") {
		$("#spanCustodianError").text("");
		$("#hdnCustodianId").val("");
	}
	var companyId = $("#companyId").val();
	if (companyId < 0) {
		$("#spanCompanyError").text("Company is required.");
	} else {
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
	var companyId = $("#companyId").val();
	var asOfDate = $.trim($("#asOfDate").val());
	var isValidFilters = true;
	if (companyId < 0) {
		$("#spanCompanyError").text("Company is required.");
		isValidFilters = false;
	}
	if ($.trim($("#spanCustodianError").text()) != "") {
		isValidFilters = false;
	}
	if (asOfDate == "") {
		$("#spanDateError").text("Date is required.");
		isValidFilters = false;
	}
	var url = "";
	if (isValidFilters) {
		clearValues();
		url = contextPath+"/unliquidatedPCVAging/generatePDF"+getCommonParam();
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
	var asOfDate = $.trim($("#asOfDate").val());
	var statusId = $.trim($("#statusId").val());
	var formatType = $("#formatType").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&custodianId="+custodianId
			+"&requestor="+processSearchName(requestor)+"&asOfDate="+asOfDate+"&statusId="+statusId+"&formatType="+formatType;
}

function companyOnChange() {
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
				<select id="companyId" onchange="companyOnChange();" class="frmSelectClass">
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
				<select id="divisionId" onchange="companyOnChange();" class="frmSelectClass">
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
				<input type="hidden" id="hdnCustodianId"/>
				<input id="txtCustodianName" class="input" onkeypress="showCustodians();">
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
			<td class="title2">As of Date</td>
			<td class="tdDateFilter">
				<input type="text" id="asOfDate" maxlength="10" class="dateClass2"
					onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('asOfDate')" style="cursor:pointer"
					style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td class="title2"></td>
			<td><span id="spanDateError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Format</td>
			<td>
				<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
				</select>
			</td>
		</tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate" onclick="generatePCVReport()"/></td>
		</tr>
	</table>
	<hr class="thin2">
	<div>
		<iframe id="iFrame"></iframe>
	</div>
</body>
</html>