<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Account main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Account</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
$(function () {
	$("#selectAccountTypes, #txtAccountNumber, #txtAccountName, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchAccount();
			e.preventDefault();
		}
	});

	$("#btnAddAccount").click(function () {
		$("#divAccountForm").load(contextPath + "/admin/accounts/form");
		$("html, body").animate({scrollTop: $("#divAccountForm").offset().top}, 0050);
	});

	$("#btnEditAccount").click(function () {
		var id = getCheckedId ("cbAccount");
		$("#divAccountForm").load(contextPath + "/admin/accounts/form?accountId="+id);
		$("html, body").animate({scrollTop: $("#divAccountForm").offset().top}, 0050);
	});
});

function hideOrShowAccountType(value) {
	value = $.trim(value);
	if (value != "") {
		$("#trAcctType").hide();
	} else {
		$("#trAcctType").show();
	}
}

function saveAccount() {
	$("#parentAccountName").val($.trim($("#txtParentAccount").val()));
	$("#btnSaveAccount").attr("disabled", "disabled");
	doPostWithCallBack ("account", "divAccountForm", function (data) {
		if (data.substring(0,5) == "saved") {
			$("#divAccountTable").load(contextPath+"/admin/accounts?"+getCommonParam()+"&pageNumber=1");
			$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "account " 
					+ $("#accountName").val() + ".");
			$("#divAccountForm").html("");
		} else {
			var parentAcct = $("#txtParentAccount").val();
			$("#divAccountForm").html(data);
			$("#txtParentAccount").val(parentAcct);
			hideOrShowAccountType(parentAcct);
	 		if (!containsAccountType)
 				$("#trRelatedAccount").hide();
		}
	});
	$("html, body").animate({scrollTop: $("#divAccountTypeTable").offset().top}, 0050);
}

function cancelForm() {
	$("#divAccountForm").html("");
	searchAccount();
}

function getCommonParam() {
	var accountTypeId = $("#selectAccountTypes").val();
	var accountNumber = processSearchName($("#txtAccountNumber").val());
	var accountName = processSearchName($("#txtAccountName").val());
	var status = $("#selectStatus").val();
	return "accountTypeId="+accountTypeId+"&accountNumber="+
		accountNumber+"&accountName="+accountName+"&status="+status;
}

function searchAccount() {
	doSearch ("divAccountTable", "/admin/accounts?"+getCommonParam()+"&pageNumber=1");
}

function printChartOfAccounts() {
	window.open(contextPath + "/admin/accounts/printChartOfAccounts");
}

</script>
</head>
<body>
	<div id="divSearch">
		<table class="formTable">
			<tr>
				<td class="title">Account Type</td>
				<td class="value">
					<select id="selectAccountTypes" class="frmSmallSelectClass">
						<option id="atAll" value="-1">All</option>
						<c:forEach var="at" items="${accountTypes}">
							<option id="at${nb.id}"
								value="${at.id}">${at.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="title">Account Number</td>
				<td class="value"><input type="text" id="txtAccountNumber" maxLength="10" class="inputSmall"></td>
			</tr>
			<tr>
				<td class="title">Account Name</td>
				<td class="value">
					<input type="text" id="txtAccountName" maxLength="100" class="inputSmall">
				</td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td class="value">
					<select id="selectStatus" class="frmSmallSelectClass">
						<c:forEach var="status" items="${status}">
							<option>${status}</option>
						</c:forEach>
					</select>
					&nbsp;&nbsp;<input type="button" id="btnSearchAccount" value="Search" onclick="searchAccount();"/>
					&nbsp;&nbsp;<input type="button" value="Print Chart Of Accounts" onclick="printChartOfAccounts();"/>
				</td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
		<div id="divAccountTable">
		<%@ include file = "AccountTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddAccount" value="Add"  />
		<input type="button" id="btnEditAccount" value="Edit"  />
	</div>
	<div id="divAccountForm" style="margin-top: 50px;"></div>
</body>
</html>