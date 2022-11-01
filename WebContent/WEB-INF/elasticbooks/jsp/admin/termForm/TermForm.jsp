<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Term form page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Term Form</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	numericInputOnly();
});

function numericInputOnly() {
	$("#days").bind("keyup keydown", function(e) {
		// 46 = DELETE button. If Delete button is not pressed
		if(e.keyCode != 46 && e.keyCode != 13){ 
			inputOnlyNumeric("days");
		}
	});
}
</script>
</head>
<body>
	<div class="formDiv">
	<form:form method="POST" commandName="term">
		<div class="modFormLabel">Term</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>* Basic Information</legend>
				<table class="formTable">
					<form:hidden path="id"/>
					<form:hidden path="createdBy"/>
					<tr>
						<td class="labels">Term Name:</td>
						<td class="value">
							<form:input path="name" class="inputSmall"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="name" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Days:</td>
						<td class="value"><form:input path="days" class="inputSmall" maxlength="10"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<form:errors path="days" cssClass="error"/>
						</td>
					</tr>
					<tr>
						<td class="labels">Active: </td>
						<td class="value">
							<form:checkbox path="active"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
		<table class="frmField_set">
			<tr>
				<td align="right">
					<input id="btnSaveTerm" align="right" type="button" value="${term.id eq 0 ? 'Save' : 'Update'}" onclick="saveTerm();"/>
					<input id="btnCancelTerm" type="button" value="Cancel" onclick="cancelForm();"/>
				</td>
			</tr>
		</table>
		</div>
	</form:form>
	</div>
</body>
</html>