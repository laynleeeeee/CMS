<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Account sale reference table.
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
	$("#tblASReference thead tr").find("th:last").css("border-right", "none");
	$("#tblASReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblASReference tbody").find("td").addClass("tdProperties");

	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	$("#divASRefTable").load(getCommonParam()+"&pageNumber=" + pageNumber);
}

function populateAS(checkbox) {
	if(asId == null){
		asId = checkbox;
	}else{
		if ($("input[type=checkbox]").length > 1) 
			$(asId).prop("checked", false);
		asId = $(checkbox);
	}
	if ($("input[type=checkbox]:checked").length == 0)
		asId = null;
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table style="margin-top : 20px;" id="tblASReference" class="dataTable">
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
			<c:forEach var="as" items="${accountSales.data}">
			<tr>
				<td style="border-left: none;">
					<input id="${as.id}" name="cbAS"
							class="cbAS" type="checkbox" onclick="populateAS(this);"/>
					<input type="hidden" class="hdnCompanyId" value="${as.companyId}">
					<input type="hidden" class="hdnCustomerId" value="${as.customerId}">
					<input type="hidden" class="hdnCustomerAccountId" value="${as.customerAcctId}">
				</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${as.transactionDate}" /></td>
				<td>${as.transactionNumber}</td>
				<td>${as.arCustomer.name}</td>
				<td>${as.arCustomerAccount.name}</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">${accountSales.dataSize + ((accountSales.currentPage - 1) *
					accountSales.pageSetting.maxResult)}/${accountSales.totalRecords}
				</td>
				<td colspan="4" style="text-align: right;">
				<c:if test="${accountSales.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${accountSales.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				<c:if test="${accountSales.lastPage > 5}">
					<c:if test="${accountSales.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>
					<c:if test="${accountSales.currentPage > 5}">
						<c:set var="modPage" value="${accountSales.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(accountSales.currentPage + (5 - modPage)) <= accountSales.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${accountSales.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(accountSales.currentPage + (5 - modPage)) >= accountSales.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${accountSales.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${accountSales.currentPage}" />
								<c:set var="minPageSet" value="${accountSales.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<c:if test="${(maxPageSet) < accountSales.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>