<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="../css/stylesheet.css" />
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<script src="../js/dojo/dojo.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/javascripts.js"></script>
<script type="text/javascript">
	function loadDefault() {
		$("#adminUsers").html("<h2 style='color: #000'>Loading... Please wait</h2>");
		$("#adminUsers").load('user');
		$("#adminCompanies").html("");
		$("#adminUserGroups").html("");
		$("#adminCustomers").html("");
		$("#tabUsers").css('opacity', '1.0');
		$("#ulUsers").css('opacity', '1.0');
		$("#tabCompanies").css('opacity', '0.4');
		$("#ulCompanies").css('opacity', '0.4');
		$("#tabUserGroups").css('opacity', '0.4');
		$("#ulUserGroups").css('opacity', '0.4');
	}
	
	$(document).ready(function() {
		loadDefault();
	});
	
	$(function(){
	  $("div#tabUsers").click(function(){
		  loadDefault();
	  });
	});

	$(function(){
	  $("div#tabCompanies").click(function(){
		  	$("#adminCompanies").html("<h2 style='color: #000'>Loading... Please wait</h2>");
			$("#adminCompanies").load('company');
			$("#adminUsers").html("");
			$("#adminUserGroups").html("");
			$("#adminCustomers").html("");
			$("#tabCompanies").css('opacity', '1.0');
			$("#ulCompanies").css('opacity', '1.0');
			$("#tabUsers").css('opacity', '0.4');
			$("#ulUsers").css('opacity', '0.4');
			$("#tabUserGroups").css('opacity', '0.4');
			$("#ulUserGroups").css('opacity', '0.4');
	  });
	});

	$(function(){
	  $("div#tabUserGroups").click(function(){
		  	$("#adminUserGroups").html("<h2 style='color: #000'>Loading... Please wait</h2>");
			$("#adminUserGroups").load('userGroup');
			$("#adminCompanies").html("");
			$("#adminUsers").html("");
			$("#adminCustomers").html("");
			$("#tabUserGroups").css('opacity', '1.0');
			$("#ulUserGroups").css('opacity', '1.0');
			$("#tabCompanies").css('opacity', '0.4');
			$("#ulCompanies").css('opacity', '0.4');
			$("#tabUsers").css('opacity', '0.4');
			$("#ulUsers").css('opacity', '0.4');
	  });
	});
</script>
<title>Administrator</title>
</head>
<body>
<div>
	<div id="tabUsers" class="tabLabel">Users</div>
	<div id="ulUsers" class="tabUnderline"></div>
	<div id="adminUsers" class="tabContent"> </div>
	<div id="tabCompanies" class="tabLabel">Companies</div>
	<div id="ulCompanies" class="tabUnderline"></div>
	<div id="adminCompanies" class="tabContent"> </div>
	<div id="tabUserGroups" class="tabLabel">User Groups</div>
	<div id="ulUserGroups" class="tabUnderline"></div>
	<div id="adminUserGroups" class="tabContent"> </div>
</div>
</body>
</html>