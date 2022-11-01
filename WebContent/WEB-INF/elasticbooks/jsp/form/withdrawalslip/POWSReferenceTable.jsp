<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: CHL reference table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery/jquery-ui-1.10.3.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/accounting.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/css/jquery-ui-1.10.3.css" rel="stylesheet" type="text/css"  />
<style>
html {
  overflow-x: hidden;
  overflow-y: auto;
}

a.plus {
	text-decoration: none;
	font-weight: bold;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblRRReference thead tr").find("th:last").css("border-right", "none");
	$("#tblRRReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblRRReference tbody").find("td").addClass("tdProperties");

	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	$("#divWSefTable").load(getCommonParam()+"&pageNumber=" + pageNumber);
}

function populatePO(checkbox) {
	if(poId == null){
		poId = checkbox;
	}else{
		if ($("input[type=checkbox]").length > 1) 
			$(poId).prop("checked", false);
		poId = $(checkbox);
	}
	if ($("input[type=checkbox]:checked").length == 0)
		poId = null;
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table style="margin-top : 20px;" id="tblRRReference" class="dataTable">
		<thead>
			<tr>
				<th width="2%"></th>
				<th width="13%">Date</th>
				<th width="20%">PO No.</th>
				<th width="40%" style="border-right: none;">Supplier</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="po" items="${rPurchaseOrders.data}">
			<tr>
				<td width="3%" style="border-left: none;">
					<input id="${po.id}" name="cbPO"
							class="cbPO" type="checkbox" onclick="populatePO(this);"/>
					<input type="hidden" class="hdnCompanyId" value="${po.companyId}">
					<input type="hidden" class="hdnSupplierId" value="${po.supplierId}">
				</td>
				<td width="10%"><fmt:formatDate pattern="MM/dd/yyyy" value="${po.poDate}" /></td>
				<td width="10%">${po.poNumber}</td>
				<td>${po.supplierName}</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">${rPurchaseOrders.dataSize + ((rPurchaseOrders.currentPage - 1) *
					rPurchaseOrders.pageSetting.maxResult)}/${rPurchaseOrders.totalRecords}
				</td>
				<td colspan="3" style="text-align: right;">
				<c:if test="${rPurchaseOrders.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${rPurchaseOrders.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				<c:if test="${rPurchaseOrders.lastPage > 5}">
					<c:if test="${rPurchaseOrders.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>
					<c:if test="${rPurchaseOrders.currentPage > 5}">
						<c:set var="modPage" value="${rPurchaseOrders.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(rPurchaseOrders.currentPage + (5 - modPage)) <= rPurchaseOrders.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${rPurchaseOrders.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(rPurchaseOrders.currentPage + (5 - modPage)) >= rPurchaseOrders.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${rPurchaseOrders.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${rPurchaseOrders.currentPage}" />
								<c:set var="minPageSet" value="${rPurchaseOrders.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<c:if test="${(maxPageSet) < rPurchaseOrders.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>