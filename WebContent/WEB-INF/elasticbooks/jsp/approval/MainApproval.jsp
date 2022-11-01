<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include.jsp" %>
 <!--

	Description: The entry point of approval
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebApproval.css" media="all">
<script src="${pageContext.request.contextPath}/js/commonUtil.js"
	type="text/javascript"></script>
<script type="text/javascript">
var formApprovals = new Array ();
var formStatusesParam = "?statuses=1";
var selectedFormType = 0;
$(document).ready(function() {
	var tabHeader = "";
	<c:forEach var="module" items="${approval}">
		var name="${module.name}";
		var title="${module.title}";
		var uri="${module.uri}";
		var editUri="${module.edit.uri}";
		var module = new FormApproval (name, title, uri, editUri);
		formApprovals.push(module);
		tabHeader += "<a id='"+(formApprovals.length - 1)+"tabMenu' class='tabMenu' onClick=\"retrieveIndivForms("+(formApprovals.length - 1)+",'')\">"+title+"</a> ";
	</c:forEach>
	$("#tabHeader").prepend (tabHeader);
	if (formApprovals.length > 0) {
		retrieveIndivForms (0, '');
	}

	$("#searchTxt").bind("keypress", function (e) {
		if (e.which == 13) {
			retrieveIndivForms (selectedFormType, processSearchName($(this).val()));
		}
	});
});

function FormApproval (name, title, uri, editUri) {
	this.name = name;
	this.title = title;
	this.uri = uri;
	this.editUri = editUri;
}

function retrieveIndivForms (formApprovalId, searchCriteria) {
	var formApproval = formApprovals[formApprovalId];
	selectedFormType = formApprovalId;
	$("#form").empty();
	$.ajax({
		url: contextPath + "/" + formApproval.uri + formStatusesParam + "&criteria="+searchCriteria, 
		success: function(responseText){
			var mainDiv = "";
			var table = "<table > <tbody>";
			var data = responseText[0].data; // Only the first result of the data.
			for (var index = 0; index < data.length; index++){
				var object =  data[index];
				var name = object["lastUpdatedBy"];
				var desc = object["shortDesc"];
				var id = object["id"];
				table += "<tr onclick='showForm("+id+", \""+formApproval.editUri+"\")' class='lineResult'>"+
						"<td width='70%'>"+concatString(desc,100)+"</td>"+ 
						"<td width='7%'>"+object.status+"</td>"+
						"<td width='15%'>"+name+"</td>"+
						"<td width='8%'>"+object.lastUpdatedDate+"</td>"+
						"</tr>";
			}
			table += "</tbody></table>";
			mainDiv += table + "</div>";
			$("#form").append (mainDiv);
			for (var i =0; i < formApprovals.length; i++) {
				if (i != formApprovalId) {
					$("#"+i+"tabMenu").attr('class', 'tabMenu');
				}
			}
			$("#"+formApprovalId+"tabMenu").attr('class', 'tabSelectedMenu');
			
		}, 
		dataType: "json"
	});
}

function retrieveApprovedForms () {
	formStatusesParam = "?statuses=2";
	retrieveIndivForms (0, processSearchName($("#searchTxt").val()));
}
function retrieveCancelledForms () {
	formStatusesParam = "?statuses=3";
	retrieveIndivForms (0, processSearchName($("#searchTxt").val()));
}

function retrieveNewFroms () {
	formStatusesParam = "?statuses=1";
	retrieveIndivForms (0, processSearchName($("#searchTxt").val()));
}
function showForm (pId, uri) {
	$("#form").load (contextPath+ "/"+uri + "?pId="+pId+"&isEdit=false");
}

function updateTable (formStatus) {
	$("#tableStatusId tr:first").after("<tr><td class='recTitle'>"+formStatus.title +"</td></tr><tr><td class='recSMS'>"+formStatus.message+"</td></tr><tr><td></td></tr>");
	retrieveIndivForms(selectedFormType, "");
}
function updateTabMenuCss (tabMenu) {
	$(".appMenuSelected").attr('class', 'appMenu');
	$(tabMenu).attr('class', 'appMenuSelected');
	
}
</script>
<title>Form Approval</title>
</head>
<body>
<div id="container">
<table height="100%">
	<tr>
		<td class="menuTD">
			<table class="appMainMenu">
				<tr>
					<td class="appMenuSelected" onclick="retrieveNewFroms ();updateTabMenuCss (this);"><a >Forms</a></td>
				</tr>
				<tr>
					<td class="appMenu" onclick="retrieveApprovedForms ();updateTabMenuCss (this);"><a >Approved</a></td>
				</tr>
				<tr>
					<td class="appMenu" onclick="retrieveCancelledForms ();updateTabMenuCss (this);" ><a  >Cancelled</a></td>
				</tr>
			</table>
		</td>
		<td class="bodyTD">
			<div>
				<div id="tabHeader">
				<hr class="slim"/></div>
			</div>
			<table>
				<tbody>
					<tr class='searchTextBox'>
						<td >Search <input type='text' id='searchTxt' > </td>
					</tr>
				</tbody>
			</table>
			<div id="form" class="mainResultDiv">
				
			</div>
		</td>
		<td class="recentTD">
			<div class="recentPost">Recently Posted
				<hr class="slim"/></div>
			<div class="recentPostProp">
				<table id="tableStatusId">
					<thead>
						<tr>
							<td></td>
						</tr>
					</thead>
				</table>
			</div>
		</td>
	</tr>
</table>
</div>
</body>
</html>