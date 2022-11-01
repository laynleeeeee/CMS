<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		 Description: Employee admin setting table -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditEmployee").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	$(".tdInfo").hide();
	$(".tdDetails").hide();
});

function loadPage(pageNumber) {
	var url = "/admin/employee/search"+getCommonParam()+"&pageNumber="+pageNumber;
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
			<th>Company</th>
			<th>Division</th>
			<th>Biometric ID</th>
			<th>Employee No.</th>
			<th>First Name</th>
			<th>Middle Name</th>
			<th>Last Name</th>
			<th>Position</th>
			<th>Shift</th>
			<th>Status</th>
			<th>Employee Information</th>
			<th></th>
			<th>Salary Details</th>
			<th></th>
			<th></th>
			<th>Active</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(employees.pageSetting.maxResult * (employees.currentPage - 1)) + 1}" />
			<c:forEach var="employee" items="${employees.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${employee.id}" name="cbEmployee"
						onclick="enableEditAndDeleteButton('cbEmployee','btnEditEmployee', null);"/></td>
					<td>${employee.company.name}</td>
					<td>${employee.division.name}</td>
					<td>${employee.biometricId}</td>
					<td>${employee.employeeNo}</td>
					<td>${employee.firstName}</td>
					<td>${employee.middleName}</td>
					<td>${employee.lastName}</td>
					<td>${employee.position.name}</td>
					<td>${employee.employeeShift.name}</td>
					<td>${employee.employeeStatus.name}</td>
					<td style="text-align: left;">
						[<a href="#" class="plus"
							onclick="showOrHideInfo(this, 'tdInfo${employee.id}');
							return false;"> + </a>] Information
					</td>
					<td></td>
					<td style="text-align: left;">
						[<a href="#" class="plus"
							onclick="showOrHideInfo(this, 'tdDetails${employee.id}');
							return false;"> + </a>] Details
					</td>
					<td></td>
					<td></td>
					<c:choose>
						<c:when test="${employee.active eq true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
				<tr>
					<td colspan="12"></td>
					<td class="tdInfo${employee.id} tdInfo" valign="top">
						<table>
							<tr>
								<td>Gender</td>
								<c:choose>
									<c:when test="${employee.gender eq 1}"><td>Male</td></c:when>
									<c:otherwise><td>Female</td></c:otherwise>
								</c:choose>
							</tr>
							<tr>
								<td>Birthdate</td>
								<td><fmt:formatDate pattern="MM/dd/yyyy" value="${employee.birthDate}"/></td>
							</tr>
							<tr>
								<td>Civil Status</td>
								<c:choose>
									<c:when test="${employee.civilStatus eq 1}"><td>Single</td></c:when>
									<c:otherwise><td>Married</td></c:otherwise>
								</c:choose>
							</tr>
							<tr>
								<td>Contact No.</td>
								<td>${employee.contactNo}</td>
							</tr>
							<tr>
								<td>Email Address</td>
								<td>${employee.emailAddress}</td>
							</tr>
							<tr>
								<td>Address</td>
								<td>${employee.address}</td>
							</tr>
						</table>
					</td>
					<td></td>
					<td colspan="2" class="tdDetails${employee.id} tdDetails" valign="top">
						<table>
						<tr>
							<td>Daily Salary</td>
							<td align="right" valign="top"><fmt:formatNumber type='number'
								minFractionDigits='2' maxFractionDigits='2'
								value='${employee.salaryDetail.dailySalary}'/></td>
						</tr>
						<tr>
							<td>COLA</td>
							<td align="right" valign="top"><fmt:formatNumber type='number'
								minFractionDigits='2' maxFractionDigits='2'
								value='${employee.salaryDetail.ecola}'/></td>
						</tr>
						<tr>
							<td colspan="2">Exclude Contribution</td>
						</tr>
						<tr>
							<td>SSS</td>
							<td><c:choose>
								<c:when test="${employee.salaryDetail.excludeSss == true}">Yes</c:when>
								<c:otherwise>No</c:otherwise></c:choose>
							</td>
						</tr>
						<tr>
							<td>PHIC</td>
							<td><c:choose>
								<c:when test="${employee.salaryDetail.excludePhic == true}">Yes</c:when>
								<c:otherwise>No</c:otherwise></c:choose>
							</td>
						</tr>
						<tr>
							<td>HDMF</td>
							<td><c:choose>
								<c:when test="${employee.salaryDetail.excludeHdmf == true}">Yes</c:when>
								<c:otherwise>No</c:otherwise></c:choose>
							</td>
						</tr>
						</table>
					</td>
				</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="14">${employees.dataSize + ((employees.currentPage - 1) *
				employees.pageSetting.maxResult)}/${employees.totalRecords}
			</td>
			<td colspan="4" style="text-align: right;">
			<c:if test="${employees.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${employees.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${employees.lastPage > 5}">
				<c:if test="${employees.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${employees.currentPage > 5}">
					<c:set var="modPage" value="${employees.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(employees.currentPage + (5 - modPage)) <= employees.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${employees.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(employees.currentPage + (5 - modPage)) >= employees.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${employees.lastPage}" />
							<c:set var="minPageSet"
								value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${employees.currentPage}" />
							<c:set var="minPageSet" value="${employees.currentPage - 4}" />
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

					<c:if test="${(maxPageSet) < employees.lastPage }">
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