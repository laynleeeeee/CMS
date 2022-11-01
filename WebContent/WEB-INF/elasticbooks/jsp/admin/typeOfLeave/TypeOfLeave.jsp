<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		Description: Type of leave main page.
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
$(function () {
	$("#txtName, #slctStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchLeaveType();
			e.preventDefault();
		}
	});
});

function addLeaveType() {
	$("#divTypeOfLeaveForm").load(contextPath + "/admin/typeOfLeave/form");
	$("html, body").animate({scrollTop: $("#divTypeOfLeaveForm").offset().top}, 0050);
}

function editLeaveType() {
	var id = getCheckedId("cbTypeOfLeaveType");
	$("#divTypeOfLeaveForm").load(contextPath + "/admin/typeOfLeave/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divTypeOfLeaveForm").offset().top}, 0050);
}

var isSaving = false;
function saveLeaveType() {
	if(isSaving == false) {
		isSaving = true;
		$("#btnSave").attr("disabled", "disabled");
		doPostWithCallBack ("typeOfLeaveId", "divTypeOfLeaveForm", function(data) {
			if (data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "leave type: " + $("#name").val() + ".");
				searchLeaveType();
				$("#divTypeOfLeaveForm").html("");
			} else {
				$("#divTypeOfLeaveForm").html(data);
			}
			isSaving = false;
		});
	}
}

function cancelLeaveType() {
	$("#divTypeOfLeaveForm").html("");
	$("html, body").animate({scrollTop: $("#divTypeOfLeaveTbl").offset().top}, 0050);
}

function getCommonParam() {
	var txtName = $.trim(processSearchName($("#txtName").val()));
	var status = $("#slctStatus").val();
	return "?name="+txtName+"&status="+status;
}

function searchLeaveType() {
	doSearch ("divTypeOfLeaveTbl", "/admin/typeOfLeave/search"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td class="title">Types of Leave</td>
				<td class="value"><input type="text" id="txtName" class="inputSmall" ></td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td class="value"><select id="slctStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				</td>
				<td><input type="button" value="Search" onclick="searchLeaveType();"/></td>
			</tr>
			<tr>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divTypeOfLeaveTbl">
		<%@ include file="TypeOfLeaveTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAdd" value="Add" onclick="addLeaveType();"></input>
		<input type="button" id="btnEdit" value="Edit" onclick="editLeaveType();"></input>
	</div>
	<div id="divTypeOfLeaveForm" style="margin-top: 50px;"></div>
</body>
</html>