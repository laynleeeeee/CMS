<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include.jsp" %>
 <!-- 

	Description: User registration form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${pageContext.request.contextPath}/css/status.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<script src="../js/dojo/dojo.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function () {

});

var isSaving = false;
function saveAccount () {
	if(isSaving == false) {
		isSaving = true;
		setPassword();
		$("#btnSaveAccount").attr("disabled", "disabled");
		doPostWithCallBack ("newAccountFomDiv", "divUserRegistration", function(data) {
			if(data == "saved") {
				$("#divUserRegistration").html("");
				alert("Successfully registered the user.");
				window.close();
				isSaving = false;
			}else{
				var password1 = $("#txtPassword").val();
				var password2 = $("#txtReEnteredPassword").val();
				$("#divUserRegistration").html(data);
				$("#txtPassword").val(password1);
				$("#txtReEnteredPassword").val(password2);
				isSaving = false;
			}
			$("#btnSaveAccount").removeAttr("disabled");
		});
	}
}

function setPassword() {
	var password = $("#txtPassword").val();
	$("#hdnPassword").val(password);
	var password2 = $("#txtReEnteredPassword").val();
	$("#hdnReEnteredPassword").val(password2);
}
</script>
</head>
<body>
<div id="divUserRegistration" >
<div class="formDivBigForms" style="width: 65%; margin-top: 15px;">
	<form:form method="POST" commandName="userRegistration" id="newAccountFomDiv">
		<div class="modFormLabel">Create Account</div>
		<form:hidden path="user.password" id="hdnPassword"/>
		<form:hidden path="user.ebObjectId"/>
		<form:hidden path="reEnteredPassword" id="hdnReEnteredPassword"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>KEY CODE</legend>
				<table style="margin-top: 10px;" class="formTable">
					<tr>
						<td class="labels">Key Code</td>
						<td class="value"><form:input path="keyCode"
								id="txtKeyCode" class="input" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="keyCode" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<!-- COMPANY -->
			<fieldset class="frmField_set">
				<legend>COMPANY</legend>
				<table class="formTable">
					<tr>
						<td class="labels">Name</td>
						<td class="value"><form:input path="company.name"
								id="txtCompanyName" class="input" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="company.name" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Address</td>
						<td class="value"><form:textarea path="company.address"
								id="txtCompanyAddress" class="input" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="company.address" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<!-- USER -->
			<fieldset class="frmField_set">
				<legend>USER</legend>
				<table class="formTable">
					<tr>
						<td class="labels">User Name</td>
						<td class="value"><form:input path="user.username"
								id="txtUserName" class="inputSmall" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="user.username" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Password</td>
						<td class="value"><input type="password"
								id="txtPassword" class="inputSmall" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="user.password" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Confirm Password</td>
						<td class="value"><input type="password"
								id="txtReEnteredPassword" class="inputSmall" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="reEnteredPassword" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">First Name</td>
						<td class="value"><form:input path="user.firstName"
								id="txtFirstName" class="inputSmall" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="user.firstName" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Middle Name</td>
						<td class="value"><form:input path="user.middleName"
								id="txtMiddleName" class="inputSmall" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="user.middleName" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Last Name</td>
						<td class="value"><form:input path="user.lastName"
								id="txtLastName" class="inputSmall" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="user.lastName" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Contact No.</td>
						<td class="value"><form:input path="user.contactNumber"
								id="txtContactNo" class="inputSmall" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="user.contactNumber" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Birthday</td>
						<td class="value">
							<form:input path="user.birthDate" onblur="evalDate('birthDay')"
								id="birthDay" style="width: 120px;" cssClass="dateClass2"/>
							<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif"
								onclick="javascript:NewCssCal('birthDay')"
								style="cursor: pointer" style="float: right;" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="user.birthDate" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Address</td>
						<td class="value"><form:textarea path="user.address"
								id="txtUserAddress" class="input" /></td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="user.address" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<table style="margin-top: 10px;" class="frmField_set">
				<tr><td ><input type="button" id="btnSaveAccount" value="Save" onclick="saveAccount();" style="float: right;"/> </td></tr>
			</table>
		</div>
	</form:form>
</div>
</div>
</body>
</html>