<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ include file="../../../../include.jsp" %>
<!--

	Description: Classification Size Form. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Classification Size Form</title>
<script type="text/javascript"></script>
</head>
<body>
	<form:form method="POST" commandName="classificationSize" >
		<div class="modFormLabel">Classification Size</div>
		<div class="modFormUnderline"> </div>
		<br>
		<div class="information">* Required field</div>
		<table class="formTable">
			<form:hidden path="id"/>
			<tr>
				<td>* Classification</td>
				<td>
					<form:select path="classificationId">
						<form:options items="${concessionaireClassifications}" itemLabel="name" itemValue="id"/>
					</form:select>
				</td>
			</tr>
			<tr>
				<td>* Classification Size</td>
				<td><form:input path="size" maxlength="5" size="15"/></td>
			</tr>
			<tr >
				<td colspan="2">
					<form:errors path="size" cssClass="error"/>
				</td>
			</tr>	
		</table>
	</form:form>
	<div class="controls">
			<input type="button" id="btnSaveClassificationSize" value="Save" />
			<input type="button" id="btnCancelClassificationSize" value="Cancel"/>	
	</div>
</body>
</html>