<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Sales quotation reference table.
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
	$("#tblSQReference thead tr").find("th:last").css("border-right", "none");
	$("#tblSQReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblSQReference tbody").find("td").addClass("tdProperties");

	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	$("#divSQRefTable").load(getCommonParam()+"&pageNumber=" + pageNumber);
}

function populateSQ(checkbox) {
	if(sqId == null){
		sqId = checkbox;
	}else{
		if ($("input[type=checkbox]").length > 1) 
			$(sqId).prop("checked", false);
		sqId = $(checkbox);
	}
	if ($("input[type=checkbox]:checked").length == 0) {
		sqId = null;
	}
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table style="margin-top : 20px;" id="tblSQReference" class="dataTable">
		<thead>
			<tr>
				<th width="3%"></th>
				<th width="12%">Date</th>
				<th width="20%">Reference No.</th>
				<th width="25%" style="border-right: none;">Customer</th>
				<th width="25%" style="border-right: none;">Customer Account</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="sq" items="${salesQuotations.data}">
			<tr>
				<td style="border-left: none;">
					<input id="${sq.id}" name="cbSQ"
							class="cbSQ" type="checkbox" onclick="populateSQ(this);"/>
					<input type="hidden" class="hdnCompanyId" value="${sq.companyId}">
					<input type="hidden" class="hdnCustomerId" value="${sq.arCustomerId}">
					<input type="hidden" class="hdnCustomerAccountId" value="${sq.arCustomerAcctId}">
				</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${sq.date}" /></td>
				<td>${sq.sequenceNumber}</td>
				<td>${sq.arCustomer.name}</td>
				<td>${sq.arCustomerAccount.name}</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">${salesQuotations.dataSize + ((salesQuotations.currentPage - 1) *
					salesQuotations.pageSetting.maxResult)}/${salesQuotations.totalRecords}
				</td>
				<td colspan="4" style="text-align: right;">
				<c:if test="${salesQuotations.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${salesQuotations.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				<c:if test="${salesQuotations.lastPage > 5}">
					<c:if test="${salesQuotations.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>
					<c:if test="${salesQuotations.currentPage > 5}">
						<c:set var="modPage" value="${salesQuotations.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(salesQuotations.currentPage + (5 - modPage)) <= salesQuotations.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${salesQuotations.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(salesQuotations.currentPage + (5 - modPage)) >= salesQuotations.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${salesQuotations.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${salesQuotations.currentPage}" />
								<c:set var="minPageSet" value="${salesQuotations.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<c:if test="${(maxPageSet) < salesQuotations.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>