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
<body>
<table class="dataTable" border="0">
	<thead>
		<tr>
			<th width="2%"> # </th>
			<th width="1%">
				<input type="checkbox" id="checkAll"
				onclick="checkUncheckedAll ('cbAccountPayableList', this, 
				'btnEditPayableList', 'btnDeletePayableList')">
			</th>
			<th width="30%">Name</th>
			<th width="10%">Contact Number</th>
			<th width="40%">Address</th>
			<th width="17%">Current Balance </th>
		</tr>
	</thead>
	
	<tbody>
		<c:forEach var="customerListing" items="${customerListings.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'" >
				<td onclick="redirectToCustomerAccount(${customerListing.id})">${status.index + 1} </td>
				<td><input type="checkbox" id="${customerListing.id}" onclick="selectCustomer(${customerListing.id});" /></td>
				<td onclick="redirectToCustomerAccount(${customerListing.id})">${customerListing.name}</td>
				<td onclick="redirectToCustomerAccount(${customerListing.id})">${customerListing.contactNumber}</td>
				<td onclick="redirectToCustomerAccount(${customerListing.id})">${customerListing.address}</td>
				<td onclick="redirectToCustomerAccount(${customerListing.id})" style="text-align: right">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerListing.currentBalance}" /> 
				</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">
				${customerListings.dataSize + ((customerListings.currentPage - 1) * customerListings.pageSetting.maxResult)}/${customerListings.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<div id="pageNumbers">
				<c:if test="${customerListings.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${customerListings.lastPage }" >
						<a href="#" id="page-${page}" onclick="goToPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
						
				<c:if test="${customerListings.lastPage > 5}">
					<c:if test="${customerListings.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5" >
							<a href="#" id="page-${page}" onclick="goToPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="goToPage (6)" class="pageNumber"> >>  </a>
					</c:if>
							
					<c:if test="${customerListings.currentPage > 5}">
						<c:set var="modPage" value="${customerListings.currentPage % 5}"/>
							<c:choose>
								<c:when test="${(customerListings.currentPage + (5 - modPage)) <= customerListings.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${customerListings.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when test="${(customerListings.currentPage + (5 - modPage)) >= customerListings.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${customerListings.lastPage}" />
									<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${customerListings.currentPage}" />
									<c:set var="minPageSet" value="${customerListings.currentPage - 4}" />
								</c:otherwise>
							</c:choose>
							
							<c:if test="${minPageSet > 5}" >
								<a href="#" onclick="goToPage (${minPageSet - 1})" class="pageNumber"> << </a>
							</c:if>
		
							<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
								<a href="#" id="page-${page}" onclick="goToPage (${page})" class="pageNumber">${page}</a>
							</c:forEach>

							<c:if test="${(maxPageSet) < customerListings.lastPage }">
								<a href="#" onclick="goToPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
							</c:if>
					</c:if>
				</c:if>
			</div>
			</td>
		</tr>
	</tfoot>
</table>
</body>
</html>