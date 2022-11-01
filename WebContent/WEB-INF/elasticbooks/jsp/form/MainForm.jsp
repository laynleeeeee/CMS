<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include.jsp" %>
  <!--

	Description: The entry point of forms.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery/sdd/sh/shCore.css" />
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery/sdd/sh/shThemeDefault.css" />
<style type="text/css">
.imgPrint: hover {
	cursor: pointer;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	$("#forms").removeClass("active");
	$("#value").html($("#forms :selected").text() + " (VALUE: " + $("#forms").val() + ")");
	$("select").change(function(){
		$("#value").html(this.options[this.selectedIndex].text + " (VALUE: " + this.value + ")");
		$("#form").text("");
		if($.trim($("#form").text()) === ""){
			$("#calBorder").css({ "visibility": 'hidden'});
		}
	});

	$("#selectBtn").click(function() {
		var selectedUri = $("#forms").val();
		var uri = contextPath + "/"+selectedUri;
		$("#form").load(uri);
	});
});

function updateTable(formStatus) {
	var objectId = formStatus.objectId;
	if (objectId != null) {
		$.ajax({
			url: contextPath + "/oinfo?objectId="+objectId,
			success: function(responseText){
				var objectInfo = JSON.parse(responseText);
				var latestStatus = objectInfo.latestStatus;
				var popupLink = contextPath+objectInfo.popupLink;
				var printOutLink = contextPath + objectInfo.printOutLink;
				var message = "<img class='imgPrint' src='../CMS/images/ic_print.png' height='20' onclick='window.open(\""+printOutLink+"\")' width='20'style='cursor: pointer; vertical-align: middle; padding-bottom: 5px;'>"
					+ "<a href='#' onClick='showEditForm(\""+popupLink+"\")'>"+objectInfo.title +"</a> was successfully " + latestStatus +".";
				$("#tableStatusId tr:first").after("<tr><td class='recSMS'>"+message +"</td></tr><tr>");
				var popUpUri = popupLink.substr(0, popupLink.indexOf("?"));
				$("#editForm").trigger('close');
				reloadForm(popUpUri);
			}
		})
	} else {
		$("#tableStatusId tr:first").after("<tr><td class='recTitle'>"+formStatus.title +"</td></tr><tr><td class='recSMS'>"+formStatus.message+"</td></tr><tr><td></td></tr>");	
		var selectedUri = $("#forms").val();
		var uri = contextPath + "/"+selectedUri;
		$("#form").load(uri);
	}
	
}

function showEditForm (path) {
	$("#form").html("");
	$("#editForm").load(path, function (data) {
		$("#editForm").lightbox_me({
			closeSelector: "#btnClose",
			centered: true,
			onClose: function() {
				var popUpUri = path.substr(0, path.indexOf("?"));
				$("#editForm").html("");
				reloadForm(popUpUri);
			}
		});
		//Set background color of the popup
		$("#editForm").css("background-color", "#FFF");
		updatePopupCss();
		//For initial loading of pop-up form.
		$("#btnClose").css("cursor","pointer");
		$("#btnClose").css("float","right");
	});
}

function reloadForm(editedFormURI) {
	var selectedFormURI = contextPath + "/" + $("#forms").val();
	if(selectedFormURI == editedFormURI) {
		$("#form").load(selectedFormURI);
	}
}

</script>
<title>Form</title>
</head>
<body>
<div id="editForm" class="formFloat"></div>
<div class="container">
	<div class="searchForm">
		<table align="center">
			<tr>
				<td class="formTitle">Form/Transaction</td>
				<td>
					<select id="forms" class="selectClass">
						<c:forEach var="form" items="${forms}">
							<option value="${form.uri}">
								${form.title}
							</option>
						</c:forEach>	
					</select>
				</td>
				<td><input type="button" value="Select" id="selectBtn"></td>
			</tr>
		</table>
		<hr class="thin"/>
	</div>
	<div class="recentAreaHeader">Recently Added
		<hr class="thin2"/></div>
	<div class="mainArea">
		<div id="form" ></div>
	</div>
	<div class="recentArea">
		<table id="tableStatusId">
			<thead>
				<tr>
					<td></td>
				</tr>
			</thead>
		</table>
	</div>
</div>
</body>
</html>