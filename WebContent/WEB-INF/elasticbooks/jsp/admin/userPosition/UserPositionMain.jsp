<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<!--     Description: User Position Main -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>User Position</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">

$(function () {
	$("#btnAddUserPosition").on("click", function () {
		$("#divUserPositionForm").load(contextPath + "/admin/userPositions/form", function () {
			$("html, body").animate({scrollTop: $("#divUserPositionForm").offset().top}, 0050);
		});
	});
	
	$("#btnEditUserPosition").on("click", function () {
		var id = getCheckedId ("cbPositions");
		$("#divUserPositionForm").load(contextPath + "/admin/userPositions/form?pId="+id, function () {
			$("html, body").animate({scrollTop: $("#divUserPositionForm").offset().top}, 0050);
		});
	});

	$("#txtName, #selectStatus").bind("keypress", function (e) {
		if(e.which == 13){
			searchPositions();
			e.preventDefault();
		}
	});
});

function savePosition() {
	$("#btnSavePosition").attr("disabled", "disabled");
	doPostWithCallBack ("userPosition", "divUserPositionForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanPositionMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "Position " + $("#positionName").val() + ".");
			$("#divUserPositionForm").html("");
			$("html, body").animate({scrollTop: $("#divSearch").offset().top}, 0050);
			searchPositions();
		} else {
			$("#divUserPositionForm").html(data);
		}
	});
}

function cancelForm() {
	$("#divUserPositionForm").html("");
	$("html, body").animate({scrollTop: $("#dataTable").offset().top}, 0050);
	searchPositions();
}

function getCommonParam(){
	var name = processSearchName($("#txtName").val());
	var status = $("#selectStatus").val();
	return "name="+name+"&status="+status;
}

function searchPositions() {
	doSearch("divUserPositionTable","/admin/userPositions?"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
<div id = "divSearch">
	<table class="formTable">
		<tr>
			<td width="20%" class="title">Name:</td>
			<td>
				<input id="txtName"/>
			</td>
		</tr>
		<tr>
			<td width="20%" class="title">Status:</td>
			<td>
				<select id="selectStatus" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<option value="1">Active</option>
					<option value="0">Inactive</option>
				</select>
				<input type="button" id="btnSearchPosition" value="Search" onclick="searchPositions();"/>
			</td>
		</tr>
	</table>
</div>
<span id="spanPositionMessage" class="message"></span>
<div id="divUserPositionTable">
	<%@ include file = "UserPositionTable.jsp" %>
</div>
<div class="controls">
	<input type="button" id="btnAddUserPosition" value="Add" />
	<input type="button" id="btnEditUserPosition" value="Edit" />
</div>

<div id="divUserPositionForm" class="formDiv" style="margin-top: 50px;"></div>
</body>
</html>