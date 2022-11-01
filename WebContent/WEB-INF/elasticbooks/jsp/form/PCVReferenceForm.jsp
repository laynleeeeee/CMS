<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Petty Cash Voucher reference form for Petty Cash Voucher Liquidation form
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

var pcvReferenceId = null;
function selectPCVReference() {
	if (pcvReferenceId == null) {
		alert("Please select a petty cash voucher.");
	} else {
		loadPCVReference(pcvReferenceId);
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
	var divisionId = $("#refDivisionId").val();
	var userCustodianId = $("#hdnCustodianAccountId").val();
	var requestor = $("#txtRequestor").val();
	var pcvNumber = $.trim($("#txtPCVNo").val());
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	return contextPath+"/pettyCashVoucherLiquidation/getPCVReferences?companyId="+companyId
			+"&divisionId="+divisionId+"&userCustodianId="+userCustodianId
			+"&requestor="+processSearchName(requestor)+"&pcvNumber="+pcvNumber+"&dateFrom="+dateFrom+"&dateTo="+dateTo;
}

function filterPCVReferences() {
	var pcvNumber = $("#txtPCVNo").val();
	if (!isNaN(pcvNumber)) {
		$("#divPCVRefTable").load(getCommonParam()+"&pageNumber=1");
		$("#spanPCVNoErrorMsg").text("");
	} else {
		$("#spanPCVNoErrorMsg").text("Only numerical PCV number is allowed.");
	}
}

function showCustodianAccounts() {
	var custodianName = $("#txtCustodianAccountName").val();
	var companyId = $("#refCompanyId").val();
	var divisionId = $("#refDivisionId").val();
	var uri = contextPath + "/getCustodianAccounts/new?name="+processSearchName(custodianName)
			+"&companyId="+companyId+"&divisionId="+divisionId+"&isExact=false";
	$("#txtCustodianAccountName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnCustodianAccountId").val(ui.item.id);
			$(this).val(ui.item.custodianName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.custodianName + "</a>" )
			.appendTo( ul );
	};
}

function getRefUserCustodian() {
	var custodianName = $.trim($("#txtCustodianAccountName").val());
	var divisionId = $("#refDivisionId").val();
	var companyId = $("#companyId").val();
	if (custodianName != "") {
		var uri = contextPath + "/getUserCustodians?name="+processSearchName(custodianName)
				+"&companyId="+companyId+"&divisionId="+divisionId;
		$.ajax({
			url: uri,
			success : function(userCustodian) {
				$("#spanCustodianAccount").text("");
				if (userCustodian != null && userCustodian[0] != undefined) {
					$("#hdnCustodianAccountId").val(userCustodian[0].id);
					$("#txtCustodianAccountName").val(userCustodian[0].custodianAccount.custodianName);
				} else {
					$("#hdnCustodianAccountId").val("");
					$("#spanCustodianAccount").text("Invalid custodian account.");
				}
			},
			error : function(error) {
				$("#spanCustodianAccount").text("Invalid custodian account.");
			},
			dataType: "json"
		});
	} else {
		$("#hdnCustodianAccountId").val("");
	}
}

</script>
<style type="text/css"></style>
</head>
<body>
<div id="divSOReferenceFormId">
	<h3 style="text-align: center;">PCV  Reference</h3>
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
				<td>Custodian</td>
				<td>
					<input type="hidden" id="hdnCustodianAccountId"> 
					<input id="txtCustodianAccountName" class="input" onkeyup="showCustodianAccounts();" onblur="getRefUserCustodian();">
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanCustodianAccount" class="error"></span>
				</td>
			</tr>
			<tr>
				<td>Requestor</td>
				<td>
					<input id="txtRequestor" class="input">
				</td>
			</tr>
			<tr>
				<td>PCV No.</td>
				<td>
					<input id="txtPCVNo" class="input">
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanPCVNoErrorMsg" class="error"></span>
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
					<input type="button" id="btnFilterSORefs" value="Search" onclick="filterPCVReferences();"/>
				</td>
			</tr>
		</table>
			<fieldset style="margin-top : 20px;"  class="frmField_set">
			<legend>Reference Table</legend>
			<div id="divPCVRefTable">
				<%@ include file = "PCVReferenceTable.jsp" %>
			</div>
		</fieldset>
		<table class="frmField_set" style="margin-left: 88%;">
			<tr>
				<td>
					<input type="button" id="btnExtractPCVRef" value="Extract"
						onclick="selectPCVReference();" style=" margin-top : 20px; "/>
				</td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>