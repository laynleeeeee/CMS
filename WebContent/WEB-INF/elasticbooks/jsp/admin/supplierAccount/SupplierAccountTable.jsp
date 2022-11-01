<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Displays the list of Supplier Accounts in table format.
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
	$("#btnEditSupplierAcct").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/supplierAccount/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("supplierAcctTable", url);
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
			<th>Supplier Name</th>
			<th>Company</th>
			<th>Account Name</th>
			<th>Terms</th>
			<th>Debit Account</th>
			<th>Credit Account</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(supplierAccounts.pageSetting.maxResult * (supplierAccounts.currentPage - 1)) + 1}" />
			<c:forEach var="supplierAccount" items="${supplierAccounts.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
						<td>${index}</td>
						<td><input type="checkbox" id="${supplierAccount.id}" name="cbSupplierAcct"
							onclick="enableEditAndDeleteButton('cbSupplierAcct','btnEditSupplierAcct', null);"/></td>
						<td>${supplierAccount.supplier.name}</td>
						<td>${supplierAccount.company.name}</td>
						<td>${supplierAccount.name}</td>
						<td>${supplierAccount.term.name}</td>
						<td><c:if test="${supplierAccount.defaultDebitAC != null}">
							${supplierAccount.defaultDebitAC.division.name} - ${supplierAccount.defaultDebitAC.account.accountName}
						</c:if></td>
						<td><c:if test="${supplierAccount.defaultCreditAC != null}">
							${supplierAccount.defaultCreditAC.division.name} - ${supplierAccount.defaultCreditAC.account.accountName}
						</c:if></td>
						<c:choose>
							<c:when test="${supplierAccount.active == true}"><td>Active</td></c:when>
							<c:otherwise><td>Inactive</td></c:otherwise>
						</c:choose>
					</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="8">${supplierAccounts.dataSize + ((supplierAccounts.currentPage - 1) *
				supplierAccounts.pageSetting.maxResult)}/${supplierAccounts.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${supplierAccounts.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${supplierAccounts.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${supplierAccounts.lastPage > 5}">
				<c:if test="${supplierAccounts.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${supplierAccounts.currentPage > 5}">
					<c:set var="modPage" value="${supplierAccounts.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(supplierAccounts.currentPage + (5 - modPage)) <= supplierAccounts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${supplierAccounts.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(supplierAccounts.currentPage + (5 - modPage)) >= supplierAccounts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${supplierAccounts.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${supplierAccounts.currentPage}" />
							<c:set var="minPageSet" value="${supplierAccounts.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < supplierAccounts.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>