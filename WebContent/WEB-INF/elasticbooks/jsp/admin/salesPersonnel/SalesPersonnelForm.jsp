<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description: Sales personnel form.
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
		<form:form method="POST" commandName="salesPersonnel" id="salesPersonnelFormId">
			<div class="modFormLabel">Sales Personnel</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass">
											<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Name</td>
							<td class="value"><form:input path="name" id="name" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="name" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Contact Number</td>
							<td class="value"><form:input path="contactNumber" cssClass="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="contactNumber" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Address</td>
							<td class="value"><form:textarea path="address" class="addressClass"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="address" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value"><form:checkbox path="active"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="active" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="formDiv">
					<tr>
						<td colspan="2" align="right">
							<input type="button" id="btnSave" value="Save" onclick="saveSalesPersonnel();"/>
							<input type="button" id="btnCancel" value="Cancel" onclick="cancelSalesPersonnel();"/>
						</td>
					</tr>
				</table>
			</div>
			<hr class="thin"/>
		</form:form>
	</div>
</body>
</html>