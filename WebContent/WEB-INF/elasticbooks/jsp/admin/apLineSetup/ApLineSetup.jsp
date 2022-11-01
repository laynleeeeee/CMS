<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AP Line Setup main jsp page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
var DIVISION_ID = 0;
var ACCOUNT_ID = 0;
$(document).ready(function () {
	$("#btnEditLineSetup").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	$("#selectCompany, #txtName, #selectStatus").bind("keypress", function (e){
		if (e.which == 13) {
			search();
			e.preventDefault();
		}
	});
});

function loadDivisions(divisionId) {
	var companyId = $("#companyId").val();
	if(companyId != null){
		var uri = contextPath+"/getDivisions?companyId="+companyId+(typeof divisionId != "undefined" ? "&divisionId="+divisionId : "");
		$("#divisionId").empty();
		var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["numberAndName"];
			}
		};
		postHandler = {
				doPost: function(data) {
					if(DIVISION_ID != 0) {
						$("#divisionId").val(DIVISION_ID);
					}
					loadAccounts(ACCOUNT_ID);
				}
		};
		loadPopulate (uri, false, DIVISION_ID, "divisionId", optionParser, postHandler);
	}
}

function loadAccounts(accountId) {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	if(divisionId) {
		var uri = contextPath+"/getAccounts?companyId="+companyId+"&divisionId="+divisionId+
		(typeof accountId != "undefined" ? "&accountId="+accountId : "");
		$("#accountId").empty();
		var optionParser =  {
				getValue: function (rowObject) {
					return rowObject["id"];
				},
				getLabel: function (rowObject) {
					return rowObject["number"] + " - "+rowObject["accountName"];
				}
			};
			postHandler = {
					doPost: function(data) {
						if(ACCOUNT_ID != 0) {
							$("#accountId").val(ACCOUNT_ID);
						}
					}
			};
		loadPopulate (uri, false, ACCOUNT_ID, "accountId", optionParser, postHandler);
	} else {
		$("#accountId").empty();
	}
}

function saveApLine() {
	$("#btnSaveApLine").attr("disabled", "disabled");
	var name = $("#txtName").val();
	doPostWithCallBack ("apLineForm", "apLineSetupForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanLineSetupMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
					+ "AP Line Setup " + name + ".");
			$("#apLineSetupForm").html("");
			search();
		} else {
			DIVISION_ID = $("#divisionId").val();
			ACCOUNT_ID = $("#accountId").val();
			$("#apLineSetupForm").html(data);
		}
	});
}

function addApLineSetup() {
	$("#apLineSetupForm").load(contextPath + "/admin/apLineSetup/form");
	$("html, body").animate({scrollTop: $("#apLineSetupForm").offset().top}, 0050);
}

function editApLineSetup() {
	var id = getCheckedId("cbLineSetup");
	$("#apLineSetupForm").load(contextPath + "/admin/apLineSetup/form?apLineSetupId="+id);
	$("html, body").animate({scrollTop: $("#apLineSetupForm").offset().top}, 0050);
}

function getCommonParam() {
	var setupName = processSearchName($("#txtName").val());
	var companyId = $("#selectCompany").val();
	var status = $("#selectStatus").val();
	return "?name="+setupName+"&companyId="+companyId+"&status="+status;
}

function search() {
	doSearch ("apLineSetupTable", "/admin/apLineSetup/search"+getCommonParam()+"&pageNumber=1");
}

function cancel() {
	$("#apLineSetupForm").html("");
	$("html, body").animate({scrollTop: $("#apLineSetupTable").offset().top}, 0050);
	search();
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
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
				<td width="25%" class="title">Name</td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="search();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanLineSetupMessage" class="message"></span>
	<div id="apLineSetupTable">
		<%@ include file="ApLineSetupTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddLineSetup" value="Add" onclick="addApLineSetup();"></input>
		<input type="button" id="btnEditLineSetup" value="Edit" onclick="editApLineSetup();"></input>
	</div>
	<br>
	<br>
	<div id="apLineSetupForm" style="margin-top: 20px;">
	</div>
</body>
</html>