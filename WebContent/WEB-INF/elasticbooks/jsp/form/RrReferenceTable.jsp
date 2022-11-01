<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description: Receiving report reference table form JSP page -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblRrReference thead tr").find("th:last").css("border-right", "none");
	$("#tblRrReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblRrReference tbody").find("td").addClass("tdProperties");
	markCurrentPageNumber($("#hdnPageNumber").val());
});

function setRrReferenceId(checkbox) {
	if (rrReferenceId == null) {
		rrReferenceId = $(checkbox).attr("id");
	} else {
		if ($("input[type=checkbox]").length > 1)  {
			$(checkbox).prop("checked", false);
		}
		rrReferenceId = $(checkbox).attr("id");
	}
	if ($("input[type=checkbox]:checked").length == 0) {
		rrReferenceId = null;
	}
}

function loadPage(pageNumber) {
	$("#tblRrReference").load(getCommonParam()+"&pageNumber="+pageNumber);
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
<table style="margin-top: 20px;" id="tblRrReference" class="dataTable">
	<thead>
		<tr>
			<th width="2%"></th>
			<th width="15%">Date</th>
			<th width="18%">RR No.</th>
			<th width="15%">BMS No.</th>
			<th width="25%">Supplier</th>
			<th width="25%">Supplier Account</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="rr" items="${receivingReports.data}">
			<tr>
				<td style="border-left: none;">
					<input type="checkbox" id="${rr.invoiceId}" name=cbRrNumber class="cbRrNumber"
						onclick="setRrReferenceId(this);"/>
				</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${rr.date}" /></td>
				<td>${rr.referenceNumber}</td>
				<td>${rr.bmsNumber}</td>
				<td>${rr.supplierName}</td>
				<td>${rr.supplierAcctName}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${receivingReports.dataSize + ((receivingReports.currentPage - 1)
				* receivingReports.pageSetting.maxResult)}/${receivingReports.totalRecords}
			</td>
			<td colspan="4" style="text-align: right;">
			<c:if test="${receivingReports.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${receivingReports.lastPage }">
					<a href="#container" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${receivingReports.lastPage > 5}">
				<c:if test="${receivingReports.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#container" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${receivingReports.currentPage > 5}">
					<c:set var="modPage" value="${receivingReports.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(receivingReports.currentPage + (5 - modPage)) <= receivingReports.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${receivingReports.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(receivingReports.currentPage + (5 - modPage)) >= receivingReports.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${receivingReports.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${receivingReports.currentPage}" />
							<c:set var="minPageSet" value="${receivingReports.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#container" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < receivingReports.lastPage }">
						<a href="#container" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>