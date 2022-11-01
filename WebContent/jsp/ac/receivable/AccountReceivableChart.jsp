<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<link href="../basic.css" type="text/css" rel="stylesheet" />
	<link href="../css/visualize.css" type="text/css" rel="stylesheet" />
	<link href="../css/visualize-light.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
	<script type="text/javascript" src="../js/jquery/visualize.jQuery.js"></script>
<script type="text/javascript" >
$(document).ready(function () {
	$("#accountReceivableChart").load('accountReceivableChart/annual');
});

$(function () {
	$("#chartType").change(function() {
		if ($("#chartType").prop("selectedIndex") == 0) 
			$("#accountReceivableChart").load('accountReceivableChart/annual');
		
		if ($("#chartType").prop("selectedIndex") == 1) 
			$("#accountReceivableChart").load('accountReceivableChart/monthly');
	});
});
</script>
</head>
<body>
<table>
	<tr>
		<td> Type: </td>
		<td> 
			<select id="chartType">
				<c:forEach items="${chartTypes}" var="type">
					<option value="${type}" ${type == 'Annual' ? 'selected' : ''}>${type }</option>
				</c:forEach>
			</select>
		</td>
	</tr>
</table>
	<div id="accountReceivableChart" >	
	</div>
</body>
</html>