<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description	: Sales personnel main page
	 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
});
$(function () {
	$("#selectCompany, #txtName, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			search();
			e.preventDefault();
		}
	});
});

function addSalesPersonnel() {
	$("#divSalesPersonnelForm").load(contextPath + "/admin/salesPersonnel/form");
	$("html, body").animate({scrollTop: $("#divSalesPersonnelForm").offset().top}, 0050);
}

function editSalesPersonnel() {
	var id = getCheckedId("cbSalesPersonnel");
	$("#divSalesPersonnelForm").load(contextPath + "/admin/salesPersonnel/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divSalesPersonnelForm").offset().top}, 0050);
}

var isSaving = false;
function saveSalesPersonnel() {
	if(isSaving == false) {
		isSaving = true;
		$("#btnSave").attr("disabled", "disabled");
		doPostWithCallBack ("salesPersonnelFormId", "divSalesPersonnelForm", function(data) {
			if(data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "sales personnel: " + $("#name").val() + ".");
				$("#divSalesPersonnelForm").html("");
				search();
			} else {
				$("#divSalesPersonnelForm").html(data);
			}
			isSaving = false;
		});
	}
}

function cancelSalesPersonnel() {
	$("#divSalesPersonnelForm").html("");
	$("html, body").animate({scrollTop: $("#divSalesPersonnelTbl").offset().top}, 0050);
}

function getCommonParam() {
	var companyId = $("#selectCompany").val();
	var txtName = processSearchName($("#txtName").val());
	var status = $("#selectStatus").val();
	return "?companyId="+companyId+"&name="+txtName+"&status="+status;
}

function search() {
	doSearch ("divSalesPersonnelTbl", "/admin/salesPersonnel/search"+getCommonParam()+"&pageNumber=1");
}

</script>
<style>
.hide {
	display: none;
}
</style>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Company</td>
				<td><select id="selectCompany" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="comp" items="${companies}">
						<option value="${comp.id}">${comp.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td width="15%" class="title">Name</td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="search();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divSalesPersonnelTbl">
		<%@ include file="SalesPersonnelTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddSalesPersonnel" value="Add" onclick="addSalesPersonnel();"></input>
		<input type="button" id="btnEditSalesPersonnel" value="Edit" onclick="editSalesPersonnel();"></input>
	</div>
	<br>
	<br>
	<div id="divSalesPersonnelForm" style="margin-top: 20px;">
	</div>
</body>
</html>