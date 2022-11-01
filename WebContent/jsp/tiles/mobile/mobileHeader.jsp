<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp" %>
  <!--

	Description: Mobile header
 -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
		<script src="${pageContext.request.contextPath}/js/jquery/jquery.js" type="text/javascript"></script>
<style type="text/css">
.appr-btn {
	background: url('../../images/app.png') center center no-repeat;
}
.rpt-btn {
	background: url('../../images/rpt.png') center center no-repeat;
}
.log-btn {
	background: url('../../images/logout.png') center center no-repeat;
}
.approval-selected {
	font-weight: bold;
	background: url('../../images/app_active.png') center center no-repeat;
}
.report-selected {
	font-weight: bold;
	background: url('../../images/rpt_active.png') center center no-repeat;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	redirectToPage ('workflows');
});

function redirectToPage (page) {
	if(page.indexOf('workflows') >= 0) {
		addClass("a#approvalBtn","approval-selected");
		removeClass("a#reportBtn","report-selected");
	}else{
		addClass("a#reportBtn","report-selected");
		removeClass("a#approvalBtn","approval-selected");
	}
// 	window.location.replace(contextPath +"/"+ page);
}

function logout () {
	window.location.replace(contextPath + "/logout");
}
</script>
</head>
<body class="ui-max-width-height">
<header id="header-content">
	<div id="header-top-content" class="v-align-top">
	 	<a href="#" id="approvalBtn" class="appr-btn ui-btn-aleft" onclick="redirectToPage('workflows')"></a>
	 	<a href="#" id="reportBtn" class="rpt-btn ui-btn-aleft" onclick="redirectToPage('reports')"></a>
	 	<a href="#" class="log-btn ui-btn-aright" onclick="logout()"></a>
	</div>
	<div id="header-bottom-content">
		*${user.firstName} ${user.lastName}  *${user.position.name}  *${serverDate}
	</div>
</header>
</body>
</html>