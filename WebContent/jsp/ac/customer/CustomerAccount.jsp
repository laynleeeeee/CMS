<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
 <!-- 

	Description: JSP file for the customer account profile form. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Customer Account</title>
<style type="text/css">
#customerDataTableControls, #customerRecordForm, #customerPaymentDataControls {
 	margin-left: 2%; 
 	margin-top: 10px; 
 	margin-bottom: 10px; 
 	width: 95%; 
}

#customerDataTableControls, #customerPaymentDataControls {
	text-align: right;
}

a.links {
	text-decoration: underline;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascripts.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>

<script type="text/javascript">
var customerId = 1;
var selectedCustomerAccountId = '';
var checkBoxes = [];
var toBeDeletedRecords = [];
var recordTypes = [];
var selectedPage = 1;
var recordType = 'RECEIVABLE';

var acctRecords = [];
var acctType = '';
var acctRefId = '';

var currentAccountBalance = 0;
var maxAllowableAmount = 0;

var isEdit = false;

$(document).ready(function() {
	customerId = $("#customerId").val();
	$("#customerDataTable").load(contextPath + "/customer/"+ customerId + "/loadAccountTable?unPaidOnly=true");
	$("#customerPaymentSearchControl").hide();
	$("#customerPaymentDataControls").hide();

	currentAccountBalance = parseFloat($('#hiddenCurrentAccountBalance').val());
	maxAllowableAmount = parseFloat($('#hiddenMaxAllowableAmount').val());
	
	$("#editCustomerRecordBtn").attr('disabled', 'disabled'); 
	$("#deleteCustomerRecordBtn").attr('disabled', 'disabled'); 
	$("#editCustomerPaymentBtn").attr('disabled', 'disabled'); 
	
	$("#cancelCustomerAcctProfile").live('click', function() {
		$("#editCustomerAcctProfileForm").html('');
		isEdit = false;
	}); 
	
	$("#cancelCustomerAcctSetting").live('click', function() {
		$("#editCustomerAcctSettingForm").html('');
		isEdit = false;
	}); 
	
	$("#editCustomerAcctProfile").live('click', function() {
		$("#editCustomerAcctProfileForm").load(contextPath + '/customer/' + customerId + '/profile/edit');
	});

	$("#editCustomerAcctSetting").live('click', function() {
		$("#editCustomerAcctSettingForm").load(contextPath + '/customer/' + customerId + '/setting/edit');
	}); 
	
	$("#redirectToReceivableBtn").live('click', function() {
		$("#customerRecordForm").load(contextPath + '/customer/' + customerId + '/receivable/add');
		$('html, body').animate({scrollTop: $(document).height()}, 'fast');
	}); 

	$("#editCustomerRecordBtn").live('click', function() {
		if (acctType != '') {
			isEdit = true;
	
			// Partially / fully paid account receivable
			if (acctType == 1 ) {
				$("#customerRecordForm").load(contextPath + '/customer/' +
						customerId +  '/receivable/edit/' + acctType + '/' + selectedCustomerAccountId );		
				$('html, body').animate({scrollTop: $(document).height()}, 'fast');
			}
			
			// Payment
			if (acctType == 2) {
				$("#customerRecordForm").load(contextPath + '/customer/' + customerId +  '/payment/edit/' + selectedCustomerAccountId );	
				$('html, body').animate({scrollTop: $(document).height()}, 'fast');
			} 
			
			// Unpaid account receivable
			if (acctType == 3) {
				$("#customerRecordForm").load(contextPath + '/customer/' + customerId +  '/receivable/edit/' + acctType + '/' +selectedCustomerAccountId );	
				$('html, body').animate({scrollTop: $(document).height()}, 'fast');
			}	
		}
	});
	
	$("#redirectToPaymentBtn").live('click', function() {
		$("#customerRecordForm").load(contextPath + '/customer/' + customerId + '/payment/add');
		$('html, body').animate({scrollTop: $(document).height()}, 'fast');
	}); 
		
	$("#deleteCustomerRecordBtn").live('click', function() {
		alert ("deleteCustomerRecordBtn");
		  /* if (selectedCustomerAccountId != '') {	
			var confirmation = '';
			if (checkBoxes.length == 1) {
				confirmation = "Are you sure you want to delete this record " + toBeDeletedRecords[0] + "?";
			} else if (checkBoxes.length > 1){
				confirmation = "Are you sure you want to delete " + checkBoxes.length + " records?\n\n";
			}
			var confirmDelete = confirm(confirmation) ;
			if (confirmDelete == true) {
				var ids = '';
				
				for (var i=0; i<checkBoxes.length; i++) {
					ids += checkBoxes[i] + ";"; 
				}
		
				if (ids.length > 0) {
					deleteCustomerRecord ('customer/' + customerId + '/record/delete/' + ids);
					$("#customerRecordForm").html('');
				}
					
			} 
		}  */
	});
	
	$("#printCustomerRecordBtn").live('click', function() {
		var url = contextPath + '/customer/' + customerId + '/' + selectedPage + '/print';
		window.open(url,'popup','width=1000,height=600,scrollbars=yes,resizable=yes,' +
		'toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0');
	}); 
	
	$("#cancelAccountReceivableBtn").live('click', function() {
		dojo.byId("customerRecordForm").innerHTML = "";	
		isEdit = false;
	});
	
	$("#cancelPaymentBtn").live('click', function() {
		dojo.byId("customerRecordForm").innerHTML = "";	
		isEdit = false;
	});
	
	$("#mainCheckbox").live('click', function() {
		if ((this).checked) {
			checkBoxes = [];
			toBeDeletedRecords = [];
			recordTypes = [];
			$("#customerInfo option").each(function(){
				$("#" + $(this).val() + "").prop("checked", true);
			 	var split = $(this).text().split(';');
			 	selectedCustomerAccountId = $(this).val();
			 	checkBoxes.push($(this).val());
			 	toBeDeletedRecords.push(split[0]);
			 	recordTypes.push(split[1]);
			});
			
			if (checkBoxes.length > 0) {
				$("#deleteCustomerRecordBtn").removeAttr('disabled'); 
				$("#redirectToPaymentBtn").removeAttr('disabled'); 
			}
				
		} else {
			checkBoxes = [];
			toBeDeletedRecords = [];
			recordTypes = [];
			selectedCustomerAccountId = '';
			$("#customerInfo option").each(function(){
				$("#" + $(this).val() + "").prop("checked", false);
			});
			
			$("#editCustomerRecordBtn").attr('disabled', 'disabled'); 
			$("#deleteCustomerRecordBtn").attr('disabled', 'disabled'); 
			$("#redirectToPaymentBtn").attr('disabled', 'disabled'); 
		}
	});
});

function saveCustomerAccountProfile () {
	var xhrArgs = {
			form : dojo.byId("customerProfile"),
			handleAs : "text",
			load : function(data) {
				if (data == "saved") {	
					dojo.byId("editCustomerAcctProfileForm").innerHTML = "";	
					checkBoxes = [];
					toBeDeletedRecords = [];
					recordTypes = [];
					reloadProfile();
					reloadTable(selectedPage);
					isEdit = false;
				} else {
					dojo.byId("editCustomerAcctProfileForm").innerHTML = data;
				}
			},
			error : function(error) {
				dojo.byId("ajaxBody").innerHTML = "Error occured while saving.";
			}
		};
		// Call the asynchronous xhrPost
		dojo.xhrPost(xhrArgs); 
}

function saveCustomerAccountSetting () {
	document.getElementById("editCustomerAcctProfileForm").innerHTML = "";
	var xhrArgs = {
			form : dojo.byId("customerProfile"),
			handleAs : "text",
			load : function(data) {
				if (data == "saved") {
					dojo.byId("editCustomerAcctSettingForm").innerHTML = "";	
					checkBoxes = [];
					toBeDeletedRecords = [];
					recordTypes = [];
					reloadProfile();
					reloadTable(selectedPage);
					isEdit = false;
				} else {
					dojo.byId("editCustomerAcctSettingForm").innerHTML = data;
				}
				
			},
			error : function(error) {
				dojo.byId("ajaxBody").innerHTML = "Error occured while saving.";
			}
		};
		// Call the asynchronous xhrPost
		dojo.xhrPost(xhrArgs); 
}

function actualSaveAccountReceivable() {
	var xhrArgs = {
			form : dojo.byId("accountReceivable"),
			handleAs : "text",
			load : function(data) {
				if (data == "saved") {
					dojo.byId("customerRecordForm").innerHTML = "";	
					reloadProfile();
					reloadAccountReceivableTable(selectedPage);
					checkBoxes = [];
					toBeDeletedRecords = [];
					recordTypes = [];
					acctRecords = [];
					acctType = '';
					selectedCustomerAccountId = '';
					disableControls();
					isEdit = false;
				} else {
					dojo.byId("customerRecordForm").innerHTML = data;
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

function saveAccountReceivable () {
	if ((currentAccountBalance + parseFloat(document.getElementById("amount").value)) > maxAllowableAmount) {
		var confirmation = 'The amount and the current account balance has exceeded the maximum allowable amount. ';
		confirmation += 'Do you still want to allow the customer to add an account receivable?';
		var confirmSave = confirm(confirmation) ;
		if (confirmSave == true) {
			actualSaveAccountReceivable();
		}
	} else {
		actualSaveAccountReceivable();
	}
}

function convertToMySQLDate(date) {
	var subString = date.split("/");
	var mysqlDate = date;
	
	if (subString.length == 3) mysqlDate =  subString[2] + "-" + subString[0] + "-" + subString[1];
	if (subString.length == 2) mysqlDate =  subString[2] + "-" + subString[0];
	
	return mysqlDate;
}

function searchAccountReceivable(pageNumber) {
	var txtField = document.getElementById("txtSearchAccountReceivable");
	var textValue = txtField.value;
	var selectCategory = document.getElementById('searchAccountReceivableCategory');
	var fieldLabel = selectCategory.value;
	
	if (textValue == "") textValue = "*";
	else textValue = convertToMySQLDate(textValue);
	
	var thisUrl = 'customer/' + customerId + '/accountReceivable/search/' + 
		textValue + '/' + fieldLabel + '/' + pageNumber;

	dojo.byId("customerDataTable").innerHTML = "searching...";
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {  
	    	  		dojo.byId("customerDataTable").innerHTML = data;
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("customerDataTable").innerHTML = "unknown error";
	      		}
	});
}

function searchPayment(pageNumber) {
	var txtField = document.getElementById("txtSearchPayment");
	var textValue = txtField.value;
	var selectCategory = document.getElementById('searchPaymentCategory');
	var fieldLabel = selectCategory.value;
	
	if (textValue == "") textValue = "*";
	else textValue = convertToMySQLDate(textValue);
	
	var thisUrl = 'customer/' + customerId + '/payment/search/' + 
		textValue + '/' + fieldLabel + '/' + pageNumber;

	dojo.byId("customerDataTable").innerHTML = "searching...";
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {  
	    	  		dojo.byId("customerDataTable").innerHTML = data;
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("customerDataTable").innerHTML = "unknown error";
	      		}
	});
}

function showPaymentDetails () {
	
		var xhrArgs = {
				form : dojo.byId("payment"),
				handleAs : "text",
				load : function(data) {
					if (data == "saved") {
						var thisUrl = contextPath + '/customer/' + customerId + '/payment/paymentDetail/' + isEdit;
						dojo.xhrGet({
						      url: thisUrl,
						      load: function (data) {  
						    	  		dojo.byId("customerRecordForm").innerHTML = data;
						      		},
						      error: function (data, ioArgs){
						    	  		dojo.byId("customerRecordForm").innerHTML = "unknown error";
						      		}
						});
						isEdit = false;
					} else {
						dojo.byId("customerRecordForm").innerHTML = data;
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

function savePayment () {
	var xhrArgs = {
			form : dojo.byId("payment"),
			handleAs : "text",
			load : function(data) {
				if (data == "saved") {
					dojo.byId("customerRecordForm").innerHTML = "";	
					reloadProfile();
					reloadAccountReceivableTable(selectedPage);
					isEdit = false;
					acctRecords = [];
					$("#editCustomerRecordBtn").attr('disabled', 'disabled'); 
					$("#deleteCustomerRecordBtn").attr('disabled', 'disabled'); 
				} else {
					dojo.byId("customerRecordForm").innerHTML = "unknown error";
				}
			},
			error : function(error) {
				dojo.byId("ajaxBody").innerHTML = "Error occured while saving.";
			}
		};
//	 	Call the asynchronous xhrPost
		dojo.xhrPost(xhrArgs);
}

function addCustomerRecord () {
	var thisUrl = 'receivable/' + customerId + '/add'; 
	
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {   
	    	  		dojo.byId("customerRecordForm").innerHTML = data;
	    	  		dojo.byId("customerRecordForm").scrollIntoView();
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("customerRecordForm").innerHTML = "unknown error";
	      		}
	});
}

function editCustomerRecord () {
	var thisUrl = 'receivable/' + customerId +  '/edit/' + selectedCustomerAccountId; 
	
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {   
	    	  		dojo.byId("customerRecordForm").innerHTML = data;
	    	  		dojo.byId("customerRecordForm").scrollIntoView();
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("customerRecordForm").innerHTML = "unknown error";
	      		}
	});
}

function deleteCustomerRecord (thisUrl) {
	var xhrArgs = {
		url : thisUrl,
		form : dojo.byId("accountReceivable"),
		handleAs : "text",
		load : function(data) {
			if (data == "saved") {
				checkBoxes = [];
				toBeDeletedRecords = [];
				recordTypes = [];
				selectedAccountReceivableId = '';
				selectedPage = 1;
				reloadProfile();
				reloadTable(selectedPage);
				dojo.byId('editCustomerRecordBtn').disabled = true;
				dojo.byId('deleteCustomerRecordBtn').disabled = true;
			} else {
				dojo.byId("customerDataTable").innerHTML = data;
			}
		},
		error : function(error) {
			dojo.byId("ajaxBody").innerHTML = "Error occured while saving.";
		}
	};
	// Call the asynchronous xhrPost
	dojo.xhrPost(xhrArgs);
}

function reloadProfile() {
	var thisUrl =  contextPath + '/customer/' + customerId + '/reloadProfile';
	dojo.xhrGet({
      url: thisUrl,
      load: function (data) {   
    	  		dojo.byId("customerAccountProfile").innerHTML = data;
      		},
      error: function (data, ioArgs){
    	  		dojo.byId("customerAccountProfile").innerHTML = "unknown error";
      		}
	});
} 

function reloadAccountReceivableTable(pageNumber) {
	document.getElementById("customerAccountSearchControl").style.display = '';
	document.getElementById("customerDataTableControls").style.display = '';
		
	var thisUrl = contextPath + '/customer/' + customerId + '/reloadAccountTable/' + pageNumber;

	dojo.xhrGet({
      url: thisUrl,
      load: function (data) {   
    	  		dojo.byId("customerDataTable").innerHTML = data;
      		},
      error: function (data, ioArgs){
    	  		dojo.byId("customerDataTable").innerHTML = "unknown error";
      		}
	});
} 

function reloadPaymentTable(pageNumber) {
	document.getElementById("customerAccountSearchControl").style.display = 'none';
	document.getElementById("customerDataTableControls").style.display = 'none';

	var thisUrl = contextPath + '/customer/' + customerId + '/reloadPaymentTable/' + pageNumber;

	dojo.xhrGet({
      url: thisUrl,
      load: function (data) {   
    	  		dojo.byId("customerDataTable").innerHTML = data;
      		},
      error: function (data, ioArgs){
    	  		dojo.byId("customerDataTable").innerHTML = "unknown error";
      		}
	});
} 

function disableControls() {
	document.getElementById("editCustomerRecordBtn").disabled = true;
	document.getElementById("deleteCustomerRecordBtn").disabled = true;
}

function goToPage (pageNumber) {
	selectedPage = pageNumber;
	reloadTable(selectedPage);
}

function selectCustomerAccount(accountId, refId, type) {
	var checked = document.getElementById(accountId).checked;
	recordType = document.getElementById(type).value;

	if (checked){ 
		checkBoxes.push(accountId);
		toBeDeletedRecords.push(refId);
		recordTypes.push(recordType);
	} else {
	 	if (checkBoxes.length > 1) {
	   		var maxLength = checkBoxes.length;
	   		var tmpCbs = [];
	   		var tmpCb = accountId;

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
	   		checkBoxes.pop(accountId);
	  	} 
	}	
	
	if (checkBoxes.length == 1) {
		selectedCustomerAccountId = checkBoxes[0];
		document.getElementById('editCustomerRecordBtn').disabled = false;
	} else {
		selectedCustomerAccountId  = '';
		document.getElementById('editCustomerRecordBtn').disabled = true;
		document.getElementById('redirectToPaymentBtn').disabled = true;
		document.getElementById('deleteCustomerRecordBtn').disabled = true;
	}
	
	if (checkBoxes.length >= 1) {
		selectedCustomerAccountId = checkBoxes[0];
		document.getElementById('deleteCustomerRecordBtn').disabled = false;
		document.getElementById('redirectToPaymentBtn').disabled = false;
	}
}

function printPaymentDetail () {	
	var thisUrl = contextPath + '/customer/' + customerId + '/payment/paymentDetail/print';
	window.open(thisUrl,'popup','width=1200,height=600,scrollbars=no,resizable=yes,' +
				'toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0');
}

function cancelPayment () {
	var thisUrl = contextPath + '/customer/' + customerId + '/payment/paymentDetail/cancel' ;

	dojo.xhrGet({
      url: thisUrl,
      load: function (data) {   
    	  		dojo.byId("customerRecordForm").innerHTML = "";
    	  		window.location.reload(true);
      		},
      error: function (data, ioArgs){
    	  		dojo.byId("customerRecordForm").innerHTML = "unknown error";
      		}
	});
	
	isEdit = false;
}

function reloadTable (checkBox) {
	$("#customerDataTable").load(contextPath + "/customer/"+ customerId + "/loadAccountTable?unPaidOnly="+checkBox.checked);
}

function removeElem(array, item){
	for(var i in array){
		if(array[i]==item){
			array.splice(i,1);
			break;
		}
	}
	return array;
}

function selectAcctRecord(arId, arType, arRefId) {
	var checked = document.getElementById(arId+''+arType).checked;
	if (checked) {
		selectedCustomerAccountId = arId;
		acctRecords.push(arId+''+arType);
		acctType = arType;
		acctRefId = arRefId;
	} else {
		removeElem(acctRecords, arId+''+arType);
	}
	
	if (acctRecords.length == 1) {
		$("#editCustomerRecordBtn").removeAttr('disabled'); 
		$("#deleteCustomerRecordBtn").removeAttr('disabled'); 
	} else {
		$("#editCustomerRecordBtn").attr('disabled', 'disabled'); 
		if (acctRecords.length == 0) {
			$("#deleteCustomerRecordBtn").attr('disabled', 'disabled'); 
			acctType = '';
			acctRefId = '';
			selectedCustomerAccountId = '';
		}
	}
}

</script>
</head>
<body>
<div id="customerAccountContainer" >
<input type="hidden" id="hiddenCurrentAccountBalance" value="${currentAccountBalance}" />
<input type="hidden" id="hiddenMaxAllowableAmount" value="${maxAllowableAmount}" />
<input type="hidden" id="customerId" value="${customerId}">
<div id="customerAccountProfile">
	<%@ include file="CustomerAccountProfile.jsp" %>
</div>
<div id="customerAccountSearchControl" style="margin-left: 2%">
<table style="width: 700px;">
	<tr>
		<td>
			<input type="checkbox" checked="checked" onclick="reloadTable(this);"> Exclude payment and paid account receivable
		</td>
	</tr>
</table>
</div>

<div id="customerDataTable">
</div>
<div id="customerDataTableControls">
	<input type="button" value="Receivable" id="redirectToReceivableBtn" />
	<input type="button" value="Payment" id="redirectToPaymentBtn"/>
	<input type="button" value="Edit" id="editCustomerRecordBtn"  />
	<input type="button" value="Delete" id="deleteCustomerRecordBtn"/>
	<input type="button" value="Print" id="printCustomerRecordBtn"/>
</div>

<div id="customerPaymentDataControls">
	<input type="button" value="Print" id="printCustomerPaymentBtn"/>
</div>
<div id="customerRecordForm">
</div>
</div>

</body>
</html>