<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description: Supplier advance payment - purchase order reference table form JSP page -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblPOReference thead tr").find("th:last").css("border-right", "none");
	$("#tblPOReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblPOReference tbody").find("td").addClass("tdProperties");
	markCurrentPageNumber($("#hdnPageNumber").val());
});

function setRefPurchaseOrderId(checkbox) {
	if (purchaseOrderRefId == null) {
		purchaseOrderRefId = $(checkbox).attr("id");
	} else {
		if ($("input[type=checkbox]").length > 1)  {
			$(checkbox).prop("checked", false);
		}
		purchaseOrderRefId = $(checkbox).attr("id");
	}
	if ($("input[type=checkbox]:checked").length == 0) {
		purchaseOrderRefId = null;
	}
}

function loadPage(pageNumber) {
	$("#tblPOReference").load(getCommonParam()+"&pageNumber="+pageNumber);
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
<table style="margin-top: 20px;" id="tblPOReference" class="dataTable">
	<thead>
		<tr>
			<th width="2%"></th>
			<th width="15%">Date</th>
			<th width="18%">Reference No.</th>
			<th width="15%">BMS No.</th>
			<th width="25%">Supplier</th>
			<th width="25%">Supplier Account</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="po" items="${rpurchaseOrders.data}">
			<tr>
				<td style="border-left: none;">
					<input type="checkbox" id="${po.purchaseOrderId}" name=cbPoNumber class="cbPoNumber"
						onclick="setRefPurchaseOrderId(this);"/>
				</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${po.poDate}" /></td>
				<td>${po.poNumber}</td>
				<td>${po.bmsNumber}</td>
				<td>${po.supplierName}</td>
				<td>${po.supplierAcctName}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${rpurchaseOrders.dataSize + ((rpurchaseOrders.currentPage - 1)
				* rpurchaseOrders.pageSetting.maxResult)}/${rpurchaseOrders.totalRecords}
			</td>
			<td colspan="4" style="text-align: right;">
			<c:if test="${rpurchaseOrders.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${rpurchaseOrders.lastPage }">
					<a href="#container" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${rpurchaseOrders.lastPage > 5}">
				<c:if test="${rpurchaseOrders.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#container" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${rpurchaseOrders.currentPage > 5}">
					<c:set var="modPage" value="${rpurchaseOrders.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(rpurchaseOrders.currentPage + (5 - modPage)) <= rpurchaseOrders.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${rpurchaseOrders.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(rpurchaseOrders.currentPage + (5 - modPage)) >= rpurchaseOrders.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${rpurchaseOrders.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${rpurchaseOrders.currentPage}" />
							<c:set var="minPageSet" value="${rpurchaseOrders.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#container" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < rpurchaseOrders.lastPage }">
						<a href="#container" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>