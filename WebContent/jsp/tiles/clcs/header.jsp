<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${pageContext.request.contextPath}/css/clcsBaseLayout.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/css/status.css" rel="stylesheet" type="text/css"/>
<script src="${pageContext.request.contextPath}/js/jquery/jquery.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/dojo/dojo.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/ajaxUtil.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/js/accounting.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/searchUtil.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/js/jquery/jquery.lightbox_me.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/accounting.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/jquery/jquery-ui-1.10.3.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/css/jquery-ui-1.10.3.css" rel="stylesheet" type="text/css"  />
<script src="${pageContext.request.contextPath}/js/formatUtil.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/js/inputUtil.js" type="text/javascript" ></script>

<script type="text/javascript">
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

function redirectToPage (page) {
	window.location.replace(contextPath +"/"+ page);
}

function logout () {
	window.location.replace(contextPath + "/logout");
}
</script>
</head>
<body>
<div id="top" class="gradientBackground">
	<div id="menu-wrapper">
		<div id="top-menu">
			<img alt="" src="${pageContext.request.contextPath}/images/velox_logo.png">
		</div>
		<div id="rightSide">
				<a id="user" href="#" onclick="logout()"
					title="Log-off user">Logout
				</a>
			<c:if test="${user.admin == true}">
			<a id="settings" href="#" onclick="redirectToPage('admin/accountTypes')"
				title="Admin Settings">Settings</a>
			</c:if>
		</div>
		<div style="float: right; padding: 10px; color: white; font-style: italic;" >
			${user.firstName} ${user.lastName}
		</div>
	</div>
	<div id="spinner" class="spinner" style="display:none;">
	    <img id="img-spinner" src="${pageContext.request.contextPath}/images/ajax-loader.gif" alt="Loading"/>
	</div>
</div>
</body>
</html>