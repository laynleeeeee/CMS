<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Employee DTR jsp page form for GVCH
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
$(document).ready(function () {
	$("#selectYearId").val("${currentYear}");
	$("#selectMonthId").val("${currentMonth}");
});

$(function(){
	$("#txtName, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchActionNotice();
			e.preventDefault();
		}
	});
	$("#btnAddActionNotice").click(function () {
		$("#divActionNoticeForm").load(contextPath + "/admin/actionNotice/form", function (){
			$("html, body").animate({scrollTop: $("#divActionNoticeForm").offset().top}, 0050);
		});
	});
});

$("#btnEditActionNotice").click(function () {
	var id = getCheckedId ("cbActionNotice");
	$("#divActionNoticeForm").load(contextPath + "/admin/actionNotice/form?actionNoticeId="+id);
	$("html, body").animate({scrollTop: $("#divActionNoticeForm").offset().top}, 0050);
});

function loadDtrDetails() {
	var employeeId = "${employeeId}";
	if (employeeId != "") {
		var year = $("#selectYearId").val();
		var month = $("#selectMonthId").val();
		var uri = contextPath + "/employeeProfile/" + employeeId + "/dtrDetail?year=" + year 
		+ "&month=" + month;
		$("#divEmployeeDTRDetailTable").load(uri);
	}
}
</script>
<title>Employee Profile - DTR</title>
</head>
<body>
	<div>
		<table class="formTable">
			<tr>
				<td colspan=2>
					<select id="selectMonthId" class="frmSmallSelectClass" onchange="loadDtrDetails();">
						<c:forEach var="mm" items="${months}">
							<option value="${mm.month}">${mm.name}</option>
						</c:forEach>
					</select>
					<select id="selectYearId" class="frmSmallSelectClass" onchange="loadDtrDetails();">
						<c:forEach var="yy" items="${years}">
							<option value="${yy}">${yy}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
		</table>
	</div>
	<div id="divEmployeeDTRDetailTable">
		<%@ include file="EmployeeDTRDetail.jsp"%>
	</div>
</body>
</html>