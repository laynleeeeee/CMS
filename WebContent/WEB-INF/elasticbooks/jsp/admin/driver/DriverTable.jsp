<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		 Description: Driver admin setting table -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditDriver").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	$(".tdInfo").hide();
});

function loadPage(pageNumber) {
	var url = "/admin/driver/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divEmployeeTbl", url);
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
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable">
	<thead>
		<tr class="alignLeft">
			<th width="1%">#</th>
			<th width="2%"></th>
			<th width="30%">Company</th>
			<th width="15%">First Name</th>
			<th width="15%">Middle Name</th>
			<th width="15%">Last Name</th>
			<th width="20%">Driver Information</th>
			<th width="2%">Active</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(drivers.pageSetting.maxResult * (drivers.currentPage - 1)) + 1}" />
			<c:forEach var="driver" items="${drivers.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${driver.id}" name="cbDriver"
						onclick="enableEditAndDeleteButton('cbDriver','btnEditDriver', null);"/></td>
					<td>${driver.company.name}</td>
					<td>${driver.firstName}</td>
					<td>${driver.middleName}</td>
					<td>${driver.lastName}</td>
					<td style="text-align: left;">
						[<a href="#" class="plus"
							onclick="showOrHideInfo(this, 'tdInfo${driver.id}');
							return false;"> + </a>] Information
					</td>
					<c:choose>
						<c:when test="${driver.active eq true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
				<tr>
					<td colspan="6"></td>
					<td class="tdInfo${driver.id} tdInfo" valign="top">
						<table>
							<tr>
								<td>Gender</td>
								<c:choose>
									<c:when test="${driver.genderId eq 1}"><td>Male</td></c:when>
									<c:otherwise><td>Female</td></c:otherwise>
								</c:choose>
							</tr>
							<tr>
								<td>Birthdate</td>
								<td><fmt:formatDate pattern="MM/dd/yyyy" value="${driver.birthDate}"/></td>
							</tr>
							<tr>
								<td>Civil Status</td>
								<c:choose>
									<c:when test="${driver.civilStatusId eq 1}"><td>Single</td></c:when>
									<c:otherwise><td>Married</td></c:otherwise>
								</c:choose>
							</tr>
							<tr>
								<td>Contact No.</td>
								<td>${driver.contactNo}</td>
							</tr>
							<tr>
								<td>Address</td>
								<td>${driver.address}</td>
							</tr>
						</table>
					</td>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="7">${drivers.dataSize + ((drivers.currentPage - 1) *
				drivers.pageSetting.maxResult)}/${drivers.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${drivers.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${drivers.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${drivers.lastPage > 5}">
				<c:if test="${drivers.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${drivers.currentPage > 5}">
					<c:set var="modPage" value="${drivers.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(drivers.currentPage + (5 - modPage)) <= drivers.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${drivers.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(drivers.currentPage + (5 - modPage)) >= drivers.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${drivers.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${drivers.currentPage}" />
							<c:set var="minPageSet" value="${drivers.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < drivers.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>