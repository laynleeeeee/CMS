<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="2%">#</th>
					<th width="1%"></th>
					<th width="10%">Date</th>
					<th width="20%">Reference ID</th>
					<th width="47%">Description</th>
					<th width="20%">Amount (Php)</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="payment" items="${payments.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>${status.index + 1} </td>
						<td><input type="checkbox" id="${payment.id}" onclick="selectPayment(${payment.id}, 
							'${payment.referenceId}', '<fmt:formatDate pattern="MM/dd/yyyy"  value="${payment.date}" />'  );" /></td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${payment.date}" /> </td>
						<td>${payment.referenceId}</td>
						<td>${payment.description}</td>
						<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${payment.amount}" /> </td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td><input type="hidden" id="customerId" value="${customerId}" /> </td>
				</tr>
				<tr>
					<td colspan="3" style="font-weight: bold; ">Total </td>
					<c:forEach var="payment" items="${payments.data}" begin="0" end="${payments.pageSetting.maxResult}" varStatus="status">
						<c:set var="totalAmount" value="${totalAmount + payment.amount}" />
					</c:forEach>
					<td colspan="3" style="text-align: right;  font-weight: bold; ">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalAmount}" /> 
					</td>
				</tr>
				<tr>
					<td colspan="3" style="font-weight: bold; font-size: 18px;">Grand Total</td>
					<td colspan="3" style="text-align: right; font-weight: bold; font-size: 18px;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${grandTotalAmount}" /> </td>
				</tr>
				<tr>
					<td colspan="3">
						${payments.dataSize + ((payments.currentPage - 1) * payments.pageSetting.maxResult)}/${payments.totalRecords}
					</td>
					<td colspan="3" style="text-align: right;">
						<c:if test="${payments.lastPage <= 5}">
							<c:forEach var="page" begin="1" end="${payments.lastPage }" >
								<a href="#" onclick="goToPage (${page})" class="pageNumber">${page}</a>
							</c:forEach>
						</c:if>
						
						<c:if test="${payments.lastPage > 5}">
							<c:if test="${payments.currentPage <= 5}">
								<c:forEach var="page" begin="1" end="5" >
									<a href="#" onclick="goToPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								<a href="#" onclick="goToPage (6)" class="pageNumber"> >>  </a>
							</c:if>
							
							<c:if test="${payments.currentPage > 5}">
								<c:set var="modPage" value="${payments.currentPage % 5}"/>
								<c:choose>
									<c:when test="${(payments.currentPage + (5 - modPage)) <= payments.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${payments.currentPage + (5 - modPage)}" />
										<c:set var="minPageSet" value="${maxPageSet - 4}" />
									</c:when>
									<c:when test="${(payments.currentPage + (5 - modPage)) >= payments.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${payments.lastPage}" />
										<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
									</c:when>
									<c:otherwise>
										<c:set var="maxPageSet" value="${payments.currentPage}" />
										<c:set var="minPageSet" value="${payments.currentPage - 4}" />
									</c:otherwise>
								</c:choose>
								
								<c:if test="${minPageSet > 5}" >
									<a href="#" onclick="goToPage (${minPageSet - 1})" class="pageNumber"> << </a>
								</c:if>
								
								<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
									<a href="#" onclick="goToPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								
								<c:if test="${(maxPageSet) < payments.lastPage }">
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