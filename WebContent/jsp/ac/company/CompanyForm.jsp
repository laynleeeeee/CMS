<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
  <!-- 

	Description: JSP file for the data entry form of companies. 
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
		$('#errorAddress').hide();
		$('#errorContactNumber').hide();
		$('#errorEmailAddress').hide();
		$('#errorActive').hide();
	});
</script>
</head>
<body>
<form:form method="POST" commandName="company" id="company">
	<div class="modFormLabel">Company</div>
	<div class="modFormUnderline"> </div>
	<div style="width: 550px; " class="modForm">
		<table class="formTable" >
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="updatedBy"/>
			<form:hidden path="updatedDate"/>
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
				<td>Address: </td>
				<td>
					<form:input path="address"/>
				</td>
			</tr>
			<tr id="errorAddress">
				<td colspan="2">
					<font color="red">
						<form:errors path="address" cssClass="error"/>
					</font>
				</td>
			</tr>
			<tr>
				<td>Contact Number: </td>
				<td>
					<form:input path="contactNumber"/>
				</td>
			</tr>
			<tr id="errorContactNumber">
				<td colspan="2">
					<font color="red">
						<form:errors path="contactNumber" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Email Address: </td>
				<td>
					<form:input path="emailAddress"/>
				</td>
			</tr>
			<tr id="errorEmailAddress">
				<td colspan="2">
					<font color="red">
						<form:errors path="emailAddress" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr >
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
			<input type="button" id="saveCompanyForm" name="saveCompanyForm" value="SAVE" onclick="saveCompany ();"/>	
			<input type="button" id="cancelCompanyForm" name="cancelCompanyForm" value="CANCEL" onclick="resetPage ();"/>	
		</div>
	</div>
</form:form>
</body>
</html>