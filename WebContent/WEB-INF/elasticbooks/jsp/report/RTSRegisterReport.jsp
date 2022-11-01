<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Return to Supplier Register main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
		var newDate = parseServerDate(new Date);
		$("#rtsDateFrom").val(newDate);
		$("#rtsDateTo").val(newDate);
		CompanyOnChange();
});

	function generateRRReport() {
		clearValues();
		var isValidFilters = true;
		var rtsDateFrom = $.trim($("#rtsDateFrom").val());
		var rtsDateTo = $.trim($("#rtsDateTo").val());
		var rrDateFrom = $.trim($("#rrDateFrom").val());
		var rrDateTo = $.trim($("#rrDateTo").val());
		var amountFrom =  $.trim($("#amountFrom").val()) != "" ? $.trim($("#amountFrom").val()) : 0;
		var amountTo =  $.trim($("#amountTo").val()) != "" ? $.trim($("#amountTo").val()) : 0;

		if ($.trim($("#supplierId").val()) == "") {
			$("#hdnSupplierId").val(-1);
		}

		if (rtsDateFrom != "" && rtsDateTo != "") {
			if (rtsDateFrom > rtsDateTo ) {
				$("#spanRTSDateError").text("RTS Date from is not valid.");
				isValidFilters = false;
			}
		}

		if (rrDateFrom != "" && rrDateFrom != "") {
			if (rrDateFrom > rrDateTo ) {
				$("#spanRRDateError").text("RR Date from is not valid.");
				isValidFilters = false;
			}
		}

		if (rtsDateFrom == "" && rtsDateTo == "") {
			$("#spanRTSDateError").text("RTS Date is required.");
			isValidFilters = false;
		}

		if (rtsDateFrom == "" && rtsDateTo != "") {
			$("#spanRTSDateError").text("Invalid date range.");
			isValidFilters = false;
		}

		if (rtsDateFrom != "" && rtsDateTo == "") {
			$("#spanRTSDateError").text("Invalid date range.");
			isValidFilters = false;
		}

		if (rrDateFrom == "" && rrDateTo != "") {
			$("#spanRRDateError").text("Invalid date range.");
			isValidFilters = false;
		}

		if (rrDateFrom != "" && rrDateTo == "") {
			$("#spanRRDateError").text("Invalid date range.");
			isValidFilters = false;
		}

		if ($("#companyId").val() == -1) {
			$("#spanCompanyError").text("Company is required.");
			isValidFilters = false;
		}

		if ($("#supplierAcctId").val() == null) {
			if ($("#hdnSupplierId").val() == "") {
				$("#supplierAcctError").text("Supplier account is required.");
				isValidFilters = false;
			}
		}

		if ($("#warehouseId").val() == null) {
			$("#spanWarehouseError").text("Warehouse is required.");
			isValidFilters = false;
		}

		if ($("#divisionId").val() == null) {
			$("#spanDivisionError").text("Division is required.");
			isValidFilters = false;
		}

		if (amountFrom > amountTo ) {
			$("#spanAmountError").text("Amount from should be lesser than amount to.");
			isValidFilters = false;
		}

		var url = "";
		if (isValidFilters) {
			clearValues();
			url = contextPath + "/rtsReportRegister/generatePDF" + getCommonParam();
		}
		$("#iFrame").attr('src', url);
		$("#iFrame").load();
	}

	function clearValues() {
		$("#spanCompanyError").text("");
		$("#spanRTSDateError").text("");
		$("#spanRRDateError").text("");
		$("#spanDivisionError").text("");
		$("#supplierError").text("");
		$("#supplierAcctError").text("");
		$("#spanAmountError").text("");
	}

	function filterSuppliers() {
		clearValues();
		var selectedDivisionId = "";
		selectedCompanyId = $("#companyId").val();
		if($("#divisionId").val() != -1 && $("#divisionId").val() != null){
			selectedDivisionId = $("#divisionId").val();
		}
		var supplierName =  $("#supplierId").val() != "" ? $.trim($("#supplierId").val()) : -1;
		var uri = contextPath+"/getSuppliers/new?name="+supplierName+"&companyId="+selectedCompanyId+
			"&divisionId="+selectedDivisionId;
		$("#supplierId").autocomplete({
			source: uri,
			select: function(event, ui) {
				$("#hdnSupplierId").val(ui.item.id);
				$(this).val(ui.item.name);
				filterSupplierAccts();
				return false;
			}, minLength: 2,
			change: function(event, ui) {
				$.ajax({
					url: uri,
					success : function(item) {
						if (ui.item != null) {
							$("#hdnSupplierId").val(ui.item.id);
							$(this).val(ui.item.name);
							clearValues();
						} else {
							$("#hdnSupplierId").val("");
						}
					},
					dataType: "json"
				});
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>"+item.name +"</a>" )
			.appendTo( ul );
		};
	}

	function getSupplier() {
		var supplierName = $.trim($("#supplierId").val());
		var uri = contextPath + "/getSuppliers/new?name="+encodeURIComponent(supplierName)
				+"&companyId="+$("#companyId").val()+"&isExact=true";
		if($("#companyId").val() != -1) {
			$.ajax({
				url: uri,
				success : function(supplier) {
					if (supplier != null && supplier[0] != undefined) {
						$("#hdnSupplierId").val(supplier[0].id);
						$("#supplierId").val(supplier[0].name);
						clearAndAddEmpty();
					}else{
						if(supplierName != "") {
							$("#supplierError").text("Invalid supplier.");
							$("#iFrame").attr('src', "");
							$("#hdnSupplierId").val("");
							$("#supplierAcctId").empty();
						}
						$("#hdnSupplierId").val("");
					}
					filterSupplierAccts();
				},
				error:function(){
					if(supplierName != "") {
						$("#supplierError").text("Invalid supplier.");
						$("#iFrame").attr('src', "");
						$("#hdnSupplierId").val("");
						$("#supplierAcctId").empty();
					}
					$("#hdnSupplierId").val("");
				},
				dataType: "json"
			});
		}
	}

	function filterSupplierAccts() {
		var selectedDivisionId = $("#divisionId").val();
		var selectedCompanyId = $("#companyId").val();

		if ($.trim($("#supplierId").val()) == "") {
			$("#hdnSupplierId").val(-1);
		}
		var supplierId = Number($("#hdnSupplierId").val()) != 0 ? $("#hdnSupplierId").val() : -1;
		var uri = contextPath+"/getApSupplierAccts?supplierId="+supplierId+"&companyId="+selectedCompanyId
				+(selectedDivisionId == -1  ? "" : "&divisionId="+selectedDivisionId);
		$("#supplierAcctId").empty();
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate (uri, true, null, "supplierAcctId", optionParser, null);
	}

	function filterWarehouses() {
		var companyId = $("#companyId").val();
		if (companyId != "") {
			var uri = contextPath+"/getWarehouse?companyId="+companyId;
			$("#warehouseId").empty();
			var optionParser = {
				getValue: function (rowObject) {
					return rowObject["id"];
				},
				getLabel: function (rowObject) {
					return rowObject["name"];
				}
			};
			loadPopulate (uri, true, null, "warehouseId", optionParser, null);
		}
	}

	function CompanyOnChange() {
		filterWarehouses();
		filterSupplierAccts();
		$("#hdnSupplierId").val("");
		$("#supplierId").val("");
		clearAndAddEmpty();
	}
	function SupplierOnChange() {
		filterSupplierAccts();
	}
	function formatMoney (textbox) {
		var money = accounting.formatMoney($(textbox).val());
		$(textbox).val(money);
	}

	function unformat() {
		$("#amountFrom").val(accounting.unformat($("#amountFrom").val()));
		$("#amountTo").val(accounting.unformat($("#amountTo").val()));
		$("#balance").val(accounting.unformat($("#balance").val()));
	}

	function getCommonParam() {
		var amountFrom = 0;
		var amountTo = 0;
		var paymentStatId = $("#paymentStatusId").val();
		var companyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var rtsDateFrom = $.trim($("#rtsDateFrom").val());
		var rtsDateTo = $.trim($("#rtsDateTo").val());
		var rrDateFrom = $.trim($("#rrDateFrom").val());
		var rrDateTo = $.trim($("#rrDateTo").val());
		var warehouseId = $("#warehouseId").val();
		if($.trim($("#amountFrom").val()) != ""){
			amountFrom = $.trim($("#amountFrom").val());
		}else {
			 amountFrom = 0.0
		}
		if($.trim($("#amountTo").val()) != ""){
			amountTo = $.trim($("#amountTo").val());
		}else {
			amountTo = 0.0;
		}
		var supplierAcctId = $("#supplierAcctId").val() == null ? -1 : $("#supplierAcctId").val();
		var supplierId = $("#hdnSupplierId").val();
		var statusId = $("#statusId").val();
		var formatType = $("#formatType").val();

		return "?companyId=" + companyId + "&divisionId=" + divisionId + "&warehouseId=" + warehouseId + "&supplierId=" + supplierId + "&supplierAcctId=" + supplierAcctId
		+ "&rtsDateFrom=" + rtsDateFrom + "&rtsDateTo=" + rtsDateTo + "&rrDateFrom=" + rrDateFrom + "&rrDateTo=" + rrDateTo +
		"&amountFrom=" + amountFrom + "&amountTo=" + amountTo + "&statusId=" + statusId + "&paymentStatId=" + paymentStatId
				+ "&formatType=" + formatType;
	}

	function clearAndAddEmpty() {
		$("#supplierAcctId").empty();
		var option = "<option selected='selected' value='-1'>ALL</option>";
		$("#supplierAcctId").append(option);
	}
</script>
</head>
<body>
	<table>
		<tr>
			<td class="title2">Company</td>
			<td><select id="companyId" onchange="CompanyOnChange();"
				class="frmSelectClass">
					<option selected='selected' value=-1>Please select a
						company</option>
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanCompanyError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Division</td>
			<td><select id="divisionId" onchange="CompanyOnChange();"
				class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Warehouse</td>
			<td><select id="warehouseId" class="frmSelectClass">
			<option selected='selected' value=-1>ALL</option>
			<c:forEach var="warehouse" items="${warehouses}">
						<option value="${warehouse.id}">${warehouse.name}</option>
			</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="2">
				<span id="spanWarehouseError" class="error"></span>
			</td>
		</tr>
		<tr>
			<td class="title2">Supplier</td>
			<td><input type="hidden" id="hdnSupplierId"/>
			<input class="input" id="supplierId"  onkeydown="filterSuppliers();"
				onkeyup="filterSuppliers();" onblur="getSupplier();"
			></td>
		</tr>
		<tr>
			<td></td>
			<td><span id="supplierError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Supplier Account</td>
			<td><select id="supplierAcctId" class="frmSelectClass"></select></td>
		</tr>
		<tr>
			<td></td>
			<td><span id="supplierAcctError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">RTS Date:</td>
			<td class="tdDateFilter">
				<input type="text" id="rtsDateFrom" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('rtsDateFrom')" style="cursor:pointer"
							style="float: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="rtsDateTo" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);">
					<img src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('rtsDateTo')" style="cursor:pointer"
							style="float: right;"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanRTSDateError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">RR Date:</td>
			<td class="tdDateFilter">
				<input type="text" id="rrDateFrom"
				maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);"> <img
				src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('rrDateFrom')" style="cursor: pointer"
				style="float: right;" /> <span style="font-size: small;">To</span>
				<input type="text" id="rrDateTo" maxlength="10" class="dateClass2" 
				onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('rrDateTo')" style="cursor: pointer"
				style="float: right;" /></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanRRDateError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Amount </td>
			<td class="tdDateFilter">
				<input type="text" id="amountFrom" onblur="inputOnlyNumeric('amountFrom');" class="dateClass2"
				 style="text-align: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="amountTo" onblur="inputOnlyNumeric('amountTo');" class="dateClass2"
				 style="text-align: right;"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="1"><span id="spanAmountError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">RTS Status</td>
			<td><select id="statusId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="rtsStatus" items="${rtsStatuses}">
						<option value="${rtsStatus.id}">${rtsStatus.description}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Payment Status</td>
			<td><select id="paymentStatusId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="status" items="${statuses}">
						<option value="${status.value}">${status.description}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Format:</td>
			<td><select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select></td>
		</tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate"
				onclick="generateRRReport()" /></td>
		</tr>
	</table>
	<hr class="thin2">
	<div>
		<iframe id="iFrame"></iframe>
	</div>
</body>
</html>