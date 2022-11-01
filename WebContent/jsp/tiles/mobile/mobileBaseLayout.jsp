<%@ include file="../../include.jsp" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta content="width=device-width, initial-scale=1, maximum-scale=1" name="viewport">
		<title><tiles:insertAttribute name="title" ignore="true" /></title>
	<link href="${pageContext.request.contextPath}/css/mobileStyleSheet.css" rel="stylesheet" type="text/css"/>
</head>
<body class="ui-max-width-height">
<div id="wrapper">
	<div id="header-wrap">
		<tiles:insertAttribute name="header" />
	</div>
	<div id="content-wrap">
		<tiles:insertAttribute name="body" />
	</div>
	<div id="footer-wrap">
		<tiles:insertAttribute name="footer" />
	</div>
</div>
</body>
</html>