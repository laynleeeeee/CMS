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
	$('#btnEditPayment').attr('disabled', 'disabled');
	$('#btnDeletePayment').attr('disabled', 'disabled');
});

$(function(){
	$("input#btnAddPayment").click(function(){
		var customerId = $('#customerId').val();
		$("#paymentForm").load('payment/' + customerId + '/add');	
		$('html, body').animate({scrollTop: $("#paymentForm").offset().top}, 0050);
	});

	$("input#btnEditPayment").click(function(){
		if (selectedPaymentId != '') {
			var customerId = $('#customerId').val();
			$("#paymentForm").load('payment/' + customerId +  '/edit/' + selectedPaymentId);	
			$("#paymentForm").scrollTop();
		}
	});
	
	$("input#btnDeletePayment").click(function(){
		if (selectedPaymentId != '') {	
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
				if (ids.length > 0) deletePayment ('payment/1/delete/' + ids);
			}
		}
	});
});

function scrollDown() {
	dojo.xhrGet({
	      load: function (data) {
	    	  		dojo.byId("paymentForm").scrollIntoView();
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("paymentForm").innerHTML = "unknown error";
	      		}
	});
}

var selectedPaymentId = '';
var checkBoxes = [];
var toBeDeletedRecords = [];
var selectedPage = 1;

function selectPayment(cbId, refId, curDate) {
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
		selectedPaymentId = checkBoxes[0];
		document.getElementById('btnEditPayment').disabled = false;
	} else {
		selectedPaymentId = '';
		document.getElementById('btnEditPayment').disabled = true;
		document.getElementById('btnDeletePayment').disabled = true;
	}
	
	if (checkBoxes.length >= 1) {
		selectedPaymentId = checkBoxes[0];
		document.getElementById('btnDeletePayment').disabled = false;
	}
}

function updateVeilHeight() {
	 var disabler = document.getElementById("veil");
	 var tableWrapperHeight = document.getElementById("paymentTable").offsetHeight - 10;
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

function savePayment () {
	var xhrArgs = {
		form : dojo.byId("payment"),
		handleAs : "text",
		load : function(data) {
			if (data == "saved") {
				dojo.byId('btnEditPayment').disabled = true;
				dojo.byId("paymentForm").innerHTML = "";	
				checkBoxes = [];
				selectedPaymentId = '';
				loadPayment (selectedPage);
				selectedPage = 0;
			} else {
				dojo.byId("paymentForm").innerHTML = data;
				if (!document.getElementById('date.errors')) $('#errorDate').hide();
				if (!document.getElementById('referenceId.errors')) $('#errorReferenceId').hide();
				if (!document.getElementById('description.errors')) $('#errorDescription').hide();
				if (!document.getElementById('amount.errors')) $('#errorAmount').hide();
				
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
			}
		},
		error : function(error) {
			dojo.byId("ajaxBody").innerHTML = "Error occured while saving.";
		}
	};
	// Call the asynchronous xhrPost
	dojo.xhrPost(xhrArgs);
}

function deletePayment (thisUrl) {
	var xhrArgs = {
		url : thisUrl,
		form : dojo.byId("payment"),
		handleAs : "text",
		load : function(data) {
			if (data == "saved") {
				dojo.byId('btnEditPayment').disabled = true;
				dojo.byId("paymentForm").innerHTML = "";	
				checkBoxes = [];
				toBeDeletedRecords = [];
				selectedPaymentId = '';
				loadPayment (selectedPage);
				selectedPage = 1;
			} else {
				dojo.byId("paymentForm").innerHTML = data;
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
 	dojo.byId("paymentForm").innerHTML = "";	
 	checkBoxes = [];
 	toBeDeletedRecords = [];
 	if (selectedPaymentId != '') document.getElementById(selectedPaymentId).checked = false;
 	selectedPaymentId = '';
 	document.getElementById('btnEditPayment').disabled = true;
 	document.getElementById('btnDeletePayment').disabled = true;
 	disableTable(false);	
}

function showForm (thisUrl) {
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {
	    	  		dojo.byId("paymentForm").innerHTML = data;
	    	  		dojo.byId("paymentForm").scrollIntoView();
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("paymentForm").innerHTML = "unknown error";
	      		}
	});
}

function loadPayment(pageNumber) {
	var customerId = $('#customerId').val();
	var thisUrl = 'payment/' + customerId  +  '/' + pageNumber;
	
	dojo.byId("paymentTable").innerHTML = "loading...";
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {   
	    	  		dojo.byId("paymentTable").innerHTML = data;
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("paymentTable").innerHTML = "unknown error";
	      		}
	});
	
	cancelSave();
}

function goToPage (pageNumber) {
	selectedPage = pageNumber;
	loadPayment(pageNumber);
}
</script>
</head>
<body>
	<div id="paymentTable">
		<%@ include file="PaymentTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddPayment" name="btnAddPayment" value="Add"  />
		<input type="button" id="btnEditPayment" name="btnEditPayment" value="Edit" />
		<input type="button" id="btnDeletePayment" name="btnDeletePayment" value="Delete" />
	</div>
	<br /> <br />
	<div id="paymentForm"></div>
	<div id="deletePaymentForm"></div>
</body>
</html>