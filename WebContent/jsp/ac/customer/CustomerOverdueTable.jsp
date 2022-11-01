<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
a.pageNumber {
	font-size: 12px;
}
</style>
</head>
<body>
<table style="width: 95%; margin-left: 2%" class="dataTable">	
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="30%">Name</th>
			<th width="40%">Address</th>
			<th width="12%">Contact Number</th>
			<th width="15%">Current Balance </th>
		</tr>
	</thead>
	
	<tbody>
		<c:forEach var="overdue" items="${overdues.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'" onclick="redirectToCustomerAccount(${overdue.id})">
						<td>${status.index + 1}</td>
						<td>${overdue.name}</td>
						<td>${overdue.address}</td>
						<td>${overdue.contactNumber}</td>
						<td style="text-align: right;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${overdue.currentAccountBalance}" /> 
						</td>
					</tr>
		</c:forEach>
	</tbody>

	<tfoot>
		<tr>
					<td colspan="4">
						${overdues.dataSize + ((overdues.currentPage - 1) * overdues.pageSetting.maxResult)}/${overdues.totalRecords}
					</td>
					<td colspan="2" style="text-align: right;">
						<c:if test="${overdues.lastPage <= 5}">
							<c:forEach var="page" begin="1" end="${overdues.lastPage }" >
								<a href="#" id="page-${page}" onclick="goToPage (${page})" class="pageNumber">${page}</a>
							</c:forEach>
						</c:if>
						
						<c:if test="${overdues.lastPage > 5}">
							<c:if test="${overdues.currentPage <= 5}">
								<c:forEach var="page" begin="1" end="5" >
									<a href="#" id="page-${page}" onclick="goToPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								<a href="#" onclick="goToPage (6)" class="pageNumber"> >>  </a>
							</c:if>
							
							<c:if test="${overdues.currentPage > 5}">
								<c:set var="modPage" value="${overdues.currentPage % 5}"/>
								<c:choose>
									<c:when test="${(overdues.currentPage + (5 - modPage)) <= overdues.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${overdues.currentPage + (5 - modPage)}" />
										<c:set var="minPageSet" value="${maxPageSet - 4}" />
									</c:when>
									<c:when test="${(overdues.currentPage + (5 - modPage)) >= overdues.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${overdues.lastPage}" />
										<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
									</c:when>
									<c:otherwise>
										<c:set var="maxPageSet" value="${overdues.currentPage}" />
										<c:set var="minPageSet" value="${overdues.currentPage - 4}" />
									</c:otherwise>
								</c:choose>
								
								<c:if test="${minPageSet > 5}" >
									<a href="#" onclick="goToPage (${minPageSet - 1})" class="pageNumber"> << </a>
								</c:if>
								
								<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
									<a href="#" id="page-${page}" onclick="goToPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								
								<c:if test="${(maxPageSet) < overdues.lastPage }">
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