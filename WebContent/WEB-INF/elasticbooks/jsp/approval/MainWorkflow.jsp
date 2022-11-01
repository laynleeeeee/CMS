<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include.jsp" %>
 <!--

	Description: The entry point of form workflow
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebApproval.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script src="${pageContext.request.contextPath}/js/commonUtil.js"
	type="text/javascript"></script>
<style type="text/css">
.formFloat {
	display:none;
	background: white;
	top: 50%;
	left: 50%;
	margin-top: -300px;
	margin-left: -250px;
	border:1px solid black;
	width: 80%;
}
</style>
<script type="text/javascript">
var formApprovals = new Array ();
var selectedFormType = 0;
$(document).ready(function() {
	$tipDiv = $("#tipDiv");
	var sideMenu = "";
	var isTagSelected =false;
	<c:forEach var="module" items="${approval}">
		var name="${module.name}";
		var title="${module.title}";
		var uri="${module.uri}";
		var editUri="${module.edit.uri}";
		var viewUri="${module.viewUri}";
		var workflowName = "${module.workflow}";
		var module = new FormApproval (name, title, uri, editUri, viewUri, workflowName);
		formApprovals.push(module);
		var classType = "appMenu";
		if (isTagSelected == false) {
			isTagSelected = true;
			classType = "appMenuSelected";
		}
		sideMenu += "<tr><th id='thApp' class='"+classType+"' onclick=\"retrieveIndivForms("+(formApprovals.length - 1)+",'',1,true);updateTabMenuCss (this);\"><a>"+title+"</a></th></tr>";
	</c:forEach>
	$("#tableSideMenu thead").append(sideMenu);
	if (formApprovals.length > 0) {
		retrieveIndivForms (0, '', 1, true);
	}

	$("#searchTxt").bind("keypress", function (e) {
		if (e.which == 13) {
			retrieveIndivForms (selectedFormType, processSearchName($(this).val()), 1,false);
		}
	});
	
	$("#selectStatusIds").change(function (e) {
		retrieveIndivForms (selectedFormType, processSearchName($("#searchTxt").val()), 1,false);
	});
});

function FormApproval (name, title, uri, editUri, viewUri, workflowName) {
	this.name = name;
	this.title = title;
	this.uri = uri;
	this.editUri = editUri;
	this.viewUri = viewUri;
	this.workflowName = workflowName;
}

function retrieveIndivForms (formApprovalId, searchCriteria, page, isPopulateStatuses) {
	$("#divFilterId").show();
	var formApproval = formApprovals[formApprovalId];
	var faUri = $.trim(formApproval.editUri) != "" ? formApproval.editUri :  formApproval.viewUri;
	selectedFormType = formApprovalId; 
	var retrieveForm = function () {
		$("#form").empty();
		if($("#form").is(":empty")){
	 		$("#calBorder").css({ "visibility": 'hidden'});
		}
		var paramSelected = "statuses="+$("#selectStatusIds").val();
		var pageCriteria = "&page="+page;
		var workflowriteria = "&workflowName="+formApproval.workflowName;
		$.ajax({
			url: contextPath + "/" + formApproval.uri +"?" + paramSelected + 
					"&criteria="+searchCriteria+pageCriteria+workflowriteria, 
			success: function(responseText){
				var mainDiv = "";
				var table = "<table > <tbody>";
				var page = responseText[0];
				var data = page.data; // Only the first result of the data.
				for (var index = 0; index < data.length; index++){
					var object =  data[index];
					var name = object["lastUpdatedBy"];
					var desc = object["shortDesc"];
					var id = object["id"];
					var highlight = object.highlight;
					var className = "lineResult";
					if (highlight == true) {
						className = "lineResultHighlight";
					}
					table += "<tr onclick='showForm("+id+", \""+faUri+"\")' class='"+className+"'>"+
							"<td class='tdApp' width='70%'>"+concatString(desc,100)+"</td>"+ 
							"<td class='tdApp' width='7%'>"+object.status+"</td>"+
							"<td class='tdApp' width='25%'>"+name+"</td>"+
							"<td class='tdApp' width='8%'>"+object.lastUpdatedDate+"</td>"+
							"</tr>";
				}
				table += "</tbody> ";
				
				//Footer
				var trFooter = "<tfoot><tr ><td>"+page.firstItemIndex +"/"+page.totalRecords + "</td>";
				var trPrevPage = "";
				var criteria = processSearchName($("#searchTxt").val());
				if (page.currentPage > 1){
					var prevPage = page.currentPage - 1;
					trPrevPage = "<a onclick=\"retrieveIndivForms("+formApprovalId+",'"+criteria+"',"+prevPage+",false)\">"+prevPage+"..prev </a>";
				}
				var trNextPage = "";
				if (page.currentPage < page.lastPage){
					var nextPage = page.currentPage + 1;
					trNextPage = "<a onclick=\"retrieveIndivForms("+formApprovalId+",'"+criteria+"',"+nextPage+",false)\">"+nextPage+"..next</a>";
				}
				trFooter += "<td colspan='3' align='right'>"+trPrevPage+"<a><b>"+page.currentPage+"</b></a> "+trNextPage+"</td><tr></tfoot>";
				table += trFooter;
				table += "</table>";
				mainDiv += table + "</div>";
				//Validate the selected form before loading.
				if(selectedFormType == formApprovalId) {
					$("#form").empty();
					$("#form").append (mainDiv);
				}
				for (var i =0; i < formApprovals.length; i++) {
					if (i != formApprovalId) {
						$("#"+i+"tabMenu").attr('class', 'tabMenu');
					}
				}
				$("#"+formApprovalId+"tabMenu").attr('class', 'tabSelectedMenu');
			}, 
			dataType: "json"
		});
	};
	if (isPopulateStatuses == true) {
		populateStatuses(formApprovalId, retrieveForm);
	} else {
		retrieveForm();	
	}
}

function populateStatuses (formApprovalId, formRetreiver) {
	var formApproval = formApprovals[formApprovalId];
	$.ajax({
		url: contextPath + "/" + formApproval.uri + "/statuses?workflowName="+formApproval.workflowName,
		success: function (data) {
			var options = "<option value='-1'>All</option>";
			for (var index = 0; index < data.length; index++){
				var formStatus = data[index];
				var selectedAttribute = "";
				if (formStatus.selected == true) {
					selectedAttribute = "selected='selected'";
				}
				options += "<option "+selectedAttribute+" value='"+formStatus.id+"'>"+formStatus.description+"</option>";
			}
			$("#selectStatusIds").empty().append(options);
			formRetreiver();
		},
		dataType: "json"
	});
}

function showForm (pId, uri) {
	$("#divFilterId").hide();
	$("#form").load (contextPath+ "/"+uri + "?pId="+pId+"&isEdit=false");
}

function updateTable (formStatus) {
	$("#tableStatusId tr:first").after("<tr><td class='recTitle'>"+formStatus.title +"</td></tr><tr><td class='recSMS'>"+formStatus.message+"</td></tr><tr><td></td></tr>");
	retrieveIndivForms(selectedFormType, "", 1);
}
function updateTabMenuCss (tabMenu) {
	$(".appMenuSelected").attr('class', 'appMenu');
	$(tabMenu).attr('class', 'appMenuSelected');
	
}

function getCompanyList() {
	$.ajax({
		url: contextPath + "/getCompany/all",
		success: function(responseText){
			var resultTable =  null;
			resultTable = "<table>";
			resultTable += "<tr><td class='searchTipTitle' colspan='2'> Companies and Number</td></tr>";
			for (var index = 0; index < responseText.length; index++){
				var object =  responseText[index];
				var name = object["name"];
				var number = object["companyNumber"];
				resultTable += "<tr><td class='searchTipDetails' style='width: 65%;'>"+name+"</td><td class='searchTipDetails'> "+number+"</td></tr>";
			}
			resultTable += "</table>";
			$("#divCompanyList").append (resultTable);
		},
    	dataType: "json"
	});
}

function showSearchTip() {
	processSearchTip();
	$tipDiv.removeClass( "noDisplay" );
	$tipDiv.css({
		display: "",
		background: "white",
		width: "300px",
		position: "fixed"
	});
	$("#searchTip").removeClass( "noDisplay" );
	getCompanyList();
}

function closeSearchTip() {
	$tipDiv.addClass( "noDisplay" );
}

function processSearchTip() {
	$("#searchTip").empty();
	var list = "";
		list += "<span class='tipClsBtn' onclick='closeSearchTip();'>[X]</span>";
		list += "<ul style='list-style: none;'>"+
			"<li class='searchTipTitle'>Search Options:</li>"+
			"<li class='searchTipTitle'>1. Search by Date (mm/dd/yyyy)</li>"+
			"<li class='searchTipDetails'>&nbsp;&nbsp;1.1 Specific Date ( 10/10/2014 )</li>"+
			"<li class='searchTipDetails'>&nbsp;&nbsp;&nbsp; - To search for October 10, 2014</li>"+
			"<li class='searchTipDetails'>&nbsp;&nbsp;1.2 Date Range ( 10/1/2014 10/10/2014 )</li>"+
			"<li class='searchTipDetails'>&nbsp;&nbsp;&nbsp; - To search from October 1 to 10, 2014</li><br />"+
			"<li class='searchTipTitle'>2. Search by Company</li>"+
			"<li class='searchTipDetails'>Use this format \"#cCompanyNumber\" in search<br />"+
				"criteria to search the transaction of the company. <br />"+
				"Ex. #c101 to search for specific company.</li>"+
			"<li><div id='divCompanyList' style=' height: 310px; overflow-y: scroll;'></div></li></ul>";
	$("#searchTip").append(list);
}
</script>
<title>Form Approval</title>
</head>
<body>
<div id="container">
<table height="100%">
	<tr>
		<td class="menuTD" >
			<table class="appMainMenu" id="tableSideMenu">
				<thead>
				</thead>
			</table>
		</td>
		<td id="tdPopUp">
			<div id="editForm" class="formFloat"></div>
		</td>
		<td class="bodyTD">
			<!-- <div>
				<div id="tabHeader">
				<hr class="slim"/></div>
			</div> -->
			<div id="divFilterId">
				<table>
					<tbody>
						<tr class='searchTextBox'>
							<td class="formTitle">Filters</td>
							<td><input type='text' id='searchTxt' class="txtApprovalSearchFilter" >
								<select id="selectStatusIds" class="frmSmallSelectClass">
									<option value="-1">All</option>
								</select>
							</td>
							<td><img id="searchTipId" src="${pageContext.request.contextPath}/images/help.png"
									onclick="showSearchTip()"/></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="tipDiv" class="noDisplay">
				<div id="searchTip" class="noDisplay" >
				</div>
			</div>
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
					<tfoot></tfoot>
				</table>
			</div>
		</td>
	</tr>
</table>
</div>
</body>
</html>