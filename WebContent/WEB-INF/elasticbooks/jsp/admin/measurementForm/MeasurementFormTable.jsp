<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description: Measurement table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditMeasurement").attr("disabled", "disabled");
	markCurrentPageNumber ("${pageNumber}");
});

function loadPage(pageNumber) {
	var url = "/admin/unitMeasurement"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("dataUMTable", url);
}
</script>
</head>
<body>
	<table id="dataUMTable" class="dataTable">
		<thead>
			<tr class="alignLeft">
				<th width="2%">#</th>
				<th width="1%"></th>
				<th width="62%">Units of Measurement</th>
				<th width="35%">Status</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="index" value="${(unitMeasurements.pageSetting.maxResult * (unitMeasurements.currentPage - 1)) + 1}" />
				<c:forEach var="unitMeasurement" items="${unitMeasurements.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
						<td>${index}</td>
						<td><input type="checkbox" id="${unitMeasurement.id}"
							name="cbMeasurement"
							onclick="enableEditAndDeleteButton('cbMeasurement','btnEditMeasurement', null);" />
						</td>
						<td>${unitMeasurement.name}</td>
						<c:choose>
							<c:when test="${unitMeasurement.active == true}"><td>Active</td></c:when>
							<c:otherwise><td>Inactive</td></c:otherwise>
						</c:choose>
					</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
		</tbody>
		<tfoot class="nav">
			<tr>
				<td colspan="2">${unitMeasurements.dataSize + 
				((unitMeasurements.currentPage - 1) * 
				unitMeasurements.pageSetting.maxResult)}/${unitMeasurements.totalRecords}
				</td>
				<td colspan="2" style="text-align: right;"><c:if
						test="${unitMeasurements.lastPage <= 5}">
						<c:forEach var="page" begin="1" end="${unitMeasurements.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${unitMeasurements.lastPage > 5}">
						<c:if test="${unitMeasurements.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>
						<c:if test="${unitMeasurements.currentPage > 5}">
							<c:set var="modPage" value="${unitMeasurements.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(unitMeasurements.currentPage + (5 - modPage)) <= unitMeasurements.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${unitMeasurements.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(unitMeasurements.currentPage + (5 - modPage)) >= unitMeasurements.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${unitMeasurements.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${unitMeasurements.currentPage}" />
									<c:set var="minPageSet" value="${unitMeasurements.currentPage - 4}" />
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
							<c:if test="${(maxPageSet) < unitMeasurements.lastPage }">
								<a href="#" onclick="loadPage (${maxPageSet + 1})"
									class="pageNumber"> >> </a>
							</c:if>
						</c:if>
					</c:if>
				</td>
			</tr>
		</tfoot>
	</table>
</body>
</html>