<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Jsp page for customer account table.
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
	$("#btnEditCustomerAcct").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/arCustomerAccount/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("customerAcctTable", url);
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
			<th>Customer Name</th>
			<th>Account Name</th>
			<th>Company</th>
			<th>Division</th>
			<th>Debit Account</th>
			<th>Terms</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(arCustomerAccts.pageSetting.maxResult * (arCustomerAccts.currentPage - 1)) + 1}" />
			<c:forEach var="arCustomerAcct" items="${arCustomerAccts.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
						<td>${index}</td>
						<td><input type="checkbox" id="${arCustomerAcct.id}" name="cbCustomerAcct"
							onclick="enableEditAndDeleteButton('cbCustomerAcct','btnEditCustomerAcct', null);"/></td>
						<td>${arCustomerAcct.arCustomer.name}</td>
						<td>${arCustomerAcct.name}</td>
						<td>${arCustomerAcct.company.name}</td>
						<td>${arCustomerAcct.defaultDebitAC.division.name}</td>
						<td>
							<c:if test="${arCustomerAcct.defaultDebitAC != null}">
								${arCustomerAcct.defaultDebitAC.account.accountName}
							</c:if>
						</td>
						<td>${arCustomerAcct.term.name}</td>
						<c:choose>
							<c:when test="${arCustomerAcct.active == true}"><td>Active</td></c:when>
							<c:otherwise><td>Inactive</td></c:otherwise>
						</c:choose>
					</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="8">${arCustomerAccts.dataSize + ((arCustomerAccts.currentPage - 1) *
				arCustomerAccts.pageSetting.maxResult)}/${arCustomerAccts.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${arCustomerAccts.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${arCustomerAccts.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${arCustomerAccts.lastPage > 5}">
				<c:if test="${arCustomerAccts.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${arCustomerAccts.currentPage > 5}">
					<c:set var="modPage" value="${arCustomerAccts.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(arCustomerAccts.currentPage + (5 - modPage)) <= arCustomerAccts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${arCustomerAccts.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(arCustomerAccts.currentPage + (5 - modPage)) >= arCustomerAccts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${arCustomerAccts.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${arCustomerAccts.currentPage}" />
							<c:set var="minPageSet" value="${arCustomerAccts.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < arCustomerAccts.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>