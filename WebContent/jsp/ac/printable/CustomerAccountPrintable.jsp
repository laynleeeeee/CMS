<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
#customerProfile {
	width: 80%; 
}

#customerTable {
	margin-top: 5px;
	width: 100%; 
	border-collapse:collapse;
}

#customerTable th, #customerTable td {
	border: 1px solid black;
	font-size: 10px;
}
	
#customerTable th{
	background-color: #eee;
}

#customerTable tfoot{
	border: none;
}

.currency, .total {
	text-align: right;
}

.total{
	font-weight: bold;
}

#main {
  	margin-left: auto;
  	margin-right: auto;
}

@media print {
	#controls {
		display: none;
	}
	
	#customerProfile {
		width: 80%; 
	}

	#customerTable {
		margin-top: 5px;
		width: 100%; 
		border-collapse:collapse;
	}

	#customerTable th, #customerTable td {
		border: 1px solid black;
		font-size: 10px;
	}
	
	#customerTable th{
		background-color: #eee;
	}
	
	#customerTable tfoot{
		border: none;
	}
}
</style>
</head>
<body>
<div id="main">
<div>
<table  id="customerProfile" style=" ">
	<tr>
	  	<td>Name: </td>
		<td>${customerName }</td>
 	</tr>

	<tr>
		<td>Address: </td>
		<td>${customerAddress }</td>
	</tr>

	<tr>
		<td>Contact Number: </td>
		<td>${customerContactNumber }</td>
	</tr>

	<tr>
		<td>Email Address: </td>
		<td>${customerEmailAddress}</td>
	</tr>
</table>

</div>
<div>
<table  id="customerTable">	
	<thead>
		<tr>
			<th width="2%">#</th>
			<th>Date </th>
			<th>Reference Id </th>
			<th>Description </th>
			<th>Receivable </th>
			<th>With Interest </th>
			<th>Payment</th>
		</tr>
	</thead>

	<tbody>
		<c:forEach var="customerAccount" items="${customerAccountRecords.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>
							${status.index + 1}
							<input type="hidden" id="type${status.index +1}" value="${customerAccount.type}"/> 
						</td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${customerAccount.date}" /></td>
						<td>${customerAccount.referenceId}</td>
						<td>${customerAccount.description}</td>
						<td class="currency">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerAccount.receivable}" /> 
						</td>
						<td class="currency">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerAccount.withInterest}" /> 
						</td>
						<td class="currency">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerAccount.payment}" /> 
						</td>
					</tr>
		</c:forEach>
	</tbody>
	
	<tfoot>
		<tr>
			<c:forEach var="customerAccount" items="${customerAccountRecords.data}" begin="0" end="${customerAccountRecords.pageSetting.maxResult}" varStatus="status">
				<c:set var="totalReceivable" value="${totalReceivable + customerAccount.receivable}" />
			</c:forEach>
			<c:forEach var="customerAccount" items="${customerAccountRecords.data}" begin="0" end="${customerAccountRecords.pageSetting.maxResult}" varStatus="status">
				<c:set var="totalWithInterest" value="${totalWithInterest + customerAccount.withInterest}" />
			</c:forEach>
			<c:forEach var="customerAccount" items="${customerAccountRecords.data}" begin="0" end="${customerAccountRecords.pageSetting.maxResult}" varStatus="status">
				<c:set var="totalPayment" value="${totalPayment + customerAccount.payment}" />
			</c:forEach>
			<td colspan="4"></td>
			<td class="total">
				<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalReceivable}" /> 
			</td>
			<td class="total">
				<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalWithInterest}" /> 
			</td>
			<td class="total">
				<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalPayment}" /> 
			</td>
		</tr>

		<tr>
			<td colspan="3" style="font-weight: bold;">Current Balance </td>
			<td colspan="5" class="total">
				<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerCurrentAccountBalance}" /> 
			</td>
		</tr>
	</tfoot>
</table>
</div>
</div>
<div id="controls" style="text-align: right; margin-top: 5px;">
	<input type="button" value="Print" onclick="window.print(); window.close();"  />
	<input type="button" value="Close" onclick="window.close();" />
</div>
</body>
</html>