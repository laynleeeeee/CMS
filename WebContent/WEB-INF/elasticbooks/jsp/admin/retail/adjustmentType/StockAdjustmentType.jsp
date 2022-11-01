<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Stock Adjustment Type main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
var SEARCH_URL = "/admin/adjustmentType/search";
var FORM_URL = contextPath+"/admin/adjustmentType/form";
$(document).ready(function () {
	$("#btnEditType").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

$(function () {
	$("#txtName").on("keypress", function (e) {
		if (e.which == 13) {
			searchAdjustmentTypes();
			e.preventDefault();
		}
	});
});

function addAdjustmentType() {
	$("#adjustmentTypeFormDiv").load(FORM_URL, function (){
		$("html, body").animate({
			scrollTop: $("#adjustmentTypeFormDiv").offset().top}, 0050);
	});
}

function editAdjustmentType() {
	var id = getCheckedId("cbAdjustmentType");
	$("#adjustmentTypeFormDiv").load(FORM_URL+"?adjustmentTypeId="+id, function (){
		$("html, body").animate({
			scrollTop: $("#adjustmentTypeFormDiv").offset().top}, 0050);
	});
}

function searchAdjustmentTypes() {
	doSearch("adjustmentTypeTableDiv",
			SEARCH_URL+getCommonParam()+"&page=1");
}

function getCommonParam() {
	var companyId = $("#searchCompanyId").val();
	var statusId = $("#selectStatusId").val();
	var name = processSearchName($.trim($("#txtName").val()));
	return "?companyId="+companyId+"&name="+name+"&status="+statusId;
}

function saveAdjustmentType() {
	$("#btnSaveSAT").attr("disabled", "disabled");
	doPostWithCallBack ("stockAdjustmentType", "adjustmentTypeFormDiv", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
					+ "stock adjustment type " + $("#satName").val() + ".");
			searchAdjustmentTypes();
			dojo.byId("adjustmentTypeFormDiv").innerHTML = "";
		} else {
			var companyId = $("#id").val() != 0 ? $("#hdnCompanyId").val() : $("#selectCompanyId").val();
			var divisionId = $("#id").val() != 0 ? $("#hdnDivisionId").val() : $("#selectDivisionId").val();
			var accountId = $("#id").val() != 0 ? $("#hdnAccountId").val() : $("#selectAccountId").val();
			var companyLabel = $("#companyLabel").text();
			var divisionLabel = $("#divisionLabel").text();
			var accountLabel = $("#accountLabel").text();
			$("#adjustmentTypeFormDiv").html(data);
			$("#selectCompanyId").val(companyId).attr('selected', true);
			if($("#id").val() != 0) {
				$("#hdnCompanyId").val(companyId);
				$("#hdnDivisionId").val(divisionId);
				$("#hdnAccountId").val(accountId);
				$("#companyLabel").text(companyLabel);
				$("#divisionLabel").text(divisionLabel);
				$("#accountLabel").text(accountLabel);
			} else {
				filterDivisions(divisionId, accountId);
			}
		}
	});
}

function cancelForm() {
	$("#adjustmentTypeFormDiv").html("");
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="tableInfo">
			<tr>
				<td width="15%" class="title">Company</td>
				<td><select id="searchCompanyId" class="frmSelectClass">
					<option value="-1">All</option>
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td class="title">Name</td>
				<td><input type="text" id="txtName" class="input"></td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td><select id="selectStatusId" class="frmSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchAdjustmentTypes();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="adjustmentTypeTableDiv">
		<%@ include file="StockAdjustmentTypeTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddType" value="Add" onclick="addAdjustmentType();"></input>
		<input type="button" id="btnEditType" value="Edit" onclick="editAdjustmentType();"></input>
	</div>
	<br>
	<br>
	<div id="adjustmentTypeFormDiv" style="margin-top: 20px;">
	</div>
</body>
</html>