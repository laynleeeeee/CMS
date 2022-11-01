<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Fleet Type Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ybl/inputUtil.js"></script>
<title>FleetType Form</title>
<script type="text/javascript">
$(document).ready(function() {
	var id = getCheckedId("cbFleetType");
});

function saveFleetType() {
	var id = getCheckedId("cbFleetType");
	$("#btnSaveFleetType").attr("disabled", "disabled");
	doPostWithCallBack ("fleetType", "divFleetTypeForm", function(data) {
		if (data == "saved") {
			if(id == 0){
			$("#spanMessage").text("Fleet Type "+
				$.trim($("#fleetTypeName").val())+" was successfully saved.");
			}
			else{$("#spanMessage").text("Fleet Type "+
				$.trim($("#fleetTypeName").val())+" was successfully updated.");
			}
			searchFleetTypes();
			dojo.byId("divFleetTypeForm").innerHTML = "";
		}else {
			var companyId = $("#companyId").val();
			$("#divFleetTypeForm").html(data);
			$("#companyId").val(companyId);
		}
	});
}

function cancelForm() {
	$("#divFleetTypeForm").html("");
}
</script>
</head>
<body>
<div class="formDiv">
<form:form method="POST" commandName="fleetType">
		<div class="modFormLabel">Fleet Type</div>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="ebObjectId"/>
	<br>
	<div class="modForm">
	<fieldset class="frmField_set">
		<legend>Basic Information</legend>
		<table class="formTable">
			<tr>
				<td class="labels">* Company</td>
				<td class="value">
					<form:select path="companyId" id="companyId" class="frmSelectClass">
						<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
					</form:select>
				</td>
			</tr>
			<tr>
				<td></td>
				<td class="value"><form:errors path="companyId" cssClass="error"/></td>
			</tr>
			<tr>
				<td class="labels">* Category</td>
				<td class="value">
					<form:select path="fleetCategoryId" id="fleetCategoryId" class="frmSelectClass">
						<form:option value="-1">SELECT A CATEGORY</form:option>
						<form:options items="${fleetCategories}" itemLabel="name" itemValue="id"/>
					</form:select>
				</td>
			</tr>
			<tr>
				<td></td>
				<td class="value"><form:errors path="fleetCategoryId" cssClass="error"/></td>
			</tr>
			<tr>
				<td class="labels">* Fleet Type</td>
				<td class="value">
					<form:input path="name" cssClass="input" id="fleetTypeName"/>
				</td>
			<tr>
				<td></td>
				<td colspan="2" class="value"><form:errors path="name" cssClass="error"/></td>
			</tr>
			<tr>
				<td class="labels">Active</td>
				<td class="value"><form:checkbox path="active"/></td>
			</tr>
			<tr>
				<td colspan="2"><form:errors path="active" cssClass="error"/></td>
			</tr>
		</table>
		<br>
	</fieldset>
		<table class="formDiv">
			<tr>
				<td></td>
				<td align="right" >
					<input type="button" id="btnSaveFleetType" value="${fleetType.id eq 0 ? 'Save' : 'Update'}" onclick="saveFleetType();"/>
					<input type="button" id="btnCancelSave" value="Cancel" onclick="cancelForm();"/>
				</td>
			</tr>
		</table>
		</div>
		</form:form>
	</div>
<hr class="thin"/>
</body>
</html>