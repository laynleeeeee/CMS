<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!--

	Description: Elasticbooks Company Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	if ($("#hdnCompanyId").val() != 0)
		$("#btnSaveCompanyForm").val("Update");
});
</script>
</head>
<body >
<div class="formDiv">
	<form:form method="POST" commandName="company">
		<div class="modFormLabel">&nbsp;Company Profile</div>
		<br>
		<div class="modForm">
		<div class="information" >&nbsp;* Required fields</div>
			<table width="100%">
				<form:hidden path="id" id="hdnCompanyId"/>
				<form:hidden path="serviceLeaseKeyId"/>
				<form:hidden path="createdBy"/>
				<form:hidden path="createdDate"/>
				<form:hidden path="updatedBy"/>
				<form:hidden path="updatedDate"/>
				<form:hidden path="ebObjectId"/>
				<tr>
					<td width="30%">
						<table>
							<tbody class="frmText">
								<tr>
									<td >Company No *</td>
									<td><form:input id="cNumber" path="companyNumber" maxlength="5" size="3"/></td>
								</tr>
								<tr><td></td>
									<td>
										<form:errors path="companyNumber" cssClass="error" />
									</td>
								</tr>
								<tr>
									<td>Company Name *</td>
									<td><form:input path="name" maxlength="100" id="companyName"/></td>
								</tr>
								<tr><td></td>
									<td>
										<form:errors path="name" cssClass="error" />
									</td>
								</tr>
								<tr>
									<td>Company code *</td>
									<td><form:input path="companyCode" maxlength="100" id="companyCode"/></td>
								</tr>
								<tr><td></td>
									<td>
										<form:errors path="companyCode" cssClass="error" />
									</td>
								</tr>
								<tr>
									<td valign="top">Address *</td>
									<td><form:textarea id="address" path="address" maxlength="200" cols="30" rows="2"/></td>
								</tr>
								<tr><td></td>
									<td>
										<form:errors path="address" cssClass="error" />
									</td>
								</tr>
								<tr style="display: none;">
									<td>Active &nbsp;</td>
									<td><form:checkbox path="active"/></td>
								</tr>
							</tbody>
						</table>
					</td>
					<td width="40%" valign="top">
						<table>
							<tbody  class="frmText">
								<tr>
									<td>TIN</td>
									<td><form:input path="tin" maxlength="15" size="8"/></td>
								</tr>
								<tr><td></td>
									<td>
										<form:errors path="tin" cssClass="error" />
									</td>
								</tr>
								<tr>
									<td>Contact Number</td>
									<td><form:input path="contactNumber" maxlength="20" size="20" /></td>
								</tr>
								<tr><td></td>
									<td>
										<form:errors path="contactNumber" cssClass="error" />
									</td>
								</tr>
								<tr>
									<td>Email Address</td>
									<td><form:input path="emailAddress" maxlength="50"/></td>
								</tr>
								<tr><td></td>
									<td>
										<form:errors path="emailAddress" cssClass="error" />
									</td>
								</tr>
								<tr >
									<td></td>
									<td align="right">
										<input type="button" id="btnSaveCompanyForm" value="${company.id eq 0 ? 'Save' : 'Update'}"  onclick="saveCompany();" />
										<input type="button" id="btnCancelCompanyForm" value="Cancel"/>	
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</form:form>
</div>
</body>
</html>