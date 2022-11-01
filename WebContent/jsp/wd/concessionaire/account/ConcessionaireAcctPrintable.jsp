<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp" %>
<!--

	Description: Concessionaire Account Printable Page
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<table>
	<tr>
		<td class="title">Name:</td>
		<td>${concessionaire.firstName} ${concessionaire.middleName} ${concessionaire.lastName}</td>
	</tr>
	<tr>
		<td class="title">Address:</td>
		<td>${concessionaire.address}</td>
	</tr>
	<tr>
		<td class="title">Contact Number:</td>
		<td>${concessionaire.contactNumber}</td>
	</tr>
	<tr>
		<td class="title">Classification:</td>
		<td>${concessionaire.concessionaireClassification.name}</td>
	</tr>
	<tr>
		<hr>
	</tr>
</table><br>
<table class="printStyle">
	<thead>
		<tr>
			<th width="3%">#</th>
			<th>Date</th>
			<th>Due Date</th>
			<th>Paid Date</th>
			<th>Water Bill Number</th>
			<th>Cubic Meters</th>
			<th>Metered Sale</th>
			<th>Penalty</th>
			<th>Meter Rental</th>
			<th>Total</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="ca" items="${concessionaireAccts.data}" varStatus="status">
			<tr>
				<td>${status.index + 1}</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${ca.date}"/></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${ca.dueDate}"/></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${ca.concessionairePayment.date}"/></td>
				<td>${ca.wbNumber}</td>
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ca.cubicMeter}" /></td>
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ca.unpaidAmount}" /></td>
					<c:set var="totalSale" value="${totalSale + ca.unpaidAmount}" />
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ca.penalty.unpaidAmount}" /></td>
					<c:set var="totalPenalty" value="${totalPenalty + ca.penalty.unpaidAmount}" />
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ca.rental.unpaidAmount}"/></td>
					<c:set var="totalRental" value="${totalRental + ca.rental.unpaidAmount}" />
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${ca.unpaidAmount + ca.penalty.unpaidAmount + ca.rental.unpaidAmount}"/></td>
					<c:set var="totalAmt" value="${totalAmt + ca.unpaidAmount + ca.penalty.unpaidAmount + ca.rental.unpaidAmount}" />
				</tr>
			</c:forEach>
			<tr class="summaryTr">
				<td colspan="6">Total</td>
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalSale}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalPenalty}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalRental}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalAmt}" />
			</tr>
	</tbody>
</table>