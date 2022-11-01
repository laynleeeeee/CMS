<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Displays the list of time periods.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Time Period</title>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript">
$(function() {
	$("#btnAddTimePeriod").click(function() {
		$("#timePeriodForm").load(contextPath + "/admin/timePeriod/form");
		$("html, body").animate({scrollTop: $("#timePeriodForm").offset().top}, 0050);
	});

	$("#btnEditTimePeriod").click(function() {
		var id = getCheckedId("cbTimePeriod");
		$("#timePeriodForm").load(contextPath + "/admin/timePeriod/form?timePeriodId="+id);
		$("html, body").animate({scrollTop: $("#timePeriodForm").offset().top}, 0050);
	});

	$("#name,#selectPeriodStatus,#startDate,#endDate").bind("keypress", function (e) {
		keypress(e);
	});
});

function saveTimePeriod() {
	doPostWithCallBack ("timePeriodId", "timePeriodForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
					+ "time period " + $("#timePeriodName").val() + ".");
			$("#timePeriodForm").html("");
			searchTimePeriod();
		} else {
			$("#timePeriodForm").html(data);
		}
	});

}

function cancelForm() {
	$("#timePeriodForm").html("");
	searchTimePeriod();
}

function getCommonParam() {
	var name = processSearchName($("#name").val());
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var statusId = ($("#selectPeriodStatus").val());
	return "?name="+name+"&periodStatusId="+statusId+"&startDate="+startDate+"&endDate="+endDate;
}

function searchTimePeriod() {
	var url = "/admin/timePeriod" +  getCommonParam() + "&pageNumber=1";
	doSearch("timePeriodTable", url);
	$("html, body").animate({scrollTop: $("#timePeriodTable").offset().top}, 0050);
}

function keypress(e) {
	if (e.which == 13) {
		searchTimePeriod();
		e.preventDefault();
	}
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="tableInfo">
			<tr>
				<td width="10%" class="title">Name</td>
				<td><input type="text" id="name" size="15"/></td>
			</tr>
			<tr>
				<td width="10%" class="title">Status</td>
				<td><select id="selectPeriodStatus">
						<option id="statusAll" value="-1">All</option>
							<c:forEach var="status" items="${periodStatus}">
								<option id="status${status.id}" value="${status.id}">${status.name}</option>
							</c:forEach>
				</select> </td>
			</tr>
			<tr>
				<td class="title">Date </td>
				<td class="tdDateFilter">
					<input type="text" id="startDate" maxlength="10" class="dateClass">
						<img src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('startDate')" style="cursor:pointer"
								style="float: right;"/>
					<span>To</span>
					<input type="text" id="endDate" maxlength="10" class="dateClass">
						<img src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('endDate')" style="cursor:pointer"
								style="float: right;"/>
				</td>
				<td align="right" colspan="2"><input type="button" id="btnSearchTimePeriod"
						value="Search" onclick="searchTimePeriod();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="timePeriodTable">
		<%@ include file ="TimePeriodTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddTimePeriod" value="Add"></input>
		<input type="button" id="btnEditTimePeriod" value="Edit"></input>
	</div>
	<div id="timePeriodForm" style="margin-top: 20px;">
	</div>
</body>
</html>