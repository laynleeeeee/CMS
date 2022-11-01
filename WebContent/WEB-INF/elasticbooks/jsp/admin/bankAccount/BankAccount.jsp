<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Elasticbooks Bank Account page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Bank Account</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
$(function() {
	$("#btnAddBankAccount").click(function() {
		$("#bankAccountForm").load(contextPath + "/admin/bankAccounts/form",
			function (){
				$("html, body").animate({scrollTop: $("#bankAccountForm").offset().top}, 0050);
		});
	});

	$("#btnEditBankAccount").click(function() {
		var id = getCheckedId("cbBankAccount");
		$("#bankAccountForm").load(contextPath + "/admin/bankAccounts/form?bankAccountId=" +id,
			function (){
				$("html, body").animate({scrollTop: $("#bankAccountForm").offset().top}, 0050);
		});
	});

	$("#name, #selectCompany, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchBankAccounts();
			e.preventDefault();
		}
	});
});

function searchBankAccounts() {
// 	$("#btnSaveBankAcct").attr("disabled", "disabled");
	doPost ("bankAccount", "bankAccountForm",
			"bankAccountTable", "/admin/bankAccounts"+ getCommonParam() +"&pageNumber=1",
		function (){
			$("html, body").animate({scrollTop: $("#bankAccountTable").offset().top}, 0050);
	});
}

function cancelForm() {
	$("#bankAccountForm").html("");
	searchBankAccounts();
}

function getCommonParam(){
	var name = processSearchName($("#name").val());
	var companyId = $("#selectCompany").val();
	var status = $("#selectStatus").val();
	return "?name="+name+"&companyId="+companyId+"&status="+status;
}

function searchBankAccounts(){
	var url = "/admin/bankAccounts" + getCommonParam() +"&pageNumber=1";
	doSearch ("divBankAccountTable", url);
}

var isSaving = false;
function saveBankAcct() {
	if (!isSaving) {
		isSaving = true;
		selectedCBADivisionValue =  $("#cbaDivisionId option:selected").val();
		selectedCBAAccountValue =  $("#cbaAcctId option:selected").val();
		$("#btnSaveCheckbook").attr("disabled", "disabled");
		doPostWithCallBack ("bankAccount", "bankAccountForm", function(data) {
			if (data.substring(0,5) == "saved") {
				$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "bank "
						+ $("#bankAcctName").val() + ".");
				searchBankAccounts();
				dojo.byId("bankAccountForm").innerHTML = "";
			} else {
				$("#bankAccountForm").html(data);
				filterDivisions();
			}
			$("#btnSaveCheckbook").removeAttr("disabled");
			isSaving = false;
		});
	}
}

function cancelBankAcct() {
	$("#bankAccountForm").html("");
	searchBankAccounts();
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Name</td>
				<td><input type="text" id="name" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Company</td>
				<td>
					<select id="selectCompany" class="frmSmallSelectClass">
						<option value="-1">All</option>
						<c:forEach var="comp" items="${companies}">
							<option value="${comp.id}">${comp.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td>
					<select id="selectStatus" class="frmSmallSelectClass">
						<option value="-1">ALL</option>
						<option value="1">Active</option>
						<option value="0">Inactive</option>
					</select>&nbsp;&nbsp;
					<input type="button" id="btnSearchBankAccount" value="Search"
							onclick="searchBankAccounts();"/>
				</td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="veil"></div>
	<div id="divBankAccountTable">
		<%@ include file="BankAccountTable.jsp" %>
	</div>
	<div class="controls" align="right">
		<input type="button" id="btnAddBankAccount"  value="Add" />
		<input type="button" id="btnEditBankAccount" value="Edit" />
	</div>
	<br /> <br />
	<div id="bankAccountForm" style="margin-top: 20px;">
	</div>
</body>
</html>