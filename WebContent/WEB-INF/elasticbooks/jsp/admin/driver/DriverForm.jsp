<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		 Description: Driver admin setting form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
</script>
</head>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="driver" id="driverFormId">
			<div class="modFormLabel">Driver</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>Driver Information</legend>
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<tr>
							<td class="labels">* Company</td>
							<td class="value" ><form:select path="companyId" id="companyId" cssClass="frmSelectClass"
								items="${companies}" itemLabel="name" itemValue="id"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value" ><form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* First Name</td>
							<td class="value" ><form:input path="firstName" id="firstName" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value" ><form:errors path="firstName" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Middle Name</td>
							<td class="value" ><form:input path="middleName" id="middleName" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value" ><form:errors path="middleName" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Last Name</td>
							<td class="value" ><form:input path="lastName" id="lastName" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value" ><form:errors path="lastName" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Gender</td>
							<td class="value" >
								<form:select path="genderId" id="genderId"
									cssClass="frmSmallSelectClass" items="${genders}"
									itemLabel="name" itemValue="id">
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value" >
								<form:errors path="genderId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Birthdate</td>
							<td class="value"><form:input path="birthDate" onblur="evalDate('birthDate')"
								id="birthDate" cssClass="dateClass2"/>
								<img id="imgDate" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('birthDate')"
								style="cursor: pointer" style="float: right;" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value" ><form:errors path="birthDate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Civil Status</td>
							<td class="value" >
								<form:select path="civilStatusId" id="civilStatusId"
									cssClass="frmSmallSelectClass" items="${civilStatuses}"
									itemLabel="name" itemValue="id">
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value" >
								<form:errors path="civilStatusId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">Contact Number</td>
							<td class="value" ><form:input path="contactNo" id="contactNo" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value" ><form:errors path="contactNo" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Address</td>
							<td class="value" ><form:textarea path="address" cssClass="addressClass"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value" ><form:errors path="address" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Active</td>
							<td class="value" ><form:checkbox path="active"/></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="formDiv">
					<tr>
						<td colspan="2" align="right">
							<input type="button" id="btnSaveDriver" value="${driver.id eq 0 ? 'Save' : 'Update'}" onclick="saveDriver();"/>
							<input type="button" id="btnCancelDriver" value="Cancel" onclick="cancelDriver();"/>
						</td>
					</tr>
				</table>
			</div>
			<hr class="thin"/>
		</form:form>
	</div>
</body>
</html>