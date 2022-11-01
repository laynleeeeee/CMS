<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

	Description: Payroll Time Period Main Page -->
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
$(document).ready(function(){
	searchPTimePeriod();
});

$("#txtName, #selectMonthId, #selectStatus, #selectYearId").bind("keypress", function (e) {
	if (e.which == 13) {
		searchPTimePeriod();
		e.preventDefault();
	}
});

function addPTimePeriod() {
	$("#divPayrollTimePeriodForm").load(contextPath + "/admin/payrollTimePeriod/form");
	$("html, body").animate({scrollTop: $("#divPayrollTimePeriodForm").offset().top}, 0050);
}

function editPTimePeriod() {
	var id = getCheckedId("cbPtp");
	$("#divPayrollTimePeriodForm").load(contextPath + "/admin/payrollTimePeriod/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divPayrollTimePeriodForm").offset().top}, 0050);
}

function cancelPTimePeriod() {
	$("#divPayrollTimePeriodForm").html("");
	$("html, body").animate({scrollTop: $("#divPayrollTimePeriodTbl").offset().top}, 0050);
}

function reformatDates() {
	$("#timeSheetTable tbody tr").each(function () {
		formatDate(this, "dateFrom");
		formatDate(this, "dateTo");
	});
}

function formatDate($row, dateClass) {
	var $dateObj = $($row).find("." + dateClass);
	var dateVal = $($dateObj).val();
	if (dateVal != "") {
		dateVal = getFormattedDate(new Date(dateVal));
		$($dateObj).val(dateVal);
	}
}

var isSaving = false;
function savePTimePeriod() {
	if(isSaving == false) {
		isSaving = true;
		if($timeSheetTable != null) {
			$("#payrollTimeScheduleJsonId").val($timeSheetTable.getData());
		}
		$("#btnSavePtp").attr("disabled", "disabled");
		doPostWithCallBack ("payrollTimePeriodForm", "divPayrollTimePeriodForm", function (data) {
			if (data.substring(0,5) == "saved") {
				$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ")
						+ "Payroll Time Period: " + $("#txtNameId").val() + ".");
				searchPTimePeriod();
				$("#divPayrollTimePeriodForm").html("");
			} else {
				$("#divPayrollTimePeriodForm").html(data);
				reformatDates();
			}
			isSaving = false;
		});
	}
}

function getCommonParam() {
	var txtName = processSearchName($.trim($("#txtName").val()));
	var month = $("#selectMonthId").val();
	var year = $("#selectYearId").val();
	var status = $("#selectStatus").val();
	return "?name="+txtName+"&month="+month+"&year="+year+"&status="+status;
}

function searchPTimePeriod() {
	doSearch ("divPayrollTimePeriodTbl", 
		"/admin/payrollTimePeriod/search"+getCommonParam()+"&pageNumber=1");
}

function setContributions() {
	$(".radioButton").each(function() {
		$(this).closest("tr").find(".computeContributions").val($(this).prop("checked"));
	});
}
</script>
</head>
<body>
	<div class="searchDiv">
		<table class="formTable">
			<tr>
				<td class="title" >Name </td>
				<td colspan="2">
					<input type="text" id="txtName" class="frmInput">
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
				<td class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				</td>
				<td></td>
				<td>
					<input type="button" value="Search" onclick="searchPTimePeriod();"/>
				</td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divPayrollTimePeriodTbl">
		<%@ include file="PayrollTimePeriodTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddPtp" value="Add" onclick="addPTimePeriod();"></input>
		<input type="button" id="btnEditPtp" value="Edit" onclick="editPTimePeriod();"></input>
	</div>
	<br>
	<br>
	<div id="divPayrollTimePeriodForm" style="margin-top: 20px;"></div>
</body>
</html>