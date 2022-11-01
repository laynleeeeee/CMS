<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Sales order reference form for Customer Advance Payment form
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

var salesOrderRefId = null;
function selectSOReference() {
	if (salesOrderRefId == null) {
		alert("Please select a sales order form.");
	} else {
		loadSalesOrderReference(salesOrderRefId);
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
	var arCustomerId = $("#hdnTxtCustomerId").val();
	var arCustomerAcctId = $("#refCustomerAccountId").val();
	var soNumber = $.trim($("#txtSONumber").val());
	var status = $("#slctReferenceStatus").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var poNumber = encodeURIComponent($.trim($("#txtPoPcrNo").val()));
	var divisionId = $("#refDivisionId").val();
	return contextPath+"/customerAdvancePayment/getSalesOrdeRefs?companyId="+companyId
			+"&arCustomerId="+arCustomerId+"&arCustomerAcctId="+(arCustomerAcctId != null ? arCustomerAcctId : "")
			+"&soNumber="+soNumber+"&status="+status+"&dateFrom="+dateFrom+"&dateTo="+dateTo
			+"&poNumber="+poNumber+"&divisionId="+divisionId+"&statusId="+status;
}

function filterSOReferences() {
	var soNumber = $("#txtSONumber").val();
	if (!isNaN(soNumber)) {
		$("#divSORefTable").load(getCommonParam()+"&pageNumber=1");
		$("#spanSONumberErrorMsg").text("");
	} else {
		$("#spanSONumberErrorMsg").text("Only numerical SO number is allowed.");
	}
}

function showRefCustomers() {
	$("#spanCustomerError").text("");
	var companyId = $("#refCompanyId").val();
	var name = $.trim($("#txtCustomer").val());
	var uri = contextPath+"/getArCustomers/new?name="+encodeURIComponent(name)+"&companyId="+companyId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(this).val(ui.item.name);
			$("#hdnTxtCustomerId").val(ui.item.id);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function getRefCustomer() {
	$("#spanCustomerError").text("");
	var companyId = $("#refCompanyId").val();
	var name = $.trim($("#txtCustomer").val());
	if (name != "") {
		$.ajax({
			url: contextPath+"/getArCustomers/new?name="+encodeURIComponent(name)+"&isExact=true"
					+"&companyId="+companyId,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					$("#txtCustomer").val(customer[0].name);
					$("#hdnTxtCustomerId").val(customer[0].id);
				} else {
					$("#spanCustomerError").text("Invalid customer.");
					$("#hdnTxtCustomerId").val("");
					$("#refCustomerAccountId").empty();
				}
				filterRefCustomerAccts();
			},
			error : function(error) {
				$("#spanCustomerError").text("Invalid customer.");
				$("#hdnTxtCustomerId").val("");
				$("#refCustomerAccountId").empty();
			},
			dataType: "json"
		});
	} else {
		$("#hdnTxtCustomerId").val("");
		$("#refCustomerAccountId").empty();
	}
}

function filterRefCustomerAccts() {
	$("#refCustomerAccountId").empty();
	var customerId = $("#hdnTxtCustomerId").val();
	var companyId = $("#refCompanyId").val();
	var uri = contextPath+"/getArCustomerAccounts?arCustomerId="+customerId+"&companyId="+companyId;
	var optionParser = {
			getValue: function (rowObject){
				if (rowObject != null)
					return rowObject["id"];
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
				$("#refCustomerAccountId option").each(function() {
					if($.inArray(this.value, found) != -1) 
						$(this).remove();
				  	found.push(this.value);
				});
			}
	};
	loadPopulate (uri, false, null, "refCustomerAccountId", optionParser, postHandler);
}
</script>
<style type="text/css"></style>
</head>
<body>
<div id="divSOReferenceFormId">
	<h3 style="text-align: center;">SO Reference</h3>
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
				<td>Customer</td>
				<td>
					<input type="hidden" id="hdnTxtCustomerId"> 
					<input id="txtCustomer" class="input" onkeyup="showRefCustomers();" onblur="getRefCustomer();">
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanCustomerError" class="error"></span>
				</td>
			</tr>
			<tr>
				<td>Customer Account</td>
				<td>
					<select id="refCustomerAccountId" class="frmSelectClass"></select>
				</td>
			</tr>
			<tr>
				<td>SO No.</td>
				<td>
					<input id="txtSONumber" class="input">
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanSONumberErrorMsg" class="error"></span>
				</td>
			</tr>
			<tr>
				<td>PO/PCR No.</td>
				<td>
					<input id="txtPoPcrNo" class="input">
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
					<input type="button" id="btnFilterSORefs" value="Search" onclick="filterSOReferences();"/>
				</td>
			</tr>
		</table>
			<fieldset style="margin-top : 20px;"  class="frmField_set">
			<legend>SO Reference Table</legend>
			<div id="divSORefTable">
				<%@ include file = "CapSoReferenceTable.jsp" %>
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