<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!-- 

	Description: Customer Account table.
 -->
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
<table style="width: 95%; margin-left: 2%" class="dataTable">	
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="1%"><input type="checkbox" id="mainCheckbox"/> </th>
			<th width="5%">Date</th>
			<th width="5%">Due Date</th>
			<th width="5%">Paid Date</th>
			<th width="5%">Reference ID</th>
			<th width="20%">Description</th>
			<th width="12.4%">Principal</th>
			<th width="12.4%">Earned Interest</th>
			<th width="12.4%">Payment</th>
			<th width="12.4%">Total</th>
			<th width="12.4%">Running Balance</th>
		</tr>
	</thead>

	<tbody>
		<c:forEach var="ar" items="${customerAccountRecords}" varStatus="status">
			<c:if test="${ar.type == 1}">
				<c:set var="trClass" value="paidAccountRow"/>
			</c:if>
			<c:if test="${ar.type == 2}">
				<c:set var="trClass" value="paymentRow"/>
			</c:if>
			<c:if test="${ar.type == 3}">
				<c:set var="trClass" value=""/>
			</c:if>
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td >${status.index + 1} </td>
				<td><input type="checkbox" id="${ar.id}${ar.type}" 
					onclick="selectAcctRecord(${ar.id}, ${ar.type}, '${ar.referenceId}');" /></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${ar.date}" /> </td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${ar.dueDate}" /> </td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${ar.paidDate}" /> </td>
				<td>${ar.referenceId}</td>
				<td>${ar.description}</td>
				<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ar.principalAmount}" /> </td>
				<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ar.earnedInterest}" /> </td>
				<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ar.payment}" /> </td>
				<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ar.total}" /> </td>
				<c:set var="runningbalance" value="${runningbalance + (ar.principalAmount + ar.earnedInterest) - ar.payment }" />
				<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${runningbalance}" /> </td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
	</tfoot>
</table>
</body>
</html>