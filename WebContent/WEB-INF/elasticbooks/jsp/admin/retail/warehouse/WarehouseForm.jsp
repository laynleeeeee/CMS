<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--


	Description: Warehouse Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ybl/inputUtil.js"></script>
<title>Warehouse Form</title>
<script type="text/javascript">
$(document).ready(function() {
	var id = getCheckedId("cbWarehouse");
	if (id != 0) {
		$("#companyId").attr("disabled", "disabled");
	}
});

var isSaving = false;
function saveWarehouse() {
	if (!isSaving) {
		isSaving = true;
		var id = getCheckedId("cbWarehouse");
		$("#btnSaveWarehouse").attr("disabled", "disabled");
		$("#companyId").removeAttr("disabled");
		doPostWithCallBack ("warehouse", "divWarehouseForm", function(data) {
			if (data.substring(0,5) == "saved") {
				$("#spanMessage").text("Successfully "+(id > 0 ? "updated " : "saved ")
						+"warehouse: "+$.trim($("#warehouseName").val())+".");
				searchWarehouses();
				dojo.byId("divWarehouseForm").innerHTML = "";
			} else {
				$("#divWarehouseForm").html(data);
				var spanParentWarehouse = $("#spanParentWarehouse").text();
				if (spanParentWarehouse != "") {
					$("#spanParentWarehouse").text("");
					$("#errorParentWarehouseName").show();
				}
				if (getCheckedId("cbWarehouse") != 0) {
					$("#companyId").attr("disabled", "disabled");
				}
			}
			$("#btnSaveWarehouse").removeAttr("disabled");
			isSaving = false;
		});
	}
}

function cancelForm() {
	$("#divWarehouseForm").html("");
}

function showParentWarehouses(elem) {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	console.log("divisionId: " + divisionId);
	var parentWarehouseName = $.trim($(elem).val());
	var hdnWarehouseId = $("#hdnWarehouseId").val();
	var uri = contextPath+"/getWarehouse/getParentWarehouses?companyId="+companyId+"&divisionId="+divisionId
			+"&name="+processSearchName(parentWarehouseName)+"&isExact=false&isActive=true";
	if (hdnWarehouseId != "" && hdnWarehouseId != null && hdnWarehouseId != 0) {
		uri += "&warehouseId="+hdnWarehouseId;
	}
	$(elem).autocomplete({
		source: uri,
		select: function(event, ui) {
			$("#hdnParentWarehouseId").val(ui.item.id);
			$(elem).val(ui.item.name);
			return false;
		}, minLength: 2
	}).data("ui-autocomplete")._renderItem = function(ul, item) {
		return $("<li>").data("ui-autocomplete-item", item)
			.append("<a style='font-size: small;'>"+item.name+"</a>")
			.appendTo(ul);
	};
}

function getParentWarehouse(elem) {
	$("#spanParentWarehouse").text("");
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	var parentWarehouseName = $.trim($(elem).val());
	if (parentWarehouseName != "") {
		var uri = contextPath+"/getWarehouse/getParentWarehouses?companyId="+companyId+"&divisionId="+divisionId
				+"&name="+processSearchName(parentWarehouseName)+"&isExact=true&isActive=false";
		$.ajax({
			url: uri,
			success : function(warehouse) {
				if (warehouse != null && warehouse[0] != undefined) {
					$("#hdnParentWarehouseId").val(warehouse[0].id);
					$(elem).val(warehouse[0].name);
				} else {
					$("#hdnParentWarehouseId").val("");
					$("#spanParentWarehouse").text("Invalid parent warehouse.");
					$("#errorParentWarehouseName").hide();
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$("#hdnParentWarehouseId").val("");
	}
}

function clearParentWarehouse() {
	$("#parentWarehouseName").val("");
	$("#hdnParentWarehouseId").val("");
}
</script>
<style type="text/css">
.form-fixed-width {
	width: 80%
}
</style>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="warehouse">
		<div class="modFormLabel form-fixed-width">Warehouse</div>
		<br>
		<div class="modForm form-fixed-width">
			<fieldset class="frmField_set">
				<legend>* Basic Information</legend>
				<form:hidden path="id" id="hdnWarehouseId"/>
				<form:hidden path="ebObjectId"/>
				<form:hidden path="createdBy"/>
				<form:hidden path="createdDate"/>
				<form:hidden path="parentWarehouseId" id="hdnParentWarehouseId"/>
				<table class="formTable">
					<tr>
						<td class="labels">* Company</td>
						<td class="value">
							<form:select path="companyId" id="companyId" cssClass="frmSelectClass">
								<form:options items="${companies}" itemLabel="name" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="companyId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Division</td>
						<td class="value">
							<form:select path="divisionId" id="divisionId" cssClass="frmSelectClass" onchange="clearParentWarehouse();">
								<form:options items="${divisions}" itemLabel="name" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="divisionId" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Name</td>
						<td class="value">
							<form:input path="name" cssClass="input" id="warehouseName"/>
						</td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="name" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels" valign="top">* Address</td>
						<td class="value">
							<form:textarea path="address" cssClass="input" id="warehouseAddress"
								style="width: 350px; font-family: sans-serif;"/>
						</td>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="address" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">Parent Storage</td>
						<td class="value">
							<form:input path="parentWarehouseName" onkeyup="showParentWarehouses(this);"
								onblur="getParentWarehouse(this);" cssClass="input" id="parentWarehouseName"/>
						</td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><span id="spanParentWarehouse" class="error"></span></td>
					</tr>
					<tr>
						<td class="labels">
						<td class="value"><form:errors path="parentWarehouseName" id="errorParentWarehouseName" cssClass="error"/></td>
					</tr>
					<tr>
						<td class="labels">* Active </td>
						<td class="value"><form:checkbox path="active"/></td>
					</tr>
				</table>
				<br>
			</fieldset>
			<br>
			<table class="formDiv">
				<tr>
					<td></td>
					<td align="right" >
						<input type="button" id="btnSaveWarehouse" value="${warehouse.id eq 0 ? 'Save' : 'Update'}" onclick="saveWarehouse();"/>
						<input type="button" id="btnCancelSave" value="Cancel" onclick="cancelForm();"/>
					</td>
				</tr>
			</table>
		</div>
	</form:form>
	<hr class="thin"/>
</div>
</body>
</html>