<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Displays the list of user custodian in table format.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<style>
.tdRowLines {
	border-top: 1px solid #C0C0C0;
}

a.plus {
	text-decoration: none;
	font-weight: bold;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditUserCustodian").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	hideAll();
});

function loadPage(pageNumber) {
	var url = "/admin/userCustodian/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("userCustodianTable", url);
}

function hideAll() {
	$(".trLineItemHeader").hide();
	$(".trLineItem").hide();
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
</script>
</head>
<body>
<span id="messageSpan" class="message"> ${message}</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th>#</th>
			<th width="3%"><input type="checkbox" id="checkAll" onclick="checkUncheckedAll
				('cbUserCustodian', this, 'btnEditUserCustodian', null)"/></th>
			<th width="20%">Company</th>
			<th width="20%">Division</th>
			<th width="20%">Name</th>
			<th width="30%">User</th>
			<th width="7%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(userCustodians.pageSetting.maxResult * (userCustodians.currentPage - 1)) + 1}" />
			<c:forEach var="userCustodian" items="${userCustodians.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}"
				style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0">
					<td>${index}</td>
					<td><input type="checkbox" id="${userCustodian.id}" name="cbUserCustodian"
						onclick="enableEditAndDeleteButton('cbUserCustodian','btnEditUserCustodian', null);"/></td>
					<td>${userCustodian.company.name}</td>
					<td>${userCustodian.division.name}</td>
					<td>${userCustodian.custodianAccount.custodianName}</td>
					<td style="text-align: left;">
						[<a href="#" class="plus"
							onclick="showOrHideInfo(this, 'trLineItems${userCustodian.id}');
							return false;"> + </a>] User
					</td>
					<c:choose>
						<c:when test="${userCustodian.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
				<tr class="trLineItems${userCustodian.id} trLineItemHeader">
					<td colspan="5"></td>
					<td style="font-weight: bold;">Users</td>
				</tr>
				<c:forEach var="lineItem" items="${userCustodian.userCustodianLines}">
					<tr class="trLineItems${userCustodian.id} trLineItem">
						<td colspan="5"></td>
						<td class="tdRowLines">${lineItem.user.firstName} ${lineItem.user.lastName}</td>
					</tr>
				</c:forEach>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6">${userCustodians.dataSize + ((userCustodians.currentPage - 1) *
				userCustodians.pageSetting.maxResult)}/${userCustodians.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${userCustodians.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${userCustodians.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${userCustodians.lastPage > 5}">
				<c:if test="${userCustodians.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${userCustodians.currentPage > 5}">
					<c:set var="modPage" value="${userCustodians.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(userCustodians.currentPage + (5 - modPage)) <= userCustodians.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${userCustodians.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(userCustodians.currentPage + (5 - modPage)) >= userCustodians.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${userCustodians.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${userCustodians.currentPage}" />
							<c:set var="minPageSet" value="${userCustodians.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < userCustodians.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>