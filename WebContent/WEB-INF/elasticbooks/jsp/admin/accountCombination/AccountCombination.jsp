<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Elasticbooks Account Combination.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Account Combination</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
function loadAccountCombination () {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var accountId = $("#accountId").val();
	$.ajax({
		url: contextPath+"/admin/accountCombinations/loadAccountCombination?companyId="+companyId+
				"&divisionId="+divisionId+"&accountId="+accountId,
		success : function (responseText) {
			$("#combination").val(responseText);
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "text"
	});
}

$(function() {
	$("#companyId, #divisionId, #accountId").live("change", function () {
		loadAccountCombination();
	});

	$("#txtCompanyNumber, #txtDivisionNumber, #txtAccountNumber, #txtCompanyName, #txtDivisionName, #txtAccountName").
		bind("keypress", function (e) {
		if (e.which == 13) {
			searchAccountCombination();
			e.preventDefault();
		}
	});

	$("#btnAddCombination").click(function() {
		$("#accountCombinationForm").load(contextPath + "/admin/accountCombinations/form");
		$("html, body").animate({scrollTop: $("#accountCombinationForm").offset().top}, 0050);
	});

	$("#btnEditCombination").click(function() {
		var id = getCheckedId ("cbCombination");
		$("#accountCombinationForm").load(contextPath + "/admin/accountCombinations/form?accountCombinationId="+id);
		$("html, body").animate({scrollTop: $("#accountCombinationForm").offset().top}, 0050);
	});
});

var isSaving = false;
function saveAcctCombi() {
	if(isSaving == false) {
		isSaving = true;
		$("#btnSaveAccountCombination").attr("disabled", "disabled");
		doPostWithCallBack ("accountCombinationFormId", "accountCombinationForm", function(data) {
			if (data.substring(0,5) == "saved") {
				$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "account combination "
		 				+ $(".accountCombiName").val() + ".");
				$("#accountCombinationForm").html("");
				searchAccountCombination();
			} else {
				$("#accountCombinationForm").html(data);
			}
			isSaving = false;
			$("#btnSaveArCustomer").removeAttr("disabled");
		});
	}
}

function cancelForm() {
	$("#accountCombinationForm").html("");
	searchAccountCombination();
}

function getCommonParam() {
	var companyNumber = removeSpace($("#txtCompanyNumber").val());
	var divisionNumber = removeSpace($("#txtDivisionNumber").val());
	var accountNumber = removeSpace($("#txtAccountNumber").val());
	var companyName = processSearchName($("#txtCompanyName").val());
	var divisionName = processSearchName($("#txtDivisionName").val());
	var accountName = processSearchName($("#txtAccountName").val());
	var status = $("#selectStatus").val();
	return "?companyNumber="+companyNumber+"&divisionNumber="+divisionNumber+"&accountNumber="+
			accountNumber+"&companyName="+companyName+"&divisionName="+divisionName+"&accountName="+accountName+
			"&status="+status;
}

function searchAccountCombination() {
	doSearch ("accountCombinationTable", "/admin/accountCombinations"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td class="title">Number</td>
				<td class="value">
					<input type="text" id="txtCompanyNumber" class="inputSmall" style="width: 70px;" maxLength="5" />
					<input type="text" id="txtDivisionNumber" class="inputSmall" style="width: 70px;" maxLength="5" />
					<input type="text" id="txtAccountNumber" class="inputSmall" style="width: 70px;" maxLength="10" />
				</td>
			</tr>
			<tr>
				<td class="title">Company</td>
				<td class="value"><input type="text" id="txtCompanyName" class="inputSmall" style="width: 217px;"></td>
			</tr>
			<tr>
				<td class="title">Division</td>
				<td class="value"><input type="text" id="txtDivisionName" class="inputSmall" style="width: 217px;"></td>
			</tr>
			<tr>
				<td class="title">Account</td>
				<td class="value">
					<input type="text" id="txtAccountName" class="inputSmall" style="width: 217px;">
				</td>
			</tr>
			<tr>
				<td class="title">Status:</td>
				<td class="value"><select id="selectStatus" class="frmSmallSelectClass" style="width: 217px;">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" id="btnSearchAccountCombination" value="Search" onclick="searchAccountCombination();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="veil"></div>
	<div id="accountCombinationTable">
		<%@ include file="AccountCombinationTable.jsp" %>
	</div>
	<div class="controls" align="right">
		<input type="button" id="btnAddCombination" name="btnAddCombination" value="Add" />
		<input type="button" id="btnEditCombination" name="btnEditCombination" value="Edit" />
	</div>
	<br /> <br />
	<div id="accountCombinationForm" style="margin-top: 20px;">
	</div>
</body>
</html>