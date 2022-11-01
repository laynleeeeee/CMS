<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Requisition Form reference for Withdrawal Slip.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<title>RF Reference</title>
<style>
#tblRTSReference {
	cellspacing: 0;
	border: none;
}

#tblRTSReference thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#tblRTSReference tbody td {
	border-top: 1px solid #000000;
}

.tdProperties {
	border-right: 1px solid #000000;
}

</style>
<script type="text/javascript">
var poId = null;
var IS_INIT = true;
var typeId = parseInt("${reqTypeId}");
$(document).ready(function() {
	IS_INIT = true;
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function getCommonParam() {
	var companyId = $("#companyId").val() == -1 ? "" : $("#companyId").val();
	var fleetId = $("#fleetId").val();
	var projectId = $("#projectId").val();
	var rfNumber = processSearchName($("#txtRFNumber").val());
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var reqClassificationId = ("${reqClassificationId}" != null && "${reqClassificationId}" != "") ? parseInt("${reqClassificationId}") : "";
	var status = $("#selectPOStatus").val();
	var uri = contextPath + "/jyeiWithdrawalSlip/"+typeId+"/loadRfs?companyId=" + companyId + "&fleetId=" + fleetId
			+ "&projectId=" + projectId + "&rfNumber=" + rfNumber + "&dateFrom=" + dateFrom + "&dateTo=" + dateTo
			+ "&status=" + status + "&isExcludePrForms=true";
	if (typeof IS_PAKYAWAN_SUBCON_ONLY != undefined) {
		uri += "&isPakyawanSubconOnly="+IS_PAKYAWAN_SUBCON_ONLY;
	}
	return uri;
}

function filterPOReference() {
	IS_INIT = false;
	if ($("#companyId").val() != -1) {
		$("#divRFRefTable").load(getCommonParam()+"&pageNumber=1");
	} else {
		alert("Please select a company.");
	}
}

function selectPOReference() {
	if (poId == null) {
		alert("Please select a requisition form.");
	}else if(companyId == -1) {
		alert("Please select a company.");
	}else{
		loadRFReference ($(poId).attr("id"));
 	}
}

function loadFleetProfiles() {
	var fleetName = $("#txtFleetName").val();
	if(fleetName != "") {
		var companyId = "${companyId}";
		fleetName = processSearchName($.trim(fleetName));
		var uri = contextPath + "/getFleetProfile?codeVesselName=" + fleetName +"&isExact=false";
		if(companyId != -1) {
			uri += "&companyId=" +companyId;
		}
		
		loadACItems("txtFleetName", "fleetId", null, uri, uri, "codeVesselName",
				function() {
					$("#spanFleetError").text("");
				}, function() {
					$("#spanFleetError").text("");
				}, function() {
					$("#spanFleetError").text("");
				}, function() {
					$("#spanFleetError").text("Invalid fleet.");
					$("#fleetId").val("");
				}
		);
	} else {
		$("#fleetId").val("");
	}
}

function loadProjects() {
	var companyId = "${companyId}";
	var name = $("#txtProjectName").val();
	if(name != "") {
		name = processSearchName($.trim(name));
		var uri = contextPath + "/getArCustomers/new?name=" + name +"&companyId="+companyId;
		loadACItems("txtProjectName", "projectId", null, uri, uri, "name",
				function() {
					$("#spanProjectError").text("");
				}, function() {
					$("#spanProjectError").text("");
				}, function() {
					$("#spanProjectError").text("");
				}, function() {
					$("#spanProjectError").text("Invalid project.");
					$("#projectId").val("");
				}
		);
	} else {
		$("#projectId").val("");
	}
}
</script>
</head>
<body>
<div id="divWSReference">
	<h3 style="text-align: center;">RF Reference</h3>
	<div>
		<table class="frmField_set">
			<tr>
				<td>Fleet</td>
				<td>
					<input id="txtFleetName" class="input" onkeydown="loadFleetProfiles();" onblur="loadFleetProfiles();"/>
					<input type="hidden" id="fleetId">
				</td>
			</tr>
			<tr>
				<td></td>
				<td><span id="spanFleetError" class="error" style="color: red;"></span></td>
			</tr>
			<tr>
				<td>Project</td>
				<td>
					<input id="txtProjectName" class="input" onkeydown="loadProjects();" onblur="loadProjects();"/>
					<input type="hidden" id="projectId">
				</td>
			</tr>
			<tr>
				<td></td>
				<td><span id="spanProjectError" class="error" style="color: red;"></span></td>
			</tr>
			<tr>
				<td>Reference No. </td>
				<td>
					<input id="txtRFNumber" type="text" class="standard" />
				</td>
			</tr>
			<tr>
				<td>Date From </td>
				<td>
					<input id="dateFrom" onblur="evalDate('dateFrom')" style="width: 120px;" class="dateClass2"/> 	
					<img src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('dateFrom')"/>		
					To
					<jsp:useBean id="currentDate" class="java.util.Date" />
					<input id="dateTo" onblur="evalDate('dateTo')" style="width: 120px;" class="dateClass2" /> 	
					<img src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('dateTo')"/>
				</td>
			</tr>
			<tr>
				<td>Status</td>
				<td>
					<select id="selectPOStatus" class="frmSelectClass">
						<option value="1">All</option>
						<option value="2" selected>Unused</option>
						<option value="3">Used</option>
					</select>
					<input type="button" id="btnSearchWSReference" value="Search" onclick="filterPOReference();"/>
				</td>
			</tr>
		</table>
	</div>
	<fieldset style="margin-top : 20px;"  class="frmField_set">
		<legend>Requisition Form Reference Table</legend>	
			<div id="divRFRefTable">
				<%@ include file = "RFReferenceTable.jsp" %>
			</div>
	</fieldset>
	<table class="frmField_set" style="margin-left: 88%;">
		<tr>	
			<td>
				<input type="button" id="btnOKWSReference" value="Extract" onclick="selectPOReference();" style=" margin-top : 20px; "/>
			</td>
		</tr>
	</table>
</div>
</body>
</html>