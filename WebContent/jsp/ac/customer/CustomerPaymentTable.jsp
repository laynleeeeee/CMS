<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
		<table style="width: 95%; margin-left: 2%" class="dataTable" id="customerPaymentTable">	
			<thead>
				<tr>
					<th width="2%">#</th>
					<th width="1%"></th>
					<th width="8%">Date</th>
					<th width="12%">Reference ID</th>
					<th>Description</th>
					<th width="12%">Amount (Php)</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="customerPayment" items="${customerPaymentRecords.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>${status.index + 1} </td>
						<td><a id="payment${status.index + 1}}" href="#" onclick="showPaymentDetails(${status.index}, ${customerPayment.id})" >[+]</a> </td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${customerPayment.date}" /> </td>
						<td>${customerPayment.referenceId}</td>
						<td>${customerPayment.description}</td>
						<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerPayment.amount}" /> </td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td><input type="hidden" id="customerId" value="${customerId}" /> </td>
				</tr>
				<tr>
					<td colspan="3" style="font-weight: bold; ">Total </td>
					<c:forEach var="customerPayment" items="${customerPaymentRecords.data}" begin="0" end="${customerPaymentRecords.pageSetting.maxResult}" varStatus="status">
						<c:set var="totalAmount" value="${totalAmount + customerPayment.amount}" />
					</c:forEach>
					<td colspan="3" style="text-align: right;  font-weight: bold; ">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalAmount}" /> 
					</td>
				</tr>
				<tr>
					<td colspan="3">
						${customerPaymentRecords.dataSize + ((customerPaymentRecords.currentPage - 1) * customerPaymentRecords.pageSetting.maxResult)}/${customerPaymentRecords.totalRecords}
					</td>
					<td colspan="3" style="text-align: right;">
						<c:if test="${customerPaymentRecords.lastPage <= 5}">
							<c:forEach var="page" begin="1" end="${customerPaymentRecords.lastPage }" >
								<a href="#" onclick="goToPage (${page})" class="pageNumber">${page}</a>
							</c:forEach>
						</c:if>
						
						<c:if test="${customerPaymentRecords.lastPage > 5}">
							<c:if test="${customerPaymentRecords.currentPage <= 5}">
								<c:forEach var="page" begin="1" end="5" >
									<a href="#" onclick="goToPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								<a href="#" onclick="goToPage (6)" class="pageNumber"> >>  </a>
							</c:if>
							
							<c:if test="${customerPaymentRecords.currentPage > 5}">
								<c:set var="modPage" value="${customerPaymentRecords.currentPage % 5}"/>
								<c:choose>
									<c:when test="${(customerPaymentRecords.currentPage + (5 - modPage)) <= customerPaymentRecords.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${customerPaymentRecords.currentPage + (5 - modPage)}" />
										<c:set var="minPageSet" value="${maxPageSet - 4}" />
									</c:when>
									<c:when test="${(customerPaymentRecords.currentPage + (5 - modPage)) >= customerPaymentRecords.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${customerPaymentRecords.lastPage}" />
										<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
									</c:when>
									<c:otherwise>
										<c:set var="maxPageSet" value="${customerPaymentRecords.currentPage}" />
										<c:set var="minPageSet" value="${customerPaymentRecords.currentPage - 4}" />
									</c:otherwise>
								</c:choose>
								
								<c:if test="${minPageSet > 5}" >
									<a href="#" onclick="goToPage (${minPageSet - 1})" class="pageNumber"> << </a>
								</c:if>
								
								<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
									<a href="#" onclick="goToPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								
								<c:if test="${(maxPageSet) < customerPaymentRecords.lastPage }">
									<a href="#" onclick="goToPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
								</c:if>
							</c:if>
						</c:if>
					</td>
				</tr>
			</tfoot>
		</table>
</body>
</html>