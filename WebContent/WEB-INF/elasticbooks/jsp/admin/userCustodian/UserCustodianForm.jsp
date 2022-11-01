<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: User custodian Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript">
var $userCustodianObj = null;
$(document).ready (function () {
	initializeTable();
	if ("${userCustodian.id}" == 0) {
	} else {
		$("#companyId").val("${userCustodian.companyId}");
		$("#divisionId").val("${userCustodian.divisionId}");
	}
});

function showUsers($txtbox) {
	$("#errorMessage").text("");
	var uri = contextPath + "/getUsers";
	$($txtbox).autocomplete({
		source: uri,
		select: function(event, ui) {
			$(this).val(ui.item.firstName + " " +ui.item.lastName);
			return false;
		}, minLength: 2,
		change: function(event, ui) {
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.name);
					}
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.firstName + " " +item.lastName+"</a>" )
			.appendTo( ul );
	};
}

function getUser(elem) {
	var $row = $(elem).closest("tr");
	$("#errorMessage").text("");
	var userName = $.trim($(elem).val());
	if (userName != "") {
		$.ajax({
			url: contextPath+"/getUsers/byExactName?name="+processSearchName(userName),
			success : function(user) {
				var userId = "";
				if (user != null && user != undefined) {
					$row.find(".userId").val(user.id);
				} else {
					$row.find(".userId").val("");
				}
			},
			error : function(error) {
				$row.find(".userId").val("");
			},
			dataType: "json"
		});
	} else {
		$row.find(".userId").val("");
	}
}

function initializeTable() {
	var userCustodianLines = JSON.parse($("#userCustodianLinesJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$userCustodianObj = $("#userCustodianLinesTable").editableItem({
		data: userCustodianLines,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "userId", "varType" : "int"},
			{"name" : "userName", "varType" : "string"},
		],
		contextPath: cPath,
		header:[
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "userId",
				"cls" : "userId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "User",
				"cls" : "userName tblInputText",
				"editor" : "text",
				"visible" : true ,
				"width" : "25%"},
		],
		"disableDuplicateStockCode" : false
	});

	$("#userCustodianLinesTable").on("keydown", ".userName", function(){
		showUsers(this);
	});

	$("#userCustodianLinesTable").on("blur", ".userName", function() {
		getUser(this);
	});
}

function cancelForm() {
	$("#divUserCustodianForm").html("");
}

var isSaving = false;
function saveUserCustodian() {
	if (!isSaving) {
		isSaving = true;
		$("#userCustodianLinesJson").val($userCustodianObj.getData());
		$("#btnSaveUserCustodian").attr("disabled", "disabled");
		doPostWithCallBack ("userCustodian", "divUserCustodianForm", function (data) {
			if (data.substring(0,5) == "saved") {
				$("#spanUserCustodianMsg").html("Successfully "
						+ ($("#id").val() != 0 ? "updated " : "added ")
						+ "User custodian of " + $("#txtCustodianAccountName").val() + ".");
				searchUserCustodian();
				dojo.byId("divUserCustodianForm").innerHTML = "";
			} else {
				var custodianAccount = $("#txtCustodianAccountName").val();
				initializeTable();
				$("#divUserCustodianForm").html(data);
				$("#txtCustodianAccountName").val(custodianAccount);
			}
			isSaving = false;
			$("#btnSaveUserCustodian").removeAttr("disabled");
		});
	}
	$("#errorMessage").text("");
}

function getCustodianAccount() {
	var custodianName = $.trim($("#txtCustodianAccountName").val());
	if (custodianName != "") {
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getCustodianAccounts/new?name="+processSearchName(custodianName)
				+"&companyId="+companyId+"&isExact=true";
		$.ajax({
			url: uri,
			success : function(custodianAccount) {
				$("#spanCustodianAccount").text("");
				if (custodianAccount != null && custodianAccount[0] != undefined) {
					$("#hndCustodianAccountId").val(custodianAccount[0].id);
					$("#txtCustodianAccountName").val(custodianAccount[0].custodianName);
				} else {
					$("#hndCustodianAccountId").val("");
					$("#cnameError").text("");
					$("#spanCustodianAccount").text("Invalid custodian account.");
				}
			},
			error : function(error) {
				$("#spanCustodianAccount").text("Invalid custodian account.");
			},
			dataType: "json"
		});
	}
}

function showCustodianAccounts() {
	var custodianName = $("#txtCustodianAccountName").val();
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var uri = contextPath + "/getCustodianAccounts/new?name="+processSearchName(custodianName)
			+"&companyId="+companyId+"&divisionId="+divisionId+"&isExact=false";
	$("#txtCustodianAccountName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hndCustodianAccountId").val(ui.item.id);
			$(this).val(ui.item.custodianName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.custodianName + "</a>" )
			.appendTo( ul );
	};
}

</script>
<title>User Custodian Form</title>
</head>
<body>
<div class="formDiv">
		<form:form method="POST" commandName="userCustodian">
			<form:hidden path="id" id="id"/>
			<form:hidden path="custodianAccountId" id="hndCustodianAccountId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="userCustodianLinesJson" id="userCustodianLinesJson"/>
			<div class="modFormLabel">User Custodian</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>* Basic Information</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<c:choose>
									<c:when test="${userCustodian.id eq 0 }">
										<form:select path="companyId" id="companyId" cssClass="frmSelectClass">
											<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
										</form:select>
									</c:when>
									<c:otherwise>
										<c:forEach var="company" items="${companies}">
											<c:if test="${company.id == userCustodian.companyId}">
												${company.name}
												<form:hidden path="companyId" id="companyId"/>
											</c:if>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2"><form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Division</td>
							<td class="value">
								<c:choose>
									<c:when test="${userCustodian.id ne 0}">
										${userCustodian.division.name}
										<form:hidden path="divisionId" id="divisionId"/>
									</c:when>
									<c:otherwise>
										<form:select path="divisionId" id="divisionId" class="frmSelectClass">
											<form:options items="${divisions}" itemLabel="numberAndName" itemValue="id"/>
										</form:select>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Custodian Name</td>
							<td class="value">
								<form:input path="custodianAccount.custodianName" cssClass="input" id="txtCustodianAccountName"
									onkeydown="showCustodianAccounts();" onblur="getCustodianAccount();"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanCustodianAccount" class="error"></span>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="custodianAccount.custodianName" cssClass="error" id="cnameError"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value">
								<form:checkbox path="active"/>
							</td>
						</tr>
					</table>
					<br>
					<div id="userCustodianLinesTable"></div>
					<form:errors path="errorMessage" cssClass="error" />
					<br>
					<span id="errorMessage" class ="error"></span>
				</fieldset>

				<table class="formDiv">
					<tr>
						<td></td>
						<td align="right">
							<input type="button" id="btnSaveUserCustodian" value="${userCustodian.id eq 0 ? 'Save' : 'Update'}" onclick="saveUserCustodian();" />
							<input type="button" id="btnCancelUserCustodian" value="Cancel" onclick="cancelUserCustodian();" /></td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
	<hr class="thin" />
</body>
</html>