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
	markCurrentICPageNumber ($("#hdnICPageNumber").val());
});

function loadICPage (pageNumber) {
	pNumber = pageNumber;
	genIC(pageNumber);
}

function markCurrentICPageNumber (pageNumber) {
	$("a#pageIC-" + pageNumber).css("font-size", "16px");
	$("a#pageIC-" + pageNumber).css("color", "red");
}
</script>
<title>Fleet Attributable Cost</title>
</head>
<body>
<input type="hidden" id="hdnICPageNumber" value="${icPageNumber}" />
<table id="tblItemsConsumed" class="dataTable">
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="10%">Date</th>
			<th width="13%">WS No.</th>
			<th width="15%">Stock Code</th>
			<th width="25%">Item Description</th>
			<th width="5%">Qty</th>
			<th width="10%">UOM (Php)</th>
			<th width="10%">Unit Cost</th>
			<th width="10%">Amount (Php)</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="ic" items="${itemsConsumed.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${status.index + 1}</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${ic.date}"/></td>
				<td>${ic.refNo}</td>
				<td>${ic.stockCode}</td>
				<td>${ic.description}</td>
				<td style="text-align: right;">
					<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${ic.qty}'/>
					<c:set var="totalQty" value="${totalQty + ic.qty}" />
				</td>
				<td>${ic.uom}</td>
				<td style="text-align: right;">
					<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${ic.amount}'/>
				</td>
				<td style="text-align: right;">
					<c:set var="amount" value="${ic.qty * ic.amount}" />
					<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${amount}'/>
					<c:set var="totalAmount" value="${totalAmount + amount}" />
				</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5" style="font-weight: bold;">Total</td>
			<td style="font-weight: bold; text-align: right;">
				<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalQty}'/>
			</td>
			<td></td>
			<td></td>
			<td style="font-weight: bold; text-align: right;">
				<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalAmount}'/>
			</td>
		</tr>
		<tr>
			<td colspan="8">${itemsConsumed.dataSize + ((itemsConsumed.currentPage - 1) *
				itemsConsumed.pageSetting.maxResult)}/${itemsConsumed.totalRecords}</td>
			<td style="text-align: right;"><c:if
					test="${itemsConsumed.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${itemsConsumed.lastPage }">
						<a href="#" id="pageIC-${page}" onclick="loadICPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${itemsConsumed.lastPage > 5}">
					<c:if test="${itemsConsumed.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="pageIC-${page}" onclick="loadICPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadICPage (6)" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${itemsConsumed.currentPage > 5}">
						<c:set var="modPage" value="${itemsConsumed.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(itemsConsumed.currentPage + (5 - modPage)) <= itemsConsumed.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${itemsConsumed.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(itemsConsumedcurrentPage + (5 - modPage)) >= itemsConsumed.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${itemsConsumed.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${itemsConsumed.currentPage}" />
								<c:set var="minPageSet" value="${itemsConsumed.currentPage - 4}" />
							</c:otherwise>
						</c:choose>

						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadICPage (${minPageSet - 1})"
								class="pageNumber"> << </a>
						</c:if>

						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="pageIC-${page}" onclick="loadICPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>

						<c:if test="${(maxPageSet) < itemsConsumed.lastPage }">
							<a href="#" onclick="loadICPage (${maxPageSet + 1})"
								class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>