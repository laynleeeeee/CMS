<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Customer Page </title>
<style type="text/css">
a, a:hover, a:visited{ 
	text-decoration: none; 
	color: #000; 
} 
</style>
<script type="text/javascript">
$(function(){
	$("#linkCustomerListing").click(function() {
		window.location.replace(contextPath + '/customerListing');
	}); 
	
	$("#linkBadDebt").click(function() {
		window.location.replace(contextPath + '/badDebt');
	});
	
	$("#linkCustomerOverdue").click(function() {
		window.location.replace(contextPath + '/overdue');
	});
	
	$("#linkAccountReceivableChart").click(function() {
		window.location.replace(contextPath + '/accountReceivableChart');
	});
	
	$("#mainMenu td").mouseover(function() {
		$("selector").css("selector", "pointer");
  	});
		
	$("#mainMenu td").click(function() {
		$("#mainMenu td").css('background-color', '#FAFAFA');
		$(this).css('background-color', '#EDEDED');
		
		if ($(this).text().trim() == "Customer Listing") {
			window.location.replace(contextPath + '/customerListing');
		} 
		
		if ($(this).text() == "Customer Listing") window.location.replace(contextPath + '/customerListing');
		if ($(this).text() == "Bad Debt") window.location.replace(contextPath + '/badDebt');
		if ($(this).text() == "Overdue Customer") window.location.replace(contextPath + '/overdue');
		if ($(this).text() == "Account Receivable Chart") window.location.replace(contextPath + '/accountReceivableChart');
		
  	});
});
</script>
</head>
<body>
<table width="100%" border=0 style="margin-left: 5%;">
	<tr>
		<td>
			<table id="mainMenu">
				<tr><td><a href="#" id="linkCustomerListing">Customer Listing</a> </td></tr>
				<tr><td><a href="#" id="linkBadDebt">Bad Debt</a> </td></tr>
				<tr><td><a href="#" id="linkCustomerOverdue">Overdue Customer</a> </td></tr>
				<tr><td><a href="#" id="linkAccountReceivableChart">Account Receivable Chart</a> </td></tr>
			</table>
		<td>
	</tr>
</table>
</body>
</html>