<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

	Description: Monthly shift schedule main page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Payroll Time Period</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<style type="text/css">
.frmInput{
	border: 1px solid gray;
	padding: 3px;
	width: 348px;
}

.tdRowLines {
	border-top: 1px solid #C0C0C0;
}
</style>
<script type="text/javascript">
$(function () {
	$(".frmSelectClass, .frmSmallSelectClass").bind("keypress", function (e) {
		if (e.which == 13) {
			searchDailyShift();
			e.preventDefault();
		}
	});
});

function addPTimePeriod() {
	$("#divPMonthlyShiftScheduleForm").load(contextPath + "/admin/monthlyShiftSchedule/form");
	$("html, body").animate({scrollTop: $("#divPMonthlyShiftScheduleForm").offset().top}, 0050);
}

function editPTimePeriod() {
	var id = getCheckedId("cbPtp");
	$("#divPMonthlyShiftScheduleForm").load(contextPath + "/admin/monthlyShiftSchedule/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divPMonthlyShiftScheduleForm").offset().top}, 0050);
}

function cancelPTimePeriod() {
	$("#divPMonthlyShiftScheduleForm").html("");
	$("html, body").animate({scrollTop: $("#divPayrollTimePeriodTbl").offset().top}, 0050);
}

function searchDailyShift() {
	doSearch ("divPayrollTimePeriodTbl", "/admin/monthlyShiftSchedule/search"+getCommonParam()+"&pageNumber=1");
}

function getCommonParam() {
	var companyId = $("#slctCompany").val();
	var month = $("#selectMonthId").val();
	var year = $("#selectYearId").val();
	var status = $.trim($("#selectStatus").val());
	return "?companyId="+companyId+"&month="+month+"&year="+year+"&status="+status;
}

</script>
</head>
<body>
	<div class="searchDiv">
		<table class="formTable">
			<tr>
				<td width="25%" class="title">Company</td>
				<td>
					<select id="slctCompany" class="frmSelectClass">
						<option value="-1">ALL</option>
						<c:forEach var="c" items="${companies}">
							<option value="${c.id}">${c.name}</option>
						</c:forEach>
					</select>
				</td>
			<tr>
				<td class="title">Month/Year </td>
				<td colspan=2>
					<select id="selectMonthId" class="frmSmallSelectClass">
						<option value="-1">ALL</option>
						<c:forEach var="mm" items="${months}">
							<option value="${mm.month}">${mm.name}</option>
						</c:forEach>
					</select>
					<select id="selectYearId" class="frmSmallSelectClass">
						<option value="-1">ALL</option>
						<c:forEach var="yy" items="${years}">
							<option value="${yy}">${yy}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
						<c:forEach var="status" items="${status}">
							<option>${status}</option>
						</c:forEach>
				</select> 
				<input type="button" value="Search" onclick="searchDailyShift();"/>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divPayrollTimePeriodTbl">
		<%@ include file="MonthlyShiftScheduleTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddPtp" value="Add" onclick="addPTimePeriod();"></input>
		<input type="button" id="btnEditPtp" value="Edit" onclick="editPTimePeriod();"></input>
	</div>
	<br>
	<br>
	<div id="divPMonthlyShiftScheduleForm" style="margin-top: 20px;"></div>
</body>
</html>