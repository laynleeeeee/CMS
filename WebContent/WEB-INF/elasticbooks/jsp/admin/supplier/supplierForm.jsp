<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Form to Add and Edit a supplier.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
</head>
<script>
var INDIVIDUAL_TYPE = 1;
var COROPORATE_TYPE = 2;
$(document).ready (function () {
	showHideByClassification();
});
function showHideByClassification() {
	var type = $("#bussinessClassificationId").val();
	if(type == INDIVIDUAL_TYPE) {
		$(".corporate").hide();
		$(".individual").show();
		clearData("corporate");
		$(".corporate td .input").each(function() {
			$(this).val('');//Clear data.
		});
	} else {
		$(".individual").hide();
		$(".corporate").show();
		clearData("individual");
	}
	changeNameLabel(type);
}

function changeNameLabel(type) {
	$("#lblName").text("");//Clear name
	var label = "";
	if(type == INDIVIDUAL_TYPE) {
		label = "* Trade Name";
	} else {
		label = "* Name";
	}
	$("#lblName").text(label);
}

function clearData(className) {
	$("." +className+" td").each(function() {
		$(this).val('');//Clear data.
	});
	$("." +className+" td .error").each(function() {
		$(this).html('');//Clear data.
	});
}

function validateNumber(evt) {
	var theEvent = evt || window.event;
	var key = theEvent.keyCode || theEvent.which;
	key = String.fromCharCode(key);
	//Check if key input is numeric.
	if( !isNumber(key) ) {
		theEvent.returnValue = false;
		if(theEvent.preventDefault) theEvent.preventDefault();
	}
}

function isNumber(val) {
	var regex = /[0-9]|\./;
	return regex.test(val);
};

function handlePaste(elem, evt) {
	evt.stopPropagation();
	evt.preventDefault();

	var clipboardData = evt.clipboardData || window.clipboardData;
	var pastedData = clipboardData.getData('Text');
	var processedTin = "";
	for(var index=0; index < pastedData.length; index++) {
		processedTin += isNumber(pastedData[index]) ? pastedData[index] : '';
	}
	var currentData = $(elem).val();
	$(elem).val(currentData + processedTin);//Combine the current data with the pasted data.
}

function removeNameData() {
	$('#nameErr').text('');
	$('#name').val('');
}
</script>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="supplier">
		<div class="modFormLabel">Supplier</div>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="id"/>
		<br>
		<div class="modForm">
			<table class="formTable">
				<tr>
					<td class="labels">* Type</td>
					<td class="value">
						<form:select path="bussinessClassificationId" id="bussinessClassificationId" cssClass="frmSelectClass"
							onchange="showHideByClassification();removeNameData();">
							<form:options items="${classifications}" itemValue="id" itemLabel="name"/>
						</form:select>
					</td>
				</tr>
				<tr>
					<td class="labels" id="lblName">* Name</td>
					<td class="value"><form:input path="name" class="input" id="name"/></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="name" cssClass="error" style="margin-left: 12px;" id="nameErr"/></td>
				</tr>
				<tr class="individual">
					<td class="labels">* First Name</td>
					<td class="value"><form:input path="firstName" class="input"/></td>
				</tr>
				<tr class="individual">
					<td></td>
					<td colspan="2"><form:errors path="firstName" cssClass="error" style="margin-left: 12px;"/></td>
				</tr>
				<tr class="individual">
					<td class="labels">* Last Name</td>
					<td class="value"><form:input path="lastName" class="input"/></td>
				</tr>
				<tr class="individual">
					<td></td>
					<td colspan="2"><form:errors path="lastName" cssClass="error" style="margin-left: 12px;"/></td>
				</tr>
				<tr class="individual">
					<td class="labels">Middle Name</td>
					<td class="value"><form:input path="middleName" class="input"/></td>
				</tr>
				<tr class="individual">
					<td></td>
					<td colspan="2"><form:errors path="middleName" cssClass="error" style="margin-left: 12px;"/></td>
				</tr>
				<tr>
					<td class="labels">* Street, Barangay</td>
					<td class="value"><form:textarea path="streetBrgy" cssClass="addressClass"/></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="streetBrgy" cssClass="error" style="margin-left: 12px;"/></td>
				</tr>
				<tr>
					<td class="labels">* City, Province, and ZIP Code</td>
					<td class="value"><form:textarea path="cityProvince" cssClass="addressClass"/></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="cityProvince" cssClass="error" style="margin-left: 12px;"/></td>
				</tr>
				<tr>
					<td class="labels" valign="top">TIN</td>
					<td class="value">
						<form:input type="text" class="inputSmall" path="tin" id="tin" onkeypress="validateNumber(event);"
							onpaste="handlePaste(this, event)"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="tin" cssClass="error" style="margin-left: 12px;" /></td>
				</tr>
				<tr>
					<td class="labels"> Contact Person</td>
					<td class="value"><form:input type="text" class="inputSmall" path="contactPerson"/></td>
				</tr>
				<tr>
					<td></td>
					<td><form:errors path="contactPerson" cssClass="error" style="margin-left: 12px;" /></td>
				</tr>
				<tr>
					<td class="labels"> Contact Number</td>
					<td class="value"><form:input type="text" class="inputSmall" path="contactNumber"/></td>
				</tr>
				<tr>
					<td></td>
					<td><form:errors path="contactNumber" cssClass="error" style="margin-left: 12px;" /></td>
				</tr>
				
				<tr>
					<td class = "labels">* Business Registration </td>
					<td class="value">
					<form:select path="busRegTypeId" class="frmSmallSelectClass">
						<option value=""></option>
						<form:options items="${busRegType}" itemLabel="name" itemValue="id"/>
					</form:select>
				</td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2">
						<form:errors path="busRegTypeId" cssClass="error" style="margin-left: 12px;"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Active: </td>
					<td class="value">
						<form:checkbox path="active"/>
					</td>
				</tr>
			</table>
		</div>
		<div class="controls">
			<input type="button" id="btnSaveSupplier" value="${supplier.id eq 0 ? 'Save' : 'Update'}" onclick="saveSupplier();"/>
			<input type="button" value="Cancel" onclick="cancelSupplier();"/>
	</div>
	<hr class="thin"/>
	</form:form>
</div>
</body>
</html>
