<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

           Description: Monthly Shift Schedule table -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
/* 	var size = Number("${fn:length(monthlyShiftSchedules.data)}");
	if (size > 0) {
		$("#btnAddPtp").hide("fast");
	} */
	$("#btnEditPtp").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	$(".tdInfo").hide();
});
function loadPage(pageNumber) {
	var url = "/admin/monthlyShiftSchedule/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divPayrollTimePeriodTbl", url);
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
			<th width="3%">#</th>
			<th></th>
			<th>Company</th>
			<th>Payroll Time Period</th>
			<th>Payroll Time Schedule</th>
			<th></th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(monthlyShiftSchedules.pageSetting.maxResult * (monthlyShiftSchedules.currentPage - 1)) + 1}" />
		<c:forEach var="monthlyShiftSchedule" items="${monthlyShiftSchedules.data}" varStatus="status">
			<tr style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0"
					onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${index}</td>
				<td><input type="checkbox" id="${monthlyShiftSchedule.id}" name="cbPtp"
					onclick="enableEditAndDeleteButton('cbPtp','btnEditPtp', null);"/></td>
				<td>${monthlyShiftSchedule.company.name}</td>
				<td>${monthlyShiftSchedule.payrollTimePeriod.name}</td>
				<td>${monthlyShiftSchedule.payrollTimePeriodSchedule.name}</td>
				<td style="text-align: left;">
					[<a href="#" class="plus"
						onclick="showOrHideInfo(this, 'tdInfo${monthlyShiftSchedule.id}');
						return false;"> + </a>] Schedule
				</td>
				<c:choose>
					<c:when test="${monthlyShiftSchedule.active == true}"><td>Active</td></c:when>
					<c:otherwise><td>Inactive</td></c:otherwise>
				</c:choose>
			</tr>
			<tr class="tdInfo${monthlyShiftSchedule.id} tdInfo">
				<td colspan="2"></td>
				<td style="font-weight: bold;">Name</td>
				<td style="font-weight: bold;">Shift</td>
			</tr>
			<c:forEach var="ssdl" items="${monthlyShiftSchedule.monthlyShiftScheduleLines}" varStatus="status">
					<tr class="tdInfo${monthlyShiftSchedule.id} tdInfo">
						<td colspan="2"></td>
						<td class="tdRowLines">${ssdl.employee.fullName}</td>
						<td class="tdRowLines">${ssdl.employeeShift.name}</td>
					</tr>
				</c:forEach>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td></td>
			<td colspan="4">${monthlyShiftSchedules.dataSize + ((monthlyShiftSchedules.currentPage - 1) *
				monthlyShiftSchedules.pageSetting.maxResult)}/${monthlyShiftSchedules.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${monthlyShiftSchedules.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${monthlyShiftSchedules.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${monthlyShiftSchedules.lastPage > 5}">
				<c:if test="${monthlyShiftSchedules.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${monthlyShiftSchedules.currentPage > 5}">
					<c:set var="modPage" value="${monthlyShiftSchedules.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(monthlyShiftSchedules.currentPage + (5 - modPage)) <= monthlyShiftSchedules.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${arCustomers.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(monthlyShiftSchedules.currentPage + (5 - modPage)) >= monthlyShiftSchedules.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${monthlyShiftSchedules.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${monthlyShiftSchedules.currentPage}" />
							<c:set var="minPageSet" value="${monthlyShiftSchedules.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < monthlyShiftSchedules.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>