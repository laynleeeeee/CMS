<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Term form table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#btnEditTerm").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var termName = processSearchName($("#txtTermName").val());
	var days = processSearchName($("#txtTermDay").val());
	var status = $("#selectStatus").val();
	var targetUrl = "/admin/termForm?termName="+termName+"&days="+days+"&status="+status+"&pageNumber="+pageNumber;
	goToPage ("divTermFormTable", targetUrl);
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="dataTable" class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th width="1%">#</th>
			<th width="2%"></th>
			<th width="37%">Term Name</th>
			<th width="30%">Days</th>
			<th width="30%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(term.pageSetting.maxResult * (term.currentPage - 1)) + 1}" />
			<c:forEach var="t" items="${term.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td>
						<input type="checkbox" id="${t.id}" name="cbTerm" 
							onclick="enableEditAndDeleteButton('cbTerm','btnEditTerm', null);"/>
					</td>
					<td>${t.name}</td>
					<td>${t.days}</td>
					<td>
						<c:choose>
							<c:when test="${t.active eq true}">Active</c:when>
							<c:otherwise>Inactive</c:otherwise>
						</c:choose>
					</td>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot class="nav">
		<tr>
				<td colspan="4">${term.dataSize +
					((term.currentPage - 1) *
					term.pageSetting.maxResult)}/${term.totalRecords}
				</td>
					<td style="text-align: right;"><c:if
						test="${term.lastPage <= 5}">
						<c:forEach var="page" begin="1"
							end="${term.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${term.lastPage > 5}">
						<c:if test="${term.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${term.currentPage > 5}">
							<c:set var="modPage"
								value="${term.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(term.currentPage + (5 - modPage)) <= term.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${term.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(term.currentPage + (5 - modPage)) >= term.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${term.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet"
										value="${term.currentPage}" />
									<c:set var="minPageSet"
										value="${term.currentPage - 4}" />
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

							<c:if test="${(maxPageSet) < term.lastPage }">
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