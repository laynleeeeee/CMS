<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Bank Account table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#btnEditBankAccount").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var targetUrl = "/admin/bankAccounts"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage ("divBankAccountTable", targetUrl);
}
</script>
</head>
<body>
<span id="spanBankAccountsMessage" class="message"></span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table id="dataTable" class="dataTable">
		<thead>
			<tr class="alignLeft">
				<th width="2%">#</th>
				<th width="1%"></th>
				<th width="18%">Company</th>
				<th width="18%">Bank Name</th>
				<th width="18%">Bank Account Name</th>
				<th width="18%">Bank Account No.</th>
				<th width="18%">CIB Account</th>
				<th width="7%">Status</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="index" value="${(bankAccounts.pageSetting.maxResult * (bankAccounts.currentPage - 1)) + 1}" />
				<c:forEach var="ba" items="${bankAccounts.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'"
						onMouseOut="this.className='${trClass}'" class="${trClass}">
						<td>${index}</td>
						<td><input type="checkbox" id="${ba.id}" name="cbBankAccount"
							onclick="enableEditAndDeleteButton('cbBankAccount','btnEditBankAccount', null);" />
						</td>
						<td>${ba.company.name}</td>
						<td>${ba.bank.name}</td>
						<td>${ba.name}</td>
						<td>${ba.bankAccountNo}</td>
						<td> <c:if test="${ba.cashInBank != null}">
								${ba.cashInBank.division.name} - ${ba.cashInBank.account.accountName}
						</c:if></td>
						<td>
							<c:choose>
								<c:when test="${ba.active eq true}">Active</c:when>
								<c:otherwise>Inactive</c:otherwise>
							</c:choose>
						</td>
					</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
		</tbody>
		<tfoot class="nav">
			<tr>
				<td colspan="4">${bankAccounts.dataSize +
					((bankAccounts.currentPage - 1) *
					bankAccounts.pageSetting.maxResult)}/${bankAccounts.totalRecords}
				</td>
				<td colspan="5" style="text-align: right;"><c:if
						test="${bankAccounts.lastPage <= 5}">
						<c:forEach var="page" begin="1" end="${bankAccounts.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${bankAccounts.lastPage > 5}">
						<c:if test="${bankAccounts.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${bankAccounts.currentPage > 5}">
							<c:set var="modPage" value="${bankAccounts.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(bankAccounts.currentPage + (5 - modPage)) <= bankAccounts.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${bankAccounts.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(bankAccounts.currentPage + (5 - modPage)) >= bankAccounts.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${bankAccounts.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${bankAccounts.currentPage}" />
									<c:set var="minPageSet" value="${bankAccounts.currentPage - 4}" />
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

							<c:if test="${(maxPageSet) < bankAccounts.lastPage }">
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