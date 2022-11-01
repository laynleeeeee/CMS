<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Displays the list of YBL Suppliers in table format.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditSupplier").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/supplier/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("supplierTable", url);
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
			<th>TIN</th>
			<th>Contact Person</th>
			<th>Contact Number</th>
			<th>VAT Type</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(suppliers.pageSetting.maxResult * (suppliers.currentPage - 1)) + 1}" />
			<c:forEach var="supplier" items="${suppliers.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${supplier.id}" name="cbSupplier"
						onclick="enableEditAndDeleteButton('cbSupplier','btnEditSupplier', null);"/></td>
					<td>${supplier.businessClassification.name}</td>
					<td>${supplier.name}</td>
					<td>${supplier.streetBrgy}</td>
					<td>${supplier.cityProvince}</td>
					<td>${supplier.tin}</td>
					<td>${supplier.contactPerson}</td>
					<td>${supplier.contactNumber}</td>
					<td>${supplier.busRegType.name}</td>
					<c:choose>
						<c:when test="${supplier.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="9">${suppliers.dataSize + ((suppliers.currentPage - 1) *
				suppliers.pageSetting.maxResult)}/${suppliers.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${suppliers.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${suppliers.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${suppliers.lastPage > 5}">
				<c:if test="${suppliers.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${suppliers.currentPage > 5}">
					<c:set var="modPage" value="${suppliers.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(suppliers.currentPage + (5 - modPage)) <= suppliers.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${suppliers.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(suppliers.currentPage + (5 - modPage)) >= suppliers.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${suppliers.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${suppliers.currentPage}" />
							<c:set var="minPageSet" value="${suppliers.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < suppliers.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>