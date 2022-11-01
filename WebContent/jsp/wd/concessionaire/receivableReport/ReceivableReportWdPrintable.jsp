<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp"%>
<!-- 

	Description: Printable receivable report for water district.
 -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<body>
<table class="printStyle">
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="19%">Name</th>
			<th width="14%">Address</th>
			<th width="11%">Contact Number</th>
			<th width="10%">Date</th>
			<th width="10%">Due Date</th>
			<th width="9%">Metered Sale</th>
			<th width="8%">Penalty</th>
			<th width="8%">Meter Rental</th>
			<th width="9%">Total Amount</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="rr" items="${receivableReportWds.data}" varStatus="status">
			<tr>
				<td>${status.index + 1}</td>
				<td>${rr.concessionaire.firstName} ${rr.concessionaire.lastName}</td>
				<td>${rr.concessionaire.address}</td>
				<td>${rr.concessionaire.contactNumber}</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${rr.date}"/></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${rr.dueDate}"/></td>
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
					value="${rr.unpaidAmount}" /></td>
				<c:set var="totalMeteredSale" value="${totalMeteredSale + rr.unpaidAmount}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
					value="${rr.penalty.unpaidAmount}"/></td>
				<c:set var="totalPenalty" value="${totalPenalty + rr.penalty.unpaidAmount}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
					value="${rr.rental.unpaidAmount}"/></td>
				<c:set var="totalMeterRental" value="${totalMeterRental + rr.rental.unpaidAmount}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
					value="${rr.unpaidAmount + rr.penalty.unpaidAmount + rr.rental.unpaidAmount}"/></td>
				<c:set var="grandTotal" value="${grandTotal + rr.unpaidAmount + rr.penalty.unpaidAmount + rr.rental.unpaidAmount}" />
			</tr>
		</c:forEach>
		<tr class="summaryTr">
			<td colspan="6">Total</td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalMeteredSale}" /></td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalPenalty}" /></td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalMeterRental}" /></td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${grandTotal}" /></td>
		</tr>
	</tbody>
</table>
</body>
</head>
</html>