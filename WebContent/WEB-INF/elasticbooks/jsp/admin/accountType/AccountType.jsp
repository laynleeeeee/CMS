<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Account type main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Account Type</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
$(function () {
	$("#selectNormalBalances, #txtAccountTypeName").bind("keypress", function (e) {
		if (e.which == 13) {
			searchAccountType();
			e.preventDefault();
		}
	});

	$("#btnAddAccountType").click(function () {
		$("#divAccountTypeForm").load(contextPath + "/admin/accountTypes/form");
		$("html, body").animate({scrollTop: $("#divAccountTypeForm").offset().top}, 0050);
	});

	$("#btnEditAccountType").click(function () {
		var cbs = document.getElementsByName("cbAccountType");
		var id = 0;
		for (var index =0; index < cbs.length; index++) {
			var cb = cbs[index];
			if (cb.checked == true){
				id = cb.id;
				break;
			}
		}
		$("#divAccountTypeForm").load(contextPath + "/admin/accountTypes/form?accountTypeId="+id);
		$("html, body").animate({scrollTop: $("#divAccountTypeForm").offset().top}, 0050);
	});
});

function saveAcctType() {
	$("#btnSaveAccountType").attr("disabled", "disabled");

	doPostWithCallBack ("accountType", "divAccountTypeForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "account type " + $("#accountTypeName").val() + ".");
			$("#divAccountTypeForm").html("");
			$("html, body").animate({scrollTop: $("#divAccountTypeTable").offset().top}, 0050);
			searchAccountType();
		}
	});

}

function cancelForm() {
	$("#divAccountTypeForm").html("");
	searchAccountType();
}

function getCommonParam() {
	var accountTypeName = processSearchName($("#txtAccountTypeName").val());
	var normalBalanceId = $("#selectNormalBalances").val();
	return "accountTypeName="+accountTypeName+"&normalBalanceId="+normalBalanceId;
}

function searchAccountType() {
	doSearch ("divAccountTypeTable", "/admin/accountTypes?"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
	<div id="divSearch">
		<table class="formTable">
			<tr>
				<td width="10%" class="title">Name</td>
				<td><input type="text" id="txtAccountTypeName" maxLength="100"></td>
			</tr>
			<tr>
				<td width="10%" class="title">Normal Balance</td>
				<td>
					<select id="selectNormalBalances" class="frmSmallSelectClass">
						<option id="nbAll" value="-1">All</option>
						<c:forEach var="nb" items="${normalBalances}">
							<option id="nb${nb.id}"
								value="${nb.id}">${nb.name}</option>
						</c:forEach>
					</select>
					<input type="button" id="btnSearchAccountType" value="Search" onclick="searchAccountType();"/> 
				</td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divAccountTypeTable">
		<%@ include file = "AccountTypeTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddAccountType" value="Add"  />
		<input type="button" id="btnEditAccountType" value="Edit"  />
	</div>
	<div id="divAccountTypeForm" style="margin-top: 50px;"></div>
</body>
</html>