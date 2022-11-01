<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!--

	Description	: PR Reference form
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<title>PR Reference</title>
<style>
#tblJOReference {
	cellspacing: 0;
	border: none;
}

#tblJOReference thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#tblJOReference tbody td {
	border-top: 1px solid #000000;
}

.tdProperties {
	border-right: 1px solid #000000;
}
</style>
<script type="text/javascript">
var rfId = null;
var slctdRequests = [];
$(document).ready(function() {
	IS_INITIAL_LOAD = true;
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function getCommonParam() {
	var companyId = $("#companyId").val();
	var fleetProfileId = $.trim($("#fleetId").val());
	var prNumber = $.trim($("#txtPrNumber").val());
	var projectId = $("#projectId").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var status = $("#selectRFStatus").val();

	return contextPath+"/retailPurchaseOrder/prReference/"+(!IS_INITIAL_LOAD ? "/reload" : "")+"?companyId="+companyId
			+"&fleetProfileId="+fleetProfileId+"&projectId="+projectId +"&prNumber="+prNumber
			+"&strDateFrom="+dateFrom+"&strDateTo="+dateTo+"&status="+status;
}

function filterRFReference() {
	IS_INITIAL_LOAD = false;
	var prNumber = $.trim($("#txtPrNumber").val());
	if(!isNaN(prNumber)) {
		$("#divPrFormRefTbl").load(getCommonParam()+"&pageNumber=1");
		$("#spanRfNoMsg").text("");
	} else {
		$("#spanRfNoMsg").text("Only numerical reference number is allowed.");
	}
}

function selectPrReference() {
	if(slctdRequests.length == 0) {
		alert("Please select a purchase requests/s.");
	} else {
		loadPrReference(slctdRequests);
 	}
}

function loadFleetProfiles() {
	var fleetName = $("#txtFleetName").val();
	if(fleetName != "") {
		var companyId = $("#companyId").val();
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
		$("#fleetId").val(null);
	}
}

function loadProjects() {
	var companyId = $("#companyId").val();
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
		$("#projectId").val(null);
	}
}
</script>
</head>
<body>
<div id="divPoRrReference">
	<div>
		<table class="frmField_set">
			<tr>
				<td>Fleet</td>
				<td>
					<input id="txtFleetName" class="input" onkeydown="loadFleetProfiles();"/>
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
					<input id="txtProjectName" class="input" onkeydown="loadProjects();"/>
					<input type="hidden" id="projectId">
				</td>
			</tr>
			<tr>
				<td></td>
				<td><span id="spanProjectError" class="error" style="color: red;"></span></td>
			</tr>
			<tr>
				<td>PR No.</td>
				<td>
					<input id="txtPrNumber" class="input">
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanRfNoMsg" class="error"></span>
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
					<select id="selectRFStatus" class="frmSelectClass">
						<option value="1">All</option>
						<option value="2" selected>Unused</option>
						<option value="3">Used</option>
					</select>
					<input type="button" id="btnSearchRFReference" value="Search" onclick="filterRFReference();"/>
				</td>
			</tr>
		</table>
	</div>
	<fieldset style="margin-top : 20px;"  class="frmField_set">
		<legend>PR Reference Table</legend>
			<div id="divPrFormRefTbl">
				<%@ include file = "PRReferenceTable.jsp" %>
			</div>
	</fieldset>
	<table class="frmField_set" style="margin-left: 88%;">
		<tr>
			<td>
				<input type="button" id="btnOkPoReference" value="Extract" onclick="selectPrReference();" style=" margin-top : 20px; "/>
			</td>
		</tr>
	</table>
</div>
</body>
</html>