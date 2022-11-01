<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
 <!-- 

	Description: JSP file for the listing of different groups of users. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User Group</title>
<link rel="stylesheet" type="text/css" href="../css/stylesheet.css" />
<link rel="stylesheet" type="text/css" href="../css/tables.css" />
<script src="../js/dojo/dojo.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<script type="text/javascript" >
$(document).ready(function() {
	$("#btnEditUserGroup").attr("disabled", "disabled");
});

$(function(){
	$("input#btnAddUserGroup").click(function(){
		disableTable(true);
		$("#userGroupForm").load("userGroup/add");	
		
	});

	$("input#btnEditUserGroup").click(function(){
		disableTable(true);
		if (selectedUserGroupId != "") {
			$("#userGroupForm").load("userGroup/edit/" + selectedUserGroupId);	
			$("#userGroupForm").scrollTop();
		}
	});
	
	$("input#btnPrintUserGroup").click(function(){
		var url = "userGroup/" + selectedPage + "/print";	
		window.open(url,"popup","width=1000,height=600,scrollbars=no,resizable=yes," +
				"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
	});
	
	$("input#btnSearchUserGroup").click(function() {
		searchUserGroup(1);
	});
});

var selectedUserGroupId = "";
var checkBoxes = [];
var selectedPage = 1;

function selectUserGroup(cbId) {
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
		selectedUserGroupId = checkBoxes[0];
		document.getElementById("btnEditUserGroup").disabled = false;
	} else {
		selectedUserGroupId = '';
		document.getElementById('btnEditUserGroup').disabled = true;
	}
}

function updateVeilHeight() {
	 var disabler = document.getElementById("veil");
	 var tableWrapperHeight = document.getElementById("userGroupTable").offsetHeight - 10;
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

function saveUserGroup () {
	var xhrArgs = {
		form : dojo.byId("userGroup"),
		handleAs : "text",
		load : function(data) {
			if (data == "saved") {
				dojo.byId('btnEditUserGroup').disabled = true;
				dojo.byId("userGroupForm").innerHTML = "";	
				checkBoxes = [];
				selectedUserGroupId = '';			
				searchUserGroup (selectedPage);
				selectedPage = 1;
				disableTable(false);
			} else {
				dojo.byId("userGroupForm").innerHTML = data;
				if (!document.getElementById('name.errors')) $('#errorName').hide();
				if (!document.getElementById('description.errors')) $('#errorDescription').hide();
				if (!document.getElementById('active.errors') )$('#errorActive').hide();
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
 	dojo.byId("userGroupForm").innerHTML = "";	
 	checkBoxes = [];
 	if (selectedUserGroupId != '') document.getElementById(selectedUserGroupId).checked = false;
 	selectedUserGroupId = '';
 	document.getElementById('btnEditUserGroup').disabled = true;
 	disableTable(false);	
}

function showForm (thisUrl) {
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {
	    	  		dojo.byId("userGroupForm").innerHTML = data;
	    	  		dojo.byId("userGroupForm").scrollIntoView();
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("userGroupForm").innerHTML = "unknown error";
	      		}
	});
}

function searchUserGroup(pageNumber) {
	var searchText = document.getElementById("searchUserGroupText").value;
	var searchCategory = document.getElementById("searchUserGroupOptionCategory").value;
	var searchStatus =  document.getElementById("searchUserGroupOptionStatus").value;
	$("#userGroupTable").load(contextPath + "/userGroup?searchText="+searchText+"&category="+searchCategory+"&status="+
			searchStatus+"&page=" + pageNumber);
}
</script>
</head>
<body >
	<table >
		<tr>
			<td style="width: 100px;"></td>
			<td>
				<div style="width: 100%; ">
					<form:select path="searchUserGroupOptionCategory" id="searchUserGroupOptionCategory">
						<form:options items="${searchUserGroupOptionCategory}" />
					</form:select>
					<input type="text" id="searchUserGroupText" name="searchUserGroupText" size="40%"/>
					<form:select path="searchUserGroupOptionStatus" id="searchUserGroupOptionStatus">
						<form:options items="${searchUserGroupOptionStatus}" />
					</form:select>
					<input type="button" id="btnSearchUserGroup" value="Search" />	
				</div>
			</td>
		</tr>
	</table>
	<div id="veil"></div>
	<div id="userGroupTable">
		<%@ include file="UserGroupTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddUserGroup" name="btnAddUserGroup" value="Add"  />
		<input type="button" id="btnEditUserGroup" name="btnEditUserGroup" value="Edit" />
		<input type="button" id="btnPrintUserGroup" name="btnPrintUserGroup" value="Print" />
	</div>
	<br /> <br />
	<div id="userGroupForm">
	</div>
</body>
</html>