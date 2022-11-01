<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR Customer Form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<style type="text/css">
input.numeric {
	width: 120px;
}
</style>
<script type="text/javascript">
var INDIVIDUAL_TYPE = 1;
var COROPORATE_TYPE = 2;
$(document).ready (function () {
	showHideByClassification();
});
$(document).ready(function() {
	$("#amount").val(Number($("#amount").val()));
	var isChecked = $("#cbProjectId").is(":checked");
	if ("${arCustomer.id}" > 0 && isChecked) {
		$("#cbProjectId").attr("disabled", "disabled");
	}
});

$(function() {
	$("#amount").blur(function() {
		var amount = $("#amount").val();
		var unformatted = accounting.unformat(amount);
		$("#amount").val(unformatted);
	});
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

function removeNameValidation() {
	$('#nameErr').text('');
	$('#name').val('');
}
</script>
<title>AR Customer Form</title>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="arCustomer" id="customerFormId">
		<div class="modFormLabel">AR Customer</div>
		<br>
		<div class="modForm">
			<table class="formTable">
				<form:hidden path="id"/>
				<form:hidden path="createdBy"/>
				<form:hidden path="createdDate"/>
				<form:hidden path="ebObjectId"/>
				<tr>
					<td class="labels">* Type</td>
					<td class="value">
						<form:select path="bussinessClassificationId" id="bussinessClassificationId" cssClass="frmSelectClass"
							onchange="showHideByClassification();removeNameValidation();">
							<form:options items="${classifications}" itemValue="id" itemLabel="name"/>
						</form:select>
					</td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="bussinessClassificationId" cssClass="error" style="margin-left: 12px;"/></td>
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
					<td class="labels">Contact Person</td>
					<td class="value"><form:input path="contactPerson" cssClass="standard"/></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="contactPerson" cssClass="error" style="margin-left: 12px;"/></td>
				</tr>
				<tr>
					<td class="labels">Contact Number</td>
					<td class="value"><form:input path="contactNumber" cssClass="standard"/></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="contactNumber" cssClass="error" style="margin-left: 12px;"/></td>
				</tr>
				<tr>
					<td class="labels">Email Address</td>
					<td class="value"><form:input path="emailAddress" cssClass="standard"/></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="emailAddress" cssClass="error" style="margin-left: 12px;"/></td>
				</tr>
				<tr>
					<td class="labels" valign="top">TIN</td>
					<td class="value">
						<form:input type="text" path="tin" cssClass="standard" onkeypress="validateNumber(event);"
							onpaste="handlePaste(this, event)"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="tin" cssClass="error" style="margin-left: 12px;" /></td>
				</tr>
				<tr>
					<td class="labels">Maximum Allowable Amount</td>
					<td class="value"><form:input path="maxAmount" id="amount" class="numeric"/></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="maxAmount" cssClass="error" style="margin-left: 12px;"/></td>
				</tr>
				<tr class="hidden">
					<td class="labels">* Cluster</td>
					<td class="value">
						<form:select path="customerTypeId" class="frmSelectClass">
							<form:option value=""></form:option>
							<form:options items="${customerTypes}" itemLabel="name" itemValue="id" />
						</form:select>
					</td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="customerTypeId" cssClass="error"/></td>
				</tr>
				<tr class="hidden">
					<td class="labels">Project</td>
					<td class="value"><form:checkbox path="project" id="cbProjectId"/></td>
				</tr>
				<tr>
					<td class="labels"></td>
					<td class="value"><form:errors path="project" cssClass="error"/></td>
				</tr>
				<tr>
					<td class="labels">Active</td>
					<td class="value"><form:checkbox path="active"/></td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="active" cssClass="error"/></td>
				</tr>
				<tr>
					<td colspan="2" align="right">
						<input type="button" id="btnSaveArCustomer" value="${arCustomer.id eq 0 ? 'Save' : 'Update'}" onclick="saveCustomer();"/>
						<input type="button" id="btnCancelArCustomer" value="Cancel" onclick="cancelCustomer();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>