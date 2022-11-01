<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Item Volume Conversion Form For Student.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ybl/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.unitcosthandler.js"></script>
<title>Item Point Conversion</title>
<script type="text/javascript">
function loadItems() {
	var companyId = $("#selectCompanyId").val();
	var stockCode = processSearchName($.trim($("#txtStockCode").val()));
	var uri = contextPath+"/getRItems/filter?companyId="+companyId+
		"&stockCode="+stockCode;
	loadACList("txtStockCode", "itemId", uri,
			contextPath + "/getItem?stockCode=", "stockCodeAndDesc", "stockCode",
			function(item) {
				//Select
				$("#spanSCError").text("");
			}, function(item) {
				//Change
				$("#spanSCError").text("");
			}, function() {
				//Success
				$("#spanSCError").text("");
			}, function() {
				//Error
				$("#spanSCError").text("Invalid stock code.");
			}
	);
}

function getItem (elem) {
	var companyId = $("#selectCompanyId").val();
	var stockCode = $.trim($("#txtStockCode").val());
	if (stockCode != "") {
		$.ajax({
			url: contextPath+"/getItem?stockCode="+processSearchName(stockCode)+
					"&companyId="+companyId,
			success: function (item) {
				if (item != null) {
					$("#spanSCError").text("");
					$(elem).val(item.stockCode);
					$("#itemId").val(item.id);
				}
			},
			error : function(error) {
				$("#itemId").val("");
				$("#txtStockCode").val("");
				$("#spanSCError").text("Invalid stock code.");
			},
			dataType: "json"
		});
	} else {
		$("#itemId").val("");
	}
}
</script>
</head>
<body>
<div class="formDiv">
<form:form method="POST" commandName="volumeConversion">
		<div class="modFormLabel">Item Volume Conversion</div>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="createdDate"/>
	<br>
	<div class="modForm">
	<fieldset class="frmField_set">
		<table class="formTable">
			<tr>
				<td class="labels">* Company</td>
				<td class="value">
					<form:select id="selectCompanyId" path="companyId" class="frmSelectClass">
						<form:options items="${companies}" itemLabel="numberAndName" itemValue="id"/>
					</form:select>
				</td>
			</tr>
			<tr>
				<td class="labels"></td>
				<td class="value">
					<span id="spanCompanyError" class="error"><form:errors path="companyId" cssClass="error"/></span>
				</td>
			</tr>
			<tr>
				<td class="labels">* Item</td>
				<td class="value">
					<form:input path="stockCode" type="text" id="txtStockCode" class="input" onkeyup="loadItems();" 
						onkeydown="loadItems();" onblur="getItem(this);"/>
					<form:input type = "hidden" path="itemId"/>
				</td>
			</tr>
			<tr>
				<td class="labels"></td>
				<td class="value">
					<span id="spanSCError" class="error"><form:errors path="stockCode" cssClass="error"/></span>
				</td>
			</tr>
			<tr>
				<td class="labels">* Quantity</td>
				<td class="value">
					<form:input path="quantity" class="numeric" id="quantityId" onblur="formatMoney(this);"/>
				</td>
			</tr>
			<tr>
				<td class="labels"></td>
				<td class="value">
					<form:errors path="quantity" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">* Volume Conversion</td>
				<td class="value">
					<form:input path="volumeConversion" class="numeric" id="volumeConversionId" onblur="formatMoney(this);"/>
				</td>
			</tr>
			<tr>
				<td class="labels"></td>
				<td class="value">
					<form:errors path="volumeConversion" cssClass="error"/>
				</td>
			</tr>
			<tr>
				<td class="labels">* Active </td>
				<td class="value"><form:checkbox path="active"/></td>
			</tr>
			<tr>
				<td colspan="2"><form:errors path="active" cssClass="error"/></td>
			</tr>
		</table>
		<br>
	</fieldset>	
	<table class="formDiv">
			<tr>
				<td></td>
				<td align="right" >
					<input type="button" id="btnSaveVolumeConversion" value="Save" onclick="saveVolumeConversion();"/>
					<input type="button" id="btnCancelSave" value="Cancel" onclick="cancelForm();"/>
				</td>
			</tr>
		</table>
		</div>
		</form:form>
	</div>
<hr class="thin"/>
</body>
</html>