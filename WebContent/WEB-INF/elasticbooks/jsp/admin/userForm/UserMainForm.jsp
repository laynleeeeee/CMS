<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../include.jsp"%>

<!--     Description: User Form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>User Form</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
var userGroupName = null;
var positionName = null;
$(function() {
	$("#btnAddUser").on("click", function () {
		$("#divUserForm").load(contextPath + "/admin/userForms/form", function () {
			$("html, body").animate({scrollTop: $("#divUserForm").offset().top}, 0050);
		});
	});

	$(document).on("click", "#btnEditUser", function () {
		var id = getCheckedId ("cbUsers");
		$("#divUserForm").load(contextPath + "/admin/userForms/form?userId="+id, function () {
			$("html, body").animate({scrollTop: $("#divUserForm").offset().top}, 0050);
		});
	});

	$("#txtUsername, #txtFirstName, #txtLastName").bind("keypress", function (e) {
		if(e.which == 13){
			searchUsers();
			e.preventDefault();
		}
	});
});

function saveUser() {
	$("#btnSaveUser").attr("disabled", "disabled");
	doPostWithCallBack ("frmUser", "divUserForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanUserMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "Username " + $("#username").val() + ".");
			$("#divUserForm").html("");
			$("html, body").animate({scrollTop: $("#divSearch").offset().top}, 0050);
			searchUsers();
		} else {
			$("#divUserForm").html(data);
			$("#txtUserGroup").val(userGroupName);
			$("#txtPosition").val(positionName);
		}
	});
}

function cancelForm() {
	$("#divUserForm").html("");
	$("html, body").animate({scrollTop: $("#dataTable").offset().top}, 0050);
	searchUsers();
}

function getCommonParam(){
	var username = processSearchName($("#txtUsername").val());
	var firstName = processSearchName($("#txtFirstName").val());
	var lastName = processSearchName($("#txtLastName").val());
	var userGroupId = $("#selectUserGroup").val();
	var positionId = $("#selectPosition").val();
	var status = $("#selectStatus").val();
	var loginStatus = $("#selectLoginStatus").val();
	return "username="+username+"&firstName="+firstName+"&lastName="+lastName
		+"&userGroupId="+userGroupId+"&positionId="+positionId+"&loginStatus="+loginStatus+"&status="+status;
}

function searchUsers(){
	doSearch("divUserTable","/admin/userForms?"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
<div id="divSearch">
	<table class="formTable">
		<tr>
			<td width="20%" class="title">Username</td>
			<td>
				<input id="txtUsername"/>
			</td>
		</tr>
		<tr>
			<td width="20%" class="title">First Name</td>
			<td>
				<input id="txtFirstName"/>
			</td>
		</tr>
		<tr>
			<td width="20%" class="title">Last Name</td>
			<td>
				<input id="txtLastName"/>
			</td>
		</tr>
		<tr>
			<td width="20%" class="title">User Group</td>
			<td>
				<select id="selectUserGroup" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="userGroup" items="${userGroups}">
						<option id="${userGroup.id}" value="${userGroup.id}">${userGroup.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td width="20%" class="title">Position</td>
			<td>
				<select id="selectPosition" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="position" items="${positions}">
						<option id="${position.id}" value="${position.id}">${position.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td width="20%" class="title">Login Status</td>
			<td><select class="frmSmallSelectClass" id="selectLoginStatus">
				<option value=-1>All</option>
				<option value=0>Unblocked</option>
				<option value=1>Blocked</option>
				</select>
			</td>
		</tr>
		<tr>
			<td width="20%" class="title">Status</td>
			<td><select class="frmSmallSelectClass" id="selectStatus">
				<option value=-1>All</option>
				<option value=1>Active</option>
				<option value=0>Inactive</option></select>
				<input type="button" id="btnSearchUser" value="Search" onclick="searchUsers();"/>
			</td>
		</tr>
	</table>
</div>
<span id="spanUserMessage" class="message"></span>
<div id="divUserTable">
	<%@ include file = "UserTable.jsp" %>
</div>
<div class="controls"> 
	<input type="button" id="btnAddUser" value="Add" />
	<input type="button" id="btnEditUser" value="Edit" />
</div>
<br>
<div id="divUserForm" class="formDiv" style="margin-top: 50px;"></div>
</body>
</html>