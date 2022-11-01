<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<!-- 

	Description: User Position table. 
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
	$("#btnEditUserPosition").attr("disabled","disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber){
	var name = processSearchName ($("#txtName").val());
	var status = $("#selectStatus").val();
	
	goToPage ("divUserPositionTable", "/admin/userPositions?name="+name+"&status="+status+"&pageNumber="+pageNumber);
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}">
	<table id="dataTable" class="dataTable">
		<thead>
			<tr class="alignLeft">
				<th width="5%">#</th>
				<th width="5%"><input type="checkbox" id="checkAll"
					onclick="checkUncheckedAll('cbPositions', this, 'btnEditUserPosition', null)"/></th>
				<th width="63%">Name</th>
				<th width="27%">Status</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="index" value="${(positions.pageSetting.maxResult * (positions.currentPage - 1)) + 1}" />
				<c:forEach var="pos" items="${positions.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'"
						onMouseOut="this.className='${trClass}'" class="${trClass}">
						<td>${index}</td>
						<td><input type="checkbox" id="${pos.id}" name="cbPositions"
							onclick="enableEditAndDeleteButton('cbPositions','btnEditUserPosition', null);" />
						</td>
						<td>${pos.name}</td>
						<td>
							<c:choose>
								<c:when test="${pos.active eq true}">Active</c:when>
								<c:otherwise>Inactive</c:otherwise>
							</c:choose>
						</td>
					</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
		</tbody>
		<tfoot class="nav">
			<tr>
				<td colspan="3">${positions.dataSize +
					((positions.currentPage - 1) *
					positions.pageSetting.maxResult)}/${positions.totalRecords}
				</td>
				<td colspan="1" style="text-align: right;"><c:if
						test="${positions.lastPage <= 5}">
						<c:forEach var="page" begin="1" end="${positions.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${positions.lastPage > 5}">
						<c:if test="${positions.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${positions.currentPage > 5}">
							<c:set var="modPage" value="${positions.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(positions.currentPage + (5 - modPage)) <= positions.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${positions.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(positions.currentPage + (5 - modPage)) >= positions.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${positions.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${positions.currentPage}" />
									<c:set var="minPageSet" value="${positions.currentPage - 4}" />
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

							<c:if test="${(maxPageSet) < positions.lastPage }">
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