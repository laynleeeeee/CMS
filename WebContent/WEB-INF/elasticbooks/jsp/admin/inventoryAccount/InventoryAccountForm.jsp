<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description:Transfer Receipt form for Inventory Retail.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	if("${inventoryAccount.id}" == 0) {
		loadReceiptMethods();
	} else {
		$("#selectCompanyId").val("${inventoryAccount.companyId}");
	}
	filterCashSalesRM("${inventoryAccount.cashSalesRmId}");
	filterCustomerAdvPaymentRM("${inventoryAccount.customerAdvPaymentRmId}");
});

function loadReceiptMethods() {
	filterCashSalesRM();
	filterCustomerAdvPaymentRM();
}

function filterCashSalesRM(cashSaleRMId) {
	var selectCSIds = new Array ();
	selectCSIds.push ("cashSalesRmId");
	filterReceiptMethod (selectCSIds, cashSaleRMId);
}

function filterCustomerAdvPaymentRM(customerAdvPaymentRMId) {
	var selectCAPIds = new Array ();
	selectCAPIds.push ("customerAdvPaymentRmId");
	filterReceiptMethod (selectCAPIds, customerAdvPaymentRMId);
}

function filterReceiptMethod (selectIds, selectedValue) {
	for (var i = 0; i < selectIds.length; i++ ){
		var selectId = selectIds[i];
		$("#"+selectId).empty();
	}
	var companyId = $("#selectCompanyId").val();
	var uri = contextPath+"/getReceiptMethods?companyId="+companyId;
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["name"];
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
	<form:form method="POST" commandName="inventoryAccount">
		<div class="modFormLabel">Inventory Forms Account Setup</div>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<table class="formTable">
					<tr>
						<td class="labels">* Company </td>
						<td class="value"><form:select path="companyId" cssClass="frmSelectClass" id="selectCompanyId" 
									onchange="loadReceiptMethods();loadInputDivisions();loadOutputDivisions();">
								<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value"><form:errors path="companyId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Active </td>
						<td class="value"><form:checkbox path="active"/></td>
					</tr>
					<tr>
						<td class="value"><form:errors path="active" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>* Cash Sales Form</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Receipt Method</td>
							<td class="value"><form:select path="cashSalesRmId"
									id="cashSalesRmId" cssClass="frmSelectClass"></form:select></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="cashSalesRmId" cssClass="error"/></td>
						</tr>
					</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>* Customer Advance Payment Form</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Receipt Method</td>
							<td class="value"><form:select path="customerAdvPaymentRmId"
									id="customerAdvPaymentRmId" cssClass="frmSelectClass"></form:select></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="customerAdvPaymentRmId" cssClass="error"/></td>
						</tr>
					</table>
			</fieldset>
		</div>
	</form:form>
	<table class="buttonClss">
		<tr>
			<td align="right">
				<input type="button" id="btnSaveInvAcct" value="${inventoryAccount.id eq 0 ? 'Save' : 'Update'}" onclick="saveInventoryAccount();"/>
				<input type="button" id="btnCancelInvAcct" value="Cancel" onclick="hideAddForm();"/>
			</td>
		</tr>
	</table>
</div>
</body>
</html>