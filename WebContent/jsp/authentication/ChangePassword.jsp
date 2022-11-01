<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../include.jsp" %>
 <!-- 

	Description: Change password page. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<script type="text/javascript"> 
$(document).ready(function () {
	$("#changePasswordForm").load('login/changePasswordForm');
});
</script>
<title>Cloud business solution</title>
</head>
<body >
<input type="hidden" id="hiddenUserName" value="${userName}" />
<div id="changePasswordForm"></div>
</body>
</html>