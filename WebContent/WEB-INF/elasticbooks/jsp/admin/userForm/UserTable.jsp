<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<!-- 

	Description: User table. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#btnEditUser").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var targetUrl = "/admin/userForms?"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage ("divUserTable", targetUrl);
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table id="dataTable" class="dataTable">
		<thead>
			<tr class="alignLeft">
				<th width="2%">#</th>
				<th width="1%"><input type="checkbox"/></th>
				<th width="10%">Username</th>
				<th width="10%">First Name</th>
				<th width="5%">Middle Name</th>
				<th width="10%">Last Name</th>
				<th width="6%">Birthdate</th>
				<th width="6%">Contact No.</th>
				<th width="6%">Email Address</th>
				<th width="13%">Address</th>
				<th width="9%">User Group</th>
				<th width="10%">Position</th>
				<th width="7%">Login Status</th>
				<th width="6%">Status</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="index" value="${(users.pageSetting.maxResult * (users.currentPage - 1)) + 1}" />
				<c:forEach var="usr" items="${users.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'"
						onMouseOut="this.className='${trClass}'" class="${trClass}">
						<td>${index}</td>
						<td><input type="checkbox" id="${usr.id}" name="cbUsers"
							onclick="enableEditAndDeleteButton('cbUsers','btnEditUser', null);" />
						</td>
						<td>${usr.username}</td>
						<td>${usr.firstName}</td>
						<td>${usr.middleName}</td>
						<td>${usr.lastName}</td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${usr.birthDate}" /></td>
						<td>${usr.contactNumber}</td>
						<td>${usr.emailAddress}</td>
						<td>${usr.address}</td>
						<td>${usr.userGroup.name}</td>
						<td>${usr.position.name}</td>
						<td>
							<c:choose>
								<c:when test="${usr.userLoginStatus.blockUser eq true}">Blocked</c:when>
								<c:otherwise>Unblocked</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${usr.active eq true}">Active</c:when>
								<c:otherwise>Inactive</c:otherwise>
							</c:choose>
						</td>
					</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
		</tbody>
		<tfoot class="nav">
			<tr>
				<td colspan="10">${users.dataSize +
					((users.currentPage - 1) *
					users.pageSetting.maxResult)}/${users.totalRecords}
				</td>
				<td colspan="4" style="text-align: right;"><c:if
						test="${users.lastPage <= 5}">
						<c:forEach var="page" begin="1" end="${users.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${users.lastPage > 5}">
						<c:if test="${users.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${users.currentPage > 5}">
							<c:set var="modPage" value="${users.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(users.currentPage + (5 - modPage)) <= users.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${users.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(users.currentPage + (5 - modPage)) >= users.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${users.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${users.currentPage}" />
									<c:set var="minPageSet" value="${users.currentPage - 4}" />
								</c:otherwise>
							</c:choose>
							<c:if test="${minPageSet > 5}">
								<a href="#" onclick="loadPage (${minPageSet - 1})"
									class="pageNumber"> << </a>
							</c:if>

							<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>

							<c:if test="${(maxPageSet) < users.lastPage }">
								<a href="#" onclick="loadPage (${maxPageSet + 1})"
									class="pageNumber"> >> </a>
							</c:if>
						</c:if>
					</c:if></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>