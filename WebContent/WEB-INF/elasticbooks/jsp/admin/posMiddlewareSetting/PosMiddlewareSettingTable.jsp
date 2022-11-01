<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: POS Middleware Setting table
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
	$("#btnEditPosMiddlewareSetting").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/posMiddlewareSetting/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divPosMiddlewareSettingTbl", url);
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th width="2%">#</th>
			<th width="3%"></th>
			<th width="15%">Company</th>
			<th width="15%">Warehouse</th>
			<th width="30%">Customer</th>
			<th width="30%">Customer Account</th>
			<th width="5%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(middlewareSettings.pageSetting.maxResult * (middlewareSettings.currentPage - 1)) + 1}" />
			<c:forEach var="pos" items="${middlewareSettings.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${pos.id}" name="cbPosMiddlewareSetting"
						onclick="enableEditAndDeleteButton('cbPosMiddlewareSetting','btnEditPosMiddlewareSetting', null);"/></td>
					<td>${pos.company.name}</td>
					<td>${pos.warehouse.name}</td>
					<td>${pos.arCustomer.name}</td>
					<td>${pos.arCustomerAccount.name}</td>
					<c:choose>
						<c:when test="${pos.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${middlewareSettings.dataSize + ((middlewareSettings.currentPage - 1) *
				middlewareSettings.pageSetting.maxResult)}/${middlewareSettings.totalRecords}
			</td>
			<td colspan="3" style="text-align: right;">
			<c:if test="${middlewareSettings.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${middlewareSettings.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${middlewareSettings.lastPage > 5}">
				<c:if test="${middlewareSettings.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${middlewareSettings.currentPage > 5}">
					<c:set var="modPage" value="${middlewareSettings.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(middlewareSettings.currentPage + (5 - modPage)) <= middlewareSettings.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${middlewareSettings.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(middlewareSettings.currentPage + (5 - modPage)) >= middlewareSettings.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${middlewareSettings.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${middlewareSettings.currentPage}" />
							<c:set var="minPageSet" value="${middlewareSettings.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < middlewareSettings.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>