<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="${pageContext.request.contextPath}/css/tables.css" rel="stylesheet" type="text/css" />
<style type="text/css">
#main {
  	margin-left: auto;
  	margin-right: auto;
}

@media print {
	#controls {
		display: none;
	}
}
</style>
</head>
<body>
<div id="main">
<div>

		<table width="100%">
			<tr>
				<td style="float: left;"> 
					<table>
						<tr>
							<td>Customer : </td>
							<td>${customerName} </td>
						</tr>
					</table>
				</td>
				
				<td style="float: right;"> 
					<table>
						<tr>
							<td>Cashier : </td>
							<td>${cashierName} </td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		
		<hr> 
		
		<table>
			<tr>
				<td> Payment Date :</td>
				<td> <fmt:formatDate pattern="MM/dd/yyyy"  value="${payment.date}" /> </td>
			</tr>
			
			<tr>
				<td> Reference ID : </td>
				<td> <c:out value="${payment.referenceId}"></c:out> </td>
			</tr>
			
				<tr>
				<td> Description :</td>
				<td> <c:out value="${payment.description}"></c:out>  </td>
			</tr>
			
				<tr>
				<td> Amount : </td>
				<td>  
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" 
							value="${payment.amount}" /> 
				</td>
			</tr>
		</table>
		
		<hr> 
		 
		<table class="dataTable" border="0">
			<thead>
				<tr>
					<th width="2%">#</th>
					<th width="8%">Date</th>
					<th width="8%">Due Date</th>
					<th width="12%">Reference ID</th>
					<th width="40%">Description</th>
					<th width="10%">Paid Principal </th>
					<th width="10%">Earned Interest</th>
					<th width="10%">Total</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="paymentDetail" items="${paymentDetails}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>${status.index + 1} </td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${paymentDetail.date}" /> </td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${paymentDetail.dueDate}" /> </td>
						<td>${paymentDetail.referenceId}</td>
						<td>${paymentDetail.description}</td>
						<td style="text-align: right;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" 
							value="${paymentDetail.paidPrincipal}" /> 
						</td>
						<td style="text-align: right;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" 
							value="${paymentDetail.earnedInterest}" /> 
						</td>
						<td style="text-align: right;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" 
							value="${paymentDetail.paidPrincipal + paymentDetail.earnedInterest}" /> 
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="8"> 
						<span style="float: left; font-weight: bold;">Total</span>
						<span style="float: right; font-weight: bold;">							
							<c:forEach var="paymentDetail" items="${paymentDetails}" varStatus="status">
								<c:set var="total" value="${total + paymentDetail.paidPrincipal + paymentDetail.earnedInterest}" />
							</c:forEach>
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${total}" />
						</span> 
					</td>
				</tr>
				
				<tr>
					<td colspan="8"> 
						<span style="float: left; font-weight: bold;">Payment</span>
						<span style="float: right; font-weight: bold;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentAmount}" />
						</span>
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