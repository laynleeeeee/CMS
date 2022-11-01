<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Stock Adjustment Type form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
var GET_DIVISIONS_URI = contextPath+"/getDivisions?companyId=";
var GET_ACCOUNTS_URI = contextPath+"/getAccounts?companyId=";
$(document).ready(function() {
	if("${stockAdjustmentType.id}" == 0) {
		filterDivisions();
	} else {
		$("#hdnCompanyId").val("${stockAdjustmentType.acctCombi.companyId}");
		$("#hdnDivisionId").val("${stockAdjustmentType.acctCombi.divisionId}");
		$("#hdnAccountId").val("${stockAdjustmentType.acctCombi.accountId}");
	}
});

function filterDivisions(divisionId, accountId) {
	var companyId = $("#selectCompanyId option:selected").val();
	$("#selectDivisionId").empty();
	$("#selectDivisionId").append("<option selected='selected' value='0'></option>");
	var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["numberAndName"];
			}
	};
	postHandler = {
		doPost: function() {
			if(divisionId != "")
				$("#selectDivisionId").val(divisionId);
			filterAccounts(divisionId, accountId);
		}
	};

	var uri = GET_DIVISIONS_URI+companyId+(typeof divisionId != "undefined" ? "&divisionId="+divisionId : "");
	loadPopulate (uri, false, null, "selectDivisionId", optionParser, postHandler);

}

function filterAccounts(divisionId, accountId) {
	var companyId = $("#selectCompanyId option:selected").val();
	if(divisionId == 0 || typeof divisionId == "undefined")
		divisionId = $("#selectDivisionId option:selected").val();
	$("#selectAccountId").empty();
	$("#selectAccountId").append("<option selected='selected' value='0'></option>");
	if(divisionId != 0) {
		var optionParser = {
				getValue: function (rowObject) {
					return rowObject["id"];
				},

				getLabel: function (rowObject) {
					return rowObject["number"] +" - " +rowObject["accountName"];
				}
		};
		postHandler = {
				doPost: function() {
					if(accountId != "")
						$("#selectAccountId").val(accountId);
				}
		};
		var uri = GET_ACCOUNTS_URI+companyId+"&divisionId="+divisionId+
			(typeof accountId != "undefined" ? "&accountId="+accountId : "");
		loadPopulate (uri, false, null, "selectAccountId", optionParser, postHandler);

	}
}
</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="stockAdjustmentType">
		<div class="modFormLabel">Stock Adjustment Type</div>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="accountCombiId" id="hdnAcId"/>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<table class="formTable">
					<tr>
						<td class="labels">* Company </td>
						<td class="value">
							<c:choose>
								<c:when test="${stockAdjustmentType.id eq 0}">
									<form:select path="companyId" cssClass="frmSelectClass"
												id="selectCompanyId" onchange="filterDivisions();">
										<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
									</form:select>
								</c:when>
								<c:otherwise>
									<form:hidden path="companyId" id="hdnCompanyId"/>
									<span id="companyLabel">${stockAdjustmentType.acctCombi.company.numberAndName}</span>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="companyId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Name</td>
						<td class="value"><form:input path="name" cssClass="input" id="satName"/></td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="name" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Division </td>
						<td class="value">
							<c:choose>
								<c:when test="${stockAdjustmentType.id eq 0}">
									<form:select path="divisionId" class="frmSelectClass"
											id="selectDivisionId" onchange="filterAccounts();"></form:select>
								</c:when>
								<c:otherwise>
									<form:hidden path="divisionId" id="hdnDivisionId"/>
									<span id="divisionLabel">${stockAdjustmentType.acctCombi.division.numberAndName}</span>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">*Charged Account</td>
						<td class="value">
							<c:choose>
								<c:when test="${stockAdjustmentType.id eq 0}">
									<form:select path="accountId" id="selectAccountId"
											class="frmSelectClass"></form:select>
								</c:when>
								<c:otherwise>
									<form:hidden path="accountId" id="hdnAccountId" />
									<span id="accountLabel">${stockAdjustmentType.acctCombi.account.number} - ${stockAdjustmentType.acctCombi.account.accountName}</span>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="accountId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Active </td>
						<td class="value"><form:checkbox path="active"/></td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="active" cssClass="error"/></td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table class="formDiv">
				<tr>
					<td></td>
					<td align="right">
						<input type="button" id="btnSaveSAT" value="${stockAdjustmentType.id eq 0 ? 'Save' : 'Update'}" onclick="saveAdjustmentType();"/>
						<input type="button" id="btnCancelSAT" value="Cancel" onclick="cancelForm();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>