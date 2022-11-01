<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
   <%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
table {
	border-collapse:collapse;
	width: 100%;
}

th, td {
	border: 1px solid black;
	font-size: 12px;
}

th{
	background-color: #eee;
}

.currency, .total {
	text-align: right;
}

#main {
  	margin-left: auto;
  	margin-right: auto;
}

@media print {
	#controls {
		display: none;
	}
	
	th, td {
		border: 1px solid black;
		font-size: 12px;
	}

	th{
		background-color: #eee;
	}
}
</style>
</head>
<body>

<div id="main"> 
<div>
 List of Bad Debts for ${company}.
</div>
<div>
<table>	
	<thead>
		<tr>
			<th width="2%">#</th>
			<th>Name </th>
			<th>Address </th>
			<th>Contact Number </th>
			<th>Current Balance </th>
		</tr>
	</thead>
	
	<tbody>
		<c:forEach var="badDebt" items="${badDebts.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>${status.index + 1}</td>
						<td>${badDebt.name}</td>
						<td>${badDebt.address}</td>
						<td>${badDebt.contactNumber}</td>
						<td class="currency">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${badDebt.currentAccountBalance}" /> 
						</td>
					</tr>
		</c:forEach>
	</tbody>
</table>
</div>
</div>
<div id="controls" style="text-align: right; margin-top: 5px;">
	<input type="button" value="Print" onclick="window.print(); window.close();"  />
	<input type="button" value="Close" onclick="window.close();" />
</div>
</body>
</html>