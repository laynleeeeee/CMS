<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Custodian Account Form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
var COMPANY_ID = "${custodianAccount.companyId}";
var DIVISION_ID = "${custodianAccount.cdDivisionId}";
var ACCOUNT_ID = "${custodianAccount.cdAccountId}";
var DIVISION_ID2 = "${custodianAccount.fdDivisionId}";
var ACCOUNT_ID2 = "${custodianAccount.fdAccountId}";
$(document).ready(function() {
	if ("${custodianAccount.id}" != 0) {
		$("#txtCustodianName").attr("disabled", "disabled");
	}
	if ("${acctCombi.id}" != 0) {
		DIVISION_ID = "${acctCombi.divisionId}";
		ACCOUNT_ID = "${acctCombi.accountId}";
		loadDivisions(COMPANY_ID, DIVISION_ID, ACCOUNT_ID);
	} else {
		var companyId = getCompany();
		loadDivisions(companyId, DIVISION_ID, ACCOUNT_ID);
	}
	if ("${custodianAccount.fdAccountCombinationId}" != 0) {
		var wdDivisionId = "${custodianAccount.fdDivisionId}";
		var wdAccountId = "${custodianAccount.fdAccountId}";
		loadWdDivisions(wdDivisionId, wdAccountId);
	} else {
		loadWdDivisions(DIVISION_ID2,ACCOUNT_ID2);
	}
});

function loadWdDivisions(divisionId, accountId) {
	var companyId = getCompany();
	divisionId = (divisionId == null || divisionId == "") ? 0 : divisionId;
	var uri = contextPath+"/getDivisions?companyId="+companyId+"&divisionId="+divisionId;
	$("#fdDivisionId").empty();
	var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["numberAndName"];
			}
		};
	var postHandler = {
			doPost: function(data) {
				if(divisionId != 0 && divisionId != "undefined")
					$("#fdDivisionId").val(divisionId).attr("selected", true);
				loadWdAccounts(divisionId, accountId);
			}
	};
	loadPopulate (uri, false, null, "fdDivisionId", optionParser, postHandler);
}

function loadWdAccounts(divisionId, accountId) {
	var companyId = getCompany();
	divisionId = divisionId == null  || divisionId == 0 ? $("#fdDivisionId option:selected").val() : divisionId;
	accountId = (accountId == null || accountId == "") ? 0 : accountId;
	if(divisionId != "") {
		var uri = contextPath+"/admin/custodianAccount/loadAccounts?companyId="+companyId+"&divisionId="
				+divisionId+"&accountId="+accountId;
		$("#fdAccountId").empty();
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
					if(accountId != 0)
						$("#fdAccountId").val(accountId).attr("selected", true);
				}
		};
		loadPopulate (uri, false, null, "fdAccountId", optionParser, postHandler);
	}
}

</script>
<title>Custodian Account Form</title>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="custodianAccount" id="custodianAccountFormId">
		<div class="modFormLabel">Custodian Account</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>Account Info</legend>
				<table class="formTable" >
					<form:hidden path="id"/>
					<form:hidden path="createdBy"/>
					<form:hidden path="createdDate"/>
					<tr>
						<td class="labels">* Custodian Name</td>
						<td class="value"><form:input path="custodianName" class="input" id="txtCustodianName"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="custodianName" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">* Company</td>
						<td class="value">
							<c:choose>
								<c:when test="${custodianAccount.id == 0}">
									<form:select path="companyId" id="companyId" cssClass="frmSelectClass"  onchange="loadDivisions();">
									<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
								</form:select>
								</c:when>
								<c:otherwise>
									<c:forEach var="company" items="${companies}">
										<c:if test="${company.id == custodianAccount.companyId}">
											${company.numberAndName}
											<form:hidden path="companyId"/> 
										</c:if>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="companyId" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">* Custodian Account</td>
						<td class="value"><form:input path="custodianAccountName" class="input"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="custodianAccountName" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">* Term</td>
						<td class="value">
							<form:select path="termId" cssClass="frmSelectClass">
								<form:options items="${terms}" itemLabel="name" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="termId" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">Active: </td>
						<td class="value"><form:checkbox path="active"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="active" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
			<form:hidden path="cdAccountCombinationId"/>
				<legend>* Custodian Default Account</legend>
				<table>
					<tr>
						<td class="labels">Cost Center</td>
						<td class="value">
							<form:select path="cdDivisionId" id="divisionId" class="frmSelectClass" onchange="loadAccounts();"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="cdDivisionId" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">Account</td>
						<td class="value">
							<form:select path="cdAccountId" id="accountId" class="frmSelectClass"></form:select>
						</td>
					</tr>
						<tr>
						<td></td>
						<td colspan="2"><form:errors path="cdAccountId" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="cdAccountCombinationId" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<form:hidden path="fdAccountCombinationId"/>
				<legend>* Fund Default Account</legend>
				<table>
					<tr>
						<td class="labels">Cost Center</td>
						<td class="value">
							<form:select path="fdDivisionId" id="fdDivisionId" class="frmSelectClass"
								onchange="loadWdAccounts();"></form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="fdDivisionId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Account</td>
						<td class="value">
							<form:select path="fdAccountId" id="fdAccountId"
								class="frmSelectClass"></form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="fdAccountId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="fdAccountCombinationId" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table class="buttonClss">
				<tr >
					<td align="right" >
						<input type="button" id="btnSaveCustodianAccount" value="${custodianAccount.id eq 0 ? 'Save' : 'Update'}" onclick="saveCustodianAccount();"/>
						<input type="button" id="btnCancelCustodianAccount" value="Cancel" onclick="cancelCustodianAccount();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>