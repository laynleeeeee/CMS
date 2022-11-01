<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: The form that will be able to add/edit a division.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	if (Number("${division.id}") != 0) {
		$("#txtParentDivision").val("${division.parentDivision.name}");
	}
});

function loadParentDivisions () {
	var uri = contextPath + "/getDivisions/getParentDivisions?numberOrName="+encodeURIComponent($("#txtParentDivision").val())
			+ "&isMainLevelOnly=false";
	var divisionId = $("#id").val();
	if (divisionId != "" && divisionId != "0") {
		uri += "&divisionId="+divisionId;
	}
	$("#txtParentDivision").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnParentDivisionId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number + " - " + item.name + "</a>" )
			.appendTo( ul );
	};
}

function getParentDivision () {
	var divisionName = encodeURIComponent($.trim($("#txtParentDivision").val()));
	if (divisionName != "") {
		$.ajax({
			url: contextPath + "/getDivisions/byExactName?divisionName="+divisionName+"&excludeDivWithAcctCombi=true",
			success : function(division) {
				$("#spanPDError").text("");
				if (division != null) {
					$("#hdnParentDivisionId").val(division.id);
					$("#txtParentDivision").val(division.name);
					if ($("#errParentDivisionId").text().includes("Invalid parent division.")) {
						$("#errParentDivisionId").text("");
					}
				} else {
					$("#hdnParentDivisionId").val("");
					checkPDError() 
				}
			},
			error : function(error) {
				checkPDError() ;
			},
			dataType: "json"
		});
	}
}

function checkPDError() {
	if (!$("#errParentDivisionId").text().includes("Invalid parent division.")) {
		$("#spanPDError").text("Invalid parent division.");
	}
}

</script>
</head>
<div class="formDiv">
<form:form method="POST" commandName="division">
	<div class="modFormLabel">Division</div>
	<br>
	<table class="formTable">
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
		<form:hidden path="serviceLeaseKeyId"/>
		<form:hidden path="ebObjectId"/>
		<form:hidden path="parentDivisionId" id="hdnParentDivisionId"/>
		<form:hidden path="parentDivisionName"/>
		<tr>
			<td class="labels">* Number </td>
			<td class="value"><form:input path="number" size="7" maxlength="5"/></td>
		</tr>
		<tr>
			<td></td>
			<td class="value">
				<form:errors path="number" cssClass="error" />
			</td>
		</tr>
		<tr>
			<td class="labels">* Name </td>
			<td class="value"><form:input path="name" maxlength="100" id="divisionName" cssClass="inputSmall"/></td>
		</tr>
		<tr>
			<td></td>
			<td class="value">
				<form:errors path="name" cssClass="error" />
			</td>
		</tr>
		<tr>
			<td class="labels">Parent Division </td>
			<td class="value">
				<input id="txtParentDivision" onkeydown="loadParentDivisions();" onkeyup="loadParentDivisions();" onblur="getParentDivision()"  class="input"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="value">
				<span id="spanPDError" class="error"></span>
				<form:errors path="parentDivisionId" id="errParentDivisionId" cssClass="error" />
			</td>
		</tr>
		<tr>
			<td class="labels" valign="top">* Description </td>
			<td class="value"><form:textarea path="description" cols="50" rows="4"/></td>
		</tr>
		<tr>
			<td></td>
			<td class="value">
				<form:errors path="description" cssClass="error" />
			</td>
		</tr>
		<tr>
			<td class="labels">Active</td>
			<td class="value"><form:checkbox path="active"/> </td>
		</tr>
		<tr>
			<td colspan="2" align="right">
				<input type="button" id="btnSaveDivision" value="${division.id eq 0 ? 'Save' : 'Update'}" onclick="saveDivision();"/>
				<input type="button" id="btnCancelDivision" value="Cancel" onclick="cancelForm();"/>
			</td>
		</tr>
	</table>
</form:form>
</div>
</html>