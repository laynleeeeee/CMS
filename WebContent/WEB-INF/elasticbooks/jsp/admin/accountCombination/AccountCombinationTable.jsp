<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Elasticbooks Account Combination Table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#btnEditCombination").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	doSearch ("accountCombinationTable", "/admin/accountCombinations"+getCommonParam()+"&pageNumber="+pageNumber);
}
</script>
</head>
<body>
<span id="messageSpan" class="message">${message}</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="dataTable" class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th width="1%">#</th>
			<th width="2%"></th>
			<th width="15%">Combination Number</th>
			<th width="25%">Company</th>
			<th width="10%">Division</th>
			<th width="40%">Account Name</th>
			<th width="7%">Status</th>
		</tr>
	</thead>
	<tbody class="tableFont">
			<c:forEach var="accountCombination" items="${accountCombinations.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${status.index +1}</td>
				<td><input type="checkbox" id="${accountCombination.id}" name="cbCombination" 
						style="text-align: center; vertical-align: middle;"onclick="enableEditAndDeleteButton
						('cbCombination', 'btnEditCombination','btnDeleteCombination');" /></td>
				<td>${accountCombination.company.companyNumber}-${accountCombination.division.number}-${accountCombination.account.number}</td>
				<td>${accountCombination.company.name}</td>
				<td>${accountCombination.division.name}</td>
				<td>${accountCombination.account.accountName}</td>
				<td>
					<c:choose>
						<c:when test="${accountCombination.active eq true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot class="nav">
		<tr>
				<td colspan="5">${accountCombinations.dataSize +
					((accountCombinations.currentPage - 1) *
					accountCombinations.pageSetting.maxResult)}/${accountCombinations.totalRecords}
				</td>
					<td colspan="5" style="text-align: right;"><c:if
						test="${accountCombinations.lastPage <= 5}">
						<c:forEach var="page" begin="1"
							end="${accountCombinations.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${accountCombinations.lastPage > 5}">
						<c:if test="${accountCombinations.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${accountCombinations.currentPage > 5}">
							<c:set var="modPage"
								value="${accountCombinations.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(accountCombinations.currentPage + (5 - modPage)) <= accountCombinations.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${accountCombinations.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(accountCombinations.currentPage + (5 - modPage)) >= accountCombinations.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${accountCombinations.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet"
										value="${accountCombinations.currentPage}" />
									<c:set var="minPageSet"
										value="${accountCombinations.currentPage - 4}" />
								</c:otherwise>
							</c:choose>
							<c:if test="${minPageSet > 5}">
								<a href="#" onclick="loadPage (${minPageSet - 1})"
									class="pageNumber"> << </a>
							</c:if>

							<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>

							<c:if test="${(maxPageSet) < accountCombinations.lastPage }">
								<a href="#" onclick="loadPage (${maxPageSet + 1})"
									class="pageNumber"> >> </a>
							</c:if>
						</c:if>
				</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>