<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
  <!-- 

	Description: JSP file for the data entry form of customers. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
#interestRate, #maxAllowableAmount { text-align: right; }
#interestRate { width: 30px;} 
#maxAllowableAmount { width: 120px;} 
</style>
<script type="text/javascript">
$(document).ready(function() {
	$('#errorCustomerName').hide();
	$('#errorCustomerContactNumber').hide();
	$('#errorCustomerAddress').hide();
	$('#errorCustomerEmailAddress').hide();
	$('#errorActive').hide();
	$('#errorInterestRate').hide();
	$('#errorMaxAllowableAmount').hide();
});

$(function(){
	$('input#interestRate').click(function () {
		if ($(this).val() == "0.0") $(this).val("");
	});

	$('input#maxAllowableAmount').click(function () {
		if ($(this).val() == "0.0") $(this).val("");
	});
});
</script>
</head>
<body>
<form:form method="POST" commandName="customerProfile" id="customerProfile">
	<div class="modFormLabel">Customer</div>
	<div class="modFormUnderline"> </div>
	<div style="width: 650px;" class="modForm">
		<table class="formTable" >
			<form:hidden path="customer.id"/>
			<tr>
				<td>Name: </td>
				<td>
					<form:input path="customer.name"/>
				</td>
			</tr>
			<tr id="errorCustomerName">
				<td colspan="2">
					<font color="red">
						<form:errors path="customer.name" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Contact Number: </td>
				<td>
					<form:input path="customer.contactNumber" />
				</td>
			</tr>
			<tr id="errorCustomerContactNumber">
				<td colspan="2">
					<font color="red">
						<form:errors path="customer.contactNumber" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Address: </td>
				<td>
					<form:input path="customer.address" size="70"/>
				</td>
			</tr>
			<tr id="errorCustomerAddress">
				<td colspan="2">
					<font color="red">
						<form:errors path="customer.address" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Email Address: </td>
				<td>
					<form:input path="customer.emailAddress"/>
				</td>
			</tr>
			<tr id="errorCustomerEmailAddress">
				<td colspan="2">
					<font color="red">
						<form:errors path="customer.emailAddress" cssClass="error"/>
					</font>
				</td>
			</tr>
			<tr>
				<td>Active: </td>
				<td>
					<form:checkbox path="customer.active"/>
				</td>
			</tr>
			<tr id="errorCustomerActive">
				<td colspan="2">
					<font color="red">
						<form:errors path="customer.active" cssClass="error"/>
					</font>
				</td>
			</tr>
		</table>
	</div>
	<div class="modFormLabel">Account Setting</div>
	<div class="modFormUnderline"> </div>
	<div style="width: 300px;" class="modForm">
		<table class="formTable" >
			<form:hidden path="customerAcctSetting.id"/>
			<form:hidden path="customerAcctSetting.customerId"/>
			<tr>
				<td>Interest Rate: </td>
				<td>
					<form:input id="interestRate" path="customerAcctSetting.interestRate" size="2"/>%
				</td>
			</tr>
			<tr id="errorInterestRate">
				<td colspan="2">
					<font color="red">
						<form:errors path="customerAcctSetting.interestRate" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Max Allowable Amount: </td>
				<td>
					<form:input id="maxAllowableAmount" path="customerAcctSetting.maxAllowableAmount" size="10"/>
				</td>
			</tr>
			<tr id="errorMaxAllowableAmount">
				<td colspan="2">
					<font color="red">
						<form:errors path="customerAcctSetting.maxAllowableAmount" cssClass="error"/>
					</font>
				</td>
			</tr>
		</table>
	</div>
	<div style="width: 850px; " class="controls">
		<input type="button" id="saveCustomerForm" name="saveCustomerForm" value="Save" onclick="saveCustomer ();"/>	
		<input type="button" id="cancelCustomerform" name="cancelCustomerForm" value="Cancel" onclick="resetPage ();"/>	
	</div>
</form:form>
</body>
</html>