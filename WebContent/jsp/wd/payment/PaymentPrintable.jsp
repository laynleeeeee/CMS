<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!-- 

	Description: Printable format for concessioanire payments
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<div id="concessionaireProfile">
	<%@ include file="../concessionaire/account/ConcessionaireAcctProfile.jsp" %>
</div>
<table id="paidTable" class="printStyle">
	<thead>
		<tr>
			<th  width="10%">Date</th>
			<th width="10%">Bill Number</th>
			<th width="60%">Description</th>
			<th width="19%">Amount</th>
			<th width="19%">Payment</th>
 		</tr>
	</thead>
	<c:set var="total" value="0"></c:set>
	<tbody>
		<c:set var="concessionaireAcctPayments" value="${concessionairePayment.paidAccounts}"></c:set>
		<c:forEach var="accountPayment" items="${concessionaireAcctPayments}">
			<tr>
				<td>
					<fmt:formatDate pattern="MM/dd/yyyy" value="${accountPayment.account.date}"/>
				</td>
				<td>
					${accountPayment.account.wbNumber}
				</td>
				<td>
					${accountPayment.account.cubicMeter} cubic meters
				</td>
				<td class="numberClass">
					<c:set var="total" value="${total + accountPayment.paidMeteredSale}"></c:set>
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${accountPayment.paidMeteredSale}"/>
				</td>
				<td></td>
			</tr>
			<c:if test="${accountPayment.paidRental > 0}">
				<tr>
					<td colspan="2"></td>
					<td>Meter Rental</td>
					<td class="numberClass">
						<c:set var="total" value="${total + accountPayment.paidRental}"></c:set>
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${accountPayment.paidRental}" />
					</td>
				</tr>
			</c:if>
			<c:if test="${accountPayment.paidPenalty > 0}">
				<tr>
					<td colspan="2"></td>
					<td>Penalty (due date : <fmt:formatDate pattern="MM/dd/yyyy" value="${accountPayment.account.dueDate}"/>)</td>
					<td class="numberClass">
						<c:set var="total" value="${total + accountPayment.paidPenalty}"></c:set>
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${accountPayment.paidPenalty}" />
					</td>
				</tr>
			</c:if>
		</c:forEach>
		<tr>
			<td> <fmt:formatDate pattern="MM/dd/yyyy" value="${concessionairePayment.date}"/></td>
			<td></td>
			<td>Payment</td>
			<td></td>
			<td class="numberClass">
				<c:set var="total" value="${total - concessionairePayment.payment}"></c:set>
				<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${concessionairePayment.payment}" />
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr class="summaryTr">
			<td>Balance</td>
			<td colspan="3"></td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${total}" /></td>
		</tr>
	</tfoot>
</table>
</body>
</html>