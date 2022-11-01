<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Petty cash voucher reference table form for petty cash voucher liquidation form
-->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblPCVReference thead tr").find("th:last").css("border-right", "none");
	$("#tblPCVReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblPCVReference tbody").find("td").addClass("tdProperties");
	markCurrentPageNumber($("#hdnPageNumber").val());
});

function setPCVRefId(checkbox) {
	if (pcvReferenceId == null) {
		pcvReferenceId = $(checkbox).attr("id");
	} else {
		if ($("input[type=checkbox]").length > 1)  {
			$(checkbox).prop("checked", false);
		}
		pcvReferenceId = $(checkbox).attr("id");
	}
	if ($("input[type=checkbox]:checked").length == 0) {
		pcvReferenceId = null;
	}
}

function loadPage(pageNumber) {
	$("#divPCVRefTable").load(getCommonParam()+"&pageNumber="+pageNumber);
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
<table style="margin-top: 20px;" id="tblPCVReference" class="dataTable">
	<thead>
		<tr>
			<th width="5%">#</th>
			<th width="15%">Date</th>
			<th width="15%">Reference No.</th>
			<th width="15%">Custodian</th>
			<th width="25%">Requestor</th>
			<th width="30%" style="border-right: none;">Description</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="pcv" items="${pcvs.data}">
			<tr>
				<td style="border-left: none;">
					<input type="checkbox"  id="${pcv.id}" name="cbPettyCashVoucher" class="cbPettyCashVoucher"
						onclick="setPCVRefId(this);"/>
				</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${pcv.pcvDate}" /></td>
				<td>${pcv.sequenceNo}</td>
				<td>${pcv.userCustodian.custodianAccount.custodianName}</td>
				<td>${pcv.requestor}</td>
				<td>${pcv.description}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${pcvs.dataSize + ((pcvs.currentPage - 1)
				* pcvs.pageSetting.maxResult)}/${pcvs.totalRecords}
			</td>
			<td colspan="4" style="text-align: right;">
			<c:if test="${pcvs.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${pcvs.lastPage }">
					<a href="#container" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${pcvs.lastPage > 5}">
				<c:if test="${pcvs.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#container" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${pcvs.currentPage > 5}">
					<c:set var="modPage" value="${pcvs.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(pcvs.currentPage + (5 - modPage)) <= pcvs.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${pcvs.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(pcvs.currentPage + (5 - modPage)) >= pcvs.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${pcvs.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${pcvs.currentPage}" />
							<c:set var="minPageSet" value="${pcvs.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#container" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < pcvs.lastPage }">
						<a href="#container" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>