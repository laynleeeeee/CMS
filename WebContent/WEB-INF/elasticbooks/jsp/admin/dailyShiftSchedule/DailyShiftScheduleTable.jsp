<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

           Description: Daily Shift Schedule table -->
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
	$("#btnEditPtp").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	$(".tdInfo").hide();
});
function loadPage(pageNumber) {
	var url = "/admin/dailyShiftSchedule/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divPayrollTimePeriodTbl", url);
}

function showOrHideInfo (link, elemClass, index) {
	var $divSched = $("#divSched" + index);
	var $trSched = $("#trSched" + index);
	if ($(link).html() == " + ") {
		if ($.trim($($divSched).html()) == "") {
			var shiftSchedId = elemClass.split("tdInfo")[1];
			var url = contextPath + "/admin/dailyShiftSchedule/loadSchedules?dailyShiftScheduleId=" + shiftSchedId;
			$($divSched).load(url);
		}
		$($divSched).show();
		$($trSched).show();
		$(link).html(" -- ");
	} else {
		$($divSched).hide();
		$($trSched).hide();
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
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(dailyTimeSheetSchedules.pageSetting.maxResult * (dailyTimeSheetSchedules.currentPage - 1)) + 1}" />
		<c:forEach var="dailyTimeSheetSchedule" items="${dailyTimeSheetSchedules.data}" varStatus="status">
			<tr style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0"
					onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${index}</td>
				<td><input type="checkbox" id="${dailyTimeSheetSchedule.id}" name="cbPtp"
					onclick="enableEditAndDeleteButton('cbPtp','btnEditPtp', null);"/></td>
				<td>${dailyTimeSheetSchedule.company.name}</td>
				<td>${dailyTimeSheetSchedule.payrollTimePeriod.name}</td>
				<td>${dailyTimeSheetSchedule.payrollTimePeriodSchedule.name}</td>
				<td style="text-align: left;">
					[<a href="#" class="plus"
						onclick="showOrHideInfo(this, 'tdInfo${dailyTimeSheetSchedule.id}', ${index});
						return false;"> + </a>] Schedule
				</td>
			</tr>
			<tr id="trSched${index}" class="tdInfo">
				<td colspan="5"></td>
				<td>
					<div id="divSched${index}"></div>
				</td>
			</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${dailyTimeSheetSchedules.dataSize + ((dailyTimeSheetSchedules.currentPage - 1) *
				dailyTimeSheetSchedules.pageSetting.maxResult)}/${dailyTimeSheetSchedules.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${dailyTimeSheetSchedules.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${dailyTimeSheetSchedules.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${dailyTimeSheetSchedules.lastPage > 5}">
				<c:if test="${dailyTimeSheetSchedules.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${dailyTimeSheetSchedules.currentPage > 5}">
					<c:set var="modPage" value="${dailyTimeSheetSchedules.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(dailyTimeSheetSchedules.currentPage + (5 - modPage)) <= dailyTimeSheetSchedules.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${arCustomers.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(dailyTimeSheetSchedules.currentPage + (5 - modPage)) >= dailyTimeSheetSchedules.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${dailyTimeSheetSchedules.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${dailyTimeSheetSchedules.currentPage}" />
							<c:set var="minPageSet" value="${dailyTimeSheetSchedules.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < dailyTimeSheetSchedules.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>
</body>
</html>