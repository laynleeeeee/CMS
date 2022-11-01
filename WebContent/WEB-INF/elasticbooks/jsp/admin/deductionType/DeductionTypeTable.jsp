<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Deduction type table form.
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
	var url = "/admin/deductionType/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divDeductionTypeTbl", url);
}
</script>
</head>
<body>
	<input type="hidden" id="hdnPageNumber" value="${pageNumber }" />
	<table id="dataTable" class="dataTable">
		<thead>
			<tr class="alignLeft">
				<th width="2%">#</th>
				<th width="3%"></th>
				<th width="55%">Deduction Type</th>
				<th width="40%">Status</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="index" value="${(deductionTypes.pageSetting.maxResult * (deductionTypes.currentPage - 1)) + 1}" />
			<c:forEach var="dt" items="${deductionTypes.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${dt.id}" name="cbDeductionType"
						onclick="enableEditAndDeleteButton('cbDeductionType','btnEdit', null);"/></td>
					<td>${dt.name}</td>
					<c:choose>
						<c:when test="${dt.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="3">${deductionTypes.dataSize + ((deductionTypes.currentPage - 1) *
				deductionTypes.pageSetting.maxResult)}/${deductionTypes.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${deductionTypes.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${deductionTypes.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${deductionTypes.lastPage > 5}">
				<c:if test="${deductionTypes.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${deductionTypes.currentPage > 5}">
					<c:set var="modPage" value="${deductionTypes.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(deductionTypes.currentPage + (5 - modPage)) <= deductionTypes.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${deductionTypes.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(deductionTypes.currentPage + (5 - modPage)) >= deductionTypes.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${deductionTypes.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${deductionTypes.currentPage}" />
							<c:set var="minPageSet" value="${deductionTypes.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < deductionTypes.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
		</tfoot>
	</table>
</body>
</body>
</html>