<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description: Fleet Type main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Items</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
var SEARCH_URL = "/admin/fleetType/search";
var FORM_URL = contextPath+"/admin/fleetType/form";

function searchFleetTypes() {
	var param = getCommonParam()+"&pageNumber=1";
	doSearch("divFleetTypeTable", SEARCH_URL+param);
}

$(function () {
	$("#fleetTypeName").on("keypress", function (e) {
		if (e.which == 13) {
			searchFleetTypes();
			e.preventDefault();
		}
	});
});

function getCommonParam() {
	var name = processSearchName($.trim($("#fleetTypeName").val()));
	var status = $("#selectStatusId").val();
	var companyId = $("#selectCompany").val();
	var fleetCategoryId = $("#selectCategory").val();
	return "?name="+name+"&status="+status+"&companyId="+companyId+"&fleetCategoryId="+fleetCategoryId;
}

function addFleetType() {
	$("#divFleetTypeForm").load(FORM_URL, function (){
		$("html, body").animate({
			scrollTop: $("#divFleetTypeForm").offset().top}, 0050);
	});
}

function editFleetType() {
	var id = getCheckedId("cbFleetType");
	$("#divFleetTypeForm").load(FORM_URL+"?pId="+id, function (){
		$("html, body").animate({
			scrollTop: $("#divFleetTypeForm").offset().top}, 0050);
	});
}
</script>
</head>
<body>
	<div id="divSearch">
		<table class="formTable">
			<tr>
				<td width="25%" class="title">Company</td>
				<td>
					<select id="selectCompany" class="frmSmallSelectClass">
						<option value="-1">All</option>
						<c:forEach var="comp" items="${companies}">
							<option value="${comp.id}">${comp.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td width="25%" class="title">Category</td>
				<td>
					<select id="selectCategory" class="frmSmallSelectClass">
						<option value="-1">All</option>
						<c:forEach var="cat" items="${categories}">
							<option value="${cat.id}">${cat.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="title">Fleet Type</td>
				<td>
					<input type="text" id="fleetTypeName" maxLength="100" size="20" class="inputSmall"/>
				</td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td><select id="selectStatusId" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" id="btnSearchFleetType" value="Search" onclick="searchFleetTypes();"/>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divFleetTypeTable">
		<%@ include file="FleetTypeTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddFleetType" value="Add" onclick="addFleetType();"/>
		<input type="button" id="btnEditFleetType" value="Edit" onclick="editFleetType();"/>
	</div>
	<div id="divFleetTypeForm" style="margin-top: 50px;">
	</div>
</body>
</html>