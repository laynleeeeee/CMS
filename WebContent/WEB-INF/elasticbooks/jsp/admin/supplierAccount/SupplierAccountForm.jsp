<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!--

	Description: Supplier Account form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<style type="text/css">
#amount {
	text-align: right;
}
</style>
<script type="text/javascript">
var selectedDebitDivisionValue =  ${supplierAccount.debitDivisionId};
var selectedDebitAccountValue =  ${supplierAccount.debitAccountId};
var selectedCreditDivisionValue =  ${supplierAccount.creditDivisionId};
var selectedCreditAccountValue =  ${supplierAccount.creditAccountId};

$(document).ready(function() {
	filterDivisions();
});
</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="supplierAccount" id="supplierAcctFormId">
		<div class="modFormLabel">Supplier's Account</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>Account Info</legend>
				<table class="formTable" >
				<form:hidden path="id"/>
				<form:hidden path="createdBy"/>
				<tr>
					<td class="labels">Active: </td>
					<td class="value">
						<form:checkbox path="active"/>
					</td>
				</tr>
				<tr >
					<td></td>
					<td>
						<font color="red">
							<form:errors path="active" cssClass="error"/>
						</font>
					</td>
				</tr>
				<tr>
					<td class="labels">* Supplier Name</td>
					<td class="value">
						<c:choose>
							<c:when test="${supplierAccount.id ne 0}">
								${supplierAccount.supplier.name}
								<form:hidden path="supplierId" id="supplierId"/>
							</c:when>
							<c:otherwise>
								<form:select path="supplierId" class="frmSelectClass">
									<form:options items="${suppliers}" itemLabel="numberAndName" itemValue="id"/>
								</form:select>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr >
					<td></td>
					<td>
						<font color="red">
							<form:errors path="supplierId" cssClass="error" style="margin-left: 12px;"/>
						</font>
					</td>
				</tr>
				<tr>
					<td class="labels">* Company</td>
					<td class="value">
						<c:choose>
							<c:when test="${supplierAccount.id == 0 }">
								<form:select path="companyId" id="companyId" cssClass="frmSelectClass" onchange="filterDivisions();">
									<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
								</form:select>
							</c:when>
							<c:otherwise>
								<c:forEach var="company" items="${companies}">
									<c:if test="${company.id == supplierAccount.companyId}">
										${company.numberAndName}
										<form:hidden path="companyId" id="companyId"/>
									</c:if>
								</c:forEach>
							</c:otherwise>
						</c:choose>
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
					<td class="labels" valign="top">* Account Name</td>
					<td class="value"><form:input path="name" maxlength="100" class="input"/></td>
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
					<td class="labels">* Term</td>
					<td class="value">
						<form:select path="termId" cssClass="frmSelectClass">
							<form:options items="${terms}" itemLabel="name" itemValue="id"/>
						</form:select>
					</td>
				</tr>
				<tr >
					<td></td>
					<td>
						<font color="red">
							<form:errors path="termId" cssClass="error" style="margin-left: 12px;"/>
						</font>
					</td>
				</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
			<form:hidden path="defaultDebitACId"/>
				<legend>Default Debit Account</legend>
				<table>
					<tr>
						<td class="labels">Division</td>
						<td class="value">
							<form:select path="debitDivisionId" id="defaultDebitDivisionId"
								class="frmSelectClass" onchange="filterAccountByDebitDivision ();">
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td >
							<font color="red">
								<form:errors path="debitDivisionId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>

					<tr>
						<td class="labels">Account</td>
						<td class="value">
						<form:select path="debitAccountId" id="defaultDebitAccountId"
								class="frmSelectClass" >
							</form:select>
						</td>
					</tr> 
					<tr>
						<td></td>
						<td >
							<font color="red">
								<form:errors path="debitAccountId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<font color="red">
								<form:errors path="defaultDebitACId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>*Default Credit Account</legend>
				<form:hidden path="defaultCreditACId"/>
				<table>
					<tr>
						<td class="labels">Division</td>
						<td class="value">
							<form:select path="creditDivisionId" id="defaultCreditDivisionId"
								class="frmSelectClass" onchange="filterAccountByCreditDivision ();">
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td >
							<font color="red">
								<form:errors path="creditDivisionId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>

					<tr>
						<td class="labels">Account</td>
						<td class="value">
							<form:select path="creditAccountId" id="defaultCreditAccountId"
								class="frmSelectClass" >
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td >
							<font color="red">
								<form:errors path="creditAccountId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>
					<tr>
						<td></td>
						<td >
							<font color="red">
								<form:errors path="defaultCreditACId" cssClass="error" style="margin-left: 12px;"/>
							</font>
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table class="buttonClss">
				<tr >
					<td align="right" >
						<input type="button" id="btnSaveSupplierAcct" value="${supplierAccount.id eq 0 ? 'Save' : 'Update'}" onclick="saveSupplierAcct();"/>
						<input type="button" id="btnCancelSupplierAcct" value="Cancel" onclick="cancelSupplierAcct ();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>