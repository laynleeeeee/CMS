<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

	Description: Holiday Setting Main Page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebForm.css"media="all">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"media="all">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript">
var HAS_ERROR = false;
var isSaving = false;
$(function(){
	$("#slctCompany, #txtName, #date, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchHolidaySetting();
			e.preventDefault();
		}
	});
});

function addHolidaySetting() {
	$("#divHolidaySettingForm").load(contextPath + "/admin/holidaySetting/form", function() {
		$("html, body").animate({scrollTop: $("#divHolidaySettingForm").offset().top}, 0050);
	});
}
function cancelHolidaySetting() {
	$("#divHolidaySettingForm").html("");
	$("html, body").animate({scrollTop: $("#divHolidaySettingTbl").offset().top}, 0050);
}
function saveHolidaySetting(){
	if(isSaving == false && !HAS_ERROR) {
		isSaving = true;
		$("#btnSaveHolidaySetting").attr("disabled", "disabled");
		doPostWithCallBack ("frmHolidaySetting", "divHolidaySettingForm", function(data) {
			if (data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "holiday setting: " + $("#txtNameId").val() + ".");
				$("#divHolidaySettingForm").html("");
				searchHolidaySetting();
			} else {
				$("#divHolidaySettingForm").html(data);
			}
			isSaving = false;
		});
	}
}

function getCommonParam(){
	var companyId = $("#slctCompany").val();
	var holidayTypeId = $("#slctHolidayType").val();
	var name = encodeURIComponent($.trim($("#txtName").val()));
	var date = $("#date").val();
	var status = $.trim($("#selectStatus").val());
	var url =  "?companyId="+companyId
			+"&name="+name+"&holidayTypeId="+holidayTypeId+"&date="+date+"&status="+status;
	return url;
}

function editHolidaySetting() {
	var id = getCheckedId("cbHolidaySetting");
	$("#divHolidaySettingForm").load(contextPath + "/admin/holidaySetting/form?pId="+id, function() {
		$("html, body").animate({
			scrollTop: $("#divHolidaySettingForm").offset().top}, 0050);
	});
}

function searchHolidaySetting(){
	doSearch("divHolidaySettingTbl", "/admin/holidaySetting/search"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Company</td>
				<td><select class="frmSmallSelectClass" id="slctCompany">
						<option value="-1">ALL</option>
						<c:forEach var="companies" items="${companies}">
							<option value="${companies.id}">${companies.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Name</td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Holiday Type</td>
				<td><select class="frmSmallSelectClass" id="slctHolidayType">
						<option value="-1">ALL</option>
						<c:forEach var="holidayType" items="${holidayTypes}">
							<option value="${holidayType.id}">${holidayType.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Date</td>
				<td><input id="date" onblur="evalDate('date')"
					style="width: 120px;" class="dateClass2" /> <img id="imgDate1"
					src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('date')" style="cursor: pointer"
					style="float: right;" /></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
						<c:forEach var="status" items="${status}">
							<option>${status}</option>
						</c:forEach>
				</select> <input type="button" value="Search"
					onclick="searchHolidaySetting();" /></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divHolidaySettingTbl">
		<%@ include file="HolidaySettingTable.jsp"%>
	</div>
	<div class="controls">
		<input type="button" id="btnAddHolidaySetting" value="Add"
			onclick="addHolidaySetting();"></input> <input type="button"
			id="btnEditHolidaySetting" value="Edit"
			onclick="editHolidaySetting();"></input>
	</div>
	<br>
	<br>
	<div id="divHolidaySettingForm" style="margin-top: 20px;"></div>
</body>
</html>