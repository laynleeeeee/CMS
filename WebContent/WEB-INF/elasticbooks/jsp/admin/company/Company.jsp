<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!--

	Description: Elasticbooks Company page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Company</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
$(function() {
	$("#btnAddCompany").click(function() {
		$("#companyForm").load(contextPath + "/admin/company/form");
		$("html, body").animate({scrollTop: $("#companyForm").offset().top}, 0050);
	});

	$("#btnEditCompany").click(function() {
		var id = getCheckedId("cbCompany");
		$("#companyForm").load(contextPath + "/admin/company/form?companyId=" +id);
		$("html, body").animate({scrollTop: $("#companyForm").offset().top}, 0050);
	});

	$("#btnSearchCompany").click(function(){
		searchCompanyList();
	});

	$("#btnCancelCompanyForm").live("click", function(){
		$("#companyForm").html("");
		searchCompanyList();
	});

	$("#companyNumber, #name, #tin, #code").bind("keypress", function (e) {
		if (e.which == 13) {
			searchCompanyList();
			e.preventDefault();
		}
	});
});

function saveCompany (){
	$(this).attr("disabled", "disabled");
	var id = getCheckedId("cbCompany");
	doPostWithCallBack ("company", "companyForm", function(data) {
	if (data.startsWith("saved")) {
			$("#spanMessage").html("Successfully " + (id != 0 ? "updated " : "added ") + "company " 
					+ $("#companyName").val() + ".");
		searchCompanyList();
		dojo.byId("companyForm").innerHTML = "";
	}else {
		$("#companyForm").html(data);
	}
	});
}


function getCommonParam(){
	var number = processSearchName($("#companyNumber").val());
	var name = processSearchName($("#name").val());
	var tin = processSearchName($("#tin").val());
	var code = processSearchName($("#code").val());
	return "?companyNumber="+number+"&name="+name+"&tin="+tin+"&code="+code;
}

function searchCompanyList(){
	var url = "/admin/company" + getCommonParam() +"&pageNumber=1";
	doSearch ("companyTable", url);
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="tableInfo">
			<tr>
				<td width="10%" class="title">Number:</td>
				<td><input type="text" id="companyNumber" size="17%"></td>
			</tr>
			<tr>
				<td width="10%" class="title">Name:</td>
				<td><input type="text" id="name" size="17%"></td>
			</tr>
			<tr>
				<td width="10%" class="title">Code:</td>
				<td><input type="text" id="code" size="17%"></td>
			</tr>
			<tr>
				<td width="10%" class="title">TIN:</td>
				<td><input type="text" id="tin" size="17%">
				<input type="button" id="btnSearchCompany"
					value="Search" /></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="veil"></div>
	<div id="companyTable">
		<%@ include file="CompanyTable.jsp" %>
	</div>
	<div class="controls" align="right">
		<input type="button" id="btnAddCompany" name="btnAddCompany" value="Add" />
		<input type="button" id="btnEditCompany" name="btnEditCompany" value="Edit" />
	</div>
	<br /> <br />
	<div id="companyForm" style="margin-top: 20px;">
	</div>
</body>
</html>