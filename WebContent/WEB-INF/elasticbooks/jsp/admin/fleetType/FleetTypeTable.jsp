<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description: FleetType table. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
	$(document).ready (function () {
		$("#btnEditFleetType").attr("disabled", "disabled");
		markCurrentPageNumber ("${pageNumber}");
	});

	function loadPage(pageNumber) {
		goToPage("divFleetTypeTable", SEARCH_URL+getCommonParam()+"&pageNumber="+pageNumber);
	}
</script>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="tblFleetType" class="dataTable" border=0>
	<thead>
		<tr class="alignLeft">
			<th width="3%">#</th>
			<th width="2%"></th>
			<th width="20%">Company</th>
			<th width="20%">Category</th>
			<th width="50%">Fleet Type</th>
			<th width="5%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(fleetTypes.pageSetting.maxResult * (fleetTypes.currentPage-1)) + 1}" />
		<c:forEach var="fleetType" items="${fleetTypes.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'"
					onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${index}</td>
				<td>
					<input type="checkbox" id="${fleetType.id}" name="cbFleetType"
						onclick="enableEditAndDeleteButton('cbFleetType','btnEditFleetType', null);"/>
				</td>
				<td style="white-space: pre-wrap;">${fleetType.company.name}</td>
				<td style="white-space: pre-wrap;">${fleetType.fleetCategory.name}</td>
				<td style="white-space: pre-wrap;">${fleetType.name}</td>
				<td>
					<c:choose>
						<c:when test="${fleetType.active eq true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
			</tr>
		<c:set var="index" value="${index + 1}" />
	</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">${fleetTypes.dataSize +
				((fleetTypes.currentPage - 1) *
				fleetTypes.pageSetting.maxResult)}/${fleetTypes.totalRecords}
			</td>
			<td colspan="3" style="text-align: right;"><c:if
					test="${fleetTypes.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${fleetTypes.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${fleetTypes.lastPage > 5}">
					<c:if test="${fleetTypes.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> </a>
					</c:if>

					<c:if test="${fleetTypes.currentPage > 5}">
						<c:set var="modPage" value="${fleetTypes.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(fleetTypes.currentPage + (5 - modPage)) <= fleetTypes.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${fleetTypes.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(fleetTypes.currentPage + (5 - modPage)) >= fleetTypes.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${fleetTypes.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${fleetTypes.currentPage}" />
								<c:set var="minPageSet" value="${fleetTypes.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1})"
								class="pageNumber"> </a>
						</c:if>

						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>

						<c:if test="${(maxPageSet) < fleetTypes.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})"
								class="pageNumber"> </a>
						</c:if>
					</c:if>
				</c:if>
			</td>
		</tr>
	</tfoot>
</table>
