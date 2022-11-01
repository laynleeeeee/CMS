<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
 <!-- 

	Description: JSP file for the listing of different customers. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Customer Profile</title>
<script type="text/javascript" src="../js/javascripts.js"></script>

<script type="text/javascript" >
$(document).ready(function() {
	$('#btnEditCustomer').attr('disabled', 'disabled');
});

$(function(){
	$("input#btnAddCustomer").click(function(){
		disableTable(true);
		$("#customerForm").load('customer/add');	
		$("#customerForm").scrollTop();
	});
	
	$("input#btnPrintCustomer").click(function(){
		var url = 'customer/' + selectedPage + '/print';	
		window.open(url,'popup','width=1000,height=600,scrollbars=no,resizable=yes,' +
				'toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0');
	});
});

var selectedCustomerId = '';
var checkBoxes = [];
var selectedPage = 1;

function selectCustomer(cbId) {
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
		selectedCustomerId = checkBoxes[0];
		document.getElementById('btnEditCustomer').disabled = false;
	} else {
		selectedCustomerId = '';
		document.getElementById('btnEditCustomer').disabled = true;
	}
}

function updateVeilHeight() {
	 var disabler = document.getElementById("veil");
	 var tableWrapperHeight = document.getElementById("customerTable").offsetHeight - 10;
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

function showAddForm () {
	disableTable(true);
	var thisUrl = "customer/add";
	showForm(thisUrl);
}

function saveCustomer () {
	var xhrArgs = {
		form : dojo.byId("customerProfile"),
		handleAs : "text",
		load : function(data) {
			if (data == "saved") {
				dojo.byId('btnEditCustomer').disabled = true;
				dojo.byId("customerForm").innerHTML = "";	
				checkBoxes = [];
				selectedCustomerId = '';	
				searchCustomer (selectedPage);
				selectedPage = 1;
				disableTable(false);
			} else {
				dojo.byId("customerForm").innerHTML = data;
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
	dojo.byId("customerForm").innerHTML = "";	
 	checkBoxes = [];
 	if (selectedCustomerId != '') document.getElementById(selectedCustomerId).checked = false;
 	selectedCustomerId = '';
 	document.getElementById('btnEditCustomer').disabled = true;
 	disableTable(false);	
}

function editAccount() {
	disableTable(true);
	if (selectedCustomerId != '') {
		var thisUrl = "customer/edit/" + selectedCustomerId;
		showForm(thisUrl);
	}
}

function showForm (thisUrl) {
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {
	    	  		dojo.byId("customerForm").innerHTML = data;
	    	  		dojo.byId("customerForm").scrollIntoView();
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("customerForm").innerHTML = "unknown error";
	      		}
	});
}

function searchCustomer(pageNumber) {
	var txtField = document.getElementById("searchCustomerText");
	var textValue = txtField.value;
	var selectCategory = document.getElementById('searchCustOptionCategory');
	var selectStatus = document.getElementById('searchCustOptionStatus');
	
	var fieldLabel = selectCategory.value;
	var status = selectStatus.value;

	if (textValue == "") textValue = "*";
	var thisUrl = 'customer/search/'+ textValue +'/'+fieldLabel+'/'+status+'/'+pageNumber;
	resetPage();

	dojo.byId("customerTable").innerHTML = "searching...";
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {  
	    	  		dojo.byId("customerTable").innerHTML = data;
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("customerTable").innerHTML = "unknown error";
	      		}
	});
}

function goToPage (pageNumber) {
	searchCustomer(pageNumber);
}

function loadBody() {
	document.getElementById('btnEditCustomer').disabled = true;
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
					<form:select path="searchCustOptionCategory" id="searchCustOptionCategory">
						<form:options items="${searchCustOptionCategory}" />
					</form:select>
					<input type="text" id="searchCustomerText" name="searchCustomerText" size="40%"/>
					<form:select path="searchCustOptionStatus" id="searchCustOptionStatus">
						<form:options items="${searchCustOptionStatus}" />
					</form:select>
					<input type="button" id="searchCustomerBtn" name="searchCustomerBtn" value="Search" onclick="searchCustomer(1);" />	
				</div>
			</td>
		</tr>
	</table>
	<div id="veil"></div> 
	<div id="customerTable" style=" width: 98%; margin-left: 2%" >
		<%@ include file="CustomerTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddCustomer" name="btnAddCustomer" value="Add"  />
		<input type="button" id="btnEditCustomer" name="btnEditCustomer" value="Edit" onclick="editAccount ();" />
		<input type="button" id="btnPrintCustomer" name="btnPrintCustomer" value="Print" />
	</div>
	<br /> <br />
	<div id="customerForm" >
	</div>
</body>
</html>