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
	<script type="text/javascript">
		$(function(){
			$('table').visualize();
		});
	</script>
</head>
<body>
<c:set var="companyName" value="${companyName}" />
<c:set var="selectedYear" value="${selectedYear}" />
	<table>
			<caption>Total Account Receivable of ${companyName} for ${selectedYear}</caption>
			<thead>
				<tr>
					<td></td>
					<th scope="col">Jan</th>
					<th scope="col">Feb</th>
					<th scope="col">Mar</th>
					<th scope="col">Apr</th>
					<th scope="col">May</th>
					<th scope="col">Jun</th>
					<th scope="col">Jul</th>
					<th scope="col">Aug</th>
					<th scope="col">Sep</th>
					<th scope="col">Oct</th>
					<th scope="col">Nov</th>
					<th scope="col">Dec</th>
				</tr>
			</thead>
			<tbody>
				<tr> 
					<th scope="row">Company 1</th>
					<c:forEach var="monthlyReceivable" items="${monthlyReceivables}" varStatus="status">
						<td>
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${monthlyReceivable}" /> 
						</td>
					</c:forEach>
				</tr>
			</tbody>
	</table>
</body>
</html>