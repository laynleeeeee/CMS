<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../include.jsp"%>

<!--     Description: User Group Table -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
	$(document).ready (function () {
		$("#btnEditUserGroup").attr("disabled", "disabled");
		markCurrentPageNumber ("${pageNumber}");
	});

	function loadPage (pageNumber) {
		goToPage ("divUserGroupTable", "/admin/userGroup?"+getCommonParam()+"&pageNumber="+pageNumber);
	}
</script>
</head>
<body>
<table class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th width="2%">#</th>
			<th width="1%"><input type="checkbox"></th>
			<th width=15%>Group Name</th>
			<th width=32%>Description</th>
			<th width=10%>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(userGroups.pageSetting.maxResult * (userGroups.currentPage - 1)) + 1}" />
			<c:forEach var="usrGroup" items="${userGroups.data}" varStatus="status">
				<tr>
					<td>${index}</td>
					<td>
						<input type="checkbox" id="${usrGroup.id}" name="cbUserGroup" 
							onclick="enableEditAndDeleteButton('cbUserGroup','btnEditUserGroup', null);"/>
					</td>
					<td>${usrGroup.name}</td>
					<td>${usrGroup.description}</td>
					<td>
						<c:choose>
							<c:when test="${usrGroup.active}">Active</c:when>
							<c:otherwise>Inactive</c:otherwise>
						</c:choose>
					</td>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${userGroups.dataSize +
				((userGroups.currentPage - 1) *
				userGroups.pageSetting.maxResult)}/${userGroups.totalRecords}</td>
			<td style="text-align: right;"><c:if
					test="${userGroups.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${userGroups.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page}); return false;"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${userGroups.lastPage > 5}">
					<c:if test="${userGroups.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page}); return false;"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6); return false;" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${userGroups.currentPage > 5}">
						<c:set var="modPage" value="${userGroups.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(userGroups.currentPage + (5 - modPage)) <= userGroups.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${userGroups.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(userGroups.currentPage + (5 - modPage)) >= userGroups.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${userGroups.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${userGroups.currentPage}" />
								<c:set var="minPageSet" value="${userGroups.currentPage - 4}" />
							</c:otherwise>
						</c:choose>

						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1}); return false;"
								class="pageNumber"> << </a>
						</c:if>

						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page}); return false; "
								class="pageNumber">${page}</a>
						</c:forEach>

						<c:if test="${(maxPageSet) < userGroups.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1}); return false;"
								class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>