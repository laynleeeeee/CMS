<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp" %>
<!--

	Description: Concessionaire classification form. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Concessionaire Classification Form</title>
<script type="text/javascript"></script>
</head>
<body>
<form:form method="POST" commandName="concessionaireClassification" >
		<div class="modFormLabel">Concessionaire Classification</div>
		<div class="modFormUnderline"> </div>
		<br>
		<div class="information">* Required fields</div>
		<table class="formTable">
			<form:hidden path="id"/>
			<form:hidden path="companyId"/>
			<tr>
				<td>* Name</td>
				<td><form:input path="name" /></td>
			</tr>
			<tr >
				<td colspan="2">
					<form:errors path="name" cssClass="error"/>
				</td>
			</tr>	
			<tr>
				<td>* Description</td>
				<td><form:input path="description" /></td>
			</tr>
			<tr >
				<td colspan="2">
					<form:errors path="description" cssClass="error"/>
				</td>
			</tr>
		</table>
	</form:form>
	<div class="controls">
			<input type="button" id="btnSaveConcessionaireClassification" value="Save" />
			<input type="button" id="btnCancelConcessionaireClassification" value="Cancel"/>	
	</div>
</body>
</html>