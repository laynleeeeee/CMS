<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
 <!-- 

	Description: JSP file for the customer account profile form. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript">
$(document).ready(function() {
	$('#errorCustomerName').hide();
	$('#errorCustomerContactNumber').hide();
	$('#errorCustomerAddress').hide();
	$('#errorCustomerEmailAddress').hide();
});
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
<body>
<form:form method="POST" commandName="customerProfile" id="customerProfile">
	<div class="modFormLabel">Customer</div>
	<div class="modFormUnderline"> </div>
	<div style="width: 400px;" class="modForm">
		<table class="formTable" >
			<form:hidden path="customer.id"/>
			<form:hidden path="customer.companyId"/>
			<tr>
				<td>Name: </td>
				<td>
					<form:input id="name" path="customer.name"/>
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
				<td>Address: </td>
				<td>
					<form:input id="address" path="customer.address" size="65"/>
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
				<td>Contact Number: </td>
				<td>
					<form:input id="contactNumber" path="customer.contactNumber" />
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
				<td>Email Address: </td>
				<td>
					<form:input id="emailAddress" path="customer.emailAddress"/>
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
	<div style="width: 400px; text-align: right;" class="controls" >
		<input type="button" id="saveCustomerAcctProfile" name="saveCustomerForm" value="Save" onclick="saveCustomerAccountProfile ();"/>	
		<input type="button" id="cancelCustomerAcctProfile" name="cancelCustomerForm" value="Cancel"/>	
	</div>
</form:form>
</body>
</html>