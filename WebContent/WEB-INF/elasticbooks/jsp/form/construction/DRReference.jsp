<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Delivery receipt reference form for AR invoice form
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
var slctdDrRefIds = [];
$(document).ready(function () {
	loadDivision("${divisionId}");
	$("#refDivisionId").attr("disabled","disabled");
});

function selectDRReference() {
	if (slctdDrRefIds.length == 0) {
		alert("Please select at least one DR form.");
	} else {
		loadDRReference(slctdDrRefIds);
	}
}

function getCommonParam() {
	var companyId = $("#refCompanyId").val();
	var arCustomerId = $("#hdnTxtCustomerId").val();
	var arCustomerAcctId = $("#refCustomerAccountId").val();
	var drNumber = $.trim($("#txtDRNumber").val());
	var drTypeId = $.trim($("#hdnDrTypeId").val());
	var divisionId = $("#refDivisionId").val();
	var arInvoiceTypeId = "${arInvoiceTypeId}";
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var drRefNumber = $.trim($("#txtDRRefNumber").val());
	return contextPath+"/arInvoice/getDRReferenceForms?companyId="+companyId
			+"&arCustomerId="+arCustomerId+"&arCustomerAcctId="+(arCustomerAcctId != null ? arCustomerAcctId : "")
			+"&drNumber="+drNumber+"&status=2"+"&divisionId="+divisionId+"&arInvoiceTypeId="+arInvoiceTypeId
			+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&drRefNumber="+drRefNumber;
}

function filterDRReferences() {
	var drNumber = $("#txtDRNumber").val();
	if (!isNaN(drNumber)) {
		$("#divDRRefTable").load(getCommonParam()+"&pageNumber=1");
		$("#spanDRNumberErrorMsg").text("");
	} else {
		$("#spanDRNumberErrorMsg").text("Only numerical DR number is allowed.");
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

</script>
<style type="text/css"></style>
</head>
<body>
<div id="divDRReferenceFormId">
	<h3 style="text-align: center;">
		<c:choose>
			<c:when test="${arInvoiceTypeId ne 2}">DR </c:when>
			<c:otherwise>DR/WB/EU </c:otherwise>
		</c:choose>
		 Reference
	</h3>
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
				<td>DR No.</td>
				<td>
					<input id="txtDRNumber" class="input">&nbsp;&nbsp;
				</td>
			</tr>
			<tr>
				<td>DR Reference No.</td>
				<td>
					<input id="txtDRRefNumber" class="input">&nbsp;&nbsp;
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
					<input type="button" id="btnFilterDRRefs" value="Search" onclick="filterDRReferences();"/>
				</td>
			</tr>
			<tr style="display: none;">
				<td>Status</td>
				<td>
					<select id="slctReferenceStatus" class="frmSelectClass">
						<option value="1">All</option>
						<option value="2" selected>Unused</option>
						<option value="3">Used</option>
					</select>
				</td>
			</tr>
		</table>
			<fieldset style="margin-top : 20px;"  class="frmField_set">
			<legend>DR Reference Table</legend>
			<div id="divDRRefTable">
				<%@ include file = "DRReferenceTable.jsp" %>
			</div>
		</fieldset>
		<table class="frmField_set" style="margin-left: 88%;">
			<tr>
				<td>
					<input type="button" id="btnExtractDRRef" value="Extract"
						onclick="selectDRReference();" style=" margin-top : 20px; "/>
				</td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>