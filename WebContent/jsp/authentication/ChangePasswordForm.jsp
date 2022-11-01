<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../include.jsp" %>
 <!-- 

	Description: Change password form page. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">

#footer {
	width: 100%;
	margin: 0 auto;
	padding: 0;
}

#footer p {
	margin: 0;
	padding: 1px 0;
	text-align: left;
	line-height: normal;
	color: #B5ADA5;
}

</style>
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$("#userName").val($("#hiddenUserName").val());
});
</script>
</head>
<body>
<form:form method="POST" commandName="loginCredential">
	<table width="100%" align="center">
		<tr height="400">
			<td>
				<table align="center">
					<tr>
						<td colspan="2" align="right"><i><b>Simple Cloud Solution</b></i><hr></hr></td>
					</tr>
					
					<tr>
					 <td colspan="2">
					 	<span style="color: red; font-size: 12px;">The system requires first time users to change their password.</span> 
					 </td>
					</tr>
					<tr> </tr>		
					<tr>
						<td>User name</td>
						<td><form:input path="userName" readonly="true"/></td>
					</tr>		
					<tr>
						<td>New Password</td>
						<td><form:password path="newPassword"/></td>
					</tr>
					<tr>
						<td>Confirm Password</td>
						<td><form:password path="confirmPassword"/></td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<font color="red">
								<form:errors path="message" cssClass="error"/>
							</font>
			           </td>
					</tr>
					<tr>
						<td colspan="2" align="right">
							<input type="submit" name="enter" value="Change Password">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</form:form>
</body>
</html>