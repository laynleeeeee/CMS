<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description: POS Middleware Setting form
	 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
selectedWarehouseId = Number("${posMiddlewareSetting.warehouseId}");
selectedCustomerAcctId = Number("${posMiddlewareSetting.arCustomerAccountId}");
selectedCompanyId = Number("${posMiddlewareSetting.companyId}");
$(document).ready(function() {
	if (Number("${posMiddlewareSetting.id}") == 0) {
		loadFrmWarehouses ();
	} else {
		$("#slctFrmCompany").val(Number("${posMiddlewareSetting.companyId}"));
		$("#slctFrmCompany").attr("disabled", "disabled");
		loadFrmWarehouses ();
		$("#txtFrmCustomerName").val("${fn:replace(posMiddlewareSetting.arCustomer.name, '\"', '\\\"')}");
		filterCustomerAccts();
	}
});

function loadFrmWarehouses () {
	if (selectedCompanyId == 0) {
		selectedCompanyId = $("#slctFrmCompany").val();
	}
	if(selectedCompanyId != "" && selectedCompanyId != null) {
		$("#slctFrmWarehouse").empty();
		var uri = contextPath+"/getWarehouse/byUserCompany?companyId="+selectedCompanyId
				+ "&isActiveOnly=true" + "&warehouseId=" 
				+ Number("${posMiddlewareSetting.warehouseId}");
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
					$("#slctFrmWarehouse option").each(function() {
						if($.inArray(this.value, found) != -1) 
							$(this).remove();
						found.push(this.value);
					});
				}
		};
		loadPopulate (uri, false, selectedWarehouseId, "slctFrmWarehouse", optionParser, postHandler);
	}
}

function showCustomers () {
	var customerName = $.trim($("#txtFrmCustomerName").val());
	var companyId = $("#slctFrmCompany").val();
	var uri = contextPath + "/getArCustomers/new?name="+customerName+"&companyId="+companyId;
	$("#txtFrmCustomerName").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnArCustomerId").val(ui.item.id);
			$(this).val(ui.item.name);
			filterCustomerAccts();
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.name);
					}
					filterCustomerAccts();
				},
				error : function(error) {
					$("#spanCustomerError").text("Invalid customer.");
					$("#txtFrmCustomerName").val("");
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

function getCustomer () {
	var customerName = encodeURIComponent($.trim($("#txtFrmCustomerName").val()));
	var noCustomerMatch = false;
	if (customerName != "") {
		var companyId = $("#slctFrmCompany").val();
		$.ajax({
			url: contextPath + "/getArCustomers/new?name="+customerName+"&isExact=true"+
					"&companyId="+companyId,
			success : function(customer) {
				$("#spanCustomerError").text("");
				if (customer != null && customer.length != 0) {
					$("#hdnArCustomerId").val(customer[0].id);
					$("#txtFrmCustomerName").val(customer[0].name);
				} else {
					noCustomerMatch = true;
				}
				filterCustomerAccts();
			},
			error : function(error) {
				clearCustomer();
			},
			complete : function () {
				if (noCustomerMatch) {
					clearCustomer();
				}
			},
			dataType: "json"
		});
	}
}

function clearCustomer() {
	$("#spanCustomerError").text("Invalid customer.");
	$("#frmSpanCustError").text("");
	$("#hdnArCustomerId").val("");
	$("#slctArCustomerAccountId").empty();
}


function filterCustomerAccts(){
	$("#slctArCustomerAccountId").empty();

	if ($.trim($("#txtFrmCustomerName").val()) == "")
		$("#hdnArCustomerId").val("");
	else {
		var customerId = $("#hdnArCustomerId").val();
		var companyId = $("#slctFrmCompany").val();
		var uri = contextPath + "/getArCustomerAccounts?arCustomerId="+customerId+"&companyId="+companyId;
		if (Number("${posMiddlewareSetting.id}") != 0) {
			uri = contextPath + "/getArCustomerAccounts/includeSavedInactive?arCustomerId="+customerId+"&companyId="+companyId
					+ "&arCustomerAccountId="+Number("${posMiddlewareSetting.arCustomerAccountId}");
		}
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
					$("#slctArCustomerAccountId option").each(function() {
						if($.inArray(this.value, found) != -1) 
							$(this).remove();
						found.push(this.value);
					});
				}
		};
		loadPopulate (uri, false, selectedCustomerAcctId, "slctArCustomerAccountId", optionParser, postHandler);
	}
}
function assignCompany (select) {
	selectedCompanyId = $(select).val();
}

function assignCustomerAcct (select) {
	selectedCustomerAcctId = $(select).val();
}
</script>
</head>
<body>
	<div class="formDiv">
		<form:form method="POST" commandName="posMiddlewareSetting" id="frmPosMiddlewareSetting">
			<div class="modFormLabel">POS Middleware Settings</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<table class="formTable">
						<form:hidden path="id"/>
						<form:hidden path="companyId" id="hdnFrmCompanyId"/>
						<form:hidden path="arCustomerId" id="hdnArCustomerId"/>
						<form:hidden path="createdBy"/>
						<form:hidden path="createdDate"/>
						<form:hidden path="customerName" id="hdnCustomerName"/>
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<select id="slctFrmCompany" class="frmSelectClass" 
									onchange="assignCompany (this); loadFrmWarehouses();">
									<c:forEach var="c" items="${companies}">
										<option value="${c.id}">${c.name}</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="companyId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Warehouse</td>
							<td class="value">
								<form:select path="warehouseId" id="slctFrmWarehouse" cssClass="frmSelectClass" >
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<form:errors path="warehouseId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer</td>
							<td class="value">
								<input id="txtFrmCustomerName" class="input" onkeydown="showCustomers();" onkeyup="showCustomers();" onblur="getCustomer();"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value">
								<span id="spanCustomerError" class="error"></span>
								<form:errors path="arCustomerId" id="frmSpanCustError" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Customer Account</td>
							<td class="value">
								<form:select path="arCustomerAccountId" id="slctArCustomerAccountId" cssClass="frmSelectClass" 
									onchange="assignCustomerAcct (this);">
								</form:select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="arCustomerAccountId" cssClass="error"/></td>
						</tr>
						<tr>
							<td class="labels">Active</td>
							<td class="value"><form:checkbox path="active"/></td>
						</tr>
						<tr>
							<td></td>
							<td class="value"><form:errors path="active" cssClass="error"/></td>
						</tr>
					</table>
				</fieldset>
				<br>
				<table class="formDiv">
					<tr>
						<td colspan="2" align="right">
							<input type="button" id="btnSavePosMiddlewareSetting" value="${posMiddlewareSetting.id eq 0 ? 'Save' : 'Update'}" onclick="savePosMiddlewareSetting();"/>
							<input type="button" id="btnCancelPosMiddlewareSetting" value="Cancel" onclick="cancelPosMiddlewareSetting();"/>
						</td>
					</tr>
				</table>
			</div>
			<hr class="thin"/>
		</form:form>
	</div>
</body>
</html>