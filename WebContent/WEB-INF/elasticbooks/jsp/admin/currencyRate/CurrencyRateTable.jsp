<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description: Currency Rate table
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
	$("#btnEditCurrencyRate").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/currencyRate/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divCurrencyTbl", url);
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
			<th width="15%">Date</th>
			<th width="15%">Time</th>
			<th width="35%">Currency</th>
			<th width="15%">Rate</th>
			<th width="16%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(currencyRates.pageSetting.maxResult * (currencyRates.currentPage - 1)) + 1}" />
			<c:forEach var="currencyRate" items="${currencyRates.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td></td>
					<td>
						<fmt:formatDate pattern="MM/dd/yyyy"  value="${currencyRate.date}" />
					</td>
					<td>
						<fmt:formatDate pattern="HH:mm"  value="${currencyRate.date}" />
					</td>
					<td>${currencyRate.currency.name}</td>
					<td class="number" style="padding-right: 10px">
						<fmt:formatNumber type="number" minFractionDigits="6" maxFractionDigits="6" value="${currencyRate.rate}" /> 
					</td>
					<c:choose>
						<c:when test="${currencyRate.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5">${currencyRates.dataSize + ((currencyRates.currentPage - 1) *
				currencyRates.pageSetting.maxResult)}/${currencyRates.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${currencyRates.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${currencyRates.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${currencyRates.lastPage > 5}">
				<c:if test="${currencyRates.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${currencyRates.currentPage > 5}">
					<c:set var="modPage" value="${currencyRates.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(currencyRates.currentPage + (5 - modPage)) <= currencyRates.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${currencyRates.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(currencyRates.currentPage + (5 - modPage)) >= currencyRates.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${currencyRates.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${currencyRates.currentPage}" />
							<c:set var="minPageSet" value="${currencyRates.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < currencyRates.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>