<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
.currency, .total {
	text-align: right;
}

.total{
	font-weight: bold;
}

#customerInfo {
	display: none; 
}
</style>
</head>
<body>
<select id="customerInfo">
	<c:forEach var="customerAccount" items="${customerAccountRecords.data}" varStatus="status">
		<option value="${customerAccount.id}">${customerAccount.referenceId};${customerAccount.type}</option>
	</c:forEach>
</select>
<table style="width: 95%; margin-left: 2%" class="dataTable">	
	<thead>
		<tr>			
			<th width="2%">#</th>
			<th width="8%">Date </th>
			<th width="8%">Due Date </th>
			<th width="12%">Reference Id </th>
			<th>Description </th>
			<th width="12%">Amount </th>
			<th width="12%">With Interest </th>
		</tr>
	</thead>

	<tbody>
		<c:forEach var="customerAccount" items="${customerAccountRecords.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>
							${status.index + 1}
							<input type="hidden" id="type${status.index +1}" value="${customerAccount.paid}"/>
						</td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${customerAccount.date}" /></td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${customerAccount.dueDate}" /></td>
						<td>${customerAccount.referenceId}</td>
						<td>${customerAccount.description}</td>
						<td class="currency">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerAccount.receivable}" /> 
						</td>
						<td class="currency">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerAccount.withInterest}" /> 
						</td>
					</tr>
		</c:forEach>
	</tbody>
</table>
</body>
</html>