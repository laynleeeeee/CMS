<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Retail - RR reference form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script src="${pageContext.request.contextPath}/js/jquery/jquery.js" type="text/javascript"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/formatUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<title>RR Reference</title>
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
var rrId = null;
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function getCommonParam() {
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	var supplierId = $("#supplierId").val();
	var rrNumber = processSearchName($("#txtRRNumber").val());
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var status = $("#selectRRStatus").val();
	if(warehouseId == null) {
		//Set to zero if warehouse is null
		//For company with no warehouse
		warehouseId = 0;
	}
	var uri = contextPath+"/retailReturnToSupplier/rtsReference?companyId=" + companyId + "&warehouseId=" + warehouseId + "&supplierId=" + supplierId +
		"&rrNumber=" + rrNumber + "&dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&status=" + status;
	return uri;
}

function filterRRReference() {
	if ($("#companyId").val() != -1) {
		$("#divRTSRefTable").load(getCommonParam()+"&pageNumber=1");
	} else
		alert("Please select a company.");
}

function selectRRReference() {
	if (rrId == null) {
		alert("Please select a receiving report.");
	}else if(companyId == -1) {
		alert("Please select a company.");
	}else if (warehouseId == null) {
		alert("Please select a warehouse.");
	}else if (warehouseId == null) {
		alert("Please select a warehouse.");
 	}else{
		var opener = window.opener;
		opener.loadRRReference ($(rrId).attr("id"));
 	}
}

function filterWarehouse() {
	var selectedCompanyId = $("#companyId").val();
	if(selectedCompanyId != -1) {
		$("#spanCompanyError").text("");
		$("#warehouseId").empty();
		var uri = contextPath+"/getWarehouse?companyId="+selectedCompanyId;
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, false, null, "warehouseId", optionParser, null);
	}else{
		$("#warehouseId").empty();
		$("#spanCompanyError").text("Please select company first.");
	}
}

function loadSuppliers() {
	var supplierName = $("#txtSupplierName").val();
	if(supplierName != "") {
		var companyId = $("#companyId").val();
		supplierName = processSearchName($.trim(supplierName));
		var uri = contextPath + "/getSuppliers/new?name="+supplierName;
		if(companyId != -1) {
			uri += "&companyId=" +companyId;
		}
		loadACItems("txtSupplierName", "supplierId", null, uri, uri, "name",
				function() {
					$("#spanSupplierError").text("");
				}, function() {
					$("#spanSupplierError").text("");
				}, function() {
					$("#spanSupplierError").text("");
				}, function() {
					$("#spanSupplierError").text("Invalid supplier.");
					$("#supplierId").val("");
				}
		);
	} else {
		$("#supplierId").val(null);
	}
}
</script>
</head>
<body>
<div id="divRTSReference">
	<h3 style="text-align: center;">RR Reference</h3>
	<div>
		<table class="frmField_set">
			<tr>
				<td>Company</td>
				<td>
					<select id="companyId" class="frmSelectClass" onchange="filterWarehouse();">
						<option selected='selected' value=-1>Please select a company</option>
						<c:forEach var="company" items="${companies}">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Warehouse </td>
				<td>
					<select id="warehouseId" class="frmSelectClass"></select>
				</td>
			</tr>
			<tr>
				<td>Supplier</td>
				<td>
					<input id="txtSupplierName" class="input" onkeydown="loadSuppliers();"/>
					<input type="hidden" id="supplierId">
				</td>
			</tr>
			<tr>
				<td></td>
				<td><span id="spanSupplierError" class="error" style="color: red;"></span></td>
			</tr>
			<tr>
				<td>RR Number </td>
				<td>
					<input id="txtRRNumber" type="text" class="standard" />
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
					<select id="selectRRStatus" class="frmSelectClass">
						<option value="1">All</option>
						<option value="2" selected>Unused</option>
						<option value="3">Used</option>
					</select>
					<input type="button" id="btnSearchRTSReference" value="Search" onclick="filterRRReference();"/>
				</td>
			</tr>
		</table>
	</div>
	<fieldset style="margin-top : 20px;"  class="frmField_set">
		<legend>RR Reference Table</legend>	
			<div id="divRTSRefTable">
				<%@ include file = "R_RRReferenceTable.jsp" %>
			</div>
	</fieldset>
	<table class="frmField_set" style="margin-left: 88%;">
		<tr>	
			<td>
				<input type="button" id="btnOKRTSReference" value="OK" onclick="selectRRReference();" style=" margin-top : 20px; "/>
			</td>
		</tr>
	</table>
</div>
</body>
</html>