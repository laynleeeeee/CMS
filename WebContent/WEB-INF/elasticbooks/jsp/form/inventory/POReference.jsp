<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Purchase oder reference form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<title>PO Reference</title>
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
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
	loadPoRefDivisions(defaultDivisionId);
	$("#slctDivisionId").attr("disabled","disabled");
});

function getCommonParam() {
	var companyId = $("#companyId").val();
	var supplierId = $("#supplierId").val();
	var poNumber = processSearchName($("#txtPONumber").val());
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var status = $("#selectPOStatus").val();
	var divisionId = $("#slctDivisionId").val();
	var bmsNumber = processSearchName($("#txtBmsNo").val());
	var uri = contextPath+"/retailReceivingReport/loadPos?companyId=" + companyId + "&supplierId=" + supplierId+ "&divisionId=" + divisionId +
		"&poNumber=" + poNumber+ "&bmsNumber=" + bmsNumber + "&dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&status=" + status;
	return uri;
}

function filterPOReference() {
	if ($("#companyId").val() != -1) {
		$("#divRRRefTable").load(getCommonParam()+"&pageNumber=1");
	} else
		alert("Please select a company.");
}

function selectPOReference() {
	if (poId == null) {
		alert("Please select a purchase order.");
	}else if(companyId == -1) {
		alert("Please select a company.");
	}else{
		loadPOReference (poId);
 	}
}

function loadSuppliers() {
	var supplierName = $("#txtSupplierName").val();
	if(supplierName != "") {
		var companyId = $("#companyId").val();
		var divisionId = $("#slctDivisionId").val();
		supplierName = processSearchName($.trim(supplierName));
		var uri = contextPath + "/getSuppliers/new?name="+supplierName+"&divisionId="+divisionId;
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

function loadPoRefDivisions(divisionId) {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+"&divisionId="+ (divisionId != null && divisionId == "" ? divisionId : 0);
	$("#slctDivisionId").empty();
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
			if (divisionId != 0 && divisionId != "" && divisionId != "undefined") {
				$("#slctDivisionId").val(divisionId);
			}
		}
	};
	loadPopulate (uri, false, divisionId, "slctDivisionId", optionParser, postHandler);
}
</script>
</head>
<body>
<div id="divRRReference">
	<h3 style="text-align: center;">PO Reference</h3>
	<div>
		<table class="frmField_set">
			<tr>
				<td>Company</td>
				<td>
					<select id="companyId" class="frmSelectClass">
						<c:forEach var="company" items="${companies}">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Division</td>
				<td>
					<select id="slctDivisionId" class="frmSelectClass"></select>
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
				<td>PO No. </td>
				<td>
					<input id="txtPONumber" type="text" class="standard" />
				</td>
			</tr>
			<tr>
				<td>BMS No. </td>
				<td>
					<input id="txtBmsNo" type="text" class="standard" />
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
					<input type="button" id="btnSearchRRReference" value="Search" onclick="filterPOReference();"/>
				</td>
			</tr>
		</table>
	</div>
	<fieldset style="margin-top : 20px;"  class="frmField_set">
		<legend>Reference Table</legend>	
			<div id="divRRRefTable">
				<%@ include file = "POReferenceTable.jsp" %>
			</div>
	</fieldset>
	<table class="frmField_set" style="margin-left: 88%;">
		<tr>	
			<td>
				<input type="button" id="btnOKRRReference" value="OK" onclick="selectPOReference();" style=" margin-top : 20px; "/>
			</td>
		</tr>
	</table>
</div>
</body>
</html>