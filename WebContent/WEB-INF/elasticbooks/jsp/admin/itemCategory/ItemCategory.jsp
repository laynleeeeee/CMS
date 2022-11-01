<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description: YBL Item Category main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
$(function () {
	$("#btnAddItemCategory").on("click", function (){
		$("#divItemCategoryForm").load(contextPath + "/admin/itemCategories/form");
		$("html, body").animate({scrollTop: $("#divItemCategoryForm").offset().top}, 0050);
	});

	$("#btnEditItemCategory").click(function () {
		var id = getCheckedId ("cbItemCategory");
		$("#divItemCategoryForm").load(contextPath + "/admin/itemCategories/form?itemCategoryId="+id);
		$("html, body").animate({scrollTop: $("#divItemCategoryForm").offset().top}, 0050);
	});

	$("#txtName, #selectStatus").bind("keypress", function(e) {
		if (e.which == 13){
			searchItemCategory();
			e.preventDefault();
		}
	});
});

function cancelForm() {
	$("#divItemCategoryForm").html("");
	searchItemCategory();
}

function getCommonParam(){
	var name = processSearchName($("#txtName").val());
	var status = $("#selectStatus").val();
	var pageNumber = "${pageNumber}";
	return "?name="+name+"&status="+status+"&pageNumber="+pageNumber;
}

function searchItemCategory() {
	doSearch ("divItemCategoryTable", "/admin/itemCategories/search"+getCommonParam());
}
</script>
<title>Item Category</title>
</head>
<body>
<div id="divSearch">
		<table class="formTable">
			<tr>
				<td class="title width="15%">Name</td>
				<td><input class="inputSmall" id="txtName" type="text" maxLength="100"/>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchItemCategory();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanCategoryMessage" class="message"></span>
	<div id="divItemCategoryTable">
		<%@ include file="ItemCategoryTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddItemCategory" value="Add" />
		<input type="button" id="btnEditItemCategory" value="Edit" />
	</div>
	<div id="divItemCategoryForm" style="margin-top: 50px;"></div>
</body>
</html>