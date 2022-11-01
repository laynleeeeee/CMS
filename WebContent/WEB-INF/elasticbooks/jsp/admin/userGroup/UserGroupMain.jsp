<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../include.jsp"%>

<!--     Description: User Group Main -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(function () {
	$("#txtName, #txtDescription").bind("keypress", function (e) {
		if (e.which == 13) {
			searchUserGroup();
			e.preventDefault();
		}
	});

	$("#btnSearchUserGroup").click(function () {
		searchUserGroup();
	});

	$("#btnAddUserGroup").click(function (){
		$("#divUserGroupForm").load(contextPath + "/admin/userGroup/form", function () {
			$("html, body").animate({scrollTop: $("#divUserGroupForm").offset().top}, 0050);
		});
	});

	$("#btnEditUserGroup").click(function (){
		var id = getCheckedId ("cbUserGroup");
		$("#divUserGroupForm").load(contextPath + "/admin/userGroup/form?userGroupId="+id, function () {
			$("html, body").animate({scrollTop: $("#divUserGroupForm").offset().top}, 0050);
		});
	});

	$(document).on("click", "#btnCancelSave", function() {
		cancelSave ();
	});
});

function saveUserGroup(btn) {
	$(btn).attr("disabled", "disabled");
	doPostWithCallBack ("userGroupFrm", "form", function (data) {
		if (data.substring(0,5) == "saved") {
			$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + $("#name").val()+".");
			searchUserGroup();
			cancelSave();
			$("html, body").animate({scrollTop: $("#divUserGroupTable").offset().top}, 0050);
		} 
		else
			$("#divUserGroupForm").html(data);
		$(btn).removeAttr("disabled");
	});
}

function getCommonParam() {
	var groupName = processSearchName($("#txtName").val());
	var description = processSearchName($("#txtDescription").val());
	var status = $("#selectStatus").val();
	return "groupName="+groupName+"&description="+description+"&status="+status;
}

function searchUserGroup() {
	$("#divUserGroupTable").load(contextPath+"/admin/userGroup?"+getCommonParam()+"&pageNumber=1");
}

function cancelSave () {
	$("#divUserGroupForm").html("");
	searchUserGroup();
}
</script>
</head>
<body>
	<div id="divSearch">
		<table class="formTable">
			<tr>
				<td class="title">Name</td>
				<td><input type="text" id="txtName" maxLength="100" size="20" class="inputSmall" /></td>
			</tr>
			<tr>
				<td class="title">Description</td>
				<td><input type="text" id="txtDescription" maxLength="100" size="20" class="inputSmall"/></td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td>
					<select id="selectStatus" class="frmSmallSelectClass">
						<option value=-1>All</option>
						<option value=1>Active</option>
						<option value=0>Inactive</option>
					</select>
					<input type="button" id="btnSearchUserGroup" value="Search" />
				</td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divUserGroupTable">
		<%@ include file="UserGroupTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddUserGroup" value="Add" />
		<input type="button" id="btnEditUserGroup" value="Edit" />
	</div>
	<div id="divUserGroupForm" style="margin-top: 50px;"></div>
</body>
</html>