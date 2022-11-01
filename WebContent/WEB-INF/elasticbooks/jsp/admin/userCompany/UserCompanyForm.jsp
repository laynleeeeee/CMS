<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../include.jsp"%>
<!--

	Description: User Company Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ybl/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript">
var $companyTableObj = null;
var currentCompany = null;
var $variable = null;
var initUserId = 0;
var initUserCompanyHeadId = 0;
$(document).ready (function () {
	initializeTable();
	initUserCompanyHeadId = "${userCompanyHead.id}";
	if("${userCompanyHead.id}" != 0){
		initUserId = "${userCompanyHead.usrId}";
	}
});

function initializeTable () {
	var companyJson = JSON.parse($("#userCompaniesJsonId").val());
	var cPath = "${pageContext.request.contextPath}";
	$companyTableObj = $("#userCompanyTable").editableItem({
		data: companyJson,
		jsonProperties: [
						{"name" : "id", "varType" : "int"},
						{"name" : "userCompanyId", "varType" : "int"},
						{"name" : "companyId", "varType" : "int"},
						{"name" : "companyName", "varType" : "string"},
						{"name" : "active", "varType" : "boolean"},
		],
		contextPath: cPath,
		header:[
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "userCompanyId",
					"cls" : "userCompanyId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "companyId",
					"cls" : "companyId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Company",
					"cls" : "companyName tblInputText",
					"editor" : "text",
					"visible" : true ,
					"width" : "50%"},
				],
	"disableDuplicateStockCode" : false
	});

	$("#userCompanyTable").on("keydown keyup", ".companyName", function(){
		showCompanies(this);
	});

	$("#userCompanyTable").on("blur", ".companyName", function(){
		showCompanies(this);
	});

	$("#userCompanyTable").on('click', '.delrow', function (){
		var rowCount = $("#userCompanyTable").find("tbody tr").length;
		if (rowCount <= 1){
			var $companyId = $(this).closest("tr").find(".companyId");
			$companyId.val("");
			var $companyName = $(this).closest("tr").find(".companyName");
			$companyName.val("");
			return;
		}
		return false;
	});
}

function showCompanies ($txtbox) {
	var uri = contextPath + "/getCompany/byName?companyName="+$.trim($($txtbox).val()
			+ "&isActiveOnly=true&limit=10");
	$($txtbox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			var currentCompany = ui.item;
			$(this).val(ui.item.name);
			processcompanyName($txtbox, currentCompany);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			var comp = $.trim($(this).val());
			var company = ui.item;
			if (company != null && comp == company.name){
				return false;
			}
			if(comp != "") {
				processcompany($txtbox, currentCompany);
			} else {
				$($txtbox).closest("tr").find(".companyId").val("");
			}
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name +"</a>" )
			.appendTo( ul );
	};
}

function processcompanyName($txtbox, currentCompany) {
	var $companyId = $($txtbox).closest("tr").find(".companyId");
	var $companyName = $($txtbox).closest("tr").find(".companyName");
	if (currentCompany == undefined) {
		$companyId.val("");
		$companyName.html("");
		return;
	}
	$companyId.val(currentCompany.id);
	$($txtbox).val(currentCompany.name);
	$companyName.html(currentCompany.companyName);
}

function clearValues($txtbox, classId, $variable, isText) {
	var $variable = $($txtbox).closest("tr").find("."+classId);
	console.log(isText);
	if(isText) {
		$variable.val("");
	}else{
		$variable.html("");
	}
}

function showUsers () {
	var uri = contextPath + "/admin/userCompany/byUserName?userName="+$("#txtUser").val()
			+"&userId="+initUserId;
	$("#txtUser").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#userId").val(ui.item.id);
			$(this).val(ui.item.username);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.username);
					}
				},
				error : function(error) {
					$("#txtUser").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + item.username + "</a>" )
			.appendTo( ul );
	};
}

function getUser () {
	var userName = $.trim($("#txtUser").val());
	if (userName != "") {
		$.ajax({
			url: contextPath + "/admin/userCompany/byExactUserName?userName="+userName+"&userId="+initUserId,
			success : function(user) {
				$("#spanUserNameError").text("");
				if (userName != null && typeof userName != "") {
					$("#usrId").val(user.id);
					$("#firstName").html(user.firstName);
					$("#lastName").html(user.lastName);
				}
			},
			error : function(error) {
				$("#spanUserNameError").text("Invalid user.");
				$("#userNameError").text("");
				$("#usrId").val("");
				$("#firstName").html("");
				$("#lastName").html("");
			},
			dataType: "json"
		});
	} else {
		$("#usrId").val("");
	}
}

function cancelForm() {
	$("#divUserCompanyForm").html("");
}

function saveUserCompany() {
	if ($("#spanUserNameError").text() == "") {
		$("#userCompaniesJsonId").val($companyTableObj.getData());
		$("#btnSaveUserCompany").attr("disabled", "disabled");
		if($("#usrId").val() == null || $.trim($("#txtUser").val()) == ""){
			$("#usrId").val(initUserId);
		}
		$("#hndUserCompanyHeadId").val(initUserCompanyHeadId);
			doPostWithCallBack ("userCompanyForm", "form", function (data) {
				if (data.startsWith("saved")) {
					$("#spanMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "user companies of " 
							+ $("#txtUser").val() + ".");
					searchUserCompany();
					dojo.byId("divUserCompanyForm").innerHTML = "";
					isSaving = false;
				} else {
					$("#divUserCompanyForm").html(data);
					isSaving = false;
				}
			});
			$("#btnSaveUserCompany").removeAttr("disabled");
	}
}

</script>
<title>User Company</title>
</head>
<body>
<div class="formDiv">
		<form:form method="POST" commandName="userCompanyHead" id="userCompanyForm">
			<form:hidden path="id" id="id"/>
			<form:hidden path="usrId" id="usrId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="userCompaniesJson" id="userCompaniesJsonId"/>
			<div class="modFormLabel">User Company</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>* Basic Information</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* User Name</td>
							<td class="value">
								<form:input path="userName" cssClass="input" id="txtUser" onkeydown="showUsers();" 
									onkeyup="showUsers();" onblur="getUser();" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2" style="text-indent: 15px;">
								<span id="spanUserNameError" class="error"></span>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2" style="text-indent: 15px;">
								<form:errors path="userName" cssClass="error" id="userNameError"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Active:</td>
							<td class="value">
								<form:checkbox path="active"/>
							</td>
						</tr>
					</table>
					<br>
					<div id="userCompanyTable"></div>
						<form:errors path="errorMessage" cssClass="error" id="errorMessage" />
				</fieldset>
				<table class="formDiv">
					<tr>
						<td></td>
						<td align="right">
							<input type="button" id="btnSaveUserCompany" value="${userCompanyHead.id eq 0 ? 'Save' : 'Update'}" onclick="saveUserCompany();" />
							<input type="button" id="btnCancelUserCompany" value="Cancel" onclick="cancelForm();" /></td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
	<hr class="thin" />
</body>
</html>