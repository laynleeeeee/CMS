<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Inventory Account main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
var isReload = false;
function addInventoryAcct() {
	$("#divInventoryAcctForm").load(contextPath + "/admin/inventoryAccount/form", function (){
		$("html, body").animate({
			scrollTop: $("#divInventoryAcctForm").offset().top}, 0050);
	});
}

function editInventoryAcct() {
	var id = getCheckedId("cbInvAcct");
	$("#divInventoryAcctForm").load(contextPath + "/admin/inventoryAccount/form?inventoryAcctId="+id, function (){
		$("html, body").animate({
			scrollTop: $("#divInventoryAcctForm").offset().top}, 0050);
	});
}

function hideAddForm() {
	$("#divInventoryAcctForm").html("");
}

function saveInventoryAccount() {
	doPostWithCallBack ("inventoryAccount", "divInventoryAcctForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
					+ "Inventory Forms Account Setup.");
			dojo.byId("divInventoryAcctForm").innerHTML = "";
			searchInventoryAccts();
		} else {
			isReload = true;
			$("#divInventoryAcctForm").html(data);
		}
	});
}

function searchInventoryAccts() {
	var url = "/admin/inventoryAccount/search"+getCommonParam()+"&page=1";
	doSearch("divInvAcctTable", url);
}

function getCommonParam() {
	var companyId = $("#searchCompany").val();
	var statusId = $("#selectStatus").val();
	return "?companyId="+companyId+"&statusId="+statusId;
}
</script>
</head>
<body>
<div id="searchDiv">
	<table class="tableInfo">
		<tr>
			<td width="10%" class="title">Company </td>
			<td>
				<select id="searchCompany" class="frmSelectClass">
					<option value="-1">All</option>
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td class="title">Status</td>
			<td>
				<select id="selectStatus" class="frmSelectClass">
					<option value="-1">ALL</option>
					<option value="1">Active</option>
					<option value="0">Inactive</option>
				</select>
				<input type="button" id="btnSearch" value="Search" onclick="searchInventoryAccts();">
			</td>
		</tr>
	</table>
</div>
<span id="spanMessage" class="message"></span>
<div id="divBody">
	<div id="divInvAcctTable">
		<%@ include file="InventoryAccountTable.jsp" %>
	</div>
</div>

<div class="controls" align="right">
	<input type="button" id="btnAddInvAccount" value="Add" onclick="addInventoryAcct();"/>
	<input type="button" id="btnEditInvAccount" value="Edit" onclick="editInventoryAcct();"/>
</div>
<br>
<div id="divInventoryAcctForm" style="margin-top: 20px;"></div>
</body>
</html>