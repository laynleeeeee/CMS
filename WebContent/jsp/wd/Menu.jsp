<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!-- 

	Description: Main menu for water district
 -->
<title>Customer Page </title>
<style type="text/css">
a, a:hover, a:visited{ 
	text-decoration: none; 
	color: #000; 
} 
</style>
<script type="text/javascript">
$(function(){
	$("#aConcessionaires").click(function() {
		window.location.replace(contextPath + "/concessionaireList");
	});
	
	$("#aUnpaidReceivable").click(function() {
		window.location.replace(contextPath + "/accountsReceivable");
	});

	$("#aCustomerPayment").click(function() {
		window.location.replace(contextPath + "/customerPayment");
	});

	$("#aReceivableReport").click(function() {
		window.location.replace(contextPath + "/accountsReceivable/report");
	});

	$("#aConcessionaireClassifications").click(function(){
		window.location.replace(contextPath + "/concessionaireClassification");
	});
	$("#aReceivableReportWds").click(function(){
		window.location.replace(contextPath + "/receivableReportWd");
	});

});

function goTo (page) {
	window.location.replace(contextPath + "/" + page);
}

</script>
</head>
<body>
<table width="100%" border=0 style="margin-left: 5%;">
	<tr>
		<td>
			<table id="mainMenu">
			<tr>
				<td><a href="#" id="aConcessionaires">Concessionaire</a></td>
			</tr>
			<tr>
				<td><a href ="#" id ="aReceivableReportWds">Receivable Report</a></td>
			</tr>
			<tr>
				<td><a href ="#" id ="aConcessionaireClassifications">Classification</a></td>
			</tr>
			<tr>
				<td><a onclick="goTo('concessionare/payment')" href="#">Payments</a></td>
			</tr>
			</table>
		<td>
	</tr>
</table>
</body>
</html>