<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: JSP page for List of AR Customers.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
$(document).ready (function () {
	$("#btnEditCustomer").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	searchCustomer();
});

$(function () {
	$("#txtName, #txtAddress, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchCustomer();
			e.preventDefault();
		}
	});
});

function searchCustomer() {
	doSearch ("customerTable", "/admin/arCustomer/search"+getCommonParam()+"&pageNumber=1");
}

function addCustomer() {
	$("#customerForm").load(contextPath + "/admin/arCustomer/form");
	$("html, body").animate({scrollTop: $("#customerForm").offset().top}, 0050);
}

function editCustomer() {
	var id = getCheckedId("cbCustomer");
	$("#customerForm").load(contextPath + "/admin/arCustomer/form?pId="+id);
	$("html, body").animate({scrollTop: $("#customerForm").offset().top}, 0050);
}

var isSaving = false;
function saveCustomer() {
	if (isSaving == false) {
		isSaving = true;
		$("#cbProjectId").removeAttr("disabled");
		$("#btnSaveArCustomer").attr("disabled", "disabled");
		doPostWithCallBack ("customerFormId", "customerForm", function(data) {
			if (data.substring(0,5) == "saved") {
				$("#spanCustomerMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "Customer " + getName() + ".");
				$("#customerForm").html("");
				searchCustomer();
			} else {
				var isChecked = $("#cbProjectId").is(":checked");
				$("#customerForm").html(data);
				$("#cbProjectId").prop("checked", isChecked);
				if ("${arCustomer.id}" > 0 && isChecked) {
					$("#cbProjectId").attr("disabled", "disabled");
				}
			}
			isSaving = false;
		});
	}
}

function getName() {
	return $("#name").val();
}

function cancelCustomer() {
	$("#customerForm").html("");
	$("html, body").animate({scrollTop: $("#customerForm").offset().top}, 0050);
	searchCustomer();
}

function getCommonParam() {
	var type = $("#slctType").val();
	var name = processSearchName($("#txtName").val());
	var txtStreetBrgy = processSearchName($("#txtStreetBrgy").val());
	var txtCityProvince = processSearchName($("#txtCityProvince").val());
	var status = $("#selectStatus").val();
	return "?name="+name+"&streetBrgy="+txtStreetBrgy+"&status="+status+"&bussClassId="+type+"&cityProvince="+txtCityProvince;
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
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchCustomer();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanCustomerMessage" class="message"></span>
	<div id="customerTable">
		<%@ include file="ArCustomerTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddCustomer" value="Add" onclick="addCustomer();"></input>
		<input type="button" id="btnEditCustomer" value="Edit" onclick="editCustomer();"></input>
	</div>
	<br>
	<br>
	<div id="customerForm" style="margin-top: 20px;">
	</div>
</body>
</html>