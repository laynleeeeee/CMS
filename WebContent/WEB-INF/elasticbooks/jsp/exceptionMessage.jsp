<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="include.jsp" %>
  <!-- 

	Description: The page for displaying exception message
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Exception Message</title>
<style type="text/css">
p {
	padding-left: 2%;
}
</style>
</head>
<body>
	<p>${exception.exceptionMsg}</p>
</body>
</html>