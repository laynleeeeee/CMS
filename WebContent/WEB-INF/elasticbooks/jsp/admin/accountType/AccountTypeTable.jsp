<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Account type table
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
	$(document).ready (function () {
		$("#btnEditAccountType").attr("disabled", "disabled");
		markCurrentPageNumber ($("#hdnPageNumber").val());
	});

	function loadPage (pageNumber) {
		var accountTypeName = processSearchName($("#txtAccountTypeName").val());
		var normalBalanceId = $("#selectNormalBalances").val();
		var targetUrl = "/admin/accountTypes?accountTypeName="+accountTypeName+
				"&normalBalanceId="+normalBalanceId+"&pageNumber="+pageNumber;
		goToPage ("divAccountTypeTable", targetUrl);
	}
</script>
<span id="messageSpan" class="message"> ${message} </span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="dataTable" class="dataTable">
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="1%"><input type="checkbox" id="checkAll"
				onclick="checkUncheckedAll('cbAccountType', this,'btnEditAccountType', null)"/></th>
			<th width="62%">Name</th>
			<th width="10%">Normal Balance</th>
			<th width="20%">Contra Account</th>
			<th width="5%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="acctType" items="${accountTypes.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${status.index + 1}</td>
				<td>
					<input type="checkbox" id="${acctType.id}" name="cbAccountType"
						onclick="enableEditAndDeleteButton('cbAccountType','btnEditAccountType', null);"/>
				</td>
				<td>${acctType.name}</td>
				<td>${acctType.normalBalance.name}</td>
				<td>
					<c:choose>
						<c:when test="${acctType.contraAccount eq true}">Yes</c:when>
						<c:otherwise>No</c:otherwise>
					</c:choose>
				</td>

				<td>
					<c:choose>
						<c:when test="${acctType.active eq true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5">${accountTypes.dataSize +
				((accountTypes.currentPage - 1) *
				accountTypes.pageSetting.maxResult)}/${accountTypes.totalRecords}</td>
			<td style="text-align: right;"><c:if
					test="${accountTypes.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${accountTypes.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${accountTypes.lastPage > 5}">
					<c:if test="${accountTypes.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${accountTypes.currentPage > 5}">
						<c:set var="modPage" value="${accountTypes.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(accountTypes.currentPage + (5 - modPage)) <= accountTypes.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${accountTypes.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(accountTypes.currentPage + (5 - modPage)) >= accountTypes.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${accountTypes.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${accountTypes.currentPage}" />
								<c:set var="minPageSet" value="${accountTypes.currentPage - 4}" />
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

						<c:if test="${(maxPageSet) < accountTypes.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})"
								class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
		</tr>
	</tfoot>
</table>