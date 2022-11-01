<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
 <!-- 

	Description: JSP file for the customer listing. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Customer Listing</title>
<style type="text/css">
#customerAccount {
	width: 95%;	
}
</style>

<script type="text/javascript">
function redirectToCustomerAccount(customerId) {  	
	window.location = contextPath + '/customer/' + customerId;
}

function goToPage(pageNumber) {
	selectedPage = pageNumber;
	var thisUrl = 'customerListing/'  + pageNumber;

	$(".controls").hide();
	document.getElementById("customerListingTable").innerHTML = 
		"<img src='../images/ajax-loader.gif' width='30' height='30' style='margin-left: 45%; margin-top: 15%;' />";
	dojo.xhrGet({
      url: thisUrl,
      load: function (data) {   
    	  		dojo.byId("customerListingTable").innerHTML = data; 
    	  		$(".controls").show();
    	  		$('a#page-' + pageNumber).css('font-size', '16px');
    	  		$('a#page-' + pageNumber).css('color', 'red');
      		},
      error: function (data, ioArgs){
    	  		dojo.byId("customerListingTable").innerHTML = "unknown error";
      		}
	});
}

$(document).ready(function() {	
	$('#btnEditCustomer').attr('disabled', 'disabled');
	$('#loader').hide();
	$("a#page-1").css('font-size', '16px');
	$("a#page-1").css('color', 'red');
});

$(function(){
	$("input#btnSearchCustomerListing").click(function(){
		searchCustomer(1);
	});
	
	$("input#btnAddCustomer").click(function(){
		$("#customerForm").load('customer/add');	
 		$('html, body').animate({scrollTop: $("#customerForm").offset().top}, 0050);
	});
	
	$("input#btnPrintCustomer").click(function(){
		var criteria = document.getElementById("txtSearchCustomerListing").value;
		var category = document.getElementById('searchCategory').value;
		if (criteria == "") criteria = "*";
		
		var url = 'customerListing/' + criteria + '/' + category + '/' + selectedPage + '/print';	
		
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

function showAddForm () {
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
}

function editAccount() {
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
	selectedPage = pageNumber;
	var txtField = document.getElementById("txtSearchCustomerListing");
	var textValue = txtField.value;
	var selectCategory = document.getElementById('searchCategory');
	
	var fieldLabel = selectCategory.value;
	
	if (textValue == "") textValue = "*";
	
	var thisUrl = contextPath + '/customerListing/search/'+ textValue +'/'+fieldLabel+'/'+pageNumber;
	resetPage();
		
	$("#customerListingTable").html($("#customerListingTable").html() + 
			"<img src='../images/ajax-loader.gif' width='30' height='30' style='margin-left: 45%; margin-top: 15%;' />");
		
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {  
	    	  		dojo.byId("customerListingTable").innerHTML = data;
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("customerListingTable").innerHTML = "unknown error";
	      		}
	});
}
</script>
</head>
<body>
<div id="searchControl" >
<table >
	<tr>
		<td><input type="text" id="txtSearchCustomerListing" size="50" /> </td>
		<td>
			<select id="searchCategory">
				<c:forEach items="${searchCategory}" var="category">
					<option value="${category}" ${category == 'Active' ? 'selected' : ''}>${category}</option>
				</c:forEach>
			</select>
		</td>
		<td><input type="button" id="btnSearchCustomerListing" value="Search"/></td>
	</tr>
</table>
</div>
<div id="veil" ></div>
<div id="customerListingTable" >
	<%@ include file="CustomerListTable.jsp" %>
</div>
<div id="customerAccount"></div>
<div class="controls">
		<input type="button" id="btnAddCustomer" name="btnAddCustomer" value="Add"  />
		<input type="button" id="btnEditCustomer" name="btnEditCustomer" value="Edit" onclick="editAccount ();" />
		<input type="button" id="btnPrintCustomer" name="btnPrintCustomer" value="Print" />
</div>
<br /> <br />
<div id="customerForm" ></div>
</body>
</html>