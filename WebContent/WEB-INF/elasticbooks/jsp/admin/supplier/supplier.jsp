<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: JSP page for YBL List of Suppliers.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<style type="text/css">
.frmSelectClass {
	width: 220px;
}
</style>
<script type="text/javascript">

$(document).ready (function () {
	$("#btnEditSupplier").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

$(function () {
	$("#txtName, #txtAddress, #selectBusRegType, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchSupplier();
			e.preventDefault();
		}
	});
});

function getCommonParam() {
	var bussinessClassificationId = $("#slctType").val();
	var name = processSearchName($("#txtName").val());
	var streetBrgy = processSearchName($("#txtStreetBrgy").val());
	var cityProvince = processSearchName($("#txtCityProvince").val());
	var busRegType = $("#selectBusRegType").val();
	var status = $("#selectStatus").val();
	return "?name="+name+"&busRegType="+busRegType+"&cityProvince="+cityProvince+"&bussinessClassificationId="+bussinessClassificationId
		+"&streetBrgy="+streetBrgy+"&status="+status;
}

function searchSupplier() {
	doSearch ("supplierTable", "/admin/supplier/search"+getCommonParam()+"&pageNumber=1");
}

function addSupplier() {
	$("#supplierForm").load(contextPath + "/admin/supplier/form");
	$("html, body").animate({scrollTop: $("#supplierForm").offset().top}, 0050);
}

function editSupplier() {
	var id = getCheckedId("cbSupplier");
	$("#supplierForm").load(contextPath + "/admin/supplier/form?supplierId="+id);
	$("html, body").animate({scrollTop: $("#supplierForm").offset().top}, 0050);
}

function saveSupplier() {
	$("#btnSaveSupplier").attr("disabled", "disabled");
	doPostWithCallBack ("supplier", "supplierForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanSupplierMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "Supplier " + getName() + ".");
			$("#supplierForm").html("");
			searchSupplier();
		} else {
			$("#supplierForm").html(data);
		}
	});
}

function getName() {
	return name = $("#name").val();
}

function cancelSupplier() {
	$("#supplierForm").html("");
	$("html, body").animate({scrollTop: $("#supplierTable").offset().top}, 0050);
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Type</td>
				<td><select id="slctType" class="frmSmallSelectClass">
					<option value="-1" selected>ALL</option>
					<c:forEach var="classification" items="${classifications}">
							<option value="${classification.id}">${classification.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td width="15%" class="title">Name</td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Street, Barangay</td>
				<td><input type="text" id="txtStreetBrgy" class="inputSmall" ></td>
			</tr>
			<tr>
				<td width="15%" class="title">City, Province, and ZIP Code</td>
				<td><input type="text" id="txtCityProvince" class="inputSmall" ></td>
			</tr>
			<tr>
				<td width="15%" class="title">VAT</td>
				<td><select id="selectBusRegType" class="frmSmallSelectClass">
					<option value="-1">All</option>
						<c:forEach var="brt" items="${busRegType}">
							<option value="${brt.id}">${brt.name}</option>
						</c:forEach>
				</select>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchSupplier();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanSupplierMessage" class="message"></span>
	<div id="supplierTable">
		<%@ include file="supplierTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddSupplier" value="Add" onclick="addSupplier();"></input>
		<input type="button" id="btnEditSupplier" value="Edit" onclick="editSupplier();"></input>
	</div>
	<br>
	<br>
	<div id="supplierForm" style="margin-top: 20px;">
	</div>
</body>
</html>