<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR Line Setup main jsp page.
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
	$("#btnEditLineSetup").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	searchLineSetup();
});

function loadDivisions(companyId, divisionId, accountId, discAccId) {
	if(companyId == null)
		companyId = $("#companyId option:selected").val();
	if(companyId != undefined){
		var uri = contextPath+"/getDivisions?companyId="+companyId
				+ ((divisionId != "") ? "&divisionId="+divisionId : "");
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
					if(divisionId != 0) {
						$("#divisionId").val(divisionId).attr('selected',true);
					}
					loadAccounts(companyId, divisionId, accountId);
					loadDiscAccounts(companyId, divisionId, discAccId);
				}
		};
		loadPopulate (uri, false, null, "divisionId", optionParser, postHandler);
	}
}

function loadAccounts(companyId, divisionId, accountId) {
	if(companyId == null)
		companyId = $("#companyId option:selected").val();
	if(divisionId == null || divisionId == 0)
		divisionId = $("#divisionId option:selected").val();
	if(companyId != undefined && divisionId != undefined){
		var uri = contextPath+"/getAccounts?companyId="+companyId+"&divisionId="+divisionId
				+ ((typeof accountId != "undefined" || accountId != null) ? "&accountId="+accountId : "");
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
						if(accountId != 0)
							$("#accountId").val(accountId).attr('selected',true);
					}
			};
		loadPopulate (uri, false, null, "accountId", optionParser, postHandler);
	}
}

function loadDiscAccounts(companyId, divisionId, accountId) {
	if(companyId == null)
		companyId = $("#companyId option:selected").val();
	if(divisionId == null || divisionId == 0)
		divisionId = $("#divisionId option:selected").val();
	if(companyId != undefined && divisionId != undefined){
		var uri = contextPath+"/getAccounts?companyId="+companyId+"&divisionId="+divisionId
				+ ((typeof accountId != "undefined" || accountId != null) ? "&accountId="+accountId : "");
		$("#discAccId").empty();
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
						if(accountId != 0)
							$("#discAccId").val(accountId).attr('selected',true);
					}
			};
		loadPopulate (uri, false, null, "discAccId", optionParser, postHandler);
	}
}

function saveArLine() {
	$("#btnSaveArLine").attr("disabled", "disabled");
	$("#amount").val(accounting.unformat($("#amount").val()));
	doPostWithCallBack ("arLineSetupFormId", "lineSetupForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanLineSetupMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
					+ "AR Line Setup " + $("#txtName").val() + ".");
			$("#lineSetupForm").html("");
			searchLineSetup();
		} else {
			companyId = $("#companyId").val();
			$("#lineSetupForm").html(data);
			$("#companyId").val(companyId);
		}
	});
}

$(function () {
	$("#txtName").bind("keypress", function (e) {
		if (e.which == 13) {
			searchLineSetup();
			e.preventDefault();
		}
	});
});

function addLineSetup() {
	$("#lineSetupForm").load(contextPath + "/admin/arLineSetup/form");
	$("html, body").animate({scrollTop: $("#lineSetupForm").offset().top}, 0050);
}

function getCommonParam() {
	var setupName = processSearchName($("#txtName").val());
	var companyId = $("#selectCompnay").val();
	var status = $("#selectStatus").val();
	return "?setupName="+setupName+"&companyId="+companyId+"&status="+status;
}

function searchLineSetup() {
	doSearch ("lineSetupTable", "/admin/arLineSetup/search"+getCommonParam()+"&pageNumber=1");
}

function editLineSetup() {
	var id = getCheckedId("cbLineSetup");
	$("#lineSetupForm").load(contextPath + "/admin/arLineSetup/form?pId="+id);
	$("html, body").animate({scrollTop: $("#lineSetupForm").offset().top}, 0050);
}

function cancelLineSetup() {
	$("#lineSetupForm").html("");
	$("html, body").animate({scrollTop: $("#lineSetupTable").offset().top}, 0050);
	searchLineSetup();
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
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
				<input type="button" value="Search" onclick="searchLineSetup();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanLineSetupMessage" class="message"></span>
	<div id="lineSetupTable">
		<%@ include file="ArLineSetupTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddLineSetup" value="Add" onclick="addLineSetup();"></input>
		<input type="button" id="btnEditLineSetup" value="Edit" onclick="editLineSetup();"></input>
	</div>
	<br>
	<br>
	<div id="lineSetupForm" style="margin-top: 20px;">
	</div>
</body>
</html>