<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Sub work order reference table form for work order
-->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblWOReference thead tr").find("th:last").css("border-right", "none");
	$("#tblWOReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblWOReference tbody").find("td").addClass("tdProperties");
	markCurrentPageNumber($("#hdnPageNumber").val());
});

function setRefWorkOrderId(checkbox) {
	if (refWorkOrderId == null) {
		refWorkOrderId = $(checkbox).attr("id");
	} else {
		if ($("input[type=checkbox]").length > 1)  {
			$(checkbox).prop("checked", false);
		}
		refWorkOrderId = $(checkbox).attr("id");
	}
	if ($("input[type=checkbox]:checked").length == 0) {
		refWorkOrderId = null;
	}
}

function loadPage(pageNumber) {
	$("#tblWOReference").load(getCommonParam()+"&pageNumber="+pageNumber);
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
<table style="margin-top: 20px;" id="tblWOReference" class="dataTable">
	<thead>
		<tr>
			<th width="5%">#</th>
			<th width="20%">WO No.</th>
			<th width="25%">Customer</th>
			<th width="10%">Target End Date</th>
			<th width="40%">Work Description</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="wo" items="${workOrders.data}">
			<tr>
				<td style="border-left: none;">
					<input type="checkbox"  id="${wo.id}" name="cbWorkOrder" class="cbWorkOrder"
						onclick="setRefWorkOrderId(this);"/>
				</td>
				<td>WO - ${wo.sequenceNumber}</td>
				<td>${wo.arCustomer.name}</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${wo.targetEndDate}"/></td>
				<td style="white-space: nowrap;">${wo.workDescription}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${workOrders.dataSize + ((workOrders.currentPage - 1)
				* workOrders.pageSetting.maxResult)}/${workOrders.totalRecords}
			</td>
			<td colspan="4" style="text-align: right;">
			<c:if test="${workOrders.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${workOrders.lastPage }">
					<a href="#container" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${workOrders.lastPage > 5}">
				<c:if test="${workOrders.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#container" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${workOrders.currentPage > 5}">
					<c:set var="modPage" value="${workOrders.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(workOrders.currentPage + (5 - modPage)) <= workOrders.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${workOrders.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(workOrders.currentPage + (5 - modPage)) >= workOrders.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${workOrders.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${workOrders.currentPage}" />
							<c:set var="minPageSet" value="${workOrders.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#container" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < workOrders.lastPage }">
						<a href="#container" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>