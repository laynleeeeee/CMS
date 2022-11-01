<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
  <!-- 

	Description: JSP file for the data entry form of customer account setting. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">
$(document).ready(function() {
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
	<div class="modFormLabel" style="text-align: left;">Account Setting</div>
	<div class="modFormUnderline"> </div>
	<div style="width: 300px;" class="modForm">
		<table class="formTable" >
			<form:hidden path="customerAcctSetting.id"/>
			<form:hidden path="customerAcctSetting.customerId"/>
			<tr>
				<td style="text-align: left;">Interest Rate: </td>
				<td style="text-align: left;">
					<form:input id="interestRate" path="customerAcctSetting.interestRate" size="2"/>
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
				<td style="text-align: left;">Max Allowable Amount: </td>
				<td style="text-align: left;">
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
	<div style="width: 400px; text-align: right;" class="controls" >
		<input type="button" id="saveCustomerAcctSetting" name="saveCustomerForm" value="Save" onclick="saveCustomerAccountSetting ();"/>	
		<input type="button" id="cancelCustomerAcctSetting" name="cancelCustomerForm" value="Cancel"/>	
	</div>
</form:form>
</body>
</html>