<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<link rel="stylesheet" type="text/css" href="../../../css/stylesheet.css" />
<script src="../../../js/dojo/dojo.js" type="text/javascript"></script>
<script type="text/javascript" src="../../../js/jquery/jquery1.7.2min.js"></script>
<script type="text/javascript" src="../../../js/datetimepicker.js"></script>
<script type="text/javascript" src="../../../js/javascripts.js"></script>
<script type="text/javascript"> 
$(document).ready(function() {
	$('#btnEditAccountReceivable').attr('disabled', 'disabled');
	$('#btnDeleteAccountReceivable').attr('disabled', 'disabled');
});

$(function(){
	$("input#btnAddAccountReceivable").click(function(){
		var customerId = $('#customerId').val();
		$("#accountReceivableForm").load('receivable/' + customerId + '/add');	
		$('html, body').animate({scrollTop: $("#accountReceivableForm").offset().top}, 0050);
	});

	$("input#btnEditAccountReceivable").click(function(){
		if (selectedAccountReceivableId != '') {
			var customerId = $('#customerId').val();
			$("#accountReceivableForm").load('receivable/' + customerId +  '/edit/' + selectedAccountReceivableId);	
			$("#accountReceivableForm").scrollTop();
		}
	});
	
	$("input#btnDeleteAccountReceivable").click(function(){
		if (selectedAccountReceivableId != '') {	
			var confirmation = '';
			if (toBeDeletedRecords.length == 1) {
				confirmation = "Are you sure you want to delete this record " + toBeDeletedRecords[0] + "?";
			} else if (toBeDeletedRecords.length > 1){
				var records = '';
				
				for(var i=0; i<toBeDeletedRecords.length; i++) {
					records += toBeDeletedRecords[i] + "\n";
				}
				
				confirmation = "Are you sure you want to delete these " + toBeDeletedRecords.length + " records?\n\n";
				confirmation += records;
			}
			var confirmDelete = confirm(confirmation) ;
			if (confirmDelete == true) {
				var ids = '';
				
				for (var i=0; i<checkBoxes.length; i++) {
					ids += checkBoxes[i] + ":"; 
				}
				if (ids.length > 0) deleteAccountReceivable ('receivable/1/delete/' + ids);
			}
		}
	});
});

var selectedAccountReceivableId = '';
var checkBoxes = [];
var toBeDeletedRecords = [];
var selectedPage = 1;

function selectAccountReceivable(cbId, refId, curDate) {
	var checked = document.getElementById(cbId).checked;
	
	if (checked){ 
		checkBoxes.push(cbId);
		toBeDeletedRecords.push(refId);
	} else {
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
	 	
	 	if (toBeDeletedRecords.length > 1) {
	 		var maxLength = toBeDeletedRecords.length;
	   		var tmpCbs = [];
	   		var tmpCb = refId;

	   		for (var i=0; i<maxLength; i++) {
	    		if (tmpCb != toBeDeletedRecords[i]) {
	     			tmpCbs.push(toBeDeletedRecords[i]);
	    		} 
	   		}
	   		tmpCbs.push(tmpCb);
	   		toBeDeletedRecords.pop(tmpCb);
	   		tmpCbs = null;
	 	} else {
	 		toBeDeletedRecords.pop(refId);
	 	}
	}	
	 
	if (checkBoxes.length == 1) {
		selectedAccountReceivableId = checkBoxes[0];
		document.getElementById('btnEditAccountReceivable').disabled = false;
	} else {
		selectedAccountReceivableId = '';
		document.getElementById('btnEditAccountReceivable').disabled = true;
		document.getElementById('btnDeleteAccountReceivable').disabled = true;
	}
	
	if (checkBoxes.length >= 1) {
		selectedAccountReceivableId = checkBoxes[0];
		document.getElementById('btnDeleteAccountReceivable').disabled = false;
	}
}

function updateVeilHeight() {
	 var disabler = document.getElementById("veil");
	 var tableWrapperHeight = document.getElementById("accountReceivableTable").offsetHeight - 10;
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

function saveAccountReceivable () {
	var xhrArgs = {
		form : dojo.byId("accountReceivable"),
		handleAs : "text",
		load : function(data) {
			if (data == "saved") {
				dojo.byId('btnEditAccountReceivable').disabled = true;
				dojo.byId("accountReceivableForm").innerHTML = "";	
				checkBoxes = [];
				selectedAccountReceivableId = '';
				loadAccountReceivable (selectedPage);
				selectedPage = 1;
			} else {
				dojo.byId("accountReceivableForm").innerHTML = data;
				if (!document.getElementById('date.errors')) $('#errorDate').hide();
				if (!document.getElementById('referenceId.errors')) $('#errorReferenceId').hide();
				if (!document.getElementById('description.errors')) $('#errorDescription').hide();
				if (!document.getElementById('amount.errors')) $('#errorAmount').hide();
				if (!document.getElementById('dueDate.errors')) $('#errorDueDate').hide();
				
				if ($("#date").val().length > 10) {
					var monthString = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
					var monthNumber = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
					var month = "01";
					var subString = $("#date").val().split(" ");
				
					for (var i=0; i<monthString.length; i++) {
						if (monthString[i] == subString[1]) {
							month = monthNumber[i];
							break;
						}
					}
					
					$("#date").val(month + "/" + subString[2] + "/" + subString[5]);
				} 
				
				if ($("#dueDate").val().length > 10) {
					var monthString = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
					var monthNumber = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
					var month = "01";
					var subString = $("#dueDate").val().split(" ");
				
					for (var i=0; i<monthString.length; i++) {
						if (monthString[i] == subString[1]) {
							month = monthNumber[i];
							break;
						}
					}
					
					$("#dueDate").val(month + "/" + subString[2] + "/" + subString[5]);
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

function deleteAccountReceivable (thisUrl) {
	var xhrArgs = {
		url : thisUrl,
		form : dojo.byId("accountReceivable"),
		handleAs : "text",
		load : function(data) {
			if (data == "saved") {
				dojo.byId('btnEditAccountReceivable').disabled = true;
				dojo.byId("accountReceivableForm").innerHTML = "";	
				checkBoxes = [];
				toBeDeletedRecords = [];
				selectedAccountReceivableId = '';
				loadAccountReceivable (selectedPage);
				selectedPage = 1;
			} else {
				dojo.byId("accountReceivableForm").innerHTML = data;
			}
		},
		error : function(error) {
			dojo.byId("ajaxBody").innerHTML = "Error occured while saving.";
		}
	};
	// Call the asynchronous xhrPost
	dojo.xhrPost(xhrArgs);
}

function cancelSave () {
 	dojo.byId("accountReceivableForm").innerHTML = "";	
 	checkBoxes = [];
 	toBeDeletedRecords = [];
 	if (selectedAccountReceivableId != '') document.getElementById(selectedAccountReceivableId).checked = false;
 	selectedAccountReceivableId = '';
 	document.getElementById('btnEditAccountReceivable').disabled = true;
 	document.getElementById('btnDeleteAccountReceivable').disabled = true;
 	disableTable(false);	
}

function showForm (thisUrl) {
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {
	    	  		dojo.byId("accountReceivableForm").innerHTML = data;
	    	  		dojo.byId("accountReceivableForm").scrollIntoView();
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("accountReceivableForm").innerHTML = "unknown error";
	      		}
	});
}

function loadAccountReceivable(pageNumber) {
	var customerId = $('#customerId').val();
	var thisUrl = 'receivable/' + customerId  +  '/' + pageNumber;
	
	dojo.byId("accountReceivableTable").innerHTML = "loading...";
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {   
	    	  		dojo.byId("accountReceivableTable").innerHTML = data;
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("accountReceivableTable").innerHTML = "unknown error";
	      		}
	});
	
	cancelSave();
}

function goToPage (pageNumber) {
	selectedPage = pageNumber;
	loadAccountReceivable(pageNumber);
}
</script>
</head>
<body>
	<div id="accountReceivableTable">
		<%@ include file="AccountReceivableTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddAccountReceivable" name="btnAddAccountReceivable" value="Add"  />
		<input type="button" id="btnEditAccountReceivable" name="btnEditAccountReceivable" value="Edit" />
		<input type="button" id="btnDeleteAccountReceivable" name="btnDeleteAccountReceivable" value="Delete" />
	</div>
	<br /> <br />
	<div id="accountReceivableForm"></div>
	<div id="deleteAcctRecForm"></div>
</body>
</html>