<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Account form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<title>Account Form</title>
<script type="text/javascript">
var containsAccountType = false;

$(document).ready (function () {
	$("#trRelatedAccount").hide();
	loadRelatedAccount ();
	if (Number("${account.id}") != 0) {
		var value = "${account.parentAccount.accountName}";
		$("#txtParentAccount").val(value);
		hideOrShowAccountType(value);
	}
});

$(function () {
	$("#accountTypeId").change(function () {
		loadRelatedAccount ();
	});

	$("#selectRelatedAccounts").change(function () {
		$("#relatedAccountId").val($(this).val());
	});
});

function loadRelatedAccount () {
	var accountId =  $("#id").val();
	var accountTypeId = $("#accountTypeId").val();
	$.ajax({ 
		url: contextPath+"/admin/accounts?accountTypeId="+accountTypeId+"&accountId="+accountId,
		success : function (responseText) {
			if (responseText.length > 0) {
				$("#selectRelatedAccounts").empty();
				for (var i=0; i<responseText.length; i++) 
					$("#selectRelatedAccounts").append("<option value='"+responseText[i]["id"]+"'>"+
							responseText[i]["accountName"]+"</option>");
				containsAccountType = true;
				$("#trRelatedAccount").show();
				$("#relatedAccountId").val($("#selectRelatedAccounts").val());
			} else {
				$("#trRelatedAccount").hide();
				$("#relatedAccountId").val(0);
				containsAccountType = false;
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json",
	});
}

function loadParentAccounts () {
	var uri = contextPath + "/getAccounts/getParentAccounts?numberOrName="+encodeURIComponent($("#txtParentAccount").val());
	var accountId = $("#id").val();
	if (accountId != "" && accountId != "0") {
		uri += "&accountId="+accountId;
	}
	hideOrShowAccountType($("#txtParentAccount").val());
	$("#txtParentAccount").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#parentAccountId").val(ui.item.id);
			$(this).val(ui.item.accountName);
			$("#trAcctType").hide();
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.accountName);
					} else {
						$("#parentAccountId").val("");
					}
				},
				error : function(error) {
					$("#trAcctType").show();
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number + " - " + item.accountName + "</a>" )
			.appendTo( ul );
	};
}

function getParentAccount () {
	var acctName = encodeURIComponent($.trim($("#txtParentAccount").val()));
	if (acctName != "") {
		$.ajax({
			url: contextPath + "/getAccounts/byExactName?accountName="+acctName,
			success : function(account) {
				$("#spanPAError").text("");
				if (account != null) {
					$("#parentAccountId").val(account.id);
					$("#txtParentAccount").val(account.accountName);
					if ($("#errParentAccountId").text().includes("Invalid parent account.")) {
						$("#errParentAccountId").text("");
					}
				}else{
					$("#parentAccountId").val("");
					checkPAError() 
				}
			},
			error : function(error) {
				checkPAError() ;
			},
			dataType: "json"
		});
	}
}

function checkPAError() {
	if (!$("#errParentAccountId").text().includes("Invalid parent account.")) {
		$("#spanPAError").text("Invalid parent account.");
	}
}


</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="account" >
		<div class="modFormLabel">&nbsp; Account</div>
		<div class="modFormLine"> </div>
		<br>
		<div class="modForm">
		<div class="information">* Required fields</div>
		<table class="formTable">
			<form:hidden path="id"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="relatedAccountId" />
			<form:hidden path="serviceLeaseKeyId"/>
			<form:hidden path="ebObjectId"/>
			<form:hidden path="parentAccountId"/>
			<form:hidden path="parentAccountName"/>
			<tr>
				<td>*Account Number</td>
				<td><form:input path="number" size="10" maxLength="10" id="accountName" class="input"/></td>
			</tr>
			<tr>
				<td></td>
				<td class="value" style="padding-left: 0;">
					<form:errors path="number" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td>*Account Name</td>
				<td><form:input path="accountName" size="25" maxLength="100" class="input"/></td>
			</tr>
			<tr>
				<td></td>
				<td class="value" style="padding-left: 0;">
					<form:errors path="accountName" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td valign="top">Description</td>
				<td><form:textarea path="description" maxlength="200" cssClass="addressClass"/></td>
			</tr>
			<tr>
				<td></td>
				<td class="value" style="padding-left: 0;">
					<form:errors path="description" cssClass="error"/>
				</td>
			</tr>
			<tr id="trAcctType">
				<td>Account Type</td>
				<td>
					<form:select path="accountTypeId" cssClass="frmSelectClass" >
						<form:options items="${accountTypes}" itemLabel="name" itemValue="id"/>
					</form:select>
				</td>
			</tr>
			<tr>
				<td valign="top">Parent Account</td>
				<td>
					<input id="txtParentAccount" onkeydown="loadParentAccounts();" onkeyup="loadParentAccounts();" onblur="getParentAccount();" class="input"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td class="value" style="padding-left: 0;">
					<span id="spanPAError" class="error"></span>
					<form:errors path="parentAccountId" id="errParentAccountId" cssClass="error"/>
				</td>
			</tr>
			<tr id="trRelatedAccount">
				<td>Related Account</td>
				<td>
					<select id="selectRelatedAccounts"></select>
				</td>
			</tr>
			<tr>
				<td>Active: </td>
				<td>
					<form:checkbox path="active"/>
				</td>
			</tr>
		</table>
		<div class="controls">
			<input type="button" id="btnSaveAccount" value="${account.id eq 0 ? 'Save' : 'Update'}" onclick="saveAccount();" />
			<input type="button" id="btnCancelAccount" value="Cancel" onclick="cancelForm();"/>
		</div>
		</div>
	</form:form>
</div>
</body>
</html>