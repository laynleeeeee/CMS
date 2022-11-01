<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
	$(document).ready (function () {
		markCurrentPageNumber ($("#hdnPageNumber").val());
	});
	
	function loadPage (pageNumber) {
		var searchText = document.getElementById("searchUserGroupText").value;
		var searchCategory = document.getElementById("searchUserGroupOptionCategory").value;
		var searchStatus =  document.getElementById("searchUserGroupOptionStatus").value;
		var targetUrl = "/userGroup?searchText="+searchText+"&category="+searchCategory+"&status="+
				searchStatus+"&page="+pageNumber;
		goToPage ("userGroupTable", targetUrl);
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
			<th width="65%">Description</th>
			<th width="3%">Active</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="userGroup" items="${userGroups.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'" >
				<td><input type="checkbox" id="${userGroup.id}" onclick="selectUserGroup(${userGroup.id});" /></td>
				<td>${userGroup.name}</td>
				<td>${userGroup.description}</td>
				<td>${userGroup.active}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				${userGroups.dataSize + ((userGroups.currentPage - 1) * userGroups.pageSetting.maxResult)}/${userGroups.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
				<c:if test="${userGroups.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${userGroups.lastPage }" >
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
						
				<c:if test="${userGroups.lastPage > 5}">
					<c:if test="${userGroups.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >>  </a>
					</c:if>
							
					<c:if test="${userGroups.currentPage > 5}">
						<c:set var="modPage" value="${userGroups.currentPage % 5}"/>
						<c:choose>
							<c:when test="${(userGroups.currentPage + (5 - modPage)) <= userGroups.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${userGroups.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when test="${(userGroups.currentPage + (5 - modPage)) >= userGroups.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${userGroups.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${userGroups.currentPage}" />
								<c:set var="minPageSet" value="${userGroups.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
					
						
						<c:if test="${minPageSet > 5}" >
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
								
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
								
						<c:if test="${(maxPageSet) < userGroups.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if>
			</td>
		</tr>
	</tfoot>
</table>