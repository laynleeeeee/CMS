<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
        <%@ include file="../../include.jsp" %>
<!--

	Description: User Company Table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<style>
.tdRowLines {
	border-top: 1px solid #C0C0C0;
	text-indent: 5%;
}

a.plus {
	text-decoration: none;
	font-weight: bold;
}
</style>
<script type="text/javascript">
$(document).ready (function () {
	$("#btnEditUserCompany").attr("disabled", "disabled");
	markCurrentPageNumber ("${pageNumber}");
	hideAll();
});

function loadPage(pageNumber) {
	goToPage("divUserCompanyTable", SEARCH_URL+getCommonParam()+"&page="+pageNumber);
}

function showOrHideInfo (link, elemClass) {
	if ($(link).html() == " + ") {
		$("." + elemClass).show();
		$(link).html(" -- ");
	} else {
		$("." + elemClass).hide();
		$(link).html(" + ");
	}
}

function hideAll() {
	$(".trUserCompany").hide();
}
</script>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="tblusers" class="dataTable" border=0>
	<thead>
		<tr class="alignLeft">
			<th width="3%">#</th>
			<th width="2%"></th>
			<th width="50%">Name</th>
			<th width="40%">Company</th>
			<th width="5%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(users.pageSetting.maxResult * (users.currentPage-1)) + 1}" />
		<c:forEach var="user" items="${users.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}"
				style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0">
				<td>${index}</td>
				<td><input type="checkbox" id="${user.id}" name="cbUsers"
					onclick="enableEditAndDeleteButton('cbUsers','btnEditUserCompany', null);" /></td>
				<td>${user.username}</td>
				<td style="text-align: left;">
					[<a href="#" class="plus"
						onclick="showOrHideInfo(this, 'trUserCompany${user.id}');
						return false;"> + </a>] Companies
				</td>
				<td>
					<c:choose>
						<c:when test="${user.active eq true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
				<c:forEach var="uc" items="${user.userCompanies}" varStatus="status">
					<tr class="trUserCompany${user.id} trUserCompany">
						<td colspan="3"></td>
						<td class="tdRowLines">${uc.company.name}</td>
						<td class="tdRowLines">
					</tr>
				</c:forEach>
			</tr>
			<c:set var="index" value="${index + 1}" />
	</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${users.dataSize +
				((users.currentPage - 1) *
				users.pageSetting.maxResult)}/${users.totalRecords}</td>
			<td style="text-align: right;"><c:if
					test="${users.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${users.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page}); return false;"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${users.lastPage > 5}">
					<c:if test="${users.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page}); return false;"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6); return false;" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${users.currentPage > 5}">
						<c:set var="modPage" value="${users.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(users.currentPage + (5 - modPage)) <= users.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${users.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(users.currentPage + (5 - modPage)) >= users.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${users.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${users.currentPage}" />
								<c:set var="minPageSet" value="${users.currentPage - 4}" />
							</c:otherwise>
						</c:choose>

						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1}); return false;"
								class="pageNumber"> << </a>
						</c:if>

						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page}); return false; "
								class="pageNumber">${page}</a>
						</c:forEach>

						<c:if test="${(maxPageSet) < users.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1}); return false;"
								class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
	</tfoot>
</table>