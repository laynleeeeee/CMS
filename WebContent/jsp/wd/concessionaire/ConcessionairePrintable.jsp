<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <%@ include file="../../include.jsp" %>
<!-- 

	Description: Printing of concessionaires. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
<table class="printStyle">
	<thead>
		<tr>
			<th width="3%">#</th>
			<th width="20%">Name</th>
			<th width="22%">Address</th>
			<th width="10%">Contact Number</th>
			<th width="8%">Classification</th>
			<th width="8%">Metered Sale</th>
			<th width="8%">Penalty</th>
			<th width="8%">Meter Rental</th>
			<th width="13%">Current Balance</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="concessionaire" items="${concessionaires.data}" varStatus="status">
			<tr>
				<td>${status.index + 1} </td>
				<td>${concessionaire.firstName} ${concessionaire.middleName} ${concessionaire.lastName}</td>
				<td>${concessionaire.address}</td>
				<td>${concessionaire.contactNumber}</td>
				<td>${concessionaire.concessionaireClassification.name}</td>
				<td align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${concessionaire.meteredSale}" />
				</td>
				<td align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${concessionaire.penalty}" />
				</td>
				<td align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${concessionaire.meterRental}" />
				</td>
				<td align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${concessionaire.meteredSale + concessionaire.penalty + concessionaire.meterRental}" />
				</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot></tfoot>
</table>
</body>
</html>
