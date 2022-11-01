<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Service table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEdit").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/service/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("serviceTable", url);
}
</script>
</head>
<body>
<span id="messageSpan" class="message"> ${message}</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th>#</th>
			<th></th>
			<th>Company</th>
			<th>Name</th>
			<th>Division</th>
			<th>Account</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
	<c:set var="index" value="${(services.pageSetting.maxResult * (services.currentPage - 1)) + 1}" />
		<c:forEach var="service" items="${services.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${service.id}" name="cbService"
						onclick="enableEditAndDeleteButton('cbService','btnEdit', null);"/></td>
					<td>${service.accountCombination.company.name}</td>
					<td>${service.name}</td>
					<td>${service.accountCombination.division.name}</td>
					<td>${service.accountCombination.account.accountName}</td>
					<c:choose>
						<c:when test="${service.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6">${services.dataSize + ((services.currentPage - 1) *
				services.pageSetting.maxResult)}/${services.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${services.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${services.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${services.lastPage > 5}">
				<c:if test="${services.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${services.currentPage > 5}">
					<c:set var="modPage" value="${services.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(services.currentPage + (5 - modPage)) <= services.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${services.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(services.currentPage + (5 - modPage)) >= services.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${services.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${services.currentPage}" />
							<c:set var="minPageSet" value="${services.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < services.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>