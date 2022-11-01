<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Form that will enable the user to create and update the time period.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">
$(document).ready (function () {
	if ($.browser.mozilla)
		$("input.dateClass").css("width", "90px");
});
</script>
</head>
<body>
<form:form method="POST" commandName="timePeriod" id="timePeriodId">
	<div class="modFormLabel">Time Period</div>
	<div class="modFormUnderline"></div>
	<br>
	<div class="information">* Required fields: </div>
	<table class="formTable">
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<tr>
			<td><span style="color: red">*</span> Name:</td>
			<td><form:input path="name" id="timePeriodName"/></td>
		</tr>
		<tr>
			<td colspan="2"><form:errors path="name" cssClass="error"/></td>
		</tr>
		<tr>
			<td><span style="color: red">*</span> Date From:</td>
			<td><form:input path="dateFrom" maxlength="10" class="dateClass"/>
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer" style="float: right;"/></td>
		</tr>
		<tr>
			<td colspan="2"><form:errors path="dateFrom" cssClass="error"/></td>
		</tr>
		<tr>
			<td><span style="color: red">*</span> Date To:</td>
			<td><form:input path="dateTo" maxlength="10" class="dateClass"/>
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer" style="float: right;"/></td>
		</tr>
		<tr>
			<td colspan="2"><form:errors path="dateTo" cssClass="error"/></td>
		</tr>
		<tr>
			<td><span style="color: red">*</span> Status:</td>
			<td><form:select path="periodStatusId">
				<form:options items="${periodStatus}" itemLabel="name" itemValue="id"></form:options>
			</form:select></td>
		</tr>
	</table>
	<table>
		<tr>
			<td colspan="2"><form:errors path="periodStatusId" cssClass="error"/></td>
		</tr>
	</table>
	<div class="controls">
			<input type="button" id="btnSaveTimePeriod" value="${timePeriod.id eq 0 ? 'Save' : 'Update'}" onclick="saveTimePeriod();"/>
			<input type="button" id="btnCancelTimePeriod" value="Cancel" onclick="cancelForm();"/>
	</div>
</form:form>
</body>
</html>