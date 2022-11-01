<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">

#accountListTable  {
 	margin-top: 10px; 
 	border-collapse:collapse; 
 	width: 100%; 
}

#accountListTable th, #accountListTable td{
	font-size: 12px;
	padding: 5px;
}

#accountListTable th{
	border: 1px solid #FFF;
	background-color: #000;
	color: #FFF;
}

#accountListTable td {
	border: 1px solid #EEE;
}

#nameAndDate {
	width: 100%; 
}
#paymentFormTable {
	border: none;
	border-collapse:none;
}

#selectedReceivables {
	display: none;
}

 #cash, #change {
 	text-align: right;
 }
 
 .controls {
 	text-align: right;
 	margin-top: 10px;
 }
 
 @media print {
	.controls, #frmPayment {
		display: none;
 }
}
</style>
<script type="text/javascript" src="../../../../js/datetimepicker.js"></script>
<script type="text/javascript" src="../../../../js/javascripts.js"></script>
<script src="../../../../js/dojo/dojo.js" type="text/javascript"></script>
<script type="text/javascript" src="../../../../js/jquery/jquery1.7.2min.js"></script>
<script type="text/javascript">
var customerId = 1;
var toBeAddedReceivables = [];
var toBeAddedRecord = [];
var toBeRemovedReceivables = [];
var allReceivableIds = "";
var selectedReceivableIds = "";
var totalSelectedAcctReceivableAmt = 0;

var currentReceivableIds = "";
var remainingReceivableIds = "";

$(document).ready(function() {		
	$("#addReceivableTableDiv").hide();
	customerId = $('#customerId').val();
	selectedReceivableIds = $('#selectedReceivableIds').val();
	allReceivableIds = $('#allReceivableIds').val();

	currentReceivableIds = $("#hiddenCurrentIds").val();
	remainingReceivableIds = $("#hiddenRemainingIds").val();
	
	totalSelectedAcctReceivableAmt = Math.round(($('#totalSelectedAcctReceivableAmt').val())*100)/100;
	$("#amount").val(totalSelectedAcctReceivableAmt);
	
	$('#errorReferenceId').hide();
	$('#errorDescription').hide();
	$('#errorAmount').hide();

	if ($("#paymentDate").val().length == 0) {
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
		$("#paymentDate").val(month + "/" + subString[2] + "/" + subString[3]);
	} 
	
	if ($("#paymentDate").val().length > 10) {
		var subString = $("#paymentDate").val().split(" ");
		var tmpDate = subString[0].split("-");
		$("#paymentDate").val(tmpDate[1] + "/" + tmpDate[2] + "/" + tmpDate[0]);
		
		setSavedDate($("#paymentDate").val());
	} 
	
	$("#date").val($("#paymentDate").val());
});

$(function(){
	$('input#cash').click(function () {
		if ($(this).val() == "0.0") $(this).val("");
	});
});

function setDefaultAmount() {
	if ($("#cash").val().length == 0) {
		$("#cash").val("0.0");
	}
}

function checkAmount () {
	if ($("#cash").val() < totalSelectedAcctReceivableAmt) {
		alert('The amount entered is less than the total debt.');
	} else {
		var changeVal = Math.round(($("#cash").val() - totalSelectedAcctReceivableAmt)*100)/100;
		if (changeVal == 0) {
			$("#changeDiv").html("0.00");
			$("#paymentChange").val(0.00);
		} else {
			$("#changeDiv").html(changeVal);
			$("#paymentChange").val(changeVal);
		}
	}
}

function convertToMySQLDate(paymentDate) {
	var subString = paymentDate.split("/");
	return subString[2] + "-" + subString[0] + "-" + subString[1];
}

function changeCurrentBalance() {
	currentReceivableIds = $("#hiddenCurrentIds").val();
	var newDate = document.getElementById('paymentDate').value;
 
 	var thisUrl = '/CBS/a/payment/' + customerId  +  '/' + currentReceivableIds + '/' + convertToMySQLDate(newDate) + '/changeCurrentBalance';
 	
	dojo.byId("receivableListTable").innerHTML = 
		"<img src='../../../../images/ajax-loader.gif' width='30' height='30' style='margin-left: 45%; margin-top: 15%;' />";
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) { 
	    	  		dojo.byId("receivableListTable").innerHTML = data;
	    	  		totalSelectedAcctReceivableAmt = Math.round(($('#totalSelectedAcctReceivableAmt').val())*100)/100;
	    	  	
	    	  		$("#date").val(newDate);
	    	  		$("#amount").val(totalSelectedAcctReceivableAmt);
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("receivableListTable").innerHTML = "unknown error";
	      		}
	});
}

function populateToBeAddedRecord(accountReceivableId) {
	var checked = document.getElementById(accountReceivableId).checked;
	
	if (checked) toBeAddedRecord.push(accountReceivableId);
	else {
	 	if (toBeAddedRecord.length > 1) {
	   		var maxLength = toBeAddedRecord.length;
	   		var tmpCbs = [];
	   		var tmpCb = accountReceivableId;

	   		for (var i=0; i<maxLength; i++) {
	    		if (tmpCb != toBeAddedRecord[i]) {
	     			tmpCbs.push(toBeAddedRecord[i]);
	    		} 
	   		}
	   		tmpCbs.push(tmpCb);
	   		toBeAddedRecord = tmpCbs;
	   		toBeAddedRecord.pop(tmpCb);
	   		tmpCbs = null;
	  	} else {
	  		toBeAddedRecord.pop(accountReceivableId);
	  	} 
	}	
}

function populateReceivable(accountReceivableId) {
	var checked = document.getElementById(accountReceivableId).checked;
	
	if (checked) toBeRemovedReceivables.push(accountReceivableId);
	else {
	 	if (toBeRemovedReceivables.length > 1) {
	   		var maxLength = toBeRemovedReceivables.length;
	   		var tmpCbs = [];
	   		var tmpCb = accountReceivableId;

	   		for (var i=0; i<maxLength; i++) {
	    		if (tmpCb != toBeRemovedReceivables[i]) {
	     			tmpCbs.push(toBeRemovedReceivables[i]);
	    		} 
	   		}
	   		tmpCbs.push(tmpCb);
	   		toBeRemovedReceivables = tmpCbs;
	   		toBeRemovedReceivables.pop(tmpCb);
	   		tmpCbs = null;
	  	} else {
	   		toBeRemovedReceivables.pop(accountReceivableId);
	  	} 
	}	
}

Array.prototype.remByVal = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] === val) {
            this.splice(i, 1);
            i--;
        }
    }
    return this;
}

function addReceivables () {
	remainingReceivableIds = $("#hiddenRemainingIds").val();
	var remainingReceivableIdsArr = remainingReceivableIds.split(":");
	var arraySize = remainingReceivableIdsArr.length - 1;
	var ids = "";
	
	if (arraySize > 0) {
		var newDate = document.getElementById('paymentDate').value;
		
		for (var i=0; i<arraySize; i++) ids += remainingReceivableIdsArr[i] + ":";
		
		var thisUrl = '/CBS/a/payment/' + customerId  +  '/' + ids + '/' + convertToMySQLDate(newDate) + '/add';
		
		dojo.byId("addReceivableTable").innerHTML = 
			"<img src='../../../../images/ajax-loader.gif' width='30' height='30' style='margin-left: 45%; margin-top: 15%;' />";
		dojo.xhrGet({
		      url: thisUrl,
		      load: function (data) { 
		    	  		dojo.byId("addReceivableTable").innerHTML = data;
		    	  		dojo.byId("addReceivableTableDiv").style.display = '';
		    	  		totalSelectedAcctReceivableAmt = Math.round(($('#totalSelectedAcctReceivableAmt').val())*100)/100;			    	
		      		},
		      error: function (data, ioArgs){
		    	  		dojo.byId("addReceivableTable").innerHTML = "unknown error";
		      		}
		});
	}
}

function removeReceivables () {
	if (toBeRemovedReceivables.length > 0) {
		currentReceivableIds = $("#hiddenCurrentIds").val();
		var currentReceivableIdsArr = currentReceivableIds.split(":");
		var arraySize1 = currentReceivableIdsArr.length - 1;
		var arraySize2 = toBeRemovedReceivables.length;
		var ids = "";
	
		if (arraySize1 == arraySize2) {
			alert('You need to have at least one record otherwise cancel the transaction by closing this form.');
		} else {
			for (var i=0; i<arraySize2; i++) {
				for (var j=0; j<arraySize1; j++) {
					if (toBeRemovedReceivables[i] ==  currentReceivableIdsArr[j]) {
						currentReceivableIdsArr.remByVal(currentReceivableIdsArr[i]);
						break;
					}
				}
			}
			
			if (currentReceivableIdsArr.length > 0) {
				var arraySize3 = currentReceivableIdsArr.length - 1; 
				for (var i=0; i<arraySize3; i++) ids += currentReceivableIdsArr[i] + ":";
				
				var newDate = document.getElementById('paymentDate').value;
				var thisUrl = '/CBS/a/payment/' + customerId  +  '/' + ids + '/' + convertToMySQLDate(newDate) + '/remove';
				
				dojo.byId("receivableListTable").innerHTML = 
					"<img src='../../../../images/ajax-loader.gif' width='30' height='30' style='margin-left: 45%; margin-top: 15%;' />";
				dojo.xhrGet({
				      url: thisUrl,
				      load: function (data) { 
				    	  		dojo.byId("receivableListTable").innerHTML = data;
				    	  		totalSelectedAcctReceivableAmt = Math.round(($('#totalSelectedAcctReceivableAmt').val())*100)/100;
				      		},
				      error: function (data, ioArgs){
				    	  		dojo.byId("receivableListTable").innerHTML = "unknown error";
				      		}
				});
			}
		}
	}
	toBeRemovedReceivables = [];
}

function addToRecord() {
	if (toBeAddedRecord.length > 0) {
		currentReceivableIds = $("#hiddenCurrentIds").val();
		var arraySize2 = toBeAddedRecord.length;
		var ids = "";
		
		for (var i=0; i<arraySize2; i++) ids += toBeAddedRecord[i] + ":";

		var newDate = document.getElementById('paymentDate').value;
	 	var thisUrl = '/CBS/a/payment/' + customerId  +  '/' + currentReceivableIds + ids + '/' + 
	 	convertToMySQLDate(newDate) + '/changeCurrentBalance';
	 	
	 	document.getElementById("addReceivableTableDiv").style.display = 'none';
		dojo.byId("receivableListTable").innerHTML = 
			"<img src='../../../../images/ajax-loader.gif' width='30' height='30' style='margin-left: 45%; margin-top: 15%;' />";
		dojo.xhrGet({
		      url: thisUrl,
		      load: function (data) { 
		    	  		dojo.byId("receivableListTable").innerHTML = data;
		    	  		totalSelectedAcctReceivableAmt = Math.round(($('#totalSelectedAcctReceivableAmt').val())*100)/100;	
		    	  
		    	  		$("#date").val(newDate);
		    	  		$("#amount").val(totalSelectedAcctReceivableAmt);
		      		},
		      error: function (data, ioArgs){
		    	  		dojo.byId("receivableListTable").innerHTML = "unknown error";
		      		}
		});

	}
	toBeAddedRecord = [];
}

function savePayment () {
	var refId = document.getElementById("referenceId").value;
	var pAmount = document.getElementById("cash").value;
	
	if (refId == "") {
		$('#errorReferenceId').show();
		var errorRefId = document.getElementById("errorMessageRefId");
		
		errorRefId.style.display = "";
		errorRefId.innerHTML = "Payment reference id is required.";
	}  else {
		$('#errorReferenceId').hide();
		var errorRefId = document.getElementById("errorMessageRefId");
		errorRefId.style.display = "none";
	}
	
	
	if (pAmount == 0 || pAmount == "0.0" || pAmount == "0.00") {
		$('#errorAmount').show();
		var errorPAmount = document.getElementById("errorMessageAmount");
		
		errorPAmount.style.display = "";
		errorPAmount.innerHTML = "Amount should be greater than zero.";
	} else {
		$('#errorAmount').hide();
		var errorPAmount = document.getElementById("errorMessageAmount");
		errorPAmount.style.display = "none";
	}
	
	if (refId != "" && pAmount != 0 && pAmount != "" && pAmount != "0.0" && pAmount != "0.00")  {
		var thisUrl = '/CBS/a/payment/' + customerId  +  '/' + selectedReceivableIds + '/pay';
		var xhrArgs = {
			url: thisUrl,
			form : dojo.byId("payment"),
			handleAs : "text",
			load : function(data) {
				if (data == "saved") {
					$('#errorReferenceId').hide();
					$('#errorAmount').hide();
					document.getElementById("errorMessageRefId").style.display = "none";
					document.getElementById("errorMessageAmount").style.display = "none";
					window.opener.location.reload(true);
					printPayment();
				} else {
					dojo.byId("paymentForm").innerHTML = data;
					if (!document.getElementById('referenceId.errors')) $('#errorReferenceId').hide();
					if (!document.getElementById('amount.errors')) $('#errorAmount').hide();
					if (!document.getElementById('description.errors')) $('#errorDescription').hide();
					
					if ($("#paymentDate").val().length > 10) {
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
						$("#paymentDate").val(month + "/" + subString[2] + "/" + subString[5]);
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
}


function printPayment() {
	var pRefId = document.getElementById("referenceId").value;
	
	if (pRefId != "") {
		var date = document.getElementById("date").value;
		var cash = document.getElementById("cash").value;
		var change = document.getElementById("paymentChange").value;
		var url = '/CBS/a/payment/'  + customerId + '/' + selectedReceivableIds + '/' + convertToMySQLDate(date) + '/' + cash +
				'/' + change + '/1/print';
		window.open(url,'popup','width=1000,height=600,scrollbars=yes,resizable=yes,' +
		'toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0');
	}
}

function refreshCustomerAccount () {
	window.opener.location.reload(true);
	window.close();
}
</script>
</head>
<body>
<input type="hidden" id="customerId" value="${customerId}">
<div>
	<table id="nameAndDate">
		<tr>
			<td style="text-align: left;">
				Name: ${customerName}
			</td>
			<td style="text-align: right;">
				Date: 
				<img src="../../../../images/cal.gif" onclick="javascript:NewCssCal('paymentDate')" style="cursor:pointer" style="float: right;"/>
				<input type="text" id="paymentDate" size="17" onchange="changeCurrentBalance();" onblur="evalDate('paymentDate')" />
			</td>
		</tr>
	</table>
</div>
<div id="receivableListTable">
	<input type="hidden" id="selectedReceivableIds" value="${selectedReceivableIds}">
	<input type="hidden" id="allReceivableIds" value="${allReceivableIds}">
	<input type="hidden" id="remainingReceivableIds" value="${remainingReceivableIds}">
	<%@ include file="SelectedReceivableTable.jsp" %>
</div>
<div style="text-align: right; margin-top: 10px;">
	<input type="button" id="btnAddReceivable" value="Add Receivable"  onclick="addReceivables();"/>
	<input type="button" id="btnRemoveReceivable" value="Remove Receivable" onclick="removeReceivables();"/>
</div>
<div id="addReceivableTableDiv">
	<div id="addReceivableTable">
		<%@ include file="AddReceivableTable.jsp" %>
	</div>
	<div style="margin-top: 10px; text-align: right;" >
		<input type="button" id="btnAddToRecord" value="Add" onclick="addToRecord();" />
		<input type="button" id="btnCloseAddToRecord" value="Close" 
		onclick="document.getElementById('addReceivableTableDiv').style.display = 'none';" />
	</div>
</div>
<center>
<div id="frmPayment">
<form:form method="POST" commandName="payment" id="payment">
	<div style="width: 825px; margin-top: 10px;" class="modForm" >
		<center>
		<table id="paymentFormTable">
			<form:hidden path="id"/>
			<form:hidden path="customerId"/>
			<form:hidden path="date" />
			<form:hidden path="amount" />
			<form:hidden path="paymentChange"/>
			<tr>
				<td>Reference Id: </td>
				<td>
					<form:input id="referenceId" path="referenceId"/>
				</td>
			</tr>
			<tr id="errorReferenceId">
				<td colspan="2">
					<span style="color: red; font-size: 12px;" id="errorMessageRefId"> </span>
				</td>
			</tr>
			
			<tr>
				<td>Description: </td>
				<td>
					<form:input path="description" size="70"/>
				</td>
			</tr>
			<tr id="errorDescription">
				<td colspan="2">
					<font color="red">
						<form:errors path="description" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Amount: </td>
				<td>
					<form:input path="cash" onblur="setDefaultAmount(); checkAmount();"  />
				</td>
			</tr>
			<tr id="errorAmount">
				<td colspan="2">
					<span style="color: red; font-size: 12px;" id="errorMessageAmount"> </span>
				</td>
			</tr>
			<tr>
				<td>Change: </td>
				<td>
					<div id="changeDiv" style="text-align: right; max-width: 228px; " >
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="0.00" />
					</div>
				</td>
			</tr>
		</table>
		</center>
		<div id="hiddenCompanyId"></div>
	</div>
</form:form>
</div>
 </center>
<div>
	<input type="hidden" id="receivableCount" value="${receivableCount}" />
</div>
<div class="controls">
	<input type="submit" id="btnPay" name="btnPay" value="Pay" onclick="savePayment();" />	
	<input type="button" id="btnCancel" name="btnCancel" value="Close" onclick="window.close();" />	
</div>
</body>
</html>