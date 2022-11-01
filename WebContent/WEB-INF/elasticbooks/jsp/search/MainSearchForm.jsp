<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include.jsp" %>
  <!--

	Description: The entry point of form search
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebSearch.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript">
var searchModules = new Array();
$(document).ready(function() {
	<c:forEach var="module" items="${formSearch}">
		var name="${module.name}";
		var title="${module.title}";
		var uri="${module.uri}";
		var editUri="${module.edit.uri}";
		var editTitle="${module.edit.title}";
		var deleteUri="${module.delete.uri}";
		var reports = new Array();
		<c:forEach var="report" items="${module.reports}">
			var reportName="${report.name}";
			var reportTitle="${report.title}";
			var reportUri="${report.uri}";
			var reportModule = new SearchModule(reportName, reportTitle, reportUri);
			reports.push(reportModule);
		</c:forEach>
		var module = new SearchModule (name, title, uri, editUri, deleteUri, reports, editTitle);
		searchModules.push(module);
	</c:forEach>
	$("#mSearch").val("${criteria}");
	search();
});

function getSearchModule (name) {
	for (var i = 0; i < searchModules.length; i++) {
		var searchModule = searchModules[i];
		if (searchModule.name == name)
			return searchModule;
	}
	return null;
}

function SearchModule (name, title, uri, editUri, deleteUri, reports, editTitle) {
	this.name = name;
	this.title = title;
	this.uri = uri;
	this.editUri = editUri;
	this.editTitle = editTitle;
	this.deleteUri = deleteUri;
	this.reports = reports;
}

function search () {
	var searchFormUris = new Array ();
	$("[name=cbSearchFroms]").each(function( index ) {
		if ($(this).is(':checked') == true){
			searchFormUris.push($(this).attr("id"));
		}
	});
	
	$("#divResultContainer").empty();
	var name = searchFormUris.shift();
	var criteria = "${criteria}";
	recursiveSearch(criteria, name, searchFormUris);
}

function recursiveSearch (criteria, name, searchFormUri) {
	var module = getSearchModule(name);
	var currentUri = module.uri;
	var currentTitle = module.title;
	$.ajax({
		url: contextPath + "/" + currentUri + "?criteria="+encodeURIComponent(criteria), 
		success: function(responseText){
			var mainDiv = "<div><h5>" + currentTitle+"</h5> <hr class='med'>";
			for (var index = 0; index < responseText.length; index++){
				var object =  responseText[index];
				var title = object["title"];
				var id = object["id"];
				
				// Icon
				var mainTr = "<table ><tr>";

				//Edit image
				var editImg = "<img src='../CMS/images/file_edit.png'  height='20' width='20'>";
				//Delete image
				var deleteImg = "<img src='../CMS/images/file_delete.png' height='20' width='20'>";
				//Report image
				var reportImg = "<img src='../CMS/images/docu_search.png' height='19' width='20'>";

				//edit
				var editTd = "<td class='editImg'></td>";
				if (module.editUri != "") { // If no access right defined.
					var editLink = contextPath +"/" +module.editUri + "?pId="+id;
					editTd = "<td class='editImg'><a  title='"+module.editTitle+"' onclick='showEditForm(\""+editLink+"\")'>"+editImg+"</a></td>";
					} 
				
				//Delete
				var deleteTd = "<td></td>";
				if (module.deleteUri != "") { // If no access right defined.
					var deleteLink = contextPath +"/" +module.deleteUri + "?pId="+id;
					deleteTd = "<td ><a  title='Delete Account' onclick='deleteForm(\""+deleteLink+"\")'>"+deleteImg+"</a></td>";
				} 
				
				//Reports
				reportMenuTd = "<td></td>";
				if (module.reports != "") {
					var reportMenus = createReportMenu(name, id, module.reports);
					reportMenuTd = "<td><ul id='navigation'> <li class='dropdown'><a title='Reports' >"+reportImg+"</a>" + reportMenus +"</li></ul></td>";
				}
				
				var iconTr = editTd + deleteTd  + reportMenuTd;

				var properties = object["properties"];
				var searchResult = "<td>";
				var resultTable = "<table class='resultTbl'>";
				resultTable += "<tr><td colspan='2'><a >"+title+"</a></td></tr>";

				for (var i = 0; i < properties.length; i++) {
					var property =  properties[i];
					var name = property["name"];
					var value = property["value"];
					resultTable += "<tr><td>"+name+"</td><td>: "+value+"</td></tr>";
				}
				resultTable += "</table>";
				searchResult += resultTable + "</td>";
				
				mainTr += iconTr + searchResult + "</tr></table><hr class='thin'>";

				mainDiv += mainTr + "</div>";
			}
			$("#divResultContainer").append (mainDiv);
			
					
			if (searchFormUri.length > 0) {
				var name = searchFormUri.shift();
				recursiveSearch(criteria, name, searchFormUri);
			} else {
				$('.dropdown').click(function() {
			        // When the event is triggered, grab the current element 'this' and
			        // find it's children '.sub_navigation' and display/hide them
					$(this).find('.sub_navigation').slideToggle(); 
				});
			}
			
    	},
    	dataType: "json" 
	});
}

function showMenu (id) {
	$(this).find("#"+id).slideToggle(); 
}

function createReportMenu(name, id, reports) {
	var menu = "<ul class='sub_navigation' id = "+name+id+">";
	for (var index = 0; index < reports.length; index++){
		var object =  reports[index];
		var title = object["title"];
		var reportUri = object["uri"];
		var uri = contextPath +"/"+ reportUri + "/generateReport?id="+id;
		menu += "<li><a href='"+uri+"'>"+title+"</a></li>";
	}
	menu += "</ul>";
	return menu;
}

function deleteForm (path) {
	var message = "";
	$("#dialog").dialog({
		height: 140,
		width: 250,
		modal: true,
		title: "Confirmation",
		buttons: {
			Ok: function() {
				$.ajax({
					url: path }).done (function (data) {
						if (data == "done") {
							message = "Successfully deleted";
							$("#dialog").html('<p align=center>' + message + '</p>');
							showCLosingDialog();
						} else {
							if(data == "deleted")
								message = "Cannot delete approve invoices!";
							else if(data == "cancelled")
								message = "Cannot delete cancelled invoices!";
							else
								message = "Error while deleting data!";
							$("#dialog").html('<p align=center>' + message + '</p>');
							showCLosingDialog();
						}
						search();
					});
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		}
	});
	message = "Do you really want to delete this?";
	$("#dialog").html('<p align=center>' + message + '</p>');
}

function showCLosingDialog() {
	$("#dialog").dialog({
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog("close");
				$(this).dialog("destroy");
			}
		}
	});
}

function showEditForm (path) {
	$("#editForm").load(path, function (data) {
		$("#editForm").lightbox_me({
			closeSelector: "#btnClose",
			centered: true});
		//Set background color of disabled inputs.
		$("#editForm :input").css("background-color", "white");
		updatePopupCss();
		//For initial loading of pop-up form.
		$("#btnClose").css("cursor","pointer");
		$("#btnClose").css("float","right");
	});
}

function updateTable(formStatus) {
	showCLosingDialog();
	$("#successDialog").dialog({
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog("close");
				$(this).dialog("destroy");
			}
		}
	});
	$('#editForm').trigger('close');
	search ();
}
</script>
<style type="text/css">
.ui-dialog{
	font-size: 75%;
	position: fixed;
	top: 50px;
	left: 50px;
}
</style>
<title>Reports</title>
</head>
<body>
<div class="searchContainer">
<div id="divSubSearchForm"></div>
<c:forEach var="formSearch" items="${formSearch}">
	<input type="hidden" name="searchForms"
		id="${formSearch.name}" value="${formSearch.uri};${formSearch.title}">
</c:forEach>
<table style="border-collapse: collapse;">
	<tr>
		<td>
			<div class="searchFilterHeader">Filter Search Results
				<hr class="normal">
			</div>
		</td>
		<td>
			<div  class="searchResultDiv">Search result
				<hr class="thin2">
				<div id="successDialog" style="display: none;">
					<p align="center">Successfully updated</p>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td valign="top">
				<table class="searchFilters">
					<c:forEach var="formSearch" items="${formSearch}">
					<tr>
						<td >
							<input type="checkbox" id="${formSearch.name}"
								checked="checked" title="${formSearch.title}" name="cbSearchFroms" onchange="search()"></td>
						<td class="titleName">${formSearch.title}</td>
					</tr>
					</c:forEach>
				</table>
		</td>
		<td>
			<div id="divResultContainer" class="mainResultDiv">
			</div>
			<div style="border:1px solid black;" id="editForm" class="formFloat" >
			</div>
		</td>
	</tr>
</table>
</div>
</body>
</html>