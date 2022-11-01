<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Time sheet reference table.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery/jquery-ui-1.10.3.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/accounting.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/css/jquery-ui-1.10.3.css" rel="stylesheet" type="text/css"  />
<style>
html {
  overflow-x: hidden;
  overflow-y: auto;
}

a.plus {
	text-decoration: none;
	font-weight: bold;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblTSReference thead tr").find("th:last").css("border-right", "none");
	$("#tblTSReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblTSReference tbody").find("td").addClass("tdProperties");

	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	$("#divTSRefTable").load(getCommonParam()+"&pageNumber=" + pageNumber);
}

function populateTS(checkbox) {
	if(tsId == null){
		tsId = checkbox;
	}else{
		if ($("input[type=checkbox]").length > 1) 
			$(tsId).prop("checked", false);
		tsId = $(checkbox);
	}
	if ($("input[type=checkbox]:checked").length == 0)
		tsId = null;
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table style="margin-top : 20px;" id="tblTSReference" class="dataTable">
		<thead>
			<tr>
				<th width="3%"></th>
				<th width="23%">Company</th>
				<th width="17%">Department</th>
				<th width="10%">Sequence No.</th>
				<th width="10%">Date</th>
				<th width="12%">Month/Year</th>
				<th width="25%">Time Period</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="ts" items="${timeSheet.data}">
			<tr>
				<td style="border-left: none;">
					<input id="${ts.id}" name="cbCS"
							class="cbCS" type="checkbox" onclick="populateTS(this);"/>
				</td>
				<td>${ts.company.name}</td>
				<td>${ts.division.name}</td>
				<td align="right">${ts.sequenceNumber}</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${ts.date}" /></td>
				<td>
					<c:choose>
						<c:when test="${ts.payrollTimePeriod.month eq 1}">JANUARY</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 2}">FEBRUARY</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 3}">MARCH</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 4}">APRIL</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 5}">MAY</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 6}">JUNE</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 7}">JULY</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 8}">AUGUST</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 9}">SEPTEMBER</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 10}">OCTOBER</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 11}">NOVEMBER</c:when>
						<c:when test="${ts.payrollTimePeriod.month eq 12}">DECEMBER</c:when>
					</c:choose>
					${ts.payrollTimePeriod.year}
				</td>
				<td>${ts.payrollTimePeriodSchedule.name}</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">${timeSheet.dataSize + ((timeSheet.currentPage - 1) *
					timeSheet.pageSetting.maxResult)}/${timeSheet.totalRecords}
				</td>
				<td colspan="4" style="text-align: right;">
				<c:if test="${timeSheet.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${timeSheet.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				<c:if test="${timeSheet.lastPage > 5}">
					<c:if test="${timeSheet.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>
					<c:if test="${timeSheet.currentPage > 5}">
						<c:set var="modPage" value="${timeSheet.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(timeSheet.currentPage + (5 - modPage)) <= timeSheet.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${timeSheet.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(timeSheet.currentPage + (5 - modPage)) >= timeSheet.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${timeSheet.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${timeSheet.currentPage}" />
								<c:set var="minPageSet" value="${timeSheet.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<c:if test="${(maxPageSet) < timeSheet.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>