<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Bank account form.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
//Set the cash in bank default division and account.
var selectedCBADivisionValue = "${bankAccount.inBADivisionId}";
var selectedCBAAccountValue = "${bankAccount.inBAAccountId}";
$(document).ready(function (){
	filterDivisions();
});

function filterDivisions() {
	var companyId = $("#companyId option:selected").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId;
	var selectIds = new Array ();
	selectIds.push ("cbaDivisionId");
	for (var i = 0; i < selectIds.length; i++ ){
		var selectId = selectIds[i];
		clearAndAddEmpty (selectId);
	}
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["number"] +" - " +rowObject["name"];
		}
	};
	var postHandler = {
			doPost: function(data) {
				addSavedInactiveDiv($("#cbaDivisionId"), selectedCBADivisionValue);
				filterAccountByCbaDivision();
			}
	};
	loadPopulateMultiSelect (uri, false, null, selectIds, optionParser, postHandler);
}

function addSavedInactiveDiv ($select, selectedValue) {
	var uri = contextPath+"/getDivisions/specific?divisionId="+selectedValue;
	$.ajax({
		url: uri,
		async: false,
		success : function (div) {
			if (div != null) {
				if(!div.active) {
					$($select).append("<option value="+selectedValue+">"+div.number+" - "+div.name+"</option>");
				}
			}
			$($select).val(selectedValue).attr('selected',true);
		},
		error : function (error) {
			console.log(error);
		},
		dataType: "json",
	});
}

function clearAndAddEmpty (selectId) {
	$("#"+selectId).empty();
	var option = "<option selected='selected' value='0'></option>";
	$("#"+selectId).append(option);
}

function filterAccountByCbaDivision() {
	var selectAccountIds = new Array ();
	selectAccountIds.push ("cbaAcctId");
	var divisionId = $("#cbaDivisionId option:selected").val();
	filterAccount (selectAccountIds, divisionId, selectedCBAAccountValue);
}

function filterAccount (selectIds, divisionId, selectedValue) {
	if (divisionId == null)
		return;
	for (var i = 0; i < selectIds.length; i++ ){
		var selectId = selectIds[i];
		clearAndAddEmpty (selectId);
}
	var companyId = $("#companyId option:selected").val();
	var uri = contextPath+"/getAccounts?companyId="+companyId+"&divisionId="+divisionId+
			(typeof selectedValue != "undefined" ? "&accountId="+selectedValue : "");
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},
		getLabel: function (rowObject) {
			return rowObject["number"] +" - " +rowObject["accountName"];
		}
	};
	var postHandler = {
		doPost: function(data) {
			for (var i = 0; i < selectIds.length; i++ ){
				var selectId = selectIds[i];
				$("#"+selectId).val(selectedValue).attr('selected',true);
			}
		}
	};
	loadPopulateMultiSelect (uri, false, null, selectIds, optionParser, postHandler);
}
</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="bankAccount">
		<div class="modFormLabel">Bank Accounts</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>* Account Info</legend>
					<table class="formTable" >
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<tr>
							<td class="labels">Bank Name</td>
							<td class="value">
								<form:select path="bankId" class="frmSelectClass">
									<form:options items="${banks}" itemLabel="name" itemValue="id"/>
								</form:select>
							</td>
						</tr>
						<tr >
							<td class="labels"></td>
							<td class="value">
								<form:errors path="bankId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Bank Account Name</td>
							<td class="value"><form:input path="name" class="input" id="bankAcctName"/></td>
						</tr>
						<tr >
							<td class="labels"></td>
							<td class="value">
								<form:errors path="name" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Bank Account No.</td>
							<td class="value"><form:input path="bankAccountNo" class="input" id="bankAccountNo"/></td>
						</tr>
						<tr >
							<td class="labels"></td>
							<td class="value">
								<form:errors path="bankAccountNo" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Company</td>
							<td class="value">
								<form:select path="companyId" class="frmSelectClass" onchange="filterDivisions();">
									<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
								</form:select>
							</td>
						</tr>
						<tr >
							<td class="labels"></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value">
								<form:checkbox path="active"/>
							</td>
						</tr>
						<tr >
							<td class="labels"></td>
							<td class="value">
								<form:errors path="active" cssClass="error"/>
							</td>
						</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
			<form:hidden path="cashInBankAcctId"/>
				<legend>* Cash in Bank Account</legend>
				<table>
					<tr>
						<td class="labels">Division</td>
						<td class="value">
							<form:select path="inBADivisionId" id="cbaDivisionId"
								class="frmSelectClass" onchange="filterAccountByCbaDivision();">
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="inBADivisionId" cssClass="error"/>
						</td>
					</tr>

					<tr>
						<td class="labels">Account</td>
						<td class="value">
							<form:select path="inBAAccountId" id="cbaAcctId"
								class="frmSelectClass"></form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value">
							<form:errors path="cashInBankAcctId" cssClass="error"/>
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table class="buttonClss">
				<tr >
					<td align="right" >
						<input type="button" id="btnSaveBankAcct" value="${bankAccount.id eq 0 ? 'Save' : 'Update'}" onclick="saveBankAcct();"/>
						<input type="button" id="btnCancelAcct" value="Cancel" onclick="cancelForm();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>