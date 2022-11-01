<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description	: Currency main page
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
$(document).ready(function() {
});
$(function () {
	$("#txtName, #txtDescription, #txtSign, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			search();
			e.preventDefault();
		}
	});
});

function addCurrency() {
	$("#divCurrencyForm").load(contextPath + "/admin/currency/form");
	$("html, body").animate({scrollTop: $("#divCurrencyForm").offset().top}, 0050);
}

function editCurrency() {
	var id = getCheckedId("cbCurrency");
	$("#divCurrencyForm").load(contextPath + "/admin/currency/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divCurrencyForm").offset().top}, 0050);
}

var isSaving = false;
function saveCurrency() {
	if(isSaving == false) {
		isSaving = true;
		$("#btnSave").attr("disabled", "disabled");
		doPostWithCallBack ("currencyFromId", "divCurrencyForm", function(data) {
			if(data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "currency: " + $("#name").val() + ".");
				$("#divCurrencyForm").html("");
				search();
			} else {
				$("#divCurrencyForm").html(data);
			}
			isSaving = false;
		});
	}
}

function cancelCurrency() {
	$("#divCurrencyForm").html("");
	$("html, body").animate({scrollTop: $("#divCurrencyTbl").offset().top}, 0050);
}

function getCommonParam() {
	var txtName = processSearchName($("#txtName").val());
	var txtDescription = processSearchName($("#txtDescription").val());
	var txtSign = processSearchName($("#txtSign").val());
	var status = $("#selectStatus").val();
	return "?name="+txtName+"&description="+txtDescription+"&sign="+txtSign+"&status="+status;
}

function search() {
	doSearch ("divCurrencyTbl", "/admin/currency/search"+getCommonParam()+"&pageNumber=1");
}

</script>
<style>
.hide {
	display: none;
}
</style>
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
			<tr class="hide">
				<td width="15%" class="title">Sign</td>
				<td><input type="text" id="txtSign" class="inputSmall" ></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="search();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divCurrencyTbl">
		<%@ include file="CurrencyTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddCurrency" value="Add" onclick="addCurrency();"></input>
		<input type="button" id="btnEditCurrency" value="Edit" onclick="editCurrency();"></input>
	</div>
	<br>
	<br>
	<div id="divCurrencyForm" style="margin-top: 20px;">
	</div>
</body>
</html>