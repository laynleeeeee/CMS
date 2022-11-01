<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: List of inventory accounts in table format.
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
	$("#btnEditInvAccount").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var targetUrl = "/admin/iventoryAccount"+getCommonParam()+"&page="+pageNumber;
	goToPage ("divInvAcctTable", targetUrl);
}
</script>
</head>
<body>
<span id="spanMessage" class="message"></span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th width="2%">#</th>
			<th width="1%"></th>
			<th width="35%">Company</th>
			<th width="25%">Cash Sales Form</th>
			<th width="25%">Customer Advance Payment Form</th>
			<th width="12%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(inventoryAccts.pageSetting.maxResult * (inventoryAccts.currentPage-1)) + 1}" />
			<c:forEach var="ia" items="${inventoryAccts.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'"
						onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${ia.id}" name="cbInvAcct"
						onclick="enableEditAndDeleteButton('cbInvAcct','btnEditInvAccount', null);" />
					</td>
					<td>${ia.company.name}</td>
					<td>${ia.cashSaleRM.name}</td>
					<td>${ia.customerAdvPaymentRM.name}</td>
					<td>
						<c:choose>
							<c:when test="${ia.active eq true}">Active</c:when>
							<c:otherwise>Inactive</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
	</tbody>
	<tfoot class="nav">
			<tr>
				<td colspan="4">${inventoryAccts.dataSize +
					((inventoryAccts.currentPage - 1) *
					inventoryAccts.pageSetting.maxResult)}/${inventoryAccts.totalRecords}
				</td>
				<td colspan="2" style="text-align: right;">
					<c:if test="${inventoryAccts.lastPage <= 5}">
						<c:forEach var="page" begin="1" end="${inventoryAccts.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if>
					<c:if test="${inventoryAccts.lastPage > 5}">
						<c:if test="${inventoryAccts.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>
						<c:if test="${inventoryAccts.currentPage > 5}">
							<c:set var="modPage" value="${inventoryAccts.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(inventoryAccts.currentPage + (5 - modPage)) <= inventoryAccts.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${inventoryAccts.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(inventoryAccts.currentPage + (5 - modPage)) >= inventoryAccts.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${inventoryAccts.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${inventoryAccts.currentPage}" />
									<c:set var="minPageSet" value="${inventoryAccts.currentPage - 4}" />
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
							<c:if test="${(maxPageSet) < inventoryAccts.lastPage }">
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