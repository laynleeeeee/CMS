<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR Line Setup table.
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
	$("#btnEditLineSetup").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/arLineSetup/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("lineSetupTable", url);
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
			<th>Discount Account</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
	<c:set var="index" value="${(arLines.pageSetting.maxResult * (arLines.currentPage - 1)) + 1}" />
		<c:forEach var="arLine" items="${arLines.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${arLine.id}" name="cbLineSetup"
						onclick="enableEditAndDeleteButton('cbLineSetup','btnEditLineSetup', null);"/></td>
					<td>${arLine.accountCombination.company.name}</td>
					<td>${arLine.name}</td>
					<td>${arLine.accountCombination.division.name}</td>
					<td>${arLine.accountCombination.account.accountName}</td>
					<td>${arLine.discAccountCombination.account.accountName}</td>
					<c:choose>
						<c:when test="${arLine.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="7">${arLines.dataSize + ((arLines.currentPage - 1) *
				arLines.pageSetting.maxResult)}/${arLines.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${arLines.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${arLines.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${arLines.lastPage > 5}">
				<c:if test="${arLines.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${arLines.currentPage > 5}">
					<c:set var="modPage" value="${arLines.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(arLines.currentPage + (5 - modPage)) <= arLines.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${arLines.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(arLines.currentPage + (5 - modPage)) >= arLines.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${arLines.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${arLines.currentPage}" />
							<c:set var="minPageSet" value="${arLines.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < arLines.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>