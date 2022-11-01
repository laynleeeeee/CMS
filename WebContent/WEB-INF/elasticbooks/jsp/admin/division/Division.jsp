<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Displays the list of divisions.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Division</title>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
$(function() {
	$("#btnAddDivision").click(function() {
		$("#divisionForm").load(contextPath + "/admin/division/form");
		$("html, body").animate({scrollTop: $("#divisionForm").offset().top}, 0050);
	});

	$("#btnEditDivision").click(function() {
		var id = getCheckedId ("cbDivision");
		$("#divisionForm").load(contextPath + "/admin/division/form?divisionId="+id);
		$("html, body").animate({scrollTop: $("#divisionForm").offset().top}, 0050);
	});

	$("#number").keypress(function (e) {
		keypress(e);
	});

	$("#name").keypress(function (e) {
		keypress(e);
	});
});

function saveDivision() {
	$("#parentDivisionName").val($.trim($("#txtParentDivision").val()));
	$("#btnSaveDivision").attr("disabled", "disabled");
	doPostWithCallBack ("division", "divisionForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "division " 
		 			+ $("#divisionName").val() + ".");
			$("#divisionForm").html("");
			searchDivision();
		} else {
			var parentDivision = $("#txtParentDivision").val();
			$("#divisionForm").html(data);
			$("#txtParentDivision").val(parentDivision);
		}
		$("#btnSaveDivision").removeAttr("disabled");
	});
}

function cancelForm() {
	$("#divisionForm").html("");
	searchDivision();
}

function getCommonParam(){
	var number = processSearchName($("#number").val());
	var name = processSearchName($("#name").val());
	var status = $("#selectStatus").val();
	return "?number="+number+"&name="+name+"&status="+status;
}

function searchDivision(){
	var param = getCommonParam()+"&pageNumber=1";
	var url ="/admin/division"+ param;
	doSearch("divisionTable", url);
	$("html, body").animate({scrollTop: $("#divisionTable").offset().top}, 0050);
}

function keypress(e) {
	if (e.which == 13) {
		searchDivision();
		e.preventDefault();
	}
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td class="title"> Number</td>
				<td class="value"><input type="text" id="number" class="inputSmall"/></td>
			</tr>
			<tr>
				<td class="title"> Name</td>
				<td class="value"><input type="text" id="name" class="inputSmall"/></td>
			</tr>
			<tr>
				<td class="title"> Status</td>
				<td class="value">
					<select id="selectStatus" class="frmSmallSelectClass">
						<c:forEach var="status" items="${status}">
								<option value="${status}">${status}</option>
						</c:forEach>
					</select>&nbsp;
					<input type="button" id="btnSearchDivision" value="Search" onclick="searchDivision();"/>
				</td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divisionTable">
		<%@ include file ="DivisionTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddDivision" value="Add"></input>
		<input type="button" id="btnEditDivision" value="Edit"></input>
	</div>
	<div id="divisionForm" style="margin-top: 20px;">
	</div>
</body>
</html>