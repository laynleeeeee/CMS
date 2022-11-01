<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Document Type main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(function(){
	$("#txtName, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchdocumentType();
			e.preventDefault();
		}
	});
	$("#btnAddDocumentType").click(function () {
		$("#divDocumentTypeForm").load(contextPath + "/admin/documentType/form");
		$("html, body").animate({scrollTop: $("#divDocumentTypeForm").offset().top}, 0050);
	});
});

$("#btnEditDocumentType").click(function () {
	var id = getCheckedId ("cbDocumentType");
	$("#divDocumentTypeForm").load(contextPath + "/admin/documentType/form?documentTypeId="+id);
	$("html, body").animate({scrollTop: $("#divDocumentTypeForm").offset().top}, 0050);
});

function saveDocumentType(){
	$("#btnSaveDocumentType").attr("disabled", "disabled");
	doPostWithCallBack("documentType", "divDocumentTypeForm", function(data){
		if (data.startsWith("saved")) {
			$("#spanBusMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "Action Notice " + $("#name").val() + ".");
			$("#divDocumentTypeForm").html("");
			searchdocumentType();
		} else {
			$("#divDocumentTypeForm").html(data);
		}
	});
}

function getCommonParam() {
	var name = processSearchName($("#txtName").val());
	var status = $("#selectStatus").val();
	return "name="+name+"&status="+status;
}

function searchdocumentType(){
	doSearch ("divDocumentTypeTable", "/admin/documentType?"+getCommonParam()+"&pageNumber=1");
}
function cancelForm() {
	$("#divDocumentTypeForm").html("");
	searchdocumentType();
}
</script>
</head>
<body>
<body>
	<div>
		<table class="formTable">
			<tr>
				<td class="title">Name</td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
						<option value=-1>All</option>
						<option value=1>Active</option>
						<option value=0>Inactive</option>
				</select> <input type="button" id="btnSearchdocumentType" value="Search"
					onclick="searchdocumentType();" /></td>
			</tr>
		</table>
	</div>
	<span id="spanBusMessage" class="message"></span>
	<div id="divDocumentTypeTable">
		<%@ include file="DocumentTypeTable.jsp"%>
	</div>
	<div class="controls">
		<input type="button" id="btnAddDocumentType" value="Add" />
		<input type="button" id="btnEditDocumentType" value="Edit" />
	</div>
	<div id="divDocumentTypeForm" style="margin-top: 50px;"></div>
</body>
</body>
</html>