<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../../jsp/include.jsp" %>
<!--  

	Description: User form.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>User Form</title>
<script type="text/javascript">
var userId = "${userId}";
$(document).ready(function () {
	if (userId != 0) {
		$("#txtUserGroup").val("${user.userGroup.name}");
		$("#txtPosition").val("${user.position.name}");
		userGroupName = $("#txtUserGroup").val();
		positionName = $("#txtPosition").val(); 
	}
});

function loadUserGroups(){
	loadACItems("txtUserGroup","userGroupId", null, contextPath + "/getUserGroups",
			contextPath + "/getUserGroup?name="+$("#txtUserGroup").val(),"name",
			function () {
				$("#spanUGError").text("");
			}, function() {
				$("#spanUGError").text("");
			}, function() {
				$("#spanUGError").text("");
			}, function() {
				$("#spanUGError").text("Invalid user group.");
				$("#userGroupId").val("");
	});
	userGroupName = $("#txtUserGroup").val();
}

function loadPositions(){
	loadACItems("txtPosition","positionId", null, contextPath + "/getPositions",
			contextPath + "/getPosition?name="+$("#txtPosition").val(),"name",
			function () {
				$("#spanPositionError").text("");
			}, function() {
				$("#spanPositionError").text("");
			}, function() {
				$("#spanPositionError").text("");
			}, function() {
				$("#spanPositionError").text("Invalid position.");
				$("#positionId").val("");
	});
	positionName = $("#txtPosition").val();
}

$(function () {
	$(document).on("keydown","#txtUserGroup", function(e) {		
		loadUserGroups();
	});
	
	$(document).on("keydown","#txtPosition", function(e) {		
		loadPositions();
	});
	
	$("#password").on("click", function () {
		$(this).select();
	});
});

</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="user" id="frmUser">
		<div class="modFormLabel">User</div>
			<br>
			<div class="modForm">
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="userGroupId"/>
			<form:hidden path="positionId"/>

			<fieldset class="frmField_set" style="margin-left: auto; margin-right: auto;">
				<legend>User Information</legend>
					<table>
						<tr>
							<td class="labels">* Username</td>
							<td class="value">
								<form:input path="username" id="username"/>
							</td>
						</tr>

						<tr>
							<td></td>
							<td class="value">
								<form:errors path="username" cssClass="error"/>
							</td>
						</tr>

						<tr>
							<td class="labels">* Password</td>
							<td class="value">
								<form:input type="password" path="password" id="password"/>
							</td>
						</tr>

						<tr>
							<td></td>
							<td class="value">
								<form:errors path="password" cssClass="error"/>
							</td>
						</tr>

						<tr>
							<td class="labels">* User Group</td>
							<td class="value">
								<input id="txtUserGroup" onblur="loadUserGroups();" />
							</td>
						</tr>

						<tr>
							<td></td>
							<td class="value">
								<form:errors id="userGroupError" path="userGroupId" cssClass="error"/>
								<span class="error" id="spanUGError" ></span>
							</td>
						</tr>

						<tr>
							<td class="labels">* Position</td>
							<td class="value">
								<input id="txtPosition" onblur="loadPositions();" />
							</td>
						</tr>

						<tr>
							<td></td>
							<td class="value">
								<form:errors id="positionError" path="positionId" cssClass="error"/>
								<span class="error" id="spanPositionError" ></span>
							</td>
						</tr>

						<c:if test="${userId != 0}">
							<tr>
								<td class="labels">* Block User</td>
								<td class="value">
									<form:checkbox path="userLoginStatus.blockUser"/>
								</td>
							</tr>
						</c:if>
						
						<tr>
							<td class="labels">Active</td>
							<td class="value">
								<form:checkbox path="active"/>
							</td>
						</tr>
					</table>
			</fieldset>	
			<br>
			<fieldset class="frmField_set">
				<legend>Basic Information</legend>
				<table class="formTable">
					<tr>
						<td class="labels">* First Name</td>
						<td class="value">
							<form:input path="firstName" id="firstName"/>
						</td>
					</tr>

					<tr>
						<td></td>
						<td class="value">
							<form:errors path="firstName" cssClass="error"/>
						</td>
					</tr>

					<tr>
						<td class="labels">Middle Name</td>
						<td class="value">
							<form:input path="middleName" id="middleName"/>
						</td>
					</tr>

					<tr>
						<td></td>
						<td class="value">
							<form:errors path="middleName" cssClass="error"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Last Name</td>
						<td class="value">
							<form:input path="lastName" id="lastName"/>
						</td>
					</tr>
					
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="lastName" cssClass="error"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Birthdate</td>
						<td class="value">
							<form:input path="birthDate" id="birthDate" onblur="evalDate('birthDate')" style="width: 120px;" cssClass="dateClass2"/>
							<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('birthDate')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>

					<tr>
						<td></td>
						<td class="value">
							<form:errors path="birthDate" cssClass="error"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Contact No.</td>
						<td class="value">
							<form:input path="contactNumber" id="contactNumber"/>
						</td>
					</tr>

					<tr>
						<td></td>
						<td class="value">
							<form:errors path="contactNumber" cssClass="error"/>
						</td>
					</tr>

					<tr>
						<td class="labels">* Email Address</td>
						<td class="value">
							<form:input path="emailAddress" id="emailAddress"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="emailAddress" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">* Address</td>
						<td class="value">
							<form:textarea path="address" id="address"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<form:errors path="address" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>

			<table style="margin-top: 10px;" class="frmField_set">
				<tr>
					<td align="right">
						<input type="button" id="btnSaveUser" value="${user.id eq 0 ? 'Save' : 'Update'}" onclick="saveUser();"/>
						<input type="button" id="btnCancel" value="Cancel" onclick="cancelForm();"/>
					</td>
				</tr>
			</table>
			</div>
	</form:form>
</div>
</body>
</html>