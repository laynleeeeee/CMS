<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Loan proceeds reference table.
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

function setSalesOrderRefId(checkbox) {
	if (lpRefId == null) {
		lpRefId = $(checkbox).attr("id");
	} else {
		if ($("input[type=checkbox]").length > 1)  {
			$(checkbox).prop("checked", false);
		}
		lpRefId = $(checkbox).attr("id");
	}
	if ($("input[type=checkbox]:checked").length == 0) {
		lpRefId = null;
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
			<th width="15%">Date</th>
			<th width="15%">LP No.</th>
			<th width="30%">Lender</th>
			<th width="25%">Lender Account</th>
			<th width="15%" style="border-right: none;">Clearing Date</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="lp" items="${lps.data}">
			<tr>
				<td style="border-left: none;">
					<input type="checkbox"  id="${lp.loanProceedsId}" name="cbSalesOrder" class="cbSalesOrder"
						onclick="setSalesOrderRefId(this);"/>
				</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${lp.date}" /></td>
				<td>${lp.sequenceNumber}</td>
				<td>${lp.supplierName}</td>
				<td>${lp.supplierAccountName}</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${lp.dateCleared}" /></td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${lps.dataSize + ((lps.currentPage - 1)
				* lps.pageSetting.maxResult)}/${lps.totalRecords}
			</td>
			<td colspan="4" style="text-align: right;">
			<c:if test="${lps.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${lps.lastPage }">
					<a href="#container" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${lps.lastPage > 5}">
				<c:if test="${lps.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#container" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${lps.currentPage > 5}">
					<c:set var="modPage" value="${lps.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(lps.currentPage + (5 - modPage)) <= lps.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${lps.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(lps.currentPage + (5 - modPage)) >= lps.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${lps.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${lps.currentPage}" />
							<c:set var="minPageSet" value="${lps.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#container" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < lps.lastPage }">
						<a href="#container" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>