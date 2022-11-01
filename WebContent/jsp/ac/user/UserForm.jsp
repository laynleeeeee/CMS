<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
  <!-- 

	Description: JSP file for the data entry form of users. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="../css/stylesheet.css" />
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<script type="text/javascript" src="../js/datetimepicker.js"></script>
<script type="text/javascript" src="../js/javascripts.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('#errorUsername').hide();
	$('#errorFirstName').hide();
	$('#errorLastName').hide();
	$('#errorMiddleName').hide();
	$('#errorBirthdate').hide();
	$('#errorContactNumber').hide();
	$('#errorAddress').hide();
	$('#errorEmailAddress').hide();
	$('#errorUserGroup').hide();
	$('#errorActive').hide();

	if ($("#birthDate").val().length == 0) {
		var dateNow = new Date();
		
		var monthString = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
		var monthNumber = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
		var month = "01";
		var subString = dateNow.toString().split(" ");
	
		for (var i=0; i<monthString.length; i++) {
			if (monthString[i] == subString[1]) {
				month = monthNumber[i];
				break;
			}
		}
		
		$("#birthDate").val(month + "/" + subString[2] + "/" + subString[3]);
	} 
	
	if ($("#birthDate").val().length > 10) {
		var subString = $("#birthDate").val().split(" ");
		var tmpDate = subString[0].split("-");
		$("#birthDate").val(tmpDate[1] + "/" + tmpDate[2] + "/" + tmpDate[0]);
		
		setSavedDate($("#birthDate").val());
	} 
});

var companyIds = [];

function pushcompanyIds(companyId) {
	companyIds.push(companyId);
}

function popcompanyIds(companyId) {
	var maxLength = companyIds.length;
	var tmpCompIds = [];
	var tmpCompId = companyId;

	if (companyIds.length > 1) {
		var maxLength = companyIds.length;
		var tmpCompIds = [];
		var tmpCompId = companyId;

   		for (var i=0; i<maxLength; i++) {
    		if (tmpCompId != companyIds[i]) {
    			tmpCompIds.push(companyIds[i]);
    		} 
   		}
   		tmpCompIds.push(tmpCompId);
   		companyIds = tmpCompIds;
   		companyIds.pop(tmpCompId);
   		tmpCompIds = null;
  	} else {
  		companyIds.pop(cbId);
  	} 
}

function clearcompanyIds() {
	companyIds = [];
}

function populateUserCompany() {
	if (usercompanyIds.length > 0 && userCompanyNames.length > 0) {
		for(var i=0; i<usercompanyIds.length; i++) {
			var option = new Option( userCompanyNames[i], usercompanyIds[i]);
	        $(option).html(userCompanyNames[i]);
		    $(option).appendTo('#companyIds');
		}
	}

	usercompanyIds = [];
	userCompanyNames = [];
}
</script>
</head>
<body >
<form:form method="POST" commandName="user" id="user">
	<div class="modFormLabel">User</div>
	<div class="modFormUnderline"> </div>
	<div style="width: 900px;" class="modForm" >
		<table class="formTable" >
			<form:hidden path="id"/>
			<form:hidden path="password" />
			<tr>
				<td>User Name: </td>
				<td>
					<form:input path="username" size="12"/>
				</td>
			</tr>
			<tr id="errorUsername">
				<td colspan="2">
					<font color="red">
						<form:errors path="username" cssClass="error"/>
					</font>
				</td>
			</tr>
				
			<tr>
				<td>First Name: </td>
				<td>
					<form:input path="firstName"/>
				</td>
			</tr>
			<tr id="errorFirstName">
				<td colspan="2">
					<font color="red">
						<form:errors path="firstName" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Last Name: </td>
				<td>
					<form:input path="lastName"/>
				</td>
			</tr>
			<tr id="errorLastName">
				<td colspan="2">
					<font color="red">
						<form:errors path="lastName" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Middle Name: </td>
				<td>
					<form:input path="middleName"/>
				</td>
			</tr>
			<tr id="errorMiddleName">
				<td colspan="2">
					<font color="red">
						<form:errors path="middleName" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Birthdate: </td>
				<td>
					<form:input path="birthDate" size="17" onblur="evalDate('birthDate')" />
					<img src="../images/cal.gif" onclick="javascript:NewCssCal('birthDate')" style="cursor:pointer" style="float: right;"/>
				</td>
			</tr>
			<tr id="errorBirthdate">
				<td colspan="2">
					<font color="red">
						<form:errors path="birthDate" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Contact Number: </td>
				<td>
					<form:input path="contactNumber"/>
				</td>
			</tr>
			<tr id="errorContactNumber">
				<td colspan="2">
					<font color="red">
						<form:errors path="contactNumber" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Address: </td>
				<td>
					<form:input path="address" size="70"/>
				</td>
			</tr>
			<tr id="errorAddress">
				<td colspan="2">
					<font color="red">
						<form:errors path="address" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Email Address: </td>
				<td>
					<form:input path="emailAddress"/>
				</td>
			</tr>
			<tr id="errorEmailAddress">
				<td colspan="2">
					<font color="red">
						<form:errors path="emailAddress" cssClass="error"/>
					</font>
				</td>
			</tr>

			<tr>
				<td>User Group: </td>
				<td>
					<form:select path="userGroupId" id="uUserGroup">
						<form:options items="${uUserGroups}" itemValue="id" itemLabel="name"/>
					</form:select>
				</td>
			</tr>
			<tr id="errorUserGroup">
				<td colspan="2">
					<font color="red">
						<form:errors path="userGroupId" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Companies: </td>
				<td> 	  
					<form:select  path="companyId">
						<form:options items="${uCompanies}" itemValue="id" itemLabel="name"/>
					</form:select>
				</td>
			</tr>
			<tr id="errorUserGroup">
				<td colspan="2">
					<font color="red">
						<form:errors path="companyId" cssClass="error"/>
					</font>
				</td>
			</tr>
				
			<tr>
				<td>Active: </td>
				<td>
					<form:checkbox path="active"/>
				</td>
			</tr>
			<tr id="errorActive">
				<td colspan="2">
					<font color="red">
						<form:errors path="active" cssClass="error"/>
					</font>
				</td>
			</tr>
		</table>
		<div id="hiddenCompanyId"></div>
		<div class="controls">
			<input type="button" id="saveUserForm" name="saveUserForm" value="SAVE" onclick="saveUser ();"/>	
			<input type="button" id="cancelUserForm" name="cancelUserForm" value="CANCEL" onclick="resetPage ();"/>	
		</div>
	</div>
</form:form>
</body>
</html>