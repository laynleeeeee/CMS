<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

	Description: Holiday Setting table -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditHolidaySetting").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/holidaySetting/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divHolidaySettingTbl", url);
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
</body>
<table class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th width="2%">#</th>
			<th width="3%"></th>
			<th width="50%">Company</th>
			<th width="30%">Name</th>
			<th width="10%">Holiday Type</th>
			<th width="10%">Date</th>
			<th width="5%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(holidaySettings.pageSetting.maxResult * (holidaySettings.currentPage - 1)) + 1}" />
			<c:forEach var="holidaySetting" items="${holidaySettings.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${holidaySetting.id}" name="cbHolidaySetting"
						onclick="enableEditAndDeleteButton('cbHolidaySetting','btnEditHolidaySetting', null);"/></td>
					<td>${holidaySetting.company.name}</td>
					<td>${holidaySetting.name}</td>
					<td>${holidaySetting.holidayType.name}</td>
					<td><fmt:formatDate pattern="MM/dd/yyyy" value="${holidaySetting.date}"/></td>
					<c:choose>
						<c:when test="${holidaySetting.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5">${holidaySettings.dataSize + ((holidaySettings.currentPage - 1) *
				holidaySettings.pageSetting.maxResult)}/${holidaySettings.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${holidaySettings.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${holidaySettings.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${holidaySettings.lastPage > 5}">
				<c:if test="${holidaySettings.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${holidaySettings.currentPage > 5}">
					<c:set var="modPage" value="${holidaySettings.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(holidaySettings.currentPage + (5 - modPage)) <= holidaySettings.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${holidaySettings.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(holidaySettings.currentPage + (5 - modPage)) >= holidaySettings.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${holidaySettings.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${holidaySettings.currentPage}" />
							<c:set var="minPageSet" value="${holidaySettings.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < holidaySettings.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</html>