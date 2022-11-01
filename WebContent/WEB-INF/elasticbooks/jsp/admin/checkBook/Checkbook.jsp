<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Elasticbooks Checkbook page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Bank Account</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
function numericInputOnly() {
	$("#checkNo").bind("keyup keydown", function(e) {
		// 46 = DELETE button. If Delete button is not pressed
		if(e.keyCode != 46){ 
			inputOnlyNumeric("checkNo");
		}
	});
	$("#checkbookNoFrom").bind("keyup keydown", function(e) {
		// 46 = DELETE button. If Delete button is not pressed
		if(e.keyCode != 46){ 
			inputOnlyNumeric("checkbookNoFrom");
		}
	});
	$("#checkbookNoTo").bind("keyup keydown", function(e) {
		// 46 = DELETE button. If Delete button is not pressed
		if(e.keyCode != 46){ 
			inputOnlyNumeric("checkbookNoTo");
		}
	});
}

function saveCheckbook(){
	if ($.trim($("#checkbookNoFrom").val()) == "")
		$("#checkbookNoFrom").val("0");
	if ($.trim($("#checkbookNoTo").val()) == "")
		$("#checkbookNoTo").val("0");
	doPostWithCallBack ("checkbookFormId", "checkbookForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanCheckbookMessage").html(
					"Successfully " + ($("#id").val() != 0 ? "updated " : "added ")
					+ "Check book " + $("#checkbookName").val() + ".");
			$("#checkbookForm").html("");
			searchCheckbooks();
		} else {
			$("#checkbookForm").html(data);
		}
	});
}

$(function() {
	$("#btnAddCheckbook").click(function() {
		$("#checkbookForm").load(contextPath + "/admin/checkbook/form", 
			function (){
				$("html, body").animate({scrollTop: $("#checkbookForm").offset().top}, 0050);
		});
	});

	$("#btnEditCheckbook").click(function() {
		var id = getCheckedId("cbCheckbook");
		$("#checkbookForm").load(contextPath + "/admin/checkbook/form?checkbookId=" +id, 
			function (){
				$("html, body").animate({scrollTop: $("#checkbookForm").offset().top}, 0050);
		});
	});

	$("#btnSearchCheckbook").click(function(){
		searchCheckbooks();
	});

	$("#btnCancelCheckbookForm").live("click", function(){
		$("bankAccountForm").html("");
		searchCheckbooks();
	});

	$("#name, #bankAccount, #checkNo, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchCheckbooks();
			e.preventDefault();
		}
	});
});

function getCommonParam(){
	var bankAccount = processSearchName($("#bankAccount").val());
	var name = processSearchName($("#name").val());
	var checkNo = processSearchName($("#checkNo").val());
	var status = $("#selectStatus").val();
	return "?bankAccountName="+bankAccount+"&name="+name+"&checkNo="+checkNo+"&status="+status;
}

function searchCheckbooks(){
	var url = "/admin/checkbook" + getCommonParam() +"&pageNumber=1";
	doSearch ("divCheckbookTable", url);
}

function cancelCheckbook() {
	dojo.byId("checkbookForm").innerHTML = "";
	searchCheckbooks();
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="tableInfo">
			<tr>
				<td width="10%" class="title">Bank Account:</td>
				<td><input type="text" id="bankAccount" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="10%" class="title">Name:</td>
				<td><input type="text" id="name" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="10%" class="title">Checkbook No.:</td>
				<td>
					<input type="text" id="checkNo" class="inputSmall">
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td>
					<select id="selectStatus" class="frmSmallSelectClass">
						<option value="-1">ALL</option>
						<option value="1">Active</option>
						<option value="0">Inactive</option>
					</select>
					<input type="button" id="btnSearchCheckbook" value="Search" />
				</td>
			</tr>
		</table>
	</div>
	<div id="veil"></div>
	<span id="spanCheckbookMessage" class="message"></span>
	<div id="divCheckbookTable">
		<%@ include file="CheckbookTable.jsp" %>
	</div>
	<div class="controls" align="right">
		<input type="button" id="btnAddCheckbook"  value="Add" />
		<input type="button" id="btnEditCheckbook" value="Edit" />
	</div>
	<br /> <br />
	<div id="checkbookForm" style="margin-top: 20px;">
	</div>
</body>
</html>