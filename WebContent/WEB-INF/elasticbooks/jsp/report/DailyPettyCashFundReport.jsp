<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--

	Description:Daily Petty Cash Fund Report.
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/ZeroClipboard.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/TableTools.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<title>Daily Petty Cash Fund Report</title>
<script type="text/javascript">
$(document).ready (function () {
	var newDate = parseServerDate(new Date);
	$("#date").val(newDate);
});

function getCommonParam () {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var custodianId = $("#custodianId").val();
	var transactionStatusId = $("#transactionStatusId").val();
	var date = $("#date").val();
	var formatType = $("#formatType").val();
	return "?companyId=" + companyId + "&divisionId="+ divisionId + "&custodianId=" + custodianId
			+ "&transactionStatusId=" + transactionStatusId+ "&date=" + date+"&formatType=" + formatType;
}

function generateDailyPettyCash() {
	$("#spanCompanyError").text("");
	$("#spanCustodianError").text("");
	$("#spanDateError").text("");

	if ($("#companyId option:selected").val() == -1) {
		$("#spanCompanyError").text("Company is required.");
	}

	if ($("#custodianId").val() == "") {
		if ($("#custodianId").val() != "") {
			$("#spanCustomerError").text("Invalid custodian");
		} else {
			$("#custodianId").val(-1);
		}
	}
	var asOfDate = $.trim($("#date").val());
	if (asOfDate == "") {
		$("#spanDateError").text("Date is required.");
	}

	if ($("#companyId option:selected").val() && asOfDate != "") {
		var url = contextPath + "/dailyPettyCashFundReport/generateReport" + getCommonParam();
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}
}

function getCustodianAccount() {
	var custodianName = $.trim($("#custodianName").val());
	if (custodianName != "") {
		var companyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var uri = contextPath + "/getUserCustodians?companyId="+companyId
			+"&divisionId="+divisionId+"&name="+processSearchName(custodianName)+"&isExact=true";
		$.ajax({
			url: uri,
			success : function(custodianAccount) {
				$("#spanCustodianAccount").text("");
				if (custodianAccount != null && custodianAccount[0] != undefined) {
					$("#custodianId").val(custodianAccount[0].id);
					$("#custodianName").val(custodianAccount[0].custodianAccount.custodianName);
				} else {
					$("#custodianId").val("");
					$("#cnameError").text("");
					$("#spanCustodianAccount").text("Invalid custodian account.");
				}
			},
			error : function(error) {
				$("#spanCustodianAccount").text("Invalid custodian account.");
			},
			dataType: "json"
		});
	}
}

function showCustodianAccounts() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var custodianName = $("#custodianName").val();
	var uri = contextPath + "/getUserCustodians?companyId="+companyId
			+"&divisionId="+divisionId+"&name="+processSearchName(custodianName);
	$("#custodianName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#custodianId").val(ui.item.id);
			$(this).val(ui.item.custodianAccount.custodianName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.custodianAccount.custodianName + "</a>")
			.appendTo( ul );
	};
}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" class="frmSelectClass">
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
		<td>
			<select id="divisionId" class="frmSelectClass" >
				<option selected='selected' value=-1>All</option>
				<c:forEach var="division" items="${division}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Custodian</td>
		<td>
			<input id="custodianName" class="input" onkeypress="showCustodianAccounts();" onblur="getCustodianAccount();">
			<input type="hidden" id="custodianId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCustodianError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Date </td>
		<td class="tdDateFilter">
			<input type="text" id="date" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('date')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDateError" class="error"></span>
		</td>
	</tr>
		<tr>
		<td class="title2">Transaction Status</td>
		<td>
			<select id="transactionStatusId" class="frmSelectClass">
				<c:forEach var="fs" items="${formStatuses}">
					<option value="${fs.id}">${fs.description}</option>
				</c:forEach>
				<option value="-1">ALL</option>
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
		<td colspan="3"><input type="button" value="Generate" onclick="generateDailyPettyCash()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>