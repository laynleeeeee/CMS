<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Receiving Report Register main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		selectOnChange(true);
		var newDate = parseServerDate(new Date);
		$("#rrDateFrom").val(newDate);
		$("#rrDateTo").val(newDate);
	});

	function generateRRReport() {
		var supplierId = $("#supplierId").val();
		if(supplierId == "") {
			$("#hdnSupplierId").val(-1);
		}

		var isValidDate = $.trim($("#rrDateFrom").val()) != ""
			&& $.trim($("#rrDateTo").val()) != "";
		if (!isValidDate) {
			$("#spanDateError").text("Date is required.");
			$("#iFrame").attr('src', "");
		} else {
			$("#spanDateError").text("");
		}

		if($("#companyId").val() == -1) {
			$("#spanCompanyError").text("Company is required.");
			$("#iFrame").attr('src', "");
		} else {
			$("#spanCompanyError").text("");
		}

		if($("#slctDivisionId").val() == null) {
			$("#spanDivisionError").text("");
		}

		if($("#warehouseId").val() == null){
			$("#spanWarehouseError").text("Warehouse is required.");
			$("#iFrame").attr('src', "");
		} else {
			$("#spanWarehouseError").text("");
		}

		if($("#supplierAcctId").val() == null) {
			if($("#hdnSupplierId").val() == "") {
				$("#supplierAcctError").text("Supplier account is required.");
			}
			$("#iFrame").attr('src', "");
		} else {
			$("#supplierAcctError").text("");
		}

		if ($("#companyId").val() != -1 && isValidDate && $("#warehouseId").val() != null && $("#hdnSupplierId").val() != ""){
			var url = contextPath + "/rrReportRegister/generatePDF" + getCommonParam();
			$("#iFrame").attr('src', url);
			$("#iFrame").load();
		} else {
			$("#iFrame").attr('src', "");
		}
	}

	function clearValues() {
		$("#spanCompanyError").text("");
		$("#spanDateError").text("");
		$("#spanWarehouseError").text("");
		$("#supplierError").text("");
		$("#supplierAcctError").text("");
	}

	function loadDivisions() {
		var companyId = $("#companyId").val();
		var uri = contextPath+"/getDivisions/byCompany?companyId="
				+companyId+"&isMainLevelOnly=true";
		$("#slctDivisionId").empty();
		var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};
		postHandler = {
				doPost: function(data) {
					// nothing
				}
		};
		loadPopulate (uri, true, null, "slctDivisionId", optionParser, postHandler);
	}

	function filterSuppliers() {
		clearValues();
		selectedCompanyId = $("#companyId").val();
		var divisionId = $("#slctDivisionId").val();
		var supplierName =  $("#supplierId").val() != "" ? $.trim($("#supplierId").val()) : $("#hdnSupplierId").val(-1);
		var uri = contextPath+"/getSuppliers/new?name="+supplierName+"&companyId="+selectedCompanyId+(divisionId > 0 ? "&divisionId="+divisionId : "");
		$("#supplierId").autocomplete({
			source: uri,
			select: function(event, ui) {
				$("#hdnSupplierId").val(ui.item.id);
				$(this).val(ui.item.name);
				return false;
			}, minLength: 2,
			change: function(event, ui) {
				$.ajax({
					url: uri,
					success : function(item) {
						if (ui.item != null) {
							$("#hdnSupplierId").val(ui.item.id);
							$(this).val(ui.item.name);
						} else {
							$("#hndSupplierId").val("");
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
						filterSupplierAccts();
					} else {
						if(supplierName != "") {
							$("#supplierError").text("Invalid supplier.");
							$("#iFrame").attr('src', "");
						}
						$("#hdnSupplierId").val("");
						$("#supplierAcctId").empty();
					}
				},
				dataType: "json"
			});
		}
	}

	function filterSupplierAccts() {
		selectedCompanyId = $("#companyId").val();
		$("#supplierAcctId").empty();
		if($.trim($("#supplierId").val()) != ""){
			var supplierId = Number($("#hdnSupplierId").val()) != 0 ? $("#hdnSupplierId").val() : -1;
			var uri = contextPath+"/getApSupplierAccts?supplierId="+supplierId+"&companyId="+selectedCompanyId;
			var optionParser = {
				getValue: function (rowObject) {
					return rowObject["id"];
				},

				getLabel: function (rowObject) {
					return rowObject["name"];
				}
			};
			loadPopulate (uri, true, null, "supplierAcctId", optionParser, null);
		} else {
			$("#hndSupplierId").val(-1);
		}
	}
	
	function filterWarehouses() {
		var companyId = $("#companyId").val();
		var divisionId = $("#slctDivisionId").val();
		if (companyId != "") {
			var uri = contextPath + "/getWarehouse/new?companyId="+companyId+(divisionId > 0 ? "&divisionId="+divisionId : "");
			$("#warehouseId").empty();
			var optionParser = {
				getValue : function(rowObject) {
					return rowObject["id"];
				},

				getLabel : function(rowObject) {
					return rowObject["name"];
				}
			};
			loadPopulate(uri, true, null, "warehouseId", optionParser);
		}
	}

	function selectOnChange(isReloadDivision) {
		if (isReloadDivision) {
			loadDivisions();
		}
		filterWarehouses();
		filterSuppliers();
		filterSupplierAccts();
		$("#hdnSupplierId").val("");
		$("#supplierId").val("");
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
		unformat();
		var paymentStatId = $("#paymentStatusId").val();
		var companyId = $("#companyId").val();
		var divisionId = $("#slctDivisionId").val();
		var rrDateFrom = document.getElementById("rrDateFrom").value;
		var rrDateTo = document.getElementById("rrDateTo").value;
		var warehouseId = $("#warehouseId").val();
		var amountFrom = $("#amountFrom").val();
		var amountTo = $("#amountTo").val();
		var supplierAcctId = $("#supplierAcctId").val() === null ? -1 : $("#supplierAcctId").val();
		var supplierId = $("#hdnSupplierId").val();
		var termId = $("#termId").val();
		var statusId = $("#statusId").val();
		var formatType = $("#formatType").val();
		var uri = "?companyId=" + companyId + "&divisionId=" + divisionId + "&warehouseId=" + warehouseId + "&supplierId=" + supplierId
				+ "&supplierAcctId=" + supplierAcctId + "&rrDateFrom=" + rrDateFrom + "&rrDateTo=" + rrDateTo+ "&termId=" + termId
				+ "&amountFrom=" + amountFrom + "&amountTo=" + amountTo + "&statusId=" + statusId
				+ "&paymentStatId=" + paymentStatId + "&formatType=" + formatType;
		return uri;
	}
</script>
</head>
<body>
	<table>
		<tr>
			<td class="title2">Company</td>
			<td>
				<select id="companyId" onchange="selectOnChange(true);" class="frmSelectClass">
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="2"><span id="spanCompanyError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Division</td>
			<td class="value"><select id="slctDivisionId" onchange="selectOnChange(false);" class="frmSelectClass"></select></td>
		</tr>
		<tr>
			<td class="title2"></td>
			<td class="value"><span id="spanDivisionError" class="error"></span>
			</td>
		</tr>
		<tr>
			<td class="title2">Warehouse</td>
			<td><select id="warehouseId" class="frmSelectClass"></select></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="2">
				<span id="spanWarehouseError" class="error"></span>
			</td>
		</tr>
		<tr>
			<td class="title2">Supplier</td>
			<td>
				<input type="hidden" id="hdnSupplierId"/>
				<input class="input" id="supplierId" onkeypress="filterSuppliers();"
					onblur="getSupplier();">
			</td>
		</tr>
		<tr>
			<td></td>
			<td><span id="supplierError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Supplier Account</td>
			<td><select id="supplierAcctId" class="frmSelectClass"></select>
			</td>
		</tr>
		<tr>
			<td></td>
			<td><span id="supplierAcctError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Terms</td>
			<td>
				<select id="termId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="term" items="${terms}">
						<option value="${term.id}">${term.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td class="title2">RR Date</td>
			<td class="tdDateFilter">
				<input type="text" id="rrDateFrom" onblur="evalDate(this.id, false);"
					maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('rrDateFrom')" style="cursor: pointer"
					style="float: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="rrDateTo" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('rrDateTo')" style="cursor: pointer"
					style="float: right;" />
			</td>
		</tr>
		<tr>
			<td class="title2"></td>
			<td><span id="spanDateError" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Amount</td>
			<td class="tdDateFilter">
				<input type="text" id="amountFrom" class="dateClass2"
				onblur="formatMoney(this);" style="text-align: right;"/>
				<span style="font-size: small;">To</span>
				<input type="text" id="amountTo" class="dateClass2"
				onblur="formatMoney(this);" style="text-align: right;"/>
			</td>
		</tr>
		<tr>
			<td class="title2">Status</td>
			<td>
				<select id="statusId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<option value=31>DRAFTED</option>
					<option value=1>CREATED</option>
					<option value=4>CANCELLED</option>
				</select>
			</td>
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
			<td class="value"><select id="formatType" class="frmSelectClass">
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