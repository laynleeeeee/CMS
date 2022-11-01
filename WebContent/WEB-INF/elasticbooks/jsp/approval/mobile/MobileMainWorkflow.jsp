<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp" %>
 <!--

	Description: The entry point of mobile form workflow
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/mobileStyleSheet.css" media="all">
<script type="text/javascript">
function loadForms (formApprovalId, searchCriteria, page, isPopulateStatuses) {
	var formApproval = formApprovals[formApprovalId];
	selectedFormType = formApprovalId;
	
	var retrieveForm = function () {
		$("#form").empty();
		var paramSelected = "statuses="+$("#selectStatusIds").val();
		var pageCriteria = "&page="+page;
		var workflowriteria = "&workflowName="+formApproval.workflowName;
		$.ajax({
			url: contextPath + "/" + formApproval.uri +"?" + paramSelected + 
					"&criteria="+searchCriteria+pageCriteria+workflowriteria, 
			success: function(responseText){
				var mainDiv = "<div style='width: 100%; position: relative;float:left;'>";
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
					var leftDiv = "<div class='leftDiv "+className+"'>"+
							"<div class='ui-line'>"+concatString(desc,80)+"</div>"+
							"<div class='ui-line'>"+object.status+ "&nbsp;&nbsp;"+name+ "&nbsp;&nbsp;"+object.lastUpdatedDate+"</div>"+
							"</div>";
					mainDiv += leftDiv;

					var rightDiv = 
							"<div class='rightDiv v-align-top' style='width: 25%;'>"+
								"<div class='rightDiv v-align-middle' style='width: 50%;background-color: #99CCFF;'>Next Status</div>"+
								"<div class='rightDiv v-align-middle' style='width: 50%;background-color: #CCFFFF;'>View</div>"+
							"</div>";
					mainDiv += rightDiv;
				}
				mainDiv += "</div>";
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
</script>
</head>
<body>
	<div id="form">
	</div>
</body>
</html>