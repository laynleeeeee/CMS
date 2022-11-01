<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Account sale reference form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/status.css" media="all">
<script src="${pageContext.request.contextPath}/js/jquery/jquery.js" type="text/javascript"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/formatUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxValidation.js"></script>
<title>Account Sales Reference</title>
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
var asId = null;
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function getCommonParam() {
	var companyId = $("#companyId").val();
	var arCustomerId = $("#arCustomerId").val();
	var arCustomerAccountId = $("#arCustomerAccountId").val() != null ? $("#arCustomerAccountId").val() : "";
	var asNumber = processSearchName($("#txtASNumber").val());
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var status = 2;

	return contextPath + "/accountSale/asReference/reload?companyId="+companyId
		+"&arCustomerId="+arCustomerId+"&arCustomerAccountId="+arCustomerAccountId+"&asNumber="+asNumber
		+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&status="+status;
}

function filterASReference() {
	if ($.trim($("#txtCustomer").val()) == "")
		showCustomers ();
	if ($("#companyId").val() != -1) {
		var asNumber = $("#txtASNumber").val();
		if(!isNaN(asNumber)) {
			$("#divASRefTable").load(getCommonParam()+"&pageNumber=1");
			$("#spanRefNoMsg").text("");
		} else {
			$("#spanRefNoMsg").text("Only numerical sequence number is allowed.");
		}
	} else {
		alert("Please select a company.");
	}
}

function selectASReference() {
	if (asId == null) {
		alert("Please select an account sale.");
	}else if(companyId == -1) {
		alert("Please select a company.");
	}else if (arCustomerId == null) {
		alert("Please select a customer.");
	}else if (arCustomerAccountId == null) {
		alert("Please select a customer account.");
 	}else{
		var opener = window.opener;
		opener.loadASReference ($(asId).attr("id"));
 	}
}

function getCustomer () {
	var companyId = $("#companyId").val();
	var name = encodeURIComponent($.trim($("#txtCustomer").val()));
	$.ajax({
		url: contextPath + "/getArCustomers/new?name="+name+"&isExact=true"+
				"&companyId="+companyId,
		success : function(customer) {
			$("#spanCustomerError").text("");
			if (customer != null && customer[0] != undefined) {
				$("#arCustomerId").val(customer[0].id);
				$("#txtCustomer").val(customer[0].name);
			}
			filterCustomerAccts();
		},
		error : function(error) {
			$("#spanCustomerError").text("Invalid customer.");
			$("#txtCustomer").val("");
			$("#arCustomerAccountId").empty();
		},
		dataType: "json"
	});
}

function showCustomers () {
	var companyId = $("#companyId").val();
	var name = encodeURIComponent($.trim($("#txtCustomer").val()));
	var uri = contextPath + "/getArCustomers/new?name="+name+"&companyId="+companyId;
	$("#txtCustomer").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#arCustomerId").val(ui.item.id);
			$("#spanCustomerError").text("");
			$(this).val(ui.item.name);
			filterCustomerAccts();
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					$("#spanCustomerError").text("");
					if (ui.item != null) {
						$(this).val(ui.item.name);
					}
					filterCustomerAccts();
				},
				error : function(error) {
					$("#spanCustomerError").text("Invalid customer.");
					$("#txtCustomer").val("");
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

function filterCustomerAccts(){
	$("#arCustomerAccountId").empty();

	if ($.trim($("#txtCustomer").val()) == "")
		$("#arCustomerId").val("");
	else {
		var customerId = $("#arCustomerId").val();
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
					$("#arCustomerAccountId option").each(function() {
						if($.inArray(this.value, found) != -1) 
							$(this).remove();
					  	found.push(this.value);
					});
				}
		};
		loadPopulate (uri, false, null, "arCustomerAccountId", optionParser, postHandler);
	}
}

function companyOnChange(){
	$("#txtCustomer").val("");
	$("#arCustomerId").val("");
	$("#arCustomerAccountId").empty();
}
</script>
</head>
<body>
<div id="divCSRReference">
	<h3 style="text-align: center;">SO Reference</h3>
	<div>
		<table class="frmField_set">
			<tr>
				<td>Company</td>
				<td>
					<select id="companyId" class="frmSelectClass" onchange="companyOnChange();">
						<option selected='selected' value=-1>Please select a company</option>
						<c:forEach var="company" items="${companies}">
							<option value="${company.id}">${company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Customer</td>
				<td>
					<input type="hidden" id="arCustomerId"> 
					<input id="txtCustomer" class="input" onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="getCustomer();">
				</td>
			</tr>
			<tr>
				<td>Customer Account</td>
				<td>
					<select id="arCustomerAccountId" class="frmSelectClass">
					</select>
				</td>
			</tr>
			<tr>
				<td>Reference No.</td>
				<td>
					<input id="txtASNumber" type="text" class="standard" />
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
					&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
					<input type="button" id="btnSearchCSReference" value="Search" onclick="filterASReference();"/>
				</td>
			</tr>
			<tr>
<!-- 				<td>Status</td> -->
<!-- 				<td> -->
<!-- 					<select id="selectCSStatus" class="frmSelectClass"> -->
<!-- 						<option value="2" selected>Unused</option> -->
<!-- 					</select> -->
<!-- 					<input type="button" id="btnSearchCSReference" value="Search" onclick="filterASReference();"/> -->
<!-- 				</td> -->
			</tr>
		</table>
	</div>
	<fieldset style="margin-top : 20px;"  class="frmField_set">
		<legend>Reference Table</legend>
			<div id="divASRefTable">
				<%@ include file = "ASPoReferenceTable.jsp" %>
			</div>
	</fieldset>
	<table class="frmField_set" style="margin-left: 88%;">
		<tr>
			<td>
				<input type="button" id="#tblASReference" value="OK" onclick="selectASReference();" style=" margin-top : 20px; "/>
			</td>
		</tr>
	</table>
</div>
</body>
</html>