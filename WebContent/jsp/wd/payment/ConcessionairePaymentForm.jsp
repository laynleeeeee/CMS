<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!-- 

	Description: Displays concessionaire payment form
 -->
<form:form method="POST" commandName="concessionairePayment" >
	<div class="information">* Required field</div>
	<table class="formTable">
		<tr>
			<td>* Date</td>
			<td class="tdDateFilter">
				<form:input path="date" maxlength="10" size="10" cssClass="dateClass" />
				<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('date')" style="cursor:pointer"
							style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td colspan="2"><form:errors path="date" cssClass="error" /></td>
		</tr>
		<tr>
			<td>* Amount</td>
			<td>
				<form:input path="payment" cssClass="numberClass"/>
			</td>
		<tr>
		<tr>
			<td colspan="2"><form:errors path="payment" cssClass="error"/></td>
		</tr>
	</table>
</form:form>