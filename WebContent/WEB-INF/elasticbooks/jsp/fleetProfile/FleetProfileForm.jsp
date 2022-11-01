<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Profile Form
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
#divCamera {
	width: 320px;
	height: 300px;
	border: 1px solid green;
}

.btnPhoto {
	width: 90px;
}

#btnSaveFleetProfile, #btnCancelFleetProfile {
	font-weight: bold;
}

</style>
<script type="text/javascript">
$(document).ready(function () {
	changeFormMode($("#refObjectId"));
	if('${fleetProfile.id}' != 0) {
		$("#txtDriver").val(processDriverName('${fleetProfile.driver.firstName}',
				'${fleetProfile.driver.lastName}', '${fleetProfile.driver.middleName}'));
	}
});

/**
 * This function will process the driver's full name in "lastName, firstName middleName" format.
 */
function processDriverName(firstName, lastName, middleName) {
	return lastName + ", " + firstName + " " + middleName;
}

function loadDrivers () {
	var driverName = encodeURIComponent($.trim($("#txtDriver").val()));
	var companyId = $("#companyId").val();
	var uri = contextPath + "/getDrivers?name="+driverName+"&companyId="+companyId;
	$("#txtDriver").autocomplete({
		source: uri,
		select: function( event, ui ) {
			var name = processDriverName(ui.item.firstName, ui.item.lastName, ui.item.middleName);
			$(this).val(name);
			$("#hdnDriverId").val(ui.item.id);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					$("#spnDriverErr").text('');
					if (ui.item != null) {
						var name = processDriverName(ui.item.firstName, ui.item.lastName, ui.item.middleName);
						$(this).val(name);
					} else if($.trim($("#txtDriver").val()) != "") {
						$("#spnDriverErr").text('Invalid Employee.');//Validation will only trigger if the driver input field is not empty.
						$("#hdnDriverId").val("");//Remove id.
					} else {
						$("#hdnDriverId").val("");//Remove id.
					}
				},
				error : function(error) {
					$("#spnDriverErr").text('Invalid Employee.');
					$("#txtDriver").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		var name = processDriverName(item.firstName, item.lastName, item.middleName);
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + name + "</a>" )
			.appendTo( ul );
	};
}

function checkDriver() {
	if($.trim($("#txtDriver").val()) == "") {
		$("#hdnDriverId").val("");//Remove id.
	}
}
</script>
<title>Fleet Form</title>
</head>
<body>
<div id="divForm" class="formDivBigForms">
<form:form method="POST" commandName="fleetProfile" id="fleetProfileForm">
	<div class="modFormLabel">
		Fleet Profile
		<span class="btnClose" id="btnClose">[X]</span>
	</div>
	<form:hidden path="id" id="hdnId"/>
	<form:hidden path="fleetTypeId" id="hdnFleetTypeId"/>
	<form:hidden path="divisionId" id="hdnDivisionId"/>
	<form:hidden path="ebObjectId" id="hdnEbObjectId"/>
	<form:hidden path="driverId" id="hdnDriverId"/>
	<form:hidden path="createdBy"/>

	<table class="formTable" style="width: 95%;" >
		<tr>
			<td class="labels">* Company</td>
			<td class="value">
				<c:choose>
					<c:when test="${fleetProfile.id ne 0}">
						<form:hidden path="companyId" id="hdnCompanyId"/>
						<select class="frmSelectClass" id="companyId" disabled="disabled">
								<option value="${company.id}" selected="selected">${company.name}</option>
						</select>
					</c:when>
					<c:otherwise>
						<form:select path="companyId" id="companyId"
							cssClass="frmSelectClass" items="${companies}" itemLabel="name" itemValue="id">
						</form:select>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td class="labels"></td>
			<td class="value">
				<form:errors path="companyId" cssClass="error"/>
			</td>
		</tr>

		<tr>
			<td class="labels">* Fleet Type</td>
			<td class="value">
				<form:hidden path="refObjectId" id="hdnRefObjectId"/>
				<c:choose>
					<c:when test="${fleetProfile.id ne 0}">
						<select class="frmSelectClass" id="refObjectId" disabled="disabled">
							<option value="${fleetType.id}-${fleetType.fleetCategoryId}" selected="selected">${fleetType.name}</option>
						</select>
					</c:when>
					<c:otherwise>
						<select class="frmSelectClass" id="refObjectId" onchange="changeFormMode(this); fleetTypeOnChange(this);">
							<c:forEach var="ft" items="${fleetTypes}">
								<option value="${ft.id}-${ft.fleetCategoryId}">${ft.name}</option>
							</c:forEach>
						</select>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td class="labels"></td>
			<td class="value">
				<form:errors path="refObjectId" cssClass="error"/>
			</td>
		</tr>

		<tr>
			<td class="labels" id="tdCode" >* Code</td>
			<td class="value">
				<form:input path="codeVesselName" class="input"/>
			</td>
		</tr>
		<tr>
			<td class="labels"></td>
			<td class="value">
				<form:errors path="codeVesselName" cssClass="error" id="codeVesselErrMsgId"/>
			</td>
		</tr>
		<tr>
			<td class="labels">Acquisition Date</td>
			<td class="value">
				<form:input path="acquisitionDate"
					id="acquisitionDate" style="width: 120px;" cssClass="dateClass2" onblur="evalDate('acquisitionDate')"/> 
				<img id="imgAcquisitionDate"
					src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('acquisitionDate')"
					style="cursor: pointer" style="float: right;" />
			</td>
		</tr>
		<tr>
			<td class="labels"></td>
			<td class="value"><form:errors path="acquisitionDate" cssClass="error" id="dateErrMsgId"/></td>
		</tr>
		<tr class="trConstruction">
			<td class="labels">* Make</td>
			<td class="value">
				<form:input path="make" class="input"/>
			</td>
		</tr>
		<tr class="trConstruction">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="make" cssClass="error" id="makeErrMsgId"/>
			</td>
		</tr>
		<tr class="trConstruction">
			<td class="labels">Model</td>
			<td class="value">
				<form:input path="model" class="input"/>
			</td>
		</tr>
		<tr class="trConstruction">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="model" cssClass="error" id="modelErrMsgId"/>
			</td>
		</tr>
		<tr class="trConstruction">
			<td class="labels">* Chassis No.</td>
			<td class="value">
				<form:input path="chassisNo" class="input"/>
			</td>
		</tr>
		<tr class="trConstruction">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="chassisNo" cssClass="error" id="chassisNoErrMsgId"/>
			</td>
		</tr>
		<tr class="trConstruction">
			<td class="labels">* Plate No.</td>
			<td class="value">
				<form:input path="plateNo" class="input"/>
			</td>
		</tr>
		<tr class="trConstruction">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="plateNo" cssClass="error" id="plateNoErrMsgId"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels">* Official No.</td>
			<td class="value">
				<form:input path="officialNo" class="input"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="officialNo" cssClass="error" id="officialNoErrMsgId"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels">* Call Sign</td>
			<td class="value">
				<form:input path="callSign" class="input"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="callSign" cssClass="error" id="callSignErrMsgId"/>
			</td>
		</tr>

		<tr class="trFishing">
			<td class="labels">Tonnage Weight</td>
			<td class="value">
				<form:input path="tonnageWeight" class="input"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="tonnageWeight" cssClass="error" id="tonnageErrMsgId"/>
			</td>
		</tr>

		<tr>
			<td class="labels">* Engine No.</td>
			<td class="value">
				<form:input path="engineNo" class="input"/>
			</td>
		</tr>
		<tr>
			<td class="labels"></td>
			<td class="value">
				<form:errors path="engineNo" cssClass="error" id="engineNoErrMsgId"/>
			</td>
		</tr>

		<tr>
			<td class="labels" valign="top">Description</td>
			<td class="value">
				<form:textarea path="description" class="input"/>
			</td>
		</tr>
		<tr>
			<td class="labels"></td>
			<td class="value">
				<form:errors path="description" cssClass="error" id="descErrMsgId"/>
			</td>
		</tr>

		<tr class="trConstruction">
			<td class="labels">* Driver</td>
			<td class="value">
				<input id="txtDriver" onkeypress="loadDrivers();" class="input" onblur="checkDriver();"/>
			</td>
		</tr>
		<tr class="trConstruction">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="driverId" cssClass="error" id="driverErrMsgId"/>
			</td>
		</tr>
		<tr class="trConstruction">
			<td class="labels"></td>
			<td class="value">
				<span id="spnDriverErr" class="error"></span>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels">VMS</td>
			<td class="value">
				<form:input path="vms" class="input"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="vms" cssClass="error" id="vmsErrMsgId"/>
			</td>
		</tr>

		<tr class="trFishing">
			<td class="labels">Propeller</td>
			<td class="value">
				<form:input path="propeller" class="input"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="propeller" cssClass="error" id="propellerErrMsgId"/>
			</td>
		</tr>
		
		<tr class="trFishing">
			<td class="labels">Winch</td>
			<td class="value">
				<form:input path="winch" class="input"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="winch" cssClass="error" id="winchErrMsgId"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels">Captain</td>
			<td class="value">
				<form:input path="captain" class="input"/>
			</td>
		</tr>
		<tr class="trFishing">
			<td class="labels"></td>
			<td class="value">
				<form:errors path="captain" cssClass="error" id="captErrMsgId"/>
			</td>
		</tr>
		<tr></tr>
		<tr align="center">
			<td style="padding-top: 5px; text-align: right;" colspan="6"  >
				<input type="button" id="btnSaveFleetProfile" value="Save" 
					onclick="saveFleetProf();" />
			</td>
		</tr>
	</table>
</form:form>
</div>
</body>
</html>