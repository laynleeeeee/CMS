<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Account table
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	$("#btnEditAccount").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var accountTypeId = $("#selectAccountTypes").val();
	var accountNumber = processSearchName($("#txtAccountNumber").val());
	var accountName = processSearchName($("#txtAccountName").val());
	var status = $("#selectStatus").val();
	var targetUrl = "/admin/accounts?accountTypeId="+accountTypeId+"&accountNumber="+accountNumber
			+"&accountName="+accountName+"&status="+status+"&pageNumber="+pageNumber;
	goToPage ("divAccountTable", targetUrl);
}

function showOrHideInfo (link, elemClass, accountId) {
	var label = $(link).text();
	var $addChildAccount = $("#addChildAccount" + accountId);
	var $mainTr = $("#tr" + accountId);
	if (label == " + ") {
		$("." + elemClass).show();
		$(link).text(" - ");
		var color = $($mainTr).css("background-color");
		if($($addChildAccount).html() == ""){
			var uri = contextPath + "/admin/accounts/showChildren?accountId="+accountId;
			$($addChildAccount).load(uri);
		}
		$($addChildAccount).css("background-color", color);
	} else {
		$("." + elemClass).hide();
		$(link).text(" + ");
	}
}
</script>
<span id="messageSpan" class="message"> ${message} </span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="dataTable" class="dataTable">
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="1%"></th>
			<th width="10%">Account Number</th>
			<th width="15%">Account Name</th>
			<th width="37%">Description</th>
			<th width="15%">Account Type</th>
			<th width="15%">Main Account</th>
			<th width="5%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="acct" items="${accounts.data}" varStatus="status">
			<tr class="noBorder" id="tr${acct.id}">
				<td>${status.index + 1}</td>
				<td>
					<input type="checkbox" id="${acct.id}" name="cbAccount" 
						onclick="enableEditAndDeleteButton('cbAccount','btnEditAccount', null);"/>
				</td>
				<td>
					<c:if test="${!acct.lastLevel and acct.active}">
						[<a href="#" class="plus" id="a${acct.id}" onclick="showOrHideInfo(this, 'merge${acct.id}', ${acct.id});"> + </a>] 
					</c:if>
					${acct.number}
				</td>
				<td>${acct.accountName}</td>
				<td>${acct.description}</td>
				<td>${acct.atName}</td>
				<td>${acct.paName}</td>
				<td>
					<c:choose>
						<c:when test="${acct.active}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
			</tr>

			<tr class="merge${acct.id} merge">
				<td colspan="20" id="addChildAccount${acct.id}"></td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="7">${accounts.dataSize +
				((accounts.currentPage - 1) *
				accounts.pageSetting.maxResult)}/${accounts.totalRecords}</td>
			<td style="text-align: right;"><c:if
					test="${accounts.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${accounts.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${accounts.lastPage > 5}">
					<c:if test="${accounts.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${accounts.currentPage > 5}">
						<c:set var="modPage" value="${accounts.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(accounts.currentPage + (5 - modPage)) <= accounts.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${accounts.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(accounts.currentPage + (5 - modPage)) >= accounts.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${accounts.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${accounts.currentPage}" />
								<c:set var="minPageSet" value="${accounts.currentPage - 4}" />
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

						<c:if test="${(maxPageSet) < accounts.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})"
								class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
		</tr>
	</tfoot>
</table>