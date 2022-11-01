<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>
     <!--

		Description: Employee shift form
      -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
</head>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="employeeShift" id="frmEmployeeShift">
			<div class="modFormLabel">Employee Shift</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<form:hidden path="employeeShiftAdditionalPay.id"/>
						<tr>
							<td class="labels">* Company</td>
							<td class="value" id="tdCompanyValue">
								<c:choose>
									<c:when test="${employeeShift.id eq 0}">
										<form:select path="companyId" id="companyId" cssClass="frmSelectClass"
											items="${companies}" itemLabel="numberAndName" itemValue="id"/>
									</c:when>
									<c:otherwise>
										<c:forEach var="company" items="${companies}">
											<c:if test="${company.id == employeeShift.companyId}">
												${company.numberAndName}
												<form:hidden path="companyId" id="companyId"/>
											</c:if>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
						<tr>
							<td class="labels">* Name</td>
							<td class="value"><form:input path="name" id="name" class="input"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="name" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Shift Start</td>
							<td class="value"><form:input path="firstHalfShiftStart" id="firstHalfShiftStart" class="inputSmall"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="firstHalfShiftStart" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Shift End</td>
							<td class="value"><form:input path="secondHalfShiftEnd" id="secondHalfShiftEnd" class="inputSmall"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="secondHalfShiftEnd" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Allowable Break Time</td>
							<td class="value">
								<form:input path="allowableBreak" id="allowableBreak" class="inputSmall" 
								onblur="formatNumeric('allowableBreak');"
								style="text-align: right"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="allowableBreak" cssClass="error"/></td>
						</tr>
						<tr style="display: none;">
							<td class="labels">Late Multiplier</td>
							<td class="value"><form:input path="lateMultiplier" id="lateMultiplier" class="inputSmall" 
								onblur="formatNumeric('lateMultiplier');"
								style="text-align: right"/>
								<b>%</b>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="lateMultiplier" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Night Shift</td>
							<td class="value"><form:checkbox path="nightShift"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="nightShift" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Daily Working Hours</td>
							<td class="value"><form:input path="dailyWorkingHours" id="dailyWorkingHours" class="inputSmall"
							onblur="formatNumeric('dailyWorkingHours');" style="text-align: right"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><span id="errorWorkingHours" class="error"></span>
							<form:errors path="dailyWorkingHours" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Day Offs</td>
						</tr>
						<tr>
							<td class="labels">Sunday</td>
							<td class="value">
								<form:checkbox path="dayOffDto.sunday"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Monday</td>
							<td class="value">
								<form:checkbox path="dayOffDto.monday"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Tuesday</td>
							<td class="value">
								<form:checkbox path="dayOffDto.Tuesday"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Wednesday</td>
							<td class="value">
								<form:checkbox path="dayOffDto.wednesday"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Thursday</td>
							<td class="value">
								<form:checkbox path="dayOffDto.thursday"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Friday</td>
							<td class="value">
								<form:checkbox path="dayOffDto.friday"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Saturday</td>
							<td class="value">
								<form:checkbox path="dayOffDto.saturday"/>
							</td>
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
							<input type="button" id="btnSaveEmployeeShift" value="${employeeShift.id eq 0 ? 'Save' : 'Update'}" onclick="saveEmployeeShift();"/>
							<input type="button" id="btnCancelEmployeeShift" value="Cancel" onclick="cancelEmployeeShift();"/>
						</td>
					</tr>
				</table>
			</div>
			<hr class="thin"/>
		</form:form>
	</div>
</body>
</html>