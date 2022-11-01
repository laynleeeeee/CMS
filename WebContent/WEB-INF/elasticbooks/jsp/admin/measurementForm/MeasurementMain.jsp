<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description: Measurement main form. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Measurement Main Form</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<script type="text/javascript">
$(function() {
	$("#btnAddMeasurement").click(function () {
		$("#divMeasurementForm").load(contextPath + "/admin/unitMeasurement/form");
		$("html, body").animate({scrollTop: $("#divMeasurementForm").offset().top}, 0050);
	});

	$("#btnEditMeasurement").click(function () {
		var id = getCheckedId ("cbMeasurement");
		$("#divMeasurementForm").load(contextPath + "/admin/unitMeasurement/form?unitMeasurementId="+id);
		$("html, body").animate({scrollTop: $("#divMeasurementForm").offset().top}, 0050);
	});

	$("#txtName").bind("keypress", function (e) {
		if (e.which == 13) {
			searchUnitMeasurement();
			e.preventDefault();
		}
	});
});

function saveMeasurement() {
	$("#btnSaveMeasurement").attr("disabled", "disabled");
	doPostWithCallBack ("unitMeasurement", "divMeasurementForm", function (data) {
		if (data.startsWith("saved")) {
			$("#spanMeasurementMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") +  $("#name").val() + " unit of measure.");
			searchUnitMeasurement();
			$("#divMeasurementForm").html("");
		} else {
			$("#divMeasurementForm").html(data);
		}
	});
}

function cancelForm() {
	$("#divMeasurementForm").html("");
	searchUnitMeasurement();
}

function getCommonParam(){
	var measurementName = processSearchName($("#txtName").val());
	var isActive = $("#selectStatus").val();
	var isAllSelected = $("#selectStatus").val() == -1 ? true : false;
	if(isAllSelected)
		isActive = false;
	return "?name="+measurementName+"&isActive="+isActive+"&isAllSelected="+isAllSelected;
}

function searchUnitMeasurement() {
	doSearch ("divUMFormTable", "/admin/unitMeasurement"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
<div>
	<table class="formTable">
		<tr>
			<td width="15%" class="title">Name:</td>
			<td><input type="text" id="txtName" class="inputSmall"></td>
		</tr>
		<tr>
			<td width="15%" class="title">Status:</td>
			<td>
				<select id="selectStatus" class="frmSmallSelectClass">
				<option value=-1>All</option>
				<option value=true>Active</option>
				<option value=false>Inactive</option>
				</select> 

				<input type="button" id="btnSearchMeasurement" value="Search" onclick="searchUnitMeasurement();"/>
			</td>
		</tr>
	</table>
	</div>
	<span id="spanMeasurementMessage" class="message"></span>
	<div id="divUMFormTable">
		<%@ include file = "MeasurementFormTable.jsp" %>
	</div>
	<div class="controls"> 
		<input type="button" id="btnAddMeasurement" value="Add"  />
		<input type="button" id="btnEditMeasurement" value="Edit"  />
	</div>
	<div id="divMeasurementForm" style="margin-top: 50px;"></div>
</body>
</html>