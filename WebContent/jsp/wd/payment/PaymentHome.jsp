<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- 

       Description: Home page of concessionaire payment
 -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="${pageContext.request.contextPath}/js/jquery/jquery1.7.2min.js"
	type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/jquery/sdd/jquery.searchabledropdown-1.0.7.min.js"
	type="text/javascript"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/sdd/sh/shCore.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/sdd/sh/shBrushJScript.js"></script>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery/sdd/sh/shCore.css" />
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery/sdd/sh/shThemeDefault.css" />
<script type="text/javascript">
SyntaxHighlighter.all();

$(document).ready(function() {
	$("select").searchable();
	$("#value").html($("#concessionaires :selected").text() + " (VALUE: " + $("#concessionaires").val() + ")");
	$("select").change(function(){
		$("#value").html(this.options[this.selectedIndex].text + " (VALUE: " + this.value + ")");
	});
	$("#selectBtn").click(function() {
		var id = $("#concessionaires").val();
		var uri = contextPath + "/concessionare/payment/"+id;
		$("#concessionareForm").load(uri);
	});
});
</script>
<style type="text/css">
	#concessionaires {
			border: 2px solid black;
			padding: 4px;
			font-weight: bold;
			text-transform: uppercase;
			background-color: #CCC;
		}
</style>
<title>Concessionaire payment home page</title>
</head>
<body>
	<table>
		<tr>
			<td>Concessionaire</td>
			<td>
				<select id="concessionaires">
					<c:forEach var="concessionaire" items="${concessionaires}">
						<option value="${concessionaire.id}">
							${concessionaire.firstName} ${concessionaire.lastName}
						</option>
					</c:forEach>	
				</select>
			</td>
			<td><input type="button" value="Select" id="selectBtn"></td>
		</tr>
	</table>
	<div id="concessionareForm" style="margin-top: 20px;">
		
	</div>
</body>
</html>