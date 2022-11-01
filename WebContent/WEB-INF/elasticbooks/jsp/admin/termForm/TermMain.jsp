<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Term main form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Term Main Form</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	numericInputOnly();
});

function numericInputOnly() {
	$("#txtTermDay").bind("keyup keydown", function(e) {
		// 46 = DELETE button. If Delete button is not pressed
		if(e.keyCode != 46 && e.keyCode != 13){ 
			inputOnlyNumeric("txtTermDay");
		}
	});
}

$(function() {
	$("#btnAddTerm").click(function () {
		$("#divTermForm").load(contextPath + "/admin/termForm/form");
		$("html, body").animate({scrollTop: $("#divTermForm").offset().top}, 0050);
	});

	$("#btnEditTerm").click(function () {
		var id = getCheckedId ("cbTerm");
		$("#divTermForm").load(contextPath + "/admin/termForm/form?termId="+id);
		$("html, body").animate({scrollTop: $("#divTermForm").offset().top}, 0050);
	});

	$("#txtTermName, #txtTermDay").bind("keypress", function (e) {
		if (e.which == 13) {
			searchTerm();
			e.preventDefault();
		}
	});

	$("#daysTxt").bind("keypress", function() {
		inputOnlyNumeric("daysTxt");
	});
});

function saveTerm() {
	$("#btnSaveTerm").attr("disabled", "disabled");
	doPostWithCallBack ("term", "divTermForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanBusMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "Term name " + $("#name").val() + ".");
			$("#divTermForm").html("");
			searchTerm();
		} else {
			$("#divTermForm").html(data);
		}
	});
}

function cancelForm() {
	$("#divTermForm").html("");
	searchTerm();
}

function getCommonParam() {
	var termName = processSearchName($("#txtTermName").val());
	var days = processSearchName(removeSpace($("#txtTermDay").val()));
	var status = $("#selectStatus").val();
	return "termName="+termName+"&days="+days+"&status="+status;
}

function searchTerm(){
	doSearch ("divTermFormTable", "/admin/termForm?"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
	<div>
		<table class="formTable">
			<tr>
				<td class="title">Term Name</td>
				<td><input type="text" id="txtTermName" class="inputSmall"></td>
			</tr>
			<tr>
				<td class="title">Day</td>
				<td><input type="text" id="txtTermDay" class="inputSmall" maxlength="10"></td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td>
					<select id="selectStatus" class="frmSmallSelectClass">
						<option value=-1>All</option>
						<option value=1>Active</option>
						<option value=0>Inactive</option>
					</select>
					<input type="button" id="btnSearchTerm" value="Search" onclick="searchTerm();"/>
				</td>
			</tr>
		</table>
	</div>
	<span id="spanBusMessage" class="message"></span>
	<div id="divTermFormTable">
		<%@ include file = "TermTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddTerm" value="Add" />
		<input type="button" id="btnEditTerm" value="Edit" />
	</div>
	<div id="divTermForm" style="margin-top: 50px;"></div>
</body>
</html>