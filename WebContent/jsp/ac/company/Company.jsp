<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!-- 

	Description: JSP file for the listing of different companies. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Company</title>
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditCompany").attr("disabled", "disabled");
});

$(function(){
	$("input#btnAddCompany").click(function(){
		disableTable(true);
		$("#companyForm").load("company/add");	
		$("#companyForm").scrollTop();
	});

	$("input#btnEditCompany").click(function(){
		disableTable(true);
		if (selectedCompanyId != "") {
			$("#companyForm").load("company/edit/" + selectedCompanyId);	
			$("#companyForm").scrollTop();
		}
	});
	
	$("input#btnPrintCompany").click(function(){
			var url = 'company/' + selectedPage + '/print';	
			window.open(url,'popup','width=1000,height=600,scrollbars=no,resizable=yes,' +
					'toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0');
	});
	
	$("input#btnSearchCompany").click(function() {
		searchCompany(1);
	});
});

var selectedCompanyId = '';
var checkBoxes = [];
var selectedPage = 1;

function selectCompany(cbId) {
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
		selectedCompanyId = checkBoxes[0];
		document.getElementById('btnEditCompany').disabled = false;
	} else {
		selectedCompanyId = '';
		document.getElementById('btnEditCompany').disabled = true;
	}
}

function updateVeilHeight() {
	 var disabler = document.getElementById("veil");
	 var tableWrapperHeight = document.getElementById("companyTable").offsetHeight - 10;
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

function saveCompany () {
	var xhrArgs = {
		form : dojo.byId("company"),
		handleAs : "text",
		load : function(data) {
			if (data == "saved") {
				dojo.byId('btnEditCompany').disabled = true;
				dojo.byId("companyForm").innerHTML = "";	
				checkBoxes = [];
				selectedCompanyId = '';
				searchCompany (selectedPage);
				selectedPage = 1;
				disableTable(false);
			} else {
				dojo.byId("companyForm").innerHTML = data;
				if (!document.getElementById('name.errors')) $('#errorName').hide();
				if (!document.getElementById('address.errors')) $('#errorAddress').hide();
				if (!document.getElementById('contactNumber.errors')) $('#errorContactNumber').hide();
				if (!document.getElementById('emailAddress.errors')) $('#errorEmailAddress').hide();
				if (!document.getElementById('active.errors')) $('#errorActive').hide();
			}
		},
		error : function(error) {
			dojo.byId("ajaxBody").innerHTML = "Error occured while saving.";
		}
	};
	// Call the asynchronous xhrPost
	dojo.xhrPost(xhrArgs);
}

function searchCompany (pageNumber) {
	var searchText = document.getElementById("searchCompanyText").value;
	var searchCategory = document.getElementById("searchCompanyOptionCategory").value;
	var searchStatus =  document.getElementById("searchCompanyOptionStatus").value;
	$("#companyTable").load(contextPath + "/company?searchText="+searchText+"&category="+searchCategory+"&status="+
			searchStatus+"&page=" + pageNumber);
}


function resetPage () {
 	dojo.byId("companyForm").innerHTML = "";	
 	checkBoxes = [];
 	if (selectedCompanyId != '') document.getElementById(selectedCompanyId).checked = false;
 	selectedCompanyId = '';
 	document.getElementById('btnEditCompany').disabled = true;
 	disableTable(false);	
}

function showForm (thisUrl) {
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {
	    	  		dojo.byId("companyForm").innerHTML = data;
	    	  		dojo.byId("companyForm").scrollIntoView();
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("companyForm").innerHTML = "unknown error";
	      		}
	});
}
</script>
</head>
<body>
	<table >
		<tr>
			<td style="width: 100px;"></td>
			<td>
				<div style="width: 100%; ">
					<form:select path="searchCompanyOptionCategory" id="searchCompanyOptionCategory">
						<form:options items="${searchCompanyOptionCategory}" />
					</form:select>
					<input type="text" id="searchCompanyText" name="searchCompanyText" size="40%"/>
					<form:select path="searchCompanyOptionStatus" id="searchCompanyOptionStatus">
						<form:options items="${searchCompanyOptionStatus}" />
					</form:select>
					<input type="button" id="btnSearchCompany" value="Search"  />	
				</div>
			</td>
		</tr>
	</table>
	<div id="veil"></div>
	<div id="companyTable">
		<%@ include file="CompanyTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddCompany" name="btnAddCompany" value="Add" />
		<input type="button" id="btnEditCompany" name="btnEditCompany" value="Edit" />
		<input type="button" id="btnPrintCompany" name="btnPrintCompany" value="Print" />
	</div>
	<br /> <br />
	<div id="companyForm">
	</div>
</body>
</html>