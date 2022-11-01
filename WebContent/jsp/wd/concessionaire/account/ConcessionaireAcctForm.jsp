<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp" %>
<!-- 

	Description: Form that will add and edit an account
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">

$(document).ready (function () {
	if ($.browser.mozilla) 
		$("input.dateClass").css("width", "90px");
		$("#trAmount").hide();
});

$(function () {	
	$("#cbOverride").click(function () {
		if ($(this).is(":checked"))
			$("#trAmount").show();
		else
			$("#trAmount").hide();
	});
});

function updateDueDate () {
	var selectedDate = new Date ($("#date").val());
	var dueDate = new Date (selectedDate);
	dueDate.setDate(selectedDate.getDate()+14);
	$("#dueDate").val (dueDate.getMonth() +"/"+dueDate.getDate()+"/"+dueDate.getFullYear());
}
</script>
</head>
<body>
	<form:form method="POST" commandName="concessionaireAcct" >
		<div class="modFormLabel">Concessionaire Account Form</div>
		<div class="modFormUnderline"> </div>
		<br>
		<div class="information">* Required fields</div>
		<table class="formTable">
			<form:hidden path="id"/>
			<form:hidden path="concessionaireId"/>
			<form:hidden path="meterRentalId"/>
			<tr>
				<td>Senior Citizen</td>
				<td><form:checkbox path="seniorCitizen" ></form:checkbox></td>
			</tr>
			<tr>
				<td>* Date</td>
				<td><form:input path="date" id="date" maxlength="10" class="dateClass" onchange="updateDueDate ();"/>
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('date')" style="cursor:pointer" style="float: right;"/></td>
			</tr>
			<tr>
				<td>* Due Date</td>
				<td><form:input path="dueDate" id="dueDate" maxlength="10" class="dateClass"/>
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dueDate')" style="cursor:pointer" style="float: right;"/></td>
			</tr>
			<tr >
				<td colspan="2"><form:errors path="date" cssClass="error"/></td>
			</tr>	
			<tr>
				<td>* Water Bill Number</td>
				<td><form:input path="wbNumber" maxlength ="10" size="10" cssClass="numberClass"/></td>
			</tr>
			<tr >
				<td colspan="2"><form:errors path="wbNumber" cssClass="error"/></td>
			</tr>
			<tr>
				<td>* Cubic Meter</td>
				<td><form:input path="cubicMeter" maxlength="9" size="10" cssClass="numberClass"/></td>
			</tr>
			<tr >
				<td colspan="2"><form:errors path="cubicMeter" cssClass="error"/></td>
			</tr>
			<tr>
				<td>Override Computation </td>
				<td>
					<form:checkbox path="override" id="cbOverride"/>
				</td>
			</tr>
			<tr id="trAmount">
				<td>Amount</td>
				<td><form:input path="overrideAmount" maxlength="9" size="10" cssClass="numberClass" onblur="checkAndSetDecimal('overrideAmount')"/></td>
			</tr>
		</table>
	</form:form>
	<div class="controls">
			<input type="button" id="btnSaveConcessionaireAcct" value="Save" />		
			<input type="button" id="btnCancelConcessionaireAcct" value="Cancel"/>	
	</div>
</body>
</html>