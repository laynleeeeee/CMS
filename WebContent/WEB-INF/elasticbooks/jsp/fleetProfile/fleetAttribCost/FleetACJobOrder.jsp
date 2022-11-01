<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Profile Form
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<style>

#btnSaveFleetDriver, #btnCancelFleetDriver {
	font-weight: bold;
}

</style>
<script type="text/javascript">
$(document).ready (function () {
	markJOCurrentPageNumber ($("#hdnJOPageNumber").val());
});

function loadJOPage (pageNumber) {
	genJO(pageNumber);
}

function markJOCurrentPageNumber(pageNumber){
	$("a#pageJO-" + pageNumber).css("font-size", "16px");
	$("a#pageJO-" + pageNumber).css("color", "red");
}
</script>
<title>Fleet Attributable Cost</title>
</head>
<body>
<input type="hidden" id="hdnJOPageNumber" value="${pageNumber}" />
<table id="tblJobOrder" class="dataTable">
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="10%">Date</th>
			<th width="13%">Reference No.</th>
			<th width="25%">Description</th>
			<th width="15%">Account Name</th>
			<th width="10%">Amount (Php)</th>
			<th width="25%">Remarks</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="jo" items="${jobOrders.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${status.index + 1}</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${jo.date}"/></td>
				<td>${jo.refNo}</td>
				<td>${jo.description}</td>
				<td>${jo.accountName}</td>
				<td style="text-align: right;">
					<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${jo.amount}'/>
					<c:set var="total" value="${total + jo.amount}" />
				</td>
				<td>${jo.remarks}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5" style="font-weight: bold;">Total</td>
			<td style="font-weight: bold; text-align: right;">
				<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${total}'/>
			</td>
		</tr>
		<tr>
			<td colspan="6">${jobOrders.dataSize + ((jobOrders.currentPage - 1) *
				jobOrders.pageSetting.maxResult)}/${jobOrders.totalRecords}</td>
			<td style="text-align: right;"><c:if
					test="${jobOrders.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${jobOrders.lastPage }">
						<a href="#" id="pageJO-${page}" onclick="loadJOPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${jobOrders.lastPage > 5}">
					<c:if test="${jobOrders.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="pageJO-${page}" onclick="loadJOPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadJOPage (6)" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${jobOrders.currentPage > 5}">
						<c:set var="modPage" value="${jobOrders.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(jobOrders.currentPage + (5 - modPage)) <= jobOrders.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${jobOrders.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(jobOrders.currentPage + (5 - modPage)) >= jobOrders.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${jobOrders.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${jobOrders.currentPage}" />
								<c:set var="minPageSet" value="${jobOrders.currentPage - 4}" />
							</c:otherwise>
						</c:choose>

						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadJOPage (${minPageSet - 1})"
								class="pageNumber"> << </a>
						</c:if>

						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="pageJO-${page}" onclick="loadJOPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>

						<c:if test="${(maxPageSet) < jobOrders.lastPage }">
							<a href="#" onclick="loadJOPage (${maxPageSet + 1})"
								class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>