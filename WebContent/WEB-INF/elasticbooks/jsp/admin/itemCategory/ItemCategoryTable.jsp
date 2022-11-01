<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>

<!-- Description: The Table for Item Category -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
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
	$("#btnEditItemCategory").attr("disabled", "disabled");
	markCurrentPageNumber ("${pageNumber}");
	hideAll();
});

function loadPage(pageNumber) {
	var name = processSearchName($("#txtName").val());
	var status = $("#selectStatus").val();

	var url = "/admin/itemCategories/search?name="+name+"&status="+status+"&pageNumber="+pageNumber;
	goToPage("divItemCategoryTable", url);
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
	$(".trCategoryAccount").hide();
}

</script>
<table id="dataTable" class="dataTable">
		<thead>
			<tr class="alignLeft">
				<th width="5%">#</th>
				<th width="5%"><input type="checkbox" id="checkAll"
					onclick="checkUncheckedAll('cbItemCategory', this,'btnEditItemCategory', null)"/></th>
				<th width="15%">Item Category</th>
				<th width="15%"></th>
				<th width="15%"></th>
				<th width="15%"></th>
				<th width="15%"></th>
				<th width="15%"></th>
				<th width="15%">Status</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="index" value="${(itemCategories.pageSetting.maxResult * (itemCategories.currentPage - 1)) + 1}" />
				<c:forEach var="itemCategory" items="${itemCategories.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}"
					style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0">
						<td>${index}</td>
						<td><input type="checkbox" id="${itemCategory.id}"
							name="cbItemCategory"
							onclick="enableEditAndDeleteButton('cbItemCategory','btnEditItemCategory', null);" />
						</td>
						<td>${itemCategory.name}</td>
						<td colspan="5"></td>
						<c:choose>
							<c:when test="${itemCategory.active == true}"><td>Active</td></c:when>
							<c:otherwise><td>Inactive</td></c:otherwise>
						</c:choose>
						<tr>
							<td colspan="2"></td>
							<td><b>Division</b></td>
							<td><b>Cost Account</b></td>
							<td><b>Inventory Account</b></td>
							<td><b>Sales Account</b></td>
							<td><b>Sales Discount Account</b></td>
							<td><b>Sales Return Account</b></td>
							<td><b>Status</b></td>
						</tr>
					<c:forEach var="accountSetupDto" items="${itemCategory.accountSetupDtos}" varStatus="status">
						<tr>
							<td colspan="1"></td>
							<td style="text-align: left;" colspan="8" class="tdRowLines">
								[<a href="#" class="plus"
									onclick="showOrHideInfo(this, 'trCategoryAccount${accountSetupDto.hdnId}');
									return false;"> + </a>] ${accountSetupDto.companyName}
							</td>
								<c:forEach var="accountSetup" items="${accountSetupDto.accountSetups}" varStatus="status">
									<tr class="trCategoryAccount${accountSetupDto.hdnId} trCategoryAccount">
										<td colspan="2"></td>
										<td class="tdRowLines">${accountSetup.costAccountCombi.division.name}</td>
										<td class="tdRowLines">${accountSetup.costAccountCombi.account.accountName}</td>
										<td class="tdRowLines">${accountSetup.inventoryAccountCombi.account.accountName}</td>
										<td class="tdRowLines">${accountSetup.salesAccountCombi.account.accountName}</td>
										<td class="tdRowLines">${accountSetup.salesDiscountAccountCombi.account.accountName}</td>
										<td class="tdRowLines">${accountSetup.salesReturnAccountCombi.account.accountName}</td>
										<c:choose>
											<c:when test="${accountSetup.active == true}"><td class="tdRowLines">Active</td></c:when>
											<c:otherwise><td class="tdRowLines">Inactive</td></c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
						</c:forEach>
						<c:set var="index" value="${index + 1}" />
					</tr>
				</c:forEach>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="7">${itemCategories.dataSize + ((itemCategories.currentPage - 1) *
				itemCategories.pageSetting.maxResult)}/${itemCategories.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${itemCategories.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${itemCategories.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${itemCategories.lastPage > 5}">
				<c:if test="${itemCategories.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${itemCategories.currentPage > 5}">
					<c:set var="modPage" value="${itemCategories.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(itemCategories.currentPage + (5 - modPage)) <= itemCategories.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${itemCategories.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(itemCategories.currentPage + (5 - modPage)) >= itemCategories.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${itemCategories.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${itemCategories.currentPage}" />
							<c:set var="minPageSet" value="${itemCategories.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < itemCategories.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>