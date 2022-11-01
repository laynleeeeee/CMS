<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<!--     Description: User Group Form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ybl/inputUtil.js"></script>
<title>User Group Form</title>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="userGroup" id="userGroupFrm">
		<div class="modFormLabel">User Group</div>
		<br>
		<div class="modForm">
		<fieldset class="frmField_set">
		<legend>Group Information </legend>
			<table class="formTable">
				<form:hidden path="id"/>
				<form:hidden path="createdBy"/>
				<tr>
					<td class="labels">* Group Name</td>
					<td class="value"><form:input path="name" size="7" cssClass="input" /></td>
				</tr>
				<tr>
					<td class="value"> </td>
					<td>
						<form:errors path="name" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td class="labels">* Group Description</td>
					<td class="value"><form:input path="description" cssClass="input" maxlength="250"  /></td>
				</tr>
				<tr>
					<td class="value"> </td>
					<td>
						<form:errors path="description" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Active</td>
					<td class="value">
						<form:checkbox path="active"/>
					</td>
				</tr>
			</table>
			<br>
			<table class="buttonClss">
				<tr>
					<td align="right" >
						<input type="button" id="btnSaveUserGroup" value="${userGroup.id eq 0 ? 'Save' : 'Update'}" onclick="saveUserGroup(this);"/>
						<input type="button" id="btnCancelSave" value="Cancel"/>
					</td>
				</tr>
			</table>
		</fieldset>
		</div>
	</form:form>
</div>
</body>
</html>