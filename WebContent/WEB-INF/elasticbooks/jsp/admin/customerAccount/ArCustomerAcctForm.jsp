<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR Customer Account Form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<style>
.hidden {
	display:none;
}
</style>
<script type="text/javascript">
var noValue = 0;
var COMPANY_ID = "${arCustomerAcct.companyId}";
var DIVISION_ID = "${arCustomerAcct.debitDivisionId}";
var ACCOUNT_ID = "${arCustomerAcct.debitAccountId}";
$(document).ready(function() {
	if("${acctCombi.id}" != 0) {
		DIVISION_ID = "${acctCombi.divisionId}";
		ACCOUNT_ID = "${acctCombi.accountId}";
		var arLineId = "${transLineId}" == null ? 0 : "${transLineId}";
		loadDivisions(COMPANY_ID, DIVISION_ID, ACCOUNT_ID, arLineId);
	} else {
		var companyId = getCompany();
		//No Id for division, account and arLine for initial loading.
		loadDivisions(companyId, DIVISION_ID, ACCOUNT_ID, noValue);
	}
	if("${arCustomerAcct.defaultWithdrawalSlipACId}" != 0) {
		var wdDivisionId = "${arCustomerAcct.withdrawalSlipDivisionId}";
		var wdAccountId = "${arCustomerAcct.withdrawalAccountId}";
		loadWdDivisions(wdDivisionId, wdAccountId);
	} else {
		loadWdDivisions();
	}
});

function loadWdDivisions(divisionId, accountId) {
	var companyId = getCompany();
	divisionId = (divisionId == null || divisionId == "") ? 0 : divisionId;
	var uri = contextPath+"/getDivisions?companyId="+companyId+"&divisionId="+divisionId;
	$("#withdrawalSlipDivisionId").empty();
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
					$("#withdrawalSlipDivisionId").val(divisionId).attr("selected", true);
				loadWdAccounts(divisionId, accountId);
			}
	};
	loadPopulateObject(uri, false, "withdrawalSlipDivisionId",
			$("#withdrawalSlipDivisionId"), optionParser, postHandler, false, true);
}

function loadWdAccounts(divisionId, accountId) {
	var companyId = getCompany();
	divisionId = divisionId == null  || divisionId == 0 ? $("#withdrawalSlipDivisionId option:selected").val() : divisionId;
	accountId = (accountId == null || accountId == "") ? 0 : accountId;
	if(divisionId != "") {
		var uri = contextPath+"/admin/arCustomerAccount/loadAccounts?companyId="+companyId+"&divisionId="
				+divisionId+"&accountId="+accountId;
		$("#withdrawalAccountId").empty();
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
						$("#withdrawalAccountId").val(accountId).attr("selected", true);
				}
		};
		loadPopulateObject(uri, false, "withdrawalAccountId",
				$("#withdrawalAccountId"), optionParser, postHandler, false, true);
	}
}

</script>
<title>AR Customer Account Form</title>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="arCustomerAcct" id="arCustomerAcctFormId">
		<div class="modFormLabel">AR Customer Account</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>Account Information</legend>
				<table class="formTable" >
					<form:hidden path="id"/>
					<form:hidden path="createdBy"/>
					<form:hidden path="createdDate"/>
					<form:hidden path="ebObjectId"/>
					<tr>
						<td class="labels">Active: </td>
						<td class="value"><form:checkbox path="active"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="active" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">* AR Customers</td>
						<td class="value">
							<form:select path="arCustomerId" class="frmSelectClass">
								<form:options items="${arCustomers}" itemLabel="numberAndName" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="arCustomerId" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">* Company</td>
						<td class="value">
							<c:choose>
								<c:when test="${arCustomerAcct.id == 0}">
									<form:select path="companyId" id="companyId" cssClass="frmSelectClass"  onchange="loadDivisions();">
									<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
								</form:select>
								</c:when>
								<c:otherwise>
									<c:forEach var="company" items="${companies}">
										<c:if test="${company.id == arCustomerAcct.companyId}">
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
						<td class="labels">* Account Name</td>
						<td class="value"><form:input path="name" class="input" id="name"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="name" cssClass="error" style="margin-left: 12px;"/></td>
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
				</table>
			</fieldset>
			<fieldset class="frmField_set">
			<form:hidden path="defaultDebitACId"/>
				<legend>* Debit Account</legend>
				<table>
					<tr>
						<td class="labels">Division</td>
						<td class="value">
							<form:select path="debitDivisionId" id="divisionId" class="frmSelectClass" onchange="loadAccounts();"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="debitDivisionId" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td class="labels">Account</td>
						<td class="value">
							<form:select path="debitAccountId" id="accountId" class="frmSelectClass"></form:select>
						</td>
					</tr>
						<tr>
						<td></td>
						<td colspan="2"><form:errors path="debitAccountId" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="defaultDebitACId" cssClass="error" style="margin-left: 12px;"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set hidden">
				<legend>Default AR Transaction Line</legend>
				<table>
					<tr>
						<td class="labels">AR Line</td>
						<td class="value">
							<form:select path="defaultTransactionLineId" id="transLineId" class="frmSelectClass"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2"><form:errors path="defaultTransactionLineId" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set hidden">
				<form:hidden path="defaultWithdrawalSlipACId"/>
				<legend>Default Withdrawal Slip Account</legend>
				<table>
					<tr>
						<td class="labels"> Division</td>
						<td class="value">
							<form:select path="withdrawalSlipDivisionId" id="withdrawalSlipDivisionId" class="frmSelectClass"
								onchange="loadWdAccounts();"></form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="withdrawalSlipDivisionId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels"> Account</td>
						<td class="value">
							<form:select path="withdrawalAccountId" id="withdrawalAccountId"
								class="frmSelectClass"></form:select>
						</td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="withdrawalAccountId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels"></td>
						<td class="value"><form:errors path="defaultWithdrawalSlipACId" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table class="buttonClss">
				<tr >
					<td align="right" >
						<input type="button" id="btnSaveCustomerAcct" value="${arCustomerAcct.id eq 0 ? 'Save' : 'Update'}" onclick="saveCustomerAccount();"/>
						<input type="button" id="btnCancelCustomerAcct" value="Cancel" onclick="cancelCustomerAcct();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>