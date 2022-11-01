<!-- 

	Description: JSP that shows the customer summarised data. 
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<table style="text-align: left; width: 100%" border="1">
	<thead>
		<tr>
			<th>Date</th>
			<th>Reference Number</th>
			<th>Description</th>
			<th>Amount</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="transaction" items="${transactions.data}">
			<tr>
				<td>${transaction.date}</td>
				<td>${transcation.referenceId}</td>
				<td>${transaction.description}
				<c:set var="amount" value="${tramsaction.amount}" />
				<td><fmt:formatNumber type="number" maxFractionDigits="2" value="${amount}" /></td>
			</tr>
		</c:forEach>
	</tbody>
</table>		