<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: JSP page for User Custodian.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(function () {
	$("#txtCAName, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchUserCustodian();
			e.preventDefault();
		}
	});
});

function getCommonParam() {
	var companyId = $("#selectCompany").val();
	var divisionId = $("#selectDivision").val();
	var userCustodianName = processSearchName($("#txtCAName").val());
	var status = $("#selectStatus").val();
	return "?companyId="+companyId+"&divisionId="+divisionId+"&userCustodianName="+userCustodianName+"&status="+status;
}

function searchUserCustodian() {
	doSearch ("userCustodianTable", "/admin/userCustodian/search"+getCommonParam()+"&pageNumber=1");
}

function addUserCustodian() {
	$("#divUserCustodianForm").load(contextPath + "/admin/userCustodian/form");
	$("html, body").animate({scrollTop: $("#divUserCustodianForm").offset().top}, 0050);
}

function editUserCustodian() {
	var id = getCheckedId("cbUserCustodian");
	$("#divUserCustodianForm").load(contextPath + "/admin/userCustodian/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divUserCustodianForm").offset().top}, 0050);
}

function cancelUserCustodian() {
	$("#divUserCustodianForm").html("");
	$("html, body").animate({scrollTop: $("#userCustodianTable").offset().top}, 0050);
	searchUserCustodian();
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
				<td width="25%" class="title">Division</td>
				<td><select id="selectDivision" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="divs" items="${divisions}">
						<option value="${divs.id}">${divs.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td width="25%" class="title">Custodian Name</td>
				<td><input type="text" id="txtCAName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchUserCustodian();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanUserCustodianMsg" class="message"></span>
	<div id="userCustodianTable">
		<%@ include file="UserCustodianTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddUserCustodian" value="Add" onclick="addUserCustodian();"></input>
		<input type="button" id="btnEditUserCustodian" value="Edit" onclick="editUserCustodian();"></input>
	</div>
	<br>
	<br>
	<div id="divUserCustodianForm" style="margin-top: 20px;">
	</div>
</body>
</html>