<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description: Currency Rate form.
	 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
</head>
<script>
$(document).ready(function() {
	formatRate();
	$("#date").attr("disabled","disabled");
	if("${currencyRate.id}" != 0){
		$("#btnSave").attr("disabled","disabled");
	}
});

function formatRate(){
	$("#rate").val(accounting.formatNumber($("#rate").val(), 6, ','));
}

function unformatRate() {
	$("#rate").val(accounting.unformat($("#rate").val()));
}
</script>
<style>
input.numeric {
	width: 120px;
	text-align: right;
}
</style>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="currencyRate" id="currencyRateFromId">
			<div class="modFormLabel">Currency Rate</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="date" id="date" maxlength="10" class="dateClass2" onblur="evalDate('date')"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="date" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Time</td>
							<td class="value"><form:input path="time" cssClass="dateClass2" onblur="validateTime(this)"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="time" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Currency</td>
							<td class="value">
								<form:select path="currencyId" id="currencyId" cssClass="frmMediumSelectClass">
									<form:options items="${currencies}" itemValue="id" itemLabel="name"/>
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="currencyId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">* Rate</td>
							<td class="value">
								<form:input path="rate" class="numeric" onblur="formatRate();"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="rate" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value"><form:checkbox path="active"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="active" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="formDiv">
					<tr>
						<td colspan="2" align="right">
							<input type="button" id="btnSave" value="Save" onclick="saveCurrencyRate();"/>
							<input type="button" id="btnCancel" value="Cancel" onclick="cancelCurrency();"/>
						</td>
					</tr>
				</table>
			</div>
			<hr class="thin"/>
		</form:form>
	</div>
</body>
</html>