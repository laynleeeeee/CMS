<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>

	Description: Holiday Setting Form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Holiday Setting</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">

</script>
</head>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="holidaySetting"
			id="frmHolidaySetting">
			<div class="modFormLabel">Holiday Settings</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<table class="formTable">
						<form:hidden path="id" />
						<form:hidden path="createdBy" />
						<form:hidden path="createdDate" />
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<c:choose>
									<c:when test="${holidaySetting.id == 0}">
										<form:select path="companyId" cssClass="frmSelectClass"
											id="slctCompany">
											<form:options items="${companies}" itemValue="id"
												itemLabel="name" />
										</form:select>
									</c:when>
									<c:otherwise>
										<c:forEach var="company" items="${companies}">
											<c:if test="${company.id == holidaySetting.companyId}">
											${company.name}
											<form:hidden path="companyId" />
											</c:if>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="companyId" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">* Name</td>
							<td class="value"><form:input path="name" cssClass="input"
									id="txtNameId" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="name" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">* Holiday Type</td>
							<td class="value">
								<form:select path="holidayTypeId" cssClass="frmSelectClass"
									id="holidayType">
									<form:options items="${holidayTypes}" itemValue="id"
										itemLabel="name" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="holidayTypeId" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value"><form:input path="date" id="dateForm"
									onblur="evalDate('dateForm')" style="width: 120px;"
									class="dateClass2" /> <img id="imgDate1"
								src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('dateForm')"
								style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="date" cssClass="error" /></td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value"><form:checkbox path="active" /></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="formDiv">
					<tr>
						<td colspan="2" align="right">
							<input type="button" id="btnSaveHolidaySetting" value="${holidaySetting.id eq 0 ? 'Save' :  'Update'}" onclick="saveHolidaySetting();" />
							<input type="button" id="btnCancelHolidaySetting" value="Cancel" onclick="cancelHolidaySetting();" />
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>