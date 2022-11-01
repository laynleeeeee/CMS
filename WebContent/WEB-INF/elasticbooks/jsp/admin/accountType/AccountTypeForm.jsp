<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Account type form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<title>Account Type Form</title>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="accountType" >
		<div class="modFormLabel">&nbsp; Account Type</div>
		<div class="modFormLine"> </div>
		<br>
		<div class="modForm">
		<div class="information">* Required fields</div>
		<table class="formTable">
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<tr>
				<td>*Name</td>
				<td><form:input path="name" size="20" maxLength="100" id="accountTypeName"/></td>
			</tr>
			<tr>
				<td colspan="2">
					<form:errors path="name" cssClass="error"/>
				</td>
			</tr>

			<tr>
				<td>Normal Balance</td>
				<td>
					<form:select path="normalBalanceId">
						<form:options items="${normalBalances}" itemLabel="name" itemValue="id"/>
					</form:select>
				</td>
			</tr>

			<tr>
				<td>Contra Account: </td>
				<td>
					<form:checkbox path="contraAccount"/>
				</td>
			</tr>

			<tr style="display: none;">
				<td>Active: </td>
				<td>
					<form:checkbox path="active"/>
				</td>
			</tr>
		</table>
		<div class="controls">
			<input type="button" id="btnSaveAccountType" value="${accountType.id eq 0 ? 'Save' : 'Update'}" onclick="saveAcctType();" />
			<input type="button" id="btnCancelAccountType" value="Cancel" onclick="cancelForm();"/>	
		</div>
		</div>
	</form:form>
</div>
</body>
</html>