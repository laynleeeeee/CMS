<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description: Customer Type main page
	 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(function () {
	$("#txtName, #txtDescription, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchCustomerType();
			e.preventDefault();
		}
	});
});

function addCustomerType() {
	$("#divCustomerTypeForm").load(contextPath + "/admin/customerType/form");
	$("html, body").animate({scrollTop: $("#divCustomerTypeForm").offset().top}, 0050);
}

function editCustomerType() {
	var id = getCheckedId("cbCustomerType");
	$("#divCustomerTypeForm").load(contextPath + "/admin/customerType/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divCustomerTypeForm").offset().top}, 0050);
}

var isSaving = false;
function saveCustomerType() {
	if(isSaving == false) {
		isSaving = true;
		$("#btnSaveCustomerType").attr("disabled", "disabled");
		doPostWithCallBack ("customerTypeFormId", "divCustomerTypeForm", function(data) {
			if(data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "customer type: " + $("#customerTypeName").val() + ".");
				$("#divCustomerTypeForm").html("");
				searchCustomerType();
			} else {
				$("#divCustomerTypeForm").html(data);
			}
			isSaving = false;
		});
	}
}

function cancelCustomerType() {
	$("#divCustomerTypeForm").html("");
	$("html, body").animate({scrollTop: $("#divCustomerTypeTbl").offset().top}, 0050);
}

function getCommonParam() {
	var txtName = processSearchName($("#txtName").val());
	var txtDescription = processSearchName($("#txtDescription").val());
	var status = $("#selectStatus").val();
	return "?name="+txtName+"&description="+txtDescription+"&status="+status;
}

function searchCustomerType() {
	doSearch ("divCustomerTypeTbl", "/admin/customerType/search"+getCommonParam()+"&pageNumber=1");
}

</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Name</td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Description</td>
				<td><input type="text" id="txtDescription" class="inputSmall" ></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchCustomerType();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divCustomerTypeTbl">
		<%@ include file="CustomerTypeTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddCustomerType" value="Add" onclick="addCustomerType();"></input>
		<input type="button" id="btnEditCustomerType" value="Edit" onclick="editCustomerType();"></input>
	</div>
	<br>
	<br>
	<div id="divCustomerTypeForm" style="margin-top: 20px;">
	</div>
</body>
</html>