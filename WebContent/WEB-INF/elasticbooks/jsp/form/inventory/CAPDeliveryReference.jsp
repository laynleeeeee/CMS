<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: CAP Delivery Reference pop-up page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script src="${pageContext.request.contextPath}/js/jquery/jquery.js" type="text/javascript"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery/jquery-ui-1.10.3.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/css/jquery-ui-1.10.3.css" rel="stylesheet" type="text/css"  />
<script type="text/javascript">
var currentReference = null;
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

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

function filterReferences() {
	if($("#companyId").val() != -1) {
		$("#capDeliveryRefDiv").load(getCommonParams()+"&pageNumber=1");
	} else {
		alert("Please select a company.");
	}
}

function getCommonParams() {
	var typeId = "${typeId}";
	var companyId = $("#companyId").val();
	var customerId = $.trim($("#arCustomerId").val()) != "" ? $.trim($("#arCustomerId").val()) : -1;
	var capNo = $.trim($("#txtCAPNo").val()) != "" ? $.trim($("#txtCAPNo").val()) : -1;
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var status = $("#statusId").val();

	return contextPath+"/capDelivery/"+typeId+"/capReference?companyId="+companyId+
			"&customerId="+customerId+"&capNo="+capNo+"&dateFrom="+
			dateFrom+"&dateTo="+dateTo+"&statusId="+status;
}

function selectReference() {
	if(currentReference != null) {
		var referenceId = $(currentReference).attr("id");
		var opener = window.opener;
		opener.loadReference(referenceId);
	} else {
		alert("Please select a reference.");
	}
}

function companyOnChange(){
	$("#txtCustomer").val("");
	$("#arCustomerId").val("");
}
</script>
</head>
<body>
<div>
	<h3 style="text-align: center;">Customer Advance Payment
		<c:if test="${typeId == 3}"> - IS</c:if> Reference
	</h3>
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
					<input id="txtCustomer" class="input" onkeydown="showCustomers();" onkeyup="showCustomers();"/>
					<input type="hidden" id="arCustomerId">
				</td>
			</tr>
			<tr>
				<td></td>
				<td><span id="spanCustomerError" class="error"></span></td>
			</tr>
			<tr>
				<td>CAP Number </td>
				<td><input id="txtCAPNo" type="text" class="standard"/></td>
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
					<select id="statusId" class="frmSelectClass">
						<option value="1">All</option>
						<option value="2" selected>Unused</option>
						<option value="3">Used</option>
					</select>
					<input type="button" id="btnSearchCAPs" value="Search" onclick="filterReferences();"/>
				</td>
			</tr>
		</table>
	</div>
	<fieldset style="margin-top : 20px;"  class="frmField_set">
		<legend>CAP <c:if test="${typeId == 3}"> - IS</c:if> Reference Table</legend>
			<div id="capDeliveryRefDiv">
				<%@ include file = "CAPDeliveryRefTable.jsp" %>
			</div>
	</fieldset>
	<table class="frmField_set" style="margin-left: 88%;">
		<tr>
			<td>
				<input type="button" id="btnOk" value="OK" onclick="selectReference();" style=" margin-top : 20px; "/>
			</td>
		</tr>
	</table>
</div>
</body>
</html>