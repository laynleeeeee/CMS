<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Elasticbooks Account Combination Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Account Combination Form</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	loadAccountCombination();
});
</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="accountCombination" id="accountCombinationFormId">
		<div class="modFormLabel">&nbsp;Account Combination</div>
		<div class="modFormLine"> </div>
		<br>
		<div class="modForm">
			<table class="formTable">
				<form:hidden path="id"/>
				<form:hidden path="createdBy"/>
				<form:hidden path="createdDate"/>
				<tr>
					<td class="labels">* Company</td>
					<td class="value">
						<form:select path="companyId" class="frmSelectClass">
							<form:option value="-1">Select Company</form:option>
							<form:options items="${companies}" itemLabel="numberAndName" itemValue="id" />
						</form:select>
					</td>
				</tr>
				<tr>
					<td></td>
					<td class="value"><form:errors path="companyId" cssClass="error"/></td>
				</tr>

				<tr>
					<td class="labels">* Division</td>
					<td class="value">
						<form:select path="divisionId" class="frmSelectClass">
							<form:option value="-1">Select Division</form:option>
							<form:options items="${divisions}" itemLabel="numberAndName" itemValue="id" />
						</form:select>
					</td>
				</tr>
				<tr>
					<td></td>
					<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
				</tr>

				<tr>
					<td class="labels">* Account</td>
					<td class="value">
						<form:select path="accountId" class="frmSelectClass">
							<form:option value="-1">Select Account</form:option>
							<form:options items="${accounts}" itemLabel="numberAndName" itemValue="id" />
						</form:select>
					</td>
				</tr>
				<tr>
					<td></td>
					<td class="value"><form:errors path="accountId" cssClass="error"/></td>
				</tr>

				<tr>
					<td class="labels">* Account Combination</td>
					<td class="value">
						<form:input path="combination" readonly="true" class="accountCombiName"/><br />
						<form:errors path="combination" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Active</td>
					<td class="value">
						<form:checkbox path="active"/>
					</td>
				</tr>
			</table>
			<div align="right">
				<input type="button" id="btnSaveAccountCombination" value="${accountCombination.id eq 0 ? 'Save' : 'Update'}" onclick="saveAcctCombi();" />
				<input type="button" id="btnCancelAccountCombination" value="Cancel" onclick="cancelForm();"/>
			</div>
		</div>
	</form:form>
</div>
</body>
</html>