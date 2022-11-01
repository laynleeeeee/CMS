<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Type of leave table form.
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
	var url = "/admin/typeOfLeave/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divTypeOfLeaveTbl", url);
}
</script>
</head>
<body>
	<input type="hidden" id="hdnPageNumber" value="${pageNumber }" />
	<table id="dataTable" class="dataTable">
		<thead>
			<tr class="alignLeft">
				<th width="1%">#</th>
				<th width="2%"></th>
				<th width="27%">Types of Leave</th>
				<th width="45%">Description</th>
				<th width="15%">Paid</th>
				<th width="15%">Status</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="index" value="${(leaveTypes.pageSetting.maxResult * (leaveTypes.currentPage - 1)) + 1}" />
			<c:forEach var="typesOfLeave" items="${leaveTypes.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${typesOfLeave.id}" name="cbTypeOfLeaveType"
						onclick="enableEditAndDeleteButton('cbTypeOfLeaveType','btnEdit', null);"/></td>
					<td>${typesOfLeave.name}</td>
					<td>${typesOfLeave.description}</td>
					<c:choose>
						<c:when test="${typesOfLeave.paidLeave == true}"><td>Paid</td></c:when>
						<c:otherwise><td>Unpaid</td></c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${typesOfLeave.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="4">${leaveTypes.dataSize + ((leaveTypes.currentPage - 1) *
				leaveTypes.pageSetting.maxResult)}/${leaveTypes.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${leaveTypes.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${leaveTypes.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${leaveTypes.lastPage > 5}">
				<c:if test="${leaveTypes.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${leaveTypes.currentPage > 5}">
					<c:set var="modPage" value="${leaveTypes.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(leaveTypes.currentPage + (5 - modPage)) <= leaveTypes.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${leaveTypes.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(leaveTypes.currentPage + (5 - modPage)) >= leaveTypes.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${leaveTypes.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${leaveTypes.currentPage}" />
							<c:set var="minPageSet" value="${leaveTypes.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < leaveTypes.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
		</tfoot>
	</table>
</body>
</html>