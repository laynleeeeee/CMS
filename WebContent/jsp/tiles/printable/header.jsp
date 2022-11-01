<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
      <%@ include file="../../include.jsp" %>
<!-- 

	Description: Report header template 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="${pageContext.request.contextPath}/css/stylesheet.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/css/tables.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/css/print.css" rel="stylesheet" type="text/css" />
<title>Header</title>
</head>
<body>
<table class="printReportHeader">
	<thead>
		<tr align="center">
			<th width="100%">${company.name}</th>
		</tr>
		<tr align="center">
			<th>${company.address}</th>
		</tr>
		<tr align="center">
			<th>${company.contactNumber}</th>
		</tr>
		<c:if test="${title != ''}">
			<tr align="center">
				<th>${title}</th>
			</tr>
		</c:if>
		<c:if test="${date != ''}">
			<tr align="center">
				<th>${date}</th>
			</tr>
		</c:if>
	</thead>
</table>
</body>
</html>