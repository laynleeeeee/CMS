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
	<table>
		<caption>Total Account Receivable of Company 1 for Month</caption>
			<thead>
				<tr>
					<td></td>
					<c:forEach var="day" begin="1" end="${numberOfDays}">
						<th scope="col">${day}</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<tr> 
					<th scope="row">Company 1</th>
					<c:forEach var="dailyReceivable" items="${dailyReceivables}" varStatus="status">
						<td>
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${dailyReceivable}" /> 
						</td>
					</c:forEach>
				</tr>
			</tbody>
	</table>

</body>
</html>