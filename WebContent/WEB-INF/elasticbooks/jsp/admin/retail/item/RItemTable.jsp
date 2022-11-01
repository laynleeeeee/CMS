<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description: Retail Item table. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
	$(document).ready (function () {
		$("#btnEditItem").attr("disabled", "disabled");
		markCurrentPageNumber ("${pageNumber}");
	});

	function loadPage (pageNumber) {
		goToPage ("divRItemTable", "/admin/rItems?"+getCommonParam()+"&pageNumber="+pageNumber);
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
<style>
.tdRowLines {
	border-top: 1px solid #C0C0C0;
}

a.plus {
	text-decoration: none;
	font-weight: bold;
}
</style>
<table id="tblItem" class="dataTable" border=0>
	<thead>
		<tr class="alignLeft">
			<th width="2%">#</th>
			<th width="1%"><input type="checkbox"></th>
			<th width=15%>Stock Code</th>
			<th width=32%>Description</th>
			<th width=15%>UOM</th>
			<th width=15%>Category</th>
			<th width=10%>RP </th>
			<th width=10%>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(items.pageSetting.maxResult * (items.currentPage - 1)) + 1}" />
			<c:forEach var="item" items="${items.data}" varStatus="status">
				<tr style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0">
					<td>${index}</td>
					<td>
						<input type="checkbox" id="${item.id}" name="cbItem"
							onclick="enableEditAndDeleteButton('cbItem','btnEditItem', null);"/>
					</td>
					<td>${item.stockCode}</td>
					<td>${item.description}</td>
					<td>${item.unitMeasurement.name}</td>
					<td>${item.itemCategory.name}</td>
					<td>
						<fmt:formatNumber pattern="#,###" value="${item.reorderingPoint}"/>
					</td>
					<td>
						<c:choose>
							<c:when test="${item.active}">Active</c:when>
							<c:otherwise>Inactive</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<c:if test="${fn:length(item.itemSrps) gt 0}">
					<tr>
						<td colspan="6"> </td>
						<td style="font-weight: bold;">Unit Cost</td>
						<td style="font-weight: bold;">SRP</td>
					</tr>
				</c:if>

				<c:forEach var="itemSrp" items="${item.itemSrps}" varStatus="status">
					<c:if test="${itemSrp.active}">
						<tr>
							<td colspan="3"> </td>
							<td style="text-align: right;">
								[<a href="#" class="plus" 
									onclick="showOrHideInfo(this, 'trInfo${itemSrp.id}${itemSrp.companyId}'); 
									return false;"> + </a>]
							</td>
							<td class="tdRowLines">${itemSrp.company.name}</td>
							<td class="tdRowLines"></td>
							<td class="tdRowLines" style="font-weight: bold;"><fmt:formatNumber type='number'
									minFractionDigits='2' maxFractionDigits='2' value='${itemSrp.itemUnitCost}' /></td>
							<td class="tdRowLines" style="font-weight: bold;">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${itemSrp.srp}' />
							</td>
						</tr>
						<c:if test="${fn:length(item.itemDiscounts) gt 0}">
							<tr class="trInfo${itemSrp.id}${itemSrp.companyId}" style="display: none;">
								<td colspan="4"></td>
								<td><b>Discounts</b></td>
							</tr>
						</c:if>
						<c:forEach var="itemDiscount" items="${item.itemDiscounts}" varStatus="status">
							<c:if test="${itemDiscount.companyId eq itemSrp.companyId}">
								<tr class="trInfo${itemSrp.id}${itemSrp.companyId}" style="display: none;">
									<td colspan="4"></td>
									<td class="tdRowLines">${itemDiscount.name}</td>
									<td class="tdRowLines" style="text-align: right;">
										<c:choose>
											<c:when test="${itemDiscount.itemDiscountTypeId eq 1}">
												<fmt:formatNumber type='number'  minFractionDigits='2' maxFractionDigits='2' 
													value='${itemDiscount.value}' />%
											</c:when>
											<c:otherwise>
												<fmt:formatNumber type='number'  minFractionDigits='2' maxFractionDigits='2' 
													value='${itemDiscount.value}' />
											</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:if>
						</c:forEach>
						<c:if test="${fn:length(item.itemAddOns) gt 0}">
							<tr style="height: 10px;"></tr>
							<tr class="trInfo${itemSrp.id}${itemSrp.companyId}" style="display: none;">
								<td colspan="4"></td>
								<td><b>Add Ons</b></td>
							</tr>
						</c:if>
						<c:forEach var="itemAddOn" items="${item.itemAddOns}" varStatus="status">
							<c:if test="${itemAddOn.companyId eq itemSrp.companyId}">
								<tr class="trInfo${itemSrp.id}${itemSrp.companyId}" style="display: none;">
									<td colspan="4"></td>
									<td class="tdRowLines">${itemAddOn.name}</td>
									<td class="tdRowLines" style="text-align: right;">
										<fmt:formatNumber type='number'  minFractionDigits='2' maxFractionDigits='2' 
													value='${itemAddOn.value}' />
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</c:if>
				</c:forEach>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6">${items.dataSize + ((items.currentPage - 1) *
				items.pageSetting.maxResult)}/${items.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;"><c:if
					test="${items.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${items.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page}); return false;"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${items.lastPage > 5}">
					<c:if test="${items.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page}); return false;"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6); return false;" class="pageNumber"> >> </a>
					</c:if>

					<c:if test="${items.currentPage > 5}">
						<c:set var="modPage" value="${items.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(items.currentPage + (5 - modPage)) <= items.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${items.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(items.currentPage + (5 - modPage)) >= items.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${items.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${items.currentPage}" />
								<c:set var="minPageSet" value="${items.currentPage - 4}" />
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

						<c:if test="${(maxPageSet) < items.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1}); return false;"
								class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
		</tr>
	</tfoot>
</table>
