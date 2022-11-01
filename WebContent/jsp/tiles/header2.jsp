<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../include.jsp" %>
  <!--

	Description: New header
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="${pageContext.request.contextPath}/css/header22.css" rel="stylesheet" type="text/css"  />
<link href="${pageContext.request.contextPath}/css/header.css" rel="stylesheet" type="text/css"  />
<link href="${pageContext.request.contextPath}/css/stylesheet.css" rel="stylesheet" type="text/css"  />
<link href="${pageContext.request.contextPath}/css/status.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/css/input.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/js/jquery/jquery.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/dojo/dojo.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/ajaxUtil.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/js/searchUtil.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/js/jquery/jquery.lightbox_me.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/accounting.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/jquery/jquery-ui-1.10.3.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/css/jquery-ui-1.10.3.css" rel="stylesheet" type="text/css"  />
<script src="${pageContext.request.contextPath}/js/formatUtil.js" type="text/javascript" ></script>
<%-- <link href="${pageContext.request.contextPath}/css/tabs.css" rel="stylesheet" type="text/css"  /> --%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">

var hasAdminAccess = false;
var isAdmin = ${user.admin == true};

accounting.settings = {
	currency: {
		symbol : "",   // default currency symbol is ''
		format: "%s%v", // controls output: %s = symbol, %v = value/number (can be object: see below)
		decimal : ".",  // decimal point separator
		thousand: ",",  // thousands separator
		precision : 2   // decimal places
	},
	number: {
		precision : 0,  // default precision on numbers is 0
		thousand: ",",
		decimal : "."
	}
};
	
$(document).ready(function() {
	$("#spinner").bind("ajaxSend", function() {
		$(this).show();
	}).bind("ajaxStop", function() {
		$(this).hide();
	}).bind("ajaxError", function() {
		$(this).hide();
	});

	$("#mSearch").click(function(){
		$("#mSearch").val("");
	});

	$("#mSearch").bind("keypress", function (e) {
		if (e.which == 13) {
			window.location.replace(contextPath +"/search?criteria="+encodeURIComponent($("#mSearch").val()));
			e.preventDefault();
		}
	});

	// 	highlighted selected menu.
	var arrCurUrl = document.URL.split("/");
	var strCurUrl = arrCurUrl[arrCurUrl.length-1];
	switch (strCurUrl) {
		case 'fleetProfile':
			$("#mFleetProfile").addClass("active");
			$("#spFPId").addClass("active");
		break;
		case 'employeeProfile':
			$("#mEmployeeProfile").addClass("active");
			$("#spEmployeeId").addClass("active");
		break;
		case 'forms':
			$("#mHome").addClass("active");
			$("#spFormId").addClass("active");
			break;
		case 'student':
			$("#mStudentId").addClass("active");
			$("#spStudentId").addClass("active");
			break;
		case 'workflows':
			$("#mWorkflowsId").addClass("active");
			$("#spApprovalId").addClass("active");
			break;
		case 'reports':
			$("#mReport").addClass("active");
			$("#spReportId").addClass("active");
			break;
		case 'adminModules':
			$("#settings").addClass("active");
			$("#spSettingsId").addClass("active");
			break;
		default:
			console.log("No menu selected!");
			break;
	}

	var userGroupId = parseInt("${user.userGroupId}");
	$.ajax({
		url: contextPath + "/admin/userGroupAccessRight/hasAdminAccess?userGroupId="+userGroupId,
		success : function(result) {
			hasAdminAccess = result == "true";
		},
		complete : function() {
			if (isAdmin || hasAdminAccess) {
				$("#settings").show();
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "text"
	});

	var hasEeProfileAccess = true;
	$.ajax({
		url: contextPath + "/employeeProfile/hasAccessRight?userGroupId="+userGroupId,
		success : function(result) {
			hasEeProfileAccess = result == "true";
		},
		complete : function() {
			if (hasEeProfileAccess) {
				$("#mEmployeeProfile").show();
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "text"
	});

	var hasFleetProfileAccess = true;
	$.ajax({
		url: contextPath + "/fleetProfile/hasAccessRight?userGroupId="+userGroupId,
		success : function(result) {
			hasFleetProfileAccess = result == "true";
		},
		complete : function() {
			if (hasFleetProfileAccess) {
				$("#mFleetProfile").show();
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "text"
	});
});

function redirectToPage (page) {
	window.location.replace(contextPath +"/"+ page);
}

function logout () {
	window.location.replace(contextPath + "/logout");
}

</script>
<style type="text/css">
.active { background-color: graytext; text-decoration: underline;}
</style>
<title>Header</title>
</head>
<body>
<div id="top">
	<div id="menu-wrapper">
		<div id="top-menu" class="grid-container-left">
			<div class="grid-item">
				<a id="mFleetProfile" class="menuLeft" href="#" onclick="redirectToPage('fleetProfile')"
					title="Fleet Profile" style="display: none; white-space: nowrap;">
					<span id = "spFPId">Fleet Profile</span>
				</a>
			</div>
			<div class="grid-item">
				<a id="mEmployeeProfile" class="menuLeft" href="#" onclick="redirectToPage('employeeProfile')"
					title="Employee" style="display: none">
					<span id = "spEmployeeId">Employee</span>
				</a>
			</div>
			<div class="grid-item">
				<a id="mHome" href="#" onclick="redirectToPage('forms')"
					title="Add transactions">
					<span id = "spFormId">Forms</span>
				</a>
			</div>
			<div class="grid-item">
				<a class="menuLeft" id = "mWorkflowsId" href="#" onclick="redirectToPage('workflows')"
					title="Approval">
					<span id = "spApprovalId">Approval</span>
				</a>
			</div>
			<div class="grid-item">
				<a id="mReport" href="#" onclick="redirectToPage('reports')"
					title="Transactions Report">
					<span id = "spReportId">Report</span>
				</a>
			</div>
			<!-- <a id="mApprove" href="#" onclick="redirectToPage('approvals')"
				title="Approve transactions">
				<span>Approval</span>
			</a> -->
		</div>
		<div>
			<input type="text" id="mSearch" value="Search..."/>
		</div>
		<div id="rightSide" class="grid-container-right">
			<div style="float: right; white-space:nowrap; padding: 10px; color: white; font-style: italic;" >
				${user.firstName} ${user.lastName}
			</div>
			<div>
				<a id="settings" href="#" onclick="redirectToPage('adminModules')"
					title="Admin Settings" style="display: none">
					<span id = "spSettingsId">Settings</span>
				</a>
			</div>
			<div>
				<a id="user" href="#" onclick="logout()"
					title="Log-off user">Logout
				</a>
			</div>
		</div>
	</div>
	<div id="spinner" class="spinner" style="display:none;">
	    <img id="img-spinner" src="${pageContext.request.contextPath}/images/ajax-loader.gif" alt="Loading"/>
	</div>
</div>
</body>
</html>