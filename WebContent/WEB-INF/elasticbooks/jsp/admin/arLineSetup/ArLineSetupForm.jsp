<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR Line Setup Form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
var COMPANY_ID = "${arLine.companyId}";
var DIVISION_ID = "${arLine.divisionId}";
var ACCOUNT_ID = "${arLine.accountId}";
var DISC_ACCOUNT_ID = "${arLine.discAccId}";
var noValue = 0;
$(document).ready(function() {
	if("${acctCombi.id}" != 0) {
		COMPANY_ID = "${acctCombi.companyId}";
		DIVISION_ID = "${acctCombi.divisionId}";
		ACCOUNT_ID = "${acctCombi.accountId}";
		DISC_ACCOUNT_ID = "${discAC.accountId}";
		$("#companyId").val(COMPANY_ID);
		$("#divisionId").val(DIVISION_ID);
		$("#accountId").val(ACCOUNT_ID);
		$("#discAccId").val(DISC_ACCOUNT_ID);
	} else {
		COMPANY_ID = $("#companyId").val();
		loadDivisions(COMPANY_ID, DIVISION_ID, ACCOUNT_ID, DISC_ACCOUNT_ID);
	}
	formatMoney($("#amount"));
});

function formatMoney(elem){
	$(elem).val(accounting.formatMoney($(elem).val()));
}

</script>
<title>AR Line Setup</title>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="arLine" id="arLineSetupFormId">
		<div class="modFormLabel">AR Line Setup</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>AR Line Setup Information</legend>
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<form:hidden path="accountCombinationId"/>
						<form:hidden path="discountACId"/>
						<tr>
							<td class="labels">* Name</td>
							<td class="value"><form:input path="name" cssClass="input"/></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="name" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Company</td>
							<td class="value">
								<c:choose>
									<c:when test="${arLine.id ne 0}">
										${companyNumberAndName}
										<form:hidden path="companyId" id="companyId"/>
									</c:when>
									<c:otherwise>
										<form:select path="companyId" id="companyId" class="frmSelectClass" onchange="loadDivisions(null, 0, 0, 0);">
											<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
										</form:select>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="companyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Division</td>
							<td class="value">
								<c:choose>
									<c:when test="${arLine.id ne 0}">
										${divisionName}
										<form:hidden path="divisionId" id="divisionId"/>
									</c:when>
									<c:otherwise>
										<form:select path="divisionId" id="divisionId"
											class="frmSelectClass" onchange="loadAccounts();loadDiscAccounts();">
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
							<td class="labels">Account</td>
							<td class="value">
								<c:choose>
									<c:when test="${arLine.id ne 0}">
										${accountName}
										<form:hidden path="accountId" id="accountId"/>
									</c:when>
									<c:otherwise>
										<form:select path="accountId" id="accountId"
											class="frmSelectClass" >
										</form:select>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="accountId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Discount Account</td>
							<td class="value">
								<c:choose>
									<c:when test="${arLine.id ne 0}">
										${discAccName}
										<form:hidden path="discAccId" id="discAccId"/>
									</c:when>
									<c:otherwise>
										<form:select path="discAccId" id="discAccId"
											class="frmSelectClass" >
										</form:select>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value"><form:errors path="discAccId" cssClass="error"/></td>
						</tr>

						<tr>
							<td class="labels">Amount</td>
							<td class="value">
								<form:input path="amount" id="amount" cssClass="inputSmall" style="text-align: right"
									onblur="formatMoney(this);"/>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td colspan="2" class="value"><form:errors path="amount" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value"><form:checkbox path="active"/></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td colspan="2" class="value"><form:errors path="active" cssClass="error"/></td>
						</tr>
					</table>
			</fieldset>
			<br>
			<table class="formDiv">
				<tr>
					<td></td>
					<td colspan="2"><form:errors path="arLineMessage" cssClass="error"/></td>
				</tr>
				<tr>
					<td></td>
					<td align="right">
						<input type="button" id="btnSaveArLine"	value="${arLine.id eq 0 ? 'Save' : 'Update'}" onclick="saveArLine();"/>
						<input type="button" id="btnCancelArLine" value="Cancel" onclick="cancelLineSetup();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>