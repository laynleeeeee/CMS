<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Shows all Stock Adjustment Types in table format.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditType").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	goToPage("adjustmentTypeTableDiv", SEARCH_URL+getCommonParam()+"&page="+pageNumber);
}
</script>
</head>
<body>
<span id="spanMessage" class="message"></span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th width="3%">#</th>
			<th width="2%"></th>
			<th width="20%">Company</th>
			<th width="25%">Name</th>
			<th width="20%">Division</th>
			<th width="20%">Charged Account</th>
			<th width="10%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(adjustmentTypes.pageSetting.maxResult * (adjustmentTypes.currentPage-1)) + 1}" />
			<c:forEach var="at" items="${adjustmentTypes.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'"
						onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${at.id}" name="cbAdjustmentType"
							onclick="enableEditAndDeleteButton('cbAdjustmentType','btnEditType', null);"/></td>
					<td>${at.acctCombi.company.name}</td>
					<td style="white-space: pre-wrap;">${at.name}</td>
					<td>${at.acctCombi.division.name}</td>
					<td>${at.acctCombi.account.accountName}</td>
					<td>
						<c:choose>
							<c:when test="${at.active eq true}">Active</c:when>
							<c:otherwise>Inactive</c:otherwise>
						</c:choose>
					</td>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">${adjustmentTypes.dataSize +
				((adjustmentTypes.currentPage - 1) *
				adjustmentTypes.pageSetting.maxResult)}/${adjustmentTypes.totalRecords}
			</td>
			<td colspan="4" style="text-align: right;"><c:if
					test="${adjustmentTypes.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${adjustmentTypes.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${adjustmentTypes.lastPage > 5}">
					<c:if test="${adjustmentTypes.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${adjustmentTypes.currentPage > 5}">
						<c:set var="modPage" value="${adjustmentTypes.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(adjustmentTypes.currentPage + (5 - modPage)) <= adjustmentTypes.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${adjustmentTypes.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(adjustmentTypes.currentPage + (5 - modPage)) >= adjustmentTypes.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${adjustmentTypes.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${adjustmentTypes.currentPage}" />
								<c:set var="minPageSet" value="${adjustmentTypes.currentPage - 4}" />
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

						<c:if test="${(maxPageSet) < adjustmentTypes.lastPage }">
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
