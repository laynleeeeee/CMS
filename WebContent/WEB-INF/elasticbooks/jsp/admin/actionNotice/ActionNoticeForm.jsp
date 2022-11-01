<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description: Action Notice form page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
</head>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="actionNotice">
			<div class="modFormLabel">Action Notice</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<table class="formTable">
						<form:hidden path="id" />
						<form:hidden path="createdBy" />
						<form:hidden path="createdDate"/>
						<tr>
							<td class="labels">* Name:</td>
							<td class="value"><form:input path="name" class="input" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="name" cssClass="error"
									style="margin-left: 12px;" /></td>
						</tr>
						<tr>
							<td class="labels">Active:</td>
							<td class="value"><form:checkbox path="active" /></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right">
							<input id="btnSaveActionNotice" align="right" type="button" value="${actionNotice.id eq 0 ? 'Save' : 'Update'}" onclick="saveActionNotice();" />
							<input id="btnCancelActionNotice" type="button" value="Cancel" onclick="cancelForm();" />
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>