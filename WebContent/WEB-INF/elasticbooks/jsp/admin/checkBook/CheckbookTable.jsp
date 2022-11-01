<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Check book table. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#btnEditCheckbook").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var targetUrl = "/admin/checkbook"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage ("divCheckbookTable", targetUrl);
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table id="dataTable" class="dataTable">
		<thead>
			<tr class="alignLeft">
				<th width="2%">#</th>
				<th width="1%"></th>
				<th width="30%">Bank Account Name</th>
				<th width="30%">Checkbook Name</th>
				<th width="15%">Check No. From</th>
				<th width="15%">Check No. To</th>
				<th width="7%">Status</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="index" value="${(checkBooks.pageSetting.maxResult * (checkBooks.currentPage - 1)) + 1}" />
				<c:forEach var="cb" items="${checkBooks.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'"
						onMouseOut="this.className='${trClass}'" class="${trClass}">
						<td>${index}</td>
						<td><input type="checkbox" id="${cb.id}" name="cbCheckbook"
							onclick="enableEditAndDeleteButton('cbCheckbook','btnEditCheckbook', null);" />
						</td>
						<td>${cb.bankAccount.name}</td>
						<td>${cb.name}</td>
						<td>${cb.checkbookNoFrom}</td>
						<td>${cb.checkbookNoTo}</td>
						<td>
							<c:choose>
								<c:when test="${cb.active eq true}">Active</c:when>
								<c:otherwise>Inactive</c:otherwise>
							</c:choose>
						</td>
					</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
		</tbody>
		<tfoot class="nav">
			<tr>
				<td colspan="5">${checkBooks.dataSize +
					((checkBooks.currentPage - 1) *
					checkBooks.pageSetting.maxResult)}/${checkBooks.totalRecords}
				</td>
				<td colspan="4" style="text-align: right;"><c:if
						test="${checkBooks.lastPage <= 5}">
						<c:forEach var="page" begin="1" end="${checkBooks.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${checkBooks.lastPage > 5}">
						<c:if test="${checkBooks.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${checkBooks.currentPage > 5}">
							<c:set var="modPage" value="${checkBooks.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(checkBooks.currentPage + (5 - modPage)) <= checkBooks.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${checkBooks.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(checkBooks.currentPage + (5 - modPage)) >= checkBooks.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${checkBooks.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${checkBooks.currentPage}" />
									<c:set var="minPageSet" value="${checkBooks.currentPage - 4}" />
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

							<c:if test="${(maxPageSet) < checkBooks.lastPage }">
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