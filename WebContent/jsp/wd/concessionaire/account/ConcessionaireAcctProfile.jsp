<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp" %>
<!-- 

	Description: Displays the profile of the Concessionaire
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
<!-- The concessionaire profile -->
	<table class="tableInfo">
		<tr>
			<td class="label" width="15%">Name:</td>
			<td>${concessionaire.firstName} ${concessionaire.middleName} ${concessionaire.lastName}</td>
		</tr>
		<tr>
			<td class="label">Address:</td>
			<td>${concessionaire.address}</td>
		</tr>
		<tr>
			<td class="label">Contact Number:</td>
			<td>${concessionaire.contactNumber}</td>
		</tr>
		<tr>
			<td class="label">Classification:</td>
			<td>${concessionaire.concessionaireClassification.name}</td>
		</tr>
		<tr>
			<td colspan="2"><hr></td>
		</tr>
	</table>
</body>
</html>