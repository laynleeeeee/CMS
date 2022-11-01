<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 


	Description: Warehouse main page.
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
var SEARCH_URL = "/admin/warehouses/search";
var FORM_URL = contextPath+"/admin/warehouses/form";

function searchWarehouses() {
	var param = getCommonParam()+"&page=1";
	doSearch("divWarehouseTable", SEARCH_URL+param);
}

$(function () {
	$("#txtWName").on("keypress", function (e) {
		if (e.which == 13) {
			searchWarehouses();
			e.preventDefault();
		}
	});
});

$(function () {
	$("#txtWAddress").on("keypress", function (e) {
		if (e.which == 13) {
			searchWarehouses();
			e.preventDefault();
		}
	});
});

function getCommonParam() {
	var companyId = $("#searchCompanyId").val();
	var name = processSearchName($.trim($("#txtWName").val()));
	var status = $("#selectStatusId").val();
	var address = processSearchName($("#txtWAddress").val());
	var divisionId = $("#slctDivisionId").val();
	return "?companyId="+companyId+"&name="+name+"&address="+address+"&status="+status+"&divisionId="+divisionId;
}

function addWarehouse() {
	$("#divWarehouseForm").load(FORM_URL, function (){
		$("html, body").animate({
			scrollTop: $("#divWarehouseForm").offset().top}, 0050);
	});
}

function editWarehouse() {
	var id = getCheckedId("cbWarehouse");
	$("#divWarehouseForm").load(FORM_URL+"?pId="+id, function (){
		$("html, body").animate({
			scrollTop: $("#divWarehouseForm").offset().top}, 0050);
	});
}
</script>
</head>
<body>
	<div id="divSearch">
		<table class="formTable">
			<tr>
				<td class="title">Company</td>
				<td class="value">
					<select id="searchCompanyId" class="frmSmallSelectClass">
						<option value="-1">All</option>
						<c:forEach var="company" items="${companies}">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="title">Division</td>
				<td class="value">
					<select id="slctDivisionId" class="frmSmallSelectClass">
						<option value="-1">All</option>
						<c:forEach var="division" items="${divisions}">
							<option value="${division.id}">${division.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="title">Warehouse</td>
				<td class="value">
					<input type="text" id="txtWName" maxLength="100" size="20" class="inputSmall"/>
				</td>
			</tr>
			<tr>
				<td class="title">Address</td>
				<td class="value">
					<input type="text" id="txtWAddress" maxLength="100" size="20" class="inputSmall"/>
				</td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td class="value">
					<select id="selectStatusId" class="frmSmallSelectClass">
						<c:forEach var="status" items="${status}">
								<option>${status}</option>
						</c:forEach>
					</select>&nbsp;&nbsp;
				<input type="button" id="btnSearchWarehouse" value="Search" onclick="searchWarehouses();"/>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divWarehouseTable">
		<%@ include file="WarehouseTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddWarehouse" value="Add" onclick="addWarehouse();"/>
		<input type="button" id="btnEditWarehouse" value="Edit" onclick="editWarehouse();"/>
	</div>
	<div id="divWarehouseForm" style="margin-top: 50px;">
	</div>
</body>
</html>