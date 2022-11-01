<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: JSP page for AR Customer table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditCustomer").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/arCustomer/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("customerTable", url);
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
			<th></th>
			<th>Type</th>
			<th>Name</th>
			<th>Street, Barangay</th>
			<th>City, Province, and ZIP Code</th>
			<th>Contact Person</th>
			<th>Contact Number</th>
			<th>Email Address</th>
			<th>TIN</th>
			<th>Max. Allow. Amount</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(arCustomers.pageSetting.maxResult * (arCustomers.currentPage - 1)) + 1}" />
			<c:forEach var="customer" items="${arCustomers.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${customer.id}" name="cbCustomer"
						onclick="enableEditAndDeleteButton('cbCustomer','btnEditCustomer', null);"/></td>
					<td>${customer.businessClassification.name}</td>
					<td>${customer.name}</td>
					<td>${customer.streetBrgy}</td>
					<td>${customer.cityProvince}</td>
					<td>${customer.contactPerson}</td>
					<td>${customer.contactNumber}</td>
					<td>${customer.emailAddress}</td>
					<td>${customer.tin}</td>
					<td style="text-align: right; padding-right: 20px;"><fmt:formatNumber type="currency" currencySymbol=""
							maxFractionDigits="2" value="${customer.maxAmount}"/></td>
					<c:choose>
						<c:when test="${customer.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="10">${arCustomers.dataSize + ((arCustomers.currentPage - 1) *
				arCustomers.pageSetting.maxResult)}/${arCustomers.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${arCustomers.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${arCustomers.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${arCustomers.lastPage > 5}">
				<c:if test="${arCustomers.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${arCustomers.currentPage > 5}">
					<c:set var="modPage" value="${arCustomers.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(arCustomers.currentPage + (5 - modPage)) <= arCustomers.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${arCustomers.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(arCustomers.currentPage + (5 - modPage)) >= arCustomers.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${arCustomers.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${arCustomers.currentPage}" />
							<c:set var="minPageSet" value="${arCustomers.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < arCustomers.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>