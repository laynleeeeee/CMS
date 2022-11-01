<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Sales order reference table form for waybill form
-->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblSOReference thead tr").find("th:last").css("border-right", "none");
	$("#tblSOReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblSOReference tbody").find("td").addClass("tdProperties");
	markCurrentPageNumber($("#hdnPageNumber").val());
});

function setSORefId(checkbox) {
	if (soRefId == null) {
		soRefId = $(checkbox).attr("id");
	} else {
		if ($("input[type=checkbox]").length > 1)  {
			$(checkbox).prop("checked", false);
		}
		soRefId = $(checkbox).attr("id");
	}
	if ($("input[type=checkbox]:checked").length == 0) {
		soRefId = null;
	}
}

function loadPage(pageNumber) {
	$("#divSORefTable").load(getCommonParam()+"&pageNumber="+pageNumber);
}
</script>
<style type="text/css">
html {
	overflow-x: hidden;
	overflow-y: auto;
}

a.plus {
	text-decoration: none;
	font-weight: bold;
}
</style>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}"/>
<table style="margin-top: 20px;" id="tblSOReference" class="dataTable">
	<thead>
		<tr>
			<th width="5%">#</th>
			<th width="20%">SO No.</th>
			<th width="30%">Customer</th>
			<th width="45%" style="border-right: none;">Ship To</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="so" items="${salesOrders.data}">
			<tr>
				<td style="border-left: none;">
					<input type="checkbox"  id="${so.id}" name="cbSalesOrder" class="cbSalesOrder"
						onclick="setSORefId(this);"/>
				</td>
				<td>SO - ${so.sequenceNumber}</td>
				<td>${so.arCustomer.name}</td>
				<td>${so.shipTo}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">${salesOrders.dataSize + ((salesOrders.currentPage - 1)
				* salesOrders.pageSetting.maxResult)}/${salesOrders.totalRecords}
			</td>
			<td style="text-align: right;">
			<c:if test="${salesOrders.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${salesOrders.lastPage }">
					<a href="#container" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${salesOrders.lastPage > 5}">
				<c:if test="${salesOrders.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#container" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${salesOrders.currentPage > 5}">
					<c:set var="modPage" value="${salesOrders.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(salesOrders.currentPage + (5 - modPage)) <= salesOrders.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${salesOrders.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(salesOrders.currentPage + (5 - modPage)) >= salesOrders.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${salesOrders.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${salesOrders.currentPage}" />
							<c:set var="minPageSet" value="${salesOrders.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#container" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < salesOrders.lastPage }">
						<a href="#container" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>