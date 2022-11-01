
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Account analysis report.
 -->
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<style type="text/css">
#selectCompanyId, #selectAccountId, #accountId {
	width: 420px;
}
#selectDivisionIdFrom, #selectDivisionIdTo {
	width: 200px;
}
</style>
<script type="text/javascript">
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function getCommonParam () {
	var companyId = $("#selectCompanyId").val();
	var accountId = $("#hdnAccountId").val();
	var divisionIdFrom = $("#selectDivisionIdFrom").val() != null ?  $("#selectDivisionIdFrom").val() : "";
	var divisionIdTo = $("#selectDivisionIdTo").val() != null ? $("#selectDivisionIdTo").val() : "";
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var formatType = $("#formatType").val();
	if($.trim(dateFrom) == "" && $.trim(dateTo) == "") {
		$("#errorDate").text("Date from and/or to are required.");
		return "";
	} else if(dateFrom != "" || dateTo != "") {
		evalDate("dateFrom", true);
		evalDate("dateTo", true);
		return "?companyId="+companyId+"&accountId="+accountId+"&divisionIdFrom="+divisionIdFrom+"&divisionIdTo="+
				divisionIdTo+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&formatType=" + formatType;
	}
	return "";
}

$(function () {
	$("#selectCompanyId").change(function () {
		filterAccounts();
	});
});

function filterAccounts() {
	var companyId = $("#selectCompanyId").val();
	var accountId = $.trim($("#accountId").val());
	var uri = contextPath+"/accountAnalysisReport/loadAccounts?accountName="+accountId+"&companyId="+companyId;
	$("#accountId").autocomplete ({
		source: uri,
		select: function(event, ui) {
			$("#hdnAccountId").val(ui.item.id);
			$(this).val(ui.item.number+" - "+ui.item.accountName);
			return false;
		}, minlength: 2,
		change: function(event, ui) {
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#hdnAccountId").val(ui.item.id);
						$(this).val(ui.item.number + " - " +ui.item.accountName);
						$("#spanAccountError").text("");
						filterDivisions();
					} else {
						$("#spanAccountError").text("Invalid account.");
						$("#hdnAccountId").val("");
						$("#accountId").val("");
						$("#selectDivisionIdFrom").empty();
						$("#selectDivisionIdTo").empty();
					}
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
		.data( "ui-autocomplete-item", item )
		.append( "<a style='font-size: small;'>"+ item.number + " - " + item.accountName +"</a>" )
		.appendTo( ul );
	};
}

function filterDivisions() {
	loadDivisions("selectDivisionIdFrom");
	loadDivisions("selectDivisionIdTo");
}

function loadDivisions (divDivision) {
	var companyId = $("#selectCompanyId").val();
	var accountId = $("#hdnAccountId").val();
	var uri = contextPath+"/accountAnalysisReport/loadDivisions?companyId="+companyId+
			"&accountId="+accountId;
	$("#" + divDivision).empty();
	var divisionSelectId = divDivision;
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["number"] + " - " + rowObject["name"];
		}
	};
	loadPopulate (uri, false, null, divisionSelectId, optionParser, null);
}

function generateAccountAnalysis () {
	if ($("#hdnAccountId").val() == "") {
		$("#spanAccountError").text("Account is required.");
		$("#selectDivisionIdFrom").empty();
		$("#selectDivisionIdTo").empty();
		$("#iFrame").attr('src', "");
	} else {
		filterTable();
	}
}

function filterTable () {
	if (getCommonParam() != "") {
		$("#errorDate").text("");
		var url = contextPath + "/accountAnalysisReport/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
<!-- Table for search criteria -->
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="selectCompanyId"  class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.companyNumber} - ${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	
	<tr>
		<td class="title2">Account</td>
		<td><input type="hidden" id="hdnAccountId"/>
		<input class="input" id="accountId" onkeydown="filterAccounts();" onkeyup="filterAccounts();"></td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanAccountError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td  class="title2">Division</td>
		<td>
			<select id="selectDivisionIdFrom" class="frmSelectClass"></select>
			<span style="font-size: small;">To</span>
			<select id="selectDivisionIdTo"  class="frmSelectClass" style="width: 197px;"></select>
		</td>
	</tr>
	
	<tr>
		<td class="title2">GL Date </td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="errorDate" class="error"></span>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateAccountAnalysis();"/></td>
	</tr>
</table>
</body>
<div>
	<iframe id="iFrame"></iframe>
</div>
</html>