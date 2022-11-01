<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
 <!-- 

	Description: JSP file for the listing of different users. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User Profile</title>
<link rel="stylesheet" type="text/css" href="../css/stylesheet.css" />
<link rel="stylesheet" type="text/css" href="../css/tables.css" />
<script type="text/javascript" src="../js/datetimepicker.js"></script>
<script type="text/javascript" src="../js/javascripts.js"></script>
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<script src="../js/dojo/dojo.js" type="text/javascript"></script>
<script type="text/javascript" >
$(document).ready(function() {
	$('#btnEditUser').attr('disabled', 'disabled');
});

$(function(){
	$("input#btnAddUser").click(function(){
		disableTable(true);
		$("#userForm").load("user/add");	
		$("#userForm").scrollTop();
	});

	$("input#btnEditUser").click(function(){
		disableTable(true);
		if (selectedUserId != "") {
			$("#userForm").load("user/edit/" + selectedUserId);	
			$("#userForm").scrollTop();
		}
	});
	
	$("input#btnPrintUser").click(function(){
		var url = "user/" + selectedPage + "/print";	
		window.open(url,"popup","width=1000,height=600,scrollbars=no,resizable=yes," +
				"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
	});
	
	$("input#btnSearchUser").click(function() {
		searchUser(1);
	});
});

var selectedUserId = '';
var checkBoxes = [];
var selectedPage = 1;

function selectUser(cbId) {
	var checked = document.getElementById(cbId).checked;
	 
	if (checked) checkBoxes.push(cbId);
	else {
	 	if (checkBoxes.length > 1) {
	   		var maxLength = checkBoxes.length;
	   		var tmpCbs = [];
	   		var tmpCb = cbId;

	   		for (var i=0; i<maxLength; i++) {
	    		if (tmpCb != checkBoxes[i]) {
	     			tmpCbs.push(checkBoxes[i]);
	    		} 
	   		}
	   		tmpCbs.push(tmpCb);
	   		checkBoxes = tmpCbs;
	   		checkBoxes.pop(tmpCb);
	   		tmpCbs = null;
	  	} else {
	   		checkBoxes.pop(cbId);
	  	} 
	}	
	 
	if (checkBoxes.length == 1) {
		selectedUserId = checkBoxes[0];
		document.getElementById('btnEditUser').disabled = false;
	} else {
		selectedUserId = '';
		document.getElementById('btnEditUser').disabled = true;
	}
}

function updateVeilHeight() {
	 var disabler = document.getElementById("veil");
	 var tableWrapperHeight = document.getElementById("userTable").offsetHeight - 10;
	 disabler.style.marginBottom = "-" + (tableWrapperHeight + 9) + "px"; 
	 disabler.style.height = tableWrapperHeight + "px"; 
}

function disableTable(disabled) {
	 if (disabled == true) updateVeilHeight();
	 
	 var elem = document.getElementById("veil");
	 if(disabled){
	 	elem.style.display = "";
	 } else {
	    elem.style.display = "none"; 
	 }
}

function saveUser () {
	var cmpIds = [];
	var cmpNames = [];
	
	 $("#companyIds > option").each(function(){
         cmpIds.push(this.value);
         cmpNames.push(this.text);
     });

     
	var xhrArgs = {
		form : dojo.byId("user"),
		handleAs : "text",
		load : function(data) {
			if (data == "saved") {
				dojo.byId('btnEditUser').disabled = true;
				dojo.byId("user").innerHTML = "";	
				checkBoxes = [];
				selectedUserId = '';			
				searchUser (selectedPage);
				selectedPage = 1;
				disableTable(false);
			} else {
				dojo.byId("userForm").innerHTML = data;
				if (!document.getElementById('username.errors')) $('#errorUsername').hide();
				if (!document.getElementById('firstName.errors')) $('#errorFirstName').hide();
				if (!document.getElementById('lastName.errors')) $('#errorLastName').hide();
				if (!document.getElementById('middleName.errors')) $('#errorMiddleName').hide();
				if (!document.getElementById('birthDate.errors')) $('#errorBirthdate').hide();
				if (!document.getElementById('contactNumber.errors')) $('#errorContactNumber').hide();
				if (!document.getElementById('address.errors')) $('#errorAddress').hide();
				if (!document.getElementById('emailAddress.errors')) $('#errorEmailAddress').hide();
				if (!document.getElementById('user.errors')) $('#errorUser').hide();
				if (!document.getElementById('active.errors')) $('#errorActive').hide();
				
				if ($("#birthDate").val().length > 10) {
					var monthString = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
					var monthNumber = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
					var month = "01";
					var subString = $("#birthDate").val().split(" ");
				
					for (var i=0; i<monthString.length; i++) {
						if (monthString[i] == subString[1]) {
							month = monthNumber[i];
							break;
						}
					}
					
					$("#birthDate").val(month + "/" + subString[2] + "/" + subString[5]);
				} 
			}
		},
		error : function(error) {
			dojo.byId("ajaxBody").innerHTML = "Error occured while saving.";
		}
	};
	// Call the asynchronous xhrPost
	dojo.xhrPost(xhrArgs);
}

function resetPage () {
 	dojo.byId("userForm").innerHTML = "";	
 	checkBoxes = [];
 	if (selectedUserId != '') document.getElementById(selectedUserId).checked = false;
 	selectedUserId = '';
 	document.getElementById('btnEditUser').disabled = true;
 	disableTable(false);	
}

function showForm (thisUrl) {
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {
	    	  		dojo.byId("userForm").innerHTML = data;
	    	  		dojo.byId("userForm").scrollIntoView();
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("userForm").innerHTML = "unknown error";
	      		}
	});
}

function searchUser(pageNumber) {
	var searchText = document.getElementById("searchUserText").value;
	var searchCategory = document.getElementById("searchUserOptionCategory").value;
	var searchStatus =  document.getElementById("searchUserOptionStatus").value;
	$("#userTable").load(contextPath + "/user?searchText="+searchText+"&category="+searchCategory+"&status="+
			searchStatus+"&page=" + pageNumber);
}

function loadBody() {
	document.getElementById('btnEditUser').disabled = true;
	document.getElementById('veil').style.display = 'none';
}
</script>
</head>
<body>
	<table >
		<tr>
			<td style="width: 100px;"></td>
			<td>
				<div style="width: 100%; ">
					<form:select path="searchUserOptionCategory" id="searchUserOptionCategory">
						<form:options items="${searchUserOptionCategory}" />
					</form:select>
					<input type="text" id="searchUserText" name="searchUserText" size="40%"/>
					<form:select path="searchUserOptionStatus" id="searchUserOptionStatus">
						<form:options items="${searchUserOptionStatus}" />
					</form:select>
					<input type="button" id="btnSearchUser" value="Search" />	
				</div>
			</td>
		</tr>
	</table>
	<div id="veil"></div> 
	<div id="userTable" style=" width: 98%; margin-left: 2%" >
		<%@ include file="UserTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddUser" name="btnAddUser" value="Add"  />
		<input type="button" id="btnEditUser" name="btnEditUser" value="Edit" />
		<input type="button" id="btnPrintUser" name="btnPrintUser" value="Print" />
	</div>
	<br /> <br />
	<div id="userForm" >
	</div>
</body>
</html>