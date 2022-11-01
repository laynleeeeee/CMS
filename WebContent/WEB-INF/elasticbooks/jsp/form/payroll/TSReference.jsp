<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!--

	Description: Time sheet reference form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/status.css" media="all">
<script src="${pageContext.request.contextPath}/js/jquery/jquery.js" type="text/javascript"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/formatUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxValidation.js"></script>
<title>Time Sheet Reference</title>
<style>
#tblCSReference {
	cellspacing: 0;
	border: none;
}

#tblCSReference thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#tblCSReference tbody td {
	border-top: 1px solid #000000;
}

.tdProperties {
	border-right: 1px solid #000000;
}
</style>
<script type="text/javascript">
var tsId = null;
var refType = null;
$(document).ready(function() {
	$("#slctMonth").val("${month}");
	$("#slctYear").val("${year}");
	loadTimePeriodSchedules ();
});

function loadTimePeriodSchedules () {
	$("#slctTimePeriodSchedule").empty();
	var month = $("#slctMonth").val();
	var year = $("#slctYear").val();
	var uri = contextPath + "/payroll/getTPSchedules?month="+month+"&year="+year;
	var optionParser = {
			getValue: function (rowObject){
				if (rowObject != null) {
					return rowObject["id"];
				}
			},

			getLabel: function (rowObject){
				if (rowObject != null)
					return rowObject["name"];
			}
	};
	postHandler = {
			doPost: function(data) {
				// This is to remove any duplication.
				var found = [];
				$("#slctTimePeriodSchedule option").each(function() {
					if($.inArray(this.value, found) != -1) 
						$(this).remove();
					found.push(this.value);
				});
			}
	};
	loadPopulate (uri, false, null, "slctTimePeriodSchedule", optionParser, postHandler);
}


function getCommonParam() {
	var companyId = $("#slctCompany").val();
	var divisionId = $("#slctDivision").val();
	var month = $("#slctMonth").val();
	var year = $("#slctYear").val();

	return contextPath + "/timesheet/"+"/reference/detail?companyId="+companyId+"&divisionId="+divisionId+
		"&month="+month+"&year="+year;
}

function filterTSReference() {
	if ($("#slctCompany").val() != -1) {
			$("#divTSRefTable").load(getCommonParam()+"&pageNumber=1");
	} else {
		alert("Please select a company.");
	}
}

function selectTSReference() {
	if (tsId == null) {
		alert("Please select a time sheet.");
	} else {
		var opener = window.opener;
		opener.loadTSReference ($(tsId).attr("id"), refType);
	}
}

function companyOnChange(){
	// Add function here.
}
</script>
</head>
<body>
<div id="divCSRReference">
	<h3 style="text-align: center;">Time Sheet Reference</h3>
	<div>
		<table class="frmField_set">
			<tr>
				<td>Company/Branch</td>
				<td>
					<select id="slctCompany" class="frmSelectClass" onchange="companyOnChange()">
						<option selected='selected' value='' >ALL</option>
						<c:forEach var="company" items="${companies}">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Division/Department</td>
				<td>
					<select id="slctDivision" class="frmSelectClass" onchange="companyOnChange()">
						<option selected='selected' value=''>ALL</option>
						<c:forEach var="division" items="${divisions}">
							<option value="${division.id}">${division.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Month/Year</td>
				<td>
					<select id="slctMonth" class="frmSmallSelectClass" onchange="loadDtrDetails();">
						<c:forEach var="mm" items="${months}">
							<option value="${mm.month}">${mm.name}</option>
						</c:forEach>
					</select>
					<select id="slctYear" class="frmSmallSelectClass" style="margin-left: 3px;" onchange="loadDtrDetails();">
						<c:forEach var="yy" items="${years}">
							<option value="${yy}">${yy}</option>
						</c:forEach>
					</select>
						<input type="button" id="btnSearchTSReference" value="Search" onclick="filterTSReference();"/>
				</td>
			</tr>
			<!-- <tr>
				<td>Time Period</td>
				<td>
					<select class="frmSelectClass" id="slctTimePeriodSchedule" onchange="reloadData();">
					</select>
					<input type="button" id="btnSearchTSReference" value="Search" onclick="filterTSReference();"/>
				</td>
			</tr> -->
		</table>
	</div>
	<fieldset style="margin-top : 20px;"  class="frmField_set">
		<legend>Timesheet Table</legend>
			<div id="divTSRefTable">
				<%@ include file = "TSReferenceTable.jsp" %>
			</div>
	</fieldset>
	<table class="frmField_set" style="margin-left: 88%;">
		<tr>
			<td>
				<input type="button" id="btnOKCSReference" value="OK" onclick="selectTSReference();" style=" margin-top : 20px; "/>
			</td>
		</tr>
	</table>
</div>
</body>
</html>