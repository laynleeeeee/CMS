<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AP Line Setup form that will enable user to add/edit.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	if("${apLineSetup.id}" != 0){
		DIVISION_ID = "${apLineSetup.divisionId}";
		ACCOUNT_ID = "${apLineSetup.accountId}";
		$("#companyId").val("${apLineSetup.companyId}");
		$("#divisionId").val(DIVISION_ID);
		$("#accountId").val(ACCOUNT_ID);
	} else {
		DIVISION_ID = 0;
		ACCOUNT_ID = 0;
		loadDivisions(DIVISION_ID);
	}
});
</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="apLineSetup" id="apLineForm">
		<div class="modFormLabel">AP Line Setup</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>* AP Line Setup Information</legend>
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<form:hidden path="accountCombinationId"/>
						<tr>
							<td class="labels">Name</td>
							<td class="value"><form:input path="name" cssClass="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td><form:errors path="name" cssClass="error" style="margin-left: 12px"/></td>
						</tr>
						<tr>
							<td class="labels">Company</td>
							<td class="value">
								<c:choose>
									 <c:when test="${apLineSetup.id ne 0}">
									   	${companyNumberAndName}
										<form:hidden path="companyId" id="companyId"/>
									 </c:when>
									 <c:otherwise>
									   <form:select path="companyId" cssClass="frmSelectClass"
											id="companyId" onchange="loadDivisions();">
											<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
										</form:select>
									 </c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td></td>
							<td><form:errors path="companyId" cssClass="error" style="margin-left: 12px"/></td>
						</tr>
						<tr>
							<td class="labels">Division</td>
							<td class="value">
								<c:choose>
									<c:when test="${apLineSetup.id ne 0}">
										${divisionName}
										<form:hidden path="divisionId" id="divisionId"/>
									</c:when>
									<c:otherwise>
										<form:select path="divisionId" id="divisionId"
											class="frmSelectClass" onchange="loadAccounts();">
										</form:select>
									</c:otherwise>
								</c:choose>
								
							</td>
						</tr>
						<tr>
							<td></td>
							<td><form:errors path="divisionId" cssClass="error" style="margin-left: 12px"/></td>
						</tr>
						<tr>
							<td class="labels">Account</td>
							<td class="value">
								<c:choose>
									<c:when test="${apLineSetup.id ne 0}">
										${accountName}
										<form:hidden path="accountId" id="accountId"/>
									</c:when>
									<c:otherwise>
										<form:select path="accountId" id="accountId" class="frmSelectClass" >
										</form:select>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td></td>
							<td><form:errors path="accountId" cssClass="error" style="margin-left: 12px"/></td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value"><form:checkbox path="active"/></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="active" cssClass="error"/></td>
						</tr>
					</table>
			</fieldset>
			<br>
			<table class="formDiv">
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="accountCombinationId" cssClass="error"/></td>
				</tr>
				<tr>
					<td></td>
					<td align="right">
						<input type="button" id="btnSaveApLine"	value="${apLineSetup.id eq 0 ? 'Save' : 'Update'}" onclick="saveApLine();"/>
						<input type="button" id="btnCancelApLine" value="Cancel" onclick="cancel();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>