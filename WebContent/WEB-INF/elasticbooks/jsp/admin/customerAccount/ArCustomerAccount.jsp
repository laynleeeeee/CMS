<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: main jsp page for customer account.
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
	$("#btnEditCustomerAcct").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	searchCustomerAcct();
});

function loadDivisions(companyId, divisionId, accountId, arLineId) {
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
				loadArLines(companyId, arLineId);
			}
	};
	loadPopulate (uri, false, null, "divisionId", optionParser, postHandler);
}

function loadAccounts(companyId, divisionId, accountId) {
	companyId = getCompany();

	divisionId = divisionId == null  || divisionId == 0 ? $("#divisionId option:selected").val() : divisionId;
	accountId = (accountId == null || accountId == "") ? 0 : accountId;
	var uri = contextPath+"/admin/arCustomerAccount/loadAccounts?companyId="+companyId+"&divisionId="
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

function loadArLines(companyId, arLineId) {
	companyId = getCompany();
	arLineId = arLineId == null ? $("#transLineId option:selected").val() : arLineId;
	var uri = contextPath+"/admin/arCustomerAccount/loadArLines?companyId="+companyId+"&arLineId="+arLineId;
	$("#transLineId").empty();
	$("#transLineId").append("<option selected='selected' value='0'></option>");
	var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
	var postHandler = {
			doPost: function(data) {
				$("#transLineId").val(arLineId).attr("selected", true);
			}
	};
	loadPopulate (uri, false, null, "transLineId", optionParser, postHandler);
}

function saveCustomerAccount() {
	DIVISION_ID = $("#divisionId").val();
	ACCOUNT_ID = $("#accountId").val();
	doPostWithCallBack ("arCustomerAcctFormId", "customerAcctForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanCustomerAcctMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
					+ "Customer Account " + $("#name").val() + ".");
			$("#customerAcctForm").html("");
			searchCustomerAcct();
		} else {
			var companyId = getCompany();
			$("#customerAcctForm").html(data);
			$("#companyId").val(companyId).attr("selected", true);
		}
	});
}

$(function () {
	$("#txtCustomerAcctName, #txtCustomerName").bind("keypress", function (e) {
		if (e.which == 13) {
			searchCustomerAcct();
			e.preventDefault();
		}
	});
});
function addCustomerAcct() {
	$("#customerAcctForm").load(contextPath + "/admin/arCustomerAccount/form");
	$("html, body").animate({scrollTop: $("#customerAcctForm").offset().top}, 0050);
}

function getCommonParam() {
	var customerName = processSearchName($("#txtCustomerName").val());
	var customerAcctName = processSearchName($("#txtCustomerAcctName").val());
	var companyId = $("#selectCompnay").val();
	var termId = $("#selectTerm").val();
	var status = $("#selectStatus").val();
	var divisionId = $("#slctDivision").val();
	return "?customerName="+customerName+"&customerAcctName="+customerAcctName+"&companyId="+companyId
				+"&termId="+termId+"&status="+status+"&divisionId="+divisionId;
}

function searchCustomerAcct() {
	doSearch ("customerAcctTable", "/admin/arCustomerAccount/search"+getCommonParam()+"&pageNumber=1");
}

function editCustomerAcct() {
	var id = getCheckedId("cbCustomerAcct");
	$("#customerAcctForm").load(contextPath + "/admin/arCustomerAccount/form?pId="+id);
	$("html, body").animate({scrollTop: $("#customerAcctForm").offset().top}, 0050);
}

function cancelCustomerAcct() {
	$("#customerAcctForm").html("");
	$("html, body").animate({scrollTop: $("#customerAcctTable").offset().top}, 0050);
	searchCustomerAcct();
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Customer Name</td>
				<td><input type="text" id="txtCustomerName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="25%" class="title">Company</td>
				<td><select id="selectCompnay" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="comp" items="${companies}">
						<option value="${comp.id}">${comp.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td class="title">Division</td>
				<td>
					<select id="slctDivision" class="frmSmallSelectClass">
						<option value="-1">All</option>
						<c:forEach var="division" items="${divisions}">
							<option value="${division.id}">${division.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td width="25%" class="title">Account Name</td>
				<td><input type="text" id="txtCustomerAcctName" class="inputSmall"></td>
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
				<input type="button" value="Search" onclick="searchCustomerAcct();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanCustomerAcctMessage" class="message"></span>
	<div id="customerAcctTable">
		<%@ include file="ArCustomerAccountTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddCustomerAcct" value="Add" onclick="addCustomerAcct();"></input>
		<input type="button" id="btnEditCustomerAcct" value="Edit" onclick="editCustomerAcct();"></input>
	</div>
	<br>
	<br>
	<div id="customerAcctForm" style="margin-top: 20px;">
	</div>
</body>
</html>