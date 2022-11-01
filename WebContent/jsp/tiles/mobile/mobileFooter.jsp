<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp" %>
 <!--

	Description: The footer of the workflow in mobile.
 -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/mobileStyleSheet.css" media="all">
<script src="${pageContext.request.contextPath}/js/searchUtil.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/commonUtil.js"
	type="text/javascript"></script>
<script type="text/javascript">
var formApprovals = new Array ();
var selectedFormType = 0;
var isVisible = false;
$(document).ready (function () {
	<c:forEach var="module" items="${approval}" varStatus="status">
		var name="${module.name}";
		var title="${module.title}";
		var uri="${module.uri}";
		var editUri="${module.edit.uri}";
		var workflowName = "${module.workflow}";
		var module = new FormApproval (name, title, uri, editUri, workflowName);
		formApprovals.push(module);
		$("#selectForms").append("<option value=" + ${status.index} + ">" + title + "</option>");
	</c:forEach>
		
	if (formApprovals.length > 0) {
		loadForms (0, '', 1, true);
	}
});


$(function () {
	$("#selectForms").change(function() {
		loadForms ($(this).val(), encodeURIComponent($("#txtCriteria").val()), 1, true);
	});
	
	$("#selectStatusIds").change(function() {
		loadForms (selectedFormType, encodeURIComponent($("#txtCriteria").val()), 1, false);
	});
	
	$("#txtCriteria").bind("keypress", function (e) {
		if (e.which == 13) {
			loadForms (selectedFormType, encodeURIComponent($(this).val()), 1 , false);
		}
	});
});

function FormApproval (name, title, uri, editUri, workflowName) {
	this.name = name;
	this.title = title;
	this.uri = uri;
	this.editUri = editUri;
	this.workflowName = workflowName;
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
function showFooter() {
	if(isVisible){
		addClass("#footerDiv","ui-hidden");
		isVisible = false;
	}else{
		removeClass("#footerDiv","ui-hidden");
		isVisible = true;
	}
}
</script>
</head>
<body>
<div class="v-align-bottom" style="display: block;" >
	<div style="background-color: transparent;height: 20px;;" >
		<img alt="" src="../../images/search_active.png"  class="ui-btn-aright" onclick="showFooter();">
	</div>
	<div id="footerDiv" class="ui-hidden v-align-bottom">
		<div id="searchParam" class="ui-btn-aleft">
			<div>
				<h3>Form:</h3>
				<span>
					<select id="selectForms">
					</select>
				</span>
			</div>
		
			<div>
				<h3>Status:</h3>
				<span>
					<select id="selectStatusIds">
						<option value="-1">All</option>
					</select>
				</span>
			</div>
		
			<div>
				<h3>Criteria:</h3>
				<span>
					<input id="txtCriteria"/> 
				</span>
			</div>
		</div>
		<div id="searchBtn" class="ui-btn-aleft">
			<div class="v-align-middle">
				<img src="../../images/search_active.png" style="margin: auto;">
				<span>SEARCH</span>
			</div>
		</div>
	</div>
</div>
</body>
</html>