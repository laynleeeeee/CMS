<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!-- 

	Description: Concessionaire form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Concessionaire Form</title>
<script type="text/javascript">
$(document).ready(function () {
	$("#classificationId").val($("#concessionaireClassification option").val());
});

$(function() {
	$("#concessionaireClassification").change(function() {
		$("#classificationId").val($("#concessionaireClassification").val());
	});
});
</script>
</head>
<body>	
	<form:form method="POST" commandName="concessionaire" >
		<div class="modFormLabel">Concessionaire</div>
		<div class="modFormUnderline"> </div>
		<br>
		<div class="information">* Required fields</div>
		<table class="formTable">
			<form:hidden path="id"/>
			<form:hidden path="companyId"/>
			<form:hidden path="classificationId"/>
				
			<tr>
				<td>Classification</td>
				<td>
					<select id="concessionaireClassification" >
						<c:forEach var="classification" items="${concessionaireClassifications}">
							<option value="${classification.id}">${classification.name}</option>
						</c:forEach>
					</select>	
				</td>
			</tr>
			
			<tr>
				<td>* First Name</td>
				<td><form:input path="firstName" maxLength="45" size="12" /></td>
			</tr>
			<tr >
				<td colspan="2">
					<form:errors path="firstName" cssClass="error"/>
				</td>
			</tr>
				
			<tr>
				<td>Middle Name</td>
				<td><form:input path="middleName" maxLength="45" size="12" /></td>
			</tr>
			<tr >
				<td colspan="2">
					<form:errors path="middleName" cssClass="error"/>
				</td>
			</tr>
			
			<tr>
				<td>* Last Name</td>
				<td><form:input path="lastName" maxLength="45" size="12" /></td>
			</tr>
			<tr >
				<td colspan="2">
					<form:errors path="lastName" cssClass="error"/>
				</td>
			</tr>
			
			<tr>
				<td>Contact Number</td>
				<td><form:input path="contactNumber" maxLength="13" size="6"/></td>
			</tr>
			<tr >
				<td colspan="2">
					<form:errors path="contactNumber" cssClass="error"/>
				</td>
			</tr>
			
			<tr>
				<td>* Address</td>
				<td><form:textarea path="address" class="addressClass" maxLength="100" /></td>
			</tr>
			<tr >
				<td colspan="2">
					<form:errors path="address" cssClass="error"/>
				</td>
			</tr>
			
			<tr>
				<td>Active: </td>
				<td>
					<form:checkbox path="active"/>
				</td>
			</tr>
		</table>
	</form:form>
	
	<div class="controls">
			<input type="button" id="btnSaveConcessionaire" value="Save" />		
			<input type="button" id="btnCancelConcessionaire" value="Cancel"/>	
	</div>
</body>
</html>