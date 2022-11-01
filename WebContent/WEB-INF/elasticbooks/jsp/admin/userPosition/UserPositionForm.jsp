<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp" %>
<!--  

	Description: User Position form.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
$(document).ready(function(){
	if("${userPosition.id}" > 0){
		$("#btnSavePosition").val("Update");
	}
});
</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="userPosition" id="userPosition">
		<div class="modFormLabel">User Position</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set" style="margin-left: auto; margin-right: auto;">
					<legend>* Basic Information</legend>
					<table>
					<form:hidden path="id"/>
					<form:hidden path="createdBy"/>
					<form:hidden path="updatedBy"/>
						<tr>
							<td class="labels">Name:</td>
							<td class="value">
								<form:input path="name" id="positionName"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="name" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Active:</td>
							<td class="value">
								<form:checkbox path="active"/>
							</td>
						</tr>
					</table>
				</fieldset>
				
				<table style="margin-top: 10px;" class="frmField_set">
				<tr>
					<td align="right">
						<input type="button" id="btnSavePosition" value="${userPosition.id eq 0 ? 'Save' : 'Update'}" onclick="savePosition();"/>
						<input type="button" id="btnCancel" value="Cancel" onclick="cancelForm();"/>
					</td>
				</tr>
			</table>
			</div>
	</form:form>
</div>
</body>
</html>