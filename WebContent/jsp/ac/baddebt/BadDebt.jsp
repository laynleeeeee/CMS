<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="../css/stylesheet.css" />
<link rel="stylesheet" type="text/css" href="../css/tables.css" />
<script src="../js/dojo/dojo.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<title>Bad Debt</title>
<script type="text/javascript">
var selectedPage = 1;

function redirectToCustomerAccount(customerId) {
	window.location = contextPath + '/customer/' + customerId;
}

$(document).ready(function () {
	$("#printBadDebtBtn").live('click', function() {
		var url = 'badDebt/' + selectedPage + '/print';
		window.open(url,'popup','width=1000,height=600,scrollbars=no,resizable=yes,' +
				'toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0');
		
	});
	
	$("a#page-1").css('font-size', '16px');
	$("a#page-1").css('color', 'red');
});

function reloadTable(pageNumber) {
	var thisUrl = 'badDebt/' + pageNumber;
	
	$(".controls").hide();
	document.getElementById("badDebtTable").innerHTML = 
		"<img src='../images/ajax-loader.gif' width='30' height='30' style='margin-left: 25%; margin-top: 20%' />";
	dojo.xhrGet({
      url: thisUrl,
      load: function (data) {   
    	  		dojo.byId("badDebtTable").innerHTML = data;
    	  		$(".controls").show();
    	  		$('a#page-' + pageNumber).css('font-size', '16px');
    	  		$('a#page-' + pageNumber).css('color', 'red');
      		},
      error: function (data, ioArgs){
    	  		dojo.byId("badDebtTable").innerHTML = "unknown error";
      		}
	});
} 

function searchBadDebt(pageNumber) {
	selectedPage = pageNumber;
	var txtField = document.getElementById("txtSearchBadDebt");
	var textValue = txtField.value;
	var selectCategory = document.getElementById('searchCategory');
	
	var fieldLabel = selectCategory.value;
	
	if (textValue == "") textValue = "*";
	
	var thisUrl = 'badDebt/search/'+ textValue +'/'+fieldLabel+'/'+pageNumber;

	dojo.byId("badDebtTable").innerHTML = "searching...";
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {  
	    	  		dojo.byId("badDebtTable").innerHTML = data;
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("badDebtTable").innerHTML = "unknown error";
	      		}
	});
}

function goToPage (pageNumber) {
	selectedPage = pageNumber;
	reloadTable(selectedPage);
}
</script>
</head>
<body>
<div id="searchControl" style="margin-left: 4%">
<table style="width: 700px;">
	<tr>
		<td style="width: 40px;">Name: </td> 
		<td><input type="text" id="txtSearchBadDebt" size="60" /> </td>
		<td>
			<select id="searchCategory">
				<c:forEach items="${searchCategory}" var="category">
					<option value="${category}" ${category == 'Active' ? 'selected' : ''}>${category}</option>
				</c:forEach>
			</select>
		</td>
		<td><input type="button" id="btnSearch" value="Search" onclick="searchBadDebt(1)"/></td>
	</tr>
</table>
</div>
<div id="badDebtTable">
	<%@ include file="BadDebtTable.jsp" %>
</div>
<div id="customerAccount"></div>
<div class="controls" style="text-align: right;  margin-top: 10px; margin-right: 30px;">
	<input type="button" id="printBadDebtBtn" value="Print" />
</div>
<div id="customerAccount"></div>
</body>
</html>