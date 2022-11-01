
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../include.jsp" %>
<!-- 

	Description: Admin main page. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin Modules</title>
<style type="text/css">
a, a:hover, a:visited{ 
	text-decoration: none; 
	color: #000; 
}

.diffColor {
	background-color: #FCFCFC;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	var count = parseInt("${fn:length(adminModules)}");
	if (count >= 1) {
		var firstRowTd = $("#mainMenu tbody tr:first td");
		$(firstRowTd).css("background-color", "#EDEDED");
		var firstLink = $(firstRowTd).find("input[type=hidden]").val();
		$("#divMenu").load(contextPath + "/" + firstLink);
	}
});

function loadMenuPage(uri, tr) {
	var td = $(tr).children("td");
	$(td).addClass("selected");
	$("#mainMenu tbody tr td").each(function () {
		if ($(this).hasClass("selected"))
			$(this).css("background-color", "#EDEDED");
		else
			$(this).css("background-color", "#FAFAFA");
	});
	$("#divMenu").load(contextPath + "/" + uri, function() {
		$(td).removeClass("selected");
	});
}
</script>
</head>
<body>
<table width="100%" border=0 style="margin-left: 5%;">
	<tr>
		<td>
			<table id="mainMenu" border=0 style="margin-left: 5%;">
				<tbody>
					<c:forEach var="module" items="${adminModules}" varStatus="status">
						<c:if test="${module.productCode != 20023 && module.productCode != 20024}">
							<c:set var="index" value="${status.index + 1}" />
							<c:set var="uri" value="${module.uri}" />
							<tr onclick="loadMenuPage('${uri}', this);" id="tr${index}">
								<td><input type="hidden" value="${uri}" /> <a
									id="link${index}"> ${module.title} </a></td>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
		</td>
	</tr>
</table>
</body>
</html>