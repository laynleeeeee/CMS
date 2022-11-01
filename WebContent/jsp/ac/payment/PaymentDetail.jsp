<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
 <!-- 

	Description: JSP file that displays the list of account receivables to be paid by the customer. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<form:form method="POST" commandName="payment" id="payment">
		<form:hidden path="id"/>
		<form:hidden path="date" />
		<form:hidden path="referenceId"/>
		<form:hidden path="description"/>
		<form:hidden path="amount"/>
		
		<table>
			<tr>
				<td> Payment Date :</td>
				<td> <fmt:formatDate pattern="MM/dd/yyyy"  value="${payment.date}" /> </td>
			</tr>
			
			<tr>
				<td> Reference Number : </td>
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
	</form:form>
	<div id="paymentDetailControls" style="float: right;">
		<input type="button" value="Save" id="btnSavePayment" onclick="savePayment ();"/>
		<input type="button" value="Print" id="btnPrintPaymentDetail" onclick="printPaymentDetail ();"/>
		<input type="button" value="Cancel" id="btnCancelPayment" onclick="cancelPayment(); "  />
	</div>
</body>
</html>