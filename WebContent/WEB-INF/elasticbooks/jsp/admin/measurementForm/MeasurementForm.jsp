<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description: Measurement form page. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Measurement Form</title>
<style type="text/css">
#btn {
    position:relative;
    float:right;
}
</style>
</head>
<body>
<form:form method="POST" commandName = "unitMeasurement">
	<div class="formDivBigForms">
		<div class="modFormLabel">Units of Measurement</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
			<legend>* Basic Information</legend>
			<table class="formTable">
				<form:hidden path="id"/>
				<tr>
					<td class="labels">Name:</td>
					<td class="value">
						<form:input path="name" size="20" class="inputSmall"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2">
						<form:errors path="name" cssClass="error"/>
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
		<table class="frmField_set">
			<tr>
				<td align="right">
					<input id="btnSaveMeasurement" align="right" type="button" value="${unitMeasurement.id eq 0 ? 'Save' : 'Update'}" onclick="saveMeasurement();"/>
					<input id="btnCancelMeasurement" align="right" type="button" value="Cancel" onclick="cancelForm();"/>
				</td>
			</tr>
		</table>
		</div>
	</div>
</form:form>
</body>
</html>