<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Item Volume Conversion table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
	$(document).ready (function () {
		$("#btnEditVolumeConversion").attr("disabled", "disabled");
		markCurrentPageNumber ("${pageNumber}");
	});

	function loadPage(pageNumber) {
		goToPage("divVolumeConversion", SEARCH_URL+getCommonParam()+"&page="+pageNumber);
	}
</script>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="tblvolumeConversion" class="dataTable" border=0>
	<thead>
		<tr class="alignLeft">
			<th width="3%">#</th>
			<th width="2%"></th>
			<th width="10%">Company Name</th>
			<th width="10%">Stock Code</th>
			<th width="25%">Description</th>
			<th width="15%">Quantity</th>
			<th width="15%">Conversion</th>
			<th width="10%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(volumeConversion.pageSetting.maxResult * (volumeConversion.currentPage-1)) + 1}" />
		<c:forEach var="volumeConversion" items="${volumeConversion.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'"
					onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${index}</td>
				<td><input type="checkbox" id="${volumeConversion.id}" name="cbVolumeConversion"
						onclick="enableEditAndDeleteButton('cbVolumeConversion','btnEditVolumeConversion', null);"/></td>
				<td style="white-space: pre-wrap;">${volumeConversion.companyName}</td>
				<td style="white-space: pre-wrap;">${volumeConversion.item.stockCode}</td>
				<td style="white-space: pre-wrap;">${volumeConversion.item.description}</td>
				<td style="text-align:right;">
					<fmt:formatNumber minFractionDigits="2" maxFractionDigits="2"
					value="${volumeConversion.quantity}"/>
				</td>
				<td style="text-align:right;">
					<fmt:formatNumber minFractionDigits="2" maxFractionDigits="2"
					value="${volumeConversion.volumeConversion}"/>
				</td>
				<td>
					<c:choose>
						<c:when test="${volumeConversion.active eq true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
			</tr>
		<c:set var="index" value="${index + 1}" />
	</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6">
				${volumeConversion.dataSize + ((volumeConversion.currentPage - 1) * volumeConversion.pageSetting.maxResult)}/${volumeConversion.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
				<c:if test="${volumeConversion.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${volumeConversion.lastPage }" >
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				
				<c:if test="${volumeConversion.lastPage > 5}">
					<c:if test="${volumeConversion.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"></a>
					</c:if>
					
					<c:if test="${volumeConversion.currentPage > 5}">
						<c:set var="modPage" value="${volumeConversion.currentPage % 5}"/>
						<c:choose>
							<c:when test="${(volumeConversion.currentPage + (5 - modPage)) <= volumeConversion.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${volumeConversion.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when test="${(volumeConversion.currentPage + (5 - modPage)) >= volumeConversion.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${volumeConversion.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${volumeConversion.currentPage}" />
								<c:set var="minPageSet" value="${volumeConversion.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						
						<c:if test="${minPageSet > 5}" >
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"></a>
						</c:if>
						
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						
						<c:if test="${(maxPageSet) < volumeConversion.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"></a>
						</c:if>
					</c:if>
				</c:if>
			</td>
		</tr>
	</tfoot>
</table>