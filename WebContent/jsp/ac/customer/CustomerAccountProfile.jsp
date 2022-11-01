<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
 <!-- 

	Description: JSP file for the customer account profile form. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
#editCustomerAcctProfile, #editCustomerAcctSetting {
	float: right;
}

.label {
	width: 20%;
}

td {
	font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
	font-size: 13px;
}
</style>
</head>
<body>
<table  style=" width: 95%; margin-left: 2%" >
	<tr>
	  	<td class="label" >Name: </td>
		<td>${customerName }</td>
 	</tr>

	<tr>
		<td class="label">Address: </td>
		<td>${customerAddress }</td>
	</tr>

	<tr>
		<td class="label">Contact Number: </td>
		<td>${customerContactNumber }</td>
	</tr>

	<tr>
		<td class="label">Email Address: </td>
		<td>${customerEmailAddress}</td>
	</tr>

	<tr>
		<td colspan="2" >
			<input type="button" id="editCustomerAcctProfile" value="Edit"> 
		</td>
	</tr>

	<tr> 
		<td colspan="2"> 
			<div id="editCustomerAcctProfileForm"> </div>
		</td>
	</tr>

	<tr>
		<td colspan="2"><hr></td>
	</tr>

	<tr>
		<td>Interest Rate: </td>
		<td>${customerInterestRate}%</td>
	</tr>

	<tr>
		<td>Maximum Allowable Amount: </td>
		<td>
			<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerMaxAllowableAmount}"/> 
			
			<c:if test="${customerCurrentAccountBalance > customerMaxAllowableAmount}">
			<c:set var="excessAmount" value="${customerCurrentAccountBalance - customerMaxAllowableAmount}"/>
				(
				<span style="color: red">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${excessAmount}"/>
				</span>
				)
			</c:if>
		</td>
	</tr>

	<tr>
		<td colspan="2" >
			<input type="button" id="editCustomerAcctSetting" value="Edit"> 
		</td>
	</tr>

	<tr> 
		<td colspan="2"> 
			<div id="editCustomerAcctSettingForm" style="text-align: center"> </div>
		</td>
	</tr>

	<tr>
		<td colspan="2"><hr></td>
	</tr>

	<tr>
	  	<td>Current Account Balance: </td>
		<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerCurrentAccountBalance}" /></td>
 	</tr>

	<tr>
		<td>Last Payment Date: </td>
		<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${customerLastPaymentDate}" /></td>
	</tr>

	<tr>
		<td>Last Payment Amount: </td>
		<td><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerLastPaymentAmount}" /></td>
	</tr>
	
	<tr>
		<td colspan="2"><hr></td>
	</tr>
</table>
</body>
</html>