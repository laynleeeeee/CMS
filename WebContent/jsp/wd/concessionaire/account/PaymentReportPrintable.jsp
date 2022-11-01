<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp"%>
<!-- 

	Description: Printable payment report for water district.
 -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<body>
<body>
<table class="printStyle">
	<thead>
		<tr>
			<th width ="10%">
				Date/Payment Date
			</th>
			<th width ="15%">
				Water Bill Number
			</th>
			<th width ="50%">
				Item/Description
			</th>
			<th width ="15%">
				Amount
			</th>
			<th>Payment</th>
		</tr>
	</thead>
		<tbody>
		<c:set var="totalAmount" value="0"></c:set>
		<c:set var="totalPayment" value="0"></c:set>
		<c:forEach var="payment" items="${paymentReports.data}" varStatus="status">
			<c:forEach var="paidAccount" items="${payment.paidAccounts}">
				<c:set var="account" value="${paidAccount.account}"></c:set>
				<c:if test="${paidAccount.paidMeteredSale > 0}">
					<tr>
						<td>
							<fmt:formatDate pattern="MM/dd/yyyy" value="${account.date}"/>
						</td>
						<td>
							${account.wbNumber}
						</td>
						<td>
							${account.cubicMeter} cubic meters
						</td>
						<td class="numberClass">
							<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${paidAccount.paidMeteredSale}" />
							<c:set var="totalAmount" value="${totalAmount+ paidAccount.paidMeteredSale}"></c:set>
						</td>
						<td></td>
					</tr>
				</c:if>
				<c:if test="${paidAccount.paidRental > 0}">
					<tr>
						<td colspan="2"></td>
						<td>
							Meter Rental
						</td>
						<td class="numberClass">
							<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${paidAccount.paidRental}" />
							<c:set var="totalAmount" value="${totalAmount+ paidAccount.paidRental}"></c:set>
						</td>
						<td></td>
					</tr>
				</c:if>
				<c:if test="${paidAccount.paidPenalty > 0}">
					<tr>
						<td colspan="2"></td>
						<td>
							Penalty (Due date: <fmt:formatDate pattern="MM/dd/yyyy" value="${account.dueDate}"/>)
						</td>
						<td class="numberClass">
							<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${paidAccount.paidPenalty}" />
							<c:set var="totalAmount" value="${totalAmount + paidAccount.paidPenalty}"></c:set>
						</td>
						<td></td>
					</tr>
				</c:if>
			</c:forEach>
			<!-- Payment data -->
			<tr>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${payment.date}"/></td>
				<td></td>
				<td>Payment</td>
				<td></td>
				<td class="numberClass" ><fmt:formatNumber type="number" minFractionDigits="2" 
 					maxFractionDigits="2" value="${payment.payment}" />
 					<c:set var="totalPayment" value="${totalPayment + payment.payment }"></c:set>
				</td>
 					
			</tr>
		</c:forEach>
		<tr class="summaryTr">
			<td colspan="3">Total</td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" 
					maxFractionDigits="2" value="${totalAmount}" /></td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" 
					maxFractionDigits="2" value="${totalPayment}" /></td>
		</tr>
	</tbody>
	</table>
</body>
</html>