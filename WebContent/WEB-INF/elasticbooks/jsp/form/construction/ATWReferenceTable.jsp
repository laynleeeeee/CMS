<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Authority to withdraw reference table form for delivery receipt form
-->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblATWReference thead tr").find("th:last").css("border-right", "none");
	$("#tblATWReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblATWReference tbody").find("td").addClass("tdProperties");
	markCurrentPageNumber($("#hdnPageNumber").val());
});

function setATWRefId(checkbox) {
	if (atwRefId == null) {
		atwRefId = $(checkbox).attr("id");
	} else {
		if ($("input[type=checkbox]").length > 1)  {
			$(checkbox).prop("checked", false);
		}
		atwRefId = $(checkbox).attr("id");
	}
	if ($("input[type=checkbox]:checked").length == 0) {
		atwRefId = null;
	}
}

function loadPage(pageNumber) {
	$("#divATWRefTable").load(getCommonParam()+"&pageNumber="+pageNumber);
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
<table style="margin-top: 20px;" id="tblATWReference" class="dataTable">
	<thead>
		<tr>
			<th width="5%">#</th>
			<th width="20%">ATW No.</th>
			<th width="30%">Customer</th>
			<th width="45%" style="border-right: none;">Remarks</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="atw" items="${authorityToWithdraws.data}">
			<tr>
				<td style="border-left: none;">
					<input type="checkbox"  id="${atw.id}" name="cbAtw" class="cbAtw"
						onclick="setATWRefId(this);"/>
				</td>
				<td>ATW - ${atw.sequenceNumber}</td>
				<td>${atw.arCustomer.name}</td>
				<td>${atw.remarks}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${authorityToWithdraws.dataSize + ((authorityToWithdraws.currentPage - 1)
				* authorityToWithdraws.pageSetting.maxResult)}/${authorityToWithdraws.totalRecords}
			</td>
			<td colspan="4" style="text-align: right;">
			<c:if test="${authorityToWithdraws.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${authorityToWithdraws.lastPage }">
					<a href="#container" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${authorityToWithdraws.lastPage > 5}">
				<c:if test="${authorityToWithdraws.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#container" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${authorityToWithdraws.currentPage > 5}">
					<c:set var="modPage" value="${authorityToWithdraws.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(authorityToWithdraws.currentPage + (5 - modPage)) <= authorityToWithdraws.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${authorityToWithdraws.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(authorityToWithdraws.currentPage + (5 - modPage)) >= authorityToWithdraws.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${authorityToWithdraws.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${authorityToWithdraws.currentPage}" />
							<c:set var="minPageSet" value="${authorityToWithdraws.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#container" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < authorityToWithdraws.lastPage }">
						<a href="#container" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>