<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
  <!-- 

	Description: JSP file for the data entry form of group of companies. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="../css/stylesheet.css" />
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('#errorName').hide();
		$('#errorDescription').hide();
		$('#errorActive').hide();
	});
</script>
</head>
<body>
<form:form method="POST" commandName="userGroup" id="userGroup">
	<div class="modFormLabel">User Group</div>
	<div class="modFormUnderline"> </div>
		<div style="width: 550px; " class="modForm">
		<table class="formTable">
			<form:hidden path="id"/>
			<tr>
				<td>Name: </td>
				<td>
					<form:input path="name"/>
				</td>
			</tr>
			<tr id="errorName">
				<td colspan="2">
					<font color="red">
						<form:errors path="name" cssClass="error"/>
					</font>
				</td>
			</tr>

			<tr>
				<td>Description: </td>
				<td>
					<form:input path="description"/>
				</td>
			</tr>
			<tr id="errorDescription"> 
				<td colspan="2">
					<font color="red">
						<form:errors path="description" cssClass="error"/>
					</font>
				</td>
			</tr>

			<tr> 
				<td>Active: </td>
				<td>
					<form:checkbox path="active"/>
				</td>
			</tr>
			<tr id="errorActive">
				<td colspan="2">
					<font color="red">
						<form:errors path="active" cssClass="error"/>
					</font>
				</td>
			</tr>
		</table>

		<div class="controls">
			<input type="button" id="saveusergroupform" name="saveusergroupform" value="SAVE" onclick="saveUserGroup ();"/>	
			<input type="button" id="cancelusergroupform" name="cancelusergroupform" value="CANCEL" onclick="resetPage ();"/>	
		</div>
	</div>
</form:form>
</body>
</html>