<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../include.jsp" %>
 <!-- 

	Description: Log in page. 
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
<script type="text/javascript">
function registerUser() {
	window.open(contextPath + "/registerUser");
}
</script>
<title>Cloud business solution</title>
</head>
<body>
<form:form method="POST" commandName="loginCredential">
	<table width="100%" align="center">
		<tr height="400">
			<td>
				<table align="center" border="0">
					<tr align="center">
						<td colspan="2" align="center">
							<img src="${pageContext.request.contextPath}/images/logo/nsb_logo.png"
								width="240px" height="80px" alt="" style="padding-bottom: 12px;"/>
						</td>
					</tr>
					<tr>
						<td align="right">User name</td>
						<td><form:input path="userName"/></td>
						<td></td>
					</tr>
					<tr>
						<td align="right">Password</td>
						<td ><form:password path="password"/> <input type="submit" name="enter" value="Login"></td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<font color="red">
								<form:errors path="message" cssClass="error"/>
							</font>
			           </td>
					</tr>
<!-- 					<tr> -->
<!-- 						<td></td> -->
<!-- 						<td> -->
<!-- 							<font color="#0876BF" style=" font-family: arial; font-size: small;"><a id="registerUser" onclick="registerUser();">Create account</a></font></td> -->
<!-- 					</tr> -->
				</table>
			</td>
		</tr>
	</table>
</form:form>
</body>
</html>