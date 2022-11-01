<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

           Description: Payroll Time Period table -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Payroll Time Period</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditPtp").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	$(".tdInfo").hide();
});

function loadPage(pageNumber) {
	var url = "/admin/payrollTimePeriod/search"+getCommonParam()+"&pageNumber="+pageNumber;
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
		<tr class="alignleft">
			<th width="3%">#</th>
			<th width="2%"></th>
			<th width="35%">Name</th>
			<th width="20%">Month and Year</th>
			<th width="15%">Schedule and Contributions</th>
			<th width="10%"></th>
			<th width="15%"></th>
			<th width="10%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(payrollTimePeriods.pageSetting.maxResult * (payrollTimePeriods.currentPage - 1)) + 1}" />
			<c:forEach var="ptp" items="${payrollTimePeriods.data}" varStatus="status">
				<tr style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0"
					onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${ptp.id}" name="cbPtp"
						onclick="enableEditAndDeleteButton('cbPtp','btnEditPtp', null);"/></td>
					<td>${ptp.name}</td>
					<td>
						<c:choose>
							<c:when test="${ptp.month eq 1}">January</c:when>
							<c:when test="${ptp.month eq 2}">February</c:when>
							<c:when test="${ptp.month eq 3}">March</c:when>
							<c:when test="${ptp.month eq 4}">April</c:when>
							<c:when test="${ptp.month eq 5}">May</c:when>
							<c:when test="${ptp.month eq 6}">June</c:when>
							<c:when test="${ptp.month eq 7}">July</c:when>
							<c:when test="${ptp.month eq 8}">August</c:when>
							<c:when test="${ptp.month eq 9}">September</c:when>
							<c:when test="${ptp.month eq 10}">October</c:when>
							<c:when test="${ptp.month eq 11}">November</c:when>
							<c:when test="${ptp.month eq 12}">December</c:when>
						</c:choose>
						${ptp.year}
					</td>
					<td style="text-align: left;">
						[<a href="#" class="plus"
							onclick="showOrHideInfo(this, 'tdInfo${ptp.id}');
							return false;"> + </a>] Schedule
					</td>
					<td></td>
					<td></td>
					<td>
					<c:choose>
						<c:when test="${ptp.active eq true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
					</td>
				</tr>
				<tr class="tdInfo${ptp.id} tdInfo">
					<td colspan="4"></td>
					<td style="font-weight: bold;">Name</td>
					<td style="font-weight: bold;">Start Date</td>
					<td style="font-weight: bold;">End Date</td>
					<td style="font-weight: bold;"></td>
				</tr>
				<c:forEach var="ptps" items="${ptp.payrollTimePeriodSchedules}" varStatus="status">
					<tr class="tdInfo${ptp.id} tdInfo">
						<td colspan="4"></td>
						<td class="tdRowLines">${ptps.name}</td>
						<td class="tdRowLines"><fmt:formatDate pattern="MM/dd/yyyy" value="${ptps.dateFrom}" /></td>
						<td class="tdRowLines"><fmt:formatDate pattern="MM/dd/yyyy" value="${ptps.dateTo}" /></td>
						<td class="tdRowLines" nowrap="nowrap">
							<c:choose>
								<c:when test="${ptps.computeContributions eq true}">
									Contribution Payment Schedule
								</c:when>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
				<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="7">${payrollTimePeriods.dataSize + ((payrollTimePeriods.currentPage - 1) *
				payrollTimePeriods.pageSetting.maxResult)}/${payrollTimePeriods.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${payrollTimePeriods.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${payrollTimePeriods.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${payrollTimePeriods.lastPage > 5}">
				<c:if test="${payrollTimePeriods.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${payrollTimePeriods.currentPage > 5}">
					<c:set var="modPage" value="${payrollTimePeriods.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(payrollTimePeriods.currentPage + (5 - modPage)) <= payrollTimePeriods.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${arCustomers.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(payrollTimePeriods.currentPage + (5 - modPage)) >= payrollTimePeriods.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${payrollTimePeriods.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${payrollTimePeriods.currentPage}" />
							<c:set var="minPageSet" value="${payrollTimePeriods.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < payrollTimePeriods.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>