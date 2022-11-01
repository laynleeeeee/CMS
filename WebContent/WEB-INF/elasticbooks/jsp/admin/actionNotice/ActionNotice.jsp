<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Action Notice main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(function(){
	$("#txtName, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchActionNotice();
			e.preventDefault();
		}
	});
	$("#btnAddActionNotice").click(function () {
		$("#divActionNoticeForm").load(contextPath + "/admin/actionNotice/form");
		$("html, body").animate({scrollTop: $("#divActionNoticeForm").offset().top}, 0050);
	});
});

$("#btnEditActionNotice").click(function () {
	var id = getCheckedId ("cbActionNotice");
	$("#divActionNoticeForm").load(contextPath + "/admin/actionNotice/form?actionNoticeId="+id);
	$("html, body").animate({scrollTop: $("#divActionNoticeForm").offset().top}, 0050);
});

function saveActionNotice(){
	$("#btnSaveActionNotice").attr("disabled", "disabled");
	doPostWithCallBack("actionNotice", "divActionNoticeForm", function(data){
		if (data.substring(0,5) == "saved") {
			$("#spanBusMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "Action Notice " + $("#name").val() + ".");
			$("#divActionNoticeForm").html("");
			searchActionNotice();
		} else {
			$("#divActionNoticeForm").html(data);
		}
	});
}

function getCommonParam() {
	var name = processSearchName($("#txtName").val());
	var status = $("#selectStatus").val();
	return "name="+name+"&status="+status;
}

function searchActionNotice(){
	doSearch ("divActionNoticeTable", "/admin/actionNotice?"+getCommonParam()+"&pageNumber=1");
}
function cancelForm() {
	$("#divActionNoticeForm").html("");
	searchActionNotice();
}
</script>
</head>
<body>
	<div>
		<table class="formTable">
			<tr>
				<td class="title">Name</td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
						<option value=-1>All</option>
						<option value=1>Active</option>
						<option value=0>Inactive</option>
				</select> <input type="button" id="btnSearchActionNotice" value="Search"
					onclick="searchActionNotice();" /></td>
			</tr>
		</table>
	</div>
	<span id="spanBusMessage" class="message"></span>
	<div id="divActionNoticeTable">
		<%@ include file="ActionNoticeTable.jsp"%>
	</div>
	<div class="controls">
		<input type="button" id="btnAddActionNotice" value="Add" /> <input
			type="button" id="btnEditActionNotice" value="Edit" />
	</div>
	<div id="divActionNoticeForm" style="margin-top: 50px;"></div>
</body>
</html>