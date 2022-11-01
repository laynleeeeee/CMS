<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: JSP page for Product line.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(function () {
	$("#txtProductLine, #txtRawMaterials, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchProductLine();
			e.preventDefault();
		}
	});
});

function getCommonParam() {
	var txtProductLine = processSearchName($("#txtProductLine").val());
	var txtRawMaterials = processSearchName($("#txtRawMaterials").val());
	var status = $("#selectStatus").val();
	return "?productLine="+txtProductLine+"&rawMaterial="+txtRawMaterials+"&status="+status;
}

function searchProductLine() {
	doSearch ("productLineTable", "/admin/productLine/search"+getCommonParam()+"&pageNumber=1");
}

function addProductLine() {
	$("#divProductLineForm").load(contextPath + "/admin/productLine/form");
	$("html, body").animate({scrollTop: $("#divProductLineForm").offset().top}, 0050);
}

function editProductLine() {
	var id = getCheckedId("cbProductLine");
	$("#divProductLineForm").load(contextPath + "/admin/productLine/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divProductLineForm").offset().top}, 0050);
}

function cancelProductLine() {
	$("#divProductLineForm").html("");
	$("html, body").animate({scrollTop: $("#productLineTable").offset().top}, 0050);
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Product Line</td>
				<td><input type="text" id="txtProductLine" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Raw Materials</td>
				<td><input type="text" id="txtRawMaterials" class="inputSmall" ></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchProductLine();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanProcductLineMsg" class="message"></span>
	<div id="productLineTable">
		<%@ include file="ProductLineTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddProductLine" value="Add" onclick="addProductLine();"></input>
		<input type="button" id="btnEditProductLine" value="Edit" onclick="editProductLine();"></input>
	</div>
	<br>
	<br>
	<div id="divProductLineForm" style="margin-top: 20px;">
	</div>
</body>
</html>