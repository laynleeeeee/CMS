<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>

		Description: Deduction type main page form
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
$(function () {
	$("#txtName, #slctStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			searchDT();
			e.preventDefault();
		}
	});
});

function addDT() {
	$("#divDeductionTypeForm").load(contextPath + "/admin/deductionType/form");
	$("html, body").animate({scrollTop: $("#divDeductionTypeForm").offset().top}, 0050);
}

function editDT() {
	var id = getCheckedId("cbDeductionType");
	$("#divDeductionTypeForm").load(contextPath + "/admin/deductionType/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divDeductionTypeForm").offset().top}, 0050);
}

var isSaving = false;
function saveDT() {
	if(isSaving == false) {
		isSaving = true;
		$("#btnSave").attr("disabled", "disabled");
		doPostWithCallBack ("deductionTypeId", "divDeductionTypeForm", function(data) {
			if (data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "deduction type: " + $("#name").val() + ".");
				searchDT();
				$("#divDeductionTypeForm").html("");
			} else {
				$("#divDeductionTypeForm").html(data);
			}
			isSaving = false;
		});
	}
}

function cancelDT() {
	$("#divDeductionTypeForm").html("");
	$("html, body").animate({scrollTop: $("#divDeductionTypeTbl").offset().top}, 0050);
}

function getCommonParam() {
	var txtName = $.trim(processSearchName($("#txtName").val()));
	var status = $("#slctStatus").val();
	return "?name="+txtName+"&status="+status;
}

function searchDT() {
	doSearch ("divDeductionTypeTbl", "/admin/deductionType/search"+getCommonParam()+"&pageNumber=1");
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Deduction Type</td>
				<td><input type="text" id="txtName" class="inputSmall" ></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="slctStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchDT();"/>
				</td>
			</tr>
			<tr>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divDeductionTypeTbl">
		<%@ include file="DeductionTypeTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAdd" value="Add" onclick="addDT();"></input>
		<input type="button" id="btnEdit" value="Edit" onclick="editDT();"></input>
	</div>
	<div id="divDeductionTypeForm" style="margin-top: 50px;"></div>
</body>
</html>