<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		 Description: Driver admin setting main page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
</head>
<script type="text/javascript">
$(function () {
	$("#txtName, #txtPosition, #slctCompany").bind("keypress", function (e) {
		if (e.which == 13) {
			searchDriver();
			e.preventDefault();
		}
	});
});

function addDriver() {
	$("#divDriverForm").load(contextPath + "/admin/driver/form");
	$("html, body").animate({scrollTop: $("#divDriverForm").offset().top}, 0050);
}

function editDriver() {
	var id = getCheckedId("cbDriver");
	$("#divDriverForm").load(contextPath + "/admin/driver/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divDriverForm").offset().top}, 0050);
}

function cancelDriver() {
	$("#divDriverForm").html("");
	$("html, body").animate({scrollTop: $("#divDriverTbl").offset().top}, 0050);
}

var isSaving = false;
function saveDriver() {
	if(isSaving == false) {
		isSaving = true;
		var gender = $("#genderId").val();
		var civilStatus = $("#civilStatusId").val();
		$("#btnSaveDriver").attr("disabled", "disabled");
		doPostWithCallBack ("driverFormId", "divDriverForm", function(data) {
			if (data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "driver: " + $("#firstName").val() 
					+ " " + $("#lastName").val() + ".");
				$("#divDriverForm").html("");
				searchDriver();
			} else {
				var position = $("#positionNameId").val();
				var gender = $("#genderId").val();
				var civilStatus = $("#civilStatusId").val();
				$("#divDriverForm").html(data);
				$("#positionNameId").val(position);
				$("#genderId").val(gender);
				$("#civilStatusId").val(civilStatus);
			}
			isSaving = false;
		});
	}
}

function getCommonParam() {
	var txtName = processSearchName($("#txtName").val());
	var companyId = $("#slctCompany").val();
	var status = $("#selectStatus").val();
	return "?name="+txtName+"&companyId="+companyId+"&status="+status;
}

function searchDriver() {
	doSearch ("divDriverTbl", "/admin/driver/search"+getCommonParam()+"&pageNumber=1");
}
</script>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Company </td>
				<td>
					<select id="slctCompany" class="frmSmallSelectClass">
						<option value=-1>ALL</option>
						<c:forEach var="c" items="${searchSlctCompanies}">
							<option value="${c.id}">${c.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Name </td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
					<input type="button" value="Search" onclick="searchDriver();"/>
				</td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divDriverTbl">
		<%@ include file="DriverTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddDriver" value="Add" onclick="addDriver();"></input>
		<input type="button" id="btnEditDriver" value="Edit" onclick="editDriver();"></input>
	</div>
	<br>
	<br>
	<div id="divDriverForm" style="margin-top: 20px;"></div>
</body>
</html>