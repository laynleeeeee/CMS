<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
	$(document).ready (function () {
		markCurrentPageNumber ($("#hdnPageNumber").val());
	});
	
	function loadPage (pageNumber) {
		var searchText = document.getElementById("searchUserText").value;
		var searchCategory = document.getElementById("searchUserOptionCategory").value;
		var searchStatus =  document.getElementById("searchUserOptionStatus").value;
		var targetUrl = "/user?searchText="+searchText+"&category="+searchCategory+"&status="+
				searchStatus+"&page="+pageNumber;
		goToPage ("userTable", targetUrl);
	}
</script>
<span id="messageSpan" class="message">
	${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable" >
			<thead>
				<tr>
					<th width="2%">#</th>
					<th width="1%"></th>
					<th width="14%">User Name</th>
					<th width="18%">Name</th>
					<th width="5%">Birthdate</th>
					<th width="10%">Email Address</th>
					<th width="8%">Contact Number</th>
					<th width="22%">Address</th>
					<th width="10%">Company</th>
					<th width="10%">User Group</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="user" items="${users.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>${status.index + 1} </td>
						<td><input type="checkbox" id="${user.id}" onclick="selectUser(${user.id});" /></td>
						<td>${user.username}</td>
						<td>${user.firstName} ${user.middleName} ${user.lastName}</td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${user.birthDate}" /></td>
						<td>${user.emailAddress}</td>
						<td>${user.contactNumber}</td>
						<td>${user.address}</td>						
						<td>${user.company.name}</td>
						<td>${user.userGroup.name}</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="9">
						${users.dataSize + ((users.currentPage - 1) * users.pageSetting.maxResult)}/${users.totalRecords}
					</td>
					<td colspan="1" style="text-align: right;">
						<c:if test="${users.lastPage <= 5}">
							<c:forEach var="page" begin="1" end="${users.lastPage }" >
								<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
							</c:forEach>
						</c:if>
						
						<c:if test="${users.lastPage > 5}">
							<c:if test="${users.currentPage <= 5}">
								<c:forEach var="page" begin="1" end="5" >
									<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								<a href="#" onclick="loadPage (6)" class="pageNumber"> >>  </a>
							</c:if>
							
							<c:if test="${users.currentPage > 5}">
								<c:set var="modPage" value="${users.currentPage % 5}"/>
								<c:choose>
									<c:when test="${(users.currentPage + (5 - modPage)) <= users.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${users.currentPage + (5 - modPage)}" />
										<c:set var="minPageSet" value="${maxPageSet - 4}" />
									</c:when>
									<c:when test="${(users.currentPage + (5 - modPage)) >= users.lastPage && modPage != 0 }">
										<c:set var="maxPageSet" value="${users.lastPage}" />
										<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
									</c:when>
									<c:otherwise>
										<c:set var="maxPageSet" value="${users.currentPage}" />
										<c:set var="minPageSet" value="${users.currentPage - 4}" />
									</c:otherwise>
								</c:choose>
								
								<c:if test="${minPageSet > 5}" >
									<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
								</c:if>
								
								<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
									<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
								</c:forEach>
								
								<c:if test="${(maxPageSet) < users.lastPage }">
									<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
								</c:if>
							</c:if>
						</c:if>
					</td>
				</tr>
			</tfoot>
</table>