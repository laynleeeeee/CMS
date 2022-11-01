<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Displays the list of time periods in table form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditTimePeriod").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var name = processSearchName($("#name").val());
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var statusId = ($("#selectPeriodStatus").val());
	var url = "/admin/timePeriod?name="+name+"&periodStatusId="+statusId+"&startDate="+startDate+"&endDate="+endDate+"&pageNumber="+pageNumber;
	goToPage("timePeriodTable", url);
}
</script>
<span id="messageSpan" class="message"> ${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable" id="dataTable">
	<thead>
		<tr>
			<th>#</th>
			<th width="2%"><input type="checkbox" id="checkAll" onclick="checkUncheckedAll
				('cbTimePeriod', this, 'btnEditTimePeriod', null)"/></th>
			<th>Name</th>
			<th>Date From</th>
			<th>Date To</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="tp" items="${timePeriods.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${status.index +1}</td>
				<td><input type="checkbox" id="${tp.id}" name="cbTimePeriod"
					onclick="enableEditAndDeleteButton('cbTimePeriod','btnEditTimePeriod', null);"/></td>
				<td>${tp.name}</td>
				<td><fmt:formatDate value="${tp.dateFrom}"  pattern="dd-MMM-yyyy"/></td>
				<td><fmt:formatDate value="${tp.dateTo}"  pattern="dd-MMM-yyyy"/></td>
				<td>${tp.periodStatus.name}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5">${timePeriods.dataSize +
				((timePeriods.currentPage - 1) *
				timePeriods.pageSetting.maxResult)}/${timePeriods.totalRecords}</td>
			<td style="text-align: right;"><c:if
					test="${timePeriods.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${timePeriods.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${timePeriods.lastPage > 5}">
					<c:if test="${timePeriods.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${timePeriods.currentPage > 5}">
						<c:set var="modPage" value="${timePeriods.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(timePeriods.currentPage + (5 - modPage)) <= timePeriods.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${timePeriods.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(timePeriods.currentPage + (5 - modPage)) >= timePeriods.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${timePeriods.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${timePeriods.currentPage}" />
								<c:set var="minPageSet" value="${timePeriods.currentPage - 4}" />
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

						<c:if test="${(maxPageSet) < timePeriods.lastPage }">
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