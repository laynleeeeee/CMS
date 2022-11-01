<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>

<!--

	Description: Petty Cash liquidation Register Report
 -->

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CMS/css/ebReport.css"
	media="all">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"
	media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/commonUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/ZeroClipboard.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/TableTools.js"></script>
<style type="text/css" title="currentStyle" >
	@import "${pageContext.request.contextPath}/css/jquery/demo_page.css";
	@import "${pageContext.request.contextPath}/css/jquery/demo_table.css";
	@import "${pageContext.request.contextPath}/media/css/TableTools.css";
</style>
<script type="text/javascript">
$(document).ready (function(){
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
	getDivisions();
});

function getCustodianAccount() {
	$("#errorCustodian").text("");
	var custodianName = $.trim($("#txtCustodianName").val());
	if (custodianName != "") {
		var companyId = $("#selectCompanyId").val();
		var divisionId = $("#divisionId").val();
		var uri = contextPath+"/getUserCustodians?companyId="+companyId+"&name="+processSearchName(custodianName)
				+(divisionId != "" ? "&divisionId="+divisionId : "")+"&isExact=true";
		$.ajax({
			url: uri,
			success : function(userCustodian) {
				if (userCustodian != null && userCustodian[0] != undefined) {
					$("#hdnCustodianId").val(userCustodian[0].custodianAccount.id);
					$("#txtCustodianName").val(userCustodian[0].custodianAccount.custodianName);
					$("#errorCustodian").text("");
				} else {
					$("#hdnCustodianId").val("");
					$("#errorCustodian").text("Invalid custodian.");
				}
			},
			error : function(error) {
				$("#hdnCustodianId").val("");
				$("#errorCustodian").text("Invalid custodian.");
			},
			dataType: "json"
		});
	} else {
		$("#hdnCustodianId").val("");
	}
};

function showCustodianAccounts() {
	var custodianName = $("#txtCustodianName").val();
	var companyId = $("#selectCompanyId").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath+"/getUserCustodians?companyId="+companyId+(divisionId != "" ? "&divisionId="+divisionId : "")
			+"&name="+processSearchName(custodianName);
	$("#txtCustodianName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnCustodianId").val(ui.item.id);
			$(this).val(ui.item.custodianAccount.custodianName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.custodianAccount.custodianName+"</a>" )
			.appendTo( ul );
	};
};

function getCommonParam(){
	var companyId = $("#selectCompanyId").val();
	var custodianId = $("#hdnCustodianId").val() != "" ? $("#hdnCustodianId").val() : -1;
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var formatType = $("#formatType").val();
	var divisionId = $("#divisionId option:selected").val();
	var transactionStatusId = $("#transactionStatusId option:selected").val();
	var requestorName = processSearchName($.trim($("#txtRequestorName").val()));
	return "?companyId="+companyId+"&divisionId="+divisionId+"&custodianId="+custodianId
			+"&transactionStatusId="+transactionStatusId+"&requestorName="+processSearchName(requestorName)
			+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType="+formatType;
};

function generatePCVReport() {
	var companyId = $("#selectCompanyId").val();
	var dateFrom = $.trim($("#dateFrom").val());
	var dateTo = $.trim($("#dateTo").val());
	var isInvalidCustodian = $("#errorCustodian").text() != "";
	var hasValidFilters = true;
	if (companyId < 0) {
		$("#errorCompany").text("Company is required.");
		hasValidFilters = false;
	}
	if (dateFrom == "" && dateTo == "") {
		$("#errorDate").text("Date from and to is required.");
		hasValidFilters = false;
	} else if (dateFrom != "" && dateTo == "") {
		$("#errorDate").text("Invalid date range.");
		hasValidFilters = false;
	} else if (dateFrom == "" && dateTo != "") {
		$("#errorDate").text("Invalid date range.");
		hasValidFilters = false;
	} else if (new Date(dateFrom) > new Date(dateTo)) {
		$("#errorDate").text("Invalid date range.");
		hasValidFilters = false;
	}
	var url = ""
	if (hasValidFilters && !isInvalidCustodian) {
		$("#errorCompany").text("");
		$("#errorDate").text("");
		url = contextPath+"/pcvlRegisterReport/generateReport"+getCommonParam();
	}
	$('#reportPCVLiquidationRegister').attr('src',url);
	$('#reportPCVLiquidationRegister').load();
}

function getDivisions() {
	var companyId = $("#selectCompanyId").val();
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

function clearCustodian() {
	$("#txtCustodianName").val("");
	$("#errorCustodian").text("");
	$("#hdnCustodianId").val("");
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="selectCompanyId" class="frmSelectClass" onchange="getDivisions();">
				<option selected='selected' value=-1>Please select a company</option>
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	<tr>
		<td class="title2"></td>
		<td>
			<span id="errorCompany" class="error"></span>
		</td>
	</tr>

	<tr>
		<td class="title2">Division </td>
		<td>
			<select id="divisionId" class="frmSelectClass" onchange="clearCustodian();" >
				<option selected='selected' value=-1>All</option>
			</select>
		</td>
	</tr>
	<tr>
	<td class="title2">Custodian</td>
		<td>
			<input type="hidden" id="hdnCustodianId"/>
			<input id="txtCustodianName" class="input" onkeypress="showCustodianAccounts();"
				onblur="getCustodianAccount();">
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td>
			<span id="errorCustodian" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Requestor</td>
		<td>
			<input id="txtRequestorName" class="input" >
		</td>
	</tr>
	<tr>
		<td class="title2">Date </td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2" onblur="evalDate('dateFrom')" >
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2"
				onblur="evalDate('dateTo');">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td>
			<span id="errorDate" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Transaction Status</td>
		<td>
			<select id="transactionStatusId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="fs" items="${formStatuses}">
					<option value="${fs.id}">${fs.description}</option>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generatePCVReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="reportPCVLiquidationRegister"></iframe>
</div>
<hr class="thin2">
</body>
</html>