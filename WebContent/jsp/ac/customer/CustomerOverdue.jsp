<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Bad Debt</title>
<script type="text/javascript">
var selectedPage = 1;

function redirectToCustomerAccount(customerId) {
	window.location = contextPath + '/customer/' + customerId;
}

$(document).ready(function () {
	$("#printOverdueBtn").live('click', function() {
		var url = 'overdue/' + selectedPage + '/print';
		window.open(url,'popup','width=1000,height=600,scrollbars=no,resizable=yes,' +
				'toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0');
		
	});
	
	$("a#page-1").css('font-size', '16px');
	$("a#page-1").css('color', 'red');
});

function reloadTable(pageNumber) {
	var thisUrl = 'overdue/' + pageNumber;
	
	$(".controls").hide();
	document.getElementById("customerOverDueTable").innerHTML = 
		"<img src='../images/ajax-loader.gif' width='30' height='30' style='margin-left: 30%; margin-top: 20%' />";
	dojo.xhrGet({
      url: thisUrl,
      load: function (data) {   
    	  		dojo.byId("customerOverDueTable").innerHTML = data;
    	  		$(".controls").show();
    	  		$('a#page-' + pageNumber).css('font-size', '16px');
    	  		$('a#page-' + pageNumber).css('color', 'red');
      		},
      error: function (data, ioArgs){
    	  		dojo.byId("customerOverDueTable").innerHTML = "unknown error";
      		}
	});
} 

function searchCustomerOverdue(pageNumber) {
	selectedPage = pageNumber;
	var txtField = document.getElementById("txtSearchCustomerOverdue");
	var textValue = txtField.value;
	var selectCategory = document.getElementById('searchCategory');
	
	var fieldLabel = selectCategory.value;
	
	if (textValue == "") textValue = "*";
	
	var thisUrl = 'overdue/search/'+ textValue +'/'+fieldLabel+'/'+pageNumber;
	
	dojo.byId("customerOverDueTable").innerHTML = "searching...";
	dojo.xhrGet({
	      url: thisUrl,
	      load: function (data) {  
	    	  		dojo.byId("customerOverDueTable").innerHTML = data;
	      		},
	      error: function (data, ioArgs){
	    	  		dojo.byId("customerOverDueTable").innerHTML = "unknown error";
	      		}
	});
}

function goToPage (pageNumber) {
	selectedPage = pageNumber;
	reloadTable(selectedPage);
}
</script>
<title>Customers with overdue</title>
</head>
<body>
<div id="searchControl" style="margin-left: 4%">
<table style="width: 700px;">
	<tr>
		<td style="width: 40px;">Name: </td> 
		<td><input type="text" id="txtSearchCustomerOverdue" size="60" /> </td>
		<td>
			<select id="searchCategory">
				<c:forEach items="${searchCategory}" var="category">
					<option value="${category}" ${category == 'Active' ? 'selected' : ''}>${category}</option>
				</c:forEach>
			</select>
		</td>
		<td><input type="button" id="btnSearch" value="Search" onclick="searchCustomerOverdue(1)"/></td>
	</tr>
</table>
</div>
<div id="customerOverDueTable">
	<%@ include file="CustomerOverdueTable.jsp" %>
</div>
<div class="controls" style="text-align: right; margin-top: 10px; margin-right: 30px;">
	<input type="button" id="printOverdueBtn" value="Print" />
</div>
<div id="customerAccount"></div>
</body>
</html>