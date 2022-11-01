<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../include.jsp"%>
<!--

	Description: User Company main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
	var FORM_URL = contextPath+"/admin/userCompany/form";
	var SEARCH_URL = "/admin/userCompany/search";

	$(function () {
		$("#txtSearchUser, #slctStatus, #txtSearchCompany").on("keypress", function (e) {
			if (e.which == 13) {
				searchUserCompany();
				e.preventDefault();
			}
		});
	});

	function addUserCompany() {
		$("#divUserCompanyForm").load(FORM_URL, function() {
			$("html, body").animate({
				scrollTop : $("#divUserCompanyForm").offset().top
			}, 0050);
		});
	}

	function editUserCompany() {
		var id = getCheckedId("cbUsers");
		$("#divUserCompanyForm").load(contextPath + "/admin/userCompany/form?userId="+id, function() {
			$("html, body").animate({scrollTop: $("#divUserCompanyForm").offset().top}, 0050);
		});
	}

	function searchUserCompany() {
		var param = getCommonParam() + "&page=1";
		var search = SEARCH_URL + param;
		doSearch("divUserCompanyTable", search);
	}

	function getCommonParam() {
		var userName = processSearchName($.trim($("#txtSearchUser").val()));
		var userCompany = processSearchName($.trim($("#txtSearchCompany").val()));
		var status = $("#slctStatus").val();
		return "?userName="+userName+"&companyName="+userCompany+"&status="+status;
	}
</script>
<title>User Company</title>
</head>
<body>
<div id="divUserCompany">
		<table class="formTable">
			<tr>
				<td width="25%" class="title">User Name</td>
				<td><input type="text" id="txtSearchUser" maxLength="100"
					size="20" class="input" /></td>
			</tr>
			<tr>
				<td width="25%" class="title">Company</td>
				<td><input type="text" id="txtSearchCompany" maxLength="100"
					size="20" class="input" /></td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td>
					<select id="slctStatus" class="frmSmallSelectClass">
						<c:forEach var="status" items="${status}">
							<option>${status}</option>
						</c:forEach>
					</select>
				</td>
				<td><input type="button" id="btnSearchUserCompany" value="Search"
					onclick="searchUserCompany();" /></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divUserCompanyTable">
		<%@ include file="UserCompanyTable.jsp"%>
	</div>
	<div class="controls">
		<input type="button" id="btnAddUserCompany" value="Add" onclick="addUserCompany();" />
		<input type="button" id="btnEditUserCompany" value="Edit" onclick="editUserCompany();" />
	</div>
	<br />
	<br />
	<div id="divUserCompanyForm" style="margin-top: 50px;">
	</div>
</body>
</html>