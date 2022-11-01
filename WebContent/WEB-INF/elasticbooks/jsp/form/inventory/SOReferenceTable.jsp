<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Cash sale reference table.
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
	$("#tblSOReference thead tr").find("th:last").css("border-right", "none");
	$("#tblSOReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblSOReference tbody").find("td").addClass("tdProperties");

	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	$("#divSORefTable").load(getCommonParam()+"&pageNumber=" + pageNumber);
}

function populateSO(checkbox) {
	if(soId == null){
		soId = checkbox;
	}else{
		if ($("input[type=checkbox]").length > 1) 
			$(soId).prop("checked", false);
		soId = $(checkbox);
	}
	if ($("input[type=checkbox]:checked").length == 0)
		soId = null;
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table style="margin-top : 20px;" id="tblSOReference" class="dataTable">
		<thead>
			<tr>
				<th width="3%"></th>
				<th width="25%">SO No.</th>
				<th width="12%">Date</th>
				<th width="12%">Due Date</th>
				<th width="20%" style="border-right: none;">Customer</th>
				<th width="20%" style="border-right: none;">Customer Account</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="artTransaction" items="${artTransactions.data}">
			<tr>
				<td style="border-left: none;">
					<input id="${artTransaction.id}" name="cbCS"
							class="cbCS" type="checkbox" onclick="populateSO(this);"/>
					<input type="hidden" class="hdnCompanyId" value="${artTransaction.companyId}">
					<input type="hidden" class="hdnCustomerId" value="${artTransaction.customerId}">
					<input type="hidden" class="hdnCustomerAccountId" value="${artTransaction.customerAcctId}">
				</td>
				<td>${artTransaction.transactionNumber}</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${artTransaction.transactionDate}" /></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${artTransaction.dueDate}" /></td>
				<td>${artTransaction.arCustomer.name}</td>
				<td>${artTransaction.arCustomerAccount.name}</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">${artTransactions.dataSize + ((artTransactions.currentPage - 1) *
					artTransactions.pageSetting.maxResult)}/${artTransactions.totalRecords}
				</td>
				<td colspan="4" style="text-align: right;">
				<c:if test="${artTransactions.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${artTransactions.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				<c:if test="${artTransactions.lastPage > 5}">
					<c:if test="${artTransactions.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>
					<c:if test="${artTransactions.currentPage > 5}">
						<c:set var="modPage" value="${artTransactions.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(artTransactions.currentPage + (5 - modPage)) <= artTransactions.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${artTransactions.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(artTransactions.currentPage + (5 - modPage)) >= artTransactions.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${artTransactions.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${artTransactions.currentPage}" />
								<c:set var="minPageSet" value="${artTransactions.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<c:if test="${(maxPageSet) < artTransactions.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>