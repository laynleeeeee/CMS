<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include.jsp" %>
  <!--

	Description: The entry point of reports.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery/sdd/sh/shCore.css" />
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery/sdd/sh/shThemeDefault.css" />
<script type="text/javascript">
$(document).ready(function() {
	$("#reports").removeClass("active");
	$("#value").html($("#reports :selected").text() + " (VALUE: " + $("#reports").val() + ")");
	$("select").change(function(){
		$("#value").html(this.options[this.selectedIndex].text + " (VALUE: " + this.value + ")");
		$("#report").text("");
		if($.trim($("#report").text()) == ""){
			$("#calBorder").css({ "visibility": 'hidden'});
		}
	});
	
	$("#selectBtn").click(function() {
		var selectedUri = $("#reports").val();
		var uri = contextPath + "/"+selectedUri;
		$("#report").load(uri);
	});

	//Changes in UI if browser is chrome
	$.browser.chrome = /chrome/.test(navigator.userAgent.toLowerCase());
	if($.browser.chrome) {
		$(".searchForm").css("background-color", "white");
		$(".mainArea").css("background-color", "white");
		$(".mainArea").css("float", "none");
	}
});
</script>
<title>Reports</title>
</head>
<body>
<div class="container">
	<div class="searchForm">
		<table align="center">
			<tr>
				<td class="formTitle">Report Name</td>
				<td>
				<select id="reports" class="selectClass">
					<c:forEach var="report" items="${reports}">
						<option value="${report.uri}">
							${report.title}
						</option>
					</c:forEach>	
				</select>
				</td>
				<td><input type="button" value="Select" id="selectBtn"></td>
			</tr>
		</table>
		<hr class="thin"/>
	</div>
	<br>
	<div id="report" class="mainArea"> </div>
</div>
</body>
</html>