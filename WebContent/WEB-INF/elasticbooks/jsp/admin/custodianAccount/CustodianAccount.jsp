<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Main jsp page for custodian account.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	$("#btneditCustodianAccount").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	searchCustodianAccount();
});

function loadDivisions(companyId, divisionId, accountId) {
	companyId = getCompany();
	divisionId = (divisionId == null || divisionId == "") ? 0 : divisionId;
	var uri = contextPath+"/getDivisions?companyId="+companyId+"&divisionId="+divisionId;
	$("#divisionId").empty();
	var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["numberAndName"];
			}
		};
	var postHandler = {
			doPost: function(data) {
				if(divisionId != 0)
					$("#divisionId").val(divisionId).attr("selected", true);
				loadAccounts(companyId, divisionId, accountId);
			}
	};
	loadPopulate (uri, false, null, "divisionId", optionParser, postHandler);
}

function loadAccounts(companyId, divisionId, accountId) {
	companyId = getCompany();

	divisionId = divisionId == null  || divisionId == 0 ? $("#divisionId option:selected").val() : divisionId;
	accountId = (accountId == null || accountId == "") ? 0 : accountId;
	var uri = contextPath+"/admin/custodianAccount/loadAccounts?companyId="+companyId+"&divisionId="
			+divisionId+"&accountId="+accountId;
	$("#accountId").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["number"] +" - " +rowObject["accountName"];
		}
	};
	var postHandler = {
			doPost: function(data) {
				if(accountId != 0)
					$("#accountId").val(accountId).attr("selected", true);
			}
	};
	loadPopulate (uri, false, null, "accountId", optionParser, postHandler);
}

function getCompany () {
	var companyId = $("#companyId option:selected").val();
	if (companyId == null) {
		companyId = $("#companyId").val()
	}
	return companyId;
}

var isSaving = false;
function saveCustodianAccount() {
	if (!isSaving) {
		isSaving = true;
		$("#txtCustodianName").removeAttr("disabled");
		$("#btnSaveCustodianAccount").attr("disabled", "disabled");
		DIVISION_ID = $("#divisionId").val();
		ACCOUNT_ID = $("#accountId").val();
		doPostWithCallBack ("custodianAccountFormId", "custodianAccountForm", function(data) {
			if (data.substring(0,5) == "saved") {
				$("#spanCustodianAccountMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
						+ "Custodian Account " + $("#txtCustodianName").val() + ".");
				$("#custodianAccountForm").html("");
				searchCustodianAccount();
			} else {
				var companyId = getCompany();
				$("#custodianAccountForm").html(data);
				$("#companyId").val(companyId).attr("selected", true);
				if ("${custodianAccount.id}" != 0) {
					$("#txtCustodianName").attr("disabled", "disabled");
				}
			}
			isSaving = false;
			$("#btnSaveCustodianAccount").removeAttr("disabled");
		});
	}
}

$(function () {
	$("#txtCAName, #txtCName").bind("keypress", function (e) {
		if (e.which == 13) {
			searchCustodianAccount();
			e.preventDefault();
		}
	});
});
function addCustodianAccount() {
	$("#custodianAccountForm").load(contextPath + "/admin/custodianAccount/form");
	$("html, body").animate({scrollTop: $("#custodianAccountForm").offset().top}, 0050);
}

function getCommonParam() {
	var custodianName = processSearchName($("#txtCName").val());
	var custodianAccountName = processSearchName($("#txtCAName").val());
	var companyId = $("#selectCompany").val();
	var termId = $("#selectTerm").val();
	var status = $("#selectStatus").val();
	return "?custodianName="+custodianName+"&custodianAccountName="+custodianAccountName+"&companyId="+companyId+"&termId="+termId+"&status="+status;
}

function searchCustodianAccount() {
	doSearch ("custodianAccountTable", "/admin/custodianAccount/search"+getCommonParam()+"&pageNumber=1");
}

function editCustodianAccount() {
	var id = getCheckedId("cbCustodianAccount");
	$("#custodianAccountForm").load(contextPath + "/admin/custodianAccount/form?pId="+id);
	$("html, body").animate({scrollTop: $("#custodianAccountForm").offset().top}, 0050);
}

function cancelCustodianAccount() {
	$("#custodianAccountForm").html("");
	$("html, body").animate({scrollTop: $("#custodianAccountTable").offset().top}, 0050);
	searchCustodianAccount();
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Custodian Name</td>
				<td><input type="text" id="txtCName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="25%" class="title">Company</td>
				<td><select id="selectCompany" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="comp" items="${companies}">
						<option value="${comp.id}">${comp.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td width="25%" class="title">Custodian Account Name</td>
				<td><input type="text" id="txtCAName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="25%" class="title">Terms</td>
				<td><select id="selectTerm" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="term" items="${terms}">
						<option value="${term.id}">${term.name}</option>
					</c:forEach>
				</select></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchCustodianAccount();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanCustodianAccountMessage" class="message"></span>
	<div id="custodianAccountTable">
		<%@ include file="CustodianAccountTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddCustodianAccount" value="Add" onclick="addCustodianAccount();"></input>
		<input type="button" id="btnEditCustodianAccount" value="Edit" onclick="editCustodianAccount();"></input>
	</div>
	<br>
	<br>
	<div id="custodianAccountForm" style="margin-top: 20px;">
	</div>
</body>
</html>