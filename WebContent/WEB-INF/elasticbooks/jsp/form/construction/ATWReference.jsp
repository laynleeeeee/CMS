<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Authority to withdraw reference form for delivery receipt form
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
var atwRefId = null
function selectATWReference() {
	if (atwRefId ==  null) {
		alert("Please select a ATW form.");
	} else {
		loadATWReference(atwRefId);
	}
}

function getCommonParam() {
	var companyId = $("#refCompanyId").val();
	var arCustomerId = $("#hdnTxtCustomerId").val();
	var arCustomerAcctId = $("#refCustomerAccountId").val();
	var atwNumber = $.trim($("#txtATWNumber").val());
	var drTypeId = $.trim($("#hdnDrTypeId").val());
	return contextPath+"/deliveryReceipt/getATWReferenceForms?companyId="+companyId
			+"&arCustomerId="+arCustomerId+"&arCustomerAcctId="+(arCustomerAcctId != null ? arCustomerAcctId : "")
			+"&atwNumber="+atwNumber+"&status=2"+"&drTypeId="+drTypeId;
}

function filterATWReferences() {
	var atwNumber = $("#txtATWNumber").val();
	if (!isNaN(atwNumber)) {
		$("#divATWRefTable").load(getCommonParam()+"&pageNumber=1");
		$("#spanATWNumberErrorMsg").text("");
	} else {
		$("#spanATWNumberErrorMsg").text("Only numerical ATW number is allowed.");
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
<div id="divATWReferenceFormId">
	<h3 style="text-align: center;">ATW Reference</h3>
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
				<td>ATW No.</td>
				<td>
					<input id="txtATWNumber" class="input">&nbsp;&nbsp;
					<input type="button" id="btnFilterATWRefs" value="Search" onclick="filterATWReferences();"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanATWNumberErrorMsg" class="error"></span>
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
			<legend>ATW Reference Table</legend>
			<div id="divATWRefTable">
				<%@ include file = "ATWReferenceTable.jsp" %>
			</div>
		</fieldset>
		<table class="frmField_set" style="margin-left: 88%;">
			<tr>
				<td>
					<input type="button" id="btnExtractATWRef" value="Extract"
						onclick="selectATWReference();" style=" margin-top : 20px; "/>
				</td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>