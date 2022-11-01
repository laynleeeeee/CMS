<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Receipt method table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditReceiptMethod").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/receiptMethods/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("receiptMethodTable", url);
}
</script>
</head>
<body>
<span id="messageSpan" class="message"> ${message}</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th>#</th>
			<th></th>
			<th>Company</th>
			<th>Name</th>
			<th>Bank Account</th>
			<th>Debit Account</th>
			<th>Credit Account</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
	<c:set var="index" value="${(receiptMethods.pageSetting.maxResult * (receiptMethods.currentPage - 1)) + 1}" />
		<c:forEach var="receiptMethod" items="${receiptMethods.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${receiptMethod.id}" name="cbReceiptMethod"
						onclick="enableEditAndDeleteButton('cbReceiptMethod','btnEditReceiptMethod', null);"/></td>
					<td>${receiptMethod.company.name}</td>
					<td>${receiptMethod.name}</td>
					<td>${receiptMethod.bankAccount.name}</td>
					<td><c:if test="${receiptMethod.debitAcctCombination != null}">
						${receiptMethod.debitAcctCombination.division.name} - ${receiptMethod.debitAcctCombination.account.accountName}
					</c:if></td>
					<td><c:if test="${receiptMethod.creditAcctCombination != null}">
						${receiptMethod.creditAcctCombination.division.name} - ${receiptMethod.creditAcctCombination.account.accountName}
					</c:if></td>
					<c:choose>
						<c:when test="${receiptMethod.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="7">${receiptMethods.dataSize + ((receiptMethods.currentPage - 1) *
				receiptMethods.pageSetting.maxResult)}/${receiptMethods.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${receiptMethods.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${receiptMethods.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${receiptMethods.lastPage > 5}">
				<c:if test="${receiptMethods.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${receiptMethods.currentPage > 5}">
					<c:set var="modPage" value="${receiptMethods.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(receiptMethods.currentPage + (5 - modPage)) <= receiptMethods.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${receiptMethods.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(receiptMethods.currentPage + (5 - modPage)) >= receiptMethods.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${receiptMethods.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${receiptMethods.currentPage}" />
							<c:set var="minPageSet" value="${receiptMethods.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < receiptMethods.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>