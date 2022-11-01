<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description: Supplier advance payment - purchase order reference form JSP page -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	loadPoRefDivision("${divisionId}");
	$("#slctPoRefDivisionId").attr("disabled","disabled");

	var newDate = parseServerDate(new Date);
	$("#dateFromId").val(newDate);
	$("#dateToId").val(newDate);
});

function loadPoRefDivision(divisionId) {
	var companyId = $("#slctPoRefCompanyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+"&divisionId="+ (divisionId != null && divisionId == "" ? divisionId : 0);
	$("#slctPoRefDivisionId").empty();
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
				$("#slctPoRefDivisionId").val(divisionId);
			}
		}
	};
	loadPopulate (uri, false, divisionId, "slctPoRefDivisionId", optionParser, postHandler);
}

function loadPoRefSuppliers() {
	var companyId = $("#slctPoRefCompanyId").val();
	var supplierName = processSearchName($("#txtPoRefSupplierId").val());
	var uri = contextPath + "/getSuppliers/new?name="+supplierName+"&companyId="+companyId+"&divisionId=${divisionId}";
	loadACItems("txtPoRefSupplierId", "hdnPoRefSupplierId", null, uri, uri+"&isExact=true", "name",
		function() {
			$("#spanPoRefSupplierError").text("");
			validatePoRefSupplier();
		}, function() {
			$("#spanPoRefSupplierError").text("");
			validatePoRefSupplier();
		}, function() {
			$("#spanPoRefSupplierError").text("");
		}, function() {
			$("#spanPoRefSupplierError").text("Invalid Supplier.");
			validatePoRefSupplier();
		}
	);
}

function validatePoRefSupplier() {
	var supplierName = $.trim($("#txtPoRefSupplierId").val());
	if (supplierName != "") {
		var supplierId = $("#hdnPoRefSupplierId").val();
		$.ajax({
			url: contextPath+"/getSupplier?name="+processSearchName(supplierName),
			success : function(supplier) {
				if (supplier == null) {
					$("#spanPoRefSupplierError").text("Invalid Supplier.");
					$("#hdnPoRefSupplierId").val("");
				}
				filterPoRefSupplierAccts();
			},
			error : function(error) {
				console.log(error);
				$("#spanPoRefSupplierError").text("Invalid supplier.");
			},
			dataType: "json"
		});
	} else {
		 $("#hdnPoRefSupplierId").val("");
		 filterPoRefSupplierAccts();
	}
}

function filterPoRefSupplierAccts() {
	var companyId = $("#slctPoRefCompanyId").val();
	var supplierName = $.trim($("#txtPoRefSupplierId").val());
	var supplierId = $("#hdnPoRefSupplierId").val();
	$("#slctPoRefSupplierAcctId").empty();
	if (supplierName != "" && supplierId != "") {
		var uri = contextPath+"/getApSupplierAccts?supplierId="+supplierId+"&companyId="+companyId;
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		postHandler = {
			doPost: function() {
				// Do nothing
			}
		};
		loadPopulate(uri, false, $("#slctPoRefSupplierAcctId").val(), "slctPoRefSupplierAcctId", optionParser, postHandler);
	}
}

var purchaseOrderRefId = null
function selectPoReference() {
	if (purchaseOrderRefId ==  null) {
		alert("Please select a purchase order reference form.");
	} else {
		loadPurchaseOrderRef(purchaseOrderRefId);
	}
}

function filterPoReferences() {
	$("#spanPoNumberError").text("");
	var poNumber = $("#txtPoNumberId").val();
	if (!isNaN(poNumber)) {
		$("#slctPoRefDivisionId").removeAttr("disabled");
		$("#divPoRefTable").load(getCommonParam()+"&pageNumber=1");
		$("#slctPoRefDivisionId").attr("disabled","disabled");
	} else {
		$("#spanPoNumberError").text("Purchase order number must be in numerical value.");
	}
}

function getCommonParam() {
	var companyId = $("#slctPoRefCompanyId").val();
	var divisionId = $("#slctPoRefDivisionId").val();
	var supplierId = $("#hdnPoRefSupplierId").val();
	var supplierAcctId = $("#slctPoRefSupplierAcctId").val();
	var poNumber = $.trim($("#txtPoNumberId").val());
	var bmsNumber = $.trim($("#txtBmsNumberId").val());
	var dateFrom = $.trim($("#dateFromId").val());
	var dateTo = $.trim($("#dateToId").val());
	return contextPath+"/supplierAdvPayment/getPurchaseOrdeRefs?companyId="+companyId
			+"&divisionId="+divisionId+"&supplierId="+supplierId+"&supplierAcctId="+(supplierAcctId != null ? supplierAcctId : "")
			+"&poNumber="+poNumber+"&bmsNumber="+bmsNumber+"&dateFrom="+dateFrom+"&dateTo="+dateTo;
}
</script>
<style type="text/css"></style>
</head>
<body>
<div id="divPoReferenceFormId">
	<h3 style="text-align: center;">PO Reference</h3>
	<div>
		<table class="frmField_set">
			<tr>
				<td>Company</td>
				<td>
					<select id="slctPoRefCompanyId" class="frmSelectClass">
						<c:forEach items="${companies}" var="company">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Division</td>
				<td>
					<select id="slctPoRefDivisionId" class="frmSelectClass"></select>
				</td>
			</tr>
			<tr>
				<td>Supplier</td>
				<td>
					<input type="hidden" id="hdnPoRefSupplierId"/>
					<input id="txtPoRefSupplierId" class="input" onkeydown="loadPoRefSuppliers();"
						onblur="validatePoRefSupplier();"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanPoRefSupplierError" class="error"></span>
				</td>
			</tr>
			<tr>
				<td>Supplier Account</td>
				<td>
					<select id="slctPoRefSupplierAcctId" class="frmSelectClass"></select>
				</td>
			</tr>
			<tr>
				<td>PO Number</td>
				<td>
					<input id="txtPoNumberId" class="input"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanPoNumberError" class="error"></span>
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
					<input type="button" id="btnFilterPoRefs" value="Search" onclick="filterPoReferences();"/>
				</td>
			</tr>
		</table>
			<fieldset style="margin-top : 20px;"  class="frmField_set">
			<legend>Reference Table</legend>
			<div id="divPoRefTable">
				<%@ include file = "PoAdvPaymentReferenceTbl.jsp" %>
			</div>
		</fieldset>
		<table class="frmField_set" style="margin-left: 88%;">
			<tr>
				<td>
					<input type="button" id="btnExtractPurchaseOrder" value="Extract"
						onclick="selectPoReference();" style=" margin-top : 20px; "/>
				</td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>