<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
 <!-- 

	Description: JSP file for the listing of different customers. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<div class="modFormLabel">Account Receivable Summary</div>
	<div class="modFormUnderline"> </div>
<br>
<table>
	<tr>
		<td>Date From</td><td><input type="text"></td>
		<td>Date To</td><td><input type="Text"></td>
		<td><input type="button" value="show Report"></td>
	</tr>
</table>
<br>
<div class="modFormUnderline"> </div>

<table>
	<c:forEach var="rs" items="${reportSummary}">
		<tr> 
			<td>${rs.title}</td> <td align="right">${rs.value}</td>
		</tr>
	</c:forEach>
</table>
</body>
</html>