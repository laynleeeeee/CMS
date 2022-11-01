<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>
     <!--

		Description: Employee shift table
      -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditEmployeeShift").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	$(".tdInfo").hide();
});

function loadPage(pageNumber) {
	var url = "/admin/employeeShift/search"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divEmployeeShiftTbl", url);
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
			<th width="22%">Company</th>
			<th width="20%">Name</th>
			<th width="7%">Start Shift</th>
			<th width="7%">End Shift</th>
			<th width="7%">Allowable <br> Break Time</th>
			<th width="7%">Late <br> Multiplier</th>
			<th width="7%">Daily <br> Working Hours</th>
			<th width="7%">Night Shift</th>
			<th width="7%">Day Offs</th>
			<th width="5%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="index" value="${(cscEmployeeShifts.pageSetting.maxResult * (cscEmployeeShifts.currentPage - 1)) + 1}" />
			<c:forEach var="employeeShift" items="${cscEmployeeShifts.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${employeeShift.id}" name="cbEmployeeShift"
						onclick="enableEditAndDeleteButton('cbEmployeeShift','btnEditEmployeeShift', null);"/></td>
					<td>${employeeShift.company.name}</td>
					<td>${employeeShift.name}</td>
					<td>${employeeShift.firstHalfShiftStart}</td>
					<td>${employeeShift.secondHalfShiftEnd}</td>
					<td style="text-align: left">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${employeeShift.allowableBreak}" />
					</td>
					<td style="text-align: left">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${employeeShift.lateMultiplier}" />%
					</td>
					<td style="text-align: left">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" 
							value="${employeeShift.dailyWorkingHours}" />
					</td>
					<c:choose>
						<c:when test="${employeeShift.nightShift == true}"><td>Yes</td></c:when>
						<c:otherwise><td>No</td></c:otherwise>
					</c:choose>
					<td style="text-align: left;">
						[<a href="#" class="plus"
							onclick="showOrHideInfo(this, 'tdInfo${employeeShift.id}');
							return false;"> + </a>]
					</td>
					<c:choose>
						<c:when test="${employeeShift.active == true}"><td>Active</td></c:when>
						<c:otherwise><td>Inactive</td></c:otherwise>
					</c:choose>
				</tr>
				<c:choose>
					<c:when test="${employeeShift.dayOffDto.sunday == true}">
						<tr class="tdInfo${employeeShift.id} tdInfo">
							<td colspan="10"></td>
							<td class="tdRowLines">Sunday</td>
						</tr>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${employeeShift.dayOffDto.monday == true}">
						<tr class="tdInfo${employeeShift.id} tdInfo">
							<td colspan="10"></td>
							<td class="tdRowLines">Monday</td>
						</tr>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${employeeShift.dayOffDto.tuesday == true}">
						<tr class="tdInfo${employeeShift.id} tdInfo">
							<td colspan="10"></td>
							<td class="tdRowLines">Tuesday
							</td>
						</tr>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${employeeShift.dayOffDto.wednesday == true}">
						<tr class="tdInfo${employeeShift.id} tdInfo">
							<td colspan="10"></td>
							<td class="tdRowLines">Wednesday
							</td>
						</tr>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${employeeShift.dayOffDto.thursday == true}">
						<tr class="tdInfo${employeeShift.id} tdInfo">
							<td colspan="10"></td>
							<td class="tdRowLines">Thursday
							</td>
						</tr>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${employeeShift.dayOffDto.friday == true}">
						<tr class="tdInfo${employeeShift.id} tdInfo">
							<td colspan="10"></td>
							<td class="tdRowLines">Friday
							</td>
						</tr>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${employeeShift.dayOffDto.saturday == true}">
						<tr class="tdInfo${employeeShift.id} tdInfo">
							<td colspan="10"></td>
							<td class="tdRowLines">Saturday
							</td>
						</tr>
					</c:when>
				</c:choose>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="10">${cscEmployeeShifts.dataSize + ((cscEmployeeShifts.currentPage - 1) *
				cscEmployeeShifts.pageSetting.maxResult)}/${cscEmployeeShifts.totalRecords}
			</td>
			<td colspan="2" style="text-align: right;">
			<c:if test="${cscEmployeeShifts.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${cscEmployeeShifts.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${cscEmployeeShifts.lastPage > 5}">
				<c:if test="${cscEmployeeShifts.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${cscEmployeeShifts.currentPage > 5}">
					<c:set var="modPage" value="${cscEmployeeShifts.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(cscEmployeeShifts.currentPage + (5 - modPage)) <= cscEmployeeShifts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${cscEmployeeShifts.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(cscEmployeeShifts.currentPage + (5 - modPage)) >= cscEmployeeShifts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${cscEmployeeShifts.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${cscEmployeeShifts.currentPage}" />
							<c:set var="minPageSet" value="${cscEmployeeShifts.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < cscEmployeeShifts.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>