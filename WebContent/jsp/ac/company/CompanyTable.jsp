<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
	$(document).ready (function () {
		markCurrentPageNumber ($("#hdnPageNumber").val());
	});
	
	function loadPage (pageNumber) {
		var searchText = document.getElementById("searchCompanyText").value;
		var searchCategory = document.getElementById("searchCompanyOptionCategory").value;
		var searchStatus =  document.getElementById("searchCompanyOptionStatus").value;
		var targetUrl = "/company?searchText="+searchText+"&category="+searchCategory+"&status="+
				searchStatus+"&page="+pageNumber;
		goToPage ("companyTable", targetUrl);
	}
</script>
<span id="messageSpan" class="message">
	${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable">
	<thead>
		<tr>
			<th width="2%"> </th>
			<th width="30%">Name</th>
			<th width="35%">Address</th>
			<th width="10%">Contact Number</th>
			<th width="15%">Email</th>
			<th width="3%">Active</th>		
		</tr>
	</thead>
	<tbody>
		<c:forEach var="company" items="${companies.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
				<td><input type="checkbox" id="${company.id}" onclick="selectCompany(${company.id});" /></td>
				<td>${company.name}</td>
				<td>${company.address}</td>
				<td>${company.contactNumber}</td>
				<td>${company.emailAddress}</td>
				<td>${company.active}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">
				${companies.dataSize + ((companies.currentPage - 1) * companies.pageSetting.maxResult)}/${companies.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
				<c:if test="${companies.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${companies.lastPage }" >
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
						
				<c:if test="${companies.lastPage > 5}">
					<c:if test="${companies.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >>  </a>
					</c:if>
							
					<c:if test="${companies.currentPage > 5}">
						<c:set var="modPage" value="${companies.currentPage % 5}"/>
							<c:choose>
								<c:when test="${(companies.currentPage + (5 - modPage)) <= companies.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${companies.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when test="${(companies.currentPage + (5 - modPage)) >= companies.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${companies.lastPage}" />
									<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${companies.currentPage}" />
									<c:set var="minPageSet" value="${companies.currentPage - 4}" />
								</c:otherwise>
							</c:choose>
							
							<c:if test="${minPageSet > 5}" >
								<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
							</c:if>
		
							<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
								<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
							</c:forEach>

							<c:if test="${(maxPageSet) < companies.lastPage }">
								<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
							</c:if>
					</c:if>
				</c:if>
			</td>
		</tr>
	</tfoot>
</table>
		