<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Jsp page for custodian account table.
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
	$("#btnEditCustodianAccount").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/custodianAccount/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("custodianAccountTable", url);
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
			<th>Custodian Name</th>
			<th>Company</th>
			<th>Division</th>
			<th>Custodian Account</th>
			<th>Terms</th>
			<th>Custodian Default Account</th>
			<th>Fund Default Account</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(custodianAccounts.pageSetting.maxResult * (custodianAccounts.currentPage - 1)) + 1}" />
			<c:forEach var="custodianAccount" items="${custodianAccounts.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
						<td>${index}</td>
						<td><input type="checkbox" id="${custodianAccount.id}" name="cbCustodianAccount"
							onclick="enableEditAndDeleteButton('cbCustodianAccount','btnEditCustodianAccount', null);"/></td>
						<td>${custodianAccount.custodianName}</td>
						<td>${custodianAccount.company.name}</td>
						<td>${custodianAccount.cdAccountCombination.division.name}</td>
						<td>${custodianAccount.custodianAccountName}</td>
						<td>${custodianAccount.term.name}</td>
						<td>${custodianAccount.cdAccountCombination.division.name} - ${custodianAccount.cdAccountCombination.account.accountName}</td>
						<td>${custodianAccount.fdAccountCombination.division.name} - ${custodianAccount.fdAccountCombination.account.accountName}</td>
						<c:choose>
							<c:when test="${custodianAccount.active == true}"><td>Active</td></c:when>
							<c:otherwise><td>Inactive</td></c:otherwise>
						</c:choose>
					</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="8">${custodianAccounts.dataSize + ((custodianAccounts.currentPage - 1) *
				custodianAccounts.pageSetting.maxResult)}/${custodianAccounts.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${custodianAccounts.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${custodianAccounts.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${custodianAccounts.lastPage > 5}">
				<c:if test="${custodianAccounts.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${custodianAccounts.currentPage > 5}">
					<c:set var="modPage" value="${custodianAccounts.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(custodianAccounts.currentPage + (5 - modPage)) <= custodianAccounts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${custodianAccounts.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(custodianAccounts.currentPage + (5 - modPage)) >= custodianAccounts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${custodianAccounts.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${custodianAccounts.currentPage}" />
							<c:set var="minPageSet" value="${custodianAccounts.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < custodianAccounts.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>