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

.header {
	font-style: normal;
	font-weight:bold;
	font-family: sans-serif;
	font-size: x-small;
	background-color: #C0C0C0;
	border: 1px solid black;
	padding: 5px;
}

.tdBorder {
	border: 1px solid black;
}

</style>
<script type="text/javascript">
$(document).ready (function () {
	$("#hdnToolsPageNumber").val("${pageNumber}");
	markCurrentToolPageNumber ("${pageNumber}");
});

function loadToolPage (pageNumber) {
	generateTools(pageNumber);
}

function markCurrentToolPageNumber(pageNumber){
	$("a#pageTool-" + pageNumber).css("font-size", "16px");
	$("a#pageTool-" + pageNumber).css("color", "red");
}
</script>
<title>Fleet Attributable Cost</title>
</head>
<body>
<input type="hidden" id="hdnToolsPageNumber" value="${pageNumber}" />
<table id="tblFleetToolCondition" >
	<thead>
		<tr>
			<th width="1%" class="header">#</th>
			<th width="6%" class="header">Date Purchased</th>
			<th width="14%" class="header">Stock Code</th>
			<th width="25%" class="header">Description</th>
			<th width="10%" class="header">Cost (Php)</th>
			<th width="27%" class="header">Condition</th>
			<th width="2%" class="header">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="t" items="${fleetToolDto.fleetToolConditions.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td class='tdProperties refObjectIds'>
					<span class="tblLabelNumeric">${status.index + 1}</span>
					<input type="hidden" class="hdnEbObjects" value="${t.fleetItemConsumedDto.ebObjectId}"/>
					<input type="hidden" class="hdnItemId" value="${t.fleetItemConsumedDto.itemId}"/>
				</td>
				<td class='tdProperties'>
					<span class="tblLabelText">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${t.fleetItemConsumedDto.date}"/>
					</span>
				</td>
				<td class='tdProperties stockCode'>${t.fleetItemConsumedDto.stockCode}</td>
				<td class='tdProperties'>${t.fleetItemConsumedDto.description}</td>
				<td class='tdProperties' style="text-align: right;">
					<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${t.fleetItemConsumedDto.amount}'/>
					<c:set var="totalAmount" value="${totalAmount + t.fleetItemConsumedDto.amount}" />
				</td>
				<td class='tdProperties conditions'>
					<input  class="tblInputText" value="${t.toolCondition}" />
				</td>
				<td class='tdProperties statuses' style="text-align: center;">
					<input type="checkbox" class="cbStatuses" <c:if test="${t.status}">checked="checked"</c:if>/>
				</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4" style="font-weight: bold;">Total</td>
			<td style="font-weight: bold; text-align: right;">
				<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${totalAmount}'/>
			</td>
		</tr>
		<tr>
			<td colspan="5">${fleetToolDto.fleetToolConditions.dataSize + ((fleetToolDto.fleetToolConditions.currentPage - 1) *
				fleetToolDto.fleetToolConditions.pageSetting.maxResult)}/${fleetToolDto.fleetToolConditions.totalRecords}</td>
			<td colspan="2" style="text-align: right;">
				<c:if test="${fleetToolDto.fleetToolConditions.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${fleetToolDto.fleetToolConditions.lastPage}">
						<a href="#" id="pageTool-${page}" onclick="loadToolPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				<c:if test="${fleetToolDto.fleetToolConditions.lastPage > 5}">
					<c:if test="${fleetToolDto.fleetToolConditions.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="pageTool-${page}" onclick="loadToolPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadToolPage (6)" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${fleetToolDto.fleetToolConditions.currentPage > 5}">
						<c:set var="modPage" value="${fleetToolDto.fleetToolConditions.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(fleetToolDto.fleetToolConditions.currentPage + (5 - modPage)) <= fleetToolDto.fleetToolConditions.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${fleetToolDto.fleetToolConditions.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(itemsConsumedcurrentPage + (5 - modPage)) >= fleetToolDto.fleetToolConditions.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${fleetToolDto.fleetToolConditions.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${fleetToolDto.fleetToolConditions.currentPage}" />
								<c:set var="minPageSet" value="${fleetToolDto.fleetToolConditions.currentPage - 4}" />
							</c:otherwise>
						</c:choose>

						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadToolPage (${minPageSet - 1})"
								class="pageNumber"> << </a>
						</c:if>

						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="pageTool-${page}" onclick="loadToolPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>

						<c:if test="${(maxPageSet) < fleetToolDto.fleetToolConditions.lastPage }">
							<a href="#" onclick="loadToolPage (${maxPageSet + 1})"
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