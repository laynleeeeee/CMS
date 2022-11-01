<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description	: PR reference form table
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
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
	$("#tblPrReference thead tr").find("th:last").css("border-right", "none");
	$("#tblPrReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblPrReference tbody").find("td").addClass("tdProperties");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "";
	if(!IS_INITIAL_LOAD){
		url = getCommonParam()+"&pageNumber="+pageNumber;
	} else {
		var companyId = $("#companyId").val();
		var status = $("#selectRFStatus").val();
		url = contextPath + "/gemmaPurchaseOrder/prReference/reload?companyId="+companyId
			+"&pageNumber="+pageNumber+"&status="+status;
	}
	$("#divPrFormRefTbl").load(url);
}

function populateRequests($checkbox, val) {
	if($($checkbox).is(":checked")) {
		slctdRequests.push(val);
	} else {
		slctdRequests.splice(slctdRequests.indexOf(val), 1);
	}
} 

</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table style="margin-top : 20px;" id="tblPrReference" class="dataTable">
		<thead>
			<tr>
				<th width="3%"></th>
				<th width="10%">Date</th>
				<th width="12%">PR No.</th>
				<th width="20%">Fleet</th>
				<th width="10%">Project</th>
				<th width="45%" style="border-right: none;">Remarks</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="pr" items="${purchaseRequisitions.data}">
				<tr>
					<td style="border-left: none;">
						<input id="pr-${pr.prId}" name="cbPr" class="cbPr" type="checkbox"
							onclick="populateRequests(this, '${pr.prObjId}');"/>
					</td>
					<td><fmt:formatDate pattern="MM/dd/yyyy" value="${pr.date}" /></td>
					<td>${pr.prNo}</td>
					<td>${pr.fleetCode}</td>
					<td>${pr.projectName}</td>
					<td>${pr.remarks}</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">${purchaseRequisitions.dataSize + ((purchaseRequisitions.currentPage - 1) *
					purchaseRequisitions.pageSetting.maxResult)}/${purchaseRequisitions.totalRecords}
				</td>
				<td colspan="4" style="text-align: right;">
				<c:if test="${purchaseRequisitions.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${purchaseRequisitions.lastPage }">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				<c:if test="${purchaseRequisitions.lastPage > 5}">
					<c:if test="${purchaseRequisitions.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#container" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#container" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>
					<c:if test="${purchaseRequisitions.currentPage > 5}">
						<c:set var="modPage" value="${purchaseRequisitions.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(purchaseRequisitions.currentPage + (5 - modPage)) <= purchaseRequisitions.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${purchaseRequisitions.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(purchaseRequisitions.currentPage + (5 - modPage)) >= purchaseRequisitions.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${purchaseRequisitions.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${purchaseRequisitions.currentPage}" />
								<c:set var="minPageSet" value="${purchaseRequisitions.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#container" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#container" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<c:if test="${(maxPageSet) < purchaseRequisitions.lastPage }">
							<a href="#container" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>