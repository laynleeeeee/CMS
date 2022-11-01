<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Displays the list of product lines in table format.
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
	$("#btnEditProductLine").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	hideAll();
});

function loadPage(pageNumber) {
	var url = "/admin/productLine/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("productLineTable", url);
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
			<th width="2%"><input type="checkbox" id="checkAll" onclick="checkUncheckedAll
				('cbProductLine', this, 'btnEditProductLine', null)"/></th>
			<th>Stock Code</th>
			<th colspan="4" width="50%">Description</th>
			<th>UOM</th>
			<th width="7%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(productLines.pageSetting.maxResult * (productLines.currentPage - 1)) + 1}" />
			<c:forEach var="productLine" items="${productLines.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}"
				style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0">
					<td>${index}</td>
					<td><input type="checkbox" id="${productLine.id}" name="cbProductLine"
						onclick="enableEditAndDeleteButton('cbProductLine','btnEditProductLine', null);"/></td>
					<td>${productLine.mainItem.stockCode}</td>
					<td colspan="4">${productLine.mainItem.description}</td>
					<td>${productLine.mainItem.unitMeasurement.name}</td>
					<c:choose>
						<c:when test="${productLine.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
				<tr>
					<td colspan="3" style="text-align: right;">
						[<a href="#" class="plus"
							onclick="showOrHideInfo(this, 'trLineItems${productLine.id}');
							return false;"> + </a>] Raw Materials
					</td>
					<td style="font-weight: bold;"></td>
				</tr>
				<tr class="trLineItems${productLine.id} trLineItemHeader">
					<td colspan="3"></td>
					<td style="font-weight: bold;">Stock Code</td>
					<td style="font-weight: bold;">Description</td>
					<td width="7%" style="font-weight: bold;">QTY</td>
					<td style="font-weight: bold;">UOM</td>
				</tr>
				<c:forEach var="lineItem" items="${productLine.productLineItems}">
					<tr class="trLineItems${productLine.id} trLineItem">
						<td colspan="3"></td>
						<td class="tdRowLines">${lineItem.item.stockCode}</td>
						<td class="tdRowLines">${lineItem.item.description}</td>
						<td class="tdRowLines" align="right"><fmt:formatNumber pattern="#,###"
							minFractionDigits='2' maxFractionDigits='2' value="${lineItem.quantity}"/></td>
						<td class="tdRowLines">${lineItem.item.unitMeasurement.name}</td>
					</tr>
				</c:forEach>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="8">${productLines.dataSize + ((productLines.currentPage - 1) *
				productLines.pageSetting.maxResult)}/${productLines.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${productLines.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${productLines.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${productLines.lastPage > 5}">
				<c:if test="${productLines.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${productLines.currentPage > 5}">
					<c:set var="modPage" value="${productLines.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(productLines.currentPage + (5 - modPage)) <= productLines.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${productLines.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(productLines.currentPage + (5 - modPage)) >= productLines.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${productLines.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${productLines.currentPage}" />
							<c:set var="minPageSet" value="${productLines.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < productLines.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>