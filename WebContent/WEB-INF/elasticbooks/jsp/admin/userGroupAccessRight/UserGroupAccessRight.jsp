<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<!-- 

	Description: User Group Access Right Form page. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
var CURRENT_UG_ID = $("#userGroupId").val();

function selectUserGroup () {
	var userGroupId = Number($("#userGroupId").val());

	if (userGroupId != 0) {
		$("#divUGAccessRightTable").load(
				contextPath+"/admin/userGroupAccessRight/form?userGroupId="+userGroupId, 
						function () {
							if (CURRENT_UG_ID != userGroupId) {
								CURRENT_UG_ID = userGroupId;
								$("#spanUGAccessRightMessage").text("");
							}
						});
	} else {
		alert ("Please select a user group.");
	}
}

function saveUGAccessRight () {
	var userGroupId = $("#userGroupId").val();
	if (userGroupId != 0) {
		doPostWithCallBack ("uGAccessRightDto", "divUGAccessRightTable", function(data) {
			if (data.substring(0,5) == "saved") {
				$("#spanUGAccessRightMessage").text("User group access right is successfully saved.");
				$("#divUGAccessRightTable").load(
						contextPath+"/admin/userGroupAccessRight/form?userGroupId="+userGroupId);
			} else {
				console.log(data);
			}
		});
	} else {
		alert ("Please select a user group.");
	}
}

function compModKeyWorkflow (isInit, checkbox, hdnIndex, moduleKey) {
	var totalModuleCode = parseInt($("#hdnWMC" + hdnIndex).val());
	var isChecked = $(checkbox).is(":checked");

	if ((isInit && isChecked) || isChecked) {
		totalModuleCode += parseInt(moduleKey);	
	} else if (!isInit && !isChecked){
		totalModuleCode -= parseInt(moduleKey);
	}
	$("#hdnWMC" + hdnIndex).val(totalModuleCode);
}
</script>
</head>
<body>
<div class="formDiv">
<form:form method="POST" commandName="uGAccessRightDto">
	<div class="modFormLabel">User Group Access Right</div>
	<br>
	<div class="modForm">
		<span class="title">User Group: </span>
		<form:select path="userGroupId" cssClass="frmSelectClass">
			<option value="0">PLEASE SELECT</option>
			<form:options items="${userGroups}" itemLabel="name" itemValue="id"/>
		</form:select>
		<input type="button" value="Select" onclick="selectUserGroup();"/>
		<br><br>
		<span id="spanUGAccessRightMessage" class="message"></span>
		<div id="divUGAccessRightTable">
			<%@ include file = "UserGroupAccessRightTable.jsp" %>
		</div>
		<table class="frmField_set">
			<tr>
				<td align="right">
					<input id="btnSaveUGAccessRight" onclick="saveUGAccessRight();" align="right" type="button" value="Update"/>
				</td>
			</tr>
		</table>
	</div>
</form:form>

</div>
</body>
</html>