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
					<th width="20%">Name</th>
					<th width="7%">Contact Number</th>
					<th width="40%">Address</th>
					<th width="10%">Email Address</th>
					<th width="10">Interest Rate</th>
					<th width="10">Maximum Allowable Amount</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="customer" items="${customers.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>${status.index + 1} </td>
						<td><input type="checkbox" id="${customer.id}" onclick="selectCustomer(${customer.id});" /></td>
						<td>${customer.name}</td>
						<td>${customer.contactNumber}</td>
						<td>${customer.address}</td>
						<td>${customer.emailAddress}</td>
						<td style="text-align: right;">${customer.customerAcctSetting.interestRate}</td>
						<td style="text-align: right;">${customer.customerAcctSetting.maxAllowableAmount}</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="7">
						${customers.dataSize + ((customers.currentPage - 1) * customers.pageSetting.maxResult)}/${customers.totalRecords}
					</td>
					<td colspan="1" style="text-align: right;">
						<c:if test="${customers.lastPage <= 5}">
							<c:forEach var="page" begin="1" end="${customers.lastPage }" >
								<a href="#" onclick="goToPage (${page})" class="pageNumber">${page}</a>
							</c:forEach>
						</c:if>
						
						<c:if test="${customers.lastPage > 5}">
							<c:if test="${customers.currentPage <= 5}">
								<c:forEach var="page" begin="1" end="5" >
									<a href="#" onclick="goToPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								<a href="#" onclick="goToPage (6)" class="pageNumber"> >>  </a>
							</c:if>
							
							<c:if test="${customers.currentPage > 5}">
								<c:set var="modPage" value="${customers.currentPage % 5}"/>
								<c:choose>
									<c:when test="${(customers.currentPage + (5 - modPage)) <= customers.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${customers.currentPage + (5 - modPage)}" />
										<c:set var="minPageSet" value="${maxPageSet - 4}" />
									</c:when>
									<c:when test="${(customers.currentPage + (5 - modPage)) >= customers.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${customers.lastPage}" />
										<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
									</c:when>
									<c:otherwise>
										<c:set var="maxPageSet" value="${customers.currentPage}" />
										<c:set var="minPageSet" value="${customers.currentPage - 4}" />
									</c:otherwise>
								</c:choose>
								
								<c:if test="${minPageSet > 5}" >
									<a href="#" onclick="goToPage (${minPageSet - 1})" class="pageNumber"> << </a>
								</c:if>
								
								<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
									<a href="#" onclick="goToPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								
								<c:if test="${(maxPageSet) < customers.lastPage }">
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