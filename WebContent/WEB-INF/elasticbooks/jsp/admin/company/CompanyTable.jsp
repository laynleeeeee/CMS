<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!--

	Description: Elasticbooks Company Table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/CMS/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script>
$(document).ready(function () {
	$("#btnEditCompany").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var url = "/admin/company" + getCommonParam() +"&pageNumber="+pageNumber;
	goToPage ("companyTable", url);
}
</script>
</head>
<body>
<span id="messageSpan" class="message">
	${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="dataTable" class="dataTable">
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="1 %"><input type="checkbox" id="checkAll"
				onclick="checkUncheckedAll ('cbCompany', this, 'btnEditCompany', 'btnDeleteCompany');" /></th>
			<th width="5%">Status</th>
			<th width="10%">Number</th>
			<th width="25%">Name</th>
			<th width="25%">Company Code</th>
			<th width="25%">Address</th>
			<th width="10%">Contact #</th>
			<th width="15%">Email</th>
			<th width="10%">TIN</th>
		</tr>
	</thead>
	<tbody class="tableFont">
		<c:forEach var="company" items="${companies.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className=
					'${trClass}'" class="${trClass}">
				<td>${status.index +1}</td>
				<td><input type="checkbox" id="${company.id}" name="cbCompany" 
						style="text-align: center; vertical-align: middle;"onclick="enableEditAndDeleteButton
						('cbCompany', 'btnEditCompany','btnDeleteContactList');" /></td>
				<td>
					<c:choose>
		                <c:when test="${company.active eq true}">Active</c:when>
		                <c:otherwise>Inactive</c:otherwise>
	                </c:choose>
				</td>
				<td>${company.companyNumber}</td>
				<td>${company.name}</td>
				<td>${company.companyCode}</td>
				<td>${company.address}</td>
				<td>${company.contactNumber}</td>
				<td>${company.emailAddress}</td>
				<td>${company.tin}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot class="nav">
		<tr>
				<td colspan="5">${companies.dataSize +
					((companies.currentPage - 1) *
					companies.pageSetting.maxResult)}/${companies.totalRecords}
				</td>
					<td colspan="4" style="text-align: right;"><c:if
						test="${companies.lastPage <= 5}">
						<c:forEach var="page" begin="1"
							end="${companies.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${companies.lastPage > 5}">
						<c:if test="${companies.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${companies.currentPage > 5}">
							<c:set var="modPage"
								value="${companies.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(companies.currentPage + (5 - modPage)) <= companies.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${companies.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(companies.currentPage + (5 - modPage)) >= companies.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${companies.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet"
										value="${companies.currentPage}" />
									<c:set var="minPageSet"
										value="${companies.currentPage - 4}" />
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

							<c:if test="${(maxPageSet) < companies.lastPage }">
								<a href="#" onclick="loadPage (${maxPageSet + 1})"
									class="pageNumber"> >> </a>
							</c:if>
						</c:if>
				</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>