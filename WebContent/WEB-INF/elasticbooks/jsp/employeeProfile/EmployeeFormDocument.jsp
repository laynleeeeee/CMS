<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Employee document form page for GVCH
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

});

function loadDocForms() {
	var employeeId = "${employeeId}";
	if (employeeId != "") {
		var formTypeId = $("#selectFormType").val();
		var uri = contextPath + "/employeeProfile/" + employeeId + "/document?formTypeId="+formTypeId+"&pageNumber=1";
		$("#divEmployeeFormDocumentTable").load(uri);
	}
}
</script>
</head>
<body>
	<div>
		<table class="formTable">
			<tr>
				<td>Form/Document</td>
				<td></td>
				<td colspan=2>
					<select id="selectFormType" class="frmSelectClass" onchange="loadDocForms();">
						<option value="-1">ALL</option>
						<option value="4">AUTHORITY TO DEDUCT</option>
						<option value="3">CASH BOND CONTRACT</option>
						<option value="1">EMPLOYEE DOCUMENT</option>
						<option value="2">EMPLOYEE LEAVE CREDIT</option>
						<option value="7">PERSONNEL ACTION NOTICE</option>
						<option value="5">REQUEST FOR LEAVE</option>
						<option value="6">REQUEST FOR OVERTIME</option>
					</select>
				</td>
			</tr>
		</table>
	</div>
	<div id="divEmployeeFormDocumentTable" style="margin-top: 5px;">
		<%@ include file="EmployeeFormDocTable.jsp"%>
	</div>
</body>
</html>