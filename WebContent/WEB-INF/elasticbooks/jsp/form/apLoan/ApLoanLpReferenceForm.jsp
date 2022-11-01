<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Loan proceeds reference form.
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
$(document).ready(function () {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
	loadDivision("${divisionId}");
	$("#refDivisionId").attr("disabled","disabled");
});

var lpRefId = null;
function selectSOReference() {
	console.log("extract:", lpRefId);
	if (lpRefId == null) {
		alert("Please select a sales order form.");
	} else {
		loadLpReference(lpRefId);
	}
}

function loadDivision(divisionId) {
	var companyId = $("#refCompanyId").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+"&divisionId="+ (divisionId != null && divisionId != "" ? divisionId : 0);
	$("#refDivisionId").empty();
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
				$("#refDivisionId").val(divisionId);
			}
		}
	};
	loadPopulate (uri, false, divisionId, "refDivisionId", optionParser, postHandler);
}

function getCommonParam() {
	var companyId = $("#refCompanyId").val();
	var supplierId = $("#hdnTxtSupplierId").val();
	var lpNumber = $.trim($("#txtLpNumber").val());
	var status = $("#slctReferenceStatus").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var poNumber = encodeURIComponent($.trim($("#txtPoPcrNo").val()));
	var divisionId = $("#refDivisionId").val();
	return contextPath+"/aPInvoiceForm/getLoanProceeds?companyId="+companyId
			+"&divisionId="+divisionId+"&supplierId="+supplierId+"&lpNumber="+lpNumber
			+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&statusId="+status;
}

function filterLpReferences() {
	var lpNumber = $("#txtLpNumber").val();
	if (!isNaN(lpNumber)) {
		$("#divSORefTable").load(getCommonParam()+"&pageNumber=1");
		$("#spanLpNumberErrorMsg").text("");
	} else {
		$("#spanLpNumberErrorMsg").text("Only numerical SO number is allowed.");
	}
}



function getSupplier() {
	$("#spanSupplierError").text("");
	var companyId = $("#refCompanyId").val();
	var name = $.trim($("#txtSupplier").val());
	var uri = contextPath + "/getSuppliers/new?name="+processSearchName(name)+"&isExact=true";
	if (name != "") {
		$.ajax({
			url: uri,
			success : function(supplier) {
				if (supplier != null && supplier[0] != undefined) {
					$("#txtSupplier").val(supplier[0].name);
					$("#hdnTxtSupplierId").val(supplier[0].id);
				} else {
					$("#spanSupplierError").text("Invalid supplier.");
					$("#hdnTxtSupplierId").val("");
				}
			},
			error : function(error) {
				$("#spanSupplierError").text("Invalid supplier.");
				$("#hdnTxtSupplierId").val("");
			},
			dataType: "json"
		});
	} else {
		$("#hdnTxtSupplierId").val("");
	}
}


function showSuppliers() {
	$("#spanSupplierError").text("");
	var companyId = $("#refCompanyId").val();
	var name = $.trim($("#txtSupplier").val());
	var divisionId = $("#refDivisionId").val();
	var uri = contextPath + "/getSuppliers/new?name="+processSearchName(name)
			+"&companyId="+companyId+"&divisionId="+divisionId;
	console.log(uri);
	$("#txtSupplier").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(this).val(ui.item.name);
			$("#hdnTxtSupplierId").val(ui.item.id);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}
</script>
<style type="text/css"></style>
</head>
<body>
<div id="divSOReferenceFormId">
	<h3 style="text-align: center;">Loan Proceeds</h3>
	<div>
		<table class="frmField_set">
			<tr>
				<td>Company</td>
				<td>
					<select class="frmSelectClass" id="refCompanyId">
						<c:forEach items="${companies}" var="company">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Division</td>
				<td>
					<select class="frmSelectClass" id="refDivisionId"></select>
				</td>
			</tr>
			<tr>
				<td>Lender</td>
				<td>
					<input type="hidden" id="hdnTxtSupplierId"> 
					<input id="txtSupplier" class="input" onkeyup="showSuppliers();" onblur="getSupplier();">
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanSupplierError" class="error"></span>
				</td>
			</tr>
			<tr>
				<td>LP No.</td>
				<td>
					<input id="txtLpNumber" class="input">
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanLpNumberErrorMsg" class="error"></span>
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
					<select id="slctReferenceStatus" class="frmSelectClass">
						<option value="1">All</option>
						<option value="2" selected>Unused</option>
						<option value="3">Used</option>
					</select>&nbsp;&nbsp;
					<input type="button" id="btnFilterSORefs" value="Search" onclick="filterLpReferences();"/>
				</td>
			</tr>
		</table>
			<fieldset style="margin-top : 20px;"  class="frmField_set">
			<legend>SO Reference Table</legend>
			<div id="divSORefTable">
				<%@ include file = "ApLoanLpReferenceTable.jsp" %>
			</div>
		</fieldset>
		<table class="frmField_set" style="margin-left: 88%;">
			<tr>
				<td>
					<input type="button" id="btnExtractSORef" value="Extract"
						onclick="selectSOReference();" style=" margin-top : 20px; "/>
				</td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>