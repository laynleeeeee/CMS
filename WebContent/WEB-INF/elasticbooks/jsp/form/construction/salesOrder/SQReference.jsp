<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Sales quotation reference form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sales Quotation Reference</title>
<style>
#tblASReference {
	cellspacing: 0;
	border: none;
}

#tblASReference thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#tblASReference tbody td {
	border-top: 1px solid #000000;
}

.tdProperties {
	border-right: 1px solid #000000;
}
</style>
<script type="text/javascript">
var sqId = null;
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function getCommonParam() {
	var companyId = $("#companyId").val();
	var arCustomerId = $("#sqArCustomerId").val();
	var arCustomerAccountId = $("#sqArCustomerAccountId").val() != null ? $("#sqArCustomerAccountId").val() : "";
	var sqNumber = processSearchName($("#txtSQNumber").val());
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var status = $("#selectCSStatus").val();

	return contextPath + "/salesOrder/sqReference/reload?companyId="+companyId
		+"&arCustomerId="+arCustomerId+"&arCustomerAccountId="+arCustomerAccountId+"&sqNumber="+sqNumber
		+"&dateFrom="+dateFrom+"&dateTo="+dateTo;
}

function filterSQReference() {
	if ($.trim($("#txtSQCustomer").val()) == "") {
		showSqCustomers ();
	}
// 	if ($("#companyId").val() != -1) {
	var sqNumber = $("#txtSQNumber").val();
	if(!isNaN(sqNumber)) {
		$("#divSQRefTable").load(getCommonParam()+"&pageNumber=1");
		$("#spanRefNoMsg").text("");
	} else {
		$("#spanRefNoMsg").text("Only numerical sequence number is allowed.");
	}
// 	}
}

function selectSQReference() {
	if (sqId == null) {
		alert("Please select a sales order.");
	}else if (sqArCustomerId == null) {
		alert("Please select a customer.");
	}else if (sqArCustomerAccountId == null) {
		alert("Please select a customer account.");
 	}else{
		loadSQReference($(sqId).attr("id"));
 	}
}

function getSqCustomer () {
	var companyId = $("#companyId").val();
	var name = encodeURIComponent($.trim($("#txtSQCustomer").val()));
	$.ajax({
		url: contextPath + "/getArCustomers/new?name="+name+"&isExact=true"+
				"&companyId="+companyId,
		success : function(customer) {
			$("#spanSqCustomerError").text("");
			if (customer != null && customer[0] != undefined) {
				$("#sqArCustomerId").val(customer[0].id);
				$("#txtSQCustomer").val(customer[0].name);
			}
			filterSqCustomerAccts();
		},
		error : function(error) {
			$("#spanSqCustomerError").text("Invalid customer.");
			$("#txtSQCustomer").val("");
			$("#sqArCustomerAccountId").empty();
		},
		dataType: "json"
	});
}

function showSqCustomers () {
	var companyId = $("#companyId").val();
	var name = encodeURIComponent($.trim($("#txtSQCustomer").val()));
	var uri = contextPath + "/getArCustomers/new?name="+name+"&companyId="+companyId;
	$("#txtSQCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#sqArCustomerId").val(ui.item.id);
			$("#spanSqCustomerError").text("");
			$(this).val(ui.item.name);
			filterSqCustomerAccts();
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					console.log(item);
					if(item != "") {
						$("#spanSqCustomerError").text("");
						if (ui.item != null) {
							$(this).val(ui.item.name);
						}
						filterSqCustomerAccts();
					} else {
						$("#spanSqCustomerError").text("Invalid customer.");
						$("#sqArCustomerId").val("");
					}
				},
				error : function(error) {
					$("#spanSqCustomerError").text("Invalid customer.");
					$("#txtSQCustomer").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function filterSqCustomerAccts(){
	$("#sqArCustomerAccountId").empty();

	if ($.trim($("#txtSQCustomer").val()) == "")
		$("#sqArCustomerId").val("");
	else {
		var customerId = $("#sqArCustomerId").val();
		var companyId = $("#companyId").val();
		var uri = contextPath + "/getArCustomerAccounts?arCustomerId="+customerId+"&companyId="+companyId;
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
					$("#sqArCustomerAccountId option").each(function() {
						if($.inArray(this.value, found) != -1) 
							$(this).remove();
					  	found.push(this.value);
					});
				}
		};
		loadPopulate (uri, false, null, "sqArCustomerAccountId", optionParser, postHandler);
	}
}
</script>
</head>
<body>
<div id="divCSRReference">
	<h3 style="text-align: center;">SQ Reference</h3>
	<div>
		<table class="frmField_set">
			<tr>
				<td>Customer</td>
				<td>
					<input type="hidden" id="sqArCustomerId"> 
					<input id="txtSQCustomer" class="input" onkeydown="showSqCustomers();" onkeyup="showSqCustomers();" onblur="getSqCustomer();">
				</td>
			</tr>
			<tr>
				<td>Customer Account</td>
				<td>
					<select id="sqArCustomerAccountId" class="frmSelectClass">
					</select>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanSqCustomerError" class="error"></span>
				</td>
			</tr>
			<tr>
				<td>SQ No.</td>
				<td>
					<input id="txtSQNumber" type="text" class="standard" />
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span id="spanRefNoMsg" class="error"></span>
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
					<input type="button" id="btnSearchCSReference" value="Search" onclick="filterSQReference();"/>
				</td>
			</tr>
		</table>
	</div>
	<fieldset style="margin-top : 20px;"  class="frmField_set">
		<legend>Reference Table</legend>
			<div id="divSQRefTable">
				<%@ include file = "SQReferenceTable.jsp" %>
			</div>
	</fieldset>
	<table class="frmField_set" style="margin-left: 88%;">
		<tr>
			<td>
				<input type="button" id="#tblASReference" value="OK" onclick="selectSQReference();" style=" margin-top : 20px; "/>
			</td>
		</tr>
	</table>
</div>
</body>
</html>