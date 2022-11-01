<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description: Receiving report reference form JSP page -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	loadRrReferenceDivision("${divisionId}");
	$("#slctRrReferenceDivisionId").attr("disabled","disabled");

	var newDate = parseServerDate(new Date);
	$("#dateFromId").val(newDate);
	$("#dateToId").val(newDate);
});

function loadRrReferenceDivision(divisionId) {
	var companyId = $("#slctRrReferenceCompanyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+"&divisionId="+(divisionId != null && divisionId == "" ? divisionId : 0);
	$("#slctRrReferenceDivisionId").empty();
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
				$("#slctRrReferenceDivisionId").val(divisionId);
			}
		}
	};
	loadPopulate (uri, false, divisionId, "slctRrReferenceDivisionId", optionParser, postHandler);
}

function loadRrReferenceSupplier() {
	var companyId = $("#slctRrReferenceCompanyId").val();
	var supplierName = processSearchName($("#txtRrReferenceSupplierId").val());
	var uri = contextPath + "/getSuppliers/new?name="+supplierName+"&companyId="+companyId+"&divisionId=${divisionId}";
	loadACItems("txtRrReferenceSupplierId", "hdnRrReferenceSupplierId", null, uri, uri+"&isExact=true", "name",
		function() {
			$("#spanRrReferenceSupplierError").text("");
			validateRrReferenceSupplier();
		}, function() {
			$("#spanRrReferenceSupplierError").text("");
			validateSupplier();
		}, function() {
			$("#spanRrReferenceSupplierError").text("");
		}, function() {
			$("#spanRrReferenceSupplierError").text("Invalid Supplier.");
		}
	);
}

function validateRrReferenceSupplier() {
	var supplierName = $.trim($("#txtRrReferenceSupplierId").val());
	if (supplierName != "") {
		var supplierId = $("#hdnRrReferenceSupplierId").val();
		$.ajax({
			url: contextPath+"/getSupplier?name="+processSearchName(supplierName),
			success : function(supplier) {
				if (supplier == null) {
					$("#spanRrReferenceSupplierError").text("Invalid Supplier.");
				}
			},
			error : function(error) {
				console.log(error);
				$("#spanRrReferenceSupplierError").text("Invalid supplier.");
			},
			dataType: "json"
		});
	} else {
		 $("#hdnRrReferenceSupplierId").val("");
	}
}

var rrReferenceId = null
function selectRrReference() {
	if (rrReferenceId ==  null) {
		alert("Please select a receiving report reference form.");
	} else {
		loadRrReference(rrReferenceId);
	}
}

function filterRrReferences() {
	$("#spanRrNumberError").text("");
	var rrNumber = $("#txtRrNumberId").val();
	if (!isNaN(rrNumber)) {
		$("#slctRrReferenceDivisionId").removeAttr("disabled");
		$("#divRrReferenceTable").load(getCommonParam()+"&pageNumber=1");
		$("#slctRrReferenceDivisionId").attr("disabled","disabled");
	} else {
		$("#spanRrNumberError").text("Receiving report number must be in numerical value.");
	}
}

function getCommonParam() {
	var companyId = $("#slctRrReferenceCompanyId").val();
	var divisionId = $("#slctRrReferenceDivisionId").val();
	var supplierId = $("#hdnRrReferenceSupplierId").val();
	var rrNumber = $.trim($("#txtRrNumberId").val());
	var bmsNumber = $.trim($("#txtBmsNumberId").val());
	var dateFrom = $.trim($("#dateFromId").val());
	var dateTo = $.trim($("#dateToId").val());
	var status = $("#selectRrStatus").val();
	return contextPath+"/apInvoiceGS/getReceivingReports?companyId="+companyId+"&divisionId="+divisionId
			+"&supplierId="+supplierId+"&rrNumber="+rrNumber+"&bmsNumber="+bmsNumber+"&dateFrom="+dateFrom
			+"&dateTo="+dateTo+"&status="+status;
}
</script>
<style type="text/css"></style>
</head>
<body>
<div id="divPoReferenceFormId">
	<h3 style="text-align: center;">RR Reference</h3>
	<div>
		<table class="frmField_set">
			<tr>
				<td>Company</td>
				<td>
					<select id="slctRrReferenceCompanyId" class="frmSelectClass">
						<c:forEach items="${companies}" var="company">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Division</td>
				<td>
					<select id="slctRrReferenceDivisionId" class="frmSelectClass"></select>
				</td>
			</tr>
			<tr>
				<td>Supplier</td>
				<td>
					<input type="hidden" id="hdnRrReferenceSupplierId"/>
					<input id="txtRrReferenceSupplierId" class="input" onkeydown="loadRrReferenceSupplier();"
						onblur="validateRrReferenceSupplier();"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanRrReferenceSupplierError" class="error"></span>
				</td>
			</tr>
			<tr>
				<td>RR Number</td>
				<td>
					<input id="txtRrNumberId" class="input"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanRrNumberError" class="error"></span>
				</td>
			</tr>
			<tr>
				<td>BMS Number</td>
				<td>
					<input id="txtBmsNumberId" class="input"/>
				</td>
			</tr>
			<tr>
				<td>Date From</td>
				<td>
					<input onblur="evalDate('dateFromId')" id="dateFromId" style="width: 120px;" class="dateClass2"/>
					<img id="imgDate10" src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateFromId')" style="cursor: pointer"
						style="float: right;"/>&nbsp;&nbsp;To&nbsp;&nbsp;
					<input onblur="evalDate('dateToId')" id=dateToId style="width: 120px;" class="dateClass2"/>
					<img id="imgDate11" src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateToId')" style="cursor: pointer"
						style="float: right;"/>&nbsp;&nbsp;
				</td>
			</tr>
			<tr>
				<td>Status</td>
				<td>
					<select id="selectRrStatus" class="frmSelectClass">
						<option value="1">All</option>
						<option value="2" selected>Unused</option>
						<option value="3">Used</option>
					</select>&nbsp;&nbsp;
					<input type="button" id="btnFilterRrReferences" value="Search" onclick="filterRrReferences();"/>
				</td>
			</tr>
		</table>
			<fieldset style="margin-top: 20px;" class="frmField_set">
			<legend>Reference Table</legend>
			<div id="divRrReferenceTable">
				<%@ include file = "RrReferenceTable.jsp" %>
			</div>
		</fieldset>
		<table class="frmField_set" style="margin-left: 88%;">
			<tr>
				<td>
					<input type="button" id="btnExtractRr" value="Extract"
						onclick="selectRrReference();" style=" margin-top : 20px; "/>
				</td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>