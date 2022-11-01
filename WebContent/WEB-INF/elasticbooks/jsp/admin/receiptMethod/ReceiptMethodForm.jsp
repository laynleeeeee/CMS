<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--     

	Description: Receipt method form.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
var selectBankAccount = "${receiptMethod.bankAccountId}";
//Set the debit account default division and account.
var selectedDebitAcctDivisionValue = "${receiptMethod.dbACDivisionId}";
var selectedDebitAcctAccountValue = "${receiptMethod.dbACAccountId}";
//Set the credit account default division and account.
var selectedCreditAcctDivisionValue = "${receiptMethod.crACDivisionId}";
var selectedCreditAcctAccountValue = "${receiptMethod.crACAccountId}";

$(document).ready(function (){
	var dbDivisionId = Number("${receiptMethod.debitAcctCombination.division.id}");
	var crDivisionId = Number("${receiptMethod.creditAcctCombination.division.id}")
	filterBankAccounts();
	filterDivDACombi(dbDivisionId);
	filterDivCACombi(crDivisionId);
});

function filterDivDACombi(divisionId) {
	//divisionId will be reset to zero when company is changed.
	if (divisionId != 0) {
		divisionId = selectedDebitAcctDivisionValue;
	}
	filterDivisions(divisionId, "dbACDivisionId", true);
}

function filterDivCACombi(divisionId) {
	//divisionId will be reset to zero when company is changed.
	if (divisionId != 0) {
		divisionId = selectedCreditAcctDivisionValue;
	}
	filterDivisions(divisionId, "crACDivisionId", false);
}

function assignBankAccount (select) {
	selectBankAccount = $(select).val();
}
</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="receiptMethod" id="receiptMethodFormId">
		<div class="modFormLabel">Receipt Method</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>* Receipt Method Information</legend>
					<table class="formTable" >
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<tr>
							<td class="labels" valign="top">Name</td>
							<td class="value"><form:input path="name" id="rmName" maxlength="100" class="input"/></td>
						</tr>
						<tr >
							<td></td>
							<td >
								<font color="red">
									<form:errors path="name" cssClass="error" style="margin-left: 12px;"/>
								</font>
							</td>
						</tr>
						<tr>
							<td class="labels">Company</td>
							<td class="value">
								<form:select path="companyId" class="frmSelectClass" onchange="filterBankAccounts();
										filterDivDACombi(0); filterDivCACombi(0);">
									<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
								</form:select>
							</td>
						</tr>
						<tr >
							<td></td>
							<td>
								<font color="red">
									<form:errors path="companyId" cssClass="error" style="margin-left: 12px;"/>
								</font>
							</td>
						</tr>
						<tr>
							<td class="labels">Bank Account</td>
							<td class="value">
								<form:select path="bankAccountId" id="bankAccountId"
									class="frmSelectClass" onchange="assignBankAccount(this);">
								</form:select>
							</td>
						</tr>
						<tr >
							<td></td>
							<td>
								<font color="red">
									<form:errors path="bankAccountId" cssClass="error" style="margin-left: 12px;"/>
								</font>
							</td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value"><form:checkbox path="active"/></td>
						</tr>
						<tr >
							<td colspan="2">
								<font color="red">
									<form:errors path="active" cssClass="error" style="margin-left: 12px;"/>
								</font>
							</td>
						</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
			<form:hidden path="debitAcctCombinationId"/>
				<legend>* Default Debit Account Combination</legend>
				<table>
					<tr>
						<td class="labels">Division</td>
						<td class="value">
							<form:select path="dbACDivisionId" id="dbACDivisionId" class="frmSelectClass"
								onchange="divisionOnChange(true);"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<font color="red">
								<form:errors path="dbACDivisionId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>

					<tr>
						<td class="labels">Account</td>
						<td class="value">
							<form:select path="dbACAccountId" id="dbACAccountId" class="frmSelectClass" ></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<font color="red">
								<form:errors path="dbACAccountId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<font color="red">
								<form:errors path="debitAcctCombinationId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
			<form:hidden path="creditAcctCombinationId"/>
				<legend>* Default Credit Account Combination</legend>
				<table>
					<tr>
						<td class="labels">Division</td>
						<td class="value">
							<form:select path="crACDivisionId" id="crACDivisionId" class="frmSelectClass"
								onchange="divisionOnChange(false);"></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<font color="red">
								<form:errors path="crACDivisionId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>

					<tr>
						<td class="labels">Account</td>
						<td class="value">
							<form:select path="crACAccountId" id="crACAccountId"
								class="frmSelectClass" ></form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<font color="red">
								<form:errors path="crACAccountId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<font color="red">
								<form:errors path="creditAcctCombinationId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table class="buttonClss">
				<tr >
					<td align="right" >
						<input type="button" id="btnSaveReceipt" value="${receiptMethod.id eq 0 ? 'Save' : 'Update'}" onclick="saveReceipt()"/>
						<input type="button" id="btnCancelReceipt" value="Cancel" onclick="cancelReceiptMethod();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>

</body>
</html>